package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.BluetoothController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class BluetoothControllerImpl implements BluetoothController, BluetoothCallback, CachedBluetoothDevice.Callback, LocalBluetoothProfileManager.ServiceListener {
    private boolean mAudioProfileOnly;
    private final Handler mBgHandler;
    private final int mCurrentUser;
    private boolean mEnabled;
    private boolean mIsActive;
    private final LocalBluetoothManager mLocalBluetoothManager;
    private int mState;
    private final UserManager mUserManager;
    private static final String TAG = "BluetoothController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final WeakHashMap<CachedBluetoothDevice, ActuallyCachedState> mCachedState = new WeakHashMap<>();
    private final List<CachedBluetoothDevice> mConnectedDevices = new ArrayList();
    private int mConnectionState = 0;
    private final H mHandler = new H(Looper.getMainLooper());

    @Inject
    public BluetoothControllerImpl(Context context, @Named("background_looper") Looper bgLooper, LocalBluetoothManager localBluetoothManager) {
        this.mLocalBluetoothManager = localBluetoothManager;
        this.mBgHandler = new Handler(bgLooper);
        LocalBluetoothManager localBluetoothManager2 = this.mLocalBluetoothManager;
        if (localBluetoothManager2 != null) {
            localBluetoothManager2.getEventManager().registerCallback(this);
            this.mLocalBluetoothManager.getProfileManager().addServiceListener(this);
            onBluetoothStateChanged(this.mLocalBluetoothManager.getBluetoothAdapter().getBluetoothState());
        }
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mCurrentUser = ActivityManager.getCurrentUser();
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean canConfigBluetooth() {
        return (this.mUserManager.hasUserRestriction("no_config_bluetooth", UserHandle.of(this.mCurrentUser)) || this.mUserManager.hasUserRestriction("no_bluetooth", UserHandle.of(this.mCurrentUser))) ? false : true;
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("BluetoothController state:");
        pw.print("  mLocalBluetoothManager=");
        pw.println(this.mLocalBluetoothManager);
        if (this.mLocalBluetoothManager == null) {
            return;
        }
        pw.print("  mEnabled=");
        pw.println(this.mEnabled);
        pw.print("  mConnectionState=");
        pw.println(stateToString(this.mConnectionState));
        pw.print("  mAudioProfileOnly=");
        pw.println(this.mAudioProfileOnly);
        pw.print("  mIsActive=");
        pw.println(this.mIsActive);
        pw.print("  mConnectedDevices=");
        pw.println(this.mConnectedDevices);
        pw.print("  mCallbacks.size=");
        pw.println(this.mHandler.mCallbacks.size());
        pw.println("  Bluetooth Devices:");
        for (CachedBluetoothDevice device : getDevices()) {
            pw.println("    " + getDeviceString(device));
        }
    }

    private static String stateToString(int state) {
        if (state != 0) {
            if (state != 1) {
                if (state != 2) {
                    if (state == 3) {
                        return "DISCONNECTING";
                    }
                    return "UNKNOWN(" + state + NavigationBarInflaterView.KEY_CODE_END;
                }
                return "CONNECTED";
            }
            return "CONNECTING";
        }
        return "DISCONNECTED";
    }

    private String getDeviceString(CachedBluetoothDevice device) {
        return device.getName() + " " + device.getBondState() + " " + device.isConnected();
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public int getBondState(CachedBluetoothDevice device) {
        return getCachedState(device).mBondState;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public List<CachedBluetoothDevice> getConnectedDevices() {
        return this.mConnectedDevices;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public int getMaxConnectionState(CachedBluetoothDevice device) {
        return getCachedState(device).mMaxConnectionState;
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(BluetoothController.Callback cb) {
        this.mHandler.obtainMessage(3, cb).sendToTarget();
        this.mHandler.sendEmptyMessage(2);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(BluetoothController.Callback cb) {
        this.mHandler.obtainMessage(4, cb).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean isBluetoothEnabled() {
        return this.mEnabled;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public int getBluetoothState() {
        return this.mState;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean isBluetoothConnected() {
        return this.mConnectionState == 2;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean isBluetoothConnecting() {
        return this.mConnectionState == 1;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean isBluetoothAudioProfileOnly() {
        return this.mAudioProfileOnly;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean isBluetoothAudioActive() {
        return this.mIsActive;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public void setBluetoothEnabled(boolean enabled) {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.getBluetoothAdapter().setBluetoothEnabled(enabled);
        }
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public boolean isBluetoothSupported() {
        return this.mLocalBluetoothManager != null;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public void connect(CachedBluetoothDevice device) {
        if (this.mLocalBluetoothManager == null || device == null) {
            return;
        }
        device.connect(true);
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public void disconnect(CachedBluetoothDevice device) {
        if (this.mLocalBluetoothManager == null || device == null) {
            return;
        }
        device.disconnect();
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public String getConnectedDeviceName() {
        if (this.mConnectedDevices.size() == 1) {
            return this.mConnectedDevices.get(0).getName();
        }
        return null;
    }

    @Override // com.android.systemui.statusbar.policy.BluetoothController
    public Collection<CachedBluetoothDevice> getDevices() {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            return localBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
        }
        return null;
    }

    private void updateConnected() {
        int state = this.mLocalBluetoothManager.getBluetoothAdapter().getConnectionState();
        this.mConnectedDevices.clear();
        for (CachedBluetoothDevice device : getDevices()) {
            int maxDeviceState = device.getMaxConnectionState();
            if (maxDeviceState > state) {
                state = maxDeviceState;
            }
            if (device.isConnected()) {
                this.mConnectedDevices.add(device);
            }
        }
        if (this.mConnectedDevices.isEmpty() && state == 2) {
            state = 0;
        }
        if (state != this.mConnectionState) {
            this.mConnectionState = state;
            this.mHandler.sendEmptyMessage(2);
        }
        updateAudioProfile();
    }

    private void updateActive() {
        boolean isActive = false;
        for (CachedBluetoothDevice device : getDevices()) {
            boolean z = true;
            if (!device.isActiveDevice(1) && !device.isActiveDevice(2) && !device.isActiveDevice(21)) {
                z = false;
            }
            isActive |= z;
        }
        if (this.mIsActive != isActive) {
            this.mIsActive = isActive;
            this.mHandler.sendEmptyMessage(2);
        }
    }

    private void updateAudioProfile() {
        boolean audioProfileConnected = false;
        boolean otherProfileConnected = false;
        Iterator<CachedBluetoothDevice> it = getDevices().iterator();
        while (true) {
            boolean z = true;
            if (!it.hasNext()) {
                break;
            }
            CachedBluetoothDevice device = it.next();
            for (LocalBluetoothProfile profile : device.getProfileListCopy()) {
                int profileId = profile.getProfileId();
                boolean isConnected = device.isConnectedProfile(profile);
                if (profileId == 1 || profileId == 2 || profileId == 21) {
                    audioProfileConnected |= isConnected;
                } else {
                    otherProfileConnected |= isConnected;
                }
            }
        }
        boolean audioProfileOnly = (!audioProfileConnected || otherProfileConnected) ? false : false;
        if (audioProfileOnly != this.mAudioProfileOnly) {
            this.mAudioProfileOnly = audioProfileOnly;
            this.mHandler.sendEmptyMessage(2);
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int bluetoothState) {
        if (DEBUG) {
            Log.d(TAG, "BluetoothStateChanged=" + stateToString(bluetoothState));
        }
        this.mEnabled = bluetoothState == 12 || bluetoothState == 11;
        this.mState = bluetoothState;
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedDevice) {
        if (DEBUG) {
            Log.d(TAG, "DeviceAdded=" + cachedDevice.getAddress());
        }
        cachedDevice.registerCallback(this);
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedDevice) {
        if (DEBUG) {
            Log.d(TAG, "DeviceDeleted=" + cachedDevice.getAddress());
        }
        this.mCachedState.remove(cachedDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedDevice, int bondState) {
        if (DEBUG) {
            Log.d(TAG, "DeviceBondStateChanged=" + cachedDevice.getAddress());
        }
        this.mCachedState.remove(cachedDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        if (DEBUG) {
            Log.d(TAG, "DeviceAttributesChanged");
        }
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedDevice, int state) {
        if (DEBUG) {
            Log.d(TAG, "ConnectionStateChanged=" + cachedDevice.getAddress() + " " + stateToString(state));
        }
        this.mCachedState.remove(cachedDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice activeDevice, int bluetoothProfile) {
        if (DEBUG) {
            Log.d(TAG, "ActiveDeviceChanged=" + activeDevice.getAddress() + " profileId=" + bluetoothProfile);
        }
        updateActive();
        this.mHandler.sendEmptyMessage(2);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAclConnectionStateChanged(CachedBluetoothDevice cachedDevice, int state) {
        if (DEBUG) {
            Log.d(TAG, "ACLConnectionStateChanged=" + cachedDevice.getAddress() + " " + stateToString(state));
        }
        this.mCachedState.remove(cachedDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    private ActuallyCachedState getCachedState(CachedBluetoothDevice device) {
        ActuallyCachedState state = this.mCachedState.get(device);
        if (state == null) {
            ActuallyCachedState state2 = new ActuallyCachedState(device, this.mHandler);
            this.mBgHandler.post(state2);
            this.mCachedState.put(device, state2);
            return state2;
        }
        return state;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener
    public void onServiceConnected() {
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener
    public void onServiceDisconnected() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class ActuallyCachedState implements Runnable {
        private int mBondState;
        private final WeakReference<CachedBluetoothDevice> mDevice;
        private int mMaxConnectionState;
        private final Handler mUiHandler;

        private ActuallyCachedState(CachedBluetoothDevice device, Handler uiHandler) {
            this.mBondState = 10;
            this.mMaxConnectionState = 0;
            this.mDevice = new WeakReference<>(device);
            this.mUiHandler = uiHandler;
        }

        @Override // java.lang.Runnable
        public void run() {
            CachedBluetoothDevice device = this.mDevice.get();
            if (device != null) {
                this.mBondState = device.getBondState();
                this.mMaxConnectionState = device.getMaxConnectionState();
                this.mUiHandler.removeMessages(1);
                this.mUiHandler.sendEmptyMessage(1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class H extends Handler {
        private static final int MSG_ADD_CALLBACK = 3;
        private static final int MSG_PAIRED_DEVICES_CHANGED = 1;
        private static final int MSG_REMOVE_CALLBACK = 4;
        private static final int MSG_STATE_CHANGED = 2;
        private final ArrayList<BluetoothController.Callback> mCallbacks;

        public H(Looper looper) {
            super(looper);
            this.mCallbacks = new ArrayList<>();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                firePairedDevicesChanged();
            } else if (i == 2) {
                fireStateChange();
            } else if (i == 3) {
                this.mCallbacks.add((BluetoothController.Callback) msg.obj);
            } else if (i == 4) {
                this.mCallbacks.remove((BluetoothController.Callback) msg.obj);
            }
        }

        private void firePairedDevicesChanged() {
            Iterator<BluetoothController.Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                BluetoothController.Callback cb = it.next();
                cb.onBluetoothDevicesChanged();
            }
        }

        private void fireStateChange() {
            Iterator<BluetoothController.Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                BluetoothController.Callback cb = it.next();
                fireStateChange(cb);
            }
        }

        private void fireStateChange(BluetoothController.Callback cb) {
            cb.onBluetoothStateChange(BluetoothControllerImpl.this.mEnabled);
        }
    }
}
