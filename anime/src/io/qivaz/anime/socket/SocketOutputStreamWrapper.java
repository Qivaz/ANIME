package io.qivaz.anime.socket;

import java.io.IOException;
import java.io.OutputStream;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketOutputStreamWrapper extends OutputStream {
    private final OutputStream outputStreamImpl;

    public SocketOutputStreamWrapper(OutputStream os) {
        outputStreamImpl = os;
    }

    @Override
    public void close() throws IOException {
        LogUtil.e("ANIME", "SocketOutputStreamWrapper(" + this + ").close()");
        outputStreamImpl.close();
    }

    @Override
    public void write(int oneByte) throws IOException {
        LogUtil.e("ANIME", "SocketOutputStreamWrapper(" + this + ").write(oneByte=" + oneByte + ")");
        outputStreamImpl.write(oneByte);
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        LogUtil.e("ANIME", "SocketOutputStreamWrapper(" + this + ").write(buffer=" + buffer + ")");
        String out = new String(buffer, "UTF-8");
//        SocketInject.saveSocketStream2File("----------------------------------------\r\nOUT:\r\n" + out);
        LogUtil.e("ANIME", "SocketOutputStreamWrapper(" + this + ").write(1), buffer=" + out);
        outputStreamImpl.write(buffer);
    }

    @Override
    public void write(byte[] buffer, int offset, int byteCount) throws IOException {
        LogUtil.e("ANIME", "SocketOutputStreamWrapper(" + this + ").write(offset=" + offset + ", byteCount=" + byteCount + ", buffer=" + buffer + ")");
        String out = new String(buffer, "UTF-8");
//        SocketInject.saveSocketStream2File("----------------------------------------\r\nOUT:\r\n" + out);
        LogUtil.e("ANIME", "SocketOutputStreamWrapper(" + this + ").write(2), buffer=" + out);
        outputStreamImpl.write(buffer, offset, byteCount);
    }
}
