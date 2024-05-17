package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public abstract class BaseVoiceWaveView extends RelativeLayout {
    private static final String TAG = "VoiceWaveView";
    private int mType;

    protected abstract void startAnim00();

    protected abstract void startAnim01();

    protected abstract void startAnim12();

    protected abstract void startAnim20();

    protected abstract void startAnimHide();

    public BaseVoiceWaveView(Context context) {
        super(context);
        this.mType = -1;
    }

    public BaseVoiceWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mType = -1;
    }

    public BaseVoiceWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mType = -1;
    }

    public void showAnim(int type, int volume) {
        Logger.i(TAG, "showAnim " + type + " , mType: " + this.mType + " -- " + hashCode());
        if (this.mType == type) {
            return;
        }
        this.mType = type;
        if (type == 0) {
            startAnim00();
        } else if (type == 1) {
            startAnim01();
        } else if (type == 2) {
            startAnim12();
        } else {
            startAnimHide();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopAnim() {
        Logger.i(TAG, "stopAnim " + hashCode());
        this.mType = -1;
    }
}
