package io.qivaz.anime.dropbox;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
public class DropBoxManagerProxy {
    public static Object createProxy() {
        try {
            IBinder binder = ServiceManager.getService(Context.DROPBOX_SERVICE);
            Object nf = asInterface(binder);
            return Proxy.newProxyInstance(nf.getClass().getClassLoader(), nf.getClass().getInterfaces(), new DropBoxManagerProxy.DropBoxManagerHandler(nf));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getName() {
        return Context.DROPBOX_SERVICE;
    }

    public static Object asInterface(IBinder binder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("com.android.internal.os.IDropBoxManagerService$Stub");
        Method method = clazz.getDeclaredMethod("asInterface", new Class[]{IBinder.class});
        method.setAccessible(true);
        return method.invoke((Object) null, new Object[]{binder});
    }

    private static class DropBoxManagerHandler implements InvocationHandler {

        private Object mDefaultDropBoxManager;

        public DropBoxManagerHandler(Object nf) {
            this.mDefaultDropBoxManager = nf;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            //LogUtil.v("ANIME", "DropBoxManagerHandler.invoked: " + methodName + "(" + args + ")");
            if ("notifyANR".equals(methodName)) {
                //LogUtil.v("ANIME", "DropBoxManagerHandler.invoked: " + methodName + "(" + args + ")");
            }

            Object result = method.invoke(this.mDefaultDropBoxManager, args);
            return result;
        }
    }
}
