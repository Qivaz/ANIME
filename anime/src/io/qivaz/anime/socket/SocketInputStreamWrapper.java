package io.qivaz.anime.socket;

import java.io.IOException;
import java.io.InputStream;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketInputStreamWrapper extends InputStream {
    private final InputStream InputStreamImpl;

    public SocketInputStreamWrapper(InputStream is) {
        InputStreamImpl = is;
    }

    @Override
    public int available() throws IOException {
        int avail = InputStreamImpl.available();
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").available(), " + avail);
        return avail;
    }

    @Override
    public void close() throws IOException {
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").close()");
        InputStreamImpl.close();
    }

    @Override
    public int read() throws IOException {
        int by = InputStreamImpl.read();
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").read(), " + by);
        return by;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        int len = InputStreamImpl.read(buffer);
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").read(buffer=" + buffer + "), " + len);
        String in = new String(buffer, "UTF-8");
//        SocketInject.saveSocketStream2File("----------------------------------------\r\nIN:\r\n" + in);
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").read(1), buffer=" + in);
        return len;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int len = InputStreamImpl.read(buffer, byteOffset, byteCount);
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").read(byteOffset=" + byteOffset + ", byteCount=" + byteCount + ", buffer=" + buffer + "), " + len);
        String in = new String(buffer, "UTF-8");
//        SocketInject.saveSocketStream2File("----------------------------------------\r\nIN:\r\n" + in);
        LogUtil.e("ANIME", "SocketInputStreamWrapper(" + this + ").read(2), buffer=" + in);
        return len;
    }
}
