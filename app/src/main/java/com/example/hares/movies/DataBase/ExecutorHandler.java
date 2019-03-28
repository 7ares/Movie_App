package com.example.hares.movies.DataBase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorHandler {

    private final Executor diskIO;


    private static ExecutorHandler sInstance;
    private static Object LOCK = new Object();

    public ExecutorHandler(Executor diskIo) {
        this.diskIO = diskIo;

    }

    public static ExecutorHandler getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new ExecutorHandler(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }


}
