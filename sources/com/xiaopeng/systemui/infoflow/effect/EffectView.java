package com.xiaopeng.systemui.infoflow.effect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.util.AttributeSet;
import com.xiaopeng.systemui.infoflow.effect.p.PEffect;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import kotlin.UByte;
/* loaded from: classes24.dex */
public class EffectView extends BaseEffectView {
    private static final float[] FREQUENCY = {25.0f, 31.5f, 40.0f, 50.0f, 63.0f, 80.0f, 100.0f, 125.0f, 160.0f, 200.0f, 250.0f, 315.0f, 400.0f, 500.0f, 630.0f, 800.0f, 1000.0f, 1250.0f, 1600.0f, 2000.0f, 2500.0f, 3150.0f, 4000.0f, 5000.0f, 6300.0f, 8000.0f, 10000.0f, 12500.0f, 16000.0f, 20000.0f};
    private static final double[] K = {3.38844156139202E-5d, 1.14815362149688E-4d, 3.46736850452532E-4d, 9.54992586021436E-4d, 0.00239883291901949d, 0.00562341325190349d, 0.0123026877081238d, 0.0245470891568503d, 0.0457088189614875d, 0.0812830516164099d, 0.138038426460288d, 0.218776162394955d, 0.331131121482591d, 0.478630092322638d, 0.645654229034656d, 0.831763771102671d, 1.0d, 1.14815362149688d, 1.25892541179417d, 1.31825673855641d, 1.34896288259165d, 1.31825673855641d, 1.25892541179417d, 1.12201845430196d, 0.977237220955811d, 0.776247116628692d, 0.562341325190349d, 0.371535229097173d, 0.218776162394955d, 0.117489755493953d};
    private static final long P_CHANGE_STYLE_INTERVAL = 5000;
    private static final long P_WINK_INTERVAL = 3000;
    public static final int STYLE_CIRCLE = 0;
    public static final int STYLE_P = 3;
    public static final int STYLE_POLYGON = 2;
    public static final int STYLE_TRIANGLE = 1;
    private boolean mAutoChangePStyle;
    private boolean mAutoWink;
    private BreathingEffect mBreathingEffect;
    private Runnable mChangeStyleRunnable;
    private CircleEffect mCircleEffect;
    private int mCurrentStyleIndex;
    private DynamicExtremeValue mDynamicExtremeValue;
    private List<Effect> mEffectList;
    private byte[] mFft;
    private float mFilterMinValue;
    private Handler mHandler;
    private PEffect mPEffect;
    private ParticleEffect mParticleEffect;
    private PolygonEffect mPolygonEffect;
    private int mStyleCount;
    private TriangleEffect mTriangleEffect;
    private Runnable mWinkRunnable;

    public EffectView(Context context) {
        this(context, null);
    }

