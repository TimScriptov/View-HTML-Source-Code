package com.mcal.codeviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HtmlInspector implements OnTouchListener {
    private Context context;
    private float webViewHeight;
    private float webViewWidth;

    HtmlInspector(float f, float f2, Context context) {
        webViewWidth = f;
        webViewHeight = f2;
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            ((MainActivity) context).loadUrl("javascript:HTMLOUT.getHTMLElement(document.elementFromPoint(" + x + "* (window.innerWidth/" + webViewWidth + ")," + y + "*(window.innerHeight/" + webViewHeight + ")).outerHTML);");
        }
        return true;
    }
}