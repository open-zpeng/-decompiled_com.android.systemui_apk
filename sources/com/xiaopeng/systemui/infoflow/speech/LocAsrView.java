package com.xiaopeng.systemui.infoflow.speech;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.utils.LogUtil;
/* loaded from: classes24.dex */
public class LocAsrView {
    private static final int ASR_INVALID_SPEECH_SHOW_TIME = 1000;
    public static final int ASR_LISTENING_ANIM_DELAY_TIME = 1000;
    public static final int ASR_LISTENING_ANIM_SHOW_TIME = 500;
    public static final int ASR_LISTENING_STATUS_SHOW_TIME = 3000;
    private static final int ASR_NETWORK_EXCEPTION_SHOW_TIME = 5000;
    private static final int ASR_NLU_LOADING_SHOW_TIME = 1000;
    private static final int MSG_TYPE_CLEAR_ASR_STATUS = 6;
    private static final int MSG_TYPE_HIDE_LISTENING_STATUS = 5;
    private static final int MSG_TYPE_START_FADE_IN_ANIM_FOR_LISTENING_STATUS_1 = 1;
    private static final int MSG_TYPE_START_FADE_IN_ANIM_FOR_LISTENING_STATUS_2 = 3;
    private static final int MSG_TYPE_START_FADE_OUT_ANIM_FOR_LISTENING_STATUS_1 = 2;
    private static final int MSG_TYPE_START_FADE_OUT_ANIM_FOR_LISTENING_STATUS_2 = 4;
    private static final int MSG_TYPE_UPDATE_ASR_TEXT = 0;
    private static final String TAG = "LocAsrView";
    private int mAsrLoc;
    private String mAsrText;
    protected IAsrView mAsrView;
    private int mAsrStatus = 0;
    private boolean mIsListening = false;
    private boolean mListeningAnimationPlayed = false;
    private boolean mIsAvatarSceneReplaced = false;
    private boolean mIsAvatarSceneRestored = true;
    private Handler mUIHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.LocAsrView.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                case 6:
                    AsrTextInfo asrTextInfo = (AsrTextInfo) msg.obj;
                    if (asrTextInfo != null) {
                        LocAsrView.this.showAsrText(asrTextInfo.getText());
                        return;
                    }
                    return;
                case 1:
                    LocAsrView.this.startFadeInAnimForListeningStatus1();
                    return;
                case 2:
                    LocAsrView.this.startFadeOutAnimForListeningStatus();
                    LocAsrView.this.mUIHandler.removeMessages(3);
                    LocAsrView.this.mUIHandler.sendEmptyMessageDelayed(3, 500L);
                    return;
                case 3:
                    LocAsrView.this.startFadeInAnimForListeningStatus2();
                    LocAsrView.this.delayToStartFadeOutAnimForListeningStatus2();
                    return;
                case 4:
                    LocAsrView.this.startFadeOutAnimForListeningStatus();
                    LocAsrView.this.mUIHandler.removeMessages(5);
                    LocAsrView.this.mUIHandler.sendEmptyMessageDelayed(5, 500L);
                    return;
                case 5:
                    LocAsrView.this.mAsrView.showAsr(false);
                    LocAsrView.this.showVuiRecommendView(true);
                    return;
                default:
                    return;
            }
        }
    };
    private Context mContext = ContextUtils.getContext();

    public LocAsrView(int asrLoc, IAsrView asrView) {
        this.mAsrLoc = asrLoc;
        this.mAsrView = asrView;
    }

    public int getAsrLoc() {
        return this.mAsrLoc;
    }

    public void setAsrLoc(int asrLoc) {
        this.mAsrLoc = asrLoc;
        this.mAsrView.setAsrLoc(asrLoc);
    }

    public String getAsrText() {
        return this.mAsrText;
    }

    public void setAsrText(String asrText) {
        this.mAsrText = asrText;
    }

    public int getAsrStatus() {
        return this.mAsrStatus;
    }

    public void setAsrStatus(int asrStatus) {
        this.mAsrStatus = asrStatus;
    }

    public IAsrView getAsrView() {
        return this.mAsrView;
    }

    public void setAsrView(IAsrView asrView) {
        this.mAsrView = asrView;
    }

    public void updateAsrContainer(ViewGroup asrContainer) {
        this.mAsrView.updateAsrContainer(asrContainer);
    }

    public void updateShowNormalText(String text, boolean invalid) {
        this.mAsrView.setAsrStatus(invalid ? 3 : 0);
        updateAsrStatus(0, this.mIsListening);
        updateShowText(text);
    }

    public void updateShowText(String text) {
        boolean isDmStarted = SpeechClient.instance().getSpeechState().isDMStarted();
        boolean isDMEndByPOISelect = SpeechClient.instance().getSpeechState().isDMEndByPOISelect();
        if (isDmStarted || isDMEndByPOISelect || text == null) {
            updateAsrTextInUIHandler(text);
        }
    }

    public void restoreAsrStatus() {
        this.mIsAvatarSceneReplaced = false;
        this.mIsAvatarSceneRestored = true;
        showAsrText(this.mAsrText);
        updateAsrStatus(this.mAsrStatus, this.mIsListening);
    }

    protected void showAsrText(String text) {
        LogUtil.d(TAG, "showAsrText : text = " + text);
        this.mAsrText = text;
        this.mAsrView.setAsrText(text);
        if (!CarModelsManager.getFeature().isOldAsr()) {
            if (TextUtils.isEmpty(text)) {
                checkAvatarGone();
            } else {
                checkAvatarShow();
            }
        }
        if (!CarModelsManager.getFeature().isMultiplayerVoiceSupport()) {
            checkToShowListeningStatus(this.mAsrText);
        }
        if (!TextUtils.isEmpty(this.mAsrText)) {
            hideVuiRecommendView();
        }
        PresenterCenter.getInstance().getInfoFlow().showAsrBackground(!TextUtils.isEmpty(this.mAsrText));
    }

    private void checkAvatarGone() {
        LogUtil.d(TAG, "checkAvatarGone : mIsPanelVisible = " + SpeechPresenter.mIsPanelVisible + " mIsAvatarSceneRestored = " + this.mIsAvatarSceneRestored);
        AIAvatarViewServiceHelper.instance().updateAsrStaus(false);
        if (SpeechPresenter.mIsPanelVisible && !this.mIsAvatarSceneRestored) {
            this.mIsAvatarSceneRestored = true;
            this.mIsAvatarSceneReplaced = false;
            AsrHelper.getInstance().getOnAvatarSceneChangeListener().onRestoreAvatarScene();
        }
    }

    private void checkAvatarShow() {
        LogUtil.d(TAG, "checkAvatarShow : mIsPanelVisible = " + SpeechPresenter.mIsPanelVisible + " mIsAvatarSceneReplaced = " + this.mIsAvatarSceneReplaced);
        AIAvatarViewServiceHelper.instance().updateAsrStaus(true);
        if (SpeechPresenter.mIsPanelVisible && !this.mIsAvatarSceneReplaced) {
            this.mIsAvatarSceneReplaced = true;
            this.mIsAvatarSceneRestored = false;
            AsrHelper.getInstance().getOnAvatarSceneChangeListener().onReplaceAvatarScene();
        }
    }

    public RecommendBean setRecommendData(String recommendData) {
        RecommendBean recommendBean = (RecommendBean) GsonUtil.fromJson(recommendData, (Class<Object>) RecommendBean.class);
        this.mAsrView.setRecommendData(recommendData);
        return recommendBean;
    }

    public void updateAsrListeningStatus(boolean isListening, boolean showListeningStatus) {
        Logger.d(TAG, "updateAsrListeningStatus== : mIsListening = " + this.mIsListening + ", " + showListeningStatus);
        this.mIsListening = isListening;
        if (showListeningStatus) {
            checkToShowListeningStatus(this.mAsrText);
        }
    }

    private void checkToShowListeningStatus(String text) {
        Logger.d(TAG, "and-checkToShowListeningStatus : " + text + " mIsListening " + this.mIsListening + " , mAsrStatus " + this.mAsrStatus);
        cancelListeningAnimation();
        if (this.mIsListening && TextUtils.isEmpty(text)) {
            showAsrStatus(7);
        } else {
            updateAsrStatus(this.mAsrStatus, this.mIsListening);
        }
    }

    private void cancelListeningAnimation() {
        LogUtil.d(TAG, "cancelListeningAnimation");
        this.mListeningAnimationPlayed = false;
        this.mUIHandler.removeMessages(1);
        this.mUIHandler.removeMessages(2);
        this.mUIHandler.removeMessages(3);
        this.mUIHandler.removeMessages(4);
        this.mUIHandler.removeMessages(5);
        this.mAsrView.clearAnimation();
    }

    public void updateAsrStatus(int status, boolean isListening) {
        LogUtil.d(TAG, "updateAsrStatus : status = " + status + " isListening = " + isListening);
        this.mAsrStatus = status;
        this.mIsListening = isListening;
        switch (status) {
            case 0:
            case 1:
            case 8:
            case 9:
            case 10:
                this.mAsrView.showAsrAnimation(status);
                return;
            case 2:
                if (isListening) {
                    showAsrStatus(2);
                    clearAsrStatus(5000L);
                }
                this.mAsrView.showAsrAnimation(status);
                return;
            case 3:
                showAsrStatus(3);
                clearAsrStatus(1000L);
                this.mAsrView.showAsrAnimation(status);
                return;
            case 4:
                this.mAsrView.setAsrStatus(1);
                this.mAsrView.showAsrAnimation(status);
                return;
            case 5:
                this.mAsrView.setAsrStatus(0);
                return;
            case 6:
                this.mAsrView.setAsrStatus(3);
                clearAsrStatus(1000L);
                return;
            case 7:
                showAsrStatus(7);
                return;
            default:
                return;
        }
    }

    private void showAsrStatus(int status) {
        if (status == 2 || status == 3 || status == 7) {
            this.mAsrView.setAsrStatus(2);
            this.mUIHandler.removeMessages(0);
        }
        if (!CarModelsManager.getFeature().isMultiplayerVoiceSupport()) {
            if (status == 2) {
                this.mAsrView.setAsrText(this.mContext.getString(R.string.infoflow_asr_network_exception));
            } else if (status != 3) {
                if (status == 7) {
                    startListeningAnimation();
                }
            } else {
                this.mAsrView.setAsrText(this.mContext.getString(R.string.infoflow_asr_timeout));
            }
        }
    }

    public void hideVuiRecommendView() {
        this.mAsrView.hideVuiRecommendView();
    }

    private void clearAsrStatus(long delayTime) {
        this.mUIHandler.removeMessages(6);
        Message msg = this.mUIHandler.obtainMessage(6);
        AsrTextInfo asrTextInfo = new AsrTextInfo();
        asrTextInfo.setText("");
        msg.obj = asrTextInfo;
        this.mUIHandler.sendMessageDelayed(msg, delayTime);
    }

    private void updateAsrTextInUIHandler(String text) {
        this.mUIHandler.removeMessages(6);
        Message msg = this.mUIHandler.obtainMessage(0);
        AsrTextInfo asrTextInfo = new AsrTextInfo();
        asrTextInfo.setText(text);
        msg.obj = asrTextInfo;
        this.mUIHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFadeInAnimForListeningStatus2() {
        this.mAsrView.fadeIn(this.mContext.getString(R.string.exit_speech));
    }

    protected void startListeningAnimation() {
        Logger.d(TAG, "startListeningAnimation : " + this.mListeningAnimationPlayed);
        if (this.mListeningAnimationPlayed) {
            return;
        }
        this.mListeningAnimationPlayed = true;
        hideVuiRecommendView();
        delayToStartFadeInAnimForListeningStatus1();
        delayToStartFadeOutAnimForListeningStatus1();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFadeInAnimForListeningStatus1() {
        this.mAsrView.fadeIn(this.mContext.getString(R.string.listening));
    }

    private void delayToStartFadeInAnimForListeningStatus1() {
        this.mUIHandler.removeMessages(1);
        this.mUIHandler.sendEmptyMessageDelayed(1, 1000L);
    }

    private void delayToStartFadeOutAnimForListeningStatus1() {
        this.mUIHandler.removeMessages(2);
        this.mUIHandler.sendEmptyMessageDelayed(2, 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFadeOutAnimForListeningStatus() {
        this.mAsrView.fadeOut();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void delayToStartFadeOutAnimForListeningStatus2() {
        this.mUIHandler.removeMessages(4);
        this.mUIHandler.sendEmptyMessageDelayed(4, 3000L);
    }

    public void showVuiRecommendView(boolean hasAnimation) {
        this.mAsrView.showVuiRecommendView(hasAnimation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class AsrTextInfo {
        private String mText;

        private AsrTextInfo() {
        }

        public String getText() {
            return this.mText;
        }

        public void setText(String text) {
            this.mText = text;
        }
    }
}
