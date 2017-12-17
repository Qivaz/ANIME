package io.qivaz.anime.surface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Qinghua Zhang @create 2017/3/29.
 */
public class SurfaceControlWorker {

    public static void startScreenshot() {
        String ret = null;
        String cmd = "export CLASSPATH=/data/app/[app_package_name]/base.apk";
        ret = doExec(cmd);
        cmd = "exec app_process /system/bin io.qivaz.anime.surface.SurfaceControlRunnable '$@'";
        ret = doExec(cmd);
        //LogUtil.v("ANIME", "SurfaceControlWorker.startScreenshot(), " + ret);
    }

    private static String doExec(String cmd) {
        String s = "/n";
        BufferedReader in = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "/n";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
}
