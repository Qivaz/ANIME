package io.qivaz.impl.sample.lifecycle;

import io.qivaz.anime.monitor.lifecycle.LifecycleOption;

/**
 * @author Qinghua Zhang @create 2017/4/19.
 */
public class LifecycleOptionAdapter<T> implements LifecycleOption<T> {
    protected T mOption;

    @Override
    public T getOption() {
        return mOption;
    }

    @Override
    public void setOption(T option) {
        mOption = option;
    }

    @Override
    public boolean enabledCollect() {
        return true;
    }

    @Override
    public boolean enabledParse() {
        return true;
    }

    @Override
    public boolean enabledFilter() {
        return true;
    }

    @Override
    public boolean enabledLogOutput() {
        return false;
    }

    @Override
    public void setCollect(boolean bCollect) {

    }

    @Override
    public void setParse(boolean bParse) {

    }

    @Override
    public void setFilter(boolean bFilter) {

    }

    @Override
    public void setLogOutput(boolean bLogOutput) {

    }
}
