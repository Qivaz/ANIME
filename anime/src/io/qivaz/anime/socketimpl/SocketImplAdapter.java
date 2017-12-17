package io.qivaz.anime.socketimpl;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

import io.qivaz.anime.log.LogUtil;

/**
 * @author Qinghua Zhang @create 2017/5/12.
 */
public class SocketImplAdapter extends SocketImpl {
    private static final boolean debug = SocketImplInject.debug;

    private static Class<?> mSocketImplClass;
    private static Constructor mSocketImplConstructor;
    private static Method mAcceptMethod;
    private static Method mAvailableMethod;
    private static Method mBindMethod;
    private static Method mCloseMethod;
    private static Method mConnectMethod1;
    private static Method mConnectMethod2;
    private static Method mConnectMethod3;
    private static Method mCreateMethod;
    private static Method mGetInputStreamMethod;
    private static Method mGetOutputStreamMethod;
    private static Method mListenMethod;
    private static Method mSendUrgentDataMethod;
    private static Method mGetOptionMethod;
    private static Method mSetOptionMethod;
    private static Field mFdField;
    private static Field mAddressField;
    private static Field mPortField;
    private static Field mLocalportField;
    private static boolean bSet;
    private SocketImpl mSocketImpl;

    public SocketImplAdapter() {
        try {
            synchronized (SocketImplAdapter.class) {
                if (!bSet) {
//                    if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
//                        mSocketImplClass = Class.forName("java.net.SocksSocketImpl");
//
//                        Class socketImplClass = Class.forName("java.net.SocketImpl");
//                        mAddressField = socketImplClass.getDeclaredField("address");
//                        mAddressField.setAccessible(true);
//                        mPortField = socketImplClass.getDeclaredField("port");
//                        mPortField.setAccessible(true);
//                        mLocalportField = socketImplClass.getDeclaredField("localport");
//                        mLocalportField.setAccessible(true);
//                    } else {
//                        mSocketImplClass = Class.forName("java.net.PlainSocketImpl");
//                        
//                        Class socketImplClass = Class.forName("java.net.SocketImpl");
//                        mFdField = socketImplClass.getDeclaredField("fd");
//                        mFdField.setAccessible(true);
//
//                        mAddressField = socketImplClass.getDeclaredField("address");
//                        mAddressField.setAccessible(true);
//                        mPortField = socketImplClass.getDeclaredField("port");
//                        mPortField.setAccessible(true);
//                        mLocalportField = socketImplClass.getDeclaredField("localport");
//                        mLocalportField.setAccessible(true);
//                    }
                    initSocketImpl();

                    mSocketImplConstructor = mSocketImplClass.getDeclaredConstructor();
                    mSocketImplConstructor.setAccessible(true);

                    mAcceptMethod = getMethod(mSocketImplClass, "accept", new Class[]{SocketImpl.class});
                    mAvailableMethod = getMethod(mSocketImplClass, "available", null);
                    mBindMethod = getMethod(mSocketImplClass, "bind", new Class[]{InetAddress.class, Integer.TYPE});
                    mCloseMethod = getMethod(mSocketImplClass, "close", null);
                    mConnectMethod1 = getMethod(mSocketImplClass, "connect", new Class[]{String.class, Integer.TYPE});
                    mConnectMethod2 = getMethod(mSocketImplClass, "connect", new Class[]{InetAddress.class, Integer.TYPE});
                    mConnectMethod3 = getMethod(mSocketImplClass, "connect", new Class[]{SocketAddress.class, Integer.TYPE});
                    mCreateMethod = getMethod(mSocketImplClass, "create", new Class[]{Boolean.TYPE});
                    mGetInputStreamMethod = getMethod(mSocketImplClass, "getInputStream", null);
                    mGetOutputStreamMethod = getMethod(mSocketImplClass, "getOutputStream", null);
                    mListenMethod = getMethod(mSocketImplClass, "listen", new Class[]{Integer.TYPE});
                    mSendUrgentDataMethod = getMethod(mSocketImplClass, "sendUrgentData", new Class[]{Integer.TYPE});
                    mGetOptionMethod = getMethod(mSocketImplClass, "getOption", new Class[]{Integer.TYPE});
                    mSetOptionMethod = getMethod(mSocketImplClass, "setOption", new Class[]{Integer.TYPE, Object.class});

                    bSet = true;
                }

                mSocketImpl = (SocketImpl) mSocketImplConstructor.newInstance();
            }
        } catch (NoSuchMethodException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(), failed!!! " + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(), failed!!! " + e);
            e.printStackTrace();
        } catch (InstantiationException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(), failed!!! " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(), failed!!! " + e);
            e.printStackTrace();
        }
        //LogUtil.v("ANIME", "SocketImplAdapter(), " + mSocketImpl);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(), " + mSocketImpl);
        }
    }

    private static void initSocketImpl() {
//        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
//            mSocketImplClass = Class.forName("java.net.SocksSocketImpl");
//
//            Class socketImplClass = Class.forName("java.net.SocketImpl");
//            mAddressField = socketImplClass.getDeclaredField("address");
//            mAddressField.setAccessible(true);
//            mPortField = socketImplClass.getDeclaredField("port");
//            mPortField.setAccessible(true);
//            mLocalportField = socketImplClass.getDeclaredField("localport");
//            mLocalportField.setAccessible(true);
//        } else {
//            mSocketImplClass = Class.forName("java.net.PlainSocketImpl");
//
//            Class socketImplClass = Class.forName("java.net.SocketImpl");
//            mFdField = socketImplClass.getDeclaredField("fd");
//            mFdField.setAccessible(true);
//
//            mAddressField = socketImplClass.getDeclaredField("address");
//            mAddressField.setAccessible(true);
//            mPortField = socketImplClass.getDeclaredField("port");
//            mPortField.setAccessible(true);
//            mLocalportField = socketImplClass.getDeclaredField("localport");
//            mLocalportField.setAccessible(true);
//        }
        Class socketImplClass = null;
        try {
            mSocketImplClass = Class.forName("java.net.SocksSocketImpl");
            LogUtil.w("ANIME", "SocketImplAdapter.initSocketImpl(), work at \"java.net.SocksSocketImpl\" class!");

            try {
                socketImplClass = Class.forName("java.net.SocketImpl");
                mFdField = socketImplClass.getDeclaredField("fd");
                mFdField.setAccessible(true);
            } catch (Exception e) {
                LogUtil.w("ANIME", "SocketImplAdapter.initSocketImpl(), no fd in \"java.net.SocksSocketImpl\"! " + e);
            }
        } catch (ClassNotFoundException e) {
            LogUtil.w("ANIME", "SocketImplAdapter.initSocketImpl(), no \"java.net.SocksSocketImpl\" class! " + e);
            try {
                mSocketImplClass = Class.forName("java.net.PlainSocketImpl");
                LogUtil.w("ANIME", "SocketImplAdapter.initSocketImpl(), work at \"java.net.PlainSocketImpl\" class!");

                socketImplClass = Class.forName("java.net.SocketImpl");
                mFdField = socketImplClass.getDeclaredField("fd");
                mFdField.setAccessible(true);
            } catch (ClassNotFoundException e1) {
                LogUtil.w("ANIME", "SocketImplAdapter.initSocketImpl(), no \"java.net.PlainSocketImpl\" class! " + e);
                LogUtil.e("ANIME", "SocketImplAdapter.initSocketImpl(), SocketImplAdapter initialize failed, must fix this issue! " + e);
            } catch (NoSuchFieldException e1) {
                LogUtil.w("ANIME", "SocketImplAdapter.initSocketImpl(), no fd in \"java.net.PlainSocketImpl\"! " + e);
            }
        }

        try {
            if (socketImplClass == null) {
                socketImplClass = Class.forName("java.net.SocketImpl");
            }
            mAddressField = socketImplClass.getDeclaredField("address");
            mAddressField.setAccessible(true);
            mPortField = socketImplClass.getDeclaredField("port");
            mPortField.setAccessible(true);
            mLocalportField = socketImplClass.getDeclaredField("localport");
            mLocalportField.setAccessible(true);
        } catch (ClassNotFoundException e) {
            LogUtil.e("ANIME", "SocketImplAdapter.initSocketImpl(), failed!!! " + e);
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            LogUtil.e("ANIME", "SocketImplAdapter.initSocketImpl(), failed!!! " + e);
            e.printStackTrace();
        }
    }

    private static Method getMethod(Class clazz, String method, Class[] paras) {
        Method m = null;
        Class c = clazz;
        boolean bFound = false;
        while (!Object.class.getName().equals(c.getName())) {
            try {
                if (paras != null) {
                    m = c.getDeclaredMethod(method, paras);
                } else {
                    m = c.getDeclaredMethod(method);
                }
                m.setAccessible(true);
                bFound = true;
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
                c = c.getSuperclass();
            }
            if (bFound) {
                break;
            }
        }
        if (m == null) {
            LogUtil.e("ANIME", "SocketImplAdapter.getMethod(" + clazz + ", " + method + "), falied!!!");
        }
        return m;
    }

    @Override
    protected void accept(SocketImpl newSocket) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").accept()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").accept()");
        }
        try {
            mAcceptMethod.setAccessible(true);
            mAcceptMethod.invoke(mSocketImpl, newSocket);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").accept(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").accept(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").accept(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").accept(), " + e);
        }
    }

    @Override
    protected int available() throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").available()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").available()");
        }
        int result = 0;
        try {
            mAvailableMethod.setAccessible(true);
            result = (int) mAvailableMethod.invoke(mSocketImpl);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").available(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").available(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").available(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").available(), " + e);
        }
        return result;
    }

    @Override
    protected void bind(InetAddress address, int port) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").bind()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").bind()");
        }
        try {
            mBindMethod.setAccessible(true);
            mBindMethod.invoke(mSocketImpl, address, port);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").bind(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").bind(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").bind(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").bind(), " + e);
        }
    }

    @Override
    protected void close() throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").close()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").close()");
        }
        try {
            mCloseMethod.setAccessible(true);
            mCloseMethod.invoke(mSocketImpl);

//            mSocketImpl = null;
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").close(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").close(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").close(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").close(), " + e);
        }
    }

    @Override
    protected void connect(String host, int port) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(1), host=" + host + ", port=" + port);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").connect(1), host=" + host + ", port=" + port);
        }
        try {
            mConnectMethod1.setAccessible(true);
            mConnectMethod1.invoke(mSocketImpl, host, port);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(1), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").connect(1), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(1), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").connect(1), " + e);
        }
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(1) return");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").connect(1) return");
        }
    }

    @Override
    protected void connect(InetAddress address, int port) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(2), address=" + address + ", port=" + port);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").connect(2), address=" + address + ", port=" + port);
        }
        try {
            mConnectMethod2.setAccessible(true);
            mConnectMethod2.invoke(mSocketImpl, address, port);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(2), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").connect(2), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(2), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").connect(2), " + e);
        }
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(2) return");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").connect(2) return");
        }
    }

    @Override
    protected void connect(SocketAddress remoteAddr, int timeout) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(3), remoteAddr=" + remoteAddr + ", timeout=" + timeout);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").connect(3), remoteAddr=" + remoteAddr + ", timeout=" + timeout);
        }
