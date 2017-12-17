package io.qivaz.anime.file;

import android.os.FileObserver;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
public class AnrFileObserver extends FileObserver {
    private static final String ANR_FOLD = "/data/anr/";
    private static final String ANR_FILE = "/data/anr/traces.txt";
    private static AnrFileObserver mInstance;
    private static LifecycleMonitor mLcm;

    private AnrFileObserver() {
        this(ANR_FOLD, FileObserver.ALL_EVENTS);
    }

    private AnrFileObserver(String path, int mask) {
        super(path, mask);
    }

    public static void run(LifecycleMonitor lcm) {
        mLcm = lcm;
        if (mInstance == null) {
            mInstance = new AnrFileObserver();
        }
        mInstance.startWatching();
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case FileObserver.CREATE:
            case FileObserver.MODIFY:
            default:
                //LogUtil.v("ANIME", "AnrFileObserver.onEvent(), event=" + event + ", path=" + path);
                break;
        }
    }
}
