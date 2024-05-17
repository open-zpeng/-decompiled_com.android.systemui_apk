package com.xiaopeng.speech.coreapi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes23.dex */
public interface IWakeupEngine extends IInterface {
    void addCommandWakeupWord(String[] strArr, String[] strArr2, String[] strArr3, String[] strArr4, String[] strArr5) throws RemoteException;

    void addShortcutWakeupWord(String[] strArr, String[] strArr2, String[] strArr3) throws RemoteException;

    void avatarClick(String str) throws RemoteException;

    void avatarPress() throws RemoteException;

    void avatarRelease() throws RemoteException;

    void clearCommandWakeupWord() throws RemoteException;

    void clearShortCutWakeupWord() throws RemoteException;

    void disableFastWake() throws RemoteException;

    void disableFastWakeEnhance(IBinder iBinder) throws RemoteException;

    void disableInterruptWake(IBinder iBinder) throws RemoteException;

    void disableMainWakeupWord(IBinder iBinder) throws RemoteException;

    void disableOneshot() throws RemoteException;

    void disableSpeechWithInfo(IBinder iBinder, String str, String str2, int i) throws RemoteException;

    void disableWakeup() throws RemoteException;

    void disableWakeupEnhance(IBinder iBinder) throws RemoteException;

    void disableWakeupWithInfo(IBinder iBinder, int i, String str, String str2, int i2) throws RemoteException;

    void disableWheelWakeupWithInfo(IBinder iBinder, String str, String str2, int i) throws RemoteException;

    void enableFastWake() throws RemoteException;

    void enableFastWakeEnhance(IBinder iBinder) throws RemoteException;

    void enableInterruptWake(IBinder iBinder) throws RemoteException;

    void enableMainWakeupWord(IBinder iBinder) throws RemoteException;

    void enableOneshot() throws RemoteException;

    void enableSpeechWithInfo(IBinder iBinder, String str, int i) throws RemoteException;

    void enableWakeup() throws RemoteException;

    void enableWakeupEnhance(IBinder iBinder) throws RemoteException;

    void enableWakeupWithInfo(IBinder iBinder, int i, String str, int i2) throws RemoteException;

    void enableWheelWakeupWithInfo(IBinder iBinder, String str, int i) throws RemoteException;

    String getMinorWakeupWord() throws RemoteException;

    String getSoundAreaStatus(int i) throws RemoteException;

    String[] getWakeupWords() throws RemoteException;

    boolean isDefaultEnableFastWake() throws RemoteException;

    boolean isDefaultEnableOneshot() throws RemoteException;

    boolean isDefaultEnableWakeup() throws RemoteException;

    boolean isEnableWakeup() throws RemoteException;

    boolean isWheelWakeupEnabled() throws RemoteException;

    void pauseDialog() throws RemoteException;

    void removeCommandWakeupWord(String[] strArr) throws RemoteException;

    void removeShortcutWakeupWord(String[] strArr) throws RemoteException;

    void resumeDialog() throws RemoteException;

    void resumeDialogWithReason(IBinder iBinder, String str, String str2) throws RemoteException;

    void resumeSpeechWithInfo(IBinder iBinder, String str) throws RemoteException;

    void resumeSpeechWithTypeInfo(IBinder iBinder, String str, int i, boolean z) throws RemoteException;

    void setDefaultFastWakeEnabled(boolean z) throws RemoteException;

    void setDefaultOneshotEnabled(boolean z) throws RemoteException;

    void setDefaultWakeupEnabled(boolean z) throws RemoteException;

    void setWheelWakeupEnabled(IBinder iBinder, boolean z) throws RemoteException;

    void startDialog() throws RemoteException;

    void startDialogFrom(String str) throws RemoteException;

    void startDialogWithSoundArea(String str, int i) throws RemoteException;

    void stopDialog() throws RemoteException;

    void stopDialogMessage() throws RemoteException;

    void stopDialogReason(String str) throws RemoteException;

    void suspendDialogWithReason(IBinder iBinder, String str, String str2) throws RemoteException;

