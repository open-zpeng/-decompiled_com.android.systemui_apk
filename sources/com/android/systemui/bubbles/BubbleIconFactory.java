package com.android.systemui.bubbles;

import android.content.Context;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class BubbleIconFactory extends BaseIconFactory {
    /* JADX INFO: Access modifiers changed from: protected */
    public BubbleIconFactory(Context context) {
        super(context, context.getResources().getConfiguration().densityDpi, context.getResources().getDimensionPixelSize(R.dimen.individual_bubble_size));
    }

    public int getBadgeSize() {
        return this.mContext.getResources().getDimensionPixelSize(com.android.launcher3.icons.R.dimen.profile_badge_size);
    }
}
