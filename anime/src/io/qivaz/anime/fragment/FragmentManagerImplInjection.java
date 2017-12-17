//package io.qivaz.anime.fragment;
//
//import android.app.Activity;
//
//import com.android.dx.stock.ProxyBuilder;
//
//import java.io.IOException;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Modifier;
//
//import io.qivaz.anime.MonitorLifecycle;
//import io.qivaz.anime.log.LogUtil;
//
///**
// * @author Qinghua Zhang @create 2017/3/24.
// */
//public class FragmentManagerImplInjection {
//    public static void run(MonitorLifecycle mlc, Activity activity) {
//        inject(mlc, activity);
//    }
//
//    private static void inject(MonitorLifecycle mlc, Activity activity) {
//        boolean isV4 = false;
//        Class<?> activityClass = activity.getClass();
//        ClassLoader cl = activityClass.getClassLoader();
//        while (!"android.app.Activity".equals(activityClass.getName())
//                && !"android.support.v4.app.FragmentActivity".equals(activityClass.getName())
//                && !"java.lang.Object".equals(activityClass.getName())) {
//            activityClass = activityClass.getSuperclass();
//        }
//        if ("java.lang.Object".equals(activityClass.getName())) {
//            return;
//        } else if ("android.app.Activity".equals(activityClass.getName())) {
//            isV4 = false;
//        } else { //"android.support.v4.app.FragmentActivity".equals(activityClass.getName()
//            isV4 = true;
//        }
//
//        try {
//            Field field = activityClass.getDeclaredField("mFragments");
//            field.setAccessible(true);
//            Object fc = field.get(activity);
//            
//            Field hostFiled = fc.getClass().getDeclaredField("mHost");
//            hostFiled.setAccessible(true);
//            Object host = hostFiled.get(fc);
//            
//            Field fieldFm = host.getClass().getSuperclass().getDeclaredField("mFragmentManager");
//            fieldFm.setAccessible(true);
//
//            if (isV4) {
//                Class<?> clazz = cl.loadClass("android.support.v4.app.FragmentManagerImpl");
//                Field conField = clazz.getClass().getDeclaredField("accessFlags");
//                conField.setAccessible(true);
//                int modif = (int)conField.get(clazz);
//                modif &= ~Modifier.FINAL;
//                conField.set(clazz, modif);
//                        
//                Class<?> proxyClass = ProxyBuilder.forClass(clazz).buildProxyClass();
//
//                Object fragmentsProxy = proxyClass.newInstance();
//                FragmentControllerInvocationHandler handler = new FragmentControllerInvocationHandler(mlc, activity, fc);
//                ProxyBuilder.setInvocationHandler(fragmentsProxy, handler);
//                fieldFm.set(host, fragmentsProxy);
//            } else {
//
//            }
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //return null;
//    }
//
//    private static class FragmentControllerInvocationHandler implements InvocationHandler {
//        private MonitorLifecycle mMlc;
//        private Object mFc;
//        private Activity mActivity;
//
//        public FragmentControllerInvocationHandler(MonitorLifecycle mlc, Activity activity, Object fc) {
//            mMlc = mlc;
//            mActivity = activity;
//            mFc = fc;
//        }
//
//        @Override
//        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            LogUtil.v("ANIME", "FragmentControllerInvocationHandler.invoked:" + method.getName() + "()");
//            Object obj = method.invoke(mFc, args);
//
//            return obj;
//        }
//    }
//
//}
