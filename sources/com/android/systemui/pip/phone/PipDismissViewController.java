package com.android.systemui.pip.phone;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.xiaopeng.systemui.helper.WindowHelper;
/* loaded from: classes21.dex */
public class PipDismissViewController {
    private static final int HIDE_TARGET_DURATION = 225;
    public static final int SHOW_TARGET_DELAY = 100;
    private static final int SHOW_TARGET_DURATION = 350;
    private Context mContext;
    private View mDismissView;
    private boolean mIntersecting;
    private int[] mLoc = new int[2];
    private int mTargetSlop;
    private View mTargetView;
    private Vibrator mVibe;
    private WindowManager mWindowManager;
    private Point mWindowSize;

    public PipDismissViewController(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mVibe = (Vibrator) context.getSystemService("vibrator");
    }

    public void createDismissTarget() {
        if (this.mDismissView == null) {
            Rect stableInsets = new Rect();
            WindowManagerWrapper.getInstance().getStableInsets(stableInsets);
            this.mWindowSize = new Point();
            this.mWindowManager.getDefaultDisplay().getRealSize(this.mWindowSize);
            int gradientHeight = this.mContext.getResources().getDimensionPixelSize(R.dimen.pip_dismiss_gradient_height);
            int bottomMargin = this.mContext.getResources().getDimensionPixelSize(R.dimen.pip_dismiss_text_bottom_margin);
            this.mTargetSlop = this.mContext.getResources().getDimensionPixelSize(R.dimen.bubble_dismiss_slop);
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            this.mDismissView = inflater.inflate(R.layout.pip_dismiss_view, (ViewGroup) null);
            this.mDismissView.setSystemUiVisibility(256);
            this.mDismissView.forceHasOverlappingRendering(false);
            Drawable gradient = this.mContext.getResources().getDrawable(R.drawable.pip_dismiss_scrim);
            gradient.setAlpha(216);
            this.mDismissView.setBackground(gradient);
            this.mTargetView = this.mDismissView.findViewById(R.id.pip_dismiss_text);
            FrameLayout.LayoutParams tlp = (FrameLayout.LayoutParams) this.mTargetView.getLayoutParams();
            tlp.bottomMargin = stableInsets.bottom + bottomMargin;
            this.mTargetView.setLayoutParams(tlp);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, gradientHeight, 0, this.mWindowSize.y - gradientHeight, WindowHelper.TYPE_NAVIGATION_BAR_PANEL, 280, -3);
            lp.setTitle("pip-dismiss-overlay");
            lp.privateFlags |= 16;
            lp.gravity = 49;
            this.mWindowManager.addView(this.mDismissView, lp);
        }
        this.mDismissView.animate().cancel();
    }

    public boolean updateTarget(View view) {
        View view2 = this.mDismissView;
        if (view2 != null && view2.getAlpha() > 0.0f) {
            view.getLocationOnScreen(this.mLoc);
            int[] iArr = this.mLoc;
            Rect viewRect = new Rect(iArr[0], iArr[1], iArr[0] + view.getWidth(), this.mLoc[1] + view.getHeight());
            this.mTargetView.getLocationOnScreen(this.mLoc);
            int[] iArr2 = this.mLoc;
            Rect targetRect = new Rect(iArr2[0], iArr2[1], iArr2[0] + this.mTargetView.getWidth(), this.mLoc[1] + this.mTargetView.getHeight());
            expandRect(targetRect, this.mTargetSlop);
            boolean intersecting = targetRect.intersect(viewRect);
            if (intersecting != this.mIntersecting) {
                this.mVibe.vibrate(VibrationEffect.get(intersecting ? 0 : 2));
            }
            this.mIntersecting = intersecting;
            return intersecting;
        }
        return false;
    }

    public void showDismissTarget() {
        this.mDismissView.animate().alpha(1.0f).setInterpolator(Interpolators.LINEAR).setStartDelay(100L).setDuration(350L).start();
    }

    public void destroyDismissTarget() {
        View view = this.mDismissView;
        if (view != null) {
            view.animate().alpha(0.0f).setInterpolator(Interpolators.LINEAR).setStartDelay(0L).setDuration(225L).withEndAction(new Runnable() { // from class: com.android.systemui.pip.phone.PipDismissViewController.1
                @Override // java.lang.Runnable
                public void run() {
                    PipDismissViewController.this.mWindowManager.removeViewImmediate(PipDismissViewController.this.mDismissView);
                    PipDismissViewController.this.mDismissView = null;
                }
            }).start();
        }
    }

    private void expandRect(Rect outRect, int expandAmount) {
        outRect.left = Math.max(0, outRect.left - expandAmount);
        outRect.top = Math.max(0, outRect.top - expandAmount);
        outRect.right = Math.min(this.mWindowSize.x, outRect.right + expandAmount);
        outRect.bottom = Math.min(this.mWindowSize.y, outRect.bottom + expandAmount);
    }
}
