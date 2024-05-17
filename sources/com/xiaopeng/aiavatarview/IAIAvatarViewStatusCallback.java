package com.xiaopeng.aiavatarview;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes22.dex */
public interface IAIAvatarViewStatusCallback extends IInterface {
    void enterFullBodyMode() throws RemoteException;

    void exitFullBodyMode() throws RemoteException;

    void onAvatarStateChanged(int i) throws RemoteException;

    void onSkinUpdate(String str) throws RemoteException;

    /* loaded from: classes22.dex */
    public static class Default implements IAIAvatarViewStatusCallback {
        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void enterFullBodyMode() throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void exitFullBodyMode() throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void onSkinUpdate(String snapshotPath) throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void onAvatarStateChanged(int state) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IAIAvatarViewStatusCallback {
        private static final String DESCRIPTOR = "com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback";
        static final int TRANSACTION_enterFullBodyMode = 1;
        static final int TRANSACTION_exitFullBodyMode = 2;
        static final int TRANSACTION_onAvatarStateChanged = 4;
        static final int TRANSACTION_onSkinUpdate = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAIAvatarViewStatusCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAIAvatarViewStatusCallback)) {
                return (IAIAvatarViewStatusCallback) iin;
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
                enterFullBodyMode();
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                exitFullBodyMode();
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                String _arg0 = data.readString();
                onSkinUpdate(_arg0);
                reply.writeNoException();
                return true;
            } else if (code != 4) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            } else {
                data.enforceInterface(DESCRIPTOR);
                int _arg02 = data.readInt();
                onAvatarStateChanged(_arg02);
                reply.writeNoException();
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes22.dex */
        public static class Proxy implements IAIAvatarViewStatusCallback {
            public static IAIAvatarViewStatusCallback sDefaultImpl;
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

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
            public void enterFullBodyMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().enterFullBodyMode();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
            public void exitFullBodyMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().exitFullBodyMode();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
            public void onSkinUpdate(String snapshotPath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(snapshotPath);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onSkinUpdate(snapshotPath);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
            public void onAvatarStateChanged(int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAvatarStateChanged(state);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IAIAvatarViewStatusCallback impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IAIAvatarViewStatusCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
