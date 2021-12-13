package com.awy.common.ws.netty;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.awy.common.message.api.packets.Message;
import com.awy.common.ws.netty.cluster.ImClusterTopic;
import com.awy.common.ws.netty.config.ImConfig;
import com.awy.common.ws.netty.config.ImPropertiesConfig;
import com.awy.common.ws.netty.constants.ImCommonConstant;
import com.awy.common.ws.netty.context.MessageManager;
import com.awy.common.ws.netty.context.ProcessManager;
import com.awy.common.ws.netty.process.AuthProcess;
import com.awy.common.ws.netty.process.CmdProcess;
import com.awy.common.ws.netty.server.WebSocketServer;
import com.awy.common.ws.netty.even.LifeCycleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author yhw
 */
@Slf4j
public class NettyWebSocketStarter {

    private Integer port;

    private String websocketPath;

    /**
     * 是否启用ssl
     */
    private boolean ssl = false;

    private WebSocketServer server;

    /**
     * 认证处理器
     */
    private AuthProcess authProcess;

    private LifeCycleEvent lifeCycleEvent;

    private ImClusterTopic imClusterTopic;

    private NettyWebSocketStarter(){}


    public NettyWebSocketStarter(AuthProcess authProcess){
        this(authProcess,null,null);
    }

    public NettyWebSocketStarter(AuthProcess authProcess, LifeCycleEvent lifeCycleEvent, ImClusterTopic imClusterTopic){
        this.authProcess = authProcess;
        this.lifeCycleEvent = lifeCycleEvent;
        this.imClusterTopic = imClusterTopic;
    }



    @PostConstruct
    public void init(){
        setAttributes(ImConfig.getImConfig().getPropertiesConfig());
        initCmdProcess();
        server = new WebSocketServer(port,websocketPath,ssl,authProcess,lifeCycleEvent,imClusterTopic);
        server.start();

        //注册
        registerDiscovery(ImConfig.getImConfig().getPropertiesConfig());
    }

    private void setAttributes(ImPropertiesConfig propertiesConfig){
        String packetScanPath = "";
        if(propertiesConfig == null){
            log.error(">>>>>>>> im config prefix: im.ws ");
            log.error(">>>>>>>> im config attributes can not by empty ");
            System.exit(0);
        }

        if(propertiesConfig.isCluster() && this.imClusterTopic == null){
            log.error(">>>>>>>> not allowed  when Cluster model imClusterTopic is null");
            System.exit(0);
        }

        this.port = propertiesConfig.getPort();
        if(this.port == null){
            this.port = getPort();
        }
        this.websocketPath = propertiesConfig.getWebsocketPath();
        if(this.websocketPath == null || this.websocketPath.isEmpty()){
            this.websocketPath = ImCommonConstant.DEFAULT_WEBSOCKET_PATH;
        }

        this.ssl = propertiesConfig.isSsl();
        packetScanPath = propertiesConfig.getPacketScan();
        if(packetScanPath == null || packetScanPath.isEmpty()){
            log.error(">>>>>>>> im.ws.packetScan can not by empty ");
            System.exit(0);
        }
        initPacket(packetScanPath);

    }

    private void registerDiscovery(ImPropertiesConfig propertiesConfig){
        if(propertiesConfig.isRegister()){
            Map<String, AbstractAutoServiceRegistration> serviceRegistrationMap = getApplicationContext().getBeansOfType(AbstractAutoServiceRegistration.class);
            for (Map.Entry<String,AbstractAutoServiceRegistration>  registrationEntry : serviceRegistrationMap.entrySet()){
//                System.err.println(">>>>>>>>>>>>>>>>> " + registrationEntry.getValue().getClass());
                registrationEntry.getValue().start();
            }

        }
    }

    private int getPort(){
        int defaultPort = 8888;
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

    private void initCmdProcess(){
        try{
            List<CmdProcess> list = new ArrayList<>();

            String[] beanDefinitionNames = getApplicationContext().getBeanDefinitionNames();
            Stream.of(beanDefinitionNames).forEach(beanName -> {
                Object bean = getApplicationContext().getBean(beanName);
                if(bean instanceof CmdProcess){
                    list.add((CmdProcess)bean);
                }
            });

            ProcessManager.getInstance().addCmdProcessList(list);
            log.info("init im process repository success ! count [" + list.size() + "]");
        }catch (Exception e){
            log.error("init im process repository error",e);
            System.exit(0);
        }
    }

    private  ApplicationContext getApplicationContext(){
        return ImConfig.getImConfig().getApplicationContext();
    }


    private void initPacket(String packetScanPath){
        try{
            Set<Class<?>> set = ClassUtil.scanPackage(packetScanPath);
            Object obj;
            List<Message> list = new ArrayList<>();
            if(CollectionUtil.isNotEmpty(set)){
                for (Class clazz : set) {
                    obj = ReflectUtil.newInstance(clazz);
                    if(obj instanceof Message){
                        list.add((Message) obj);
                    }
                }
            }

            MessageManager.getInstance().addMessages(list);
            log.info("init IM packet repository success ! count [" + list.size() + "]");
        }catch (Exception e){
            log.error("init packet repository error",e);
            System.exit(0);
        }
    }

    @PreDestroy
    public void stop(){
        server.stop();
    }
}
