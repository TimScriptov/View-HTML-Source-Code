package com.mcal.codeviewer;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class WebsiteAnalyzerWebChromeClient extends WebChromeClient {
    private Context context;

    WebsiteAnalyzerWebChromeClient(Context context) {
        this.context = context;
    }

    public void onProgressChanged(WebView webView, int i) {
        ((MainActivity) context).setProgressBar(i);
        super.onProgressChanged(webView, i);
    }
}
