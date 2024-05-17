package com.xiaopeng.systemui.infoflow.widget.layer;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.widget.ShimmerLayout;
/* loaded from: classes24.dex */
public abstract class AbsCardLayout extends ShimmerLayout {
    private static final String TAG = "AbsCardLayout";
    Camera camera;
    float centerX;
    float centerY;
    private TextView debugTextView;
    private long index;
    boolean isCamera;
    Matrix matrix;
    boolean startCountTime;
    private long temp;
    private long time;
    private long totalTime;

    protected abstract void changeCamera(View view, Camera camera);

    protected abstract void drawSelf(Canvas canvas);

    public AbsCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isCamera = true;
        this.matrix = new Matrix();
        this.camera = new Camera();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.widget.ShimmerLayout, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.startCountTime) {
            this.temp = System.currentTimeMillis() - this.time;
            long j = this.temp;
            if (j < 400) {
                this.index++;
                this.totalTime += j;
                String msg = String.format("AvgTime:%s, time:%s", Long.valueOf(this.totalTime / this.index), Long.valueOf(this.temp));
                TextView textView = this.debugTextView;
                if (textView != null) {
                    textView.setText(msg);
                }
                log(msg);
            } else {
                log(String.format(" time:%s", Long.valueOf(j)));
            }
        } else {
            this.index = 0L;
            this.totalTime = 0L;
        }
        this.time = System.currentTimeMillis();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0) {
                if (this.isCamera) {
                    this.camera.save();
                    changeCamera(child, this.camera);
                    this.camera.getMatrix(this.matrix);
                    this.camera.restore();
                    this.centerX = getWidth() / 2;
                    this.centerY = getHeight() / 2;
                    this.matrix.preTranslate(-this.centerX, -this.centerY);
                    this.matrix.postTranslate(this.centerX, this.centerY);
                    canvas.save();
                    canvas.concat(this.matrix);
                    drawChild(canvas, child, getDrawingTime());
                    canvas.restore();
                } else {
                    drawChild(canvas, child, getDrawingTime());
                }
            }
        }
        boolean angleEnable = InfoFlowConfigDao.getInstance().getConfig().angleCardEnable;
        if (!angleEnable) {
            super.dispatchDraw(canvas);
        }
    }

    public void setDebugTextView(TextView textView) {
        this.debugTextView = textView;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.isCamera) {
            this.camera.save();
            changeCamera(this, this.camera);
            this.camera.getMatrix(this.matrix);
            this.camera.restore();
            this.centerX = getWidth() / 2;
            this.centerY = getHeight() / 2;
            this.matrix.preTranslate(-this.centerX, -this.centerY);
            this.matrix.postTranslate(this.centerX, this.centerY);
            canvas.save();
            canvas.concat(this.matrix);
            drawSelf(canvas);
            canvas.restore();
            return;
        }
        drawSelf(canvas);
    }

    public void setCamera(boolean isCamera) {
        this.isCamera = isCamera;
        invalidate();
    }

    protected void log(String msg) {
    }
}
