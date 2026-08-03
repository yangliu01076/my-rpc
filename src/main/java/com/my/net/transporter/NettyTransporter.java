package com.my.net.transporter;

import com.my.net.Transporter;
import com.my.net.codec.ProtostuffDecoder;
import com.my.net.codec.ProtostuffEncoder;
import com.my.net.handler.RequestHandler;
import com.my.request.RpcRequest;
import com.my.response.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author duoyian
 * @date 2026/8/2
 */
public class NettyTransporter implements Transporter {

    // 1. 业务线程池共享 (防止高并发下创建过多线程池导致 OOM)
    private static final ExecutorService BUSINESS_EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public void start(int port, RequestHandler handler) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    // 处理粘包半包 (长度域解码)
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    // 对象编解码 (实际生产中可替换为 Protostuff)
                                    .addLast(new ProtostuffEncoder())
                                    .addLast(new ProtostuffDecoder())
                                    // 业务处理 Handler
                                    .addLast(new NettyServerHandler(handler));
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("[Netty] Server 启动成功，监听端口: " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // Netty 的业务处理器 (服务端)
    static class NettyServerHandler extends ChannelInboundHandlerAdapter {
        private final RequestHandler requestHandler;

        public NettyServerHandler(RequestHandler requestHandler) {
            this.requestHandler = requestHandler;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            RpcRequest request = (RpcRequest) msg;
            // 提交到业务线程池，防止阻塞 Netty IO 线程
            BUSINESS_EXECUTOR.submit(() -> {
                RpcResponse response = new RpcResponse();
                try {
                    response = requestHandler.handle(request); // 假设 requestHandler 会生成 response
                    // 这里演示需要把原本的 response 对象拿回来，根据实际业务调整
                } catch (Exception e) {
                    // 异常处理：让客户端知道业务执行失败
                    response = new RpcResponse();
                    response.setError(e.getMessage());
                }
                ctx.writeAndFlush(response); // 响应最终会在服务端回写
            });
        }
    }

    @Override
    public RpcResponse send(String host, int port, RpcRequest request) {
        // 简化版客户端实现 (生产环境需考虑连接池复用)
        // 2. 抽取共享的线程组 (避免每次调用都创建 Selector 轮询线程)
        EventLoopGroup group = new NioEventLoopGroup();

        // 3. 提前把 NettyClientHandler 放在外面，以便展示给它用户的方法拿结果
        NettyClientHandler clientHandler = new NettyClientHandler();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new ProtostuffEncoder())
                                    .addLast(new ProtostuffDecoder())
                                    // 传入获取到的 clientHandler
                                    .addLast(clientHandler);
                        }
                    });

            // 建立 TCP 连接
            ChannelFuture connectFuture = bootstrap.connect(host, port).sync();

            // 发送 RPC 数据包
            connectFuture.channel().writeAndFlush(request).sync();

            // 4. 【核心同步逻辑】：阻塞等待服务端返回，直到响应被放入队列
            RpcResponse response = (RpcResponse) clientHandler.getResponse(5000); // 等待 5 秒防死锁

            // 返回最终真实结果
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 当前架构是短连接，所以这里才需要关闭线程组释放资源。后续升级为长连接后此处可以拿掉。
            group.shutdownGracefully();
        }
    }

    static class NettyClientHandler extends ChannelInboundHandlerAdapter {

        // 使用阻塞队列来存放服务端返回的响应
        private final BlockingQueue<Object> responseQueue = new LinkedBlockingQueue<>();

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 接收到服务端的响应，放入队列中
            responseQueue.offer(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        /**
         * 设置超时时间，防止服务端挂死导致客户端调用线程永久卡死
         */
        public Object getResponse(long timeoutMillis) throws InterruptedException {
            // 等待队列中取出消息，超时则被唤醒
            return responseQueue.poll(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
}