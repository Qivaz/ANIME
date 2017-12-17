package io.qivaz.anime.monitor.lifecycle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import java.io.Serializable;
import java.util.Map;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/3/19.
 */
public class LifecycleMonitorType {

    /**
     * Enum Types
     */
    public enum ActionType {
        CLICK,
        LONGCLICK,
    }

    public enum HttpType {
        GET,
        POST,
        PUT,
        DELETE
    }

    /**
     * Callback Types
     */
    public interface ActionCallback {
        void onClick(Activity activity, View view, String resId, String description, String extra);
    }

    /**
     * CrashRecord class defined for "Resource" JSON string
     * NOTICE: rename "resInfo" to "Resource"
     */
    public static class ResourceRecord implements Serializable, Cloneable {
        public ResourceInfo Resource;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Resource");
            sb.append('[');
            if (Resource != null) {
                sb.append(Resource.app.pkgName);
                sb.append('/');
                sb.append(Resource.app.appVer);
                sb.append(',');
                sb.append(Resource.app.appName);
                sb.append(',');
                sb.append(Resource.animeVer);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class ResourceInfo implements Serializable, Cloneable {
            public String name;
            public String sid;
            public String rid;
            public String animeVer; // Use for ANIME version.
            public AppInfo app = new AppInfo();
            public DeviceInfo device = new DeviceInfo();
            public String lang;
            public String timeZone; //In the form of "Area/Location", such as "Asia/Shanghai"

            public static class AppInfo implements Serializable, Cloneable {
                public String appName;
                public String appVer;
                public String pkgName;
                public String type = "APP";
                public String verCode;
            }

            public static class DeviceInfo implements Serializable, Cloneable {
                public String CPU;
                public String RAM;
                public String modelNum;
                public String name;
                public String osType;
                public String osVer;
                public String serialNo;
            }
        }
    }

    /**
     * CrashRecord class defined for "Crash" JSON string
     * NOTICE: "Crash" with uppercase 'C'
     */
    public static class CrashRecord implements Serializable, Cloneable {
        public CrashInfo Crash;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Crash");
            sb.append('[');
            if (Crash != null) {
                sb.append(Crash.data.crashInfo);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class CrashInfo implements Serializable, Cloneable {
            public String name; // Exception type, Ex.) "java.lang.NullPointerException"
            public String sid;
            public String rid;
            public Data data = new Data();

            public class Data implements Serializable, Cloneable {
                public Long startTime; // When the exception happened
                public String crashInfo; // Crash Info., the same as Exception.printStackTrace()
//                public String resId; // Resource id of View, on which user operate lately
            }
        }
    }

    /**
     * AppRecord class defined for "AppRecord" JSON string
     * NOTICE: rename "AppRecord" to "App"
     */
    public static class AppRecord implements Serializable, Cloneable {
        public AppInfo App; //Replaced "AppRecord"

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("App");
            sb.append('[');
            if (App != null) {
                sb.append(App.name);
                sb.append(',');
                sb.append(App.data.waste);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class AppInfo implements Serializable, Cloneable {
            public String name; // Package name
            public String sid;
            public String rid;
            public Data data = new Data();

            public class Data implements Serializable, Cloneable {
                public Long startTime; // The beginning of Application
                public Long waste; // The waste during startup
            }
        }
    }

    /**
     * ActivityRecord class defined for both "Page" and "Stuck" JSON string
     */
    public static class ActivityRecord implements Serializable, Cloneable {
        public PageInfo Page;
        public StuckInfo Stuck;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (Page != null) {
                sb.append("Page");
                sb.append('[');
                sb.append(Page.name);
                sb.append(',');
                sb.append(Page.data.waste);
                sb.append(']');
            } else if (Stuck != null) {
                sb.append("Stuck");
                sb.append('[');
                sb.append(Stuck.name);
                sb.append(',');
                sb.append(Stuck.data.anr);
                sb.append(']');
            } else {
                sb.append("ActivityRecord Null Exception");
            }
            return sb.toString();
        }

