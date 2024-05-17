package com.android.keyguard;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
/* loaded from: classes19.dex */
class LiftToActivateListener implements View.OnHoverListener {
    private final AccessibilityManager mAccessibilityManager;
    private boolean mCachedClickableState;

    public LiftToActivateListener(Context context) {
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
    }

    @Override // android.view.View.OnHoverListener
    public boolean onHover(View v, MotionEvent event) {
        if (this.mAccessibilityManager.isEnabled() && this.mAccessibilityManager.isTouchExplorationEnabled()) {
            int actionMasked = event.getActionMasked();
            if (actionMasked == 9) {
                this.mCachedClickableState = v.isClickable();
                v.setClickable(false);
            } else if (actionMasked == 10) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x > v.getPaddingLeft() && y > v.getPaddingTop() && x < v.getWidth() - v.getPaddingRight() && y < v.getHeight() - v.getPaddingBottom()) {
                    v.performClick();
                }
                v.setClickable(this.mCachedClickableState);
            }
        }
        v.onHoverEvent(event);
        return true;
    }
}
