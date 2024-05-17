package com.xiaopeng.systemui.infoflow.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.xuimanager.XUIManager;
import com.xiaopeng.xuimanager.XUIServiceNotConnectedException;
import com.xiaopeng.xuimanager.mediacenter.MediaCenterManager;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import com.xiaopeng.xuimanager.mediacenter.SDPlaybackListener;
import com.xiaopeng.xuimanager.mediacenter.lyric.LyricInfo;
import com.xiaopeng.xuimanager.mediacenter.lyric.LyricInfoListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes24.dex */
public class MediaManager {
    private static final String TAG = "MediaManager";
    private static volatile MediaManager mInstance;
    private Context mContext;
    private MediaCenterManager mMediaCenterManager;
    private MediaCenterManager.VisualCaptureListener mVisualCaptureListener;
    private XUIManager mXUIManager;
    private ArrayList<OnPlayStatusChangedListener> mOnPlayStatusChangedListeners = new ArrayList<>();
    private ArrayList<OnMediaInfoChangedListener> mOnMediaInfoChangedListener = new ArrayList<>();
    private ArrayList<OnPlayPositionChangedListener> mOnPlayPositionChangedListener = new ArrayList<>();
    private ArrayList<OnPlayStatusChangedListener> mOnSecondaryPlayStatusChangedListeners = new ArrayList<>();
    private ArrayList<OnMediaInfoChangedListener> mOnSecondaryMediaInfoChangedListeners = new ArrayList<>();
    private ArrayList<OnPlayPositionChangedListener> mOnSecondaryPlayPositionChangedListeners = new ArrayList<>();
    private ArrayList<OnFftDataCaptureListener> mOnFftDataCaptureListeners = new ArrayList<>();
    private List<OnVisualizerViewEnableListener> mVisualizerViewEnableListeners = new ArrayList();
    private ArrayList<OnLyricUpdatedListener> mLyricUpdatedListeners = new ArrayList<>();
    private SDPlaybackListener mPlaybackListener = new SDPlaybackListener() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.1
        public void OnPlaybackChanged(final int displayId, final int status) {
            Logger.i(MediaManager.TAG, "OnPlaybackChanged : displayId = " + displayId + " status = " + status);
            if (MediaManager.this.mOnPlayStatusChangedListeners.size() > 0) {
                if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                    MediaManager.this.notifyMediaStatusChanged(displayId, status);
                } else {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MediaManager.this.notifyMediaStatusChanged(displayId, status);
                        }
                    });
                }
            }
        }

        public void OnUpdatePosition(final int displayId, final long position, final long duration) {
            Logger.d(MediaManager.TAG, "OnUpdatePosition : displayId = " + displayId + " position = " + position);
            if (MediaManager.this.mOnPlayPositionChangedListener.size() > 0) {
                if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                    MediaManager.this.notifyMediaPositionChanged(displayId, position, duration);
                } else {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.1.2
                        @Override // java.lang.Runnable
                        public void run() {
                            MediaManager.this.notifyMediaPositionChanged(displayId, position, duration);
                        }
                    });
                }
            }
        }

        public void OnMediaInfoNotify(final int displayId, final MediaInfo mediaInfo) {
            Logger.i(MediaManager.TAG, "OnMediaInfoNotify : displayId = " + displayId + " title = " + mediaInfo.getTitle());
            if (MediaManager.this.mOnMediaInfoChangedListener.size() > 0) {
                if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                    MediaManager.this.notifyMediaInfoChanged(displayId, mediaInfo);
                } else {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.1.3
                        @Override // java.lang.Runnable
                        public void run() {
                            MediaManager.this.notifyMediaInfoChanged(displayId, mediaInfo);
                        }
                    });
                }
            }
        }
    };
    private LyricInfoListener mLyricInfoListener = new LyricInfoListener() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.2
        public void onLyricInfoUpdated(final int displayId, final LyricInfo lyricInfo) {
            if (!MediaManager.this.mLyricUpdatedListeners.isEmpty()) {
                if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                    MediaManager.this.notifyLyricUpdated(displayId, lyricInfo);
                } else {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MediaManager.this.notifyLyricUpdated(displayId, lyricInfo);
                        }
                    });
                }
            }
        }
    };
    private final ServiceConnection mXUIServiceConnectionCb = new ServiceConnection() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.3
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(MediaManager.TAG, "xuiservice onServiceConnected");
            try {
                MediaManager.this.mMediaCenterManager = (MediaCenterManager) MediaManager.this.mXUIManager.getXUIServiceManager("mediacenter");
                MediaManager.this.registerListener();
                MediaManager.this.notifyMusicCard();
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.w(MediaManager.TAG, "xuiservice onServiceDisconnected");
            MediaManager.this.reconnectToXuiService();
        }
    };

    /* loaded from: classes24.dex */
    public interface OnFftDataCaptureListener {
        void onFftData(byte[] bArr, int i);
    }

    /* loaded from: classes24.dex */
    public interface OnLyricUpdatedListener {
        void onLyricUpdated(int i, LyricInfo lyricInfo);
    }

    /* loaded from: classes24.dex */
    public interface OnMediaInfoChangedListener {
        void onInfoChanged(MediaInfo mediaInfo);
    }

    /* loaded from: classes24.dex */
    public interface OnPlayPositionChangedListener {
        void onPositionChanged(long j, long j2);
    }

    /* loaded from: classes24.dex */
    public interface OnPlayStatusChangedListener {
        void onStatusChanged(int i);
    }

    /* loaded from: classes24.dex */
    public interface OnVisualizerViewEnableListener {
        void onViewEnable(boolean z);
    }

    private MediaManager() {
    }

    public static MediaManager getInstance() {
        if (mInstance == null) {
            synchronized (MediaManager.class) {
                if (mInstance == null) {
                    mInstance = new MediaManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        this.mContext = context;
        connectToXuiService();
    }

    private void connectToXuiService() {
        this.mXUIManager = XUIManager.createXUIManager(this.mContext, this.mXUIServiceConnectionCb);
        Log.i(TAG, "Start to connect XUI service");
        this.mXUIManager.connect();
    }

    public MediaCenterManager getMediaCenterManager() {
        return this.mMediaCenterManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconnectToXuiService() {
        connectToXuiService();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerListener() {
        if (this.mMediaCenterManager != null) {
            this.mVisualCaptureListener = new MediaCenterManager.VisualCaptureListener() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.4
                public void OnFftDataCapture(final byte[] bytes, final int i) {
                    if (MediaManager.this.mOnFftDataCaptureListeners.size() > 0) {
                        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.4.1
                            @Override // java.lang.Runnable
                            public void run() {
                                MediaManager.this.notifyFFTDataChanged(bytes, i);
                            }
                        });
                    }
                }
            };
            try {
                this.mMediaCenterManager.registerPlaybackListener(0, this.mPlaybackListener);
                this.mMediaCenterManager.registerPlaybackListener(1, this.mPlaybackListener);
                if (!this.mLyricUpdatedListeners.isEmpty()) {
                    this.mMediaCenterManager.registerLyricInfoListener(this.mLyricInfoListener);
                }
                this.mMediaCenterManager.registerVisualizerViewEnableListener(new MediaCenterManager.VisualizerViewEnableListener() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.5
                    public void OnVisualizerViewEnable(final boolean enabled) {
                        if (MediaManager.this.mVisualizerViewEnableListeners.size() > 0) {
                            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.5.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    MediaManager.this.notifyVisualizerViewEnabled(enabled);
                                }
                            });
                        }
                    }
                });
            } catch (XUIServiceNotConnectedException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMusicCard() {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                final MediaInfo currentInfo0 = mediaCenterManager.getCurrentMediaInfo(0);
                final MediaInfo currentInfo1 = this.mMediaCenterManager.getCurrentMediaInfo(1);
                if (this.mOnMediaInfoChangedListener.size() > 0) {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.6
                        @Override // java.lang.Runnable
                        public void run() {
                            MediaManager.this.notifyMediaInfoChanged(0, currentInfo0);
                            MediaManager.this.notifyMediaInfoChanged(1, currentInfo1);
                        }
                    });
                }
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyFFTDataChanged(byte[] fftData, int sampleRate) {
        if (this.mOnFftDataCaptureListeners.size() > 0) {
            Iterator<OnFftDataCaptureListener> it = this.mOnFftDataCaptureListeners.iterator();
            while (it.hasNext()) {
                OnFftDataCaptureListener listener = it.next();
                listener.onFftData(fftData, sampleRate);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMediaInfoChanged(int displayId, MediaInfo mediaInfo) {
        if (displayId == 0) {
            notifyMediaInfoChanged(this.mOnMediaInfoChangedListener, mediaInfo);
        } else {
            notifyMediaInfoChanged(this.mOnSecondaryMediaInfoChangedListeners, mediaInfo);
        }
    }

    private void notifyMediaInfoChanged(List<OnMediaInfoChangedListener> listeners, MediaInfo mediaInfo) {
        if (listeners.size() > 0) {
            for (OnMediaInfoChangedListener listener : listeners) {
                listener.onInfoChanged(mediaInfo);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMediaStatusChanged(int displayId, int status) {
        if (displayId == 0) {
            notifyMediaStatusChanged(this.mOnPlayStatusChangedListeners, status);
        } else {
            notifyMediaStatusChanged(this.mOnSecondaryPlayStatusChangedListeners, status);
        }
    }

    private void notifyMediaStatusChanged(List<OnPlayStatusChangedListener> listeners, int status) {
        if (listeners.size() <= 0 || status == 10) {
            return;
        }
        for (OnPlayStatusChangedListener listener : listeners) {
            listener.onStatusChanged(status);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyVisualizerViewEnabled(boolean enable) {
        if (this.mVisualizerViewEnableListeners.size() > 0) {
            for (OnVisualizerViewEnableListener listener : this.mVisualizerViewEnableListeners) {
                listener.onViewEnable(enable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMediaPositionChanged(int displayId, long position, long duration) {
        if (displayId == 0) {
            notifyMediaPositionChanged(this.mOnPlayPositionChangedListener, position, duration);
        } else {
            notifyMediaPositionChanged(this.mOnSecondaryPlayPositionChangedListeners, position, duration);
        }
    }

    private void notifyMediaPositionChanged(List<OnPlayPositionChangedListener> listeners, long position, long duration) {
        if (listeners.size() > 0) {
            for (OnPlayPositionChangedListener listener : listeners) {
                listener.onPositionChanged(position, duration);
            }
        }
    }

    public MediaInfo getCurrentMediaInfo() {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                return mediaCenterManager.getCurrentMediaInfo();
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public MediaInfo getCurrentMediaInfo(int displayId) {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                return mediaCenterManager.getCurrentMediaInfo(displayId);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public LyricInfo getCurrentLyricInfo(int displayId) {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                return mediaCenterManager.getCurrentLyricInfo(displayId);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void setFavorite(boolean favorite, String id) {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                mediaCenterManager.setFavorite(favorite, id);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentPlayStatus() {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                return mediaCenterManager.getCurrentPlayStatus();
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
                return 2;
            }
        }
        return 2;
    }

    public int getCurrentPlayStatus(int displayId) {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                return mediaCenterManager.getCurrentPlayStatus(displayId);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
                return 2;
            }
        }
        return 2;
    }

    public void seekTo(int position) {
        playbackControl(4, position);
    }

    public void pause() {
        pause(0);
    }

    public void pause(int displayId) {
        playbackControl(displayId, 2, 0);
    }

    public void play() {
        play(0);
    }

    public void play(int displayId) {
        playbackControl(displayId, 2, 0);
    }

    public void next() {
        next(0);
    }

    public void next(int displayId) {
        playbackControl(displayId, 6, 0);
    }

    public void previous() {
        previous(0);
    }

    public void previous(int displayId) {
        playbackControl(displayId, 7, 0);
    }

    public void enterMusicApp(int arg) {
        enterMusicAppWithDisplayId(0, arg);
    }

    public void enterMusicAppWithDisplayId(int displayId, int arg) {
        playbackControl(displayId, 11, arg);
    }

    public void enterMusicApp() {
        enterMusicAppWithDisplayId(0);
    }

    public void enterMusicAppWithDisplayId(int displayId) {
        playbackControl(displayId, 11, 0);
    }

    public void exitMusicAppWithDisplayId() {
        playbackControl(12, 0);
    }

    public void favorite() {
        playbackControl(8, 0);
    }

    private void playbackControl(int command, int arg) {
        playbackControl(0, command, arg);
    }

    private void playbackControl(final int displayId, final int command, final int arg) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.MediaManager.7
            @Override // java.lang.Runnable
            public void run() {
                if (MediaManager.this.mMediaCenterManager != null) {
                    try {
                        Log.i(MediaManager.TAG, "playbackControl displayId:" + displayId + " command:" + command + " &params:" + arg);
                        MediaManager.this.mMediaCenterManager.playbackControl(displayId, command, arg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void addOnPlayStatusChangedListener(OnPlayStatusChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnPlayStatusChangedListeners.contains(listener)) {
            this.mOnPlayStatusChangedListeners.add(listener);
        }
    }

    public void removeOnPlayStatusChangedListener(OnPlayStatusChangedListener listener) {
        if (listener != null && this.mOnPlayStatusChangedListeners.contains(listener)) {
            this.mOnPlayStatusChangedListeners.remove(listener);
        }
    }

    public void addOnSecondaryPlayStatusChangedListener(OnPlayStatusChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnSecondaryPlayStatusChangedListeners.contains(listener)) {
            this.mOnSecondaryPlayStatusChangedListeners.add(listener);
        }
    }

    public void addOnPlayPositionChangedListener(OnPlayPositionChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnPlayPositionChangedListener.contains(listener)) {
            this.mOnPlayPositionChangedListener.add(listener);
        }
    }

    public void addOnSecondaryPlayPositionChangedListener(OnPlayPositionChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnSecondaryPlayPositionChangedListeners.contains(listener)) {
            this.mOnSecondaryPlayPositionChangedListeners.add(listener);
        }
    }

    public void removeOnPlayPositionChangedListener(OnPlayPositionChangedListener listener) {
        if (listener != null && this.mOnPlayPositionChangedListener.contains(listener)) {
            this.mOnPlayPositionChangedListener.remove(listener);
        }
    }

    public void addOnMediaInfoChangedListener(OnMediaInfoChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnMediaInfoChangedListener.contains(listener)) {
            this.mOnMediaInfoChangedListener.add(listener);
        }
    }

    public void addOnSecondaryMediaInfoChangedListener(OnMediaInfoChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnSecondaryMediaInfoChangedListeners.contains(listener)) {
            this.mOnSecondaryMediaInfoChangedListeners.add(listener);
        }
    }

    public void removeOnMediaInfoChangedListener(OnPlayStatusChangedListener listener) {
        if (listener != null && this.mOnMediaInfoChangedListener.contains(listener)) {
            this.mOnMediaInfoChangedListener.remove(listener);
        }
    }

    public void addOnFftDataCaptureListener(OnFftDataCaptureListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mOnFftDataCaptureListeners.contains(listener)) {
            if (this.mOnFftDataCaptureListeners.isEmpty()) {
                try {
                    if (this.mMediaCenterManager != null) {
                        this.mMediaCenterManager.registerVisualizerListener(this.mVisualCaptureListener);
                    }
                } catch (XUIServiceNotConnectedException e) {
                }
            }
            this.mOnFftDataCaptureListeners.add(listener);
        }
    }

    public void removeOnFftDataCaptureListener(OnFftDataCaptureListener listener) {
        if (listener != null && this.mOnFftDataCaptureListeners.contains(listener)) {
            this.mOnFftDataCaptureListeners.remove(listener);
            if (this.mOnFftDataCaptureListeners.isEmpty()) {
                try {
                    if (this.mMediaCenterManager != null) {
                        this.mMediaCenterManager.unRegisterVisualizerListener(this.mVisualCaptureListener);
                    }
                } catch (XUIServiceNotConnectedException e) {
                }
            }
        }
    }

    public void addVisualizerViewEnableListener(OnVisualizerViewEnableListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("the listener is null!!!");
        }
        if (!this.mVisualizerViewEnableListeners.contains(listener)) {
            this.mVisualizerViewEnableListeners.add(listener);
        }
    }

    public void removeVisualizerViewEnableListener(OnVisualizerViewEnableListener listener) {
        if (listener != null && this.mVisualizerViewEnableListeners.contains(listener)) {
            this.mVisualizerViewEnableListeners.remove(listener);
        }
    }

    public void addLyricListener(OnLyricUpdatedListener listener) {
        if (this.mMediaCenterManager != null && this.mLyricUpdatedListeners.isEmpty()) {
            try {
                this.mMediaCenterManager.registerLyricInfoListener(this.mLyricInfoListener);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
        if (!this.mLyricUpdatedListeners.contains(listener)) {
            this.mLyricUpdatedListeners.add(listener);
        }
    }

    public void removeLyricListener(OnLyricUpdatedListener listener) {
        if (this.mLyricUpdatedListeners.contains(listener)) {
            this.mLyricUpdatedListeners.remove(listener);
        }
        if (this.mMediaCenterManager != null && this.mLyricUpdatedListeners.isEmpty()) {
            try {
                this.mMediaCenterManager.unregisterLyricInfoListener(this.mLyricInfoListener);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLyricUpdated(int displayId, LyricInfo info) {
        Iterator<OnLyricUpdatedListener> it = this.mLyricUpdatedListeners.iterator();
        while (it.hasNext()) {
            OnLyricUpdatedListener listener = it.next();
            listener.onLyricUpdated(displayId, info);
        }
    }
}
