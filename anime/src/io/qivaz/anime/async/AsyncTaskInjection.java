package io.qivaz.anime.async;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/4/10.
 */
public class AsyncTaskInjection {
    private static WeakReference<Activity> mWeakRefActivity;
    private static boolean mExecutorSet;
    private static Field mDefaultExecutorField;
    private static Field mThreadPoolExecutorField;
    private static boolean injectOnce = false;

    public static void run() {
        if (!injectOnce) {
            inject();
            injectOnce = true;
        }
    }

    public static void setActivity(Activity activity) {
        mWeakRefActivity = new WeakReference<>(activity);
    }

    private static void inject() {
        try {
            Class<?> atClass = AsyncTask.class;
            if (!mExecutorSet) {
                mDefaultExecutorField = atClass.getDeclaredField("sDefaultExecutor");
                mDefaultExecutorField.setAccessible(true);
                mThreadPoolExecutorField = atClass.getDeclaredField("THREAD_POOL_EXECUTOR");
                mThreadPoolExecutorField.setAccessible(true);
                mExecutorSet = true;
            }

            Object threadPoolExecutor = mThreadPoolExecutorField.get(null);
            if (!"java.lang.reflect.Proxy".equals(threadPoolExecutor.getClass().getSuperclass().getName())) { //!java.lang.reflect.Proxy
                Object threadPoolExecutorProxy = Proxy.newProxyInstance(atClass.getClassLoader(),
                        new Class[]{Executor.class}, new ThreadPoolExecutorInvocationHandler(threadPoolExecutor));
                mThreadPoolExecutorField.set(null, threadPoolExecutorProxy);
            }

            Object defaultExecutor = mDefaultExecutorField.get(null);
            if (!"java.lang.reflect.Proxy".equals(defaultExecutor.getClass().getSuperclass().getName())) { //!java.lang.reflect.Proxy
                Object defaultExecutorProxy = Proxy.newProxyInstance(atClass.getClassLoader(),
                        new Class[]{Executor.class}, new DefaultExecutorInvocationHandler(defaultExecutor));
                mDefaultExecutorField.set(null, defaultExecutorProxy);
            }
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "AsyncTaskInjection.inject(), " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "AsyncTaskInjection.inject(), " + e);
            e.printStackTrace();
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "AsyncTaskInjection.inject(), " + e);
            e.printStackTrace();
        }
        //LogUtil.v("ANIME", "AsyncTaskInjection.inject(), finished");
    }

    private static class DefaultExecutorInvocationHandler implements InvocationHandler {
        private Object mExecutor;

        public DefaultExecutorInvocationHandler(Object executor) {
            mExecutor = executor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("execute".equals(method.getName()) && args.length == 1) {
                if (args[0] instanceof FutureTask) {
                    FutureTask ft = (FutureTask) args[0];
                    Field callField = null;
                    try {
                        callField = FutureTask.class.getDeclaredField("callable");
                        callField.setAccessible(true);

                        Field parentField = ft.getClass().getDeclaredField("this$0");
                        parentField.setAccessible(true);
                        Object parent = parentField.get(ft);

                        Object callable = callField.get(ft);
                        if ("android.os.AsyncTask$WorkerRunnable".equals(callable.getClass().getSuperclass().getName())) { //!java.lang.reflect.Proxy
                            Object callableProxy = Proxy.newProxyInstance(FutureTask.class.getClassLoader(),
                                    new Class[]{Callable.class}, new CallableInvocationHandler(callable, parent.getClass().getName()));
                            callField.set(ft, callableProxy);
                        }
                    } catch (NoSuchFieldException e) {
                        LogUtil.e("ANIME", "AsyncTaskInjection.DefaultExecutorInvocationHandler.invoke(), " + e);
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        LogUtil.e("ANIME", "AsyncTaskInjection.DefaultExecutorInvocationHandler.invoke(), " + e);
                        e.printStackTrace();
                    } catch (Exception e) {
                        LogUtil.e("ANIME", "AsyncTaskInjection.DefaultExecutorInvocationHandler.invoke(), " + e);
                        e.printStackTrace();
                    }
                }
            }
            Object obj = method.invoke(mExecutor, args);
            //LogUtil.v("ANIME", "AsyncTaskInjection.DefaultExecutorInvocationHandler." + method.getName() + "(" + Arrays.toString(args) + "), return(" + obj + ")");
            return obj;
        }
    }

    private static class ThreadPoolExecutorInvocationHandler implements InvocationHandler {
        private Object mExecutor;

        public ThreadPoolExecutorInvocationHandler(Object executor) {
            mExecutor = executor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("execute".equals(method.getName()) && args.length == 1) {
                if (args[0] instanceof FutureTask) {
                    FutureTask ft = (FutureTask) args[0];
                    Field callField = null;
                    try {
                        callField = FutureTask.class.getDeclaredField("callable");
                        callField.setAccessible(true);

                        Field parentField = ft.getClass().getDeclaredField("this$0");
                        parentField.setAccessible(true);
                        Object parent = parentField.get(ft);

                        Object callable = callField.get(ft);
                        if ("android.os.AsyncTask$WorkerRunnable".equals(callable.getClass().getSuperclass().getName())) { //!java.lang.reflect.Proxy
                            Object callableProxy = Proxy.newProxyInstance(FutureTask.class.getClassLoader(),
                                    new Class[]{Callable.class}, new CallableInvocationHandler(callable, parent.getClass().getName()));
                            callField.set(ft, callableProxy);
                        }
                    } catch (NoSuchFieldException e) {
                        LogUtil.e("ANIME", "AsyncTaskInjection.ThreadPoolExecutorInvocationHandler.invoke(), " + e);
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        LogUtil.e("ANIME", "AsyncTaskInjection.ThreadPoolExecutorInvocationHandler.invoke(), " + e);
                        e.printStackTrace();
                    } catch (Exception e) {
                        LogUtil.e("ANIME", "AsyncTaskInjection.ThreadPoolExecutorInvocationHandler.invoke(), " + e);
                        e.printStackTrace();
                    }
                }
            }
            Object obj = method.invoke(mExecutor, args);
            //LogUtil.v("ANIME", "AsyncTaskInjection.ThreadPoolExecutorInvocationHandler." + method.getName() + "(" + Arrays.toString(args) + "), return(" + obj + ")");
            return obj;
        }
    }

    private static class CallableInvocationHandler implements InvocationHandler {
        private Object mCallable;
        private String mClass;

        public CallableInvocationHandler(Object callable, String parent) {
            mCallable = callable;
            mClass = parent;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String traceId = UUID.randomUUID().toString();
            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                AnimeInjection.getLifecycleMonitor().onAsyncStart(mClass, traceId, null);
            }
            Object ret = method.invoke(mCallable, args);
            //LogUtil.v("ANIME", "AsyncTaskInjection.CallableInvocationHandler." + method.getName() + "(" + Arrays.toString(args) + "), return(" + ret + ")");

            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                AnimeInjection.getLifecycleMonitor().onAsyncFinish(mClass, traceId, null);
            }
            return ret;
        }
    }
}
