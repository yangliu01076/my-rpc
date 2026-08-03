package com.my.spi.impl;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.my.spi.Serialization;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class ProtostuffSerialization implements Serialization {
    @Override
    @SuppressWarnings("unchecked")
    public byte[] serialize(Object obj) {
        Schema<Object> schema = (Schema<Object>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("反序列化失败：无法实例化 " + clazz.getName(), e);
        }
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}
