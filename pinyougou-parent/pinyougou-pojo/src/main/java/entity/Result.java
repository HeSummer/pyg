package entity;

import java.io.Serializable;

/**
 * 返回结果类的封装
 * @author Aming.he
 * @Time   2019.6.24
 * @version  1.0
 * */

public class Result implements Serializable {
    private boolean success; //结果
    private String message;  //提示信息
    public Result(){}
    public Result(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
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
}
