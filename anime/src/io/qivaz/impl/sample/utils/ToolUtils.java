package io.qivaz.impl.sample.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import io.qivaz.anime.systemproperty.SystemPropertiesUtils;


/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class ToolUtils {

    private ToolUtils() {
    }

    public static String getCurrentNetType(Context context) {
        String type = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "WiFi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2G";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3G";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE��3g��4g�Ĺ��ɣ���3.9G��ȫ���׼
                type = "4G";
            }
        }
        return type;
    }

    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    public static String getCpuName() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static String getNetworkPlmn(Context context) {
        return SystemPropertiesUtils.getOperatorPlmn();
    }

    public static String getNetworkOperatorName(Context context) {
        return SystemPropertiesUtils.getOperatorName();
    }

    public static String getWiFiSsid(Context context) {
        String ssid = "";
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return "PERMISSION_NOT_GRANTED";
            }
        } catch (java.lang.NoSuchMethodError e) {
            e.printStackTrace();
        }

        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssid;
    }

    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getTotalMemory() {
        String path = "/proc/meminfo";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);
            String line = "";
            while ((line = br.readLine()) != null) {
                line = line.replace("MemTotal:", "").trim();
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    }

    public static String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    public static String getSerialNum() {

        String serialNum = Build.SERIAL;

        return serialNum;

    }

    public static String getAppName(Context context) {
        PackageInfo pkg = null;
        try {
            pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String appName = pkg.applicationInfo.loadLabel(context.getPackageManager()).toString();
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
}
