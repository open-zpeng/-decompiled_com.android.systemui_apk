package com.android.systemui;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.jarvisproto.DMEnd;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: DualToneHandler.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u0012B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ \u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\tH\u0002J\u000e\u0010\u000e\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0002\u001a\u00020\u0003R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/android/systemui/DualToneHandler;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "darkColor", "Lcom/android/systemui/DualToneHandler$Color;", "lightColor", "getBackgroundColor", "", "intensity", "", "getColorForDarkIntensity", "darkIntensity", "getFillColor", "getSingleColor", "setColorsFromContext", "", "Color", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class DualToneHandler {
    private Color darkColor;
    private Color lightColor;

    public DualToneHandler(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        setColorsFromContext(context);
    }

    public final void setColorsFromContext(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        ContextThemeWrapper dualToneDarkTheme = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R.attr.darkIconTheme));
        ContextThemeWrapper dualToneLightTheme = new ContextThemeWrapper(context, Utils.getThemeAttr(context, R.attr.lightIconTheme));
        this.darkColor = new Color(Utils.getColorAttrDefaultColor(dualToneDarkTheme, R.attr.singleToneColor), Utils.getColorAttrDefaultColor(dualToneDarkTheme, R.attr.backgroundColor), Utils.getColorAttrDefaultColor(dualToneDarkTheme, R.attr.fillColor));
        this.lightColor = new Color(Utils.getColorAttrDefaultColor(dualToneLightTheme, R.attr.singleToneColor), Utils.getColorAttrDefaultColor(dualToneLightTheme, R.attr.backgroundColor), Utils.getColorAttrDefaultColor(dualToneLightTheme, R.attr.fillColor));
    }

    private final int getColorForDarkIntensity(float darkIntensity, int lightColor, int darkColor) {
        Object evaluate = ArgbEvaluator.getInstance().evaluate(darkIntensity, Integer.valueOf(lightColor), Integer.valueOf(darkColor));
        if (evaluate != null) {
            return ((Integer) evaluate).intValue();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Int");
    }

    public final int getSingleColor(float intensity) {
        Color color = this.lightColor;
        if (color == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lightColor");
        }
        int single = color.getSingle();
        Color color2 = this.darkColor;
        if (color2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("darkColor");
        }
        return getColorForDarkIntensity(intensity, single, color2.getSingle());
    }

    public final int getBackgroundColor(float intensity) {
        Color color = this.lightColor;
        if (color == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lightColor");
        }
        int background = color.getBackground();
        Color color2 = this.darkColor;
        if (color2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("darkColor");
        }
        return getColorForDarkIntensity(intensity, background, color2.getBackground());
    }

    public final int getFillColor(float intensity) {
        Color color = this.lightColor;
        if (color == null) {
            Intrinsics.throwUninitializedPropertyAccessException("lightColor");
        }
        int fill = color.getFill();
        Color color2 = this.darkColor;
        if (color2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("darkColor");
        }
        return getColorForDarkIntensity(intensity, fill, color2.getFill());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: DualToneHandler.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b¨\u0006\u0015"}, d2 = {"Lcom/android/systemui/DualToneHandler$Color;", "", "single", "", ThemeManager.AttributeSet.BACKGROUND, "fill", "(III)V", "getBackground", "()I", "getFill", "getSingle", "component1", "component2", "component3", "copy", "equals", "", DMEnd.REASON_OTHER, "hashCode", "toString", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Color {
        private final int background;
        private final int fill;
        private final int single;

        @NotNull
        public static /* synthetic */ Color copy$default(Color color, int i, int i2, int i3, int i4, Object obj) {
            if ((i4 & 1) != 0) {
                i = color.single;
            }
            if ((i4 & 2) != 0) {
                i2 = color.background;
            }
            if ((i4 & 4) != 0) {
                i3 = color.fill;
            }
            return color.copy(i, i2, i3);
        }

        public final int component1() {
            return this.single;
        }

        public final int component2() {
            return this.background;
        }

        public final int component3() {
            return this.fill;
        }

        @NotNull
        public final Color copy(int i, int i2, int i3) {
            return new Color(i, i2, i3);
        }

        public boolean equals(@Nullable Object obj) {
            if (this != obj) {
                if (obj instanceof Color) {
                    Color color = (Color) obj;
                    if (this.single == color.single) {
                        if (this.background == color.background) {
                            if (this.fill == color.fill) {
                            }
                        }
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (((Integer.hashCode(this.single) * 31) + Integer.hashCode(this.background)) * 31) + Integer.hashCode(this.fill);
        }

        @NotNull
        public String toString() {
            return "Color(single=" + this.single + ", background=" + this.background + ", fill=" + this.fill + NavigationBarInflaterView.KEY_CODE_END;
        }

        public Color(int single, int background, int fill) {
            this.single = single;
            this.background = background;
            this.fill = fill;
        }

        public final int getBackground() {
            return this.background;
        }

        public final int getFill() {
            return this.fill;
        }

        public final int getSingle() {
            return this.single;
        }
    }
}
