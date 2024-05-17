package com.xiaopeng.systemui.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes24.dex */
public class BluetoothController implements BluetoothCallback, CachedBluetoothDevice.Callback {
    private static final String BROADCAST_PSN_A2DP_CONNECTION_STATE = "xiaopeng.bluetooth.a2dp.action.CONNECTION_STATE_CHANGED";
    private static final String BROADCAST_PSN_BLUETOOTH_STATE = "xiaopeng.bluetooth.action.ACTION_STATE_CHANGED";
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_OFF = 10;
    public static final int STATE_ON = 12;
    private static final String TAG = "BluetoothController";
    private boolean mBluetoothEnabled;
    private int mBluetoothNumber;
    private int mBluetoothState;
    private final ArrayList<Callback> mCallbacks;
    private Context mContext;
    private final H mHandler;
    private CachedBluetoothDevice mLastDevice;
    private LocalBluetoothManager mLocalBluetoothManager;
    private List<PsnBluetoothCallback> mPsnBluetoothCallbacks;
    private int mPsnBluetoothState;
    private BroadcastReceiver mReceiver;

    /* loaded from: classes24.dex */
    public interface Callback {
        void onBluetoothChanged(int i, int i2);
    }

    /* loaded from: classes24.dex */
    public interface PsnBluetoothCallback {
        void onPsnBluetoothStateChanged(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final BluetoothController sInstance = new BluetoothController();

        private SingleHolder() {
        }
    }

    public static BluetoothController getInstance() {
        return SingleHolder.sInstance;
    }

    public void addPsnBluetoothCallback(PsnBluetoothCallback callback) {
        this.mPsnBluetoothCallbacks.add(callback);
    }

