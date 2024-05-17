package com.android.systemui.statusbar.notification;

import android.util.Pools;
import android.view.View;
import com.android.internal.widget.MessagingImageMessage;
import com.android.systemui.R;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.TransformState;
/* loaded from: classes21.dex */
public class MessagingImageTransformState extends ImageTransformState {
    private MessagingImageMessage mImageMessage;
    private static Pools.SimplePool<MessagingImageTransformState> sInstancePool = new Pools.SimplePool<>(40);
    private static final int START_ACTUAL_WIDTH = R.id.transformation_start_actual_width;
    private static final int START_ACTUAL_HEIGHT = R.id.transformation_start_actual_height;

    @Override // com.android.systemui.statusbar.notification.ImageTransformState, com.android.systemui.statusbar.notification.TransformState
    public void initFrom(View view, TransformState.TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        this.mImageMessage = (MessagingImageMessage) view;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.ImageTransformState, com.android.systemui.statusbar.notification.TransformState
    public boolean sameAs(TransformState otherState) {
        if (super.sameAs(otherState)) {
            return true;
        }
        if (otherState instanceof MessagingImageTransformState) {
            MessagingImageTransformState otherMessage = (MessagingImageTransformState) otherState;
            return this.mImageMessage.sameAs(otherMessage.mImageMessage);
        }
        return false;
    }

    public static MessagingImageTransformState obtain() {
        MessagingImageTransformState instance = (MessagingImageTransformState) sInstancePool.acquire();
        if (instance != null) {
            return instance;
        }
        return new MessagingImageTransformState();
    }

    @Override // com.android.systemui.statusbar.notification.ImageTransformState, com.android.systemui.statusbar.notification.TransformState
    protected boolean transformScale(TransformState otherState) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void transformViewFrom(TransformState otherState, int transformationFlags, ViewTransformationHelper.CustomTransformation customTransformation, float transformationAmount) {
        MessagingImageMessage messagingImageMessage;
        MessagingImageMessage messagingImageMessage2;
        super.transformViewFrom(otherState, transformationFlags, customTransformation, transformationAmount);
        float interpolatedValue = this.mDefaultInterpolator.getInterpolation(transformationAmount);
        if ((otherState instanceof MessagingImageTransformState) && sameAs(otherState)) {
            MessagingImageMessage otherMessage = ((MessagingImageTransformState) otherState).mImageMessage;
            if (transformationAmount == 0.0f) {
                setStartActualWidth(otherMessage.getActualWidth());
                setStartActualHeight(otherMessage.getActualHeight());
            }
            float startActualWidth = getStartActualWidth();
            this.mImageMessage.setActualWidth((int) NotificationUtils.interpolate(startActualWidth, messagingImageMessage.getStaticWidth(), interpolatedValue));
            float startActualHeight = getStartActualHeight();
            this.mImageMessage.setActualHeight((int) NotificationUtils.interpolate(startActualHeight, messagingImageMessage2.getHeight(), interpolatedValue));
        }
    }

    public int getStartActualWidth() {
        Object tag = this.mTransformedView.getTag(START_ACTUAL_WIDTH);
        if (tag == null) {
            return -1;
        }
        return ((Integer) tag).intValue();
    }

    public void setStartActualWidth(int actualWidth) {
        this.mTransformedView.setTag(START_ACTUAL_WIDTH, Integer.valueOf(actualWidth));
    }

    public int getStartActualHeight() {
        Object tag = this.mTransformedView.getTag(START_ACTUAL_HEIGHT);
        if (tag == null) {
            return -1;
        }
        return ((Integer) tag).intValue();
    }

    public void setStartActualHeight(int actualWidth) {
        this.mTransformedView.setTag(START_ACTUAL_HEIGHT, Integer.valueOf(actualWidth));
    }

    @Override // com.android.systemui.statusbar.notification.ImageTransformState, com.android.systemui.statusbar.notification.TransformState
    public void recycle() {
        super.recycle();
        if (getClass() == MessagingImageTransformState.class) {
            sInstancePool.release(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void resetTransformedView() {
        super.resetTransformedView();
        MessagingImageMessage messagingImageMessage = this.mImageMessage;
        messagingImageMessage.setActualWidth(messagingImageMessage.getStaticWidth());
        MessagingImageMessage messagingImageMessage2 = this.mImageMessage;
        messagingImageMessage2.setActualHeight(messagingImageMessage2.getHeight());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.ImageTransformState, com.android.systemui.statusbar.notification.TransformState
    public void reset() {
        super.reset();
        this.mImageMessage = null;
    }
}
