package org.example.dto;

import java.io.Serializable;

public class InspectionResultDTO implements Serializable {

    private Integer id;
    private String gmtCreate;
    private String gmtModified;
    private String isDeleted;

    private String tag;
    private String workNo;
    private String showName;
    private String methodName;
    private String checkFlag;
    private String req;
    private String prompt;
    private String lastResp;
    private String thisResp;
    private String inspectionType;
    private String errorMsg;

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

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
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

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "InspectionDTO{" +
                "id=" + id +
                ", gmtCreate='" + gmtCreate + '\'' +
                ", gmtModified='" + gmtModified + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
        
                ", tag='" + tag + '\'' +
                ", workNo='" + workNo + '\'' +
                ", showName='" + showName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", checkFlag='" + checkFlag + '\'' +
                ", req='" + req + '\'' +
                ", prompt='" + prompt + '\'' +
                ", lastResp='" + lastResp + '\'' +
                ", thisResp='" + thisResp + '\'' +
                ", inspectionType='" + inspectionType + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}