package com.xiaopeng.speech;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.speech.common.bean.Value;
/* loaded from: classes23.dex */
public interface IRemoteDataSensor extends IInterface {
    Value onQuery(String str, String str2) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements IRemoteDataSensor {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.IRemoteDataSensor";
        static final int TRANSACTION_onQuery = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRemoteDataSensor asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRemoteDataSensor)) {
                return (IRemoteDataSensor) iin;
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
            String _arg0 = data.readString();
            String _arg1 = data.readString();
            Value _result = onQuery(_arg0, _arg1);
            reply.writeNoException();
            if (_result != null) {
                reply.writeInt(1);
                _result.writeToParcel(reply, 1);
            } else {
                reply.writeInt(0);
            }
            return true;
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements IRemoteDataSensor {
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

            @Override // com.xiaopeng.speech.IRemoteDataSensor
            public Value onQuery(String dataApi, String data) throws RemoteException {
                Value _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dataApi);
                    _data.writeString(data);
                    this.mRemote.transact(1, _data, _reply, 0);
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
