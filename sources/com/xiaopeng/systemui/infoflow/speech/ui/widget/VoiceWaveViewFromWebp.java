package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.DrawableRes;
import com.android.systemui.R;
import com.xiaopeng.iotlib.utils.WebpUtils;
import com.xiaopeng.libtheme.ThemeManager;
/* loaded from: classes24.dex */
public class VoiceWaveViewFromWebp extends BaseVoiceWaveView {
    private static final String TAG = VoiceWaveViewFromWebp.class.getSimpleName();
    private static final int WAVE_LOC_LEFT = 1;
    private static final int WAVE_LOC_MID = 0;
    private static final int WAVE_LOC_RIGHT = 2;
    @DrawableRes
    private int mVoiceWaveId;
    private ImageView mVoiceWaveView;
    private int mWaveLocation;

    public VoiceWaveViewFromWebp(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWaveLocation = 1;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VoiceWaveView, 0, 0);
        this.mWaveLocation = a.getInteger(4, this.mWaveLocation);
        this.mVoiceWaveView = new ImageView(context);
        this.mVoiceWaveView.setVisibility(0);
        setVisibility(0);
        this.mVoiceWaveView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        addView(this.mVoiceWaveView);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim00() {
        this.mVoiceWaveId = getWebpViewIdType1();
        WebpUtils.loadWebp(this.mVoiceWaveView, this.mVoiceWaveId);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim01() {
        this.mVoiceWaveId = getWebpViewIdType2();
        WebpUtils.loadWebp(this.mVoiceWaveView, this.mVoiceWaveId);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim12() {
        this.mVoiceWaveId = getWebpViewIdType3();
        WebpUtils.loadWebp(this.mVoiceWaveView, this.mVoiceWaveId);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim20() {
        this.mVoiceWaveId = -1;
        WebpUtils.destroy(this.mVoiceWaveView);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnimHide() {
        Log.d(TAG, "webp destroy");
        this.mVoiceWaveId = -1;
        WebpUtils.destroy(this.mVoiceWaveView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    public void stopAnim() {
        super.stopAnim();
        this.mVoiceWaveId = -1;
        WebpUtils.destroy(this.mVoiceWaveView);
    }

    private int getWebpViewIdType1() {
        Log.d(TAG, "Type 1");
        if (this.mWaveLocation == 0) {
            return R.drawable.voicewave_global_1_fast;
        }
        return R.drawable.voicewave_corner_1_fast;
    }

    private int getWebpViewIdType2() {
        Log.d(TAG, "Type 2");
        if (this.mWaveLocation == 0) {
            return R.drawable.voicewave_global_1_slow;
        }
        return R.drawable.voicewave_corner_1_slow;
    }

    private int getWebpViewIdType3() {
        Log.d(TAG, "Type 3");
        if (this.mWaveLocation == 0) {
            return R.drawable.voicewave_global_2_fast;
        }
        return R.drawable.voicewave_corner_2_fast;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig) && this.mVoiceWaveId != -1) {
            Log.d(TAG, "Theme changed.");
            WebpUtils.loadWebp(this.mVoiceWaveView, this.mVoiceWaveId);
        }
    }
}
