package io.qivaz.anime.monitor.lifecycle;

/**
 * @author Qinghua Zhang @create 2017/4/19.
 */
public interface LifecycleParser {

    /**
     * Parse the collected monitor data.
     *
     * @param object collected data
     */
    void onParse(Object object);

    /**
     * Fire, with parsed data.
     * <p/>
     * If you just fire each time after parsed,
     * no need implement&invoke this method.
     */
    void onFire(boolean immediate);
}
