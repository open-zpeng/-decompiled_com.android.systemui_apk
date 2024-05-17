package com.xiaopeng.systemui.infoflow.util;

import android.graphics.Bitmap;
import java.lang.reflect.Array;
/* loaded from: classes24.dex */
public class FastBlur {
    public static Bitmap blur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        int yp;
        int i;
        int p = radius;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        if (p < 1) {
            return null;
        }
        int w = bitmap.getWidth();
        int p2 = bitmap.getHeight();
        int[] pix = new int[w * p2];
        bitmap.getPixels(pix, 0, w, 0, 0, w, p2);
        int wm = w - 1;
        int hm = p2 - 1;
        int wh = w * p2;
        int div = p + p + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, p2)];
        int divsum = (div + 1) >> 1;
        int divsum2 = divsum * divsum;
        int[] dv = new int[divsum2 * 256];
        int i2 = 0;
        while (true) {
            int wh2 = wh;
            if (i2 >= divsum2 * 256) {
                break;
            }
            dv[i2] = i2 / divsum2;
            i2++;
            wh = wh2;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(int.class, div, 3);
        int r1 = p + 1;
        int y = 0;
        while (y < p2) {
            int h = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            int divsum3 = divsum2;
            int i3 = -p;
            int i4 = 0;
            while (i3 <= p) {
                Bitmap bitmap2 = bitmap;
                int i5 = h;
                int h2 = p2;
                int h3 = Math.max(i3, i5);
                int p3 = pix[yi + Math.min(wm, h3)];
                int[] sir = stack[i3 + p];
                sir[i5] = (p3 & 16711680) >> 16;
                sir[1] = (p3 & 65280) >> 8;
                sir[2] = p3 & 255;
                int rbs = r1 - Math.abs(i3);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                i4 += sir[2] * rbs;
                if (i3 > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                i3++;
                p2 = h2;
                bitmap = bitmap2;
                h = 0;
            }
            Bitmap bitmap3 = bitmap;
            int h4 = p2;
            int stackpointer = radius;
            int x = 0;
            while (x < w) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[i4];
                int rsum2 = rsum - routsum;
                int gsum2 = gsum - goutsum;
                int bsum = i4 - boutsum;
                int stackstart = (stackpointer - p) + div;
                int[] sir2 = stack[stackstart % div];
                int routsum2 = routsum - sir2[0];
                int goutsum2 = goutsum - sir2[1];
                int boutsum2 = boutsum - sir2[2];
                if (y != 0) {
                    i = i3;
                } else {
                    i = i3;
                    int i6 = x + p + 1;
                    vmin[x] = Math.min(i6, wm);
                }
                int i7 = vmin[x];
                int p4 = pix[yw + i7];
                sir2[0] = (p4 & 16711680) >> 16;
                sir2[1] = (p4 & 65280) >> 8;
                int wm2 = wm;
                int wm3 = p4 & 255;
                sir2[2] = wm3;
                int rinsum2 = rinsum + sir2[0];
                int ginsum2 = ginsum + sir2[1];
                int binsum2 = binsum + sir2[2];
                rsum = rsum2 + rinsum2;
                gsum = gsum2 + ginsum2;
                i4 = bsum + binsum2;
                stackpointer = (stackpointer + 1) % div;
                int[] sir3 = stack[stackpointer % div];
                routsum = routsum2 + sir3[0];
                goutsum = goutsum2 + sir3[1];
                boutsum = boutsum2 + sir3[2];
                rinsum = rinsum2 - sir3[0];
                ginsum = ginsum2 - sir3[1];
                binsum = binsum2 - sir3[2];
                yi++;
                x++;
                wm = wm2;
                i3 = i;
            }
            yw += w;
            y++;
            p2 = h4;
            divsum2 = divsum3;
            bitmap = bitmap3;
        }
        Bitmap bitmap4 = bitmap;
        int stackstart2 = p2;
        int x2 = 0;
        int rbs2 = y;
        while (x2 < w) {
            int goutsum3 = 0;
            int routsum3 = 0;
            int ginsum3 = 0;
            int rinsum3 = 0;
            int yp2 = (-p) * w;
            int i8 = -p;
            int i9 = 0;
            int gsum3 = 0;
            int boutsum3 = 0;
            int yp3 = yp2;
            int binsum3 = 0;
            int binsum4 = 0;
            while (i8 <= p) {
                int y2 = rbs2;
                int yi2 = Math.max(0, yp3) + x2;
                int[] sir4 = stack[i8 + p];
                sir4[0] = r[yi2];
                sir4[1] = g[yi2];
                sir4[2] = b[yi2];
                int rbs3 = r1 - Math.abs(i8);
                gsum3 += r[yi2] * rbs3;
                i9 += g[yi2] * rbs3;
                boutsum3 += b[yi2] * rbs3;
                if (i8 > 0) {
                    rinsum3 += sir4[0];
                    ginsum3 += sir4[1];
                    binsum3 += sir4[2];
                } else {
                    routsum3 += sir4[0];
                    goutsum3 += sir4[1];
                    binsum4 += sir4[2];
                }
                if (i8 < hm) {
                    yp3 += w;
                }
                i8++;
                rbs2 = y2;
            }
            int y3 = x2;
            int stackpointer2 = radius;
            int yi3 = y3;
            rbs2 = 0;
            while (true) {
                int yp4 = yp3;
                yp = stackstart2;
                if (rbs2 < yp) {
                    pix[yi3] = (pix[yi3] & (-16777216)) | (dv[gsum3] << 16) | (dv[i9] << 8) | dv[boutsum3];
                    int rsum3 = gsum3 - routsum3;
                    int gsum4 = i9 - goutsum3;
                    int bsum2 = boutsum3 - binsum4;
                    int stackstart3 = (stackpointer2 - p) + div;
                    int[] sir5 = stack[stackstart3 % div];
                    int routsum4 = routsum3 - sir5[0];
                    int goutsum4 = goutsum3 - sir5[1];
                    int boutsum4 = binsum4 - sir5[2];
                    if (x2 == 0) {
                        vmin[rbs2] = Math.min(rbs2 + r1, hm) * w;
                    }
                    int p5 = vmin[rbs2] + x2;
                    sir5[0] = r[p5];
                    sir5[1] = g[p5];
                    sir5[2] = b[p5];
                    int rinsum4 = rinsum3 + sir5[0];
                    int ginsum4 = ginsum3 + sir5[1];
                    int binsum5 = binsum3 + sir5[2];
                    gsum3 = rsum3 + rinsum4;
                    i9 = gsum4 + ginsum4;
                    boutsum3 = bsum2 + binsum5;
                    stackpointer2 = (stackpointer2 + 1) % div;
                    int[] sir6 = stack[stackpointer2];
                    routsum3 = routsum4 + sir6[0];
                    goutsum3 = goutsum4 + sir6[1];
                    binsum4 = boutsum4 + sir6[2];
                    rinsum3 = rinsum4 - sir6[0];
                    ginsum3 = ginsum4 - sir6[1];
                    binsum3 = binsum5 - sir6[2];
                    yi3 += w;
                    rbs2++;
                    p = radius;
                    stackstart2 = yp;
                    yp3 = yp4;
                }
            }
            x2++;
            p = radius;
            stackstart2 = yp;
        }
        int h5 = stackstart2;
        bitmap4.setPixels(pix, 0, w, 0, 0, w, h5);
        return bitmap4;
    }
}