//        InetSocketAddress inetAddr = (InetSocketAddress) remoteAddr;
//        InetAddressInject.hadDnsCached(inetAddr.getHostString());
        try {
            mConnectMethod3.setAccessible(true);
            mConnectMethod3.invoke(mSocketImpl, remoteAddr, timeout);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(3), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").connect(3), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(3), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").connect(3), " + e);
        }
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").connect(3) return");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").connect(3) return");
        }
    }

    @Override
    protected void create(boolean isStreaming) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").create(), isStreaming=" + isStreaming);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").create(), isStreaming=" + isStreaming);
        }
        try {
            mCreateMethod.setAccessible(true);
            mCreateMethod.invoke(mSocketImpl, isStreaming);

//            if (Build.VERSION.SDK_INT < 24/*Build.VERSION_CODES.N*/) {
//                fd = (FileDescriptor) mFdField.get(mSocketImpl);
//            }
            if (mFdField != null) {
                fd = (FileDescriptor) mFdField.get(mSocketImpl);
            }
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").create(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").create(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").create(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").create(), " + e);
        }
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").create(), return");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").create(), return");
        }
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getInputStream()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").getInputStream()");
        }
        InputStream result = null;
        try {
            mGetInputStreamMethod.setAccessible(true);
            result = (InputStream) mGetInputStreamMethod.invoke(mSocketImpl);
            return new SocketImplInputStreamWrapper(result, mSocketImpl, (InetAddress) mAddressField.get(mSocketImpl), (int) mPortField.get(mSocketImpl), (int) mLocalportField.get(mSocketImpl));
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getInputStream(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").getInputStream(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getInputStream(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").getInputStream(), " + e);
        }
