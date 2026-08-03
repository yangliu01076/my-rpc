package com.my.net.codec;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class ProtostuffEncoder extends MessageToByteEncoder<Object> {
    private static final Map<Class<?>, Schema<?>> CACHED_SCHEMA = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Class<?> clazz = msg.getClass();
        Schema<Object> schema = (Schema<Object>) RuntimeSchema.getSchema(clazz);

        // 1. 先把类名字节算出来
        byte[] classNameBytes = clazz.getName().getBytes(StandardCharsets.UTF_8);

        // 2. 序列化对象本身
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(msg, schema, buffer);
        } finally {
            buffer.clear();
        }

        // 3. 按顺序写入 ByteBuf（注意：这个顺序要和 Decoder 读取顺序严格对应！）
        out.writeInt(classNameBytes.length);   // 先写类名长度（4字节）
        out.writeBytes(classNameBytes);         // 再写类名字节
        out.writeInt(data.length);              // 再写数据长度（4字节）
        out.writeBytes(data);                   // 最后写真实数据
    }
}
