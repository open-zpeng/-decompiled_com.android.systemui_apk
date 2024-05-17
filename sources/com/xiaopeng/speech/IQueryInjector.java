package com.xiaopeng.speech;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.speech.IRemoteDataSensor;
import com.xiaopeng.speech.common.bean.Value;
/* loaded from: classes23.dex */
public interface IQueryInjector extends IInterface {
    boolean isQueryInject(String str) throws RemoteException;

    Value queryApiRouterData(String str, String str2) throws RemoteException;

    Value queryData(String str, String str2) throws RemoteException;

    void registerDataSensor(String[] strArr, IRemoteDataSensor iRemoteDataSensor) throws RemoteException;

    void unRegisterDataSensor(String[] strArr) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements IQueryInjector {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.IQueryInjector";
        static final int TRANSACTION_isQueryInject = 4;
        static final int TRANSACTION_queryApiRouterData = 5;
        static final int TRANSACTION_queryData = 3;
        static final int TRANSACTION_registerDataSensor = 1;
        static final int TRANSACTION_unRegisterDataSensor = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IQueryInjector asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IQueryInjector)) {
                return (IQueryInjector) iin;
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
                IRemoteDataSensor _arg1 = IRemoteDataSensor.Stub.asInterface(data.readStrongBinder());
                registerDataSensor(_arg0, _arg1);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                String[] _arg02 = data.createStringArray();
                unRegisterDataSensor(_arg02);
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                String _arg03 = data.readString();
                String _arg12 = data.readString();
                Value _result = queryData(_arg03, _arg12);
                reply.writeNoException();
                if (_result != null) {
                    reply.writeInt(1);
                    _result.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                String _arg04 = data.readString();
                boolean isQueryInject = isQueryInject(_arg04);
                reply.writeNoException();
                reply.writeInt(isQueryInject ? 1 : 0);
                return true;
            } else if (code != 5) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            } else {
                data.enforceInterface(DESCRIPTOR);
                String _arg05 = data.readString();
                String _arg13 = data.readString();
                Value _result2 = queryApiRouterData(_arg05, _arg13);
                reply.writeNoException();
                if (_result2 != null) {
                    reply.writeInt(1);
                    _result2.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements IQueryInjector {
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

            @Override // com.xiaopeng.speech.IQueryInjector
            public void registerDataSensor(String[] command, IRemoteDataSensor query) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(command);
                    _data.writeStrongBinder(query != null ? query.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.IQueryInjector
            public void unRegisterDataSensor(String[] command) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(command);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.IQueryInjector
            public Value queryData(String dataApi, String data) throws RemoteException {
                Value _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dataApi);
                    _data.writeString(data);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = Value.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.IQueryInjector
            public boolean isQueryInject(String event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.IQueryInjector
            public Value queryApiRouterData(String dataApi, String data) throws RemoteException {
                Value _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dataApi);
                    _data.writeString(data);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = Value.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
