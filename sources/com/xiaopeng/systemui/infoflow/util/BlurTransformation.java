package com.xiaopeng.systemui.infoflow.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.xiaopeng.systemui.helper.BitmapHelper;
import java.security.MessageDigest;
/* loaded from: classes24.dex */
public class BlurTransformation extends BitmapTransformation {
    private static final String ID = "BlurTransformation.1";
    private static final int VERSION = 1;
    private int radius;
    private int sampling;
    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    public BlurTransformation() {
        this(MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius) {
        this(radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius, int sampling) {
        this.radius = radius;
        this.sampling = sampling;
    }

    @Override // com.bumptech.glide.load.resource.bitmap.BitmapTransformation
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap cropBitmap = BitmapHelper.getCropBitmap(toTransform);
        int width = cropBitmap.getWidth();
        int height = cropBitmap.getHeight();
        int i = this.sampling;
        int scaledWidth = width / i;
        int scaledHeight = height / i;
        Bitmap bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int i2 = this.sampling;
        canvas.scale(1.0f / i2, 1.0f / i2);
        Paint paint = new Paint();
        paint.setFlags(2);
        canvas.drawBitmap(cropBitmap, 0.0f, 0.0f, paint);
        return FastBlur.blur(bitmap, this.radius, true);
    }

    public String toString() {
        return "BlurTransformation(radius=" + this.radius + ", sampling=" + this.sampling + NavigationBarInflaterView.KEY_CODE_END;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        return (o instanceof BlurTransformation) && ((BlurTransformation) o).radius == this.radius && ((BlurTransformation) o).sampling == this.sampling;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        return ID.hashCode() + (this.radius * 1000) + (this.sampling * 10);
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update((ID + this.radius + this.sampling).getBytes(CHARSET));
    }
}
