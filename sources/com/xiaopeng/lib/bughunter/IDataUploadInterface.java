package com.xiaopeng.lib.bughunter;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
/* loaded from: classes22.dex */
public interface IDataUploadInterface extends IInterface {
    void uploadCan(String str) throws RemoteException;

    void uploadFiles(List<String> list) throws RemoteException;

    void uploadLog(String str) throws RemoteException;

    void uploadLogImmediately(String str, String str2) throws RemoteException;

    void uploadLogOrigin(String str, String str2) throws RemoteException;

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IDataUploadInterface {
        private static final String DESCRIPTOR = "com.xiaopeng.lib.bughunter.IDataUploadInterface";
        static final int TRANSACTION_uploadCan = 3;
        static final int TRANSACTION_uploadFiles = 5;
        static final int TRANSACTION_uploadLog = 1;
        static final int TRANSACTION_uploadLogImmediately = 4;
        static final int TRANSACTION_uploadLogOrigin = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDataUploadInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDataUploadInterface)) {
                return (IDataUploadInterface) iin;
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
                uploadLog(_arg0);
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                String _arg02 = data.readString();
                String _arg1 = data.readString();
                uploadLogOrigin(_arg02, _arg1);
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                String _arg03 = data.readString();
                uploadCan(_arg03);
                return true;
            } else if (code == 4) {
                data.enforceInterface(DESCRIPTOR);
                String _arg04 = data.readString();
                String _arg12 = data.readString();
                uploadLogImmediately(_arg04, _arg12);
                return true;
            } else if (code != 5) {
                if (code == 1598968902) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            } else {
                data.enforceInterface(DESCRIPTOR);
                List<String> _arg05 = data.createStringArrayList();
                uploadFiles(_arg05);
                return true;
            }
        }

        /* loaded from: classes22.dex */
        private static class Proxy implements IDataUploadInterface {
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

            @Override // com.xiaopeng.lib.bughunter.IDataUploadInterface
            public void uploadLog(String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(data);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.lib.bughunter.IDataUploadInterface
            public void uploadLogOrigin(String eventName, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(eventName);
                    _data.writeString(data);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.lib.bughunter.IDataUploadInterface
            public void uploadCan(String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(data);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.lib.bughunter.IDataUploadInterface
            public void uploadLogImmediately(String eventName, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(eventName);
                    _data.writeString(data);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.lib.bughunter.IDataUploadInterface
            public void uploadFiles(List<String> filePaths) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(filePaths);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
