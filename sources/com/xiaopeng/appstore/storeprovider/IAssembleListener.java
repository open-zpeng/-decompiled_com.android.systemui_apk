package com.xiaopeng.appstore.storeprovider;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes22.dex */
public interface IAssembleListener extends IInterface {
    void onAssembleEvent(int i, AssembleInfo assembleInfo) throws RemoteException;

    /* loaded from: classes22.dex */
    public static class Default implements IAssembleListener {
        @Override // com.xiaopeng.appstore.storeprovider.IAssembleListener
        public void onAssembleEvent(int eventType, AssembleInfo info) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IAssembleListener {
        private static final String DESCRIPTOR = "com.xiaopeng.appstore.storeprovider.IAssembleListener";
        static final int TRANSACTION_onAssembleEvent = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAssembleListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAssembleListener)) {
                return (IAssembleListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            AssembleInfo _arg1;
            if (code != 1) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            }
            data.enforceInterface(DESCRIPTOR);
            int _arg0 = data.readInt();
            if (data.readInt() != 0) {
                _arg1 = AssembleInfo.CREATOR.createFromParcel(data);
            } else {
                _arg1 = null;
            }
            onAssembleEvent(_arg0, _arg1);
            reply.writeNoException();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes22.dex */
        public static class Proxy implements IAssembleListener {
            public static IAssembleListener sDefaultImpl;
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

            @Override // com.xiaopeng.appstore.storeprovider.IAssembleListener
            public void onAssembleEvent(int eventType, AssembleInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(eventType);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAssembleEvent(eventType, info);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IAssembleListener impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IAssembleListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
