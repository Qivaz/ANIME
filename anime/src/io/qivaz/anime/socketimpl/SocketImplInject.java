package io.qivaz.anime.socketimpl;

import java.io.IOException;
import java.net.Socket;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketImplInject {
    static final boolean debug = false;

    public static void run() {
        inject();
    }

    private static void inject() {
        try {
            Socket.setSocketImplFactory(new SocketImplFactoryImpl());
        } catch (IOException e) {
            LogUtil.e("ANIME", "SocketImplInject.inject(), " + e);
            e.printStackTrace();
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "SocketImplInject.inject(), " + e);
            e.printStackTrace();
        }
    }
}