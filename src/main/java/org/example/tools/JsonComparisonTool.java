package org.example.tools;

import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * JSON比较工具
 */
public class JsonComparisonTool implements ComparisonTool {

    private static final Logger log = LoggerFactory.getLogger(JsonComparisonTool.class);
    private final StringComparisonTool stringComparisonTool;

    public JsonComparisonTool() {
        this.stringComparisonTool = new StringComparisonTool();
    }

    @Override
    public String getToolName() {
        return "compare_json";
    }

    @Override
    public ToolFunction createTool() {
        String name = getToolName();
        String description = "深度比较两个JSON对象或数组的结构和值";

        String parameters = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "\"actual\": {" +
                "\"type\": \"string\"," +
                "\"description\": \"实际JSON字符串\"" +
                "}," +
                "\"expected\": {" +
                "\"type\": \"string\"," +
                "\"description\": \"期望JSON字符串\"" +
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

            return compareAsJson(actual, expected);
        } catch (Exception e) {
            log.error("JSON比较工具执行失败: {}", e.getMessage(), e);
            return "JSON比较工具执行失败: " + e.getMessage();
        }
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
            // 使用数字比较工具
            NumberComparisonTool numberTool = new NumberComparisonTool();
            return numberTool.compareNumbers((Number) actual, (Number) expected).toString();
        }

        if (actual instanceof String && expected instanceof String) {
            return stringComparisonTool.compareString((String) actual, (String) expected);
        }

        boolean isEqual = Objects.equals(actual, expected);
        return isEqual ? "对象相等" : 
                String.format("对象不相等: 实际值=%s, 期望值=%s", actual, expected);
    }
}