    private BluetoothController() {
        this.mCallbacks = new ArrayList<>();
        this.mPsnBluetoothCallbacks = new ArrayList();
        this.mBluetoothNumber = 0;
        this.mBluetoothState = 0;
        this.mPsnBluetoothState = 10;
        this.mHandler = new H();
        this.mReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.BluetoothController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                char c;
                String action = intent.getAction();
                int hashCode = action.hashCode();
                if (hashCode != -611508076) {
                    if (hashCode == 279629229 && action.equals(BluetoothController.BROADCAST_PSN_A2DP_CONNECTION_STATE)) {
                        c = 0;
                    }
                    c = 65535;
                } else {
                    if (action.equals(BluetoothController.BROADCAST_PSN_BLUETOOTH_STATE)) {
                        c = 1;
                    }
                    c = 65535;
                }
                if (c == 0) {
                    int state = intent.getIntExtra("state", 0);
                    Logger.d(BluetoothController.TAG, "psn a2dp state = " + state);
                    BluetoothController.this.updatePsnA2dpState(state);
                } else if (c == 1) {
                    int state2 = intent.getIntExtra("state", 10);
                    Logger.d(BluetoothController.TAG, "psn bluetooth state = " + state2);
                    BluetoothController.this.updatePsnBluetoothState(state2);
                }
            }
        };
        this.mContext = ContextUtils.getContext();
        Log.d(TAG, "CompileConfig.COMPILE_DEBUG false");
        this.mLocalBluetoothManager = LocalBluetoothManager.getInstance(this.mContext, null);
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.getEventManager().registerCallback(this);
            onBluetoothStateChanged(this.mLocalBluetoothManager.getBluetoothAdapter().getBluetoothState());
        }
        registerBroadCastReceiver();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && adapter.isDeviceConnected(1)) {
            this.mPsnBluetoothState = 2;
        } else {
            this.mPsnBluetoothState = 10;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePsnBluetoothState(int state) {
        Logger.d(TAG, "updatePsnBluetoothState : " + state);
        int psnBluetoothState = 10;
        if (state == 10) {
            psnBluetoothState = 10;
        } else if (state == 12) {
            psnBluetoothState = 12;
        }
        onPsnBluetoothStateChanged(psnBluetoothState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePsnA2dpState(int state) {
        Logger.d(TAG, "updatePsnA2dpState : " + state);
        int psnBluetoothState = 0;
        if (state == 1) {
            psnBluetoothState = 1;
        } else if (state == 2) {
            psnBluetoothState = 2;
        }
        onPsnBluetoothStateChanged(psnBluetoothState);
    }

    private void onPsnBluetoothStateChanged(int state) {
        Logger.d(TAG, "onPsnBluetoothStateChanged : " + state + "," + this.mPsnBluetoothState);
        if (this.mPsnBluetoothState != state) {
            this.mPsnBluetoothState = state;
            for (PsnBluetoothCallback callback : this.mPsnBluetoothCallbacks) {
                callback.onPsnBluetoothStateChanged(this.mPsnBluetoothState);
            }
        }
    }

    public int getPsnBluetoothState() {
        return this.mPsnBluetoothState;
    }

    private void registerBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_PSN_A2DP_CONNECTION_STATE);
        filter.addAction(BROADCAST_PSN_BLUETOOTH_STATE);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    public int getBluetoothState() {
        return this.mBluetoothState;
    }

    public int getBluetoothNumber() {
        return this.mBluetoothNumber;
    }

    public void addBluetoothCallback(Callback callback) {
        this.mCallbacks.add(callback);
        this.mHandler.sendEmptyMessage(2);
    }

    public void removeBluetoothCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    public boolean isBluetoothSupported() {
        return this.mLocalBluetoothManager != null;
    }

    public boolean isBluetoothEnabled() {
        return this.mBluetoothEnabled;
    }

    public String getLastDeviceName() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mLastDevice;
        if (cachedBluetoothDevice != null) {
            return cachedBluetoothDevice.getName();
        }
        return null;
    }

    public void setBluetoothEnabled(boolean enabled) {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.getBluetoothAdapter().setBluetoothEnabled(enabled);
        }
    }

    public Collection<CachedBluetoothDevice> getDevices() {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            return localBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
        }
        return null;
    }

    public void connect(CachedBluetoothDevice device) {
        if (this.mLocalBluetoothManager == null || device == null) {
            return;
        }
        device.connect(true);
    }

    public void disconnect(CachedBluetoothDevice device) {
        if (this.mLocalBluetoothManager == null || device == null) {
            return;
        }
        device.disconnect();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int bluetoothState) {
        Logger.d(TAG, "onBluetoothStateChanged : bluetoothState = " + bluetoothState);
        this.mBluetoothEnabled = bluetoothState == 12;
        int bluetoothConnectionState = this.mLocalBluetoothManager.getBluetoothAdapter().getConnectionState();
        this.mBluetoothState = this.mBluetoothEnabled ? bluetoothConnectionState : 10;
        updateBluetooth(this.mBluetoothState);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean b) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        Logger.d(TAG, "onDeviceAdded");
        cachedBluetoothDevice.registerCallback(this);
        updateBluetooth();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        Logger.d(TAG, "onDeviceDeleted");
        updateBluetooth();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        updateBluetooth();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedDevice, int state) {
        Logger.d(TAG, "onConnectionStateChanged state =" + state + " mBluetoothEnabled = " + this.mBluetoothEnabled);
        this.mLastDevice = cachedDevice;
        if (this.mBluetoothEnabled) {
            this.mBluetoothState = state;
            Logger.d(TAG, "onConnectionStateChanged state =" + state);
            updateBluetooth(state);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public final class H extends Handler {
        private static final int MSG_STATE_CHANGED = 2;

        private H() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                fireStateChanged();
            }
        }

        private void fireStateChanged() {
            Iterator it = BluetoothController.this.mCallbacks.iterator();
            while (it.hasNext()) {
                Callback cb = (Callback) it.next();
                cb.onBluetoothChanged(BluetoothController.this.mBluetoothState, BluetoothController.this.mBluetoothNumber);
            }
        }
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
    }

    private void updateBluetooth() {
        int connectedNumber = 0;
        Set<BluetoothDevice> devices = this.mLocalBluetoothManager.getBluetoothAdapter().getBondedDevices();
        if (devices != null) {
            for (BluetoothDevice device : devices) {
                if (device.isConnected()) {
                    connectedNumber++;
                }
            }
        }
        this.mBluetoothNumber = connectedNumber;
        int state = this.mLocalBluetoothManager.getBluetoothAdapter().getConnectionState();
        this.mBluetoothState = this.mBluetoothEnabled ? state : 10;
        this.mHandler.sendEmptyMessage(2);
        Logger.d(TAG, "updateBluetooth mBluetoothNumber=" + this.mBluetoothNumber);
        Logger.d(TAG, "updateBluetooth mBluetoothState =" + this.mBluetoothState);
    }

    private void updateBluetooth(int state) {
        this.mBluetoothState = state;
        this.mHandler.sendEmptyMessage(2);
        Logger.d(TAG, "updateBluetooth mBluetoothState = " + this.mBluetoothState);
    }
}
