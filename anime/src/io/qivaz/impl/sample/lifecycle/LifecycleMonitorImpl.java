package io.qivaz.impl.sample.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabWidget;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.qivaz.anime.executor.SerialExecutor;
import io.qivaz.anime.monitor.AnimeInjection;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitor;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.ActionRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.ActivityRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.AppRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.AsyncRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.CrashRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.HttpRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.LoginRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.ResourceRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.WebviewRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleParser;
import io.qivaz.impl.sample.utils.ToolUtils;


/**
 * @author Qinghua Zhang @create 2017/3/24.
 *         You must handle collected data here with SerialExecutor.run();
 */
public class LifecycleMonitorImpl implements LifecycleMonitor, LifecycleParser {
    private Application app;
    private Handler handler;
    private LifecycleParser mLcp;

    private String sid = UUID.randomUUID().toString();
    private String rid = UUID.randomUUID().toString();

    private long appStartTime;
    private long activityStartTime;
    private boolean bAppStarted;

    private ResourceRecord.ResourceInfo mResourceInfo;
    private CrashRecord.CrashInfo mCrashInfo;
    private AppRecord.AppInfo mAppInfo;
    private ActivityRecord.PageInfo mPageInfo;
    private ActivityRecord.StuckInfo mStuckInfo;
    private ActivityRecord.StuckInfo mANRInfo;
    private HttpRecord.HttpInfo mHttpInfo;
    private AsyncRecord.AsyncInfo mAsyncInfo;
    private LoginRecord.LoginInfo mLoginInfo; //TODO
    private ActionRecord.ActionInfo mActionInfo;
    private WebviewRecord.WebviewInfo mWebviewInfo;

    private Map<String, ActionRecord.ActionInfo> mActionInfoMap = new HashMap<>();
    private Map<String, WebviewRecord.WebviewInfo> mWebviewInfoMap = new HashMap<>();
    private Map<String, AsyncRecord.AsyncInfo> mAsyncInfoMap = new HashMap<>();
    private Map<String, HttpRecord.HttpInfo> mHttpInfoMap = new HashMap<>();
    private WeakReference<Activity> mWeakRefCurActivity = new WeakReference<>(null);

    public LifecycleMonitorImpl(Application applicaton) {
        app = applicaton;
        handler = new Handler();
        mLcp = new LifecycleParserImpl(app);

        if (AnimeInjection.getLifecycleOption().enabledParse()) {
            onInit("");
        }
    }

    /**
     * Parse the collected monitor data.
     *
     * @param object collected data
     */
    @Override
    public void onParse(Object object) {
        if (mLcp != null) {
            mLcp.onParse(object);
        }
    }

    /**
     * Fire, with parsed data.
     * <p/>
     * If you just fire each time after parsed,
     * no need implement&invoke this method.
     *
     * @param immediate
     */
    @Override
    public void onFire(boolean immediate) {
        if (mLcp != null) {
            mLcp.onFire(immediate);
        }
    }

    /**
     * Login with {UserId/Device Info./Network Info./App Info.}
     *
     * @param common
     */
    @Override
    public void onInit(String common) {
        String versionName = ToolUtils.getVersionName(app.getApplicationContext());
        String versionCode = String.valueOf(ToolUtils.getVersionCode(app.getApplicationContext()));
        String systemVersion = ToolUtils.getSystemVersion();
        String phoneBrand = ToolUtils.getPhoneBrand();
        String phoneModel = ToolUtils.getPhoneModel();
        String cpuName = ToolUtils.getCpuName();
        String memerySize = ToolUtils.getTotalMemory();
        String lang = ToolUtils.getSystemLanguage();
        String timeZone = ToolUtils.getTimeZone();
        String serialNum = ToolUtils.getSerialNum();
        String appName = ToolUtils.getAppName(app.getApplicationContext());

        mResourceInfo = new LifecycleMonitorType.ResourceRecord.ResourceInfo();
        mResourceInfo.app.appName = appName;
        mResourceInfo.app.appVer = versionName;
        mResourceInfo.app.pkgName = app.getPackageName();
        mResourceInfo.app.type = "APP";
        mResourceInfo.app.verCode = versionCode;

        mResourceInfo.device.CPU = cpuName;
        mResourceInfo.device.RAM = memerySize;
        mResourceInfo.device.modelNum = phoneModel;
        mResourceInfo.device.name = phoneBrand;
        mResourceInfo.device.osType = "Android";
        mResourceInfo.device.osVer = systemVersion;
        mResourceInfo.device.serialNo = serialNum;

        mResourceInfo.lang = lang;
        mResourceInfo.timeZone = timeZone;
        mResourceInfo.sid = sid;
        mResourceInfo.rid = rid;
        mResourceInfo.animeVer = "1.0.0";

        if (mResourceInfo != null) {
            ResourceRecord cr = new ResourceRecord();
            cr.Resource = mResourceInfo;
            onParse(cr);

            mResourceInfo = null;
        }
    }

