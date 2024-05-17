package com.xiaopeng.xui.view.font;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import com.xiaopeng.xui.Xui;
/* loaded from: classes25.dex */
public class XFontScaleHelper {
    private float mComplexToFloat;

    @Nullable
    public static XFontScaleHelper create(@NonNull Resources resources, @DimenRes int value) {
        if (Xui.isFontScaleDynamicChangeEnable()) {
            float complexToFloat = complexToFloatForSp(resources, value);
            if (complexToFloat != -1.0f) {
                return new XFontScaleHelper(complexToFloat);
            }
            return null;
        }
        return null;
    }

    @Nullable
    public static XFontScaleHelper create(@NonNull TypedArray typedArray, @StyleableRes int index) {
        return create(typedArray, index, 0);
    }

    @Nullable
    public static XFontScaleHelper create(@NonNull TypedArray typedArray, @StyleableRes int index, @DimenRes int defaultValue) {
        if (Xui.isFontScaleDynamicChangeEnable()) {
            float complexToFloat = complexToFloatForSp(typedArray, index, defaultValue);
            if (complexToFloat != -1.0f) {
                return new XFontScaleHelper(complexToFloat);
            }
            return null;
        }
        return null;
    }

    private XFontScaleHelper(float complexToFloat) {
        this.mComplexToFloat = complexToFloat;
    }

    public float getCurrentFontSize(@NonNull DisplayMetrics displayMetrics) {
        return TypedValue.applyDimension(2, this.mComplexToFloat, displayMetrics);
    }

    public void refreshTextSize(@NonNull TextView textView) {
        textView.setTextSize(this.mComplexToFloat);
    }

    private static float complexToFloatForSp(@NonNull TypedArray typedArray, @StyleableRes int index, @DimenRes int defaultValue) {
        if (typedArray.hasValue(index)) {
            TypedValue out = new TypedValue();
            typedArray.getValue(index, out);
            if (out.getComplexUnit() == 2) {
                return TypedValue.complexToFloat(out.data);
            }
            return -1.0f;
        }
        return complexToFloatForSp(typedArray.getResources(), defaultValue);
    }

    private static float complexToFloatForSp(@NonNull Resources resources, @DimenRes int defaultValue) {
        if (defaultValue != 0) {
            TypedValue out = new TypedValue();
            resources.getValue(defaultValue, out, true);
            if (out.getComplexUnit() == 2) {
                return TypedValue.complexToFloat(out.data);
            }
            return -1.0f;
        }
        return -1.0f;
    }
}
