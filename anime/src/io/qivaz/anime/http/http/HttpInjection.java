package io.qivaz.anime.http.http;

import android.app.Activity;
import android.view.Window;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class HttpInjection {
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
                    new Class[]{Window.Callback.class}, new HttpInvocationHandler(lcm, activity, cb));

            mCallbackField.set(window, windowCallbackProxy);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class HttpInvocationHandler implements InvocationHandler {
        private LifecycleMonitor mLcm;
        private Window.Callback mCb;
        private WeakReference<Activity> mWeakRefActivity;

        public HttpInvocationHandler(LifecycleMonitor lcm, Activity activity, Object cb) {
            mLcm = lcm;
            mWeakRefActivity = new WeakReference<>(activity);
            mCb = (Window.Callback) cb;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //LogUtil.v("ANIME", "WindowCallbackInjection.invoked:" + method.getName() + "(), start");
            Object obj = method.invoke(mCb, args);
            //LogUtil.v("ANIME", "WindowCallbackInjection.invoked:" + method.getName() + "(), end");
            return obj;
        }
    }

}
