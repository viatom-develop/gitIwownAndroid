package com.zeroner.bledemo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Daemon on 2016/5/25 14:41.
 */
public class ExecutorUtils {

    private  static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static ExecutorService getExecutorService(){
        return executor;
    }
}
