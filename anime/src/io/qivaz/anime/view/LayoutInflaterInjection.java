package io.qivaz.anime.view;

import android.app.Activity;
import android.view.LayoutInflater;

import java.lang.reflect.Field;

/**
 * @author Qinghua Zhang @create 2017/3/31.
 */
public class LayoutInflaterInjection {
    private static Field sLayoutInflaterField;
    private static boolean sCheckedField;

    public static void run(Activity activity) {
        inject2(activity);
    }

    public static void inject(LayoutInflater li) {
        LayoutInflaterFactory2Compat.setFactory2Wrapper(li, li.getFactory2());
    }

    public static void inject2(Activity activity) {
        LayoutInflater li = activity.getLayoutInflater();
        ViewCreateFactory2Impl vcf2 = new ViewCreateFactory2Impl(activity, li);
        try {
            if (!sCheckedField) {
                Class<?> liClass = li.getClass();
                while (!"android.view.LayoutInflater".equals(liClass.getName())
                        && !"java.lang.Object".equals(liClass.getName())) {
                    liClass = liClass.getSuperclass();
                }
                if ("java.lang.Object".equals(liClass.getName())) {
                    return;
                }
                sLayoutInflaterField = liClass.getDeclaredField("mFactorySet");
                sLayoutInflaterField.setAccessible(true);

                sCheckedField = true;
            }

            sLayoutInflaterField.set(li, false);
            li.setFactory2(vcf2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
