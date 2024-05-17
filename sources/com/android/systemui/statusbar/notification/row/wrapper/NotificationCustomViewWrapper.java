package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.view.View;
import com.android.internal.graphics.ColorUtils;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public class NotificationCustomViewWrapper extends NotificationViewWrapper {
    private boolean mIsLegacy;
    private int mLegacyColor;

    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationCustomViewWrapper(Context ctx, View view, ExpandableNotificationRow row) {
        super(ctx, view, row);
        this.mLegacyColor = row.getContext().getColor(R.color.notification_legacy_background_color);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.mView.setAlpha(visible ? 1.0f : 0.0f);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void onContentUpdated(ExpandableNotificationRow row) {
        super.onContentUpdated(row);
        if (needsInversion(this.mBackgroundColor, this.mView)) {
            invertViewLuminosity(this.mView);
            float[] hsl = {0.0f, 0.0f, 0.0f};
            ColorUtils.colorToHSL(this.mBackgroundColor, hsl);
            if (this.mBackgroundColor != 0 && hsl[2] > 0.5d) {
                hsl[2] = 1.0f - hsl[2];
                this.mBackgroundColor = ColorUtils.HSLToColor(hsl);
            }
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    protected boolean shouldClearBackgroundOnReapply() {
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public int getCustomBackgroundColor() {
        int customBackgroundColor = super.getCustomBackgroundColor();
        if (customBackgroundColor == 0 && this.mIsLegacy) {
            return this.mLegacyColor;
        }
        return customBackgroundColor;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void setLegacy(boolean legacy) {
        super.setLegacy(legacy);
        this.mIsLegacy = legacy;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public boolean shouldClipToRounding(boolean topRounded, boolean bottomRounded) {
        return true;
    }
}
