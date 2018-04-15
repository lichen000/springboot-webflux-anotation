package mangolost.webfluxdemo.common;

import java.io.Serializable;

/**
 *
 */
public class CommonResult implements Serializable {

    public static final long serialVersionUID = 1L;

    private int code = 200;
    private String message = "OK";
    private Object data = null;
    private long ts = System.currentTimeMillis();

    public CommonResult() {

    }

    public CommonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCodeAndMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", ts=" + ts +
                '}';
    }
}