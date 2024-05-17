package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
/* loaded from: classes24.dex */
public class BaikeView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = BaikeView.class.getSimpleName();
    private TextView mContent;
    private TextView mTitle;

    public BaikeView(Context context) {
        super(context);
    }

    public BaikeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTitle = (TextView) findViewById(R.id.tv_title);
        this.mContent = (TextView) findViewById(R.id.tv_content);
    }

    public void setTitle(String title) {
        this.mTitle.setText(title);
    }

    public void setContent(String content) {
        this.mContent.setText(content);
    }
}
