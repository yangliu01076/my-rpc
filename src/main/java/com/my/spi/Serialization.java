package com.my.spi;

import com.my.spi.annotation.SPI;

import java.io.IOException;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@SPI("protostuff") // 默认用 protostuff
public interface Serialization {
    byte[] serialize(Object obj) throws IOException;
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException;
}
