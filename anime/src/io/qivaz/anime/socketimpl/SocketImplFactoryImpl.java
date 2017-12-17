package io.qivaz.anime.socketimpl;

import java.net.SocketImpl;
import java.net.SocketImplFactory;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketImplFactoryImpl implements SocketImplFactory {
    private static final boolean debug = SocketImplInject.debug;

    @Override
    public SocketImpl createSocketImpl() {
        //LogUtil.v("ANIME", "SocketImplFactoryImpl.createSocketImpl()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplFactoryImpl.createSocketImpl()");
        }
        return new SocketImplAdapter();
    }
}