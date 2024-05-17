package com.xiaopeng.systemui.infoflow.message.presenter;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class SmartCardPresenter extends BaseCardPresenter {
    private static final String TAG = "SmartCardPresenter";
    private String mTtsId;
    private Runnable mTtsWakeupAction = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.SmartCardPresenter.1
        @Override // java.lang.Runnable
        public void run() {
            if (!TextUtils.isEmpty(SmartCardPresenter.this.mCardData.content)) {
                SmartCardPresenter smartCardPresenter = SmartCardPresenter.this;
                smartCardPresenter.mTtsId = smartCardPresenter.speak(smartCardPresenter.mCardData.content);
            }
            SmartCardPresenter.this.mTtsWakeupAction = null;
        }
    };
    private Handler mHandler = new Handler();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final SmartCardPresenter sInstance = new SmartCardPresenter();

        private SingleHolder() {
        }
    }

    public static SmartCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    protected SmartCardPresenter() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 28;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
        Runnable runnable = this.mTtsWakeupAction;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
        }
        String str = this.mTtsId;
        if (str != null) {
            shutup(str);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        if (TextUtils.isEmpty(this.mCardData.action)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(this.mCardData.action);
        intent.setFlags(16777216);
        try {
            this.mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Logger.d(TAG, "send broadcast failed");
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String speak(String tts) {
        if (!TextUtils.isEmpty(tts)) {
            return TextToSpeechHelper.instance().speak(tts, new TextToSpeechHelper.ISpeakCallback() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.SmartCardPresenter.2
                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onStart(String utteranceId) {
                }

                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onEnd(String utteranceId) {
                }

                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onStop(String name) {
                }

                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onError(String utteranceId) {
                }
            });
        }
        return null;
    }

    private void shutup(String ttsId) {
        if (!TextUtils.isEmpty(ttsId)) {
            TextToSpeechHelper.instance().stop();
        }
    }
}
