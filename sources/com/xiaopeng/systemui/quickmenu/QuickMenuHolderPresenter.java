package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.TileViewModel;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
import com.xiaopeng.systemui.quickmenu.CarSettingsManager;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.bluetooth.BluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.volume.AudioViewModel;
import com.xiaopeng.widget.ThemeViewModel;
import com.xiaopeng.xuimanager.XUIServiceNotConnectedException;
import com.xiaopeng.xuimanager.mediacenter.MediaCenterManager;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes24.dex */
public class QuickMenuHolderPresenter implements LifecycleOwner, IQuickMenuHolderPresenter, MediaCenterManager.PlaybackListener, CarSettingsManager.OnServiceConnectCompleteListener {
    public static final String AVAS_LOUD_SPEAKER_SW = "avas_speaker";
    public static final int MEDIA_TYPE_BT = 2;
    public static final int MEDIA_TYPE_INSIDE = 0;
    public static final int MEDIA_TYPE_OUTSIDE = 1;
    public static final int MUSIC_NULL = -1;
    public static final int MUSIC_OTHER = 1;
    public static final int MUSIC_XP = 0;
    private AudioViewModel mAudioViewModel;
    private BluetoothViewModel mBluetoothViewModel;
    private Context mContext;
    private List<String> mInitMainScreenViewList;
    private List<String> mInitSecondScreenViewList;
    private boolean mIsLandScape;
    private int mMaxTemperature;
    private int mMaxVolume;
    private int mMaxWind;
    private MediaCenterManager mMediaCenterManager;
    private int mMinTemperature;
    private int mPlaybackState;
    public IQuickMenuViewHolder mQuickMenuView;
    private TileViewModel mQuickMenuViewModel;
    private int mScreenId;
    private ThemeViewModel mThemeViewModel;
    private static final String TAG = QuickMenuHolderPresenter.class.getSimpleName();
    private static final String DEFAULT_SONG_LABEL = ContextUtils.getString(R.string.qs_panel_music_unknown_title);
    private static final String DEFAULT_ARTIST_LABEL = ContextUtils.getString(R.string.qs_panel_music_unknown_sub_title);
    protected final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private HashSet<String> mQuickMenuVisibleSet = new HashSet<>();

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final QuickMenuHolderPresenter sInstance = new QuickMenuHolderPresenter();

