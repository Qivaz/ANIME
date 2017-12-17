package io.qivaz.anime.crash;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import io.qivaz.anime.R;
import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/4/20.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static boolean bAlreadyInvoked;
    private static Context mContext;
    private static CrashHandler mInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static synchronized CrashHandler getInstance(Context context) {
        mContext = context;
        return mInstance;
    }

    public synchronized void checkAndInit() {
        Thread.UncaughtExceptionHandler ueh = Thread.getDefaultUncaughtExceptionHandler();
        if (ueh != mInstance) {
            bAlreadyInvoked = false;
            mDefaultHandler = ueh;
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            LogUtil.e("ANIME", "CrashHandler.handleException(), " + ex);
            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                LifecycleMonitor lcm = AnimeInjection.getLifecycleMonitor();
                lcm.onAppCrash(ex);
            }
            return true;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        /*
         * Avoid recursively invoking
         */
        if (bAlreadyInvoked) {
            (new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, mContext.getString(R.string.anime_program_error), Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }).start();

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                LogUtil.e("ANIME", "uncaughtException(), Error: " + e);
            }
            System.exit(1);
            return;
        }
        bAlreadyInvoked = true;

        handleException(ex);
        if (mDefaultHandler != mInstance) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }
}