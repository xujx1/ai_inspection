package org.example.enums;

public enum CheckFlagEnum {

    ALL("ALL", "所有"),

    U("U", "未执行"),


    N("N", "未通过"),


    Y("Y", "通过");

    private final String code;
    private final String description;

    CheckFlagEnum(String code, String description) {
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
    public static CheckFlagEnum getByCode(String code) {
        for (CheckFlagEnum inspectionTypeEnum : values()) {
            if (inspectionTypeEnum.getCode().equals(code)) {
                return inspectionTypeEnum;
            }
        }
        return null;
    }
}
