package com.xiaopeng.systemui.navigationbar;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.controller.ItemController;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.systemui.ui.widget.AnimatedProgressBar;
import com.xiaopeng.systemui.ui.widget.AnimatedTextView;
import com.xiaopeng.systemui.ui.widget.HvacComboView;
import com.xiaopeng.systemui.ui.widget.NavigationItemView;
import com.xiaopeng.systemui.ui.widget.TemperatureTextView;
import com.xiaopeng.systemui.ui.window.NavigationBarWindow;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public class PlatformNavigationBar2DView extends AbstractNavigationBar2DView {
    private static final String TAG = "PlatformNavigationBar2D";
    private AnimatedImageView mHvacAuto;
    private AnimatedTextView mHvacQuality;
    private AnimatedImageView mHvacQualityBg;
    private AnimatedImageView mHvacSwitch;
    private RelativeLayout mHvacViewContainer;
    private AnimatedImageView mHvacWind;
    private ItemController.ItemInfo mLeftItemInfo1;
    private ItemController.ItemInfo mLeftItemInfo2;
    private ItemController.ItemInfo mRightItemInfo1;
    private ItemController.ItemInfo mRightItemInfo2;

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected void findView() {
        super.findView();
        this.mHvacAuto = (AnimatedImageView) this.mNavigationBarView.findViewById(R.id.hvac_auto);
        this.mHvacQuality = (AnimatedTextView) this.mNavigationBarView.findViewById(R.id.hvac_quality);
        this.mHvacQualityBg = (AnimatedImageView) this.mNavigationBarView.findViewById(R.id.hvac_quality_bg);
        this.mHvacViewContainer = (RelativeLayout) this.mNavigationBarView.findViewById(R.id.hvac_container);
        this.mHvacSwitch = (AnimatedImageView) this.mNavigationBarView.findViewById(R.id.hvac_switch);
        this.mHvacWind = (AnimatedImageView) this.mNavigationBarView.findViewById(R.id.hvac_wind);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected void initView() {
        super.initView();
        if (!CarModelsManager.getConfig().isAirCleanSupport()) {
            this.mHvacQualityBg.setVisibility(8);
            this.mHvacQuality.setVisibility(8);
        }
        this.mHvacSwitch.setOnClickListener(this);
        this.mHvacSwitch.setOnLongClickListener(this);
        this.mHvacSwitch.setOnTouchListener(this);
        if (CarModelsManager.getFeature().getWindLevel() == 5) {
            this.mHvacWind.setImageResource(R.drawable.ic_navbar_hvac_wind_5_level);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected View getBackDefrostView() {
        return this.mNavigationBarView.findViewById(R.id.right2);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected View getFrontDefrostView() {
        return this.mNavigationBarView.findViewById(R.id.right1);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedTextView getDriverSyncView() {
        return (AnimatedTextView) this.mNavigationBarView.findViewById(R.id.havc_synchronized);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedTextView getPassengerSyncView() {
        return null;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedProgressBar getDriverProgressBar() {
        return (AnimatedProgressBar) this.mNavigationBarView.findViewById(R.id.hvac_driver_progress_bar);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected AnimatedProgressBar getPassengerProgressBar() {
        return (AnimatedProgressBar) this.mNavigationBarView.findViewById(R.id.hvac_passenger_progress_bar);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected TemperatureTextView getDriverTemperature() {
        return (TemperatureTextView) this.mNavigationBarView.findViewById(R.id.driver_quick_temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected TemperatureTextView getPassengerTemperature() {
        return (TemperatureTextView) this.mNavigationBarView.findViewById(R.id.passenger_quick_temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected HvacComboView getDriverComboView() {
        return (HvacComboView) this.mNavigationBarView.findViewById(R.id.driver_hvac_combo);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected HvacComboView getPassengerComboView() {
        return (HvacComboView) this.mNavigationBarView.findViewById(R.id.passenger_hvac_combo);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected NavigationBarWindow inflateNavigation() {
        return (NavigationBarWindow) View.inflate(this.mContext, R.layout.navigation_bar_platform, null);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, android.view.View.OnClickListener
    public void onClick(View view) {
        if (Utils.isFastClick()) {
            Logger.d(TAG, "isFastClick");
            return;
        }
        switch (view.getId()) {
            case R.id.havc_synchronized /* 2131362359 */:
                this.mNavigationBarPresenter.onHvacSynchronizedClicked();
                return;
            case R.id.hvac_switch /* 2131362392 */:
                this.mNavigationBarPresenter.onDockHvacClicked();
                return;
            case R.id.left1 /* 2131362607 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(2);
                return;
            case R.id.left2 /* 2131362608 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(1);
                return;
            case R.id.right1 /* 2131362985 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(6);
                return;
            case R.id.right2 /* 2131362986 */:
                this.mNavigationBarPresenter.onNavigationButtonClicked(7);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.hvac_switch) {
            this.mNavigationBarPresenter.onDockHvacLongClicked();
            return true;
        }
        return true;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 1) {
            return false;
        }
        int id = view.getId();
        if (id == R.id.driver_hvac_combo || id == R.id.hvac_switch || id == R.id.passenger_hvac_combo) {
            BIHelper.sendBIData(BIHelper.ID.air_conditioning, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setHvacInfo(HvacInfo hvacInfo) {
        super.setHvacInfo(hvacInfo);
        boolean showAuto = hvacInfo.isAuto;
        boolean powerOn = hvacInfo.isPowerOn;
        int wind = hvacInfo.windLevel;
        this.mHvacAuto.setVisibility(showAuto ? 0 : 8);
        this.mHvacViewContainer.setAlpha(powerOn ? 1.0f : 0.56f);
        this.mHvacSwitch.setImageLevel(powerOn ? 1 : 0);
        this.mHvacWind.setImageLevel(powerOn ? wind : 0);
        this.mHvacWind.setVisibility(showAuto ? 8 : 0);
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView, com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setInnerQuality(int innerQuality, String qualityContent) {
        this.mHvacQuality.setText(qualityContent);
        if (innerQuality == 1023) {
            this.mHvacQualityBg.setVisibility(8);
            return;
        }
        this.mHvacQualityBg.setVisibility(0);
        this.mHvacQualityBg.setImageResource(R.drawable.bg_navbar_hvac_quality);
        this.mHvacQualityBg.setImageLevel(CarController.getInnerQualityLevel(innerQuality));
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setAutoDefog(boolean autoDefog) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatHeatLevel(int heatLevel) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPsnSeatVentLevel(int ventLevel) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView
    protected void initItemViews() {
        if (this.mNavigationBarView != null) {
            NavigationItemView left2 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.left2);
            NavigationItemView left1 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.left1);
            NavigationItemView right1 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.right1);
            NavigationItemView right2 = (NavigationItemView) this.mNavigationBarView.findViewById(R.id.right2);
            ItemController.NavigationItemFactory navigationItemFactory = ItemController.NavigationItemFactory.getInstance();
            this.mLeftItemInfo1 = navigationItemFactory.getLeftItemInfo(1);
            this.mLeftItemInfo2 = navigationItemFactory.getLeftItemInfo(2);
            this.mRightItemInfo1 = navigationItemFactory.getRightItemInfo(1);
            this.mRightItemInfo2 = navigationItemFactory.getRightItemInfo(2);
            left2.setTag(this.mContext.getString(this.mLeftItemInfo2.tagId));
            left1.setTag(this.mContext.getString(this.mLeftItemInfo1.tagId));
            this.mItemViews.add(left2);
            left2.setOnClickListener(this);
            left1.setOnClickListener(this);
            this.mItemViews.add(left1);
            right1.setOnClickListener(this);
            right2.setOnClickListener(this);
            left2.setImageResource(this.mLeftItemInfo2.resId);
            left1.setImageResource(this.mLeftItemInfo1.resId);
            right1.setImageResource(this.mRightItemInfo1.resId);
            right2.setImageResource(this.mRightItemInfo2.resId);
        }
    }
}
