package com.xiaopeng.systemui.infoflow.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.systemui.I3DEngineService;
import com.android.systemui.IFaceAngleListener;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.service.Xp3DEngineService;
/* loaded from: classes24.dex */
public class FaceAngleManager {
    private static final String TAG = "FaceAngleManager";
    private static volatile FaceAngleManager mInstance;
    private FaceAngleListener mFaceAngleListener;
    private I3DEngineService mI3DEngineService;
    private float minYawValue = 5.0f;
    private float maxYawValue = 40.0f;
    private float minPitchValue = 10.0f;
    private float maxPitchValue = 40.0f;
    private float maxXAngleValue = 10.0f;
    private float maxYAngleValue = 10.0f;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.systemui.infoflow.manager.FaceAngleManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FaceAngleManager.this.mI3DEngineService = I3DEngineService.Stub.asInterface(iBinder);
            try {
                Logger.d(FaceAngleManager.TAG, "bind onServiceConnected");
                FaceAngleManager.this.mI3DEngineService.registeFaceAngleListener(FaceAngleManager.this.mIFaceAngleListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            FaceAngleManager.this.mI3DEngineService = null;
        }
    };
    private IFaceAngleListener.Stub mIFaceAngleListener = new IFaceAngleListener.Stub() { // from class: com.xiaopeng.systemui.infoflow.manager.FaceAngleManager.2
        @Override // com.android.systemui.IFaceAngleListener
        public void onFaceAngleChanged(int status, float yawl, float roll, float pitch) throws RemoteException {
            boolean angleEnable = InfoFlowConfigDao.getInstance().getConfig().angleCardEnable;
            if (angleEnable) {
                Logger.d(FaceAngleManager.TAG, "angleCardFeature enabled");
                FaceAngleManager.this.onXpFaceAngleChanged(status, yawl, roll, pitch);
            }
        }
    };

    /* loaded from: classes24.dex */
    public interface FaceAngleListener {
        void onFaceAngleChanged(float f, float f2);
    }

    private FaceAngleManager() {
    }

    public static FaceAngleManager getInstance() {
        if (mInstance == null) {
            synchronized (FaceAngleManager.class) {
                if (mInstance == null) {
                    mInstance = new FaceAngleManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        Logger.d(TAG, "bind Xp3DEngineService");
        Intent intent = new Intent(context, Xp3DEngineService.class);
        context.bindService(intent, this.mServiceConnection, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onXpFaceAngleChanged(int status, float yawl, float roll, float pitch) {
        Logger.d(TAG, String.format("onXpFaceAngleChanged %d %f %f %f", Integer.valueOf(status), Float.valueOf(yawl), Float.valueOf(roll), Float.valueOf(pitch)));
        if (this.mFaceAngleListener == null) {
            return;
        }
        float[] result = new float[2];
        processRawData(status, yawl, roll, pitch, result);
        FaceAngleListener faceAngleListener = this.mFaceAngleListener;
        if (faceAngleListener != null) {
            faceAngleListener.onFaceAngleChanged(result[0], result[1]);
        }
    }

    private void processRawData(int status, float yawl, float roll, float pitch, float[] result) {
        result[0] = 0.0f;
        result[1] = 0.0f;
        if (yawl > (-this.minYawValue) || yawl < (-this.maxYawValue)) {
            result[0] = 0.0f;
            result[1] = 0.0f;
            return;
        }
        float abs = Math.abs(yawl);
        float f = this.minYawValue;
        float x = ((abs - f) / (this.maxYawValue - f)) * this.maxXAngleValue;
        float y = 0.0f;
        float f2 = this.minPitchValue;
        if (pitch >= f2) {
            float f3 = this.maxPitchValue;
            if (pitch <= f3) {
                y = ((pitch - f2) / (f3 - f2)) * this.maxYAngleValue;
            }
        }
        result[0] = x;
        result[1] = y;
    }

    public void registerFaceAngleListener(FaceAngleListener faceAngleListener) {
        this.mFaceAngleListener = faceAngleListener;
    }
}
