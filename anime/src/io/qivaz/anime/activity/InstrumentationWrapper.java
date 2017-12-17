package io.qivaz.anime.activity;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import io.qivaz.anime.choreographer.FrameHandlerLooperPrinterCallbackImpl;
import io.qivaz.anime.crash.CrashHandler;
import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;
import io.qivaz.anime.systemkey.SystemKeyHandler;
import io.qivaz.anime.view.ActivityViewsStack;
import io.qivaz.anime.view.LayoutInflaterInjection;
import io.qivaz.anime.view.ViewOnClickListenerCompat;
import io.qivaz.anime.window.WindowCallbackInjection;

/**
 * @author Qinghua Zhang @create 2017/3/23.
 */
public class InstrumentationWrapper extends Instrumentation {
    private static final boolean debug = false;
    private static Context mContext;
    private Instrumentation mInstrumentation;
    private LifecycleMonitor lcm;
    private WeakReference<Activity> mWeakRefCurActivity;
    private FragmentManager mFm;

    public InstrumentationWrapper(Instrumentation inst, Context context, LifecycleMonitor lc) {
        mInstrumentation = inst;
        mContext = context;
        lcm = lc;
    }

    private Window getWindow(Activity activity) {
        Class<?> cls = null;
        try {
            cls = Class.forName("io.qivaz.aster.runtime.bundle.BundleActivity");
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        }

        Window window = null;
        if (cls != null && cls.isAssignableFrom(activity.getClass())) {
            Field BundleActivity$mTargetActivity = null;
            try {
                BundleActivity$mTargetActivity = cls.getDeclaredField("mTargetActivity");
                BundleActivity$mTargetActivity.setAccessible(true);
                Activity target = (Activity) BundleActivity$mTargetActivity.get(activity);

                window = target.getWindow();
                //LogUtil.v("ANIME", "InstrumentationWrapper.getWindow(), from target activity of BundleActivity");
            } catch (NoSuchFieldException e) {
                LogUtil.e("ANIME", "InstrumentationWrapper.getWindow(), " + e);
                e.printStackTrace();
                window = activity.getWindow();
            } catch (IllegalAccessException e) {
                LogUtil.e("ANIME", "InstrumentationWrapper.getWindow(), " + e);
                e.printStackTrace();
                window = activity.getWindow();
            }
        } else {
            window = activity.getWindow();
        }
        return window;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnCreate(activity);
        }
        super.callActivityOnCreate(activity, icicle);
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnCreated(activity);
        }
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnCreate(1), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            if (AnimeInjection.getUseMonitorView()) {
                LayoutInflaterInjection.run(activity);
            }
            WindowCallbackInjection.run(lcm, activity, getWindow(activity));
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle,
                                     PersistableBundle persistentState) {
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnCreate(activity);
        }
        super.callActivityOnCreate(activity, icicle, persistentState);
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnCreated(activity);
        }
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnCreate(2), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            if (AnimeInjection.getUseMonitorView()) {
                LayoutInflaterInjection.run(activity);
            }
            WindowCallbackInjection.run(lcm, activity, getWindow(activity));
        }
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnNewIntent(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnNewIntent(activity, intent);
        }
        super.callActivityOnNewIntent(activity, intent);
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnStart(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnStart(activity);
        }
        super.callActivityOnStart(activity);
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnRestart(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnRestart(activity);
        }
        super.callActivityOnRestart(activity);
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnResume(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnResume(activity);
        }
        super.callActivityOnResume(activity);
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnResumed(activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            Activity currentActivity = null;
            if (mWeakRefCurActivity != null) {
                currentActivity = mWeakRefCurActivity.get();
            }
            if (!activity.equals(currentActivity)) {
                FrameHandlerLooperPrinterCallbackImpl.setActivity(activity);

                if (AnimeInjection.getUseMonitorView()
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    List<View> list = ActivityViewsStack.viewStack.get(activity.hashCode());
                    if (list != null) {
                        for (View view : list) {
                            if (view != null && view.hasOnClickListeners()) {
                                ViewOnClickListenerCompat.setWrapper(view);
                            }
                        }
                    }
                }
                mWeakRefCurActivity = new WeakReference<>(activity);
            }
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnPause(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnPause(activity);
        }
        super.callActivityOnPause(activity);
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnStop(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnStop(activity);
        }
        super.callActivityOnStop(activity);
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnDestroy(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnDestroy(activity);
        }
        super.callActivityOnDestroy(activity);
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.callActivityOnUserLeaving(), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityOnUserLeaving(activity);
            SystemKeyHandler.getInstance().onUserLeaving();
        }
        super.callActivityOnUserLeaving(activity);
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context,
                                IBinder token, Application application, Intent intent, ActivityInfo info,
                                CharSequence title, Activity parent, String id,
                                Object lastNonConfigurationInstance) throws InstantiationException,
            IllegalAccessException {
        Activity activity = super.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.newActivity(1), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityStart(activity);
            CrashHandler.getInstance(mContext).checkAndInit();
        }
        return activity;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Activity activity = super.newActivity(cl, className, intent);
        if (debug) {
            LogUtil.w("ANIME", "InstrumentationWrapper.newActivity(2), " + activity);
        }
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            lcm.onActivityStart(activity);
            CrashHandler.getInstance(mContext).checkAndInit();
        }
        return activity;
    }
}
