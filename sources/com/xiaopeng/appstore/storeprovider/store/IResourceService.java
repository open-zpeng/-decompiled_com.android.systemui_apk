package com.xiaopeng.appstore.storeprovider.store;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.appstore.storeprovider.store.IRMDownloadCallback;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceBean;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceContainerBean;
import com.xiaopeng.appstore.storeprovider.store.bean.ResourceDownloadInfo;
import java.util.List;
/* loaded from: classes22.dex */
public interface IResourceService extends IInterface {
    void basicTypes(int i, long j, boolean z, float f, double d, String str) throws RemoteException;

    boolean cancelDownload(String str) throws RemoteException;

    boolean cancelResDownload(String str, String str2) throws RemoteException;

    long enqueue(String str, String str2) throws RemoteException;

    int fetchDownloadStatusById(long j) throws RemoteException;

    int fetchDownloadStatusByUrl(String str) throws RemoteException;

    int getDownloadStatusById(long j) throws RemoteException;

    int getDownloadStatusByUrl(String str) throws RemoteException;

    long getDownloadedBytesById(long j) throws RemoteException;

    long getDownloadedBytesByUrl(String str) throws RemoteException;

    String getLocalFilePath(String str) throws RemoteException;

    long getTotalBytesById(long j) throws RemoteException;

    long getTotalBytesByUrl(String str) throws RemoteException;

    boolean pause(String str) throws RemoteException;

    boolean pauseDownload(String str) throws RemoteException;

    boolean pauseResDownload(String str, String str2) throws RemoteException;

    List<ResourceDownloadInfo> queryAllInfo() throws RemoteException;

    List<ResourceDownloadInfo> queryDownloadInfo(String[] strArr) throws RemoteException;

    ResourceContainerBean queryResourceData(String str) throws RemoteException;

    void registerDownloadListener(IRMDownloadCallback iRMDownloadCallback) throws RemoteException;

    boolean remove(String str) throws RemoteException;

    void removeLocalDataById(long j) throws RemoteException;

    void removeLocalDataByUrl(String str) throws RemoteException;

    boolean resume(String str) throws RemoteException;

    boolean resumeDownload(String str) throws RemoteException;

    boolean resumeResDownload(String str, String str2) throws RemoteException;

    boolean start(ResourceBean resourceBean) throws RemoteException;

    void unregisterDownloadListener(IRMDownloadCallback iRMDownloadCallback) throws RemoteException;

