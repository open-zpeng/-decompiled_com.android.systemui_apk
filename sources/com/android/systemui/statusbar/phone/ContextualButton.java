package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
/* loaded from: classes21.dex */
public class ContextualButton extends ButtonDispatcher {
    private ContextualButtonGroup mGroup;
    protected final int mIconResId;
    private ContextButtonListener mListener;

    /* loaded from: classes21.dex */
    public interface ContextButtonListener {
        void onVisibilityChanged(ContextualButton contextualButton, boolean z);
    }

    public ContextualButton(int buttonResId, int iconResId) {
        super(buttonResId);
        this.mIconResId = iconResId;
    }

    public void updateIcon() {
        if (getCurrentView() == null || !getCurrentView().isAttachedToWindow() || this.mIconResId == 0) {
            return;
        }
        KeyButtonDrawable currentDrawable = getImageDrawable();
        KeyButtonDrawable drawable = getNewDrawable();
        if (currentDrawable != null) {
            drawable.setDarkIntensity(currentDrawable.getDarkIntensity());
        }
        setImageDrawable(drawable);
    }

    @Override // com.android.systemui.statusbar.phone.ButtonDispatcher
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        KeyButtonDrawable currentDrawable = getImageDrawable();
        if (visibility != 0 && currentDrawable != null && currentDrawable.canAnimate()) {
            currentDrawable.clearAnimationCallbacks();
            currentDrawable.resetAnimation();
        }
        ContextButtonListener contextButtonListener = this.mListener;
        if (contextButtonListener != null) {
            contextButtonListener.onVisibilityChanged(this, visibility == 0);
        }
    }

    public void setListener(ContextButtonListener listener) {
        this.mListener = listener;
    }

    public boolean show() {
        ContextualButtonGroup contextualButtonGroup = this.mGroup;
        if (contextualButtonGroup != null) {
            return contextualButtonGroup.setButtonVisibility(getId(), true) == 0;
        }
        setVisibility(0);
        return true;
    }

    public boolean hide() {
        ContextualButtonGroup contextualButtonGroup = this.mGroup;
        if (contextualButtonGroup != null) {
            return contextualButtonGroup.setButtonVisibility(getId(), false) != 0;
        }
        setVisibility(4);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attachToGroup(ContextualButtonGroup group) {
        this.mGroup = group;
    }

    protected KeyButtonDrawable getNewDrawable() {
        return KeyButtonDrawable.create(getContext().getApplicationContext(), this.mIconResId, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Context getContext() {
        return getCurrentView().getContext();
    }
}
