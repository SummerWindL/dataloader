package com.advance.dataloader.common.http.enums;

/**
 * @description:
 * @author: fuyanliang
 * @create: 2021-11-13 17:42
 **/
public enum ResponseResult {
    // +++++++++++++++++++++++++++++++++++++++++++++++++
    // ++++++++++++++++++基础级别 错误+++++++++++++++++++++++
    // +++++++++++++++++++++++++++++++++++++++++++++++++
    COMMON_ERROR(-100, "基础错误"),
    COMMON_ERROR_EXCEPTION(-101, "异常错误"),
    COMMON_ERROR_UNKNOWN(-102, "未知错误"),
    COMMON_ERROR_INVALID_PARAM(-103, "无效参数"),
    COMMON_ERROR_NULL_POINTER(-104, "空指针"),
    COMMON_ERROR_ALLOC_FAILED(-105, "分配内存错误"),
    COMMON_ERROR_CONVERT_PARAM(-106, "参数类型转换错误"),
    COMMON_ERROR_PARAM_NOT_NULL(-107, "参数不能为空"),

    // ++++++++++++++++++成功+++++++++++++++++++++++
    DT_SUCCESS(0, "成功"),
    COMMON_SUCCESS(0, "成功");


    private int resultcode;

    private String resultmsg;

    private ResponseResult(int resultcode, String resultmsg) {
        this.resultcode = resultcode;
        this.resultmsg = resultmsg;
    }

    public int getResultcode() {
        return resultcode;
    }

    public void setResultcode(int resultcode) {
        this.resultcode = resultcode;
    }

    public String getResultmsg() {
        return resultmsg;
    }

    public void setResultmsg(String resultmsg) {
        this.resultmsg = resultmsg;
    }
}
