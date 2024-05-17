package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.systemui.R;
/* loaded from: classes24.dex */
public class AppIconContainer extends AlphaOptimizedFrameLayout {
    private AnimatedImageView mAppIcon;
    private AnimatedImageView mBackground;
    private int mIconSrc;

    public AppIconContainer(Context context) {
        super(context);
        initView(context);
    }

    public AppIconContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView(context);
    }

    public AppIconContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
    }

    @Override // android.view.View
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        this.mBackground.setSelected(selected);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_app_icon_container, (ViewGroup) this, true);
        this.mAppIcon = (AnimatedImageView) findViewById(R.id.app_icon);
        this.mBackground = (AnimatedImageView) findViewById(R.id.app_icon_background);
        this.mAppIcon.setImageResource(this.mIconSrc);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AppIconContainer, 0, 0);
        try {
            this.mIconSrc = a.getResourceId(0, 0);
        } finally {
            a.recycle();
        }
    }
}
