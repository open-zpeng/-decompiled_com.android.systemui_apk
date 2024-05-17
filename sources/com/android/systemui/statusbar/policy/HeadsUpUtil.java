package com.android.systemui.statusbar.policy;

import android.view.View;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public final class HeadsUpUtil {
    private static final int TAG_CLICKED_NOTIFICATION = R.id.is_clicked_heads_up_tag;

    public static void setIsClickedHeadsUpNotification(View view, boolean clicked) {
        view.setTag(TAG_CLICKED_NOTIFICATION, clicked ? true : null);
    }

    public static boolean isClickedHeadsUpNotification(View view) {
        Boolean clicked = (Boolean) view.getTag(TAG_CLICKED_NOTIFICATION);
        return clicked != null && clicked.booleanValue();
    }
}
