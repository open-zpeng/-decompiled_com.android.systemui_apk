package com.android.systemui.qs.external;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.VisibleForTesting;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class TileColorPicker {
    @VisibleForTesting
    static final int[] DISABLE_STATE_SET = {-16842910};
    @VisibleForTesting
    static final int[] ENABLE_STATE_SET = {16842910, 16843518};
    @VisibleForTesting
    static final int[] INACTIVE_STATE_SET = {-16843518};
    private static TileColorPicker sInstance;
    private ColorStateList mColorStateList;

    private TileColorPicker(Context context) {
        this.mColorStateList = context.getResources().getColorStateList(R.color.tint_color_selector, context.getTheme());
    }

    public static TileColorPicker getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TileColorPicker(context);
        }
        return sInstance;
    }

    public int getColor(int state) {
        if (state != 0) {
            if (state != 1) {
                return state != 2 ? this.mColorStateList.getColorForState(ENABLE_STATE_SET, 0) : this.mColorStateList.getColorForState(ENABLE_STATE_SET, 0);
            }
            return this.mColorStateList.getColorForState(INACTIVE_STATE_SET, 0);
        }
        return this.mColorStateList.getColorForState(DISABLE_STATE_SET, 0);
    }
}
