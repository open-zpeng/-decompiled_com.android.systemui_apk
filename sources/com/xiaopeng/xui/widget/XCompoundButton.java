package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes25.dex */
public class XCompoundButton extends CompoundButton implements VuiView {
    private boolean mFromUser;
    protected XViewDelegate mXViewDelegate;

    public XCompoundButton(Context context) {
        this(context, null);
    }

    public XCompoundButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XCompoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XCompoundButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mXViewDelegate = XViewDelegate.create(this, attrs, defStyleAttr, defStyleRes);
        initVui(this, attrs);
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

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean checked) {
        boolean isChecked = isChecked();
        super.setChecked(checked);
        if (isChecked != checked) {
            updateVui(this);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }

    @Override // android.widget.CompoundButton, android.view.View
    public boolean performClick() {
        setFromUser(true);
        boolean handled = super.performClick();
        setFromUser(false);
        return handled;
    }

    private void setFromUser(boolean fromUser) {
        this.mFromUser = fromUser;
    }

    public boolean isFromUser() {
        return this.mFromUser;
    }
}
