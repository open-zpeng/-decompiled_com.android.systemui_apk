package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.MathUtils;
import androidx.palette.graphics.Palette;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.graphics.ColorUtils;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: MediaArtworkProcessor.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\u00042\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0004R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lcom/android/systemui/statusbar/MediaArtworkProcessor;", "", "()V", "mArtworkCache", "Landroid/graphics/Bitmap;", "mTmpSize", "Landroid/graphics/Point;", "clearCache", "", "processArtwork", "context", "Landroid/content/Context;", "artwork", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class MediaArtworkProcessor {
    private Bitmap mArtworkCache;
    private final Point mTmpSize = new Point();

    @Nullable
    public final Bitmap processArtwork(@NotNull Context context, @NotNull Bitmap artwork) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(artwork, "artwork");
        Bitmap bitmap = this.mArtworkCache;
        if (bitmap != null) {
            return bitmap;
        }
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation input = null;
        Allocation output = null;
        Bitmap inBitmap = null;
        try {
            try {
                context.getDisplay().getSize(this.mTmpSize);
                Rect rect = new Rect(0, 0, artwork.getWidth(), artwork.getHeight());
                MathUtils.fitRect(rect, Math.max(this.mTmpSize.x / 6, this.mTmpSize.y / 6));
                inBitmap = Bitmap.createScaledBitmap(artwork, rect.width(), rect.height(), true);
                Intrinsics.checkExpressionValueIsNotNull(inBitmap, "inBitmap");
                if (inBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                    inBitmap = inBitmap.copy(Bitmap.Config.ARGB_8888, false);
                    inBitmap.recycle();
                }
                Intrinsics.checkExpressionValueIsNotNull(inBitmap, "inBitmap");
                Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                input = Allocation.createFromBitmap(renderScript, inBitmap, Allocation.MipmapControl.MIPMAP_NONE, 2);
                output = Allocation.createFromBitmap(renderScript, outBitmap);
                blur.setRadius(25.0f);
                blur.setInput(input);
                blur.forEach(output);
                output.copyTo(outBitmap);
                Palette.Swatch swatch = MediaNotificationProcessor.findBackgroundSwatch(artwork);
                Canvas canvas = new Canvas(outBitmap);
                Intrinsics.checkExpressionValueIsNotNull(swatch, "swatch");
                canvas.drawColor(ColorUtils.setAlphaComponent(swatch.getRgb(), (int) Opcodes.GETSTATIC));
                if (input != null) {
                    input.destroy();
                }
                output.destroy();
                blur.destroy();
                inBitmap.recycle();
                return outBitmap;
            } catch (IllegalArgumentException ex) {
                Log.e("MediaArtworkProcessor", "error while processing artwork", ex);
                if (input != null) {
                    input.destroy();
                }
                if (output != null) {
                    output.destroy();
                }
                blur.destroy();
                if (inBitmap != null) {
                    inBitmap.recycle();
                }
                return null;
            }
        } catch (Throwable th) {
            if (input != null) {
                input.destroy();
            }
            if (output != null) {
                output.destroy();
            }
            blur.destroy();
            if (inBitmap != null) {
                inBitmap.recycle();
            }
            throw th;
        }
    }

    public final void clearCache() {
        Bitmap bitmap = this.mArtworkCache;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.mArtworkCache = null;
    }
}
