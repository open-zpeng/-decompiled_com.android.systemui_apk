package com.xiaopeng.appstore.storeprovider;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.appstore.storeprovider.IAssembleListener;
import com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp;
import java.util.List;
/* loaded from: classes22.dex */
public interface IResourceServiceV2 extends IInterface {
    AssembleResult assembleAction(String str, SimpleRequest simpleRequest) throws RemoteException;

    AssembleResult assembleEnqueue(String str, EnqueueRequest enqueueRequest) throws RemoteException;

    List<AssembleInfo> getAssembleInfoList(int i, String str) throws RemoteException;

    ResourceContainer query(String str, ResourceRequest resourceRequest) throws RemoteException;

    AppGroupsResp queryAppGroups(int[] iArr) throws RemoteException;

    void registerAssembleListener(int i, String str, IAssembleListener iAssembleListener) throws RemoteException;

    void unregisterAssembleListener(IAssembleListener iAssembleListener) throws RemoteException;

    /* loaded from: classes22.dex */
    public static class Default implements IResourceServiceV2 {
        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public AssembleResult assembleEnqueue(String callingPackage, EnqueueRequest assembleRequest) throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public AssembleResult assembleAction(String callingPackage, SimpleRequest assembleRequest) throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public List<AssembleInfo> getAssembleInfoList(int resType, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public void registerAssembleListener(int resType, String callingPackage, IAssembleListener listener) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public void unregisterAssembleListener(IAssembleListener listener) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public ResourceContainer query(String callingPackage, ResourceRequest request) throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
        public AppGroupsResp queryAppGroups(int[] appGroupTypes) throws RemoteException {
            return null;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IResourceServiceV2 {
        private static final String DESCRIPTOR = "com.xiaopeng.appstore.storeprovider.IResourceServiceV2";
        static final int TRANSACTION_assembleAction = 2;
        static final int TRANSACTION_assembleEnqueue = 1;
        static final int TRANSACTION_getAssembleInfoList = 3;
        static final int TRANSACTION_query = 6;
        static final int TRANSACTION_queryAppGroups = 7;
        static final int TRANSACTION_registerAssembleListener = 4;
        static final int TRANSACTION_unregisterAssembleListener = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IResourceServiceV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IResourceServiceV2)) {
                return (IResourceServiceV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            EnqueueRequest _arg1;
            SimpleRequest _arg12;
            ResourceRequest _arg13;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = EnqueueRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    AssembleResult _result = assembleEnqueue(_arg0, _arg1);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    if (data.readInt() != 0) {
                        _arg12 = SimpleRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    AssembleResult _result2 = assembleAction(_arg02, _arg12);
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(1);
                        _result2.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    String _arg14 = data.readString();
                    List<AssembleInfo> _result3 = getAssembleInfoList(_arg03, _arg14);
                    reply.writeNoException();
                    reply.writeTypedList(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    String _arg15 = data.readString();
                    IAssembleListener _arg2 = IAssembleListener.Stub.asInterface(data.readStrongBinder());
                    registerAssembleListener(_arg04, _arg15, _arg2);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    IAssembleListener _arg05 = IAssembleListener.Stub.asInterface(data.readStrongBinder());
                    unregisterAssembleListener(_arg05);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    if (data.readInt() != 0) {
                        _arg13 = ResourceRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    ResourceContainer _result4 = query(_arg06, _arg13);
                    reply.writeNoException();
                    if (_result4 != null) {
                        reply.writeInt(1);
                        _result4.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg07 = data.createIntArray();
                    AppGroupsResp _result5 = queryAppGroups(_arg07);
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(1);
                        _result5.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes22.dex */
        public static class Proxy implements IResourceServiceV2 {
            public static IResourceServiceV2 sDefaultImpl;
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

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public AssembleResult assembleEnqueue(String callingPackage, EnqueueRequest assembleRequest) throws RemoteException {
                AssembleResult _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    if (assembleRequest != null) {
                        _data.writeInt(1);
                        assembleRequest.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().assembleEnqueue(callingPackage, assembleRequest);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = AssembleResult.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public AssembleResult assembleAction(String callingPackage, SimpleRequest assembleRequest) throws RemoteException {
                AssembleResult _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    if (assembleRequest != null) {
                        _data.writeInt(1);
                        assembleRequest.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().assembleAction(callingPackage, assembleRequest);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = AssembleResult.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public List<AssembleInfo> getAssembleInfoList(int resType, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(resType);
                    _data.writeString(callingPackage);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getAssembleInfoList(resType, callingPackage);
                    }
                    _reply.readException();
                    List<AssembleInfo> _result = _reply.createTypedArrayList(AssembleInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public void registerAssembleListener(int resType, String callingPackage, IAssembleListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(resType);
                    _data.writeString(callingPackage);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerAssembleListener(resType, callingPackage, listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public void unregisterAssembleListener(IAssembleListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterAssembleListener(listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public ResourceContainer query(String callingPackage, ResourceRequest request) throws RemoteException {
                ResourceContainer _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().query(callingPackage, request);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = ResourceContainer.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.IResourceServiceV2
            public AppGroupsResp queryAppGroups(int[] appGroupTypes) throws RemoteException {
                AppGroupsResp _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(appGroupTypes);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().queryAppGroups(appGroupTypes);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = AppGroupsResp.CREATOR.createFromParcel(_reply);
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

        public static boolean setDefaultImpl(IResourceServiceV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IResourceServiceV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
