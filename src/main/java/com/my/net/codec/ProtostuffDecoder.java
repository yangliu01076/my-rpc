package com.my.net.codec;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.my.request.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author duoyian
 * @date 2026/8/3
 */
public class ProtostuffDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final Map<Class<?>, Schema<?>> CACHED_SCHEMA = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. 标记当前读取位置，防止半包时数据丢失（TCP 拆包保护）
        in.markReaderIndex();

        // 2. 至少要读到类名长度（4字节）才能继续
        if (in.readableBytes() < 4) {
            in.resetReaderIndex(); // 回到起点，等下次有数据再读
            return;
        }
        int classNameLen = in.readInt();

        // 3. 确保类名和数据长度都到了（4字节类名长度 + 类名长度 + 4字节数据长度）
        if (in.readableBytes() < classNameLen + 4) {
            in.resetReaderIndex();
            return;
        }

        // 4. 读类名
        byte[] classNameBytes = new byte[classNameLen];
        in.readBytes(classNameBytes);
        String className = new String(classNameBytes, StandardCharsets.UTF_8);

        // 5. 读数据长度
        int dataLen = in.readInt();
        if (in.readableBytes() < dataLen) {
            in.resetReaderIndex(); // 数据还没到齐，等下一次
            return;
        }

        // 6. 读真实数据并反序列化
        byte[] data = new byte[dataLen];
        in.readBytes(data);

        Class<?> clazz = Class.forName(className);
        Schema<Object> schema = (Schema<Object>) RuntimeSchema.getSchema(clazz);
        Object obj = clazz.getDeclaredConstructor().newInstance(); // 反射实例化（类必须有无参构造！）
        ProtostuffIOUtil.mergeFrom(data, obj, schema);

        // 7. 放入结果列表，交给下一个 Handler
        out.add(obj);
    }
}
