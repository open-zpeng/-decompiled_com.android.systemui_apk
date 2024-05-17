package com.xiaopeng.appstore.storeprovider.store;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceDownloadInfo;
/* loaded from: classes22.dex */
public interface IRMDownloadCallback extends IInterface {
    void basicTypes(int i, long j, boolean z, float f, double d, String str) throws RemoteException;

    void onDownloadCallback(int i, ResourceDownloadInfo resourceDownloadInfo) throws RemoteException;

    void onMenuOpenCallback(String str) throws RemoteException;

    void unbindService() throws RemoteException;

    /* loaded from: classes22.dex */
    public static class Default implements IRMDownloadCallback {
        @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
        public void onDownloadCallback(int type, ResourceDownloadInfo resourceDownloadInfo) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
        public void onMenuOpenCallback(String resId) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
        public void unbindService() throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IRMDownloadCallback {
        private static final String DESCRIPTOR = "com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback";
        static final int TRANSACTION_basicTypes = 1;
        static final int TRANSACTION_onDownloadCallback = 2;
        static final int TRANSACTION_onMenuOpenCallback = 3;
        static final int TRANSACTION_unbindService = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRMDownloadCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRMDownloadCallback)) {
                return (IRMDownloadCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ResourceDownloadInfo _arg1;
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                int _arg0 = data.readInt();
                long _arg12 = data.readLong();
                boolean _arg2 = data.readInt() != 0;
                float _arg3 = data.readFloat();
                double _arg4 = data.readDouble();
                String _arg5 = data.readString();
                basicTypes(_arg0, _arg12, _arg2, _arg3, _arg4, _arg5);
                reply.writeNoException();
                return true;
            } else if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                int _arg02 = data.readInt();
                if (data.readInt() != 0) {
                    _arg1 = ResourceDownloadInfo.CREATOR.createFromParcel(data);
                } else {
                    _arg1 = null;
                }
                onDownloadCallback(_arg02, _arg1);
                reply.writeNoException();
                return true;
            } else if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                String _arg03 = data.readString();
                onMenuOpenCallback(_arg03);
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
                unbindService();
                reply.writeNoException();
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes22.dex */
        public static class Proxy implements IRMDownloadCallback {
            public static IRMDownloadCallback sDefaultImpl;
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

            @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    try {
                        _data.writeInt(anInt);
                        try {
                            _data.writeLong(aLong);
                            _data.writeInt(aBoolean ? 1 : 0);
                            try {
                                _data.writeFloat(aFloat);
                                _data.writeDouble(aDouble);
                                _data.writeString(aString);
                                boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                                if (!_status && Stub.getDefaultImpl() != null) {
                                    Stub.getDefaultImpl().basicTypes(anInt, aLong, aBoolean, aFloat, aDouble, aString);
                                    _reply.recycle();
                                    _data.recycle();
                                    return;
                                }
                                _reply.readException();
                                _reply.recycle();
                                _data.recycle();
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
            public void onDownloadCallback(int type, ResourceDownloadInfo resourceDownloadInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    if (resourceDownloadInfo != null) {
                        _data.writeInt(1);
                        resourceDownloadInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onDownloadCallback(type, resourceDownloadInfo);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
            public void onMenuOpenCallback(String resId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resId);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onMenuOpenCallback(resId);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback
            public void unbindService() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unbindService();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IRMDownloadCallback impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IRMDownloadCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
