package com.android.systemui.statusbar.notification.row.wrapper;

import android.app.Notification;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public abstract class NotificationViewWrapper implements TransformableView {
    protected int mBackgroundColor = 0;
    protected final ExpandableNotificationRow mRow;
    protected final View mView;

    public static NotificationViewWrapper wrap(Context ctx, View v, ExpandableNotificationRow row) {
        if (v.getId() == 16909507) {
            if ("bigPicture".equals(v.getTag())) {
                return new NotificationBigPictureTemplateViewWrapper(ctx, v, row);
            }
            if ("bigText".equals(v.getTag())) {
                return new NotificationBigTextTemplateViewWrapper(ctx, v, row);
            }
            if ("media".equals(v.getTag()) || "bigMediaNarrow".equals(v.getTag())) {
                return new NotificationMediaTemplateViewWrapper(ctx, v, row);
            }
            if ("messaging".equals(v.getTag())) {
                return new NotificationMessagingTemplateViewWrapper(ctx, v, row);
            }
            Class<? extends Notification.Style> style = row.getEntry().notification.getNotification().getNotificationStyle();
            if (Notification.DecoratedCustomViewStyle.class.equals(style)) {
                return new NotificationDecoratedCustomViewWrapper(ctx, v, row);
            }
            return new NotificationTemplateViewWrapper(ctx, v, row);
        } else if (v instanceof NotificationHeaderView) {
            return new NotificationHeaderViewWrapper(ctx, v, row);
        } else {
            return new NotificationCustomViewWrapper(ctx, v, row);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationViewWrapper(Context ctx, View view, ExpandableNotificationRow row) {
        this.mView = view;
        this.mRow = row;
        onReinflated();
    }

    public void onContentUpdated(ExpandableNotificationRow row) {
    }

    public void onReinflated() {
        if (shouldClearBackgroundOnReapply()) {
            this.mBackgroundColor = 0;
        }
        int backgroundColor = getBackgroundColor(this.mView);
        if (backgroundColor != 0) {
            this.mBackgroundColor = backgroundColor;
            this.mView.setBackground(new ColorDrawable(0));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean needsInversion(int defaultBackgroundColor, View view) {
        if (view == null) {
            return false;
        }
        Configuration configuration = this.mView.getResources().getConfiguration();
        boolean nightMode = (configuration.uiMode & 48) == 32;
        if (!nightMode || this.mRow.getEntry().targetSdk >= 29) {
            return false;
        }
        int background = getBackgroundColor(view);
        if (background == 0) {
            background = defaultBackgroundColor;
        }
        if (background == 0) {
            background = resolveBackgroundColor();
        }
        float[] hsl = {0.0f, 0.0f, 0.0f};
        ColorUtils.colorToHSL(background, hsl);
        if (hsl[1] != 0.0f) {
            return false;
        }
        boolean isLightGrayOrWhite = hsl[1] == 0.0f && ((double) hsl[2]) > 0.5d;
        if (isLightGrayOrWhite) {
            return true;
        }
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        return childrenNeedInversion(background, (ViewGroup) view);
    }

    @VisibleForTesting
    boolean childrenNeedInversion(int parentBackground, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return false;
        }
        int backgroundColor = getBackgroundColor(viewGroup);
        if (Color.alpha(backgroundColor) != 255) {
            backgroundColor = ColorUtils.setAlphaComponent(ContrastColorUtil.compositeColors(backgroundColor, parentBackground), 255);
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof TextView) {
                int foreground = ((TextView) child).getCurrentTextColor();
                if (ColorUtils.calculateContrast(foreground, backgroundColor) < 3.0d) {
                    return true;
                }
            } else if ((child instanceof ViewGroup) && childrenNeedInversion(backgroundColor, (ViewGroup) child)) {
                return true;
            }
        }
        return false;
    }

    protected int getBackgroundColor(View view) {
        if (view == null) {
            return 0;
        }
        Drawable background = view.getBackground();
        if (!(background instanceof ColorDrawable)) {
            return 0;
        }
        return ((ColorDrawable) background).getColor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invertViewLuminosity(View view) {
        Paint paint = new Paint();
        ColorMatrix matrix = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();
        matrix.setRGB2YUV();
        tmp.set(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
        matrix.postConcat(tmp);
        tmp.setYUV2RGB();
        matrix.postConcat(tmp);
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        view.setLayerType(2, paint);
    }

    protected boolean shouldClearBackgroundOnReapply() {
        return true;
    }

    public void updateExpandability(boolean expandable, View.OnClickListener onClickListener) {
    }

    public NotificationHeaderView getNotificationHeader() {
        return null;
    }

    public int getHeaderTranslation(boolean forceNoHeader) {
        return 0;
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public TransformState getCurrentState(int fadingView) {
        return null;
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, Runnable endRunnable) {
        CrossFadeHelper.fadeOut(this.mView, endRunnable);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, float transformationAmount) {
        CrossFadeHelper.fadeOut(this.mView, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification) {
        CrossFadeHelper.fadeIn(this.mView);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification, float transformationAmount) {
        CrossFadeHelper.fadeIn(this.mView, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void setVisible(boolean visible) {
        this.mView.animate().cancel();
        this.mView.setVisibility(visible ? 0 : 4);
    }

    public void setRemoved() {
    }

    public int getCustomBackgroundColor() {
        if (this.mRow.isSummaryWithChildren()) {
            return 0;
        }
        return this.mBackgroundColor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int resolveBackgroundColor() {
        int customBackgroundColor = getCustomBackgroundColor();
        if (customBackgroundColor != 0) {
            return customBackgroundColor;
        }
        return this.mView.getContext().getColor(17170886);
    }

    public void setLegacy(boolean legacy) {
    }

    public void setContentHeight(int contentHeight, int minHeightHint) {
    }

    public void setRemoteInputVisible(boolean visible) {
    }

    public void setIsChildInGroup(boolean isChildInGroup) {
    }

    public boolean isDimmable() {
        return true;
    }

    public boolean disallowSingleClick(float x, float y) {
        return false;
    }

    public int getMinLayoutHeight() {
        return 0;
    }

    public boolean shouldClipToRounding(boolean topRounded, boolean bottomRounded) {
        return false;
    }

    public void setHeaderVisibleAmount(float headerVisibleAmount) {
    }

    public int getExtraMeasureHeight() {
        return 0;
    }
}
