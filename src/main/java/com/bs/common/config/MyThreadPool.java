package com.bs.common.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Desc 线程池
 * Date 2020/5/26
 */
public class MyThreadPool {

    public static ThreadPoolExecutor threadPool;

    static {
        int coreNumbers = Runtime.getRuntime().availableProcessors() * 2 + 1;
        threadPool = new ThreadPoolExecutor(
                coreNumbers,
                coreNumbers,
                10L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(6000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
