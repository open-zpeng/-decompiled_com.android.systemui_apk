package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import com.android.systemui.statusbar.notification.stack.StackStateAnimator;
import java.util.Arrays;
/* loaded from: classes19.dex */
public class ColorExtractor {
    private final int NUM_SAMPLES = 20;
    private final float[] mTmpHsv = new float[3];
    private final float[] mTmpHueScoreHistogram = new float[StackStateAnimator.ANIMATION_DURATION_STANDARD];
    private final int[] mTmpPixels = new int[20];
    private final SparseArray<Float> mTmpRgbScores = new SparseArray<>();

    public int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    public int findDominantColorByHue(Bitmap bitmap, int samples) {
        int height;
        int width;
        int height2 = bitmap.getHeight();
        int width2 = bitmap.getWidth();
        int sampleStride = (int) Math.sqrt((height2 * width2) / samples);
        if (sampleStride < 1) {
            sampleStride = 1;
        }
        float[] hsv = this.mTmpHsv;
        Arrays.fill(hsv, 0.0f);
        float[] hueScoreHistogram = this.mTmpHueScoreHistogram;
        Arrays.fill(hueScoreHistogram, 0.0f);
        float highScore = -1.0f;
        int bestHue = -1;
        int[] pixels = this.mTmpPixels;
        Arrays.fill(pixels, 0);
        int pixelCount = 0;
        for (int y = 0; y < height2; y += sampleStride) {
            int x = 0;
            while (x < width2) {
                int argb = bitmap.getPixel(x, y);
                int alpha = (argb >> 24) & 255;
                if (alpha < 128) {
                    height = height2;
                    width = width2;
                } else {
                    int rgb = argb | (-16777216);
                    Color.colorToHSV(rgb, hsv);
                    height = height2;
                    int hue = (int) hsv[0];
                    if (hue >= 0) {
                        width = width2;
                        if (hue < hueScoreHistogram.length) {
                            if (pixelCount < samples) {
                                pixels[pixelCount] = rgb;
                                pixelCount++;
                            }
                            hueScoreHistogram[hue] = hueScoreHistogram[hue] + (hsv[1] * hsv[2]);
                            if (hueScoreHistogram[hue] > highScore) {
                                highScore = hueScoreHistogram[hue];
                                bestHue = hue;
                            }
                        }
                    } else {
                        width = width2;
                    }
                }
                x += sampleStride;
                height2 = height;
                width2 = width;
            }
        }
        SparseArray<Float> rgbScores = this.mTmpRgbScores;
        rgbScores.clear();
        int bestColor = -16777216;
        float highScore2 = -1.0f;
        for (int i = 0; i < pixelCount; i++) {
            int rgb2 = pixels[i];
            Color.colorToHSV(rgb2, hsv);
            if (((int) hsv[0]) == bestHue) {
                float s = hsv[1];
                float v = hsv[2];
                int bucket = ((int) (s * 100.0f)) + ((int) (v * 10000.0f));
                float score = s * v;
                Float oldTotal = rgbScores.get(bucket);
                float newTotal = oldTotal == null ? score : oldTotal.floatValue() + score;
                rgbScores.put(bucket, Float.valueOf(newTotal));
                if (newTotal > highScore2) {
                    float highScore3 = newTotal;
                    bestColor = rgb2;
                    highScore2 = highScore3;
                }
            }
        }
        return bestColor;
    }
}
