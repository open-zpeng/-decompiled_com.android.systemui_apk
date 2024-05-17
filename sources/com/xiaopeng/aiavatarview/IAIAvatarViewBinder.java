package com.xiaopeng.aiavatarview;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MotionEvent;
import com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback;
/* loaded from: classes22.dex */
public interface IAIAvatarViewBinder extends IInterface {
    void notifyAvatarAction(int i, String str) throws RemoteException;

    void notifyClickEvent(int i) throws RemoteException;

    void notifyMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void registerStatusCallback(IAIAvatarViewStatusCallback iAIAvatarViewStatusCallback) throws RemoteException;

    void replaceAvatarScene() throws RemoteException;

    void restoreScene() throws RemoteException;

    void setVisible(boolean z) throws RemoteException;

    void smallScene() throws RemoteException;

    void unregisterStatusCallback(IAIAvatarViewStatusCallback iAIAvatarViewStatusCallback) throws RemoteException;

    /* loaded from: classes22.dex */
    public static class Default implements IAIAvatarViewBinder {
        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void registerStatusCallback(IAIAvatarViewStatusCallback callback) throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void unregisterStatusCallback(IAIAvatarViewStatusCallback callback) throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void notifyClickEvent(int area) throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void notifyMotionEvent(MotionEvent ev) throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void notifyAvatarAction(int actionType, String params) throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void replaceAvatarScene() throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void restoreScene() throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void smallScene() throws RemoteException {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
        public void setVisible(boolean visible) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IAIAvatarViewBinder {
        private static final String DESCRIPTOR = "com.xiaopeng.aiavatarview.IAIAvatarViewBinder";
        static final int TRANSACTION_notifyAvatarAction = 5;
        static final int TRANSACTION_notifyClickEvent = 3;
        static final int TRANSACTION_notifyMotionEvent = 4;
        static final int TRANSACTION_registerStatusCallback = 1;
        static final int TRANSACTION_replaceAvatarScene = 6;
        static final int TRANSACTION_restoreScene = 7;
        static final int TRANSACTION_setVisible = 9;
        static final int TRANSACTION_smallScene = 8;
        static final int TRANSACTION_unregisterStatusCallback = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAIAvatarViewBinder asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAIAvatarViewBinder)) {
                return (IAIAvatarViewBinder) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            MotionEvent _arg0;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IAIAvatarViewStatusCallback _arg02 = IAIAvatarViewStatusCallback.Stub.asInterface(data.readStrongBinder());
                    registerStatusCallback(_arg02);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IAIAvatarViewStatusCallback _arg03 = IAIAvatarViewStatusCallback.Stub.asInterface(data.readStrongBinder());
                    unregisterStatusCallback(_arg03);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    notifyClickEvent(_arg04);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (MotionEvent) MotionEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    notifyMotionEvent(_arg0);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg1 = data.readString();
                    notifyAvatarAction(_arg05, _arg1);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    replaceAvatarScene();
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    restoreScene();
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    smallScene();
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg06 = data.readInt() != 0;
                    setVisible(_arg06);
                    reply.writeNoException();
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes22.dex */
        public static class Proxy implements IAIAvatarViewBinder {
            public static IAIAvatarViewBinder sDefaultImpl;
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

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void registerStatusCallback(IAIAvatarViewStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerStatusCallback(callback);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void unregisterStatusCallback(IAIAvatarViewStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterStatusCallback(callback);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void notifyClickEvent(int area) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(area);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().notifyClickEvent(area);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void notifyMotionEvent(MotionEvent ev) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ev != null) {
                        _data.writeInt(1);
                        ev.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().notifyMotionEvent(ev);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void notifyAvatarAction(int actionType, String params) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actionType);
                    _data.writeString(params);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().notifyAvatarAction(actionType, params);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void replaceAvatarScene() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().replaceAvatarScene();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void restoreScene() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().restoreScene();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void smallScene() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().smallScene();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiavatarview.IAIAvatarViewBinder
            public void setVisible(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visible ? 1 : 0);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setVisible(visible);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IAIAvatarViewBinder impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IAIAvatarViewBinder getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
