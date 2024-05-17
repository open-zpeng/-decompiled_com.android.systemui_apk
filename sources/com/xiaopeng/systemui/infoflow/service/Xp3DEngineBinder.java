package com.xiaopeng.systemui.infoflow.service;

import android.os.RemoteException;
import com.android.systemui.I3DEngineService;
import com.android.systemui.IFaceAngleListener;
/* loaded from: classes24.dex */
public class Xp3DEngineBinder extends I3DEngineService.Stub {
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
