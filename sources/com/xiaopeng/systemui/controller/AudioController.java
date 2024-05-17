package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.OsdController;
/* loaded from: classes24.dex */
public class AudioController extends BroadcastReceiver {
    private static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_STREAM_FLAG = "android.media.EXTRA_VOLUME_STREAM_FLAG";
    private static final String EXTRA_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    private static final String KEY_AVAS_SPEAKER = "avas_speaker";
    public static final int STREAM_TYPE_PASSENGER = 13;
    private static final String TAG = "VolumeController";
    private static final int VOLUME_MAX = 30;
    private static AudioController sAudioController = null;
    private AudioManager mAudioManager;
    private boolean mAvasEnable;
    private Context mContext;
    private OnVolumeListener mVolumeListener;
    private boolean mActiveStreamLocked = false;
    private ContentObserver mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.controller.AudioController.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor("avas_speaker"))) {
                AudioController audioController = AudioController.this;
                audioController.mAvasEnable = Settings.System.getInt(audioController.mContext.getContentResolver(), "avas_speaker", 0) == 1;
                Log.d(AudioController.TAG, "AudioController onChange avasStreamEnable : " + AudioController.this.mAvasEnable);
                if (AudioController.this.mVolumeListener != null) {
                    AudioController.this.mVolumeListener.onAvasStreamEnabled(AudioController.this.mAvasEnable);
                    int streamType = AudioController.this.mAvasEnable ? 11 : 3;
                    AudioController.this.mVolumeListener.onVolumeChanged(streamType, AudioController.this.getStreamVolume(streamType));
                }
            }
        }
    };
    private Handler mHandler = new WorkHandler(Looper.getMainLooper());

    /* loaded from: classes24.dex */
    public interface OnVolumeListener {
        void onAvasStreamEnabled(boolean z);

        void onMicrophoneMuteChanged();

        void onVolumeChanged(int i, int i2);
    }

    public boolean isAvasStreamEnabled() {
        return this.mAvasEnable;
    }

    public AudioController(Context context) {
        this.mAvasEnable = false;
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("avas_speaker"), true, this.mCallbackObserver);
        this.mAvasEnable = Settings.System.getInt(this.mContext.getContentResolver(), "avas_speaker", 0) == 1;
    }

    public static AudioController getInstance(Context context) {
        if (sAudioController == null) {
            synchronized (AudioController.class) {
                if (sAudioController == null) {
                    sAudioController = new AudioController(context);
                }
            }
        }
        return sAudioController;
    }

    public void start() {
        registerReceiver();
        looperInitWhenConnected();
    }

    public void stop() {
        unregisterReceiver();
    }

    public boolean isMicrophoneMute() {
        return this.mAudioManager.isMicrophoneMute();
    }

    public void setVolumeListener(OnVolumeListener listener) {
        this.mVolumeListener = listener;
    }

    public synchronized void handleVolumeKeyEvent(KeyEvent event) {
        if (event != null) {
            int action = event.getAction();
            int keycode = event.getKeyCode();
            Logger.d(TAG, "handleVolumeKeyEvent keycode=" + keycode);
            if (keycode == 164 && action == 0) {
                notifyMuteOsd();
            }
        }
    }

    public void notifyMuteOsd() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.AudioController.2
            @Override // java.lang.Runnable
            public void run() {
                boolean mute = AudioController.this.mAudioManager.isStreamMute(3);
                int volume = AudioController.this.getStreamVolume(3);
                Logger.d(AudioController.TAG, "notifyMuteOsd volume=" + volume + " mute=" + mute);
                int volumeMax = AudioController.this.getStreamMaxVolume(3);
                OsdController.notifyMuteOsd(AudioController.this.mContext, volume, volumeMax, mute);
            }
        }, 100L);
    }

    public void showVolumeOsd() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.AudioController.3
            @Override // java.lang.Runnable
            public void run() {
                int volume = AudioController.this.getStreamVolume(3);
                Logger.d(AudioController.TAG, "showVolumeOsd volume=" + volume);
                int volumeMax = AudioController.this.getStreamMaxVolume(3);
                OsdController.notifyMuteOsd(AudioController.this.mContext, volume, volumeMax, false);
            }
        }, 100L);
    }

    public void checkToHideMuteOsd() {
        boolean mute = this.mAudioManager.isStreamMute(getCurrentStreamType());
        if (mute) {
            OsdController.getInstance(this.mContext).hideOsd();
        }
    }

    private void onVolumeChanged(int streamType, int volume, int streamFlag) {
        int volumeMax = getStreamMaxVolume(streamType);
        Logger.d(TAG, "onVolumeChanged streamType=" + streamType + " volume=" + volume + " volumeMax=" + volumeMax);
        if (streamType != 13) {
            boolean showUI = (streamFlag & 1025) != 0;
            boolean needUpdateOnVolumeChanged = OsdController.getInstance(this.mContext).isOsdShown() && OsdController.getInstance(this.mContext).getOsdType() == 1;
            Logger.d(TAG, "onVolumeChanged showUI=" + showUI + " needUpdateOnVolumeChanged=" + needUpdateOnVolumeChanged);
            if (isStreamTypeSupport(streamType) && (showUI || needUpdateOnVolumeChanged)) {
                if (CarModelsManager.getFeature().isOsdReduceSelfUse()) {
                    if (streamType == 3 && this.mAudioManager.isStreamMute(3)) {
                        OsdController.notifyMuteOsd(this.mContext, 0, volumeMax, true);
                    } else {
                        OsdController.notifyVolumeOsd(this.mContext, streamType, volume, volumeMax);
                    }
                } else {
                    boolean mute = this.mAudioManager.isStreamMute(streamType);
                    Logger.d(TAG, "onVolumeChanged mute=" + mute + " , streamType:" + streamType);
                    if (mute) {
                        notifyMuteOsd();
                    } else {
                        OsdController.notifyVolumeOsd(this.mContext, streamType, volume, volumeMax);
                    }
                }
                postStreamLockMessageDelayed(true, 0L);
                postStreamLockMessageDelayed(false, OsdController.TN.DURATION_TIMEOUT_LONG);
            }
        }
        OnVolumeListener onVolumeListener = this.mVolumeListener;
        if (onVolumeListener != null) {
            onVolumeListener.onVolumeChanged(streamType, volume);
        }
    }

    private void onMicrophoneMuteChanged() {
        OnVolumeListener onVolumeListener = this.mVolumeListener;
        if (onVolumeListener != null) {
            onVolumeListener.onMicrophoneMuteChanged();
        }
    }

    private void registerReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.media.VOLUME_CHANGED_ACTION");
            filter.addAction("android.media.action.MICROPHONE_MUTE_CHANGED");
            this.mContext.registerReceiver(this, filter);
        } catch (Exception e) {
        }
    }

    private void unregisterReceiver() {
        try {
            this.mContext.unregisterReceiver(this);
        } catch (Exception e) {
        }
    }

    private void postStreamLockMessageDelayed(boolean lock, long delay) {
        if (lock) {
            if (!this.mActiveStreamLocked) {
                this.mHandler.removeMessages(101);
                this.mHandler.sendEmptyMessageDelayed(101, delay);
                return;
            }
            return;
        }
        this.mHandler.removeMessages(102);
        this.mHandler.sendEmptyMessageDelayed(102, delay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStreamLockEvent(int what) {
        if (what == 101) {
            this.mAudioManager.lockActiveStream(true);
            this.mActiveStreamLocked = true;
        } else if (what == 102) {
            this.mAudioManager.lockActiveStream(false);
            this.mActiveStreamLocked = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void looperInitWhenConnected() {
        OnVolumeListener onVolumeListener = this.mVolumeListener;
        if (onVolumeListener != null) {
            onVolumeListener.onVolumeChanged(3, this.mAudioManager.getStreamVolume(3));
            try {
                int psnVolume = this.mAudioManager.getStreamVolume(13);
                this.mVolumeListener.onVolumeChanged(13, psnVolume);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isStreamMute() {
        AudioManager audioManager = this.mAudioManager;
        return audioManager != null && audioManager.isStreamMute(3);
    }

    public boolean isStreamMute(int type) {
        AudioManager audioManager = this.mAudioManager;
        return audioManager != null && audioManager.isStreamMute(type);
    }

    public int getStreamVolume(int type) {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            return audioManager.getStreamVolume(type);
        }
        return -1;
    }

    public int getStreamMaxVolume(int type) {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(type);
        }
        return 15;
    }

    public int getCurrentStreamType() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            return audioManager.getCurrentAudioFocusAttributes().getVolumeControlStream();
        }
        return 3;
    }

    public void setVolume(int streamType, int volume) {
        Logger.d(TAG, "setVolume : streamType = " + streamType + " volume = " + volume);
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.setStreamVolume(streamType, volume, 0);
        }
    }

    public int getMusicVolumeMax(int displayId) {
        return 30;
    }

    public int getMusicVolume(int displayId) {
        try {
            if (this.mAudioManager != null) {
                if (displayId == 1) {
                    int psnBluetoothState = BluetoothController.getInstance().getPsnBluetoothState();
                    if (psnBluetoothState == 2) {
                        Logger.d(TAG, "psn volume = " + this.mAudioManager.getStreamVolume(13));
                        return this.mAudioManager.getStreamVolume(13);
                    }
                    int driverVolume = getDriverMusicVolume();
                    Logger.d(TAG, "driver volume = " + driverVolume);
                    return driverVolume;
                }
                return getDriverMusicVolume();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int getDriverMusicVolume() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            return audioManager.getStreamVolume(this.mAvasEnable ? 11 : 3);
        }
        return 0;
    }

    public void setMusicVolume(int displayId, int volume) {
        Logger.d(TAG, "setMusicVolume : displayId = " + displayId + " volume = " + volume);
        try {
            if (this.mAudioManager != null) {
                if (displayId == 1) {
                    int psnBluetoothState = BluetoothController.getInstance().getPsnBluetoothState();
                    Logger.d(TAG, "setMusicVolume : psnBluetoothState = " + psnBluetoothState);
                    if (psnBluetoothState == 2) {
                        this.mAudioManager.setStreamVolume(13, volume, 0);
                    } else {
                        setMediaVolume(volume);
                    }
                } else {
                    setMediaVolume(volume);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaVolume(int volume) {
        if (this.mAvasEnable) {
            Logger.d(TAG, "setMediaVolume avas : 11");
            this.mAudioManager.setStreamVolume(11, volume, 0);
            return;
        }
        Logger.d(TAG, "setMediaVolume music : 3");
        this.mAudioManager.setStreamVolume(3, volume, 0);
    }

    public static boolean isStreamTypeSupport(int type) {
        boolean support = false;
        if (CarModelsManager.getFeature().isOsdReduceSelfUse()) {
            if (type == 3 || type == 6 || type == 11) {
                support = true;
            }
        } else {
            if (type != 0 && type != 6 && type != 2 && type != 3) {
                switch (type) {
                }
            }
            support = true;
        }
        Logger.d(TAG, "isStreamTypeSupport type=" + type + " support=" + support);
        return support;
    }

    public static int getStreamVolumeLevel(int volume, int maxVolume) {
        if (volume <= 0) {
            return 0;
        }
        if (volume > 0 && volume <= maxVolume / 3) {
            return 1;
        }
        if (volume > maxVolume / 3 && volume <= (maxVolume * 2) / 3) {
            return 2;
        }
        if (volume <= (maxVolume * 2) / 3 || volume <= maxVolume) {
            return 3;
        }
        return 3;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        boolean z;
        String action = intent.getAction();
        int hashCode = action.hashCode();
        if (hashCode != -1940635523) {
            if (hashCode == 835336980 && action.equals("android.media.action.MICROPHONE_MUTE_CHANGED")) {
                z = true;
            }
            z = true;
        } else {
            if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                z = false;
            }
            z = true;
        }
        if (z) {
            if (z) {
                onMicrophoneMuteChanged();
                return;
            }
            return;
        }
        int streamFlag = intent.getIntExtra(EXTRA_STREAM_FLAG, 0);
        int streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
        int streamVolume = getStreamVolume(streamType);
        onVolumeChanged(streamType, streamVolume, streamFlag);
    }

    /* loaded from: classes24.dex */
    private class WorkHandler extends Handler {
        public static final int MSG_INIT_VOLUME = 100;
        public static final int MSG_STREAM_LOCKED = 101;
        public static final int MSG_STREAM_UNLOCK = 102;

        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    AudioController.this.looperInitWhenConnected();
                    return;
                case 101:
                case 102:
                    AudioController.this.handleStreamLockEvent(msg.what);
                    return;
                default:
                    return;
            }
        }
    }
}
