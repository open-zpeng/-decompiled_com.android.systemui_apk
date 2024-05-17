package com.android.systemui.assist.ui;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.NavigationBarTransitions;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class InvocationLightsView extends View implements NavigationBarTransitions.DarkIntensityListener {
    private static final int LIGHT_HEIGHT_DP = 3;
    private static final float MINIMUM_CORNER_RATIO = 0.6f;
    private static final String TAG = "InvocationLightsView";
    protected final ArrayList<EdgeLight> mAssistInvocationLights;
    private final int mDarkColor;
    protected final PerimeterPathGuide mGuide;
    private final int mLightColor;
    private final Paint mPaint;
    private final Path mPath;
    private boolean mRegistered;
    private int[] mScreenLocation;
    private final int mStrokeWidth;
    private boolean mUseNavBarColor;
    private final int mViewHeight;

    public InvocationLightsView(Context context) {
        this(context, null);
    }

    public InvocationLightsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InvocationLightsView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public InvocationLightsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mAssistInvocationLights = new ArrayList<>();
        this.mPaint = new Paint();
        this.mPath = new Path();
        this.mScreenLocation = new int[2];
        this.mRegistered = false;
        this.mUseNavBarColor = true;
        this.mStrokeWidth = DisplayUtils.convertDpToPx(3.0f, context);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.MITER);
        this.mPaint.setAntiAlias(true);
        int displayWidth = DisplayUtils.getWidth(context);
        int displayHeight = DisplayUtils.getHeight(context);
        this.mGuide = new PerimeterPathGuide(context, createCornerPathRenderer(context), this.mStrokeWidth / 2, displayWidth, displayHeight);
        int cornerRadiusBottom = DisplayUtils.getCornerRadiusBottom(context);
        int cornerRadiusTop = DisplayUtils.getCornerRadiusTop(context);
        this.mViewHeight = Math.max(Math.max(cornerRadiusBottom, cornerRadiusTop), DisplayUtils.convertDpToPx(3.0f, context));
        int dualToneDarkTheme = Utils.getThemeAttr(this.mContext, R.attr.darkIconTheme);
        int dualToneLightTheme = Utils.getThemeAttr(this.mContext, R.attr.lightIconTheme);
        Context lightContext = new ContextThemeWrapper(this.mContext, dualToneLightTheme);
        Context darkContext = new ContextThemeWrapper(this.mContext, dualToneDarkTheme);
        this.mLightColor = Utils.getColorAttrDefaultColor(lightContext, R.attr.singleToneColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor(darkContext, R.attr.singleToneColor);
        for (int i = 0; i < 4; i++) {
            this.mAssistInvocationLights.add(new EdgeLight(0, 0.0f, 0.0f));
        }
    }

    public void onInvocationProgress(float progress) {
        if (progress == 0.0f) {
            setVisibility(8);
        } else {
            attemptRegisterNavBarListener();
            float cornerLengthNormalized = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM_LEFT);
            float arcLengthNormalized = 0.6f * cornerLengthNormalized;
            float arcOffsetNormalized = (cornerLengthNormalized - arcLengthNormalized) / 2.0f;
            float maxLightLength = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM) / 4.0f;
            float lightLength = MathUtils.lerp(0.0f, maxLightLength, progress);
            float leftStart = ((-cornerLengthNormalized) + arcOffsetNormalized) * (1.0f - progress);
            float rightStart = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM) + ((cornerLengthNormalized - arcOffsetNormalized) * (1.0f - progress));
            setLight(0, leftStart, lightLength);
            setLight(1, leftStart + lightLength, lightLength);
            setLight(2, rightStart - (2.0f * lightLength), lightLength);
            setLight(3, rightStart - lightLength, lightLength);
            setVisibility(0);
        }
        invalidate();
    }

    public void hide() {
        setVisibility(8);
        Iterator<EdgeLight> it = this.mAssistInvocationLights.iterator();
        while (it.hasNext()) {
            EdgeLight light = it.next();
            light.setLength(0.0f);
        }
        attemptUnregisterNavBarListener();
    }

    public void setColors(Integer color) {
        if (color == null) {
            this.mUseNavBarColor = true;
            this.mPaint.setStrokeCap(Paint.Cap.BUTT);
            attemptRegisterNavBarListener();
            return;
        }
        setColors(color.intValue(), color.intValue(), color.intValue(), color.intValue());
    }

    public void setColors(int color1, int color2, int color3, int color4) {
        this.mUseNavBarColor = false;
        attemptUnregisterNavBarListener();
        this.mAssistInvocationLights.get(0).setColor(color1);
        this.mAssistInvocationLights.get(1).setColor(color2);
        this.mAssistInvocationLights.get(2).setColor(color3);
        this.mAssistInvocationLights.get(3).setColor(color4);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationBarTransitions.DarkIntensityListener
    public void onDarkIntensity(float darkIntensity) {
        updateDarkness(darkIntensity);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        getLayoutParams().height = this.mViewHeight;
        requestLayout();
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int rotation = getContext().getDisplay().getRotation();
        this.mGuide.setRotation(rotation);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        getLocationOnScreen(this.mScreenLocation);
        int[] iArr = this.mScreenLocation;
        canvas.translate(-iArr[0], -iArr[1]);
        if (this.mUseNavBarColor) {
            Iterator<EdgeLight> it = this.mAssistInvocationLights.iterator();
            while (it.hasNext()) {
                EdgeLight light = it.next();
                renderLight(light, canvas);
            }
            return;
        }
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        renderLight(this.mAssistInvocationLights.get(0), canvas);
        renderLight(this.mAssistInvocationLights.get(3), canvas);
        this.mPaint.setStrokeCap(Paint.Cap.BUTT);
        renderLight(this.mAssistInvocationLights.get(1), canvas);
        renderLight(this.mAssistInvocationLights.get(2), canvas);
    }

    protected void setLight(int index, float offset, float length) {
        if (index < 0 || index >= 4) {
            Log.w(TAG, "invalid invocation light index: " + index);
        }
        this.mAssistInvocationLights.get(index).setOffset(offset);
        this.mAssistInvocationLights.get(index).setLength(length);
    }

    protected CornerPathRenderer createCornerPathRenderer(Context context) {
        return new CircularCornerPathRenderer(context);
    }

    protected void updateDarkness(float darkIntensity) {
        if (this.mUseNavBarColor) {
            int invocationColor = ((Integer) ArgbEvaluator.getInstance().evaluate(darkIntensity, Integer.valueOf(this.mLightColor), Integer.valueOf(this.mDarkColor))).intValue();
            Iterator<EdgeLight> it = this.mAssistInvocationLights.iterator();
            while (it.hasNext()) {
                EdgeLight light = it.next();
                light.setColor(invocationColor);
            }
            invalidate();
        }
    }

    private void renderLight(EdgeLight light, Canvas canvas) {
        this.mGuide.strokeSegment(this.mPath, light.getOffset(), light.getOffset() + light.getLength());
        this.mPaint.setColor(light.getColor());
        canvas.drawPath(this.mPath, this.mPaint);
    }

    private void attemptRegisterNavBarListener() {
        NavigationBarController controller;
        NavigationBarFragment navBar;
        if (this.mRegistered || (controller = (NavigationBarController) Dependency.get(NavigationBarController.class)) == null || (navBar = controller.getDefaultNavigationBarFragment()) == null) {
            return;
        }
        updateDarkness(navBar.getBarTransitions().addDarkIntensityListener(this));
        this.mRegistered = true;
    }

    private void attemptUnregisterNavBarListener() {
        NavigationBarController controller;
        NavigationBarFragment navBar;
        if (!this.mRegistered || (controller = (NavigationBarController) Dependency.get(NavigationBarController.class)) == null || (navBar = controller.getDefaultNavigationBarFragment()) == null) {
            return;
        }
        navBar.getBarTransitions().removeDarkIntensityListener(this);
        this.mRegistered = false;
    }
}