    void suspendSpeechWithInfo(IBinder iBinder, String str, String str2, int i, boolean z) throws RemoteException;

    void updateCommandWakeupWord(String[] strArr, String[] strArr2, String[] strArr3, String[] strArr4, String[] strArr5) throws RemoteException;

    void updateMinorWakeupWord(String str, String str2, String str3, String[] strArr) throws RemoteException;

    void updateShortcutWakeupWord(String[] strArr, String[] strArr2, String[] strArr3) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements IWakeupEngine {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.coreapi.IWakeupEngine";
        static final int TRANSACTION_addCommandWakeupWord = 13;
        static final int TRANSACTION_addShortcutWakeupWord = 17;
        static final int TRANSACTION_avatarClick = 3;
        static final int TRANSACTION_avatarPress = 4;
        static final int TRANSACTION_avatarRelease = 5;
        static final int TRANSACTION_clearCommandWakeupWord = 12;
        static final int TRANSACTION_clearShortCutWakeupWord = 16;
        static final int TRANSACTION_disableFastWake = 35;
        static final int TRANSACTION_disableFastWakeEnhance = 41;
        static final int TRANSACTION_disableInterruptWake = 43;
        static final int TRANSACTION_disableMainWakeupWord = 39;
        static final int TRANSACTION_disableOneshot = 31;
        static final int TRANSACTION_disableSpeechWithInfo = 55;
        static final int TRANSACTION_disableWakeup = 7;
        static final int TRANSACTION_disableWakeupEnhance = 25;
        static final int TRANSACTION_disableWakeupWithInfo = 45;
        static final int TRANSACTION_disableWheelWakeupWithInfo = 47;
        static final int TRANSACTION_enableFastWake = 34;
        static final int TRANSACTION_enableFastWakeEnhance = 40;
        static final int TRANSACTION_enableInterruptWake = 42;
        static final int TRANSACTION_enableMainWakeupWord = 38;
        static final int TRANSACTION_enableOneshot = 30;
        static final int TRANSACTION_enableSpeechWithInfo = 56;
        static final int TRANSACTION_enableWakeup = 6;
        static final int TRANSACTION_enableWakeupEnhance = 24;
        static final int TRANSACTION_enableWakeupWithInfo = 46;
        static final int TRANSACTION_enableWheelWakeupWithInfo = 48;
        static final int TRANSACTION_getMinorWakeupWord = 10;
        static final int TRANSACTION_getSoundAreaStatus = 54;
        static final int TRANSACTION_getWakeupWords = 8;
        static final int TRANSACTION_isDefaultEnableFastWake = 32;
        static final int TRANSACTION_isDefaultEnableOneshot = 28;
        static final int TRANSACTION_isDefaultEnableWakeup = 22;
        static final int TRANSACTION_isEnableWakeup = 21;
        static final int TRANSACTION_isWheelWakeupEnabled = 26;
        static final int TRANSACTION_pauseDialog = 19;
        static final int TRANSACTION_removeCommandWakeupWord = 14;
        static final int TRANSACTION_removeShortcutWakeupWord = 18;
        static final int TRANSACTION_resumeDialog = 20;
        static final int TRANSACTION_resumeDialogWithReason = 50;
        static final int TRANSACTION_resumeSpeechWithInfo = 53;
        static final int TRANSACTION_resumeSpeechWithTypeInfo = 57;
        static final int TRANSACTION_setDefaultFastWakeEnabled = 33;
        static final int TRANSACTION_setDefaultOneshotEnabled = 29;
        static final int TRANSACTION_setDefaultWakeupEnabled = 23;
        static final int TRANSACTION_setWheelWakeupEnabled = 27;
        static final int TRANSACTION_startDialog = 1;
        static final int TRANSACTION_startDialogFrom = 44;
        static final int TRANSACTION_startDialogWithSoundArea = 51;
        static final int TRANSACTION_stopDialog = 2;
        static final int TRANSACTION_stopDialogMessage = 36;
        static final int TRANSACTION_stopDialogReason = 37;
        static final int TRANSACTION_suspendDialogWithReason = 49;
        static final int TRANSACTION_suspendSpeechWithInfo = 52;
        static final int TRANSACTION_updateCommandWakeupWord = 11;
        static final int TRANSACTION_updateMinorWakeupWord = 9;
        static final int TRANSACTION_updateShortcutWakeupWord = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWakeupEngine asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWakeupEngine)) {
                return (IWakeupEngine) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean _arg3;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    startDialog();
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    stopDialog();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    avatarClick(_arg0);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    avatarPress();
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    avatarRelease();
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    enableWakeup();
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    disableWakeup();
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result = getWakeupWords();
                    reply.writeNoException();
                    reply.writeStringArray(_result);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    String _arg1 = data.readString();
                    String _arg2 = data.readString();
                    updateMinorWakeupWord(_arg02, _arg1, _arg2, data.createStringArray());
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _result2 = getMinorWakeupWord();
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg03 = data.createStringArray();
                    String[] _arg12 = data.createStringArray();
                    String[] _arg22 = data.createStringArray();
                    String[] _arg32 = data.createStringArray();
                    String[] _arg4 = data.createStringArray();
                    updateCommandWakeupWord(_arg03, _arg12, _arg22, _arg32, _arg4);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    clearCommandWakeupWord();
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg04 = data.createStringArray();
                    String[] _arg13 = data.createStringArray();
                    String[] _arg23 = data.createStringArray();
                    String[] _arg33 = data.createStringArray();
                    String[] _arg42 = data.createStringArray();
                    addCommandWakeupWord(_arg04, _arg13, _arg23, _arg33, _arg42);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg05 = data.createStringArray();
                    removeCommandWakeupWord(_arg05);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg06 = data.createStringArray();
                    String[] _arg14 = data.createStringArray();
                    String[] _arg24 = data.createStringArray();
                    updateShortcutWakeupWord(_arg06, _arg14, _arg24);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    clearShortCutWakeupWord();
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg07 = data.createStringArray();
                    String[] _arg15 = data.createStringArray();
                    String[] _arg25 = data.createStringArray();
                    addShortcutWakeupWord(_arg07, _arg15, _arg25);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg08 = data.createStringArray();
                    removeShortcutWakeupWord(_arg08);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    pauseDialog();
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    resumeDialog();
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isEnableWakeup = isEnableWakeup();
                    reply.writeNoException();
                    reply.writeInt(isEnableWakeup ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDefaultEnableWakeup = isDefaultEnableWakeup();
                    reply.writeNoException();
                    reply.writeInt(isDefaultEnableWakeup ? 1 : 0);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    _arg3 = data.readInt() != 0;
                    setDefaultWakeupEnabled(_arg3);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg09 = data.readStrongBinder();
                    enableWakeupEnhance(_arg09);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg010 = data.readStrongBinder();
                    disableWakeupEnhance(_arg010);
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isWheelWakeupEnabled = isWheelWakeupEnabled();
                    reply.writeNoException();
                    reply.writeInt(isWheelWakeupEnabled ? 1 : 0);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg011 = data.readStrongBinder();
                    _arg3 = data.readInt() != 0;
                    setWheelWakeupEnabled(_arg011, _arg3);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDefaultEnableOneshot = isDefaultEnableOneshot();
                    reply.writeNoException();
                    reply.writeInt(isDefaultEnableOneshot ? 1 : 0);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    _arg3 = data.readInt() != 0;
                    setDefaultOneshotEnabled(_arg3);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    enableOneshot();
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    disableOneshot();
                    reply.writeNoException();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDefaultEnableFastWake = isDefaultEnableFastWake();
                    reply.writeNoException();
                    reply.writeInt(isDefaultEnableFastWake ? 1 : 0);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    _arg3 = data.readInt() != 0;
                    setDefaultFastWakeEnabled(_arg3);
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    enableFastWake();
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    disableFastWake();
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    stopDialogMessage();
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    stopDialogReason(_arg012);
                    reply.writeNoException();
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg013 = data.readStrongBinder();
                    enableMainWakeupWord(_arg013);
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg014 = data.readStrongBinder();
                    disableMainWakeupWord(_arg014);
                    reply.writeNoException();
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg015 = data.readStrongBinder();
                    enableFastWakeEnhance(_arg015);
                    reply.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg016 = data.readStrongBinder();
                    disableFastWakeEnhance(_arg016);
                    reply.writeNoException();
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg017 = data.readStrongBinder();
                    enableInterruptWake(_arg017);
                    reply.writeNoException();
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg018 = data.readStrongBinder();
                    disableInterruptWake(_arg018);
                    reply.writeNoException();
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    startDialogFrom(_arg019);
                    reply.writeNoException();
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg020 = data.readStrongBinder();
                    int _arg16 = data.readInt();
                    String _arg26 = data.readString();
                    String _arg34 = data.readString();
                    int _arg43 = data.readInt();
                    disableWakeupWithInfo(_arg020, _arg16, _arg26, _arg34, _arg43);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg021 = data.readStrongBinder();
                    int _arg17 = data.readInt();
                    String _arg27 = data.readString();
                    enableWakeupWithInfo(_arg021, _arg17, _arg27, data.readInt());
                    reply.writeNoException();
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg022 = data.readStrongBinder();
                    String _arg18 = data.readString();
                    String _arg28 = data.readString();
                    disableWheelWakeupWithInfo(_arg022, _arg18, _arg28, data.readInt());
                    reply.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg023 = data.readStrongBinder();
                    String _arg19 = data.readString();
                    int _arg29 = data.readInt();
                    enableWheelWakeupWithInfo(_arg023, _arg19, _arg29);
                    reply.writeNoException();
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg024 = data.readStrongBinder();
                    String _arg110 = data.readString();
                    String _arg210 = data.readString();
                    suspendDialogWithReason(_arg024, _arg110, _arg210);
                    reply.writeNoException();
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg025 = data.readStrongBinder();
                    String _arg111 = data.readString();
                    String _arg211 = data.readString();
                    resumeDialogWithReason(_arg025, _arg111, _arg211);
                    reply.writeNoException();
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg026 = data.readString();
                    int _arg112 = data.readInt();
                    startDialogWithSoundArea(_arg026, _arg112);
                    reply.writeNoException();
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg027 = data.readStrongBinder();
                    String _arg113 = data.readString();
                    String _arg212 = data.readString();
                    int _arg35 = data.readInt();
                    boolean _arg44 = data.readInt() != 0;
                    suspendSpeechWithInfo(_arg027, _arg113, _arg212, _arg35, _arg44);
                    reply.writeNoException();
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg028 = data.readStrongBinder();
                    String _arg114 = data.readString();
                    resumeSpeechWithInfo(_arg028, _arg114);
                    reply.writeNoException();
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    String _result3 = getSoundAreaStatus(_arg029);
                    reply.writeNoException();
                    reply.writeString(_result3);
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg030 = data.readStrongBinder();
                    String _arg115 = data.readString();
                    String _arg213 = data.readString();
                    disableSpeechWithInfo(_arg030, _arg115, _arg213, data.readInt());
                    reply.writeNoException();
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg031 = data.readStrongBinder();
                    String _arg116 = data.readString();
                    int _arg214 = data.readInt();
                    enableSpeechWithInfo(_arg031, _arg116, _arg214);
                    reply.writeNoException();
                    return true;
                case 57:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg032 = data.readStrongBinder();
                    String _arg117 = data.readString();
                    int _arg215 = data.readInt();
                    _arg3 = data.readInt() != 0;
                    resumeSpeechWithTypeInfo(_arg032, _arg117, _arg215, _arg3);
                    reply.writeNoException();
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements IWakeupEngine {
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

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void startDialog() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void stopDialog() throws RemoteException {
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

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void avatarClick(String greeting) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(greeting);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void avatarPress() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void avatarRelease() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableWakeup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableWakeup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public String[] getWakeupWords() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void updateMinorWakeupWord(String word, String pinyin, String threshold, String[] greetings) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(word);
                    _data.writeString(pinyin);
                    _data.writeString(threshold);
                    _data.writeStringArray(greetings);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public String getMinorWakeupWord() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void updateCommandWakeupWord(String[] actions, String[] words, String[] pinyin, String[] threshold, String[] greetings) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(actions);
                    _data.writeStringArray(words);
                    _data.writeStringArray(pinyin);
                    _data.writeStringArray(threshold);
                    _data.writeStringArray(greetings);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void clearCommandWakeupWord() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void addCommandWakeupWord(String[] actions, String[] words, String[] pinyin, String[] threshold, String[] greetings) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(actions);
                    _data.writeStringArray(words);
                    _data.writeStringArray(pinyin);
                    _data.writeStringArray(threshold);
                    _data.writeStringArray(greetings);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void removeCommandWakeupWord(String[] words) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(words);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void updateShortcutWakeupWord(String[] words, String[] pinyin, String[] threshold) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(words);
                    _data.writeStringArray(pinyin);
                    _data.writeStringArray(threshold);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void clearShortCutWakeupWord() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void addShortcutWakeupWord(String[] words, String[] pinyin, String[] threshold) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(words);
                    _data.writeStringArray(pinyin);
                    _data.writeStringArray(threshold);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void removeShortcutWakeupWord(String[] words) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(words);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void pauseDialog() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void resumeDialog() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public boolean isEnableWakeup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public boolean isDefaultEnableWakeup() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void setDefaultWakeupEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableWakeupEnhance(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableWakeupEnhance(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public boolean isWheelWakeupEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void setWheelWakeupEnabled(IBinder binder, boolean state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(state ? 1 : 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public boolean isDefaultEnableOneshot() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void setDefaultOneshotEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableOneshot() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableOneshot() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public boolean isDefaultEnableFastWake() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void setDefaultFastWakeEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableFastWake() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableFastWake() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void stopDialogMessage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void stopDialogReason(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableMainWakeupWord(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableMainWakeupWord(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableFastWakeEnhance(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableFastWakeEnhance(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableInterruptWake(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableInterruptWake(IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void startDialogFrom(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableWakeupWithInfo(IBinder binder, int type, String byWho, String info, int notifyType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(type);
                    _data.writeString(byWho);
                    _data.writeString(info);
                    _data.writeInt(notifyType);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableWakeupWithInfo(IBinder binder, int type, String byWho, int notifyType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(type);
                    _data.writeString(byWho);
                    _data.writeInt(notifyType);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableWheelWakeupWithInfo(IBinder binder, String byWho, String info, int notifyType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeString(info);
                    _data.writeInt(notifyType);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableWheelWakeupWithInfo(IBinder binder, String byWho, int notifyType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeInt(notifyType);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void suspendDialogWithReason(IBinder binder, String byWho, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeString(reason);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void resumeDialogWithReason(IBinder binder, String byWho, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeString(reason);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void startDialogWithSoundArea(String reason, int soundArea) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    _data.writeInt(soundArea);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void suspendSpeechWithInfo(IBinder binder, String byWho, String info, int notifyType, boolean needMic) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeString(info);
                    _data.writeInt(notifyType);
                    _data.writeInt(needMic ? 1 : 0);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void resumeSpeechWithInfo(IBinder binder, String byWho) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public String getSoundAreaStatus(int soundArea) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(soundArea);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void disableSpeechWithInfo(IBinder binder, String byWho, String info, int notifyType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeString(info);
                    _data.writeInt(notifyType);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void enableSpeechWithInfo(IBinder binder, String byWho, int notifyType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeInt(notifyType);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
            public void resumeSpeechWithTypeInfo(IBinder binder, String byWho, int notifyType, boolean removeAll) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(byWho);
                    _data.writeInt(notifyType);
                    _data.writeInt(removeAll ? 1 : 0);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
