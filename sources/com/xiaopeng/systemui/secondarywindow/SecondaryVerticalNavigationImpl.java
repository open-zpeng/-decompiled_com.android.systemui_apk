package com.xiaopeng.systemui.secondarywindow;

import android.content.Context;
import android.content.Intent;
import androidx.lifecycle.Observer;
import com.android.systemui.R;
import com.xiaopeng.lib.utils.ThreadUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.navigationbar.INavigationBarView;
import com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.car.BcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.IBcmViewModel;
/* loaded from: classes24.dex */
public class SecondaryVerticalNavigationImpl extends VerticalNavigationImpl {
    private static final String TAG = "SecondaryVerticalNavigationImpl";
    private BcmViewModel mBcmViewModel;
    private int mPsnSeatHeatLevel;
    private int mPsnSeatVentLevel;

    public SecondaryVerticalNavigationImpl(Context context) {
        super(context);
        this.mPsnSeatHeatLevel = 0;
        this.mPsnSeatVentLevel = 0;
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl, com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl, com.xiaopeng.systemui.navigationbar.INavigation
    public void initNavigationBar() {
        super.initNavigationBar();
        this.mBcmViewModel = (BcmViewModel) ViewModelManager.getInstance().getViewModel(IBcmViewModel.class, this.mContext);
        this.mBcmViewModel.getPsnSeatHeatLevel().observe(this, new Observer() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondaryVerticalNavigationImpl$tVsEPpCkECNBfH4paB1gXenqb64
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SecondaryVerticalNavigationImpl.this.lambda$initNavigationBar$0$SecondaryVerticalNavigationImpl((Integer) obj);
            }
        });
        this.mBcmViewModel.getPsnSeatVentLevel().observe(this, new Observer() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondaryVerticalNavigationImpl$N3cND7plG4ZG14ZYBKamXi-xWmg
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SecondaryVerticalNavigationImpl.this.lambda$initNavigationBar$1$SecondaryVerticalNavigationImpl((Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$initNavigationBar$0$SecondaryVerticalNavigationImpl(Integer value) {
        if (this.mPsnSeatHeatLevel != value.intValue()) {
            this.mPsnSeatHeatLevel = value.intValue();
            onPsnSeatHeatLevelChanged(value.intValue());
        }
    }

    public /* synthetic */ void lambda$initNavigationBar$1$SecondaryVerticalNavigationImpl(Integer value) {
        if (this.mPsnSeatVentLevel != value.intValue()) {
            this.mPsnSeatVentLevel = value.intValue();
            onPsnSeatVentLevelChanged(value.intValue());
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl
    public int getDisplayId() {
        return 1;
    }

    private void onPsnSeatHeatLevelChanged(int heatLevel) {
        this.mNavigationBarView.setPsnSeatHeatLevel(heatLevel);
    }

    private void onPsnSeatVentLevelChanged(int ventLevel) {
        this.mNavigationBarView.setPsnSeatVentLevel(ventLevel);
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl, com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl
    protected INavigationBarView createNavigationBarView() {
        return ViewFactory.getSecondaryVerticalNavigationBarView();
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl
    protected float getQuickTemperature() {
        return this.mHvacViewModel.getHvacPassengerTemperature();
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl, com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl, com.xiaopeng.systemui.navigationbar.NavigationHvacHandler.OnHvacListener
    public void onHvacItemChanged(int type, Object value) {
        if (type == 2106) {
            setQuickTemperature(value);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl
    protected void registerNavigationBarPresenter() {
        PresenterCenter.getInstance().addNavigationBarPresenter(1, this);
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl, com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationButtonClicked(int btnIndex) {
        if (btnIndex == 0) {
            onNavBarAppListClicked();
        } else if (btnIndex == 1) {
            onNavBarSeatHeatVentClicked();
        } else if (btnIndex == 2) {
            onNavBarChildSafeModeClicked();
        } else if (btnIndex == 3) {
            onNavBarSeatMassageClicked();
        }
    }

    private void onNavBarChildSafeModeClicked() {
    }

    private void onNavBarAppListClicked() {
        Logger.d(TAG, "onNavBarAppListClicked");
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.secondarywindow.-$$Lambda$SecondaryVerticalNavigationImpl$0b1W5DYPAuiHvIzjvLWXL7cvdL4
            @Override // java.lang.Runnable
            public final void run() {
                SecondaryVerticalNavigationImpl.this.lambda$onNavBarAppListClicked$2$SecondaryVerticalNavigationImpl();
            }
        });
    }

    public /* synthetic */ void lambda$onNavBarAppListClicked$2$SecondaryVerticalNavigationImpl() {
        PackageHelper.startActivityInSecondaryWindow(this.mContext, (int) R.string.component_app);
    }

    private void onNavBarSeatAdjustmentClicked() {
        Logger.d(TAG, "onNavBarSeatAdjustmentClicked");
        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.carcontrol.intent.action.ACTION_SHOW_SEAT_CONTROL_PANEL");
        intent.setFlags(16777216);
        try {
            this.mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Logger.d(TAG, "send broadcast failed");
        }
    }

    private void onNavBarSeatMassageClicked() {
        Logger.d(TAG, "onNavBarSeatMassageClicked");
        PackageHelper.startSeatMassage(this.mContext, 1);
    }

    private void onNavBarSeatVentClicked() {
        int i = this.mPsnSeatVentLevel;
        int nextLevel = i + (-1) >= 0 ? i - 1 : 3;
        Logger.d(TAG, "onNavBarSeatVentClicked : " + nextLevel);
        this.mBcmViewModel.setPsnSeatVentLevel(nextLevel);
    }

    private void onNavBarSeatHeatClicked() {
        int i = this.mPsnSeatHeatLevel;
        int nextLevel = i + (-1) >= 0 ? i - 1 : 3;
        Logger.d(TAG, "onNavBarSeatHeatClicked : " + nextLevel);
        this.mBcmViewModel.setPsnSeatHeatLevel(nextLevel);
    }

    private void onNavBarSeatHeatVentClicked() {
    }
}
