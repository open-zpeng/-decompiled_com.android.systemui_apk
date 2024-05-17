package com.android.systemui.statusbar.notification;

import android.util.Pools;
/* loaded from: classes21.dex */
public class ActionListTransformState extends TransformState {
    private static Pools.SimplePool<ActionListTransformState> sInstancePool = new Pools.SimplePool<>(40);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public boolean sameAs(TransformState otherState) {
        return otherState instanceof ActionListTransformState;
    }

    public static ActionListTransformState obtain() {
        ActionListTransformState instance = (ActionListTransformState) sInstancePool.acquire();
        if (instance != null) {
            return instance;
        }
        return new ActionListTransformState();
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void transformViewFullyFrom(TransformState otherState, float transformationAmount) {
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void transformViewFullyTo(TransformState otherState, float transformationAmount) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.TransformState
    public void resetTransformedView() {
        float y = getTransformedView().getTranslationY();
        super.resetTransformedView();
        getTransformedView().setTranslationY(y);
    }

    @Override // com.android.systemui.statusbar.notification.TransformState
    public void recycle() {
        super.recycle();
        sInstancePool.release(this);
    }
}
