package com.my.net;

import java.net.Socket;

/**
 * @author duoyian
 * @date 2026/7/31
 */
public interface RequestHandler {
    void handleRequest(Socket socket);
}
