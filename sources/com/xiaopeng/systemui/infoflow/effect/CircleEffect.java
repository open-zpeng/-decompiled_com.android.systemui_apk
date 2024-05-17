package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.util.Iterator;
import java.util.LinkedList;
/* loaded from: classes24.dex */
public class CircleEffect extends AbsEffect {
    private static final String TAG = "CircleEffect";
    private OnUpdateFloatValueListener mOnUpdateFloatValueListener;
    public Paint mPaint = new Paint(1);
    public Paint mPaint2 = new Paint(1);
    public Paint mDashPaint = new Paint(1);
    private LinkedList<CircleState> mCircleStateList = new LinkedList<>();
    private float mCenterRadius = 60.0f;
    private int mLastDirection = 1;
    private SpringEffectInterpolator mSpringEffectInterpolator = new SpringEffectInterpolator();
    private boolean mShowFirstLine = true;

    /* loaded from: classes24.dex */
    public interface OnUpdateFloatValueListener {
        void onUpdate(float f);
    }

    public CircleEffect() {
        this.mPaint.setColor(-14890);
        this.mPaint.setStrokeWidth(4.0f);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint2.setColor(-48862);
        this.mPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        this.mPaint2.setMaskFilter(new BlurMaskFilter(11.0f, BlurMaskFilter.Blur.NORMAL));
        this.mPaint2.setStrokeWidth(4.0f);
        this.mPaint2.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint2.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint2.setStyle(Paint.Style.STROKE);
        this.mDashPaint.setColor(1727991845);
        this.mDashPaint.setStrokeWidth(2.0f);
        this.mDashPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mDashPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDashPaint.setStyle(Paint.Style.STROKE);
        this.mCircleStateList.add(new CircleState(60.0f, 1, 0.0f, 1, 1));
        CircleState circleState = new CircleState(120.0f, 4, 0.0f, 0, 1);
        circleState.tag = true;
        this.mCircleStateList.add(circleState);
        this.mCircleStateList.add(new CircleState(180.0f, 1, 0.0f, 1, 1));
        this.mCircleStateList.add(new CircleState(240.0f, 3, 145.0f, 0, -1));
        this.mCircleStateList.add(new CircleState(300.0f, 1, 0.0f, 1, 1));
        this.mCircleStateList.add(new CircleState(360.0f, 3, 145.0f, 0, -1));
    }

    private static void drawCircle(Canvas canvas, Paint paint, float centerX, float centerY, float radius, int count, float padding, float startAngleOffset) {
        float d = 360.0f / count;
        float paddingAngle = (float) ((padding / ((radius + radius) * 3.141592653589793d)) * 360.0d);
        float sweepAngle = d - (2.0f * paddingAngle);
        for (int i = 0; i < count; i++) {
            canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, startAngleOffset + (i * d) + paddingAngle, sweepAngle, false, paint);
        }
    }

    public boolean isShowFirstLine() {
        return this.mShowFirstLine;
    }

    public void setShowFirstLine(boolean show) {
        this.mShowFirstLine = show;
    }

    public SpringEffectInterpolator getSpringEffectInterpolator() {
        return this.mSpringEffectInterpolator;
    }

    private void log(Object obj) {
        Log.d(TAG, NavigationBarInflaterView.KEY_CODE_START + hashCode() + NavigationBarInflaterView.KEY_CODE_END + obj);
    }

    public void updateFloatValue(OnUpdateFloatValueListener updateFloatValueListener) {
        this.mOnUpdateFloatValueListener = updateFloatValueListener;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    protected void onDraw(Canvas canvas) {
        int width;
        float value;
        OnUpdateFloatValueListener onUpdateFloatValueListener;
        int width2 = canvas.getWidth();
        int height = canvas.getHeight();
        int centerX = width2 >> 1;
        int centerY = height >> 1;
        Iterator<CircleState> iterator = this.mCircleStateList.descendingIterator();
        LinkedList<CircleState> temp = null;
        while (iterator.hasNext()) {
            CircleState circleState = iterator.next();
            if (circleState.radius < 453.0f) {
                int i = circleState.type;
                if (i == 0) {
                    EffectInterpolator effectInterpolator = this.mSpringEffectInterpolator;
                    if (effectInterpolator == null) {
                        value = 0.0f;
                    } else {
                        value = effectInterpolator.getValue();
                    }
                    if (circleState.tag && (onUpdateFloatValueListener = this.mOnUpdateFloatValueListener) != null) {
                        onUpdateFloatValueListener.onUpdate(value);
                    }
                    float v = value;
                    circleState.offset = v;
                    circleState.move();
                    float r = circleState.radius + circleState.offset;
                    if (r <= this.mCenterRadius) {
                        width = width2;
                    } else {
                        width = width2;
                        drawCircle(canvas, this.mPaint, centerX, centerY, r, circleState.count, 20.0f, circleState.startAngleOffset);
                        drawCircle(canvas, this.mPaint2, centerX, centerY, r, circleState.count, 20.0f, circleState.startAngleOffset);
                    }
                } else if (i != 1) {
                    width = width2;
                } else {
                    circleState.move();
                    float radius = circleState.radius;
                    float angle = 360.0f / 6;
                    for (int i2 = 0; i2 < 6; i2++) {
                        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, (i2 * angle) + 1.0f, angle - 2.0f, false, this.mDashPaint);
                    }
                    width = width2;
                }
            } else {
                width = width2;
                iterator.remove();
                if (temp == null) {
                    temp = new LinkedList<>();
                }
                circleState.radius = 60.0f;
                if (circleState.type == 0) {
                    this.mLastDirection *= -1;
                    circleState.angleDirection = this.mLastDirection;
                }
                temp.addFirst(circleState);
            }
            width2 = width;
        }
        if (this.mShowFirstLine) {
            float f = this.mCenterRadius;
            canvas.drawArc(centerX - f, centerY - f, centerX + f, centerY + f, 0.0f, 360.0f, false, this.mPaint);
            float f2 = this.mCenterRadius;
            canvas.drawArc(centerX - f2, centerY - f2, centerX + f2, centerY + f2, 0.0f, 360.0f, false, this.mPaint2);
        }
        if (temp != null) {
            this.mCircleStateList.addAll(0, temp);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onUpdate(float value) {
        this.mSpringEffectInterpolator.update(value);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
        this.mPaint2.setAlpha(alpha);
        this.mDashPaint.setAlpha(alpha);
    }

    /* loaded from: classes24.dex */
    private static class CircleState {
        public static final int TYPE_DASH = 1;
        public static final int TYPE_SOLID = 0;
        int angleDirection;
        int count;
        float offset;
        float radius;
        float startAngleOffset;
        boolean tag = false;
        int type;

        public CircleState(float radius, int count, float startAngleOffset, int type, int angleDirection) {
            this.radius = radius;
            this.count = count;
            this.startAngleOffset = startAngleOffset;
            this.type = type;
            this.angleDirection = angleDirection;
        }

        void move() {
            this.radius = (float) (this.radius + 1.2d);
            this.startAngleOffset += this.angleDirection * 2.0f;
            this.startAngleOffset %= 360.0f;
        }
    }
}
