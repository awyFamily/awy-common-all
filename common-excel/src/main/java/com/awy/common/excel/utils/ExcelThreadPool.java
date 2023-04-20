package com.awy.common.excel.utils;

import cn.hutool.core.util.RuntimeUtil;
import lombok.Getter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2023-04-20
 */
public final class ExcelThreadPool {
    private  Thread shutdownHook;

    @Getter
    private ThreadPoolExecutor executor;

    private  final Object startupShutdownMonitor = new Object();

    private  final String SHUTDOWN_HOOK_THREAD_NAME = "excelThreadPoolShutdownHook";

    // Implementing Singleton pattern using double-check locking with volatile keyword
    private static volatile ExcelThreadPool instance;

    public static ExcelThreadPool getInstant() {
        if (instance == null) {
            synchronized (ExcelThreadPool.class) {
                if (instance == null) {
                    instance = new ExcelThreadPool();
                }
            }
        }
        return instance;
    }

    private ExcelThreadPool() {
        registerShutdownHook();
    }

    void registerShutdownHook() {
        if (shutdownHook == null) {
            // No shutdown hook registered yet.
            shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    synchronized (startupShutdownMonitor) {
                        doClose();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
        if (executor == null) {
            executor = getThreadPoolExecutor();
        }
    }

    private  ThreadPoolExecutor getThreadPoolExecutor(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                RuntimeUtil.getProcessorCount(),
                RuntimeUtil.getProcessorCount() * 2,
                60L,
//                1,10, 60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "excel batch export job-" + r.hashCode());
                    }
                });

        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private  void doClose() {
        if (executor != null) {
            executor.shutdown();
        }
        // if (shutdownHook != null) {
        //     Runtime.getRuntime().removeShutdownHook(shutdownHook);
        // }  
    }

}
