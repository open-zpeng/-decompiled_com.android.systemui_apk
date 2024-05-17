package com.android.systemui.qs;

import android.view.View;
import androidx.annotation.Nullable;
/* loaded from: classes21.dex */
public interface QSFooter {
    int getHeight();

    void setExpandClickListener(View.OnClickListener onClickListener);

    void setExpanded(boolean z);

    void setExpansion(float f);

    void setKeyguardShowing(boolean z);

    void setListening(boolean z);

    void setQSPanel(@Nullable QSPanel qSPanel);

    void setVisibility(int i);

    default void disable(int state1, int state2, boolean animate) {
    }
}
