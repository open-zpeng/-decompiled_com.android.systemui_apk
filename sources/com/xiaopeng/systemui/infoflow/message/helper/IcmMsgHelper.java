package com.xiaopeng.systemui.infoflow.message.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.NotificationList;
import com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter;
/* loaded from: classes24.dex */
public class IcmMsgHelper {
    private static final String TAG = IcmMsgHelper.class.getSimpleName();

    public static void openCard(Context context, CardEntry cardEntry) {
        String action = cardEntry.action;
        String content = cardEntry.content;
        int i = cardEntry.type;
        if (i == 1) {
            openNotificationCenter(context);
        } else if (i == 8) {
            String packageName = "";
            String activityName = "";
            String[] result = content.split("/");
            if (result.length == 1) {
                packageName = result[0];
            } else if (result.length == 2) {
                packageName = result[0];
                activityName = result[1];
            }
            openAppWithPackageName(context, packageName, activityName);
        } else if (i == 13) {
            if (!TextUtils.isEmpty(action)) {
                Intent intent = new Intent(action);
                intent.setPackage(VuiConstants.CARCONTROL);
                intent.putExtra("key_low_battery", true);
                context.sendBroadcast(intent);
            }
        } else if (i == 14) {
            if (!TextUtils.isEmpty(action)) {
                Intent intent2 = new Intent(action);
                intent2.setPackage(VuiConstants.CARCONTROL);
                intent2.putExtra("key_air_protect", true);
                context.sendBroadcast(intent2);
            }
        } else {
            openCardWithAction(context, action);
        }
    }

    public static void openAppWithPackageName(Context context, String pkgName, String activity) {
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(activity)) {
            return;
        }
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(pkgName, activity);
        intent.setComponent(componentName);
        PackageHelper.startActivity(context, intent, (Bundle) null);
    }

    public static void openCardWithAction(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        context.sendBroadcast(intent);
    }

    public static void openNotificationCenter(Context context) {
        CardEntry showEntry = NotificationList.getInstance().getShowEntry();
        if (showEntry == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(NotificationCardPresenter.MESSAGE_URI + showEntry.key));
        PackageHelper.startActivity(context, intent, (Bundle) null);
    }
}
