package io.qivaz.anime.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qinghua Zhang @create 2017/3/31.
 */
public class ViewCreateFactory2Impl implements LayoutInflater.Factory2 {
    LayoutInflater mLi;
    WeakReference<Activity> mWeakRefActivity;

    ViewCreateFactory2Impl(Activity activity, LayoutInflater li) {
        mWeakRefActivity = new WeakReference<>(activity);
        mLi = li;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = null;
        if (-1 == name.indexOf('.')) {
            view = onCreateViewHook(parent, name, attrs);
        } else {
            if (name.startsWith("com.android.internal.widget.")
                    || name.startsWith("android.support.design.widget.")) { //com.android.internal.widget.AlertDialogLayout
                return null;
            }

            try {
                view = mLi.createView(name, null, attrs);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if (view != null && view.hasOnClickListeners()) {
                ViewOnClickListenerCompat.setWrapper(view);
            } else if (view != null && view.isClickable()) {
                if (mWeakRefActivity.get() != null) {
                    List list;
                    if ((list = ActivityViewsStack.viewStack.get(mWeakRefActivity.get().hashCode())) == null) {
                        list = new ArrayList();
                        ActivityViewsStack.viewStack.put(mWeakRefActivity.get().hashCode(), list);
                    }
                    list.add(view);
                }

            }
        }
        return view;
    }

    private View onCreateViewHook(View parent, String name, AttributeSet attrs) {
        try {
            Class<?> liClass = mLi.getClass();
            while (!"android.view.LayoutInflater".equals(liClass.getName())
                    && !"java.lang.Object".equals(liClass.getName())) {
                liClass = liClass.getSuperclass();
            }
            if ("java.lang.Object".equals(liClass.getName())) {
                return null;
            }

            Class<?>[] methodSignature = new Class[]{View.class, String.class, AttributeSet.class};
            Method method = liClass.getDeclaredMethod("onCreateView", methodSignature);
            method.setAccessible(true);
            Object ret = method.invoke(mLi, parent, name, attrs);
            return (View) ret;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private View createViewHook(String name, String prefix, AttributeSet attrs) {
        try {
            Class<?> liClass = mLi.getClass();
            while (!"android.view.LayoutInflater".equals(liClass.getName())
                    && !"java.lang.Object".equals(liClass.getName())) {
                liClass = liClass.getSuperclass();
            }
            if ("java.lang.Object".equals(liClass.getName())) {
                return null;
            }
            Class<?>[] methodSignature = new Class[]{String.class, String.class, AttributeSet.class};
            Method method = liClass.getDeclaredMethod("createView", methodSignature);
            method.setAccessible(true);
            Object ret = method.invoke(name, prefix, attrs);
            return (View) ret;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }
}
