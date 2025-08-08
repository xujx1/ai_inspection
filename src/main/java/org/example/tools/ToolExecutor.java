package org.example.tools;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.tools.ToolCallFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.utils.JsonCleanerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 工具执行器
 * 负责处理AI模型的工具调用请求
 */
@Component
public class ToolExecutor {

    private static final Logger log = LoggerFactory.getLogger(ToolExecutor.class);
    private final ComparisonToolManager toolManager;

    public ToolExecutor() {
        this.toolManager = new ComparisonToolManager();
    }

    /**
     * 处理模型响应，包括工具调用
     */
    public String handleModelResponse(MultiModalConversation conv, MultiModalConversationResult result, 
                                     String apiKey, List<MultiModalMessage> messages, String model) {
        try {
            String finishReason = result.getOutput().getChoices().get(0).getFinishReason();
            log.info("模型响应 finishReason: {}", finishReason);

            // 如果不需要工具调用，直接返回文本结果
            if (!"tool_calls".equals(finishReason)) {
                return extractTextFromModelResponse(result);
            }

            // 处理工具调用
            log.info("AI请求工具调用，开始处理...");
            return handleToolCalls(conv, result, apiKey, messages, model);

        } catch (Exception e) {
            log.error("处理模型响应失败: {}", e.getMessage(), e);
            return "模型响应处理失败: " + e.getMessage();
        }
    }

    /**
     * 从模型响应中提取文本内容
     */
    private String extractTextFromModelResponse(MultiModalConversationResult result) {
        Object textContent = result.getOutput().getChoices().get(0).getMessage().getContent();
        log.info("AI直接返回结果，无工具调用，内容类型: {}", 
                textContent != null ? textContent.getClass().getSimpleName() : "null");
        
        if (textContent instanceof List) {
            List<?> contentList = (List<?>) textContent;
            if (!contentList.isEmpty() && contentList.get(0) instanceof Map) {
                Map<?, ?> contentMap = (Map<?, ?>) contentList.get(0);
                String finalText = String.valueOf(contentMap.get("text"));
                log.info("AI最终返回文本: {}", finalText);
                return finalText;
            }
        }
        log.warn("AI返回了空内容或格式异常");
        return "";
    }

    /**
     * 处理工具调用
     */
    private String handleToolCalls(MultiModalConversation conv, MultiModalConversationResult result, 
                                  String apiKey, List<MultiModalMessage> messages, String model) {
        try {
            List<MultiModalMessage> toolReqMessages = new ArrayList<>(messages);
            List toolCalls = result.getOutput().getChoices().get(0).getMessage().getToolCalls();
            
            if (CollectionUtils.isEmpty(toolCalls)) {
                log.warn("finishReason为tool_calls但toolCalls为空");
                return "工具调用列表为空";
            }

            log.info("开始处理 {} 个工具调用", toolCalls.size());

            // 处理每个工具调用
            for (int i = 0; i < toolCalls.size(); i++) {
                ToolCallFunction functionCall = parseToolCall(toolCalls.get(i));
                if (functionCall == null) {
                    continue;
                }

                String functionName = functionCall.getFunction().getName();
                String arguments = functionCall.getFunction().getArguments();
                String toolCallId = functionCall.getId();

                log.info("处理工具调用 {}: {} 参数: {}", i + 1, functionName, arguments);

                // 执行工具调用
                String toolResult = executeToolCall(functionName, arguments);

                // 构建工具调用结果消息
                Map<String, Object> content = new HashMap<>();
                content.put("text", toolResult);

                MultiModalMessage toolResultMessage = MultiModalMessage.builder()
                        .role("tool")
                        .content(Collections.singletonList(content))
                        .toolCallId(toolCallId)
                        .build();

                toolReqMessages.add(toolResultMessage);
            }

            // 重新调用模型，传入工具调用结果
            return continueConversationWithToolResults(conv, messages, apiKey, toolReqMessages, model);

        } catch (Exception e) {
            log.error("处理工具调用失败: {}", e.getMessage(), e);
            return "工具调用处理失败: " + e.getMessage();
        }
    }

    /**
     * 解析工具调用对象
     */
    private ToolCallFunction parseToolCall(Object toolCall) {
        try {
            if (toolCall instanceof LinkedTreeMap<?, ?>) {
                return JSON.parseObject(JSON.toJSONString(toolCall), ToolCallFunction.class);
            } else if (toolCall instanceof ToolCallFunction) {
                return (ToolCallFunction) toolCall;
            }
        } catch (Exception e) {
            log.warn("解析工具调用失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 执行具体的工具调用
     */
    private String executeToolCall(String functionName, String arguments) {
        try {
            log.info("执行工具调用: {} 参数: {}", functionName, arguments);

            // 处理参数中可能包含的额外文本
            String cleanedArguments = JsonCleanerUtils.cleanJsonArguments(arguments);
            return toolManager.executeToolCall(functionName, cleanedArguments);

        } catch (Exception e) {
            log.error("执行工具调用失败: {} 参数: {}", functionName, arguments, e);
            return "工具调用执行失败: " + e.getMessage() + "，原始参数: " + arguments;
        }
    }



    /**
     * 继续对话，传入工具调用结果
     */
    private String continueConversationWithToolResults(MultiModalConversation conv, 
                                                      List<MultiModalMessage> originalMessages,
                                                      String apiKey, 
                                                      List<MultiModalMessage> toolResultMessages, 
                                                      String model) {
        try {
            log.info("继续对话，传入工具结果，消息数量: {}", toolResultMessages.size());

            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(toolResultMessages)
                    .tools(toolManager.getAllTools()) // 继续提供工具定义
                    .build();

            log.info("重新调用AI模型，等待响应...");
            MultiModalConversationResult result = conv.call(param);

            // 递归处理响应（可能还会有更多工具调用）
            log.info("收到AI模型响应，递归处理...");
            return handleModelResponse(conv, result, apiKey, originalMessages, model);

        } catch (Exception e) {
            log.error("继续对话失败: {}", e.getMessage(), e);
            return "继续对话失败: " + e.getMessage();
        }
    }

    public ComparisonToolManager getToolManager() {
        return toolManager;
    }
}