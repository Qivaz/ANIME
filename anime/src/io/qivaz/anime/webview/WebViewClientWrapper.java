package io.qivaz.anime.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

//import android.webkit.WebResourceError;

/**
 * @author Qinghua Zhang @create 2017/5/5.
 */
public class WebViewClientWrapper extends WebViewClient {
    private static final boolean debug = false;
    private WebViewClient mDelegate;

    public WebViewClientWrapper(WebViewClient delegate) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.WebViewClientWrapper(), delegate=" + delegate);
        }
        if (delegate == null) {
            delegate = new WebViewClient();
        }

        mDelegate = delegate;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.shouldOverrideUrlLoading()");
        }
        return mDelegate.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onPageStarted()");
        }
        mDelegate.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onPageFinished()");
        }
        mDelegate.onPageFinished(view, url);

        //onPageFinished() is unreliable, so we rely on WebChromeClient.onProgressChanged()
        //AnimeInjection.getLifecycleMonitor().onWebViewShow(view.hashCode(), url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onLoadResource(), url=" + url);
        }
        mDelegate.onLoadResource(view, url);
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            AnimeInjection.getLifecycleMonitor().onWebViewLoad(view.hashCode(), url);
        }
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onPageCommitVisible(WebView view, String url) {
//        LogUtil.v("ANIME", "WebViewClientWrapper.onPageCommitVisible()");
//        mDelegate.onPageCommitVisible(view, url);
//    }

    @Override
    @Deprecated
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      String url) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.shouldInterceptRequest()");
        }
        return mDelegate.shouldInterceptRequest(view, url);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      WebResourceRequest request) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.shouldInterceptRequest()");
        }
        return mDelegate.shouldInterceptRequest(view, request);
    }

    @Override
    @Deprecated
    public void onTooManyRedirects(WebView view, Message cancelMsg,
                                   Message continueMsg) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onTooManyRedirects()");
        }
        mDelegate.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    @Deprecated
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onReceivedError(1), code=" + errorCode + ", desc=" + description + ", url=" + failingUrl);
        }
        mDelegate.onReceivedError(view, errorCode, description, failingUrl);
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            AnimeInjection.getLifecycleMonitor().onWebViewError(view.hashCode(), errorCode, description, failingUrl);
        }
    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    public void onReceivedError(WebView view, WebResourceRequest request,
//                                WebResourceError error) {
//        LogUtil.v("ANIME", "WebViewClientWrapper.onReceivedError(2), code=" + error.getErrorCode() + ", desc=" + error.getDescription() + ", url=" + request.getUrl());
//        mDelegate.onReceivedError(view, request, error);
//        AnimeInjection.getLifecycleMonitor().onWebViewError(view.hashCode(), error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
//    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    public void onReceivedHttpError(
//            WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//        LogUtil.v("ANIME", "WebViewClientWrapper.onReceivedHttpError(), code=" + errorResponse.getStatusCode() + ", reason=" + errorResponse.getReasonPhrase());
//        mDelegate.onReceivedHttpError(view, request, errorResponse);
//        AnimeInjection.getLifecycleMonitor().onWebViewError(view.hashCode(), errorResponse.getStatusCode(), errorResponse.getReasonPhrase(), request.getUrl().toString());
//    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend,
                                   Message resend) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onFormResubmission()");
        }
        mDelegate.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url,
                                       boolean isReload) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.doUpdateVisitedHistory()");
        }
        mDelegate.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onReceivedSslError()");
        }
        mDelegate.onReceivedSslError(view, handler, error);
        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            AnimeInjection.getLifecycleMonitor().onWebViewError(view.hashCode(), error.getPrimaryError(), error.toString(), error.getUrl());
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onReceivedClientCertRequest()");
        }
        mDelegate.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view,
                                          HttpAuthHandler handler,
                                          String host, String realm) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onReceivedHttpAuthRequest()");
        }
        mDelegate.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.shouldOverrideKeyEvent()");
        }
        return mDelegate.shouldOverrideKeyEvent(view, event);
    }

    @Override
    @Deprecated
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onUnhandledKeyEvent()");
        }
        mDelegate.onUnhandledKeyEvent(view, event);
    }

//    @Override
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public void onUnhandledInputEvent(WebView view, InputEvent event) {
//        LogUtil.v("ANIME", "WebViewClientWrapper.onUnhandledInputEvent()");
//        mDelegate.onUnhandledInputEvent(view, event);
//    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onScaleChanged()");
        }
        mDelegate.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm,
                                       String account, String args) {
        if (debug) {
            LogUtil.w("ANIME", "WebViewClientWrapper.onReceivedLoginRequest()");
        }
        mDelegate.onReceivedLoginRequest(view, realm, account, args);
    }
}
