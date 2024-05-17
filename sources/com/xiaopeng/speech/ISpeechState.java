package com.xiaopeng.speech;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes23.dex */
public interface ISpeechState extends IInterface {
    boolean canExit() throws RemoteException;

    void changeCurrentTTS(String str) throws RemoteException;

    String getCurrentTTS() throws RemoteException;

    boolean getFuncState(int i) throws RemoteException;

    int getOOBEStatus() throws RemoteException;

    int getPhoneCallStatus() throws RemoteException;

    boolean isDMEndByPOISelect() throws RemoteException;

    boolean isDMStarted() throws RemoteException;

    boolean isHotWordEngineReady() throws RemoteException;

    boolean isInitOK() throws RemoteException;

    boolean isMicrophoneMute() throws RemoteException;

    boolean isShowSpeechDialog() throws RemoteException;

    boolean isStateWaiting() throws RemoteException;

    boolean onWaiting() throws RemoteException;

    void setCanExitFlag(boolean z) throws RemoteException;

    void setFuncState(int i, boolean z) throws RemoteException;

    void setOOBEStatus(int i) throws RemoteException;

    void setPhoneCallStatus(int i) throws RemoteException;

    void setPhoneCallStatusWithBinder(IBinder iBinder, int i) throws RemoteException;

    void setShowSpeechDialog(boolean z) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements ISpeechState {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.ISpeechState";
        static final int TRANSACTION_canExit = 9;
        static final int TRANSACTION_changeCurrentTTS = 12;
        static final int TRANSACTION_getCurrentTTS = 11;
        static final int TRANSACTION_getFuncState = 14;
        static final int TRANSACTION_getOOBEStatus = 7;
        static final int TRANSACTION_getPhoneCallStatus = 3;
        static final int TRANSACTION_isDMEndByPOISelect = 17;
        static final int TRANSACTION_isDMStarted = 10;
        static final int TRANSACTION_isHotWordEngineReady = 18;
        static final int TRANSACTION_isInitOK = 1;
        static final int TRANSACTION_isMicrophoneMute = 19;
        static final int TRANSACTION_isShowSpeechDialog = 5;
        static final int TRANSACTION_isStateWaiting = 15;
        static final int TRANSACTION_onWaiting = 16;
        static final int TRANSACTION_setCanExitFlag = 8;
        static final int TRANSACTION_setFuncState = 13;
        static final int TRANSACTION_setOOBEStatus = 6;
        static final int TRANSACTION_setPhoneCallStatus = 2;
        static final int TRANSACTION_setPhoneCallStatusWithBinder = 20;
        static final int TRANSACTION_setShowSpeechDialog = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISpeechState asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISpeechState)) {
                return (ISpeechState) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean _arg1;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isInitOK = isInitOK();
                    reply.writeNoException();
                    reply.writeInt(isInitOK ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    setPhoneCallStatus(_arg0);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getPhoneCallStatus();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setShowSpeechDialog(_arg1);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isShowSpeechDialog = isShowSpeechDialog();
                    reply.writeNoException();
                    reply.writeInt(isShowSpeechDialog ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    setOOBEStatus(_arg02);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = getOOBEStatus();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setCanExitFlag(_arg1);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean canExit = canExit();
                    reply.writeNoException();
                    reply.writeInt(canExit ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDMStarted = isDMStarted();
                    reply.writeNoException();
                    reply.writeInt(isDMStarted ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _result3 = getCurrentTTS();
                    reply.writeNoException();
                    reply.writeString(_result3);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    changeCurrentTTS(_arg03);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    _arg1 = data.readInt() != 0;
                    setFuncState(_arg04, _arg1);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    boolean funcState = getFuncState(_arg05);
                    reply.writeNoException();
                    reply.writeInt(funcState ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isStateWaiting = isStateWaiting();
                    reply.writeNoException();
                    reply.writeInt(isStateWaiting ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    boolean onWaiting = onWaiting();
                    reply.writeNoException();
                    reply.writeInt(onWaiting ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDMEndByPOISelect = isDMEndByPOISelect();
                    reply.writeNoException();
                    reply.writeInt(isDMEndByPOISelect ? 1 : 0);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isHotWordEngineReady = isHotWordEngineReady();
                    reply.writeNoException();
                    reply.writeInt(isHotWordEngineReady ? 1 : 0);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isMicrophoneMute = isMicrophoneMute();
                    reply.writeNoException();
                    reply.writeInt(isMicrophoneMute ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg06 = data.readStrongBinder();
                    setPhoneCallStatusWithBinder(_arg06, data.readInt());
                    reply.writeNoException();
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements ISpeechState {
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

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isInitOK() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void setPhoneCallStatus(int callStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(callStatus);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public int getPhoneCallStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void setShowSpeechDialog(boolean isShow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(isShow ? 1 : 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isShowSpeechDialog() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void setOOBEStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public int getOOBEStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void setCanExitFlag(boolean flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag ? 1 : 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean canExit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isDMStarted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public String getCurrentTTS() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void changeCurrentTTS(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void setFuncState(int func, boolean flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(func);
                    _data.writeInt(flag ? 1 : 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean getFuncState(int func) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(func);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isStateWaiting() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean onWaiting() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isDMEndByPOISelect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isHotWordEngineReady() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public boolean isMicrophoneMute() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechState
            public void setPhoneCallStatusWithBinder(IBinder binder, int callStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(callStatus);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
