package io.qivaz.anime.message;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;

import java.lang.reflect.Field;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/4/13.
 */
public class MainMessageQueueInject {
    private static Looper L;
    private static Handler H;
    private static boolean mSet;
    private static Field mQField;
    private static Field mMField;
    private static MessageWrapper mMw;

    public static void run() {
        L = Looper.getMainLooper();
        inject();
    }

    public static void inject() {
        try {
            if (!mSet) {
                mQField = Looper.class.getDeclaredField("mQueue");
                mQField.setAccessible(true);
                mMField = MessageQueue.class.getDeclaredField("mMessages");
                mMField.setAccessible(true);
                mSet = true;
            }
            MessageQueue q = (MessageQueue) mQField.get(L);
            mMw = new MessageWrapper(q, mMField);
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "MainMessageQueueInject.inject(), " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "MainMessageQueueInject.inject(), " + e);
            e.printStackTrace();
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "MainMessageQueueInject.inject(), " + e);
            e.printStackTrace();
        }
    }

    static MessageWrapper getMW() {
        return mMw;
    }
}
