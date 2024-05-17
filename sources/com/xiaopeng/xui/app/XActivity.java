package com.xiaopeng.xui.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.xiaopeng.xui.app.delegate.XActivityDelegate;
import com.xiaopeng.xui.app.delegate.XActivityDismiss;
import com.xiaopeng.xui.app.delegate.XActivityTemplate;
import com.xiaopeng.xui.app.delegate.XActivityWindowAttributes;
import com.xiaopeng.xui.app.delegate.XActivityWindowVisible;
/* loaded from: classes25.dex */
public abstract class XActivity extends AppCompatActivity {
    private XActivityDelegate mDelegate;

    @NonNull
    public XActivityDelegate getXuiDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = XActivityDelegate.create(this);
        }
        return this.mDelegate;
    }

    @NonNull
    protected <T extends XActivityTemplate> T getActivityTemplate() {
        return (T) getXuiDelegate().getActivityTemplate();
    }

    @NonNull
    protected XActivityDismiss getActivityDismiss() {
        return getXuiDelegate().getActivityDismiss();
    }

    @NonNull
    protected XActivityWindowAttributes getWindowAttributes() {
        return getXuiDelegate().getWindowAttributes();
    }

    @NonNull
    protected XActivityWindowVisible getWindowVisible() {
        return getXuiDelegate().getWindowVisible();
    }

    public void dismissActivity() {
        getActivityDismiss().dismiss(0);
    }

    public void dispatchUserEvent() {
        getXuiDelegate().dispatchUserEvent();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        XActivityDelegate delegate = getXuiDelegate();
        delegate.installViewFactory();
        delegate.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        getXuiDelegate().onStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getXuiDelegate().onPostCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        getXuiDelegate().onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getXuiDelegate().onConfigurationChanged(newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        getXuiDelegate().onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        getXuiDelegate().onStop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getXuiDelegate().onSaveInstanceState(outState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        getXuiDelegate().onDestroy();
    }

    @Override // android.app.Activity
    public void recreate() {
        super.recreate();
        getXuiDelegate().onRecreate();
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        if (getXuiDelegate().onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getXuiDelegate().dispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        getXuiDelegate().onBackPressed();
    }
}
