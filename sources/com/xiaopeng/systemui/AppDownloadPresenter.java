package com.xiaopeng.systemui;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.xiaopeng.appstore.storeprovider.AssembleConstants;
import com.xiaopeng.appstore.storeprovider.AssembleInfo;
import com.xiaopeng.appstore.storeprovider.AssembleRequest;
import com.xiaopeng.appstore.storeprovider.AssembleResult;
import com.xiaopeng.appstore.storeprovider.EnqueueRequest;
import com.xiaopeng.appstore.storeprovider.IAssembleClientListener;
import com.xiaopeng.appstore.storeprovider.RequestContinuation;
import com.xiaopeng.appstore.storeprovider.ResourceProviderContract;
import com.xiaopeng.appstore.storeprovider.StoreProviderManager;
import com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp;
import com.xiaopeng.systemui.IAppDownloadPresenter;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView;
import com.xiaopeng.systemui.utils.BootManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes24.dex */
public class AppDownloadPresenter implements IAppDownloadPresenter, IAssembleClientListener {
    private static final Uri ASSEMBLE_LOCAL_STATE_URI = ContentUris.withAppendedId(ResourceProviderContract.LOCAL_STATE_URI, 1000);
    private static final String TAG = "AppDownloadPresenter";
    private final CopyOnWriteArraySet<IAppDownloadPresenter.AppCallBack> mCallbacks;
    private BroadcastReceiver mReceiver;

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final AppDownloadPresenter sInstance = new AppDownloadPresenter();