    public EffectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mEffectList = new LinkedList();
        this.mAutoWink = false;
        this.mAutoChangePStyle = false;
        this.mCircleEffect = new CircleEffect();
        this.mPolygonEffect = new PolygonEffect();
        this.mTriangleEffect = new TriangleEffect();
        this.mParticleEffect = new ParticleEffect(96);
        this.mBreathingEffect = new BreathingEffect(1728005199, 1727994133);
        this.mPEffect = new PEffect();
        this.mDynamicExtremeValue = new DynamicExtremeValue(25);
        this.mStyleCount = 3;
        this.mCurrentStyleIndex = -1;
        this.mFilterMinValue = 0.7f;
        this.mFft = null;
        this.mHandler = new Handler();
        this.mWinkRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.effect.EffectView.1
            @Override // java.lang.Runnable
            public void run() {
                EffectView.this.mPEffect.wink();
                if (EffectView.this.mAutoWink) {
                    EffectView.this.mHandler.postDelayed(this, EffectView.P_WINK_INTERVAL);
                }
            }
        };
        this.mChangeStyleRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.effect.EffectView.2
            @Override // java.lang.Runnable
            public void run() {
                EffectView.this.mPEffect.setStyle((int) (Math.random() * EffectView.this.mPEffect.getStyleCount()));
                if (EffectView.this.mAutoChangePStyle) {
                    EffectView.this.mHandler.postDelayed(this, EffectView.P_CHANGE_STYLE_INTERVAL);
                }
            }
        };
        this.mEffectList.add(this.mParticleEffect);
        this.mEffectList.add(this.mBreathingEffect);
        this.mEffectList.add(this.mCircleEffect);
        this.mEffectList.add(this.mTriangleEffect);
        this.mEffectList.add(this.mPolygonEffect);
        this.mEffectList.add(this.mPEffect);
        if (this.mAutoWink) {
            this.mHandler.post(this.mWinkRunnable);
        }
        if (this.mAutoChangePStyle) {
            this.mHandler.post(this.mChangeStyleRunnable);
        }
    }

    public void setAutoWink(boolean autoWink) {
        this.mAutoWink = autoWink;
        if (autoWink) {
            this.mHandler.post(this.mWinkRunnable);
        }
    }

    public void setAutoChangePStyle(boolean autoChangePStyle) {
        this.mAutoChangePStyle = autoChangePStyle;
        if (autoChangePStyle) {
            this.mHandler.post(this.mChangeStyleRunnable);
        }
    }

    private void updatePEffectColor() {
        int i = this.mCurrentStyleIndex;
        if (i == 0) {
            this.mPEffect.setColor(-4370, -21846);
        } else if (i == 1) {
            this.mPEffect.setColor(-1118465, -5592321);
        } else if (i == 2) {
            this.mPEffect.setColor(-1114130, -5570646);
        }
    }

    public PEffect getPEffect() {
        return this.mPEffect;
    }

    public void togglePEffect() {
        updatePEffectColor();
        PEffect pEffect = this.mPEffect;
        pEffect.setEnable(!pEffect.isEnable());
        this.mCircleEffect.setShowFirstLine(!this.mPEffect.isEnable());
        this.mPolygonEffect.setShowFirstLine(!this.mPEffect.isEnable());
        this.mTriangleEffect.setShowFirstLine(!this.mPEffect.isEnable());
        invalidateEffect();
    }

    public boolean isPEffectShowing() {
        return this.mPEffect.isEnable();
    }

    public void showPEffect(boolean show) {
        updatePEffectColor();
        this.mPEffect.setEnable(show);
        this.mCircleEffect.setShowFirstLine(show);
        this.mPolygonEffect.setShowFirstLine(show);
        this.mTriangleEffect.setShowFirstLine(show);
        invalidateEffect();
    }

    public float getFilterMinValue() {
        return this.mFilterMinValue;
    }

    public void setFilterMinValue(float filterMinValue) {
        this.mFilterMinValue = filterMinValue;
    }

    public void addEffect(Effect effect) {
        this.mEffectList.add(effect);
        invalidateEffect();
    }

    public void removeEffect(Effect effect) {
        effect.setEnable(false);
        invalidateEffect();
    }

    public void removeAllEffect() {
        for (Effect effect : this.mEffectList) {
            effect.setEnable(false);
        }
        invalidateEffect();
    }

    public CircleEffect getCircleEffect() {
        return this.mCircleEffect;
    }

    public PolygonEffect getPolygonEffect() {
        return this.mPolygonEffect;
    }

    public TriangleEffect getTriangleEffect() {
        return this.mTriangleEffect;
    }

    public float getMaxValue() {
        return this.mDynamicExtremeValue.getMaxValue();
    }

    public float getMinValue() {
        return this.mDynamicExtremeValue.getMinValue();
    }

    public BreathingEffect getBreathingEffect() {
        return this.mBreathingEffect;
    }

    public ParticleEffect getParticleEffect() {
        return this.mParticleEffect;
    }

    public void update(float value) {
        this.mDynamicExtremeValue.addValue(value);
        float maxValue = this.mDynamicExtremeValue.getMaxValue();
        float minValue = this.mDynamicExtremeValue.getMinValue();
        float p = maxValue != 0.0f ? value / maxValue : 0.0f;
        boolean filtered = value < this.mFilterMinValue * maxValue || value < 1.3f * minValue;
        for (Effect effect : this.mEffectList) {
            effect.update(p, filtered);
        }
    }

    public int getStyleCount() {
        return this.mStyleCount;
    }

    public void setStyleWithAnimation(final int index) {
        final float startY = getPositionPercentageY(this.mCurrentStyleIndex);
        final float endY = getPositionPercentageY(index);
        ValueAnimator disappearAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        disappearAnimator.setDuration(600L);
        disappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.effect.EffectView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                PEffect pEffect = EffectView.this.mPEffect;
                float f = startY;
                pEffect.setPositionPercentageY(f + ((1.0f - value) * (endY - f)));
                int alpha = (int) (255.0f * value);
                for (Effect effect : EffectView.this.mEffectList) {
                    if (!effect.equals(EffectView.this.mPEffect)) {
                        effect.setAlpha(alpha);
                    }
                }
            }
        });
        ValueAnimator appearAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        appearAnimator.setDuration(600L);
        appearAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.effect.EffectView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                EffectView.this.setStyleInternal(index);
            }
        });
        appearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.effect.EffectView.5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                int alpha = (int) (255.0f * value);
                for (Effect effect : EffectView.this.mEffectList) {
                    if (!effect.equals(EffectView.this.mPEffect)) {
                        effect.setAlpha(alpha);
                    }
                }
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(disappearAnimator, appearAnimator);
        animatorSet.start();
    }

    public void setStyle(int index) {
        setStyleInternal(index);
    }

    private float getPositionPercentageY(int style) {
        if (style == 0 || style == 1 || style != 2) {
            return 0.5f;
        }
        return 0.7f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStyleInternal(int index) {
        if (index == this.mCurrentStyleIndex) {
            return;
        }
        this.mCurrentStyleIndex = index;
        int i = this.mCurrentStyleIndex;
        if (i == 0) {
            for (Effect effect : this.mEffectList) {
                if (effect != this.mPEffect) {
                    effect.setEnable(false);
                }
            }
            this.mParticleEffect.setParticlesColor(1727987712, 301924352);
            this.mParticleEffect.setDegree(0.0f, 360.0f);
            this.mParticleEffect.setPositionPercentage(0.5f, 0.5f);
            this.mParticleEffect.setEnable(true);
            this.mBreathingEffect.setBreathingColor(-855703552, -855703552);
            this.mBreathingEffect.setPositionPercentage(0.5f, 0.5f);
            this.mBreathingEffect.setEnable(true);
            this.mCircleEffect.setEnable(true);
            this.mCircleEffect.setShowFirstLine(!this.mPEffect.isEnable());
            this.mPEffect.setPositionPercentage(0.5f, getPositionPercentageY(index));
            updatePEffectColor();
        } else if (i == 1) {
            for (Effect effect2 : this.mEffectList) {
                if (effect2 != this.mPEffect) {
                    effect2.setEnable(false);
                }
            }
            this.mBreathingEffect.setBreathingColor(-872414977, -872414977);
            this.mBreathingEffect.setPositionPercentage(0.5f, 0.5f);
            this.mBreathingEffect.setEnable(true);
            this.mParticleEffect.setParticlesColor(1711276287, 285212927);
            this.mParticleEffect.setPositionPercentage(0.5f, 0.5f);
            this.mParticleEffect.setDegree(0.0f, 360.0f);
            this.mParticleEffect.setEnable(true);
            this.mTriangleEffect.setEnable(true);
            this.mTriangleEffect.setShowFirstLine(!this.mPEffect.isEnable());
            this.mPEffect.setPositionPercentage(0.5f, getPositionPercentageY(index));
            updatePEffectColor();
        } else if (i == 2) {
            for (Effect effect3 : this.mEffectList) {
                if (effect3 != this.mPEffect) {
                    effect3.setEnable(false);
                }
            }
            this.mBreathingEffect.setBreathingColor(-872349952, -872349952);
            this.mBreathingEffect.setPositionPercentage(0.5f, 0.7f);
            this.mBreathingEffect.setEnable(true);
            this.mParticleEffect.setParticlesColor(855703296, 285277952);
            this.mParticleEffect.setPositionPercentage(0.5f, 0.7f);
            this.mParticleEffect.setDegree(-90.0f, 90.0f);
            this.mParticleEffect.setEnable(true);
            this.mPolygonEffect.setPositionPercentage(0.5f, 0.7f);
            this.mPolygonEffect.setEnable(true);
            this.mPolygonEffect.setShowFirstLine(!this.mPEffect.isEnable());
            this.mPEffect.setPositionPercentage(0.5f, getPositionPercentageY(index));
            updatePEffectColor();
        } else if (i == 3) {
            PEffect pEffect = this.mPEffect;
            pEffect.setEnable(!pEffect.isEnable());
            this.mCircleEffect.setShowFirstLine(!this.mPEffect.isEnable());
            this.mPolygonEffect.setShowFirstLine(!this.mPEffect.isEnable());
            this.mTriangleEffect.setShowFirstLine(!this.mPEffect.isEnable());
            updatePEffectColor();
        } else {
            throw new RuntimeException("Unknown style. index = " + index);
        }
        invalidateEffect();
    }

    public int getCurrentStyleIndex() {
        return this.mCurrentStyleIndex;
    }

    public void nextStyle() {
        int nextStyleIndex = this.mCurrentStyleIndex + 1;
        if (nextStyleIndex >= this.mStyleCount) {
            nextStyleIndex = 0;
        }
        setStyle(nextStyleIndex);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.BaseEffectView
    public void pause() {
        super.pause();
        for (Effect effect : this.mEffectList) {
            effect.onPause();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.BaseEffectView
    public void resume() {
        super.resume();
        for (Effect effect : this.mEffectList) {
            effect.onResume();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.ThreadView
    protected void onSurfaceDraw(Canvas canvas) {
        for (Effect effect : this.mEffectList) {
            if (effect.isEnable()) {
                int checkpoint = canvas.save();
                effect.performDraw(canvas);
                canvas.restoreToCount(checkpoint);
            }
        }
    }

    private float getValue(Visualizer visualizer, byte[] bytes, int samplingRate) {
        int i = 1;
        int frequencyCounts = (bytes.length / 2) + 1;
        byte[] bArr = this.mFft;
        if (bArr == null || bArr.length != frequencyCounts) {
            this.mFft = new byte[frequencyCounts];
        }
        this.mFft[0] = (byte) Math.abs((int) bytes[0]);
        for (int i2 = 1; i2 < frequencyCounts - 1; i2++) {
            this.mFft[i2] = (byte) Math.hypot(bytes[i2 * 2], bytes[(i2 * 2) + 1]);
        }
        this.mFft[frequencyCounts - 1] = (byte) Math.abs((int) bytes[1]);
        float frequencyEach = (samplingRate * 2.0f) / bytes.length;
        float result = 1.0f;
        int i3 = 0;
        while (i3 < this.mFft.length) {
            float hz = i3 * frequencyEach;
            int index = Arrays.binarySearch(FREQUENCY, hz);
            if (index < 0) {
                index = (-index) - 2;
            }
            int index2 = Math.max(0, Math.min(K.length - i, index));
            int fftValue = this.mFft[i3] & UByte.MAX_VALUE;
            result = (float) (result + (K[index2] * fftValue));
            i3++;
            i = 1;
        }
        return result;
    }

    protected void onUpdate(float value) {
        update(value);
    }

    public float updateFftDataCapture(byte[] bytes, int samplingRate) {
        return updateFftDataCapture(null, bytes, samplingRate);
    }

    public float updateFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
        return capture(visualizer, bytes, samplingRate);
    }

    private float capture(Visualizer visualizer, byte[] bytes, int samplingRate) {
        float size = getValue(visualizer, bytes, samplingRate);
        onUpdate(size);
        return size;
    }
}
