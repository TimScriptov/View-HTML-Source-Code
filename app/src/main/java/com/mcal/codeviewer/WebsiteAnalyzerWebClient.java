package com.mcal.codeviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class WebsiteAnalyzerWebClient extends WebViewClient {
    private Context context;

    WebsiteAnalyzerWebClient(Context context) {
        this.context = context;
    }

    public void onPageFinished(WebView webView, String str) {
        ((MainActivity) context).hideProgressBar();
        ((MainActivity) context).loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
        if (!str.equalsIgnoreCase("about:blank")) {
            ((MainActivity) context).setWebsiteAddress(str);
        }
        super.onPageFinished(webView, str);
    }

    public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
        ((MainActivity) context).setProgressBar(0);
        ((MainActivity) context).showProgressBar();
        super.onPageStarted(webView, str, bitmap);
    }

    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
        return super.shouldInterceptRequest(webView, webResourceRequest);
    }

    public boolean shouldOverrideUrlLoading(WebView webView, String str) {
        if (str.toLowerCase().contains("youtube")) {
            ((MainActivity) context).showYoutubeViolationToast();
        } else if (str.startsWith("http://") || str.startsWith("https://")) {
            webView.loadUrl(str);
            ((MainActivity) context).setEditText(str);
        } else {
            webView.loadUrl("http://" + str);
            ((MainActivity) context).setEditText("http://" + str);
        }
        return true;
    }
}