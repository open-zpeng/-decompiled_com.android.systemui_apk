package com.xiaopeng.systemui.infoflow.widget.layer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
/* loaded from: classes24.dex */
public class CardImageView extends AppCompatImageView {
    float angleX;
    float angleY;
    Matrix matrix;

    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.matrix = new Matrix();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        this.matrix.reset();
        this.matrix.postScale(1.1f, 1.1f, getWidth() / 2, getHeight() / 2);
        int paddingH = getWidth() / 5;
        int paddingV = getHeight() / 5;
        int offsetX = (int) ((this.angleX / 45.0f) * paddingH);
        int offsetY = (int) ((this.angleY / 45.0f) * paddingV);
        this.matrix.postTranslate(offsetX, offsetY);
        canvas.concat(this.matrix);
        super.onDraw(canvas);
    }

    public void setAngle(float angleX, float angleY) {
        this.angleX = angleX;
        this.angleY = angleY;
        invalidate();
    }
}
