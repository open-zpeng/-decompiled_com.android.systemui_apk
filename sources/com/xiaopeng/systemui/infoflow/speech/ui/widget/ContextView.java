package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
/* loaded from: classes24.dex */
public class ContextView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = ContextView.class.getSimpleName();
    private TextView mTextView;

    public ContextView(Context context) {
        super(context);
    }

    public ContextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTextView = (TextView) findViewById(R.id.tv_context);
    }

    public void updateContextText(String s) {
        this.mTextView.setText(s);
    }
}
