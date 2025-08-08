package org.example.tools;

import com.alibaba.dashscope.tools.ToolBase;
import com.alibaba.dashscope.tools.ToolFunction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 比较工具管理器
 * 负责管理所有的比较工具函数
 */
@Component
public class ComparisonToolManager {

    private final NumberComparisonTool numberComparisonTool;
    private final StringComparisonTool stringComparisonTool;
    private final JsonComparisonTool jsonComparisonTool;
    private final FluctuationAnalysisTool fluctuationAnalysisTool;
    private final ThresholdComparisonTool thresholdComparisonTool;

    public ComparisonToolManager() {
        this.numberComparisonTool = new NumberComparisonTool();
        this.stringComparisonTool = new StringComparisonTool();
        this.jsonComparisonTool = new JsonComparisonTool();
        this.fluctuationAnalysisTool = new FluctuationAnalysisTool();
        this.thresholdComparisonTool = new ThresholdComparisonTool();
    }

    /**
     * 获取所有比较工具
     */
    public List<ToolBase> getAllTools() {
        List<ToolBase> tools = new ArrayList<>();
        tools.add(numberComparisonTool.createTool());
        tools.add(stringComparisonTool.createTool());
        tools.add(jsonComparisonTool.createTool());
        tools.add(fluctuationAnalysisTool.createTool());
        tools.add(thresholdComparisonTool.createTool());
        return tools;
    }

    /**
     * 执行工具调用
     */
    public String executeToolCall(String functionName, String arguments) {
        switch (functionName) {
            case "compare_numbers":
                return numberComparisonTool.execute(arguments);
            case "compare_strings":
                return stringComparisonTool.execute(arguments);
            case "compare_json":
                return jsonComparisonTool.execute(arguments);
            case "analyze_fluctuation":
                return fluctuationAnalysisTool.execute(arguments);
            case "compare_threshold":
                return thresholdComparisonTool.execute(arguments);
            default:
                return "未知的工具调用: " + functionName;
        }
    }

    /**
     * 创建工具描述说明
     */
    public String createToolsDescription() {
        return "\n\n【可用的数据比较工具】\n" + 
               "系统提供以下专业的数据比较工具，请在分析时考虑使用：\n\n" +
               
               "1. compare_numbers(actual, expected)\n" + 
               "   功能：比较两个数字值，返回绝对差异和相对差异的详细分析\n" + 
               "   输入：actual(实际数值), expected(期望数值)\n" + 
               "   输出：绝对差异、相对差异百分比、详细分析描述\n" + 
               "   示例：compare_numbers(1250, 1000) → 绝对差异: 250, 相对差异: 25.00%\n\n" +
               
               "2. compare_strings(actual, expected, filterKeywords)\n" + 
               "   功能：比较两个字符串，支持过滤关键字和数值提取比较\n" + 
               "   输入：actual(实际字符串), expected(期望字符串), filterKeywords(过滤关键字)\n" + 
               "   默认过滤：'分钟', '个', '1:', '```', '%'等单位字符\n" + 
               "   输出：过滤后的比较结果和数值分析\n\n" +
               
               "3. compare_json(actual, expected)\n" + 
               "   功能：深度比较两个JSON对象或数组的结构和值\n" + 
               "   输入：actual(实际JSON), expected(期望JSON)\n" + 
               "   输出：逐层比较结果，标识差异字段和值\n\n" +
               
               "4. analyze_fluctuation(baseValue, compareValue, threshold)\n" + 
               "   功能：分析两个数值的波动情况，提供详细的波动分析报告\n" + 
               "   输入：baseValue(基准值), compareValue(比较值), threshold(波动阈值，可选)\n" + 
               "   输出：波动百分比、是否在合理范围内、详细分析\n" + 
               "   【重要】阈值限制：最低不能低于10%，除非originalPrompt中明确指定\n\n" +
               
               "5. compare_threshold(actualValue, expectedValue, threshold)\n" + 
               "   功能：精确比较相对变化率与阈值，避免逻辑判断错误，返回明确的比较结果\n" + 
               "   输入：actualValue(实际值), expectedValue(期望值), threshold(阈值百分比)\n" + 
               "   输出：精确的比较结果，包含相对变化率计算过程和明确的判断结论\n" + 
               "   【重要】专门用于避免类似\"1.27%超过5%\"这种逻辑错误，提供准确的阈值判断\n\n" +
               
               "【使用建议】\n" + 
               "- 对于数值型数据，优先使用 compare_numbers 和 analyze_fluctuation\n" + 
               "- 对于包含单位的文本数据，使用 compare_strings 并指定过滤关键字\n" + 
               "- 对于结构化数据，使用 compare_json 进行深度分析\n" + 
               "- 【重要】当需要进行阈值比较时，必须使用 compare_threshold 工具，避免手动计算出现逻辑错误\n" + 
               "- 【严格禁止】使用低于10%的阈值，除非originalPrompt中明确指定\n" + 
               "- 【严格禁止】手动进行阈值比较判断，必须使用 compare_threshold 工具确保准确性\n" + 
               "- 【参数格式】调用工具时，参数必须是纯JSON格式\n\n";
    }

    /**
     * 创建分析指南
     */
    public String createAnalysisGuide() {
        return "【数据比较分析指南】\n" + 
               "请使用上述工具进行专业的数据比较分析：\n\n" + 
               "1. 数值比较策略：\n" + 
               "   - 使用 compare_numbers 获取精确的绝对差异和相对差异\n" + 
               "   - 根据业务场景判断差异是否合理，不预设固定阈值\n" + 
               "   - 考虑数值的量级和业务重要性\n\n" +
               
               "2. 字符串处理策略：\n" + 
               "   - 使用 compare_strings 自动过滤无关字符\n" + 
               "   - 智能提取数值进行专业比较\n" + 
               "   - 处理带单位的业务指标\n\n" +
               
               "3. 阈值设置原则（重要）：\n" + 
               "   - 【严格禁止】使用低于10%的阈值，除非originalPrompt中明确指定\n" + 
               "   - 核心业务指标：10-20的波动范围\n" + 
               "   - 一般业务指标：15-35的波动范围\n" + 
               "   - 辅助统计指标：25-55的波动范围\n\n" +
               
               "4. 结构化数据分析：\n" + 
               "   - 使用 compare_json 进行深度递归比较\n" + 
               "   - 逐层检查字段值和数据结构\n" + 
               "   - 识别缺失或新增的数据字段\n\n" +
               
               "【分析重点】\n" + 
               "- 业务核心指标的变化幅度和趋势\n" + 
               "- 数据格式的一致性和完整性\n" + 
               "- 统计维度的准确性\n" + 
               "- 计算逻辑的正确性\n" + 
               "- 【核心】只报告真正异常的数据，正常波动范围内的数据绝对不输出\n\n";
    }
}