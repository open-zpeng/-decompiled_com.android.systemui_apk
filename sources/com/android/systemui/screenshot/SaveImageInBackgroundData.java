package com.android.systemui.screenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import java.util.function.Consumer;
/* compiled from: GlobalScreenshot.java */
/* loaded from: classes21.dex */
class SaveImageInBackgroundData {
    Context context;
    int errorMsgResId;
    Consumer<Uri> finisher;
    int iconSize;
    Bitmap image;
    Uri imageUri;
    int previewWidth;
    int previewheight;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearImage() {
        this.image = null;
        this.imageUri = null;
        this.iconSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearContext() {
        this.context = null;
    }
}
