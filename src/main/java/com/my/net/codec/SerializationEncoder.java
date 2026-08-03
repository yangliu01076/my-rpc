package com.my.net.codec;

import com.my.spi.Serialization;
import com.my.spi.loader.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.StringUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class SerializationEncoder extends MessageToByteEncoder<Object> {

    private final String serializationName;

    public SerializationEncoder(String serializationName) {
        this.serializationName = serializationName;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        // 1. 通过 SPI 拿到序列化器
        ExtensionLoader<Serialization> extensionLoader = ExtensionLoader
                .getExtensionLoader(Serialization.class);
        Serialization serialization = StringUtil.isNullOrEmpty(serializationName)
                ? extensionLoader.getDefaultExtension()
                : extensionLoader.getExtension(serializationName);

        // 2. 序列化对象
        byte[] data = serialization.serialize(msg);

        // 3. 写入类型标识（类名）
        byte[] classNameBytes = msg.getClass().getName().getBytes(StandardCharsets.UTF_8);
        out.writeInt(classNameBytes.length);
        out.writeBytes(classNameBytes);

        // 4. 写入数据长度和真实数据
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
