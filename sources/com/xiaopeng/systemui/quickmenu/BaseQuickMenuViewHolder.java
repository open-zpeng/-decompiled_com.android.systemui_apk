package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.quickmenu.widgets.QcSliderLayout;
import com.xiaopeng.systemui.quickmenu.widgets.XTileButton;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.xui.widget.XImageView;
/* loaded from: classes24.dex */
public abstract class BaseQuickMenuViewHolder extends MetaQuickMenuViewHolder implements IQuickMenuViewHolder, View.OnClickListener, QcSliderLayout.OnSlideChangeListener {
    protected String TAG = "BaseQuickMenuViewHolder";
    protected XTileButton mAutoWiperBtn;
    protected XTileButton mBackBoxCloseBtn;
    protected XTileButton mBackBoxOpenBtn;
    private QcSliderLayout mBrightnessSlider;
    protected XTileButton mCleanModeBtn;
    protected IQuickMenuHolderPresenter mQuickMenuHolderPresenter;
    protected XImageView mSoundImage;
    private QcSliderLayout mSoundSlider;
    protected XTileButton mVoiceinputBtn;

    public BaseQuickMenuViewHolder() {
        this.mIntervalControl = new IntervalControl("QuickMenu");
    }

    @Override // com.xiaopeng.systemui.quickmenu.MetaQuickMenuViewHolder, com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public View initView(Context context, ViewGroup viewGroup) {
        super.initView(context, viewGroup);
        this.mQuickMenuHolderPresenter = QuickMenuHolderPresenter.getInstance();
        this.mCleanModeBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_clean_mode);
        this.mAutoWiperBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_auto_wiper);
        this.mSoundSlider = (QcSliderLayout) this.mView.findViewById(R.id.quick_menu_music_volume);
        QcSliderLayout qcSliderLayout = this.mSoundSlider;
        if (qcSliderLayout != null) {
            this.mSoundImage = (XImageView) qcSliderLayout.findViewById(R.id.volume_indicator1);
        }
        this.mBrightnessSlider = (QcSliderLayout) this.mView.findViewById(R.id.quick_menu_bright);
        this.mBackBoxOpenBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_backbox_open);
        this.mBackBoxCloseBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_backbox_close);
        this.mCleanModeBtn.setOnClickListener(this);
        this.mAutoWiperBtn.setOnClickListener(this);
        this.mBrightnessSlider.setOnSlideChangeListener(this);
        QcSliderLayout qcSliderLayout2 = this.mSoundSlider;
        if (qcSliderLayout2 != null) {
            qcSliderLayout2.setOnSlideChangeListener(this);
        }
        this.mBackBoxOpenBtn.setOnClickListener(this);
        this.mBackBoxCloseBtn.setOnClickListener(this);
        this.mVoiceinputBtn = (XTileButton) this.mView.findViewById(R.id.quick_menu_speech_setting);
        if (this.mVoiceinputBtn != null && this.mIsChinese) {
            this.mVoiceinputBtn.setVisibility(0);
            this.mVoiceinputBtn.setOnClickListener(this);
        }
        if (CarModelsManager.getFeature().isOnlyOpenBackBoxSupport()) {
            this.mBackBoxCloseBtn.setVisibility(8);
            this.mBackBoxOpenBtn.setVisibility(8);
        } else {
            this.mBackBoxCloseBtn.setVisibility(0);
            this.mBackBoxOpenBtn.setVisibility(0);
        }
        int autoWiper = getAutoWiper();
        if (autoWiper == 1) {
            this.mAutoWiperBtn.setTextRes(R.string.qs_panel_auto_wiper_speed);
        } else if (autoWiper == 2) {
            this.mAutoWiperBtn.setTextRes(R.string.qs_panel_auto_wiper_speed_sensitivity);
        }
        initView();
        return this.mView;
    }

    @Override // com.xiaopeng.systemui.quickmenu.MetaQuickMenuViewHolder
    public void initView() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.MetaQuickMenuViewHolder
    public void themeChanged() {
        this.mCleanModeBtn.refreshTheme();
        this.mAutoWiperBtn.refreshTheme();
        QcSliderLayout qcSliderLayout = this.mSoundSlider;
        if (qcSliderLayout != null) {
            qcSliderLayout.refreshTheme();
        }
        this.mBrightnessSlider.refreshTheme();
        if (this.mVoiceinputBtn != null && this.mIsChinese) {
            this.mVoiceinputBtn.refreshTheme();
        }
        this.mBackBoxCloseBtn.refreshTheme();
        this.mBackBoxOpenBtn.refreshTheme();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateViewState(String key, int value) {
        char c;
        switch (key.hashCode()) {
            case 36070600:
                if (key.equals("open_back_box")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 940987760:
                if (key.equals("auto_wiper_speed_switch")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1829750706:
                if (key.equals("volume_adjustment")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1853221403:
                if (key.equals("brightness_adjustment")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            refreshAutoWiperDrawable(value);
        } else if (c == 1) {
            QcSliderLayout qcSliderLayout = this.mSoundSlider;
            if (qcSliderLayout != null && qcSliderLayout.getProgress() != value) {
                this.mSoundSlider.setProgress(value);
            }
        } else if (c != 2) {
            if (c == 3) {
                refreshBackBoxDrawable(value);
            }
        } else if (this.mBrightnessSlider.getProgress() != value) {
            this.mBrightnessSlider.setProgress(value);
        }
    }

    protected int getAutoWiper() {
        return CarModelsManager.getConfig().isAutoWiperSupport();
    }

    private void refreshAutoWiperDrawable(int state) {
        if (state == 0) {
            this.mAutoWiperBtn.setImageResource(R.drawable.ic_mid_qc_wiperwash_auto);
        } else if (state == 1) {
            this.mAutoWiperBtn.setImageResource(R.drawable.ic_mid_qc_wiperwash1);
        } else if (state == 2) {
            this.mAutoWiperBtn.setImageResource(R.drawable.ic_mid_qc_wiperwash2);
        } else if (state == 3) {
            this.mAutoWiperBtn.setImageResource(R.drawable.ic_mid_qc_wiperwash3);
        } else if (state == 4) {
            this.mAutoWiperBtn.setImageResource(R.drawable.ic_mid_qc_wiperwash4);
        }
    }

    public void refreshBackBoxDrawable(int state) {
        this.mBackBoxOpenBtn.setTextRes(R.string.qs_panel_backbox_open);
        this.mBackBoxOpenBtn.setImageResource(R.drawable.ic_mid_qc_trunk_open_selector);
        this.mBackBoxCloseBtn.setTextRes(R.string.qs_panel_backbox_close);
        this.mBackBoxCloseBtn.setImageResource(R.drawable.ic_mid_qc_trunk_close_selector);
        String str = this.TAG;
        Log.d(str, "xptile backbox state:" + state);
        switch (state) {
            case 1:
                setOpenCloseBackBoxState(true, false);
                return;
            case 2:
                setOpenCloseBackBoxState(false, true);
                return;
            case 3:
                this.mBackBoxOpenBtn.setTextRes(R.string.qs_panel_trunk_pause);
                setOpenCloseBackBoxState(true, false);
                return;
            case 4:
                this.mBackBoxCloseBtn.setTextRes(R.string.qs_panel_trunk_pause);
                setOpenCloseBackBoxState(false, true);
                return;
            case 5:
                this.mBackBoxOpenBtn.setTextRes(R.string.qs_panel_backbox_open);
                setOpenCloseBackBoxState(true, true);
                return;
            case 6:
                this.mBackBoxCloseBtn.setTextRes(R.string.qs_panel_backbox_close);
                setOpenCloseBackBoxState(true, true);
                return;
            case 7:
                setOpenCloseBackBoxState(false, false);
                return;
            default:
                return;
        }
    }

    private void setOpenCloseBackBoxState(boolean open, boolean close) {
        this.mBackBoxOpenBtn.setEnabled(open);
        this.mBackBoxCloseBtn.setEnabled(close);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.quick_menu_clean_mode) {
            this.mClickKey = "clean_mode";
        } else if (id != R.id.quick_menu_speech_setting) {
            switch (id) {
                case R.id.quick_menu_auto_wiper /* 2131362906 */:
                    this.mClickKey = "auto_wiper_speed_switch";
                    break;
                case R.id.quick_menu_backbox_close /* 2131362907 */:
                    this.mClickKey = "close_back_box";
                    break;
                case R.id.quick_menu_backbox_open /* 2131362908 */:
                    this.mClickKey = "open_back_box";
                    break;
            }
        } else {
            this.mClickKey = "speech_setting_switch";
        }
        if (this.mClickKey == null) {
            Log.d(this.TAG, "Find no key clicked");
        } else if ("auto_wiper_speed_switch".equals(this.mClickKey) && this.mIntervalControl.isFrequently(500)) {
            String str = this.TAG;
            Log.d(str, "quickmenu isFrequently key:" + this.mClickKey);
        } else {
            if (("rear_mirror_angle_switch".equals(this.mClickKey) || "clean_mode".equals(this.mClickKey) || "sleep_mode_switch".equals(this.mClickKey) || "movie_mode_switch".equals(this.mClickKey) || "speech_setting_switch".equals(this.mClickKey)) && (this.mParentView instanceof QuickMenuFloatingView)) {
                QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
            }
            this.mQuickMenuHolderPresenter.onClickTile(this.mClickKey, -1);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.QcSliderLayout.OnSlideChangeListener
    public void onProgressChanged(QcSliderLayout qcSliderLayout, int progress) {
        if (qcSliderLayout == this.mSoundSlider) {
            this.mQuickMenuHolderPresenter.onClickTile("volume_adjustment", progress);
        } else if (qcSliderLayout == this.mBrightnessSlider) {
            this.mQuickMenuHolderPresenter.onClickTile("brightness_adjustment", progress);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.QcSliderLayout.OnSlideChangeListener
    public void onStartTrackingTouch(QcSliderLayout qcSliderLayout) {
        if (qcSliderLayout == this.mSoundSlider) {
            this.mQuickMenuHolderPresenter.unRegisterSoundCallback();
        } else if (qcSliderLayout == this.mBrightnessSlider) {
            this.mQuickMenuHolderPresenter.unRegisterBrightnessCallback();
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.widgets.QcSliderLayout.OnSlideChangeListener
    public void onStopTrackingTouch(QcSliderLayout qcSliderLayout) {
        if (qcSliderLayout == this.mSoundSlider) {
            this.mQuickMenuHolderPresenter.registerSoundCallback();
            DataLogUtils.sendDataLog("P00005", "B004");
        } else if (qcSliderLayout == this.mBrightnessSlider) {
            this.mQuickMenuHolderPresenter.registerBrightnessCallback();
            DataLogUtils.sendDataLog("P00005", "B001");
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void initSlider(int maxWind, int maxVolume, int minTemperature, int maxTemperature) {
        this.mBrightnessSlider.setMin(1);
        this.mBrightnessSlider.setMax(Config.BRIGHTNESS_TO_MAX_VALUE);
        QcSliderLayout qcSliderLayout = this.mSoundSlider;
        if (qcSliderLayout != null) {
            qcSliderLayout.setMax(maxVolume);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateSoundType(int state) {
    }
}
