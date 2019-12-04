package com.mcal.codeviewer;

import android.content.Context;
import android.webkit.JavascriptInterface;

class MyJavaScriptInterface {
    private Context context;

    MyJavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void getHTMLElement(String str) {
        ((MainActivity) context).setTextViewText(str);
    }

    @JavascriptInterface
    public void processHTML(String str) {
        ((MainActivity) context).setHTMLSourceCode(str);
    }
}
