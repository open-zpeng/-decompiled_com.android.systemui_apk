package com.xiaopeng.systemui.helper;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import androidx.slice.core.SliceHints;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.List;
/* loaded from: classes24.dex */
public class PackageHelper {
    public static final String ACTION_ACTIVITY_CHANGED = "com.xiaopeng.intent.action.ACTIVITY_CHANGED";
    public static final String ACTION_ASR_CONFIRMED = "com.xiaopeng.intent.action.ASR_CONFIRMED";
    public static final String ACTION_DIALOG_CHANGED = "com.xiaopeng.intent.action.XUI_DIALOG_CHANGED";
    public static final String ACTION_SWITCH_DAYNIGHT = "com.xiaopeng.intent.action.SWITCH_DAYNIGHT";
    public static final String CATEGORY_XPENG = "android.intent.category.XPENG";
    public static final String EXTRA_ASR_CONTENT = "com.xiaopeng.intent.extra.ASR_CONTENT";
    public static final String EXTRA_COMPONENT = "android.intent.extra.COMPONENT";
    public static final String EXTRA_FULLSCREEN_ACTIVITY = "android.intent.extra.FULLSCREEN";
    public static final String EXTRA_TOPPING_DIALOG = "android.intent.extra.topping.DIALOG_INFO";
    public static final String EXTRA_WINDOW_LEVEL = "android.intent.extra.WINDOW_LEVEL";
    private static final String INTENT_CLOSE_AUTH_MODE_DIALOG = "com.xiaopeng.intent.action.CLOSE_QUIT_AUTH_MODE_DIALOG";
    private static final String INTENT_OPEN_AUTH_MODE_DIALOG = "com.xiaopeng.intent.action.OPEN_QUIT_AUTH_MODE_DIALOG";
    private static final String INTENT_OPEN_QUIT_REPAIR_MODE_DIALOG = "com.xiaopeng.action.CHECKMODE_DIALOG";
    public static final String PACKAGE_ACCOUNT_CENTER = "com.xiaopeng.caraccount";
    public static final String PACKAGE_INSTRUMENT = "com.xiaopeng.instrument";
    public static final String PACKAGE_NAME = "com.android.systemui";
    public static final String PACKAGE_OOBE = "com.xiaopeng.oobe";
    public static final int TAB_INDEX_AMAZON_MUSIC = 3;
    public static final int TAB_INDEX_BLUETOOTH_MUSIC = 2;
    public static final int TAB_INDEX_DAB = 1;
    public static final int TAB_INDEX_SPOTIFY = 0;
    public static final int TAB_INDEX_TIDAL = 4;
    public static final int TAB_INDEX_TUNEIN = 5;
    private static final String TAG = "PackageHelper";
    private static PackageHelper sInstance;
    private ArrayMap<String, Intent> mAppLaunchIntentMap = new ArrayMap<>();
    private Context mContext = ContextUtils.getContext();
    public String mPkgMap = this.mContext.getString(R.string.pkg_map);

    public static PackageHelper getInstance() {
        if (sInstance == null) {
            sInstance = new PackageHelper();
        }
        return sInstance;
    }

    private PackageHelper() {
    }

    public String getMapPkgName() {
        return this.mPkgMap;
    }

