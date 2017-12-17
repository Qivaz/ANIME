package io.qivaz.anime.message;

import android.os.Message;

/**
 * @author Qinghua Zhang @create 2017/4/13.
 */
public class MessageUtil {
    static MessageWrapper mMw;

    public static void obtainMessage() {
        if (mMw == null) {
            mMw = MainMessageQueueInject.getMW();
        }

        if (mMw != null) {
            mMw.offer();
        }
    }

    public static Message getMessage() {
        if (mMw == null) {
            mMw = MainMessageQueueInject.getMW();
        }

        Message msg = null;
        if (mMw != null) {
            msg = mMw.poll();
        }
        return msg;
    }
}
