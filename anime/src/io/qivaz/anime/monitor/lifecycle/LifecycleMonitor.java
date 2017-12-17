package io.qivaz.anime.monitor.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.net.InetAddress;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public interface LifecycleMonitor {

    /**
     * Login with {UserId/Device Info./Network Info./App Info.}
     *
     * @param common
     */
    void onInit(String common);

    /**
     * Notify change of {UserId/Device Info./Network Info./App Info.}
     *
     * @param common
     */
    //void onChange(String common);

    /**
     * Application lifecycle
     */
    void onAppStart(Application app);

    void onAppCrash(Throwable ex);

    /**
     * Activity lifecycle
     */
    void onActivityStart(Activity activity);

    void onActivityOnCreate(Activity activity);

    void onActivityOnCreated(Activity activity); // Added created timing

    void onActivityOnNewIntent(Activity activity, Intent intent);

    void onActivityOnStart(Activity activity);

    void onActivityOnRestart(Activity activity);

    void onActivityOnResume(Activity activity);

    void onActivityOnResumed(Activity activity); // Added resumed timing

    void onActivityOnPause(Activity activity);

    void onActivityOnStop(Activity activity);

    void onActivityOnDestroy(Activity activity);

    void onActivityOnUserLeaving(Activity activity);

    void onActivityShow(Activity activity);

    void onActivityTouchEvent(Activity activity);

    void onActivityKey(Activity activity);

    void onActivityStuck(Activity activity, long duration, long skipped, Object extra);

    void onActivityAnr();

    void onActivityOnCreateCatch(Object stack);

    void onActivityOnResumeCatch(Object stack);

    /**
     * Fragment lifecycle
     */
    void onFragmentStart(Fragment fragment);

    void onFragmentShow(Fragment fragment);

    void onFragmentTouchEvent(Fragment fragment);

    void onFragmentKey(Fragment fragment);

    /**
     * View lifecycle
     */
    void onViewClick(Object view);

    void onViewClickFinish(Object view);

    void onViewLongClick(Object view);

    void onViewLongClickFinish(Object view);

    void onViewFling(Object view);

    void onViewFlingFinish(Object view);

    /**
     * WebView lifecycle
     */
    /**
     * When WebView start load url
     */
    void onWebViewStart(int webview, String url);

    void onWebViewLoad(int webview, String url);

    void onWebViewError(int webview, int code, String desc, String url);

    void onWebViewShow(int webview, String url);

    void onWebViewFinish(int webview);

    /**
     * Async Task lifecycle
     */
    void onAsyncStart(String asyncTask, String traceId, Object param);

    void onAsyncFinish(String asyncTask, String traceId, Object result);

    /**
     * Network lifecycle
     */
    void onSocketRequest(InetAddress addr, int localPort, int remotePort, String buf);

    void onSocketResponse(InetAddress addr, int localPort, int remotePort, String buf);

    /**
     * Login lifecycle
     */
    void onLoginStart();

    void onLoginFinish(Object res, Object extra);

    /**
     * When exit
     */
    void onExit(boolean immediate);
}
