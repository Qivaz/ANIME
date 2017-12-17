package io.qivaz.anime.choreographer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Printer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.lang.reflect.Field;

import io.qivaz.anime.executor.SerialExecutor;
import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.message.MessageUtil;
import io.qivaz.anime.message.MessageWrapper;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/3/25.
 */
public class FrameHandlerLooperPrinter implements Printer {
    private static final boolean debug = false;
    private static final char START_TAG = '>';
    private static final char END_TAG = '<';
    private static final char TGT_TAG = 'H';
    private static final int PRE_T_LEN = 21;
    private static Callback mCb;
    private static boolean started;
    private static long startTime;
    private static long endTime;
    private final Field mTouchModeResetField;
    private final Field mFirstPositionField;
    private final Field mMotionPositionField;
    private int what;
    private View currentView;

    public FrameHandlerLooperPrinter(Callback callback) {
        mCb = callback;

        mTouchModeResetField = getFiled(AbsListView.class, "mTouchModeReset");
        mFirstPositionField = getFiled(AbsListView.class, "mFirstPosition");
        mMotionPositionField = getFiled(AbsListView.class, "mMotionPosition");
    }

    public static void setCallback(Callback callback) {
        mCb = callback;
    }

    public static Handler getHandler() {
        return mCb.getHandler();
    }

    private static Field getFiled(Class clazz, String field) {
        Field f = null;
        Class c = clazz;
        boolean bFound = false;
        while (!Object.class.getName().equals(c.getName())) {
            try {
                f = c.getDeclaredField(field);
                f.setAccessible(true);
                bFound = true;
            } catch (NoSuchFieldException e) {
                //e.printStackTrace();
                c = c.getSuperclass();
            }
            if (bFound) {
                break;
            }
        }
        if (f == null) {
            LogUtil.e("ANIME", "FrameHandlerLooperPrinter.getFiled(" + clazz + ", " + field + "), falied!!!");
        }
        return f;
    }

    @Override
    public void println(String x) {
        if (debug) {
            //LogUtil.v("ANIME", x);
        }
        analyze(x);
    }

    private void analyze(String x) {
        if (x.charAt(0) == START_TAG) {
            analyzeStart(x);
        } else if (x.charAt(0) == END_TAG) {
            analyzeEnd(x);
        }

        if (!started && what != -1) {
            long duration = endTime - startTime;
            if (mCb != null) {
                mCb.doFrame(duration);
            }
//            switch (what) {
//                case 0:
//                    if (mCb != null) {
//                        mCb.doFrame(duration);
//                    }
//                    break;
//                case 1:
//                    if (mCb != null) {
//                        mCb.doScheduleVsync(duration);
//                    }
//                    break;
//                case 2:
//                    if (mCb != null) {
//                        mCb.doScheduleCallback(duration);
//                    }
//                    break;
//                default:
//                    break;
//            }
        }
    }

    private void analyzeStart(String x) {
        started = true;
        startTime = System.nanoTime();
        int len = x.length();
        if (x.charAt(len - 2) == ' ' && x.charAt(len - 3) == ':') {
            char c = x.charAt(len - 1);
            what = Integer.parseInt(String.valueOf(c));
        }

        if (!AnimeInjection.getUseAction()) {
            return;
        }
        currentView = null;

        if (isActionMatched(x)) {
            final Message msg = MessageUtil.getMessage();
            if (msg == null) {
                LogUtil.e("ANIME", "analyzeStart(), msg=" + msg);
                return;
            }
            final Handler h = msg.getTarget();
            final Runnable r = msg.getCallback();
            final Object parent;
            final Object cb;
            final String cbName = r.getClass().getName();
            final boolean listViewClicked;
            final int firstPos;
            final int motionPos;
            if (h == null || r == null) {
                LogUtil.e("ANIME", "analyzeStart(), msg=" + msg);
                return;
            }

            try {
                Field parentField = r.getClass().getDeclaredField("this$0");
                parentField.setAccessible(true);
                parent = parentField.get(r);
                if (parent == null) {
                    LogUtil.e("ANIME", "analyzeStart(), parent=" + parent + "msg=" + msg);
                    return;
                }
                if (parent instanceof AbsListView) {
                    cb = mTouchModeResetField.get(parent);
                    listViewClicked = cb == r;
                    if (listViewClicked) {
                        firstPos = (int) mFirstPositionField.get(parent);
                        motionPos = (int) mMotionPositionField.get(parent);
                    } else {
                        firstPos = 0;
                        motionPos = 0;
                    }
                } else {
                    listViewClicked = false;
                    firstPos = 0;
                    motionPos = 0;
                }
            } catch (NoSuchFieldException e) {
                LogUtil.e("ANIME", "analyzeStart(), " + msg + ", " + e);
                e.printStackTrace();
                return;
            } catch (IllegalAccessException e) {
                LogUtil.e("ANIME", "analyzeStart(), " + msg + ", " + e);
                e.printStackTrace();
                return;
            } catch (Exception e) { // In case...
                LogUtil.e("ANIME", "analyzeStart(), " + msg + ", " + e);
                return;
            }

            if (MessageWrapper.TGT_CB_CL[MessageWrapper.TGT_CB_CLICK_ID][MessageWrapper.CB_ID].equals(cbName)) {
                if (AnimeInjection.getLifecycleOption().enabledParse()) {
                    SerialExecutor.run(new Runnable() {
                        @Override
                        public void run() {
                            AnimeInjection.getLifecycleMonitor().onViewClick(parent);
                        }
                    });
                    currentView = (View) parent;
                    if (debug) {
                        LogUtil.w("ANIME", "analyzeStart(), currentView=" + currentView);
                    }
                }
            } else if (listViewClicked) {
                /*
                 * For ListView items click handle
                 */
                if (AnimeInjection.getLifecycleOption().enabledParse()) {
                    final View child = ((ViewGroup) parent).getChildAt(motionPos - firstPos);
                    SerialExecutor.run(new Runnable() {
                        @Override
                        public void run() {
                            AnimeInjection.getLifecycleMonitor().onViewClick(child);
                        }
                    });
                    currentView = child;
                    if (debug) {
                        LogUtil.w("ANIME", "analyzeStart(), currentView=" + currentView);
                    }
                }
            } else {
                LogUtil.w("ANIME", "analyzeStart(), not matched, msg=" + msg);
            }
        }
    }

    private void analyzeEnd(String x) {
        started = false;
        endTime = System.nanoTime();

        if (AnimeInjection.getLifecycleOption().enabledParse() && currentView != null) {
            if (debug) {
                LogUtil.w("ANIME", "analyzeEnd(), currentView=" + currentView);
            }
            final View view = currentView;
            SerialExecutor.run(new Runnable() {
                @Override
                public void run() {
                    AnimeInjection.getLifecycleMonitor().onViewClickFinish(view);
                }
            });
        }
    }

    public void start() {
        Looper.getMainLooper().setMessageLogging(this);
    }

    public void start(Looper looper) {
        looper.setMessageLogging(this);
    }

    private boolean isActionMatched(final String x) {
        if (-1 != x.lastIndexOf(MessageWrapper.TGT_CB_CL[MessageWrapper.TGT_CB_CLICK_ID][MessageWrapper.CB_ID])
                || -1 != x.lastIndexOf(MessageWrapper.TGT_CB_CL[MessageWrapper.TGT_CB_LIST_CLICK_ID][MessageWrapper.CB_ID])) {
            return true;
        }
        return false;
    }

    public interface Callback {
        void doFrame(long duration);

        void doScheduleVsync(long duration);

        void doScheduleCallback(long duration);

        Handler getHandler();
    }

}
