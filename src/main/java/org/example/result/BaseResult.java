package org.example.result;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public abstract class BaseResult<D> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2360806571162643908L;

    public BaseResult() {
        this.success = true;
    }

    /**
     * 【强制】标记请求是否成功
     */
    private boolean success;
    /**
     * 【强制】请求错误code，业务系统定义，如果请求错误
     */
    private String errorCode;
    /**
     * 【建议】调用错误消息返回值
     */
    private String errorMsg;
    /**
     * 【可选】错误扩展信息，如：按照bu csrf token处理规范，csrf过期，需要返回新的csrf token。则可以用该扩展字段
     */
    private Map<String, String> errorCtx;
    /**
     * 【可选】错误级别，业务系统自己定义
     */
    private String errorLevel;

    /**
     * 【强制】数据正常返回值字段
     */
    protected D content;

    /**
     * 结果子类必须根据实际情况，重写值获取方式
     */
    public abstract D getContent();

    /**
     * 结果子类必须重写根据实际情况值封装
     */
    public abstract void setContent(D content);

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setError(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Map<String, String> getErrorCtx() {
        return errorCtx;
    }

    public void setErrorCtx(Map<String, String> errorCtx) {
        this.errorCtx = errorCtx;
    }

    public String getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(String errorLevel) {
        this.errorLevel = errorLevel;
    }

}
