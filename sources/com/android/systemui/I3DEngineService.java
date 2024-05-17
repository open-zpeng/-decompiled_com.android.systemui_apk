package com.android.systemui;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.systemui.IFaceAngleListener;
/* loaded from: classes21.dex */
public interface I3DEngineService extends IInterface {
    void registeFaceAngleListener(IFaceAngleListener iFaceAngleListener) throws RemoteException;

    void unRegisteFaceAngleListener(IFaceAngleListener iFaceAngleListener) throws RemoteException;

    void updateFaceAngle(int i, float f, float f2, float f3) throws RemoteException;

    /* loaded from: classes21.dex */
    public static class Default implements I3DEngineService {
        @Override // com.android.systemui.I3DEngineService
        public void updateFaceAngle(int status, float yaw, float roll, float pitch) throws RemoteException {
        }

        @Override // com.android.systemui.I3DEngineService
        public void registeFaceAngleListener(IFaceAngleListener listener) throws RemoteException {
        }

        @Override // com.android.systemui.I3DEngineService
        public void unRegisteFaceAngleListener(IFaceAngleListener listener) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes21.dex */
    public static abstract class Stub extends Binder implements I3DEngineService {
        private static final String DESCRIPTOR = "com.android.systemui.I3DEngineService";
        static final int TRANSACTION_registeFaceAngleListener = 2;
        static final int TRANSACTION_unRegisteFaceAngleListener = 3;
        static final int TRANSACTION_updateFaceAngle = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static I3DEngineService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof I3DEngineService)) {
                return (I3DEngineService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                int _arg0 = data.readInt();
                float _arg1 = data.readFloat();
                float _arg2 = data.readFloat();
                float _arg3 = data.readFloat();
                updateFaceAngle(_arg0, _arg1, _arg2, _arg3);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                IFaceAngleListener _arg02 = IFaceAngleListener.Stub.asInterface(data.readStrongBinder());
                registeFaceAngleListener(_arg02);
                reply.writeNoException();
                return true;
            } else if (code != 3) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            } else {
                data.enforceInterface(DESCRIPTOR);
                IFaceAngleListener _arg03 = IFaceAngleListener.Stub.asInterface(data.readStrongBinder());
                unRegisteFaceAngleListener(_arg03);
                reply.writeNoException();
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public static class Proxy implements I3DEngineService {
            public static I3DEngineService sDefaultImpl;
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

            @Override // com.android.systemui.I3DEngineService
            public void updateFaceAngle(int status, float yaw, float roll, float pitch) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeFloat(yaw);
                    _data.writeFloat(roll);
                    _data.writeFloat(pitch);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().updateFaceAngle(status, yaw, roll, pitch);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.I3DEngineService
            public void registeFaceAngleListener(IFaceAngleListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registeFaceAngleListener(listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.systemui.I3DEngineService
            public void unRegisteFaceAngleListener(IFaceAngleListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unRegisteFaceAngleListener(listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(I3DEngineService impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static I3DEngineService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
