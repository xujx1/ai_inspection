package org.example.tools;

import com.alibaba.dashscope.tools.ToolFunction;

/**
 * 比较工具接口
 */
public interface ComparisonTool {
    
    /**
     * 创建工具函数定义
     */
    ToolFunction createTool();
    
    /**
     * 执行工具调用
     */
    String execute(String arguments);
    
    /**
     * 获取工具名称
     */
    String getToolName();
}