package com.android.systemui.shared.system;

import android.app.WallpaperColors;
import android.content.Context;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.types.Tonal;
/* loaded from: classes21.dex */
public class TonalCompat {
    private final Tonal mTonal;

    /* loaded from: classes21.dex */
    public static class ExtractionInfo {
        public int mainColor;
        public int secondaryColor;
        public boolean supportsDarkText;
        public boolean supportsDarkTheme;
    }

    public TonalCompat(Context context) {
        this.mTonal = new Tonal(context);
    }

    public ExtractionInfo extractDarkColors(WallpaperColors colors) {
        ColorExtractor.GradientColors darkColors = new ColorExtractor.GradientColors();
        this.mTonal.extractInto(colors, new ColorExtractor.GradientColors(), darkColors, new ColorExtractor.GradientColors());
        ExtractionInfo result = new ExtractionInfo();
        result.mainColor = darkColors.getMainColor();
        result.secondaryColor = darkColors.getSecondaryColor();
        result.supportsDarkText = darkColors.supportsDarkText();
        if (colors != null) {
            result.supportsDarkTheme = (colors.getColorHints() & 2) != 0;
        }
        return result;
    }
}
