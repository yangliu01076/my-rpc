package com.my.net.codec;

import com.my.spi.Serialization;
import com.my.spi.loader.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.StringUtil;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class SerializationDecoder extends ByteToMessageDecoder {

    private final String serializationName;

    public SerializationDecoder(String serializationName) {
        this.serializationName = serializationName;
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 1. 防止半包读取
        in.markReaderIndex();

        // 2. 读类名长度（4字节）
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        int classNameLen = in.readInt();

        // 3. 读类名 + 数据长度（4字节）
        if (in.readableBytes() < classNameLen + 4) {
            in.resetReaderIndex();
            return;
        }
        byte[] classNameBytes = new byte[classNameLen];
        in.readBytes(classNameBytes);
        String className = new String(classNameBytes, StandardCharsets.UTF_8);

        Class<?> clazz = Class.forName(className);

        // 4. 读数据长度并校验
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        int dataLen = in.readInt();
        if (in.readableBytes() < dataLen) {
            in.resetReaderIndex();
            return;
        }

        // 5. 读真实数据并通过 SPI 反序列化
        byte[] data = new byte[dataLen];
        in.readBytes(data);

        ExtensionLoader<Serialization> extensionLoader = ExtensionLoader
                .getExtensionLoader(Serialization.class);
        Serialization serialization = StringUtil.isNullOrEmpty(serializationName)
                ? extensionLoader.getDefaultExtension()
                : extensionLoader.getExtension(serializationName);

        Object obj = serialization.deserialize(data, clazz);
        out.add(obj);
    }
}
