package com.xiaopeng.systemui.infoflow.speech;

import android.text.TextUtils;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class VerticalSpeechPresenter extends SpeechPresenter implements ActivityController.OnTopViewTypeChangedListener {
    private static final String TAG = "VerticalSpeechPresenter";

    public VerticalSpeechPresenter() {
        ActivityController.getInstance(this.mContext).addPanelVisibleChangeListener(this);
    }

    @Override // com.xiaopeng.systemui.controller.ActivityController.OnTopViewTypeChangedListener
    public void onTopViewTypeChanged(int viewType, boolean isLauncher) {
        Logger.d(TAG, "onTopViewTypeChanged : " + viewType + " isLauncher = " + isLauncher);
        mIsPanelVisible = isPanelVisible(viewType, isLauncher);
        if (this.mSpeechViewContainer != null) {
            this.mSpeechViewContainer.onTopViewTypeChanged(viewType, isLauncher);
        }
    }

    protected boolean isPanelVisible(int viewType, boolean isLauncher) {
        return viewType == 2 || (viewType == 3 && !isLauncher) || viewType == 4;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechPresenter, com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onListeningStatusChanged(boolean listeningStatus) {
        if (this.mSpeechViewContainer != null) {
            this.mSpeechViewContainer.onListeningStatusChanged(listeningStatus);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechPresenter, com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onShowWidget(SpeechWidget listWidget) {
        super.onShowWidget(listWidget);
        onListeningStatusChanged(false);
        if (this.mSpeechViewContainer != null) {
            if (ListWidget.EXTRA_TYPE_NAVI_ROUTE.equals(listWidget.getExtraType()) || "navi".equals(listWidget.getExtraType())) {
                this.mSpeechViewContainer.showSpeechBackground(false);
            } else {
                this.mSpeechViewContainer.showSpeechBackground(true);
            }
            this.mSpeechViewContainer.notifyAvatarAction(2);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechPresenter
    protected void showRecommendView(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        RecommendBean recommendBean = AsrHelper.getInstance().setRecommendData(data);
        AsrHelper.getInstance().showVuiRecommendView(false);
        if (recommendBean != null && this.mSpeechViewContainer != null) {
            this.mSpeechViewContainer.updateSceneType(recommendBean.getCardType());
        }
    }
}
