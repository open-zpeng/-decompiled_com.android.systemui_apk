package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.TileViewModel;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider;
import com.xiaopeng.systemui.quickmenu.widgets.VerticalSimpleSlider;
import com.xiaopeng.systemui.quickmenu.widgets.XTileButton;
import com.xiaopeng.systemui.ui.widget.AnimatedProgressBar;
import com.xiaopeng.systemui.ui.widget.TemperatureTextView;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.car.BcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.IBcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.IHvacViewModel;
import com.xiaopeng.xui.widget.XConstraintLayout;
/* loaded from: classes24.dex */
public class SecondNavigationBar extends XConstraintLayout implements LifecycleOwner, View.OnClickListener {
    private static final int MSG_SET_PSN_TEMPERATURE = 2001;
    private static final int MSG_UPDATE_PSN_SEAT_HEAT = 2003;
    private static final int MSG_UPDATE_PSN_SEAT_VENT = 2004;
    private static final int MSG_UPDATE_PSN_TEMPERATURE = 2002;
    private static final int PROGRESS_SCALE = 10;
    private static final String TAG = "SecondaryNavigationWindow";
    private final AnimatedProgressBar.OnProgressListener mAnimatedProgressBarListener;
    private BcmViewModel mBcmViewModel;
    private Handler mHandler;
    private XTileButton mHeadSetBtn;
    private float mHvacPsnTempPassenger;
    private HvacViewModel mHvacViewModel;
    private LifecycleRegistry mLifecycleRegistry;
    private AnimatedProgressBar mMusicVolumeProgressBar;
    private TileViewModel mNavTileViewModel;
    private int mPsnSeatHeatLevel;
    private int mPsnSeatVentLevel;
    private AnimatedProgressBar mPsnTemperatureProgressBar;
    private TemperatureTextView mPsnTemperatureTV;
    private final SimpleSlider.OnSlideChangeListener mSlideChangeListener;
    private VerticalSimpleSlider mWindSlider;

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePsnTemperature(float temperature) {
        AnimatedProgressBar progressBar = this.mPsnTemperatureProgressBar;
        Logger.d(TAG, "updatePsnTemperature temperature =" + temperature);
        if (progressBar != null && !progressBar.isTouchTracking()) {
            progressBar.setProgress(Utils.convertTemperatureToProgress(temperature, 10));
            setPsnTemperatureTV(temperature);
        }
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    public SecondNavigationBar(Context context) {
        super(context);
        this.mHvacPsnTempPassenger = 18.0f;
        this.mPsnSeatHeatLevel = 0;
        this.mPsnSeatVentLevel = 0;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2001:
                        float temperature = ((Float) msg.obj).floatValue();
                        Logger.d(SecondNavigationBar.TAG, "MSG_SET_PSN_TEMPERATURE temperature=" + temperature);
                        SecondNavigationBar.this.mHvacViewModel.setHvacPassengerTemperature(temperature);
                        return;
                    case 2002:
                        SecondNavigationBar.this.updatePsnTemperature(((Float) msg.obj).floatValue());
                        return;
                    case 2003:
                        SecondNavigationBar.this.mPsnSeatHeatLevel = msg.arg1;
                        Logger.d(SecondNavigationBar.TAG, "MSG_UPDATE_PSN_SEAT_HEAT level = " + SecondNavigationBar.this.mPsnSeatHeatLevel);
                        return;
                    case 2004:
                        SecondNavigationBar.this.mPsnSeatVentLevel = msg.arg1;
                        Logger.d(SecondNavigationBar.TAG, "MSG_UPDATE_PSN_SEAT_VENT level = " + SecondNavigationBar.this.mPsnSeatVentLevel);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mSlideChangeListener = new SimpleSlider.OnSlideChangeListener() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.2
            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onProgressChanged(SimpleSlider simpleSlider, int progress, boolean fromUser) {
                if (simpleSlider == SecondNavigationBar.this.mWindSlider && fromUser && progress < 1) {
                    simpleSlider.setProgress(1);
                }
            }

            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onStartTrackingTouch(SimpleSlider simpleSlider) {
                SecondNavigationBar.this.mNavTileViewModel.unregisterWindCallback();
            }

            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onStopTrackingTouch(SimpleSlider simpleSlider) {
                SecondNavigationBar.this.mNavTileViewModel.registerWindCallback();
                if (simpleSlider == SecondNavigationBar.this.mWindSlider) {
                    SecondNavigationBar.this.mNavTileViewModel.onClickTileView("wind_adjustment", simpleSlider.getProgress());
                }
            }
        };
        this.mAnimatedProgressBarListener = new AnimatedProgressBar.OnProgressListener() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.3
            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onProgressChanged(AnimatedProgressBar progressBar, int progress, boolean fromUser) {
                if (progressBar != SecondNavigationBar.this.mPsnTemperatureProgressBar) {
                    if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                        SecondNavigationBar.this.mNavTileViewModel.onClickTileView("volume_adjustment", progressBar.getProgress());
                        return;
                    }
                    return;
                }
                float temperature = Utils.convertProgressToTemperature(progress, 10);
                Logger.e(SecondNavigationBar.TAG, "onProgressChanged i=" + progress + " fromUser=" + fromUser + " temperature=" + temperature);
                SecondNavigationBar.this.setPsnTemperatureTV(temperature);
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStartTrackingTouch(AnimatedProgressBar progressBar) {
                if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                    SecondNavigationBar.this.mNavTileViewModel.unregisterSoundCallback();
                }
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStopTrackingTouch(AnimatedProgressBar progressBar) {
                if (progressBar != null) {
                    if (progressBar != SecondNavigationBar.this.mPsnTemperatureProgressBar) {
                        if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                            SecondNavigationBar.this.mNavTileViewModel.registerSoundCallback();
                            return;
                        }
                        return;
                    }
                    int progress = progressBar.getProgress();
                    float temperature = Utils.convertProgressToTemperature(progress, 10);
                    SecondNavigationBar.this.setPsnTemperature(temperature);
                    Logger.e(SecondNavigationBar.TAG, "onStopTrackingTouch type=2 progress=" + progress + " temperature=" + temperature);
                }
            }
        };
    }

    public SecondNavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHvacPsnTempPassenger = 18.0f;
        this.mPsnSeatHeatLevel = 0;
        this.mPsnSeatVentLevel = 0;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2001:
                        float temperature = ((Float) msg.obj).floatValue();
                        Logger.d(SecondNavigationBar.TAG, "MSG_SET_PSN_TEMPERATURE temperature=" + temperature);
                        SecondNavigationBar.this.mHvacViewModel.setHvacPassengerTemperature(temperature);
                        return;
                    case 2002:
                        SecondNavigationBar.this.updatePsnTemperature(((Float) msg.obj).floatValue());
                        return;
                    case 2003:
                        SecondNavigationBar.this.mPsnSeatHeatLevel = msg.arg1;
                        Logger.d(SecondNavigationBar.TAG, "MSG_UPDATE_PSN_SEAT_HEAT level = " + SecondNavigationBar.this.mPsnSeatHeatLevel);
                        return;
                    case 2004:
                        SecondNavigationBar.this.mPsnSeatVentLevel = msg.arg1;
                        Logger.d(SecondNavigationBar.TAG, "MSG_UPDATE_PSN_SEAT_VENT level = " + SecondNavigationBar.this.mPsnSeatVentLevel);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mSlideChangeListener = new SimpleSlider.OnSlideChangeListener() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.2
            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onProgressChanged(SimpleSlider simpleSlider, int progress, boolean fromUser) {
                if (simpleSlider == SecondNavigationBar.this.mWindSlider && fromUser && progress < 1) {
                    simpleSlider.setProgress(1);
                }
            }

            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onStartTrackingTouch(SimpleSlider simpleSlider) {
                SecondNavigationBar.this.mNavTileViewModel.unregisterWindCallback();
            }

            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onStopTrackingTouch(SimpleSlider simpleSlider) {
                SecondNavigationBar.this.mNavTileViewModel.registerWindCallback();
                if (simpleSlider == SecondNavigationBar.this.mWindSlider) {
                    SecondNavigationBar.this.mNavTileViewModel.onClickTileView("wind_adjustment", simpleSlider.getProgress());
                }
            }
        };
        this.mAnimatedProgressBarListener = new AnimatedProgressBar.OnProgressListener() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.3
            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onProgressChanged(AnimatedProgressBar progressBar, int progress, boolean fromUser) {
                if (progressBar != SecondNavigationBar.this.mPsnTemperatureProgressBar) {
                    if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                        SecondNavigationBar.this.mNavTileViewModel.onClickTileView("volume_adjustment", progressBar.getProgress());
                        return;
                    }
                    return;
                }
                float temperature = Utils.convertProgressToTemperature(progress, 10);
                Logger.e(SecondNavigationBar.TAG, "onProgressChanged i=" + progress + " fromUser=" + fromUser + " temperature=" + temperature);
                SecondNavigationBar.this.setPsnTemperatureTV(temperature);
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStartTrackingTouch(AnimatedProgressBar progressBar) {
                if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                    SecondNavigationBar.this.mNavTileViewModel.unregisterSoundCallback();
                }
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStopTrackingTouch(AnimatedProgressBar progressBar) {
                if (progressBar != null) {
                    if (progressBar != SecondNavigationBar.this.mPsnTemperatureProgressBar) {
                        if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                            SecondNavigationBar.this.mNavTileViewModel.registerSoundCallback();
                            return;
                        }
                        return;
                    }
                    int progress = progressBar.getProgress();
                    float temperature = Utils.convertProgressToTemperature(progress, 10);
                    SecondNavigationBar.this.setPsnTemperature(temperature);
                    Logger.e(SecondNavigationBar.TAG, "onStopTrackingTouch type=2 progress=" + progress + " temperature=" + temperature);
                }
            }
        };
    }

    public SecondNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHvacPsnTempPassenger = 18.0f;
        this.mPsnSeatHeatLevel = 0;
        this.mPsnSeatVentLevel = 0;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 2001:
                        float temperature = ((Float) msg.obj).floatValue();
                        Logger.d(SecondNavigationBar.TAG, "MSG_SET_PSN_TEMPERATURE temperature=" + temperature);
                        SecondNavigationBar.this.mHvacViewModel.setHvacPassengerTemperature(temperature);
                        return;
                    case 2002:
                        SecondNavigationBar.this.updatePsnTemperature(((Float) msg.obj).floatValue());
                        return;
                    case 2003:
                        SecondNavigationBar.this.mPsnSeatHeatLevel = msg.arg1;
                        Logger.d(SecondNavigationBar.TAG, "MSG_UPDATE_PSN_SEAT_HEAT level = " + SecondNavigationBar.this.mPsnSeatHeatLevel);
                        return;
                    case 2004:
                        SecondNavigationBar.this.mPsnSeatVentLevel = msg.arg1;
                        Logger.d(SecondNavigationBar.TAG, "MSG_UPDATE_PSN_SEAT_VENT level = " + SecondNavigationBar.this.mPsnSeatVentLevel);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mSlideChangeListener = new SimpleSlider.OnSlideChangeListener() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.2
            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onProgressChanged(SimpleSlider simpleSlider, int progress, boolean fromUser) {
                if (simpleSlider == SecondNavigationBar.this.mWindSlider && fromUser && progress < 1) {
                    simpleSlider.setProgress(1);
                }
            }

            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onStartTrackingTouch(SimpleSlider simpleSlider) {
                SecondNavigationBar.this.mNavTileViewModel.unregisterWindCallback();
            }

            @Override // com.xiaopeng.systemui.quickmenu.widgets.SimpleSlider.OnSlideChangeListener
            public void onStopTrackingTouch(SimpleSlider simpleSlider) {
                SecondNavigationBar.this.mNavTileViewModel.registerWindCallback();
                if (simpleSlider == SecondNavigationBar.this.mWindSlider) {
                    SecondNavigationBar.this.mNavTileViewModel.onClickTileView("wind_adjustment", simpleSlider.getProgress());
                }
            }
        };
        this.mAnimatedProgressBarListener = new AnimatedProgressBar.OnProgressListener() { // from class: com.xiaopeng.systemui.secondarywindow.SecondNavigationBar.3
            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onProgressChanged(AnimatedProgressBar progressBar, int progress, boolean fromUser) {
                if (progressBar != SecondNavigationBar.this.mPsnTemperatureProgressBar) {
                    if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                        SecondNavigationBar.this.mNavTileViewModel.onClickTileView("volume_adjustment", progressBar.getProgress());
                        return;
                    }
                    return;
                }
                float temperature = Utils.convertProgressToTemperature(progress, 10);
                Logger.e(SecondNavigationBar.TAG, "onProgressChanged i=" + progress + " fromUser=" + fromUser + " temperature=" + temperature);
                SecondNavigationBar.this.setPsnTemperatureTV(temperature);
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStartTrackingTouch(AnimatedProgressBar progressBar) {
                if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                    SecondNavigationBar.this.mNavTileViewModel.unregisterSoundCallback();
                }
            }

            @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
            public void onStopTrackingTouch(AnimatedProgressBar progressBar) {
                if (progressBar != null) {
                    if (progressBar != SecondNavigationBar.this.mPsnTemperatureProgressBar) {
                        if (progressBar == SecondNavigationBar.this.mMusicVolumeProgressBar) {
                            SecondNavigationBar.this.mNavTileViewModel.registerSoundCallback();
                            return;
                        }
                        return;
                    }
                    int progress = progressBar.getProgress();
                    float temperature = Utils.convertProgressToTemperature(progress, 10);
                    SecondNavigationBar.this.setPsnTemperature(temperature);
                    Logger.e(SecondNavigationBar.TAG, "onStopTrackingTouch type=2 progress=" + progress + " temperature=" + temperature);
                }
            }
        };
    }

    private void initValue() {
        this.mHvacPsnTempPassenger = this.mHvacViewModel.getHvacPassengerTemperature();
        setPsnTemperatureTV(this.mHvacPsnTempPassenger);
        this.mMusicVolumeProgressBar.setMin(0);
        int maxSound = this.mNavTileViewModel.getSoundMaxValue();
        this.mMusicVolumeProgressBar.setMax(maxSound);
        int windMax = this.mNavTileViewModel.getWindMaxValue();
        if (windMax > 0) {
            this.mWindSlider.setMin(0);
            this.mWindSlider.setMax(windMax);
        }
        this.mWindSlider.setProgress(this.mNavTileViewModel.getCurrentState("wind_adjustment"));
        this.mPsnSeatHeatLevel = this.mBcmViewModel.getPsnSeatHeatLevel().getValue().intValue();
        this.mPsnSeatVentLevel = this.mBcmViewModel.getPsnSeatVentLevel().getValue().intValue();
        this.mPsnTemperatureProgressBar.setProgress(Utils.convertTemperatureToProgress(this.mHvacPsnTempPassenger, 10));
        this.mMusicVolumeProgressBar.setProgress(this.mNavTileViewModel.getCurrentState("volume_adjustment"));
    }

    private void initViewModel() {
        this.mNavTileViewModel = new TileViewModel(this.mContext.getApplicationContext());
        this.mNavTileViewModel.getTileLiveData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondNavigationBar$QyVbyqSZBPHGlCNN3w9jTjYw2Ck
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SecondNavigationBar.this.lambda$initViewModel$0$SecondNavigationBar((Pair) obj);
            }
        });
        this.mHvacViewModel = (HvacViewModel) ViewModelManager.getInstance().getViewModel(IHvacViewModel.class, this.mContext);
        this.mHvacViewModel.getHvacTempPassengerData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondNavigationBar$KeqShxlP6LOkH8oaUygpcpFDr38
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SecondNavigationBar.this.lambda$initViewModel$1$SecondNavigationBar((Float) obj);
            }
        });
        this.mBcmViewModel = (BcmViewModel) ViewModelManager.getInstance().getViewModel(IBcmViewModel.class, this.mContext);
        this.mBcmViewModel.getPsnSeatHeatLevel().observe(this, new Observer() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondNavigationBar$QGsZZW29wiojPPkxh9liQweP8yY
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SecondNavigationBar.this.lambda$initViewModel$2$SecondNavigationBar((Integer) obj);
            }
        });
        this.mBcmViewModel.getPsnSeatVentLevel().observe(this, new Observer() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondNavigationBar$wjepvc02OZ9M2_mE6W2KQWecerA
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SecondNavigationBar.this.lambda$initViewModel$3$SecondNavigationBar((Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initViewModel$0$SecondNavigationBar(Pair pair) {
        char c;
        int state = ((Integer) pair.second).intValue();
        String str = (String) pair.first;
        int hashCode = str.hashCode();
        if (hashCode != -1654800892) {
            if (hashCode == 1829750706 && str.equals("volume_adjustment")) {
                c = 0;
            }
            c = 65535;
        } else {
            if (str.equals("wind_adjustment")) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            if (this.mMusicVolumeProgressBar.getProgress() != state) {
                this.mMusicVolumeProgressBar.setProgress(state);
            }
        } else if (c == 1) {
            if (state <= 0) {
                this.mWindSlider.setProgress(0);
            } else if (state <= this.mNavTileViewModel.getWindMaxValue()) {
                if (this.mWindSlider.getProgress() != state) {
                    this.mWindSlider.setProgress(state);
                }
            } else {
                this.mWindSlider.setProgress(this.mNavTileViewModel.getWindMaxValue());
            }
        }
    }

    public /* synthetic */ void lambda$initViewModel$1$SecondNavigationBar(Float value) {
        if (this.mHvacPsnTempPassenger != value.floatValue()) {
            onPsnTemperatureChanged(value.floatValue());
        }
    }

    public /* synthetic */ void lambda$initViewModel$2$SecondNavigationBar(Integer value) {
        if (this.mPsnSeatHeatLevel != value.intValue()) {
            onPsnSeatHeatLevelChanged(value.intValue());
        }
    }

    public /* synthetic */ void lambda$initViewModel$3$SecondNavigationBar(Integer value) {
        if (this.mPsnSeatVentLevel != value.intValue()) {
            onPsnSeatVentLevelChanged(value.intValue());
        }
    }

    private void initView() {
        Logger.d(TAG, "Second NavigationBar init");
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
        initViewModel();
        this.mPsnTemperatureTV = (TemperatureTextView) findViewById(R.id.second_window_navi_temperature);
        this.mPsnTemperatureProgressBar = (AnimatedProgressBar) findViewById(R.id.second_window_navi_hvac_progress_bar);
        this.mWindSlider = (VerticalSimpleSlider) findViewById(R.id.second_window_navi_air_progress_bar);
        this.mMusicVolumeProgressBar = (AnimatedProgressBar) findViewById(R.id.second_window_navi_music_volume_progress_bar);
        this.mHeadSetBtn = (XTileButton) findViewById(R.id.second_window_navi_headset_btn);
        this.mPsnTemperatureProgressBar.setProgressListener(this.mAnimatedProgressBarListener);
        this.mWindSlider.setOnSlideChangeListener(this.mSlideChangeListener);
        this.mMusicVolumeProgressBar.setProgressListener(this.mAnimatedProgressBarListener);
        this.mHeadSetBtn.setOnClickListener(this);
        initValue();
        this.mNavTileViewModel.onStartVm();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v != null && v.isClickable() && !Utils.isFastClick()) {
            Logger.d(TAG, "onItemClicked() called with: view = [" + v + NavigationBarInflaterView.SIZE_MOD_END);
            if (v == this.mHeadSetBtn) {
                Intent intent = new Intent("com.xiaopeng.intent.action.POPUP_PSN_BLUETOOTH");
                intent.setFlags(268435456);
                PackageHelper.startActivityInSecondaryWindow(this.mContext, intent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XConstraintLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!ThemeManager.isThemeChanged(newConfig) || this.mContext == null) {
            return;
        }
        this.mHeadSetBtn.refreshTheme();
        this.mWindSlider.refreshVisual();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void onPsnSeatHeatLevelChanged(int heatLevel) {
        this.mHandler.removeMessages(2003);
        Message msg = this.mHandler.obtainMessage(2003);
        msg.arg1 = heatLevel;
        this.mHandler.sendMessage(msg);
    }

    private void onPsnSeatVentLevelChanged(int ventLevel) {
        this.mHandler.removeMessages(2004);
        Message msg = this.mHandler.obtainMessage(2004);
        msg.arg1 = ventLevel;
        this.mHandler.sendMessage(msg);
    }

    private void onPsnTemperatureChanged(float value) {
        this.mHandler.removeMessages(2002);
        Message msg = this.mHandler.obtainMessage(2002);
        msg.obj = Float.valueOf(value);
        this.mHandler.sendMessage(msg);
    }

    public void setPsnTemperatureTV(float text) {
        this.mHvacPsnTempPassenger = text;
        this.mPsnTemperatureTV.setText(text);
    }

    public void setPsnTemperature(float temperature) {
        Message msg = this.mHandler.obtainMessage();
        msg.what = 2001;
        msg.obj = Float.valueOf(temperature);
        this.mHandler.removeMessages(2001);
        this.mHandler.sendMessageDelayed(msg, 10L);
    }
}
