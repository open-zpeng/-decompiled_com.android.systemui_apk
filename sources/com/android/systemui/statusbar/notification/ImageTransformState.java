package com.android.systemui.statusbar.notification;

import android.graphics.drawable.Icon;
import android.util.Pools;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.notification.row.HybridNotificationView;
/* loaded from: classes21.dex */
public class ImageTransformState extends TransformState {
    public static final long ANIMATION_DURATION_LENGTH = 210;
    public static final int ICON_TAG = R.id.image_icon_tag;
    private static Pools.SimplePool<ImageTransformState> sInstancePool = new Pools.SimplePool<>(40);
    private Icon mIcon;

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void initFrom(View view, TransformState.TransformInfo transformInfo) {
        super.initFrom(view, transformInfo);
        if (view instanceof ImageView) {
            this.mIcon = (Icon) view.getTag(ICON_TAG);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public boolean sameAs(TransformState otherState) {
        if (super.sameAs(otherState)) {
            return true;
        }
        if (otherState instanceof ImageTransformState) {
            Icon icon = this.mIcon;
            return icon != null && icon.sameAs(((ImageTransformState) otherState).getIcon());
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void appear(float transformationAmount, TransformableView otherView) {
        if (otherView instanceof HybridNotificationView) {
            if (transformationAmount == 0.0f) {
                this.mTransformedView.setPivotY(0.0f);
                this.mTransformedView.setPivotX(this.mTransformedView.getWidth() / 2);
                prepareFadeIn();
            }
            float transformationAmount2 = mapToDuration(transformationAmount);
            CrossFadeHelper.fadeIn(this.mTransformedView, transformationAmount2, false);
            float transformationAmount3 = Interpolators.LINEAR_OUT_SLOW_IN.getInterpolation(transformationAmount2);
            this.mTransformedView.setScaleX(transformationAmount3);
            this.mTransformedView.setScaleY(transformationAmount3);
            return;
        }
        super.appear(transformationAmount, otherView);
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void disappear(float transformationAmount, TransformableView otherView) {
        if (otherView instanceof HybridNotificationView) {
            if (transformationAmount == 0.0f) {
                this.mTransformedView.setPivotY(0.0f);
                this.mTransformedView.setPivotX(this.mTransformedView.getWidth() / 2);
            }
            float transformationAmount2 = mapToDuration(1.0f - transformationAmount);
            CrossFadeHelper.fadeOut(this.mTransformedView, 1.0f - transformationAmount2, false);
            float transformationAmount3 = Interpolators.LINEAR_OUT_SLOW_IN.getInterpolation(transformationAmount2);
            this.mTransformedView.setScaleX(transformationAmount3);
            this.mTransformedView.setScaleY(transformationAmount3);
            return;
        }
        super.disappear(transformationAmount, otherView);
    }

    private static float mapToDuration(float scaleAmount) {
        return Math.max(Math.min(((360.0f * scaleAmount) - 150.0f) / 210.0f, 1.0f), 0.0f);
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public static ImageTransformState obtain() {
        ImageTransformState instance = (ImageTransformState) sInstancePool.acquire();
        if (instance != null) {
            return instance;
        }
        return new ImageTransformState();
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    protected boolean transformScale(TransformState otherState) {
        return sameAs(otherState);
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void recycle() {
        super.recycle();
        if (getClass() == ImageTransformState.class) {
            sInstancePool.release(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void reset() {
        super.reset();
        this.mIcon = null;
    }
}
