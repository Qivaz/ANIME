package io.qivaz.anime.input;

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
public class InputManagerProxy {
    public static Object createProxy() {
        try {
            IBinder binder = ServiceManager.getService(Context.INPUT_SERVICE);
            Object nf = asInterface(binder);
            return Proxy.newProxyInstance(nf.getClass().getClassLoader(), nf.getClass().getInterfaces(), new InputManagerHandler(nf));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getName() {
        return Context.INPUT_SERVICE;
    }

    public static Object asInterface(IBinder binder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("android.hardware.input.IInputManager$Stub");
        Method method = clazz.getDeclaredMethod("asInterface", new Class[]{IBinder.class});
        method.setAccessible(true);
        return method.invoke((Object) null, new Object[]{binder});
    }

    private static class InputManagerHandler implements InvocationHandler {

        private Object mDefaultInputManager;

        public InputManagerHandler(Object nf) {
            this.mDefaultInputManager = nf;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            //LogUtil.v("ANIME", "InputManagerProxy.invoked: " + methodName + "(" + args + ")");
            if ("notifyANR".equals(methodName)) {
                //LogUtil.v("ANIME", "InputManagerProxy.invoked: " + methodName + "(" + args + ")");
            }

            Object result = method.invoke(this.mDefaultInputManager, args);
            return result;
        }
    }
}