        private SingleHolder() {
        }
    }

    private AppDownloadPresenter() {
        this.mReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                char c = 65535;
                int hashCode = action.hashCode();
                if (hashCode != 525384130) {
                    if (hashCode == 1544582882 && action.equals("android.intent.action.PACKAGE_ADDED")) {
                        c = 1;
                    }
                } else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                    c = 0;
                }
                if (c == 0) {
                    if (intent.getData() != null) {
                        String packageName = intent.getData().getSchemeSpecificPart();
                        Logger.d(AppDownloadPresenter.TAG, "AppDownloadPresenter : remove : " + packageName);
                        ViewFactory.getSecondaryWindowView().notifyUninstallResult(packageName, 1);
                        Iterator it = AppDownloadPresenter.this.mCallbacks.iterator();
                        while (it.hasNext()) {
                            IAppDownloadPresenter.AppCallBack callback = (IAppDownloadPresenter.AppCallBack) it.next();
                            callback.onAppUnInstalled(packageName);
                        }
                    }
                } else if (c == 1 && intent.getData() != null) {
                    String packageName2 = intent.getData().getSchemeSpecificPart();
                    Logger.d(AppDownloadPresenter.TAG, "AppDownloadPresenter : add : " + packageName2);
                    Iterator it2 = AppDownloadPresenter.this.mCallbacks.iterator();
                    while (it2.hasNext()) {
                        IAppDownloadPresenter.AppCallBack callback2 = (IAppDownloadPresenter.AppCallBack) it2.next();
                        callback2.onAppInstalled(packageName2);
                    }
                }
            }
        };
        this.mCallbacks = new CopyOnWriteArraySet<>();
        BootManager.getInstance().init();
        StoreProviderManager.get().initialize(ContextUtils.getContext());
        StoreProviderManager.get().registerListener(1000, null, this);
        BootManager.getInstance().addBootCompleteTask(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.2
            @Override // java.lang.Runnable
            public void run() {
                Logger.w(AppDownloadPresenter.TAG, "receiver LockedBootComplete!");
                StoreProviderManager.get().startObserve();
            }
        });
        if (BootManager.isBootComplete()) {
            Logger.w(TAG, "lockedBootComplete!");
            StoreProviderManager.get().startObserve();
        }
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");
        ContextUtils.getContext().registerReceiver(this.mReceiver, filter);
        try {
            ContentObserver mNewInstalledObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.xiaopeng.systemui.AppDownloadPresenter.3
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    List<String> apps = AppDownloadPresenter.this.getNewInstalledApp();
                    Iterator it = AppDownloadPresenter.this.mCallbacks.iterator();
                    while (it.hasNext()) {
                        IAppDownloadPresenter.AppCallBack callback = (IAppDownloadPresenter.AppCallBack) it.next();
                        callback.onAppNewInstalledChanged(apps);
                    }
                }
            };
            ContextUtils.getContext().getContentResolver().registerContentObserver(ASSEMBLE_LOCAL_STATE_URI, true, mNewInstalledObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConfigAppsInfo(final int type) {
        if (!BootManager.isBootComplete()) {
            return;
        }
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.4
            @Override // java.lang.Runnable
            public void run() {
                AppGroupsResp resp = StoreProviderManager.get().queryAppGroups(type);
                Logger.d(AppDownloadPresenter.TAG, "type : " + type + "  resp : " + resp);
                if (resp != null) {
                    if (type == 0) {
                        Iterator it = AppDownloadPresenter.this.mCallbacks.iterator();
                        while (it.hasNext()) {
                            IAppDownloadPresenter.AppCallBack callBack = (IAppDownloadPresenter.AppCallBack) it.next();
                            callBack.onSecondScreenAppsInfo(resp);
                        }
                        return;
                    }
                    Iterator it2 = AppDownloadPresenter.this.mCallbacks.iterator();
                    while (it2.hasNext()) {
                        IAppDownloadPresenter.AppCallBack callBack2 = (IAppDownloadPresenter.AppCallBack) it2.next();
                        callBack2.onPreInstalledAppsInfo(resp);
                    }
                }
            }
        });
    }

    public static AppDownloadPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public List<AssembleInfo> getAppDownloadInfoList() {
        if (BootManager.isBootComplete()) {
            return StoreProviderManager.get().getAssembleInfoList(1000, null);
        }
        return null;
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public List<String> getNewInstalledApp() {
        Cursor cursor;
        ContentResolver cr = ContextUtils.getContext().getContentResolver();
        try {
            cursor = cr.query(ASSEMBLE_LOCAL_STATE_URI, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
        List<String> apps = new ArrayList<>();
        while (cursor.moveToNext()) {
            int state = cursor.getInt(2);
            if (state == 1001) {
                String packageName = cursor.getString(1);
                apps.add(packageName);
            }
        }
        cursor.close();
        return apps;
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void startDownloadApp(final String pkgName, final String label) {
        if (!BootManager.isBootComplete()) {
            return;
        }
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.5
            @Override // java.lang.Runnable
            public void run() {
                EnqueueRequest assembleRequest = AppDownloadPresenter.this.createRequest(pkgName, label).request();
                final AssembleResult assembleResult = StoreProviderManager.get().assemble(assembleRequest, null);
                Logger.d(AppDownloadPresenter.TAG, "startDownloadApp : pkgName = " + pkgName);
                if (assembleResult != null) {
                    Logger.d(AppDownloadPresenter.TAG, "startDownloadApp : result = " + assembleResult.getCode());
                }
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.5.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ISecondaryWindowView view = ViewFactory.getSecondaryWindowView();
                        view.notifyDownloadResult(pkgName, assembleResult);
                    }
                });
            }
        });
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void startDownloadApp(final EnqueueRequest assembleRequest, final String pkgName) {
        if (!BootManager.isBootComplete()) {
            return;
        }
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.6
            @Override // java.lang.Runnable
            public void run() {
                final AssembleResult assembleResult = StoreProviderManager.get().assemble(assembleRequest, null);
                Logger.d(AppDownloadPresenter.TAG, "startDownloadApp : pkgName = " + pkgName);
                if (assembleResult != null) {
                    Logger.d(AppDownloadPresenter.TAG, "startDownloadApp : result = " + assembleResult.getCode());
                }
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ISecondaryWindowView view = ViewFactory.getSecondaryWindowView();
                        view.notifyDownloadResult(pkgName, assembleResult);
                    }
                });
            }
        });
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public RequestContinuation createRequest(String pkgName, String label) {
        return AssembleRequest.enqueue(1000, pkgName, label).putExtra(AssembleConstants.EXTRA_KEY_PARAMS_SHOW_DIALOG, true).putExtra(AssembleConstants.EXTRA_KEY_PARAMS_START_DOWNLOAD_SHOW_TOAST, true).putExtra(AssembleConstants.EXTRA_KEY_PARAMS_TOAST_SHARE_ID, 1).putExtra(AssembleConstants.EXTRA_KEY_PARAMS_DIALOG_SHARE_ID, 1);
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void pauseDownloadApp(final String pkgName) {
        if (!BootManager.isBootComplete()) {
            return;
        }
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.7
            @Override // java.lang.Runnable
            public void run() {
                StoreProviderManager.get().assemble(AssembleRequest.pause(1000, pkgName), null);
            }
        });
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void cancelDownloadApp(final String pkgName) {
        if (!BootManager.isBootComplete()) {
            return;
        }
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.8
            @Override // java.lang.Runnable
            public void run() {
                StoreProviderManager.get().assemble(AssembleRequest.cancel(1000, pkgName), null);
            }
        });
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void resumeDownloadApp(final String pkgName) {
        if (!BootManager.isBootComplete()) {
            return;
        }
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.9
            @Override // java.lang.Runnable
            public void run() {
                StoreProviderManager.get().assemble(AssembleRequest.resume(1000, pkgName), null);
            }
        });
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void addCallBack(IAppDownloadPresenter.AppCallBack callBack) {
        this.mCallbacks.add(callBack);
        Logger.d(TAG, "addCallBack : callBack = " + callBack);
    }

    @Override // com.xiaopeng.systemui.IAppDownloadPresenter
    public void removeCallBack(IAppDownloadPresenter.AppCallBack callBack) {
        this.mCallbacks.remove(callBack);
        Logger.d(TAG, "addCallBack : callBack = " + callBack);
    }

    @Override // com.xiaopeng.appstore.storeprovider.IAssembleClientListener
    public void onAssembleEvent(final int eventType, final AssembleInfo assembleInfo) {
        Logger.d(TAG, "onAssembleEvent : eventType = " + eventType + " assembleInfo = " + assembleInfo.toString());
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.AppDownloadPresenter.10
            @Override // java.lang.Runnable
            public void run() {
                int i = eventType;
                if (i == 1000 || i == 1001) {
                    Logger.d(AppDownloadPresenter.TAG, "onAssembleEvent : " + assembleInfo.toString());
                    ISecondaryWindowView view = ViewFactory.getSecondaryWindowView();
                    Logger.d(AppDownloadPresenter.TAG, "onAssembleEvent : " + view);
                    view.notifyDownloadInfo(assembleInfo);
                }
            }
        });
    }
}
