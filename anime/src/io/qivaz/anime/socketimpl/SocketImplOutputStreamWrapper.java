package io.qivaz.anime.socketimpl;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketImpl;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketImplOutputStreamWrapper extends OutputStream {
    private static final boolean debug = SocketImplInject.debug;

    private final OutputStream outputStreamImpl;
    private final SocketImpl mSocketImpl;
    private final InetAddress address;
    private final int port;
    private final int localport;

    public SocketImplOutputStreamWrapper(OutputStream os, SocketImpl socketImpl, InetAddress addr, int p, int lp) {
        outputStreamImpl = os;
        mSocketImpl = socketImpl;

        address = addr;
        port = p;
        localport = lp;
    }

    /**
     * ONLY FOR DEBUG
     */
    private void saveSocketStream2File(String str) {
        FileOutputStream fos = null;
        final String day = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                //if (fos == null) {
                String path = Environment.getExternalStorageDirectory() + "/ANIME";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = "anime_socket_" + day + ".log";
                fos = new FileOutputStream(path + File.separator + fileName, true);
                //}

                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("\r\n");
                fos.write(sb.toString().getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void close() throws IOException {
        //LogUtil.v("ANIME", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").close()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").close()");
        }
        outputStreamImpl.close();
    }

    @Override
    public void write(final int oneByte) throws IOException {
        //LogUtil.v("ANIME", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(oneByte=" + oneByte + ")");
        outputStreamImpl.write(oneByte);
    }

    @Override
    public void write(final byte[] buffer) throws IOException {
        //LogUtil.v("ANIME", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(buffer=" + buffer + ")");
        try {
            if (debug) {
                String out = new String(buffer, 0, buffer.length);
                saveSocketStream2File("----------------------------------------\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(new Date()) + "/OUT(" + mSocketImpl + "):\r\n" + out);
                LogUtil.w("ANIME/SOCKET", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(1), buffer=" + out);
            }
            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                AnimeInjection.getLifecycleMonitor().onSocketRequest(address, localport, port, new String(buffer, 0, buffer.length));
            }
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(2), " + e);
        }
        outputStreamImpl.write(buffer);
    }

    @Override
    public void write(final byte[] buffer, int offset, int len) throws IOException {
        //LogUtil.v("ANIME", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(offset=" + offset + ", byteCount=" + len + ", buffer=" + buffer + ")");
        try {
            if (debug) {
                String out = new String(buffer, offset, len);
                saveSocketStream2File("----------------------------------------\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(new Date()) + "/OUT(" + mSocketImpl + "):\r\n" + out);
                LogUtil.w("ANIME/SOCKET", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(2), buffer=" + out);
            }
            if (AnimeInjection.getLifecycleOption().enabledParse()) {
                AnimeInjection.getLifecycleMonitor().onSocketRequest(address, localport, port, new String(buffer, offset, len));
            }
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "SocketImplOutputStreamWrapper(" + mSocketImpl + ").write(2), " + e);
        }
        outputStreamImpl.write(buffer, offset, len);
    }
}
