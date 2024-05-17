package com.xiaopeng.systemui.navigationbar.impl;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.android.systemui.R;
import com.xiaopeng.lib.utils.ThreadUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.controller.ItemController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.navigationbar.HvacInfo;
import com.xiaopeng.systemui.navigationbar.INavigationBarView;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
/* loaded from: classes24.dex */
public class VerticalNavigationImpl extends AbstractNavigationImpl implements LifecycleOwner {
    private static final String TAG = "VerticalNavigationImpl";
    private LifecycleRegistry mLifecycleRegistry;

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl
    protected INavigationBarView createNavigationBarView() {
        return ViewFactory.getVerticalNavigationBarView();
    }

    public VerticalNavigationImpl(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateCarControlButtonStatus */
    public void lambda$initNavigationBar$0$VerticalNavigationImpl(Boolean ready) {
        if (CarModelsManager.getFeature().isCarControlPreloadSupport()) {
            Logger.d(TAG, "updateCarControlButtonStatus : ready = " + ready);
            ActivityController.setCarControlReady(ready.booleanValue());
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl, com.xiaopeng.systemui.navigationbar.INavigation
    public void initNavigationBar() {
        Logger.d(TAG, "NavigationBar init");
        super.initNavigationBar();
        StatusBarGlobal.getInstance(this.mContext).setNavigation(this);
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
        boolean isCarControlReady = this.mCarViewModel.isCarControlLoadReady();
        Logger.d(TAG, "initNavigationBar : isCarControlReady = " + isCarControlReady);
        lambda$initNavigationBar$0$VerticalNavigationImpl(Boolean.valueOf(isCarControlReady));
        setQuickTemperature(null);
        this.mCarViewModel.getCarControlReadyData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.impl.-$$Lambda$VerticalNavigationImpl$iE2QgpcG9Slyx-S7HxO3-daaoKI
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                VerticalNavigationImpl.this.lambda$initNavigationBar$0$VerticalNavigationImpl((Boolean) obj);
            }
        });
    }

    protected float getQuickTemperature() {
        return this.mHvacViewModel.getHvacDriverTemperature();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setQuickTemperature(Object value) {
        float temperature = getQuickTemperature();
        if (value != null) {
            try {
                temperature = Float.parseFloat(value.toString());
            } catch (Exception e) {
            }
        }
        Logger.d(TAG, "setQuickTemperature : " + temperature);
        this.mNavigationBarView.setQuickTemperature(temperature);
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    private void onNavigationAppListClicked() {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.navigationbar.impl.-$$Lambda$VerticalNavigationImpl$OyXVvksdWvpBG1LogTC5MHvuP94
            @Override // java.lang.Runnable
            public final void run() {
                VerticalNavigationImpl.this.lambda$onNavigationAppListClicked$1$VerticalNavigationImpl();
            }
        });
    }

    public /* synthetic */ void lambda$onNavigationAppListClicked$1$VerticalNavigationImpl() {
        PackageHelper.startActivityInPrimaryWindow(this.mContext, R.string.component_app, null);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public void onNavigationButtonClicked(int btnIndex) {
        if (btnIndex == 0) {
            onNavigationAppListClicked();
        } else if (btnIndex == 1) {
            ItemController.VerticalNavigationItem.sTop2.onClicked(this.mContext);
        } else if (btnIndex == 2) {
            ItemController.VerticalNavigationItem.sTop1.onClicked(this.mContext);
        } else if (btnIndex == 3) {
            ItemController.VerticalNavigationItem.sBottom1.onClicked(this.mContext);
        } else if (btnIndex == 4) {
            ItemController.VerticalNavigationItem.sBottom2.onClicked(this.mContext);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarPresenter
    public String getHvacInfo() {
        HvacInfo hvacInfo = this.mHvacHandler.getHvacInfo();
        Logger.d(TAG, "getHvacInfo : " + GsonUtil.toJson(hvacInfo).toString());
        return GsonUtil.toJson(hvacInfo);
    }

    @Override // com.xiaopeng.systemui.navigationbar.impl.AbstractNavigationImpl, com.xiaopeng.systemui.navigationbar.NavigationHvacHandler.OnHvacListener
    public void onHvacItemChanged(int type, Object value) {
        Logger.d(TAG, "onHvacItemChanged : " + type);
        if (type == 2104) {
            setQuickTemperature(value);
        } else if (type == 2111) {
            this.mNavigationBarView.setInnerQuality(Integer.valueOf(value.toString()).intValue(), value.toString());
        } else if (type == 2115) {
            this.mNavigationBarView.setPurgeMode(((Boolean) value).booleanValue(), this.mHvacViewModel.isHvacAuto(), this.mHvacViewModel.getHvacQualityInner());
        } else if (type == 2116) {
            this.mNavigationBarView.setAutoDefog(((Boolean) value).booleanValue());
        }
    }
}