        public static class PageInfo implements Serializable, Cloneable {
            public String name; // Activity name
            public String sid;
            public String rid;
            public Data data = new Data();
            public BundleInfo bundle;

            public class Data implements Serializable, Cloneable {
                public Long startTime; // Timing of very beginning
                public Long waste; // waste = (onVisibleTime - startTime) OR (waste1 + waste2)

                public Long onCreateTime; // Timing of onCreate()
                public Long onRestartTime; // Timing of onRestart()
                public Long onStartTime; // Timing of onStart()
                public Long onResumeTime; // Timing of onResume()
                public Long onPauseTime; // Timing of onPause()
                public Long onVisibleTime; // Timing of really shown
//          public Long onNewIntentTime;
//          public Long onStopTime;
//          public Long onDestroyTime;
//          public Long onUserLeavingTime;
            }
        }

        public static class StuckInfo implements Serializable, Cloneable {
            public String name; // Activity where stuck happened
            public String sid;
            public String rid;
            public Data data = new Data();
            public BundleInfo bundle;

            @Override
            public StuckInfo clone() {
                try {
                    StuckInfo si = (StuckInfo) super.clone();
                    si.data = data.clone();
                    return si;
                } catch (CloneNotSupportedException e) {
                    LogUtil.e("ANIME", "StuckInfo.clone(), " + e);
                    e.printStackTrace();
                    return this;
                }
            }

            public class Data implements Serializable, Cloneable {
                public boolean anr; // If ANR happened
                public Long startTime; // Start time
                public Long waste; // How Long stucked
                public Long skipped; // How many frames skipped
                //                public String resId; // Resource id of View, on which user operate lately
                public Bitmap bitmap; // Only for Debug
                public StackTraceElement[] stack; // Sampled stack trace when stucked

                @Override
                public Data clone() {
                    try {
                        return (Data) super.clone();
                    } catch (CloneNotSupportedException e) {
                        LogUtil.e("ANIME", "StuckInfo.Data.clone(), " + e);
                        e.printStackTrace();
                        return this;
                    }
                }
            }
        }
    }

    /**
     * HttpRecord class defined for "Http" JSON string
     */
    public static class HttpRecord implements Serializable, Cloneable {
        public HttpInfo Http;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Http");
            sb.append('[');
            if (Http != null) {
                sb.append(Http.name);
                sb.append(',');
                sb.append(Http.data.waste);
                sb.append(',');
                sb.append(Http.data.api);
                sb.append(',');
                sb.append(Http.data.code);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class HttpInfo implements Serializable, Cloneable {
            public String name; //Service alias name
            public String sid;
            public String rid;
            public String uid; //Current user id
            public Data data = new Data();

            public class Data implements Serializable, Cloneable {
                public HttpType type; // Get/Post/Put/Delete/etc.
                public String url; // URL
                public String api; // Request API, without service alias
                public String traceId; // Tracing certain transaction between client and servers

                public Integer code; // Response code, like HTTP code
                public String networkType;
                public String operator; // Operator of carriers(Maybe two), or WiFi ssid if networkType is WiFi
                public String plmn; // PLMN of current registered carriers(Maybe two), not available for WiFi-Only

                public RemoteServer remoteServer = new RemoteServer();
                public String localIp; // Local IP
                public Long startTime; // Start time
                public Long waste; // Waste time

                public ParamInfo params = new ParamInfo(); // Request parameters (HashMap<String, Object>)
                public Object result; // Response result (HashMap<String, Object>)

                public class RemoteServer implements Serializable, Cloneable {
                    public String name;
                    public String IP;
                }

                public class ParamInfo implements Serializable, Cloneable {
                    public Map<String, String> headers;
                    public String body;
                }
            }
        }
    }

    /**
     * LoginRecord class defined for "Async" JSON string
     */
    public static class AsyncRecord implements Serializable, Cloneable {
        public AsyncInfo Async;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Async");
            sb.append('[');
            if (Async != null) {
                sb.append(Async.name);
                sb.append(',');
                sb.append(Async.data.waste);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class AsyncInfo implements Serializable, Cloneable {
            public String name; // AsyncTask class name
            public String sid;
            public String rid;
            public Data data = new Data();

            public class Data implements Serializable, Cloneable {
                public Long startTime; // When AsyncTask triggered in background
                public Long waste; // Waste of running in BG
//              public String activity;
            }
        }
    }

