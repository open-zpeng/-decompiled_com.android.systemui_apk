package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class SpeechView extends RelativeLayout {
    private static final String TAG = SpeechView.class.getSimpleName();
    private AnimationHelper mAnimationHelper;
    private boolean mCreateNavi;
    private ResultView mResultView;
    private ImageView mStopDialogImg;
    private String mWidgetType;

    public SpeechView(Context context) {
        super(context);
        this.mWidgetType = "";
    }

    public SpeechView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWidgetType = "";
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mStopDialogImg = (ImageView) findViewById(R.id.img_stop_dialog);
        this.mResultView = (ResultView) findViewById(R.id.view_speech_result);
        this.mStopDialogImg.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SpeechClient.instance().getWakeupEngine().stopDialog();
            }
        });
        this.mAnimationHelper = new AnimationHelper(this.mContext);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAnimationHelper.destroyAnimation();
        this.mWidgetType = "";
    }

    public void showListWidget(SpeechWidget widget) {
        Logger.d(TAG, "showListWidget");
        showSpeechView(widget);
    }

    private void showSpeechView(SpeechWidget widget) {
        this.mResultView.setListData(widget);
        String widgetType = widget.getExtraType();
        String str = TAG;
        Logger.i(str, "showSpeechView : mWidgetType = " + this.mWidgetType + " widgetType = " + widgetType);
        if (widgetType != null && widgetType.equals(this.mWidgetType)) {
            this.mResultView.setVisibility(0);
            return;
        }
        this.mWidgetType = widgetType;
        this.mAnimationHelper.showCard(this.mResultView);
    }

    public void startDialog(int type) {
        if (!this.mCreateNavi) {
            this.mResultView.clearContentContainer();
            Logger.d(TAG, "bb_startDialog: ");
        }
        if (type == 0 || type == 1) {
            this.mCreateNavi = true;
        }
    }

    public void endDialog() {
        this.mResultView.clearContentContainer();
        this.mResultView.setVisibility(8);
        this.mResultView.resetChildrenView();
        this.mCreateNavi = false;
    }

    public void showCaracView() {
        Logger.d(TAG, "showCaracView");
        this.mResultView.setVisibility(0);
        this.mResultView.showCaracView();
    }

    public void showCtrlCard(int groupType, String data, CardValue cardValue) {
        String str = TAG;
        Logger.d(str, "showCtrlCard() called with: groupType = [" + groupType + "], data = [" + data + "],cardValue=[" + cardValue.toString() + NavigationBarInflaterView.SIZE_MOD_END);
        showCtrlView(groupType, cardValue);
    }

    private void showCtrlView(int groupType, CardValue cardValue) {
        this.mResultView.showCtrlView(groupType, cardValue);
        this.mAnimationHelper.showCard(this.mResultView);
    }

    public void onBugReportBegin() {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onBugReportBegin();
        }
    }

    public void onBugReportEnd() {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onBugReportEnd();
        }
    }

    public void exitCtrlCard() {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.exitCtrlCard();
        }
    }

    public void onIcmBrightnessChanged(int value) {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onIcmBrightnessChanged(value);
        }
    }

    public void onScreenBrightnessChanged(int value) {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onScreenBrightnessChanged(value);
        }
    }

    public void onWidgetListFocus(int index) {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onWidgetListFocus(index);
        }
    }

    public void onWidgetListSelect(int index) {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onWidgetListSelect(index);
        }
    }

    public void onWidgetListCancelFocus(int index) {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onWidgetListCancelFocus(index);
        }
    }

    public void onWidgetListExpend() {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onWidgetListExpend();
        }
    }

    public void onWidgetListFold() {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onWidgetListFold();
        }
    }

    public void onWidgetListStopCountdown() {
        ResultView resultView = this.mResultView;
        if (resultView != null) {
            resultView.onWidgetListStopCountdown();
        }
    }
}
