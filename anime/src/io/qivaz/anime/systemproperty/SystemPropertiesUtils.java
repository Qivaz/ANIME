package io.qivaz.anime.systemproperty;

import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/4/24.
 */
public class SystemPropertiesUtils {
    private static final String OPERATOR_PLMN = "gsm.operator.numeric";
    private static final String OPERATOR_NAME = "gsm.operator.alpha";
    private static Method GET;
    private static boolean bSet;

    private static void init() {
        try {
            if (!bSet) {
                Class<?> clazz = SystemPropertiesUtils.class.getClassLoader().loadClass("android.os.SystemProperties");
                GET = clazz.getDeclaredMethod("get", new Class[]{String.class, String.class});
                bSet = true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static String getOperatorPlmn() {
        init();

        String[] props = getProperties(OPERATOR_PLMN, "");

        if (props.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String prop : props) {
                sb.append(prop);
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            if (TextUtils.isEmpty(sb.toString())) {
                LogUtil.e("ANIME", "getOperatorPlmn(), props=null");
                return null;
            }
            return sb.toString();
        } else {
            LogUtil.e("ANIME", "getOperatorPlmn(), props.length=" + props.length);
            return null;
        }
    }

    public static String getOperatorName() {
        init();

        String[] props = getProperties(OPERATOR_NAME, "");
        StringBuilder sb = new StringBuilder();
        for (String prop : props) {
            sb.append(prop);
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * getProperty()
     *
     * @param phoneId    TelephonyManager.getDefaultSubscription()
     * @param property
     * @param defaultVal
     * @return
     */
    private static String getProperty(int phoneId, String property, String defaultVal) {
        String propVal = null;
        try {
            String prop = (String) GET.invoke(null, property, defaultVal);//SystemProperties.get(property);
            if ((prop != null) && (prop.length() > 0)) {
                String values[] = prop.split(",");
                if ((phoneId >= 0) && (phoneId < values.length) && (values[phoneId] != null)) {
                    propVal = values[phoneId];
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return propVal == null ? defaultVal : propVal;
    }

    private static String getProperty(String property, String defaultVal) {
        String propVal = null;
        try {
            String prop = (String) GET.invoke(null, property, defaultVal);//SystemProperties.get(property);
            if ((prop != null) && (prop.length() > 0)) {
                String values[] = prop.split(",");
                for (String val : values) {
                    if (!val.equals("")) {
                        propVal = val;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return propVal == null ? defaultVal : propVal;
    }

    private static String[] getProperties(String property, String defaultVal) {
        String[] propVals = null;
        List<String> propList = new ArrayList<>();
        try {
            String prop = (String) GET.invoke(null, property, defaultVal);//SystemProperties.get(property);
            if ((prop != null) && (prop.length() > 0)) {
                String values[] = prop.split(",");
                for (String val : values) {
                    if (!val.equals("")) {
                        propList.add(val);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return propList.size() == 0 ? new String[]{defaultVal} : propList.toArray(new String[0]);
    }
}
