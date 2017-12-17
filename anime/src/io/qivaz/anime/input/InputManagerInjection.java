package io.qivaz.anime.input;

import android.hardware.input.InputManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
public class InputManagerInjection {
    public static void run() {
        inject();
    }

    private static void inject() {
        Object proxy;
        Field input;
        try {
            proxy = InputManagerProxy.createProxy();
            Method instanceMethod = InputManager.class.getDeclaredMethod("getInstance");
            instanceMethod.setAccessible(true);
            Object im = instanceMethod.invoke(null);

            input = InputManager.class.getDeclaredField("mIm");
            input.setAccessible(true);
            input.set(im, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
