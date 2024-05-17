package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.rastermill.FrameSequenceUtil;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.Input;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviPreferenceBean;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.helper.XBesselCurve3Interpolator;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardUtils;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView;
import com.xiaopeng.systemui.infoflow.speech.ui.model.CtrlCardContent;
import com.xiaopeng.systemui.infoflow.speech.utils.ColorUtils;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class CtrlCardView extends AlphaOptimizedRelativeLayout implements ILogicCtrlView {
    private static final String TAG = "CtrlCardView";
    private RadialImageView mBgView;
    private RelativeLayout mContainerLayout;
    private AnimatedImageView mCtrlCardAnim;
    private int mCurrentType;
    private AnimatedTextView mFanAutoText;
    private AnimatedImageView mHintView;
    private AnimatedTextView mNumTv;
    private ICtrlCardPresenter mPresenter;
    private CtrlProgressView mProgressV;
    private View mRootView;
    private AnimatedImageView mSeatIcon;
    private String mSetValue;
    private ThemeViewModel mThemeViewModel;
    private AnimatedImageView mTitleImg;
    private AnimatedTextView mTitleTv;
    private AnimatedTextView mTvOff;
    private AnimatedTextView mUinitTv;
    private ObjectAnimator mUpdateProgressAnimator;

    public CtrlCardView(Context context) {
        super(context);
    }

    public CtrlCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CtrlCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CtrlCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
        Logger.d(TAG, "");
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContainerLayout = (RelativeLayout) findViewById(R.id.ctrl_container);
        this.mRootView = findViewById(R.id.view_ctrl_card);
        this.mTitleImg = (AnimatedImageView) findViewById(R.id.ctrl_title_img);
        this.mTitleTv = (AnimatedTextView) findViewById(R.id.ctrl_title);
        this.mBgView = (RadialImageView) findViewById(R.id.img_context_tip);
        this.mProgressV = (CtrlProgressView) findViewById(R.id.ctrl_progressbar);
        this.mTvOff = (AnimatedTextView) findViewById(R.id.tv_off);
        this.mCtrlCardAnim = (AnimatedImageView) findViewById(R.id.iv_ctrl_card_anim);
        this.mUpdateProgressAnimator = ObjectAnimator.ofInt(this.mProgressV, "progress", 0);
        this.mUpdateProgressAnimator.setAutoCancel(true);
        this.mFanAutoText = (AnimatedTextView) findViewById(R.id.fan_auto);
        this.mHintView = (AnimatedImageView) findViewById(R.id.img_context_explain);
        if (!CarModelsManager.getConfig().isAllWheelKeyToICMSupport()) {
            this.mHintView.setVisibility(0);
        }
    }

    public void initTitle(String title) {
        String title2;
        int drawableId = -1;
        int i = this.mCurrentType;
        if (i == 2) {
            title2 = getContext().getString(R.string.ctrl_card_wind_rating_title);
            drawableId = R.drawable.ic_airvolume;
            this.mProgressV.setDashMode(true);
            this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
            this.mProgressV.setCtrlType(2);
        } else if (i == 3) {
            title2 = getContext().getString(R.string.ctrl_card_wind_rating_title);
            drawableId = R.drawable.ic_airvolume;
            this.mProgressV.setDashMode(true);
            this.mProgressV.setThumbColor(Color.rgb(255, 152, 0), Color.rgb(255, 255, 255));
            this.mProgressV.setCtrlType(3);
        } else if (i == 4) {
            title2 = getContext().getString(R.string.ctrl_card_wind_rating_title);
            drawableId = R.drawable.ic_airvolume;
            this.mProgressV.setDashMode(true);
            this.mProgressV.setCtrlType(4);
            this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
        } else {
            switch (i) {
                case 8:
                    title2 = getContext().getString(R.string.ctrl_card_temp_rating_cold_title);
                    drawableId = R.drawable.ic_ac;
                    this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
                    this.mProgressV.setCtrlType(8);
                    this.mBgView.setRadialBg(0.2f, ColorUtils.getInstance().createRadialBgColdWindColorDay(0), ColorUtils.getInstance().createRadialBgColdWindColorNight());
                    break;
                case 9:
                    title2 = getContext().getString(R.string.ctrl_card_temp_rating_hot_title);
                    drawableId = R.drawable.ic_heating;
                    this.mProgressV.setThumbColor(Color.rgb(255, 152, 0), Color.rgb(255, 255, 255));
                    this.mProgressV.setCtrlType(9);
                    this.mBgView.setRadialBg(0.2f, ColorUtils.getInstance().createRadialBgHotWindColorDay(0), ColorUtils.getInstance().createRadialBgHotWindColorNight(0));
                    break;
                case 10:
                    title2 = getContext().getString(R.string.ctrl_card_temp_rating_blowing_title);
                    drawableId = R.drawable.ic_ventilate;
                    this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
                    this.mProgressV.setCtrlType(10);
                    this.mBgView.setRadialBg(0.8f, ColorUtils.getInstance().createRadialBgBlowingWindColorDay(0), ColorUtils.getInstance().createRadialBgBlowingWindColorNight(0));
                    break;
                case 11:
                    title2 = getContext().getString(R.string.ctrl_card_lighting_brightness_title);
                    drawableId = R.drawable.ic_toplight;
                    if (this.mBgView != null) {
                        int[] mRadiaBgColorsDay = {Color.argb(0, (int) Opcodes.INVOKEINTERFACE, (int) NaviPreferenceBean.PATH_PREF_AVOID_HIGHWAY, 228), Color.rgb(255, 255, 255)};
                        int[] mRadiaBgColorsNight = {Color.rgb(115, 149, 204), Color.rgb(35, 46, 63)};
                        this.mBgView.setRadialBg(mRadiaBgColorsDay, mRadiaBgColorsNight);
                        this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
                        this.mProgressV.setCtrlType(11);
                        break;
                    }
                    break;
                case 12:
                    title2 = getContext().getString(R.string.ctrl_card_lighting_color_title);
                    drawableId = R.drawable.ic_toplight;
                    RadialImageView radialImageView = this.mBgView;
                    if (radialImageView != null) {
                        radialImageView.setImageResource(R.drawable.bg_ctrl_ambient_lighting);
                        int[] mRadiaBgColorsDay2 = {-1, Color.rgb(255, 255, 255)};
                        int[] mRadiaBgColorsNight2 = {Color.rgb(35, 46, 63), 0};
                        this.mBgView.setRadialBg(mRadiaBgColorsDay2, mRadiaBgColorsNight2);
                    }
                    this.mProgressV.setCtrlType(12);
                    this.mProgressV.setChangeInSide(true);
                    this.mProgressV.setThumbColor(Color.rgb(255, 255, 255), Color.rgb((int) Input.Keys.COLON, 229, 68));
                    break;
                case 13:
                    title2 = getContext().getString(R.string.ctrl_card_seat_hot_passenger_title);
                    drawableId = R.drawable.ic_rightoff;
                    this.mProgressV.setSeatMode(true);
                    this.mProgressV.setSeatProgressbarColor(this.mContext.getColor(R.color.infoflow_ctrl_card_seat_off), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_hot_on), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_off_out), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_off_in), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_on_in));
                    this.mProgressV.setCtrlType(13);
                    break;
                case 14:
                    title2 = getContext().getString(R.string.ctrl_card_seat_hot_title);
                    drawableId = R.drawable.ic_rightoff;
                    this.mProgressV.setSeatMode(true);
                    this.mProgressV.setSeatProgressbarColor(this.mContext.getColor(R.color.infoflow_ctrl_card_seat_off), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_hot_on), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_off_out), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_off_in), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_on_in));
                    this.mProgressV.setCtrlType(14);
                    break;
                case 15:
                    title2 = getContext().getString(R.string.ctrl_card_seat_blowing_title);
                    drawableId = R.drawable.ic_rightoff;
                    this.mProgressV.setSeatMode(true);
                    this.mProgressV.setSeatProgressbarColor(this.mContext.getColor(R.color.infoflow_ctrl_card_seat_off), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_vent_on), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_off_out), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_off_in), this.mContext.getColor(R.color.infoflow_ctrl_card_seat_indicator_on_in));
                    this.mProgressV.setCtrlType(15);
                    break;
                case 16:
                    title2 = getContext().getString(R.string.ctrl_card_screen_title);
                    drawableId = R.drawable.ic_brightness;
                    this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
                    this.mProgressV.setCtrlType(16);
                    break;
                case 17:
                    title2 = getContext().getString(R.string.ctrl_card_icm_title);
                    drawableId = R.drawable.ic_brightness;
                    this.mProgressV.setThumbColor(Color.rgb(154, 172, 191), Color.rgb(255, 255, 255));
                    this.mProgressV.setCtrlType(17);
                    break;
                default:
                    title2 = title;
                    break;
            }
        }
        AnimatedTextView animatedTextView = this.mTitleTv;
        if (animatedTextView != null) {
            animatedTextView.setText(title2);
        }
        AnimatedImageView animatedImageView = this.mTitleImg;
        if (animatedImageView != null && drawableId != -1) {
            animatedImageView.setImageResource(drawableId);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        Logger.d(TAG, "onConfigurationChanged : " + newConfig);
        super.onConfigurationChanged(newConfig);
        this.mThemeViewModel.onConfigurationChanged(this, newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        Logger.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
        this.mCtrlCardAnim.clearAnimation();
        this.mUpdateProgressAnimator.cancel();
        AnimationHelper.destroyAnim(this.mSeatIcon);
    }

    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.View
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        this.mThemeViewModel.setBackgroundResource(resid);
    }

    private void initNumView(String unitValue) {
        int i = this.mCurrentType;
        if (i != 2 && i != 3 && i != 4 && i != 16 && i != 17) {
            switch (i) {
            }
            checkAutoFan(unitValue);
            initUnitTV();
        }
        this.mContainerLayout.addView(LayoutInflater.from(getContext()).inflate(R.layout.card_temp_rating, (ViewGroup) null, false));
        this.mNumTv = (AnimatedTextView) findViewById(R.id.temperature_num);
        this.mUinitTv = (AnimatedTextView) findViewById(R.id.tv_uinit);
        checkAutoFan(unitValue);
        initUnitTV();
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0023  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void checkAutoFan(java.lang.String r6) {
        /*
            r5 = this;
            r0 = 0
            int r1 = r5.mCurrentType
            r2 = 2
            if (r1 == r2) goto L18
            r2 = 3
            if (r1 == r2) goto L18
            r2 = 4
            if (r1 == r2) goto L18
            r2 = 16
            if (r1 == r2) goto L18
            r2 = 17
            if (r1 == r2) goto L18
            switch(r1) {
                case 8: goto L18;
                case 9: goto L18;
                case 10: goto L18;
                case 11: goto L18;
                default: goto L17;
            }
        L17:
            goto L19
        L18:
            r0 = 1
        L19:
            boolean r1 = r5.isWindCard()
            boolean r2 = android.text.TextUtils.isEmpty(r6)
            if (r2 != 0) goto L43
            float r2 = java.lang.Float.parseFloat(r6)
            if (r1 == 0) goto L30
            r3 = 1096810496(0x41600000, float:14.0)
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 != 0) goto L30
            r0 = 0
        L30:
            com.xiaopeng.systemui.infoflow.theme.AnimatedTextView r3 = r5.mNumTv
            if (r3 == 0) goto L43
            if (r0 == 0) goto L3e
            java.lang.String r4 = r5.getTemperatureShowText(r6)
            r3.setText(r4)
            goto L43
        L3e:
            java.lang.String r4 = ""
            r3.setText(r4)
        L43:
            if (r1 == 0) goto L65
            com.xiaopeng.systemui.infoflow.theme.AnimatedTextView r2 = r5.mFanAutoText
            r3 = 0
            if (r0 == 0) goto L4d
            r4 = 8
            goto L4e
        L4d:
            r4 = r3
        L4e:
            r2.setVisibility(r4)
            com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView r2 = r5.mProgressV
            if (r0 == 0) goto L56
            r3 = 1
        L56:
            r2.setIndicatorVisible(r3)
            if (r0 != 0) goto L65
            com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlCardView$1 r2 = new com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlCardView$1
            r2.<init>()
            r3 = 100
            r5.postDelayed(r2, r3)
        L65:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlCardView.checkAutoFan(java.lang.String):void");
    }

    private boolean isWindCard() {
        int i = this.mCurrentType;
        return i == 2 || i == 3 || i == 4;
    }

    private String getTemperatureShowText(String unitValue) {
        int i = this.mCurrentType;
        if (i != 2 && i != 3 && i != 4) {
            if (i != 16 && i != 17) {
                switch (i) {
                    case 8:
                    case 9:
                    case 10:
                        break;
                    case 11:
                        break;
                    default:
                        return "";
                }
            }
            return String.format("%d", Integer.valueOf((int) Float.parseFloat(unitValue)));
        }
        float value = Float.parseFloat(unitValue);
        int roundValue = (int) Float.parseFloat(unitValue);
        return value - ((float) roundValue) > 0.0f ? String.format("%.1f", Float.valueOf(value)) : String.format("%d", Integer.valueOf(roundValue));
    }

    private void initSeatView() {
        switch (this.mCurrentType) {
            case 13:
            case 14:
            case 15:
                this.mContainerLayout.addView(LayoutInflater.from(getContext()).inflate(R.layout.ctrl_seat_content, (ViewGroup) null, false));
                this.mSeatIcon = (AnimatedImageView) findViewById(R.id.ctrl_seat_content_icon);
                this.mSeatIcon.setImageResource(R.drawable.ctrl_seat_off);
                return;
            default:
                return;
        }
    }

    private void initUnitTV() {
        AnimatedTextView animatedTextView = this.mUinitTv;
        if (animatedTextView != null) {
            int i = this.mCurrentType;
            if (i == 2 || i == 3 || i == 4) {
                this.mUinitTv.setVisibility(8);
                return;
            }
            if (i != 16 && i != 17) {
                switch (i) {
                    case 8:
                    case 9:
                    case 10:
                        animatedTextView.setText("℃");
                        return;
                    case 11:
                        break;
                    default:
                        return;
                }
            }
            this.mUinitTv.setText("%");
        }
    }

    public void updateNumContent(String progress) {
        Logger.d(TAG, "updateNumContent : progress = " + progress);
        checkAutoFan(progress);
    }

    public void updateContentbg(int progress) {
        int i;
        RadialImageView radialImageView = this.mBgView;
        if (radialImageView == null || (i = this.mCurrentType) == 2 || i == 3 || i == 4) {
            return;
        }
        switch (i) {
            case 8:
            case 9:
            case 10:
            default:
                return;
            case 11:
                radialImageView.refreshColor(Color.argb(progress * 255, 115, 149, 204));
                return;
            case 12:
                radialImageView.refreshColor(-1);
                return;
            case 13:
            case 14:
                radialImageView.setVisibility(8);
                if (progress == 25) {
                    AnimationHelper.startAnimInfinite(this.mSeatIcon, R.drawable.infoflow_ctrl_card_seat_heat_level1);
                    return;
                } else if (progress == 50) {
                    AnimationHelper.startAnimInfinite(this.mSeatIcon, R.drawable.infoflow_ctrl_card_seat_heat_level2);
                    return;
                } else if (progress == 75) {
                    AnimationHelper.startAnimInfinite(this.mSeatIcon, R.drawable.infoflow_ctrl_card_seat_heat_level3);
                    return;
                } else {
                    AnimatedImageView animatedImageView = this.mSeatIcon;
                    if (animatedImageView != null) {
                        AnimationHelper.destroyAnim(animatedImageView);
                        this.mSeatIcon.setImageResource(R.drawable.ctrl_seat_off);
                        return;
                    }
                    return;
                }
            case 15:
                radialImageView.setVisibility(8);
                if (progress == 25) {
                    AnimationHelper.startAnim(this.mSeatIcon, R.drawable.infoflow_ctrl_card_seat_vent_level1);
                } else if (progress == 50) {
                    AnimationHelper.startAnim(this.mSeatIcon, R.drawable.infoflow_ctrl_card_seat_vent_level2);
                } else if (progress == 75) {
                    AnimationHelper.startAnim(this.mSeatIcon, R.drawable.infoflow_ctrl_card_seat_vent_level3);
                } else {
                    AnimatedImageView animatedImageView2 = this.mSeatIcon;
                    if (animatedImageView2 != null) {
                        AnimationHelper.destroyAnim(animatedImageView2);
                        this.mSeatIcon.setImageResource(R.drawable.ctrl_seat_off);
                    }
                }
                this.mBgView.setVisibility(8);
                return;
            case 16:
                radialImageView.setBackgroundResource(R.drawable.card_bg_screen_brightness);
                return;
            case 17:
                radialImageView.setBackgroundResource(R.drawable.card_bg_icm_brightness);
                return;
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void setViewStub(int type, CtrlCardContent content) {
        this.mCurrentType = type;
        this.mSetValue = content.getData();
        int childCount = this.mContainerLayout.getChildCount();
        if (childCount > 0) {
            this.mContainerLayout.removeAllViews();
        }
        int progressValue = 0;
        if (!TextUtils.isEmpty(content.getData())) {
            progressValue = CtrlCardUtils.getInstance().unit2ProgressValue(this.mCurrentType, Float.parseFloat(content.getData()));
        }
        Log.d(TAG, "setViewStub() called with: type = [" + type + "], data = [" + content.getData() + "]progressValue=" + progressValue);
        initTitle(content.getTitle());
        initNumView(content.getData());
        initSeatView();
        updateContentbg(progressValue);
        initProgress(progressValue);
        updateOffText();
    }

    private void updateOffText() {
        int progress = this.mProgressV.getProgress();
        Logger.d(TAG, "updateOffText : progress = " + progress);
        updateOffText(progress);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void refreshView(int progressValue) {
        String numString;
        Logger.d(TAG, "refreshView() called with:mCurrentType=" + this.mCurrentType + " progressValue = [" + progressValue + NavigationBarInflaterView.SIZE_MOD_END);
        float unitValue = CtrlCardUtils.getInstance().progress2UnitValue(this.mCurrentType, progressValue);
        int intValue = (int) unitValue;
        if (intValue == unitValue) {
            numString = intValue + "";
        } else {
            numString = unitValue + "";
        }
        updateNumContent(numString);
        updateContentbg(progressValue);
        updateRadialValue(progressValue);
        updateProgress(progressValue);
        updateOffText(progressValue);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void playBgAnimation(int type, float from, float to) {
        FrameSequenceUtil.destroy(this.mCtrlCardAnim);
        if (type == 2 || type == 3 || type == 4) {
            float fromScale = getWindAnimScale(from);
            float toScale = getWindAnimScale(to);
            FrameSequenceUtil.with(this.mCtrlCardAnim).resourceId(getWindAnimResourceId(type)).loopBehavior(2).applyAsync();
            startBgScaleAnimation(this.mCtrlCardAnim, fromScale, toScale);
            return;
        }
        switch (type) {
            case 8:
                FrameSequenceUtil.with(this.mCtrlCardAnim).resourceId(R.drawable.infoflow_ctrl_card_hvac_temp_cold).loopBehavior(1).applyAsync();
                return;
            case 9:
                FrameSequenceUtil.with(this.mCtrlCardAnim).resourceId(R.drawable.infoflow_ctrl_card_hvac_temp_hot).loopBehavior(1).applyAsync();
                return;
            case 10:
                FrameSequenceUtil.with(this.mCtrlCardAnim).resourceId(R.drawable.infoflow_ctrl_card_hvac_temp_level).loopBehavior(1).applyAsync();
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void updateCtrlCardContent(CtrlCardContent ctrlCardContent) {
        if (ctrlCardContent != null) {
            this.mCurrentType = ctrlCardContent.getType();
            initTitle(ctrlCardContent.getTitle());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void stopProgressAnim() {
        ObjectAnimator objectAnimator = this.mUpdateProgressAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private int getWindAnimResourceId(int type) {
        if (type != 2) {
            if (type != 3) {
                return type != 4 ? R.drawable.infoflow_ctrl_card_hvac_wind_level : R.drawable.infoflow_ctrl_card_hvac_wind_level;
            }
            return R.drawable.infoflow_ctrl_card_hvac_wind_hot;
        }
        return R.drawable.infoflow_ctrl_card_hvac_wind_cold;
    }

    private void startBgScaleAnimation(View view, float fromScale, float toScale) {
        Logger.d(TAG, "startBgScaleAnimation : " + fromScale + " - " + toScale);
        view.clearAnimation();
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromScale, toScale, fromScale, toScale, 1, 0.5f, 1, 1.0f);
        scaleAnimation.setDuration(200L);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new XBesselCurve3Interpolator(0.33f, 0.0f, 0.1f, 1.0f));
        scaleAnimation.setRepeatCount(0);
        view.setAnimation(scaleAnimation);
        scaleAnimation.start();
    }

    private float getWindAnimScale(float level) {
        int intLevel = (int) level;
        switch (intLevel) {
            case 1:
                return 0.4f;
            case 2:
                return 0.47f;
            case 3:
                return 0.54f;
            case 4:
                return 0.61f;
            case 5:
                return 0.68f;
            case 6:
                return 0.75f;
            case 7:
                return 0.82f;
            case 8:
                return 0.89f;
            case 9:
                return 0.96f;
            case 10:
                return 1.03f;
            default:
                return 0.4f;
        }
    }

    private void initProgress(int progressValue) {
        this.mProgressV.setProgress(progressValue);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void setPresenter(ICtrlCardPresenter presenter) {
        this.mPresenter = presenter;
        CtrlProgressView ctrlProgressView = this.mProgressV;
        if (ctrlProgressView != null) {
            ctrlProgressView.setOnColorPickerChangeListener(this.mPresenter);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void updateProgress(int value) {
        Logger.d(TAG, "updateProgress : current = " + value);
        this.mUpdateProgressAnimator.cancel();
        this.mUpdateProgressAnimator.setIntValues(value);
        this.mUpdateProgressAnimator.setDuration(200L);
        this.mUpdateProgressAnimator.start();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void refreshBgView(int color) {
        int i = this.mCurrentType;
        if (i == 8 || i == 9 || i == 10 || i == 12) {
            Log.d(TAG, "refreshBgView() called with: color = [" + color + NavigationBarInflaterView.SIZE_MOD_END);
            this.mBgView.refreshColor(color);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void updateNumTv(String progress) {
        Logger.d(TAG, "updateNumTv : progress = " + progress);
        checkAutoFan(progress);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void updateRadialValue(int progress) {
        RadialImageView radialImageView = this.mBgView;
        if (radialImageView != null && this.mCurrentType == 11) {
            radialImageView.updateBgByProgress(progress);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void updateSeatIcon(int progress) {
        if (this.mSeatIcon != null) {
            int i = this.mCurrentType;
            if (i == 14 || i == 15 || i == 13) {
                updateContentbg(progress);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ILogicCtrlView
    public void updateOffText(int progressValue) {
        Logger.d(TAG, "updateOffText : progress = " + progressValue);
        switch (this.mCurrentType) {
            case 13:
            case 14:
            case 15:
                this.mTvOff.setVisibility(progressValue != 0 ? 0 : 8);
                return;
            default:
                this.mTvOff.setVisibility(8);
                return;
        }
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        this.mProgressV.setOnClickListener(l);
    }

    public void onIcmBrightnessChanged(int value) {
        ICtrlCardPresenter iCtrlCardPresenter = this.mPresenter;
        if (iCtrlCardPresenter != null) {
            iCtrlCardPresenter.onIcmBrightnessChanged(value);
        }
    }

    public void onScreenBrightnessChanged(int value) {
        ICtrlCardPresenter iCtrlCardPresenter = this.mPresenter;
        if (iCtrlCardPresenter != null) {
            iCtrlCardPresenter.onScreenBrightnessChanged(value);
        }
    }
}
