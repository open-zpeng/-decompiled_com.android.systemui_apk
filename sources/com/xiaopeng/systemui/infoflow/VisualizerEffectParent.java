package com.xiaopeng.systemui.infoflow;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.effect.EffectView;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class VisualizerEffectParent extends RelativeLayout {
    private static final String TAG = "VisualizerEffectParent";
    private EffectView mEffectView;
    MediaManager.OnFftDataCaptureListener mFftDataCaptureListener;
    private MediaManager mMediaManager;
    MediaManager.OnPlayStatusChangedListener mPlayStatusChangedListener;
    MediaManager.OnVisualizerViewEnableListener mVisualizerViewEnableListener;

    public VisualizerEffectParent(Context context) {
        this(context, null);
    }

    public VisualizerEffectParent(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VisualizerEffectParent(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mFftDataCaptureListener = new MediaManager.OnFftDataCaptureListener() { // from class: com.xiaopeng.systemui.infoflow.VisualizerEffectParent.1
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnFftDataCaptureListener
            public void onFftData(byte[] bytes, int i) {
                if (VisualizerEffectParent.this.mEffectView.getVisibility() == 0) {
                    VisualizerEffectParent.this.mEffectView.updateFftDataCapture(bytes, i);
                }
            }
        };
        this.mPlayStatusChangedListener = new MediaManager.OnPlayStatusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.VisualizerEffectParent.2
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayStatusChangedListener
            public void onStatusChanged(int status) {
                if (status == 0) {
                    VisualizerEffectParent.this.mEffectView.start();
                } else {
                    VisualizerEffectParent.this.mEffectView.stop();
                }
            }
        };
        this.mVisualizerViewEnableListener = new MediaManager.OnVisualizerViewEnableListener() { // from class: com.xiaopeng.systemui.infoflow.VisualizerEffectParent.3
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnVisualizerViewEnableListener
            public void onViewEnable(boolean enable) {
                VisualizerEffectParent.this.mEffectView.setVisibility(enable ? 0 : 8);
            }
        };
        init();
    }

    private void init() {
        this.mMediaManager = MediaManager.getInstance();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow");
        registerListener();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        Log.i(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        unRegisterListener();
    }

    private void registerListener() {
        this.mMediaManager.addOnFftDataCaptureListener(this.mFftDataCaptureListener);
        this.mMediaManager.addOnPlayStatusChangedListener(this.mPlayStatusChangedListener);
        this.mMediaManager.addVisualizerViewEnableListener(this.mVisualizerViewEnableListener);
    }

    private void unRegisterListener() {
        this.mMediaManager.removeOnFftDataCaptureListener(this.mFftDataCaptureListener);
        this.mMediaManager.removeOnPlayStatusChangedListener(this.mPlayStatusChangedListener);
        this.mMediaManager.removeVisualizerViewEnableListener(this.mVisualizerViewEnableListener);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mEffectView = (EffectView) findViewById(R.id.effectView);
        InfoFlowConfigDao.Config config = InfoFlowConfigDao.getInstance().getConfig();
        if (config != null) {
            this.mEffectView.setStyle(config.visualizerViewType);
            this.mEffectView.setVisibility(config.visualizerEnable ? 0 : 8);
            return;
        }
        Logger.w(TAG, "parseConfig file may be failed");
        this.mEffectView.setStyle(0);
    }
}
