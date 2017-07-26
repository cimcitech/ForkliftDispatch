package com.cimcitech.forkliftdispatch.updateApk;

import android.os.*;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务池
 */
public class TaskPool {
    private ExecutorService executor;
    private Handler mainHandler;

    private static TaskPool taskPool;

    private TaskPool() {
        executor = Executors.newFixedThreadPool(5);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     *
     * @return
     */
    public static TaskPool myPool() {
        synchronized (TaskPool.class) {
            if (taskPool == null) {
                taskPool = new TaskPool();
            }
            return taskPool;
        }
    }

    /**
     *
     * @param asyncRunnable
     */
    public <T> void post(final AsyncRunnable<T> asyncRunnable) {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                final T result =	asyncRunnable.run();
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        asyncRunnable.postForeground(result);
                    }
                });
            }
        });
    }

    /**
     * 子线程执行Runnable
     * @param runnable
     */
    public void postBg(Runnable runnable){
        executor.execute(runnable);
    }

    /**
     *
     * @param runnable Runnable
     */
    public void postTaskOnMain(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void postTaskOnMainDelay(Runnable runnable,long time) {
        mainHandler.postDelayed(runnable,time);
    }
}
