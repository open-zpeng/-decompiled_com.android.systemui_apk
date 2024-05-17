package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import com.xiaopeng.systemui.infoflow.effect.CrestTroughInterpolator;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class ParticleEffect extends AbsEffect {
    private static final float COUNT_DEVIATION = 0.15f;
    private static final float SPEED_DEVIATION = 0.2f;
    private static final float SPEED_OFFSET = 0.005f;
    private static final float SPEED_RANGE = 0.015f;
    private int count;
    private ArrayList<ParticleState> particles = new ArrayList<>();
    private PathMeasure pathMeasure = new PathMeasure();
    private float[] tempPos = new float[2];
    private float[] tempTan = new float[2];
    private Paint mPaint = new Paint();
    private int mParticlesColorFrom = -65536;
    private int mParticlesColorTo = -65536;
    private CrestTroughInterpolator mCrestTroughInterpolator = new CrestTroughInterpolator();
    private float mPositionPercentageX = 0.5f;
    private float mPositionPercentageY = 0.5f;
    private float mSpeedOffset = SPEED_OFFSET;
    private float mSpeedRange = SPEED_RANGE;
    private float mSpeedDeviation = 0.2f;
    private float mCountDeviation = COUNT_DEVIATION;
    private float mStartDegree = 0.0f;
    private float mEndDegree = 360.0f;

    public ParticleEffect(int count) {
        this.mCrestTroughInterpolator.addListener(new CrestTroughInterpolator.Listener() { // from class: com.xiaopeng.systemui.infoflow.effect.ParticleEffect.1
            @Override // com.xiaopeng.systemui.infoflow.effect.CrestTroughInterpolator.Listener
            public void onTrough(float value) {
            }

            @Override // com.xiaopeng.systemui.infoflow.effect.CrestTroughInterpolator.Listener
            public void onCrest(float value) {
                ParticleEffect.this.setSpeed(value);
            }
        });
        this.count = count;
    }

    public float getSpeedOffset() {
        return this.mSpeedOffset;
    }

    public void setSpeedOffset(float speedOffset) {
        this.mSpeedOffset = speedOffset;
    }

    public float getSpeedRange() {
        return this.mSpeedRange;
    }

    public void setSpeedRange(float speedRange) {
        this.mSpeedRange = speedRange;
    }

    public float getSpeedDeviation() {
        return this.mSpeedDeviation;
    }

    public void setSpeedDeviation(float speedDeviation) {
        this.mSpeedDeviation = speedDeviation;
    }

    public float getCountDeviation() {
        return this.mCountDeviation;
    }

    public void setCountDeviation(float countDeviation) {
        this.mCountDeviation = countDeviation;
    }

    public float getStartDegree() {
        return this.mStartDegree;
    }

    public void setStartDegree(float startDegree) {
        this.mStartDegree = startDegree;
    }

    public float getEndDegree() {
        return this.mEndDegree;
    }

    public void setEndDegree(float endDegree) {
        this.mEndDegree = endDegree;
    }

    public void setDegree(float startDegree, float endDegree) {
        this.mStartDegree = startDegree;
        this.mEndDegree = endDegree;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    protected void onDraw(Canvas canvas) {
        if (!isRunning()) {
            return;
        }
        Iterator<ParticleState> it = this.particles.iterator();
        while (it.hasNext()) {
            ParticleState state = it.next();
            if (state.speed >= 0.0f) {
                state.progress += state.speed;
                if (state.progress > 1.0f) {
                    state.progress = 0.0f;
                    state.random(this.mStartDegree, this.mEndDegree);
                    state.speed = -1.0f;
                } else {
                    this.pathMeasure.setPath(state.path, false);
                    float distance = this.pathMeasure.getLength() * state.progress;
                    this.pathMeasure.getPosTan(distance, this.tempPos, this.tempTan);
                    int x = (int) (this.tempPos[0] + ((this.mPositionPercentageX - 0.5d) * getWidth()));
                    int y = (int) (this.tempPos[1] + ((this.mPositionPercentageY - 0.5d) * getHeight()));
                    canvas.drawCircle(x, y, Math.min(state.particleWidth, state.particleHeight) >> 1, this.mPaint);
                }
            }
        }
    }

    public void setParticlesColor(int colorFrom, int colorTo) {
        int width = getWidth();
        int height = getHeight();
        int centerX = width >> 1;
        int centerY = height >> 1;
        this.mParticlesColorFrom = colorFrom;
        this.mParticlesColorTo = colorTo;
        if (centerX > 0) {
            this.mPaint.setShader(new RadialGradient(centerX, centerY, centerX, colorFrom, colorTo, Shader.TileMode.CLAMP));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onSizeChange(int w, int h) {
        super.onSizeChange(w, h);
        setParticlesColor(this.mParticlesColorFrom, this.mParticlesColorTo);
        for (int i = 1; i <= this.count; i++) {
            int v = (int) ((Math.random() * 10.0d) + 5.0d);
            Path path = new Path();
            ParticleState particleState = new ParticleState(path, -1.0f, 0.0f, v, v, w, h);
            particleState.randomPath(this.mStartDegree, this.mEndDegree);
            this.particles.add(particleState);
        }
    }

    public void setSpeed(float value) {
        int maxCount = Math.max(4, (int) (this.particles.size() * this.mCountDeviation * value));
        int count = 0;
        Iterator<ParticleState> it = this.particles.iterator();
        while (it.hasNext()) {
            ParticleState particleState = it.next();
            if (particleState.speed == -1.0f) {
                count++;
                if (count <= maxCount) {
                    particleState.speed = (float) ((this.mSpeedOffset + (this.mSpeedRange * value)) * ((1.0f - this.mSpeedDeviation) + (Math.random() * this.mSpeedDeviation)));
                } else {
                    return;
                }
            }
        }
    }

    public float getPositionPercentageX() {
        return this.mPositionPercentageX;
    }

    public void setPositionPercentageX(float positionPercentageX) {
        this.mPositionPercentageX = positionPercentageX;
    }

    public float getPositionPercentageY() {
        return this.mPositionPercentageY;
    }

    public void setPositionPercentageY(float positionPercentageY) {
        this.mPositionPercentageY = positionPercentageY;
    }

    public void setPositionPercentage(float positionPercentageX, float positionPercentageY) {
        this.mPositionPercentageX = positionPercentageX;
        this.mPositionPercentageY = positionPercentageY;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect, com.xiaopeng.systemui.infoflow.effect.Effect
    public void update(float value, boolean shouldFilter) {
        super.update(value, shouldFilter);
        if (shouldFilter) {
            this.mCrestTroughInterpolator.update(0.0f);
        } else {
            this.mCrestTroughInterpolator.update(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public static class ParticleState {
        int height;
        int particleHeight;
        int particleWidth;
        final Path path;
        float progress;
        float speed;
        int width;
        int minSize = 5;
        int maxSize = 20;

        ParticleState(Path path, float speed, float progress, int particleWidth, int particleHeight, int width, int height) {
            this.path = path;
            this.speed = speed;
            this.progress = progress;
            this.particleWidth = particleWidth;
            this.particleHeight = particleHeight;
            this.width = width;
            this.height = height;
        }

        void randomSize() {
            int r = (int) (this.minSize + (Math.random() * (this.maxSize - this.minSize)));
            this.particleWidth = r;
            this.particleHeight = r;
        }

        void randomPath(float startDegree, float endDegree) {
            this.path.reset();
            float p0x = this.width >> 1;
            float p0y = this.height >> 1;
            this.path.moveTo(p0x, p0y);
            float degree = (float) (((startDegree / 180.0f) * 3.141592653589793d) + ((float) (Math.random() * ((endDegree - startDegree) / 180.0f) * 3.141592653589793d)));
            float dx = (float) (Math.sin(degree) * p0x);
            float dy = (float) (Math.cos(degree) * p0x);
            this.path.lineTo(p0x + dx, p0y - dy);
        }

        void random(float startDegree, float endDegree) {
            randomSize();
            randomPath(startDegree, endDegree);
        }

        private PointF findEndPoint(float w, float h, float degree) {
            float x;
            float y;
            if (degree >= 0.0f && degree <= 360.0f) {
                if (w >= 0.0f && h >= 0.0f) {
                    double a = (Math.atan(w / h) / 3.141592653589793d) * 180.0d;
                    double tan = Math.tan((degree / 180.0d) * 3.141592653589793d);
                    if (degree < a) {
                        x = w;
                        y = (float) (w * tan);
                    } else if (degree < 180.0d - a) {
                        y = h;
                        x = (float) (h / tan);
                    } else if (degree < 180.0d + a) {
                        x = -w;
                        y = (float) (x * tan);
                    } else if (degree < 360.0d - a) {
                        y = -h;
                        x = (float) (y / tan);
                    } else {
                        x = w;
                        y = (float) (w * tan);
                    }
                    return new PointF(x + w, h - y);
                }
                throw new RuntimeException("w, h should be >0, w = " + w + ", h = " + h);
            }
            throw new RuntimeException("degrees should be [0, 360], angle  =" + degree);
        }
    }
}
