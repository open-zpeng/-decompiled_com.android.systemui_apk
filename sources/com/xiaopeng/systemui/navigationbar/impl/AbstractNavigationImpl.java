package com.xiaopeng.systemui.navigationbar.impl;

import android.content.ComponentName;
import android.content.Context;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.controller.ItemController;
import com.xiaopeng.systemui.navigationbar.INavigation;
import com.xiaopeng.systemui.navigationbar.INavigationBarPresenter;
import com.xiaopeng.systemui.navigationbar.INavigationBarView;
import com.xiaopeng.systemui.navigationbar.NavigationBar;
import com.xiaopeng.systemui.navigationbar.NavigationHvacHandler;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.viewmodel.car.CarViewModel;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
/* loaded from: classes24.dex */
public abstract class AbstractNavigationImpl implements INavigation, INavigationBarPresenter, NavigationHvacHandler.OnHvacListener {
    private static final String TAG = "AbstractNavigationImpl";
    protected CarViewModel mCarViewModel;
    protected Context mContext;
    protected NavigationHvacHandler mHvacHandler;
    protected HvacViewModel mHvacViewModel;
    protected NavigationBar mNavigationBar;
    protected INavigationBarView mNavigationBarView;

    protected abstract INavigationBarView createNavigationBarView();

    public AbstractNavigationImpl(Context context) {
        this.mContext = context;
        registerNavigationBarPresenter();
        this.mNavigationBarView = createNavigationBarView();
    }

    protected void registerNavigationBarPresenter() {
        PresenterCenter.getInstance().addNavigationBarPresenter(0, this);
    }

    public int getDisplayId() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigation
    public void initNavigationBar() {
        this.mHvacHandler = new NavigationHvacHandler(this.mContext, this.mNavigationBar, this.mHvacViewModel);
        this.mHvacHandler.setNavigationBarView(this.mNavigationBarView);
        this.mHvacHandler.setListener(this);
        this.mHvacHandler.init();
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigation
    public void setNavigationBar(NavigationBar navigationBar) {
        this.mNavigationBar = navigationBar;
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigation
    public void setCarViewModel(CarViewModel model) {
        this.mCarViewModel = model;
        ItemController.setCarViewModel(model);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigation
    public void setHvacViewModel(HvacViewModel model) {
        this.mHvacViewModel = model;
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onDockHvacClicked() {
        NavigationHvacHandler navigationHvacHandler = this.mHvacHandler;
        if (navigationHvacHandler != null) {
            navigationHvacHandler.startCarHvac(getDisplayId());
        }
        BIHelper.sendBIData(BIHelper.ID.air_conditioning, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onDockHvacLongClicked() {
        this.mHvacHandler.setPower(!this.mHvacViewModel.isHvacPowerOn());
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationTouchMove() {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationTouchDown() {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationTouchUp() {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigation
    public void onActivityChanged(ComponentName cn, boolean isAppListOpened, int appListSharedId) {
        this.mNavigationBarView.onActivityChanged(cn.getPackageName(), cn.getClassName(), ActivityController.sIsCarControlReady, isAppListOpened, appListSharedId);
        onHvacPanelChanged(cn);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void setTemperature(int type, float temperature) {
        this.mHvacHandler.setTemperature(type, false, temperature);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onTemperatureDownClicked(int type) {
        this.mHvacHandler.setTemperature(type, true, -1.0f);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onTemperatureUpClicked(int type) {
        this.mHvacHandler.setTemperature(type, true, 1.0f);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onTemperatureProgressChanged(int type, float temperature, boolean fromUser) {
        this.mHvacHandler.onTemperatureProgressChanged(type, temperature, fromUser);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onHvacSynchronizedClicked() {
        this.mHvacHandler.setSynchronized(1, !this.mHvacViewModel.isHvacDriverSync());
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onHvacComboClicked() {
        this.mHvacHandler.onHvacComboSingleTapConfirmed(getDisplayId());
    }

    @Override // com.xiaopeng.systemui.navigationbar.NavigationHvacHandler.OnHvacListener
    public void onHvacItemChanged(int type, Object value) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationBarClicked() {
        showNavigationBar();
    }

    private void onHvacPanelChanged(ComponentName cn) {
        NavigationHvacHandler navigationHvacHandler;
        if (cn != null && (navigationHvacHandler = this.mHvacHandler) != null) {
            navigationHvacHandler.onActivityChanged(cn);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public float getTemperature(int type) {
        return this.mHvacHandler.getTemperature(type);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public int getInnerQuality() {
        return this.mHvacHandler.getInnerQuality();
    }
}
