package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.xiaopeng.appstore.storeprovider.AssembleInfo;
import com.xiaopeng.appstore.storeprovider.AssembleResult;
import com.xiaopeng.systemui.AppDownloadPresenter;
import com.xiaopeng.systemui.IMusicPlayerView;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.MusicPlayerViewManger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder;
import com.xiaopeng.systemui.infoflow.message.presenter.SecondaryMusicCardPresenter;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.xui.widget.XConstraintLayout;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XProgressBar;
import com.xiaopeng.xui.widget.XRecyclerView;
import com.xiaopeng.xui.widget.XSeekBar;
import com.xiaopeng.xui.widget.XTextView;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class SecondaryWindow extends XConstraintLayout implements View.OnClickListener, IMusicPlayerView, SeekBar.OnSeekBarChangeListener, XuiClientWrapper.MakeupLightListener {
    private static final int MSG_SET_PLACE_ALBUM = 1;
    private static final String TAG = "SecondaryWindow";
    private final long DELAY_SET_PLACE_ALBUM;
    private AbstractAppsFactory mAppInfoFactory;
    private ArrayMap<String, AssembleInfo> mAssembleInfoArrayMap;
    private XImageView mBtnBluetoothHeadset;
    private XImageView mBtnExchange;
    private XImageView mBtnMakeupSpace;
    private Handler mHandler;
    private boolean mIsSbVolumeDragging;
    private XImageView mIvAlbum;
    private XImageView mIvNext;
    private XImageView mIvPlay;
    private XImageView mIvPrev;
    private XImageView mIvVolumeDown;
    private XImageView mIvVolumeUp;
    private ArrayMap<String, BaseAppInfo> mLargeAppArray;
    private List<BaseAppInfo> mLargeApps;
    private AppsAdapter mLargeAppsAdapter;
    private XRecyclerView mLargeAppsRecyclerView;
    private MediaInfo mMediaInfo;
    private SparseArray<BaseAppInfo> mModeEntryArray;
    private XConstraintLayout mMusicPlayerContainer;
    private XProgressBar mPbMusic;
    private RequestOptions mRequestOptions;
    private XSeekBar mSbVolume;
    private ArrayMap<String, BaseAppInfo> mSmallAppArray;
    private List<BaseAppInfo> mSmallApps;
    private AppsAdapter mSmallAppsAdapter;
    private XRecyclerView mSmallAppsRecyclerView;
    private XTextView mTvPlayDuration;
    private XTextView mTvPlayTime;
    private XTextView mTvSubtitle;
    private XTextView mTvTitle;
    private int mVolume;
    private WindowManager mWindowManager;

    public SecondaryWindow(Context context) {
        super(context);
        this.mIsSbVolumeDragging = false;
        this.mVolume = 0;
        this.mModeEntryArray = new SparseArray<>();
        this.mSmallAppArray = new ArrayMap<>();
        this.mLargeAppArray = new ArrayMap<>();
        this.mAssembleInfoArrayMap = new ArrayMap<>();
        this.DELAY_SET_PLACE_ALBUM = OsdController.TN.DURATION_TIMEOUT_SHORT;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    SecondaryWindow.this.mIvAlbum.setImageResource(R.drawable.ic_album_default);
                }
            }
        };
        init();
    }

    public SecondaryWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsSbVolumeDragging = false;
        this.mVolume = 0;
        this.mModeEntryArray = new SparseArray<>();
        this.mSmallAppArray = new ArrayMap<>();
        this.mLargeAppArray = new ArrayMap<>();
        this.mAssembleInfoArrayMap = new ArrayMap<>();
        this.DELAY_SET_PLACE_ALBUM = OsdController.TN.DURATION_TIMEOUT_SHORT;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    SecondaryWindow.this.mIvAlbum.setImageResource(R.drawable.ic_album_default);
                }
            }
        };
        init();
    }

    public SecondaryWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsSbVolumeDragging = false;
        this.mVolume = 0;
        this.mModeEntryArray = new SparseArray<>();
        this.mSmallAppArray = new ArrayMap<>();
        this.mLargeAppArray = new ArrayMap<>();
        this.mAssembleInfoArrayMap = new ArrayMap<>();
        this.DELAY_SET_PLACE_ALBUM = OsdController.TN.DURATION_TIMEOUT_SHORT;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    SecondaryWindow.this.mIvAlbum.setImageResource(R.drawable.ic_album_default);
                }
            }
        };
        init();
    }

    public void init() {
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        MusicPlayerViewManger.getInstance().addMusicPlayerView(1, this);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Logger.d(TAG, "dispatchTouchEvent : ev = " + ev);
        QuickMenuPresenterManager.getInstance().dispatchTouchEvent(1, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLargeAppsRecyclerView = (XRecyclerView) findViewById(R.id.rv_large_apps);
        this.mSmallAppsRecyclerView = (XRecyclerView) findViewById(R.id.rv_small_apps);
        this.mBtnExchange = (XImageView) findViewById(R.id.btn_exchange);
        this.mBtnBluetoothHeadset = (XImageView) findViewById(R.id.iv_bluetooth_headset);
        this.mBtnExchange.setOnClickListener(this);
        this.mBtnBluetoothHeadset.setOnClickListener(this);
        RecyclerView.ItemDecoration itemDecoration = new SpacesItemDecoration(12);
        this.mLargeAppsRecyclerView.addItemDecoration(itemDecoration);
        this.mSmallAppsRecyclerView.addItemDecoration(itemDecoration);
        RecyclerView.LayoutManager largeAppsRvLayoutManager = new LinearLayoutManager(this.mContext, 0, false);
        RecyclerView.LayoutManager smallAppsRvLayoutManager = new LinearLayoutManager(this.mContext, 0, false);
        this.mLargeAppsRecyclerView.setLayoutManager(largeAppsRvLayoutManager);
        this.mSmallAppsRecyclerView.setLayoutManager(smallAppsRvLayoutManager);
        XuiClientWrapper.getInstance().addMakeupLightListener(this);
        initMusicPlayer();
        loadEntries();
    }

    private void loadEntries() {
        this.mAppInfoFactory = AppInfoFactoryProducer.getFactory();
        this.mLargeApps = this.mAppInfoFactory.createLargeApps();
        this.mSmallApps = this.mAppInfoFactory.createSmallApps();
        if (!Utils.isChineseLanguage()) {
            for (int i = 0; i < this.mSmallApps.size(); i++) {
                BaseAppInfo baseAppInfo = this.mSmallApps.get(i);
                baseAppInfo.setIndex(i);
                this.mModeEntryArray.put(baseAppInfo.getSubType(), baseAppInfo);
            }
        } else {
            for (int i2 = 0; i2 < this.mSmallApps.size(); i2++) {
                BaseAppInfo baseAppInfo2 = this.mSmallApps.get(i2);
                baseAppInfo2.setIndex(i2);
                this.mSmallAppArray.put(baseAppInfo2.getPkgName(), baseAppInfo2);
            }
        }
        for (int i3 = 0; i3 < this.mLargeApps.size(); i3++) {
            BaseAppInfo baseAppInfo3 = this.mLargeApps.get(i3);
            baseAppInfo3.setIndex(i3);
            this.mLargeAppArray.put(baseAppInfo3.getPkgName(), baseAppInfo3);
        }
        this.mLargeAppsAdapter = new AppsAdapter(this.mContext, this.mLargeApps);
        this.mSmallAppsAdapter = new AppsAdapter(this.mContext, this.mSmallApps);
        this.mLargeAppsRecyclerView.setAdapter(this.mLargeAppsAdapter);
        this.mSmallAppsRecyclerView.setAdapter(this.mSmallAppsAdapter);
        loadAppStatus();
    }

    private void loadAppStatus() {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.2
            @Override // java.lang.Runnable
            public void run() {
                List<AssembleInfo> assembleInfoList = AppDownloadPresenter.getInstance().getAppDownloadInfoList();
                if (assembleInfoList != null) {
                    for (AssembleInfo assembleInfo : assembleInfoList) {
                        Logger.d(SecondaryWindow.TAG, "pkg = " + assembleInfo.getKey() + " state = " + assembleInfo.getState() + " progress = " + assembleInfo.getProgress());
                        SecondaryWindow.this.mAssembleInfoArrayMap.put(assembleInfo.getKey(), assembleInfo);
                    }
                }
                for (int i = 0; i < SecondaryWindow.this.mLargeApps.size(); i++) {
                    BaseAppInfo baseAppInfo = (BaseAppInfo) SecondaryWindow.this.mLargeApps.get(i);
                    String pkgName = baseAppInfo.getPkgName();
                    if (PackageHelper.isAppInstalled(SecondaryWindow.this.mContext, pkgName)) {
                        baseAppInfo.setStatus(6);
                    } else {
                        SecondaryWindow secondaryWindow = SecondaryWindow.this;
                        secondaryWindow.transferAppStatus(baseAppInfo, (AssembleInfo) secondaryWindow.mAssembleInfoArrayMap.get(baseAppInfo.getPkgName()));
                    }
                }
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SecondaryWindow.this.mLargeAppsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transferAppStatus(BaseAppInfo baseAppInfo, AssembleInfo assembleInfo) {
        if (assembleInfo == null) {
            baseAppInfo.setStatus(1);
            return;
        }
        int state = assembleInfo.getState();
        Logger.d(TAG, "transferAppStatus pkg = " + assembleInfo.getKey() + " state = " + assembleInfo.getState() + " progress = " + assembleInfo.getProgress());
        if (state == 1 || state == 2) {
            baseAppInfo.setStatus(2);
        } else if (state == 200) {
            baseAppInfo.setStatus(3);
            baseAppInfo.setDownloadProgress((int) (assembleInfo.getProgress() * 100.0f));
        } else {
            switch (state) {
                case 100:
                case 101:
                    baseAppInfo.setStatus(4);
                    baseAppInfo.setDownloadProgress((int) (assembleInfo.getProgress() * 100.0f));
                    return;
                case 102:
                    baseAppInfo.setStatus(5);
                    return;
                default:
                    baseAppInfo.setStatus(1);
                    return;
            }
        }
    }

    private void initMusicPlayer() {
        initGlideRequestOption();
        this.mTvTitle = (XTextView) findViewById(R.id.tv_title);
        this.mTvSubtitle = (XTextView) findViewById(R.id.tv_subtitle);
        this.mPbMusic = (XProgressBar) findViewById(R.id.pb_music);
        this.mTvPlayTime = (XTextView) findViewById(R.id.tv_play_time);
        this.mTvPlayDuration = (XTextView) findViewById(R.id.tv_play_duration);
        this.mIvAlbum = (XImageView) findViewById(R.id.iv_album);
        this.mMusicPlayerContainer = (XConstraintLayout) findViewById(R.id.psn_music_player);
        this.mIvPlay = (XImageView) findViewById(R.id.iv_play);
        this.mIvNext = (XImageView) findViewById(R.id.iv_next);
        this.mIvPrev = (XImageView) findViewById(R.id.iv_prev);
        this.mSbVolume = (XSeekBar) findViewById(R.id.sb_volume);
        this.mIvVolumeUp = (XImageView) findViewById(R.id.iv_volume_up);
        this.mIvVolumeDown = (XImageView) findViewById(R.id.iv_volume_down);
        this.mIvPlay.setOnClickListener(this);
        this.mIvPrev.setOnClickListener(this);
        this.mIvNext.setOnClickListener(this);
        this.mIvVolumeDown.setOnClickListener(this);
        this.mIvVolumeUp.setOnClickListener(this);
        this.mBtnMakeupSpace = (XImageView) findViewById(R.id.makeup_space_container);
        if (CarModelsManager.getConfig().isMakeupSpaceSupport()) {
            this.mBtnMakeupSpace.setVisibility(0);
        } else {
            this.mMusicPlayerContainer.getLayoutParams().height = 954;
        }
        int volumeMax = AudioController.getInstance(this.mContext).getMusicVolumeMax(1);
        Logger.d(TAG, "initMusicPlayer : volumeMax = " + volumeMax);
        this.mSbVolume.setMax(volumeMax);
        this.mSbVolume.setOnSeekBarChangeListener(this);
    }

    private void initGlideRequestOption() {
        this.mRequestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.ic_album_default).error(R.drawable.ic_album_default);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XConstraintLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exchange /* 2131362069 */:
                this.mWindowManager.setSharedEvent(0, 0);
                return;
            case R.id.iv_bluetooth_headset /* 2131362495 */:
                Intent intent = new Intent("com.xiaopeng.intent.action.POPUP_PSN_BLUETOOTH");
                intent.setFlags(268435456);
                PackageHelper.startActivityInSecondaryWindow(this.mContext, intent);
                return;
            case R.id.iv_next /* 2131362508 */:
                SecondaryMusicCardPresenter.getInstance().onMusicCardNextClicked();
                return;
            case R.id.iv_play /* 2131362510 */:
                SecondaryMusicCardPresenter.getInstance().onMusicCardPlayPauseClicked();
                return;
            case R.id.iv_prev /* 2131362511 */:
                SecondaryMusicCardPresenter.getInstance().onMusicCardPrevClicked();
                return;
            case R.id.iv_volume_down /* 2131362514 */:
                this.mVolume--;
                AudioController.getInstance(this.mContext).setMusicVolume(1, this.mVolume);
                return;
            case R.id.iv_volume_up /* 2131362515 */:
                this.mVolume++;
                AudioController.getInstance(this.mContext).setMusicVolume(1, this.mVolume);
                return;
            default:
                return;
        }
    }

    public void onActivityChanged(String packageName) {
        boolean showExchange = !TextUtils.isEmpty(packageName);
        this.mBtnExchange.setVisibility(showExchange ? 0 : 8);
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void updateMusicCardMediaInfo(MediaInfo mediaInfo) {
        MediaInfo mediaInfo2;
        Logger.d(TAG, "updateMusicCardMediaInfo : " + mediaInfo);
        if (mediaInfo != null && (mediaInfo2 = this.mMediaInfo) != null) {
            String lastMediaId = mediaInfo2.getId();
            String curMediaId = mediaInfo.getId();
            if (!TextUtils.isEmpty(lastMediaId) && !lastMediaId.equals(curMediaId)) {
                this.mPbMusic.setVisibility(8);
            }
        }
        if (mediaInfo != null && !MusicCardHolder.isSameAlbum(mediaInfo, this.mMediaInfo)) {
            updateAlbumImage(mediaInfo);
        }
        this.mMediaInfo = mediaInfo;
        if (mediaInfo == null) {
            return;
        }
        this.mTvTitle.setText(mediaInfo.getTitle());
        this.mTvSubtitle.setText(mediaInfo.getArtist());
    }

    private void updateAlbumImage(MediaInfo mediaInfo) {
        this.mHandler.removeMessages(1);
        Bitmap albumBmp = mediaInfo.getAlbumBitmap();
        String logoUri = mediaInfo.getAlbumUri();
        int source = mediaInfo.getSource();
        Logger.d(TAG, "updateAlbumImage");
        if (albumBmp != null) {
            Logger.d(TAG, "updateAlbumImage : " + albumBmp.getWidth() + "," + albumBmp.getHeight());
            updateAlbumBitmap(albumBmp);
        } else if (!TextUtils.isEmpty(logoUri)) {
            updateAlbumUri(logoUri, source);
        } else {
            updateDefaultAlbum();
        }
    }

    private void updateAlbumBitmap(Bitmap albumBmp) {
        this.mIvAlbum.setImageBitmap(albumBmp);
    }

    private void updateAlbumUri(String logoUri, int source) {
        Log.i(TAG, "album uri - " + logoUri + " &source:" + source);
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, OsdController.TN.DURATION_TIMEOUT_SHORT);
        Glide.with(this.mContext).load(logoUri).apply(this.mRequestOptions).listener(new RequestListener<Drawable>() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.3
            @Override // com.bumptech.glide.request.RequestListener
            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                Log.i(SecondaryWindow.TAG, "onLoadFailed");
                SecondaryWindow.this.mHandler.removeMessages(1);
                return false;
            }

            @Override // com.bumptech.glide.request.RequestListener
            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                Log.i(SecondaryWindow.TAG, "onResourceReady");
                SecondaryWindow.this.mHandler.removeMessages(1);
                return false;
            }
        }).into(this.mIvAlbum);
    }

    private void updateDefaultAlbum() {
        this.mIvAlbum.setImageResource(R.mipmap.ic_card_music_default);
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void updateMusicCardPlayStatus(int playStatus) {
        if (playStatus == 0) {
            this.mIvPlay.setImageResource(R.drawable.ic_action_pause_ext);
        } else {
            this.mIvPlay.setImageResource(R.drawable.ic_action_play_ext);
        }
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void updateMusicCardProgress(int progress) {
        this.mPbMusic.setProgress(progress);
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void showMusicCardProgress(boolean show) {
        this.mPbMusic.setVisibility(show ? 0 : 8);
        this.mTvPlayTime.setVisibility(show ? 0 : 8);
        this.mTvPlayDuration.setVisibility(show ? 0 : 8);
    }

    @Override // com.xiaopeng.systemui.IMusicPlayerView
    public void setMusicCardPosition(String position, String duration) {
        this.mTvPlayTime.setText(position);
        this.mTvPlayDuration.setText(duration);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            AudioController.getInstance(this.mContext).setMusicVolume(1, progress);
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mIsSbVolumeDragging = true;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mIsSbVolumeDragging = false;
    }

    public void setPsnVolume(int volume) {
        Logger.d(TAG, "setPsnVolume : " + volume);
        if (!this.mIsSbVolumeDragging) {
            this.mVolume = volume;
            Logger.d(TAG, "setPsnVolume : volumeMax = " + this.mSbVolume.getMax());
            this.mSbVolume.setProgress(volume);
        }
    }

    public void notifyDownloadInfo(AssembleInfo assembleInfo) {
        if (assembleInfo == null) {
            return;
        }
        BaseAppInfo baseAppInfo = this.mLargeAppArray.get(assembleInfo.getKey());
        if (baseAppInfo != null) {
            transferAppStatus(baseAppInfo, assembleInfo);
            this.mLargeAppsAdapter.notifyDownloadInfo(baseAppInfo);
        }
        BaseAppInfo baseAppInfo2 = this.mSmallAppArray.get(assembleInfo.getKey());
        if (baseAppInfo2 != null) {
            transferAppStatus(baseAppInfo2, assembleInfo);
            this.mSmallAppsAdapter.notifyDownloadInfo(baseAppInfo2);
        }
    }

    public void setPsnBluetoothState(int state) {
        XImageView xImageView = this.mBtnBluetoothHeadset;
        if (xImageView != null) {
            xImageView.setImageLevel(getImageLevelByPsnBluetoothState(state));
        }
    }

    private int getImageLevelByPsnBluetoothState(int state) {
        if (state != 2) {
            if (state == 12) {
                return 1;
            }
            return 0;
        }
        return 2;
    }

    @Override // com.xiaopeng.systemui.carmanager.XuiClientWrapper.MakeupLightListener
    public void onMakeupLightStatusChanged(boolean on) {
        BaseAppInfo appInfo = this.mModeEntryArray.get(3);
        if (appInfo != null) {
            appInfo.setSubtitle(this.mContext.getString(on ? R.string.status_on : R.string.status_off));
            this.mSmallAppsAdapter.notifyItemChanged(appInfo.getIndex());
        }
    }

    public void notifyUninstallResult(final String packageName, final int returnCode) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.4
            @Override // java.lang.Runnable
            public void run() {
                BaseAppInfo appInfo = (BaseAppInfo) SecondaryWindow.this.mLargeAppArray.get(packageName);
                if (appInfo != null && returnCode == 1) {
                    appInfo.setStatus(1);
                    SecondaryWindow.this.mLargeAppsAdapter.notifyItemChanged(appInfo.getIndex());
                }
            }
        });
    }

    public void notifyDownloadResult(final String pkgName, final AssembleResult assembleResult) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.secondarywindow.SecondaryWindow.5
            @Override // java.lang.Runnable
            public void run() {
                BaseAppInfo appInfo = (BaseAppInfo) SecondaryWindow.this.mLargeAppArray.get(pkgName);
                if (appInfo != null && assembleResult.getCode() == 1) {
                    appInfo.setStatus(7);
                    SecondaryWindow.this.mLargeAppsAdapter.notifyItemChanged(appInfo.getIndex());
                }
            }
        });
    }
}