    /**
     * LoginRecord class defined for "Login" JSON string
     */
    public static class LoginRecord implements Serializable, Cloneable {
        public LoginInfo Login;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Login");
            sb.append('[');
            if (Login != null) {
                if (TextUtils.isEmpty(Login.name) || "no_user".equals(Login.name)) {
                    sb.append("NG");
                } else {
                    sb.append("OK");
                }
                sb.append(',');
                sb.append(Login.data.waste);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class LoginInfo implements Serializable, Cloneable {
            public String name; // The user id used for login
            public String sid;
            public String rid;
            public Data data = new Data();

            public class Data implements Serializable, Cloneable {
                public Long initTime; // Very beginning of Login, including Plugin initializing, etc.
                public Long startTime; // When start logging into server
                public Long waste; // From login timing(startTime) to server respond
                public Long totalWaste; // From initializing timing(initTime) to server respond, (totalWaste - waste) == (startTime - initTime)
                public Map result; // Login result, when Code == 200, success, or failed
                public Integer type; // 0:loginWithNamePwd(), 1:autoLogin(), 2:autoLoginForCookieExpired(), 3:Dummy
//                public StackTraceElement[] loginStack; // Not null only for debug
            }
        }
    }

    /**
     * ActionRecord class defined for "Action" JSON string
     */
    public static class ActionRecord implements Serializable, Cloneable {
        public ActionInfo Action;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Action");
            sb.append('[');
            if (Action != null) {
                sb.append(Action.data.resId);
                sb.append(',');
                sb.append(Action.data.waste);
                sb.append(',');
                sb.append(Action.data.desc);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class ActionInfo implements Serializable, Cloneable {
            public String name; // View class name
            public String sid;
            public String rid;
            public Data data = new Data();
            public BundleInfo bundle;

            public class Data implements Serializable, Cloneable {
                public String activity; // Activity in which the view shown
                //public String view; // View class name, deprecated
                public ActionType action; //CLICK or Long_CLICK
                public Long startTime; // When it happened
                public Long waste; //  Waste time of onClick()
                public String resId; // Resource id
                public String desc; // Content description
                public String extra; // Hierarchical Views
            }
        }
    }

    /**
     * WebviewRecord class defined for "Webview" JSON string
     */
    public static class WebviewRecord implements Serializable, Cloneable {
        public WebviewInfo Webview;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Webview");
            sb.append('[');
            if (Webview != null) {
                sb.append(Webview.name);
                sb.append(',');
                sb.append(Webview.data.waste);
                sb.append(',');
                sb.append(Webview.data.url);
            }
            sb.append(']');
            return sb.toString();
        }

        public static class WebviewInfo implements Serializable, Cloneable {
            public String name; // Activity where WebView attached
            public String sid;
            public String rid;
            public Data data = new Data();
            public BundleInfo bundle;

            public static class WebviewInfoError {
                public String code;
                public String desc;
                public String url;
            }

            public class Data implements Serializable, Cloneable {
                //public String activity; // Activity in which the WebView shown
                public String url; // The first URL when WebView.loadUrl()
                public Long startTime; // When WebView loadUrl()
                public Long waste; // Finished when onPageFinish()

                public String[] urls; // The URLs when WebView.loadUrl()
                public String[] loads; // The info. from WebViewClient.onLoadResource()
                public WebviewInfoError[] errors; // The errors from WebViewClient.onReceivedError()/onReceivedHttpError()/onReceivedSslError()/...
            }
        }
    }

    /**
     * BundleInfo class for ActivityRecord/StuckRecord/ActionRecord/WebviewRecord,
     * defined for "Bundle" JSON string
     */
    public static class BundleInfo implements Serializable, Cloneable {
        public String pkgName;
        public String verCode;
        public String verName;
    }
}