package io.qivaz.anime.window;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

final class ViewRootInvocationHandler implements InvocationHandler {
    private Object mViewRootImpl;

    public ViewRootInvocationHandler(Object viewRootImpl) {
        mViewRootImpl = viewRootImpl;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(mViewRootImpl, args);
    }
}
