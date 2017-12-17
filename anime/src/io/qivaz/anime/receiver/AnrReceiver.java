package io.qivaz.anime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;
import io.qivaz.anime.monitor.lifecycle.LifecycleOption;

/**
 * @author Qinghua Zhang @create 2017/4/6.
 */
public class AnrReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.ANR".equals(intent.getAction())) {
            LifecycleOption lco = AnimeInjection.getLifecycleOption();
            if (lco != null && lco.enabledCollect() && lco.enabledParse()) {
                LogUtil.e("ANIME", "AnrReceiver.onReceive(), intent=" + intent);
                AnimeInjection.getLifecycleMonitor().onActivityAnr();
            }
        }
    }
}
