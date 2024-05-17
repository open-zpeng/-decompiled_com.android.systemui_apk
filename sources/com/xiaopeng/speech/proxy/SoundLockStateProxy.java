package com.xiaopeng.speech.proxy;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISoundLockState;
import com.xiaopeng.speech.ISpeechEngine;
/* loaded from: classes23.dex */
public class SoundLockStateProxy extends ISoundLockState.Stub implements ConnectManager.OnConnectCallback {
    private ISoundLockState mSoundLockState;

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mSoundLockState = speechEngine.getSoundLockState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mSoundLockState = null;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public int getDriveSoundLocation() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.getDriveSoundLocation();
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setDriveSoundLocation(int location) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setDriveSoundLocation(location);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setSoundLocationAngle(int angle) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setSoundLocationAngle(angle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public int getSoundLocationAngle() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.getSoundLocationAngle();
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public boolean isDefaultEnableSoundLocation() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.isDefaultEnableSoundLocation();
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setDefaultSoundLocationEnabled(boolean enable) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setDefaultSoundLocationEnabled(enable);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setSupportSoundLock(boolean enable) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setSupportSoundLock(enable);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public boolean isSupportSoundLock() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.isSupportSoundLock();
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public int getSoundSourceAngle() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.getSoundSourceAngle();
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setVoiceLockAngle(int start, int end) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setVoiceLockAngle(start, end);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setDspMode(int mode) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setDspMode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public int getDspMode() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.getDspMode();
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setData(int action, long address, int value) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setData(action, address, value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setDefaultSoundLockEnabled(boolean enabled) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setDefaultSoundLockEnabled(enabled);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public boolean isDefaultEnableSoundLock() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.isDefaultEnableSoundLock();
            } catch (RemoteException e) {
                e.printStackTrace();
                return true;
            }
        }
        return true;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void lockSoundLocation(int angle) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.lockSoundLocation(angle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void lockSoundLocationByWakeup(int angle) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.lockSoundLocationByWakeup(angle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setMode(int mode) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setMode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public int getMode() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.getMode();
            } catch (RemoteException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void setIsNeedResetSoundLock(boolean needReset) {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.setIsNeedResetSoundLock(needReset);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public boolean isVoicePositionSet() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                return iSoundLockState.isVoicePositionSet();
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override // com.xiaopeng.speech.ISoundLockState
    public void initSoundConfig() {
        ISoundLockState iSoundLockState = this.mSoundLockState;
        if (iSoundLockState != null) {
            try {
                iSoundLockState.initSoundConfig();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
