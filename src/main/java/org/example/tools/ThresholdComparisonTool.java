package org.example.tools;

import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 阈值比较工具
 */
public class ThresholdComparisonTool implements ComparisonTool {

    private static final Logger log = LoggerFactory.getLogger(ThresholdComparisonTool.class);

    @Override
    public String getToolName() {
        return "compare_threshold";
    }

    @Override
    public ToolFunction createTool() {
        String name = getToolName();
        String description = "精确比较相对变化率与阈值，避免逻辑判断错误，返回明确的比较结果";

        String parameters = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "\"actualValue\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"实际值\"" +
                "}," +
                "\"expectedValue\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"期望值\"" +
                "}," +
                "\"threshold\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"阈值百分比（例如：5表示5%）\"" +
                "}" +
                "}," +
                "\"required\": [\"actualValue\", \"expectedValue\", \"threshold\"]" +
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
            Number actualValue = (Number) params.get("actualValue");
            Number expectedValue = (Number) params.get("expectedValue");
            Number threshold = (Number) params.get("threshold");

            if (actualValue == null || expectedValue == null || threshold == null) {
                return "参数错误: actualValue、expectedValue和threshold不能为空";
            }

            return compareThreshold(actualValue.doubleValue(), expectedValue.doubleValue(), threshold.doubleValue());
        } catch (Exception e) {
            log.error("阈值比较工具执行失败: {}", e.getMessage(), e);
            return "阈值比较工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 精确的阈值比较，避免逻辑判断错误
     */
    public String compareThreshold(double actualValue, double expectedValue, double thresholdPercent) {
        // 处理特殊值
        if (Double.isNaN(actualValue) || Double.isNaN(expectedValue)) {
            return "阈值比较失败: 包含NaN值";
        }

        if (Double.isInfinite(actualValue) || Double.isInfinite(expectedValue)) {
            return "阈值比较失败: 包含无穷大值";
        }

        // 如果期望值为0，特殊处理
        if (isZero(expectedValue)) {
            if (isZero(actualValue)) {
                return String.format("阈值比较结果: 实际值=%.6f, 期望值=%.6f, 相对变化率=0%%, 阈值=%.2f%%, 结果=在阈值范围内", 
                        actualValue, expectedValue, thresholdPercent);
            } else {
                return String.format("阈值比较结果: 实际值=%.6f, 期望值=%.6f(为0), 无法计算相对变化率, 属于绝对变化", 
                        actualValue, expectedValue);
            }
        }

        // 计算相对变化率
        double absoluteDiff = Math.abs(actualValue - expectedValue);
        double relativeChangePercent = (absoluteDiff / Math.abs(expectedValue)) * 100.0;

        // 确保阈值最低为10%
        double adjustedThreshold = thresholdPercent;
        if (thresholdPercent < 10.0) {
            adjustedThreshold = 10.0;
            log.warn("阈值{}%过低，自动调整为10%", thresholdPercent);
        }

        // 进行精确的比较
        boolean isWithinThreshold = relativeChangePercent <= adjustedThreshold;

        // 构建详细的比较结果
        String comparisonOperator = relativeChangePercent <= adjustedThreshold ? "≤" : ">";
        String result = isWithinThreshold ? "在阈值范围内" : "超出阈值范围";

        return String.format("阈值比较结果: 实际值=%.6f, 期望值=%.6f, 相对变化率=%.2f%%, %s %.2f%%(阈值), 结果=%s", 
                actualValue, expectedValue, relativeChangePercent, comparisonOperator, adjustedThreshold, result);
    }

    /**
     * 判断double值是否为0（考虑浮点数精度问题）
     */
    private static boolean isZero(double value) {
        return Math.abs(value) < 1e-9;
    }
}