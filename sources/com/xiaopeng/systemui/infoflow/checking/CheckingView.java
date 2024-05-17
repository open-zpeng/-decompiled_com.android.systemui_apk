package com.xiaopeng.systemui.infoflow.checking;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.checking.bean.CheckInfo;
/* loaded from: classes24.dex */
public class CheckingView extends LinearLayout {
    private static final String TAG = CheckingView.class.getSimpleName();
    private TextView mCheckingContentTv;
    private SeekBar mCheckingProgressView;
    private TextView mCheckingTitleTv;

    public CheckingView(Context context) {
        this(context, null);
    }

    public CheckingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CheckingView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void setData(String title, String content, CheckInfo checkInfo) {
        this.mCheckingTitleTv.setText(title);
        this.mCheckingContentTv.setText(content);
        int progress = (checkInfo.checkedCount * 100) / checkInfo.totalCount;
        this.mCheckingProgressView.setProgress(progress);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCheckingTitleTv = (TextView) findViewById(R.id.tv_checking_title);
        this.mCheckingContentTv = (TextView) findViewById(R.id.tv_checking_content);
        this.mCheckingProgressView = (SeekBar) findViewById(R.id.seekbar_check);
    }
}
