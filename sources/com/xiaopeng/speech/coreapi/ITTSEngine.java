package com.xiaopeng.speech.coreapi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.speech.coreapi.ITTSCallback;
/* loaded from: classes23.dex */
public interface ITTSEngine extends IInterface {
    void addListener(ITTSCallback iTTSCallback) throws RemoteException;

    String getSpeaker() throws RemoteException;

    float getSpeed() throws RemoteException;

    int getVolume() throws RemoteException;

    boolean isSingleTTS() throws RemoteException;

    boolean isTTSSupportSpell() throws RemoteException;

    void removeListener(ITTSCallback iTTSCallback) throws RemoteException;

    void setMode(int i) throws RemoteException;

    void setSingleTTS(boolean z, IBinder iBinder) throws RemoteException;

    void setSoloMode(boolean z) throws RemoteException;

    void setSoloModeByChannel(int i, boolean z) throws RemoteException;

    void setSpeaker(String str) throws RemoteException;

    void setSpeed(float f) throws RemoteException;

    void setVolume(int i) throws RemoteException;

    void shutup(String str) throws RemoteException;

    void shutupByReason(String str, String str2) throws RemoteException;

    boolean speak(String str, int i, String str2, int i2) throws RemoteException;

    boolean speakEnhance(String str, int i, String str2, int i2, int i3, int i4, long j) throws RemoteException;

    boolean speakEnhanceByVoicePosition(String str, int i, String str2, int i2, int i3, int i4, long j, int i5) throws RemoteException;

    boolean speakParam(String str, String str2) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements ITTSEngine {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.coreapi.ITTSEngine";
        static final int TRANSACTION_addListener = 9;
        static final int TRANSACTION_getSpeaker = 3;
        static final int TRANSACTION_getSpeed = 5;
        static final int TRANSACTION_getVolume = 7;
        static final int TRANSACTION_isSingleTTS = 12;
        static final int TRANSACTION_isTTSSupportSpell = 13;
        static final int TRANSACTION_removeListener = 10;
        static final int TRANSACTION_setMode = 15;
        static final int TRANSACTION_setSingleTTS = 11;
        static final int TRANSACTION_setSoloMode = 16;
        static final int TRANSACTION_setSoloModeByChannel = 19;
        static final int TRANSACTION_setSpeaker = 2;
        static final int TRANSACTION_setSpeed = 4;
        static final int TRANSACTION_setVolume = 6;
        static final int TRANSACTION_shutup = 8;
        static final int TRANSACTION_shutupByReason = 17;
        static final int TRANSACTION_speak = 1;
        static final int TRANSACTION_speakEnhance = 14;
        static final int TRANSACTION_speakEnhanceByVoicePosition = 18;
        static final int TRANSACTION_speakParam = 20;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITTSEngine asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITTSEngine)) {
                return (ITTSEngine) iin;
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
                    String _arg0 = data.readString();
                    int _arg12 = data.readInt();
                    String _arg2 = data.readString();
                    int _arg3 = data.readInt();
                    boolean speak = speak(_arg0, _arg12, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeInt(speak ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    setSpeaker(_arg02);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _result = getSpeaker();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    float _arg03 = data.readFloat();
                    setSpeed(_arg03);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    float _result2 = getSpeed();
                    reply.writeNoException();
                    reply.writeFloat(_result2);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    setVolume(_arg04);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _result3 = getVolume();
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    shutup(_arg05);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    ITTSCallback _arg06 = ITTSCallback.Stub.asInterface(data.readStrongBinder());
                    addListener(_arg06);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    ITTSCallback _arg07 = ITTSCallback.Stub.asInterface(data.readStrongBinder());
                    removeListener(_arg07);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setSingleTTS(_arg1, data.readStrongBinder());
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isSingleTTS = isSingleTTS();
                    reply.writeNoException();
                    reply.writeInt(isSingleTTS ? 1 : 0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isTTSSupportSpell = isTTSSupportSpell();
                    reply.writeNoException();
                    reply.writeInt(isTTSSupportSpell ? 1 : 0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    int _arg13 = data.readInt();
                    String _arg22 = data.readString();
                    int _arg32 = data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    long _arg6 = data.readLong();
                    boolean speakEnhance = speakEnhance(_arg08, _arg13, _arg22, _arg32, _arg4, _arg5, _arg6);
                    reply.writeNoException();
                    reply.writeInt(speakEnhance ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    setMode(_arg09);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setSoloMode(_arg1);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    shutupByReason(_arg010, data.readString());
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    int _arg14 = data.readInt();
                    String _arg23 = data.readString();
                    int _arg33 = data.readInt();
                    int _arg42 = data.readInt();
                    int _arg52 = data.readInt();
                    long _arg62 = data.readLong();
                    int _arg7 = data.readInt();
                    boolean speakEnhanceByVoicePosition = speakEnhanceByVoicePosition(_arg011, _arg14, _arg23, _arg33, _arg42, _arg52, _arg62, _arg7);
                    reply.writeNoException();
                    reply.writeInt(speakEnhanceByVoicePosition ? 1 : 0);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    _arg1 = data.readInt() != 0;
                    setSoloModeByChannel(_arg012, _arg1);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    boolean speakParam = speakParam(_arg013, data.readString());
                    reply.writeNoException();
                    reply.writeInt(speakParam ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements ITTSEngine {
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

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public boolean speak(String text, int priority, String ttsId, int audioFocus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    _data.writeInt(priority);
                    _data.writeString(ttsId);
                    _data.writeInt(audioFocus);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setSpeaker(String speaker) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(speaker);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public String getSpeaker() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setSpeed(float speed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(speed);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public float getSpeed() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setVolume(int volume) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(volume);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public int getVolume() throws RemoteException {
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

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void shutup(String ttsid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ttsid);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void addListener(ITTSCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void removeListener(ITTSCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setSingleTTS(boolean state, IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state ? 1 : 0);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public boolean isSingleTTS() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public boolean isTTSSupportSpell() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public boolean speakEnhance(String text, int priority, String ttsId, int audioFocus, int streamType, int ttsMode, long timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    _data.writeInt(priority);
                    _data.writeString(ttsId);
                    _data.writeInt(audioFocus);
                    _data.writeInt(streamType);
                    _data.writeInt(ttsMode);
                    _data.writeLong(timeout);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setSoloMode(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void shutupByReason(String ttsid, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(ttsid);
                    _data.writeString(reason);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public boolean speakEnhanceByVoicePosition(String text, int priority, String ttsId, int audioFocus, int streamType, int ttsMode, long timeout, int position) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    _data.writeInt(priority);
                    _data.writeString(ttsId);
                    _data.writeInt(audioFocus);
                    _data.writeInt(streamType);
                    _data.writeInt(ttsMode);
                    _data.writeLong(timeout);
                    _data.writeInt(position);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public void setSoloModeByChannel(int channel, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(channel);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.ITTSEngine
            public boolean speakParam(String text, String param) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    _data.writeString(param);
                    this.mRemote.transact(20, _data, _reply, 0);
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
