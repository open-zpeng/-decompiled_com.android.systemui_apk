package com.xiaopeng.systemui.infoflow.widget;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes24.dex */
public class RoundedDrawable extends Drawable {
    public static final int DEFAULT_BORDER_COLOR = -16777216;
    public static final String TAG = "RoundedDrawable";
    private final Bitmap mBitmap;
    private final int mBitmapHeight;
    private final Paint mBitmapPaint;
    private final int mBitmapWidth;
    private final Paint mBorderPaint;
    private final RectF mBounds = new RectF();
    private final RectF mDrawableRect = new RectF();
    private final RectF mBitmapRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private final Matrix mShaderMatrix = new Matrix();
    private final RectF mSquareCornersRect = new RectF();
    private Shader.TileMode mTileModeX = Shader.TileMode.CLAMP;
    private Shader.TileMode mTileModeY = Shader.TileMode.CLAMP;
    private boolean mRebuildShader = true;
    private float mCornerRadius = 0.0f;
    private final boolean[] mCornersRounded = {true, true, true, true};
    private boolean mOval = false;
    private float mBorderWidth = 0.0f;
    private ColorStateList mBorderColor = ColorStateList.valueOf(-16777216);
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;

    public RoundedDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mBitmapWidth = bitmap.getWidth();
        this.mBitmapHeight = bitmap.getHeight();
        this.mBitmapRect.set(0.0f, 0.0f, this.mBitmapWidth, this.mBitmapHeight);
        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setStyle(Paint.Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(getState(), -16777216));
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
    }

    public static RoundedDrawable fromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return new RoundedDrawable(bitmap);
        }
        return null;
    }

    public static Drawable fromDrawable(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof RoundedDrawable) {
                return drawable;
            }
            if (drawable instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable) drawable;
                int num = ld.getNumberOfLayers();
                for (int i = 0; i < num; i++) {
                    Drawable d = ld.getDrawable(i);
                    ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d));
                }
                return ld;
            }
            Bitmap bm = drawableToBitmap(drawable);
            if (bm != null) {
                return new RoundedDrawable(bm);
            }
        }
        return drawable;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = Math.max(drawable.getIntrinsicWidth(), 2);
        int height = Math.max(drawable.getIntrinsicHeight(), 2);
        try {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Throwable e) {
            e.printStackTrace();
            Log.w(TAG, "Failed to create bitmap from drawable!");
            return null;
        }
    }

    public Bitmap getSourceBitmap() {
        return this.mBitmap;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mBorderColor.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        int newColor = this.mBorderColor.getColorForState(state, 0);
        if (this.mBorderPaint.getColor() != newColor) {
            this.mBorderPaint.setColor(newColor);
            return true;
        }
        return super.onStateChange(state);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.infoflow.widget.RoundedDrawable$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ImageView.ScaleType.values().length];

        static {
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_CROP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_INSIDE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private void updateShaderMatrix() {
        float scale;
        float scale2;
        int i = AnonymousClass1.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
        if (i == 1) {
            this.mBorderRect.set(this.mBounds);
            RectF rectF = this.mBorderRect;
            float f = this.mBorderWidth;
            rectF.inset(f / 2.0f, f / 2.0f);
            this.mShaderMatrix.reset();
            this.mShaderMatrix.setTranslate((int) (((this.mBorderRect.width() - this.mBitmapWidth) * 0.5f) + 0.5f), (int) (((this.mBorderRect.height() - this.mBitmapHeight) * 0.5f) + 0.5f));
        } else if (i == 2) {
            this.mBorderRect.set(this.mBounds);
            RectF rectF2 = this.mBorderRect;
            float f2 = this.mBorderWidth;
            rectF2.inset(f2 / 2.0f, f2 / 2.0f);
            this.mShaderMatrix.reset();
            float dx = 0.0f;
            float dy = 0.0f;
            if (this.mBitmapWidth * this.mBorderRect.height() > this.mBorderRect.width() * this.mBitmapHeight) {
                scale = this.mBorderRect.height() / this.mBitmapHeight;
                dx = (this.mBorderRect.width() - (this.mBitmapWidth * scale)) * 0.5f;
            } else {
                scale = this.mBorderRect.width() / this.mBitmapWidth;
                dy = (this.mBorderRect.height() - (this.mBitmapHeight * scale)) * 0.5f;
            }
            this.mShaderMatrix.setScale(scale, scale);
            Matrix matrix = this.mShaderMatrix;
            float f3 = this.mBorderWidth;
            matrix.postTranslate(((int) (dx + 0.5f)) + (f3 / 2.0f), ((int) (0.5f + dy)) + (f3 / 2.0f));
        } else if (i == 3) {
            this.mShaderMatrix.reset();
            if (this.mBitmapWidth <= this.mBounds.width() && this.mBitmapHeight <= this.mBounds.height()) {
                scale2 = 1.0f;
            } else {
                scale2 = Math.min(this.mBounds.width() / this.mBitmapWidth, this.mBounds.height() / this.mBitmapHeight);
            }
            float dx2 = (int) (((this.mBounds.width() - (this.mBitmapWidth * scale2)) * 0.5f) + 0.5f);
            float dy2 = (int) (((this.mBounds.height() - (this.mBitmapHeight * scale2)) * 0.5f) + 0.5f);
            this.mShaderMatrix.setScale(scale2, scale2);
            this.mShaderMatrix.postTranslate(dx2, dy2);
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            RectF rectF3 = this.mBorderRect;
            float f4 = this.mBorderWidth;
            rectF3.inset(f4 / 2.0f, f4 / 2.0f);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
        } else if (i == 5) {
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, Matrix.ScaleToFit.END);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            RectF rectF4 = this.mBorderRect;
            float f5 = this.mBorderWidth;
            rectF4.inset(f5 / 2.0f, f5 / 2.0f);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
        } else if (i == 6) {
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, Matrix.ScaleToFit.START);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            RectF rectF5 = this.mBorderRect;
            float f6 = this.mBorderWidth;
            rectF5.inset(f6 / 2.0f, f6 / 2.0f);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
        } else if (i != 7) {
            this.mBorderRect.set(this.mBitmapRect);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBounds, Matrix.ScaleToFit.CENTER);
            this.mShaderMatrix.mapRect(this.mBorderRect);
            RectF rectF6 = this.mBorderRect;
            float f7 = this.mBorderWidth;
            rectF6.inset(f7 / 2.0f, f7 / 2.0f);
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
        } else {
            this.mBorderRect.set(this.mBounds);
            RectF rectF7 = this.mBorderRect;
            float f8 = this.mBorderWidth;
            rectF7.inset(f8 / 2.0f, f8 / 2.0f);
            this.mShaderMatrix.reset();
            this.mShaderMatrix.setRectToRect(this.mBitmapRect, this.mBorderRect, Matrix.ScaleToFit.FILL);
        }
        this.mDrawableRect.set(this.mBorderRect);
        this.mRebuildShader = true;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        this.mBounds.set(bounds);
        updateShaderMatrix();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        if (this.mRebuildShader) {
            BitmapShader bitmapShader = new BitmapShader(this.mBitmap, this.mTileModeX, this.mTileModeY);
            if (this.mTileModeX == Shader.TileMode.CLAMP && this.mTileModeY == Shader.TileMode.CLAMP) {
                bitmapShader.setLocalMatrix(this.mShaderMatrix);
            }
            this.mBitmapPaint.setShader(bitmapShader);
            this.mRebuildShader = false;
        }
        if (this.mOval) {
            if (this.mBorderWidth > 0.0f) {
                canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
                canvas.drawOval(this.mBorderRect, this.mBorderPaint);
                return;
            }
            canvas.drawOval(this.mDrawableRect, this.mBitmapPaint);
        } else if (any(this.mCornersRounded)) {
            float radius = this.mCornerRadius;
            if (this.mBorderWidth > 0.0f) {
                canvas.drawRoundRect(this.mDrawableRect, radius, radius, this.mBitmapPaint);
                canvas.drawRoundRect(this.mBorderRect, radius, radius, this.mBorderPaint);
                redrawBitmapForSquareCorners(canvas);
                redrawBorderForSquareCorners(canvas);
                return;
            }
            canvas.drawRoundRect(this.mDrawableRect, radius, radius, this.mBitmapPaint);
            redrawBitmapForSquareCorners(canvas);
        } else {
            canvas.drawRect(this.mDrawableRect, this.mBitmapPaint);
            if (this.mBorderWidth > 0.0f) {
                canvas.drawRect(this.mBorderRect, this.mBorderPaint);
            }
        }
    }

    private void redrawBitmapForSquareCorners(Canvas canvas) {
        if (all(this.mCornersRounded) || this.mCornerRadius == 0.0f) {
            return;
        }
        float left = this.mDrawableRect.left;
        float top = this.mDrawableRect.top;
        float right = this.mDrawableRect.width() + left;
        float bottom = this.mDrawableRect.height() + top;
        float radius = this.mCornerRadius;
        if (!this.mCornersRounded[0]) {
            this.mSquareCornersRect.set(left, top, left + radius, top + radius);
            canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
        }
        if (!this.mCornersRounded[1]) {
            this.mSquareCornersRect.set(right - radius, top, right, radius);
            canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
        }
        if (!this.mCornersRounded[2]) {
            this.mSquareCornersRect.set(right - radius, bottom - radius, right, bottom);
            canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
        }
        if (!this.mCornersRounded[3]) {
            this.mSquareCornersRect.set(left, bottom - radius, left + radius, bottom);
            canvas.drawRect(this.mSquareCornersRect, this.mBitmapPaint);
        }
    }

    private void redrawBorderForSquareCorners(Canvas canvas) {
        if (all(this.mCornersRounded) || this.mCornerRadius == 0.0f) {
            return;
        }
        float left = this.mDrawableRect.left;
        float top = this.mDrawableRect.top;
        float right = left + this.mDrawableRect.width();
        float bottom = top + this.mDrawableRect.height();
        float radius = this.mCornerRadius;
        float offset = this.mBorderWidth / 2.0f;
        if (!this.mCornersRounded[0]) {
            canvas.drawLine(left - offset, top, left + radius, top, this.mBorderPaint);
            canvas.drawLine(left, top - offset, left, top + radius, this.mBorderPaint);
        }
        if (!this.mCornersRounded[1]) {
            canvas.drawLine((right - radius) - offset, top, right, top, this.mBorderPaint);
            canvas.drawLine(right, top - offset, right, top + radius, this.mBorderPaint);
        }
        if (!this.mCornersRounded[2]) {
            canvas.drawLine((right - radius) - offset, bottom, right + offset, bottom, this.mBorderPaint);
            canvas.drawLine(right, bottom - radius, right, bottom, this.mBorderPaint);
        }
        if (!this.mCornersRounded[3]) {
            canvas.drawLine(left - offset, bottom, left + radius, bottom, this.mBorderPaint);
            canvas.drawLine(left, bottom - radius, left, bottom, this.mBorderPaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mBitmapPaint.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mBitmapPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mBitmapPaint.getColorFilter();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mBitmapPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        this.mBitmapPaint.setDither(dither);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean filter) {
        this.mBitmapPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public float getCornerRadius(int corner) {
        if (this.mCornersRounded[corner]) {
            return this.mCornerRadius;
        }
        return 0.0f;
    }

    public RoundedDrawable setCornerRadius(float radius) {
        setCornerRadius(radius, radius, radius, radius);
        return this;
    }

    public RoundedDrawable setCornerRadius(int corner, float radius) {
        if (radius != 0.0f) {
            float f = this.mCornerRadius;
            if (f != 0.0f && f != radius) {
                throw new IllegalArgumentException("Multiple nonzero corner radii not yet supported.");
            }
        }
        if (radius != 0.0f) {
            if (this.mCornerRadius == 0.0f) {
                this.mCornerRadius = radius;
            }
            this.mCornersRounded[corner] = true;
        } else {
            if (only(corner, this.mCornersRounded)) {
                this.mCornerRadius = 0.0f;
            }
            this.mCornersRounded[corner] = false;
        }
        return this;
    }

    public RoundedDrawable setCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        Set<Float> radiusSet = new HashSet<>(4);
        radiusSet.add(Float.valueOf(topLeft));
        radiusSet.add(Float.valueOf(topRight));
        radiusSet.add(Float.valueOf(bottomRight));
        radiusSet.add(Float.valueOf(bottomLeft));
        radiusSet.remove(Float.valueOf(0.0f));
        if (radiusSet.size() > 1) {
            throw new IllegalArgumentException("Multiple nonzero corner radii not yet supported.");
        }
        if (!radiusSet.isEmpty()) {
            float radius = radiusSet.iterator().next().floatValue();
            if (Float.isInfinite(radius) || Float.isNaN(radius) || radius < 0.0f) {
                throw new IllegalArgumentException("Invalid radius value: " + radius);
            }
            this.mCornerRadius = radius;
        } else {
            this.mCornerRadius = 0.0f;
        }
        this.mCornersRounded[0] = topLeft > 0.0f;
        this.mCornersRounded[1] = topRight > 0.0f;
        this.mCornersRounded[2] = bottomRight > 0.0f;
        this.mCornersRounded[3] = bottomLeft > 0.0f;
        return this;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public RoundedDrawable setBorderWidth(float width) {
        this.mBorderWidth = width;
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        return this;
    }

    public int getBorderColor() {
        return this.mBorderColor.getDefaultColor();
    }

    public RoundedDrawable setBorderColor(@ColorInt int color) {
        return setBorderColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getBorderColors() {
        return this.mBorderColor;
    }

    public RoundedDrawable setBorderColor(ColorStateList colors) {
        this.mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
        this.mBorderPaint.setColor(this.mBorderColor.getColorForState(getState(), -16777216));
        return this;
    }

    public boolean isOval() {
        return this.mOval;
    }

    public RoundedDrawable setOval(boolean oval) {
        this.mOval = oval;
        return this;
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public RoundedDrawable setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            scaleType = ImageView.ScaleType.FIT_CENTER;
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            updateShaderMatrix();
        }
        return this;
    }

    public Shader.TileMode getTileModeX() {
        return this.mTileModeX;
    }

    public RoundedDrawable setTileModeX(Shader.TileMode tileModeX) {
        if (this.mTileModeX != tileModeX) {
            this.mTileModeX = tileModeX;
            this.mRebuildShader = true;
            invalidateSelf();
        }
        return this;
    }

    public Shader.TileMode getTileModeY() {
        return this.mTileModeY;
    }

    public RoundedDrawable setTileModeY(Shader.TileMode tileModeY) {
        if (this.mTileModeY != tileModeY) {
            this.mTileModeY = tileModeY;
            this.mRebuildShader = true;
            invalidateSelf();
        }
        return this;
    }

    private static boolean only(int index, boolean[] booleans) {
        int i = 0;
        int len = booleans.length;
        while (true) {
            if (i >= len) {
                return true;
            }
            if (booleans[i] != (i == index)) {
                return false;
            }
            i++;
        }
    }

    private static boolean any(boolean[] booleans) {
        for (boolean b : booleans) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    private static boolean all(boolean[] booleans) {
        for (boolean b : booleans) {
            if (b) {
                return false;
            }
        }
        return true;
    }

    public Bitmap toBitmap() {
        return drawableToBitmap(this);
    }
}
