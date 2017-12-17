package io.qivaz.anime.executor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/4/5.
 */
public class SerialExecutor implements Executor {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "SerialExecutor #" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    private static volatile Executor mExecutor = new SerialExecutor();
    final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    Runnable mActive;

    public static void run(Runnable r) {
        mExecutor.execute(r);
    }

    @Override
    public synchronized void execute(final Runnable r) {
        mTasks.offer(new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Exception | Error e) {
                    LogUtil.e("ANIME", "SerialExecutor.execute(), e=" + e);

                    try {
                        Writer writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        e.printStackTrace(printWriter);
                        Throwable cause = e.getCause();
                        while (cause != null) {
                            cause.printStackTrace(printWriter);
                            cause = cause.getCause();
                        }
                        printWriter.close();
                        String exceptionInfo = writer.toString();
                        LogUtil.e("ANIME", "SerialExecutor.execute(), " + exceptionInfo);
                    } catch (Exception | Error e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if ((mActive = mTasks.poll()) != null) {
            THREAD_POOL_EXECUTOR.execute(mActive);
        }
    }
}
