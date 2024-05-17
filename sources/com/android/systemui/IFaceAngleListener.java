package com.android.systemui;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes21.dex */
public interface IFaceAngleListener extends IInterface {
    void onFaceAngleChanged(int i, float f, float f2, float f3) throws RemoteException;

    /* loaded from: classes21.dex */
    public static class Default implements IFaceAngleListener {
        @Override // com.android.systemui.IFaceAngleListener
        public void onFaceAngleChanged(int status, float yawl, float roll, float pitch) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes21.dex */
    public static abstract class Stub extends Binder implements IFaceAngleListener {
        private static final String DESCRIPTOR = "com.android.systemui.IFaceAngleListener";
        static final int TRANSACTION_onFaceAngleChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFaceAngleListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFaceAngleListener)) {
                return (IFaceAngleListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code != 1) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            }
            data.enforceInterface(DESCRIPTOR);
            int _arg0 = data.readInt();
            float _arg1 = data.readFloat();
            float _arg2 = data.readFloat();
            float _arg3 = data.readFloat();
            onFaceAngleChanged(_arg0, _arg1, _arg2, _arg3);
            reply.writeNoException();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public static class Proxy implements IFaceAngleListener {
            public static IFaceAngleListener sDefaultImpl;
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

            @Override // com.android.systemui.IFaceAngleListener
            public void onFaceAngleChanged(int status, float yawl, float roll, float pitch) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeFloat(yawl);
                    _data.writeFloat(roll);
                    _data.writeFloat(pitch);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onFaceAngleChanged(status, yawl, roll, pitch);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IFaceAngleListener impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IFaceAngleListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
