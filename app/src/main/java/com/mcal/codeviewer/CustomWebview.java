package com.mcal.codeviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CustomWebview extends WebView {
    public CustomWebview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setDesktopMode(boolean z) {
        WebSettings settings = getSettings();
        settings.setUserAgentString(z ? settings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA") : settings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android"));
        settings.setUseWideViewPort(z);
        settings.setLoadWithOverviewMode(z);
        settings.setSupportZoom(z);
        settings.setBuiltInZoomControls(z);
    }
}