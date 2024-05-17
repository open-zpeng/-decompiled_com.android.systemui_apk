package com.xiaopeng.appstore.storeprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class ResourceProviderContract {
    public static final String ASSEMBLE_PATH = "assemble";
    public static final String AUTHORITY = "resource_service";
    public static final String COLUMN_KEY = "key";
    public static final int INDEX_ASSEMBLE_CALLING = 2;
    public static final int INDEX_ASSEMBLE_ICON_URL = 4;
    public static final int INDEX_ASSEMBLE_KEY = 0;
    public static final int INDEX_ASSEMBLE_NAME = 3;
    public static final int INDEX_ASSEMBLE_PROGRESS = 6;
    public static final int INDEX_ASSEMBLE_STATE = 5;
    public static final int INDEX_ASSEMBLE_TYPE = 1;
    public static final int LOCAL_STATE_INDEX_KEY = 1;
    public static final int LOCAL_STATE_INDEX_STATE = 2;
    public static final int LOCAL_STATE_INDEX_TYPE = 0;
    public static final String LOCAL_STATE_URI_PATH_STATES = "local_states";
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_NEW_INSTALLED = 1001;
    private static final String TAG = "ResourceProviderContrac";
    private static final String URI_PATH_ROOT = "content://resource_service/";
    public static final Uri ASSEMBLE_URI = Uri.parse("content://resource_service/assemble");
    public static final Uri LOCAL_STATE_URI = Uri.parse("content://resource_service/local_states");

    public static void clearState(@NonNull Context context, int type, @NonNull String key) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues(2);
        values.put("key", key);
        Uri uri = ContentUris.withAppendedId(LOCAL_STATE_URI, type);
        try {
            resolver.update(uri, values, null, null);
        } catch (Exception ex) {
            Log.w(TAG, "clearState: update ex:" + ex);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0078  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean queryIsAssembling(@androidx.annotation.NonNull android.content.Context r8, @androidx.annotation.NonNull java.lang.String r9, int r10) {
        /*
            android.content.ContentResolver r0 = r8.getContentResolver()
            android.net.Uri r1 = com.xiaopeng.appstore.storeprovider.ResourceProviderContract.ASSEMBLE_URI
            long r2 = (long) r10
            android.net.Uri r1 = android.content.ContentUris.withAppendedId(r1, r2)
            r6 = 1
            java.lang.String[] r4 = new java.lang.String[r6]
            r7 = 0
            r4[r7] = r9
            r2 = 0
            java.lang.String r3 = "key LIKE ?"
            r5 = 0
            android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5)
            java.lang.String r1 = "ResourceProviderContrac"
            if (r0 == 0) goto L62
            int r2 = r0.getCount()     // Catch: java.lang.Throwable -> L60
            if (r2 == 0) goto L62
            boolean r2 = r0.moveToFirst()     // Catch: java.lang.Throwable -> L60
            if (r2 == 0) goto L76
            r2 = 5
            int r2 = r0.getInt(r2)     // Catch: java.lang.Throwable -> L60
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L60
            r3.<init>()     // Catch: java.lang.Throwable -> L60
            java.lang.String r4 = "queryIsAssembling finish: key="
            r3.append(r4)     // Catch: java.lang.Throwable -> L60
            r3.append(r9)     // Catch: java.lang.Throwable -> L60
            java.lang.String r4 = " state="
            r3.append(r4)     // Catch: java.lang.Throwable -> L60
            java.lang.String r4 = com.xiaopeng.appstore.storeprovider.AssembleInfo.stateToStr(r2)     // Catch: java.lang.Throwable -> L60
            r3.append(r4)     // Catch: java.lang.Throwable -> L60
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L60
            android.util.Log.d(r1, r3)     // Catch: java.lang.Throwable -> L60
            boolean r1 = com.xiaopeng.appstore.storeprovider.AssembleInfo.isRunning(r2)     // Catch: java.lang.Throwable -> L60
            if (r1 != 0) goto L5c
            boolean r1 = com.xiaopeng.appstore.storeprovider.AssembleInfo.isPreparing(r2)     // Catch: java.lang.Throwable -> L60
            if (r1 == 0) goto L5b
            goto L5c
        L5b:
            r6 = r7
        L5c:
            r0.close()
            return r6
        L60:
            r1 = move-exception
            goto L7c
        L62:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L60
            r2.<init>()     // Catch: java.lang.Throwable -> L60
            java.lang.String r3 = "queryIsAssembling finish: no data relative to "
            r2.append(r3)     // Catch: java.lang.Throwable -> L60
            r2.append(r9)     // Catch: java.lang.Throwable -> L60
            java.lang.String r2 = r2.toString()     // Catch: java.lang.Throwable -> L60
            android.util.Log.d(r1, r2)     // Catch: java.lang.Throwable -> L60
        L76:
            if (r0 == 0) goto L7b
            r0.close()
        L7b:
            return r7
        L7c:
            throw r1     // Catch: java.lang.Throwable -> L7d
        L7d:
            r2 = move-exception
            if (r0 == 0) goto L88
            r0.close()     // Catch: java.lang.Throwable -> L84
            goto L88
        L84:
            r3 = move-exception
            r1.addSuppressed(r3)
        L88:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.appstore.storeprovider.ResourceProviderContract.queryIsAssembling(android.content.Context, java.lang.String, int):boolean");
    }
}
