package com.xiaopeng.systemui.infoflow.helper;

import android.graphics.PointF;
import android.view.animation.Interpolator;
/* loaded from: classes24.dex */
public class XBesselCurve3Interpolator implements Interpolator {
    private static final float STEP_SIZE = 2.4414062E-4f;
    private int mLastI;
    private final PointF point1;
    private final PointF point2;

    public XBesselCurve3Interpolator() {
        this(0.2f, 0.0f, 0.2f, 1.0f);
    }

    public XBesselCurve3Interpolator(float x1, float y1, float x2, float y2) {
        this.mLastI = 0;
        this.point1 = new PointF();
        this.point2 = new PointF();
        PointF pointF = this.point1;
        pointF.x = x1;
        pointF.y = y1;
        PointF pointF2 = this.point2;
        pointF2.x = x2;
        pointF2.y = y2;
    }

    public void set(float x1, float y1, float x2, float y2) {
        PointF pointF = this.point1;
        pointF.x = x1;
        pointF.y = y1;
        PointF pointF2 = this.point2;
        pointF2.x = x2;
        pointF2.y = y2;
        this.mLastI = 0;
    }

    public void reset() {
        this.mLastI = 0;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        float t = input;
        if (input == 0.0f) {
            this.mLastI = 0;
        }
        int i = this.mLastI;
        while (true) {
            if (i >= 4096) {
                break;
            }
            t = i * STEP_SIZE;
            double tempX = cubicEquation(t, this.point1.x, this.point2.x);
            if (tempX < input) {
                i++;
            } else {
                this.mLastI = i;
                break;
            }
        }
        double tempX2 = t;
        double value = cubicEquation(tempX2, this.point1.y, this.point2.y);
        if (input == 1.0f) {
            this.mLastI = 0;
        }
        return (float) value;
    }

    public static double cubicEquation(double t, double p1, double p2) {
        double u = 1.0d - t;
        double tt = t * t;
        double uu = u * u;
        double ttt = tt * t;
        return (uu * 3.0d * t * p1) + (3.0d * u * tt * p2) + ttt;
    }
}
