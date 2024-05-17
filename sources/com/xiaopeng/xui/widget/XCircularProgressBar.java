package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.annotation.DrawableRes;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.drawable.progress.XCircularProgressBgDrawable;
import com.xiaopeng.xui.drawable.progress.XCircularProgressIndeterminateDrawable;
import com.xiaopeng.xui.drawable.progress.XCircularProgressPgDrawable;
import com.xiaopeng.xui.view.XViewDelegate;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes25.dex */
public class XCircularProgressBar extends ProgressBar {
    public static final int INDICATOR_TYPE_PAUSED = 0;
    public static final int INDICATOR_TYPE_PLAYING_PAUSE = 2;
    public static final int INDICATOR_TYPE_PLAYING_STOP = 1;
    public static final int INDICATOR_TYPE_START_DOWNLOAD = 3;
    public static final float OPAQUE = 1.0f;
    public static final float TRANSLUCENT = 0.16f;
    private XCircularProgressBgDrawable mCircularBgDrawable;
    private XCircularProgressIndeterminateDrawable mCircularIndeterminateDrawable;
    private XCircularProgressPgDrawable mCircularPgDrawable;
    private boolean mEnableIndicator;
    @DrawableRes
    private int mIndeterminateDrId;
    private Drawable mIndicatorPause;
    @DrawableRes
    private int mIndicatorPauseId;
    private Drawable mIndicatorPlay;
    @DrawableRes
    private int mIndicatorPlayId;
    private Drawable mIndicatorStart;
    @DrawableRes
    private int mIndicatorStartId;
    private Drawable mIndicatorStop;
    @DrawableRes
    private int mIndicatorStopId;
    private int mIndicatorType;
    private float mInset;
    private float mLightRadius;
    @DrawableRes
    private int mProgressDrawableId;
    private float mStrokeWidth;
    private XViewDelegate mXViewDelegate;
    private static final int[] STATE_PLAYING_STOP = {R.attr.progress_state_playing_stop};
    private static final int[] STATE_PLAYING_PAUSE = {R.attr.progress_state_playing_pause};
    private static final int[] STATE_PAUSED = {R.attr.progress_state_paused};
    private static final int[] STATE_START_DOWNLOAD = {R.attr.progress_state_start_download};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes25.dex */
    public @interface IndicatorType {
    }

    public XCircularProgressBar(Context context) {
        this(context, null);
    }

