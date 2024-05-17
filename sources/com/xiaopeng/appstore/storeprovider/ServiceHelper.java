package com.xiaopeng.appstore.storeprovider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
/* loaded from: classes22.dex */
public class ServiceHelper {
    private static final String ACTION_DATA_SCHEMA = "xp://";
    public static final String PACKAGE_NAME = "com.xiaopeng.resourceservice";
    private static final String SCHEMA_ACTION_CANCEL = "cancel/";
    private static final String SCHEMA_ACTION_OPEN = "open/";
    private static final String SCHEMA_ACTION_PAUSE = "pause/";
    private static final String SCHEMA_ACTION_RESUME = "resume/";
    private static final String SCHEMA_ACTION_RETRY = "retry/";
    public static final String SERVICE = "com.xiaopeng.appstore.resourceservice.ResourceServiceV2";
    private static final String TAG = "ServiceHelper";

    private ServiceHelper() {
    }

    public static Intent pauseIntent(int resType, @NonNull String key, String callingPackage) {
        Intent intent = new Intent(AssembleConstants.ACTION_PAUSE);
        intent.setClassName("com.xiaopeng.resourceservice", "com.xiaopeng.appstore.resourceservice.ResourceServiceV2");
        intent.putExtra(AssembleConstants.INTENT_KEY_RES_TYPE, resType);
        intent.putExtra(AssembleConstants.INTENT_KEY_ASSEMBLE_KEY, key);
        intent.putExtra(AssembleConstants.INTENT_KEY_CALLING_PACKAGE, callingPackage);
        intent.setData(Uri.parse("xp://pause/" + key));
        return intent;
    }

    public static Intent resumeIntent(int resType, @NonNull String key, String callingPackage) {
        Intent intent = new Intent(AssembleConstants.ACTION_RESUME);
        intent.setClassName("com.xiaopeng.resourceservice", "com.xiaopeng.appstore.resourceservice.ResourceServiceV2");
        intent.putExtra(AssembleConstants.INTENT_KEY_RES_TYPE, resType);
        intent.putExtra(AssembleConstants.INTENT_KEY_ASSEMBLE_KEY, key);
        intent.putExtra(AssembleConstants.INTENT_KEY_CALLING_PACKAGE, callingPackage);
        intent.setData(Uri.parse("xp://resume/" + key));
        return intent;
    }

    public static Intent cancelIntent(int resType, @NonNull String key, String callingPackage) {
        Intent intent = new Intent(AssembleConstants.ACTION_CANCEL);
        intent.setClassName("com.xiaopeng.resourceservice", "com.xiaopeng.appstore.resourceservice.ResourceServiceV2");
        intent.putExtra(AssembleConstants.INTENT_KEY_RES_TYPE, resType);
        intent.putExtra(AssembleConstants.INTENT_KEY_ASSEMBLE_KEY, key);
        intent.putExtra(AssembleConstants.INTENT_KEY_CALLING_PACKAGE, callingPackage);
        intent.setData(Uri.parse("xp://cancel/" + key));
        return intent;
    }

    public static Intent retryIntent(int resType, @NonNull String key, String callingPackage) {
        Intent intent = new Intent(AssembleConstants.ACTION_RETRY);
        intent.setClassName("com.xiaopeng.resourceservice", "com.xiaopeng.appstore.resourceservice.ResourceServiceV2");
        intent.putExtra(AssembleConstants.INTENT_KEY_RES_TYPE, resType);
        intent.putExtra(AssembleConstants.INTENT_KEY_ASSEMBLE_KEY, key);
        intent.putExtra(AssembleConstants.INTENT_KEY_CALLING_PACKAGE, callingPackage);
        intent.setData(Uri.parse("xp://retry/" + key));
        return intent;
    }

    public static Intent successIntent(int resType, @NonNull String key, String callingPackage) {
        Intent intent = new Intent(AssembleConstants.ACTION_SUCCESS);
        intent.setClassName("com.xiaopeng.resourceservice", "com.xiaopeng.appstore.resourceservice.ResourceServiceV2");
        intent.putExtra(AssembleConstants.INTENT_KEY_RES_TYPE, resType);
        intent.putExtra(AssembleConstants.INTENT_KEY_ASSEMBLE_KEY, key);
        intent.putExtra(AssembleConstants.INTENT_KEY_CALLING_PACKAGE, callingPackage);
        intent.setData(Uri.parse("xp://open/" + key));
        return intent;
    }

    public static void startServiceToPause(@NonNull Context context, int resType, @NonNull String key) {
        String myPackage = context.getPackageName();
        Intent it = pauseIntent(resType, key, myPackage);
        context.startForegroundService(it);
    }

    public static void startServiceToResume(@NonNull Context context, int resType, @NonNull String key) {
        String myPackage = context.getPackageName();
        startServiceToResume(context, resType, key, myPackage);
    }

    public static void startServiceToResume(@NonNull Context context, int resType, @NonNull String key, String callingPackage) {
        Log.d(TAG, "startServiceToResume: resType:" + resType + ", key:" + key + ", calling:" + callingPackage);
        String myPackage = context.getPackageName();
        Intent it = resumeIntent(resType, key, myPackage);
        context.startForegroundService(it);
    }

    public static void startServiceToCancel(@NonNull Context context, int resType, @NonNull String key) {
        String myPackage = context.getPackageName();
        Intent it = cancelIntent(resType, key, myPackage);
        context.startForegroundService(it);
    }

    public static void startServiceToRetry(@NonNull Context context, int resType, @NonNull String key) {
        String myPackage = context.getPackageName();
        Intent it = retryIntent(resType, key, myPackage);
        context.startForegroundService(it);
    }

    public static void startServiceSuccess(@NonNull Context context, int resType, @NonNull String key) {
        String myPackage = context.getPackageName();
        Intent it = successIntent(resType, key, myPackage);
        context.startForegroundService(it);
    }
}
