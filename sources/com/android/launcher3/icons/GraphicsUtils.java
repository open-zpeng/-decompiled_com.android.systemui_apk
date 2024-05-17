package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.Log;
import androidx.annotation.ColorInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/* loaded from: classes19.dex */
public class GraphicsUtils {
    private static final String TAG = "GraphicsUtils";

    @ColorInt
    public static int setColorAlphaBound(int color, int alpha) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }
        return (16777215 & color) | (alpha << 24);
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w(TAG, "Could not write bitmap");
            return null;
        }
    }

    public static int getArea(Region r) {
        RegionIterator itr = new RegionIterator(r);
        int area = 0;
        Rect tempRect = new Rect();
        while (itr.next(tempRect)) {
            area += tempRect.width() * tempRect.height();
        }
        return area;
    }
}
