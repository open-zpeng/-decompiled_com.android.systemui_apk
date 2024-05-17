package com.xiaopeng.speech.coreapi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes23.dex */
public interface IRecordEngine extends IInterface {
    void asr(String str, String str2) throws RemoteException;

    boolean pcm2Amr(String str, String str2, boolean z) throws RemoteException;

    void startRecord(String str, int i, int i2) throws RemoteException;

    void stopRecord() throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements IRecordEngine {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.coreapi.IRecordEngine";
        static final int TRANSACTION_asr = 3;
        static final int TRANSACTION_pcm2Amr = 4;
        static final int TRANSACTION_startRecord = 1;
        static final int TRANSACTION_stopRecord = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRecordEngine asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRecordEngine)) {
                return (IRecordEngine) iin;
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
                int _arg1 = data.readInt();
                int _arg2 = data.readInt();
                startRecord(_arg0, _arg1, _arg2);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                stopRecord();
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                String _arg02 = data.readString();
                String _arg12 = data.readString();
                asr(_arg02, _arg12);
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
                String _arg03 = data.readString();
                String _arg13 = data.readString();
                boolean _arg22 = data.readInt() != 0;
                boolean pcm2Amr = pcm2Amr(_arg03, _arg13, _arg22);
                reply.writeNoException();
                reply.writeInt(pcm2Amr ? 1 : 0);
                return true;
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements IRecordEngine {
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

            @Override // com.xiaopeng.speech.coreapi.IRecordEngine
            public void startRecord(String dstFilePath, int maxLengthMs, int eosTimeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dstFilePath);
                    _data.writeInt(maxLengthMs);
                    _data.writeInt(eosTimeout);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IRecordEngine
            public void stopRecord() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IRecordEngine
            public void asr(String token, String filePath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(token);
                    _data.writeString(filePath);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IRecordEngine
            public boolean pcm2Amr(String srcFilePath, String dstFileName, boolean deleteFileAfterSuc) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(srcFilePath);
                    _data.writeString(dstFileName);
                    _data.writeInt(deleteFileAfterSuc ? 1 : 0);
                    this.mRemote.transact(4, _data, _reply, 0);
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
