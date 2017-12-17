package io.qivaz.impl.sample.lifecycle;

import android.app.Application;

import com.google.gson.Gson;

import java.util.ArrayDeque;
import java.util.Deque;

import io.qivaz.anime.monitor.lifecycle.LifecycleMonitorType.CrashRecord;
import io.qivaz.anime.monitor.lifecycle.LifecycleParser;

/**
 * @author Qinghua Zhang @create 2017/4/19.
 */
public class LifecycleParserImpl implements LifecycleParser {
    private static Gson gson = new Gson();
    private static int BATCH_NUM = 50;
    private final Deque<Object> mJsonDeque = new ArrayDeque<>();
    private Application app;

    public LifecycleParserImpl(Application application) {
        app = application;
    }

    @Override
    public void onParse(Object object) {
        if (true) {
            handleBatch(object);
        } else {
            handleEach(object);
        }
    }

    @Override
    public void onFire(boolean immediate) {
        fireBatch(immediate);
    }

    /**
     * To handle the collected data one by one at once.
     *
     * @param object collected data
     */
    private void handleEach(Object object) {
        dealJson(gson.toJson(object), false);
    }

    /**
     * To batch handle the collected data, may delay to dealJson.
     *
     * @param object collected data
     */
    private void handleBatch(Object object) {
        batch(object);
    }

    /**
     * Batch handle the collected data.
     * If the count in Deque reached <b>BATCH_NUM</b>, dealJson with the data,
     * or, push the data in Deque and return.
     * If Crash happened, dealJson immediately.
     *
     * @param object collected data
     */
    private void batch(Object object) {
        synchronized (mJsonDeque) {
            mJsonDeque.offer(object);
            if (mJsonDeque.size() >= BATCH_NUM
                    || object instanceof CrashRecord
                    /*|| object instanceof ResourceRecord*/) {
                Object obj = null;
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                while ((obj = mJsonDeque.poll()) != null) {
                    sb.append(gson.toJson(obj));
                    sb.append(',');
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(']');

                dealJson(sb.toString(), false);
            }
        }
    }

    /**
     * Fire batch to handle the collected data.
     * Run immediately, no matter the count reached or not.
     */
    private void fireBatch(boolean immediate) {
        synchronized (mJsonDeque) {
            if (mJsonDeque.size() == 0) {
                return;
            }

            Object obj = null;
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            while ((obj = mJsonDeque.poll()) != null) {
                sb.append(gson.toJson(obj));
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(']');

            dealJson(sb.toString(), immediate);
        }
    }

    /**
     * To deal with the JSON data
     * (Single JSON object, or JSON array combining <b>BATCH_NUM</b> JSON objects),
     * usually we upload it to back-end server, or|and save it in local.
     *
     * @param str JSON string
     */
    private void dealJson(final String str, boolean forceLocal) {
        //Todo
    }

}
