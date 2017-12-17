package io.qivaz.anime.shutdown;

import android.content.Context;

import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/5/8.
 */
public class ShutdownInject {
    public static void run(Context context) {
        inject();
    }

    private static void inject() {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(AnimeInjection.getLifecycleMonitor()));
    }
}
