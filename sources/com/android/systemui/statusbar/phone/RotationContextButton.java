package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
/* loaded from: classes21.dex */
public class RotationContextButton extends ContextualButton implements NavigationModeController.ModeChangedListener, RotationButton {
    public static final boolean DEBUG_ROTATION = false;
    private int mNavBarMode;
    private RotationButtonController mRotationButtonController;

    public RotationContextButton(int buttonResId, int iconResId) {
        super(buttonResId, iconResId);
        this.mNavBarMode = 0;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public void setRotationButtonController(RotationButtonController rotationButtonController) {
        this.mRotationButtonController = rotationButtonController;
    }

    @Override // com.android.systemui.statusbar.phone.ContextualButton, com.android.systemui.statusbar.phone.ButtonDispatcher
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        KeyButtonDrawable currentDrawable = getImageDrawable();
        if (visibility == 0 && currentDrawable != null) {
            currentDrawable.resetAnimation();
            currentDrawable.startAnimation();
        }
    }

    @Override // com.android.systemui.statusbar.phone.ContextualButton
    protected KeyButtonDrawable getNewDrawable() {
        Context context = new ContextThemeWrapper(getContext().getApplicationContext(), this.mRotationButtonController.getStyleRes());
        return KeyButtonDrawable.create(context, this.mIconResId, false, null);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
    }

    @Override // com.android.systemui.statusbar.phone.RotationButton
    public boolean acceptRotationProposal() {
        View currentView = getCurrentView();
        return currentView != null && currentView.isAttachedToWindow();
    }
}
