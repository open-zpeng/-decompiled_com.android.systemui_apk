package com.xiaopeng.systemui.infoflow.speech;

import android.text.TextUtils;
import android.widget.TextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class TtsEchoView implements ITtsEchoView {
    private static final int AUTO_HIDE_TIMEOUT = 3000;
    private static final String TAG = "TtsEchoView";
    private final Runnable mAutoHideRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.TtsEchoView.1
        @Override // java.lang.Runnable
        public void run() {
            if (TtsEchoView.this.mTtsEchoTextView != null) {
                TtsEchoView.this.mTtsEchoTextView.setVisibility(8);
            }
        }
    };
    private int mLoc;
    private String mTtsEchoText;
    private TextView mTtsEchoTextView;

    public TtsEchoView(int loc) {
        this.mLoc = loc;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ITtsEchoView
    public void updateTtsEchoView(TextView ttsEchoView) {
        this.mTtsEchoTextView = ttsEchoView;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ITtsEchoView
    public void showTtsEcho(boolean show) {
        TextView textView = this.mTtsEchoTextView;
        if (textView != null) {
            textView.setVisibility(show ? 0 : 8);
            this.mTtsEchoTextView.removeCallbacks(this.mAutoHideRunnable);
            if (show) {
                this.mTtsEchoTextView.postDelayed(this.mAutoHideRunnable, 3000L);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ITtsEchoView
    public void setTtsEcho(final String text) {
        TextView textView;
        Logger.d(TAG, "setTtsEcho " + text);
        this.mTtsEchoText = text;
        showTtsEcho(TextUtils.isEmpty(text) ^ true);
        if (!TextUtils.isEmpty(text) && (textView = this.mTtsEchoTextView) != null) {
            textView.post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.TtsEchoView.2
                @Override // java.lang.Runnable
                public void run() {
                    TtsEchoView.this.mTtsEchoTextView.setText(text);
                }
            });
        }
    }
}
