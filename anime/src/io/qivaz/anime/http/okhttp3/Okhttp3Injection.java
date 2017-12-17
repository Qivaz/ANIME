package io.qivaz.anime.http.okhttp3;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/4/8.
 */
public class Okhttp3Injection {

    public static void run(LifecycleMonitor lcm) {
        inject(lcm);
    }

    public static void inject(LifecycleMonitor lcm) {
        try {
            /*Class<?> clazz = lcm.getClass().getClassLoader().loadClass("okhttp3.Dns");
            Field field = clazz.getDeclaredField("SYSTEM");
            field.setAccessible(true);
            Object sys = field.get(null);

            Object systemProxy = Proxy.newProxyInstance(lcm.getClass().getClassLoader(),
                    new Class[]{Window.Callback.class}, new Okhttp3DnsInvocationHandler(lcm, sys));

            field.set(null, systemProxy);*/

            Class<?> clazz1 = lcm.getClass().getClassLoader().loadClass("java.net.InetAddress");
            Field cacheField1 = clazz1.getDeclaredField("addressCache");
            cacheField1.setAccessible(true);
            Object addressCache = cacheField1.get(null);

            Class<?> clazz2 = lcm.getClass().getClassLoader().loadClass("java.net.AddressCache");
            Field cacheField2 = clazz1.getDeclaredField("cache");
            cacheField2.setAccessible(true);
            Object cache = cacheField2.get(addressCache);

            //LogUtil.v("ANIME", "Okhttp3Injection, " + cache);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class Okhttp3DnsInvocationHandler implements InvocationHandler {
        private LifecycleMonitor mLcm;
        private Object mOrig;

        public Okhttp3DnsInvocationHandler(LifecycleMonitor lcm, Object obj) {
            mLcm = lcm;
            mOrig = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //LogUtil.v("ANIME", "Okhttp3DnsInvocationHandler.invoked:" + method.getName() + "()");
            Object obj = method.invoke(mOrig, args);
            return obj;
        }
    }
}
