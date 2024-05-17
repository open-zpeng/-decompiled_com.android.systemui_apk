package com.xiaopeng.speech.coreapi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.speech.coreapi.IEventObserver;
/* loaded from: classes23.dex */
public interface ISubscriber extends IInterface {
    boolean isSubscribed(String str) throws RemoteException;

    void subscribe(String[] strArr, IEventObserver iEventObserver) throws RemoteException;

    void unSubscribe(IEventObserver iEventObserver) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements ISubscriber {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.coreapi.ISubscriber";
        static final int TRANSACTION_isSubscribed = 3;
        static final int TRANSACTION_subscribe = 1;
        static final int TRANSACTION_unSubscribe = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISubscriber asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISubscriber)) {
                return (ISubscriber) iin;
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
                String[] _arg0 = data.createStringArray();
                IEventObserver _arg1 = IEventObserver.Stub.asInterface(data.readStrongBinder());
                subscribe(_arg0, _arg1);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                IEventObserver _arg02 = IEventObserver.Stub.asInterface(data.readStrongBinder());
                unSubscribe(_arg02);
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
                String _arg03 = data.readString();
                boolean isSubscribed = isSubscribed(_arg03);
                reply.writeNoException();
                reply.writeInt(isSubscribed ? 1 : 0);
                return true;
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements ISubscriber {
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

            @Override // com.xiaopeng.speech.coreapi.ISubscriber
            public void subscribe(String[] command, IEventObserver eventObserver) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(command);
                    _data.writeStrongBinder(eventObserver != null ? eventObserver.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ISubscriber
            public void unSubscribe(IEventObserver eventObserver) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(eventObserver != null ? eventObserver.asBinder() : null);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ISubscriber
            public boolean isSubscribed(String event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
