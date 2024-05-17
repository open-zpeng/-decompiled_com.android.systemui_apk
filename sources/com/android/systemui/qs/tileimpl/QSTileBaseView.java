package com.android.systemui.qs.tileimpl;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.PathParser;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import com.android.settingslib.Utils;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
/* loaded from: classes21.dex */
public class QSTileBaseView extends com.android.systemui.plugins.qs.QSTileView {
    private static final int ICON_MASK_ID = 17039747;
    private static final String TAG = "QSTileBaseView";
    private String mAccessibilityClass;
    private final ImageView mBg;
    private int mBgSize;
    private int mCircleColor;
    private boolean mClicked;
    private boolean mCollapsedView;
    private final int mColorActive;
    private final int mColorDisabled;
    private final int mColorInactive;
    private final H mHandler;
    protected QSIconView mIcon;
    private final FrameLayout mIconFrame;
    private final int[] mLocInScreen;
    protected RippleDrawable mRipple;
    private boolean mShowRippleEffect;
    private Drawable mTileBackground;
    private boolean mTileState;

    public QSTileBaseView(Context context, QSIconView icon) {
        this(context, icon, false);
    }

    public QSTileBaseView(Context context, QSIconView icon, boolean collapsedView) {
        super(context);
        this.mHandler = new H();
        this.mLocInScreen = new int[2];
        this.mShowRippleEffect = true;
        context.getResources().getDimensionPixelSize(R.dimen.qs_quick_tile_padding);
        this.mIconFrame = new FrameLayout(context);
        int size = context.getResources().getDimensionPixelSize(R.dimen.qs_quick_tile_size);
        addView(this.mIconFrame, new LinearLayout.LayoutParams(size, size));
        this.mBg = new ImageView(getContext());
        Path path = new Path(PathParser.createPathFromPathData(context.getResources().getString(ICON_MASK_ID)));
        PathShape p = new PathShape(path, 100.0f, 100.0f);
        ShapeDrawable d = new ShapeDrawable(p);
        d.setTintList(ColorStateList.valueOf(0));
        int bgSize = context.getResources().getDimensionPixelSize(R.dimen.qs_tile_background_size);
        d.setIntrinsicHeight(bgSize);
        d.setIntrinsicWidth(bgSize);
        this.mBg.setImageDrawable(d);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(bgSize, bgSize, 17);
        this.mIconFrame.addView(this.mBg, lp);
        this.mBg.setLayoutParams(lp);
        this.mIcon = icon;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2, 17);
        this.mIconFrame.addView(this.mIcon, params);
        this.mIconFrame.setClipChildren(false);
        this.mIconFrame.setClipToPadding(false);
        this.mTileBackground = newTileBackground();
        Drawable drawable = this.mTileBackground;
        if (drawable instanceof RippleDrawable) {
            setRipple((RippleDrawable) drawable);
        }
        setImportantForAccessibility(1);
        setBackground(this.mTileBackground);
        this.mColorActive = Utils.getColorAttrDefaultColor(context, 16843829);
        this.mColorDisabled = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843282));
        this.mColorInactive = Utils.getColorAttrDefaultColor(context, 16842808);
        setPadding(0, 0, 0, 0);
        setClipChildren(false);
        setClipToPadding(false);
        this.mCollapsedView = collapsedView;
        setFocusable(true);
    }

    public View getBgCircle() {
        return this.mBg;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Drawable newTileBackground() {
        int[] attrs = {16843868};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        Drawable d = ta.getDrawable(0);
        ta.recycle();
        return d;
    }

    private void setRipple(RippleDrawable tileBackground) {
        this.mRipple = tileBackground;
        if (getWidth() != 0) {
            updateRippleSize();
        }
    }

    private void updateRippleSize() {
        int cx = (this.mIconFrame.getMeasuredWidth() / 2) + this.mIconFrame.getLeft();
        int cy = (this.mIconFrame.getMeasuredHeight() / 2) + this.mIconFrame.getTop();
        int rad = (int) (this.mIcon.getHeight() * 0.85f);
        this.mRipple.setHotspotBounds(cx - rad, cy - rad, cx + rad, cy + rad);
    }

    @Override // com.android.systemui.plugins.qs.QSTileView
    public void init(final QSTile tile) {
        init(new View.OnClickListener() { // from class: com.android.systemui.qs.tileimpl.-$$Lambda$QSTileBaseView$aVxKNvlJE7IFS8nVmOyLdAcByFA
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QSTile.this.click();
            }
        }, new View.OnClickListener() { // from class: com.android.systemui.qs.tileimpl.-$$Lambda$QSTileBaseView$W9w1scJAVZm5V6Q1VB4ZO5o3C8A
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QSTile.this.secondaryClick();
            }
        }, new View.OnLongClickListener() { // from class: com.android.systemui.qs.tileimpl.-$$Lambda$QSTileBaseView$STEfvGmwtIL_pMrVYwBQuK3x1jo
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return QSTileBaseView.lambda$init$2(QSTile.this, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$init$2(QSTile tile, View view) {
        tile.longClick();
        return true;
    }

    public void init(View.OnClickListener click, View.OnClickListener secondaryClick, View.OnLongClickListener longClick) {
        setOnClickListener(click);
        setOnLongClickListener(longClick);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mRipple != null) {
            updateRippleSize();
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.systemui.plugins.qs.QSTileView
    public View updateAccessibilityOrder(View previousView) {
        setAccessibilityTraversalAfter(previousView.getId());
        return this;
    }

    @Override // com.android.systemui.plugins.qs.QSTileView
    public void onStateChanged(QSTile.State state) {
        this.mHandler.obtainMessage(1, state).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleStateChanged(QSTile.State state) {
        boolean newState;
        int circleColor = getCircleColor(state.state);
        boolean allowAnimations = animationsEnabled();
        int i = this.mCircleColor;
        if (circleColor != i) {
            if (allowAnimations) {
                ValueAnimator animator = ValueAnimator.ofArgb(i, circleColor).setDuration(350L);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.qs.tileimpl.-$$Lambda$QSTileBaseView$R4RxHhlQ5aUQCBgq0kdDEHJXn14
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        QSTileBaseView.this.lambda$handleStateChanged$3$QSTileBaseView(valueAnimator);
                    }
                });
                animator.start();
            } else {
                QSIconViewImpl.setTint(this.mBg, circleColor);
            }
            this.mCircleColor = circleColor;
        }
        this.mShowRippleEffect = state.showRippleEffect;
        setClickable(state.state != 0);
        setLongClickable(state.handlesLongClick);
        this.mIcon.setIcon(state, allowAnimations);
        setContentDescription(state.contentDescription);
        this.mAccessibilityClass = state.state == 0 ? null : state.expandedAccessibilityClassName;
        if ((state instanceof QSTile.BooleanState) && this.mTileState != (newState = ((QSTile.BooleanState) state).value)) {
            this.mClicked = false;
            this.mTileState = newState;
        }
    }

    public /* synthetic */ void lambda$handleStateChanged$3$QSTileBaseView(ValueAnimator animation) {
        this.mBg.setImageTintList(ColorStateList.valueOf(((Integer) animation.getAnimatedValue()).intValue()));
    }

    protected boolean animationsEnabled() {
        if (isShown() && getAlpha() == 1.0f) {
            getLocationOnScreen(this.mLocInScreen);
            return this.mLocInScreen[1] >= (-getHeight());
        }
        return false;
    }

    private int getCircleColor(int state) {
        if (state == 0 || state == 1) {
            return this.mColorDisabled;
        }
        if (state == 2) {
            return this.mColorActive;
        }
        Log.e(TAG, "Invalid state " + state);
        return 0;
    }

    @Override // android.view.View
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        setBackground((clickable && this.mShowRippleEffect) ? this.mRipple : null);
    }

    @Override // com.android.systemui.plugins.qs.QSTileView
    public int getDetailY() {
        return getTop() + (getHeight() / 2);
    }

    @Override // com.android.systemui.plugins.qs.QSTileView
    public QSIconView getIcon() {
        return this.mIcon;
    }

    @Override // com.android.systemui.plugins.qs.QSTileView
    public View getIconWithBackground() {
        return this.mIconFrame;
    }

    @Override // android.view.View
    public boolean performClick() {
        this.mClicked = true;
        return super.performClick();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (!TextUtils.isEmpty(this.mAccessibilityClass)) {
            event.setClassName(this.mAccessibilityClass);
            if (Switch.class.getName().equals(this.mAccessibilityClass)) {
                boolean b = this.mClicked ? !this.mTileState : this.mTileState;
                String label = getResources().getString(b ? R.string.switch_bar_on : R.string.switch_bar_off);
                event.setContentDescription(label);
                event.setChecked(b);
            }
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        boolean b = false;
        info.setSelected(false);
        if (!TextUtils.isEmpty(this.mAccessibilityClass)) {
            info.setClassName(this.mAccessibilityClass);
            if (Switch.class.getName().equals(this.mAccessibilityClass)) {
                if (!this.mClicked) {
                    b = this.mTileState;
                } else if (!this.mTileState) {
                    b = true;
                }
                String label = getResources().getString(b ? R.string.switch_bar_on : R.string.switch_bar_off);
                info.setText(label);
                info.setChecked(b);
                info.setCheckable(true);
                if (isLongClickable()) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK.getId(), getResources().getString(R.string.accessibility_long_click_tile)));
                }
            }
        }
    }

    @Override // android.view.View
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
        sb.append("locInScreen=(" + this.mLocInScreen[0] + ", " + this.mLocInScreen[1] + NavigationBarInflaterView.KEY_CODE_END);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(", iconView=");
        sb2.append(this.mIcon.toString());
        sb.append(sb2.toString());
        sb.append(", tileState=" + this.mTileState);
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }

    /* loaded from: classes21.dex */
    private class H extends Handler {
        private static final int STATE_CHANGED = 1;

        public H() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                QSTileBaseView.this.handleStateChanged((QSTile.State) msg.obj);
            }
        }
    }
}
