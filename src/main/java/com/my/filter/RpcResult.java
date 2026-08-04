package com.my.filter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author duoyian
 * @since 2026/8/4
 */
@Data
public class RpcResult implements Serializable {
    private static final long serialVersionUID = 6242929265043455691L;

    private Object data;
    private Throwable exception;
    private String exceptionMessage;

    public RpcResult() {
    }

    public RpcResult(Object data) {
        this.data = data;
    }
}
