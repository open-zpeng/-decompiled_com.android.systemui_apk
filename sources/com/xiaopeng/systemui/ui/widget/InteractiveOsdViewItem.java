package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.OsdPresenter;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.ui.widget.AnimatedProgressBar;
/* loaded from: classes24.dex */
public class InteractiveOsdViewItem extends AlphaOptimizedRelativeLayout {
    private static final int MSG_SET_IN_TOUCH_MODE = 1;
    private static final String TAG = "InteractiveOsdViewItem";
    private int mIconSrc;
    private boolean mInTouchMode;
    private boolean mIsMediaItem;
    private AnimatedImageView mLargeIcon;
    private AnimatedProgressBar mLargeProgressbar;
    private int mStreamType;
    private Handler mUiHanlder;
    private int mVolumeName;
    private AnimatedTextView mVolumeNameView;

    public InteractiveOsdViewItem(Context context) {
        super(context);
        this.mInTouchMode = false;
        this.mIsMediaItem = false;
        this.mUiHanlder = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.InteractiveOsdViewItem.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    InteractiveOsdViewItem.this.mInTouchMode = false;
                    OsdController.getInstance(InteractiveOsdViewItem.this.mContext).setInTouchMode(((Boolean) msg.obj).booleanValue());
                }
            }
        };
        initView(context);
    }

    public InteractiveOsdViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInTouchMode = false;
        this.mIsMediaItem = false;
        this.mUiHanlder = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.InteractiveOsdViewItem.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    InteractiveOsdViewItem.this.mInTouchMode = false;
                    OsdController.getInstance(InteractiveOsdViewItem.this.mContext).setInTouchMode(((Boolean) msg.obj).booleanValue());
                }
            }
        };
        initAttrs(context, attrs);
        initView(context);
    }

    public InteractiveOsdViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInTouchMode = false;
        this.mIsMediaItem = false;
        this.mUiHanlder = new Handler() { // from class: com.xiaopeng.systemui.ui.widget.InteractiveOsdViewItem.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    InteractiveOsdViewItem.this.mInTouchMode = false;
                    OsdController.getInstance(InteractiveOsdViewItem.this.mContext).setInTouchMode(((Boolean) msg.obj).booleanValue());
                }
            }
        };
        initAttrs(context, attrs);
        initView(context);
    }

    public void setStreamType(int streamType) {
        this.mStreamType = streamType;
        AnimatedImageView animatedImageView = this.mLargeIcon;
        if (animatedImageView != null && this.mIsMediaItem) {
            animatedImageView.setImageResource(streamType == 11 ? R.drawable.ic_sysui_osd_volume_avas : R.drawable.ic_sysui_osd_volume_media);
        }
    }

    public void setIsMediaItem(boolean isMediaItem) {
        this.mIsMediaItem = isMediaItem;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_osd_item_interactive, (ViewGroup) this, true);
        this.mLargeIcon = (AnimatedImageView) findViewById(R.id.large_icon);
        this.mVolumeNameView = (AnimatedTextView) findViewById(R.id.volume_name);
        this.mLargeProgressbar = (AnimatedProgressBar) findViewById(R.id.large_progressbar);
        this.mLargeIcon.setImageResource(this.mIconSrc);
        this.mVolumeNameView.setText(this.mVolumeName);
        this.mLargeProgressbar.setProgressListener(new AnimatedProgressBar.OnProgressListener() { // from class: com.xiaopeng.systemui.ui.widget.InteractiveOsdViewItem.2
            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onProgressChanged(AnimatedProgressBar progressBar, int progress, boolean fromUser) {
                OsdPresenter.getInstance().setVolume(InteractiveOsdViewItem.this.mStreamType, progress);
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStartTrackingTouch(AnimatedProgressBar progressBar) {
                Logger.d(InteractiveOsdViewItem.TAG, "onStartTrackingTouch");
                InteractiveOsdViewItem.this.mInTouchMode = true;
                OsdPresenter.getInstance().stopAutoHideOsd();
                InteractiveOsdViewItem.this.mUiHanlder.removeMessages(1);
                OsdPresenter.getInstance().setInTouchMode(true);
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStopTrackingTouch(AnimatedProgressBar progressBar) {
                Logger.d(InteractiveOsdViewItem.TAG, "onStopTrackingTouch");
                OsdPresenter.getInstance().startAutoHideOsd();
                Message msg = InteractiveOsdViewItem.this.mUiHanlder.obtainMessage(1);
                msg.obj = false;
                InteractiveOsdViewItem.this.mUiHanlder.sendMessageDelayed(msg, 1000L);
            }
        });
    }

    public void destroy() {
        AnimatedProgressBar animatedProgressBar = this.mLargeProgressbar;
        if (animatedProgressBar != null) {
            animatedProgressBar.destroy();
        }
        this.mInTouchMode = false;
        OsdPresenter.getInstance().setInTouchMode(false);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InteractiveOsdViewItem, 0, 0);
        try {
            this.mIconSrc = a.getResourceId(0, 0);
            this.mVolumeName = a.getResourceId(1, 0);
        } finally {
            a.recycle();
        }
    }

    public void setMax(int progressMax) {
        this.mLargeProgressbar.setMax(progressMax);
    }

    public void setMin(int progressMin) {
        this.mLargeProgressbar.setMin(progressMin);
    }

    public void setProgress(int progress) {
        Logger.d(TAG, "setProgress : mInTouchMode = " + this.mInTouchMode);
        if (!this.mInTouchMode) {
            this.mLargeProgressbar.setProgress(progress);
        }
    }

    public AnimatedProgressBar getLargeProgressBar() {
        return this.mLargeProgressbar;
    }
}
