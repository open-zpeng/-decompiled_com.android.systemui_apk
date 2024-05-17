package com.xiaopeng.systemui.infoflow.speech;

import android.content.Context;
import android.view.WindowManager;
import com.xiaopeng.systemui.controller.ui.ISpeechUI;
import com.xiaopeng.systemui.controller.ui.SystemUIController;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.VuiViewParent;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewContainer;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class VoiceWaveView implements IVoiceWavwView {
    private static final String TAG = VoiceWaveView.class.getSimpleName();
    private VoiceWaveViewContainer mBottomVoiceWaveContainer;
    private VoiceWaveViewContainer mTopVoiceWaveContainer;
    private final HashMap<Integer, Boolean> mIsShowVoiceWaveTop = new HashMap<>();
    private final HashMap<Integer, Boolean> mIsShowVoiceWaveBottom = new HashMap<>();
    protected Context mContext = ContextUtils.getContext();
    protected WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();

    public VoiceWaveView() {
        SystemUIController.get().getISpeechUI().addSpeechUICallBack(new ISpeechUI.ISpeechUICallBack() { // from class: com.xiaopeng.systemui.infoflow.speech.-$$Lambda$VoiceWaveView$0q-PXxPj3luRVRvQqan7mUemb9I
            @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI.ISpeechUICallBack
            public final void onSpeechUIEnableChanged(boolean z) {
                VoiceWaveView.this.lambda$new$0$VoiceWaveView(z);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$VoiceWaveView(boolean enable) {
        if (!enable) {
            Logger.d(TAG, "onSpeechUIEnableChanged stopVoiceWaveAnim ");
            stopVoiceWaveAnim();
        }
    }

    private boolean isEnable() {
        return SystemUIController.get().getISpeechUI().isSpeechUIEnable();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IVoiceWavwView
    public void showVoiceWaveAnim(int regionType, int voiceWaveType) {
        boolean isEnable = isEnable();
        String str = TAG;
        Logger.d(str, "showVoiceWaveAnim :  " + voiceWaveType + " , isEnable : " + isEnable);
        if (isEnable) {
            if (regionType == 1 || regionType == 2) {
                this.mIsShowVoiceWaveTop.put(Integer.valueOf(regionType), true);
                addTopVoiceWaveViewContainer();
                this.mTopVoiceWaveContainer.showVoiceWaveAnim(regionType, voiceWaveType, -1);
            } else if (regionType == 3 || regionType == 4 || regionType == 999) {
                this.mIsShowVoiceWaveBottom.put(Integer.valueOf(regionType), true);
                addBottomVoiceWaveViewContainer();
                this.mBottomVoiceWaveContainer.showVoiceWaveAnim(VuiViewParent.getRelativeVoiceRegionType(regionType), voiceWaveType, -1);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IVoiceWavwView
    public void stopVoiceWaveAnim(int regionType) {
        if (regionType == 1 || regionType == 2) {
            this.mIsShowVoiceWaveTop.put(Integer.valueOf(regionType), false);
            stopTopVoiceWaveAnimWithRegion(regionType);
        } else if (regionType == 3 || regionType == 4 || regionType == 999) {
            this.mIsShowVoiceWaveBottom.put(Integer.valueOf(regionType), false);
            stopBottomVoiceWaveAnimWithRegion(regionType);
        }
    }

    private void stopVoiceWaveAnim() {
        stopVoiceWaveAnim(1);
        stopVoiceWaveAnim(2);
        stopVoiceWaveAnim(3);
        stopVoiceWaveAnim(4);
        stopVoiceWaveAnim(999);
    }

    private void addTopVoiceWaveViewContainer() {
        if (this.mTopVoiceWaveContainer == null) {
            this.mTopVoiceWaveContainer = WindowHelper.addTopVoiceWaveViewOnly(this.mContext, this.mWindowManager);
        }
    }

    private void addBottomVoiceWaveViewContainer() {
        if (this.mBottomVoiceWaveContainer == null) {
            this.mBottomVoiceWaveContainer = WindowHelper.addBottomVoiceWaveViewOnly(this.mContext, this.mWindowManager);
        }
    }

    private void stopTopVoiceWaveAnimWithRegion(int regionType) {
        if (this.mTopVoiceWaveContainer != null) {
            if (!this.mIsShowVoiceWaveTop.containsValue(true)) {
                this.mWindowManager.removeView(this.mTopVoiceWaveContainer);
                this.mTopVoiceWaveContainer = null;
                return;
            }
            this.mTopVoiceWaveContainer.stopVoiceWaveAnim(regionType);
        }
    }

    private void stopBottomVoiceWaveAnimWithRegion(int regionType) {
        if (this.mBottomVoiceWaveContainer != null) {
            if (!this.mIsShowVoiceWaveBottom.containsValue(true)) {
                this.mWindowManager.removeView(this.mBottomVoiceWaveContainer);
                this.mBottomVoiceWaveContainer = null;
                return;
            }
            this.mBottomVoiceWaveContainer.stopVoiceWaveAnim(VuiViewParent.getRelativeVoiceRegionType(regionType));
        }
    }
}
