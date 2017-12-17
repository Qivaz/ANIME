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
//import java.util.Objects;
//
//import io.qivaz.anime.MonitorLifecycle;
//import io.qivaz.anime.log.LogUtil;
//
///**
// * @author Qinghua Zhang @create 2017/3/24.
// */
//public class FragmentControllerInjection {
//    public static void run(MonitorLifecycle mlc, Activity activity) {
//        inject(mlc, activity);
//    }
//
//    private static void inject(MonitorLifecycle mlc, Activity activity) {
//        boolean isV4 = false;
//        Class<?> activityClass = activity.getClass();
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
////            Field[] fields = activityClass.getDeclaredFields();
////            for (Field f : fields) {
////                LogUtil.v("ANIME", f.getName());
////            }
//            Field field = activityClass.getDeclaredField("mFragments");
//            field.setAccessible(true);
//            Object fc = field.get(activity);
//
//
//            Field hostFiled = fc.getClass().getDeclaredField("mHost");
//            hostFiled.setAccessible(true);
//            Object host = hostFiled.get(fc);
//
////            Object fragmentsProxy = Proxy.newProxyInstance(activity.getClass().getClassLoader(),
////                    new Class[]{android.app.FragmentController.class}, new FragmentControllerInvocationHandler(mlc, activity, fc));
//
//            Class proxyClass = null;
//            Object fragmentsProxy = null;
//            Constructor c = null;
//            Method m = null;
//            try {
//                if (isV4) {
//                    Class<?> clazz = android.support.v4.app.FragmentController.class;
//                    Constructor cFc = clazz.getDeclaredConstructor(android.support.v4.app.FragmentHostCallback.class);
//                    cFc.setAccessible(true);
//                    Field conField = cFc.getClass().getSuperclass().getDeclaredField("accessFlags");
//                    conField.setAccessible(true);
//                    int modif = (int)conField.get(cFc);
//                    modif &= ~Modifier.PRIVATE;
//                    modif |= Modifier.PUBLIC;
//                    conField.set(cFc, modif);
//
//                    /**
//                     * How to set Constructor back to Class?
//                     */
//                    
//                    proxyClass = ProxyBuilder.forClass(clazz).buildProxyClass();
//                    c = proxyClass.getDeclaredConstructor(android.support.v4.app.FragmentHostCallback.class);
//                    //m = proxyClass.getDeclaredMethod("createController", new Class[]{android.support.v4.app.FragmentHostCallback.class});
//                    
//                } else {
//                    proxyClass = ProxyBuilder.forClass(android.app.FragmentController.class).buildProxyClass();
//                    c = proxyClass.getDeclaredConstructor(new Class[]{android.app.FragmentHostCallback.class});
//                    //m = proxyClass.getDeclaredMethod("createController", new Class[]{android.app.FragmentHostCallback.class});
//                }
//                c.setAccessible(true);
//                fragmentsProxy = c.newInstance(host);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            }
//
//            FragmentControllerInvocationHandler handler = new FragmentControllerInvocationHandler(mlc, activity, fc);
//            ProxyBuilder.setInvocationHandler(fragmentsProxy, handler);
//
//
//            field.set(activity, fragmentsProxy);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
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
