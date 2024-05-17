package com.xiaopeng.systemui.infoflow.speech;

import android.text.TextUtils;
import android.widget.TextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class SpeechHintView implements ISpeechHintView {
    private static final String TAG = "SpeechHintView";
    private String mHintText;
    private TextView mHintView;
    private boolean mShow;

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechHintView
    public void updateHintView(TextView hintView) {
        if (this.mHintView == null) {
            Logger.d(TAG, "updateHintView " + hintView);
        }
        this.mHintView = hintView;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechHintView
    public void showHint(boolean show) {
        Logger.d(TAG, "showHint " + show);
        this.mShow = show;
        updateHintVisibility();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechHintView
    public void setHintText(String text) {
        Logger.d(TAG, "setHintText " + text);
        this.mShow = true;
        this.mHintText = text;
        TextView textView = this.mHintView;
        if (textView != null) {
            textView.setText(this.mHintText);
        }
        updateHintVisibility();
    }

    private void updateHintVisibility() {
        if (this.mHintView != null) {
            boolean visible = this.mShow && !TextUtils.isEmpty(this.mHintText);
            this.mHintView.setVisibility(visible ? 0 : 8);
        }
    }
}
