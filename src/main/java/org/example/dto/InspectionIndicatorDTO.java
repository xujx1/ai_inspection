package org.example.dto;

import java.io.Serializable;

public class InspectionIndicatorDTO implements Serializable {

    /**
     * 指标code
     */
    private String indicatorCode;

    /**
     * 指标描述
     */
    private String indicatorName;

    /**
     * 实际结果
     */
    private String actualValue;

    /**
     * 期望结果
     */
    private String expectedValue;

    /**
     * 理由
     */
    private String reason;

    // Getters and Setters
    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
