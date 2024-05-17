package com.xiaopeng.xui.app.delegate;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.xiaopeng.xui.app.delegate.XActivityDismissCause;
import com.xiaopeng.xui.app.delegate.XActivityDismissExtend;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XLogUtils;
import java.util.Iterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes25.dex */
public class XActivityDelegateImpl extends XActivityDelegate implements XActivityDismissCause.CallBack, XActivityDismissExtend.OnDismissListenerEx {
    private static final String TAG = "XActivityDelegate";
    private AppCompatActivity mActivity;
    private XActivityDismissExtend mActivityDismiss;
    private XActivityDismissCauseGroup mActivityDismissCauseGroup;
    private XActivityTemplateExtend mActivityTemplate;
    private XActivityWindowAttributes mWindowAttributes;
    private int mWindowBackgroundId;
    private XActivityWindowVisible mWindowVisible;
    private ArraySet<XActivityLifecycle> mActivityLifeCycles = new ArraySet<>();
    private Handler mHandler = new Handler();

    /* JADX INFO: Access modifiers changed from: package-private */
    public XActivityDelegateImpl(AppCompatActivity activity) {
        this.mActivity = activity;
        init();
    }

    private void init() {
        XTimeLogs timeLogs = XTimeLogs.create();
        timeLogs.start("XActivityDelegate-init");
        int templateType = analysisAnnotation(this.mActivity.getClass());
        timeLogs.record("analysisAnnotation " + templateType);
        this.mWindowAttributes = new XActivityWindowAttributes(this.mActivity);
        timeLogs.record("attributes");
        this.mWindowVisible = new XActivityWindowVisible(this.mActivity);
        timeLogs.record("visible");
        this.mActivityDismissCauseGroup = XActivityDismissCauseGroup.create(this.mActivity, this);
        timeLogs.record("cause");
        this.mActivityDismiss = XActivityDismissExtend.create(this.mActivity);
        this.mActivityDismiss.setOnDismissListenerEx(this);
        timeLogs.record("dismiss");
        this.mActivityTemplate = XActivityTemplateExtend.create(this.mActivity, templateType);
        timeLogs.record("template");
        this.mActivityTemplate.initDismiss(this.mActivityDismiss);
        this.mActivityTemplate.initDismissCauseGroup(this.mActivityDismissCauseGroup);
        this.mActivityTemplate.initWindowAttributes(this.mWindowAttributes);
        this.mActivityTemplate.initWindowVisible(this.mWindowVisible);
        timeLogs.record("template-init");
        this.mActivityLifeCycles.add(this.mWindowVisible);
        this.mActivityLifeCycles.add(this.mActivityDismiss);
        this.mActivityLifeCycles.add(this.mActivityTemplate);
        this.mActivityLifeCycles.add(this.mActivityDismissCauseGroup);
        timeLogs.end();
    }

    private int analysisAnnotation(Class<?> c) {
        XActivityBind annotation;
        if (c.isAnnotationPresent(XActivityBind.class) && (annotation = (XActivityBind) c.getAnnotation(XActivityBind.class)) != null) {
            return annotation.value();
        }
        Class<?> superClass = c.getSuperclass();
        if (superClass != null && !superClass.getName().equals(AppCompatActivity.class.getName())) {
            return analysisAnnotation(superClass);
        }
        return 0;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public <T extends XActivityTemplate> T getActivityTemplate() {
        return this.mActivityTemplate;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public XActivityDismiss getActivityDismiss() {
        return this.mActivityDismiss;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public XActivityWindowVisible getWindowVisible() {
        return this.mWindowVisible;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public XActivityWindowAttributes getWindowAttributes() {
        return this.mWindowAttributes;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onPostCreate(Bundle var1) {
        this.mWindowAttributes.apply();
        TypedArray array = this.mActivity.getTheme().obtainStyledAttributes(new int[]{16842836});
        this.mWindowBackgroundId = array.getResourceId(0, 0);
        array.recycle();
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.xui.app.delegate.-$$Lambda$XActivityDelegateImpl$6XVAO5Gie534uR4q1YRdDVP8vl8
            @Override // java.lang.Runnable
            public final void run() {
                XActivityDelegateImpl.this.lambda$onPostCreate$0$XActivityDelegateImpl();
            }
        });
    }

    public /* synthetic */ void lambda$onPostCreate$0$XActivityDelegateImpl() {
        XLogUtils.d(TAG, "activityTemplate: " + this.mActivityTemplate.toString());
        XLogUtils.d(TAG, "dismiss: " + this.mActivityDismiss.toString());
        XLogUtils.d(TAG, "windowVisible: " + this.mWindowVisible.toString());
        XLogUtils.d(TAG, "dismissCause: " + this.mActivityDismissCauseGroup.toString());
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onCreate(Bundle var1) {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onCreate(var1);
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onStart() {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onStart();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onResume() {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onResume();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onRecreate() {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onRecreate();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onPause() {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onPause();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onStop() {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onStop();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onDestroy() {
        Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
        while (it.hasNext()) {
            XActivityLifecycle lifecycle = it.next();
            lifecycle.onDestroy();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onConfigurationChanged(Configuration var1) {
        if (this.mWindowBackgroundId > 0) {
            XThemeManager.setWindowBackgroundResource(var1, this.mActivity.getWindow(), this.mWindowBackgroundId);
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void installViewFactory() {
        this.mActivity.getLayoutInflater().setFactory2(new LayoutInflater.Factory2() { // from class: com.xiaopeng.xui.app.delegate.XActivityDelegateImpl.1
            @Override // android.view.LayoutInflater.Factory2
            @Nullable
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return XActivityDelegateImpl.this.createView(parent, name, context, attrs);
            }

            @Override // android.view.LayoutInflater.Factory
            @Nullable
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return onCreateView(null, name, context, attrs);
            }
        });
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public View createView(@Nullable View parent, String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return this.mActivity.getDelegate().createView(parent, name, context, attrs);
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mActivityDismissCauseGroup.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mActivityDismissCauseGroup.dispatchTouchEvent();
        return false;
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void dispatchUserEvent() {
        this.mActivityDismissCauseGroup.dispatchUserEvent();
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDelegate
    public void onBackPressed() {
        this.mActivityDismissCauseGroup.onBackPressed();
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCause.CallBack
    public void onTriggerDismiss(int cause) {
        this.mActivityDismiss.dismiss(cause);
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDismissExtend.OnDismissListenerEx
    public void beforeDismiss(int cause) {
        this.mWindowVisible.changeWindowVisible(false);
        if (cause != 2 && this.mActivityDismissCauseGroup.getOnPauseSceneInspect() != null) {
            this.mActivityDismissCauseGroup.getOnPauseSceneInspect().ignoreDismissOneshot();
        }
    }

    @Override // com.xiaopeng.xui.app.delegate.XActivityDismissExtend.OnDismissListenerEx
    public void afterDismiss(int cause) {
    }
}
