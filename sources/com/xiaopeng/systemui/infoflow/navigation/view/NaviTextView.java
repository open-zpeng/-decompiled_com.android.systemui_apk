package com.xiaopeng.systemui.infoflow.navigation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class NaviTextView extends AnimatedTextView {
    private static final String TAG = "NaviTextView";
    private String mLabelString;
    private boolean mRequestLog;

    public NaviTextView(Context context) {
        this(context, null);
    }

    public NaviTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NaviTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NaviTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mLabelString = "";
        this.mRequestLog = false;
    }

    @Override // android.widget.TextView
    public void setText(CharSequence text, TextView.BufferType type) {
        log(" set text:" + ((Object) text));
        if (getLayout() == null && !TextUtils.isEmpty(getText())) {
            log("getLayout null and text not null");
            onPreDraw();
        }
        super.setText(text, type);
    }

    public void setContent(CharSequence content) {
        CharSequence currentText = getText();
        if (content.equals(currentText)) {
            log(" return as current text:" + ((Object) currentText));
            return;
        }
        setText(content);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        log(" onDraw:" + ((Object) getText()));
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public void requestLayout() {
        log(" requestLayout");
        super.requestLayout();
    }

    @Override // android.view.View
    public void invalidate() {
        log(" invalidate");
        super.invalidate();
    }

    @Override // android.view.View
    public void setTag(Object tag) {
        super.setTag(tag);
        this.mLabelString = getViewTag();
    }

    private String getViewTag() {
        if (getTag() != null) {
            return getTag().toString();
        }
        return "";
    }

    private void log(String log) {
        if (this.mRequestLog) {
            Logger.d(TAG, this.mLabelString + log);
        }
    }

    public void setRequestLog(boolean requestLog) {
        this.mRequestLog = requestLog;
    }
}
