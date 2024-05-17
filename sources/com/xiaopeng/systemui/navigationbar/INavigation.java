package com.xiaopeng.systemui.navigationbar;

import android.content.ComponentName;
import android.content.res.Configuration;
import android.view.MotionEvent;
import com.xiaopeng.systemui.viewmodel.car.CarViewModel;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
/* loaded from: classes24.dex */
public interface INavigation {
    default void setNavigationBar(NavigationBar navigationBar) {
    }

    default void initNavigationBar() {
    }

    default void onActivityChanged(ComponentName cn, boolean isAppListOpened, int appListSharedId) {
    }

    default void setCarViewModel(CarViewModel model) {
    }

    default void setHvacViewModel(HvacViewModel model) {
    }

    default void onConfigurationChanged(Configuration newConfig) {
    }

    default void dispatchTouchEvent(MotionEvent ev) {
    }

    default void hideNavigationBar() {
    }

    default void showNavigationBar() {
    }
}
