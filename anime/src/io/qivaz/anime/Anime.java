package io.qivaz.anime;

import android.app.Application;
import android.os.Build;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.ActionCallback;
import io.qivaz.anime.monitor.lifecycle.LifecycleOption;
import io.qivaz.impl.sample.lifecycle.LifecycleMonitorImpl;
import io.qivaz.impl.sample.lifecycle.LifecycleOptionImpl;

/**
 * ANIME (Android Non-Intrusive Monitor Environment)
 * <p/>
 *
 * @author Qinghua Zhang @create 2017/3/23.
 */
public class Anime {
    private static final int ANIME_SUPPORT_VERSION_MIN = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    private static final int ANIME_SUPPORT_VERSION_MAX = 26;//Build.VERSION_CODES.O; //26
    private static boolean debug;

    public static void start(Application context, boolean debug) {
        setLifecycleMonitor(new LifecycleMonitorImpl(context));
        setLifecycleOption(new LifecycleOptionImpl(context));
        getLifecycleOption().setLogOutput(debug); // debug only
        startAnimeInjection(context, debug);
    }

    public static boolean checkSDKVersionAllowable() {
        return Build.VERSION.SDK_INT >= ANIME_SUPPORT_VERSION_MIN
                && Build.VERSION.SDK_INT <= ANIME_SUPPORT_VERSION_MAX;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static void setDebug(boolean d) {
        debug = d;
    }

    public static LifecycleMonitor getLifecycleMonitor() {
        return AnimeInjection.getLifecycleMonitor();
    }

    /**
     * Must invoke setLifecycleMonitor() before start()
     *
     * @param lcm monitor implementation
     */
    public static void setLifecycleMonitor(LifecycleMonitor lcm) {
        AnimeInjection.setLifecycleMonitor(lcm);
    }

    public static LifecycleOption getLifecycleOption() {
        return AnimeInjection.getLifecycleOption();
    }

    /**
     * Better invoke setLifecycleOption() before start()
     *
     * @param lco option implementation
     */
    public static void setLifecycleOption(LifecycleOption lco) {
        AnimeInjection.setLifecycleOption(lco);
    }

    public static void setActionCallback(ActionCallback callback) {
        AnimeInjection.setActionCallback(callback);
        AnimeInjection.setUseAction(true);
    }

    public static void getActivityStack(long interval, int type) {
        AnimeInjection.getActivityStack(interval, type);
    }

    public static void startAnimeInjection(Application context, boolean debug) {
        if (checkSDKVersionAllowable()) {
            setDebug(debug);
            try {
                AnimeInjection.trigger(context);
            } catch (Exception | Error e) {
                LogUtil.e("ANIME", "Anime.startAnimeInjection(), failed, " + e);
                e.printStackTrace();
            }
        } else {
            LogUtil.w("ANIME", "Anime.startAnimeInjection(), not allowed to launch ANIME for current SDK version API-" + Build.VERSION.SDK_INT + "!");
        }
    }
}
