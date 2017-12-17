package io.qivaz.anime.choreographer;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/3/25.
 */
public class FrameHandlerLooperPrinterCallbackImpl implements FrameHandlerLooperPrinter.Callback {
    private static final boolean debug = false;
    private static final int STUCK_STACK_FREQUENCE_IN_LIMIT = 2; // Times to catch stack during STUCK_SECONDS_LIMIT
    private static final int INTERVAL_ADVANCE = 0;
    private static final int MAX_STUCK_STACK_TIMES = 5; // Maximum times to catch stack
    private static WeakReference<Activity> mWeakRefActivity;
    private static int STUCK_SECONDS_LIMIT;
    private static int STUCK_SKIPPED_LIMIT;
    private static int interval;
    private static int count;
    private static long frameTime;
    private static StackTraceElement[][] mStuckSte;
    private static List<StackTraceElement[]> mStuckPreSte = new ArrayList<>();
    private static StackTraceElement[] mActPreSte;

    public FrameHandlerLooperPrinterCallbackImpl() {
        this(null);
    }

    public FrameHandlerLooperPrinterCallbackImpl(Activity activity) {
        mWeakRefActivity = new WeakReference<>(activity);
        STUCK_SKIPPED_LIMIT = (int) (AnimeInjection.SKIPPED_FRAME_WARNING_LIMIT * AnimeInjection.SKIPPED_FRAME_LIMIT_COEFFICIENT);
        STUCK_SECONDS_LIMIT = (int) (STUCK_SKIPPED_LIMIT * AnimeInjection.FRAME_INTERNAL);
        interval = STUCK_SECONDS_LIMIT / (STUCK_STACK_FREQUENCE_IN_LIMIT + 1);
        interval -= INTERVAL_ADVANCE;
        count = 0;
        LogUtil.w("ANIME", "FrameHandlerLooperPrinterCallbackImpl(), " + STUCK_SKIPPED_LIMIT + ", " + STUCK_SECONDS_LIMIT + ", " + interval);
    }

    public static void setActivity(Activity activity) {
        if (mWeakRefActivity != null) {
            mWeakRefActivity.clear();
        }
        mWeakRefActivity = new WeakReference<>(activity);
    }

    /**
     * postInternal0()
     * <p/>
     * The interval is doubled after post
     * Ex.) interval == 160ms, then 320ms, 640ms, 1280ms, 2560ms, ...
     * Timing to handle: +160ms, +480ms, +1120ms, +2400ms, +4960ms, ...
     */
    private static void postInternal0() {
        if (debug) {
            LogUtil.w("ANIME", "postInternal0(), interval=" + interval + ", count=" + count);
        }
        count++;
        Message msg = FrameHandler.getInstance().obtainMessage(0, 0, 0, frameTime);
        FrameHandler.getInstance().sendMessageDelayed(msg, interval);
        interval *= 2;
    }

    @Override
    public void doFrame(long duration) {
        long skipped = duration / AnimeInjection.FRAME_INTERNAL_NANOS;
        if (skipped >= STUCK_SKIPPED_LIMIT
                && mWeakRefActivity != null && mWeakRefActivity.get() != null) {
            mStuckSte = mStuckPreSte.toArray(new StackTraceElement[0][0]);
            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                AnimeInjection.getLifecycleMonitor().onActivityStuck(mWeakRefActivity.get(), duration, skipped, mStuckSte);
            }
            mStuckSte = null;
        } else if (skipped < STUCK_SKIPPED_LIMIT
                && skipped >= AnimeInjection.SKIPPED_FRAME_WARNING_LIMIT) {
            LogUtil.w("ANIME", "doFrame(), Skipped " + skipped + " frames! STUCK_SKIPPED_LIMIT = " + STUCK_SKIPPED_LIMIT);
        }
        mStuckPreSte.clear();

        if (AnimeInjection.getUseWatcher()) {
            frameTime = System.currentTimeMillis();
            interval = STUCK_SECONDS_LIMIT / (STUCK_STACK_FREQUENCE_IN_LIMIT + 1);
            interval -= INTERVAL_ADVANCE;
            count = 0;
            postInternal0();
        }
    }

    @Override
    public void doScheduleVsync(long duration) {
        //LogUtil.v("ANIME", "doScheduleVsync(), " + duration);
    }

    @Override
    public void doScheduleCallback(long duration) {
        //LogUtil.v("ANIME", "doScheduleCallback(), " + duration);
    }

    @Override
    public Handler getHandler() {
        return FrameHandler.getInstance();
    }

    private static class FrameHandler extends Handler {
        private static FrameHandler fh;
        private long start;
        private Thread mainThread = Looper.getMainLooper().getThread();

        private FrameHandler() {
            super(AnimeInjection.getLooper());
        }

        public static FrameHandler getInstance() {
            if (fh == null) {
                fh = new FrameHandler();
            }
            return fh;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    start = (long) msg.obj;
                    if (start == frameTime) {
                        StackTraceElement[] elements = mainThread.getStackTrace();
                        mStuckPreSte.add(elements);

                        if (count < MAX_STUCK_STACK_TIMES) {
                            postInternal0();
                        }
                    }
                    break;
                case 1:
                    StackTraceElement[] elements = mainThread.getStackTrace();
                    mActPreSte = elements;
                    switch (msg.arg1) {
                        case 0:
                            AnimeInjection.getLifecycleMonitor().onActivityOnCreateCatch(mActPreSte);
                            break;
                        case 1:
                            AnimeInjection.getLifecycleMonitor().onActivityOnResumeCatch(mActPreSte);
                    }
                    mActPreSte = null;
                    break;
                default:
                    break;
            }
            ChoreographerInjection.start();
        }
    }
}
