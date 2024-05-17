package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.DisplayCutout;
import android.view.View;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.AlphaOptimizedLinearLayout;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class HeadsUpStatusBarView extends AlphaOptimizedLinearLayout {
    private static final String ALPHA = "alpha";
    private static final String FIRST_LAYOUT = "first_layout";
    private static final String HEADS_UP_STATUS_BAR_VIEW_SUPER_PARCELABLE = "heads_up_status_bar_view_super_parcelable";
    private static final String VISIBILITY = "visibility";
    private int mAbsoluteStartPadding;
    private List<Rect> mCutOutBounds;
    private int mCutOutInset;
    private Point mDisplaySize;
    private int mEndMargin;
    private boolean mFirstLayout;
    private Rect mIconDrawingRect;
    private View mIconPlaceholder;
    private Rect mLayoutedIconRect;
    private int mMaxWidth;
    private Runnable mOnDrawingRectChangedListener;
    private View mRootView;
    private NotificationEntry mShowingEntry;
    private int mSysWinInset;
    private TextView mTextView;
    private int[] mTmpPosition;

    public HeadsUpStatusBarView(Context context) {
        this(context, null);
    }

    public HeadsUpStatusBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadsUpStatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HeadsUpStatusBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mLayoutedIconRect = new Rect();
        this.mTmpPosition = new int[2];
        this.mFirstLayout = true;
        this.mIconDrawingRect = new Rect();
        Resources res = getResources();
        this.mAbsoluteStartPadding = res.getDimensionPixelSize(R.dimen.notification_side_paddings) + res.getDimensionPixelSize(17105313);
        this.mEndMargin = res.getDimensionPixelSize(17105312);
        setPaddingRelative(this.mAbsoluteStartPadding, 0, this.mEndMargin, 0);
        updateMaxWidth();
    }

    private void updateMaxWidth() {
        int maxWidth = getResources().getDimensionPixelSize(R.dimen.qs_panel_width);
        if (maxWidth != this.mMaxWidth) {
            this.mMaxWidth = maxWidth;
            requestLayout();
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mMaxWidth > 0) {
            int newSize = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), this.mMaxWidth);
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(newSize, View.MeasureSpec.getMode(widthMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateMaxWidth();
    }

    @Override // android.view.View
    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(HEADS_UP_STATUS_BAR_VIEW_SUPER_PARCELABLE, super.onSaveInstanceState());
        bundle.putBoolean(FIRST_LAYOUT, this.mFirstLayout);
        bundle.putInt(VISIBILITY, getVisibility());
        bundle.putFloat("alpha", getAlpha());
        return bundle;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle bundle = (Bundle) state;
        Parcelable superState = bundle.getParcelable(HEADS_UP_STATUS_BAR_VIEW_SUPER_PARCELABLE);
        super.onRestoreInstanceState(superState);
        this.mFirstLayout = bundle.getBoolean(FIRST_LAYOUT, true);
        if (bundle.containsKey(VISIBILITY)) {
            setVisibility(bundle.getInt(VISIBILITY));
        }
        if (bundle.containsKey("alpha")) {
            setAlpha(bundle.getFloat("alpha"));
        }
    }

    @VisibleForTesting
    public HeadsUpStatusBarView(Context context, View iconPlaceholder, TextView textView) {
        this(context);
        this.mIconPlaceholder = iconPlaceholder;
        this.mTextView = textView;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconPlaceholder = findViewById(R.id.icon_placeholder);
        this.mTextView = (TextView) findViewById(R.id.text);
    }

    /* renamed from: setEntry */
    public void lambda$setEntry$0$HeadsUpStatusBarView(final NotificationEntry entry) {
        if (entry != null) {
            this.mShowingEntry = entry;
            CharSequence text = entry.headsUpStatusBarText;
            if (entry.isSensitive()) {
                text = entry.headsUpStatusBarTextPublic;
            }
            this.mTextView.setText(text);
            this.mShowingEntry.setOnSensitiveChangedListener(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$HeadsUpStatusBarView$9LEXjhJrDwN7VfE5FR5-LkCG-lg
                @Override // java.lang.Runnable
                public final void run() {
                    HeadsUpStatusBarView.this.lambda$setEntry$0$HeadsUpStatusBarView(entry);
                }
            });
            return;
        }
        NotificationEntry notificationEntry = this.mShowingEntry;
        if (notificationEntry != null) {
            notificationEntry.setOnSensitiveChangedListener(null);
            this.mShowingEntry = null;
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cutOutStart;
        super.onLayout(changed, l, t, r, b);
        this.mIconPlaceholder.getLocationOnScreen(this.mTmpPosition);
        int left = (int) (this.mTmpPosition[0] - getTranslationX());
        int top = this.mTmpPosition[1];
        int right = this.mIconPlaceholder.getWidth() + left;
        int bottom = this.mIconPlaceholder.getHeight() + top;
        this.mLayoutedIconRect.set(left, top, right, bottom);
        updateDrawingRect();
        int targetPadding = this.mAbsoluteStartPadding + this.mSysWinInset + this.mCutOutInset;
        boolean isRtl = isLayoutRtl();
        int start = isRtl ? this.mDisplaySize.x - right : left;
        if (start != targetPadding) {
            List<Rect> list = this.mCutOutBounds;
            if (list != null) {
                Iterator<Rect> it = list.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Rect cutOutRect = it.next();
                    if (isRtl) {
                        cutOutStart = this.mDisplaySize.x - cutOutRect.right;
                        continue;
                    } else {
                        cutOutStart = cutOutRect.left;
                        continue;
                    }
                    if (start > cutOutStart) {
                        start -= cutOutRect.width();
                        break;
                    }
                }
            }
            int newPadding = (targetPadding - start) + getPaddingStart();
            setPaddingRelative(newPadding, 0, this.mEndMargin, 0);
        }
        if (this.mFirstLayout) {
            setVisibility(8);
            this.mFirstLayout = false;
        }
    }

    public void setPanelTranslation(float translationX) {
        setTranslationX(translationX);
        updateDrawingRect();
    }

    private void updateDrawingRect() {
        Runnable runnable;
        float oldLeft = this.mIconDrawingRect.left;
        this.mIconDrawingRect.set(this.mLayoutedIconRect);
        this.mIconDrawingRect.offset((int) getTranslationX(), 0);
        if (oldLeft != this.mIconDrawingRect.left && (runnable = this.mOnDrawingRectChangedListener) != null) {
            runnable.run();
        }
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect insets) {
        int i;
        boolean isRtl = isLayoutRtl();
        this.mSysWinInset = isRtl ? insets.right : insets.left;
        DisplayCutout displayCutout = getRootWindowInsets().getDisplayCutout();
        if (displayCutout != null) {
            i = isRtl ? displayCutout.getSafeInsetRight() : displayCutout.getSafeInsetLeft();
        } else {
            i = 0;
        }
        this.mCutOutInset = i;
        getDisplaySize();
        this.mCutOutBounds = null;
        if (displayCutout != null && displayCutout.getSafeInsetRight() == 0 && displayCutout.getSafeInsetLeft() == 0) {
            this.mCutOutBounds = displayCutout.getBoundingRects();
        }
        if (this.mSysWinInset != 0) {
            this.mCutOutInset = 0;
        }
        return super.fitSystemWindows(insets);
    }

    public NotificationEntry getShowingEntry() {
        return this.mShowingEntry;
    }

    public Rect getIconDrawingRect() {
        return this.mIconDrawingRect;
    }

    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        this.mTextView.setTextColor(DarkIconDispatcher.getTint(area, this, tint));
    }

    public void setOnDrawingRectChangedListener(Runnable onDrawingRectChangedListener) {
        this.mOnDrawingRectChangedListener = onDrawingRectChangedListener;
    }

    private void getDisplaySize() {
        if (this.mDisplaySize == null) {
            this.mDisplaySize = new Point();
        }
        getDisplay().getRealSize(this.mDisplaySize);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getDisplaySize();
    }
}
