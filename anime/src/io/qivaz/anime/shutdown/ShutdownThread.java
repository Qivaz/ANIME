package io.qivaz.anime.shutdown;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/5/8.
 */
public class ShutdownThread extends Thread {
    private static LifecycleMonitor mLcm;

    public ShutdownThread(LifecycleMonitor lcm) {
        mLcm = lcm;
    }

    @Override
    public void run() {
        //LogUtil.v("ANIME", "ShutdownThread.run()");
        //LogUtil.w("ANIME", "ShutdownThread.run(), mLcm.onExit(true) start");
        mLcm.onExit(true);
        //LogUtil.w("ANIME", "ShutdownThread.run(), mLcm.onExit(true) finish");
//        try {
//            //LogUtil.w("ANIME", "ShutdownThread.run(), sleep(1000) start");
//            sleep(500);
//            //LogUtil.w("ANIME", "ShutdownThread.run(), sleep(1000) finish");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