//        return result;
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getOutputStream()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").getOutputStream()");
        }
        OutputStream result = null;
        try {
            mGetOutputStreamMethod.setAccessible(true);
            result = (OutputStream) mGetOutputStreamMethod.invoke(mSocketImpl);
            return new SocketImplOutputStreamWrapper(result, mSocketImpl, (InetAddress) mAddressField.get(mSocketImpl), (int) mPortField.get(mSocketImpl), (int) mLocalportField.get(mSocketImpl));
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getOutputStream(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").getOutputStream(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getOutputStream(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").getOutputStream(), " + e);
        }
//        return result;
    }

    @Override
    protected void listen(int backlog) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").listen()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").listen()");
        }
        try {
            mListenMethod.setAccessible(true);
            mListenMethod.invoke(mSocketImpl, backlog);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").listen(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").listen(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").listen(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").listen(), " + e);
        }
    }

    @Override
    protected void sendUrgentData(int value) throws IOException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").sendUrgentData()");
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").sendUrgentData()");
        }
        try {
            mSendUrgentDataMethod.setAccessible(true);
            mSendUrgentDataMethod.invoke(mSocketImpl, value);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").sendUrgentData(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").sendUrgentData(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").sendUrgentData(), " + e);
            e.printStackTrace();
            throw new IOException("SocketImplAdapter(" + mSocketImpl + ").sendUrgentData(), " + e);
        }
    }

    @Override
    public Object getOption(int optID) throws SocketException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getOption(), optID="+optID);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").getOption(), optID=" + optID);
        }
        Object result = null;
        try {
            mGetOptionMethod.setAccessible(true);
            result = mGetOptionMethod.invoke(mSocketImpl, optID);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getOption(), " + e);
            e.printStackTrace();
            throw new SocketException("SocketImplAdapter(" + mSocketImpl + ").getOption(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").getOption(), " + e);
            e.printStackTrace();
            throw new SocketException("SocketImplAdapter(" + mSocketImpl + ").getOption(), " + e);
        }
        return result;
    }

    @Override
    public void setOption(int optID, Object val) throws SocketException {
        //LogUtil.v("ANIME", "SocketImplAdapter(" + mSocketImpl + ").setOption(), optID=" + optID + ", val=" + val);
        if (debug) {
            LogUtil.w("ANIME/SOCKET", "SocketImplAdapter(" + mSocketImpl + ").setOption(), optID=" + optID + ", val=" + val);
        }
        try {
            mSetOptionMethod.setAccessible(true);
            mSetOptionMethod.invoke(mSocketImpl, optID, val);
        } catch (InvocationTargetException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").setOption(), " + e);
            e.printStackTrace();
            throw new SocketException("SocketImplAdapter(" + mSocketImpl + ").setOption(), " + e);
        } catch (IllegalAccessException e) {
            LogUtil.e("ANIME", "SocketImplAdapter(" + mSocketImpl + ").setOption(), " + e);
            e.printStackTrace();
            throw new SocketException("SocketImplAdapter(" + mSocketImpl + ").setOption(), " + e);
        }
    }

    @Override
    public String toString() {
        return mSocketImpl.toString();
    }
}
