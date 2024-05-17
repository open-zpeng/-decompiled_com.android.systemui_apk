package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import java.nio.ByteBuffer;
import kotlin.UByte;
/* loaded from: classes19.dex */
public class IconNormalizer {
    private static final float BOUND_RATIO_MARGIN = 0.05f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final boolean DEBUG = false;
    public static final float ICON_VISIBLE_AREA_FACTOR = 0.92f;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final String TAG = "IconNormalizer";
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas;
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IconNormalizer(Context context, int iconBitmapSize, boolean shapeDetection) {
        this.mMaxSize = iconBitmapSize * 2;
        int i = this.mMaxSize;
        this.mBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ALPHA_8);
        this.mCanvas = new Canvas(this.mBitmap);
        int i2 = this.mMaxSize;
        this.mPixels = new byte[i2 * i2];
        this.mLeftBorder = new float[i2];
        this.mRightBorder = new float[i2];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        this.mPaintMaskShape = new Paint();
        this.mPaintMaskShape.setColor(-65536);
        this.mPaintMaskShape.setStyle(Paint.Style.FILL);
        this.mPaintMaskShape.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        this.mPaintMaskShapeOutline = new Paint();
        this.mPaintMaskShapeOutline.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Paint.Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = shapeDetection;
    }

    private static float getScale(float hullArea, float boundingArea, float fullArea) {
        float scaleRequired;
        float hullByRect = hullArea / boundingArea;
        if (hullByRect < CIRCLE_AREA_BY_RECT) {
            scaleRequired = MAX_CIRCLE_AREA_FACTOR;
        } else {
            scaleRequired = MAX_SQUARE_AREA_FACTOR + ((1.0f - hullByRect) * LINEAR_SCALE_SLOPE);
        }
        float areaScale = hullArea / fullArea;
        if (areaScale > scaleRequired) {
            return (float) Math.sqrt(scaleRequired / areaScale);
        }
        return 1.0f;
    }

    @TargetApi(26)
    public static float normalizeAdaptiveIcon(Drawable d, int size, @Nullable RectF outBounds) {
        Rect tmpBounds = new Rect(d.getBounds());
        d.setBounds(0, 0, size, size);
        Path path = ((AdaptiveIconDrawable) d).getIconMask();
        Region region = new Region();
        region.setPath(path, new Region(0, 0, size, size));
        Rect hullBounds = region.getBounds();
        int hullArea = GraphicsUtils.getArea(region);
        if (outBounds != null) {
            float sizeF = size;
            outBounds.set(hullBounds.left / sizeF, hullBounds.top / sizeF, 1.0f - (hullBounds.right / sizeF), 1.0f - (hullBounds.bottom / sizeF));
        }
        d.setBounds(tmpBounds);
        return getScale(hullArea, hullArea, size * size);
    }

    private boolean isShape(Path maskPath) {
        float iconRatio = this.mBounds.width() / this.mBounds.height();
        if (Math.abs(iconRatio - 1.0f) > BOUND_RATIO_MARGIN) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale(this.mBounds.width(), this.mBounds.height());
        this.mMatrix.postTranslate(this.mBounds.left, this.mBounds.top);
        maskPath.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        ByteBuffer buffer = ByteBuffer.wrap(this.mPixels);
        buffer.rewind();
        this.mBitmap.copyPixelsToBuffer(buffer);
        int y = this.mBounds.top;
        int i = this.mMaxSize;
        int index = y * i;
        int rowSizeDiff = i - this.mBounds.right;
        int sum = 0;
        while (y < this.mBounds.bottom) {
            int index2 = index + this.mBounds.left;
            for (int x = this.mBounds.left; x < this.mBounds.right; x++) {
                if ((this.mPixels[index2] & UByte.MAX_VALUE) > 40) {
                    sum++;
                }
                index2++;
            }
            index = index2 + rowSizeDiff;
            y++;
        }
        float percentageDiffPixels = sum / (this.mBounds.width() * this.mBounds.height());
        return percentageDiffPixels < PIXEL_DIFF_PERCENTAGE_THRESHOLD;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0055, code lost:
        if (r4 <= r20.mMaxSize) goto L86;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0058, code lost:
        r6 = r4;
     */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00ef A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0109 A[Catch: all -> 0x018d, TryCatch #0 {, blocks: (B:4:0x0009, B:6:0x000d, B:8:0x0011, B:10:0x0018, B:12:0x0024, B:13:0x0029, B:16:0x002d, B:20:0x003a, B:22:0x003e, B:39:0x0069, B:44:0x00a4, B:50:0x00b5, B:51:0x00bd, B:56:0x00d4, B:57:0x00df, B:62:0x00f9, B:64:0x0109, B:68:0x011f, B:67:0x0114, B:69:0x0122, B:71:0x0134, B:74:0x0161, B:76:0x0165, B:78:0x0168, B:80:0x0174, B:24:0x0042, B:26:0x0053, B:33:0x005f, B:37:0x0066, B:30:0x005a), top: B:89:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0134 A[Catch: all -> 0x018d, TryCatch #0 {, blocks: (B:4:0x0009, B:6:0x000d, B:8:0x0011, B:10:0x0018, B:12:0x0024, B:13:0x0029, B:16:0x002d, B:20:0x003a, B:22:0x003e, B:39:0x0069, B:44:0x00a4, B:50:0x00b5, B:51:0x00bd, B:56:0x00d4, B:57:0x00df, B:62:0x00f9, B:64:0x0109, B:68:0x011f, B:67:0x0114, B:69:0x0122, B:71:0x0134, B:74:0x0161, B:76:0x0165, B:78:0x0168, B:80:0x0174, B:24:0x0042, B:26:0x0053, B:33:0x005f, B:37:0x0066, B:30:0x005a), top: B:89:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x015b  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0185 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized float getScale(@androidx.annotation.NonNull android.graphics.drawable.Drawable r21, @androidx.annotation.Nullable android.graphics.RectF r22, @androidx.annotation.Nullable android.graphics.Path r23, @androidx.annotation.Nullable boolean[] r24) {
        /*
            Method dump skipped, instructions count: 400
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }

    private static void convertToConvexArray(float[] xCoordinates, int direction, int topY, int bottomY) {
        int start;
        int total = xCoordinates.length;
        float[] angles = new float[total - 1];
        int last = -1;
        float lastAngle = Float.MAX_VALUE;
        for (int i = topY + 1; i <= bottomY; i++) {
            if (xCoordinates[i] > -1.0f) {
                if (lastAngle == Float.MAX_VALUE) {
                    start = topY;
                } else {
                    float currentAngle = (xCoordinates[i] - xCoordinates[last]) / (i - last);
                    int start2 = last;
                    if ((currentAngle - lastAngle) * direction >= 0.0f) {
                        start = start2;
                    } else {
                        start = start2;
                        while (start > topY) {
                            start--;
                            float currentAngle2 = (xCoordinates[i] - xCoordinates[start]) / (i - start);
                            if ((currentAngle2 - angles[start]) * direction >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                float lastAngle2 = (xCoordinates[i] - xCoordinates[start]) / (i - start);
                for (int j = start; j < i; j++) {
                    angles[j] = lastAngle2;
                    xCoordinates[j] = xCoordinates[start] + ((j - start) * lastAngle2);
                }
                last = i;
                lastAngle = lastAngle2;
            }
        }
    }

    public static int getNormalizedCircleSize(int size) {
        float area = size * size * MAX_CIRCLE_AREA_FACTOR;
        return (int) Math.round(Math.sqrt((4.0f * area) / 3.141592653589793d));
    }
}
