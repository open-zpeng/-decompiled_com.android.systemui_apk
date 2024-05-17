package com.xiaopeng.systemui.infoflow.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes24.dex */
public class ImageUtil {

    /* loaded from: classes24.dex */
    public interface LoadedFileFinishListener {
        void onLoad(Drawable drawable);
    }

    public static String getBase64String(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String result = Base64.encodeToString(byteArray, 0);
        return result;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable != null) {
            Bitmap.Config config = drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    public static byte[] getByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return byteArray;
    }

    public static void loadDrawable(final Context context, final String dir, final String fileName, final LoadedFileFinishListener listener) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.util.ImageUtil.1
            /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:9:0x0036 -> B:30:0x0046). Please submit an issue!!! */
            @Override // java.lang.Runnable
            public void run() {
                Drawable drawable = null;
                InputStream inputStream = null;
                try {
                    try {
                        try {
                            File newFile = new File(context.getFilesDir(), dir);
                            File imagePath = new File(newFile, fileName);
                            Uri contentUri = FileProvider.getUriForFile(context, "com.xiaopeng.aiassistant.fileprovider", imagePath);
                            inputStream = context.getContentResolver().openInputStream(contentUri);
                            drawable = Drawable.createFromStream(inputStream, null);
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    if (drawable != null) {
                        Drawable finalDrawable = drawable;
                        LoadedFileFinishListener loadedFileFinishListener = listener;
                        if (loadedFileFinishListener != null) {
                            loadedFileFinishListener.onLoad(finalDrawable);
                        }
                    }
                } catch (Throwable th) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
        });
    }
}
