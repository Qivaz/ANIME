package io.qivaz.anime.dropbox;

import android.content.Context;
import android.os.DropBoxManager;

import java.lang.reflect.Field;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
public class DropBoxManagerInjection {
    public static void run(LifecycleMonitor lcm, Context context) {
        inject(lcm, context);
    }

    private static void inject(LifecycleMonitor lcm, Context context) {
        Object proxy;
        Field dropbox;
        try {
            proxy = DropBoxManagerProxy.createProxy();
            DropBoxManager dbox = (DropBoxManager) context.getSystemService(Context.DROPBOX_SERVICE);

            dropbox = DropBoxManager.class.getDeclaredField("mService");
            dropbox.setAccessible(true);
            dropbox.set(dbox, proxy);

            //LogUtil.v("ANIME", "" + dropbox);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
