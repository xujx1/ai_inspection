package org.example.query;

/**
 * 巡检标签查询对象
 */
public class InspectionTagQuery {

    /**
     * 业务标签/类型
     */
    private String tag;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签描述
     */
    private String tagDesc;

    // 构造函数
    public InspectionTagQuery() {
    }

    public InspectionTagQuery(String tag, String tagName, String tagDesc) {
        this.tag = tag;
        this.tagName = tagName;
        this.tagDesc = tagDesc;
    }

    // Getter和Setter方法
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public void setTagDesc(String tagDesc) {
        this.tagDesc = tagDesc;
    }

    @Override
    public String toString() {
        return "InspectionTagQuery{" +
                "tag='" + tag + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagDesc='" + tagDesc + '\'' +
                '}';
    }
}