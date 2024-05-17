package com.android.systemui.glwallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class ImageProcessHelper {
    private static final float DEFAULT_OTSU_THRESHOLD = 0.0f;
    private static final float DEFAULT_THRESHOLD = 0.8f;
    private static final float MAX_THRESHOLD = 0.89f;
    private static final int MSG_UPDATE_THRESHOLD = 1;
    private final Handler mHandler = new Handler(new Handler.Callback() { // from class: com.android.systemui.glwallpaper.ImageProcessHelper.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                ImageProcessHelper.this.mThreshold = ((Float) msg.obj).floatValue();
                return true;
            }
            return false;
        }
    });
    private float mThreshold = 0.8f;
    private static final String TAG = ImageProcessHelper.class.getSimpleName();
    private static final float[] LUMINOSITY_MATRIX = {0.2126f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7152f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0722f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface ThresholdAlgorithm {
        float compute(Bitmap bitmap, int[] iArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start(Bitmap bitmap) {
        new ThresholdComputeTask(this.mHandler).execute(bitmap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getThreshold() {
        return Math.min(this.mThreshold, (float) MAX_THRESHOLD);
    }

    /* loaded from: classes21.dex */
    private static class ThresholdComputeTask extends AsyncTask<Bitmap, Void, Float> {
        private Handler mUpdateHandler;

        ThresholdComputeTask(Handler handler) {
            super(handler);
            this.mUpdateHandler = handler;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Float doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            Float valueOf = Float.valueOf(0.8f);
            if (bitmap == null) {
                Log.e(ImageProcessHelper.TAG, "ThresholdComputeTask: Can't get bitmap");
                return valueOf;
            }
            try {
                return Float.valueOf(new Threshold().compute(bitmap));
            } catch (RuntimeException e) {
                String str = ImageProcessHelper.TAG;
                Log.e(str, "Failed at computing threshold, color space=" + bitmap.getColorSpace(), e);
                return valueOf;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Float result) {
            Message msg = this.mUpdateHandler.obtainMessage(1, result);
            this.mUpdateHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class Threshold {
        private Threshold() {
        }

        public float compute(Bitmap bitmap) {
            Bitmap grayscale = toGrayscale(bitmap);
            int[] histogram = getHistogram(grayscale);
            boolean isSolidColor = isSolidColor(grayscale, histogram);
            ThresholdAlgorithm algorithm = isSolidColor ? new Percentile85() : new Otsus();
            return algorithm.compute(grayscale, histogram);
        }

        private Bitmap toGrayscale(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap grayscale = Bitmap.createBitmap(width, height, bitmap.getConfig(), false, bitmap.getColorSpace());
            Canvas canvas = new Canvas(grayscale);
            ColorMatrix cm = new ColorMatrix(ImageProcessHelper.LUMINOSITY_MATRIX);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(bitmap, new Matrix(), paint);
            return grayscale;
        }

        private int[] getHistogram(Bitmap grayscale) {
            int width = grayscale.getWidth();
            int height = grayscale.getHeight();
            int[] histogram = new int[256];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int pixel = grayscale.getPixel(col, row);
                    int y = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                    histogram[y] = histogram[y] + 1;
                }
            }
            return histogram;
        }

        private boolean isSolidColor(Bitmap bitmap, int[] histogram) {
            int pixels = bitmap.getWidth() * bitmap.getHeight();
            for (int value : histogram) {
                if (value != 0 && value != pixels) {
                    return false;
                }
                if (value == pixels) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class Percentile85 implements ThresholdAlgorithm {
        private Percentile85() {
        }

        @Override // com.android.systemui.glwallpaper.ImageProcessHelper.ThresholdAlgorithm
        public float compute(Bitmap bitmap, int[] histogram) {
            float per85 = 0.8f;
            int pixelCount = bitmap.getWidth() * bitmap.getHeight();
            float[] acc = new float[256];
            int i = 0;
            while (i < acc.length) {
                acc[i] = histogram[i] / pixelCount;
                float prev = i == 0 ? 0.0f : acc[i - 1];
                float next = acc[i];
                float idx = (i + 1) / 255.0f;
                float sum = prev + next;
                if (prev < 0.85f && sum >= 0.85f) {
                    per85 = idx;
                }
                if (i > 0) {
                    acc[i] = acc[i] + acc[i - 1];
                }
                i++;
            }
            return per85;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class Otsus implements ThresholdAlgorithm {
        private Otsus() {
        }

        @Override // com.android.systemui.glwallpaper.ImageProcessHelper.ThresholdAlgorithm
        public float compute(Bitmap bitmap, int[] histogram) {
            float threshold = 0.0f;
            float maxVariance = 0.0f;
            float pixelCount = bitmap.getWidth() * bitmap.getHeight();
            float[] w = new float[2];
            float[] m = new float[2];
            float[] u = new float[2];
            for (int i = 0; i < histogram.length; i++) {
                m[1] = m[1] + (histogram[i] * i);
            }
            w[1] = pixelCount;
            for (int tonalValue = 0; tonalValue < histogram.length; tonalValue++) {
                float numPixels = histogram[tonalValue];
                float tmp = tonalValue * numPixels;
                w[0] = w[0] + numPixels;
                w[1] = w[1] - numPixels;
                if (w[0] != 0.0f && w[1] != 0.0f) {
                    m[0] = m[0] + tmp;
                    m[1] = m[1] - tmp;
                    u[0] = m[0] / w[0];
                    u[1] = m[1] / w[1];
                    float dU = u[0] - u[1];
                    float variance = w[0] * w[1] * dU * dU;
                    if (variance > maxVariance) {
                        float threshold2 = (tonalValue + 1.0f) / histogram.length;
                        maxVariance = variance;
                        threshold = threshold2;
                    }
                }
            }
            return threshold;
        }
    }
}
