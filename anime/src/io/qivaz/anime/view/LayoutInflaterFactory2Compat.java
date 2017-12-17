package io.qivaz.anime.view;

/**
 * @author Qinghua Zhang @create 2017/3/31.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;

class LayoutInflaterFactory2Compat {
    private static Field sLayoutInflaterFactory2Field;
    private static boolean sCheckedField;

    static void setFactory2Wrapper(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        LayoutInflater.Factory2 f2 = inflater.getFactory2();
        if (f2 != null) {
            setFactory2(inflater, factory);
        } else {
//            setFactory2(inflater, new ViewCreateFactory2Impl(inflater));
        }
    }

    static void setFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        final LayoutInflater.Factory2 factory2 = factory != null
                ? new Factory2Wrapper(factory) : null;
        forceSetFactory2(inflater, factory2);
    }

    /**
     * For APIs >= 11 && < 21, there was a framework bug that prevented a LayoutInflater's
     * Factory2 from being merged properly if set after a cloneInContext from a LayoutInflater
     * that already had a Factory2 registered. We work around that bug here. If we can't we
     * log an error.
     */
    static void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        if (!sCheckedField) {
            try {
                sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
                sLayoutInflaterFactory2Field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            sCheckedField = true;
        }
        if (sLayoutInflaterFactory2Field != null) {
            try {
                sLayoutInflaterFactory2Field.set(inflater, factory);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    static class Factory2Wrapper implements LayoutInflater.Factory2 {

        final LayoutInflater.Factory2 mDelegateFactory;

        Factory2Wrapper(LayoutInflater.Factory2 delegateFactory) {
            mDelegateFactory = delegateFactory;
        }

        @Override
        public View onCreateView(View parent, String name, Context context,
                                 AttributeSet attributeSet) {
            View view = mDelegateFactory.onCreateView(parent, name, context, attributeSet);
            if (view != null) {
                ViewOnClickListenerCompat.setWrapper(view);
            }
            return view;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View view = mDelegateFactory.onCreateView(null, name, context, attrs);
            return view;
        }

        @Override
        public String toString() {
            return getClass().getName() + "{" + mDelegateFactory + "}";
        }
    }

}

