package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.net.INetworkPolicyListener;
import android.net.NetworkPolicyManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import com.android.systemui.statusbar.policy.DataSaverController;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class DataSaverControllerImpl implements DataSaverController {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<DataSaverController.Listener> mListeners = new ArrayList<>();
    private final INetworkPolicyListener mPolicyListener = new NetworkPolicyManager.Listener() { // from class: com.android.systemui.statusbar.policy.DataSaverControllerImpl.1
        public void onRestrictBackgroundChanged(final boolean isDataSaving) {
            DataSaverControllerImpl.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.DataSaverControllerImpl.1.1
                @Override // java.lang.Runnable
                public void run() {
                    DataSaverControllerImpl.this.handleRestrictBackgroundChanged(isDataSaving);
                }
            });
        }
    };
    private final NetworkPolicyManager mPolicyManager;

    public DataSaverControllerImpl(Context context) {
        this.mPolicyManager = NetworkPolicyManager.from(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRestrictBackgroundChanged(boolean isDataSaving) {
        synchronized (this.mListeners) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                this.mListeners.get(i).onDataSaverChanged(isDataSaving);
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(DataSaverController.Listener listener) {
        synchronized (this.mListeners) {
            this.mListeners.add(listener);
            if (this.mListeners.size() == 1) {
                this.mPolicyManager.registerListener(this.mPolicyListener);
            }
        }
        listener.onDataSaverChanged(isDataSaverEnabled());
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(DataSaverController.Listener listener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(listener);
            if (this.mListeners.size() == 0) {
                this.mPolicyManager.unregisterListener(this.mPolicyListener);
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.DataSaverController
    public boolean isDataSaverEnabled() {
        return this.mPolicyManager.getRestrictBackground();
    }

    @Override // com.android.systemui.statusbar.policy.DataSaverController
    public void setDataSaverEnabled(boolean enabled) {
        this.mPolicyManager.setRestrictBackground(enabled);
        try {
            this.mPolicyListener.onRestrictBackgroundChanged(enabled);
        } catch (RemoteException e) {
        }
    }
}
