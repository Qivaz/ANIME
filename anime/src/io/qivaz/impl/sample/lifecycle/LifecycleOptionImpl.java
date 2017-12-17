package io.qivaz.impl.sample.lifecycle;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Qinghua Zhang @create 2017/4/19.
 */
public class LifecycleOptionImpl extends LifecycleOptionAdapter<LifecycleOptionImpl> {
    public static final String SP_ANIME = "Anime";
    public static final String SP_ANIME_OPTION_COLLECT = "options.COLLECT_ENABLED";
    public static final String SP_ANIME_OPTION_PARSE = "options.PARSE_ENABLED";
    public static final String SP_ANIME_OPTION_FILTER = "options.FILTER_ENABLED";
    public static final String SP_ANIME_OPTION_LOGOUT = "options.LOG_OUTPUT_ENABLED";

    public boolean COLLECT_ENABLED;
    public boolean PARSE_ENABLED;
    public boolean FILTER_ENABLED;
    public boolean LOG_OUTPUT_ENABLED;

    public LifecycleOptionImpl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_ANIME, Context.MODE_PRIVATE);
        COLLECT_ENABLED = sp.getBoolean(SP_ANIME_OPTION_COLLECT, true);
        PARSE_ENABLED = sp.getBoolean(SP_ANIME_OPTION_PARSE, true);
        FILTER_ENABLED = sp.getBoolean(SP_ANIME_OPTION_FILTER, true);
        LOG_OUTPUT_ENABLED = sp.getBoolean(SP_ANIME_OPTION_LOGOUT, false);
    }

    @Override
    public boolean enabledCollect() {
        return COLLECT_ENABLED;
    }

    @Override
    public boolean enabledParse() {
        return PARSE_ENABLED;
    }

    @Override
    public boolean enabledFilter() {
        return FILTER_ENABLED;
    }

    @Override
    public boolean enabledLogOutput() {
        return LOG_OUTPUT_ENABLED;
    }

    @Override
    public void setCollect(boolean bCollect) {
        COLLECT_ENABLED = bCollect;
    }

    @Override
    public void setParse(boolean bParse) {
        PARSE_ENABLED = bParse;
    }

    @Override
    public void setFilter(boolean bFilter) {
        FILTER_ENABLED = bFilter;
    }

    @Override
    public void setLogOutput(boolean bLogOutput) {
        LOG_OUTPUT_ENABLED = bLogOutput;
    }

}
