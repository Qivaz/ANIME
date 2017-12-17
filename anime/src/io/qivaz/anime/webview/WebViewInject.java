package io.qivaz.anime.webview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/4/28.
 */
public class WebViewInject {
    private static final boolean debug = false;
    private static boolean mWVSet;
    private static Class<?> mWVFClass;
    private static Method mProvider;
    private static Field mProviderInstance;
    private static Class<?> mWVClass;
    private static Field mProviderInstance2;

    public static void run() {
        inject();
    }

    private static void inject() {
        try {
            if (!mWVSet) {
                mWVFClass = WebViewInject.class.getClassLoader().loadClass("android.webkit.WebViewFactory");
                mProvider = mWVFClass.getDeclaredMethod("getProvider");
                mProvider.setAccessible(true);

                mProviderInstance = mWVFClass.getDeclaredField("sProviderInstance");
                mProviderInstance.setAccessible(true);


                mWVClass = WebViewInject.class.getClassLoader().loadClass("android.webkit.WebView");
                mProviderInstance2 = mWVClass.getDeclaredField("mProvider");
                mProviderInstance2.setAccessible(true);

                mWVSet = true;
            }

            Object provider = mProvider.invoke(null); //WebViewFactoryProvider
            Object providerInstance = mProviderInstance.get(null);

            if (debug) {
                LogUtil.w("ANIME/WebView", "provider=" + provider);
                LogUtil.w("ANIME/WebView", "provider.getClass()=" + provider.getClass());
                LogUtil.w("ANIME/WebView", "provider.getClass().getInterfaces()=" + Arrays.toString(provider.getClass().getInterfaces()));

                LogUtil.w("ANIME/WebView", "providerInstance=" + providerInstance);
                LogUtil.w("ANIME/WebView", "providerInstance.getClass()=" + providerInstance.getClass());
                LogUtil.w("ANIME/WebView", "providerInstance.getClass().getInterfaces()=" + Arrays.toString(providerInstance.getClass().getInterfaces()));
            }

            Object WVFPProxy = Proxy.newProxyInstance(WebViewInject.class.getClassLoader(),
                    provider.getClass().getInterfaces()/*WebViewFactoryProvider.class*/,
                    new WebViewFactoryProviderInvocationHandler(providerInstance));

            if (debug) {
                LogUtil.w("ANIME/WebView", "WVFPProxy=" + WVFPProxy);
                LogUtil.w("ANIME/WebView", "WVFPProxy.getClass()=" + WVFPProxy.getClass());
                LogUtil.w("ANIME/WebView", "WVFPProxy.getClass().getInterfaces()=" + Arrays.toString(WVFPProxy.getClass().getInterfaces()));
            }

            mProviderInstance.set(null, WVFPProxy);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "WebViewInject.inject(), " + e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LogUtil.e("ANIME", "WebViewInject.inject(), " + e);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            LogUtil.e("ANIME", "WebViewInject.inject(), " + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "WebViewInject.inject(), " + e);
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "WebViewInject.inject(), " + e);
            e.printStackTrace();
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "WebViewInject.inject(), " + e);
            e.printStackTrace();
        }
    }

    private static class WebViewFactoryProviderInvocationHandler implements InvocationHandler {
        private Object mInstance;

        public WebViewFactoryProviderInvocationHandler(Object instance) {
            mInstance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //LogUtil.v("ANIME", "WebViewFactoryProviderInvocationHandler." + method.getName() + "(), " + Arrays.toString(args));
            Object obj = method.invoke(mInstance, args);
            if ("createWebView".equals(method.getName())) {
                WebView wv = null;
                if (args.length > 0) {
                    wv = (WebView) args[0];
                } else {
                    LogUtil.e("ANIME", "WebViewFactoryProviderInvocationHandler(), WebView is null!!!");
                    return obj;
                }

                // Only keep WebViewProvider interface
                Class<?>[] all = obj.getClass().getInterfaces();
                Class<?>[] wvpOnly = new Class<?>[1];
                wvpOnly[0] = all[0];

                Object WVPProxy = Proxy.newProxyInstance(WebViewInject.class.getClassLoader(),
                        wvpOnly/*WebViewProvider.class*/,
                        new WebViewProviderInvocationHandler(obj, wv));
                obj = WVPProxy;
                if (debug) {
                    LogUtil.w("ANIME", "WebViewFactoryProviderInvocationHandler().invoke()..createWebView() successful!");
                }
            }
            return obj;
        }
    }

    private static class WebViewProviderInvocationHandler implements InvocationHandler {
        private Object mInstance;
        private WebView mWv;
        private boolean bWebViewClientSet;
        private boolean bWebChromeClientSet;

        public WebViewProviderInvocationHandler(Object instance, WebView wv) {
            mInstance = instance;
            mWv = wv;
            bWebViewClientSet = false;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //LogUtil.v("ANIME", "WebViewProviderInvocationHandler." + method.getName() + "(), " + Arrays.toString(args));
            if ("loadUrl".equals(method.getName())) {
                if (args.length > 0) {
                    String url = (String) args[0];
                    if (AnimeInjection.getLifecycleOption().enabledParse()) {
                        AnimeInjection.getLifecycleMonitor().onWebViewStart(mWv.hashCode(), url);
                    }

                    if (!bWebViewClientSet) {
                        mWv.setWebViewClient(new WebViewClientWrapper(null));
                        bWebViewClientSet = true;
                    }
                    if (!bWebChromeClientSet) {
                        mWv.setWebChromeClient(new WebChromeClientWrapper(null));
                        bWebChromeClientSet = true;
                    }
                }
                if (debug) {
                    LogUtil.w("ANIME", "WebViewProviderInvocationHandler().invoke()..loadUrl() successful!");
                }
            } else if ("setWebViewClient".equals(method.getName())) {
                WebViewClient wvc = null;
                if (args.length > 0) {
                    wvc = (WebViewClient) args[0];
                }
                if (wvc != null) { // WebViewClient would be set null when destroy WebView
                    WebViewClientWrapper wvcw = new WebViewClientWrapper(wvc);
                    args[0] = wvcw;
                    bWebViewClientSet = true;
                }
                if (debug) {
                    LogUtil.w("ANIME", "WebViewProviderInvocationHandler().invoke()..setWebViewClient() successful!");
                }
            } else if ("setWebChromeClient".equals(method.getName())) {
                WebChromeClient wcc = null;
                if (args.length > 0) {
                    wcc = (WebChromeClient) args[0];
                }
                if (wcc != null) { // WebChromeClient would be set null when destroy WebView
                    WebChromeClientWrapper wccw = new WebChromeClientWrapper(wcc);
                    args[0] = wccw;
                    bWebChromeClientSet = true;
                }
                if (debug) {
                    LogUtil.w("ANIME", "WebViewProviderInvocationHandler().invoke()..setWebChromeClient() successful!");
                }
            } else if ("destroy".equals(method.getName())) {
                if (AnimeInjection.getLifecycleOption().enabledParse()) {
                    AnimeInjection.getLifecycleMonitor().onWebViewFinish(mWv.hashCode());
                }
                bWebViewClientSet = false;
                bWebChromeClientSet = false;
                if (debug) {
                    LogUtil.w("ANIME", "WebViewProviderInvocationHandler().invoke()..destroy() successful!");
                }
            } else if ("init".equals(method.getName())) {
                if (debug) {
                    LogUtil.w("ANIME", "WebViewProviderInvocationHandler().invoke()..init() successful!");
                }
            }
            return method.invoke(mInstance, args);
        }
    }

}