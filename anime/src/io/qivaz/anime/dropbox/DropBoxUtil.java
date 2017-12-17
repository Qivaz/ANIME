package io.qivaz.anime.dropbox;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.DropBoxManager;
import android.support.v4.content.ContextCompat;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
public class DropBoxUtil {
    public static void getText(Context context) throws Exception {
        DropBoxManager dropbox = (DropBoxManager) context.getSystemService(Context.DROPBOX_SERVICE);
//        long before = System.currentTimeMillis();
//        Thread.sleep(5);
//        dropbox.addText("DropBoxTest", "TEST0");
//        Thread.sleep(5);
//        long between = System.currentTimeMillis();
//        Thread.sleep(5);
//        dropbox.addText("DropBoxTest", "TEST1");
//        dropbox.addText("DropBoxTest", "TEST2");
//        Thread.sleep(5);
//        long after = System.currentTimeMillis();

        try {
            if (PackageManager.PERMISSION_GRANTED
                    == ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_LOGS)) {
                DropBoxManager.Entry e0 = dropbox.getNextEntry(getTag(), System.currentTimeMillis());
                DropBoxManager.Entry e1 = dropbox.getNextEntry(getTag(), e0.getTimeMillis());
                DropBoxManager.Entry e2 = dropbox.getNextEntry(getTag(), e1.getTimeMillis());
            }
        } catch (java.lang.NoSuchMethodError e) {
            LogUtil.e("ANIME", "writeCacheFile(1), lower V4 version, " + e);
            e.printStackTrace();
        }

//        e0.close();
//        e1.close();
//        e2.close();
    }

    private static String processClass() {
        /*if (process == null || process.pid == MY_PID) {
            return "system_server";
        } else if ((process.info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return "system_app";
        } else*/
        {
            return "data_app";
        }
    }

    private static String getTag() {
        String eventType = "anr";
        return processClass() + "_" + eventType;
    }
}
