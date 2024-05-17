package com.xiaopeng.appstore.storeprovider.store;

import android.os.RemoteException;
import android.util.Log;
import com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceDownloadInfo;
import java.lang.ref.WeakReference;
/* loaded from: classes22.dex */
public class RMDownloadCallbackListenerToService extends IRMDownloadCallback.Stub {
    private static final String TAG = RMDownloadCallbackListenerToService.class.getSimpleName();
    private WeakReference<StoreResourceProvider> mStoreResourceProvider;

    public RMDownloadCallbackListenerToService(StoreResourceProvider storeResourceProvider) {
        this.mStoreResourceProvider = new WeakReference<>(storeResourceProvider);
    }

    @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
    }

    @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
    public void onDownloadCallback(int status, ResourceDownloadInfo resourceDownloadInfo) {
        StoreResourceProvider storeResourceProvider = this.mStoreResourceProvider.get();
        if (storeResourceProvider != null) {
            storeResourceProvider.dispatchDownloadCallback(status, resourceDownloadInfo);
        } else {
            Log.d(TAG, "storeResourceProvider is null");
        }
    }

    @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
    public void onMenuOpenCallback(String resourceId) {
        StoreResourceProvider storeResourceProvider = this.mStoreResourceProvider.get();
        if (storeResourceProvider != null) {
            storeResourceProvider.dispatchMenuOpenCallback(resourceId);
        } else {
            Log.d(TAG, "storeResourceProvider is null");
        }
    }

    @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
    public void unbindService() {
        StoreResourceProvider storeResourceProvider = this.mStoreResourceProvider.get();
        if (storeResourceProvider != null) {
            storeResourceProvider.unbindService();
        } else {
            Log.d(TAG, "storeResourceProvider is null");
        }
    }
}
