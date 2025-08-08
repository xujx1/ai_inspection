package org.example.query;

import java.io.Serializable;

public class InspectionPromptQuery implements Serializable {

    private String tag;
    private String promptName;
    private String methodName;

    // Getters and Setters
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

    @Override
    public String toString() {
        return "InspectionPromptQuery{" +
                "tag='" + tag + '\'' +
                ", promptName='" + promptName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}