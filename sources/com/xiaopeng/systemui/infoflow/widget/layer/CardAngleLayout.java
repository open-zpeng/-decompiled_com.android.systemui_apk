package com.xiaopeng.systemui.infoflow.widget.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class CardAngleLayout extends AbsCardLayout {
    private static final String TAG = "CardAngleLayout";
    float angleX;
    float angleY;
    Bitmap bitmap;
    HashMap<View, Integer> levelMap;
    float translateZ;

    public CardAngleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.translateZ = 0.0f;
        this.levelMap = new HashMap<>();
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.layer.AbsCardLayout
    protected void changeCamera(View child, Camera camera) {
        camera.rotateY(-this.angleX);
        camera.rotateX(this.angleY);
        Integer level = this.levelMap.get(child);
        if (level != null) {
            if (level.intValue() == 0) {
                camera.rotateX(this.angleY);
            }
            camera.translate(0.0f, 0.0f, (-this.translateZ) * level.intValue());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.layer.AbsCardLayout
    protected void drawSelf(Canvas canvas) {
    }

    public void angleMove(float targetAngleX, float targetAngleY) {
        this.angleX = targetAngleX;
        this.angleY = targetAngleY;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.widget.layer.AbsCardLayout, com.xiaopeng.systemui.infoflow.widget.ShimmerLayout, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.widget.ShimmerLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.bitmap = null;
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        initLevel();
    }

    private void initLevel() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            this.levelMap.put(child, Integer.valueOf(i));
        }
    }

    public void setProgress(int progress) {
        this.translateZ = -progress;
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
