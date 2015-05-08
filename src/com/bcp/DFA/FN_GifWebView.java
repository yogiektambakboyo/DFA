package com.bcp.DFA;

import android.content.Context;
import android.webkit.WebView;

public class FN_GifWebView extends WebView {
    public FN_GifWebView(Context context, String path) {
        super(context);
        loadUrl("file:///android_asset/gambar.gif");
    }
}