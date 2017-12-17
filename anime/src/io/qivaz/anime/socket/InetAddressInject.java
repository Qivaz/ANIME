package io.qivaz.anime.socket;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class InetAddressInject {
    private static boolean mInetAddressSet;
    private static Field mInetAddressCacheField;
    private static Method mAddressCacheGetMethod;

    public static void run() {
        inject();
    }

    private static void inject() {
        if (!mInetAddressSet) {
            try {
                try {
                    final Class<?> inetAddressClass = Class.forName("java.net.Inet6AddressImpl");
                    mInetAddressCacheField = inetAddressClass.getDeclaredField("addressCache");
                    mInetAddressCacheField.setAccessible(true);
                } catch (ClassNotFoundException e) {
                    final Class<?> inetAddressClass = Class.forName("java.net.InetAddress");
                    mInetAddressCacheField = inetAddressClass.getDeclaredField("addressCache");
                    mInetAddressCacheField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    final Class<?> inetAddressClass = Class.forName("java.net.InetAddress");
                    mInetAddressCacheField = inetAddressClass.getDeclaredField("addressCache");
                    mInetAddressCacheField.setAccessible(true);
                }

                final Class<?> addressCacheClass = Class.forName("java.net.AddressCache");
                mAddressCacheGetMethod = addressCacheClass.getDeclaredMethod("get", new Class[]{String.class, Integer.TYPE});
                mAddressCacheGetMethod.setAccessible(true);

                mInetAddressSet = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hadDnsCached(String host) {
        inject();
        try {
            Object ac = mInetAddressCacheField.get(null);
            Object cachedResult = mAddressCacheGetMethod.invoke(ac, host, 0);//NETID_UNSET(0)
            if (cachedResult != null) {
                if (cachedResult instanceof InetAddress[]) {
                    LogUtil.e("ANIME", "InetAddressInject.hadDnsCached(" + host + "), cachedResult=" + Arrays.toString((InetAddress[]) cachedResult));
                    if (((InetAddress[]) cachedResult).length > 0) {
                        return true;
                    }
                } else {
                    LogUtil.e("ANIME", "InetAddressInject.hadDnsCached(" + host + "), cachedResult=" + Arrays.toString((Object[]) cachedResult));
                }
            } else { //Not requested DNS or requested before AddressCache.TTL_NANOS
                //AddressCache.TTL_NANOS
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        LogUtil.e("ANIME", "InetAddressInject.hadDnsCached(" + host + "), cachedResult=null");
        return false;
    }
}