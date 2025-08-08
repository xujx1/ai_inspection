package org.example.enums;

/**
 * 是否删除标记枚举
 */
public enum YNTypeEnum {
    /**
     * 是
     */
    Y("Y", "是"),
    
    /**
     * 否
     */
    N("N", "否");

    private final String code;
    private final String description;

    YNTypeEnum(String code, String description) {
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
    public static YNTypeEnum getByCode(String code) {
        for (YNTypeEnum ynTypeEnum : values()) {
            if (ynTypeEnum.getCode().equals(code)) {
                return ynTypeEnum;
            }
        }
        return null;
    }
}