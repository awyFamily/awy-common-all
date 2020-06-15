package com.yhw.nc.common.ws.netty;

import com.yhw.nc.common.ws.netty.server.WebSocketServer;

public class WebSocketServerTest {

    public static void main(String[] args) {
        WebSocketServer webSocket = new WebSocketServer(8080, "/webSocket", false, null, null, null);
        webSocket.start();
    }
}
