package com.xiaopeng.systemui.infoflow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ui.ISpeechUI;
import com.xiaopeng.systemui.controller.ui.SystemUIController;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.helper.SpeechHintHelper;
import com.xiaopeng.systemui.infoflow.helper.TtsEchoHelper;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.NgpWarningView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewContainer;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xuimanager.utils.LogUtil;
/* loaded from: classes24.dex */
public class VuiViewParent {
    private static final int BOTTOM_LAYER = 2;
    private static final String TAG = "VuiViewParent";
    private static final int TOP_LAYER = 1;
    private RelativeLayout mBottomLeftAsrContainer;
    private RelativeLayout mBottomMidAsrContainer;
    private RelativeLayout mBottomRightAsrContainer;
    private Context mContext;
    private NgpWarningView mNgpWarningView;
    private RelativeLayout mTopLeftAsrContainer;
    private VoiceWaveViewContainer mTopVoiceWaveViewContainer;
    private WindowManager mWindowManager;
    private int mLastVoiceWaveRegion = -1;
    private int mAsrLoc = -1;

    public VuiViewParent(Context context, WindowManager windowManager) {
        this.mContext = context;
        this.mWindowManager = windowManager;
        SystemUIController.get().getISpeechUI().addSpeechUICallBack(new ISpeechUI.ISpeechUICallBack() { // from class: com.xiaopeng.systemui.infoflow.VuiViewParent.1
            @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI.ISpeechUICallBack
            public void onSpeechUIEnableChanged(boolean enable) {
                if (!enable) {
                    VuiViewParent.this.removeVoiceWaveLayer();
                }
            }
        });
    }

    public void checkToAddTopVoiceWaveViewContainer() {
        VoiceWaveViewContainer voiceWaveViewContainer = this.mTopVoiceWaveViewContainer;
    }

    private void checkToAddTopLeftAsrContainer() {
        RelativeLayout relativeLayout = this.mTopLeftAsrContainer;
    }

    public void checkToAddBottomLeftVoiceWaveViewContainer() {
        RelativeLayout relativeLayout = this.mBottomLeftAsrContainer;
    }

    public void checkToAddBottomMidVoiceWaveViewContainer() {
        RelativeLayout relativeLayout = this.mBottomMidAsrContainer;
    }

    public void checkToAddBottomRightVoiceWaveViewContainer() {
        RelativeLayout relativeLayout = this.mBottomRightAsrContainer;
    }

    public void showTopLeftAsrBackground(boolean visible) {
        Logger.d(TAG, "showTopLeftAsrBackground " + visible);
        if (enableAsr()) {
            if (visible) {
                checkToAddTopLeftAsrContainer();
            } else {
                removeTopAsrContainer();
            }
            RelativeLayout relativeLayout = this.mTopLeftAsrContainer;
            if (relativeLayout != null) {
                XImageView bgAsr = (XImageView) relativeLayout.findViewById(R.id.iv_left_asr_bg);
                setAsrBgClickListener(bgAsr);
            }
        }
    }

    private boolean enableAsr() {
        return CarModelsManager.getFeature().isMultiplayerVoiceSupport() && SystemUIController.get().getISpeechUI().isSpeechUIEnable();
    }

    public void showTopRightAsrBackground(boolean visible) {
        if (enableAsr() && visible) {
            checkToAddTopVoiceWaveViewContainer();
        }
    }

    public void showBottomLeftAsrBackground(boolean visible) {
        if (enableAsr() && visible) {
            checkToAddBottomLeftVoiceWaveViewContainer();
        }
    }

    public void showBottomMidAsrBackground(boolean visible) {
        if (enableAsr() && visible) {
            checkToAddBottomMidVoiceWaveViewContainer();
        }
    }

    public void showBottomRightAsrBackground(boolean visible) {
        if (enableAsr() && visible) {
            checkToAddBottomRightVoiceWaveViewContainer();
        }
    }

