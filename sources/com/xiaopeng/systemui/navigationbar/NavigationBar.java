package com.xiaopeng.systemui.navigationbar;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
import com.xiaopeng.systemui.navigationbar.impl.PlatformNavigationImpl;
import com.xiaopeng.systemui.navigationbar.impl.VerticalNavigationImpl;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.car.CarViewModel;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.ICarViewModel;
import com.xiaopeng.systemui.viewmodel.car.IHvacViewModel;
/* loaded from: classes24.dex */
public class NavigationBar implements LifecycleOwner, ActivityController.OnActivityCallback {
    private static final String TAG = "CarNavigationBar";
    private CarViewModel mCarViewModel;
    protected Context mContext;
    private HvacViewModel mHvacViewModel;
    private LifecycleRegistry mLifecycleRegistry;
    protected INavigation mNavigation;

    public NavigationBar(Context context) {
        this.mContext = context;
        createNavigation();
        initNavigationBar();
    }

    protected void createNavigation() {
        if (OrientationUtil.isLandscapeScreen(this.mContext)) {
            this.mNavigation = new VerticalNavigationImpl(this.mContext);
        } else {
            this.mNavigation = new PlatformNavigationImpl(this.mContext);
        }
    }

    @Override // androidx.lifecycle.LifecycleOwner
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    public void initNavigationBar() {
        Logger.d(TAG, "NavigationBar init");
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
        this.mCarViewModel = (CarViewModel) ViewModelManager.getInstance().getViewModel(ICarViewModel.class, this.mContext);
        this.mHvacViewModel = (HvacViewModel) ViewModelManager.getInstance().getViewModel(IHvacViewModel.class, this.mContext);
        this.mNavigation.setNavigationBar(this);
        this.mNavigation.setCarViewModel(this.mCarViewModel);
        this.mNavigation.setHvacViewModel(this.mHvacViewModel);
        this.mNavigation.initNavigationBar();
        ActivityController.getInstance(this.mContext).addActivityCallback(this);
    }

    @Override // com.xiaopeng.systemui.controller.ActivityController.OnActivityCallback
    public void onActivityChanged(ActivityController.ComponentInfo ci) {
        Logger.d(TAG, "onActivityChanged  ci = " + ci);
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            if (ci.getPrimaryTopPackage() != null && ci.getPrimaryTopPackage().equals(this.mContext.getString(R.string.pkg_app))) {
                this.mNavigation.onActivityChanged(ci.getName(), true, 0);
            } else {
                this.mNavigation.onActivityChanged(ci.getName(), false, 0);
            }
            if (ci.getSecondaryTopPackage() != null && ci.getSecondaryTopPackage().equals(this.mContext.getString(R.string.pkg_app))) {
                this.mNavigation.onActivityChanged(ci.getName(), true, 1);
            } else {
                this.mNavigation.onActivityChanged(ci.getName(), false, 1);
            }
        } else if (ci.isActivityChange()) {
            this.mNavigation.onActivityChanged(ci.getName(), true, 0);
        }
    }
}
