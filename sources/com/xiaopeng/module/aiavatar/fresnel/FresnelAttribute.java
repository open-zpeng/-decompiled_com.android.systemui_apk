package com.xiaopeng.module.aiavatar.fresnel;

import android.util.Log;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
/* loaded from: classes23.dex */
public class FresnelAttribute extends Attribute {
    public static final int BODY_STATUS_FULL_DAY = 2;
    public static final int BODY_STATUS_FULL_NIGHT = 3;
    public static final int BODY_STATUS_HALF_DAY = 0;
    public static final int BODY_STATUS_HALF_NIGHT = 1;
    public static final int BODY_STATUS_NULL = -1;
    public static final int WARINIG_LEVEL_1 = 1;
    public static final int WARINIG_LEVEL_2 = 2;
    public static final int WARINIG_LEVEL_NORMAL = 0;
    public static final int WINDOW_STATUS_DIALOG = 1;
    public static final int WINDOW_STATUS_INFOFLOW = 0;
    private final long ANIMATION_DURATION;
    public float mBodyRenderY;
    public int mBodyStatus;
    public Color mCenterColor;
    public Color mDefaultEndColor;
    public Color mDefaultStartColor;
    public Color mEndColor;
    public float mFactor;
    public float mInterpolatedTime;
    public float mLow;
    public int mPow;
    public Color mStartColor;
    public long mStartTime;
    public int mWarningLevel;
    public int mWindowStatus;
    public static final String Alias = "fresnel";
    public static final long Type = register(Alias);
    protected static long Mask = Type;

    public void setPow(int pow) {
        this.mPow = pow;
    }

    public int getPow() {
        return this.mPow;
    }

    public float getFactor() {
        return this.mFactor;
    }

    public float getInterpolatedTime() {
        return this.mInterpolatedTime;
    }

    public void setAnimTime(long animTime) {
        long time = Math.abs(animTime - this.mStartTime);
        float interpolatedTime = ((float) time) / 500.0f;
        this.mInterpolatedTime = interpolatedTime <= 1.0f ? interpolatedTime : 1.0f;
        Log.d("lun", "mInterpolatedTime:" + this.mInterpolatedTime);
    }

    public float getLow() {
        return this.mLow;
    }

    public int getWarningLevel() {
        return this.mWarningLevel;
    }

    public void setWarningLevel(int level) {
        this.mInterpolatedTime = 0.0f;
        this.mStartTime = System.currentTimeMillis();
        this.mWarningLevel = level;
        if (level == 1) {
            setStartColor(0.63f, 0.9f, 1.0f, 1.0f);
            setCenterColor(1.0f, 0.9f, 0.57f, 1.0f);
            setEndColor(0.63f, 0.9f, 1.0f, 1.0f);
        } else if (level == 2) {
            setStartColor(0.63f, 0.9f, 1.0f, 1.0f);
            setCenterColor(1.0f, 0.37f, 0.37f, 1.0f);
            setEndColor(0.63f, 0.9f, 1.0f, 1.0f);
        }
    }

    public Color getDefaultStartColor() {
        if (this.mDefaultStartColor == null) {
            this.mDefaultStartColor = new Color();
        }
        return this.mDefaultStartColor;
    }

    public Color getDefaultEndColor() {
        if (this.mDefaultEndColor == null) {
            this.mDefaultEndColor = new Color();
        }
        return this.mDefaultEndColor;
    }

    public Color getStartColor() {
        if (this.mStartColor == null) {
            this.mStartColor = new Color();
        }
        return this.mStartColor;
    }

    public Color getCenterColor() {
        if (this.mCenterColor == null) {
            this.mCenterColor = new Color();
        }
        return this.mCenterColor;
    }

    public Color getEndColor() {
        if (this.mEndColor == null) {
            this.mEndColor = new Color();
        }
        return this.mEndColor;
    }

    public void setFactor(float factor) {
        this.mFactor = factor;
    }

    public void setLow(float low) {
        this.mLow = low;
    }

    public void setBodyStatus(int status) {
        this.mBodyStatus = status;
    }

    public int getBodyStatus() {
        return this.mBodyStatus;
    }

    public void setBodyRenderY(float renderY) {
        this.mBodyRenderY = renderY;
    }

    public float getBodyRenderY() {
        return this.mBodyRenderY;
    }

    public void setWindowStatus(int status) {
        this.mWindowStatus = status;
    }

    public int getWindowStatus() {
        return this.mWindowStatus;
    }

    public void setDefaultStartColor(float r, float g, float b, float a) {
        getDefaultStartColor().set(r, g, b, a);
    }

    public void setDefautEndColor(float r, float g, float b, float a) {
        getDefaultEndColor().set(r, g, b, a);
    }

    public void setStartColor(float r, float g, float b, float a) {
        getStartColor().set(r, g, b, a);
    }

    public void setCenterColor(float r, float g, float b, float a) {
        getCenterColor().set(r, g, b, a);
    }

    public void setEndColor(float r, float g, float b, float a) {
        getEndColor().set(r, g, b, a);
    }

    public static final boolean is(long mask) {
        return (Mask & mask) != 0;
    }

    public FresnelAttribute(long type) {
        super(type);
        this.mPow = 2;
        this.mFactor = 1.5f;
        this.mLow = 0.0f;
        this.mWarningLevel = 0;
        this.mBodyStatus = -1;
        this.mWindowStatus = 0;
        this.mInterpolatedTime = 0.0f;
        this.ANIMATION_DURATION = 500L;
        if (!is(type)) {
            throw new GdxRuntimeException("Invalid type specified");
        }
    }

    @Override // com.badlogic.gdx.graphics.g3d.Attribute
    public Attribute copy() {
        return null;
    }

    @Override // java.lang.Comparable
    public int compareTo(Attribute attribute) {
        return this.type != attribute.type ? (int) (this.type - attribute.type) : ((FresnelAttribute) attribute).mStartColor.toIntBits() - this.mStartColor.toIntBits();
    }
}
