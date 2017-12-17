package io.qivaz.anime.systemkey;

import io.qivaz.anime.executor.SerialExecutor;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/5/8.
 */
public class SystemKeyHandler {
    private static final long MAX_EXIT_INTERVAL = 100;
    public static long mUserLeaving;
    public static long mHomeKey;
    public static long mRecentKey;
    private static SystemKeyHandler mInstance;

    private SystemKeyHandler() {
    }

    public static synchronized SystemKeyHandler getInstance() {
        if (mInstance == null) {
            mInstance = new SystemKeyHandler();
        }
        return mInstance;
    }

    public void onUserLeaving() {
        mUserLeaving = System.currentTimeMillis();
        handle();
    }

    public void onHomeKey() {
        mHomeKey = System.currentTimeMillis();
        handle();
    }

    public void onSwitchKey() {
        mRecentKey = System.currentTimeMillis();
        handle();
    }

    private boolean checkExit() {
        long key = mHomeKey > mRecentKey ? mHomeKey : mRecentKey;
        long interval = key > mUserLeaving ? (key - mUserLeaving) : (mUserLeaving - key);

        return interval < MAX_EXIT_INTERVAL;
    }

    private void handle() {
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                if (checkExit()) {
                    handleExit();
                }
            }
        });
    }

    private void handleExit() {
        //LogUtil.v("ANIME", "SystemKeyHandler.handleExit()");
        AnimeInjection.getLifecycleMonitor().onExit(false);
    }
}
