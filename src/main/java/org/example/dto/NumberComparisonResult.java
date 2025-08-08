package org.example.dto;

public class NumberComparisonResult {

    private boolean isEqual;
    private double absoluteDifference;
    private double relativeDifference;
    private String description;

    public NumberComparisonResult(boolean isEqual, double absoluteDifference, double relativeDifference, String description) {
        this.isEqual = isEqual;
        this.absoluteDifference = absoluteDifference;
        this.relativeDifference = relativeDifference;
        this.description = description;
    }

    public boolean isEqual() {
        return isEqual;
    }

    public double getAbsoluteDifference() {
        return absoluteDifference;
    }

    public double getRelativeDifference() {
        return relativeDifference;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("差异分析: %s (绝对差异: %.6f, 相对差异: %.2f%%)",
                description, absoluteDifference, relativeDifference * 100);
    }
}
