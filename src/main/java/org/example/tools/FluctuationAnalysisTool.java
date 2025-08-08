package org.example.tools;

import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolFunction;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 波动分析工具
 */
public class FluctuationAnalysisTool implements ComparisonTool {

    private static final Logger log = LoggerFactory.getLogger(FluctuationAnalysisTool.class);

    @Override
    public String getToolName() {
        return "analyze_fluctuation";
    }

    @Override
    public ToolFunction createTool() {
        String name = getToolName();
        String description = "分析两个数值的波动情况，提供详细的波动分析报告";

        String parameters = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "\"baseValue\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"基准值\"" +
                "}," +
                "\"compareValue\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"比较值\"" +
                "}," +
                "\"threshold\": {" +
                "\"type\": \"number\"," +
                "\"description\": \"波动阈值（例如：0.1表示10%），可选参数\"," +
                "\"default\": 0.3" +
                "}" +
                "}," +
                "\"required\": [\"baseValue\", \"compareValue\"]" +
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
            Number baseValue = (Number) params.get("baseValue");
            Number compareValue = (Number) params.get("compareValue");
            Number threshold = (Number) params.get("threshold");

            if (baseValue == null || compareValue == null) {
                return "参数错误: baseValue和compareValue不能为空";
            }

            double thresholdValue = threshold != null ? threshold.doubleValue() : 0.3;
            return analyzeFluctuation(baseValue.doubleValue(), compareValue.doubleValue(), thresholdValue);
        } catch (Exception e) {
            log.error("波动分析工具执行失败: {}", e.getMessage(), e);
            return "波动分析工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 检查数值波动范围，返回详细的波动分析
     */
    public String analyzeFluctuation(double baseValue, double compareValue, double threshold) {
        // 处理特殊值
        if (Double.isNaN(baseValue) || Double.isNaN(compareValue)) {
            return "无法分析波动: 包含NaN值";
        }

        if (Double.isInfinite(baseValue) || Double.isInfinite(compareValue)) {
            return "无法分析波动: 包含无穷大值";
        }

        // 处理阈值为负数的情况
        if (threshold < 0) {
            threshold = Math.abs(threshold);
        }

        // 如果两个值都为0，认为在波动范围内
        if (isZero(baseValue) && isZero(compareValue)) {
            return "波动分析: 两个值都为0，无波动";
        }

        // 如果基准值为0，但比较值不为0，则计算绝对变化
        if (isZero(baseValue)) {
            return String.format("波动分析: 基准值为0，比较值为%.6f，属于绝对变化", compareValue);
        }

        // 计算绝对差异
        double absoluteDiff = Math.abs(compareValue - baseValue);

        // 小数值特殊处理（绝对值≤10）
        if (Math.abs(baseValue) <= 10.0) {
            if (absoluteDiff <= 10.0) {
                return String.format("波动分析: 小数值基准值=%.2f, 比较值=%.2f, 绝对差异=%.2f, 在合理范围内(≤10)", 
                        baseValue, compareValue, absoluteDiff);
            } else {
                return String.format("波动分析: 小数值基准值=%.2f, 比较值=%.2f, 绝对差异=%.2f, 超出合理范围(>10)", 
                        baseValue, compareValue, absoluteDiff);
            }
        }

        // 正常数值处理
        // 使用BigDecimal进行精确计算（用于边界判断）
        BigDecimal base = BigDecimal.valueOf(baseValue);
        BigDecimal compare = BigDecimal.valueOf(compareValue);
        BigDecimal thresholdDecimal = BigDecimal.valueOf(threshold);

        // 计算波动范围
        BigDecimal absBase = base.abs();
        BigDecimal fluctuationRange = absBase.multiply(thresholdDecimal);

        // 计算上下界
        BigDecimal upperBound = base.add(fluctuationRange);
        BigDecimal lowerBound = base.subtract(fluctuationRange);

        // 计算相对变化率
        double relativeChange = (absoluteDiff / Math.abs(baseValue)) * 100.0;

        // 判断是否在波动范围内
        boolean withinRange = relativeChange <= (threshold * 100.0);

        // 双重验证：相对变化率和绝对范围都检查
        boolean withinAbsoluteRange = compare.compareTo(lowerBound) >= 0 && compare.compareTo(upperBound) <= 0;
        boolean finalResult = withinRange && withinAbsoluteRange;

        return String.format("波动分析: 基准值=%.2f, 比较值=%.2f, 相对变化=%.2f%%, 阈值=%.2f%%, %s", 
                baseValue, compareValue, relativeChange, threshold * 100, 
                finalResult ? "在波动范围内" : "超出波动范围");
    }

    /**
     * 简化版波动检查，根据给定阈值判断是否在范围内
     */
    public boolean isWithinFluctuation(double baseValue, double compareValue, double threshold) {
        String analysis = analyzeFluctuation(baseValue, compareValue, threshold);
        return analysis.contains("在波动范围内") || analysis.contains("无波动");
    }

    /**
     * 判断double值是否为0（考虑浮点数精度问题）
     */
    private static boolean isZero(double value) {
        return Math.abs(value) < 1e-9;
    }
}