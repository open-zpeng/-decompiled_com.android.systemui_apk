package com.xiaopeng.systemui.statusbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.DeviceFactory;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.BitmapHelper;
import com.xiaopeng.systemui.helper.TelephonyHelper;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.theme.XTextView;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
import com.xiaopeng.systemui.informationbar.InformationBar;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
import com.xiaopeng.systemui.server.SystemBarRecord;
import com.xiaopeng.systemui.ui.widget.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.ui.widget.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.systemui.ui.widget.TextClock;
import com.xiaopeng.systemui.ui.window.StatusBarWindow;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.systemui.viewmodel.bluetooth.BluetoothViewModel;
import com.xiaopeng.util.FeatureOption;
import com.xiaopeng.xui.widget.XImageView;
/* loaded from: classes24.dex */
public class Statusbar2DView extends BaseStatusbarView implements View.OnClickListener, View.OnTouchListener, View.OnAttachStateChangeListener, StatusBarWindow.OnViewListener {
    private static final String TAG = "Statusbar2DView";
    private AnimatedImageView mAuthModeBtn;
    private AnimatedImageView mBluetooth;
    private AnimatedVectorDrawable mBluetoothAnimatedVectorDrawable;
    private XImageView mBtnChildMode;
    private XImageView mBtnMicrophoneMute;
    private XImageView mBtnOverflow;
    private AnimatedImageView mCharging;
    private AnimatedImageView mChargingOuter;
    private AnimatedImageView mChildSafetySeat;
    private TextClock mClock;
    private AnimatedImageView mDefrostBack;
    private AnimatedImageView mDefrostFront;
    private AnimatedImageView mDiagnosticModeBtn;
    private AnimatedImageView mDisableModeBtn;
    private AnimatedImageView mDownload;
    private AlphaOptimizedRelativeLayout mDownloadContainer;
    private AnimatedImageView mDriverAccount;
    private RelativeLayout mDriverHotRegion;
    private RelativeLayout mDriverLayout;
    private AnimatedImageView mECallBtn;
    private AnimationDrawable mEnergyAnimationDrawable;
    private AlphaOptimizedLinearLayout mEnergyContainer;
    private XImageView mIhb;
    private InformationBar mInformationBar;
    protected StatusBarWindow mInformationBarWindow;
    private AnimatedImageView mLightening;
    private AnimatedImageView mLock;
    private AlphaOptimizedRelativeLayout mLockContainer;
    private AnimatedImageView mLogo;
    private AnimatedImageView mMiniProgClose;
    private RelativeLayout mMiniProgCloseContainer;
    private AlphaOptimizedLinearLayout mNetworkStatusContainer;
    private AnimatedImageView mRepairModeBtn;
    private XImageView mSeatHeatImage;
    private RelativeLayout mSeatStatus;
    private XImageView mSeatVentImage;
    private AnimatedImageView mSignal;
    private AnimatedImageView mSignalText;
    private XImageView mSrsImage;
    private XTextView mSrsTxt;
    private StatusBarItemManager mStatusBarItemManager;
    private FrameLayout mStatusBarView;
    protected StatusBarWindow mStatusBarWindow;
    private AnimatedImageView mUsb;
    private AnimatedImageView mWifi;
    private AnimationDrawable mWifiAnimationDrawable;
    private boolean mDownloadAnimStarted = false;
    private int mDownloadStatus = 4;
    private boolean mLockStatusChanged = false;
    private Context mContext = ContextUtils.getContext();
    private WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();
    private QuickMenuPresenterManager mQuickMenuPresenterManager = QuickMenuPresenterManager.getInstance();

    public Statusbar2DView() {
        init();
    }

