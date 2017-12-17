package io.qivaz.anime.window;

import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class WindowGlobalInjection {
    public static Handler H;

    public static void run(Activity activity) {
        inject(activity);
    }

    public static void inject(Activity activity) {
        ClassLoader cl = activity.getClass().getClassLoader();
        try {
            Class clazzWmg = cl.loadClass("android.view.WindowManagerGlobal");
            Method method = clazzWmg.getDeclaredMethod("getInstance");
            method.setAccessible(true);
            Object windowGlobal = method.invoke(null);

            Class clazzActivity = cl.loadClass("android.app.Activity");
            Method method1 = clazzActivity.getDeclaredMethod("getActivityToken");
            method1.setAccessible(true);
            Object ibinder = method1.invoke(activity);

            Method method2 = clazzWmg.getDeclaredMethod("getRootViews", new Class[]{IBinder.class});
            method2.setAccessible(true);
            Object list = method2.invoke(windowGlobal, ibinder);
            ArrayList<Object> views = (ArrayList<Object>) list;

            if (views.size() > 0) {
                Object viewRootImpl = views.get(0);

                Class clazzVri = cl.loadClass("android.view.ViewRootImpl");
                Field h = clazzVri.getDeclaredField("mHandler");
                h.setAccessible(true);
                H = (Handler) h.get(viewRootImpl);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
