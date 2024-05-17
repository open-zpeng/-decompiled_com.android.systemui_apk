package com.xiaopeng.systemui.infoflow.music;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.effect.EffectView;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.music.receiver.MusicModeReceiver;
/* loaded from: classes24.dex */
public class MusicRootView extends RelativeLayout implements IMusicView {
    private static final String TAG = "MusicRootView";
    private EffectView mEffectView;
    private MediaCardView mMediaCardView;
    private MediaManager mMediaManager;
    private MusicViewContainer mMusicViewContainer;
    MediaManager.OnFftDataCaptureListener mOnFftDataCaptureListener;
    MediaManager.OnPlayStatusChangedListener mOnPlayStatusChangedListener;

    public MusicRootView(Context context) {
        this(context, null);
    }

    public MusicRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MusicRootView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.mOnFftDataCaptureListener = new MediaManager.OnFftDataCaptureListener() { // from class: com.xiaopeng.systemui.infoflow.music.MusicRootView.1
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnFftDataCaptureListener
            public void onFftData(byte[] bytes, int i) {
                MusicRootView.this.mEffectView.updateFftDataCapture(bytes, i);
            }
        };
        this.mOnPlayStatusChangedListener = new MediaManager.OnPlayStatusChangedListener() { // from class: com.xiaopeng.systemui.infoflow.music.MusicRootView.2
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnPlayStatusChangedListener
            public void onStatusChanged(int status) {
                if (status == 0) {
                    MusicRootView.this.mEffectView.start();
                } else {
                    MusicRootView.this.mEffectView.stop();
                }
            }
        };
        init();
    }

    private void init() {
        this.mMediaManager = MediaManager.getInstance();
        this.mMediaManager.addOnFftDataCaptureListener(this.mOnFftDataCaptureListener);
        this.mMediaManager.addOnPlayStatusChangedListener(this.mOnPlayStatusChangedListener);
        new MusicModeReceiver(getContext(), this).register();
    }

    public void setupWithContainer(MusicViewContainer musicViewContainer) {
        this.mMusicViewContainer = musicViewContainer;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mMediaCardView = (MediaCardView) findViewById(R.id.view_media_card);
        this.mEffectView = (EffectView) findViewById(R.id.effectView);
        this.mEffectView.setStyle(0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // com.xiaopeng.systemui.infoflow.music.IMusicView
    public void enterMusicMode() {
        this.mMediaCardView.setVisibility(0);
        MusicViewContainer musicViewContainer = this.mMusicViewContainer;
        if (musicViewContainer != null) {
            musicViewContainer.enterMusicMode();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.music.IMusicView
    public void exitMusicMode() {
        this.mMediaCardView.setVisibility(8);
        this.mMusicViewContainer.exitMusicMode();
    }
}
