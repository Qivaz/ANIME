package io.qivaz.anime.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/5/5.
 */
public class WebChromeClientWrapper extends WebChromeClient {
    private static final boolean debug = false;
    WebChromeClient mDelegate;

    public WebChromeClientWrapper(WebChromeClient delegate) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.WebChromeClientWrapper(), delegate=" + delegate);
        }
        if (delegate == null) {
            delegate = new WebChromeClient();
        }

        mDelegate = delegate;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onProgressChanged(), newProgress=" + newProgress);
        }
        mDelegate.onProgressChanged(view, newProgress);
        if (newProgress == 100) {
            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                AnimeInjection.getLifecycleMonitor().onWebViewShow(view.hashCode(), view.getUrl());
            }
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onReceivedTitle(), title=" + title);
        }
        mDelegate.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onReceivedIcon()");
        }
        mDelegate.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url,
                                       boolean precomposed) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onReceivedTouchIconUrl()");
        }
        mDelegate.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onShowCustomView()");
        }
        mDelegate.onShowCustomView(view, callback);
    }

    @Override
    @Deprecated
    public void onShowCustomView(View view, int requestedOrientation,
                                 CustomViewCallback callback) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onShowCustomView()");
        }
        mDelegate.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onHideCustomView() {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onHideCustomView()");
        }
        mDelegate.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onCreateWindow()");
        }
        return mDelegate.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onRequestFocus()");
        }
        mDelegate.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onCloseWindow()");
        }
        mDelegate.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             JsResult result) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onJsAlert()");
        }
        return mDelegate.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               JsResult result) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onJsConfirm()");
        }
        return mDelegate.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, JsPromptResult result) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onJsPrompt()");
        }
        return mDelegate.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message,
                                    JsResult result) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onJsBeforeUnload()");
        }
        return mDelegate.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    @Deprecated
    public void onExceededDatabaseQuota(String url, String databaseIdentifier,
                                        long quota, long estimatedDatabaseSize, long totalQuota,
                                        WebStorage.QuotaUpdater quotaUpdater) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onExceededDatabaseQuota()");
        }
        mDelegate.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
    }

    @Override
    @Deprecated
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
                                         WebStorage.QuotaUpdater quotaUpdater) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onReachedMaxAppCacheSize()");
        }
        mDelegate.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onGeolocationPermissionsShowPrompt()");
        }
        mDelegate.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onGeolocationPermissionsHidePrompt()");
        }
        mDelegate.onGeolocationPermissionsHidePrompt();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequest(PermissionRequest request) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onPermissionRequest()");
        }
        mDelegate.onPermissionRequest(request);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onPermissionRequestCanceled()");
        }
        mDelegate.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onJsTimeout() {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onJsTimeout()");
        }
        return mDelegate.onJsTimeout();
    }

    @Override
    @Deprecated
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onConsoleMessage(1), message=" + message + ", lineNumber=" + lineNumber + ", sourceID=" + sourceID);
        }
        mDelegate.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onConsoleMessage(2), message=" + consoleMessage.message() + ", lineNumber=" + consoleMessage.lineNumber() + ", sourceID=" + consoleMessage.sourceId());
        }
        return mDelegate.onConsoleMessage(consoleMessage);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.getDefaultVideoPoster()");
        }
        return mDelegate.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.getVideoLoadingProgressView()");
        }
        return mDelegate.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.getVisitedHistory()");
        }
        mDelegate.getVisitedHistory(callback);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        if (debug) {
            LogUtil.w("ANIME", "WebChromeClientWrapper.onShowFileChooser()");
        }
        return mDelegate.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

//    @Override
//    @Deprecated
//    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
//        mDelegate.openFileChooser(uploadFile, acceptType, capture);
//    }

//    public void setupAutoFill(Message msg) {
//        mDelegate.setupAutoFill(msg);
//    }
}
