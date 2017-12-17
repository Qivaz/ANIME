package io.qivaz.anime.surface;

import android.content.Context;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * @author Qinghua Zhang @create 2017/3/29.
 */
public class SurfaceControlInjection {

    public static void run(Context context) {
        inject(context);
    }

    public static void inject(Context context) {
        String surfaceClassName;
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }
        try {
            Method screenshotMethod = Class.forName(surfaceClassName)
                    .getDeclaredMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE});
            screenshotMethod.setAccessible(true);
            ScreenshotUtil.setContext(context);
            ScreenshotUtil.setScreenshotMethod(screenshotMethod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
