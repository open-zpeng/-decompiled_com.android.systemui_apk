package com.xiaopeng.systemui.infoflow.devtool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.widget.AppCompatImageView;
import com.xiaopeng.systemui.infoflow.widget.layer.Smoother;
/* loaded from: classes24.dex */
public class AngleImageView extends AppCompatImageView {
    private static final float MAX_ANGLE = 45.0f;
    private static final String TAG = "AngleImageView";
    private float ANGLE_RATIO;
    private long CHANGE_INTERVAL;
    float angleX;
    float angleY;
    private AngleViewTouchEventListener mAngleViewTouchEventListener;
    private Paint mCirclePaint;
    private long mLastChangeAngleTime;
    Smoother mSmoother;
    private int point_radius;
    private float x;
    private float y;

    /* loaded from: classes24.dex */
    public interface AngleViewTouchEventListener {
        void angleMove(float f, float f2);
    }

    public AngleImageView(Context context) {
        this(context, null);
    }

    public AngleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.point_radius = 20;
        this.ANGLE_RATIO = 5.0f;
        this.CHANGE_INTERVAL = 100L;
        init();
    }

    private void init() {
        this.mCirclePaint = new Paint();
        this.mCirclePaint.setColor(-1);
        this.mCirclePaint.setAntiAlias(true);
        this.mSmoother = new Smoother(new Smoother.CallBack() { // from class: com.xiaopeng.systemui.infoflow.devtool.AngleImageView.1
            @Override // com.xiaopeng.systemui.infoflow.widget.layer.Smoother.CallBack
            public void onCallBack(float currX, float currY) {
                if (AngleImageView.this.mAngleViewTouchEventListener != null) {
                    AngleImageView.this.mAngleViewTouchEventListener.angleMove(currX, currY);
                }
            }
        });
        this.mSmoother.setInterpolator(new DecelerateInterpolator(5.0f));
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x0031, code lost:
        if (r5 != 3) goto L7;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            float r0 = r9.getX()
            r8.x = r0
            float r0 = r9.getY()
            r8.y = r0
            int r0 = r8.getWidth()
            r1 = 2
            int r0 = r0 / r1
            float r0 = (float) r0
            int r2 = r8.getHeight()
            int r2 = r2 / r1
            float r2 = (float) r2
            float r3 = r8.x
            float r3 = r0 - r3
            float r3 = r3 / r0
            float r4 = r8.y
            float r4 = r2 - r4
            float r4 = r4 / r2
            int r5 = r9.getAction()
            r6 = 1000(0x3e8, float:1.401E-42)
            r7 = 1
            if (r5 == 0) goto L43
            if (r5 == r7) goto L3a
            if (r5 == r1) goto L34
            r1 = 3
            if (r5 == r1) goto L3a
            goto L47
        L34:
            r1 = 300(0x12c, float:4.2E-43)
            r8.touchDelay(r7, r1, r3, r4)
            goto L47
        L3a:
            r1 = 0
            r8.touchDelay(r7, r6, r1, r1)
            r8.x = r1
            r8.y = r1
            goto L47
        L43:
            r8.touchDelay(r7, r6, r3, r4)
        L47:
            r8.invalidate()
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.infoflow.devtool.AngleImageView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.x == 0.0f && this.y == 0.0f) {
            return;
        }
        canvas.drawCircle(this.x, this.y, this.point_radius, this.mCirclePaint);
    }

    public void setAngleViewTouchEventListener(AngleViewTouchEventListener angleViewTouchEventListener) {
        this.mAngleViewTouchEventListener = angleViewTouchEventListener;
    }

    void stopSmooth() {
        Smoother smoother = this.mSmoother;
        if (smoother != null) {
            smoother.stop();
        }
    }

    void touchDelay(boolean isAnim, int duration, float touchX, float touchY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.mLastChangeAngleTime < this.CHANGE_INTERVAL) {
            return;
        }
        this.mLastChangeAngleTime = currentTime;
        stopSmooth();
        float f = this.ANGLE_RATIO;
        float targetAngleX = touchX * f;
        float targetAngleY = f * touchY;
        if (targetAngleX > 0.0f) {
            targetAngleX = Math.min(targetAngleX, (float) MAX_ANGLE);
        } else if (targetAngleX < 0.0f) {
            targetAngleX = Math.max(targetAngleX, -45.0f);
        }
        if (targetAngleY > 0.0f) {
            targetAngleY = Math.min(targetAngleY, (float) MAX_ANGLE);
        } else if (targetAngleY < 0.0f) {
            targetAngleY = Math.max(targetAngleY, -45.0f);
        }
        if (isAnim && this.mSmoother != null) {
            float dx = targetAngleX - this.angleX;
            float dy = targetAngleY - this.angleY;
            float max = Math.max(Math.abs(dx), Math.abs(dy));
            Smoother smoother = this.mSmoother;
            float f2 = this.angleX;
            float f3 = this.angleY;
            smoother.start(f2, f3, targetAngleX - f2, targetAngleY - f3, (int) (duration + (5.0f * max)));
            post(this.mSmoother);
        }
    }
}
