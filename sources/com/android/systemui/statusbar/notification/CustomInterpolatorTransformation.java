package com.android.systemui.statusbar.notification;

import android.view.View;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
/* loaded from: classes21.dex */
public abstract class CustomInterpolatorTransformation extends ViewTransformationHelper.CustomTransformation {
    private final int mViewType;

    public CustomInterpolatorTransformation(int viewType) {
        this.mViewType = viewType;
    }

    @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
    public boolean transformTo(TransformState ownState, TransformableView notification, float transformationAmount) {
        TransformState otherState;
        if (hasCustomTransformation() && (otherState = notification.getCurrentState(this.mViewType)) != null) {
            View view = ownState.getTransformedView();
            CrossFadeHelper.fadeOut(view, transformationAmount);
            ownState.transformViewFullyTo(otherState, this, transformationAmount);
            otherState.recycle();
            return true;
        }
        return false;
    }

    protected boolean hasCustomTransformation() {
        return true;
    }

    @Override // com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation
    public boolean transformFrom(TransformState ownState, TransformableView notification, float transformationAmount) {
        TransformState otherState;
        if (hasCustomTransformation() && (otherState = notification.getCurrentState(this.mViewType)) != null) {
            View view = ownState.getTransformedView();
            CrossFadeHelper.fadeIn(view, transformationAmount);
            ownState.transformViewFullyFrom(otherState, this, transformationAmount);
            otherState.recycle();
            return true;
        }
        return false;
    }
}