    /* loaded from: classes22.dex */
    public static class Default implements IResourceService {
        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public ResourceContainerBean queryResourceData(String type) throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public List<ResourceDownloadInfo> queryDownloadInfo(String[] resIds) throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public void registerDownloadListener(IRMDownloadCallback listener) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public void unregisterDownloadListener(IRMDownloadCallback listener) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean start(ResourceBean resourceBean) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean resume(String resourceId) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean remove(String resourceId) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean pause(String resourceId) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean resumeResDownload(String resType, String resourceId) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean cancelResDownload(String resType, String resourceId) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean pauseResDownload(String resType, String resourceId) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean pauseDownload(String url) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean cancelDownload(String url) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public boolean resumeDownload(String url) throws RemoteException {
            return false;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public long enqueue(String url, String title) throws RemoteException {
            return 0L;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public int fetchDownloadStatusById(long id) throws RemoteException {
            return 0;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public int fetchDownloadStatusByUrl(String url) throws RemoteException {
            return 0;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public int getDownloadStatusByUrl(String url) throws RemoteException {
            return 0;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public int getDownloadStatusById(long id) throws RemoteException {
            return 0;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public long getDownloadedBytesById(long id) throws RemoteException {
            return 0L;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public long getDownloadedBytesByUrl(String url) throws RemoteException {
            return 0L;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public long getTotalBytesById(long id) throws RemoteException {
            return 0L;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public long getTotalBytesByUrl(String url) throws RemoteException {
            return 0L;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public List<ResourceDownloadInfo> queryAllInfo() throws RemoteException {
            return null;
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public void removeLocalDataByUrl(String url) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public void removeLocalDataById(long id) throws RemoteException {
        }

        @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
        public String getLocalFilePath(String url) throws RemoteException {
            return null;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IResourceService {
        private static final String DESCRIPTOR = "com.xiaopeng.appstore.storeprovider.store.IResourceService";
        static final int TRANSACTION_basicTypes = 1;
        static final int TRANSACTION_cancelDownload = 14;
        static final int TRANSACTION_cancelResDownload = 11;
        static final int TRANSACTION_enqueue = 16;
        static final int TRANSACTION_fetchDownloadStatusById = 17;
        static final int TRANSACTION_fetchDownloadStatusByUrl = 18;
        static final int TRANSACTION_getDownloadStatusById = 20;
        static final int TRANSACTION_getDownloadStatusByUrl = 19;
        static final int TRANSACTION_getDownloadedBytesById = 21;
        static final int TRANSACTION_getDownloadedBytesByUrl = 22;
        static final int TRANSACTION_getLocalFilePath = 28;
        static final int TRANSACTION_getTotalBytesById = 23;
        static final int TRANSACTION_getTotalBytesByUrl = 24;
        static final int TRANSACTION_pause = 9;
        static final int TRANSACTION_pauseDownload = 13;
        static final int TRANSACTION_pauseResDownload = 12;
        static final int TRANSACTION_queryAllInfo = 25;
        static final int TRANSACTION_queryDownloadInfo = 3;
        static final int TRANSACTION_queryResourceData = 2;
        static final int TRANSACTION_registerDownloadListener = 4;
        static final int TRANSACTION_remove = 8;
        static final int TRANSACTION_removeLocalDataById = 27;
        static final int TRANSACTION_removeLocalDataByUrl = 26;
        static final int TRANSACTION_resume = 7;
        static final int TRANSACTION_resumeDownload = 15;
        static final int TRANSACTION_resumeResDownload = 10;
        static final int TRANSACTION_start = 6;
        static final int TRANSACTION_unregisterDownloadListener = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IResourceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IResourceService)) {
                return (IResourceService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ResourceBean _arg0;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    long _arg1 = data.readLong();
                    boolean _arg2 = data.readInt() != 0;
                    float _arg3 = data.readFloat();
                    double _arg4 = data.readDouble();
                    String _arg5 = data.readString();
                    basicTypes(_arg02, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    ResourceContainerBean _result = queryResourceData(_arg03);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg04 = data.createStringArray();
                    List<ResourceDownloadInfo> _result2 = queryDownloadInfo(_arg04);
                    reply.writeNoException();
                    reply.writeTypedList(_result2);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IRMDownloadCallback _arg05 = IRMDownloadCallback.Stub.asInterface(data.readStrongBinder());
                    registerDownloadListener(_arg05);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    IRMDownloadCallback _arg06 = IRMDownloadCallback.Stub.asInterface(data.readStrongBinder());
                    unregisterDownloadListener(_arg06);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = ResourceBean.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    boolean start = start(_arg0);
                    reply.writeNoException();
                    reply.writeInt(start ? 1 : 0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    boolean resume = resume(_arg07);
                    reply.writeNoException();
                    reply.writeInt(resume ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    boolean remove = remove(_arg08);
                    reply.writeNoException();
                    reply.writeInt(remove ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    boolean pause = pause(_arg09);
                    reply.writeNoException();
                    reply.writeInt(pause ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    String _arg12 = data.readString();
                    boolean resumeResDownload = resumeResDownload(_arg010, _arg12);
                    reply.writeNoException();
                    reply.writeInt(resumeResDownload ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    String _arg13 = data.readString();
                    boolean cancelResDownload = cancelResDownload(_arg011, _arg13);
                    reply.writeNoException();
                    reply.writeInt(cancelResDownload ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    String _arg14 = data.readString();
                    boolean pauseResDownload = pauseResDownload(_arg012, _arg14);
                    reply.writeNoException();
                    reply.writeInt(pauseResDownload ? 1 : 0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    boolean pauseDownload = pauseDownload(_arg013);
                    reply.writeNoException();
                    reply.writeInt(pauseDownload ? 1 : 0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    boolean cancelDownload = cancelDownload(_arg014);
                    reply.writeNoException();
                    reply.writeInt(cancelDownload ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    boolean resumeDownload = resumeDownload(_arg015);
                    reply.writeNoException();
                    reply.writeInt(resumeDownload ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg016 = data.readString();
                    String _arg15 = data.readString();
                    long _result3 = enqueue(_arg016, _arg15);
                    reply.writeNoException();
                    reply.writeLong(_result3);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg017 = data.readLong();
                    int _result4 = fetchDownloadStatusById(_arg017);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg018 = data.readString();
                    int _result5 = fetchDownloadStatusByUrl(_arg018);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    int _result6 = getDownloadStatusByUrl(_arg019);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg020 = data.readLong();
                    int _result7 = getDownloadStatusById(_arg020);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg021 = data.readLong();
                    long _result8 = getDownloadedBytesById(_arg021);
                    reply.writeNoException();
                    reply.writeLong(_result8);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg022 = data.readString();
                    long _result9 = getDownloadedBytesByUrl(_arg022);
                    reply.writeNoException();
                    reply.writeLong(_result9);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg023 = data.readLong();
                    long _result10 = getTotalBytesById(_arg023);
                    reply.writeNoException();
                    reply.writeLong(_result10);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg024 = data.readString();
                    long _result11 = getTotalBytesByUrl(_arg024);
                    reply.writeNoException();
                    reply.writeLong(_result11);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    List<ResourceDownloadInfo> _result12 = queryAllInfo();
                    reply.writeNoException();
                    reply.writeTypedList(_result12);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg025 = data.readString();
                    removeLocalDataByUrl(_arg025);
                    reply.writeNoException();
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg026 = data.readLong();
                    removeLocalDataById(_arg026);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg027 = data.readString();
                    String _result13 = getLocalFilePath(_arg027);
                    reply.writeNoException();
                    reply.writeString(_result13);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes22.dex */
        public static class Proxy implements IResourceService {
            public static IResourceService sDefaultImpl;
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

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
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

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public ResourceContainerBean queryResourceData(String type) throws RemoteException {
                ResourceContainerBean _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().queryResourceData(type);
                    }
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = ResourceContainerBean.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public List<ResourceDownloadInfo> queryDownloadInfo(String[] resIds) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(resIds);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().queryDownloadInfo(resIds);
                    }
                    _reply.readException();
                    List<ResourceDownloadInfo> _result = _reply.createTypedArrayList(ResourceDownloadInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public void registerDownloadListener(IRMDownloadCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerDownloadListener(listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public void unregisterDownloadListener(IRMDownloadCallback listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterDownloadListener(listener);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean start(ResourceBean resourceBean) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (resourceBean != null) {
                        _data.writeInt(1);
                        resourceBean.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().start(resourceBean);
                    }
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean resume(String resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resourceId);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().resume(resourceId);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean remove(String resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resourceId);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().remove(resourceId);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean pause(String resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resourceId);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().pause(resourceId);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean resumeResDownload(String resType, String resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resType);
                    _data.writeString(resourceId);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().resumeResDownload(resType, resourceId);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean cancelResDownload(String resType, String resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resType);
                    _data.writeString(resourceId);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().cancelResDownload(resType, resourceId);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean pauseResDownload(String resType, String resourceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(resType);
                    _data.writeString(resourceId);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().pauseResDownload(resType, resourceId);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean pauseDownload(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().pauseDownload(url);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean cancelDownload(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().cancelDownload(url);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public boolean resumeDownload(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().resumeDownload(url);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public long enqueue(String url, String title) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    _data.writeString(title);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().enqueue(url, title);
                    }
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public int fetchDownloadStatusById(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    boolean _status = this.mRemote.transact(17, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().fetchDownloadStatusById(id);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public int fetchDownloadStatusByUrl(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(18, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().fetchDownloadStatusByUrl(url);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public int getDownloadStatusByUrl(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(19, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDownloadStatusByUrl(url);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public int getDownloadStatusById(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    boolean _status = this.mRemote.transact(20, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDownloadStatusById(id);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public long getDownloadedBytesById(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    boolean _status = this.mRemote.transact(21, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDownloadedBytesById(id);
                    }
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public long getDownloadedBytesByUrl(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(22, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getDownloadedBytesByUrl(url);
                    }
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public long getTotalBytesById(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    boolean _status = this.mRemote.transact(23, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTotalBytesById(id);
                    }
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public long getTotalBytesByUrl(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(24, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getTotalBytesByUrl(url);
                    }
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public List<ResourceDownloadInfo> queryAllInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(25, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().queryAllInfo();
                    }
                    _reply.readException();
                    List<ResourceDownloadInfo> _result = _reply.createTypedArrayList(ResourceDownloadInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public void removeLocalDataByUrl(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(26, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().removeLocalDataByUrl(url);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public void removeLocalDataById(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    boolean _status = this.mRemote.transact(27, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().removeLocalDataById(id);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.appstore.storeprovider.store.IResourceService
            public String getLocalFilePath(String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(url);
                    boolean _status = this.mRemote.transact(28, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getLocalFilePath(url);
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IResourceService impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IResourceService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
