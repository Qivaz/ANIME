package io.qivaz.anime.view;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qinghua Zhang @create 2017/3/31.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ActivityViewsStack {
    public static Map<Integer, List> viewStack = new HashMap<>();
}
