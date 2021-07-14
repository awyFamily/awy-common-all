package com.awy.common.tcp;


import com.awy.common.tcp.codec.ITcpDecoder;
import com.awy.common.tcp.codec.ITcpEncoder;
import com.awy.common.tcp.context.ISessionLifecycle;
import com.awy.common.tcp.handler.IBusinessProcess;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

/**
 * @author yhw
 * @date 2021-07-14
 */
@Slf4j
public class TcpServerStarter {

    private TcpServer server;

    private Integer port;
    private boolean ssl = false;
    private ITcpDecoder decoder;
    private ITcpEncoder encoder;
    private ISessionLifecycle lifecycle;
    private List<IBusinessProcess> processes;


    private TcpServerStarter(){}

    public TcpServerStarter(ITcpDecoder decoder, ITcpEncoder encoder, ISessionLifecycle lifecycle, List<IBusinessProcess> processes){
        this(null,false,decoder,encoder,lifecycle,processes);
    }

    public TcpServerStarter(Integer port, boolean ssl, ITcpDecoder decoder, ITcpEncoder encoder, ISessionLifecycle lifecycle, List<IBusinessProcess> processes){
        this.port = port == null ? this.getPort() : port;
        this.ssl = ssl;
        this.decoder = decoder;
        this.encoder = encoder;
        this.lifecycle = lifecycle;
        this.processes = processes;
    }


    @PostConstruct
    public void init(){
        if(decoder == null || encoder == null || lifecycle == null || processes == null){
            log.info("tcp server init error");
        }else {
            server = new TcpServer(port,ssl,decoder,encoder,lifecycle,processes);
            server.start();
        }
    }


    private int getPort(){
        int defaultPort = 8181;
        return getPort(defaultPort);
    }

    private int getPort(int port){
        ServerSocket socket = null;
        try{
            socket = new ServerSocket(port);
        }catch (IOException e){
            ++port;
            return getPort(port);
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("");
                }
            }
        }
        return port;
    }


    @PreDestroy
    public void stop(){
        if(decoder == null || encoder == null || lifecycle == null || processes == null){
            return;
        }
        server.stop();
    }
}
