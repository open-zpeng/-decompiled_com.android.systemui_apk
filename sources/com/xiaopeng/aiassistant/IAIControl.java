package com.xiaopeng.aiassistant;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes22.dex */
public interface IAIControl extends IInterface {
    void closeWindow(int i, int i2) throws RemoteException;

    int getShowingWindow() throws RemoteException;

    String getXpAttributeConfig() throws RemoteException;

    void hideXp(int i) throws RemoteException;

    boolean isShowingWindow(int i) throws RemoteException;

    boolean isXpObscured() throws RemoteException;

    void onWindowAttached(int i) throws RemoteException;

    void onWindowDetached(int i) throws RemoteException;

    void pauseXp(int i) throws RemoteException;

    void resumeXp(int i) throws RemoteException;

    void setAnimState(int i, int i2) throws RemoteException;

    void showXp(int i) throws RemoteException;

    /* loaded from: classes22.dex */
    public static abstract class Stub extends Binder implements IAIControl {
        private static final String DESCRIPTOR = "com.xiaopeng.aiassistant.IAIControl";
        static final int TRANSACTION_closeWindow = 1;
        static final int TRANSACTION_getShowingWindow = 8;
        static final int TRANSACTION_getXpAttributeConfig = 9;
        static final int TRANSACTION_hideXp = 5;
        static final int TRANSACTION_isShowingWindow = 10;
        static final int TRANSACTION_isXpObscured = 11;
        static final int TRANSACTION_onWindowAttached = 2;
        static final int TRANSACTION_onWindowDetached = 3;
        static final int TRANSACTION_pauseXp = 4;
        static final int TRANSACTION_resumeXp = 12;
        static final int TRANSACTION_setAnimState = 7;
        static final int TRANSACTION_showXp = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAIControl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAIControl)) {
                return (IAIControl) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    closeWindow(_arg0, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    onWindowAttached(_arg02);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    onWindowDetached(_arg03);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    pauseXp(_arg04);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    hideXp(_arg05);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    showXp(_arg06);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg12 = data.readInt();
                    setAnimState(_arg07, _arg12);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getShowingWindow();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _result2 = getXpAttributeConfig();
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    boolean isShowingWindow = isShowingWindow(_arg08);
                    reply.writeNoException();
                    reply.writeInt(isShowingWindow ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isXpObscured = isXpObscured();
                    reply.writeNoException();
                    reply.writeInt(isXpObscured ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    resumeXp(_arg09);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes22.dex */
        private static class Proxy implements IAIControl {
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

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void closeWindow(int from, int target) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    _data.writeInt(target);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void onWindowAttached(int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void onWindowDetached(int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void pauseXp(int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void hideXp(int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void showXp(int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void setAnimState(int state, int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(from);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public int getShowingWindow() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public String getXpAttributeConfig() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public boolean isShowingWindow(int target) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(target);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public boolean isXpObscured() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.aiassistant.IAIControl
            public void resumeXp(int from) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
