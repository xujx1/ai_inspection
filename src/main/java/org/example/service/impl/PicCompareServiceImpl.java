package org.example.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.config.DashScopeApiConfig;
import org.example.dto.InspectionIndicatorDTO;
import org.example.entity.InspectionPrompt;
import org.example.entity.InspectionResult;
import org.example.mapper.InspectionPromptMapper;
import org.example.mapper.InspectionResultMapper;
import org.example.service.PicCompareService;
import org.example.tools.ToolExecutor;
import org.example.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.protocol.Protocol;

import java.util.*;

@Service
public class PicCompareServiceImpl implements PicCompareService {

    private static final Logger log = LoggerFactory.getLogger(PicCompareServiceImpl.class);

    @Autowired
    private DashScopeApiConfig dashScopeConfig;

    @Autowired
    private InspectionResultMapper inspectionResultMapper;

    @Autowired
    private InspectionPromptMapper inspectionPromptMapper;
    
    @Autowired
    private ToolExecutor toolExecutor;

    @Override
    public void picCompare(List<InspectionResult> resultList) {
        resultList.forEach(this::picCompareSingle);
    }

    @Override
    public void picCompareSingle(InspectionResult result) {
        try {
            Map<String, String> req = JSON.parseObject(result.getReq(), new TypeReference<>() {
            });

            // 多模态模型接口
            MultiModalConversation conv = new MultiModalConversation(Protocol.HTTP.getValue(), dashScopeConfig.getUrl());

            String originalPrompt = null;

            if (StringUtils.isNotEmpty(req.get("prompt"))) {
                originalPrompt = req.get("prompt");
            } else {
                List<InspectionPrompt> prompts = inspectionPromptMapper.selectByCondition(result.getTag(), null, result.getMethodName());

                if (CollectionUtils.isNotEmpty(prompts)) {
                    originalPrompt = prompts.get(0).getPromptContent();
                }
            }

            // 构建系统消息，确保originalPrompt的优先级更高
            String finalSystemPrompt = "originalPrompt:\n" + originalPrompt + "\n\n" + "sysPrompt:\n" + sysPrompt();

            MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue()).content(Collections.singletonList(Collections.singletonMap("text", finalSystemPrompt))).build();

            MultiModalMessage toolsDescription = MultiModalMessage.builder().role(Role.TOOL.getValue()).content(Collections.singletonList(Collections.singletonMap("text", toolExecutor.getToolManager().createToolsDescription()))).build();
            MultiModalMessage analysisGuide = MultiModalMessage.builder().role(Role.TOOL.getValue()).content(Collections.singletonList(Collections.singletonMap("text", toolExecutor.getToolManager().createAnalysisGuide()))).build();

            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue()).content(Arrays.asList(
                    // 第一张图像url
                    new HashMap<String, Object>(1) {{
                        put("image", result.getLastResp());
                        put("lastPicName", req.get("lastPicName"));
                        put("key", "actualValue");
                    }}, new HashMap<String, Object>(1) {{
                        put("image", result.getThisResp());
                        put("thisPicName", req.get("thisPicName"));
                        put("key", "expectedValue");
                    }})).build();

            List<MultiModalMessage> list = Arrays.asList(systemMessage, toolsDescription, analysisGuide, userMessage);

            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(dashScopeConfig.getKey())
                    .model(dashScopeConfig.getModel())
                    .messages(list)
                    .tools(toolExecutor.getToolManager().getAllTools())
                    .build();

            MultiModalConversationResult conversationResult = conv.call(param);

            // 处理工具调用
            String finalText = toolExecutor.handleModelResponse(conv, conversationResult, dashScopeConfig.getKey(), list, dashScopeConfig.getModel());
            log.info("AI比较结果: " + finalText);

            List<InspectionIndicatorDTO> resultDTOS = convertTextToResultDTOs(finalText);

            result.setErrorMsg(JSONArray.toJSONString(resultDTOS));
            if (CollectionUtils.isNotEmpty(resultDTOS)) {
                result.setCheckFlag("N");
            } else {
                result.setCheckFlag("Y");
            }

