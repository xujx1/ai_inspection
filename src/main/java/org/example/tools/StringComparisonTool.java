package org.example.tools;

import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.dto.NumberComparisonResult;
import org.example.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 字符串比较工具
 */
public class StringComparisonTool implements ComparisonTool {

    private static final Logger log = LoggerFactory.getLogger(StringComparisonTool.class);
    private final NumberComparisonTool numberComparisonTool;

    public StringComparisonTool() {
        this.numberComparisonTool = new NumberComparisonTool();
    }

    @Override
    public String getToolName() {
        return "compare_strings";
    }

    @Override
    public ToolFunction createTool() {
        String name = getToolName();
        String description = "比较两个字符串，支持过滤关键字和数值提取比较";

        String parameters = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "\"actual\": {" +
                "\"type\": \"string\"," +
                "\"description\": \"实际字符串\"" +
                "}," +
                "\"expected\": {" +
                "\"type\": \"string\"," +
                "\"description\": \"期望字符串\"" +
                "}," +
                "\"filterKeywords\": {" +
                "\"type\": \"array\"," +
                "\"items\": {\"type\": \"string\"}," +
                "\"description\": \"需要过滤的关键字列表\"," +
                "\"default\": [\"分钟\", \"个\", \"1:\", \"```\", \"%\"]" +
                "}" +
                "}," +
                "\"required\": [\"actual\", \"expected\"]" +
                "}";

        JsonObject jsonObject = JsonParser.parseString(parameters).getAsJsonObject();
        FunctionDefinition functionDefinition = FunctionDefinition.builder()
                .name(name)
                .description(description)
                .parameters(jsonObject)
                .build();

        return ToolFunction.builder().function(functionDefinition).build();
    }

    @Override
    public String execute(String arguments) {
        try {
            Map<String, Object> params = JSON.parseObject(arguments, new TypeReference<Map<String, Object>>() {});
            String actual = (String) params.get("actual");
            String expected = (String) params.get("expected");

            if (actual == null || expected == null) {
                return "参数错误: actual和expected不能为空";
            }

            return compareString(actual, expected);
        } catch (Exception e) {
            log.error("字符串比较工具执行失败: {}", e.getMessage(), e);
            return "字符串比较工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 比较字符串
     */
    public String compareString(String actual, String expected) {
        if (actual == null || expected == null) {
            boolean isEqual = Objects.equals(actual, expected);
            return isEqual ? "字符串相等(都为null)" : "字符串不相等(其中一个为null)";
        }

        // 检查是否都是数字
        if (NumberUtils.isNumber(actual) && NumberUtils.isNumber(expected)) {
            NumberComparisonResult result = numberComparisonTool.compareNumbers(
                    Double.valueOf(actual), Double.valueOf(expected));
            return "数字字符串比较: " + result.toString();
        }

        if (JsonUtils.isJson(actual) && JsonUtils.isJson(expected)) {
            return "JSON比较: " + compareAsJson(actual, expected);
        }

        boolean isEqual = actual.equals(expected);
        return isEqual ? "字符串相等" : 
                String.format("字符串不相等: 实际值='%s', 期望值='%s'", actual, expected);
    }

    /**
     * 替换关键字
     */
    private String replaceKeywords(String text, List<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return text;
        }

        String[] keywordArray = keywords.toArray(new String[0]);
        String[] emptyArray = new String[keywords.size()];
        Arrays.fill(emptyArray, "");

        return StringUtils.replaceEach(text, keywordArray, emptyArray);
    }

    /**
     * JSON比较
     */
    public String compareAsJson(String actualJson, String expectedJson) {
        try {
            if (StringUtils.isEmpty(actualJson) || StringUtils.isEmpty(expectedJson)) {
                boolean isEqual = Objects.equals(actualJson, expectedJson);
                return isEqual ? "JSON字符串相等(都为空)" : "JSON字符串不相等(其中一个为空)";
            }

            Object actualObj = JSON.parse(actualJson);
            Object expectedObj = JSON.parse(expectedJson);

            return deepCompareObjects(actualObj, expectedObj);
        } catch (Exception e) {
            log.error("JSON比较失败: {}", e.getMessage());
            return "JSON解析失败: " + e.getMessage();
        }
    }

    /**
     * 深度比较对象
     */
    private String deepCompareObjects(Object actual, Object expected) {
        if (actual == null && expected == null) {
            return "对象相等(都为null)";
        }
        if (actual == null || expected == null) {
            return "对象不相等(其中一个为null)";
        }

        if (actual instanceof Number && expected instanceof Number) {
            NumberComparisonResult result = numberComparisonTool.compareNumbers(
                    (Number) actual, (Number) expected);
            return result.toString();
        }

        if (actual instanceof String && expected instanceof String) {
            return compareString((String) actual, (String) expected);
        }

        boolean isEqual = Objects.equals(actual, expected);
        return isEqual ? "对象相等" : 
                String.format("对象不相等: 实际值=%s, 期望值=%s", actual, expected);
    }
}