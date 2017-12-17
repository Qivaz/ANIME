package io.qivaz.anime.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketWrapper extends Socket {
    private Socket mSocket;

    public SocketWrapper(Socket socket) {
        mSocket = socket;
    }

    @Override
    public synchronized void close() throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").close()");
        mSocket.close();
    }

//    /**
//     * Sets the Socket and its related SocketImpl state as if a successful close() took place,
//     * without actually performing an OS close().
//     *
//     * @hide used in java.nio
//     */
//    public void onClose() {
//    }

    @Override
    public InetAddress getInetAddress() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getInetAddress()");
        return mSocket.getInetAddress();
    }


    @Override
    public InputStream getInputStream() throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getInputStream()");
        return new SocketInputStreamWrapper(mSocket.getInputStream());
    }


    @Override
    public boolean getKeepAlive() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getKeepAlive()");
        return mSocket.getKeepAlive();
    }

    @Override
    public void setKeepAlive(boolean keepAlive) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setKeepAlive()");
        mSocket.setKeepAlive(keepAlive);
    }

    @Override
    public InetAddress getLocalAddress() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getLocalAddress()");
        return mSocket.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getLocalPort()");
        return mSocket.getLocalPort();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getOutputStream()");
        return new SocketOutputStreamWrapper(mSocket.getOutputStream());
    }

    @Override
    public int getPort() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getPort()");
        return mSocket.getPort();
    }

    @Override
    public int getSoLinger() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getSoLinger()");
        return mSocket.getSoLinger();
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getReceiveBufferSize()");
        return mSocket.getReceiveBufferSize();
    }

    @Override
    public synchronized void setReceiveBufferSize(int size) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setReceiveBufferSize()");
        mSocket.setReceiveBufferSize(size);
    }

    @Override
    public synchronized int getSendBufferSize() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getSendBufferSize()");
        return mSocket.getSendBufferSize();
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setSendBufferSize()");
        mSocket.setSendBufferSize(size);
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getSoTimeout()");
        return mSocket.getSoTimeout();
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setSoTimeout()");
        mSocket.setSoTimeout(timeout);
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getTcpNoDelay()");
        return mSocket.getTcpNoDelay();
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setTcpNoDelay()");
        mSocket.setTcpNoDelay(on);
    }

    @Override
    public void setSoLinger(boolean on, int timeout) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setSoLinger()");
        mSocket.setSoLinger(on, timeout);
    }

    @Override
    public String toString() {
        return mSocket.toString();
    }

    @Override
    public void shutdownInput() throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").shutdownInput()");
        mSocket.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").shutdownOutput()");
        mSocket.shutdownOutput();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getLocalSocketAddress()");
        return mSocket.getLocalSocketAddress();
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getRemoteSocketAddress()");
        return mSocket.getRemoteSocketAddress();
    }

    @Override
    public boolean isBound() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").isBound()");
        return mSocket.isBound();
    }

    @Override
    public boolean isConnected() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").isConnected()");
        return mSocket.isConnected();
    }

    @Override
    public boolean isClosed() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").isClosed()");
        return mSocket.isClosed();
    }

    @Override
    public void bind(SocketAddress localAddr) throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").bind()");
        mSocket.bind(localAddr);
    }

//    /**
//     * Sets the Socket and its related SocketImpl state as if a successful bind() took place,
//     * without actually performing an OS bind().
//     *
//     * @hide used in java.nio
//     */
//    public void onBind(InetAddress localAddress, int localPort) {
//    }

    @Override
    public void connect(SocketAddress remoteAddr) throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").connect(1), " + remoteAddr);
        mSocket.connect(remoteAddr);
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").connect(1) return");
    }

    @Override
    public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").connect(2), " + remoteAddr + ", " + timeout);
        InetSocketAddress inetAddr = (InetSocketAddress) remoteAddr;
//        InetAddress addr = null;
//        if ((addr = inetAddr.getAddress()) == null) {
//            throw new UnknownHostException("Host is unresolved: " + inetAddr.getHostName());
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            InetAddressInject.hadDnsCached(inetAddr.getHostString());
//        }

        mSocket.connect(remoteAddr, timeout);
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").connect(2) return");
    }

//    /**
//     * Sets the Socket and its related SocketImpl state as if a successful connect() took place,
//     * without actually performing an OS connect().
//     *
//     * @hide internal use only
//     */
//    public void onConnect(InetAddress remoteAddress, int remotePort) {
//    }

    @Override
    public boolean isInputShutdown() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").isInputShutdown()");
        return mSocket.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").isOutputShutdown()");
        return mSocket.isOutputShutdown();
    }

    @Override
    public boolean getReuseAddress() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getReuseAddress()");
        return mSocket.getReuseAddress();
    }

    @Override
    public void setReuseAddress(boolean reuse) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setReuseAddress()");
        mSocket.setReuseAddress(reuse);
    }

    @Override
    public boolean getOOBInline() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getOOBInline()");
        return mSocket.getOOBInline();
    }

    @Override
    public void setOOBInline(boolean oobinline) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setOOBInline()");
        mSocket.setOOBInline(oobinline);
    }

    @Override
    public int getTrafficClass() throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getTrafficClass()");
        return mSocket.getTrafficClass();
    }

    @Override
    public void setTrafficClass(int value) throws SocketException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setTrafficClass()");
        mSocket.setTrafficClass(value);
    }

    @Override
    public void sendUrgentData(int value) throws IOException {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").sendUrgentData()");
        mSocket.sendUrgentData(value);
    }

    @Override
    public SocketChannel getChannel() {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").getChannel()");
        return mSocket.getChannel();
    }

//    /**
//     * @hide internal use only
//     */
//    public FileDescriptor getFileDescriptor$() {
//    }

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        LogUtil.e("ANIME", "SocketWrapper(" + this + ").setPerformancePreferences()");
        mSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
}
