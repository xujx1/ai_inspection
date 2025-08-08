package org.example.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JSON清理工具类
 * 用于清理AI模型返回的可能包含非JSON内容的参数
 */
public class JsonCleanerUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonCleanerUtils.class);

    /**
     * 清理并验证JSON参数
     *
     * @param arguments 原始参数字符串
     * @return 清理后的JSON字符串
     */
    public static String cleanJsonArguments(String arguments) {
        if (StringUtils.isEmpty(arguments)) {
            return arguments;
        }

        // 先尝试直接解析
        try {
            JSON.parseObject(arguments, new TypeReference<Map<String, Object>>() {});
            return arguments;
        } catch (Exception ignored) {
            // 需要清理
        }

        log.debug("原始参数需要清理: {}", arguments);

        // 移除markdown代码块标记
        String cleaned = arguments
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();

        // 移除可能的前后缀文本，只保留JSON部分
        cleaned = extractJsonObject(cleaned);

        log.debug("清理后的参数: {}", cleaned);
        return cleaned;
    }

    /**
     * 从文本中提取JSON对象
     */
    private static String extractJsonObject(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        // 查找完整的JSON对象
        int braceCount = 0;
        int start = -1;
        int end = -1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                if (start == -1) {
                    start = i;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    end = i;
                    break;
                }
            }
        }

        if (start != -1 && end != -1) {
            String extracted = text.substring(start, end + 1);
            
            // 验证提取的JSON是否有效
            try {
                JSON.parseObject(extracted, new TypeReference<Map<String, Object>>() {});
                return extracted;
            } catch (Exception e) {
                log.warn("提取的JSON无效: {}, 错误: {}", extracted, e.getMessage());
                return text; // 返回原文本
            }
        }

        return text;
    }

    /**
     * 尝试修复常见的JSON格式问题
     */
    public static String fixCommonJsonIssues(String json) {
        if (StringUtils.isEmpty(json)) {
            return json;
        }

        // 移除尾随逗号
        String fixed = json.replaceAll(",\\s*([}\\]])", "$1");
        
        // 确保字符串值被引号包围
        // 这里只处理简单情况，复杂情况可能需要更复杂的解析
        
        return fixed;
    }
}