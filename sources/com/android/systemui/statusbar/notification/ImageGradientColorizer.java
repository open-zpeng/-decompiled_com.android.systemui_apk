package com.android.systemui.statusbar.notification;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
/* loaded from: classes21.dex */
public class ImageGradientColorizer {
    public Bitmap colorize(Drawable drawable, int backgroundColor, boolean isRtl) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int size = Math.min(width, height);
        int widthInset = (width - size) / 2;
        int heightInset = (height - size) / 2;
        Drawable drawable2 = drawable.mutate();
        drawable2.setBounds(-widthInset, -heightInset, width - widthInset, height - heightInset);
        Bitmap newBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        int tri = Color.red(backgroundColor);
        int tgi = Color.green(backgroundColor);
        int tbi = Color.blue(backgroundColor);
        float tr = tri / 255.0f;
        float tg = tgi / 255.0f;
        float tb = tbi / 255.0f;
        float cLum = ((tr * 0.2126f) + (tg * 0.7152f) + (tb * 0.0722f)) * 255.0f;
        ColorMatrix m = new ColorMatrix(new float[]{0.2126f, 0.7152f, 0.0722f, 0.0f, tri - cLum, 0.2126f, 0.7152f, 0.0722f, 0.0f, tgi - cLum, 0.2126f, 0.7152f, 0.0722f, 0.0f, tbi - cLum, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
        Paint paint = new Paint(1);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, size, 0.0f, new int[]{0, Color.argb(0.5f, 1.0f, 1.0f, 1.0f), -16777216}, new float[]{0.0f, 0.4f, 1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        Bitmap fadeIn = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas fadeInCanvas = new Canvas(fadeIn);
        drawable2.clearColorFilter();
        drawable2.draw(fadeInCanvas);
        if (isRtl) {
            fadeInCanvas.translate(size, 0.0f);
            fadeInCanvas.scale(-1.0f, 1.0f);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        fadeInCanvas.drawPaint(paint);
        Paint coloredPaint = new Paint(1);
        coloredPaint.setColorFilter(new ColorMatrixColorFilter(m));
        coloredPaint.setAlpha(127);
        canvas.drawBitmap(fadeIn, 0.0f, 0.0f, coloredPaint);
        LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, size, 0.0f, new int[]{0, Color.argb(0.5f, 1.0f, 1.0f, 1.0f), -16777216}, new float[]{0.0f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient2);
        fadeInCanvas.drawPaint(paint);
        canvas.drawBitmap(fadeIn, 0.0f, 0.0f, (Paint) null);
        return newBitmap;
    }
}
