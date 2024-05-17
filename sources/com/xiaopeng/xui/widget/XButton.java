package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.utils.XBackgroundPaddingUtils;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes25.dex */
public class XButton extends AppCompatButton implements VuiView {
    private Rect mRectBgPadding;
    protected XViewDelegate mXViewDelegate;

    public XButton(Context context) {
        super(context);
    }

    public XButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mXViewDelegate = XViewDelegate.create(this, attrs);
        init(attrs);
    }

    public XButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mXViewDelegate = XViewDelegate.create(this, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initVui(this, attrs);
        this.mRectBgPadding = XBackgroundPaddingUtils.backgroundPadding(this, attrs);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null && xViewDelegate.getThemeViewModel() != null) {
            this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XButton$bQLWIPstTakjCA8X4JBkR4xO3_s
                @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
                public final void onThemeChanged() {
                    XButton.this.lambda$init$1$XButton();
                }
            });
        }
    }

    public /* synthetic */ void lambda$init$1$XButton() {
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XButton$W4DwNpm_x2Hg_uCeAR1B-dyN6Nk
            @Override // java.lang.Runnable
            public final void run() {
                XButton.this.lambda$null$0$XButton();
            }
        });
    }

    public /* synthetic */ void lambda$null$0$XButton() {
        if (this.mRectBgPadding != null) {
            logD("XButton change theme reset backgroundPadding");
            this.mRectBgPadding = XBackgroundPaddingUtils.backgroundPadding(this, this.mRectBgPadding.left, this.mRectBgPadding.top, this.mRectBgPadding.right, this.mRectBgPadding.bottom);
        }
    }

    public void backgroundPadding(int insetLeft, int insetTop, int insetRight, int insetBottom) {
        this.mRectBgPadding = XBackgroundPaddingUtils.backgroundPadding(this, insetLeft, insetTop, insetRight, insetBottom);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(newConfig);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setVuiVisibility(this, visibility);
    }

    @Override // android.widget.TextView, android.view.View
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setVuiSelected(this, selected);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }
}
