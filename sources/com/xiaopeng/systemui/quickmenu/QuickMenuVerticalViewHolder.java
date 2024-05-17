package com.xiaopeng.systemui.quickmenu;

import android.util.Log;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.quickmenu.widgets.XSelectorAnimButton;
import com.xiaopeng.systemui.quickmenu.widgets.XTileButton;
import com.xiaopeng.util.FeatureOption;
import com.xiaopeng.xui.app.XToast;
/* loaded from: classes24.dex */
public class QuickMenuVerticalViewHolder extends BaseQuickMenuViewHolder implements XSelectorAnimButton.OnSelectChangedListener {
    public static final String TAG = "QuickMenuVerticalViewHolder";
    private XTileButton m360Btn;
    private XTileButton mAirCleanBtn;
    private XTileButton mAutoParkBtn;
    private XTileButton mDeodorizeBtn;
    private XTileButton mDownHillBtn;
    private XSelectorAnimButton mDriverModeBtn;
    private IntervalControl mIntervalControlDriverMode;
    private XTileButton mMeditationModeBtn;
    private XTileButton mMovieModeBtn;
    private XTileButton mRapidCoolingBtn;
    private XTileButton mRearMirrorCloseBtn;
    private XTileButton mRearMirrorOpenBtn;
    private XTileButton mSleepModeBtn;
    private XTileButton mTruckBtn;
    private XTileButton mWindowAirBtn;
    private XTileButton mWindowCloseBtn;
    private XTileButton mWindowOpenBtn;

