package io.qivaz.anime.surface;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Qinghua Zhang @create 2017/3/29.
 */
public class ScreenshotUtil {
    private static int SCREENSHOT_WIDTH = 480;
    private static int SCREENSHOT_HEIGHT = 720;
    private static Method screenshotMethod;
    private static String screenshotPath;
    private static Context context;

    public static void setContext(Context c) {
        context = c;
    }

    public static void setScreenshotMethod(Method method) {
        screenshotMethod = method;
    }

    public static void setScreenshotSize(int width, int height) {
        SCREENSHOT_WIDTH = width;
        SCREENSHOT_HEIGHT = height;
    }

    public static void setScreenshotPath(String path) {
        screenshotPath = path;
    }

    public static Bitmap createScreenshotBitmap() {
        try {
            return (Bitmap) screenshotMethod.invoke(null, new Object[]{Integer.valueOf(SCREENSHOT_WIDTH), Integer.valueOf(SCREENSHOT_HEIGHT)});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveScreenshot(String path, String filename) {
        Bitmap b = createScreenshotBitmap();
        if (TextUtils.isEmpty(path)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (TextUtils.isEmpty(filename)) {
            filename = "anime_screenshot_" + System.currentTimeMillis();
        }
        File f = new File(path, filename);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap createActivityScreenshotBitmap(Activity activity) {
        return createActivityScreenshotBitmap(activity, false);
    }

    private static Bitmap createActivityScreenshotBitmap(Activity activity, boolean statusbar) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        int y0 = 0;
        if (!statusbar) {
            // Status Bar Height
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            y0 = statusBarHeight;
        }
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(bitmap, 0, y0, width, height - y0);

        view.destroyDrawingCache();

        return b;
    }

    public static void saveActivityScreenshot(Activity activity, String path, String filename) {
        Bitmap b = createActivityScreenshotBitmap(activity);
        if (TextUtils.isEmpty(path)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (TextUtils.isEmpty(filename)) {
            filename = "anime_screenshot_" + System.currentTimeMillis() + ".png";
        }
        File f = new File(path, filename);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
