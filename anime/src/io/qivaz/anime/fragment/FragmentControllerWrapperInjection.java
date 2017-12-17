package io.qivaz.anime.fragment;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class FragmentControllerWrapperInjection {
    public static void run(LifecycleMonitor lcm, Activity activity) {
        inject(lcm, activity);
    }

    private static void inject(LifecycleMonitor lcm, Activity activity) {
        boolean isV4 = false;
        Class<?> activityClass = activity.getClass();
        while (!"android.app.Activity".equals(activityClass.getName())
                && !"android.support.v4.app.FragmentActivity".equals(activityClass.getName())
                && !"java.lang.Object".equals(activityClass.getName())) {
            activityClass = activityClass.getSuperclass();
        }
        if ("java.lang.Object".equals(activityClass.getName())) {
            return;
        } else if ("android.app.Activity".equals(activityClass.getName())) {
            isV4 = false;
        } else { //"android.support.v4.app.FragmentActivity".equals(activityClass.getName()
            isV4 = true;
        }

        try {
//            Field[] fields = activityClass.getDeclaredFields();
//            for (Field f : fields) {
//                LogUtil.v("ANIME", f.getName());
//            }
            Field field = activityClass.getDeclaredField("mFragments");
            field.setAccessible(true);
            Object fc = field.get(activity);


            Field hostField = fc.getClass().getDeclaredField("mHost");
            hostField.setAccessible(true);
            Object host = hostField.get(fc);

            if (isV4) {
                HostCallbacksWrapperV4.setHandler((FragmentActivity) activity);
                HostCallbacksWrapperV4 wrapper = new HostCallbacksWrapperV4((FragmentActivity) activity, null, 0);
                hostField.set(fc, wrapper);
            } else {

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //return null;
    }

    private static class FragmentControllerInvocationHandler implements InvocationHandler {
        private LifecycleMonitor mLcm;
        private Object mFc;
        private Activity mActivity;

        public FragmentControllerInvocationHandler(LifecycleMonitor lcm, Activity activity, Object fc) {
            mLcm = lcm;
            mActivity = activity;
            mFc = fc;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //LogUtil.v("ANIME", "FragmentControllerInvocationHandler.invoked:" + method.getName() + "()");
            Object obj = method.invoke(mFc, args);

            return obj;
        }
    }

}
