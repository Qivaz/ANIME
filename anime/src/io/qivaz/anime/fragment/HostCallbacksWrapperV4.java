package io.qivaz.anime.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentHostCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Qinghua Zhang @create 2017/3/27.
 */
public class HostCallbacksWrapperV4 extends FragmentHostCallback<FragmentActivity> {

//    {
//        Handler handler = null;
//        try {
//            Class<?> clazz = fa.getClass().getClassLoader().loadClass("android.support.v4.app.FragmentActivity");
//            Field field = clazz.getDeclaredField("mHandler");
//            field.setAccessible(true);
//            Object obj = field.get(fa);
//            handler = (Handler) obj;
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }

    private static Handler mH;
    private FragmentActivity mA;

    /**
     * Must invoke after setHandler()
     *
     * @param context
     * @param handler
     * @param windowAnimations
     */
    public HostCallbacksWrapperV4(Context context, Handler handler, int windowAnimations) {
        super(context, mH, 0);

        mA = (FragmentActivity) context;

        try {
            ClassLoader cl = mA.getClass().getClassLoader();
            Class<?> clazz = cl.loadClass("android.support.v4.app.FragmentHostCallback");
            Field field = clazz.getDeclaredField("mActivity");
            field.setAccessible(true);
            field.set(this, mA);


//            Class<?> clazz1 = cl.loadClass("android.support.v4.app.FragmentActivity");
//            Field field1 = clazz1.getDeclaredField("mHandler");
//            field1.setAccessible(true);
//            Object obj = field1.get(mActivity);
//            handler = (Handler) obj;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setHandler(FragmentActivity activity) {
        try {
            ClassLoader cl = activity.getClass().getClassLoader();
            Class<?> clazz = cl.loadClass("android.support.v4.app.FragmentActivity");
            Field field = clazz.getDeclaredField("mHandler");

            field.setAccessible(true);
            Object obj = field.get(activity);
            mH = (Handler) obj;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        mA.dump(prefix, fd, writer, args);
    }

    @Override
    public boolean onShouldSaveFragmentState(Fragment fragment) {
        return !mA.isFinishing();
    }

    @Override
    public LayoutInflater onGetLayoutInflater() {
        return mA.getLayoutInflater().cloneInContext(mA);
    }

    @Override
    public FragmentActivity onGetHost() {
        return mA;
    }

    @Override
    public void onSupportInvalidateOptionsMenu() {
        mA.supportInvalidateOptionsMenu();
    }

    @Override
    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        mA.startActivityFromFragment(fragment, intent, requestCode);
    }

    @Override
    public void onStartActivityFromFragment(
            Fragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
        mA.startActivityFromFragment(fragment, intent, requestCode, options);
    }

    @Override
    public void onRequestPermissionsFromFragment(@NonNull Fragment fragment,
                                                 @NonNull String[] permissions, int requestCode) {
//        mA.requestPermissionsFromFragment(fragment, permissions, requestCode);
        try {
            Class<?> clazz = mA.getClass().getClassLoader().loadClass("android.support.v4.app.FragmentActivity");
            Method method = clazz.getDeclaredMethod("requestPermissionsFromFragment");
            method.setAccessible(true);
            method.invoke(mA, fragment, permissions, requestCode);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onShouldShowRequestPermissionRationale(@NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                mA, permission);
    }

    @Override
    public boolean onHasWindowAnimations() {
        return mA.getWindow() != null;
    }

    @Override
    public int onGetWindowAnimations() {
        final Window w = mA.getWindow();
        return (w == null) ? 0 : w.getAttributes().windowAnimations;
    }

//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        mA.onAttachFragment(fragment);
//    }

    @Nullable
    @Override
    public View onFindViewById(int id) {
        return mA.findViewById(id);
    }

    @Override
    public boolean onHasView() {
        final Window w = mA.getWindow();
        return (w != null && w.peekDecorView() != null);
    }
}
