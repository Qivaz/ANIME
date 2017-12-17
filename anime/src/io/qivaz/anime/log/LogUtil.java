package io.qivaz.anime.log;

/**
 * @author Qinghua Zhang @create 2017/3/24.
 */
public class LogUtil {

    private static LogInterface impl = new LogInterface() {
        @Override
        public void v(String tag, String msg) {

        }

        @Override
        public void d(String tag, String msg) {

        }

        @Override
        public void i(String tag, String msg) {

        }

        @Override
        public void w(String tag, String msg) {

        }

        @Override
        public void e(String tag, String msg) {

        }
    };

    public static void setImpl(LogInterface log) {
        impl = log;
    }

    public static void v(String tag, String msg) {
//        impl.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        impl.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        impl.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        impl.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        impl.e(tag, msg);
    }

    public interface LogInterface {
        void v(String tag, String msg);

        void d(String tag, String msg);

        void i(String tag, String msg);

        void w(String tag, String msg);

        void e(String tag, String msg);
    }
}
