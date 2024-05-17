package com.android.systemui.statusbar.phone;

import android.view.View;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public interface RotationButton {
    View getCurrentView();

    KeyButtonDrawable getImageDrawable();

    boolean hide();

    boolean isVisible();

    void setDarkIntensity(float f);

    void setOnClickListener(View.OnClickListener onClickListener);

    void setOnHoverListener(View.OnHoverListener onHoverListener);

    void setRotationButtonController(RotationButtonController rotationButtonController);

    boolean show();

    void updateIcon();

    default void setCanShowRotationButton(boolean canShow) {
    }

    default boolean acceptRotationProposal() {
        return getCurrentView() != null;
    }
}
