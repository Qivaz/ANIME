package io.qivaz.anime.surface;

import android.os.Looper;

/**
 * @author Qinghua Zhang @create 2017/3/29.
 */
public class SurfaceControlRunnable {

    static Looper looper;

    public static void main(String[] args) {
        Looper.prepare();
        looper = Looper.myLooper();
        ScreenshotUtil.saveScreenshot(null, null);

        Looper.loop();

    }

}
