package org.example.result;

/**
 * 处理结果类
 */
public class ProcessResult {
    
    private boolean success;
    private String message;
    private Object data;

    public ProcessResult() {
    }

    public ProcessResult(boolean success) {
        this.success = success;
    }

    public ProcessResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ProcessResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProcessResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}