package io.qivaz.anime.monitor.lifecycle;

/**
 * @author Qinghua Zhang @create 2017/4/19.
 */
public interface LifecycleOption<T> {
    T getOption();

    void setOption(T option);

    /**
     * If collect data.
     *
     * @return
     */
    boolean enabledCollect();

    /**
     * If parse the collected data.
     *
     * @return
     */
    boolean enabledParse();

    /**
     * If set filter for parsed data.
     *
     * @return
     */
    boolean enabledFilter();

    /**
     * If log out debug info. in local SD card and Logcat,
     * in debug version.
     * NOTICE: NOT output in default.
     *
     * @return
     */
    boolean enabledLogOutput();

    /**
     * Set if collect data.
     * (Useful once, expired after restarted)
     */
    void setCollect(boolean bCollect);

    /**
     * Set if parse the collected data.
     * (Useful once, expired after restarted)
     */
    void setParse(boolean bParse);

    /**
     * Set if filter for parsed data.
     * (Useful once, expired after restarted)
     */
    void setFilter(boolean bFilter);

    /**
     * Set if log out detail info. in local SD card or Logcat,
     * in debug version.
     * (Useful once, expired after restarted)
     */
    void setLogOutput(boolean bLogOutput);
}
