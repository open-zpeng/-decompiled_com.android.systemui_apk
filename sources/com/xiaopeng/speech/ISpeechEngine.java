package com.xiaopeng.speech;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.speech.IActorBridge;
import com.xiaopeng.speech.ICarSystemProperty;
import com.xiaopeng.speech.IQueryInjector;
import com.xiaopeng.speech.ISoundLockState;
import com.xiaopeng.speech.ISpeechState;
import com.xiaopeng.speech.IWindowEngine;
import com.xiaopeng.speech.asr.IRecognizer;
import com.xiaopeng.speech.coreapi.IASREngine;
import com.xiaopeng.speech.coreapi.IAgent;
import com.xiaopeng.speech.coreapi.IAppMgr;
import com.xiaopeng.speech.coreapi.IHotwordEngine;
import com.xiaopeng.speech.coreapi.IRecordEngine;
import com.xiaopeng.speech.coreapi.ISubscriber;
import com.xiaopeng.speech.coreapi.ITTSEngine;
import com.xiaopeng.speech.coreapi.IVADEngine;
import com.xiaopeng.speech.coreapi.IWakeupEngine;
/* loaded from: classes23.dex */
public interface ISpeechEngine extends IInterface {
    IASREngine getASREngine() throws RemoteException;

    IActorBridge getActorBridge() throws RemoteException;

    IAgent getAgent() throws RemoteException;

    IAppMgr getAppMgr() throws RemoteException;

    ICarSystemProperty getCarSystemProperty() throws RemoteException;

    IHotwordEngine getHotwordEngine() throws RemoteException;

    IQueryInjector getQueryInjector() throws RemoteException;

    IRecognizer getRecognizer() throws RemoteException;

    IRecordEngine getRecordEngine() throws RemoteException;

    ISoundLockState getSoundLockState() throws RemoteException;

    ISpeechState getSpeechState() throws RemoteException;

    ISubscriber getSubscriber() throws RemoteException;

    ITTSEngine getTTSEngine() throws RemoteException;

    IVADEngine getVadEngine() throws RemoteException;

    IWakeupEngine getWakeupEngine() throws RemoteException;

    IWindowEngine getWindowEngine() throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements ISpeechEngine {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.ISpeechEngine";
        static final int TRANSACTION_getASREngine = 9;
        static final int TRANSACTION_getActorBridge = 1;
        static final int TRANSACTION_getAgent = 5;
        static final int TRANSACTION_getAppMgr = 6;
        static final int TRANSACTION_getCarSystemProperty = 15;
        static final int TRANSACTION_getHotwordEngine = 14;
        static final int TRANSACTION_getQueryInjector = 10;
        static final int TRANSACTION_getRecognizer = 13;
        static final int TRANSACTION_getRecordEngine = 11;
        static final int TRANSACTION_getSoundLockState = 8;
        static final int TRANSACTION_getSpeechState = 7;
        static final int TRANSACTION_getSubscriber = 2;
        static final int TRANSACTION_getTTSEngine = 3;
        static final int TRANSACTION_getVadEngine = 16;
        static final int TRANSACTION_getWakeupEngine = 4;
        static final int TRANSACTION_getWindowEngine = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISpeechEngine asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISpeechEngine)) {
                return (ISpeechEngine) iin;
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
                    IActorBridge _result = getActorBridge();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result != null ? _result.asBinder() : null);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    ISubscriber _result2 = getSubscriber();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result2 != null ? _result2.asBinder() : null);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    ITTSEngine _result3 = getTTSEngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result3 != null ? _result3.asBinder() : null);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IWakeupEngine _result4 = getWakeupEngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result4 != null ? _result4.asBinder() : null);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    IAgent _result5 = getAgent();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result5 != null ? _result5.asBinder() : null);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IAppMgr _result6 = getAppMgr();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result6 != null ? _result6.asBinder() : null);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    ISpeechState _result7 = getSpeechState();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result7 != null ? _result7.asBinder() : null);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    ISoundLockState _result8 = getSoundLockState();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result8 != null ? _result8.asBinder() : null);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    IASREngine _result9 = getASREngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result9 != null ? _result9.asBinder() : null);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    IQueryInjector _result10 = getQueryInjector();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result10 != null ? _result10.asBinder() : null);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    IRecordEngine _result11 = getRecordEngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result11 != null ? _result11.asBinder() : null);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    IWindowEngine _result12 = getWindowEngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result12 != null ? _result12.asBinder() : null);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    IRecognizer _result13 = getRecognizer();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result13 != null ? _result13.asBinder() : null);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    IHotwordEngine _result14 = getHotwordEngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result14 != null ? _result14.asBinder() : null);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    ICarSystemProperty _result15 = getCarSystemProperty();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result15 != null ? _result15.asBinder() : null);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    IVADEngine _result16 = getVadEngine();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result16 != null ? _result16.asBinder() : null);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements ISpeechEngine {
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

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IActorBridge getActorBridge() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IActorBridge _result = IActorBridge.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public ISubscriber getSubscriber() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ISubscriber _result = ISubscriber.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public ITTSEngine getTTSEngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ITTSEngine _result = ITTSEngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IWakeupEngine getWakeupEngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    IWakeupEngine _result = IWakeupEngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IAgent getAgent() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    IAgent _result = IAgent.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IAppMgr getAppMgr() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    IAppMgr _result = IAppMgr.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public ISpeechState getSpeechState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ISpeechState _result = ISpeechState.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public ISoundLockState getSoundLockState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    ISoundLockState _result = ISoundLockState.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IASREngine getASREngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    IASREngine _result = IASREngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IQueryInjector getQueryInjector() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    IQueryInjector _result = IQueryInjector.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IRecordEngine getRecordEngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    IRecordEngine _result = IRecordEngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IWindowEngine getWindowEngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IWindowEngine _result = IWindowEngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IRecognizer getRecognizer() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    IRecognizer _result = IRecognizer.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IHotwordEngine getHotwordEngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    IHotwordEngine _result = IHotwordEngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public ICarSystemProperty getCarSystemProperty() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    ICarSystemProperty _result = ICarSystemProperty.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.ISpeechEngine
            public IVADEngine getVadEngine() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    IVADEngine _result = IVADEngine.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
