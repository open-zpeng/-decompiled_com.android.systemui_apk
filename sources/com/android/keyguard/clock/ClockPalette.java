package com.android.keyguard.clock;

import android.graphics.Color;
import android.util.MathUtils;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;
/* compiled from: ClockPalette.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\r\u001a\u00020\u0004J\u0006\u0010\u000e\u001a\u00020\u0004J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014J\u000e\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lcom/android/keyguard/clock/ClockPalette;", "", "()V", "accentPrimary", "", "accentSecondaryDark", "accentSecondaryLight", "darkAmount", "", "darkHSV", "", "hsv", "lightHSV", "getPrimaryColor", "getSecondaryColor", "setColorPalette", "", "supportsDarkText", "", "colorPalette", "", "setDarkAmount", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes19.dex */
public final class ClockPalette {
    private float darkAmount;
    private int accentPrimary = -1;
    private int accentSecondaryLight = -1;
    private int accentSecondaryDark = -16777216;
    private final float[] lightHSV = new float[3];
    private final float[] darkHSV = new float[3];
    private final float[] hsv = new float[3];

    public final int getPrimaryColor() {
        return this.accentPrimary;
    }

    public final int getSecondaryColor() {
        Color.colorToHSV(this.accentSecondaryLight, this.lightHSV);
        Color.colorToHSV(this.accentSecondaryDark, this.darkHSV);
        for (int i = 0; i <= 2; i++) {
            this.hsv[i] = MathUtils.lerp(this.darkHSV[i], this.lightHSV[i], this.darkAmount);
        }
        return Color.HSVToColor(this.hsv);
    }

    public final void setColorPalette(boolean supportsDarkText, @Nullable int[] colorPalette) {
        if (colorPalette != null) {
            if (!(colorPalette.length == 0)) {
                int length = colorPalette.length;
                this.accentPrimary = colorPalette[Math.max(0, length - 5)];
                this.accentSecondaryLight = colorPalette[Math.max(0, length - 2)];
                this.accentSecondaryDark = colorPalette[Math.max(0, length - (supportsDarkText ? 8 : 2))];
                return;
            }
        }
        this.accentPrimary = -1;
        this.accentSecondaryLight = -1;
        this.accentSecondaryDark = supportsDarkText ? -16777216 : -1;
    }

    public final void setDarkAmount(float darkAmount) {
        this.darkAmount = darkAmount;
    }
}
