package org.example.entity;

import java.time.LocalDateTime;

/**
 * 巡检结果数据对象
 * 用于适配原有的EpmInspectionResultDO
 */
public class EpmInspectionResultDO {
    
    private Integer id;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String isDeleted;
    private String env;
    private String tag;
    private String workNo;
    private String methodName;
    private String req;
    private String lastResp;
    private String thisResp;
    private String checkFlag;
    private String errorMsg;
    private String inspectionType;
    private String bunitTag;

    // Constructors
    public EpmInspectionResultDO() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getLastResp() {
        return lastResp;
    }

    public void setLastResp(String lastResp) {
        this.lastResp = lastResp;
    }

    public String getThisResp() {
        return thisResp;
    }

    public void setThisResp(String thisResp) {
        this.thisResp = thisResp;
    }

    public String getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public String getBunitTag() {
        return bunitTag;
    }

    public void setBunitTag(String bunitTag) {
        this.bunitTag = bunitTag;
    }
}