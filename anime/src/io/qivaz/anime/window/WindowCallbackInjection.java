package io.qivaz.anime.window;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.Window;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.message.MessageUtil;
import io.qivaz.anime.monitor.AnimeInjection;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class WindowCallbackInjection {
    private static boolean mCallbackSet;
    private static Field mCallbackField;

    public static void run(LifecycleMonitor lcm, Activity activity, Window window) {
        inject(lcm, activity, window);
    }

    private static void inject(LifecycleMonitor lcm, Activity activity, Window window) {
        try {
            Class<?> winClass = Window.class;
            if (!mCallbackSet) {
                mCallbackField = winClass.getDeclaredField("mCallback");
                mCallbackField.setAccessible(true);

                mCallbackSet = true;
            }
            Object cb = mCallbackField.get(window);
            Object windowCallbackProxy = Proxy.newProxyInstance(winClass.getClassLoader(),
                    new Class[]{Window.Callback.class}, new WindowCallbackHandler(lcm, activity, cb));
            mCallbackField.set(window, windowCallbackProxy);
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "WindowCallbackInjection.inject(), " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "WindowCallbackInjection.inject(), " + e);
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.e("ANIME", "WindowCallbackInjection.inject(), " + e);
            e.printStackTrace();
        }
    }

    private static class WindowCallbackHandler implements InvocationHandler {
        private LifecycleMonitor mLcm;
        private Window.Callback mCb;
        private WeakReference<Activity> mWeakRefActivity;

        public WindowCallbackHandler(LifecycleMonitor lcm, Activity activity, Object cb) {
            mLcm = lcm;
            mWeakRefActivity = new WeakReference<>(activity);
            mCb = (Window.Callback) cb;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("onWindowFocusChanged".equals(method.getName())) {
                Activity activity = mWeakRefActivity.get();
                if ((Boolean) args[0] && activity != null) {
                    if (AnimeInjection.getLifecycleOption().enabledParse()) {
                        mLcm.onActivityShow(activity);
                    }
                }/* else if (!((Boolean) args[0]) && activity != null) {
//                    if (mLcm.isActivityResumed(activity)) {
//                        mLcm.onActivityAnr();
//                    }
//                }*/
            }
            Object obj = method.invoke(mCb, args);
            if (AnimeInjection.getUseAction() && "dispatchTouchEvent".equals(method.getName())) {
                MotionEvent event = (MotionEvent) args[0];
                final float x = event.getX();
                final float y = event.getY();
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        MessageUtil.obtainMessage();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        MessageUtil.obtainMessage();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    default:
                        break;
                }
            }

            //LogUtil.v("ANIME", "WindowCallbackHandler." + method.getName() + "(" + Arrays.toString(args) + "), return(" + obj + "), " + mWeakRefActivity.get());
            return obj;
        }
    }

}
