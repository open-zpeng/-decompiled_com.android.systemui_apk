package com.xiaopeng.module.aiavatar.graphics.animation;
/* loaded from: classes23.dex */
public class AlphaAnimation extends Animation {
    private float mCurrentAlpValue;
    private float mFromAlpha;
    private float mToAlpha;

    public AlphaAnimation(float fromAlpha, float toAlpha) {
        this.mFromAlpha = fromAlpha;
        this.mToAlpha = toAlpha;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        float alpha = this.mFromAlpha;
        this.mCurrentAlpValue = ((this.mToAlpha - alpha) * interpolatedTime) + alpha;
        t.setAlpha(this.mCurrentAlpValue);
    }

    public float getCurrentAlpValue() {
        return this.mCurrentAlpValue;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean willChangeBounds() {
        return false;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.animation.Animation
    public boolean hasAlpha() {
        return true;
    }
}
