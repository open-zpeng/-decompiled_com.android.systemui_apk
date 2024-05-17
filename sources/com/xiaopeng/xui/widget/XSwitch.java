package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes25.dex */
public class XSwitch extends Switch implements VuiView {
    private static final String TAG = "XSwitch";
    private boolean mCheckSoundEnable;
    private boolean mFromUser;
    private OnInterceptListener mOnInterceptListener;
    private final int mThumbResId;
    private final int mTrackResId;
    protected XViewDelegate mXViewDelegate;

    public XSwitch(Context context) {
        this(context, null);
    }

    public XSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 16843839);
    }

    public XSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCheckSoundEnable = true;
        this.mXViewDelegate = XViewDelegate.create(this, attrs, defStyleAttr, defStyleRes);
        initVui(this, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XSwitch, defStyleAttr, defStyleRes);
        this.mThumbResId = a.getResourceId(R.styleable.XSwitch_android_thumb, 0);
        this.mTrackResId = a.getResourceId(R.styleable.XSwitch_android_track, 0);
        a.recycle();
        super.setSoundEffectsEnabled(false);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(newConfig);
        }
        if (ThemeManager.isThemeChanged(newConfig)) {
            int[] states = getDrawableState();
            Log.d(TAG, "onConfigurationChanged: thumb:" + this.mThumbResId + ", track:" + this.mTrackResId);
            if (this.mThumbResId != 0) {
                Drawable thumbDrawable = Xui.getContext().getDrawable(this.mThumbResId);
                thumbDrawable.setState(states);
                setThumbDrawable(thumbDrawable);
            } else {
                Log.e(TAG, "onConfigurationChanged: thumb res not found");
            }
            if (this.mTrackResId != 0) {
                Drawable trackDrawable = Xui.getContext().getDrawable(this.mTrackResId);
                trackDrawable.setState(states);
                setTrackDrawable(trackDrawable);
                return;
            }
            Log.e(TAG, "onConfigurationChanged: track res not found");
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
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        setCheckSoundEnable(soundEffectsEnabled);
    }

    public void setCheckSoundEnable(boolean enable) {
        this.mCheckSoundEnable = enable;
    }

    @Override // android.widget.Switch, android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean checked) {
        OnInterceptListener onInterceptListener = this.mOnInterceptListener;
        if (onInterceptListener != null && onInterceptListener.onInterceptCheck(this, checked)) {
            Log.d(TAG, "Intercept setChecked: " + checked + ", current:" + isChecked());
            super.setChecked(isChecked());
            return;
        }
        boolean currentChecked = isChecked();
        if (currentChecked != checked && isPressed() && this.mCheckSoundEnable) {
            XSoundEffectManager.get().play(checked ? 3 : 4);
        }
        setCheckedAndUpdateVui(checked);
    }

    public void setChecked(boolean checked, boolean animator) {
        if (animator) {
            setChecked(checked);
        } else {
            setCheckedAndUpdateVui(checked);
        }
    }

    private void setCheckedAndUpdateVui(boolean checked) {
        boolean currentChecked = isChecked();
        super.setChecked(checked);
        if (currentChecked != checked) {
            updateVui(this);
        }
    }

    @Override // android.widget.CompoundButton, android.view.View
    public boolean performClick() {
        OnInterceptListener onInterceptListener = this.mOnInterceptListener;
        if (onInterceptListener == null || !onInterceptListener.onInterceptClickEvent(this)) {
            setFromUser(true);
            boolean handled = super.performClick();
            setFromUser(false);
            return handled;
        }
        return false;
    }

    private void setFromUser(boolean fromUser) {
        this.mFromUser = fromUser;
    }

    public boolean isFromUser() {
        return this.mFromUser;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setVuiVisibility(this, visibility);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        releaseVui();
    }

    public void setOnInterceptListener(OnInterceptListener listener) {
        this.mOnInterceptListener = listener;
    }

    /* loaded from: classes25.dex */
    public interface OnInterceptListener {
        boolean onInterceptCheck(View view, boolean z);

        default boolean onInterceptClickEvent(View v) {
            return false;
        }
    }
}