    /**
     * Application lifecycle
     *
     * @param app
     */
    @Override
    public void onAppStart(Application app) {
        appStartTime = System.currentTimeMillis();
    }

    @Override
    public void onAppCrash(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String crashInfo = writer.toString();

        mCrashInfo = new CrashRecord.CrashInfo();
        mCrashInfo.name = ex.getClass().getName();
        mCrashInfo.rid = rid;
        mCrashInfo.sid = sid;
        mCrashInfo.data.startTime = System.currentTimeMillis();
        mCrashInfo.data.crashInfo = crashInfo;

        if (mCrashInfo != null) {
            CrashRecord cr = new CrashRecord();
            cr.Crash = mCrashInfo;
            onParse(cr);

            mCrashInfo = null;
        }
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
        activityStartTime = System.currentTimeMillis();
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
    public void onActivityOnResumed(final Activity activity) {
        final long showTime = System.currentTimeMillis();
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                if (!bAppStarted) {
                    bAppStarted = true;

                    mAppInfo = new AppRecord.AppInfo();
                    mAppInfo.name = app.getPackageName();
                    mAppInfo.sid = sid;
                    mAppInfo.rid = rid;
                    mAppInfo.data.startTime = appStartTime;
                    long showWaste;
                    if (showTime > appStartTime) {
                        showWaste = showTime - appStartTime;
                    } else {
                        showWaste = 0l;
                    }
                    mAppInfo.data.waste = showWaste;

                    if (app != null) {
                        AppRecord ar = new AppRecord();
                        ar.App = mAppInfo;
                        onParse(ar);
                        mAppInfo = null;
                    }
                } else {
                    Activity realActivity = activity;
                    String name = realActivity.getClass().getName();

                    ActivityRecord.PageInfo pi = new ActivityRecord.PageInfo();
                    pi.name = name;
                    pi.sid = sid;
                    pi.rid = rid;

                    pi.data.startTime = activityStartTime;
                    long showWaste;
                    if (showTime > activityStartTime) {
                        showWaste = showTime - activityStartTime;
                    } else {
                        showWaste = 0l;
                    }
                    pi.data.waste = showWaste;

                    mPageInfo = pi;
                    if (mPageInfo != null) {
                        ActivityRecord ar = new ActivityRecord();
                        ar.Page = mPageInfo;
                        onParse(ar);
                        mPageInfo = null;
                    }
                }
            }

        });
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
    public void onActivityStuck(final Activity activity, final long duration, final long skipped, final Object extra) {
        final long startTime = System.currentTimeMillis() - (duration / 1000000);
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                Activity realActivity = activity;
                String name = realActivity.getClass().getName();

                ActivityRecord.StuckInfo si = new ActivityRecord.StuckInfo();
                si.name = name;
                si.sid = sid;
                si.rid = rid;

                si.data.startTime = startTime;
                si.data.skipped = skipped;
                si.data.waste = (duration / 1000000);
                si.data.stack = ((StackTraceElement[][]) extra)[0];

                mStuckInfo = si;
                mANRInfo = si.clone();

                if (mStuckInfo != null) {
                    ActivityRecord ar = new ActivityRecord();
                    ar.Stuck = mStuckInfo;
                    onParse(ar);
                    mStuckInfo = null;
                }
            }
        });
    }

    @Override
    public void onActivityAnr() {
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                ActivityRecord.StuckInfo si = mANRInfo;
                si.data.anr = true;

                if (mANRInfo != null) {
                    ActivityRecord ar = new ActivityRecord();
                    ar.Stuck = mANRInfo;
                    onParse(ar);
                    mANRInfo = null;
                }
            }
        });
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
    public String getResourceId(View v) {
        String reSid = "NO_ID";
        try {
            reSid = v.getResources().getResourceEntryName(v.getId());
        } catch (Resources.NotFoundException e) {
        }
        return reSid;
    }

    public boolean isClickableTextView(View v) {
        boolean bClickableTextView = v.isClickable() && v instanceof TextView;
        return bClickableTextView;
    }

    public String getResourceIdWithType(View v) {
        String reSid = "NO_ID" + "(" + v.getClass().getSimpleName() + ")";
        try {
            reSid = v.getResources().getResourceEntryName(v.getId()) + "(" + v.getClass().getSimpleName() + ")";
        } catch (Resources.NotFoundException e) {
        }
        return reSid;
    }

    @Override
    public void onViewClick(final Object view) {
        final long time = System.currentTimeMillis();
        final WeakReference weakRefView = new WeakReference<>(view);
        final String viewHashCode = String.valueOf(view.hashCode());
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                View v = (View) weakRefView.get();
                if (v == null) { //View was released!
                    mActionInfoMap.remove(viewHashCode);
                    return;
                }
                String name = v.getClass().getName();

                ActionRecord.ActionInfo ai = mActionInfoMap.get(String.valueOf(v.hashCode()));
                if (ai == null) {
                    ai = new ActionRecord.ActionInfo();
                    mActionInfoMap.put(String.valueOf(v.hashCode()), ai);
                }

                ai.name = name;
                ai.sid = sid;
                ai.rid = rid;
                ai.data.startTime = time;
                ai.data.waste = -1l;
                Activity activity = mWeakRefCurActivity.get();
                Activity realActivity = activity;
                String activityName = null;
                if (realActivity != null) {
                    activityName = realActivity.getClass().getName();
                } else {
                    activityName = "RELEASED_ACTIVITY";
                }
                ai.data.activity = activityName;
                //ai.data.view = name;
                ai.data.action = LifecycleMonitorType.ActionType.CLICK;

                if (isClickableTextView(v)) {
                    ai.data.resId = getResourceId(v);
                    if (v.getContentDescription() != null) {
                        ai.data.desc = v.getContentDescription().toString();
                    } else {
                        CharSequence charSequence = ((TextView) v).getText();
                        if (charSequence != null) {
                            ai.data.desc = charSequence.toString();
                        }
                    }
                } else {
                    ai.data.resId = getResourceId(v);
                    if (v.getContentDescription() != null) {
                        ai.data.desc = v.getContentDescription().toString();
                    }
                }

                // Add more info. of layout tree
                View child = v;
                StringBuilder sbLayout = new StringBuilder();
                sbLayout.append(getResourceIdWithType(v));
                while (child != null && child.getParent() != null && child.getParent() instanceof ViewGroup) {
                    sbLayout.append("<-");
                    sbLayout.append(getResourceIdWithType((View) child.getParent()));

                    if (child.getParent() instanceof TabWidget) {
                        ai.name = ((View) child.getParent()).getClass().getName();
                        ai.data.resId = getResourceId((View) child.getParent());
                    }

                    child = (View) child.getParent();
                }
                ai.data.extra = sbLayout.toString();
            }
        });
    }

    @Override
    public void onViewClickFinish(final Object view) {
        final long time = System.currentTimeMillis();
        final WeakReference weakRefView = new WeakReference<>(view);
        final String viewHashCode = String.valueOf(view.hashCode());
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                View v = (View) weakRefView.get();
                if (v == null) { //View was released
                    mActionInfoMap.remove(viewHashCode);
                    return;
                }
                ActionRecord.ActionInfo ai = mActionInfoMap.get(viewHashCode);

                if (time > ai.data.startTime) {
                    ai.data.waste = time - ai.data.startTime;
                } else {
                    ai.data.waste = 0l;
                }

                mActionInfo = ai;
                if (mActionInfo != null) {
                    ActionRecord ar = new ActionRecord();
                    ar.Action = mActionInfo;
                    onParse(ar);
                    mActionInfoMap.remove(String.valueOf(view.hashCode()));
                    mActionInfo = null;
                }
            }
        });
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
    public void onWebViewStart(final int webview, final String url) {
        final long time = System.currentTimeMillis();
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                WebviewRecord.WebviewInfo wi = mWebviewInfoMap.get(String.valueOf(webview));
                if (wi == null) {
                    wi = new WebviewRecord.WebviewInfo();
                    wi.sid = sid;
                    wi.rid = rid;
                    wi.data.url = url;
                    wi.data.startTime = time; // Start time
                    wi.data.waste = -1l; // End time

                    mWebviewInfoMap.put(String.valueOf(webview), wi);
                } else {
                    List<String> list = null;
                    if (wi.data.urls != null) {
                        list = new ArrayList<>(Arrays.asList(wi.data.urls));
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(url);
                    wi.data.urls = list.toArray(new String[0]);
                }
            }
        });
    }

    @Override
    public void onWebViewLoad(final int webview, final String url) {
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                WebviewRecord.WebviewInfo wi = mWebviewInfoMap.get(String.valueOf(webview));
                if (wi == null) {
                    return;
                }

                List<String> list = null;
                if (wi.data.loads != null) {
                    list = new ArrayList<>(Arrays.asList(wi.data.loads));
                } else {
                    list = new ArrayList<>();
                }
                list.add(url);
                wi.data.loads = list.toArray(new String[0]);
            }
        });
    }

    @Override
    public void onWebViewError(final int webview, final int code, final String desc, final String url) {
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                WebviewRecord.WebviewInfo wi = mWebviewInfoMap.get(String.valueOf(webview));
                if (wi == null) {
                    return;
                }

                WebviewRecord.WebviewInfo.WebviewInfoError error = new WebviewRecord.WebviewInfo.WebviewInfoError();
                error.code = String.valueOf(code);
                error.desc = desc;
                error.url = url;

                List<WebviewRecord.WebviewInfo.WebviewInfoError> list = null;
                if (wi.data.errors != null) {
                    list = new ArrayList<>(Arrays.asList(wi.data.errors));
                } else {
                    list = new ArrayList<>();
                }
                list.add(error);
                wi.data.errors = list.toArray(new WebviewRecord.WebviewInfo.WebviewInfoError[0]);
            }

        });
    }

    @Override
    public void onWebViewShow(final int webview, final String url) {
        final long time = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SerialExecutor.run(new Runnable() {
                    @Override
                    public void run() {
                        WebviewRecord.WebviewInfo wi = mWebviewInfoMap.get(String.valueOf(webview));
                        if (wi == null) {
                            return;
                        }

                        if (time > wi.data.startTime) {
                            wi.data.waste = time - wi.data.startTime;
                        } else {
                            wi.data.waste = 0l;
                        }
                        Activity activity = mWeakRefCurActivity.get();
                        Activity realActivity = activity;
                        String activityName = null;
                        if (realActivity != null) {
                            activityName = realActivity.getClass().getName();
                        } else {
                            activityName = "RELEASED_ACTIVITY";
                        }
                        wi.name = activityName;

                        mWebviewInfo = wi;
                        if (mWebviewInfo != null) {
                            WebviewRecord wr = new WebviewRecord();
                            wr.Webview = mWebviewInfo;
                            onParse(wr);

                            mWebviewInfoMap.remove(String.valueOf(webview));
                            mWebviewInfo = null;
                        }
                    }
                });
            }
        }, 1500);
    }

    @Override
    public void onWebViewFinish(final int webview) {
    }


    /**
     * Async Task lifecycle
     *
     * @param asyncTask
     * @param traceId
     * @param param
     */
    @Override
    public void onAsyncStart(final String asyncTask, final String traceId, final Object param) {
        final long time = System.currentTimeMillis();
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                String name = asyncTask;

                AsyncRecord.AsyncInfo ri = new AsyncRecord.AsyncInfo();
                ri.name = name;
                ri.sid = sid;
                ri.rid = rid;
                ri.data.startTime = time;
                ri.data.waste = -1l;

                mAsyncInfoMap.put(traceId, ri);

            }
        });
    }

    @Override
    public void onAsyncFinish(final String asyncTask, final String traceId, final Object result) {
        final long time = System.currentTimeMillis();
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                String name = asyncTask;
                AsyncRecord.AsyncInfo ri = mAsyncInfoMap.get(traceId);
                if (ri == null) {
                    return;
                }

                mAsyncInfo = ri;
                if (time > ri.data.startTime) {
                    ri.data.waste = time - ri.data.startTime;
                } else {
                    ri.data.waste = 0l;
                }
                ri.name = name;
                ri.sid = sid;
                ri.rid = rid;

                if (mAsyncInfo != null) {
                    AsyncRecord ar = new AsyncRecord();
                    ar.Async = mAsyncInfo;
                    onParse(ar);

                    mAsyncInfoMap.remove(traceId);
                    mAsyncInfo = null;
                }

            }
        });
    }


    /**
     * Network lifecycle
     */
    @Override
    public void onSocketRequest(final InetAddress addr, final int localPort, final int remotePort, final String buf) {
        final long time = System.currentTimeMillis();
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                LifecycleMonitorType.HttpType type;
                String svcAndApi = null;
                if (buf.indexOf("GET") == 0) {
                    type = LifecycleMonitorType.HttpType.GET;
                    svcAndApi = buf.substring("GET".length() + 1, buf.indexOf(" HTTP/"));
                } else if (buf.indexOf("POST") == 0) {
                    type = LifecycleMonitorType.HttpType.POST;
                    svcAndApi = buf.substring("POST".length() + 1, buf.indexOf(" HTTP/"));
                } else if (buf.indexOf("PUT") == 0) {
                    type = LifecycleMonitorType.HttpType.PUT;
                    svcAndApi = buf.substring("PUT".length() + 1, buf.indexOf(" HTTP/"));
                } else if (buf.indexOf("DELETE") == 0) {
                    type = LifecycleMonitorType.HttpType.DELETE;
                    svcAndApi = buf.substring("DELETE".length() + 1, buf.indexOf(" HTTP/"));
                } else {
                    return;
                }

                String url = "http://" + addr.getHostName() + svcAndApi;
                HttpRecord.HttpInfo hi = new HttpRecord.HttpInfo();
                hi.name = svcAndApi;
                hi.data.type = type; // Type
                hi.data.url = url; // URL
                hi.data.startTime = time; // Start time
                hi.data.waste = -1l; // End time

                hi.data.api = null;
                hi.data.traceId = null;
                hi.data.params = null;
                hi.data.result = null;

                mHttpInfoMap.put(String.valueOf(localPort), hi);
            }

        });
    }

    @Override
    public void onSocketResponse(final InetAddress addr, final int localPort, final int remotePort, final String buf) {
        final long time = System.currentTimeMillis();
        SerialExecutor.run(new Runnable() {
            @Override
            public void run() {
                HttpRecord.HttpInfo hi = mHttpInfoMap.get(String.valueOf(localPort));
                if (hi == null) {
                    return;
                }
                mHttpInfo = hi;

                hi.sid = sid;
                hi.rid = rid;

                if (time > hi.data.startTime) {
                    hi.data.waste = time - hi.data.startTime;
                } else {
                    hi.data.waste = 0l;
                }

                hi.data.networkType = ToolUtils.getCurrentNetType(app);
                if (hi.data.networkType == null) {
                    hi.data.operator = null;
                } else if ("WiFi".equals(hi.data.networkType)) {
                    hi.data.operator = ToolUtils.getWiFiSsid(app);
                    hi.data.plmn = ToolUtils.getNetworkPlmn(app);
                } else {
                    hi.data.operator = ToolUtils.getNetworkOperatorName(app);
                    hi.data.plmn = ToolUtils.getNetworkPlmn(app);
                }

                hi.data.code = getCodeBySocket(buf);
                hi.data.remoteServer.IP = addr.getHostAddress();
                hi.data.remoteServer.name = addr.getHostName();
                InetAddress local = getLocalInetAddress();
                if (local != null) {
                    hi.data.localIp = local.getHostAddress();
                } else {
                    hi.data.localIp = "127.0.0.1";
                }

                if (mHttpInfo != null) {
                    HttpRecord hr = new HttpRecord();
                    hr.Http = mHttpInfo;
                    onParse(hr);
                    mHttpInfoMap.remove(String.valueOf(localPort));
                    mHttpInfo = null;
                }
            }

        });
    }

    public InetAddress getLocalInetAddress() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress addr = ips.nextElement();
                    if (!addr.isLoopbackAddress() && !addr.getHostAddress().contains("::")) {
                        return addr;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getCodeBySocket(String result) {
        String code = "0";
        String firstLine = null;
        if (!TextUtils.isEmpty(result)) {
            try {
                firstLine = result.substring(0, result.indexOf("\r\n")); // HTTP/1.1 200 OK
                if (!TextUtils.isEmpty(firstLine) && firstLine.startsWith("HTTP/")) {
                    int start = firstLine.indexOf(' ');
                    if (start != -1) {
                        int end = firstLine.indexOf(' ', start + 1);
                        if (end != -1) {
                            code = firstLine.substring(start + 1, end);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Integer.parseInt(code);
    }

    /**
     * Login lifecycle
     */
    @Override
    public void onLoginStart() {
        //TODO
    }

    @Override
    public void onLoginFinish(Object res, Object extra) {
        //TODO
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
