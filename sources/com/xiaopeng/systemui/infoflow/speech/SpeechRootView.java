package com.xiaopeng.systemui.infoflow.speech;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechView;
/* loaded from: classes24.dex */
public class SpeechRootView extends RelativeLayout {
    private static final String ACTION_NAVI_SPEECH_TEST = "com.android.systemui.TEST_SPEECH";
    private static final String TAG = "SpeechRootView";
    protected SpeechView mSpeechView;

    public SpeechRootView(Context context) {
        this(context, null);
    }

    public SpeechRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SpeechRootView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void showSpeechView() {
        if (this.mSpeechView == null) {
            LayoutInflater.from(this.mContext).inflate(R.layout.view_speech, (ViewGroup) this, true);
            this.mSpeechView = (SpeechView) findViewById(R.id.view_speech);
        }
    }

    public void showListWidget(SpeechWidget widget) {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.showListWidget(widget);
        }
    }

    public void onWidgetListFocus(int index) {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onWidgetListFocus(index);
        }
    }

    public void onWidgetListSelect(int index) {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onWidgetListSelect(index);
        }
    }

    public void onBugReportBegin() {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onBugReportBegin();
        }
    }

    public void onBugReportEnd() {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onBugReportEnd();
        }
    }

    public void onWidgetListCancelFocus(int index) {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onWidgetListCancelFocus(index);
        }
    }

    public void startDialog(int type) {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.startDialog(type);
        }
    }

    public void endDialog() {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.endDialog();
        }
    }

    public void onWidgetListStopCountdown() {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onWidgetListStopCountdown();
        }
    }

    public void onWidgetListExpend() {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onWidgetListExpend();
        }
    }

    public void onWidgetListFold() {
        SpeechView speechView = this.mSpeechView;
        if (speechView != null) {
            speechView.onWidgetListFold();
        }
    }
}
