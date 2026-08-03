package com.my.server;

import com.my.net.transporter.NettyTransporter;
import com.my.server.RpcServer;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author duoyian
 * @date 2026/8/3
 */
@Component
public class RpcServerSmartLifecycle implements SmartLifecycle {
    private volatile boolean running = false;
    private Thread serverThread;

    // 1. 是否自动启动（默认true，如果false需要手动调用context.getBean(...).start()）
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    // 2. 启动逻辑（Spring容器刷新到特定阶段时自动调用）
    @Override
    public void start() {
        if (running) {
            return; // 防止重复启动
        }

        // 关键：Netty的bind().sync()是阻塞的，必须放新线程
        serverThread = new Thread(() -> {
            RpcServer server = new RpcServer(new NettyTransporter());
            server.start(8080); // 阻塞监听
        }, "rpc-server");
        serverThread.setDaemon(true);
        serverThread.start();

        running = true;
        System.out.println("【SmartLifecycle】RpcServer started");
    }

    // 3. 同步停止（Spring关闭容器时先调用）
    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        // 同步关闭逻辑（比如中断线程、关闭Channel）
        System.out.println("【SmartLifecycle】RpcServer stopping...");
    }

    // 4. 异步停止（支持优雅停机回调，通常调用stop()后执行callback.run()）
    @Override
    public void stop(Runnable callback) {
        stop(); // 执行同步停止
        callback.run(); // 通知Spring："我停完了，你可以继续关闭其他组件了"
    }

    // 5. 当前运行状态
    @Override
    public boolean isRunning() {
        return running;
    }

    // 6. 相位值：控制启动顺序（越大启动越晚，关闭越早）
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE; // 最后再启动，最先关闭
    }
}
