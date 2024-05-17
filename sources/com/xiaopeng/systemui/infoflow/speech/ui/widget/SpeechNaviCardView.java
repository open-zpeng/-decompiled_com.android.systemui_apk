package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.xiaopeng.systemui.infoflow.theme.XRelativeLayout;
import com.xiaopeng.systemui.infoflow.widget.IFocusView;
/* loaded from: classes24.dex */
public class SpeechNaviCardView extends XRelativeLayout implements IFocusView {
    private static final String TAG = "SpeechNaviCardView";
    protected IFocusView.OnFocusChangedListener mOnFocusChangedListener;

    public SpeechNaviCardView(Context context) {
        super(context);
    }

    public SpeechNaviCardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setFocused(boolean b) {
        setFocused(b, true);
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setPreFocused(boolean preFocused) {
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setOnFocusChangedListener(IFocusView.OnFocusChangedListener onFocusChangedListener) {
        this.mOnFocusChangedListener = onFocusChangedListener;
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setFocused(boolean focused, boolean triggerListener) {
        IFocusView.OnFocusChangedListener onFocusChangedListener;
        setSelected(focused);
        if (triggerListener && (onFocusChangedListener = this.mOnFocusChangedListener) != null) {
            onFocusChangedListener.onFocusedChanged(focused);
        }
        IFocusView.OnFocusChangedListener onFocusChangedListener2 = this.mOnFocusChangedListener;
        if (onFocusChangedListener2 != null) {
            onFocusChangedListener2.onFocusChangedForViewUpdate(focused);
        }
    }
}
