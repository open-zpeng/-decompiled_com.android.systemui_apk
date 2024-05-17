package com.android.systemui.shared.recents;

import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MotionEvent;
import com.android.systemui.shared.recents.ISystemUiProxy;
/* loaded from: classes21.dex */
public interface IOverviewProxy extends IInterface {
    void onActiveNavBarRegionChanges(Region region) throws RemoteException;

    void onAssistantAvailable(boolean z) throws RemoteException;

    void onAssistantVisibilityChanged(float f) throws RemoteException;

    void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException;

    void onBind(ISystemUiProxy iSystemUiProxy) throws RemoteException;

    void onInitialize(Bundle bundle) throws RemoteException;

    void onMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void onOverviewHidden(boolean z, boolean z2) throws RemoteException;

    void onOverviewShown(boolean z) throws RemoteException;

    void onOverviewToggle() throws RemoteException;

    void onPreMotionEvent(int i) throws RemoteException;

    void onQuickScrubEnd() throws RemoteException;

    void onQuickScrubProgress(float f) throws RemoteException;

    void onQuickScrubStart() throws RemoteException;

    void onQuickStep(MotionEvent motionEvent) throws RemoteException;

    void onSystemUiStateChanged(int i) throws RemoteException;

    void onTip(int i, int i2) throws RemoteException;

    /* loaded from: classes21.dex */
    public static class Default implements IOverviewProxy {
        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onActiveNavBarRegionChanges(Region activeRegion) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onInitialize(Bundle params) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onBind(ISystemUiProxy sysUiProxy) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onPreMotionEvent(int downHitTarget) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onMotionEvent(MotionEvent event) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onQuickScrubStart() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onQuickScrubEnd() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onQuickScrubProgress(float progress) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewToggle() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewShown(boolean triggeredFromAltTab) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewHidden(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onQuickStep(MotionEvent event) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onTip(int actionType, int viewType) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onAssistantAvailable(boolean available) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onAssistantVisibilityChanged(float visibility) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onBackAction(boolean completed, int downX, int downY, boolean isButton, boolean gestureSwipeLeft) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSystemUiStateChanged(int stateFlags) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes21.dex */
    public static abstract class Stub extends Binder implements IOverviewProxy {
        private static final String DESCRIPTOR = "com.android.systemui.shared.recents.IOverviewProxy";
        static final int TRANSACTION_onActiveNavBarRegionChanges = 12;
        static final int TRANSACTION_onAssistantAvailable = 14;
        static final int TRANSACTION_onAssistantVisibilityChanged = 15;
        static final int TRANSACTION_onBackAction = 16;
        static final int TRANSACTION_onBind = 1;
        static final int TRANSACTION_onInitialize = 13;
        static final int TRANSACTION_onMotionEvent = 3;
        static final int TRANSACTION_onOverviewHidden = 9;
        static final int TRANSACTION_onOverviewShown = 8;
        static final int TRANSACTION_onOverviewToggle = 7;
        static final int TRANSACTION_onPreMotionEvent = 2;
        static final int TRANSACTION_onQuickScrubEnd = 5;
        static final int TRANSACTION_onQuickScrubProgress = 6;
        static final int TRANSACTION_onQuickScrubStart = 4;
        static final int TRANSACTION_onQuickStep = 10;
        static final int TRANSACTION_onSystemUiStateChanged = 17;
        static final int TRANSACTION_onTip = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOverviewProxy asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IOverviewProxy)) {
                return (IOverviewProxy) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            MotionEvent _arg0;
            boolean _arg02;
            MotionEvent _arg03;
            Region _arg04;
            Bundle _arg05;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    onBind(ISystemUiProxy.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    onPreMotionEvent(data.readInt());
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (MotionEvent) MotionEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onMotionEvent(_arg0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    onQuickScrubStart();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    onQuickScrubEnd();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    onQuickScrubProgress(data.readFloat());
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    onOverviewToggle();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readInt() != 0;
                    onOverviewShown(_arg02);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg06 = data.readInt() != 0;
                    _arg02 = data.readInt() != 0;
                    onOverviewHidden(_arg06, _arg02);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (MotionEvent) MotionEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    onQuickStep(_arg03);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg1 = data.readInt();
                    onTip(_arg07, _arg1);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg04 = (Region) Region.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    onActiveNavBarRegionChanges(_arg04);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    onInitialize(_arg05);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readInt() != 0;
                    onAssistantAvailable(_arg02);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    onAssistantVisibilityChanged(data.readFloat());
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg08 = data.readInt() != 0;
                    int _arg12 = data.readInt();
                    int _arg2 = data.readInt();
                    boolean _arg3 = data.readInt() != 0;
                    boolean _arg4 = data.readInt() != 0;
                    onBackAction(_arg08, _arg12, _arg2, _arg3, _arg4);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    onSystemUiStateChanged(data.readInt());
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public static class Proxy implements IOverviewProxy {
            public static IOverviewProxy sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onActiveNavBarRegionChanges(Region activeRegion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (activeRegion != null) {
                        _data.writeInt(1);
                        activeRegion.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onActiveNavBarRegionChanges(activeRegion);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onInitialize(Bundle params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (params != null) {
                        _data.writeInt(1);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onInitialize(params);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onBind(ISystemUiProxy sysUiProxy) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(sysUiProxy != null ? sysUiProxy.asBinder() : null);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onBind(sysUiProxy);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onPreMotionEvent(int downHitTarget) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(downHitTarget);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onPreMotionEvent(downHitTarget);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onMotionEvent(MotionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onMotionEvent(event);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onQuickScrubStart() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onQuickScrubStart();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onQuickScrubEnd() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onQuickScrubEnd();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onQuickScrubProgress(float progress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(progress);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onQuickScrubProgress(progress);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewToggle() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOverviewToggle();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewShown(boolean triggeredFromAltTab) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(triggeredFromAltTab ? 1 : 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOverviewShown(triggeredFromAltTab);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewHidden(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(triggeredFromAltTab ? 1 : 0);
                    _data.writeInt(triggeredFromHomeKey ? 1 : 0);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOverviewHidden(triggeredFromAltTab, triggeredFromHomeKey);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onQuickStep(MotionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onQuickStep(event);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onTip(int actionType, int viewType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actionType);
                    _data.writeInt(viewType);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onTip(actionType, viewType);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onAssistantAvailable(boolean available) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(available ? 1 : 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAssistantAvailable(available);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onAssistantVisibilityChanged(float visibility) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(visibility);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAssistantVisibilityChanged(visibility);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onBackAction(boolean completed, int downX, int downY, boolean isButton, boolean gestureSwipeLeft) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(completed ? 1 : 0);
                    _data.writeInt(downX);
                    _data.writeInt(downY);
                    _data.writeInt(isButton ? 1 : 0);
                    _data.writeInt(gestureSwipeLeft ? 1 : 0);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onBackAction(completed, downX, downY, isButton, gestureSwipeLeft);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onSystemUiStateChanged(int stateFlags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(stateFlags);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onSystemUiStateChanged(stateFlags);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IOverviewProxy impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IOverviewProxy getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
