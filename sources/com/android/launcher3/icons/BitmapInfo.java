package com.android.launcher3.icons;

import android.graphics.Bitmap;
/* loaded from: classes19.dex */
public class BitmapInfo {
    public static final Bitmap LOW_RES_ICON = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
    public int color;
    public Bitmap icon;

    public void applyTo(BitmapInfo info) {
        info.icon = this.icon;
        info.color = this.color;
    }

    public final boolean isLowRes() {
        return LOW_RES_ICON == this.icon;
    }

    public static BitmapInfo fromBitmap(Bitmap bitmap) {
        return fromBitmap(bitmap, null);
    }

    public static BitmapInfo fromBitmap(Bitmap bitmap, ColorExtractor dominantColorExtractor) {
        int i;
        BitmapInfo info = new BitmapInfo();
        info.icon = bitmap;
        if (dominantColorExtractor != null) {
            i = dominantColorExtractor.findDominantColorByHue(bitmap);
        } else {
            i = 0;
        }
        info.color = i;
        return info;
    }
}