    public static void startApplicationWithPackageName(Context context, String pkgName) {
        Logger.d(TAG, "startApplicationWithPackageName : " + pkgName);
        Intent intent = getInstance().getLaunchIntent(context, pkgName);
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            try {
                intent.setScreenId(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(VuiConstants.SCENE_PACKAGE_NAME, "com.android.systemui");
        startActivity(context, intent, (Bundle) null);
    }

    public void uninstall(String packageName, IPackageDeleteObserver.Stub observer) {
        try {
            PackageManager packageManager = SystemUIApplication.getContext().getPackageManager();
            packageManager.deletePackage(packageName, observer, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent getLaunchIntent(Context context, String pkgName) {
        Intent launchIntent = this.mAppLaunchIntentMap.get(pkgName);
        if (launchIntent == null) {
            Intent launchIntent2 = makeLaunchIntent(context, pkgName);
            this.mAppLaunchIntentMap.put(pkgName, launchIntent2);
            return launchIntent2;
        }
        return launchIntent;
    }

    public static Intent makeLaunchIntent(ComponentName cn) {
        return new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(cn).setFlags(270532608);
    }

    public static int makeIntentFlag() {
        return 270532608;
    }

    public static boolean startActivity(Context context, Intent intent, Bundle bundle) {
        if (intent == null) {
            return false;
        }
        try {
            int flag = makeIntentFlag();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.addFlags(flag);
            context.startActivityAsUser(intent, UserHandle.CURRENT);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void startActivity(Context context, int displayId, String action, String packageName, String className, Bundle bundle) {
        try {
            Intent intent = new Intent();
            intent.setScreenId(displayId);
            int flag = makeIntentFlag();
            if (!TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName);
            }
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className)) {
                intent.setClassName(packageName, className);
            }
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.addFlags(flag);
            context.startActivityAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
        }
    }

    public static void startActivity(Context context, String action, String packageName, String className, Bundle bundle) {
        startActivity(context, 0, action, packageName, className, bundle);
    }

    public static void sendBroadcast(Context context, String action, String packageName, String className, Bundle bundle) {
        try {
            Intent intent = new Intent();
            int flag = makeIntentFlag();
            if (!TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName);
            }
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className)) {
                intent.setClassName(packageName, className);
            }
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.addFlags(flag);
            context.sendBroadcast(intent);
        } catch (Exception e) {
        }
    }

    public static boolean startActivityByAction(Context context, String action, Bundle bundle) {
        try {
            Intent intent = new Intent(action);
            int flag = makeIntentFlag();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.addFlags(flag);
            intent.addCategory(CATEGORY_XPENG);
            startActivity(context, intent, bundle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getApplicationInfo(pkgName, 1);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean startActivityInSecondaryWindow(Context context, String pkgName) {
        Logger.d(TAG, "startActivityInSecondaryWindow : " + pkgName);
        Intent intent = getInstance().getLaunchIntent(context, pkgName);
        return startActivityInSecondaryWindow(context, intent);
    }

    public static boolean startActivityInSecondaryWindow(Context context, int componentRes) {
        String component = context.getString(componentRes);
        if (!TextUtils.isEmpty(component)) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            return startActivityInSecondaryWindow(context, intent);
        }
        return false;
    }

    public static void startSecondaryApp(Context context, String pkgName) {
        Logger.d(TAG, "startSecondaryApp : " + pkgName);
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse("content://appstore/open_app_stage");
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        intent.setScreenId(1);
        Bundle openAppBundle = new Bundle();
        openAppBundle.putParcelable("intent", intent);
        cr.call(uri, "openApp", pkgName, openAppBundle);
    }

    public static void startSeatMassage(Context context, int displayId) {
        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.carcontrol.intent.action.SHOW_SEAT_COMFORT");
        intent.putExtra("seat_comfort_type", 0);
        intent.putExtra("seat_comfort_seat", displayId);
        if (displayId == 0) {
            startActivity(context, intent, (Bundle) null);
        } else if (displayId == 1) {
            startActivityInSecondaryWindow(context, intent);
        }
    }

    public static void startChildSafetyMode(Context context) {
        Logger.d(TAG, "startChildSafetyMode");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(VuiConstants.CARCONTROL, "com.xiaopeng.carcontrol.CarControlService"));
        intent.setAction("com.xiaopeng.carcontrol.intent.action.ACTION_SHOW_CHILD_MODE_SETTING_DIALOG");
        context.startService(intent);
    }

    public static boolean startActivityInSecondaryWindow(Context context, Intent intent) {
        intent.setScreenId(1);
        return startActivity(context, intent, (Bundle) null);
    }

    public static boolean startActivity(Context context, int componentRes, Bundle bundle) {
        String component = context.getString(componentRes);
        if (!TextUtils.isEmpty(component)) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            return startActivity(context, intent, bundle);
        }
        return false;
    }

    public static boolean startActivityInPrimaryWindow(Context context, int componentRes, Bundle bundle) {
        String component = context.getString(componentRes);
        if (TextUtils.isEmpty(component)) {
            return false;
        }
        ComponentName componentName = ComponentName.unflattenFromString(component);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setScreenId(0);
        return startActivity(context, intent, bundle);
    }

    public static boolean checkAppOpened(Context context, String component) {
        ComponentName curComponentName = getTopComponentName(context);
        boolean isAppOpend = false;
        if (curComponentName != null && curComponentName.getPackageName() != null) {
            ComponentName cn = ComponentName.unflattenFromString(component);
            String pkgName = cn.getPackageName();
            String className = cn.getClassName();
            if (ActivityController.needCheckClassName(pkgName)) {
                isAppOpend = pkgName.equals(curComponentName.getPackageName()) && className.equals(curComponentName.getClassName());
            } else {
                isAppOpend = pkgName.equals(curComponentName.getPackageName());
            }
        }
        if (isAppOpend) {
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.helper.PackageHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(4);
                }
            });
        }
        return isAppOpend;
    }

    public static boolean startActivityWithRunCheck(Context context, int componentRes, Bundle bundle) {
        String component = context.getString(componentRes);
        if (isTopComponent(context, component, false)) {
            return true;
        }
        ComponentName componentName = ComponentName.unflattenFromString(component);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
            if (taskInfo.topActivity.getPackageName().equals(componentName.getPackageName())) {
                activityManager.moveTaskToFront(taskInfo.id, 0);
                return true;
            }
        }
        return startActivity(context, componentRes, bundle);
    }

    public static boolean startActivitySafely(Context context, int displayId, int componentRes, Bundle bundle) {
        String component = context.getString(componentRes);
        if (!TextUtils.isEmpty(component)) {
            if (checkToGoHome(context, component)) {
                return true;
            }
            ComponentName targetComponent = ComponentName.unflattenFromString(component);
            Intent intent = new Intent();
            intent.putExtra("displayId", displayId);
            intent.setScreenId(displayId);
            intent.setComponent(targetComponent);
            return startActivity(context, intent, bundle);
        }
        return false;
    }

    public static boolean startActivitySafely(Context context, int componentRes, Bundle bundle) {
        return startActivitySafely(context, 0, componentRes, bundle);
    }

    public static boolean startActivitySafely(Context context, int componentRes, Intent intent, Bundle bundle, boolean checkClass) {
        String component = context.getString(componentRes);
        if (!TextUtils.isEmpty(component)) {
            if (checkToGoHome(context, component, checkClass)) {
                return true;
            }
            return startActivity(context, intent, bundle);
        }
        return false;
    }

    private static boolean checkToGoHome(Context context, String componentName) {
        return checkToGoHome(context, componentName, false);
    }

    private static boolean isTopComponent(Context context, String componentName, boolean checkClass) {
        ComponentName currentComponent = getTopComponentName(context);
        boolean isTopComponent = false;
        if (!TextUtils.isEmpty(componentName)) {
            ComponentName targetComponent = ComponentName.unflattenFromString(componentName);
            if (currentComponent != null && targetComponent != null) {
                boolean isTopComponent2 = targetComponent.getPackageName().equals(currentComponent.getPackageName());
                if (checkClass) {
                    if (isTopComponent2 && targetComponent.getClassName().equals(currentComponent.getClassName())) {
                        isTopComponent = true;
                    }
                    return isTopComponent;
                }
                return isTopComponent2;
            }
        }
        return false;
    }

    private static boolean checkToGoHome(Context context, String componentName, boolean checkClass) {
        if (isTopComponent(context, componentName, checkClass)) {
            startHomePackage(context);
            return true;
        }
        return false;
    }

    public static boolean startService(Context context, int componentRes, Bundle bundle) {
        String component = context.getString(componentRes);
        if (!TextUtils.isEmpty(component)) {
            ComponentName targetComponent = ComponentName.unflattenFromString(component);
            Intent intent = new Intent();
            if (targetComponent != null) {
                intent.setClassName(targetComponent.getPackageName(), targetComponent.getClassName());
            }
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            try {
                context.startServiceAsUser(intent, UserHandle.CURRENT);
                return true;
            } catch (Exception e) {
                Logger.d(TAG, "startService e=" + e);
                return false;
            }
        }
        return false;
    }

    public static boolean startService(Context context, int actionRes, String packageName, Bundle bundle) {
        String action = context.getString(actionRes);
        if (!TextUtils.isEmpty(action)) {
            Intent intent = new Intent(action);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName);
            }
            try {
                context.startServiceAsUser(intent, UserHandle.CURRENT);
                return true;
            } catch (Exception e) {
                Logger.d(TAG, "startService e=" + e);
                return false;
            }
        }
        return false;
    }

    public static void startAppPackages(Context context) {
        startAppPackages(context, 0);
    }

    public static void startAppPackages(Context context, int displayId) {
        startAppPackages(context, displayId, true);
    }

    public static void startAppPackages(Context context, boolean checkGoHome) {
        startAppPackages(context, 0, checkGoHome);
    }

    public static void startAppPackages(Context context, int displayId, boolean checkGoHome) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.intent.extra.XUI_FULLSCREEN", true);
        if (checkGoHome) {
            startActivitySafely(context, displayId, R.string.component_app, bundle);
        } else {
            startActivity(context, (int) R.string.component_app, bundle);
        }
    }

    public static void gotoHome(Context context) {
        ComponentName currentComponent = ActivityController.getCurrentComponent();
        Logger.d(TAG, "gotoHome currentComponent=" + currentComponent);
        if (currentComponent == null || TextUtils.isEmpty(currentComponent.getPackageName())) {
            return;
        }
        String currentPackageName = currentComponent.getPackageName();
        if (!isHomePackage(context, currentPackageName)) {
            startHomePackage(context);
        }
    }

    public static void startCarHvac(Context context, int displayId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.intent.extra.XUI_FULLSCREEN", true);
        bundle.putBoolean("showCarControl", true);
        startActivity(context, displayId, "com.xiaopeng.carcontrol.intent.action.SHOW_HVAC_PANEL", VuiConstants.CARCONTROL, "", bundle);
    }

    public static void startSeatHeatVent(Context context, int displayId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.intent.extra.XUI_FULLSCREEN", true);
        bundle.putBoolean("showCarControl", true);
        Log.d(TAG, "startSeatHeatVent clicked");
        sendBroadcast(context, "com.xiaopeng.carcontrol.intent.action.SHOW_SEAT_HEAT_DIALOG", VuiConstants.CARCONTROL, "", bundle);
    }

    public static void startXpMusic(Context context) {
        startXpMusic(context, 0);
    }

    public static void startXpMusic(Context context, int displayId) {
        startCarMusic(context, displayId, true, true);
    }

    public static void startCarMusic(Context context, int displayId) {
        startCarMusic(context, displayId, true, false);
    }

    public static void startCarMusic(Context context, int displayId, boolean checkGoHome, boolean validXpMusic) {
        if (checkGoHome) {
            String component = context.getString(R.string.component_music);
            if (!TextUtils.isEmpty(component) && checkToGoHome(context, component)) {
                return;
            }
        }
        MediaManager.getInstance().enterMusicAppWithDisplayId(displayId, validXpMusic ? 1 : 0);
    }

    public static void startCarCamera(Context context) {
        startCarCamera(context, true);
    }

    public static void startCarCamera(Context context, boolean checkGoHome) {
        if (checkGoHome) {
            startActivitySafely(context, R.string.component_camera, null);
        } else {
            startActivity(context, (int) R.string.component_camera, (Bundle) null);
        }
    }

    public static void startCarControl(Context context) {
        Bundle bundle = new Bundle();
        if (CarModelsManager.getFeature().isChineseVersion()) {
            bundle.putBoolean("android.intent.extra.XUI_FULLSCREEN", true);
        }
        bundle.putBoolean("showCarControl", true);
        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.carcontrol.intent.action.SHOW_CAR_CONTROL");
        startActivity(context, intent, bundle);
    }

    public static void startSuperPark(Context context) {
        startService(context, R.string.action_infoflow_autopilot, "com.xiaopeng.autopilot", null);
    }

    public static void startSettings(Context context) {
        startSettings(context, true);
    }

    public static void startSettings(Context context, boolean checkGoHome) {
        if (checkGoHome) {
            startActivitySafely(context, R.string.component_settings, null);
        } else {
            startActivity(context, (int) R.string.component_settings, (Bundle) null);
        }
    }

    public static void startMusicLogin(Context context, boolean checkGoHome) {
        if (checkGoHome) {
            startActivitySafely(context, R.string.component_music_login, null);
        } else {
            startActivityWithRunCheck(context, R.string.component_music_login, null);
        }
    }

    public static void startMusicLogin(Context context, int displayId) {
        Intent intent = getInstance().getLaunchIntent(context, context.getString(R.string.pkg_music_login));
        intent.putExtra("tab_index", 0);
        if (displayId == 0) {
            startActivity(context, intent, (Bundle) null);
        } else if (displayId == 1) {
            startActivityInSecondaryWindow(context, intent);
        }
    }

    public static void startCarMedia(Context context, int displayId, int tab_index) {
        Intent intent = new Intent();
        ComponentName componentName = ComponentName.unflattenFromString(context.getString(R.string.component_music_login));
        intent.setComponent(componentName);
        intent.putExtra("tab_index", tab_index);
        intent.putExtra("display_location", displayId);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void sendDayNightChanged(Context context) {
        sendBroadcast(context, ACTION_SWITCH_DAYNIGHT, null, null, null);
    }

    public static String getHomePackage(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        int priority = 0;
        String pkgName = null;
        List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 65536);
        for (ResolveInfo ri : resolveInfo) {
            if (!"com.android.provision".equals(ri.activityInfo.packageName) && ri.activityInfo.enabled) {
                Logger.d(TAG, "getHomePackage: pkg = " + ri.activityInfo.packageName + " priority = " + ri.priority);
                if (pkgName == null || ri.priority > priority) {
                    priority = ri.priority;
                    pkgName = ri.activityInfo.packageName;
                }
            }
        }
        Logger.d(TAG, "getHomePackage : " + pkgName);
        return pkgName;
    }

    public static boolean isHomePackage(Context context, String packageName) {
        String homePackage = getHomePackage(context);
        if (!TextUtils.isEmpty(packageName) && packageName.equals(homePackage)) {
            Logger.d(TAG, "isHomePackage : " + packageName);
            return true;
        }
        return false;
    }

    public static void startHomePackage(Context context) {
        Logger.d(TAG, "startHomePackage");
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.addFlags(270532608);
            context.startActivityAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
            Logger.d(TAG, "startHomePackage e=" + e);
        }
    }

    public static int[] getViewLocation(View view) {
        int[] location = new int[2];
        if (view != null) {
            view.getLocationOnScreen(location);
            int w = view.getWidth();
            int h = view.getHeight();
            location[0] = location[0] + (2 / w);
            location[1] = location[1] + (2 / h);
        }
        return location;
    }

    public static ComponentName getTopComponentName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
            return am.getRunningTasks(1).get(0).topActivity;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTopActivityName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
            return am.getRunningTasks(1).get(0).topActivity.getClassName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getTopPackageName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
            return am.getRunningTasks(1).get(0).topActivity.getPackageName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getClassName(Context context, int resId) {
        try {
            String component = context.getString(resId);
            ComponentName componentName = ComponentName.unflattenFromString(component);
            if (componentName != null) {
                return componentName.getClassName();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static void startBtPhone(Context context) {
        startBtPhone(context, true);
    }

    public static void startBtPhone(Context context, boolean checkGoHome) {
        Intent intent = new Intent();
        int flags = makeIntentFlag();
        intent.setAction("android.intent.action.DIAL");
        intent.addFlags(flags);
        if (checkGoHome) {
            startActivitySafely(context, R.string.component_phone, intent, null, false);
        } else {
            startActivity(context, intent, (Bundle) null);
        }
    }

    public static void showQuitRepairModeDialog(Context context) {
        Logger.d(TAG, "showQuitRepairModeDialog");
        Intent intent = new Intent();
        intent.setAction(INTENT_OPEN_QUIT_REPAIR_MODE_DIALOG);
        intent.setFlags(16777216);
        context.sendBroadcast(intent);
    }

    public static void startMap(Context context) {
        gotoHome(context);
    }

    private static Intent makeLaunchIntent(Context context, String pkgName) {
        return context.getPackageManager().getLaunchIntentForPackage(pkgName);
    }

    public static void startFm(Context context) {
        startActivity(context, (int) R.string.component_fm, (Bundle) null);
    }

    public static void startOutOfDataPage(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.action.OPEN_ACCOUNT_RN_PAGE");
        intent.putExtra("_intent_key_name", "XPCar_ReactNativeCarFlow");
        intent.putExtra("ExtraPageName", "PageMyWallet");
        startActivity(context, intent, (Bundle) null);
    }

    public static void showAuthModeDialog(Context context, String authEndTime) {
        Logger.d(TAG, "showAuthModeDialog : " + authEndTime);
        Intent intent = new Intent();
        intent.setAction(INTENT_OPEN_AUTH_MODE_DIALOG);
        intent.putExtra("AUTO_CLOSE_TIME", authEndTime);
        intent.setFlags(16777216);
        context.sendBroadcast(intent);
    }

    public static void closeAuthModeDialog(Context context) {
        Logger.d(TAG, "closeAuthModeDialog");
        Intent intent = new Intent();
        intent.setAction(INTENT_CLOSE_AUTH_MODE_DIALOG);
        intent.setFlags(16777216);
        context.sendBroadcast(intent);
    }

    public static void startMakeupSpace(Context context, int displayId) {
        Uri uri = Uri.parse("xiaopeng://com.xiaopeng.beauty.makeup/start?from=systemui");
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addFlags(268435456);
        intent.setScreenId(displayId);
        context.startActivity(intent);
    }

    public static void startScentSpace(Context context, int displayId) {
        Uri uri = Uri.parse("xiaopeng://aiot/device/detail?type=fragrance&from=systemui");
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addFlags(268435456);
        intent.setScreenId(displayId);
        context.startActivity(intent);
    }
}