    private void setAsrBgClickListener(XImageView bgAsr) {
        if (bgAsr != null) {
            bgAsr.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.VuiViewParent.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SpeechClient.instance().getWakeupEngine().stopDialog();
                }
            });
        }
    }

    public static int getRelativeVoiceRegionType(int regionType) {
        if (regionType != 3) {
            if (regionType != 4) {
                if (regionType == 999) {
                    return 0;
                }
                return regionType;
            }
            return 2;
        }
        return 1;
    }

    private void stopVoiceWaveAnim(VoiceWaveViewContainer voiceWaveViewContainer) {
        if (voiceWaveViewContainer != null) {
            voiceWaveViewContainer.stopVoiceWaveAnim();
            this.mWindowManager.removeViewImmediate(voiceWaveViewContainer);
        }
    }

    public void startNgpWarningAnim() {
        Logger.d(TAG, "startNgpWarningAnim");
        if (this.mNgpWarningView == null) {
            this.mNgpWarningView = WindowHelper.addNgpWarningView(this.mContext, this.mWindowManager);
        }
        this.mNgpWarningView.startWarningAnim();
    }

    public void stopNgpWarningAnim() {
        Logger.d(TAG, "stopNgpWarningAnim");
        NgpWarningView ngpWarningView = this.mNgpWarningView;
        if (ngpWarningView != null) {
            ngpWarningView.stopWarningAnim();
            this.mWindowManager.removeViewImmediate(this.mNgpWarningView);
            this.mNgpWarningView = null;
        }
    }

    public void endAsrLoc(int arsLoc) {
        showAsrBackground(arsLoc, false);
    }

    public void setAsrLoc(int asrLoc) {
        RelativeLayout relativeLayout;
        showAsrBackground(asrLoc, true);
        ViewGroup asrContainer = null;
        TextView ttsEchoView = null;
        if (asrLoc == 1) {
            RelativeLayout relativeLayout2 = this.mTopLeftAsrContainer;
            if (relativeLayout2 != null) {
                asrContainer = (ViewGroup) relativeLayout2.findViewById(R.id.left_asr_container);
                ttsEchoView = (TextView) this.mTopLeftAsrContainer.findViewById(R.id.tv_tts_echo);
                View hintContainer = this.mTopLeftAsrContainer.findViewById(R.id.left_hint_container);
                TextView hint = (TextView) this.mTopLeftAsrContainer.findViewById(R.id.tv_hint);
                SpeechHintHelper.getInstance().updateHintView(hint);
                if (hintContainer != null) {
                    hintContainer.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.VuiViewParent.3
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            SpeechClient.instance().getWakeupEngine().stopDialog();
                        }
                    });
                }
            }
        } else if (asrLoc == 2) {
            VoiceWaveViewContainer voiceWaveViewContainer = this.mTopVoiceWaveViewContainer;
            if (voiceWaveViewContainer != null) {
                asrContainer = voiceWaveViewContainer.getAsrContainer(asrLoc);
                ttsEchoView = (TextView) this.mTopVoiceWaveViewContainer.findViewById(R.id.tv_right_ts_echo);
            }
        } else if (asrLoc == 3) {
            RelativeLayout relativeLayout3 = this.mBottomLeftAsrContainer;
            if (relativeLayout3 != null) {
                asrContainer = (ViewGroup) relativeLayout3.findViewById(R.id.left_asr_container);
                ttsEchoView = (TextView) this.mBottomLeftAsrContainer.findViewById(R.id.tv_left_ts_echo);
            }
        } else if (asrLoc == 4) {
            RelativeLayout relativeLayout4 = this.mBottomRightAsrContainer;
            if (relativeLayout4 != null) {
                asrContainer = (ViewGroup) relativeLayout4.findViewById(R.id.right_asr_container);
                ttsEchoView = (TextView) this.mBottomRightAsrContainer.findViewById(R.id.tv_right_ts_echo);
            }
        } else if (asrLoc == 999 && (relativeLayout = this.mBottomMidAsrContainer) != null) {
            asrContainer = (ViewGroup) relativeLayout.findViewById(R.id.mid_asr_container);
        }
        if (asrContainer != null) {
            AsrHelper.getInstance().updateAsrContainer(asrLoc, asrContainer);
        }
        if (ttsEchoView != null) {
            TtsEchoHelper.getInstance().updateTtsEchoContainer(asrLoc, ttsEchoView);
        }
    }

    public void showAsrBackground(int asrLoc, boolean visible) {
        Logger.d(TAG, "and-showAsrBackground " + visible + " , " + asrLoc);
        if (asrLoc == 1) {
            showTopLeftAsrBackground(visible);
        } else if (asrLoc == 2) {
            showTopRightAsrBackground(visible);
        } else if (asrLoc == 3) {
            showBottomLeftAsrBackground(visible);
        } else if (asrLoc == 4) {
            showBottomRightAsrBackground(visible);
        } else if (asrLoc == 999) {
            showBottomMidAsrBackground(visible);
        }
    }

    public void removeVoiceWaveLayer() {
        removeTopVoiceLayer();
        removeTopAsrContainer();
        removeBottomLeftVoiceLayer();
        removeBottomMidVoiceLayer();
        removeBottomRightVoiceLayer();
    }

    private void removeTopAsrContainer() {
        Logger.d(TAG, "removeTopAsrContainer ");
        RelativeLayout relativeLayout = this.mTopLeftAsrContainer;
        if (relativeLayout != null && relativeLayout.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mTopLeftAsrContainer);
            this.mTopLeftAsrContainer = null;
        }
    }

    private void removeTopVoiceLayer() {
        VoiceWaveViewContainer voiceWaveViewContainer = this.mTopVoiceWaveViewContainer;
        if (voiceWaveViewContainer != null && voiceWaveViewContainer.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mTopVoiceWaveViewContainer);
            this.mTopVoiceWaveViewContainer = null;
        }
    }

    private void removeBottomLeftVoiceLayer() {
        RelativeLayout relativeLayout = this.mBottomLeftAsrContainer;
        if (relativeLayout != null && relativeLayout.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mBottomLeftAsrContainer);
            this.mBottomLeftAsrContainer = null;
        }
    }

    private void removeBottomMidVoiceLayer() {
        RelativeLayout relativeLayout = this.mBottomMidAsrContainer;
        if (relativeLayout != null && relativeLayout.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mBottomMidAsrContainer);
            this.mBottomMidAsrContainer = null;
        }
    }

    private void removeBottomRightVoiceLayer() {
        RelativeLayout relativeLayout = this.mBottomRightAsrContainer;
        if (relativeLayout != null && relativeLayout.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mBottomRightAsrContainer);
            this.mBottomRightAsrContainer = null;
        }
    }

    public void showAsrBackground(boolean visible) {
        LogUtil.d(TAG, "showAsrBackground = " + visible + " mAsrLoc " + this.mAsrLoc);
        showAsrBackground(this.mAsrLoc, visible);
    }
}
