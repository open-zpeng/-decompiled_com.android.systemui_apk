package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.jarvisproto.AsrEvent;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
/* loaded from: classes24.dex */
public class SpeechCardStack extends CardStack {
    private static final String TAG = SpeechCardStack.class.getSimpleName();
    ISpeechContext mSpeechContext;

    public SpeechCardStack(Context context) {
        super(context);
        this.mSpeechContext = new ISpeechContext() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechCardStack.1
            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onInputText(int sourceArea, String text, boolean isEof, boolean isInterrupted, boolean invalid) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onOutputText(String text) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onShowWidget(SpeechWidget widget) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListFocus(int index) {
                if (SpeechCardStack.this.getVisibility() == 0) {
                    SpeechCardStack.this.forceFocusItem(index, false);
                }
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListSelect(int index) {
                if (SpeechCardStack.this.getVisibility() == 0) {
                    SpeechCardStack.this.forceFocusItem(index, true);
                }
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetText(String text) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetRecommend(String text) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onBugReportBegin() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onBugReportEnd() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onSayWelcome(String data) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onAsrEvent(int event) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetCancel(String widgetId, String cancelWay) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListCancelFocus(int index) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onExitRecommendCard() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListExpend() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListFold() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListStopCountdown() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onAsrEvent(AsrEvent event) {
            }
        };
    }

    public SpeechCardStack(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSpeechContext = new ISpeechContext() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechCardStack.1
            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onInputText(int sourceArea, String text, boolean isEof, boolean isInterrupted, boolean invalid) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onOutputText(String text) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onShowWidget(SpeechWidget widget) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListFocus(int index) {
                if (SpeechCardStack.this.getVisibility() == 0) {
                    SpeechCardStack.this.forceFocusItem(index, false);
                }
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListSelect(int index) {
                if (SpeechCardStack.this.getVisibility() == 0) {
                    SpeechCardStack.this.forceFocusItem(index, true);
                }
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetText(String text) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetRecommend(String text) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onBugReportBegin() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onBugReportEnd() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onSayWelcome(String data) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onAsrEvent(int event) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetCancel(String widgetId, String cancelWay) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListCancelFocus(int index) {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onExitRecommendCard() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListExpend() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListFold() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onWidgetListStopCountdown() {
            }

            @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
            public void onAsrEvent(AsrEvent event) {
            }
        };
    }

    public void resetFocusStatus() {
        this.mFocusedPosition = -1;
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.CardStack
    public void forceFocusItem(final int index) {
        RecyclerView.SmoothScroller smoothScroller = createSmoothScroller(index - 1);
        this.mLinearLayoutManager.startSmoothScroll(smoothScroller);
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechCardStack.2
            @Override // java.lang.Runnable
            public void run() {
                SpeechCardStack.this.focusItem(index - 1);
            }
        }, 250L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SpeechManager.instance().getSpeechContextManager().addCallback(this.mSpeechContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.XRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpeechManager.instance().getSpeechContextManager().removeCallback(this.mSpeechContext);
    }
}
