package com.xiaopeng.speech.coreapi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.speech.common.bean.SliceData;
/* loaded from: classes23.dex */
public interface IAgent extends IInterface {
    void feedbackResult(String str, String str2) throws RemoteException;

    String getConfigData(String str) throws RemoteException;

    String getMessageBoxData(String str, String str2) throws RemoteException;

    String getRecommendData(String str) throws RemoteException;

    String getSkillData(String str) throws RemoteException;

    boolean isDefaultEnableASRInterrupt() throws RemoteException;

    boolean isDefaultEnableWelcome() throws RemoteException;

    boolean isEnableASRInterrupt() throws RemoteException;

    boolean isWheelVoiceButtonEnable() throws RemoteException;

    void leaveTrigger() throws RemoteException;

    void leaveTriggerWithID(int i) throws RemoteException;

    int newTriggerDialog(int i, int[] iArr, String str) throws RemoteException;

    void sendApiRoute(String str, String str2) throws RemoteException;

    void sendEvent(String str, String str2) throws RemoteException;

    void sendInfoFlowCardState(String str, int i) throws RemoteException;

    void sendInfoFlowStatData(int i, String str) throws RemoteException;

    void sendSceneData(String str, String str2, String str3, String str4, String str5) throws RemoteException;

    void sendScript(String str) throws RemoteException;

    void sendText(String str) throws RemoteException;

    void sendTextWithSoundArea(String str, int i) throws RemoteException;

    void sendThirdCMD(String str) throws RemoteException;

    void sendUIEvent(String str, String str2) throws RemoteException;

    void setASRInterruptEnabled(boolean z) throws RemoteException;

    void setConfigData(String str, String str2) throws RemoteException;

    void setDefaultASRInterruptEnabled(boolean z) throws RemoteException;

    void setDefaultWelcomeEnabled(boolean z) throws RemoteException;

    void setSkillData(String str) throws RemoteException;

    void setUseWheelVoiceButton(IBinder iBinder, boolean z) throws RemoteException;

    void triggerDialog(int i, int[] iArr, String str) throws RemoteException;

    void triggerEvent(String str, String str2) throws RemoteException;

    void triggerIntent(String str, String str2, String str3, String str4) throws RemoteException;

    void triggerIntentWithBinder(IBinder iBinder, String str, String str2, String str3, String str4) throws RemoteException;

    void updateDeviceInfo(String str) throws RemoteException;

    void updateVocab(String str, String[] strArr, boolean z) throws RemoteException;

    void uploadContact(String str, SliceData sliceData, int i) throws RemoteException;

    void uploadContacts(String str, String str2, int i) throws RemoteException;

