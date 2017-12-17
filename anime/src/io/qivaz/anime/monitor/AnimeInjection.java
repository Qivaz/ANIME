package io.qivaz.anime.monitor;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import io.qivaz.anime.activity.ActivityInjection;
import io.qivaz.anime.async.AsyncTaskInjection;
import io.qivaz.anime.choreographer.ChoreographerInjection;
import io.qivaz.anime.choreographer.FrameHandlerLooperPrinter;
import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.message.MainMessageQueueInject;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.ActionCallback;
import io.qivaz.anime.monitor.lifecycle.LifecycleOption;
import io.qivaz.anime.monitor.lifecycle.impl.DefaultLifecycleMonitorImpl;
import io.qivaz.anime.receiver.ReceiverRegistry;
import io.qivaz.anime.shutdown.ShutdownInject;
import io.qivaz.anime.socketimpl.SocketImplInject;
import io.qivaz.anime.webview.WebViewInject;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class AnimeInjection {
    public static long FRAME_INTERNAL_NANOS = 16666666;
    public static long FRAME_INTERNAL = 16;
    public static int SKIPPED_FRAME_WARNING_LIMIT = 30; //Maybe 15 for high-performance target
    public static double SKIPPED_FRAME_LIMIT_COEFFICIENT = 2;
    private static HandlerThread mThread;
    private static Handler mMainHandler;
    private static BgHandler mHandler;
    private static LifecycleMonitor mLcm = new DefaultLifecycleMonitorImpl();
    private static LifecycleOption mLco;
    private static boolean useAction = true;
    private static boolean useMonitorView;
    private static ActionCallback actionCallback;

    public static LifecycleMonitor getLifecycleMonitor() {
        return mLcm;
    }

    public static void setLifecycleMonitor(LifecycleMonitor lcm) {
        mLcm = lcm;
    }

    public static LifecycleOption getLifecycleOption() {
        return mLco;
    }

    public static void setLifecycleOption(LifecycleOption lco) {
        mLco = lco;
    }

    public static boolean getUseWatcher() {
        return true;
    }

    public static boolean getUseAction() {
        return useAction;
    }

    public static void setUseAction(boolean b) {
        useAction = b;
    }

    public static boolean getUseMonitorView() {
        return useMonitorView;
    }

    public static void setUseMonitorView(boolean b) {
        useMonitorView = b;
    }

    public static ActionCallback getActionCallback() {
        return actionCallback;
    }

    public static void setActionCallback(ActionCallback callback) {
        actionCallback = callback;
    }

    public static void trigger(Application context) {
        if (getLifecycleOption().enabledCollect()) {
            mMainHandler = new Handler();
            ShutdownInject.run(context);
            ActivityInjection.run(context);
            if (getUseAction()) {
                MainMessageQueueInject.run();
            }
            ChoreographerInjection.run();
            AsyncTaskInjection.run();
            WebViewInject.run();
            ReceiverRegistry.run(context);

            //SocketInject.run();
            SocketImplInject.run();

            if (getUseWatcher()) {
                mThread = new HandlerThread("AnimeWatcher",
                        10); //Process.THREAD_PRIORITY_BACKGROUND
                mThread.start();
                mHandler = new BgHandler(mThread.getLooper());
            }
        } else {
            LogUtil.w("ANIME", "AnimeInjection.trigger(), enabledCollect() = false");
        }
    }

    public static Handler getMainHandler() {
        return mMainHandler;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static Looper getLooper() {
        return mThread.getLooper();
    }

    public static void getActivityStack(long interval, int type) {
        Message msg = FrameHandlerLooperPrinter.getHandler().obtainMessage(1, type, 0);
        FrameHandlerLooperPrinter.getHandler().sendMessageDelayed(msg, interval);
    }

    private static class BgHandler extends Handler {
        private long start;
        private Thread mainThread = Looper.getMainLooper().getThread();

        BgHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                default:
                    break;
            }
        }
    }
}
