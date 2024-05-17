package com.xiaopeng.systemui.infoflow.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.xiaopeng.systemui.helper.BitmapHelper;
/* loaded from: classes24.dex */
public class RenderScriptHelper {
    private static final String TAG = "RenderScriptHelper";
    private static RenderScriptHelper mInstance;
    private RenderScript mRenderScript;

    public static RenderScriptHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RenderScriptHelper.class) {
                if (mInstance == null) {
                    mInstance = new RenderScriptHelper(context);
                }
            }
        }
        return mInstance;
    }

    private RenderScriptHelper(Context context) {
        this.mRenderScript = RenderScript.create(context);
    }

    public Bitmap blur(Bitmap bitmap, int radius) {
        Bitmap cropBitmap = RGB565toARGB888(BitmapHelper.getCropBitmap(bitmap));
        Allocation input = Allocation.createFromBitmap(this.mRenderScript, cropBitmap);
        Allocation output = Allocation.createTyped(this.mRenderScript, input.getType());
        RenderScript renderScript = this.mRenderScript;
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        try {
            scriptIntrinsicBlur.setRadius(radius);
            scriptIntrinsicBlur.setInput(input);
            scriptIntrinsicBlur.forEach(output);
            output.copyTo(cropBitmap);
            input.destroy();
            output.destroy();
            scriptIntrinsicBlur.destroy();
            return cropBitmap;
        } catch (Throwable th) {
            input.destroy();
            if (output != null) {
                output.destroy();
            }
            if (scriptIntrinsicBlur != null) {
                scriptIntrinsicBlur.destroy();
            }
            throw th;
        }
    }

    private Bitmap RGB565toARGB888(Bitmap img) {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