        private SingleHolder() {
        }
    }

    public static QuickMenuHolderPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    public QuickMenuHolderPresenter() {
        init();
    }

    public void init() {
        Log.d(TAG, "init quickmenuholderpresenter ");
        this.mContext = SystemUIApplication.getContext();
        this.mQuickMenuViewModel = new TileViewModel(this.mContext);
        this.mAudioViewModel = (AudioViewModel) ViewModelManager.getInstance().getViewModel(AudioViewModel.class, this.mContext);
        this.mBluetoothViewModel = (BluetoothViewModel) ViewModelManager.getInstance().getViewModel(IBluetoothViewModel.class, this.mContext);
        this.mIsLandScape = OrientationUtil.isLandscapeScreen(this.mContext);
        CarSettingsManager.getInstance().addServiceConnectCompleteListener(this);
        this.mQuickMenuViewModel.getTileLiveData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.quickmenu.-$$Lambda$QuickMenuHolderPresenter$O9YvQMaz7mD0TLwh82SosetBDVg
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                QuickMenuHolderPresenter.this.lambda$init$0$QuickMenuHolderPresenter((Pair) obj);
            }
        });
        ContentObserver mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuHolderPresenter.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri.equals(Settings.System.getUriFor("avas_speaker"))) {
                    QuickMenuHolderPresenter.this.updateSoundType();
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("avas_speaker"), true, mCallbackObserver);
        this.mBluetoothViewModel.getPsnBluetoothStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.quickmenu.-$$Lambda$QuickMenuHolderPresenter$w1o8xBnquMxIxnvfRbw3Ib9ntpw
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                QuickMenuHolderPresenter.this.updateMediaVolumeBt(((Integer) obj).intValue());
            }
        });
        if (this.mIsLandScape) {
            return;
        }
        this.mQuickMenuView = ViewFactory.getQuickMenuVerticalViewHolder();
    }

    public /* synthetic */ void lambda$init$0$QuickMenuHolderPresenter(Pair pair) {
        String key = (String) pair.first;
        if (this.mInitMainScreenViewList.contains(key)) {
            updateViewState((String) pair.first, ((Integer) pair.second).intValue(), 0);
        }
        if (this.mInitSecondScreenViewList.contains(key)) {
            updateViewState((String) pair.first, ((Integer) pair.second).intValue(), 1);
        }
    }

    public View initView(Context context, ViewGroup viewGroup, int screenId) {
        this.mScreenId = screenId;
        return this.mQuickMenuView.initView(context, viewGroup);
    }

    public void onStart(String id) {
        Log.d(TAG, "xpquickmenu onStart");
        this.mQuickMenuVisibleSet.add(id);
        if (this.mQuickMenuVisibleSet.size() > 1) {
            String str = TAG;
            Log.d(str, "add second screen id: " + id + ", while there is other quick menu view execute this func;");
            return;
        }
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        initMediaCenterManager(MediaManager.getInstance().getMediaCenterManager());
        refreshMediaCenter();
        updateSoundType();
        initSlider();
        onMediaFragmentResume();
        this.mInitMainScreenViewList = Arrays.asList("brightness_adjustment", "rear_mirror_angle_switch", "volume_adjustment", "wind_adjustment", "psn_temperature_adjustment", "media_adjustment", "back_box_lock_switch", "dc_charging_cover_switch", "ac_charging_cover_switch", "dc_charging_cover_switch", "auto_wiper_speed_switch", "clean_mode", "ihb_switch", "driver_mode_switch", "close_back_box", "open_back_box", "open_rear_mirror", "close_rear_mirror", "downhill_auxiliary_switch", "full_window_open_switch", "full_window_close_switch", "air_conditioning_cleaning_switch", "open_window_air", "auto_hold_switch", "rapid_cooling_switch", "intelligent_deodorization_switch", "meditation_mode_switch", "sleep_mode_switch", "movie_mode_switch", "speech_setting_switch", "driver_seat_vent_adjustment", "driver_seat_heat_adjustment", "vehicle_sound_wave_in", "vehicle_sound_wave_out", "child_mode_sw", "passenger_screen_off_in_drv", "screen_brightness_1");
        for (String key : this.mInitMainScreenViewList) {
            int state = this.mQuickMenuViewModel.getCurrentState(key);
            updateViewState(key, state, 0);
            this.mQuickMenuViewModel.setScreenIdByKey(key, 0);
        }
        this.mInitSecondScreenViewList = Arrays.asList("passenger_screen_off", "passenger_volume_adjustment", "passenger_temperature_adjustment", "screen_brightness_1", "psn_seat_vent_adjustment", "psn_seat_heat_adjustment");
        for (String key2 : this.mInitSecondScreenViewList) {
            int state2 = this.mQuickMenuViewModel.getCurrentState(key2);
            updateViewState(key2, state2, 1);
            this.mQuickMenuViewModel.setScreenIdByKey(key2, 1);
        }
        this.mQuickMenuViewModel.onStartVm();
    }

    public void onStop(String id) {
        this.mQuickMenuVisibleSet.remove(id);
        if (this.mQuickMenuVisibleSet.size() != 0) {
            String str = TAG;
            Log.d(str, "remove screen id: " + id + "; while there is quickmenu view still remain.");
            return;
        }
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        this.mQuickMenuViewModel.onStopVm();
        onFragmentPause();
        Log.d(TAG, "xpquickmenu onStop");
    }

    public void onDestroy() {
        Log.d(TAG, "xpquickmenu onDestroy");
        CarSettingsManager.getInstance().removeServiceConnectCompleteListener(this);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void updateViewState(String key, int state, int screenId) {
        char c;
        Log.d(TAG, "updateviewstate key: " + key + ", state: " + state);
        switch (key.hashCode()) {
            case -2027031142:
                if (key.equals("full_window_close_switch")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2009121622:
                if (key.equals("full_window_open_switch")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -551990896:
                if (key.equals("open_window_air")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 308941893:
                if (key.equals("open_rear_mirror")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1024440051:
                if (key.equals("close_rear_mirror")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0 || c == 1 || c == 2) {
            int openState = this.mQuickMenuViewModel.getCurrentState("full_window_open_switch");
            int closeState = this.mQuickMenuViewModel.getCurrentState("full_window_close_switch");
            state = (openState * 10) + closeState;
        } else if (c == 3 || c == 4) {
            int stateRearMirrorOpen = this.mQuickMenuViewModel.getCurrentState("open_rear_mirror");
            int stateRearMirrorClose = this.mQuickMenuViewModel.getCurrentState("close_rear_mirror");
            state = (stateRearMirrorOpen * 10) + stateRearMirrorClose;
        }
        this.mQuickMenuView.updateViewState(key, state);
        Log.d(TAG, "updateviewstate key: " + key + ", state: " + state);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMediaVolumeBt(int value) {
        updateSoundType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSoundType() {
    }

    public void initMediaCenterManager(MediaCenterManager mediaCenterManager) {
        this.mMediaCenterManager = mediaCenterManager;
        if (mediaCenterManager != null) {
            enable();
        }
    }

    private void enable() {
        this.mQuickMenuView.enableMediaBtn(true);
    }

    private void initSlider() {
        this.mMaxWind = this.mQuickMenuViewModel.getWindMaxValue();
        this.mMaxVolume = this.mQuickMenuViewModel.getSoundMaxValue();
        this.mMinTemperature = this.mQuickMenuViewModel.getTemperatureMinValue();
        this.mMaxTemperature = this.mQuickMenuViewModel.getTemperatureMaxValue();
        this.mQuickMenuView.initSlider(this.mMaxWind, this.mMaxVolume, this.mMinTemperature, this.mMaxTemperature);
    }

    public void onFragmentPause() {
        Log.i(TAG, "ON_PAUSE");
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager != null) {
            try {
                mediaCenterManager.unRegisterPlaybackListener(this);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void themeChanged() {
        this.mQuickMenuView.themeChanged(true);
    }

    public void onMediaFragmentResume() {
        Log.i(TAG, "ON_RUSUME");
        refreshMediaCenter();
    }

    public void refreshMediaCenter() {
        MediaCenterManager mediaCenterManager = this.mMediaCenterManager;
        if (mediaCenterManager == null) {
            return;
        }
        try {
            long[] positionInfo = mediaCenterManager.getCurrentPosition();
            long position = 0;
            long duration = 0;
            if (positionInfo != null && positionInfo.length > 1) {
                position = positionInfo[0];
                duration = positionInfo[1];
            }
            String str = TAG;
            Log.i(str, "refresh: pos=" + position + " duration=" + duration);
            this.mQuickMenuView.updateMusicProgress(position, duration);
            OnMediaInfoNotify(this.mMediaCenterManager.getCurrentMediaInfo());
            OnPlaybackChanged(this.mMediaCenterManager.getCurrentPlayStatus());
            this.mMediaCenterManager.unRegisterPlaybackListener(this);
            this.mMediaCenterManager.registerPlaybackListener(this);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void startMusic() {
        try {
            this.mMediaCenterManager.playbackControl(0, 0);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void resumeMusic() {
        try {
            this.mMediaCenterManager.playbackControl(0, 0);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic() {
        try {
            this.mMediaCenterManager.playbackControl(2, 0);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        try {
            this.mMediaCenterManager.playbackControl(1, 0);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void initQuickMenuPresenter() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void onClickTile(String key, int value) {
        String str = TAG;
        Log.d(str, "Onclicked key: " + key + ", value: " + value);
        this.mQuickMenuViewModel.onClickTileView(key, value);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void onMediaSeekTo(int position) {
        try {
            this.mMediaCenterManager.playbackControl(4, position);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerAllTileCallback() {
        this.mQuickMenuViewModel.onStartVm();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterAllTileCallback() {
        this.mQuickMenuViewModel.onStopVm();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerBrightnessCallback() {
        this.mQuickMenuViewModel.registerBrightnessCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterSoundCallback() {
        this.mQuickMenuViewModel.unregisterSoundCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterBrightnessCallback() {
        this.mQuickMenuViewModel.unregisterBrightnessCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerSoundCallback() {
        this.mQuickMenuViewModel.registerSoundCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerWindCallback() {
        this.mQuickMenuViewModel.registerWindCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterWindCallback() {
        this.mQuickMenuViewModel.unregisterWindCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerMediaControlCallback() {
        try {
            this.mMediaCenterManager.registerPlaybackListener(this);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterMediaControlCallback() {
        try {
            this.mMediaCenterManager.unRegisterPlaybackListener(this);
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public String getQuickMenuLayout() {
        List<Object> mLayout = Arrays.asList(Arrays.asList("back_box_lock_switch", "rear_mirror_angle_switch", "dc_charging_cover_switch", "speech_setting_switch", "auto_wiper_speed_switch", "clean_mode", "ihb_switch"), Arrays.asList("psn_temperature_adjustment", "wind_adjustment", "brightness_adjustment"), Arrays.asList("media_adjustment", "volume_adjustment"));
        return GsonUtil.toJson(mLayout);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public String getSecondaryQuickMenuLayout() {
        return null;
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterPsnSoundCallback() {
        this.mQuickMenuViewModel.unregisterPsnSoundCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void unRegisterPsnBrightnessCallback() {
        this.mQuickMenuViewModel.unregisterPsnBrightnessCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerPsnSoundCallback() {
        this.mQuickMenuViewModel.registerPsnSoundCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void registerPsnBrightnessCallback() {
        this.mQuickMenuViewModel.registerPsnBrightnessCallback();
    }

    @Override // com.xiaopeng.systemui.quickmenu.CarSettingsManager.OnServiceConnectCompleteListener
    public void onServiceConnectComplete() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void onClickMediaControl() {
        if (this.mMediaCenterManager != null) {
            Log.i(TAG, "onClick: control click");
            pauseMusic();
        }
        if (this.mPlaybackState == 0) {
            DataLogUtils.sendDataLog("P00005", "B007", 2);
        } else {
            DataLogUtils.sendDataLog("P00005", "B007", 1);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void onClickMediaPrev() {
        try {
            if (this.mMediaCenterManager != null) {
                this.mMediaCenterManager.playbackControl(7, 0);
            }
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuHolderPresenter
    public void onClickMediaNext() {
        try {
            if (this.mMediaCenterManager != null) {
                this.mMediaCenterManager.playbackControl(6, 0);
            }
        } catch (XUIServiceNotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void OnPlaybackChanged(int playbackStatus) {
        String str = TAG;
        Log.d(str, "OnPlaybackChanged: state=" + playbackStatus + " this=" + hashCode());
        this.mQuickMenuView.updateControlBtn(playbackStatus);
    }

    public void OnUpdatePosition(final long position, final long duration) {
        String str = TAG;
        Log.v(str, "OnUpdatePosition: pos=" + position + " duration=" + duration);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.-$$Lambda$QuickMenuHolderPresenter$KjYAXsNUGv87VT3dXkW4Hm7bB8w
            @Override // java.lang.Runnable
            public final void run() {
                QuickMenuHolderPresenter.this.lambda$OnUpdatePosition$1$QuickMenuHolderPresenter(position, duration);
            }
        });
    }

    public /* synthetic */ void lambda$OnUpdatePosition$1$QuickMenuHolderPresenter(long position, long duration) {
        this.mQuickMenuView.updateMusicProgress(position, duration);
    }

    public void OnMediaInfoNotify(MediaInfo mediaInfo) {
        String songTitle;
        String artist;
        int stateMediaInfo = -1;
        String album = "";
        if (mediaInfo != null) {
            songTitle = mediaInfo.getTitle();
            artist = mediaInfo.getArtist();
            album = mediaInfo.getAlbum();
            String str = TAG;
            Log.i(str, "OnMediaInfoNotify: title=" + songTitle + " artist=" + artist + " album=" + album);
            if (!mediaInfo.isXpMusic()) {
                stateMediaInfo = 1;
            } else {
                stateMediaInfo = 0;
            }
        } else {
            songTitle = DEFAULT_SONG_LABEL;
            artist = DEFAULT_ARTIST_LABEL;
            Log.i(TAG, "OnMediaInfoNotify: info = null");
        }
        this.mQuickMenuView.updateMusicInfo(songTitle, artist, album, stateMediaInfo);
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
}
