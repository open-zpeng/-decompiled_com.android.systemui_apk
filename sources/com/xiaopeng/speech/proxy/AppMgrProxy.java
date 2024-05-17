package com.xiaopeng.speech.proxy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IAppMgr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes23.dex */
public class AppMgrProxy extends IAppMgr.Stub implements ConnectManager.OnConnectCallback {
    private Context mContext;
    private IPCRunner<IAppMgr> mIpcRunner = new IPCRunner<>("AppMgrProxy");
    private List<String> mAppNames = new ArrayList();

    public AppMgrProxy(Context context) {
        this.mContext = context;
        registerApp(this.mContext.getPackageName(), getAppName(this.mContext));
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    @Override // com.xiaopeng.speech.coreapi.IAppMgr
    public void registerApp(final String packageName, final String appName) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAppMgr, Object>() { // from class: com.xiaopeng.speech.proxy.AppMgrProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAppMgr proxy) throws RemoteException {
                if (!AppMgrProxy.this.mAppNames.contains(appName)) {
                    AppMgrProxy.this.mAppNames.add(appName);
                }
                proxy.registerApp(packageName, appName);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAppMgr
    public String getPackageByAppName(final String appName) {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAppMgr, String>() { // from class: com.xiaopeng.speech.proxy.AppMgrProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAppMgr proxy) throws RemoteException {
                return proxy.getPackageByAppName(appName);
            }
        }, "");
    }

    @Override // com.xiaopeng.speech.coreapi.IAppMgr
    public String[] getAppNameByPackage(final String packageName) {
        List<String> appList = (List) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAppMgr, List<String>>() { // from class: com.xiaopeng.speech.proxy.AppMgrProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public List<String> run(IAppMgr proxy) throws RemoteException {
                return Arrays.asList(proxy.getAppNameByPackage(packageName));
            }
        }, null);
        if (appList != null) {
            return (String[]) appList.toArray(new String[appList.size()]);
        }
        return null;
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getAppMgr());
            reRegisterApp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.mIpcRunner.fetchAll();
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    private void reRegisterApp() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAppMgr, Object>() { // from class: com.xiaopeng.speech.proxy.AppMgrProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAppMgr proxy) throws RemoteException {
                LogUtils.i(this, "reRegisterApp...");
                for (String appName : AppMgrProxy.this.mAppNames) {
                    proxy.registerApp(AppMgrProxy.this.mContext.getPackageName(), appName);
                }
                return null;
            }
        });
    }

    private String getAppName(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            int stringId = applicationInfo.labelRes;
            String name = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
            return name;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
