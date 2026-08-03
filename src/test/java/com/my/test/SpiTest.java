package com.my.test;

import com.my.request.RpcRequest;
import com.my.spi.Serialization;
import com.my.spi.loader.ExtensionLoader;

import java.io.IOException;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class SpiTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ExtensionLoader<Serialization> loader = ExtensionLoader.getExtensionLoader(Serialization.class);
        ExtensionLoader<Serialization> loader2 = ExtensionLoader.getExtensionLoader(Serialization.class);

        // 按需加载
        Serialization protostuff = loader.getExtension("hessian");
        System.out.println("protostuff: " + protostuff.getClass().getSimpleName());

        // 加载默认实现
        Serialization defaultSer = loader.getDefaultExtension();
        System.out.println("默认实现: " + defaultSer.getClass().getSimpleName());

        // 测试序列化和反序列化
        RpcRequest req = new RpcRequest();
        req.setRequestId("123");
        byte[] data = protostuff.serialize(req);
        RpcRequest deserialized = protostuff.deserialize(data, RpcRequest.class);
        System.out.println("反序列化结果: " + deserialized.getRequestId());
    }
}
