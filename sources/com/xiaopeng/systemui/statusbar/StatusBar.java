package com.xiaopeng.systemui.statusbar;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.TrafficStatusChangeReceiver;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.AccountController;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.controller.AudioController;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.controller.SystemController;
import com.xiaopeng.systemui.controller.ThemeController;
import com.xiaopeng.systemui.helper.BitmapHelper;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.helper.TrafficStatusEventHelper;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import com.xiaopeng.systemui.navigationbar.NavigationBar;
import com.xiaopeng.systemui.quickmenu.CarSettingsManager;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
import com.xiaopeng.systemui.secondarywindow.ISecondaryWindowView;
import com.xiaopeng.systemui.secondarywindow.PreInstalledPresenter;
import com.xiaopeng.systemui.secondarywindow.SecondaryNavigationBar;
import com.xiaopeng.systemui.server.SystemBarRecord;
import com.xiaopeng.systemui.server.SystemBarServer;
import com.xiaopeng.systemui.statusbar.MaskLayer.WatermarkPresenter;
import com.xiaopeng.systemui.statusbar.NotificationListener;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.utils.TestReceiver;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.bluetooth.BluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.car.BcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.CarViewModel;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.IBcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.ICarViewModel;
import com.xiaopeng.systemui.viewmodel.car.IHvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.ITboxViewModel;
import com.xiaopeng.systemui.viewmodel.car.IVcuViewModel;
import com.xiaopeng.systemui.viewmodel.car.TboxViewModel;
import com.xiaopeng.systemui.viewmodel.car.VcuViewModel;
import com.xiaopeng.systemui.viewmodel.carmode.CarModeViewModel;
import com.xiaopeng.systemui.viewmodel.iot.IoTViewModel;
import com.xiaopeng.systemui.viewmodel.signal.ISignalViewModel;
import com.xiaopeng.systemui.viewmodel.signal.SignalViewModel;
import com.xiaopeng.systemui.viewmodel.upgrade.IUpgradeViewModel;
import com.xiaopeng.systemui.viewmodel.upgrade.UpgradeViewModel;
import com.xiaopeng.systemui.viewmodel.usb.IUsbViewModel;
import com.xiaopeng.systemui.viewmodel.usb.UsbViewModel;
import com.xiaopeng.systemui.viewmodel.volume.AudioViewModel;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XToast;
import java.util.List;
/* loaded from: classes24.dex */
public class StatusBar extends SystemUI implements NotificationListener.NotificationCallback, LifecycleOwner, ThemeController.OnThemeListener, TrafficStatusEventHelper.OnTrafficStatusListener, IStatusbarPresenter, AccountController.OnAvatarLoadListener, SystemController.OnTimeFormatChangedListener {
    private static final String ACTION_XKEY = "com.xiaopeng.intent.action.xkey";
    private static final int AVATAR_LOAD_RETRY_COUNT = 3;
    public static final String DASHCAM_ACTION = "com.xiaopeng.dvr.action.OPEN";
    public static final String DASHCAM_PKGNAME = "com.xiaopeng.dvr";
    public static final String DASHCAM_PKGNAME_CLS = "com.xiaopeng.dvr.DvrReceiver";
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 1;
    public static final int DOWNLOAD_STATUS_FAIL = 5;
    public static final int DOWNLOAD_STATUS_FINISHED = 4;
    public static final int DOWNLOAD_STATUS_PAUSE = 3;
    public static final int DVR_USE_STATE_AVAILABLE = 1;
    public static final int DVR_USE_STATE_AVAILABLE_RECORDING = 2;
    public static final int DVR_USE_STATE_UNAVAILABLE = 0;
    private static final String EXTRA_DOWNLOAD_STATUS = "xp.download.status";
    private static final String KEY_DVR_USE_STATE = "DVR_USE_STATE";
    private static final int POPUPWINDOW_TYPE_BLUETOOTH = 2;
    private static final int POPUPWINDOW_TYPE_DOWNLOAD = 3;
    private static final int POPUPWINDOW_TYPE_NETWORK = 4;
    private static final int POPUPWINDOW_TYPE_USB = 1;
    private static final String PSN_SRC_ENABLE = "psn_srs_enable";
    private static final String SETTINGS_KEY_DOWNLOAD_STATUS = "xp_download_status";
    private static final String SETTINGS_KEY_ECALL_AVAILABLE = "xp_ecall_enable";
    private static final String SETTINGS_KEY_ECALL_SHOW = "xp_statusbar_ecall_show";
    private static final String SETTINGS_KEY_IHB = "ihb_switch";
    private static final String TAG = "CarStatusBar";
    private static final String XKEY_KEYFUNC = "keyfunc";
    private static final int XKEY_SWITCH_MEDIA = 1;
    private IActivityManager mActivityManager;
    private AudioViewModel mAudioViewModel;
    private Bitmap mAvatarBitmap;
    private BcmViewModel mBcmViewModel;
    private BluetoothViewModel mBluetoothViewModel;
    private CarModeViewModel mCarModeViewModel;
    private CarViewModel mCarViewModel;
    private int mChildSafetySeatStatus;
    private int mDasCamStatus;
    private AccountController.AvatarInfo mDriverAvatarInfo;
    private HandlerThread mHandlerThread;
    private HvacViewModel mHvacViewModel;
    private int mIHBStatus;
    private IoTViewModel mIoTViewModel;
    private boolean mIsECallEnalbe;
    private boolean mIsSrsOn;
    private KeyEventListener mKeyEventListener;
    private LifecycleRegistry mLifecycleRegistry;
    private NavigationBar mNavigationBar;
    private NotificationListener mNotificationListener;
    private QuickMenuGuide mQuickMenuGuide;
    private QuickMenuPresenterManager mQuickMenuPresenterManager;
    private ISecondaryWindowView mSecondaryWindowView;
    private SignalViewModel mSignalViewModel;
    private IStatusbarView mStatusbarView;
    private TboxViewModel mTboxViewModel;
    private UpgradeViewModel mUpgradeViewModel;
    private UsbViewModel mUsbViewModel;
    private VcuViewModel mVcuViewModel;
    private StatusBarGlobal.StatusBarUser mDriverUser = new StatusBarGlobal.StatusBarUser();
    private boolean mLockValue = false;
    private boolean mHasUsbDevice = false;
    private boolean mHasDownload = false;
    private boolean mDriverActive = false;
    private int mDownloadStatus = 4;
    private int mChargeState = 0;
    private int mElecPercent = 0;
    private float mDriveDistance = 0.0f;
    private int mDcPreWarmState = 0;
    private int mWirelessChargeStatus = 0;
    private int mPsnWirelessChargeStatus = 0;
    private int mDriverSeatVentLevel = -1;
    private int mDriverSeatHeatLevel = -1;
    private boolean mIsFrontDefrostOn = false;
    private boolean mIsBackDefrostOn = false;
    private boolean mNeedShowCloseBtn = SystemProperties.getBoolean("persist.miniprogram.statusbar.closebutton", false);
    private boolean mLastShowMiniProgram = false;
    private String mDriverAvatarUrl = null;
    private boolean mIsUpgrading = false;
    private boolean mOutOfData = false;
    private int mSignalType = 0;
    private int mSignalRssi = 0;
    private int mSignalRsrp = 0;
    private int mSignalLevel = 0;
    private int mWifiLevel = 0;
    private boolean mIsWifiConnected = false;
    private boolean mNeedCheckToShowQuickMenuGuide = true;
    private int mBluetoothState = 10;
    private int mPsnBluetoothState = 10;
    private int mBatteryState = -1;
    private boolean mIsCharging = false;
    private int mBatteryLevel = 0;
    private boolean mIsMicrophoneMute = false;
    private boolean mIsRepairModeOn = false;
    private boolean mIsAuthModeOn = false;
    private boolean mIsChildModeOn = false;
    private int mDisableMode = 0;
    private boolean mIsDiagnosticModeOn = false;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            String action = intent.getAction();
            int hashCode = action.hashCode();
            if (hashCode != 575128097) {
                if (hashCode == 833559602 && action.equals("android.intent.action.USER_UNLOCKED")) {
                    c = 0;
                }
                c = 65535;
            } else {
                if (action.equals(StatusBar.ACTION_XKEY)) {
                    c = 1;
                }
                c = 65535;
            }
            if (c == 0) {
                Logger.d(StatusBar.TAG, "receive user unlocked action");
                StatusBar.this.updateAccount();
            } else if (c == 1) {
                int keyFunc = intent.getIntExtra(StatusBar.XKEY_KEYFUNC, 0);
                Logger.d(StatusBar.TAG, "xkey is pressed : " + keyFunc);
                if (keyFunc == 1) {
                    AudioController.getInstance(StatusBar.this.mContext).checkToHideMuteOsd();
                }
            }
        }
    };
    private ActivityController.OnActivityCallback mActivityCallback = new ActivityController.OnActivityCallback() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.2
        @Override // com.xiaopeng.systemui.controller.ActivityController.OnActivityCallback
        public void onActivityChanged(ActivityController.ComponentInfo ci) {
            StatusBar.this.dispatchActivityChanged(ci);
        }
    };
    private AccountController.OnAccountListener mAccountListener = new AccountController.OnAccountListener() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.3
        @Override // com.xiaopeng.systemui.controller.AccountController.OnAccountListener
        public void onAccountsChanged() {
            StatusBar.this.updateAccount();
        }
    };
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.4
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                StatusBar.this.reloadAvatar((AccountController.AvatarInfo) msg.obj);
            }
        }
    };
    private ContentObserver mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.statusbar.StatusBar.5
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(StatusBar.PSN_SRC_ENABLE))) {
                StatusBar statusBar = StatusBar.this;
                statusBar.mIsSrsOn = Settings.System.getInt(statusBar.mContext.getContentResolver(), StatusBar.PSN_SRC_ENABLE, 0) == 1;
                Logger.i(StatusBar.TAG, "onChange isSrsOn : " + StatusBar.this.mIsSrsOn);
                if (StatusBar.this.mStatusbarView != null) {
                    StatusBar.this.mStatusbarView.setSrsState(StatusBar.this.mIsSrsOn);
                }
            } else if (uri.equals(Settings.System.getUriFor(StatusBar.SETTINGS_KEY_ECALL_SHOW)) || uri.equals(Settings.System.getUriFor(StatusBar.SETTINGS_KEY_ECALL_AVAILABLE))) {
                StatusBar.this.updateEcall();
            } else if (uri.equals(Settings.System.getUriFor("ihb_switch"))) {
                StatusBar.this.updateIhb();
            } else if (uri.equals(Settings.System.getUriFor(StatusBar.SETTINGS_KEY_DOWNLOAD_STATUS))) {
                StatusBar statusBar2 = StatusBar.this;
                statusBar2.mDownloadStatus = Settings.System.getInt(statusBar2.mContext.getContentResolver(), StatusBar.SETTINGS_KEY_DOWNLOAD_STATUS, 0);
                boolean hasDownload = Settings.System.getInt(StatusBar.this.mContext.getContentResolver(), StatusBar.SETTINGS_KEY_DOWNLOAD_STATUS, 0) != 0;
                if (StatusBar.this.mHasDownload != hasDownload) {
                    StatusBar.this.mHasDownload = hasDownload;
                    BIHelper.sendBIData(BIHelper.ID.download, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(StatusBar.this.mHasDownload));
                }
                Logger.i(StatusBar.TAG, "onChange mHasDownload : " + StatusBar.this.mHasDownload + "mDownloadStatus :" + StatusBar.this.mDownloadStatus);
                if (StatusBar.this.mStatusbarView != null) {
                    BIHelper.sendBIData(BIHelper.ID.download, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(StatusBar.this.mHasDownload));
                    StatusBar.this.mStatusbarView.showDownloadIcon(StatusBar.this.mHasDownload);
                    StatusBar.this.mStatusbarView.setDownloadStatus(StatusBar.this.mDownloadStatus);
                }
            } else if (uri.equals(Settings.Secure.getUriFor(StatusBar.KEY_DVR_USE_STATE))) {
                StatusBar.this.updateDashCam();
            }
        }
    };
    private Runnable mAccountRunnable = new Runnable() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.6
        @Override // java.lang.Runnable
        public void run() {
            StatusBar.this.updateAccount();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadAvatar(AccountController.AvatarInfo avatarInfo) {
        int type = avatarInfo.getType();
        if (type == 1) {
            AccountController.loadAvatar(type, this.mContext, this.mHandler, this, avatarInfo.getUrl(), avatarInfo.getWidth(), avatarInfo.getHeight(), avatarInfo.getRetryCount());
        }
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        Logger.d(TAG, "start");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction(ACTION_XKEY);
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
        registerTrafficStatusReceiver();
        PresenterCenter.getInstance().setStatusbarPresenter(this);
        StatusBarGlobal.getInstance(this.mContext).initialize();
        ContextUtils.initContext(this.mContext);
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            this.mQuickMenuPresenterManager = QuickMenuPresenterManager.getInstance();
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.7
                @Override // android.os.MessageQueue.IdleHandler
                public boolean queueIdle() {
                    StatusBar.this.mQuickMenuPresenterManager.attachQuickMenu();
                    return false;
                }
            });
        }
        initView();
        initModel();
        PreInstalledPresenter.get().init(this.mContext);
        SystemBarServer.get().addSystemBarServerListener(new SystemBarServer.SystemBarServerListener() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.8
            @Override // com.xiaopeng.systemui.server.SystemBarServer.SystemBarServerListener
            public void onShow(SystemBarRecord systemBarRecord) {
                int status;
                Logger.d(StatusBar.TAG, "onShow  systemBarRecord" + systemBarRecord);
                if (StatusBar.this.mStatusbarView != null) {
                    if (TextUtils.isEmpty(systemBarRecord.getBar().getTitle())) {
                        status = 1;
                    } else {
                        status = 2;
                    }
                    StatusBar.this.mStatusbarView.setStatusBarIcon(status, systemBarRecord);
                }
            }

            @Override // com.xiaopeng.systemui.server.SystemBarServer.SystemBarServerListener
            public void onHide(SystemBarRecord systemBarRecord) {
                Logger.d(StatusBar.TAG, "onHide  systemBarRecord" + systemBarRecord);
                if (StatusBar.this.mStatusbarView != null) {
                    StatusBar.this.mStatusbarView.setStatusBarIcon(0, systemBarRecord);
                }
            }
        });
        new TestReceiver().register(this.mContext);
        Logger.d(TAG, "status start : end");
    }

    public void stop() {
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mHandlerThread = null;
        }
        this.mUpgradeViewModel.destroy();
        ThemeController.getInstance(this.mContext).unregisterThemeListener(this);
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    }

    private void initModel() {
        Logger.d(TAG, "initModel start");
        DataLogUtils.init(this.mContext);
        CarSettingsManager.getInstance();
        this.mHandlerThread = new HandlerThread(TAG, 10);
        this.mHandlerThread.start();
        this.mNavigationBar = new NavigationBar(this.mContext);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(PSN_SRC_ENABLE), true, this.mCallbackObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(SETTINGS_KEY_DOWNLOAD_STATUS), true, this.mCallbackObserver);
        initStatusBar();
    }

    private void initView() {
        Xui.init(SystemUIApplication.getApplication());
        this.mStatusbarView = ViewFactory.getStatusbarView();
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            this.mSecondaryWindowView = ViewFactory.getSecondaryWindowView();
            new SecondaryNavigationBar(this.mContext);
        }
    }

    private void registerTrafficStatusReceiver() {
        new TrafficStatusChangeReceiver().register(this.mContext);
    }

    private void initStatusBar() {
        Logger.d(TAG, "initStatusBar start");
        this.mNotificationListener = new NotificationListener(this.mContext);
        this.mNotificationListener.setCallback(this);
        this.mNotificationListener.registerAsSystemService();
        this.mKeyEventListener = new KeyEventListener(this.mContext, this.mHandler);
        this.mKeyEventListener.register();
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
        initNotification();
        initUsbViewData();
        initCarViewData();
        initVcuViewData();
        initBcmViewData();
        initHvacViewData();
        initTboxViewData();
        initVolumeViewData();
        initSignalViewData();
        initBluetoothViewData();
        initUpgradeViewData();
        initCarModeViewData();
        initSrs();
        initEcall();
        initIhb();
        initDashCam();
        if (StatusBarGlobal.getInstance(this.mContext).isBootCompleted()) {
            initIotDeviceViewData();
        }
        AccountController.getInstance(this.mContext).register(this.mAccountListener);
        ActivityController.getInstance(this.mContext).addActivityCallback(this.mActivityCallback);
        SystemController.getInstance(this.mContext).addOnTimeFormatChangeListener(this);
        ThemeController.getInstance(this.mContext).registerThemeListener(this);
        TrafficStatusEventHelper.getInstance().setTrafficStatusListener(this);
        this.mActivityManager = ActivityManager.getService();
        Logger.d(TAG, "initStatusBar end");
    }

    private void showMicrophoneMuteDialog() {
        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.carcontrol.intent.action.ACTION_SHOW_MICROPHONE_UNMUTE_DIALOG");
        intent.setFlags(16777216);
        this.mContext.sendBroadcast(intent);
    }

    private void showChildModeSettingDialog() {
        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.carcontrol.intent.action.ACTION_SHOW_CHILD_MODE_SETTING_DIALOG");
        intent.setFlags(16777216);
        this.mContext.sendBroadcast(intent);
    }

    private void checkToRetryLoadAvatar(int avatarType) {
        AccountController.AvatarInfo avatarInfo;
        Logger.d(TAG, "checkToRetryLoadAvatar : " + avatarType);
        int width = this.mContext.getResources().getDimensionPixelSize(R.dimen.item_icon_size);
        int height = this.mContext.getResources().getDimensionPixelSize(R.dimen.item_icon_size);
        if (avatarType != 1) {
            avatarInfo = null;
        } else {
            AccountController.AvatarInfo avatarInfo2 = this.mDriverAvatarInfo;
            avatarInfo = avatarInfo2;
        }
        if (avatarInfo != null && !avatarInfo.isAvatarLoaded()) {
            AccountController.loadAvatar(avatarType, this.mContext, this.mHandler, this, avatarInfo.getUrl(), width, height, 3);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void closeMiniProg() {
        Logger.d(TAG, "closeMiniProg");
        showMiniProgramCloseBtn(false);
        try {
            this.mActivityManager.finishMiniProgram();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.lifecycle.LifecycleOwner
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.d(TAG, "onConfigurationChanged newConfig=" + newConfig);
        StatusBarGlobal.getInstance(this.mContext).onConfigurationChanged(newConfig);
        this.mStatusbarView.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            updateBluetooth();
        }
        ISecondaryWindowView iSecondaryWindowView = this.mSecondaryWindowView;
        if (iSecondaryWindowView != null) {
            iSecondaryWindowView.dispatchConfigurationChanged(newConfig);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onBootCompleted() {
        super.onBootCompleted();
        Logger.d(TAG, "onBootCompleted");
        initIotDeviceViewData();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchActivityChanged(ActivityController.ComponentInfo ci) {
        onActivityChanged(ci);
    }

    public void onActivityChanged(ActivityController.ComponentInfo ci) {
        if (ci == null) {
            return;
        }
        if (this.mNeedShowCloseBtn) {
            showMiniProgramCloseBtn(ci.isMiniProgram());
        }
        String currentPkgName = ci.getName().getPackageName();
        if (ActivityController.getInstance(this.mContext).getTopViewType() == 0 && this.mNeedCheckToShowQuickMenuGuide && this.mCarViewModel.getIgStatus() == 1 && !PackageHelper.PACKAGE_INSTRUMENT.equals(currentPkgName) && !PackageHelper.PACKAGE_OOBE.equals(currentPkgName)) {
            this.mNeedCheckToShowQuickMenuGuide = false;
            checkToShowQuickMenuGuide();
        }
        if (!TextUtils.isEmpty(ci.getTopPackage()) || !TextUtils.isEmpty(ci.getPrimaryTopPackage())) {
            this.mStatusbarView.autoHideQuickMenu(0);
        }
        if (!TextUtils.isEmpty(ci.getSecondaryTopPackage())) {
            this.mStatusbarView.autoHideQuickMenu(1);
        }
        this.mStatusbarView.onActivityChanged(ci.getTopPackage(), ci.getPrimaryTopPackage(), ci.getSecondaryTopPackage());
        ISecondaryWindowView iSecondaryWindowView = this.mSecondaryWindowView;
        if (iSecondaryWindowView != null) {
            iSecondaryWindowView.onActivityChanged(ci.getSecondaryTopPackage());
        }
    }

    private void initUsbViewData() {
        this.mUsbViewModel = (UsbViewModel) ViewModelManager.getInstance().getViewModel(IUsbViewModel.class, this.mContext);
        this.mUsbViewModel.getUsbTypeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$vtDF5qFINe375EhrAyCxwDV0W7M
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initUsbViewData$0$StatusBar((Integer) obj);
            }
        });
        updateUsb();
    }

    public /* synthetic */ void lambda$initUsbViewData$0$StatusBar(Integer value) {
        updateUsb();
    }

    private void initIotDeviceViewData() {
        Logger.d(TAG, "init IotDevice");
        this.mIoTViewModel = (IoTViewModel) ViewModelManager.getInstance().getViewModel(IoTViewModel.class, this.mContext);
        this.mChildSafetySeatStatus = this.mIoTViewModel.getDeviceStatus().getValue().intValue();
        this.mIoTViewModel.getDeviceStatus().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$Evp0ZSBlC5s051hrlvRDbUQl1Aw
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initIotDeviceViewData$1$StatusBar((Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initIotDeviceViewData$1$StatusBar(Integer status) {
        updateChildSafetySeatStatus(status.intValue());
    }

    private void initCarViewData() {
        Logger.d(TAG, "initCarViewData");
        this.mCarViewModel = (CarViewModel) ViewModelManager.getInstance().getViewModel(ICarViewModel.class, this.mContext);
        this.mVcuViewModel = (VcuViewModel) ViewModelManager.getInstance().getViewModel(IVcuViewModel.class, this.mContext);
        this.mLockValue = this.mCarViewModel.isCenterLocked();
        this.mStatusbarView.setLockStatus(this.mLockValue);
        updateEnergy(false);
        updateAccount();
        this.mCarViewModel.getCenterLockedData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$bSTRhpe3h6SsNfL8zQ2hBY43Ois
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$2$StatusBar((Boolean) obj);
            }
        });
        this.mCarViewModel.getChargeStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$7fH7_S60SZ1G3JuO-MyEAh5feYc
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$3$StatusBar((Integer) obj);
            }
        });
        this.mVcuViewModel.getElecPercentData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$BxCL9R0RI5iKKWfA36nZuw2H-3U
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$4$StatusBar((Integer) obj);
            }
        });
        this.mVcuViewModel.getDriveDistanceData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$V4RrxL9BhyUcEFwSstZ7DTg30jI
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$5$StatusBar((Float) obj);
            }
        });
        this.mVcuViewModel.getDcPreWarmStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$TM07i7BxMv_kNRFnW6iQK0N-pkI
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$6$StatusBar((Integer) obj);
            }
        });
        this.mQuickMenuGuide = QuickMenuGuide.getInstance();
        this.mVcuViewModel.getGearLevelData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$nUoimlky18gu8DmvN9YaG2gAkzE
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$7$StatusBar((Integer) obj);
            }
        });
        this.mCarViewModel.getDriverActiveData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$Dsm2FUUccNqQ-i7NfFwm7dIVI_I
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$8$StatusBar((Boolean) obj);
            }
        });
        this.mCarViewModel.getIgStatusData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$CtJNvWE_iyEMU8clmxN3REPhJKc
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarViewData$9$StatusBar((Integer) obj);
            }
        });
        initStatus();
    }

    public /* synthetic */ void lambda$initCarViewData$2$StatusBar(Boolean value) {
        if (this.mLockValue != value.booleanValue()) {
            Logger.d(TAG, "updateLockStatus : value = " + value);
            BIHelper.sendBIData(BIHelper.ID.lock, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(value));
            this.mStatusbarView.setLockStatus(value.booleanValue());
            this.mLockValue = value.booleanValue();
        }
    }

    public /* synthetic */ void lambda$initCarViewData$3$StatusBar(Integer value) {
        if (this.mChargeState != value.intValue()) {
            updateEnergy(false);
            this.mChargeState = value.intValue();
        }
    }

    public /* synthetic */ void lambda$initCarViewData$4$StatusBar(Integer value) {
        if (this.mElecPercent != value.intValue()) {
            updateEnergy();
            this.mElecPercent = value.intValue();
        }
    }

    public /* synthetic */ void lambda$initCarViewData$5$StatusBar(Float value) {
        if (this.mDriveDistance != value.floatValue()) {
            updateEnergy();
            this.mDriveDistance = value.floatValue();
            Logger.d(TAG, "setDistanceRemain : value = " + this.mDriveDistance);
            this.mStatusbarView.setDistanceRemain(this.mDriveDistance);
        }
    }

    public /* synthetic */ void lambda$initCarViewData$6$StatusBar(Integer value) {
        if (this.mDcPreWarmState != value.intValue()) {
            this.mDcPreWarmState = value.intValue();
            updateDcPreWarmState(this.mDcPreWarmState);
        }
    }

    public /* synthetic */ void lambda$initCarViewData$8$StatusBar(Boolean value) {
        if (this.mDriverActive != value.booleanValue()) {
            boolean active = value.booleanValue();
            this.mHandler.removeCallbacks(this.mAccountRunnable);
            this.mHandler.postDelayed(this.mAccountRunnable, active ? 0L : 3000L);
        }
    }

    public /* synthetic */ void lambda$initCarViewData$9$StatusBar(Integer value) {
        int intValue = value.intValue();
        if (intValue == 0) {
            this.mNeedCheckToShowQuickMenuGuide = true;
        } else if (intValue == 1) {
            Logger.d(TAG, "updateEnergy after ig on");
            updateEnergy(true);
        }
    }

    private void updateDiagnosticMode(boolean on) {
        Logger.d(TAG, "updateDiagnosticMode : " + on);
        this.mIsDiagnosticModeOn = on;
        this.mStatusbarView.setDiagnosticMode(on);
        WatermarkPresenter.getInstance().getDiagnosticModeMaskLayer().updateView(on);
        BIHelper.sendBIData(BIHelper.ID.mode_factory, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(this.mIsDiagnosticModeOn));
    }

    private void updateDcPreWarmState(int dcPreWarmState) {
        this.mStatusbarView.setDcPreWarmState(dcPreWarmState);
    }

    private void checkToShowQuickMenuGuide() {
        int igStatus = this.mCarViewModel.getIgStatus();
        int gearLevel = this.mVcuViewModel.getGearLevel();
        Logger.d(TAG, "QuickMenuGuide : igStatus = " + igStatus + " gearLevel = " + gearLevel);
        if (gearLevel == 4) {
            this.mQuickMenuGuide.checkToDisplay();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: checkToDestroyQuickMenuGuide */
    public void lambda$initCarViewData$7$StatusBar(Integer gearLevel) {
        if (this.mQuickMenuGuide != null && gearLevel.intValue() != 4 && gearLevel.intValue() != 0) {
            this.mQuickMenuGuide.destroy();
        }
    }

    private void updateWirelessChargeStatus(int wirelessChargeStatus) {
        Logger.d(TAG, "updateWirelessChargeStatus : " + wirelessChargeStatus);
        this.mStatusbarView.setWirelessChargeStatus(wirelessChargeStatus);
        BIHelper.sendBIData(BIHelper.ID.wireless_charge, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(wirelessChargeStatus));
    }

    private void updatePsnWirelessChargeStatus(int wirelessChargeStatus) {
        Logger.d(TAG, "updatePsnWirelessChargeStatus : " + wirelessChargeStatus);
        this.mStatusbarView.setPsnWirelessChargeStatus(wirelessChargeStatus);
        BIHelper.sendBIData(BIHelper.ID.wireless_charge, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(wirelessChargeStatus));
    }

    private void updateDriverSeatHeatLevel(int level) {
        Logger.d(TAG, "updateDriverSeatHeatLevel : " + level);
        this.mStatusbarView.setSeatHeatLevel(level);
    }

    private void updateDriverSeatVentLevel(int level) {
        Logger.d(TAG, "updateDriverSeatVentLevel : " + level);
        this.mStatusbarView.setSeatVentLevel(level);
    }

    private void initStatus() {
        this.mChargeState = this.mCarViewModel.getChargeStateData().getValue().intValue();
        this.mElecPercent = this.mVcuViewModel.getElecPercentData().getValue().intValue();
        this.mDriveDistance = this.mVcuViewModel.getDriveDistanceData().getValue().floatValue();
    }

    private void initVcuViewData() {
        Logger.d(TAG, "initVcuViewData");
        this.mVcuViewModel = (VcuViewModel) ViewModelManager.getInstance().getViewModel(IVcuViewModel.class, this.mContext);
    }

    private void initBcmViewData() {
        Logger.d(TAG, "initBcmViewData");
        this.mBcmViewModel = (BcmViewModel) ViewModelManager.getInstance().getViewModel(IBcmViewModel.class, this.mContext);
        this.mBcmViewModel.getHeadLampGroupData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$MMYdZjsBV6Ab9tuxO5zKey2UrLM
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$10$StatusBar((Integer) obj);
            }
        });
        this.mBcmViewModel.getNearLampStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$uy0XDADT5xg9R1ryiTrOl6Pcu5I
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$11$StatusBar((Integer) obj);
            }
        });
        this.mBcmViewModel.getDoorStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$PAeENviWcMlr4pf4CF1odzRRuuU
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$12$StatusBar((int[]) obj);
            }
        });
        this.mBcmViewModel.getWirelessChargeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$jtKDvyjB3aCqp1Zy7sfTLAYwAjE
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$13$StatusBar((Integer) obj);
            }
        });
        this.mBcmViewModel.getPsnWirelessChargeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$mNFpbT8C4JAiTc0qmeUawpJkSq8
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$14$StatusBar((Integer) obj);
            }
        });
        this.mBcmViewModel.getSeatHeatLevel().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$-ZarrNDm9-AZaopN4nlXdmSZ92A
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$15$StatusBar((Integer) obj);
            }
        });
        this.mBcmViewModel.getSeatVentLevel().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$O7BOeLga4OBf7e9rYG8YQICHZ-M
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBcmViewData$16$StatusBar((Integer) obj);
            }
        });
        updateLamp();
        this.mWirelessChargeStatus = this.mBcmViewModel.getWirelessChargeStatus();
        updateWirelessChargeStatus(this.mWirelessChargeStatus);
        this.mPsnWirelessChargeStatus = this.mBcmViewModel.getPsnWirelessChargeStatus();
        updatePsnWirelessChargeStatus(this.mPsnWirelessChargeStatus);
    }

    public /* synthetic */ void lambda$initBcmViewData$10$StatusBar(Integer value) {
        updateLamp();
    }

    public /* synthetic */ void lambda$initBcmViewData$11$StatusBar(Integer value) {
        updateLamp();
    }

    public /* synthetic */ void lambda$initBcmViewData$13$StatusBar(Integer value) {
        if (this.mWirelessChargeStatus != value.intValue()) {
            this.mWirelessChargeStatus = value.intValue();
            updateWirelessChargeStatus(value.intValue());
        }
    }

    public /* synthetic */ void lambda$initBcmViewData$14$StatusBar(Integer value) {
        if (this.mPsnWirelessChargeStatus != value.intValue()) {
            this.mPsnWirelessChargeStatus = value.intValue();
            updatePsnWirelessChargeStatus(value.intValue());
        }
    }

    public /* synthetic */ void lambda$initBcmViewData$15$StatusBar(Integer value) {
        if (this.mDriverSeatHeatLevel != value.intValue()) {
            this.mDriverSeatHeatLevel = value.intValue();
            this.mStatusbarView.setSeatHeatLevel(value.intValue());
        }
    }

    public /* synthetic */ void lambda$initBcmViewData$16$StatusBar(Integer value) {
        if (this.mDriverSeatVentLevel != value.intValue()) {
            this.mDriverSeatVentLevel = value.intValue();
            this.mStatusbarView.setSeatVentLevel(value.intValue());
        }
    }

    private void initHvacViewData() {
        Logger.d(TAG, "initHvacViewData");
        this.mHvacViewModel = (HvacViewModel) ViewModelManager.getInstance().getViewModel(IHvacViewModel.class, this.mContext);
        this.mHvacViewModel.getHvacFrontDefrostData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$jqaHdxdPKZ_674YmmZnh-N2Bvg4
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initHvacViewData$17$StatusBar((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacPowerData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$vTnkOFmBxYiwEyXUw7bQe1xhzJo
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initHvacViewData$18$StatusBar((Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initHvacViewData$17$StatusBar(Boolean isDefrostFrontOn) {
        onHvacChanged();
    }

    public /* synthetic */ void lambda$initHvacViewData$18$StatusBar(Boolean isPowerOn) {
        onHvacChanged();
    }

    private void onHvacChanged() {
        this.mIsFrontDefrostOn = this.mHvacViewModel.isHvacFrontDefrostOn();
        this.mIsBackDefrostOn = this.mHvacViewModel.isHvacBackDefrostOn();
        StringBuffer buffer = new StringBuffer();
        buffer.append("onHvacChanged");
        buffer.append(" isFrontDefrostOn=" + this.mIsFrontDefrostOn);
        buffer.append(" isBackDefrostOn=" + this.mIsBackDefrostOn);
        Logger.d(TAG, buffer.toString());
        this.mStatusbarView.setFrontDefrostStatus(this.mIsFrontDefrostOn);
        this.mStatusbarView.setBackDefrostStatus(this.mIsBackDefrostOn);
    }

    private void initTboxViewData() {
        Logger.d(TAG, "initTboxViewData");
        this.mTboxViewModel = (TboxViewModel) ViewModelManager.getInstance().getViewModel(ITboxViewModel.class, this.mContext);
        this.mTboxViewModel.getNetworkRssiData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$amq-PDFmvH92GOi8WHmS4eVEPc8
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initTboxViewData$19$StatusBar((Integer) obj);
            }
        });
        this.mTboxViewModel.getNetworkTypeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$aLqeFKlEzSnF_KQFHqLY7HkWqEA
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initTboxViewData$20$StatusBar((Integer) obj);
            }
        });
        this.mTboxViewModel.getNetworkRsrpData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$T6oceDbhifcOoi-LfKeMbOFKoyI
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initTboxViewData$21$StatusBar((Integer) obj);
            }
        });
        this.mTboxViewModel.getTboxConnectStatusData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$G1k8gvAW41BsDtQDnyONVitxaH0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initTboxViewData$22$StatusBar((Integer) obj);
            }
        });
        this.mOutOfData = TrafficStatusEventHelper.getInstance().isTrafficOut();
        updateNetwork();
        Logger.i(TAG, "initTboxViewData : mOutOfData = " + this.mOutOfData);
    }

    public /* synthetic */ void lambda$initTboxViewData$19$StatusBar(Integer value) {
        if (this.mSignalRssi != value.intValue()) {
            updateNetwork();
        }
    }

    public /* synthetic */ void lambda$initTboxViewData$20$StatusBar(Integer value) {
        if (this.mSignalType != value.intValue()) {
            updateNetwork();
        }
    }

    public /* synthetic */ void lambda$initTboxViewData$21$StatusBar(Integer value) {
        if (this.mSignalRsrp != value.intValue()) {
            updateNetwork();
        }
    }

    public /* synthetic */ void lambda$initTboxViewData$22$StatusBar(Integer value) {
        updateNetwork();
    }

    private void initVolumeViewData() {
        Logger.d(TAG, "initVolumeViewData");
        this.mAudioViewModel = (AudioViewModel) ViewModelManager.getInstance().getViewModel(AudioViewModel.class, this.mContext);
        this.mAudioViewModel.initViewModel();
        this.mAudioViewModel.getMicrophoneMuteState().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$SKb7vUqaTAGN7N86N7kZXL5BPYk
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initVolumeViewData$23$StatusBar((Boolean) obj);
            }
        });
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            this.mAudioViewModel.getPsnVolumeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$sL0Rp4noryN8LNSNJZ6Jy5j8sp4
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    StatusBar.this.lambda$initVolumeViewData$24$StatusBar((Integer) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initVolumeViewData$23$StatusBar(Boolean value) {
        updateMicrophoneMuteState(value.booleanValue());
    }

    public /* synthetic */ void lambda$initVolumeViewData$24$StatusBar(Integer volume) {
        this.mSecondaryWindowView.setPsnVolume(volume.intValue());
    }

    private void updateMicrophoneMuteState(boolean show) {
        this.mIsMicrophoneMute = show;
        this.mStatusbarView.setMicrophoneMuteStatus(show);
        BIHelper.sendBIData(BIHelper.ID.microphone, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(show));
    }

    private void initBluetoothViewData() {
        Logger.d(TAG, "initBluetoothViewData");
        this.mBluetoothViewModel = (BluetoothViewModel) ViewModelManager.getInstance().getViewModel(IBluetoothViewModel.class, this.mContext);
        this.mBluetoothViewModel.initViewModel(this.mHandlerThread.getLooper());
        updateBluetooth();
        updatePsnBluetooth();
        this.mBluetoothViewModel.getBluetoothStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$fE-BGpcoojRAIEwahVDGXdfUBFU
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBluetoothViewData$25$StatusBar((Integer) obj);
            }
        });
        this.mBluetoothViewModel.getBluetoothNumberData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$kmu5SojDusYaPzXuoOGDHrc6gTE
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBluetoothViewData$26$StatusBar((Integer) obj);
            }
        });
        this.mBluetoothViewModel.getPsnBluetoothStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$rHe_7MkhMRsj5lsBEigMBtPc9gs
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initBluetoothViewData$27$StatusBar((Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initBluetoothViewData$25$StatusBar(Integer value) {
        updateBluetooth();
    }

    public /* synthetic */ void lambda$initBluetoothViewData$26$StatusBar(Integer value) {
        updateBluetooth();
    }

    public /* synthetic */ void lambda$initBluetoothViewData$27$StatusBar(Integer state) {
        updatePsnBluetooth(state.intValue());
    }

    private void updatePsnBluetooth() {
        updatePsnBluetooth(this.mBluetoothViewModel.getPsnBluetoothState());
    }

    private void updatePsnBluetooth(int state) {
        this.mPsnBluetoothState = state;
        Logger.d(TAG, "updatePsnBluetooth : " + this.mPsnBluetoothState);
        ISecondaryWindowView iSecondaryWindowView = this.mSecondaryWindowView;
        if (iSecondaryWindowView != null) {
            iSecondaryWindowView.setPsnBluetoothState(state);
        }
    }

    private void initUpgradeViewData() {
        Logger.d(TAG, "initUpgradeViewData");
        this.mUpgradeViewModel = (UpgradeViewModel) ViewModelManager.getInstance().getViewModel(IUpgradeViewModel.class, this.mContext);
        lambda$initUpgradeViewData$28$StatusBar(Boolean.valueOf(this.mUpgradeViewModel.getUpgradeState()));
        this.mUpgradeViewModel.getUpgradeStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$5l1v1j1CCce7ERMT3bDmEOekWTE
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initUpgradeViewData$28$StatusBar((Boolean) obj);
            }
        });
    }

    private void initCarModeViewData() {
        Logger.d(TAG, "initCarModeViewData");
        this.mCarModeViewModel = (CarModeViewModel) ViewModelManager.getInstance().getViewModel(CarModeViewModel.class, this.mContext);
        this.mCarModeViewModel.getRepairModeStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$I_qfFNots2vffdQqK57f8xghLKo
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarModeViewData$29$StatusBar((Boolean) obj);
            }
        });
        this.mCarModeViewModel.getAuthModeStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$9T_J-axczOXPK2bkYngcFbBmx9E
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarModeViewData$30$StatusBar((Boolean) obj);
            }
        });
        this.mCarModeViewModel.getDisableModeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$4-6KEfospqC9oN7QC24AwWl5CTk
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarModeViewData$31$StatusBar((Integer) obj);
            }
        });
        this.mCarModeViewModel.getChildModeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$k1uNP4tTcTXL8XRTiK2qIvaNjFQ
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarModeViewData$32$StatusBar((Boolean) obj);
            }
        });
        updateDiagnosticMode(this.mCarModeViewModel.isDiagnosticModeOn());
        this.mCarModeViewModel.getDiagnosticModeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$DkWTgfaktNlXXtL1VLvVIkyd85s
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initCarModeViewData$33$StatusBar((Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initCarModeViewData$31$StatusBar(Integer status) {
        updateDisableMode(status.intValue());
    }

    public /* synthetic */ void lambda$initCarModeViewData$32$StatusBar(Boolean on) {
        updateChildMode(on.booleanValue());
    }

    public /* synthetic */ void lambda$initCarModeViewData$33$StatusBar(Boolean on) {
        updateDiagnosticMode(on.booleanValue());
    }

    private void initSrs() {
        Logger.d(TAG, "init srs");
        this.mIsSrsOn = Settings.System.getInt(this.mContext.getContentResolver(), PSN_SRC_ENABLE, 0) == 1;
        this.mStatusbarView.setSrsState(this.mIsSrsOn);
    }

    private void initEcall() {
        Logger.d(TAG, "init ECall");
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(SETTINGS_KEY_ECALL_SHOW), true, this.mCallbackObserver);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(SETTINGS_KEY_ECALL_AVAILABLE), true, this.mCallbackObserver);
        updateEcall();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEcall() {
        boolean isECallShow = Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_KEY_ECALL_SHOW, 1) == 1;
        boolean isECallAvailable = Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_KEY_ECALL_AVAILABLE, 0) == 1;
        this.mIsECallEnalbe = isECallShow & isECallAvailable;
        BIHelper.sendBIData(BIHelper.ID.ecall, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(this.mIsECallEnalbe));
        Logger.i(TAG, "updateEcall isECallShow: " + isECallShow + " ,isECallAvailable: " + isECallAvailable);
        IStatusbarView iStatusbarView = this.mStatusbarView;
        if (iStatusbarView != null) {
            iStatusbarView.setECallEnable(this.mIsECallEnalbe);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isECallEnable() {
        Logger.d(TAG, "isECallEnable ECall : " + this.mIsECallEnalbe);
        return this.mIsECallEnalbe;
    }

    private void initIhb() {
        Logger.d(TAG, "init initIhb");
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("ihb_switch"), true, this.mCallbackObserver);
        updateIhb();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIhb() {
        if (!CarModelsManager.getConfig().isIhbSupport()) {
            this.mIHBStatus = 0;
            Logger.d(TAG, "updateIhb not support ihb");
            return;
        }
        int status = 0;
        String values = Settings.System.getString(this.mContext.getContentResolver(), "ihb_switch");
        if (!TextUtils.isEmpty(values)) {
            String[] value = values.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            try {
                status = Integer.parseInt(value[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (status != 2) {
            status = 1;
        }
        if (this.mIHBStatus != status) {
            Logger.i(TAG, "updateIhb mIHBStatus : " + this.mIHBStatus + " ,status: " + status + " ,values: " + values);
            this.mIHBStatus = status;
            IStatusbarView iStatusbarView = this.mStatusbarView;
            if (iStatusbarView != null) {
                iStatusbarView.setIHBStatus(this.mIHBStatus);
            }
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getIHBStatus() {
        Logger.d(TAG, "getIHBStatus mIHBStatus : " + this.mIHBStatus);
        return this.mIHBStatus;
    }

    private void initDashCam() {
        Logger.i(TAG, "init dashcam");
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(KEY_DVR_USE_STATE), true, this.mCallbackObserver);
        updateDashCam();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDashCam() {
        int state = Settings.Secure.getInt(this.mContext.getContentResolver(), KEY_DVR_USE_STATE, 0);
        Logger.i(TAG, "updateDashCam: " + state);
        if (state != this.mDasCamStatus) {
            this.mDasCamStatus = state;
            this.mStatusbarView.setDashCamStatus(this.mDasCamStatus);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getDashCamStatus() {
        Logger.i(TAG, "getDashCamStatus: " + this.mDasCamStatus);
        return this.mDasCamStatus;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onDashCamClicked() {
        Logger.i(TAG, "onDashCamClicked");
        Intent intent = new Intent(DASHCAM_ACTION);
        intent.setComponent(new ComponentName(DASHCAM_PKGNAME, DASHCAM_PKGNAME_CLS));
        this.mContext.sendBroadcast(intent);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onIHBClicked() {
        Logger.i(TAG, "onIhbClicked " + this.mIHBStatus);
        int state = this.mIHBStatus;
        int nextState = -1;
        if (state == 1) {
            nextState = 2;
        } else if (state == 2) {
            nextState = 1;
        }
        String lastValues = Settings.System.getString(this.mContext.getContentResolver(), "ihb_switch");
        String value = state + NavigationBarInflaterView.KEY_IMAGE_DELIM + nextState + ":s";
        Settings.System.putString(this.mContext.getContentResolver(), "ihb_switch", value);
        if (value.equals(lastValues)) {
            this.mContext.getContentResolver().notifyChange(Settings.System.getUriFor("ihb_switch"), null);
        }
        BIHelper.sendBIData(BIHelper.ID.ihb, BIHelper.Type.statusbar, nextState == 2 ? BIHelper.Action.open : BIHelper.Action.close);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onStatusBarIconClick(String key) {
        try {
            SystemBarServer.get().notifySystemBarContent(key);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getIfSupportSeatHeatVent() {
        int state = CarModelsManager.getConfig().isSeatHeatSupport() ? 0 | (1 << 1) : 0;
        if (CarModelsManager.getConfig().isSeatVentSupport()) {
            return state | (1 << 0);
        }
        return state;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean getIfSrsOn() {
        return this.mIsSrsOn;
    }

    private void updateChildMode(boolean on) {
        Logger.d(TAG, "updateChildMode : on = " + on);
        this.mIsChildModeOn = on;
        this.mStatusbarView.setChildMode(on);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateAuthMode */
    public void lambda$initCarModeViewData$30$StatusBar(Boolean status) {
        Logger.d(TAG, "updateAuthMode : status = " + status);
        BIHelper.sendBIData(BIHelper.ID.mode_auth, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(status));
        this.mIsAuthModeOn = status.booleanValue();
        this.mStatusbarView.setAuthMode(status.booleanValue());
        if (!status.booleanValue()) {
            PackageHelper.closeAuthModeDialog(this.mContext);
        }
    }

    private void updateDisableMode(int status) {
        Logger.d(TAG, "updateDisableMode : status = " + status);
        this.mDisableMode = status;
        this.mStatusbarView.setDisableMode(status);
        BIHelper.sendBIData(BIHelper.ID.mode_disable, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(status));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateRepairMode */
    public void lambda$initCarModeViewData$29$StatusBar(Boolean status) {
        Logger.d(TAG, "updateRepairMode : status = " + status);
        this.mIsRepairModeOn = status.booleanValue();
        this.mStatusbarView.setRepairMode(status.booleanValue());
        WatermarkPresenter.getInstance().getRepairModeMaskLayer().updateView(status.booleanValue());
        BIHelper.sendBIData(BIHelper.ID.mode_check, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(status));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateUpgradeStatus */
    public void lambda$initUpgradeViewData$28$StatusBar(Boolean show) {
        this.mIsUpgrading = show.booleanValue();
        Logger.d(TAG, "updateUpgradeStatus : show = " + show);
        this.mStatusbarView.setUpgradeStatus(show.booleanValue());
    }

    private void initSignalViewData() {
        Logger.d(TAG, "initSignalViewData");
        this.mSignalViewModel = (SignalViewModel) ViewModelManager.getInstance().getViewModel(ISignalViewModel.class, this.mContext);
        this.mSignalViewModel.initViewModel(this.mHandlerThread.getLooper());
        this.mSignalViewModel.getWifiLevelData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$kqicfyc5o0B8MVGNnTU3blnfaPM
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initSignalViewData$34$StatusBar((Integer) obj);
            }
        });
        this.mSignalViewModel.getWifiStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$cprtLBzcR97McCSOXTZLv5s1tQk
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initSignalViewData$35$StatusBar((Integer) obj);
            }
        });
        this.mSignalViewModel.getWifiConnectionStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$StatusBar$w4vRWcRcebzqIZlIedUFeYMYKzo
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                StatusBar.this.lambda$initSignalViewData$36$StatusBar((Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initSignalViewData$34$StatusBar(Integer value) {
        updateWifiLevel();
    }

    public /* synthetic */ void lambda$initSignalViewData$36$StatusBar(Boolean value) {
        updateWifiVisibility(value.booleanValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateWifiState */
    public void lambda$initSignalViewData$35$StatusBar(Integer value) {
        this.mStatusbarView.showWifiConnectionAnim(value.intValue() == 2, this.mSignalViewModel.getWifiLevel());
    }

    private void updateUsb() {
        UsbViewModel usbViewModel = this.mUsbViewModel;
        if (usbViewModel != null) {
            int usbType = usbViewModel.getUsbType();
            this.mHasUsbDevice = usbType != -1;
            Logger.d(TAG, "updateUsb type=" + usbType);
            BIHelper.sendBIData(BIHelper.ID.usb, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(this.mHasUsbDevice));
            this.mStatusbarView.setUsbStatus(this.mHasUsbDevice);
        }
    }

    private void updateLamp() {
    }

    private void updateEnergy() {
        updateEnergy(false);
    }

    public void updateEnergy(final boolean forceUpdate) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.statusbar.StatusBar.9
            @Override // java.lang.Runnable
            public void run() {
                if (StatusBar.this.mCarViewModel != null) {
                    int chargeState = StatusBar.this.mCarViewModel.getChargeState();
                    int elecPercent = StatusBar.this.mVcuViewModel.getElecPercent();
                    StatusBar.this.mBatteryLevel = CarController.getBatteryLevel(elecPercent);
                    float driveDistance = StatusBar.this.mVcuViewModel.getDriveDistance();
                    int state = CarController.getBatteryState((int) driveDistance);
                    boolean charging = CarController.isBatteryCharging(chargeState);
                    StringBuffer buffer = new StringBuffer("");
                    buffer.append("updateEnergy");
                    buffer.append(" chargeState=" + chargeState);
                    buffer.append(" elecPercent=" + elecPercent);
                    buffer.append(" driveDistance=" + driveDistance);
                    buffer.append(" level=" + StatusBar.this.mBatteryLevel);
                    buffer.append(" state=" + state);
                    buffer.append(" charging=" + charging);
                    Logger.i(StatusBar.TAG, buffer.toString());
                    if (!charging) {
                        if (state != StatusBar.this.mBatteryState || StatusBar.this.mIsCharging) {
                            StatusBar.this.mBatteryState = state;
                            StatusBar.this.updateBatteryStatus(state);
                        }
                        StatusBar statusBar = StatusBar.this;
                        statusBar.updateBatteryLevel(statusBar.mBatteryLevel);
                    }
                    if (forceUpdate || charging != StatusBar.this.mIsCharging) {
                        StatusBar.this.updateChargingStatus(charging);
                        StatusBar.this.mIsCharging = charging;
                    }
                }
            }
        };
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateChargingStatus(boolean charging) {
        this.mStatusbarView.setChargingStatus(charging);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryLevel(int level) {
        Logger.d(TAG, "updateBatteryLevel : level = " + level);
        if (level < 0) {
            level = 0;
        } else if (level > 10) {
            level = 10;
        }
        this.mStatusbarView.setBatteryLevel(level);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryStatus(int state) {
        this.mStatusbarView.setBatteryStatus(state);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccount() {
        Logger.d(TAG, "updateAccount");
        if (this.mCarViewModel != null && this.mBcmViewModel != null) {
            AccountController account = AccountController.getInstance(this.mContext);
            int width = this.mContext.getResources().getDimensionPixelSize(R.dimen.avatar_icon_size);
            int height = this.mContext.getResources().getDimensionPixelSize(R.dimen.avatar_icon_size);
            this.mDriverUser.setUserActive(this.mCarViewModel.isDriverSeatActive());
            this.mDriverUser.setUserLogin(account.hasDriverAccount());
            this.mDriverUser.setUserVisible(true);
            this.mDriverUser.setUserWelcome(false);
            this.mDriverUser.setUserDoorClosed(true);
            if (this.mDriverUser.userLogin) {
                String url = account.getAvatarUrl(4);
                Logger.d(TAG, "driver url = " + url);
                if (avatarUrlChanged(url, this.mDriverAvatarUrl)) {
                    this.mStatusbarView.setDriverStatus(true);
                    this.mDriverAvatarUrl = url;
                    if (!TextUtils.isEmpty(url)) {
                        loadAvatar(width, height, url, 1);
                    }
                }
            } else {
                Logger.d(TAG, "updateAccount : mDriverUser.userActive = " + this.mDriverUser.userActive);
                this.mStatusbarView.setDriverStatus(CarModelsManager.getFeature().isSimpleAccountIcon() ? false : this.mDriverUser.userActive);
                this.mDriverAvatarUrl = "";
                this.mDriverActive = this.mDriverUser.userActive;
            }
            StringBuffer buffer = new StringBuffer("");
            buffer.append("updateAccount");
            buffer.append(" driverUser=" + this.mDriverUser.toString());
            buffer.append("\n");
            Logger.d(TAG, buffer.toString());
        }
    }

    private void loadAvatar(int width, int height, String url, int avatarType) {
        AccountController.AvatarInfo avatarInfo = null;
        if (avatarType == 1) {
            if (this.mDriverAvatarInfo == null) {
                this.mDriverAvatarInfo = new AccountController.AvatarInfo();
            }
            avatarInfo = this.mDriverAvatarInfo;
        }
        if (avatarInfo != null) {
            avatarInfo.setAvatarLoaded(false);
            avatarInfo.setUrl(url);
            AccountController.loadAvatar(avatarType, this.mContext, this.mHandler, this, url, width, height, 3);
        }
    }

    private boolean avatarUrlChanged(String newUrl, String lastUrl) {
        Logger.d(TAG, "avatarUrlChanged : newUrl = " + newUrl + " lastUrl = " + lastUrl);
        return (TextUtils.isEmpty(newUrl) && !TextUtils.isEmpty(lastUrl)) || !(TextUtils.isEmpty(newUrl) || newUrl.equals(lastUrl));
    }

    private void showMiniProgramCloseBtn(boolean show) {
        if (show != this.mLastShowMiniProgram) {
            Logger.d(TAG, "showMiniProgramCloseBtn : " + show);
            this.mStatusbarView.showMiniProgramCloseBtn(show);
            this.mLastShowMiniProgram = show;
        }
    }

    private void updateWifiLevel() {
        this.mWifiLevel = this.mSignalViewModel.getWifiLevel();
        Logger.d(TAG, "updateWifiLevel wifiLevel=" + this.mWifiLevel);
        this.mStatusbarView.setWifiLevel(this.mWifiLevel);
        onNetworkStateChanged(this.mWifiLevel, this.mSignalLevel);
    }

    private void updateChildSafetySeatStatus(int status) {
        this.mChildSafetySeatStatus = status;
        BIHelper.sendBIData(BIHelper.ID.childsafety, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(status));
        this.mStatusbarView.setChildSafetySeatStatus(status);
    }

    private void updateNetwork() {
        TboxViewModel tboxViewModel;
        if (this.mSignalViewModel != null && (tboxViewModel = this.mTboxViewModel) != null) {
            this.mSignalType = tboxViewModel.getNetworkType();
            this.mSignalRssi = this.mTboxViewModel.getNetworkRssi();
            this.mSignalRsrp = this.mTboxViewModel.getNetworkRsrp();
            Logger.d(TAG, "updateNetwork signalType=" + this.mSignalType + " signalRssi=" + this.mSignalRssi + " signalRsrp=" + this.mSignalRsrp + " outOfData = " + this.mOutOfData);
            int i = this.mSignalType;
            if (i == 4 || i == 5) {
                int i2 = this.mSignalRsrp;
                if (i2 == -1) {
                    this.mSignalLevel = 0;
                } else {
                    this.mSignalLevel = i2;
                }
            } else {
                int i3 = this.mSignalRssi;
                if (i3 == -1) {
                    this.mSignalLevel = 0;
                } else {
                    this.mSignalLevel = i3;
                }
            }
            this.mStatusbarView.setSignalLevel(this.mSignalLevel);
            updateSignalStatus(this.mSignalType);
            onNetworkStateChanged(this.mWifiLevel, this.mSignalLevel);
        }
    }

    private void updateWifiVisibility(boolean isWifiConnected) {
        if (CarModelsManager.getFeature().isShowWifiAndSignalTogether()) {
            this.mIsWifiConnected = isWifiConnected;
            Logger.d(TAG, "updateWifiVisibility : isWifiConnected = " + isWifiConnected);
            this.mStatusbarView.showWifiIcon(isWifiConnected);
            this.mStatusbarView.showSignalIcon(isWifiConnected ^ true);
            StatusBarGlobal.getInstance(this.mContext).setClickNetworkButtonToShowOutOfDataPage(this.mOutOfData && !isWifiConnected);
        }
    }

    private void updateSignalStatus(int signalType) {
        if (this.mOutOfData) {
            signalType = -2;
        }
        this.mStatusbarView.setSignalType(signalType);
        StatusBarGlobal.getInstance(this.mContext).setClickNetworkButtonToShowOutOfDataPage(this.mOutOfData && !this.mSignalViewModel.isWifiConnected());
    }

    private void updateBluetooth() {
        Logger.d(TAG, "mBluetoothViewModel = " + this.mBluetoothViewModel);
        BluetoothViewModel bluetoothViewModel = this.mBluetoothViewModel;
        if (bluetoothViewModel != null) {
            this.mBluetoothState = bluetoothViewModel.getBluetoothState();
            Logger.d(TAG, "updateBluetooth state=" + this.mBluetoothState);
            this.mStatusbarView.setBluetoothStatus(this.mBluetoothState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onDoorStateChanged */
    public void lambda$initBcmViewData$12$StatusBar(int[] state) {
        updateAccount();
    }

    private void onNetworkStateChanged(int wifiLevel, int signalLevel) {
        boolean isAvatarLoaded = AccountController.isAvatarLoaded();
        if ((wifiLevel > 0 || signalLevel > 0) && !isAvatarLoaded) {
            updateAccount();
        }
    }

    private int getDownloadStatusInNotifications() {
        List<StatusBarNotification> list;
        int status = 4;
        NotificationListener notificationListener = this.mNotificationListener;
        if (notificationListener != null && (list = notificationListener.getNotification(64)) != null && list.size() > 0) {
            for (StatusBarNotification sbn : list) {
                Notification notification = sbn.getNotification();
                if (notification != null) {
                    int currentStatus = notification.extras.getInt(EXTRA_DOWNLOAD_STATUS, 4);
                    if (currentStatus == 1) {
                        return currentStatus;
                    }
                    if (currentStatus == 3) {
                        status = currentStatus;
                    } else if (currentStatus == 4 || currentStatus == 5) {
                        if (status == 4 || status == 5) {
                            status = currentStatus;
                        }
                    }
                }
            }
        }
        return status;
    }

    private void initNotification() {
        if (this.mNotificationListener != null) {
            if (CarModelsManager.getFeature().isDataProviderSupport()) {
                this.mDownloadStatus = Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_KEY_DOWNLOAD_STATUS, 0);
                this.mHasDownload = Settings.System.getInt(this.mContext.getContentResolver(), SETTINGS_KEY_DOWNLOAD_STATUS, 0) != 0;
            } else {
                this.mHasDownload = this.mNotificationListener.hasNotification(64);
                this.mDownloadStatus = getDownloadStatusInNotifications();
            }
            this.mStatusbarView.showDownloadIcon(this.mHasDownload);
            this.mStatusbarView.setDownloadStatus(this.mDownloadStatus);
        }
    }

    private void handleNotification(StatusBarNotification sbn, int event) {
        if (this.mNotificationListener != null && sbn != null) {
            if (NotificationListener.isOtaNotification(sbn.getNotification())) {
                Logger.d(TAG, "handleNotification isOtaNotification");
                BIHelper.sendBIData(BIHelper.ID.xlogo, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(event));
                if (event != 1) {
                }
            } else if (NotificationListener.isDownloadNotification(sbn)) {
                this.mHasDownload = this.mNotificationListener.hasNotification(64);
                this.mStatusbarView.showDownloadIcon(this.mHasDownload);
                Logger.d(TAG, "event = " + event);
                if (event == 1 || event == 2) {
                    int status = getDownloadStatusInNotifications();
                    Logger.d(TAG, "downloadStatus = " + status);
                    BIHelper.sendBIData(BIHelper.ID.download, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(this.mHasDownload));
                    if (status != this.mDownloadStatus) {
                        this.mStatusbarView.setDownloadStatus(status);
                        this.mDownloadStatus = status;
                        BIHelper.sendBIData(BIHelper.ID.download, BIHelper.Type.statusbar, BIHelper.Action.update, BIHelper.Screen.main, String.valueOf(this.mHasDownload));
                    }
                }
            }
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.NotificationListener.NotificationCallback
    public void onNotificationPosted(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        handleNotification(sbn, 1);
    }

    @Override // com.xiaopeng.systemui.statusbar.NotificationListener.NotificationCallback
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        handleNotification(sbn, 2);
    }

    @Override // com.xiaopeng.systemui.controller.ThemeController.OnThemeListener
    public void onThemeChanged(boolean selfChange, Uri uri) {
        Logger.d(TAG, "onThemeChanged : " + uri);
        if (uri.equals(ThemeController.URI_THEME_STATE)) {
            this.mStatusbarView.onThemeChanged();
            updateEnergy(true);
            this.mQuickMenuGuide.onThemeChanged();
        }
    }

    @Override // com.xiaopeng.systemui.helper.TrafficStatusEventHelper.OnTrafficStatusListener
    public void onTrafficStatusChanged(boolean trafficOut) {
        Logger.i(TAG, "onTrafficStatusChanged : " + trafficOut);
        this.mOutOfData = trafficOut;
        updateSignalStatus(this.mSignalType);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onLogoClicked() {
        String action = this.mContext.getString(R.string.action_ota);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 1303);
        PackageHelper.startActivity(this.mContext, action, "com.xiaopeng.ota", "", null);
        BIHelper.sendBIData(BIHelper.ID.xlogo, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onLockClicked() {
        boolean locked = this.mCarViewModel.isCenterLocked();
        this.mCarViewModel.setCenterLock(!locked);
        BIHelper.sendBIData(BIHelper.ID.lock, BIHelper.Type.statusbar, locked ? BIHelper.Action.open : BIHelper.Action.click);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onEnergyClicked() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("android.intent.extra.XUI_FULLSCREEN", true);
        PackageHelper.startActivitySafely(this.mContext, R.string.component_energycenter, bundle);
        BIHelper.sendBIData(BIHelper.ID.power, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onDriverClicked() {
        PackageHelper.startApplicationWithPackageName(this.mContext, PackageHelper.PACKAGE_ACCOUNT_CENTER);
        checkToRetryLoadAvatar(1);
        BIHelper.sendBIData(BIHelper.ID.account, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onDefrostFrontClicked() {
        boolean isHvacFrontDefrostOn = this.mHvacViewModel.isHvacFrontDefrostOn();
        this.mHvacViewModel.setHvacFrontDefrostOn(!isHvacFrontDefrostOn);
        BIHelper.sendBIData(BIHelper.ID.demist_front, BIHelper.Type.statusbar, isHvacFrontDefrostOn ? BIHelper.Action.close : BIHelper.Action.open);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onDefrostBackClicked() {
        boolean isHvacBackDefrostOn = this.mHvacViewModel.isHvacBackDefrostOn();
        this.mHvacViewModel.setHvacBackDefrostOn(!isHvacBackDefrostOn);
        BIHelper.sendBIData(BIHelper.ID.demist_back, BIHelper.Type.statusbar, isHvacBackDefrostOn ? BIHelper.Action.close : BIHelper.Action.open);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onRepairModeClicked() {
        PackageHelper.showQuitRepairModeDialog(this.mContext);
        BIHelper.sendBIData(BIHelper.ID.mode_check, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onAuthModeClicked() {
        PackageHelper.showAuthModeDialog(this.mContext, this.mCarModeViewModel.getAuthEndTime());
        BIHelper.sendBIData(BIHelper.ID.mode_auth, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onMicrophoneMuteClicked() {
        showMicrophoneMuteDialog();
        BIHelper.sendBIData(BIHelper.ID.microphone, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onUsbClicked() {
        showPopupWindow(1);
        BIHelper.sendBIData(BIHelper.ID.usb, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onBluetoothClicked() {
        showPopupWindow(2);
        BIHelper.sendBIData(BIHelper.ID.bluetooth, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onDownloadClicked() {
        showPopupWindow(3);
        BIHelper.sendBIData(BIHelper.ID.download, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onNetworkClicked() {
        if (StatusBarGlobal.getInstance(this.mContext).clickNetworkButtonToShowOutOfDataPage()) {
            PackageHelper.startOutOfDataPage(this.mContext);
            BIHelper.sendBIData(BIHelper.ID.signal, BIHelper.Type.statusbar);
            return;
        }
        showPopupWindow(4);
        BIHelper.sendBIData(BIHelper.ID.wifi, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onChildSafetySeatClicked() {
        if (CarCheckHelper.isOOBERuning()) {
            return;
        }
        int i = this.mChildSafetySeatStatus;
        if (i != -1) {
            if (i == 0) {
                XToast.show((int) R.string.sysbar_child_safety_seat_not_connected);
            } else if (i == 1) {
                XToast.show((int) R.string.sysbar_child_safety_seat_connected);
            }
        }
        BIHelper.sendBIData(BIHelper.ID.childsafety, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onChildModeClicked() {
        showChildModeSettingDialog();
        BIHelper.sendBIData(BIHelper.ID.mode_child, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void destroyQuickMenuGuide() {
        QuickMenuGuide quickMenuGuide = this.mQuickMenuGuide;
        if (quickMenuGuide != null) {
            quickMenuGuide.destroy();
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isDriverSeatActive() {
        return this.mDriverUser.userActive;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public String getDriverAvatar() {
        return ImageUtil.getBase64String(this.mAvatarBitmap);
    }

    private void showPopupWindow(int type) {
        String intentName = null;
        if (type == 1) {
            intentName = "com.xiaopeng.intent.action.POPUP_USB";
        } else if (type == 2) {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.BLUETOOTH_ID);
            intentName = "com.xiaopeng.intent.action.POPUP_BLUETOOTH";
        } else if (type == 3) {
            intentName = "com.xiaopeng.intent.action.POPUP_DOWNLOAD";
            DataLogUtils.sendDataLog(DataLogUtils.STATUSBAR_PAGE_ID, "B004");
        } else if (type == 4) {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.WIFI_ID);
            intentName = "com.xiaopeng.intent.action.POPUP_WLAN";
        }
        if (intentName != null) {
            Intent intent = new Intent(intentName);
            intent.setFlags(268435456);
            this.mContext.startActivity(intent);
        }
    }

    @Override // com.xiaopeng.systemui.controller.AccountController.OnAvatarLoadListener
    public void onAvatarLoad(int avatarType, Bitmap bitmap) {
        AccountController.AvatarInfo avatarInfo = null;
        if (avatarType == 1) {
            avatarInfo = this.mDriverAvatarInfo;
        }
        Bitmap bitmap2 = this.mAvatarBitmap;
        if (bitmap2 != null && !bitmap2.isRecycled()) {
            this.mAvatarBitmap.recycle();
        }
        this.mAvatarBitmap = BitmapHelper.createCircleBitmap(bitmap);
        this.mStatusbarView.setDriverAvatar(this.mAvatarBitmap);
        if (avatarInfo != null) {
            avatarInfo.setAvatarLoaded(true);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean hasDownload() {
        return this.mHasDownload;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getDownloadStatus() {
        return getDownloadStatusInNotifications();
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isUpgrading() {
        return this.mIsUpgrading;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getSignalType() {
        if (this.mOutOfData) {
            return -2;
        }
        int signalType = this.mSignalType;
        return signalType;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getSignalLevel() {
        return this.mSignalLevel;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isWifiConnected() {
        return this.mIsWifiConnected;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getWifiLevel() {
        return this.mWifiLevel;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isRepairModeOn() {
        return this.mIsRepairModeOn;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isAuthModeOn() {
        return this.mIsAuthModeOn;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isChildModeOn() {
        return this.mIsChildModeOn;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void showStatusBar(boolean visible) {
        IStatusbarView iStatusbarView = this.mStatusbarView;
        if (iStatusbarView != null) {
            iStatusbarView.setVisibility(visible);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isDiagnosticModeOn() {
        Logger.d(TAG, "isDiagnosticModeOn : " + this.mIsDiagnosticModeOn);
        return this.mIsDiagnosticModeOn;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onDiagnosticModeClicked() {
        Logger.d(TAG, "onDiagnosticModeClicked");
        Intent intent = new Intent("com.xiaopeng.diagnostic.factoryDialog");
        intent.putExtra("from", "xui");
        intent.setFlags(268435456);
        this.mContext.startActivity(intent);
        BIHelper.sendBIData(BIHelper.ID.mode_factory, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onSeatVentHeatClicked() {
        PackageHelper.startSeatHeatVent(this.mContext, 0);
        BIHelper.sendBIData(BIHelper.ID.seat_heat_vent, BIHelper.Type.statusbar, BIHelper.Action.click);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public void onECallClicked() {
        Logger.i(TAG, "onECallClicked");
        Intent intent = new Intent("com.xiaopeng.ecall.callout");
        intent.putExtra("from", "systemui");
        intent.setFlags(268435456);
        try {
            this.mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BIHelper.sendBIData(BIHelper.ID.ecall, BIHelper.Type.statusbar);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getDisableMode() {
        return this.mDisableMode;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean hasUsbDevice() {
        return this.mHasUsbDevice;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isCenterLocked() {
        return this.mLockValue;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isFrontDefrostOn() {
        return this.mIsFrontDefrostOn;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isBackDefrostOn() {
        return this.mIsBackDefrostOn;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getBluetoothState() {
        return this.mBluetoothState;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getPsnBluetoothState() {
        Logger.d(TAG, "getPsnBluetoothState : " + this.mPsnBluetoothState);
        return this.mPsnBluetoothState;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getBatteryLevel() {
        return this.mBatteryLevel;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getBatteryState() {
        return this.mBatteryState;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isBatteryCharging() {
        return this.mIsCharging;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public boolean isMicrophoneMute() {
        return this.mIsMicrophoneMute;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getWirelessChargeStatus() {
        return this.mWirelessChargeStatus;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public int getFRWirelessChargeStatus() {
        return this.mPsnWirelessChargeStatus;
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarPresenter
    public String getTimeFormat() {
        String strTimeFormat = Settings.System.getString(this.mContext.getContentResolver(), "time_12_24");
        return strTimeFormat;
    }

    @Override // com.xiaopeng.systemui.controller.SystemController.OnTimeFormatChangedListener
    public void onTimeFormatChanged() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            String strTimeFormat = Settings.System.getString(this.mContext.getContentResolver(), "time_12_24");
            this.mStatusbarView.refreshTimeFormat(strTimeFormat);
        }
    }
}
