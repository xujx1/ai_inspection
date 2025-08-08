package org.example.entity;

import java.time.LocalDateTime;

/**
 * 巡检结果实体类
 * 对应表：inspection_result
 */
public class InspectionResult {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 是否删除标记
     */
    private String isDeleted;



    /**
     * 业务标签/类型
     */
    private String tag;

    /**
     * 工号
     */
    private String workNo;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 请求参数
     */
    private String req;

    /**
     * 上一次接口返回
     */
    private String lastResp;

    /**
     * 本次接口返回
     */
    private String thisResp;

    /**
     * 执行结果(Y-通过, N-不通过, U-未检查)
     */
    private String checkFlag;

    /**
     * 错误信息/不一致指标
     */
    private String errorMsg;

    /**
     * 巡检方式(api-接口, page-页面)
     */
    private String inspectionType;

    // Constructors
    public InspectionResult() {}

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

    @Override
    public String toString() {
        return "InspectionResult{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted='" + isDeleted + '\'' +
        
                ", tag='" + tag + '\'' +
                ", workNo='" + workNo + '\'' +
                ", methodName='" + methodName + '\'' +
                ", req='" + req + '\'' +
                ", lastResp='" + lastResp + '\'' +
                ", thisResp='" + thisResp + '\'' +
                ", checkFlag='" + checkFlag + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", inspectionType='" + inspectionType + '\'' +
                '}';
    }
}