    private void init() {
        attachStatusBar();
        attachInformationBar();
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.xiaopeng.systemui.statusbar.Statusbar2DView.1
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                Statusbar2DView.this.mQuickMenuPresenterManager.attachQuickMenu();
                return false;
            }
        });
        initView();
        initStatusBarManager();
        initStatus();
    }

    private void initStatus() {
        updateLockStatus(this.mPresenter.isCenterLocked());
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDriverAvatar(Bitmap avatar) {
        AnimatedImageView animatedImageView = this.mDriverAccount;
        if (animatedImageView != null) {
            animatedImageView.setImageResource(0);
            this.mDriverAccount.setImageBitmap(avatar);
        }
    }

    private void initView() {
        XImageView xImageView;
        this.mStatusBarView = (FrameLayout) this.mStatusBarWindow.findViewById(R.id.status_bar);
        this.mClock = (TextClock) this.mStatusBarWindow.findViewById(R.id.clock);
        TextClock textClock = this.mClock;
        if (textClock != null) {
            textClock.setOnTouchListener(this);
            TextPaint tp = this.mClock.getPaint();
            tp.setFakeBoldText(true);
            this.mClock.setVisibility(CarModelsManager.getFeature().isClockSupport() ? 0 : 8);
            this.mClock.setClickable(false);
            this.mClock.setLongClickable(false);
        }
        this.mUsb = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.usb);
        this.mLogo = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.logo);
        this.mWifi = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.wifi);
        this.mNetworkStatusContainer = (AlphaOptimizedLinearLayout) this.mStatusBarWindow.findViewById(R.id.network_status_container);
        this.mNetworkStatusContainer.setOnClickListener(this);
        this.mLock = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.lock);
        this.mLockContainer = (AlphaOptimizedRelativeLayout) this.mStatusBarWindow.findViewById(R.id.lock_container);
        this.mDefrostFront = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.defrost_front);
        this.mDefrostBack = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.defrost_back);
        if (CarModelsManager.getFeature().isShowDefrostOnStatusBar()) {
            this.mDefrostFront.setOnClickListener(this);
            this.mDefrostFront.setVisibility(0);
        }
        if (CarModelsManager.getFeature().isShowDefrostBackOnStatusBar()) {
            this.mDefrostBack.setOnClickListener(this);
            this.mDefrostBack.setVisibility(0);
        }
        this.mIhb = (XImageView) this.mStatusBarWindow.findViewById(R.id.btn_ihb);
        if (CarModelsManager.getConfig().isXPUSupport() && (xImageView = this.mIhb) != null) {
            xImageView.setVisibility(0);
            this.mIhb.setOnClickListener(this);
        }
        this.mSignal = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.signal);
        if (!CarModelsManager.getFeature().isShowWifiAndSignalTogether()) {
            this.mSignal.setOnTouchListener(this);
        }
        this.mSignalText = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.signal_text);
        this.mEnergyContainer = (AlphaOptimizedLinearLayout) this.mStatusBarWindow.findViewById(R.id.energy_container);
        this.mCharging = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.iv_charging);
        this.mChargingOuter = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.iv_charging_outer);
        this.mLightening = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.iv_lightening);
        this.mDownload = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.download);
        this.mDownload.addOnAttachStateChangeListener(this);
        this.mBtnMicrophoneMute = (XImageView) this.mStatusBarWindow.findViewById(R.id.btn_microphone_mute);
        this.mBtnMicrophoneMute.setOnClickListener(this);
        this.mChildSafetySeat = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.child_safety_seat);
        AnimatedImageView animatedImageView = this.mChildSafetySeat;
        if (animatedImageView != null) {
            animatedImageView.setOnClickListener(this);
        }
        this.mBtnChildMode = (XImageView) this.mStatusBarWindow.findViewById(R.id.btn_child_mode);
        XImageView xImageView2 = this.mBtnChildMode;
        if (xImageView2 != null) {
            xImageView2.setOnClickListener(this);
        }
        this.mBtnOverflow = (XImageView) this.mStatusBarWindow.findViewById(R.id.btn_overflow);
        this.mBtnOverflow.setOnClickListener(this);
        this.mDownloadContainer = (AlphaOptimizedRelativeLayout) this.mStatusBarWindow.findViewById(R.id.download_container);
        this.mDownloadContainer.setOnClickListener(this);
        this.mBluetooth = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.bluetooth);
        this.mBluetooth.setOnClickListener(this);
        this.mDriverLayout = (RelativeLayout) this.mStatusBarWindow.findViewById(R.id.driver_container);
        this.mDriverHotRegion = (RelativeLayout) this.mStatusBarWindow.findViewById(R.id.driver_hot_region);
        this.mMiniProgCloseContainer = (RelativeLayout) this.mStatusBarWindow.findViewById(R.id.miniprog_close_container);
        RelativeLayout relativeLayout = this.mMiniProgCloseContainer;
        if (relativeLayout != null) {
            relativeLayout.setOnClickListener(this);
        }
        this.mDriverAccount = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.driver);
        this.mMiniProgClose = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.miniprog_close);
        this.mRepairModeBtn = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.btn_repair_mode);
        this.mDiagnosticModeBtn = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.btn_diagnostic_mode);
        AnimatedImageView animatedImageView2 = this.mDiagnosticModeBtn;
        if (animatedImageView2 != null) {
            animatedImageView2.setOnClickListener(this);
        }
        this.mAuthModeBtn = (AnimatedImageView) this.mStatusBarView.findViewById(R.id.btn_auth_mode);
        AnimatedImageView animatedImageView3 = this.mAuthModeBtn;
        if (animatedImageView3 != null) {
            animatedImageView3.setOnClickListener(this);
        }
        this.mDisableModeBtn = (AnimatedImageView) this.mStatusBarView.findViewById(R.id.iv_disable_mode);
        AlphaOptimizedRelativeLayout alphaOptimizedRelativeLayout = this.mLockContainer;
        if (alphaOptimizedRelativeLayout != null) {
            alphaOptimizedRelativeLayout.setVisibility(CarModelsManager.getFeature().isLockSupport() ? 0 : 8);
            this.mLockContainer.setOnClickListener(this);
        }
        this.mUsb.setOnClickListener(this);
        AnimatedImageView animatedImageView4 = this.mLogo;
        if (animatedImageView4 != null) {
            animatedImageView4.setOnClickListener(this);
        }
        this.mDriverAccount.setOnClickListener(this);
        this.mEnergyContainer.setOnClickListener(this);
        this.mDriverLayout.setOnClickListener(this);
        this.mDriverHotRegion.setOnClickListener(this);
        AnimatedImageView animatedImageView5 = this.mRepairModeBtn;
        if (animatedImageView5 != null) {
            animatedImageView5.setOnClickListener(this);
        }
        this.mSeatStatus = (RelativeLayout) this.mStatusBarView.findViewById(R.id.btn_seatventheat);
        if (!Utils.isChineseLanguage()) {
            Log.d(TAG, "FeatureHelper.CFG_SEAT_VENT_SUPPORT: " + CarModelsManager.getConfig().isSeatVentSupport());
            Log.d(TAG, "FeatureHelper.CFG_SEAT_HEAT_SUPPORT: " + CarModelsManager.getConfig().isSeatHeatSupport());
            if (CarModelsManager.getConfig().isSeatVentSupport() && CarModelsManager.getConfig().isSeatHeatSupport()) {
                this.mSeatHeatImage = (XImageView) this.mStatusBarView.findViewById(R.id.btn_seatheat_level);
                this.mSeatVentImage = (XImageView) this.mStatusBarView.findViewById(R.id.btn_seatvent_level);
                this.mSeatStatus.setVisibility(0);
                this.mSeatHeatImage.setVisibility(0);
                this.mSeatVentImage.setVisibility(0);
                this.mSeatStatus.setOnClickListener(this);
            } else if (CarModelsManager.getConfig().isSeatHeatSupport()) {
                this.mSeatHeatImage = (XImageView) this.mStatusBarView.findViewById(R.id.btn_seatheat_level_only);
                this.mSeatStatus.setVisibility(0);
                this.mSeatHeatImage.setVisibility(0);
                this.mSeatStatus.setOnClickListener(this);
            } else {
                this.mSeatStatus.setVisibility(8);
            }
        }
        this.mSrsTxt = (XTextView) this.mStatusBarView.findViewById(R.id.srs_txt);
        this.mSrsImage = (XImageView) this.mStatusBarView.findViewById(R.id.srs_img);
        if (!CarModelsManager.getConfig().isSrsSupport()) {
            this.mSrsTxt.setVisibility(8);
            this.mSrsImage.setVisibility(8);
        }
        this.mECallBtn = (AnimatedImageView) this.mStatusBarWindow.findViewById(R.id.btn_ecall);
        AnimatedImageView animatedImageView6 = this.mECallBtn;
        if (animatedImageView6 != null) {
            animatedImageView6.setOnClickListener(this);
        }
    }

    private void initStatusBarManager() {
        this.mStatusBarItemManager = new StatusBarItemManager(this.mContext);
        this.mStatusBarItemManager.setStatusBarContainer(this.mStatusBarWindow);
        this.mStatusBarItemManager.setOnClickListener(this);
        this.mStatusBarItemManager.setOverflowButton(this.mBtnOverflow);
        this.mStatusBarItemManager.setDownloadButton(this.mDownload);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showMiniProgramCloseBtn(boolean show) {
        this.mDriverLayout.setVisibility(show ? 8 : 0);
        this.mDriverAccount.setVisibility(show ? 8 : 0);
        this.mMiniProgCloseContainer.setVisibility(show ? 0 : 8);
        this.mMiniProgClose.setVisibility(show ? 0 : 8);
        this.mMiniProgCloseContainer.setSelected(show);
    }

    private void attachStatusBar() {
        Context context = this.mContext;
        this.mStatusBarWindow = (StatusBarWindow) View.inflate(context, DeviceFactory.getStatusBarResId(), null);
        WindowHelper.addStatusBar(this.mWindowManager, this.mStatusBarWindow, 0);
        this.mStatusBarWindow.addListener(this);
    }

    private void attachInformationBar() {
        if (OrientationUtil.isLandscapeScreen(this.mContext)) {
            Context context = this.mContext;
            this.mInformationBarWindow = (StatusBarWindow) View.inflate(context, R.layout.information_bar_content, null);
            WindowHelper.addInformationBar(this.mWindowManager, this.mInformationBarWindow, 0);
            this.mInformationBar = new InformationBar(context, this.mInformationBarWindow);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        Logger.d(TAG, "onClick view=" + view);
        if (Utils.isFastClick() && id != R.id.btn_overflow) {
            return;
        }
        switch (id) {
            case R.id.bluetooth /* 2131362023 */:
                this.mPresenter.onBluetoothClicked();
                return;
            case R.id.btn_auth_mode /* 2131362058 */:
                this.mPresenter.onAuthModeClicked();
                return;
            case R.id.btn_child_mode /* 2131362061 */:
                this.mPresenter.onChildModeClicked();
                return;
            case R.id.btn_diagnostic_mode /* 2131362067 */:
                this.mPresenter.onDiagnosticModeClicked();
                return;
            case R.id.btn_ecall /* 2131362068 */:
                this.mPresenter.onECallClicked();
                return;
            case R.id.btn_ihb /* 2131362072 */:
                this.mPresenter.onIHBClicked();
                return;
            case R.id.btn_microphone_mute /* 2131362073 */:
                this.mPresenter.onMicrophoneMuteClicked();
                return;
            case R.id.btn_overflow /* 2131362078 */:
                showStatusBarOverflowItemContainer();
                return;
            case R.id.btn_repair_mode /* 2131362080 */:
                this.mPresenter.onRepairModeClicked();
                return;
            case R.id.btn_seatventheat /* 2131362087 */:
                this.mPresenter.onSeatVentHeatClicked();
                return;
            case R.id.child_safety_seat /* 2131362145 */:
                this.mPresenter.onChildSafetySeatClicked();
                return;
            case R.id.defrost_back /* 2131362205 */:
                this.mPresenter.onDefrostBackClicked();
                return;
            case R.id.defrost_front /* 2131362206 */:
                this.mPresenter.onDefrostFrontClicked();
                return;
            case R.id.download_container /* 2131362248 */:
                this.mPresenter.onDownloadClicked();
                return;
            case R.id.driver /* 2131362254 */:
                this.mPresenter.onDriverClicked();
                return;
            case R.id.driver_container /* 2131362255 */:
                this.mDriverAccount.callOnClick();
                return;
            case R.id.driver_hot_region /* 2131362256 */:
                this.mDriverLayout.callOnClick();
                return;
            case R.id.energy_container /* 2131362274 */:
                this.mPresenter.onEnergyClicked();
                return;
            case R.id.lock_container /* 2131362637 */:
                this.mPresenter.onLockClicked();
                return;
            case R.id.logo /* 2131362641 */:
                this.mPresenter.onLogoClicked();
                return;
            case R.id.miniprog_close_container /* 2131362681 */:
                this.mPresenter.closeMiniProg();
                return;
            case R.id.network_status_container /* 2131362766 */:
                this.mPresenter.onNetworkClicked();
                return;
            case R.id.usb /* 2131363384 */:
                this.mPresenter.onUsbClicked();
                return;
            default:
                return;
        }
    }

    private void showStatusBarOverflowItemContainer() {
        AnimationHelper.destroyAnim(this.mBtnOverflow);
        resetOverflowBtnStatus();
        this.mStatusBarItemManager.showOverflowItemContainer();
    }

    @Override // com.xiaopeng.systemui.ui.window.StatusBarWindow.OnViewListener
    public void onAttachedToWindow() {
    }

    @Override // com.xiaopeng.systemui.ui.window.StatusBarWindow.OnViewListener
    public void onFinishInflate() {
    }

    @Override // com.xiaopeng.systemui.ui.window.StatusBarWindow.OnViewListener
    public void dispatchTouchEvent(MotionEvent ev) {
        this.mQuickMenuPresenterManager.dispatchTouchEvent(0, ev);
        this.mPresenter.destroyQuickMenuGuide();
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void onActivityChanged(String topPackage, String primaryTopPackage, String secondTopPackage) {
        InformationBar informationBar = this.mInformationBar;
        if (informationBar != null) {
            informationBar.onActivityChanged(topPackage, primaryTopPackage);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void autoHideQuickMenu(int screenIndex) {
        this.mQuickMenuPresenterManager.autoHideQuickMenu(screenIndex);
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View v) {
        if (v == this.mDownload) {
            updateDownloadStatus();
        }
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View v) {
    }

    private void updateLockStatus(boolean lockValue) {
        AnimatedImageView animatedImageView = this.mLock;
        if (animatedImageView != null) {
            animatedImageView.setImageLevel(lockValue ? 1 : 0);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setLockStatus(boolean status) {
        updateLockStatus(status);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setWirelessChargeStatus(int status) {
        this.mStatusBarItemManager.showAndCheckOverflow(3, status == 1);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setPsnWirelessChargeStatus(int status) {
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setFrontDefrostStatus(boolean on) {
        AnimatedImageView animatedImageView = this.mDefrostFront;
        if (animatedImageView != null) {
            animatedImageView.setSelected(on);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBackDefrostStatus(boolean on) {
        AnimatedImageView animatedImageView = this.mDefrostBack;
        if (animatedImageView != null) {
            animatedImageView.setSelected(on);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setMicrophoneMuteStatus(boolean status) {
        this.mStatusBarItemManager.showAndCheckOverflow(1, status);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setAuthMode(boolean status) {
        this.mStatusBarItemManager.showAndCheckOverflow(2, status);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDisableMode(int status) {
        AnimatedImageView animatedImageView = this.mDisableModeBtn;
        if (animatedImageView != null) {
            animatedImageView.setVisibility(status == 0 ? 8 : 0);
            this.mDisableModeBtn.setImageResource(getDisableModeResId(status));
        }
    }

    private int getDisableModeResId(int status) {
        if (status == 1) {
            return R.drawable.ic_disable_mode_test;
        }
        return R.drawable.ic_disable_mode_exhibition;
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setRepairMode(boolean status) {
        this.mRepairModeBtn.setVisibility(status ? 0 : 8);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setUpgradeStatus(boolean show) {
        AnimatedImageView animatedImageView = this.mLogo;
        if (animatedImageView != null) {
            if (show) {
                animatedImageView.setImageResource(R.drawable.ic_sysbar_logo_upgrade);
                this.mLogo.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_logo_upgrade));
                return;
            }
            animatedImageView.setImageResource(R.drawable.ic_sysbar_logo);
            this.mLogo.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_logo));
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setWifiLevel(int level) {
        this.mWifi.setImageLevel(level);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSignalLevel(int level) {
        this.mSignal.setImageLevel(level);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showWifiIcon(boolean show) {
        this.mWifi.setVisibility(show ? 0 : 8);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showSignalIcon(boolean show) {
        this.mSignalText.setVisibility(show ? 0 : 8);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSignalType(int type) {
        this.mSignalText.setImageResource(TelephonyHelper.getNetworkTypeImgResourceId(type));
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showWifiConnectionAnim(boolean show, int level) {
        if (show) {
            this.mWifi.setImageResource(R.drawable.anim_wifi_connecting);
            this.mWifi.setImageDrawable(this.mContext.getDrawable(R.drawable.anim_wifi_connecting));
            this.mWifiAnimationDrawable = (AnimationDrawable) this.mWifi.getDrawable();
            this.mWifiAnimationDrawable.start();
            return;
        }
        stopAnimationDrawable(this.mWifiAnimationDrawable);
        this.mWifi.setImageResource(R.drawable.ic_sysbar_wifi);
        this.mWifi.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_wifi));
        this.mWifi.setImageLevel(level);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setUsbStatus(boolean hasUsbDevice) {
        if (FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED) {
            hasUsbDevice = false;
        }
        this.mStatusBarItemManager.showAndCheckOverflow(4, hasUsbDevice);
    }

    private void stopAnimationDrawable(AnimationDrawable animationDrawable) {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setChargingStatus(boolean charging) {
        Logger.i(TAG, "setChargingStatus : " + charging);
        if (charging) {
            this.mCharging.setImageResource(R.drawable.anim_sysbar_battery);
            this.mCharging.setImageDrawable(this.mContext.getDrawable(R.drawable.anim_sysbar_battery));
            this.mCharging.setSelected(false);
            this.mCharging.setActivated(false);
            this.mEnergyAnimationDrawable = (AnimationDrawable) this.mCharging.getDrawable();
            this.mEnergyAnimationDrawable.start();
            this.mLightening.setVisibility(0);
            return;
        }
        stopAnimationDrawable(this.mEnergyAnimationDrawable);
        this.mLightening.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBatteryStatus(int state) {
        Logger.d(TAG, "setBatteryStatus : " + state);
        if (state != 0) {
            if (state == 1) {
                this.mCharging.setSelected(false);
                this.mCharging.setActivated(true);
                return;
            } else if (state == 2) {
                this.mCharging.setSelected(false);
                this.mCharging.setActivated(false);
                return;
            } else {
                return;
            }
        }
        this.mCharging.setSelected(true);
        this.mCharging.setActivated(false);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBatteryLevel(int level) {
        int drawableId = 0;
        switch (level) {
            case 0:
                drawableId = R.drawable.ic_sysbar_battery_0;
                break;
            case 1:
                drawableId = R.drawable.ic_sysbar_battery_10;
                break;
            case 2:
                drawableId = R.drawable.ic_sysbar_battery_20;
                break;
            case 3:
                drawableId = R.drawable.ic_sysbar_battery_30;
                break;
            case 4:
                drawableId = R.drawable.ic_sysbar_battery_40;
                break;
            case 5:
                drawableId = R.drawable.ic_sysbar_battery_50;
                break;
            case 6:
                drawableId = R.drawable.ic_sysbar_battery_60;
                break;
            case 7:
                drawableId = R.drawable.ic_sysbar_battery_70;
                break;
            case 8:
                drawableId = R.drawable.ic_sysbar_battery_80;
                break;
            case 9:
                drawableId = R.drawable.ic_sysbar_battery_90;
                break;
            case 10:
                drawableId = R.drawable.ic_sysbar_battery_100;
                break;
        }
        if (drawableId != 0) {
            this.mCharging.setImageResource(drawableId);
            this.mCharging.setImageDrawable(this.mContext.getDrawable(drawableId));
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDriverStatus(boolean hasPeople) {
        if (hasPeople) {
            this.mDriverAccount.setImageResource(R.drawable.ic_sysbar_account_has_people);
            this.mDriverAccount.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_account_has_people));
            return;
        }
        this.mDriverAccount.setImageResource(R.drawable.ic_sysbar_account_no_people);
        this.mDriverAccount.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_account_no_people));
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setChildSafetySeatStatus(int status) {
        AnimatedImageView animatedImageView = this.mChildSafetySeat;
        if (animatedImageView == null) {
            return;
        }
        if (status == -1) {
            animatedImageView.setVisibility(8);
        } else if (status == 0) {
            animatedImageView.setImageLevel(0);
            this.mChildSafetySeat.setVisibility(0);
        } else if (status == 1) {
            animatedImageView.setImageLevel(1);
            this.mChildSafetySeat.setVisibility(0);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDcPreWarmState(int dcPreWarmState) {
        if (dcPreWarmState == 1) {
            this.mChargingOuter.setImageResource(R.drawable.ic_sysbar_battery_pre_heat);
        } else if (dcPreWarmState == 17) {
            this.mChargingOuter.setImageResource(R.drawable.ic_sysbar_battery_pre_cool);
        } else {
            this.mChargingOuter.setImageResource(R.drawable.ic_sysbar_battery_0);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setChildMode(boolean on) {
        XImageView xImageView = this.mBtnChildMode;
        if (xImageView != null) {
            xImageView.setVisibility(on ? 0 : 8);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setVisibility(boolean visible) {
        FrameLayout frameLayout = this.mStatusBarView;
        if (frameLayout != null) {
            frameLayout.setVisibility(visible ? 0 : 8);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView, com.xiaopeng.systemui.IView
    public void onThemeChanged() {
        updateDownloadStatus();
        setOverflowStatus();
        updateLockStatus(this.mPresenter.isCenterLocked());
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDiagnosticMode(boolean on) {
        AnimatedImageView animatedImageView = this.mDiagnosticModeBtn;
        if (animatedImageView != null) {
            animatedImageView.setVisibility(on ? 0 : 8);
        }
    }

    private AnimatedVectorDrawable getBluetoothAnimatedVectorDrawable() {
        Drawable drawable = this.mBluetooth.getDrawable();
        if (drawable instanceof LevelListDrawable) {
            LevelListDrawable levelListDrawable = (LevelListDrawable) drawable;
            Drawable currentDrawable = levelListDrawable.getCurrent();
            if (currentDrawable instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable animatedDrawable = (AnimatedVectorDrawable) currentDrawable;
                return animatedDrawable;
            }
            return null;
        }
        return null;
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setBluetoothStatus(int state) {
        showBluetoothConnectingAnim(false);
        if (state == 1) {
            showBluetoothConnectingAnim(true);
        } else if (state == 2) {
            this.mBluetooth.setImageResource(0);
            this.mBluetooth.setImageBitmap(BitmapHelper.createBluetoothBitmap(this.mContext, 0));
        } else {
            showBluetoothConnectingAnim(false);
            this.mBluetooth.setImageResource(R.drawable.ic_sysbar_bluetooth);
            this.mBluetooth.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_bluetooth));
            this.mBluetooth.setImageLevel(BluetoothViewModel.getBluetoothLevel(state));
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void showDownloadIcon(boolean hasDownload) {
        this.mStatusBarItemManager.showAndCheckOverflow(5, hasDownload);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDownloadStatus(int downloadStatus) {
        Logger.d(TAG, "updateDownloadStatus : " + downloadStatus);
        if (downloadStatus == 1) {
            startDownloadAnim();
        } else if (downloadStatus == 3) {
            pauseDownloadAnim();
        } else if (downloadStatus == 4 || downloadStatus == 5) {
            stopDownloadAnim();
        }
        this.mDownloadStatus = downloadStatus;
    }

    private void setOverflowStatus() {
        boolean isOverflowBtnAnimFinished = this.mStatusBarItemManager.isOverflowButtonAnimFinished();
        if (isOverflowBtnAnimFinished) {
            resetOverflowBtnStatus();
        } else {
            AnimationHelper.startAnimOnce(this.mBtnOverflow, R.drawable.anim_sysbar_overflow, R.drawable.ic_sysbar_overflow, this.mStatusBarItemManager);
        }
    }

    private void updateDownloadStatus() {
        this.mDownloadAnimStarted = false;
        if (this.mDownloadStatus == 3) {
            startDownloadAnim();
        }
        setDownloadStatus(this.mDownloadStatus);
    }

    private void resetOverflowBtnStatus() {
        this.mBtnOverflow.setImageResource(R.drawable.ic_sysbar_overflow);
        this.mBtnOverflow.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_overflow));
    }

    private void startDownloadAnim() {
        Logger.d(TAG, "startDownloadAnim :" + this.mDownloadStatus);
        if (this.mDownloadAnimStarted && this.mDownloadStatus == 3) {
            AnimationHelper.resumeAnim(this.mDownload);
            return;
        }
        this.mDownloadAnimStarted = true;
        AnimationHelper.startAnimInfiniteWithoutDestroy(this.mDownload, R.drawable.anim_sysbar_download);
    }

    private void stopDownloadAnim() {
        Logger.d(TAG, "stopDownloadAnim");
        this.mDownloadAnimStarted = false;
        AnimationHelper.destroyAnim(this.mDownload);
        this.mDownload.setImageResource(R.drawable.ic_sysbar_download);
        this.mDownload.setImageDrawable(this.mContext.getDrawable(R.drawable.ic_sysbar_download));
    }

    private void pauseDownloadAnim() {
        Logger.d(TAG, "pauseDownloadAnim");
        AnimationHelper.pauseAnim(this.mDownload);
    }

    private void showBluetoothConnectingAnim(boolean b) {
        Logger.d(TAG, "showBluetoothConnectingAnim : b = " + b);
        this.mBluetoothAnimatedVectorDrawable = getBluetoothAnimatedVectorDrawable();
        if (b) {
            startAnimatedVectorDrawable(this.mBluetoothAnimatedVectorDrawable);
        } else {
            stopAnimatedVectorDrawable(this.mBluetoothAnimatedVectorDrawable);
        }
    }

    private void startAnimatedVectorDrawable(AnimatedVectorDrawable animatedVectorDrawable) {
        if (animatedVectorDrawable != null && !animatedVectorDrawable.isRunning()) {
            animatedVectorDrawable.start();
        }
    }

    private void stopAnimatedVectorDrawable(AnimatedVectorDrawable animatedVectorDrawable) {
        if (animatedVectorDrawable != null && animatedVectorDrawable.isRunning()) {
            animatedVectorDrawable.stop();
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            return false;
        }
        int id = view.getId();
        if (id == R.id.clock) {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.CLOCK_ID);
            return true;
        } else if (id != R.id.signal) {
            return false;
        } else {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.G4_ID);
            return true;
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSeatVentLevel(int level) {
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSeatHeatLevel(int level) {
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseStatusbarView, com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setSrsState(boolean state) {
        if (state) {
            SpannableString s = new SpannableString(this.mContext.getString(R.string.passenger_airbag_on));
            s.setSpan(new ForegroundColorSpan(this.mContext.getColor(R.color.x_statusbar_srs_yellow)), 18, 20, 33);
            this.mSrsTxt.setText(s);
            this.mSrsImage.setImageResource(R.drawable.ic_mid_pabon);
            return;
        }
        SpannableString s2 = new SpannableString(this.mContext.getString(R.string.passenger_airbag_off));
        s2.setSpan(new ForegroundColorSpan(this.mContext.getColor(R.color.x_statusbar_srs_yellow)), 17, 20, 33);
        this.mSrsTxt.setText(s2);
        this.mSrsImage.setImageResource(R.drawable.ic_mid_paboff);
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setECallEnable(boolean enable) {
        Logger.d(TAG, "setECallEnable " + enable + " , " + this.mECallBtn);
        AnimatedImageView animatedImageView = this.mECallBtn;
        if (animatedImageView != null) {
            animatedImageView.setVisibility(enable ? 0 : 8);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setIHBStatus(int status) {
        XImageView xImageView = this.mIhb;
        if (xImageView == null) {
            return;
        }
        if (status == 0) {
            xImageView.setVisibility(8);
        } else if (status == 1 || status == 2) {
            this.mIhb.setImageLevel(status - 1);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setDashCamStatus(int status) {
    }

    @Override // com.xiaopeng.systemui.statusbar.IStatusbarView
    public void setStatusBarIcon(int status, SystemBarRecord item) {
    }
}
