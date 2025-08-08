package org.example.dto;

/**
 * 巡检Prompt数据传输对象
 */
public class InspectionPromptDTO {

    private Integer id;
    private String gmtCreate;
    private String gmtModified;
    private String isDeleted;
    private String tag;
    private String promptName;
    private String methodName;
    private String promptContent;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
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
        return "InspectionPromptDTO{" +
                "id=" + id +
                ", gmtCreate='" + gmtCreate + '\'' +
                ", gmtModified='" + gmtModified + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", tag='" + tag + '\'' +
                ", promptName='" + promptName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", promptContent='" + promptContent + '\'' +
                '}';
    }
}