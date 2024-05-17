package com.xiaopeng.systemui.infoflow.helper;

import android.widget.TextView;
import com.xiaopeng.systemui.infoflow.speech.ISpeechHintView;
import com.xiaopeng.systemui.infoflow.speech.SpeechHintView;
/* loaded from: classes24.dex */
public class SpeechHintHelper {
    private ISpeechHintView mSpeechHintView;

    public static final SpeechHintHelper getInstance() {
        return SingletonHolder.sInstance;
    }

    /* loaded from: classes24.dex */
    private static class SingletonHolder {
        private static final SpeechHintHelper sInstance = new SpeechHintHelper();

        private SingletonHolder() {
        }
    }

    private SpeechHintHelper() {
        this.mSpeechHintView = new SpeechHintView();
    }

    public void showHint(boolean show) {
        this.mSpeechHintView.showHint(show);
    }

    public void setHintText(String text) {
        this.mSpeechHintView.setHintText(text);
    }

    public void updateHintView(TextView view) {
        this.mSpeechHintView.updateHintView(view);
    }
}
