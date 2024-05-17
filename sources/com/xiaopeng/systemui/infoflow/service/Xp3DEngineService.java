package com.xiaopeng.systemui.infoflow.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.systemui.I3DEngineService;
import com.android.systemui.IFaceAngleListener;
/* loaded from: classes24.dex */
public class Xp3DEngineService extends Service {
    private static final String TAG = "Xp3DEngineService";
    private Xp3DEngineBinder mXp3DEngineBinder = new Xp3DEngineBinder(this);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mXp3DEngineBinder;
    }

    /* loaded from: classes24.dex */
    class Xp3DEngineBinder extends I3DEngineService.Stub {
        private IFaceAngleListener mIFaceAngleListener;
        private Xp3DEngineService mXp3DEngineService;

        public Xp3DEngineBinder(Xp3DEngineService service) {
            this.mXp3DEngineService = service;
        }

        @Override // com.android.systemui.I3DEngineService
        public void updateFaceAngle(int status, float yaw, float roll, float pitch) throws RemoteException {
            this.mIFaceAngleListener.onFaceAngleChanged(status, yaw, roll, pitch);
        }

        @Override // com.android.systemui.I3DEngineService
        public void registeFaceAngleListener(IFaceAngleListener listener) throws RemoteException {
            this.mIFaceAngleListener = listener;
        }

        @Override // com.android.systemui.I3DEngineService
        public void unRegisteFaceAngleListener(IFaceAngleListener listener) throws RemoteException {
            this.mIFaceAngleListener = null;
        }
    }
}
