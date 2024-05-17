package com.xiaopeng.xui.widget.pageindicator;

import android.content.Context;
import android.content.res.Configuration;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
public class XNumberPagerIndicator extends XViewPagerIndicatorAbs {
    private int mChildViewSize;
    private int mCount;
    private String mFormat;
    private int mPosition;
    private TextView mTextView;

    public XNumberPagerIndicator(Context context) {
        super(context);
    }

    public XNumberPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XNumberPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XNumberPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.pageindicator.XViewPagerIndicatorAbs
    public void init() {
        super.init();
        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.x_pager_indicator_number_padding);
        setPadding(padding, 0, padding, 0);
        this.mChildViewSize = getContext().getResources().getDimensionPixelSize(R.dimen.x_pager_indicator_view_size);
        this.mFormat = getContext().getString(R.string.x_number_indicator_desc_format);
    }

    @Override // com.xiaopeng.xui.widget.pageindicator.XViewPagerIndicatorAbs
    public void populateData(int count) {
        if (this.mTextView == null) {
            this.mTextView = new TextView(getContext());
            this.mTextView.setTextAppearance(R.style.XNumberIndicatorSelectedTextStyle);
            this.mTextView.setGravity(17);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, this.mChildViewSize);
            lp.weight = 1.0f;
            addView(this.mTextView, lp);
        }
        this.mCount = count;
    }

    @Override // com.xiaopeng.xui.widget.pageindicator.XViewPagerIndicatorAbs
    public void select(int position) {
        this.mPosition = position + 1;
        refreshText();
    }

    private void refreshText() {
        if (this.mTextView != null) {
            if (this.mPosition > this.mCount) {
                throw new IllegalArgumentException("Position cannot greater than count");
            }
            TextAppearanceSpan textSpan = new TextAppearanceSpan(getContext(), R.style.XNumberIndicatorTextStyle);
            String desc = String.format(this.mFormat, Integer.valueOf(this.mPosition), Integer.valueOf(this.mCount));
            SpannableString ss = new SpannableString(desc);
            ss.setSpan(textSpan, desc.indexOf(47), desc.length(), 17);
            this.mTextView.setText(ss);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            TextView textView = this.mTextView;
            if (textView != null) {
                textView.setTextAppearance(R.style.XNumberIndicatorSelectedTextStyle);
            }
            refreshText();
        }
    }
}
