package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.FlashlightController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class FlashlightControllerImpl implements FlashlightController {
    private static final String ACTION_FLASHLIGHT_CHANGED = "com.android.settings.flashlight.action.FLASHLIGHT_CHANGED";
    private static final int DISPATCH_AVAILABILITY_CHANGED = 2;
    private static final int DISPATCH_CHANGED = 1;
    private static final int DISPATCH_ERROR = 0;
    private String mCameraId;
    private final CameraManager mCameraManager;
    private final Context mContext;
    private boolean mFlashlightEnabled;
    private Handler mHandler;
    private boolean mTorchAvailable;
    private static final String TAG = "FlashlightController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final ArrayList<WeakReference<FlashlightController.FlashlightListener>> mListeners = new ArrayList<>(1);
    private final CameraManager.TorchCallback mTorchCallback = new CameraManager.TorchCallback() { // from class: com.android.systemui.statusbar.policy.FlashlightControllerImpl.1
        @Override // android.hardware.camera2.CameraManager.TorchCallback
        public void onTorchModeUnavailable(String cameraId) {
            if (TextUtils.equals(cameraId, FlashlightControllerImpl.this.mCameraId)) {
                setCameraAvailable(false);
                Settings.Secure.putInt(FlashlightControllerImpl.this.mContext.getContentResolver(), "flashlight_available", 0);
            }
        }

        @Override // android.hardware.camera2.CameraManager.TorchCallback
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (TextUtils.equals(cameraId, FlashlightControllerImpl.this.mCameraId)) {
                setCameraAvailable(true);
                setTorchMode(enabled);
                Settings.Secure.putInt(FlashlightControllerImpl.this.mContext.getContentResolver(), "flashlight_available", 1);
                Settings.Secure.putInt(FlashlightControllerImpl.this.mContext.getContentResolver(), "flashlight_enabled", enabled ? 1 : 0);
                FlashlightControllerImpl.this.mContext.sendBroadcast(new Intent(FlashlightControllerImpl.ACTION_FLASHLIGHT_CHANGED));
            }
        }

        private void setCameraAvailable(boolean available) {
            boolean changed;
            synchronized (FlashlightControllerImpl.this) {
                changed = FlashlightControllerImpl.this.mTorchAvailable != available;
                FlashlightControllerImpl.this.mTorchAvailable = available;
            }
            if (changed) {
                if (FlashlightControllerImpl.DEBUG) {
                    Log.d(FlashlightControllerImpl.TAG, "dispatchAvailabilityChanged(" + available + NavigationBarInflaterView.KEY_CODE_END);
                }
                FlashlightControllerImpl.this.dispatchAvailabilityChanged(available);
            }
        }

        private void setTorchMode(boolean enabled) {
            boolean changed;
            synchronized (FlashlightControllerImpl.this) {
                changed = FlashlightControllerImpl.this.mFlashlightEnabled != enabled;
                FlashlightControllerImpl.this.mFlashlightEnabled = enabled;
            }
            if (changed) {
                if (FlashlightControllerImpl.DEBUG) {
                    Log.d(FlashlightControllerImpl.TAG, "dispatchModeChanged(" + enabled + NavigationBarInflaterView.KEY_CODE_END);
                }
                FlashlightControllerImpl.this.dispatchModeChanged(enabled);
            }
        }
    };

    @Inject
    public FlashlightControllerImpl(Context context) {
        this.mContext = context;
        this.mCameraManager = (CameraManager) this.mContext.getSystemService("camera");
        tryInitCamera();
    }

    private void tryInitCamera() {
        try {
            this.mCameraId = getCameraId();
            if (this.mCameraId != null) {
                ensureHandler();
                this.mCameraManager.registerTorchCallback(this.mTorchCallback, this.mHandler);
            }
        } catch (Throwable e) {
            Log.e(TAG, "Couldn't initialize.", e);
        }
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController
    public void setFlashlight(boolean enabled) {
        boolean pendingError = false;
        synchronized (this) {
            if (this.mCameraId == null) {
                return;
            }
            if (this.mFlashlightEnabled != enabled) {
                this.mFlashlightEnabled = enabled;
                try {
                    this.mCameraManager.setTorchMode(this.mCameraId, enabled);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Couldn't set torch mode", e);
                    this.mFlashlightEnabled = false;
                    pendingError = true;
                }
            }
            dispatchModeChanged(this.mFlashlightEnabled);
            if (pendingError) {
                dispatchError();
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController
    public boolean hasFlashlight() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController
    public synchronized boolean isEnabled() {
        return this.mFlashlightEnabled;
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController
    public synchronized boolean isAvailable() {
        return this.mTorchAvailable;
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(FlashlightController.FlashlightListener l) {
        synchronized (this.mListeners) {
            if (this.mCameraId == null) {
                tryInitCamera();
            }
            cleanUpListenersLocked(l);
            this.mListeners.add(new WeakReference<>(l));
            l.onFlashlightAvailabilityChanged(this.mTorchAvailable);
            l.onFlashlightChanged(this.mFlashlightEnabled);
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(FlashlightController.FlashlightListener l) {
        synchronized (this.mListeners) {
            cleanUpListenersLocked(l);
        }
    }

    private synchronized void ensureHandler() {
        if (this.mHandler == null) {
            HandlerThread thread = new HandlerThread(TAG, 10);
            thread.start();
            this.mHandler = new Handler(thread.getLooper());
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = this.mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = this.mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = (Boolean) c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = (Integer) c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable.booleanValue() && lensFacing != null && lensFacing.intValue() == 1) {
                return id;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchModeChanged(boolean enabled) {
        dispatchListeners(1, enabled);
    }

    private void dispatchError() {
        dispatchListeners(1, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchAvailabilityChanged(boolean available) {
        dispatchListeners(2, available);
    }

    private void dispatchListeners(int message, boolean argument) {
        synchronized (this.mListeners) {
            int N = this.mListeners.size();
            boolean cleanup = false;
            for (int i = 0; i < N; i++) {
                FlashlightController.FlashlightListener l = this.mListeners.get(i).get();
                if (l != null) {
                    if (message == 0) {
                        l.onFlashlightError();
                    } else if (message == 1) {
                        l.onFlashlightChanged(argument);
                    } else if (message == 2) {
                        l.onFlashlightAvailabilityChanged(argument);
                    }
                } else {
                    cleanup = true;
                }
            }
            if (cleanup) {
                cleanUpListenersLocked(null);
            }
        }
    }

    private void cleanUpListenersLocked(FlashlightController.FlashlightListener listener) {
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            FlashlightController.FlashlightListener found = this.mListeners.get(i).get();
            if (found == null || found == listener) {
                this.mListeners.remove(i);
            }
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("FlashlightController state:");
        pw.print("  mCameraId=");
        pw.println(this.mCameraId);
        pw.print("  mFlashlightEnabled=");
        pw.println(this.mFlashlightEnabled);
        pw.print("  mTorchAvailable=");
        pw.println(this.mTorchAvailable);
    }
}
