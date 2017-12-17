package io.qivaz.anime.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketFactoryWrapper extends SocketFactory {
    private SocketFactory mSocketFactory;

    public SocketFactoryWrapper(SocketFactory sf) {
        mSocketFactory = sf;
    }

    @Override
    public Socket createSocket() throws IOException {
        Socket socket = mSocketFactory.createSocket();
        LogUtil.e("ANIME", "SocketFactoryWrapper.createSocket(1), socket=" + socket);
        return new SocketWrapper(socket);
    }

    @Override
    public Socket createSocket(String host, int port)
            throws IOException, UnknownHostException {
        Socket socket = mSocketFactory.createSocket(host, port);
        LogUtil.e("ANIME", "SocketFactoryWrapper.createSocket(2), socket=" + socket);
        return new SocketWrapper(socket);
    }

    @Override
    public Socket createSocket(InetAddress address, int port)
            throws IOException {
        Socket socket = mSocketFactory.createSocket(address, port);
        LogUtil.e("ANIME", "SocketFactoryWrapper.createSocket(3), socket=" + socket);
        return new SocketWrapper(socket);
    }

    @Override
    public Socket createSocket(String host, int port,
                               InetAddress clientAddress, int clientPort)
            throws IOException, UnknownHostException {
        Socket socket = mSocketFactory.createSocket(host, port, clientAddress, clientPort);
        LogUtil.e("ANIME", "SocketFactoryWrapper.createSocket(4), socket=" + socket);
        return new SocketWrapper(socket);
    }

    @Override
    public Socket createSocket(InetAddress address, int port,
                               InetAddress clientAddress, int clientPort)
            throws IOException {
        Socket socket = mSocketFactory.createSocket(address, port, clientAddress, clientPort);
        LogUtil.e("ANIME", "SocketFactoryWrapper.createSocket(5), socket=" + socket);
        return new SocketWrapper(socket);
    }
}