package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import com.android.internal.util.CollectionUtils;
import com.android.systemui.R;
import com.xiaopeng.app.xpDialogInfo;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import com.xiaopeng.view.SharedDisplayListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes24.dex */
public class ActivityController extends SharedDisplayListener {
    public static final int FLAG_ACTIVITY_LAUNCH_PANEL = 256;
    private static final int FLAG_MINI_PROG = 512;
    public static final String PKG_AI_ASSISTANT = "com.xiaopeng.aiassistant";
    private static final String TAG = "ActivityController";
    public static final int TOP_VIEW_TYPE_DIALOG = 4;
    public static final int TOP_VIEW_TYPE_FULLSCREEN = 3;
    public static final int TOP_VIEW_TYPE_NORMAL = 0;
    public static final int TOP_VIEW_TYPE_PANEL = 2;
    public static final int TOP_VIEW_TYPE_SUPER_PANEL = 1;
    private ComponentInfo mComponentInfo;
    private Context mContext;
    private String mPrimaryTopActivity;
    private String mSecondaryTopActivity;
    private String mTopActivity;
    private WindowManager mWindowManager;
    private static ComponentName sLastComponent = null;
    private static ComponentName sCurrentComponent = null;
    private static ActivityController sActivityController = null;
    private static boolean sPanelFocused = false;
    private static boolean sHvacPanelFocused = false;
    private static boolean sMiniProgramVisible = false;
    private static String sCurrentClassName = null;
    public static String HVAC_CLASS_NAME = "com.xiaopeng.carcontrol.view.HvacActivity";
    public static boolean sIsCarControlReady = false;
    private ArrayList<OnActivityCallback> mActivityCallbacks = new ArrayList<>();
    private List<OnTopViewTypeChangedListener> mOnTopViewTypeChangedListeners = new ArrayList();
    private int mSubtopViewType = 0;
    private int mTopViewType = 0;
    private boolean mIsLauncher = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.ActivityController.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean z = true;
            if (PackageHelper.ACTION_ACTIVITY_CHANGED.equals(action)) {
                try {
                    String component = intent.getStringExtra(PackageHelper.EXTRA_COMPONENT);
                    Logger.d(ActivityController.TAG, "onReceive component=" + component);
                    if (!TextUtils.isEmpty(component)) {
                        ComponentName componentName = ComponentName.unflattenFromString(component);
                        if (ActivityController.this.mComponentInfo == null) {
                            ActivityController.this.mComponentInfo = new ComponentInfo();
                        }
                        int flags = intent.getIntExtra("android.intent.extra.FLAGS", 0);
                        boolean miniProgVisible = intent.getBooleanExtra("android.intent.extra.mini.PROGRAM", false);
                        boolean isMiniProgram = (flags & 512) == 512 && miniProgVisible;
                        boolean isPanelVisible = (flags & 256) == 256;
                        boolean isFullScreen = intent.getBooleanExtra(PackageHelper.EXTRA_FULLSCREEN_ACTIVITY, false);
                        boolean isSuperPanel = intent.getIntExtra(PackageHelper.EXTRA_WINDOW_LEVEL, 0) > 0;
                        Logger.d(ActivityController.TAG, "onReceive flags=" + flags + " miniProgVisible = " + miniProgVisible + " isMiniProgram = " + isMiniProgram + " isPanelVisible = " + isPanelVisible + " isFullScreen = " + isFullScreen + " isSuperPanel = " + isSuperPanel + " isLauncher = " + ActivityController.this.mIsLauncher);
                        boolean unused = ActivityController.sMiniProgramVisible = miniProgVisible;
                        ActivityController.this.mComponentInfo.setName(componentName);
                        ActivityController.this.mComponentInfo.setMiniProgram(miniProgVisible);
                        ActivityController.this.mComponentInfo.setActivityChange(true);
                        ActivityController.this.onActivityChanged();
                        boolean isLauncher = PackageHelper.isHomePackage(ActivityController.this.mContext, ActivityController.this.mComponentInfo.mTopPackage);
                        StringBuilder sb = new StringBuilder();
                        sb.append("onReceive isLauncher = ");
                        sb.append(isLauncher);
                        sb.append(", mTopPackage = ");
                        sb.append(ActivityController.this.mComponentInfo.mTopPackage);
                        Logger.d(ActivityController.TAG, sb.toString());
                        ActivityController.this.handleViewTypeChange(isPanelVisible, isFullScreen, isSuperPanel, isLauncher);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (PackageHelper.ACTION_DIALOG_CHANGED.equals(action)) {
                boolean hasVisibleDialog = false;
                if (intent.hasExtra(PackageHelper.EXTRA_TOPPING_DIALOG)) {
                    xpDialogInfo dialogInfo = intent.getParcelableExtra(PackageHelper.EXTRA_TOPPING_DIALOG);
                    if (!dialogInfo.fullscreen || !dialogInfo.visible || dialogInfo.dimAmount == 0.0f) {
                        z = false;
                    }
                    hasVisibleDialog = z;
                    Logger.i(ActivityController.TAG, "onReceive DIALOG_CHANGED : dialogInfo.visible = " + dialogInfo.visible + " dialogInfo.dimAmount = " + dialogInfo.dimAmount + " dialogInfo.fullscreen = " + dialogInfo.fullscreen);
                } else {
                    Logger.i(ActivityController.TAG, "onReceive DIALOG_CHANGED hasVisibleDialog :  false mTopViewType = " + ActivityController.this.mTopViewType + " mSubtopViewType = " + ActivityController.this.mSubtopViewType + " mIsLauncher = " + ActivityController.this.mIsLauncher);
                }
                ActivityController activityController = ActivityController.this;
                int i = 4;
                if (!hasVisibleDialog) {
                    i = activityController.mTopViewType != 4 ? ActivityController.this.mTopViewType : ActivityController.this.mSubtopViewType;
                }
                activityController.handleTopViewTypeChange(i, ActivityController.this.mIsLauncher);
            }
        }
    };

    /* loaded from: classes24.dex */
    public interface OnActivityCallback {
        void onActivityChanged(ComponentInfo componentInfo);
    }

    /* loaded from: classes24.dex */
    public interface OnTopViewTypeChangedListener {
        void onTopViewTypeChanged(int i, boolean z);
    }

    public void onChanged(String packageName, int sharedId) {
    }

    public void onPositionChanged(String packageName, int event, int from, int to) {
        ComponentInfo componentInfo;
        Logger.d(TAG, "onPositionChanged : pkg = " + packageName + " event = " + event);
        if (event == 4 && (componentInfo = this.mComponentInfo) != null) {
            componentInfo.setActivityChange(false);
            onActivityChanged();
        }
    }

    public void onActivityChanged(int screenId, String property) {
        Logger.d(TAG, "onActivityChanged2 : screenId = " + screenId + " property = " + property);
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.controller.ActivityController.1
            @Override // java.lang.Runnable
            public void run() {
                if (ActivityController.this.mComponentInfo != null) {
                    ActivityController.this.mComponentInfo.setActivityChange(false);
                    ActivityController.this.onActivityChanged();
                    if (PackageHelper.isHomePackage(ActivityController.this.mContext, ActivityController.this.mComponentInfo.mTopPackage)) {
                        ActivityController.this.handleViewTypeChange(false, true, false, true);
                    }
                }
            }
        });
    }

    public void onActivityChanged() {
        this.mTopActivity = this.mWindowManager.getTopActivity(1, -1);
        this.mPrimaryTopActivity = this.mWindowManager.getTopActivity(1, 0);
        this.mSecondaryTopActivity = this.mWindowManager.getTopActivity(1, 1);
        Logger.d(TAG, "onActivityChanged mTopActivity = " + this.mTopActivity + " ,mPrimaryTopActivity = " + this.mPrimaryTopActivity + " ,mSecondaryTopActivity = " + this.mSecondaryTopActivity);
        ComponentInfo componentInfo = this.mComponentInfo;
        if (componentInfo == null) {
            Logger.d(TAG, "mComponentInfo is null");
            return;
        }
        componentInfo.setTopPackage(getPackageFromActivity(this.mTopActivity));
        this.mComponentInfo.setPrimaryTopPackage(getPackageFromActivity(this.mPrimaryTopActivity));
        this.mComponentInfo.setSecondaryTopPackage(getPackageFromActivity(this.mSecondaryTopActivity));
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.controller.ActivityController.2
            @Override // java.lang.Runnable
            public void run() {
                ActivityController activityController = ActivityController.this;
                activityController.onActivityChanged(activityController.mComponentInfo);
            }
        });
    }

    public static String getPackageFromActivity(String activity) {
        ComponentName cn;
        if (activity != null && (cn = ComponentName.unflattenFromString(activity)) != null) {
            return cn.getPackageName();
        }
        return null;
    }

    /* loaded from: classes24.dex */
    public static class ComponentInfo {
        private boolean mIsActivityChange = true;
        private boolean mIsMiniProgram;
        private ComponentName mName;
        private String mPrimaryTopPackage;
        private String mSecondaryTopPackage;
        private String mTopPackage;

        public String toString() {
            return "ComponentInfo{mName=" + this.mName + ", mIsMiniProgram=" + this.mIsMiniProgram + ", mTopPackage='" + this.mTopPackage + "', mPrimaryTopPackage='" + this.mPrimaryTopPackage + "', mSecondaryTopPackage='" + this.mSecondaryTopPackage + "', mIsActivityChange=" + this.mIsActivityChange + '}';
        }

        public ComponentName getName() {
            return this.mName;
        }

        public void setName(ComponentName name) {
            this.mName = name;
        }

        public boolean isMiniProgram() {
            return this.mIsMiniProgram;
        }

        public void setMiniProgram(boolean miniProgram) {
            this.mIsMiniProgram = miniProgram;
        }

        public String getTopPackage() {
            return this.mTopPackage;
        }

        public void setTopPackage(String topPackage) {
            this.mTopPackage = topPackage;
        }

        public String getSecondaryTopPackage() {
            return this.mSecondaryTopPackage;
        }

        public void setSecondaryTopPackage(String secondaryTopPackage) {
            this.mSecondaryTopPackage = secondaryTopPackage;
        }

        public String getPrimaryTopPackage() {
            return this.mPrimaryTopPackage;
        }

        public void setPrimaryTopPackage(String primaryTopPackage) {
            this.mPrimaryTopPackage = primaryTopPackage;
        }

        public boolean isActivityChange() {
            return this.mIsActivityChange;
        }

        public void setActivityChange(boolean activityChange) {
            this.mIsActivityChange = activityChange;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleViewTypeChange(boolean isPanelVisible, boolean isFullScreen, boolean isSuperPanel, boolean isLauncher) {
        Logger.d(TAG, "handleViewTypeChange2 isPanelVisible = " + isPanelVisible + "，isFullScreen：" + isFullScreen + ",isSuperPanel：" + isSuperPanel + ",isLauncher:" + isLauncher + ",mTopViewType:" + this.mTopViewType);
        int topViewType = 0;
        if (isSuperPanel) {
            topViewType = 1;
        } else if (isPanelVisible) {
            topViewType = 2;
        } else if (isFullScreen) {
            topViewType = 3;
        }
        if (this.mTopViewType != 4) {
            handleTopViewTypeChange(topViewType, isLauncher);
            return;
        }
        this.mSubtopViewType = topViewType;
        Logger.d(TAG, "handleViewTypeChange2   topViewType = " + topViewType + "  mTopViewType = " + this.mTopViewType + "  mSubtopViewType = " + this.mSubtopViewType);
    }

    public int getTopViewType() {
        return this.mTopViewType;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTopViewTypeChange(int topViewType, boolean isLauncher) {
        Logger.d(TAG, "handleTopViewTypeChange : topViewType = " + topViewType + ", isLauncher = " + isLauncher + ", mTopViewType = " + this.mTopViewType + ",mIsLauncher = " + this.mIsLauncher);
        if (this.mTopViewType != topViewType || this.mIsLauncher != isLauncher) {
            int i = this.mTopViewType;
            if (i != 4) {
                this.mSubtopViewType = i;
            }
            this.mTopViewType = topViewType;
            this.mIsLauncher = isLauncher;
            onTopViewTypeChanged(topViewType, isLauncher);
        }
    }

    private void onTopViewTypeChanged(int topViewType, boolean isLauncher) {
        if (!CollectionUtils.isEmpty(this.mOnTopViewTypeChangedListeners)) {
            for (OnTopViewTypeChangedListener callback : this.mOnTopViewTypeChangedListeners) {
                callback.onTopViewTypeChanged(topViewType, isLauncher);
            }
        }
    }

    public ActivityController(Context context) {
        init(context);
    }

    public static ActivityController getInstance(Context context) {
        if (sActivityController == null) {
            synchronized (ActivityController.class) {
                if (sActivityController == null) {
                    sActivityController = new ActivityController(context);
                }
            }
        }
        return sActivityController;
    }

    private void init(Context context) {
        this.mContext = context;
        registerReceiver();
        this.mWindowManager = StatusBarGlobal.getInstance(context).getWindowManager();
        this.mWindowManager.registerSharedListener(this);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PackageHelper.ACTION_ACTIVITY_CHANGED);
        filter.addAction(PackageHelper.ACTION_DIALOG_CHANGED);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    public void onActivityChanged(ComponentInfo ci) {
        if (ci == null) {
            return;
        }
        String hvacName = PackageHelper.getClassName(this.mContext, R.string.component_hvacpanel);
        ComponentName componentName = sCurrentComponent;
        sLastComponent = componentName != null ? componentName.clone() : null;
        ComponentName cn = ci.getName();
        sCurrentComponent = cn != null ? cn.clone() : null;
        sHvacPanelFocused = cn != null ? Objects.equals(hvacName, cn.getClassName()) : false;
        ArrayList<OnActivityCallback> arrayList = this.mActivityCallbacks;
        if (arrayList != null && !arrayList.isEmpty()) {
            Iterator<OnActivityCallback> it = this.mActivityCallbacks.iterator();
            while (it.hasNext()) {
                OnActivityCallback callback = it.next();
                callback.onActivityChanged(ci);
            }
        }
    }

    public void addActivityCallback(OnActivityCallback callback) {
        ArrayList<OnActivityCallback> arrayList = this.mActivityCallbacks;
        if (arrayList != null) {
            arrayList.add(callback);
        }
    }

    public void removeActivityCallback(OnActivityCallback callback) {
        ArrayList<OnActivityCallback> arrayList = this.mActivityCallbacks;
        if (arrayList != null && arrayList.contains(callback)) {
            this.mActivityCallbacks.remove(callback);
        }
    }

    public void addPanelVisibleChangeListener(OnTopViewTypeChangedListener listener) {
        if (listener != null && !this.mOnTopViewTypeChangedListeners.contains(listener)) {
            this.mOnTopViewTypeChangedListeners.add(listener);
        }
    }

    public static ComponentName getLastComponent() {
        return sLastComponent;
    }

    public static ComponentName getCurrentComponent() {
        return sCurrentComponent;
    }

    public static boolean isHvacPanelFocused() {
        return sHvacPanelFocused;
    }

    public static boolean isMiniProgramVisible() {
        return sMiniProgramVisible;
    }

    public static void setCarControlReady(boolean isReady) {
        sIsCarControlReady = isReady;
    }

    public static void onNavigationItemChanged(String currentPackageName, String currentClassName, List<View> itemViews, boolean isCarControlReady) {
        boolean isSelected;
        Logger.d(TAG, "onNavigationItemChanged sIsCarControlReady = " + isCarControlReady);
        if (itemViews != null && !TextUtils.isEmpty(currentPackageName)) {
            for (View view : itemViews) {
                if (view != null && view.getTag() != null) {
                    ComponentName component = ComponentName.unflattenFromString(view.getTag().toString());
                    String viewPackageName = component != null ? component.getPackageName() : "";
                    String viewClassName = component != null ? component.getClassName() : "";
                    if (!TextUtils.isEmpty(viewPackageName) && !TextUtils.isEmpty(viewClassName)) {
                        boolean isCarControl = isCarControlPackage(viewPackageName);
                        boolean isCarControlPreloadSupport = CarModelsManager.getFeature().isCarControlPreloadSupport();
                        if (needCheckClassName(viewPackageName)) {
                            isSelected = viewClassName.equals(currentClassName);
                        } else {
                            isSelected = viewPackageName.equals(currentPackageName);
                        }
                        if (isSelected && isCarControlPreloadSupport && isCarControl && !isCarControlReady) {
                            isSelected = false;
                        }
                        view.setSelected(isSelected);
                        Logger.d(TAG, "onNavigationItemChanged package=" + currentPackageName + " isSelected=" + isSelected + " viewPackageName=" + viewPackageName + " viewClassName=" + viewClassName);
                    }
                }
            }
        }
    }

    public static boolean needCheckClassName(String viewPackageName) {
        return isCarControlPackage(viewPackageName);
    }

    private static boolean isSettingsPackage(String viewPackageName) {
        Context context = ContextUtils.getContext();
        ComponentName component = ComponentName.unflattenFromString(context.getString(R.string.component_settings));
        String pkgName = component.getPackageName();
        if (viewPackageName.equals(pkgName)) {
            return true;
        }
        return false;
    }

    private static boolean isCarControlPackage(String viewPackageName) {
        Context context = ContextUtils.getContext();
        ComponentName component = ComponentName.unflattenFromString(context.getString(R.string.component_carcontrol));
        String pkgName = component.getPackageName();
        if (viewPackageName.equals(pkgName)) {
            return true;
        }
        return false;
    }
}
