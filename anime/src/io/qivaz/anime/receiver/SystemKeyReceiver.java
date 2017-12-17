package io.qivaz.anime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.qivaz.anime.systemkey.SystemKeyHandler;

/**
 * @author Qinghua Zhang @create 2017/5/8.
 */
public class SystemKeyReceiver extends BroadcastReceiver {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {// android.intent.action.CLOSE_SYSTEM_DIALOGS
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            //LogUtil.v("ANIME", "SystemKeyReceiver.onReceive(), action=" + action + ", reason=" + reason);
            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {// Click Home Key
                SystemKeyHandler.getInstance().onHomeKey();
            } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {// Long-Press Home Key or click Switch Key
                SystemKeyHandler.getInstance().onSwitchKey();
            } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {// Long-Press Home Key for Samsung
                SystemKeyHandler.getInstance().onSwitchKey();
            } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {// Click Lock Key
            }
        }
    }
}