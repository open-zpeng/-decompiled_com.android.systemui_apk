package com.xiaopeng.xui.app.delegate;

import android.app.Activity;
import android.view.WindowManager;
import androidx.annotation.NonNull;
/* loaded from: classes25.dex */
public class XActivityWindowAttributes {
    private int flags;
    private int gravity;
    private int height;
    private final Activity mActivity;
    private int systemUiVisibility;
    private int width;
    private int x;
    private int y;

    public XActivityWindowAttributes(@NonNull Activity activity) {
        this.mActivity = activity;
        init();
    }

    private void init() {
        WindowManager.LayoutParams lp;
        Activity activity = this.mActivity;
        if (activity != null && activity.getWindow() != null && (lp = this.mActivity.getWindow().getAttributes()) != null) {
            this.x = lp.x;
            this.y = lp.y;
            this.flags = lp.flags;
            this.width = lp.width;
            this.height = lp.height;
            this.gravity = lp.gravity;
            this.systemUiVisibility = lp.systemUiVisibility;
        }
    }

    public void apply() {
        WindowManager.LayoutParams lp;
        Activity activity = this.mActivity;
        if (activity != null && activity.getWindow() != null && (lp = this.mActivity.getWindow().getAttributes()) != null) {
            lp.x = this.x;
            lp.y = this.y;
            lp.flags |= this.flags;
            lp.width = this.width;
            lp.height = this.height;
            lp.gravity = this.gravity;
            lp.systemUiVisibility |= this.systemUiVisibility;
            this.mActivity.getWindow().setAttributes(lp);
        }
    }

    public XActivityWindowAttributes setX(int x) {
        this.x = x;
        return this;
    }

    public XActivityWindowAttributes setY(int y) {
        this.y = y;
        return this;
    }

    public XActivityWindowAttributes setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public XActivityWindowAttributes setWidth(int width) {
        this.width = width;
        return this;
    }

    public XActivityWindowAttributes setHeight(int height) {
        this.height = height;
        return this;
    }

    public XActivityWindowAttributes setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public XActivityWindowAttributes setSystemUiVisibility(int visibility) {
        this.systemUiVisibility = visibility;
        return this;
    }

    @NonNull
    public String toString() {
        return "{x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ", flags=" + this.flags + ", gravity=" + this.gravity + ", systemUiVisibility=" + this.systemUiVisibility + ", mActivity=" + this.mActivity + '}';
    }
}