    public XCircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.XProgressBar_Circular_Medium);
    }

    public XCircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XProgressBar_Circular_Medium);
    }

    public XCircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mEnableIndicator = false;
        init(attrs, defStyleAttr, defStyleRes);
    }

    public void setIndicatorType(int indicatorType) {
        if (this.mIndicatorType == 3) {
            super.setProgress(getMin());
        }
        if (indicatorType == 3) {
            super.setProgress(getMax());
        }
        this.mIndicatorType = indicatorType;
        refreshDrawableState();
    }

    public int getIndicatorType() {
        return this.mIndicatorType;
    }

    public void setEnableIndicator(boolean enableIndicator) {
        this.mEnableIndicator = enableIndicator;
        refreshDrawableState();
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return;
        }
        this.mXViewDelegate = XViewDelegate.create(this, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XCircularProgressBar, defStyleAttr, defStyleRes);
        this.mEnableIndicator = a.getBoolean(R.styleable.XCircularProgressBar_progress_enableIndicator, false);
        this.mIndicatorType = a.getInt(R.styleable.XCircularProgressBar_progress_indicatorType, 0);
        this.mInset = a.getDimensionPixelSize(R.styleable.XCircularProgressBar_progress_inset, 0);
        this.mStrokeWidth = a.getDimensionPixelOffset(R.styleable.XCircularProgressBar_progress_strokeWidth, 0);
        boolean stateEnabled = a.getBoolean(R.styleable.XCircularProgressBar_android_enabled, true);
        this.mLightRadius = a.getDimensionPixelOffset(R.styleable.XCircularProgressBar_progress_light_radius, 0);
        this.mIndicatorPlay = a.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_play);
        this.mIndicatorPause = a.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_pause);
        this.mIndicatorStop = a.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_stop);
        this.mIndicatorStart = a.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_start);
        this.mIndicatorPlayId = a.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_play, 0);
        this.mIndicatorPauseId = a.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_pause, 0);
        this.mIndicatorStopId = a.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_stop, 0);
        this.mIndicatorStartId = a.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_start, 0);
        this.mIndeterminateDrId = a.getResourceId(R.styleable.XCircularProgressBar_android_indeterminateDrawable, 0);
        this.mProgressDrawableId = a.getResourceId(R.styleable.XCircularProgressBar_android_progressDrawable, 0);
        a.recycle();
        setEnabled(stateEnabled);
        if (this.mIndicatorType == 3) {
            super.setProgress(getMax());
        }
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null && xViewDelegate.getThemeViewModel() != null) {
            this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$SPexaB5wHRdqIMQjiB7GGtOU9Lg
                @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
                public final void onThemeChanged() {
                    XCircularProgressBar.this.updateThemeResource();
                }
            });
        }
        setProgressDrawableProp();
        setIndeterminateProp();
    }

    private void parseProgressDrawable(Drawable progressDrawable) {
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            Drawable pgDrawable = layerDrawable.findDrawableByLayerId(16908301);
            if (pgDrawable instanceof XCircularProgressPgDrawable) {
                this.mCircularPgDrawable = (XCircularProgressPgDrawable) pgDrawable;
            }
            Drawable background = layerDrawable.findDrawableByLayerId(16908288);
            if (background instanceof XCircularProgressBgDrawable) {
                this.mCircularBgDrawable = (XCircularProgressBgDrawable) background;
            }
        }
    }

    private void parseIndeterminateDrawable(Drawable indeterminateDrawable) {
        if (indeterminateDrawable instanceof RotateDrawable) {
            Drawable indeterminate = ((RotateDrawable) indeterminateDrawable).getDrawable();
            if (indeterminate instanceof XCircularProgressIndeterminateDrawable) {
                this.mCircularIndeterminateDrawable = (XCircularProgressIndeterminateDrawable) indeterminate;
            }
        }
    }

    private void setProgressDrawableProp() {
        XCircularProgressPgDrawable xCircularProgressPgDrawable = this.mCircularPgDrawable;
        if (xCircularProgressPgDrawable != null) {
            xCircularProgressPgDrawable.setInset(this.mInset);
            this.mCircularPgDrawable.setStrokeWidth(this.mStrokeWidth);
            this.mCircularPgDrawable.setLightRadius(this.mLightRadius);
        }
        XCircularProgressBgDrawable xCircularProgressBgDrawable = this.mCircularBgDrawable;
        if (xCircularProgressBgDrawable != null) {
            xCircularProgressBgDrawable.setInset(this.mInset);
            this.mCircularBgDrawable.setStrokeWidth(this.mStrokeWidth);
            this.mCircularBgDrawable.setIndicatorPlay(this.mIndicatorPlay);
            this.mCircularBgDrawable.setIndicatorPause(this.mIndicatorPause);
            this.mCircularBgDrawable.setIndicatorStop(this.mIndicatorStop);
            this.mCircularBgDrawable.setIndicatorStart(this.mIndicatorStart);
        }
    }

    private void setIndeterminateProp() {
        XCircularProgressIndeterminateDrawable xCircularProgressIndeterminateDrawable = this.mCircularIndeterminateDrawable;
        if (xCircularProgressIndeterminateDrawable != null) {
            xCircularProgressIndeterminateDrawable.setInset(this.mInset);
            this.mCircularIndeterminateDrawable.setStrokeWidth(this.mStrokeWidth);
            this.mCircularIndeterminateDrawable.setLightRadius(this.mLightRadius);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateThemeResource() {
        int i = this.mIndicatorStartId;
        if (i != 0) {
            this.mIndicatorStart = getDrawable(i);
        }
        int i2 = this.mIndicatorPauseId;
        if (i2 != 0) {
            this.mIndicatorPause = getDrawable(i2);
        }
        int i3 = this.mIndicatorStopId;
        if (i3 != 0) {
            this.mIndicatorStop = getDrawable(i3);
        }
        int i4 = this.mIndicatorPlayId;
        if (i4 != 0) {
            this.mIndicatorPlay = getDrawable(i4);
        }
        int i5 = this.mIndeterminateDrId;
        if (i5 != 0) {
            setIndeterminateDrawable(getDrawable(i5));
        }
        int i6 = this.mProgressDrawableId;
        if (i6 != 0) {
            setProgressDrawable(getDrawable(i6));
        }
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int progress) {
        if (this.mIndicatorType == 3) {
            progress = getMax();
        }
        super.setProgress(progress);
    }

    @Override // android.widget.ProgressBar
    public void setProgressDrawable(Drawable d) {
        parseProgressDrawable(d);
        setProgressDrawableProp();
        super.setProgressDrawable(d);
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateDrawable(Drawable d) {
        parseIndeterminateDrawable(d);
        setIndeterminateProp();
        super.setIndeterminateDrawable(d);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mXViewDelegate.onConfigurationChanged(newConfig);
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mXViewDelegate.onAttachedToWindow();
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mXViewDelegate.onDetachedFromWindow();
    }

    @Override // android.widget.ProgressBar
    public synchronized void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(indeterminate);
        if (!indeterminate) {
            refreshDrawableState();
        }
    }

    @Override // android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        if (isIndeterminate() || !this.mEnableIndicator) {
            return super.onCreateDrawableState(extraSpace);
        }
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        int i = this.mIndicatorType;
        if (i == 0) {
            mergeDrawableStates(drawableState, STATE_PAUSED);
        } else if (i == 1) {
            mergeDrawableStates(drawableState, STATE_PLAYING_STOP);
        } else if (i == 2) {
            mergeDrawableStates(drawableState, STATE_PLAYING_PAUSE);
        } else if (i == 3) {
            mergeDrawableStates(drawableState, STATE_START_DOWNLOAD);
        }
        return drawableState;
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (isEnabled()) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.16f);
        }
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return Xui.getContext().getResources().getDrawable(id, null);
    }
}
