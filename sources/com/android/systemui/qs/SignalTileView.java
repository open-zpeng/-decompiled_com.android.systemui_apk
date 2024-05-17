package com.android.systemui.qs;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSIconViewImpl;
import com.android.systemui.qs.tileimpl.SlashImageView;
/* loaded from: classes21.dex */
public class SignalTileView extends QSIconViewImpl {
    private static final long DEFAULT_DURATION = new ValueAnimator().getDuration();
    private static final long SHORT_DURATION = DEFAULT_DURATION / 3;
    protected FrameLayout mIconFrame;
    private ImageView mIn;
    private ImageView mOut;
    private ImageView mOverlay;
    protected ImageView mSignal;
    private int mSignalIndicatorToIconFrameSpacing;
    private int mWideOverlayIconStartPadding;

    public SignalTileView(Context context) {
        super(context);
        this.mIn = addTrafficView(R.drawable.ic_qs_signal_in);
        this.mOut = addTrafficView(R.drawable.ic_qs_signal_out);
        setClipChildren(false);
        setClipToPadding(false);
        this.mWideOverlayIconStartPadding = context.getResources().getDimensionPixelSize(R.dimen.wide_type_icon_start_padding_qs);
        this.mSignalIndicatorToIconFrameSpacing = context.getResources().getDimensionPixelSize(R.dimen.signal_indicator_to_icon_frame_spacing);
    }

    private ImageView addTrafficView(int icon) {
        ImageView traffic = new ImageView(this.mContext);
        traffic.setImageResource(icon);
        traffic.setAlpha(0.0f);
        addView(traffic);
        return traffic;
    }

    @Override // com.android.systemui.qs.tileimpl.QSIconViewImpl
    protected View createIcon() {
        this.mIconFrame = new FrameLayout(this.mContext);
        this.mSignal = createSlashImageView(this.mContext);
        this.mIconFrame.addView(this.mSignal);
        this.mOverlay = new ImageView(this.mContext);
        this.mIconFrame.addView(this.mOverlay, -2, -2);
        return this.mIconFrame;
    }

    protected SlashImageView createSlashImageView(Context context) {
        return new SlashImageView(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSIconViewImpl, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hs = View.MeasureSpec.makeMeasureSpec(this.mIconFrame.getMeasuredHeight(), 1073741824);
        int ws = View.MeasureSpec.makeMeasureSpec(this.mIconFrame.getMeasuredHeight(), Integer.MIN_VALUE);
        this.mIn.measure(ws, hs);
        this.mOut.measure(ws, hs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSIconViewImpl, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        layoutIndicator(this.mIn);
        layoutIndicator(this.mOut);
    }

    @Override // com.android.systemui.qs.tileimpl.QSIconViewImpl
    protected int getIconMeasureMode() {
        return Integer.MIN_VALUE;
    }

    private void layoutIndicator(View indicator) {
        int left;
        int right;
        boolean isRtl = getLayoutDirection() == 1;
        if (isRtl) {
            right = getLeft() - this.mSignalIndicatorToIconFrameSpacing;
            left = right - indicator.getMeasuredWidth();
        } else {
            int right2 = getRight();
            left = this.mSignalIndicatorToIconFrameSpacing + right2;
            right = indicator.getMeasuredWidth() + left;
        }
        indicator.layout(left, this.mIconFrame.getBottom() - indicator.getMeasuredHeight(), right, this.mIconFrame.getBottom());
    }

    @Override // com.android.systemui.qs.tileimpl.QSIconViewImpl, com.android.systemui.plugins.qs.QSIconView
    public void setIcon(QSTile.State state, boolean allowAnimations) {
        QSTile.SignalState s = (QSTile.SignalState) state;
        setIcon(this.mSignal, s, allowAnimations);
        boolean z = false;
        if (s.overlayIconId > 0) {
            this.mOverlay.setVisibility(0);
            this.mOverlay.setImageResource(s.overlayIconId);
        } else {
            this.mOverlay.setVisibility(8);
        }
        if (s.overlayIconId > 0 && s.isOverlayIconWide) {
            this.mSignal.setPaddingRelative(this.mWideOverlayIconStartPadding, 0, 0, 0);
        } else {
            this.mSignal.setPaddingRelative(0, 0, 0, 0);
        }
        if (allowAnimations && isShown()) {
            z = true;
        }
        boolean shouldAnimate = z;
        setVisibility(this.mIn, shouldAnimate, s.activityIn);
        setVisibility(this.mOut, shouldAnimate, s.activityOut);
    }

    private void setVisibility(View view, boolean shown, boolean visible) {
        float newAlpha = (shown && visible) ? 1.0f : 0.0f;
        if (view.getAlpha() == newAlpha) {
            return;
        }
        if (shown) {
            view.animate().setDuration(visible ? SHORT_DURATION : DEFAULT_DURATION).alpha(newAlpha).start();
        } else {
            view.setAlpha(newAlpha);
        }
    }
}
