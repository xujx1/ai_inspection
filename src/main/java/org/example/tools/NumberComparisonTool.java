package org.example.tools;

import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.dto.NumberComparisonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 数字比较工具
 */
public class NumberComparisonTool implements ComparisonTool {

    private static final Logger log = LoggerFactory.getLogger(NumberComparisonTool.class);

    @Override
    public String getToolName() {
        return "compare_numbers";
    }

    @Override
    public ToolFunction createTool() {
        String name = getToolName();
        String description = "比较两个数字值，返回绝对差异和相对差异的详细分析，不预设阈值让AI自行判断";

        String parameters = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "\"actual\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"实际数值\"" +
                "}," +
                "\"expected\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"期望数值\"" +
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
            Number actual = (Number) params.get("actual");
            Number expected = (Number) params.get("expected");

            if (actual == null || expected == null) {
                return "参数错误: actual和expected不能为空";
            }

            NumberComparisonResult result = compareNumbers(actual, expected);
            return result.toString();
        } catch (Exception e) {
            log.error("数字比较工具执行失败: {}", e.getMessage(), e);
            return "数字比较工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 比较数字类型，返回详细的差异分析
     */
    public NumberComparisonResult compareNumbers(Number actual, Number expected) {
        if (actual == null || expected == null) {
            boolean isEqual = Objects.equals(actual, expected);
            return new NumberComparisonResult(isEqual, 0.0, 0.0, 
                    isEqual ? "两个值都为null" : "其中一个值为null");
        }

        double actualDouble = actual.doubleValue();
        double expectedDouble = expected.doubleValue();

        // 处理特殊值
        if (Double.isNaN(actualDouble) && Double.isNaN(expectedDouble)) {
            return new NumberComparisonResult(true, 0.0, 0.0, "两个值都为NaN");
        }
        if (Double.isNaN(actualDouble) || Double.isNaN(expectedDouble)) {
            return new NumberComparisonResult(false, Double.NaN, Double.NaN, "其中一个值为NaN");
        }
        if (Double.isInfinite(actualDouble) && Double.isInfinite(expectedDouble)) {
            boolean isEqual = actualDouble == expectedDouble;
            return new NumberComparisonResult(isEqual, 0.0, 0.0, 
                    isEqual ? "两个值都为相同的无穷大" : "两个值为不同的无穷大");
        }
        if (Double.isInfinite(actualDouble) || Double.isInfinite(expectedDouble)) {
            return new NumberComparisonResult(false, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 
                    "其中一个值为无穷大");
        }

        // 计算绝对误差和相对误差
        double absoluteDiff = Math.abs(actualDouble - expectedDouble);
        double relativeDiff = 0.0;

        if (Math.max(Math.abs(actualDouble), Math.abs(expectedDouble)) != 0) {
            relativeDiff = absoluteDiff / Math.max(Math.abs(actualDouble), Math.abs(expectedDouble));
        }

        // 精确相等检查
        boolean isEqual = actualDouble == expectedDouble;

        String description;
        if (isEqual) {
            description = "数值完全相等";
        } else {
            description = String.format("数值不相等: 实际值=%.6f, 期望值=%.6f", actualDouble, expectedDouble);
        }

        return new NumberComparisonResult(isEqual, absoluteDiff, relativeDiff, description);
    }
}