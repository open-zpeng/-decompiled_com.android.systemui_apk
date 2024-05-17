package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
/* loaded from: classes24.dex */
public class VoiceWaveViewContainer extends RelativeLayout {
    private static final String TAG = "VoiceWaveViewContainer";
    public static final int VOICE_WAVE_TYPE_00 = 0;
    public static final int VOICE_WAVE_TYPE_01 = 1;
    public static final int VOICE_WAVE_TYPE_12 = 2;
    public static final int VOICE_WAVE_TYPE_20 = 3;
    public static final int VOICE_WAVE_TYPE_INVALID = -1;
    private ViewGroup mAsrContainerLeft;
    private ViewGroup mAsrContainerRight;
    private BaseVoiceWaveView mVoiceWaveLeft;
    private BaseVoiceWaveView mVoiceWaveMid;
    private BaseVoiceWaveView mVoiceWaveRight;

    public VoiceWaveViewContainer(Context context) {
        super(context);
    }

    public VoiceWaveViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceWaveViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mVoiceWaveLeft = (BaseVoiceWaveView) findViewById(R.id.left_voice_wave);
        this.mVoiceWaveMid = (BaseVoiceWaveView) findViewById(R.id.mid_voice_wave);
        this.mVoiceWaveRight = (BaseVoiceWaveView) findViewById(R.id.right_voice_wave);
        this.mAsrContainerLeft = (ViewGroup) findViewById(R.id.left_asr_container);
        this.mAsrContainerRight = (ViewGroup) findViewById(R.id.right_asr_container);
    }

    public void showVoiceWaveAnim(int regionType, int voiceWaveType, int volume) {
        if (CarModelsManager.getFeature().isOldAsr()) {
            regionType = SpeechClient.instance().getSoundLockState().getDriveSoundLocation();
        }
        BaseVoiceWaveView view = getView(regionType);
        if (view == null) {
            return;
        }
        view.showAnim(voiceWaveType, volume);
    }

    public BaseVoiceWaveView getView(int regionType) {
        if (regionType != 0) {
            if (regionType != 1) {
                if (regionType == 2) {
                    return this.mVoiceWaveRight;
                }
                return null;
            }
            return this.mVoiceWaveLeft;
        }
        return this.mVoiceWaveMid;
    }

    public ViewGroup getAsrContainer(int regionType) {
        if (regionType != 1) {
            if (regionType == 2) {
                return this.mAsrContainerRight;
            }
            return null;
        }
        return this.mAsrContainerLeft;
    }

    public void stopVoiceWaveAnim() {
        this.mVoiceWaveLeft.stopAnim();
        this.mVoiceWaveMid.stopAnim();
        this.mVoiceWaveRight.stopAnim();
    }

    public void stopVoiceWaveAnim(int type) {
        if (type == 0) {
            this.mVoiceWaveMid.stopAnim();
        } else if (type == 1) {
            this.mVoiceWaveLeft.stopAnim();
        } else if (type == 2) {
            this.mVoiceWaveRight.stopAnim();
        }
    }
}