    @Override // com.xiaopeng.systemui.quickmenu.BaseQuickMenuViewHolder, com.xiaopeng.systemui.quickmenu.MetaQuickMenuViewHolder
    public void initView() {
        this.mDriverModeBtn = (XSelectorAnimButton) this.mView.findViewById(R.id.quick_menu_driver_mode);
        this.mWindowOpenBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_window_open);
        this.mWindowCloseBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_window_close);
        this.mWindowAirBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_window_air);
        this.mAutoParkBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_auto_park);
        this.mRearMirrorOpenBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_rear_mirror_open);
        this.mRearMirrorCloseBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_rear_mirror_close);
        this.mDownHillBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_down_hill);
        this.mRapidCoolingBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_rapid_cooling);
        this.mDeodorizeBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_deodorize);
        this.mMeditationModeBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_meditation_mode);
        this.mSleepModeBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_space_capsule_sleep);
        this.mMovieModeBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_space_capsule_movie);
        this.mAirCleanBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_air_clean);
        this.mTruckBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_truck);
        this.m360Btn = (XTileButton) this.mView.findViewById(R.id.quick_menu_360);
        this.mDriverModeBtn.setOnSelectChangedListener(this);
        this.mWindowOpenBtn.setOnClickListener(this);
        this.mWindowCloseBtn.setOnClickListener(this);
        this.mWindowAirBtn.setOnClickListener(this);
        this.mAutoParkBtn.setOnClickListener(this);
        this.mRearMirrorOpenBtn.setOnClickListener(this);
        this.mRearMirrorCloseBtn.setOnClickListener(this);
        this.mDownHillBtn.setOnClickListener(this);
        this.mRapidCoolingBtn.setOnClickListener(this);
        this.mDeodorizeBtn.setOnClickListener(this);
        this.mMeditationModeBtn.setOnClickListener(this);
        this.mMovieModeBtn.setOnClickListener(this);
        this.mSleepModeBtn.setOnClickListener(this);
        this.mAirCleanBtn.setOnClickListener(this);
        this.m360Btn.setOnClickListener(this);
        this.mMovieModeBtn.setVisibility((this.mIsChinese && isSpaceModeEnable()) ? 0 : 8);
        this.mSleepModeBtn.setVisibility(isSpaceModeEnable() ? 0 : 8);
        boolean is360Support = CarModelsManager.getFeature().isSupport360() && CarModelsManager.getConfig().isAVMSupport();
        this.m360Btn.setVisibility(is360Support ? 0 : 8);
        if (CarModelsManager.getFeature().isOnlyOpenBackBoxSupport()) {
            this.mTruckBtn.setVisibility(0);
            this.mTruckBtn.setOnClickListener(this);
        }
        if (!CarModelsManager.getFeature().isMeditationModeSupport() || FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED) {
            this.mMeditationModeBtn.setVisibility(8);
        }
        if (!CarModelsManager.getConfig().isRearMirrorFoldSupport()) {
            this.mRearMirrorCloseBtn.setVisibility(8);
            this.mRearMirrorOpenBtn.setVisibility(8);
        }
        this.mAirCleanBtn.setVisibility(isAirCleanEnable() ? 0 : 8);
        this.mIntervalControlDriverMode = new IntervalControl("quickmenu-driverMode");
    }

    @Override // com.xiaopeng.systemui.quickmenu.MetaQuickMenuViewHolder
    int getLayoutId() {
        return R.layout.quick_menu_vertical_tiles;
    }

    public void checkDriverMode(int state) {
        if (state != 1) {
            if (state == 2) {
                this.mDriverModeBtn.setSelectorIndex(1);
                this.mDriverModeBtn.setEnabled(true, true);
                return;
            } else if (state == 3) {
                this.mDriverModeBtn.setSelectorIndex(0);
                this.mDriverModeBtn.setEnabled(true, true);
                return;
            } else {
                this.mDriverModeBtn.setAllUnSelect();
                return;
            }
        }
        this.mDriverModeBtn.setSelectorIndex(2);
        this.mDriverModeBtn.setEnabled(true, true);
    }

    private void refreshWindow(int value) {
        int stateFullWindowClose = value % 10;
        int stateFullWindowOpen = value / 10;
        if (CarModelsManager.getFeature().isWindowLoadingDisable()) {
            if (stateFullWindowClose == 3) {
                this.mWindowCloseBtn.setEnabled(false);
                this.mWindowOpenBtn.setEnabled(false);
                this.mWindowAirBtn.setEnabled(false);
                return;
            }
            this.mWindowCloseBtn.setEnabled(true);
            this.mWindowOpenBtn.setEnabled(true);
            this.mWindowAirBtn.setEnabled(true);
            return;
        }
        this.mWindowCloseBtn.setEnabled(stateFullWindowClose == 2 || stateFullWindowClose == 4);
        this.mWindowOpenBtn.setEnabled(stateFullWindowOpen == 1 || stateFullWindowOpen == 4);
        this.mWindowAirBtn.setEnabled(stateFullWindowClose == 1);
    }

    private void refreshSelectedBtn(String key, int value) {
        XTileButton xTileButton = null;
        if ("auto_hold_switch".equals(key)) {
            xTileButton = this.mAutoParkBtn;
        } else if ("downhill_auxiliary_switch".equals(key)) {
            xTileButton = this.mDownHillBtn;
        } else if ("intelligent_deodorization_switch".equals(key)) {
            xTileButton = this.mDeodorizeBtn;
        } else if ("rapid_cooling_switch".equals(key)) {
            xTileButton = this.mRapidCoolingBtn;
        } else if ("air_conditioning_cleaning_switch".equals(key)) {
            xTileButton = this.mAirCleanBtn;
        }
        if (value == 2) {
            xTileButton.setSelected(true);
        } else {
            xTileButton.setSelected(false);
        }
    }

    private void refreshMirror(int value) {
        int stateRearMirrorClose = value % 10;
        int stateRearMirrorOpen = value / 10;
        Log.d(TAG, "xptile d21 rear mirror close:" + stateRearMirrorClose + " open:" + stateRearMirrorOpen);
        if (stateRearMirrorClose == 3 || stateRearMirrorOpen == 3) {
            this.mRearMirrorCloseBtn.setEnabled(false);
            this.mRearMirrorOpenBtn.setEnabled(false);
            return;
        }
        this.mRearMirrorCloseBtn.setEnabled(true);
        this.mRearMirrorOpenBtn.setEnabled(true);
    }

    @Override // com.xiaopeng.systemui.quickmenu.BaseQuickMenuViewHolder, com.xiaopeng.systemui.quickmenu.MetaQuickMenuViewHolder
    public void themeChanged() {
        super.themeChanged();
        this.mDriverModeBtn.refreshTheme();
        this.mWindowCloseBtn.refreshTheme();
        this.mWindowOpenBtn.refreshTheme();
        this.mWindowAirBtn.refreshTheme();
        this.mAutoParkBtn.refreshTheme();
        this.mRearMirrorOpenBtn.refreshTheme();
        this.mRearMirrorCloseBtn.refreshTheme();
        this.mDownHillBtn.refreshTheme();
        this.mRapidCoolingBtn.refreshTheme();
        this.mDeodorizeBtn.refreshTheme();
        this.mAirCleanBtn.refreshTheme();
        this.mMeditationModeBtn.refreshTheme();
        this.mMovieModeBtn.refreshTheme();
        this.mSleepModeBtn.refreshTheme();
        this.mTruckBtn.refreshTheme();
        this.m360Btn.refreshTheme();
    }

    @Override // com.xiaopeng.systemui.quickmenu.BaseQuickMenuViewHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quick_menu_360 /* 2131362903 */:
                this.mClickKey = "panoramic_view";
                break;
            case R.id.quick_menu_air_clean /* 2131362904 */:
                this.mClickKey = "air_conditioning_cleaning_switch";
                break;
            case R.id.quick_menu_auto_park /* 2131362905 */:
                this.mClickKey = "auto_hold_switch";
                break;
            case R.id.quick_menu_deodorize /* 2131362913 */:
                this.mClickKey = "intelligent_deodorization_switch";
                break;
            case R.id.quick_menu_down_hill /* 2131362914 */:
                this.mClickKey = "downhill_auxiliary_switch";
                break;
            case R.id.quick_menu_meditation_mode /* 2131362917 */:
                this.mClickKey = "meditation_mode_switch";
                break;
            case R.id.quick_menu_rapid_cooling /* 2131362921 */:
                this.mClickKey = "rapid_cooling_switch";
                break;
            case R.id.quick_menu_rear_mirror_close /* 2131362922 */:
                this.mClickKey = "close_rear_mirror";
                break;
            case R.id.quick_menu_rear_mirror_open /* 2131362923 */:
                this.mClickKey = "open_rear_mirror";
                break;
            case R.id.quick_menu_space_capsule_movie /* 2131362925 */:
                this.mClickKey = "movie_mode_switch";
                break;
            case R.id.quick_menu_space_capsule_sleep /* 2131362926 */:
                this.mClickKey = "sleep_mode_switch";
                break;
            case R.id.quick_menu_truck /* 2131362928 */:
                this.mClickKey = "back_box_lock_switch";
                break;
            case R.id.quick_menu_window_air /* 2131362931 */:
                this.mClickKey = "open_window_air";
                break;
            case R.id.quick_menu_window_close /* 2131362932 */:
                this.mClickKey = "full_window_close_switch";
                break;
            case R.id.quick_menu_window_open /* 2131362933 */:
                this.mClickKey = "full_window_open_switch";
                break;
        }
        super.onClick(view);
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.XSelectorAnimButton.OnSelectChangedListener
    public void onSelectChanged(XSelectorAnimButton xSelectorButton, int index, boolean isFromUser) {
        if (isFromUser && this.mDriverModeBtn == xSelectorButton) {
            this.mQuickMenuHolderPresenter.onClickTile("driver_mode_switch", index);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.XSelectorAnimButton.OnSelectChangedListener
    public boolean onSelectIntercept(XSelectorAnimButton xSelectorButton, int index, boolean isFromUser) {
        IntervalControl intervalControl;
        if (this.mDriverModeBtn == xSelectorButton && (intervalControl = this.mIntervalControlDriverMode) != null && intervalControl.isFrequently(1000)) {
            Log.d(TAG, "quickmenu isFrequently key:drviermode");
            XToast.show((int) R.string.operation_fast_tips);
            return true;
        }
        return false;
    }

    private boolean isSpaceModeEnable() {
        return CarModelsManager.getConfig().isSleepSupport();
    }

    private boolean isAirCleanEnable() {
        return CarModelsManager.getConfig().isAirCleanSupport();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateControlBtn(int state) {
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateMusicProgress(long position, long duration) {
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateMusicInfo(String songTitle, String artist, String album, int stateMusic) {
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void enableMediaBtn(boolean state) {
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void themeChanged(boolean state) {
        themeChanged();
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void openNapaAppWindow(String tileKey) {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.systemui.quickmenu.BaseQuickMenuViewHolder, com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateViewState(String key, int value) {
        char c;
        super.updateViewState(key, value);
        switch (key.hashCode()) {
            case -2136764233:
                if (key.equals("air_conditioning_cleaning_switch")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -2078090512:
                if (key.equals("intelligent_deodorization_switch")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -2027031142:
                if (key.equals("full_window_close_switch")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -2009121622:
                if (key.equals("full_window_open_switch")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1674495063:
                if (key.equals("downhill_auxiliary_switch")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1473685539:
                if (key.equals("rapid_cooling_switch")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -551990896:
                if (key.equals("open_window_air")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 308941893:
                if (key.equals("open_rear_mirror")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 499705892:
                if (key.equals("auto_hold_switch")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1024440051:
                if (key.equals("close_rear_mirror")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1989404601:
                if (key.equals("driver_mode_switch")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                checkDriverMode(value);
                return;
            case 1:
            case 2:
            case 3:
                refreshWindow(value);
                return;
            case 4:
            case 5:
                refreshMirror(value);
                return;
            case 6:
            case 7:
            case '\b':
            case '\t':
            case '\n':
                refreshSelectedBtn(key, value);
                return;
            default:
                return;
        }
    }
}
