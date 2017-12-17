package io.qivaz.anime.view;

import android.view.View;

import java.lang.reflect.Field;

/**
 * @author Qinghua Zhang @create 2017/3/31.
 */
public class ViewOnClickListenerCompat {
    private static Field sViewListenerInfoField;
    private static Field sViewListenerField;
    private static boolean sCheckedField;

    public static void setWrapper(View view) {
        ClassLoader cl = view.getClass().getClassLoader();

        try {
            if (!sCheckedField) {
                sViewListenerInfoField = View.class.getDeclaredField("mListenerInfo");
                sViewListenerInfoField.setAccessible(true);

                Class<?> clazz = cl.loadClass("android.view.View$ListenerInfo");
                sViewListenerField = clazz.getDeclaredField("mOnClickListener");
                sViewListenerField.setAccessible(true);

                sCheckedField = true;
            }

            Object info = sViewListenerInfoField.get(view);
            if (info != null) {
                Object l = sViewListenerField.get(info);
                if (l != null) {
                    OnClickListenerWrapper wrapper = new OnClickListenerWrapper((View.OnClickListener) l);
                    sViewListenerField.set(info, wrapper);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static class OnClickListenerWrapper implements View.OnClickListener {
        final View.OnClickListener mDelegateListener;

        OnClickListenerWrapper(View.OnClickListener listener) {
            mDelegateListener = listener;
        }

        @Override
        public void onClick(View v) {
            mDelegateListener.onClick(v);
        }
    }
}