            inspectionResultMapper.updateById(result);
        } catch (Exception e) {
            log.error("图片比较失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将AI返回的文本转换为结果DTO列表
     */
    private List<InspectionIndicatorDTO> convertTextToResultDTOs(Object text) {
        if (text == null) {
            return new ArrayList<>();
        }

        String t = text.toString();

        if (StringUtils.isEmpty(t)) {
            return new ArrayList<>();
        }

        // 提取文本中的 JSON 部分
        t = extractJsonFromText(t);

        if (!JsonUtils.isJson(t)) {
            return new ArrayList<>();
        }

        // 清理 markdown 代码块标记
        if (t.contains("```json")) {
            t = t.replace("```json", "");
        }

        if (t.contains("```")) {
            t = t.replace("```", "");
        }

        JSONObject jsonObject = JSONObject.parseObject(t);

        JSONObject object = null;
        if (jsonObject.containsKey("diff")) {
            object = jsonObject.getJSONObject("diff");
        } else if (jsonObject.containsKey("不一致点")) {
            object = jsonObject.getJSONObject("不一致点");
        }

        if (object == null || object.isEmpty()) {
            return new ArrayList<>();
        }

        List<InspectionIndicatorDTO> list = new ArrayList<>();

        for (String key : object.keySet()) {
            JSONObject value = object.getJSONObject(key);
            InspectionIndicatorDTO resultDTO = new InspectionIndicatorDTO();
            resultDTO.setIndicatorName(key);
            Object reason = value.remove("reason");

            if (reason != null) {
                String reasonStr = reason.toString();

                // 过滤掉无需比较的数据
                if (reasonStr.contains("无需比较")) {
                    log.debug("过滤掉无需比较的数据: {}", key);
                    continue;
                }

                // 过滤掉在合理波动范围内的数据
                if (reasonStr.contains("在合理波动范围内") || reasonStr.contains("数据一致") || reasonStr.contains("波动在可接受范围内") || reasonStr.contains("未超过预设的阈值") || reasonStr.contains("不报告为异常") || reasonStr.contains("在阈值范围内") || reasonStr.contains("正常波动") || reasonStr.contains("合理范围") || reasonStr.contains("可接受") || reasonStr.contains("无需报告")) {
                    log.debug("过滤掉在合理波动范围内的数据: {}", key);
                    continue;
                }

                resultDTO.setReason(reasonStr);
            }

            // 如果实际值和期望值相等，也过滤掉
            if (StringUtils.equals(value.getString("actualValue"), value.getString("expectedValue"))) {
                log.debug("过滤掉值相等的数据: {}", key);
                continue;
            }

            resultDTO.setActualValue(value.getString("actualValue"));
            resultDTO.setExpectedValue(value.getString("expectedValue"));
            list.add(resultDTO);
        }

        return list;
    }

    /**
     * 从文本中提取 JSON 部分
     */
    private String extractJsonFromText(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        // 查找 JSON 对象的开始和结束位置
        int startBrace = text.indexOf('{');
        int endBrace = text.lastIndexOf('}');

        // 如果找到了完整的 JSON 对象
        if (startBrace != -1 && endBrace != -1 && endBrace > startBrace) {
            return text.substring(startBrace, endBrace + 1);
        }

        // 如果没有找到 JSON 对象，返回原文本
        return text;
    }

    private static String sysPrompt() {
        return "你是一个严谨的端到端巡检测试专家，能根据提供的两张图片，准确的寻找到差异点，并返回差异的信息。" + 
               "【核心原则】只报告真正异常的数据，正常波动范围内的数据绝对不要输出到JSON中！\n" + 
               "【强制要求】对于任何数值比较，必须使用compare_threshold工具，严禁手动计算！\n" + 
               "返回的结果格式为：{\\\"不一致点\\\":{\\\"actualValue\\\":\\\"xxx\\\",\\\"expectedValue\\\":\\\"xxx\\\",\\\"reason\\\":\\\"xxx\\\"}}。\n" + 
               "【再次强调】在合理波动范围内的数据绝对不要输出到JSON中，无论任何情况！";
    }
}