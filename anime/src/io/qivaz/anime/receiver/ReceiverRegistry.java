package io.qivaz.anime.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author Qinghua Zhang @create 2017/5/8.
 */
public class ReceiverRegistry {
    public static void run(Context context) {
        registerAnrReceiver(context);
        registerHomeKeyReceiver(context);
    }

    private static void registerAnrReceiver(Context context) {
        AnrReceiver mAnrReceiver = new AnrReceiver();
        final IntentFilter anrFilter = new IntentFilter("android.intent.action.ANR");
        context.registerReceiver(mAnrReceiver, anrFilter);
    }

    private static void registerHomeKeyReceiver(Context context) {
        SystemKeyReceiver mSystemKeyReceiver = new SystemKeyReceiver();
        final IntentFilter keyFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mSystemKeyReceiver, keyFilter);
    }
}
