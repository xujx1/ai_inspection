package org.example.enums;

/**
 * 巡检类型枚举
 */
public enum InspectionTypeEnum {
    /**
     * API接口巡检
     */
    API("api", "API接口巡检"),
    
    /**
     * 页面巡检
     */
    PAGE("page", "页面巡检");

    private final String code;
    private final String description;

    InspectionTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static InspectionTypeEnum getByCode(String code) {
        for (InspectionTypeEnum inspectionEnum : values()) {
            if (inspectionEnum.getCode().equals(code)) {
                return inspectionEnum;
            }
        }
        return null;
    }
}