    /* loaded from: classes23.dex */
    public static abstract class Stub extends Binder implements IAgent {
        private static final String DESCRIPTOR = "com.xiaopeng.speech.coreapi.IAgent";
        static final int TRANSACTION_feedbackResult = 3;
        static final int TRANSACTION_getConfigData = 22;
        static final int TRANSACTION_getMessageBoxData = 26;
        static final int TRANSACTION_getRecommendData = 17;
        static final int TRANSACTION_getSkillData = 20;
        static final int TRANSACTION_isDefaultEnableASRInterrupt = 10;
        static final int TRANSACTION_isDefaultEnableWelcome = 13;
        static final int TRANSACTION_isEnableASRInterrupt = 9;
        static final int TRANSACTION_isWheelVoiceButtonEnable = 19;
        static final int TRANSACTION_leaveTrigger = 33;
        static final int TRANSACTION_leaveTriggerWithID = 34;
        static final int TRANSACTION_newTriggerDialog = 36;
        static final int TRANSACTION_sendApiRoute = 25;
        static final int TRANSACTION_sendEvent = 2;
        static final int TRANSACTION_sendInfoFlowCardState = 28;
        static final int TRANSACTION_sendInfoFlowStatData = 24;
        static final int TRANSACTION_sendSceneData = 27;
        static final int TRANSACTION_sendScript = 14;
        static final int TRANSACTION_sendText = 1;
        static final int TRANSACTION_sendTextWithSoundArea = 35;
        static final int TRANSACTION_sendThirdCMD = 15;
        static final int TRANSACTION_sendUIEvent = 7;
        static final int TRANSACTION_setASRInterruptEnabled = 8;
        static final int TRANSACTION_setConfigData = 23;
        static final int TRANSACTION_setDefaultASRInterruptEnabled = 11;
        static final int TRANSACTION_setDefaultWelcomeEnabled = 12;
        static final int TRANSACTION_setSkillData = 21;
        static final int TRANSACTION_setUseWheelVoiceButton = 18;
        static final int TRANSACTION_triggerDialog = 32;
        static final int TRANSACTION_triggerEvent = 16;
        static final int TRANSACTION_triggerIntent = 5;
        static final int TRANSACTION_triggerIntentWithBinder = 31;
        static final int TRANSACTION_updateDeviceInfo = 4;
        static final int TRANSACTION_updateVocab = 6;
        static final int TRANSACTION_uploadContact = 30;
        static final int TRANSACTION_uploadContacts = 29;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAgent asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAgent)) {
                return (IAgent) iin;
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
            SliceData _arg12;
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    sendText(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    sendEvent(_arg02, data.readString());
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    feedbackResult(_arg03, data.readString());
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    updateDeviceInfo(_arg04);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    String _arg13 = data.readString();
                    String _arg2 = data.readString();
                    String _arg3 = data.readString();
                    triggerIntent(_arg05, _arg13, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    String[] _arg14 = data.createStringArray();
                    _arg1 = data.readInt() != 0;
                    updateVocab(_arg06, _arg14, _arg1);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    sendUIEvent(_arg07, data.readString());
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setASRInterruptEnabled(_arg1);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isEnableASRInterrupt = isEnableASRInterrupt();
                    reply.writeNoException();
                    reply.writeInt(isEnableASRInterrupt ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDefaultEnableASRInterrupt = isDefaultEnableASRInterrupt();
                    reply.writeNoException();
                    reply.writeInt(isDefaultEnableASRInterrupt ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setDefaultASRInterruptEnabled(_arg1);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    _arg1 = data.readInt() != 0;
                    setDefaultWelcomeEnabled(_arg1);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isDefaultEnableWelcome = isDefaultEnableWelcome();
                    reply.writeNoException();
                    reply.writeInt(isDefaultEnableWelcome ? 1 : 0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    sendScript(_arg08);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    sendThirdCMD(_arg09);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    triggerEvent(_arg010, data.readString());
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    String _result = getRecommendData(_arg011);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg012 = data.readStrongBinder();
                    _arg1 = data.readInt() != 0;
                    setUseWheelVoiceButton(_arg012, _arg1);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    boolean isWheelVoiceButtonEnable = isWheelVoiceButtonEnable();
                    reply.writeNoException();
                    reply.writeInt(isWheelVoiceButtonEnable ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    String _result2 = getSkillData(_arg013);
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    setSkillData(_arg014);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    String _result3 = getConfigData(_arg015);
                    reply.writeNoException();
                    reply.writeString(_result3);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg016 = data.readString();
                    setConfigData(_arg016, data.readString());
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    sendInfoFlowStatData(_arg017, data.readString());
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg018 = data.readString();
                    sendApiRoute(_arg018, data.readString());
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    String _result4 = getMessageBoxData(_arg019, data.readString());
                    reply.writeNoException();
                    reply.writeString(_result4);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg020 = data.readString();
                    String _arg15 = data.readString();
                    String _arg22 = data.readString();
                    String _arg32 = data.readString();
                    String _arg4 = data.readString();
                    sendSceneData(_arg020, _arg15, _arg22, _arg32, _arg4);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg021 = data.readString();
                    sendInfoFlowCardState(_arg021, data.readInt());
                    reply.writeNoException();
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg022 = data.readString();
                    String _arg16 = data.readString();
                    int _arg23 = data.readInt();
                    uploadContacts(_arg022, _arg16, _arg23);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg023 = data.readString();
                    if (data.readInt() != 0) {
                        _arg12 = SliceData.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    int _arg24 = data.readInt();
                    uploadContact(_arg023, _arg12, _arg24);
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg024 = data.readStrongBinder();
                    String _arg17 = data.readString();
                    String _arg25 = data.readString();
                    String _arg33 = data.readString();
                    String _arg42 = data.readString();
                    triggerIntentWithBinder(_arg024, _arg17, _arg25, _arg33, _arg42);
                    reply.writeNoException();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    int[] _arg18 = data.createIntArray();
                    String _arg26 = data.readString();
                    triggerDialog(_arg025, _arg18, _arg26);
                    reply.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    leaveTrigger();
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    leaveTriggerWithID(_arg026);
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg027 = data.readString();
                    sendTextWithSoundArea(_arg027, data.readInt());
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    int[] _arg19 = data.createIntArray();
                    String _arg27 = data.readString();
                    int _result5 = newTriggerDialog(_arg028, _arg19, _arg27);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes23.dex */
        private static class Proxy implements IAgent {
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

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendText(String text) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendEvent(String event, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    _data.writeString(data);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void feedbackResult(String event, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    _data.writeString(data);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void updateDeviceInfo(String deviceJson) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(deviceJson);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void triggerIntent(String skill, String task, String intent, String slots) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(skill);
                    _data.writeString(task);
                    _data.writeString(intent);
                    _data.writeString(slots);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void updateVocab(String vocabName, String[] contents, boolean addOrDelete) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(vocabName);
                    _data.writeStringArray(contents);
                    _data.writeInt(addOrDelete ? 1 : 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendUIEvent(String event, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    _data.writeString(data);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void setASRInterruptEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public boolean isEnableASRInterrupt() throws RemoteException {
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

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public boolean isDefaultEnableASRInterrupt() throws RemoteException {
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

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void setDefaultASRInterruptEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void setDefaultWelcomeEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public boolean isDefaultEnableWelcome() throws RemoteException {
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

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendScript(String text) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendThirdCMD(String cmd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cmd);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void triggerEvent(String event, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(event);
                    _data.writeString(data);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public String getRecommendData(String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void setUseWheelVoiceButton(IBinder binder, boolean useVoiceButton) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(useVoiceButton ? 1 : 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public boolean isWheelVoiceButtonEnable() throws RemoteException {
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

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public String getSkillData(String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void setSkillData(String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(data);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public String getConfigData(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void setConfigData(String key, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(data);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendInfoFlowStatData(int eventId, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(eventId);
                    _data.writeString(data);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendApiRoute(String type, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeString(data);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public String getMessageBoxData(String type, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeString(data);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendSceneData(String sceneId, String appName, String appVersion, String sceneData, String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sceneId);
                    _data.writeString(appName);
                    _data.writeString(appVersion);
                    _data.writeString(sceneData);
                    _data.writeString(type);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendInfoFlowCardState(String type, int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    _data.writeInt(state);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void uploadContacts(String vocabName, String contents, int operation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(vocabName);
                    _data.writeString(contents);
                    _data.writeInt(operation);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void uploadContact(String vocabName, SliceData contents, int operation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(vocabName);
                    if (contents != null) {
                        _data.writeInt(1);
                        contents.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(operation);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void triggerIntentWithBinder(IBinder binder, String skill, String task, String intent, String slots) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(skill);
                    _data.writeString(task);
                    _data.writeString(intent);
                    _data.writeString(slots);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void triggerDialog(int triggerID, int[] soundArea, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(triggerID);
                    _data.writeIntArray(soundArea);
                    _data.writeString(data);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void leaveTrigger() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void leaveTriggerWithID(int triggerID) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(triggerID);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public void sendTextWithSoundArea(String text, int soundArea) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    _data.writeInt(soundArea);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.xiaopeng.speech.coreapi.IAgent
            public int newTriggerDialog(int triggerID, int[] soundArea, String data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(triggerID);
                    _data.writeIntArray(soundArea);
                    _data.writeString(data);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
