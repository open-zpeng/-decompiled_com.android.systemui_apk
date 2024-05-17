package com.xiaopeng.appstore.storeprovider.store;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.WorkerThread;
import com.xiaopeng.appstore.storeprovider.store.IResourceService;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceBean;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceContainerBean;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceDownloadInfo;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes22.dex */
public class StoreResourceProvider {
    public static final long INVALID_DOWNLOAD_BYTES = -1;
    public static final long INVALID_DOWNLOAD_ID = -1;
    public static final int INVALID_DOWNLOAD_STATUS = -1;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String STORE_RESOURCE_MANAGER_SERVICE = "com.xiaopeng.resourceservice";
    public static final String STORE_RESOURCE_MANAGER_SERVICE_INTERFACE_NAME = "com.xiaopeng.appstore.resourceservice.ResourceService";
    private static final String TAG = StoreResourceProvider.class.getSimpleName();
    private static volatile StoreResourceProvider sResourceManager;
    private int mConnectionState;
    private final Context mContext;
    private IResourceService mService;
    private ServiceConnection mServiceConnectionListenerClient;
    private final Object mResourceServiceReady = new Object();
    private final ServiceConnection mServiceConnectionListener = new ServiceConnection() { // from class: com.xiaopeng.appstore.storeprovider.store.StoreResourceProvider.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(StoreResourceProvider.TAG, "service connected, Service.  1");
            Log.d(StoreResourceProvider.TAG, "service connected, Service.  2");
            StoreResourceProvider.this.mService = IResourceService.Stub.asInterface(service);
            synchronized (StoreResourceProvider.this.mResourceServiceReady) {
                Log.d(StoreResourceProvider.TAG, "service connected, Service.  3");
                StoreResourceProvider.this.mConnectionState = 2;
                StoreResourceProvider.this.mResourceServiceReady.notifyAll();
                try {
                    StoreResourceProvider.this.mService.registerDownloadListener(StoreResourceProvider.this.mIRMDownloadCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                String str = StoreResourceProvider.TAG;
                Log.d(str, "service connected, Service = " + StoreResourceProvider.this.mService + " state = " + StoreResourceProvider.this.mConnectionState);
                if (StoreResourceProvider.this.mServiceConnectionListenerClient != null) {
                    StoreResourceProvider.this.mServiceConnectionListenerClient.onServiceConnected(name, service);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            String str = StoreResourceProvider.TAG;
            Log.d(str, "onServiceDisconnected, " + name);
            synchronized (StoreResourceProvider.this) {
                StoreResourceProvider.this.mService = null;
                if (StoreResourceProvider.this.mConnectionState == 0) {
                    Log.w(StoreResourceProvider.TAG, "onServiceDisconnected, already disconnected.");
                    return;
                }
                StoreResourceProvider.this.mConnectionState = 0;
                if (StoreResourceProvider.this.mServiceConnectionListenerClient != null) {
                    StoreResourceProvider.this.mServiceConnectionListenerClient.onServiceDisconnected(name);
                }
            }
        }
    };
    private IRMDownloadCallback mIRMDownloadCallback = new RMDownloadCallbackListenerToService(this);
    private Set<RMDownloadListener> mClientRMDownloadListeners = new HashSet();

    public StoreResourceProvider(Context context) {
        this.mContext = context;
    }

    boolean isResourceServiceConnected() {
        synchronized (this.mResourceServiceReady) {
            while (this.mConnectionState != 2) {
                try {
                    String str = TAG;
                    Log.d(str, "Waiting Resource service connected, mConnectionState = " + this.mConnectionState);
                    startResourceManagerService();
                    this.mResourceServiceReady.wait();
                } catch (InterruptedException e) {
                    String str2 = TAG;
                    Log.d(str2, "Waiting Resource service connected, InterruptedException " + e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        return true;
    }

    public void setServiceConnectionListenerClient(ServiceConnection mServiceConnectionListenerClient) {
        this.mServiceConnectionListenerClient = mServiceConnectionListenerClient;
    }

    private void startResourceManagerService() {
        Intent intent = new Intent();
        intent.setClassName("com.xiaopeng.resourceservice", STORE_RESOURCE_MANAGER_SERVICE_INTERFACE_NAME);
        this.mContext.bindService(intent, this.mServiceConnectionListener, 1);
    }

    @WorkerThread
    public ResourceContainerBean queryResourceData(String type) {
        try {
            isResourceServiceConnected();
            return this.mService.queryResourceData(type);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @WorkerThread
    public List<ResourceDownloadInfo> queryDownloadInfo(String[] resIds) {
        try {
            isResourceServiceConnected();
            return this.mService.queryDownloadInfo(resIds);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @WorkerThread
    public void registerDownloadListener(RMDownloadListener listener) {
        if (listener != null && !this.mClientRMDownloadListeners.contains(listener)) {
            this.mClientRMDownloadListeners.add(listener);
            return;
        }
        String str = TAG;
        Log.i(str, "registerDownloadListener, ignore this listener:" + listener);
    }

    @WorkerThread
    public void unregisterDownloadListener(RMDownloadListener listener) {
        if (listener != null) {
            this.mClientRMDownloadListeners.remove(listener);
        }
    }

    @WorkerThread
    public boolean start(ResourceBean resourceBean) {
        String str = TAG;
        Log.d(str, "start resourceBean:" + resourceBean);
        try {
            isResourceServiceConnected();
            return this.mService.start(resourceBean);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    @Deprecated
    public boolean resume(String resourceId) {
        String str = TAG;
        Log.d(str, "resume resourceId：" + resourceId);
        try {
            isResourceServiceConnected();
            return this.mService.resume(resourceId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    @Deprecated
    public boolean remove(String resourceId) {
        String str = TAG;
        Log.d(str, "remove resourceId：" + resourceId);
        try {
            isResourceServiceConnected();
            return this.mService.remove(resourceId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    @Deprecated
    public boolean pause(String resourceId) {
        String str = TAG;
        Log.d(str, "pause resourceId：" + resourceId);
        try {
            isResourceServiceConnected();
            return this.mService.pause(resourceId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public boolean resumeResDownload(String resType, String resourceId) {
        String str = TAG;
        Log.d(str, "resumeResDownload resType:" + resType + ", resourceId：" + resourceId);
        try {
            isResourceServiceConnected();
            return this.mService.resumeResDownload(resType, resourceId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public boolean cancelResDownload(String resType, String resourceId) {
        String str = TAG;
        Log.d(str, "cancelResDownload resType:" + resType + ", resourceId：" + resourceId);
        try {
            isResourceServiceConnected();
            return this.mService.cancelResDownload(resType, resourceId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public boolean pauseResDownload(String resType, String resourceId) {
        String str = TAG;
        Log.d(str, "pauseResDownload resType:" + resType + ", resourceId：" + resourceId);
        try {
            isResourceServiceConnected();
            return this.mService.pauseResDownload(resType, resourceId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public boolean resumeDownload(String url) {
        String str = TAG;
        Log.d(str, "resumeDownload url:" + url);
        try {
            isResourceServiceConnected();
            return this.mService.resumeDownload(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public boolean cancelDownload(String url) {
        String str = TAG;
        Log.d(str, "cancelDownload url:" + url);
        try {
            isResourceServiceConnected();
            return this.mService.cancelDownload(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public boolean pauseDownload(String url) {
        String str = TAG;
        Log.d(str, "pauseDownload url:" + url);
        try {
            isResourceServiceConnected();
            return this.mService.pauseDownload(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    @WorkerThread
    public long enqueue(String url, String title) {
        String str = TAG;
        Log.d(str, "enqueue url : " + url + "  title = " + title);
        try {
            isResourceServiceConnected();
            return this.mService.enqueue(url, title);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @WorkerThread
    public List<ResourceDownloadInfo> queryAllInfo() {
        Log.d(TAG, "queryAllInfo");
        List<ResourceDownloadInfo> list = null;
        try {
            isResourceServiceConnected();
            list = this.mService.queryAllInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String str = TAG;
        Log.d(str, "queryAllInfo " + list);
        return list;
    }

    @WorkerThread
    public int fetchDownloadStatus(long id) {
        String str = TAG;
        Log.d(str, "fetchDownloadStatusById id : " + id);
        try {
            isResourceServiceConnected();
            return this.mService.fetchDownloadStatusById(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @WorkerThread
    public int fetchDownloadStatus(String url) {
        String str = TAG;
        Log.d(str, "fetchDownloadStatusByUrl url : " + url);
        try {
            isResourceServiceConnected();
            return this.mService.fetchDownloadStatusByUrl(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @WorkerThread
    public int getDownloadStatus(String url) {
        String str = TAG;
        Log.d(str, "getDownloadStatusByUrl url : " + url);
        try {
            isResourceServiceConnected();
            return this.mService.getDownloadStatusByUrl(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @WorkerThread
    public int getDownloadStatus(long id) {
        String str = TAG;
        Log.d(str, "getDownloadStatusById id : " + id);
        try {
            isResourceServiceConnected();
            return this.mService.getDownloadStatusById(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @WorkerThread
    public long getTotalBytes(long id) {
        String str = TAG;
        Log.d(str, "getTotalBytesById id : " + id);
        try {
            isResourceServiceConnected();
            return this.mService.getTotalBytesById(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @WorkerThread
    public long getTotalBytes(String url) {
        String str = TAG;
        Log.d(str, "getDownloadStatusById url : " + url);
        try {
            isResourceServiceConnected();
            return this.mService.getTotalBytesByUrl(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @WorkerThread
    public long getDownloadedBytes(long id) {
        String str = TAG;
        Log.d(str, "getDownloadedBytesById id : " + id);
        try {
            isResourceServiceConnected();
            return this.mService.getDownloadedBytesById(id);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @WorkerThread
    public long getDownloadedBytes(String url) {
        String str = TAG;
        Log.d(str, "getDownloadedBytesByUrl url : " + url);
        try {
            isResourceServiceConnected();
            return this.mService.getDownloadedBytesByUrl(url);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @WorkerThread
    public void removeLocalData(long id) {
        String str = TAG;
        Log.d(str, "removeLocalData id : " + id);
        try {
            isResourceServiceConnected();
            this.mService.removeLocalDataById(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    public void removeLocalData(String url) {
        String str = TAG;
        Log.d(str, "removeLocalData url : " + url);
        try {
            isResourceServiceConnected();
            this.mService.removeLocalDataByUrl(url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    public File getLocalFile(String url) {
        String str = TAG;
        Log.d(str, "getLocalFileByUrl url : " + url);
        try {
            isResourceServiceConnected();
            String filePath = this.mService.getLocalFilePath(url);
            if (filePath != null) {
                return new File(filePath);
            }
            return null;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dispatchDownloadCallback(int status, ResourceDownloadInfo resourceDownloadInfo) {
        Set<RMDownloadListener> set = this.mClientRMDownloadListeners;
        if (set != null) {
            for (RMDownloadListener rmDownloadListener : set) {
                rmDownloadListener.onDownloadCallback(status, resourceDownloadInfo);
            }
        }
    }

    public void dispatchMenuOpenCallback(String resourceId) {
        Set<RMDownloadListener> set = this.mClientRMDownloadListeners;
        if (set != null) {
            for (RMDownloadListener rmDownloadListener : set) {
                rmDownloadListener.onMenuOpenCallback(resourceId);
            }
        }
    }

    public void unbindService() {
        Set<RMDownloadListener> set = this.mClientRMDownloadListeners;
        if (set != null) {
            for (RMDownloadListener rmDownloadListener : set) {
                rmDownloadListener.unbindService();
            }
        }
        Log.d(TAG, "unbindService, disconnect  automatically");
        disconnect();
    }

    public void connect() {
        synchronized (this) {
            if (this.mConnectionState != 0) {
                throw new IllegalStateException("already connected or connecting");
            }
            this.mConnectionState = 1;
            startResourceManagerService();
        }
    }

    public void disconnect() {
        synchronized (this) {
            if (this.mConnectionState == 0) {
                return;
            }
            try {
                this.mService.unregisterDownloadListener(this.mIRMDownloadCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.mService = null;
            this.mConnectionState = 0;
            this.mContext.unbindService(this.mServiceConnectionListener);
        }
    }

    public void releaseService() {
        if (this.mService != null) {
            isResourceServiceConnected();
            try {
                this.mService.unregisterDownloadListener(this.mIRMDownloadCallback);
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
        }
        Log.w(TAG, "releaseService, service is null.");
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this) {
            z = this.mService != null;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this) {
            z = true;
            if (this.mConnectionState != 1) {
                z = false;
            }
        }
        return z;
    }
}
