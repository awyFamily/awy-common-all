package com.yhw.nc.common.ws.netty.context;


import com.yhw.nc.common.ws.netty.cluster.ImClusterTopic;
import com.yhw.nc.common.ws.netty.even.LifeCycleEvent;
import com.yhw.nc.common.ws.netty.process.AuthProcess;

/**
 * 全局上下文(从上下文中加载，设置)
 * @author yhw
 */
public final class GlobalContent {

    private LifeCycleEvent lifeCycleEvent;

    private ImClusterTopic imClusterTopic;

    private AuthProcess authProcess;

    private GlobalContent(){}

    public static GlobalContent getInstance(){
        return Single.INSTANCE;
    }

    public void  setLifeCycleEvent(LifeCycleEvent lifeCycleEvent){
        if(this.lifeCycleEvent != null){
            throw new RuntimeException("Illegal operation, no changes allowed");
        }
        this.lifeCycleEvent = lifeCycleEvent;
    }

    public LifeCycleEvent getLifeCycleEvent(){
        return lifeCycleEvent;
    }

    public ImClusterTopic getImClusterTopic(){
        return imClusterTopic;
    }

    public void  setImClusterTopic(ImClusterTopic imClusterTopic){
        if(this.imClusterTopic != null){
            throw new RuntimeException("Illegal operation, no changes allowed");
        }
        this.imClusterTopic = imClusterTopic;
    }

    public AuthProcess getAuthProcess() {
        return authProcess;
    }

    public void setAuthProcess(AuthProcess authProcess) {
        if(this.authProcess != null){
            throw new RuntimeException("Illegal operation, no changes allowed");
        }
        this.authProcess = authProcess;
    }


    private static class Single{
        private static GlobalContent INSTANCE = new GlobalContent();
    }

}
