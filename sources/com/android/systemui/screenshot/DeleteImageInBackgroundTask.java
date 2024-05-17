package com.android.systemui.screenshot;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
/* compiled from: GlobalScreenshot.java */
/* loaded from: classes21.dex */
class DeleteImageInBackgroundTask extends AsyncTask<Uri, Void, Void> {
    private Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeleteImageInBackgroundTask(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Void doInBackground(Uri... params) {
        if (params.length != 1) {
            return null;
        }
        Uri screenshotUri = params[0];
        ContentResolver resolver = this.mContext.getContentResolver();
        resolver.delete(screenshotUri, null, null);
        return null;
    }
}
