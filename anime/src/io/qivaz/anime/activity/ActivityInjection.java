package io.qivaz.anime.activity;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Singleton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class ActivityInjection {
    private static boolean mInstrumentationSet;
    private static Instrumentation mDefaultInstrumentation;

    public static void run(Context context) {
        AnimeInjection.getLifecycleMonitor().onAppStart((Application) context);
        injectInstrument(context);
//        injectActivityManager();
    }

    private static void injectInstrument(Context context) {
        // Inject instrumentation, just once.
        if (!mInstrumentationSet) {
            try {
                final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                final Method method = activityThreadClass.getMethod("currentActivityThread");
                Object thread = method.invoke(null, (Object[]) null);
                Field field = activityThreadClass.getDeclaredField("mInstrumentation");
                field.setAccessible(true);
                mDefaultInstrumentation = (Instrumentation) field.get(thread);
                Instrumentation wrapper = new InstrumentationWrapper(mDefaultInstrumentation, context, AnimeInjection.getLifecycleMonitor());
                field.set(thread, wrapper);

                mInstrumentationSet = true;
            } catch (Exception | Error e) {
                LogUtil.e("ANIME", "ActivityInjection.injectInstrument(), " + e);
                e.printStackTrace();
            }
        }
    }

    private static void injectActivityManager() {
        try {
            Class amnClazz = Class.forName("android.app.ActivityManagerNative");
            Field gField = amnClazz.getDeclaredField("gDefault");
            gField.setAccessible(true);
            Object g = gField.get(null);
            if (g == null) {
                Method gMethod = amnClazz.getDeclaredMethod("getDefault");
                gMethod.setAccessible(true);
                gMethod.invoke(null);

                g = gField.get(null);
            }

            final Object instanceProxy;
            Object instance;
            if (Class.forName("android.app.IActivityManager").isInstance(g)) {
                instanceProxy = Proxy.newProxyInstance(g.getClass().getClassLoader(), g.getClass().getInterfaces(), new ActivityHandler(g));
                gField.set(null, instanceProxy);
            } else if (Class.forName("android.util.Singleton").isInstance(g)) {
                Field instField = g.getClass().getSuperclass().getDeclaredField("mInstance");
                instField.setAccessible(true);
                instance = instField.get(g);
                if (instance == null) {
                    Method get = g.getClass().getDeclaredMethod("get");
                    get.invoke(g);

                    instance = instField.get(g);
                }

                instanceProxy = Proxy.newProxyInstance(instance.getClass().getClassLoader(), instance.getClass().getInterfaces(), new ActivityHandler(instance));
                gField.set(null, new Singleton() {
                    @Override
                    protected Object create() {
                        return instanceProxy;
                    }
                });
            }
        } catch (Exception e) {
            LogUtil.e("ANIME", "ActivityInjection.injectActivityManager(), " + e);
            e.printStackTrace();
        }
    }


    private static class ActivityHandler implements InvocationHandler {

        private Object mDefaultActivityManager;

        public ActivityHandler(Object am) {
            this.mDefaultActivityManager = am;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            LogUtil.v("ANIME", "ActivityHandler." + method.getName() + "(" + Arrays.toString(args) + ")");
//            if ("finishActivity".equals(method.getName())) {
//                LogUtil.v("ANIME", "ActivityHandler.finishActivity()");
//            } else if ("handleApplicationCrash".equals(method.getName())) {
//                LogUtil.v("ANIME", "ActivityHandler.handleApplicationCrash()");
//            }
            Object object = method.invoke(this.mDefaultActivityManager, args);
            return object;
        }
    }
}
