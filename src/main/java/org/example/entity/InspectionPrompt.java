package org.example.entity;

import java.util.Date;

/**
 * 巡检Prompt实体类
 */
public class InspectionPrompt {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 是否删除标记
     */
    private String isDeleted;

    /**
     * 业务标签/类型
     */
    private String tag;

    /**
     * Prompt名称
     */
    private String promptName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * Prompt内容
     */
    private String promptContent;

    // 构造函数
    public InspectionPrompt() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPromptName() {
        return promptName;
    }

    public void setPromptName(String promptName) {
        this.promptName = promptName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPromptContent() {
        return promptContent;
    }

    public void setPromptContent(String promptContent) {
        this.promptContent = promptContent;
    }

    @Override
    public String toString() {
        return "InspectionPrompt{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted='" + isDeleted + '\'' +
                ", tag='" + tag + '\'' +
                ", promptName='" + promptName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", promptContent='" + promptContent + '\'' +
                '}';
    }
}