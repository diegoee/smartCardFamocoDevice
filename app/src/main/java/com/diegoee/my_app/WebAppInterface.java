package com.diegoee.my_app;

import android.webkit.JavascriptInterface;

public class WebAppInterface {
    InterfaceMainActivity listener;

    WebAppInterface(InterfaceMainActivity listener) {
        this.listener=listener;
    }

        @JavascriptInterface
    public void writeActionOK() {
        if (this.listener!=null) {
            this.listener.writeActionUser(true);
        }
    }

    @JavascriptInterface
    public void writeActionNoOK() {
        if (this.listener!=null) {
            this.listener.writeActionUser(false);
        }
    }
}