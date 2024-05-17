package com.xiaopeng.speech.asr;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes23.dex */
public interface IRecognizeListener extends IInterface {
    void onError(int i, String str) throws RemoteException;

    void onExtra(int i, int i2, int i3, String str, byte[] bArr) throws RemoteException;

    void onResult(String str, boolean z) throws RemoteException;

    void onState(int i, int i2) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements IRecognizeListener {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.asr.IRecognizeListener";
        static final int TRANSACTION_onError = 2;
        static final int TRANSACTION_onExtra = 4;
        static final int TRANSACTION_onResult = 1;
        static final int TRANSACTION_onState = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecognizeListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRecognizeListener)) {
                return (IRecognizeListener) iin;
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
                String _arg0 = data.readString();
                boolean _arg1 = data.readInt() != 0;
                onResult(_arg0, _arg1);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                int _arg02 = data.readInt();
                String _arg12 = data.readString();
                onError(_arg02, _arg12);
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                int _arg03 = data.readInt();
                int _arg13 = data.readInt();
                onState(_arg03, _arg13);
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
                int _arg04 = data.readInt();
                int _arg14 = data.readInt();
                int _arg2 = data.readInt();
                String _arg3 = data.readString();
                byte[] _arg4 = data.createByteArray();
                onExtra(_arg04, _arg14, _arg2, _arg3, _arg4);
                reply.writeNoException();
                return true;
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements IRecognizeListener {
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

            @Override // com.xiaopeng.speech.asr.IRecognizeListener
            public void onResult(String result, boolean last) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(result);
                    _data.writeInt(last ? 1 : 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.asr.IRecognizeListener
            public void onError(int code, String extraInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(extraInfo);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.asr.IRecognizeListener
            public void onState(int state, int extra) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(extra);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.asr.IRecognizeListener
            public void onExtra(int type, int arg1, int arg2, String info, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(arg1);
                    _data.writeInt(arg2);
                    _data.writeString(info);
                    _data.writeByteArray(data);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
