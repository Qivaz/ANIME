package io.qivaz.anime.socketimpl;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketImpl;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.qivaz.anime.log.LogUtil;
import io.qivaz.anime.monitor.AnimeInjection;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketImplInputStreamWrapper extends InputStream {
    private static final boolean debug = SocketImplInject.debug;

    private final InputStream InputStreamImpl;
    private final SocketImpl mSocketImpl;
    private final InetAddress address;
    private final int port;
    private final int localport;

    public SocketImplInputStreamWrapper(InputStream is, SocketImpl socketImpl, InetAddress addr, int p, int lp) {
        InputStreamImpl = is;
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
    public int available() throws IOException {
        int avail = InputStreamImpl.available();
        //LogUtil.v("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").available(), " + avail);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplInputStreamWrapper(" + mSocketImpl + ").available(), " + avail);
        }
        return avail;
    }

    @Override
    public void close() throws IOException {
        //LogUtil.v("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").close()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplInputStreamWrapper(" + mSocketImpl + ").close()");
        }
        InputStreamImpl.close();
    }

    @Override
    public int read() throws IOException {
        int by = InputStreamImpl.read();
        //LogUtil.v("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(), " + by);
        return by;
    }

    @Override
    public int read(final byte[] buffer) throws IOException {
        int len = InputStreamImpl.read(buffer);
        //LogUtil.v("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(buffer=" + buffer + "), " + len);
        try {
            if (len > 0) {
                if (debug) {
                    String in = new String(buffer, 0, len);
                    saveSocketStream2File("----------------------------------------\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(new Date()) + "/IN(" + mSocketImpl + "):\r\n" + in);
                    LogUtil.w("ANIME/SOCKET", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(1), buffer=" + in);
                }
                if (AnimeInjection.getLifecycleOption().enabledParse()) {
                    AnimeInjection.getLifecycleMonitor().onSocketResponse(address, localport, port, new String(buffer, 0, len));
                }
            } else {
                LogUtil.e("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(1), len=" + len);
            }
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(1), " + e);
        }
        return len;
    }

    @Override
    public int read(final byte[] buffer, int byteOffset, int maxLen) throws IOException {
        int len = InputStreamImpl.read(buffer, byteOffset, maxLen);
        try {
            //LogUtil.v("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(byteOffset=" + byteOffset + ", byteCount=" + maxLen + ", buffer=" + buffer + "), " + len);
            if (len > 0) {
                if (debug) {
                    String in = new String(buffer, byteOffset, len);
                    saveSocketStream2File("----------------------------------------\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS").format(new Date()) + "/IN(" + mSocketImpl + "):\r\n" + in);
                    LogUtil.w("ANIME/SOCKET", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(2), buffer=" + in);
                }
                if (AnimeInjection.getLifecycleOption().enabledParse()) {
                    AnimeInjection.getLifecycleMonitor().onSocketResponse(address, localport, port, new String(buffer, byteOffset, len));
                }
            } else {
                LogUtil.e("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(2), len=" + len);
            }
        } catch (Exception | Error e) {
            LogUtil.e("ANIME", "SocketImplInputStreamWrapper(" + mSocketImpl + ").read(2), " + e);
        }
        return len;
    }
}
