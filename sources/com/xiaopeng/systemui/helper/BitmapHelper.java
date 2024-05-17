package com.xiaopeng.systemui.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.message.helper.MusicResourcesHelper;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
/* loaded from: classes24.dex */
public class BitmapHelper {
    private static final String TAG = "BitmapHelper";

    public static Bitmap createBluetoothBitmap(Context context, int number) {
        Bitmap bitmap = getBitmap(context, R.drawable.ic_sysbar_bluetooth_connected);
        Logger.d(TAG, "createBluetoothBitmap bitmap=" + bitmap);
        if (bitmap != null) {
            if (number > 0) {
                try {
                    return mergeBitmapBadge(bitmap, createBadge(context, number));
                } catch (Exception e) {
                    Logger.d(TAG, "createBluetoothBitmap e=" + e);
                    return null;
                }
            }
            return bitmap;
        }
        return null;
    }

    public static Bitmap mergeBitmapBadge(Bitmap target, Bitmap badge) {
        if (target != null && badge != null) {
            try {
                Bitmap bitmap = target.copy(Bitmap.Config.ARGB_8888, true);
                if (bitmap != null) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    Logger.d(TAG, "mergeBitmapBadge width=" + width + " height=" + height);
                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setDither(true);
                    paint.setColor(-1);
                    canvas.drawBitmap(badge, width - badge.getWidth(), height - badge.getHeight(), paint);
                }
                return bitmap;
            } catch (Exception e) {
                Logger.d(TAG, "mergeBitmapBadge e=" + e);
                return null;
            }
        }
        return null;
    }

    public static Bitmap createBadge(Context context, int number) {
        Bitmap bitmap;
        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_badge_circle);
        if (drawable != null && (bitmap = drawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true)) != null && number > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Rect rect = new Rect(0, 0, width, height);
            Logger.d(TAG, "createBadge width=" + width + " height=" + height);
            String content = String.valueOf(number);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setTextSize(12.0f);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setFakeBoldText(true);
            paint.setColor(-16777216);
            if (number > 9) {
                canvas.drawCircle((width / 2) - 4, height / 2, 1.5f, paint);
                canvas.drawCircle(width / 2, height / 2, 1.5f, paint);
                canvas.drawCircle((width / 2) + 4, height / 2, 1.5f, paint);
            } else {
                Paint.FontMetrics metrics = paint.getFontMetrics();
                float distance = ((metrics.bottom - metrics.top) / 2.0f) - metrics.bottom;
                float baseLineY = rect.centerY() + distance;
                float baseLineX = rect.centerX();
                canvas.drawText(content, baseLineX, baseLineY, paint);
            }
            return bitmap;
        }
        return null;
    }

    public static Bitmap getBitmap(Context context, int vectorDrawableId) {
        if (Build.VERSION.SDK_INT > 21) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }
        return BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
    }

    public static Bitmap createCircleBitmap(Bitmap resource) {
        if (resource != null) {
            try {
                int width = resource.getWidth();
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                Bitmap circleBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(circleBitmap);
                canvas.drawCircle(width / 2, width / 2, width / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(resource, 0.0f, 0.0f, paint);
                return circleBitmap;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Bitmap getCropBitmap(Bitmap toTransform) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int cropWidth = (width * 7) / 10;
        int cropHeight = (height * 276) / 500;
        int cropX = (width - cropWidth) / 2;
        int cropY = (height - cropHeight) / 2;
        Bitmap cropBitmap = Bitmap.createBitmap(toTransform, cropX, cropY, cropWidth, cropHeight, (Matrix) null, false);
        return cropBitmap;
    }

    public static String getAppIconByPackage(String pkgName) {
        Bitmap bitmap = MusicResourcesHelper.getAppIconBitmap(pkgName);
        if (bitmap != null) {
            String bmpStr = ImageUtil.getBase64String(bitmap);
            Logger.d(TAG, "getAppIconByPackage bitmap with " + bitmap.getWidth() + " &height:" + bitmap.getHeight());
            return bmpStr;
        }
        return "";
    }
}
