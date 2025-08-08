package org.example.dto;

/**
 * 巡检标签传输对象
 */
public class InspectionTagDTO {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 创建时间（字符串格式）
     */
    private String gmtCreate;

    /**
     * 修改时间（字符串格式）
     */
    private String gmtModified;

    /**
     * 是否删除标记
     */
    private String isDeleted;

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

    /**
     * 登录账号
     */
    private String account;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 登录URL
     */
    private String loginUrl;

    /**
     * 巡检URL
     */
    private String inspectionUrl;

    // 构造函数
    public InspectionTagDTO() {
    }

    // Getter和Setter方法
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getInspectionUrl() {
        return inspectionUrl;
    }

    public void setInspectionUrl(String inspectionUrl) {
        this.inspectionUrl = inspectionUrl;
    }

    @Override
    public String toString() {
        return "InspectionTagDTO{" +
                "id=" + id +
                ", gmtCreate='" + gmtCreate + '\'' +
                ", gmtModified='" + gmtModified + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", tag='" + tag + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagDesc='" + tagDesc + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", loginUrl='" + loginUrl + '\'' +
                ", inspectionUrl='" + inspectionUrl + '\'' +
                '}';
    }
}