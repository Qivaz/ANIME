package io.qivaz.anime.choreographer;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class ChoreographerInjection {
    private static FrameHandlerLooperPrinter mFlp;

    public static void run() {
        inject();
        injectFrameHandler();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void inject() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            LogUtil.w("ANIME", "ChoreographerInjection.inject(), failed, Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN, Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
            return;
        }
        try {
            Class clazzChor = Choreographer.getInstance().getClass(); //cl.loadClass("android.view.Choreographer");
            Method method = clazzChor.getDeclaredMethod("getInstance");
            method.setAccessible(true);
            Object chor = method.invoke(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Field limit = clazzChor.getDeclaredField("SKIPPED_FRAME_WARNING_LIMIT");
                limit.setAccessible(true);
                Object skipped_limit = limit.get(null);
                AnimeInjection.SKIPPED_FRAME_WARNING_LIMIT = (int) skipped_limit;
            } else {
                LogUtil.w("ANIME", "ChoreographerInjection.inject(), no SKIPPED_FRAME_WARNING_LIMIT field, Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Method frameInterval = clazzChor.getDeclaredMethod("getFrameIntervalNanos");
                frameInterval.setAccessible(true);
                Object frame_interval_nano = frameInterval.invoke(chor);
                AnimeInjection.FRAME_INTERNAL_NANOS = (long) frame_interval_nano;
                AnimeInjection.FRAME_INTERNAL = AnimeInjection.FRAME_INTERNAL_NANOS / 1000000;
            } else {
                LogUtil.w("ANIME", "ChoreographerInjection.inject(), no getFrameIntervalNanos() method, Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
            }

            LogUtil.w("ANIME", "ChoreographerInjection.inject(), frame_interval_nano=" + AnimeInjection.FRAME_INTERNAL_NANOS + ", skipped_limit=" + AnimeInjection.SKIPPED_FRAME_WARNING_LIMIT);
        } catch (NoSuchMethodException e) {
            LogUtil.e("ANIME", "ChoreographerInjection.inject(), " + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "ChoreographerInjection.inject(), " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "ChoreographerInjection.inject(), " + e);
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "ChoreographerInjection.inject(), " + e);
            e.printStackTrace();
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "ChoreographerInjection.inject(), " + e);
            e.printStackTrace();
        }
    }

    public static void injectFrameHandler() {
        mFlp = new FrameHandlerLooperPrinter(new FrameHandlerLooperPrinterCallbackImpl());
        start();
    }

    public static void start() {
        mFlp.start();
    }
}
