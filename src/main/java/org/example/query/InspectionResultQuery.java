package org.example.query;

import java.io.Serializable;

public class InspectionResultQuery implements Serializable {


    private String workNo;
    private String tag;
    private String methodName;
    private String checkFlag;
    private String inspectionType;

    // Getters and Setters


    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    @Override
    public String toString() {
        return "InspectionQuery{" +
        
                ", workNo='" + workNo + '\'' +
                ", tag='" + tag + '\'' +
                ", methodName='" + methodName + '\'' +
                ", checkFlag='" + checkFlag + '\'' +
                ", inspectionType='" + inspectionType + '\'' +
                '}';
    }
}