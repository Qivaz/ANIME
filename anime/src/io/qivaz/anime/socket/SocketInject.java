package io.qivaz.anime.socket;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.net.SocketFactory;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketInject {
    private static boolean mSocketFactorySet;
    private static Field mFactotryField;
    private static SocketFactory mDefaultSocketFactory;

    public static void run() {
        inject();
    }

    private static void inject() {
        if (!mSocketFactorySet) {
            try {
                final Class<?> socketFactoryClass = SocketFactory.class;
                final Method method = socketFactoryClass.getMethod("getDefault");
                mDefaultSocketFactory = (SocketFactory) method.invoke(null, (Object[]) null);
                try {
                    mFactotryField = socketFactoryClass.getDeclaredField("theFactory");
                    mFactotryField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    mFactotryField = socketFactoryClass.getDeclaredField("defaultFactory");
                    mFactotryField.setAccessible(true);
                }
                SocketFactory wrapper = new SocketFactoryWrapper(mDefaultSocketFactory);
                mFactotryField.set(null, wrapper);

                mSocketFactorySet = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}