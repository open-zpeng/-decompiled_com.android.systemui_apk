package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.systemui.OsdPresenter;
import com.xiaopeng.systemui.controller.AudioController;
/* loaded from: classes24.dex */
public class InteractiveOsdView extends AlphaOptimizedRelativeLayout implements View.OnTouchListener, View.OnClickListener {
    private static final String TAG = "InteractiveOsdView";
    private AnimatedProgressBar mCurrentLargeProgressbar;
    private InteractiveOsdViewItem mCurrentVolumeContainer;
    private InteractiveOsdViewItem mMediaVolumeContainer;
    private InteractiveOsdViewItem mNaviVolumeContainer;
    private AlphaOptimizedLinearLayout mOtherOsdButton;
    private InteractiveOsdViewItem mSpeechVolumeContainer;

    public InteractiveOsdView(Context context) {
        super(context);
    }

    public InteractiveOsdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveOsdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mMediaVolumeContainer = (InteractiveOsdViewItem) findViewById(R.id.view_osd_media_volume);
        boolean isAvasStreamEnabled = AudioController.getInstance(this.mContext).isAvasStreamEnabled();
        this.mMediaVolumeContainer.setIsMediaItem(true);
        this.mMediaVolumeContainer.setStreamType(isAvasStreamEnabled ? 11 : 3);
        this.mNaviVolumeContainer = (InteractiveOsdViewItem) findViewById(R.id.view_osd_navi_volume);
        this.mNaviVolumeContainer.setStreamType(9);
        this.mSpeechVolumeContainer = (InteractiveOsdViewItem) findViewById(R.id.view_osd_speech_volume);
        this.mSpeechVolumeContainer.setStreamType(10);
        this.mOtherOsdButton = (AlphaOptimizedLinearLayout) findViewById(R.id.other_osd_button);
        this.mOtherOsdButton.setVisibility(8);
        this.mMediaVolumeContainer.setOnTouchListener(this);
        this.mNaviVolumeContainer.setOnTouchListener(this);
        this.mSpeechVolumeContainer.setOnTouchListener(this);
        this.mOtherOsdButton.setOnClickListener(this);
    }

    public void setCurrentStreamType(int streamType) {
        if (streamType == 9) {
            this.mCurrentVolumeContainer = this.mNaviVolumeContainer;
        } else if (streamType == 10) {
            this.mCurrentVolumeContainer = this.mSpeechVolumeContainer;
        } else {
            this.mCurrentVolumeContainer = this.mMediaVolumeContainer;
            this.mCurrentVolumeContainer.setStreamType(streamType);
        }
    }

    public void setProgress(int streamType, int progress, int progressMin, int progressMax) {
        if (streamType != 3) {
            switch (streamType) {
                case 9:
                    setProgress(this.mNaviVolumeContainer, progress, progressMin, progressMax);
                    return;
                case 10:
                    setProgress(this.mSpeechVolumeContainer, progress, progressMin, progressMax);
                    return;
                case 11:
                    if (AudioController.getInstance(this.mContext).isAvasStreamEnabled()) {
                        setProgress(this.mMediaVolumeContainer, progress, progressMin, progressMax);
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else if (!AudioController.getInstance(this.mContext).isAvasStreamEnabled()) {
            setProgress(this.mMediaVolumeContainer, progress, progressMin, progressMax);
        }
    }

    private void setProgress(InteractiveOsdViewItem viewItem, int progress, int progressMin, int progressMax) {
        if (viewItem != null) {
            viewItem.setMin(progressMin);
            viewItem.setMax(progressMax);
            viewItem.setProgress(progress);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        InteractiveOsdViewItem interactiveOsdViewItem;
        super.setVisibility(visibility);
        if (visibility != 0 && (interactiveOsdViewItem = this.mCurrentVolumeContainer) != null) {
            interactiveOsdViewItem.destroy();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            boolean needUpdateSelectedContainer = false;
            switch (view.getId()) {
                case R.id.view_osd_media_volume /* 2131363422 */:
                    InteractiveOsdViewItem interactiveOsdViewItem = this.mCurrentVolumeContainer;
                    InteractiveOsdViewItem interactiveOsdViewItem2 = this.mMediaVolumeContainer;
                    if (interactiveOsdViewItem != interactiveOsdViewItem2) {
                        this.mCurrentVolumeContainer = interactiveOsdViewItem2;
                        needUpdateSelectedContainer = true;
                        break;
                    }
                    break;
                case R.id.view_osd_navi_volume /* 2131363423 */:
                    InteractiveOsdViewItem interactiveOsdViewItem3 = this.mCurrentVolumeContainer;
                    InteractiveOsdViewItem interactiveOsdViewItem4 = this.mNaviVolumeContainer;
                    if (interactiveOsdViewItem3 != interactiveOsdViewItem4) {
                        this.mCurrentVolumeContainer = interactiveOsdViewItem4;
                        needUpdateSelectedContainer = true;
                        break;
                    }
                    break;
                case R.id.view_osd_speech_volume /* 2131363424 */:
                    InteractiveOsdViewItem interactiveOsdViewItem5 = this.mCurrentVolumeContainer;
                    InteractiveOsdViewItem interactiveOsdViewItem6 = this.mSpeechVolumeContainer;
                    if (interactiveOsdViewItem5 != interactiveOsdViewItem6) {
                        this.mCurrentVolumeContainer = interactiveOsdViewItem6;
                        needUpdateSelectedContainer = true;
                        break;
                    }
                    break;
            }
            if (needUpdateSelectedContainer) {
                OsdPresenter.getInstance().stopAutoHideOsd();
                this.mCurrentLargeProgressbar = this.mCurrentVolumeContainer.getLargeProgressBar();
            }
        } else if (action == 1) {
            OsdPresenter.getInstance().startAutoHideOsd();
        }
        AnimatedProgressBar animatedProgressBar = this.mCurrentLargeProgressbar;
        if (animatedProgressBar != null) {
            animatedProgressBar.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.other_osd_button) {
            OsdPresenter.getInstance().startAutoHideOsd();
            showOtherOsd();
        }
    }

    public void reset() {
        this.mNaviVolumeContainer.setVisibility(8);
        this.mSpeechVolumeContainer.setVisibility(8);
        this.mOtherOsdButton.setVisibility(0);
    }

    private void showOtherOsd() {
        this.mNaviVolumeContainer.setVisibility(0);
        this.mSpeechVolumeContainer.setVisibility(0);
        this.mOtherOsdButton.setVisibility(8);
    }
}
