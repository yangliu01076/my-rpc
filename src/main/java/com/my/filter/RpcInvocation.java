package com.my.filter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author duoyian
 * @since 2026/8/4
 */
@Data
public class RpcInvocation implements Serializable {
    private static final long serialVersionUID = 4611217550293189451L;

    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
