package io.qivaz.anime.message;

import android.os.Message;
import android.os.MessageQueue;

import java.lang.reflect.Field;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/4/13.
 */
public class MessageWrapper {
    public static final int TGT_CB_CLICK_ID = 0;
    public static final int TGT_CB_LCLICK_PRE_ID = 1;
    public static final int TGT_CB_FRAME_ID = 2;
    public static final int TGT_CB_UNSETPRESS_ID = 3;
    public static final int TGT_CB_LIST_TAP_ID = 4;
    public static final int TGT_CB_LIST_CLICK_ID = 5;
    public static final int TGT_ID = 0;
    public static final int CB_ID = 1;
    public static final int CB_REGEX_ID = 2;
    public static final String TGT_ID_VIEW_ROOT = "android.view.ViewRootImpl$ViewRootHandler";
    public static final String TGT_ID_CHOR_FRAME = "android.view.Choreographer$FrameHandler";

    public static final String[][] TGT_CB_CL = {
            {TGT_ID_VIEW_ROOT, "android.view.View$PerformClick", "^android\\.view\\.View$PerformClick$"},
            {TGT_ID_VIEW_ROOT, "android.view.View$CheckForLongPress", "^android\\.view\\.View\\$CheckForLongPress$"},
            {TGT_ID_CHOR_FRAME, "android.view.Choreographer$FrameDisplayEventReceiver", "^android\\.view\\.Choreographer\\$FrameDisplayEventReceiver$"},
            {TGT_ID_VIEW_ROOT, "android.view.View$UnsetPressedState", "^android\\.view\\.View$UnsetPressedState$"},
            {TGT_ID_VIEW_ROOT, "android.widget.AbsListView$CheckForTap", "^android\\.widget\\.AbsListView\\$CheckForTap$"},
            {TGT_ID_VIEW_ROOT, "android.widget.AbsListView$", "^android\\.widget\\.AbsListView\\$\\d+$"}};

    private static MessageQueue Q;
    private static Field M;
    private static Field N;
    private static Message mMsg;

    MessageWrapper(MessageQueue q, Field f) {
        Q = q;
        M = f;
        try {
            N = Message.class.getDeclaredField("next");
            N.setAccessible(true);
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "MessageWrapper(), " + e);
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.e("ANIME", "MessageWrapper(), " + e);
            e.printStackTrace();
        }
    }

    public void offer() {
        Message msg = null;
        boolean bReady = false;
        try {
            msg = (Message) M.get(Q);

            while (msg != null) {
                if (msg.getCallback() != null
                        && (msg.getCallback().getClass().getName().equals(TGT_CB_CL[TGT_CB_CLICK_ID][CB_ID])
                        || msg.getCallback().getClass().getName().matches(TGT_CB_CL[TGT_CB_LIST_CLICK_ID][CB_REGEX_ID]))) {
                    //LogUtil.v("ANIME", "MessageWrapper.offer(), found msg=" + msg);
                    bReady = true;
                    break;
                } else {
                    msg = (Message) N.get(msg);
                }
            }
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "MessageWrapper.offer(), " + e);
            e.printStackTrace();
        } catch (Exception e) {
            LogUtil.e("ANIME", "MessageWrapper.offer(), " + e);
            e.printStackTrace();
        }

        if (bReady && msg != null) {
            mMsg = Message.obtain(msg);
        }
    }

    public Message peek() {
        return mMsg;
    }

    public Message poll() {
        Message msg = mMsg;
        mMsg = null;
        return msg;
    }
}
