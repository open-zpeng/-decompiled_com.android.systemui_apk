package com.xiaopeng.systemui.viewmodel.volume;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.BluetoothController;
import com.xiaopeng.systemui.viewmodel.IViewModel;
/* loaded from: classes24.dex */
public class AudioViewModel implements IViewModel, AudioController.OnVolumeListener, BluetoothController.PsnBluetoothCallback {
    private static final String TAG = "AudioViewModel";
    private AudioController mAudioController;
    private BluetoothController mBluetoothController;
    private Context mContext;
    private final MutableLiveData<Integer> mVolume = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPsnVolume = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mMicrophoneMuteState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mAvasStreamEnableState = new MutableLiveData<>();

    public AudioViewModel(Context context) {
        this.mContext = context;
        initLiveData();
    }

    public void initViewModel() {
        this.mAudioController = AudioController.getInstance(this.mContext);
        this.mAudioController.setVolumeListener(this);
        this.mAudioController.start();
        this.mBluetoothController = BluetoothController.getInstance();
        this.mBluetoothController.addPsnBluetoothCallback(this);
        initLiveData();
    }

    private void initLiveData() {
        AudioController audioController = this.mAudioController;
        if (audioController == null) {
            this.mVolume.setValue(0);
            this.mMicrophoneMuteState.setValue(false);
            this.mPsnVolume.setValue(0);
            this.mAvasStreamEnableState.setValue(false);
            return;
        }
        this.mVolume.setValue(Integer.valueOf(audioController.getMusicVolume(0)));
        this.mMicrophoneMuteState.setValue(Boolean.valueOf(this.mAudioController.isMicrophoneMute()));
        this.mPsnVolume.setValue(Integer.valueOf(this.mAudioController.getMusicVolume(1)));
        this.mAvasStreamEnableState.setValue(Boolean.valueOf(this.mAudioController.isAvasStreamEnabled()));
    }

    @Override // com.xiaopeng.systemui.controller.AudioController.OnVolumeListener
    public void onVolumeChanged(int streamType, int volume) {
        Logger.d(TAG, "onVolumeChanged : " + streamType + "," + volume);
        if (streamType == 3 || streamType == 11) {
            this.mPsnVolume.postValue(Integer.valueOf(AudioController.getInstance(this.mContext).getMusicVolume(1)));
            this.mVolume.postValue(Integer.valueOf(volume));
        } else if (streamType == 13) {
            this.mPsnVolume.postValue(Integer.valueOf(AudioController.getInstance(this.mContext).getMusicVolume(1)));
        }
    }

    @Override // com.xiaopeng.systemui.controller.AudioController.OnVolumeListener
    public void onMicrophoneMuteChanged() {
        AudioController audioController = this.mAudioController;
        if (audioController != null) {
            this.mMicrophoneMuteState.postValue(Boolean.valueOf(audioController.isMicrophoneMute()));
        }
    }

    @Override // com.xiaopeng.systemui.controller.AudioController.OnVolumeListener
    public void onAvasStreamEnabled(boolean enable) {
        this.mAvasStreamEnableState.postValue(Boolean.valueOf(enable));
    }

    public MutableLiveData<Integer> getPsnVolumeData() {
        return this.mPsnVolume;
    }

    public int getPsnVolume() {
        return this.mPsnVolume.getValue().intValue();
    }

    public int getMusicVolume() {
        return this.mVolume.getValue().intValue();
    }

    public boolean isAvasStreamEnable() {
        return this.mAvasStreamEnableState.getValue().booleanValue();
    }

    public void setPsnVolume(int volume) {
        AudioController.getInstance(this.mContext).setMusicVolume(1, volume);
    }

    public MutableLiveData<Boolean> getMicrophoneMuteState() {
        return this.mMicrophoneMuteState;
    }

    @Override // com.xiaopeng.systemui.controller.BluetoothController.PsnBluetoothCallback
    public void onPsnBluetoothStateChanged(int state) {
        this.mPsnVolume.postValue(Integer.valueOf(AudioController.getInstance(this.mContext).getMusicVolume(1)));
    }

    public MutableLiveData<Boolean> getAvasStreamEnableState() {
        return this.mAvasStreamEnableState;
    }

    public MutableLiveData<Integer> getMusicVolumeData() {
        return this.mVolume;
    }

    public void setMusicVolume(int value) {
        this.mAudioController.setMusicVolume(0, value);
    }
}
