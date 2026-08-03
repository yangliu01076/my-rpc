package com.my.response;

import lombok.Data;

/**
 * @author duoyian
 * @date 2026/7/31
 */
@Data
public class RpcResponse implements java.io.Serializable {
    private static final long serialVersionUID = 2629185170470591528L;

    private String requestId;

    private Object data;       // 真实返回结果

    private String error;      // 异常信息

    public static RpcResponse success(String requestId, Object data) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setData(data);
        return response;
    }

    public static RpcResponse fail(String requestId, String error) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setError(error);
        return response;
    }
}
