package io.qivaz.anime.monitor.lifecycle.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.net.InetAddress;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class DefaultLifecycleMonitorImpl implements LifecycleMonitor {

    /**
     * Login with {UserId/Device Info./Network Info./App Info.}
     *
     * @param common
     */
    @Override
    public void onInit(String common) {

    }

    /**
     * Application lifecycle
     *
     * @param app
     */
    @Override
    public void onAppStart(Application app) {

    }

    @Override
    public void onAppCrash(Throwable ex) {

    }

    /**
     * Activity lifecycle
     *
     * @param activity
     */
    @Override
    public void onActivityStart(Activity activity) {

    }

    @Override
    public void onActivityOnCreate(Activity activity) {

    }

    @Override
    public void onActivityOnCreated(Activity activity) {

    }

    @Override
    public void onActivityOnNewIntent(Activity activity, Intent intent) {

    }

    @Override
    public void onActivityOnStart(Activity activity) {

    }

    @Override
    public void onActivityOnRestart(Activity activity) {

    }

    @Override
    public void onActivityOnResume(Activity activity) {

    }

    @Override
    public void onActivityOnResumed(Activity activity) {

    }

    @Override
    public void onActivityOnPause(Activity activity) {

    }

    @Override
    public void onActivityOnStop(Activity activity) {

    }

    @Override
    public void onActivityOnDestroy(Activity activity) {

    }

    @Override
    public void onActivityOnUserLeaving(Activity activity) {

    }

    @Override
    public void onActivityShow(Activity activity) {

    }

    @Override
    public void onActivityTouchEvent(Activity activity) {

    }

    @Override
    public void onActivityKey(Activity activity) {

    }

    @Override
    public void onActivityStuck(Activity activity, long duration, long skipped, Object extra) {

    }

    @Override
    public void onActivityAnr() {

    }

    @Override
    public void onActivityOnCreateCatch(Object stack) {

    }

    @Override
    public void onActivityOnResumeCatch(Object stack) {

    }

    /**
     * Fragment lifecycle
     *
     * @param fragment
     */
    @Override
    public void onFragmentStart(Fragment fragment) {

    }

    @Override
    public void onFragmentShow(Fragment fragment) {

    }

    @Override
    public void onFragmentTouchEvent(Fragment fragment) {

    }

    @Override
    public void onFragmentKey(Fragment fragment) {

    }

    /**
     * View lifecycle
     *
     * @param view
     */
    @Override
    public void onViewClick(Object view) {

    }

    @Override
    public void onViewClickFinish(Object view) {

    }

    @Override
    public void onViewLongClick(Object view) {

    }

    @Override
    public void onViewLongClickFinish(Object view) {

    }

    @Override
    public void onViewFling(Object view) {

    }

    @Override
    public void onViewFlingFinish(Object view) {

    }

    /**
     * When WebView start load url
     *
     * @param webview
     * @param url
     */
    @Override
    public void onWebViewStart(int webview, String url) {

    }

    @Override
    public void onWebViewLoad(int webview, String url) {

    }

    @Override
    public void onWebViewError(int webview, int code, String desc, String url) {

    }

    @Override
    public void onWebViewShow(int webview, String url) {

    }

    @Override
    public void onWebViewFinish(int webview) {

    }

    /**
     * Async Task lifecycle
     *
     * @param asyncTask
     * @param traceId
     * @param param
     */
    @Override
    public void onAsyncStart(String asyncTask, String traceId, Object param) {

    }

    @Override
    public void onAsyncFinish(String asyncTask, String traceId, Object result) {

    }

    /**
     * Network lifecycle
     */
    @Override
    public void onSocketRequest(InetAddress addr, int localPort, int remotePort, String buf) {

    }

    @Override
    public void onSocketResponse(InetAddress addr, int localPort, int remotePort, String buf) {

    }

    /**
     * Login lifecycle
     */
    @Override
    public void onLoginStart() {

    }

    @Override
    public void onLoginFinish(Object res, Object extra) {

    }

    /**
     * When exit
     *
     * @param immediate
     */
    @Override
    public void onExit(boolean immediate) {

    }
}