package com.android.systemui.statusbar.notification.row.wrapper;

import android.app.Notification;
import android.content.Context;
import android.util.ArraySet;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.widget.NotificationExpandButton;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.CustomInterpolatorTransformation;
import com.android.systemui.statusbar.notification.ImageTransformState;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.Stack;
/* loaded from: classes21.dex */
public class NotificationHeaderViewWrapper extends NotificationViewWrapper {
    private static final Interpolator LOW_PRIORITY_HEADER_CLOSE = new PathInterpolator(0.4f, 0.0f, 0.7f, 1.0f);
    protected int mColor;
    private NotificationExpandButton mExpandButton;
    private TextView mHeaderText;
    private ImageView mIcon;
    private boolean mIsLowPriority;
    protected NotificationHeaderView mNotificationHeader;
    private boolean mShowExpandButtonAtEnd;
    private boolean mTransformLowPriorityTitle;
    protected final ViewTransformationHelper mTransformationHelper;
    private ImageView mWorkProfileImage;

    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationHeaderViewWrapper(Context ctx, View view, ExpandableNotificationRow row) {
        super(ctx, view, row);
        this.mShowExpandButtonAtEnd = ctx.getResources().getBoolean(R.bool.config_showNotificationExpandButtonAtEnd) || NotificationUtils.useNewInterruptionModel(ctx);
        this.mTransformationHelper = new ViewTransformationHelper();
        this.mTransformationHelper.setCustomTransformation(new CustomInterpolatorTransformation(1) { // from class: com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper.1
            @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
            public Interpolator getCustomInterpolator(int interpolationType, boolean isFrom) {
                boolean isLowPriority = NotificationHeaderViewWrapper.this.mView instanceof NotificationHeaderView;
                if (interpolationType == 16) {
                    if ((!isLowPriority || isFrom) && (isLowPriority || !isFrom)) {
                        return NotificationHeaderViewWrapper.LOW_PRIORITY_HEADER_CLOSE;
                    }
                    return Interpolators.LINEAR_OUT_SLOW_IN;
                }
                return null;
            }

            @Override // com.android.systemui.statusbar.notification.CustomInterpolatorTransformation
            protected boolean hasCustomTransformation() {
                return NotificationHeaderViewWrapper.this.mIsLowPriority && NotificationHeaderViewWrapper.this.mTransformLowPriorityTitle;
            }
        }, 1);
        resolveHeaderViews();
        addAppOpsOnClickListener(row);
    }

    protected void resolveHeaderViews() {
        this.mIcon = (ImageView) this.mView.findViewById(16908294);
        this.mHeaderText = (TextView) this.mView.findViewById(16909076);
        this.mExpandButton = this.mView.findViewById(16908998);
        this.mWorkProfileImage = (ImageView) this.mView.findViewById(16909358);
        this.mNotificationHeader = this.mView.findViewById(16909265);
        this.mNotificationHeader.setShowExpandButtonAtEnd(this.mShowExpandButtonAtEnd);
        this.mColor = this.mNotificationHeader.getOriginalIconColor();
    }

    private void addAppOpsOnClickListener(ExpandableNotificationRow row) {
        this.mNotificationHeader.setAppOpsOnClickListener(row.getAppOpsOnClickListener());
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void onContentUpdated(ExpandableNotificationRow row) {
        super.onContentUpdated(row);
        this.mIsLowPriority = row.isLowPriority();
        this.mTransformLowPriorityTitle = (row.isChildInGroup() || row.isSummaryWithChildren()) ? false : true;
        ArraySet<View> previousViews = this.mTransformationHelper.getAllTransformingViews();
        resolveHeaderViews();
        updateTransformedTypes();
        addRemainingTransformTypes();
        updateCropToPaddingForImageViews();
        Notification notification = row.getStatusBarNotification().getNotification();
        this.mIcon.setTag(ImageTransformState.ICON_TAG, notification.getSmallIcon());
        this.mWorkProfileImage.setTag(ImageTransformState.ICON_TAG, notification.getSmallIcon());
        ArraySet<View> currentViews = this.mTransformationHelper.getAllTransformingViews();
        for (int i = 0; i < previousViews.size(); i++) {
            View view = previousViews.valueAt(i);
            if (!currentViews.contains(view)) {
                this.mTransformationHelper.resetTransformedView(view);
            }
        }
    }

    private void addRemainingTransformTypes() {
        this.mTransformationHelper.addRemainingTransformTypes(this.mView);
    }

    private void updateCropToPaddingForImageViews() {
        Stack<View> stack = new Stack<>();
        stack.push(this.mView);
        while (!stack.isEmpty()) {
            View child = stack.pop();
            if (child instanceof ImageView) {
                ((ImageView) child).setCropToPadding(true);
            } else if (child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) child;
                for (int i = 0; i < group.getChildCount(); i++) {
                    stack.push(group.getChildAt(i));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateTransformedTypes() {
        this.mTransformationHelper.reset();
        this.mTransformationHelper.addTransformedView(0, this.mIcon);
        if (this.mIsLowPriority) {
            this.mTransformationHelper.addTransformedView(1, this.mHeaderText);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void updateExpandability(boolean expandable, View.OnClickListener onClickListener) {
        this.mExpandButton.setVisibility(expandable ? 0 : 8);
        this.mNotificationHeader.setOnClickListener(expandable ? onClickListener : null);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public NotificationHeaderView getNotificationHeader() {
        return this.mNotificationHeader;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public TransformState getCurrentState(int fadingView) {
        return this.mTransformationHelper.getCurrentState(fadingView);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, Runnable endRunnable) {
        this.mTransformationHelper.transformTo(notification, endRunnable);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, float transformationAmount) {
        this.mTransformationHelper.transformTo(notification, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification) {
        this.mTransformationHelper.transformFrom(notification);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification, float transformationAmount) {
        this.mTransformationHelper.transformFrom(notification, transformationAmount);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void setIsChildInGroup(boolean isChildInGroup) {
        super.setIsChildInGroup(isChildInGroup);
        this.mTransformLowPriorityTitle = !isChildInGroup;
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper, com.android.systemui.statusbar.TransformableView
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.mTransformationHelper.setVisible(visible);
    }
}
