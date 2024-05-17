package com.xiaopeng.systemui.infoflow.speech;

import android.text.TextUtils;
import com.xiaopeng.systemui.controller.ActivityController2;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.message.listener.XNotificationListener;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class LandscapeSpeechPresenter extends SpeechPresenter implements ActivityController2.OnTopWindowChangedListener {
    public LandscapeSpeechPresenter() {
        ActivityController2.get().addOnTopWindowChangedListener(this);
    }

    @Override // com.xiaopeng.systemui.controller.ActivityController2.OnTopWindowChangedListener
    public void onTopWindowChanged(int screenId, int type) {
        if (screenId != ActivityController2.ID_SHARED_PRIMARY_REDEFINE && ActivityController2.ID_SHARED_PRIMARY_REDEFINE != -1) {
            return;
        }
        mIsPanelVisible = isPanelVisible(type);
        AIAvatarViewServiceHelper.instance().updatePanelVisibleStatus(mIsPanelVisible);
    }

    protected boolean isPanelVisible(int viewType) {
        return viewType == 2 || viewType == 3 || viewType == 4;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechPresenter, com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onSayWelcome(String data) {
        super.onSayWelcome(data);
        if (!TextUtils.isEmpty(data)) {
            AsrHelper.getInstance().setRecommendData(data);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechPresenter
    public void enterSpeechMode(int type) {
        super.enterSpeechMode(type);
        XNotificationListener.getInstance(this.mContext).onExitRecommendMode();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechPresenter, com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onExitRecommendCard() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.LandscapeSpeechPresenter.1
            @Override // java.lang.Runnable
            public void run() {
                AsrHelper.getInstance().hideVuiRecommendView();
            }
        });
    }
}
