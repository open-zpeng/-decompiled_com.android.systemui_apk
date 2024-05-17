package com.xiaopeng.systemui.server;

import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.ArraySet;
import androidx.annotation.NonNull;
import com.xiaopeng.xuimanager.systemui.systembar.ISystemBarListener;
import com.xiaopeng.xuimanager.systemui.systembar.SystemBarItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class SystemBarServer extends BaseServer {
    private final HashMap<String, SystemBarRecord> mBarRecords;
    private final Handler mHandler;
    private final ArrayList<SystemBarListenerRecord> mListenerRecords;
    private final Object mLock;
    private final ArraySet<SystemBarServerListener> mServerListeners;

    /* loaded from: classes24.dex */
    public interface SystemBarServerListener {
        void onHide(SystemBarRecord systemBarRecord);

        void onShow(SystemBarRecord systemBarRecord);
    }

    @Override // com.xiaopeng.systemui.server.BaseServer
    protected String logTag() {
        return "Bar";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int showSystemBar(String pkg, String id, SystemBarItem bar) {
        logI(String.format("showSystemBar pkg:%s, id: %s,bar:%s", pkg, id, bar));
        if (!"personal".equals(id)) {
            logW(String.format("showSystemBar NOT personal pkg:%s, id: %s,bar:%s", pkg, id, bar));
            return 0;
        }
        String barKey = SystemBarRecord.getBarKey(pkg, id);
        final SystemBarRecord record = new SystemBarRecord(pkg, id, bar);
        synchronized (this.mBarRecords) {
            this.mBarRecords.put(barKey, record);
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.server.-$$Lambda$SystemBarServer$kClCG7kIZiwdWGhDLrNRf1w-jVg
            @Override // java.lang.Runnable
            public final void run() {
                SystemBarServer.this.lambda$showSystemBar$0$SystemBarServer(record);
            }
        });
        return 1;
    }

    public /* synthetic */ void lambda$showSystemBar$0$SystemBarServer(SystemBarRecord record) {
        synchronized (this.mServerListeners) {
            Iterator<SystemBarServerListener> it = this.mServerListeners.iterator();
            while (it.hasNext()) {
                SystemBarServerListener listener = it.next();
                listener.onShow(record);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int cancelSystemBar(String pkg, String id) {
        final SystemBarRecord record;
        logI(String.format("cancelSystemBar pkg:%s, id:%s", pkg, id));
        String barKey = SystemBarRecord.getBarKey(pkg, id);
        synchronized (this.mBarRecords) {
            record = this.mBarRecords.get(barKey);
            if (record != null) {
                this.mBarRecords.remove(barKey);
            }
        }
        if (record != null) {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.server.-$$Lambda$SystemBarServer$mzR8gVUmhP2TujJ1y9LSNYgGwrI
                @Override // java.lang.Runnable
                public final void run() {
                    SystemBarServer.this.lambda$cancelSystemBar$1$SystemBarServer(record);
                }
            });
        } else {
            logI(String.format("cancelSystemBar pkg:%s, id:%s  is  not show ", pkg, id));
        }
        return 1;
    }

    public /* synthetic */ void lambda$cancelSystemBar$1$SystemBarServer(SystemBarRecord record) {
        synchronized (this.mServerListeners) {
            Iterator<SystemBarServerListener> it = this.mServerListeners.iterator();
            while (it.hasNext()) {
                SystemBarServerListener listener = it.next();
                listener.onHide(record);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int registerSystemBarListener(String pkg, ISystemBarListener listener) {
        logI(String.format("registerSystemBarListener pkg:%s, listener:%s", pkg, listener));
        int pid = Binder.getCallingPid();
        int uid = Binder.getCallingUid();
        synchronized (this.mLock) {
            int index = findIndexOfListenerLocked(listener);
            if (index != -1) {
                logW("SystemBarListener is already added, ignoring");
                return 1;
            }
            SystemBarListenerRecord record = new SystemBarListenerRecord(pid, uid, pkg, listener);
            try {
                listener.asBinder().linkToDeath(record, 0);
                synchronized (this.mListenerRecords) {
                    this.mListenerRecords.add(record);
                }
                return 1;
            } catch (RemoteException e) {
                logE("SystemBarListener is dead, ignoring it");
                return 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int unRegisterSystemBarListener(String pkg, ISystemBarListener listener) {
        SystemBarListenerRecord record;
        logI(String.format("unRegisterSystemBarListener pkg:%s, listener:%s", pkg, listener));
        synchronized (this.mLock) {
            int index = findIndexOfListenerLocked(listener);
            if (index != -1) {
                synchronized (this.mListenerRecords) {
                    record = this.mListenerRecords.remove(index);
                }
                if (record != null) {
                    try {
                        record.listener.asBinder().unlinkToDeath(record, 0);
                    } catch (Exception e) {
                    }
                }
            } else {
                logW("SystemBarListener is already remove, ignoring");
            }
        }
        return 1;
    }

    private int findIndexOfListenerLocked(ISystemBarListener listener) {
        synchronized (this.mListenerRecords) {
            for (int i = this.mListenerRecords.size() - 1; i >= 0; i--) {
                if (this.mListenerRecords.get(i).listener.asBinder() == listener.asBinder()) {
                    return i;
                }
            }
            return -1;
        }
    }

    private SystemBarListenerRecord findListenerLocked(String pkg) {
        synchronized (this.mListenerRecords) {
            Iterator<SystemBarListenerRecord> it = this.mListenerRecords.iterator();
            while (it.hasNext()) {
                SystemBarListenerRecord listenerRecord = it.next();
                if (listenerRecord.pkg != null && listenerRecord.pkg.equals(pkg)) {
                    return listenerRecord;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class SystemBarListenerRecord implements IBinder.DeathRecipient {
        ISystemBarListener listener;
        final int pid;
        final String pkg;
        final int uid;

        SystemBarListenerRecord(int pid, int uid, String pkg, ISystemBarListener listener) {
            this.pid = pid;
            this.uid = uid;
            this.pkg = pkg;
            this.listener = listener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (SystemBarServer.this.mListenerRecords) {
                SystemBarServer.this.mListenerRecords.remove(this);
            }
            this.listener = null;
            SystemBarServer.this.cancelSystemBar(this.pkg, "personal");
        }

        @NonNull
        public String toString() {
            return "SystemBarListenerRecord{pid=" + this.pid + ", uid=" + this.uid + ", pkg='" + this.pkg + "', listener=" + this.listener + '}';
        }
    }

    public void addSystemBarServerListener(SystemBarServerListener barServerListener) {
        synchronized (this.mServerListeners) {
            this.mServerListeners.add(barServerListener);
            logI("addSystemBarServerListener");
        }
    }

    public void removeSystemBarServerListener(SystemBarServerListener barServerListener) {
        synchronized (this.mServerListeners) {
            this.mServerListeners.remove(barServerListener);
            logI("removeSystemBarServerListener");
        }
    }

    public void notifySystemBarContent(String key) throws RemoteException {
        SystemBarRecord record;
        synchronized (this.mBarRecords) {
            record = this.mBarRecords.get(key);
        }
        if (record != null) {
            notifySystemBarContent(record);
            return;
        }
        logI("notifySystemBarContent not record " + key);
    }

    public void notifySystemBarContent(SystemBarRecord record) throws RemoteException {
        SystemBarListenerRecord listenerRecord = findListenerLocked(record.getPkg());
        logI("notifySystemBarContent listenerRecord " + listenerRecord);
        if (listenerRecord != null) {
            ISystemBarListener listener = listenerRecord.listener;
            if (listener == null) {
                logI("notifySystemBarContent listener is null " + record);
                return;
            }
            listener.onContent(record.getId());
        }
    }

    public ArrayList<SystemBarRecord> getCurrentSystemBarRecords() {
        ArrayList<SystemBarRecord> arrayList;
        synchronized (this.mBarRecords) {
            Collection<SystemBarRecord> list = this.mBarRecords.values();
            arrayList = new ArrayList<>(list);
        }
        return arrayList;
    }

    /* loaded from: classes24.dex */
    private static class Holder {
        private static final SystemBarServer sInstance = new SystemBarServer();

        private Holder() {
        }
    }

    private SystemBarServer() {
        this.mLock = new Object();
        this.mListenerRecords = new ArrayList<>();
        this.mBarRecords = new HashMap<>();
        this.mServerListeners = new ArraySet<>();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public static SystemBarServer get() {
        return Holder.sInstance;
    }
}
