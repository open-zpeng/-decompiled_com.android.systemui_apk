package com.xiaopeng.appstore.storeprovider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import androidx.annotation.GuardedBy;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import com.xiaopeng.appstore.storeprovider.IAssembleListener;
import com.xiaopeng.appstore.storeprovider.IResourceServiceV2;
import com.xiaopeng.appstore.storeprovider.StoreProviderManager;
import com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
/* loaded from: classes22.dex */
public class StoreProviderManager {
    public static final String PACKAGE_NAME = "com.xiaopeng.resourceservice";
    private static final long RELEASE_DELAY = 10000;
    public static final String SERVICE = "com.xiaopeng.appstore.resourceservice.ResourceServiceV2";
    private static final String TAG = "StoreProviderManager";
    private static final long WAIT_CONNECTED_TIMEOUT = 45000;
    private static String sCallingPackageName;
    private static volatile StoreProviderManager sInstance;
    private Context mContext;
    private HandlerThread mHandlerThread;
    private IResourceServiceV2 mResourceService;
    private Handler mWorkHandler;
    private final ServiceConnection mServiceConnection = new AnonymousClass1();
    private volatile boolean mIsConnected = false;
    private final Object mConnectLock = new Object();
    private volatile boolean mIsConnecting = false;
    private final Set<RemoteCallbackCookie> mListenerCookies = new ArraySet();
    private final Object mListenerLock = new Object();
    private final Runnable mTryToBindAction = new Runnable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$_-6etHrtks4xr8SvQfXHrYRirrk
        @Override // java.lang.Runnable
        public final void run() {
            StoreProviderManager.this.tryToBind();
        }
    };
    private final Runnable mTryToReleaseAction = new Runnable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$6aVBPmB4mSwf5S3Kvz5KOpanvS0
        @Override // java.lang.Runnable
        public final void run() {
            StoreProviderManager.this.tryToRelease();
        }
    };

    public static StoreProviderManager get() {
        if (sInstance == null) {
            synchronized (StoreProviderManager.class) {
                if (sInstance == null) {
                    sInstance = new StoreProviderManager();
                }
            }
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.appstore.storeprovider.StoreProviderManager$1  reason: invalid class name */
    /* loaded from: classes22.dex */
    public class AnonymousClass1 implements ServiceConnection {
        AnonymousClass1() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, final IBinder service) {
            Log.i(StoreProviderManager.TAG, "onServiceConnected: " + name);
            StoreProviderManager.this.mWorkHandler.post(new Runnable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$1$O9aNe0CKNGtb2m8o0kP_YILK0GU
                @Override // java.lang.Runnable
                public final void run() {
                    StoreProviderManager.AnonymousClass1.this.lambda$onServiceConnected$0$StoreProviderManager$1(service);
                }
            });
        }

        public /* synthetic */ void lambda$onServiceConnected$0$StoreProviderManager$1(IBinder service) {
            StoreProviderManager.this.initInternal(service);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.i(StoreProviderManager.TAG, "onServiceDisconnected: " + name);
            StoreProviderManager.this.mWorkHandler.post(StoreProviderManager.this.mTryToReleaseAction);
        }
    }

    public void initialize(@NonNull Context context) {
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null && handlerThread.isAlive()) {
            Log.w(TAG, "Already initialized! Old:" + this.mContext + ", new:" + context);
            return;
        }
        this.mContext = context.getApplicationContext();
        sCallingPackageName = this.mContext.getPackageName();
        this.mHandlerThread = new HandlerThread(TAG);
        this.mHandlerThread.start();
        this.mWorkHandler = new Handler(this.mHandlerThread.getLooper());
    }

    public void release() {
        this.mListenerCookies.clear();
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        this.mContext = null;
    }

    public static void setFakeCalling(String packageName) {
        sCallingPackageName = packageName;
    }

    public void startObserve() {
        assertContextValid();
        scheduleToBind();
    }

    public void stopObserve() {
        assertContextValid();
        scheduleToRelease();
    }

    Context getContext() {
        return this.mContext;
    }

    @MainThread
    private void scheduleToBind() {
        Log.i(TAG, "scheduleToBind");
        cancelSchedule();
        this.mWorkHandler.post(this.mTryToBindAction);
    }

    @MainThread
    private void scheduleToRelease() {
        Log.i(TAG, "scheduleToRelease");
        this.mWorkHandler.postDelayed(this.mTryToReleaseAction, RELEASE_DELAY);
    }

    @MainThread
    private void cancelSchedule() {
        this.mWorkHandler.removeCallbacks(this.mTryToReleaseAction);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryToBind() {
        synchronized (this.mConnectLock) {
            if (!this.mIsConnected && !this.mIsConnecting) {
                bindService();
            } else {
                Log.i(TAG, "tryToBind: bind already.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryToRelease() {
        synchronized (this.mConnectLock) {
            if (this.mIsConnected) {
                releaseInternal();
            } else {
                Log.i(TAG, "tryToRelease: not connected.");
            }
        }
    }

    @GuardedBy("mConnectLock")
    private void bindService() {
        Context context = this.mContext;
        Log.d(TAG, "bindService, " + context + ", pn:com.xiaopeng.resourceservice");
        assertContextValid();
        this.mIsConnecting = true;
        Intent it = new Intent();
        it.setClassName("com.xiaopeng.resourceservice", "com.xiaopeng.appstore.resourceservice.ResourceServiceV2");
        context.bindService(it, this.mServiceConnection, 1);
    }

    private void unbindService(@NonNull Context context) {
        Log.d(TAG, "unbindService: " + context);
        context.getApplicationContext().unbindService(this.mServiceConnection);
    }

    private boolean waitConnected() {
        synchronized (this.mConnectLock) {
            while (!this.mIsConnected) {
                try {
                    this.mConnectLock.wait(WAIT_CONNECTED_TIMEOUT);
                } catch (InterruptedException e) {
                    Log.w(TAG, "waitConnected ex:" + e);
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isConnectedWait() {
        boolean z;
        synchronized (this.mConnectLock) {
            if (!this.mIsConnected) {
                try {
                    this.mConnectLock.wait(WAIT_CONNECTED_TIMEOUT);
                } catch (InterruptedException e) {
                    Log.w(TAG, "waitConnected ex:" + e);
                    Thread.currentThread().interrupt();
                }
            }
            z = this.mIsConnected;
        }
        return z;
    }

    private void notifyConnected() {
        synchronized (this.mConnectLock) {
            this.mIsConnecting = false;
            this.mIsConnected = true;
            this.mConnectLock.notifyAll();
        }
    }

    private void notifyDisconnected() {
        synchronized (this.mConnectLock) {
            this.mIsConnected = false;
            this.mIsConnecting = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initInternal(IBinder service) {
        Log.d(TAG, "init: service:" + this.mContext);
        assertContextValid();
        this.mResourceService = IResourceServiceV2.Stub.asInterface(service);
        synchronized (this.mListenerLock) {
            if (!this.mListenerCookies.isEmpty()) {
                for (RemoteCallbackCookie cookie : this.mListenerCookies) {
                    try {
                        Log.i(TAG, "Register remote callback in init:" + cookie);
                        this.mResourceService.registerAssembleListener(cookie.resType, cookie.callingPackage, cookie.listenerWrapper);
                    } catch (RemoteException e) {
                        Log.w(TAG, "init, registerCallback ex:" + e);
                    }
                }
            }
        }
        notifyConnected();
        Log.d(TAG, "init: service END:" + this.mContext);
    }

    private void releaseInternal() {
        Log.d(TAG, "release");
        if (this.mResourceService != null) {
            for (RemoteCallbackCookie cookie : this.mListenerCookies) {
                try {
                    Log.i(TAG, "Unregister remote callback in release:" + cookie);
                    this.mResourceService.unregisterAssembleListener(cookie.listenerWrapper);
                } catch (RemoteException e) {
                    Log.w(TAG, "release, unregisterAssembleListener ex:" + e);
                }
            }
        }
        unbindService(this.mContext);
        notifyDisconnected();
        this.mResourceService = null;
        Log.d(TAG, "release end");
    }

    public ResourceContainer query(@NonNull final ResourceRequest resourceRequest) {
        Log.d(TAG, "query start:" + resourceRequest);
        return (ResourceContainer) executeWaitConnected(new Callable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$66PZefPvGkRd5PI_lSTTbL3iV80
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return StoreProviderManager.this.lambda$query$0$StoreProviderManager(resourceRequest);
            }
        });
    }

    public /* synthetic */ ResourceContainer lambda$query$0$StoreProviderManager(ResourceRequest resourceRequest) throws Exception {
        IResourceServiceV2 iResourceServiceV2 = this.mResourceService;
        if (iResourceServiceV2 != null) {
            try {
                return iResourceServiceV2.query(sCallingPackageName, resourceRequest);
            } catch (RemoteException e) {
                Log.w(TAG, "query ex:" + e);
                return null;
            }
        }
        return null;
    }

    @WorkerThread
    public AssembleResult assemble(@NonNull AssembleRequest assembleRequest) {
        return assemble(assembleRequest, sCallingPackageName);
    }

    @WorkerThread
    public AssembleResult assemble(@NonNull final AssembleRequest assembleRequest, final String callingPackage) {
        Log.d(TAG, "assemble start:" + assembleRequest + ", calling:" + callingPackage);
        return (AssembleResult) executeWaitConnected(new Callable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$M9U6jBT973prKQuxiV7bItCMLyI
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return StoreProviderManager.this.lambda$assemble$1$StoreProviderManager(assembleRequest, callingPackage);
            }
        });
    }

    public /* synthetic */ AssembleResult lambda$assemble$1$StoreProviderManager(AssembleRequest assembleRequest, String callingPackage) throws Exception {
        IResourceServiceV2 iResourceServiceV2 = this.mResourceService;
        if (iResourceServiceV2 != null) {
            try {
                if (assembleRequest instanceof EnqueueRequest) {
                    return iResourceServiceV2.assembleEnqueue(callingPackage, (EnqueueRequest) assembleRequest);
                }
                if (assembleRequest instanceof SimpleRequest) {
                    return iResourceServiceV2.assembleAction(callingPackage, (SimpleRequest) assembleRequest);
                }
                Log.w(TAG, "assemble: not support request:" + assembleRequest);
                return null;
            } catch (RemoteException e) {
                Log.w(TAG, "assemble ex:" + e);
                return null;
            }
        }
        return null;
    }

    public List<AssembleInfo> getAssembleInfoList() {
        return getAssembleInfoList(-1, sCallingPackageName);
    }

    public List<AssembleInfo> getAssembleInfoList(int resType) {
        return getAssembleInfoList(resType, null);
    }

    public List<AssembleInfo> getAssembleInfoList(final int resType, final String callingPackage) {
        List<AssembleInfo> list = (List) executeWaitConnected(new Callable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$5BNSgbuTeV94KY0k2erIpbjj7eA
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return StoreProviderManager.this.lambda$getAssembleInfoList$2$StoreProviderManager(resType, callingPackage);
            }
        });
        Log.d(TAG, "getAssembleInfoList:" + list);
        return list;
    }

    public /* synthetic */ List lambda$getAssembleInfoList$2$StoreProviderManager(int resType, String callingPackage) throws Exception {
        if (this.mResourceService != null) {
            Log.d(TAG, "getAssembleInfoList start, resType:" + resType + ", calling:" + callingPackage);
            try {
                return this.mResourceService.getAssembleInfoList(resType, callingPackage);
            } catch (RemoteException e) {
                Log.w(TAG, "getAssembleInfoList ex:" + e);
                return null;
            }
        }
        return null;
    }

    public void registerListener(int resourceType, IAssembleClientListener listener) {
        registerListener(resourceType, sCallingPackageName, listener);
    }

    public void registerListener(int resourceType, String callingPackage, IAssembleClientListener listener) {
        if (listener == null) {
            return;
        }
        if (this.mResourceService != null) {
            Log.i(TAG, "registerListener, register remoteCallback for resType:" + resourceType + ", calling:" + callingPackage + ", lis:" + listener);
            try {
                this.mResourceService.registerAssembleListener(resourceType, callingPackage, new RemoteCallbackWrapper(listener));
            } catch (RemoteException e) {
                Log.w(TAG, "registerListener, registerAssembleListener ex:" + e);
            }
        } else {
            Log.i(TAG, "registerListener, not connected yet, add to pending, resType:" + resourceType + ", calling:" + callingPackage + ", lis:" + listener);
        }
        synchronized (this.mListenerLock) {
            Log.i(TAG, "registerListener, add to list, lis:" + listener + ", list:" + this.mListenerCookies);
            RemoteCallbackCookie cookie = new RemoteCallbackCookie(null);
            cookie.callingPackage = callingPackage;
            cookie.resType = resourceType;
            cookie.listenerWrapper = new RemoteCallbackWrapper(listener);
            this.mListenerCookies.add(cookie);
        }
    }

    public void unregisterListener(IAssembleClientListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (this.mListenerLock) {
            Iterator<RemoteCallbackCookie> it = this.mListenerCookies.iterator();
            while (it.hasNext()) {
                RemoteCallbackCookie cookie = it.next();
                if (cookie.listenerWrapper.clientListener == listener) {
                    Log.i(TAG, "unregisterListener, unregisterListener remoteCallback now:" + cookie);
                    if (this.mResourceService != null) {
                        try {
                            this.mResourceService.unregisterAssembleListener(cookie.listenerWrapper);
                        } catch (RemoteException e) {
                            Log.w(TAG, "unregisterListener, registerAssembleListener ex:" + e);
                        }
                    }
                    it.remove();
                }
            }
        }
    }

    public AppGroupsResp queryAppGroups(final int... appGroupTypes) {
        return (AppGroupsResp) executeWaitConnected(new Callable() { // from class: com.xiaopeng.appstore.storeprovider.-$$Lambda$StoreProviderManager$CSlhitjN8a_Il3a66RpQSi8Uop4
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return StoreProviderManager.this.lambda$queryAppGroups$3$StoreProviderManager(appGroupTypes);
            }
        });
    }

    public /* synthetic */ AppGroupsResp lambda$queryAppGroups$3$StoreProviderManager(int[] appGroupTypes) throws Exception {
        if (this.mResourceService != null) {
            Log.d(TAG, "queryAppGroups start, appGroupTypes:" + appGroupTypes);
            try {
                return this.mResourceService.queryAppGroups(appGroupTypes);
            } catch (RemoteException e) {
                Log.w(TAG, "queryAppGroups ex:" + e);
                return null;
            }
        }
        return null;
    }

    private void assertContextValid() {
        if (this.mContext == null) {
            throw new IllegalStateException("initialize(Context context) should be called first.");
        }
    }

    private boolean tryConnected() {
        boolean z;
        synchronized (this.mConnectLock) {
            boolean connected = this.mIsConnected;
            if (!connected || this.mResourceService == null) {
                if (!this.mIsConnecting) {
                    bindService();
                }
                connected = isConnectedWait();
            }
            z = connected && this.mResourceService != null;
        }
        return z;
    }

    private <T> T executeWaitConnected(@NonNull Callable<T> callback) {
        T result;
        Log.d(TAG, "executeWaitConnected");
        synchronized (this.mConnectLock) {
            boolean connected = this.mIsConnected;
            if (connected && this.mResourceService != null) {
                Log.d(TAG, "executeWaitConnected: already connected");
                result = null;
                if (!connected && this.mResourceService != null) {
                    try {
                        result = callback.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w(TAG, "executeWaitConnected error, callback ex:" + e);
                    }
                } else {
                    Log.w(TAG, "executeWaitConnected error: bindService fail");
                    notifyDisconnected();
                }
            }
            if (!this.mIsConnecting) {
                bindService();
            }
            connected = isConnectedWait();
            result = null;
            if (!connected) {
            }
            Log.w(TAG, "executeWaitConnected error: bindService fail");
            notifyDisconnected();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public static class RemoteCallbackCookie {
        public String callingPackage;
        public RemoteCallbackWrapper listenerWrapper;
        public int resType;

        private RemoteCallbackCookie() {
        }

        /* synthetic */ RemoteCallbackCookie(AnonymousClass1 x0) {
            this();
        }

        public String toString() {
            return "RemoteCallbackCookie{lis=" + this.listenerWrapper + ", calling='" + this.callingPackage + "', resType=" + this.resType + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof RemoteCallbackCookie) {
                RemoteCallbackCookie cookie = (RemoteCallbackCookie) o;
                return this.resType == cookie.resType && Objects.equals(this.listenerWrapper, cookie.listenerWrapper) && Objects.equals(this.callingPackage, cookie.callingPackage);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.listenerWrapper, this.callingPackage, Integer.valueOf(this.resType));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public static class RemoteCallbackWrapper extends IAssembleListener.Stub {
        public final IAssembleClientListener clientListener;

        public RemoteCallbackWrapper(@NonNull IAssembleClientListener clientListener) {
            this.clientListener = clientListener;
        }

        @Override // com.xiaopeng.appstore.storeprovider.IAssembleListener
        public void onAssembleEvent(int eventType, AssembleInfo info) throws RemoteException {
            Log.d(StoreProviderManager.TAG, "onAssembleEvent, type:" + eventType + ", info:" + info + ", client:" + this.clientListener);
            this.clientListener.onAssembleEvent(eventType, info);
        }

        public String toString() {
            return "RemoteCallbackWrapper{client=" + this.clientListener + "} " + super.toString();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof RemoteCallbackWrapper) {
                RemoteCallbackWrapper that = (RemoteCallbackWrapper) o;
                return Objects.equals(this.clientListener, that.clientListener);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.clientListener);
        }
    }
}
