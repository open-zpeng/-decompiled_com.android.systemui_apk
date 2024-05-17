package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.OsdPresenter;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.xui.widget.XScrollView;
/* loaded from: classes24.dex */
public class OsdView2 extends XScrollView {
    private static final String TAG = "OsdView2";
    private int mScreenId;
    private boolean mTouching;
    private ImageView mViewIcon;
    private OsdController.OsdParams mViewParams;
    private SeekBar mViewSeekbar;
    private TextView mViewTitle;

    public OsdView2(Context context) {
        super(context);
        this.mScreenId = -1;
        init(context, null);
    }

    public OsdView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScreenId = -1;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_osd_new_progress, this);
        this.mViewIcon = (ImageView) findViewById(R.id.icon);
        this.mViewTitle = (TextView) findViewById(R.id.name);
        this.mViewSeekbar = (SeekBar) findViewById(R.id.seekbar);
        this.mViewSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.xiaopeng.systemui.ui.widget.OsdView2.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    OsdPresenter.getInstance().setVolume(OsdView2.this.mViewParams.mStreamType, progress);
                    if (!OsdView2.this.mTouching) {
                        OsdView2.this.setInTouch(true);
                    }
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                Logger.d(OsdView2.TAG, "onStartTrackingTouch ");
                OsdView2.this.setInTouch(true);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                Logger.d(OsdView2.TAG, "onStopTrackingTouch ");
                OsdView2.this.setInTouch(false);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInTouch(boolean inTouchMode) {
        Logger.d(TAG, "setInTouch " + inTouchMode);
        this.mTouching = inTouchMode;
        OsdPresenter.getInstance().setInTouchMode(inTouchMode);
        if (inTouchMode) {
            OsdPresenter.getInstance().stopAutoHideOsd();
        } else {
            OsdPresenter.getInstance().startAutoHideOsd();
        }
    }

    private void setTitle(CharSequence title) {
        this.mViewTitle.setText(title);
    }

    private void setIcon(Drawable icon) {
        this.mViewIcon.setImageDrawable(icon);
    }

    private void setProgress(int progress, int min, int max) {
        this.mViewSeekbar.setMin(min);
        this.mViewSeekbar.setMax(max);
        this.mViewSeekbar.setProgress(progress);
    }

    public void apply(OsdController.OsdParams params) {
        Logger.d(TAG, "apply params=" + params);
        if (params == null) {
            return;
        }
        boolean isDragEnable = params.mType == 1;
        if (!isDragEnable && this.mTouching) {
            setInTouch(false);
        }
        this.mViewSeekbar.setEnabled(isDragEnable);
        this.mViewParams = params;
        this.mScreenId = params.mScreenId;
        setProgress(params.mProgress, params.mProgressMin, params.mProgressMax);
        setTitle(params.mTitle);
        Drawable icon = params.mIcon != null ? params.mIcon.loadDrawable(getContext()) : null;
        setIcon(icon);
    }

    public int getScreenId() {
        return this.mScreenId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XScrollView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            apply(this.mViewParams);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        Logger.d(TAG, "onFinishInflate");
    }
}
