package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.stack.StackStateAnimator;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class VoiceWaveViewByCode extends BaseVoiceWaveView {
    private static final int ANIM_DURATION = 600;
    private static final int CORNER_WAVE_NUM = 4;
    private static final int FRAME_RATIO = 40;
    private static final int MID_WAVE_NUM = 6;
    private static final int MSG_DESTROY_ANIM = 0;
    private static final int MSG_FINISH_FADE_OUT_ANIM = 2;
    private static final int MSG_INVALIDATE = 4;
    private static final int MSG_START_ANIM = 1;
    private static final int NOT_RECORD_WAVE_HEIGHT = 40;
    private static final int STATUS_INIT = 0;
    private static final int STATUS_START_00 = 1;
    private static final int STATUS_START_01 = 2;
    private static final int STATUS_START_12 = 4;
    private static final int STATUS_START_20 = 6;
    private static final int STATUS_START_HIDE = 8;
    private static final String TAG = "VoiceWaveView";
    private static final int WAVE_LOC_LEFT = 1;
    private static final int WAVE_LOC_MID = 0;
    private static final int WAVE_LOC_RIGHT = 2;
    protected int mBaseWaveHeight;
    private int[] mCornerWaveHeight;
    private int[] mCornerWaveOffset;
    private List<ValueAnimator> mCornerWaveValueAnimatorList;
    private Paint mCoverPaint;
    private ValueAnimator mFadeOutValueAnimator;
    private Handler mHandler;
    private int mHeight;
    protected int mInitBaseWaveHeight;
    private boolean mIsFadeOutAnimStarted;
    private int mMainWaveColor;
    private Paint mMainWavePaint;
    protected float[] mMidWaveFactor;
    protected int mMidWaveOffsetBase;
    private List<ValueAnimator> mMidWaveValueAnimatorList;
    private Path[] mPaths;
    private float mScale;
    private int mSecondaryWaveColor;
    protected Paint mSecondaryWavePaint;
    private int mStatus;
    private int mWaveLocation;
    private int mWaveNum;
    protected int mWidth;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class MidVoiceWaveAnimParam {
        long mDuration;
        float mValue1;
        float mValue2;
        float mValue3;
        float mValue4;
        float mValue5;
        float mValue6;

        private MidVoiceWaveAnimParam() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class CornerVoiceWaveAnimParam {
        long mDuration;
        float mValueFrom;
        float mValueTo;

        private CornerVoiceWaveAnimParam() {
        }
    }

    public VoiceWaveViewByCode(Context context) {
        super(context);
        this.mInitBaseWaveHeight = StackStateAnimator.ANIMATION_DURATION_BLOCKING_HELPER_FADE;
        this.mBaseWaveHeight = this.mInitBaseWaveHeight;
        this.mWaveNum = 3;
        this.mWaveLocation = 1;
        this.mCornerWaveValueAnimatorList = new ArrayList();
        this.mCornerWaveOffset = new int[4];
        this.mCornerWaveHeight = new int[4];
        this.mMidWaveValueAnimatorList = new ArrayList();
        this.mMidWaveFactor = new float[6];
        this.mStatus = 0;
        this.mScale = 1.0f;
        this.mIsFadeOutAnimStarted = false;
        this.mMidWaveOffsetBase = 500;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewByCode.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 0) {
                    VoiceWaveViewByCode.this.destroyAnim();
                    VoiceWaveViewByCode.this.setVisibility(8);
                } else if (i == 1) {
                    ValueAnimator valueAnimator = (ValueAnimator) msg.obj;
                    valueAnimator.start();
                } else if (i == 2) {
                    VoiceWaveViewByCode.this.mIsFadeOutAnimStarted = false;
                } else if (i == 4) {
                    VoiceWaveViewByCode.this.invalidate();
                    VoiceWaveViewByCode.this.mHandler.sendEmptyMessageDelayed(4, 40L);
                }
            }
        };
    }

    public VoiceWaveViewByCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInitBaseWaveHeight = StackStateAnimator.ANIMATION_DURATION_BLOCKING_HELPER_FADE;
        this.mBaseWaveHeight = this.mInitBaseWaveHeight;
        this.mWaveNum = 3;
        this.mWaveLocation = 1;
        this.mCornerWaveValueAnimatorList = new ArrayList();
        this.mCornerWaveOffset = new int[4];
        this.mCornerWaveHeight = new int[4];
        this.mMidWaveValueAnimatorList = new ArrayList();
        this.mMidWaveFactor = new float[6];
        this.mStatus = 0;
        this.mScale = 1.0f;
        this.mIsFadeOutAnimStarted = false;
        this.mMidWaveOffsetBase = 500;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewByCode.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 0) {
                    VoiceWaveViewByCode.this.destroyAnim();
                    VoiceWaveViewByCode.this.setVisibility(8);
                } else if (i == 1) {
                    ValueAnimator valueAnimator = (ValueAnimator) msg.obj;
                    valueAnimator.start();
                } else if (i == 2) {
                    VoiceWaveViewByCode.this.mIsFadeOutAnimStarted = false;
                } else if (i == 4) {
                    VoiceWaveViewByCode.this.invalidate();
                    VoiceWaveViewByCode.this.mHandler.sendEmptyMessageDelayed(4, 40L);
                }
            }
        };
        init(context, attrs);
    }

    public VoiceWaveViewByCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInitBaseWaveHeight = StackStateAnimator.ANIMATION_DURATION_BLOCKING_HELPER_FADE;
        this.mBaseWaveHeight = this.mInitBaseWaveHeight;
        this.mWaveNum = 3;
        this.mWaveLocation = 1;
        this.mCornerWaveValueAnimatorList = new ArrayList();
        this.mCornerWaveOffset = new int[4];
        this.mCornerWaveHeight = new int[4];
        this.mMidWaveValueAnimatorList = new ArrayList();
        this.mMidWaveFactor = new float[6];
        this.mStatus = 0;
        this.mScale = 1.0f;
        this.mIsFadeOutAnimStarted = false;
        this.mMidWaveOffsetBase = 500;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewByCode.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 0) {
                    VoiceWaveViewByCode.this.destroyAnim();
                    VoiceWaveViewByCode.this.setVisibility(8);
                } else if (i == 1) {
                    ValueAnimator valueAnimator = (ValueAnimator) msg.obj;
                    valueAnimator.start();
                } else if (i == 2) {
                    VoiceWaveViewByCode.this.mIsFadeOutAnimStarted = false;
                } else if (i == 4) {
                    VoiceWaveViewByCode.this.invalidate();
                    VoiceWaveViewByCode.this.mHandler.sendEmptyMessageDelayed(4, 40L);
                }
            }
        };
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VoiceWaveView, 0, 0);
        this.mWaveNum = a.getInteger(6, this.mWaveNum);
        this.mMainWaveColor = a.getColor(5, context.getColor(R.color.color_main_voice_wave));
        this.mSecondaryWaveColor = a.getColor(7, context.getColor(R.color.color_secondary_voice_wave));
        this.mWaveLocation = a.getInteger(4, this.mWaveLocation);
        a.recycle();
        this.mMainWavePaint = new Paint();
        this.mMainWavePaint.setColor(this.mMainWaveColor);
        this.mMainWavePaint.setStyle(Paint.Style.FILL);
        this.mMainWavePaint.setAlpha(200);
        this.mMainWavePaint.setAntiAlias(true);
        this.mSecondaryWavePaint = new Paint();
        this.mSecondaryWavePaint.setColor(this.mSecondaryWaveColor);
        this.mSecondaryWavePaint.setAlpha(Opcodes.GETFIELD);
        this.mSecondaryWavePaint.setStyle(Paint.Style.FILL);
        this.mSecondaryWavePaint.setAntiAlias(true);
        this.mCoverPaint = new Paint();
        this.mCoverPaint.setColor(this.mContext.getColor(R.color.color_voice_wave_cover));
        this.mCoverPaint.setAlpha(100);
        this.mCoverPaint.setStyle(Paint.Style.FILL);
        this.mCoverPaint.setAntiAlias(true);
        this.mPaths = new Path[]{new Path(), new Path()};
        initAnim();
    }

    private void initAnim() {
        int i = this.mWaveLocation;
        if (i == 0) {
            initMidWaveAnim();
        } else if (i == 1 || i == 2) {
            initCornerWaveAnim();
        }
        initFadeOutAnim();
    }

    private void initFadeOutAnim() {
        this.mFadeOutValueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.mFadeOutValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.-$$Lambda$VoiceWaveViewByCode$cx6848mcH7tUaqXXL49Ok7SdnLw
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoiceWaveViewByCode.this.lambda$initFadeOutAnim$0$VoiceWaveViewByCode(valueAnimator);
            }
        });
        this.mFadeOutValueAnimator.setDuration(600L);
        this.mFadeOutValueAnimator.start();
    }

    public /* synthetic */ void lambda$initFadeOutAnim$0$VoiceWaveViewByCode(ValueAnimator valueAnimator) {
        this.mScale = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    private void initCornerWaveAnim() {
        CornerVoiceWaveAnimParam[] cornerVoiceWaveAnimParams = new CornerVoiceWaveAnimParam[4];
        for (int i = 0; i < 4; i++) {
            cornerVoiceWaveAnimParams[i] = new CornerVoiceWaveAnimParam();
        }
        cornerVoiceWaveAnimParams[0].mValueFrom = -0.4f;
        cornerVoiceWaveAnimParams[0].mValueTo = 0.5f;
        cornerVoiceWaveAnimParams[0].mDuration = 1000L;
        cornerVoiceWaveAnimParams[1].mValueFrom = -0.4f;
        cornerVoiceWaveAnimParams[1].mValueTo = 0.5f;
        cornerVoiceWaveAnimParams[1].mDuration = 1000L;
        cornerVoiceWaveAnimParams[2].mValueFrom = -0.4f;
        cornerVoiceWaveAnimParams[2].mValueTo = 0.5f;
        cornerVoiceWaveAnimParams[2].mDuration = 1000L;
        cornerVoiceWaveAnimParams[3].mValueFrom = -0.4f;
        cornerVoiceWaveAnimParams[3].mValueTo = 0.5f;
        cornerVoiceWaveAnimParams[3].mDuration = 1000L;
        Interpolator interpolator = new LinearInterpolator();
        for (int i2 = 0; i2 < 4; i2++) {
            CornerVoiceWaveAnimParam param = cornerVoiceWaveAnimParams[i2];
            final int index = i2;
            ValueAnimator waveValueAnimator = ValueAnimator.ofFloat(param.mValueFrom, param.mValueTo);
            waveValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.-$$Lambda$VoiceWaveViewByCode$Jyzxnlf9b_96XlfYgH1qFfrh--g
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoiceWaveViewByCode.this.lambda$initCornerWaveAnim$1$VoiceWaveViewByCode(index, valueAnimator);
                }
            });
            waveValueAnimator.setDuration(param.mDuration);
            waveValueAnimator.setInterpolator(interpolator);
            waveValueAnimator.setRepeatMode(1);
            waveValueAnimator.setRepeatCount(-1);
            this.mCornerWaveValueAnimatorList.add(waveValueAnimator);
        }
    }

    public /* synthetic */ void lambda$initCornerWaveAnim$1$VoiceWaveViewByCode(int index, ValueAnimator valueAnimator) {
        float factor = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mCornerWaveOffset[index] = (int) ((factor - 0.1d) * this.mWidth);
        this.mCornerWaveHeight[index] = (int) ((0.6d - factor) * this.mBaseWaveHeight * this.mScale);
    }

    private void startCornerAnimation() {
        for (int i = 0; i < 4; i++) {
            Message msg = this.mHandler.obtainMessage(1);
            msg.obj = this.mCornerWaveValueAnimatorList.get(i);
            this.mHandler.sendMessageDelayed(msg, i * 200);
        }
        this.mHandler.sendEmptyMessage(4);
    }

    private void initMidWaveAnim() {
        MidVoiceWaveAnimParam[] midVoiceWaveAnimParams = new MidVoiceWaveAnimParam[6];
        for (int i = 0; i < 6; i++) {
            midVoiceWaveAnimParams[i] = new MidVoiceWaveAnimParam();
        }
        midVoiceWaveAnimParams[0].mValue1 = 0.3f;
        midVoiceWaveAnimParams[0].mValue2 = 0.5f;
        midVoiceWaveAnimParams[0].mValue3 = 0.3f;
        midVoiceWaveAnimParams[0].mValue4 = 0.4f;
        midVoiceWaveAnimParams[0].mValue5 = 0.5f;
        midVoiceWaveAnimParams[0].mValue6 = 0.3f;
        midVoiceWaveAnimParams[0].mDuration = 1200L;
        midVoiceWaveAnimParams[1].mValue1 = 0.3f;
        midVoiceWaveAnimParams[1].mValue2 = 0.5f;
        midVoiceWaveAnimParams[1].mValue3 = 0.3f;
        midVoiceWaveAnimParams[1].mValue4 = 0.4f;
        midVoiceWaveAnimParams[1].mValue5 = 0.5f;
        midVoiceWaveAnimParams[1].mValue6 = 0.3f;
        midVoiceWaveAnimParams[1].mDuration = 400L;
        midVoiceWaveAnimParams[2].mValue1 = 0.3f;
        midVoiceWaveAnimParams[2].mValue2 = 0.5f;
        midVoiceWaveAnimParams[2].mValue3 = 0.3f;
        midVoiceWaveAnimParams[2].mValue4 = 0.6f;
        midVoiceWaveAnimParams[2].mValue5 = 0.3f;
        midVoiceWaveAnimParams[2].mValue6 = 0.4f;
        midVoiceWaveAnimParams[2].mDuration = 1200L;
        midVoiceWaveAnimParams[3].mValue1 = 0.5f;
        midVoiceWaveAnimParams[3].mValue2 = 0.9f;
        midVoiceWaveAnimParams[3].mValue3 = 0.6f;
        midVoiceWaveAnimParams[3].mValue4 = 0.8f;
        midVoiceWaveAnimParams[3].mValue5 = 0.5f;
        midVoiceWaveAnimParams[3].mValue6 = 0.3f;
        midVoiceWaveAnimParams[3].mDuration = 600L;
        midVoiceWaveAnimParams[4].mValue1 = 0.3f;
        midVoiceWaveAnimParams[4].mValue2 = 0.5f;
        midVoiceWaveAnimParams[4].mValue3 = 0.3f;
        midVoiceWaveAnimParams[4].mValue4 = 0.6f;
        midVoiceWaveAnimParams[4].mValue5 = 0.3f;
        midVoiceWaveAnimParams[4].mValue6 = 0.4f;
        midVoiceWaveAnimParams[4].mDuration = 1400L;
        midVoiceWaveAnimParams[5].mValue1 = 0.3f;
        midVoiceWaveAnimParams[5].mValue2 = 0.5f;
        midVoiceWaveAnimParams[5].mValue3 = 0.3f;
        midVoiceWaveAnimParams[5].mValue4 = 0.6f;
        midVoiceWaveAnimParams[5].mValue5 = 0.3f;
        midVoiceWaveAnimParams[5].mValue6 = 0.4f;
        midVoiceWaveAnimParams[5].mDuration = 1500L;
        Interpolator interpolator = new LinearInterpolator();
        for (int i2 = 0; i2 < 6; i2++) {
            MidVoiceWaveAnimParam param = midVoiceWaveAnimParams[i2];
            final int index = i2;
            ValueAnimator waveValueAnimator = ValueAnimator.ofFloat(param.mValue1, param.mValue2, param.mValue3, param.mValue4, param.mValue5, param.mValue6);
            waveValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.-$$Lambda$VoiceWaveViewByCode$qeXoIK_RXqJ9_UtoouTg89UFgG4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoiceWaveViewByCode.this.lambda$initMidWaveAnim$2$VoiceWaveViewByCode(index, valueAnimator);
                }
            });
            waveValueAnimator.setDuration(param.mDuration);
            waveValueAnimator.setInterpolator(interpolator);
            waveValueAnimator.setRepeatMode(1);
            waveValueAnimator.setRepeatCount(-1);
            this.mMidWaveValueAnimatorList.add(waveValueAnimator);
        }
    }

    public /* synthetic */ void lambda$initMidWaveAnim$2$VoiceWaveViewByCode(int index, ValueAnimator valueAnimator) {
        float factor = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mMidWaveFactor[index] = this.mScale * factor;
    }

    private void startMidAnimation() {
        for (ValueAnimator waveAnimator : this.mMidWaveValueAnimatorList) {
            waveAnimator.start();
        }
        this.mHandler.sendEmptyMessage(4);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyAnim();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroyAnim() {
        int i = this.mWaveLocation;
        if (i == 0) {
            for (ValueAnimator animator : this.mMidWaveValueAnimatorList) {
                animator.cancel();
            }
            this.mHandler.removeMessages(4);
        } else if (i == 1 || i == 2) {
            for (ValueAnimator animator2 : this.mCornerWaveValueAnimatorList) {
                animator2.cancel();
            }
            this.mHandler.removeMessages(4);
        }
    }

    @Override // android.widget.RelativeLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPathByLocation(canvas, this.mWaveLocation);
    }

    private void drawPathByLocation(Canvas canvas, int waveLocation) {
        if (waveLocation == 0) {
            drawMidWave(canvas);
        } else if (waveLocation == 2) {
            drawRightWave(canvas);
        } else {
            drawLeftWave(canvas);
        }
    }

    private void drawLeftWave(Canvas canvas) {
        drawCornerWave(canvas, this.mMainWavePaint, this.mCornerWaveOffset[0], (this.mWidth * 3) / 4, this.mCornerWaveHeight[0]);
        drawCornerWave(canvas, this.mSecondaryWavePaint, this.mCornerWaveOffset[1], (this.mWidth * 2) / 3, this.mCornerWaveHeight[1]);
        drawCornerWave(canvas, this.mMainWavePaint, this.mCornerWaveOffset[2], (this.mWidth * 3) / 5, this.mCornerWaveHeight[2]);
        drawCornerWave(canvas, this.mSecondaryWavePaint, this.mCornerWaveOffset[3], (this.mWidth * 2) / 3, this.mCornerWaveHeight[3]);
    }

    private void drawCornerWave(Canvas canvas, Paint paint, int startX, int width, int waveHeight) {
        this.mPaths[0].moveTo(startX, this.mHeight);
        this.mPaths[0].rCubicTo(width / 3, 0.0f, width / 3, -waveHeight, width / 2, -waveHeight);
        this.mPaths[0].rLineTo(0.0f, waveHeight);
        this.mPaths[0].close();
        this.mPaths[1].moveTo(startX + width, this.mHeight);
        this.mPaths[1].rCubicTo((-width) / 3, 0.0f, (-width) / 3, -waveHeight, (-width) / 2, -waveHeight);
        this.mPaths[1].rLineTo(0.0f, waveHeight);
        this.mPaths[1].close();
        int i = 0;
        while (true) {
            Path[] pathArr = this.mPaths;
            if (i < pathArr.length) {
                canvas.drawPath(pathArr[i], paint);
                this.mPaths[i].reset();
                i++;
            } else {
                return;
            }
        }
    }

    private void drawRightWave(Canvas canvas) {
        int i = this.mWidth;
        int width = (i * 3) / 4;
        drawCornerWave(canvas, this.mMainWavePaint, (i - this.mCornerWaveOffset[0]) - width, width, this.mCornerWaveHeight[0]);
        int i2 = this.mWidth;
        int width2 = (i2 * 2) / 3;
        drawCornerWave(canvas, this.mSecondaryWavePaint, (i2 - this.mCornerWaveOffset[1]) - width2, width2, this.mCornerWaveHeight[1]);
        drawCornerWave(canvas, this.mSecondaryWavePaint, (this.mWidth - this.mCornerWaveOffset[3]) - width2, width2, this.mCornerWaveHeight[3]);
        int i3 = this.mWidth;
        int width3 = (i3 * 3) / 5;
        drawCornerWave(canvas, this.mMainWavePaint, (i3 - this.mCornerWaveOffset[2]) - width3, width3, this.mCornerWaveHeight[2]);
    }

    protected void drawMidWave(Canvas canvas) {
        Paint paint = this.mMainWavePaint;
        int i = this.mWidth;
        drawMidWave(canvas, paint, i / 8, (i * 3) / 8, (int) (this.mMidWaveFactor[0] * this.mBaseWaveHeight));
        Paint paint2 = this.mMainWavePaint;
        int i2 = this.mWidth;
        drawMidWave(canvas, paint2, i2 / 2, (i2 * 3) / 8, (int) (this.mMidWaveFactor[1] * this.mBaseWaveHeight));
        Paint paint3 = this.mSecondaryWavePaint;
        int i3 = this.mWidth;
        drawMidWave(canvas, paint3, i3 / 2, i3 / 3, (int) (this.mMidWaveFactor[2] * this.mBaseWaveHeight));
        Paint paint4 = this.mMainWavePaint;
        int i4 = this.mWidth;
        drawMidWave(canvas, paint4, i4 / 4, i4 / 2, (int) (this.mMidWaveFactor[3] * this.mBaseWaveHeight));
        float[] fArr = this.mMidWaveFactor;
        int offset = (int) ((1.0f - fArr[4]) * this.mMidWaveOffsetBase);
        Paint paint5 = this.mSecondaryWavePaint;
        int i5 = this.mWidth;
        drawMidWave(canvas, paint5, (i5 / 2) + offset, (i5 / 2) - offset, (int) (fArr[4] * this.mBaseWaveHeight));
        float[] fArr2 = this.mMidWaveFactor;
        int offset2 = (int) ((1.0f - fArr2[5]) * this.mMidWaveOffsetBase);
        drawMidWave(canvas, this.mSecondaryWavePaint, offset2, (this.mWidth / 2) - offset2, (int) (fArr2[5] * this.mBaseWaveHeight));
    }

    protected void drawMidWave(Canvas canvas, Paint paint, int startX, int width, int waveHeight) {
        this.mPaths[0].moveTo(startX, this.mHeight);
        int anchorX1 = (width / 3) + startX;
        int anchorX2 = (width / 2) + startX;
        int i = this.mHeight;
        this.mPaths[0].cubicTo(anchorX1, i, anchorX1, i - waveHeight, anchorX2, i - waveHeight);
        this.mPaths[0].lineTo(anchorX2, this.mHeight);
        this.mPaths[0].close();
        this.mPaths[1].moveTo(startX + width, this.mHeight);
        int anchorX12 = startX + ((width * 2) / 3);
        int anchorX22 = (width / 2) + startX;
        int i2 = this.mHeight;
        this.mPaths[1].cubicTo(anchorX12, i2, anchorX12, i2 - waveHeight, anchorX22, i2 - waveHeight);
        this.mPaths[1].lineTo(anchorX22, this.mHeight);
        this.mPaths[1].close();
        int i3 = 0;
        while (true) {
            Path[] pathArr = this.mPaths;
            if (i3 < pathArr.length) {
                canvas.drawPath(pathArr[i3], paint);
                this.mPaths[i3].reset();
                i3++;
            } else {
                return;
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    public void showAnim(int type, int volume) {
        super.showAnim(type, volume);
        if (type == 0) {
            this.mBaseWaveHeight = 40;
        } else {
            this.mBaseWaveHeight = (int) (((((volume * volume) / 10000.0f) * 0.8d) + 0.2d) * this.mInitBaseWaveHeight);
        }
        Logger.d(TAG, "showAnim : volume = " + volume + " mBaseWaveHeight = " + this.mBaseWaveHeight);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim00() {
        Logger.d(TAG, "startAnim00 : status = " + this.mStatus);
        this.mStatus = 1;
        setVisibility(0);
        startAnimation();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim01() {
        Logger.d(TAG, "startAnim01 : status = " + this.mStatus);
        this.mStatus = 2;
        setVisibility(0);
        startAnimation();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim12() {
        Logger.d(TAG, "startAnim12 : status = " + this.mStatus);
        this.mStatus = 4;
        setVisibility(0);
        startAnimation();
    }

    private void startAnimation() {
        Logger.d(TAG, "startAnimation");
        this.mHandler.removeCallbacksAndMessages(null);
        this.mScale = 1.0f;
        if (this.mWaveLocation == 0) {
            startMidAnimation();
        } else {
            startCornerAnimation();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnim20() {
        Logger.d(TAG, "startAnim20 : status = " + this.mStatus);
        this.mStatus = 6;
        startFadeOutAnimation();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    protected void startAnimHide() {
        Logger.d(TAG, "startAnimHide : status = " + this.mStatus);
        this.mStatus = 8;
        startFadeOutAnimation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.BaseVoiceWaveView
    public void stopAnim() {
        super.stopAnim();
        this.mStatus = 0;
        startFadeOutAnimation();
    }

    private void startFadeOutAnimation() {
        if (!this.mIsFadeOutAnimStarted) {
            this.mFadeOutValueAnimator.start();
        }
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessageDelayed(0, 600L);
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 600L);
    }
}
