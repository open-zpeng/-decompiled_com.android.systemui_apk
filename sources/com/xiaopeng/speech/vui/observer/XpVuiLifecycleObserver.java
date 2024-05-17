package com.xiaopeng.speech.vui.observer;

import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.xiaopeng.speech.XpSpeechEngine;
import com.xiaopeng.speech.vui.listener.IXpVuiSceneListener;
import com.xiaopeng.speech.vui.utils.LogUtils;
/* loaded from: classes.dex */
public class XpVuiLifecycleObserver implements LifecycleObserver {
    private static final String TAG = "VuiLifecycleObserver";
    private Lifecycle mLifeCycle;
    private IXpVuiSceneListener mListener;
    private String mSceneId;

    public XpVuiLifecycleObserver(String sceneId, IXpVuiSceneListener listener, Lifecycle lifecycle) {
        this.mSceneId = sceneId;
        this.mListener = listener;
        this.mLifeCycle = lifecycle;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        LogUtils.d(TAG, "onCreate: " + this.mSceneId);
        if (!TextUtils.isEmpty(this.mSceneId)) {
            XpSpeechEngine.registerVuiSceneListener(this.mSceneId, this.mListener);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        LogUtils.d(TAG, "onResume: " + this.mSceneId);
        if (!TextUtils.isEmpty(this.mSceneId)) {
            XpSpeechEngine.enterScene(this.mSceneId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        LogUtils.d(TAG, "onPause: " + this.mSceneId);
        if (!TextUtils.isEmpty(this.mSceneId)) {
            XpSpeechEngine.exitScene(this.mSceneId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy: " + this.mSceneId);
        if (!TextUtils.isEmpty(this.mSceneId)) {
            XpSpeechEngine.unregisterVuiSceneListener(this.mSceneId, this.mListener);
        }
        Lifecycle lifecycle = this.mLifeCycle;
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
        }
    }
}
