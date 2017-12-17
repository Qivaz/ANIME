package io.qivaz.anime.webview;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Qinghua Zhang @create 2017/5/4.
 */
public class WebViewWrapper extends WebView {
    WebView mWebView;

    public WebViewWrapper(Context context) {
        super(context);
    }

    public void setDelegate(WebView webview) {
        mWebView = webview;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        //LogUtil.v("ANIME", "WebViewWrapper.setWebViewClient(), client=" + client);
        super.setWebViewClient(client);
    }
}
