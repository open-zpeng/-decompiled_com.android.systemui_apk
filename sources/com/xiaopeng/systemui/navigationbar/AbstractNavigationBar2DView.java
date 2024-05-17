package com.xiaopeng.systemui.navigationbar;

import android.content.Context;
import android.os.SystemProperties;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.controller.DropmenuController;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import com.xiaopeng.systemui.ui.widget.AirPurgeView;
import com.xiaopeng.systemui.ui.widget.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.systemui.ui.widget.AnimatedProgressBar;
import com.xiaopeng.systemui.ui.widget.AnimatedTextView;
import com.xiaopeng.systemui.ui.widget.HvacComboView;
import com.xiaopeng.systemui.ui.widget.TemperatureTextView;
import com.xiaopeng.systemui.ui.window.NavigationBarWindow;
import java.util.ArrayList;
/* loaded from: classes24.dex */
public abstract class AbstractNavigationBar2DView implements INavigationBarView, NavigationBarWindow.OnViewListener, View.OnClickListener, AnimatedProgressBar.OnProgressListener, View.OnLongClickListener, View.OnTouchListener {
    private static final int DEFAULT_SCROLL_Y_DIST_TO_SHOW_HVAC_DASHBOARD = 56;
    private static final String KEY_PROP_SCROLL_Y_DIST_TO_SHOW_HVAC_DASHBOARD = "persist.hvac.dashboard.show.scroll.ydist";
    private static final int PROGRESS_SCALE = 10;
    private static final String TAG = "BaseNavigationBar2DView";
    private static final long VIEW_ANIMATION_INTERVAL = 200;
    protected AnimatedImageView mActionPanel;
    protected AirPurgeView mAirPurgeView;
    protected View mBackDefrostView;
    protected HvacComboView mDriverComboView;
    protected AnimatedProgressBar mDriverProgressBar;
    protected AnimatedTextView mDriverSyncView;
    protected TemperatureTextView mDriverTemperature;
    protected View mFrontDefrostView;
    protected AnimatedTextView mHvacQuality;
    protected AnimatedImageView mHvacSwitcher;
    protected FrameLayout mNavigationBarView;
    protected NavigationBarWindow mNavigationBarWindow;
    protected AlphaOptimizedRelativeLayout mNavigationPanel;
    protected HvacComboView mPassengerComboView;
    protected AnimatedProgressBar mPassengerProgressBar;
    protected AnimatedTextView mPassengerSyncView;
    protected TemperatureTextView mPassengerTemperature;
    protected AlphaOptimizedRelativeLayout mQuickPanel;
    protected ArrayList<View> mItemViews = new ArrayList<>();
    private HvacComboListener mDriverHvacListener = new HvacComboListener(1);
    private HvacComboListener mPassengerHvacListener = new HvacComboListener(2);
    protected Context mContext = ContextUtils.getContext();
    protected WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();
    private float mScrollYDistanceToShowHvacDashboard = SystemProperties.getInt(KEY_PROP_SCROLL_Y_DIST_TO_SHOW_HVAC_DASHBOARD, 56);
    protected INavigationBarPresenter mNavigationBarPresenter = getNavigationBarPresenter();

    protected abstract View getBackDefrostView();

    protected abstract HvacComboView getDriverComboView();

    protected abstract AnimatedProgressBar getDriverProgressBar();

    protected abstract AnimatedTextView getDriverSyncView();

    protected abstract TemperatureTextView getDriverTemperature();

    protected abstract View getFrontDefrostView();

    protected abstract HvacComboView getPassengerComboView();

    protected abstract AnimatedProgressBar getPassengerProgressBar();

    protected abstract AnimatedTextView getPassengerSyncView();

    protected abstract TemperatureTextView getPassengerTemperature();

    protected abstract NavigationBarWindow inflateNavigation();

    protected abstract void initItemViews();

    public AbstractNavigationBar2DView() {
        findView();
        initView();
        initItemViews();
    }

    protected INavigationBarPresenter getNavigationBarPresenter() {
        return PresenterCenter.getInstance().getNavigationBarPresenter(0);
    }

    protected void addNavigationBar() {
        WindowHelper.addNavigationBar(this.mWindowManager, this.mNavigationBarWindow, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void findView() {
        this.mNavigationBarWindow = inflateNavigation();
        addNavigationBar();
        this.mNavigationBarWindow.addListener(this);
        this.mNavigationBarView = (FrameLayout) this.mNavigationBarWindow.findViewById(R.id.navigation_bar);
        this.mQuickPanel = (AlphaOptimizedRelativeLayout) this.mNavigationBarView.findViewById(R.id.quick_panel_container);
        this.mActionPanel = (AnimatedImageView) this.mNavigationBarView.findViewById(R.id.action);
        AnimatedImageView animatedImageView = this.mActionPanel;
        if (animatedImageView != null) {
            animatedImageView.setOnClickListener(this);
        }
        this.mNavigationPanel = (AlphaOptimizedRelativeLayout) this.mNavigationBarView.findViewById(R.id.navigation_panel_container);
        this.mDriverProgressBar = getDriverProgressBar();
        this.mPassengerProgressBar = getPassengerProgressBar();
        this.mDriverTemperature = getDriverTemperature();
        this.mPassengerTemperature = getPassengerTemperature();
        this.mDriverComboView = getDriverComboView();
        HvacComboView hvacComboView = this.mDriverComboView;
        if (hvacComboView != null) {
            hvacComboView.setOnTouchListener(this);
        }
        this.mPassengerComboView = getPassengerComboView();
        HvacComboView hvacComboView2 = this.mPassengerComboView;
        if (hvacComboView2 != null) {
            hvacComboView2.setOnTouchListener(this);
        }
        this.mDriverSyncView = getDriverSyncView();
        this.mPassengerSyncView = getPassengerSyncView();
        this.mHvacSwitcher = (AnimatedImageView) this.mNavigationBarView.findViewById(R.id.hvac_switch);
        AnimatedImageView animatedImageView2 = this.mHvacSwitcher;
        if (animatedImageView2 != null) {
            animatedImageView2.setOnClickListener(this);
            this.mHvacSwitcher.setOnLongClickListener(this);
            this.mHvacSwitcher.setOnTouchListener(this);
        }
        this.mBackDefrostView = getBackDefrostView();
        this.mFrontDefrostView = getFrontDefrostView();
        this.mHvacQuality = (AnimatedTextView) this.mNavigationBarView.findViewById(R.id.hvac_combo_quality);
        HvacComboView hvacComboView3 = this.mDriverComboView;
        if (hvacComboView3 != null) {
            this.mAirPurgeView = (AirPurgeView) hvacComboView3.findViewById(R.id.hvac_air_purge);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
    }

    @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
    public void onProgressChanged(AnimatedProgressBar progressBar, int progress, boolean fromUser) {
        int type = getProgressBarType(progressBar);
        Logger.e(TAG, "onProgressChanged progress=" + progress + " fromUser=" + fromUser);
        float temperature = NavigationHvacHandler.getTemperatureByProgress(progress);
        this.mNavigationBarPresenter.onTemperatureProgressChanged(type, temperature, fromUser);
    }

    @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
    public void onStartTrackingTouch(AnimatedProgressBar progressBar) {
    }

    @Override // com.xiaopeng.systemui.ui.widget.AnimatedProgressBar.OnProgressListener
    public void onStopTrackingTouch(AnimatedProgressBar progressBar) {
        if (progressBar != null) {
            int progress = progressBar.getProgress();
            int type = getProgressBarType(progressBar);
            float temperature = getTemperatureByProgress(progress);
            setTemperature(type, temperature);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setHvacInfo(HvacInfo hvacInfo) {
        HvacComboView hvacComboView = this.mDriverComboView;
        if (hvacComboView != null) {
            hvacComboView.setAuto(hvacInfo.isAuto);
            this.mDriverComboView.setPower(hvacInfo.isPowerOn);
            this.mDriverComboView.setWindLevel(hvacInfo.windLevel);
            this.mDriverComboView.setWindColor(hvacInfo.windColor);
        }
        HvacComboView hvacComboView2 = this.mPassengerComboView;
        if (hvacComboView2 != null) {
            hvacComboView2.setAuto(hvacInfo.isAuto);
            this.mPassengerComboView.setPower(hvacInfo.isPowerOn);
            this.mPassengerComboView.setWindLevel(hvacInfo.windLevel);
            this.mPassengerComboView.setWindColor(hvacInfo.windColor);
        }
        AnimatedTextView animatedTextView = this.mDriverSyncView;
        if (animatedTextView != null) {
            animatedTextView.setSelected(hvacInfo.isDriverSync);
        }
        AnimatedTextView animatedTextView2 = this.mPassengerSyncView;
        if (animatedTextView2 != null) {
            animatedTextView2.setSelected(hvacInfo.isPassengerSync);
        }
        View view = this.mBackDefrostView;
        if (view != null) {
            view.setSelected(hvacInfo.isBackDefrostOn);
        }
        View view2 = this.mFrontDefrostView;
        if (view2 != null) {
            view2.setSelected(hvacInfo.isFrontDefrostOn);
        }
        AnimatedProgressBar animatedProgressBar = this.mDriverProgressBar;
        if (animatedProgressBar != null) {
            animatedProgressBar.setActivated(hvacInfo.isPowerOn);
        }
        AnimatedProgressBar animatedProgressBar2 = this.mPassengerProgressBar;
        if (animatedProgressBar2 != null) {
            animatedProgressBar2.setActivated(hvacInfo.isPowerOn);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setInnerQuality(int quality, String qualityContent) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPurgeMode(boolean purgeMode, boolean isAuto, int quality) {
    }

    private int getProgressBarType(AnimatedProgressBar progressBar) {
        int type = -1;
        if (progressBar == this.mDriverProgressBar) {
            type = 1;
        }
        if (progressBar == this.mPassengerProgressBar) {
            return 2;
        }
        return type;
    }

    private float getTemperatureByProgress(int progress) {
        if (progress > 640 || progress < 360) {
            return 18.0f;
        }
        float temperature = (progress / 10) * 0.5f;
        if (progress % 10 > 0 && temperature == 18.0f) {
            return 18.5f;
        }
        return temperature;
    }

    private void setTemperature(int type, float temperature) {
        Logger.d(TAG, "setTemperature type=" + type + " temperature=" + temperature);
        this.mNavigationBarPresenter.setTemperature(type, temperature);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        return false;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class HvacComboListener implements HvacComboView.OnGestureListener {
        private int mType;

        public HvacComboListener(int type) {
            this.mType = type;
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public void onArrowDownClicked() {
            Logger.d(AbstractNavigationBar2DView.TAG, "onArrowDownClicked type=" + this.mType);
            AbstractNavigationBar2DView.this.mNavigationBarPresenter.onTemperatureDownClicked(this.mType);
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public void onArrowUpClicked() {
            Logger.d(AbstractNavigationBar2DView.TAG, "onArrowUpClicked");
            AbstractNavigationBar2DView.this.mNavigationBarPresenter.onTemperatureUpClicked(this.mType);
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public boolean onSingleTapConfirmed(MotionEvent e) {
            AbstractNavigationBar2DView.this.mNavigationBarPresenter.onHvacComboClicked();
            return false;
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public void onScrollChanged(boolean isStarted) {
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public boolean onTouchEvent(MotionEvent event) {
            Logger.d(AbstractNavigationBar2DView.TAG, "onTouchEvent event2=" + event);
            if (!ActivityController.isHvacPanelFocused()) {
                if (AbstractNavigationBar2DView.this.mDriverProgressBar != null) {
                    AbstractNavigationBar2DView.this.mDriverProgressBar.dispatchTouchEvent(event);
                }
                if (AbstractNavigationBar2DView.this.mPassengerProgressBar != null) {
                    AbstractNavigationBar2DView.this.mPassengerProgressBar.dispatchTouchEvent(event);
                    return false;
                }
                return false;
            }
            return false;
        }

        @Override // com.xiaopeng.systemui.ui.widget.HvacComboView.OnGestureListener
        public void dispatchTouchEvent(MotionEvent event) {
            Logger.d(AbstractNavigationBar2DView.TAG, "dispatchTouchEvent event2=" + event);
        }
    }

    /* loaded from: classes24.dex */
    protected static class NavigationAnimationHelper {
        protected NavigationAnimationHelper() {
        }

        public static void executeVisibilityAnimation(final View view, long durationMillis, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, float fromAlpha, final float toAlpha) {
            if (view != null) {
                TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
                AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
                final AnimationSet animationSet = new AnimationSet(true);
                animationSet.setFillAfter(true);
                animationSet.setDuration(durationMillis);
                animationSet.addAnimation(translateAnimation);
                animationSet.addAnimation(alphaAnimation);
                view.post(new Runnable() { // from class: com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView.NavigationAnimationHelper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        view.startAnimation(animationSet);
                    }
                });
                view.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.navigationbar.AbstractNavigationBar2DView.NavigationAnimationHelper.2
                    @Override // java.lang.Runnable
                    public void run() {
                        view.clearAnimation();
                        view.setVisibility(toAlpha == 1.0f ? 0 : 8);
                    }
                }, durationMillis);
            }
        }
    }

    @Override // com.xiaopeng.systemui.ui.window.NavigationBarWindow.OnViewListener
    public void onAttachedToWindow() {
    }

    @Override // com.xiaopeng.systemui.ui.window.NavigationBarWindow.OnViewListener
    public void onFinishInflate() {
    }

    @Override // com.xiaopeng.systemui.ui.window.NavigationBarWindow.OnViewListener
    public void dispatchTouchEvent(MotionEvent ev) {
        onNavigationTouchEvent(ev);
        int action = ev.getAction();
        if (action == 0 || action == 1 || action == 3) {
            DropmenuController.getInstance(this.mContext).onOutsideTouched(ev);
        }
    }

    protected void onNavigationTouchEvent(MotionEvent ev) {
        float rawX = ev.getRawX();
        if (rawX > 130.0f && rawX < 4670.0f) {
            return;
        }
        AlphaOptimizedRelativeLayout alphaOptimizedRelativeLayout = this.mNavigationPanel;
        boolean isVisibleToUser = alphaOptimizedRelativeLayout != null ? alphaOptimizedRelativeLayout.isVisibleToUser() : false;
        Logger.d(TAG, "onNavigationTouchEvent isVisibleToUser=" + isVisibleToUser + " ev=" + ev);
        int action = ev.getAction();
        if (action == 0) {
            this.mNavigationBarPresenter.onNavigationTouchDown();
        } else if (action == 1) {
            this.mNavigationBarPresenter.onNavigationTouchUp();
        } else if (action == 2) {
            this.mNavigationBarPresenter.onNavigationTouchMove();
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void onActivityChanged(String packageName, String className, boolean isCarControlReady, boolean isAppListOpened, int appListSharedId) {
        ActivityController.onNavigationItemChanged(packageName, className, this.mItemViews, isCarControlReady);
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setQuickTemperature(float temperature) {
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public synchronized void switchHvacDashboard(boolean showNavigation, boolean showQuick) {
        long durationMillis;
        boolean quickVisible = this.mQuickPanel.isVisibleToUser();
        boolean actionVisible = this.mActionPanel.isVisibleToUser();
        boolean navigationVisible = this.mNavigationPanel.isVisibleToUser();
        boolean showAction = false;
        boolean showQuick2 = showNavigation ? false : showQuick;
        if (!showNavigation) {
            showAction = true;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("handleDashboard");
        buffer.append(" showQuick=" + showQuick2);
        buffer.append(" showAction=" + showAction);
        buffer.append(" showNavigation=" + showNavigation);
        buffer.append(" quickVisible=" + quickVisible);
        buffer.append(" actionVisible=" + actionVisible);
        buffer.append(" navigationVisible=" + navigationVisible);
        buffer.append(" durationMillis=200");
        Logger.d(TAG, buffer.toString());
        if (quickVisible == showQuick2) {
            durationMillis = 200;
        } else {
            float fromAlpha = showQuick2 ? 0.0f : 1.0f;
            float toAlpha = showQuick2 ? 1.0f : 0.0f;
            durationMillis = 200;
            NavigationAnimationHelper.executeVisibilityAnimation(this.mQuickPanel, 200L, 0.0f, 0.0f, 0.0f, 0.0f, fromAlpha, toAlpha);
        }
        if (actionVisible != showAction) {
            float fromAlpha2 = showAction ? 0.0f : 1.0f;
            float toAlpha2 = showAction ? 1.0f : 0.0f;
            NavigationAnimationHelper.executeVisibilityAnimation(this.mActionPanel, durationMillis, 0.0f, 0.0f, 0.0f, 0.0f, fromAlpha2, toAlpha2);
        }
        if (navigationVisible != showNavigation) {
            float fromAlpha3 = showNavigation ? 0.0f : 1.0f;
            float toAlpha3 = showNavigation ? 1.0f : 0.0f;
            NavigationAnimationHelper.executeVisibilityAnimation(this.mNavigationPanel, durationMillis, 0.0f, 0.0f, 0.0f, 0.0f, fromAlpha3, toAlpha3);
        }
        if (showQuick2) {
            this.mNavigationBarView.setOnClickListener(this);
        } else {
            this.mNavigationBarView.setOnClickListener(null);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void onHvacPanelChanged(boolean focused) {
        AnimatedImageView animatedImageView = this.mHvacSwitcher;
        if (animatedImageView != null) {
            animatedImageView.setSelected(focused);
        }
        HvacComboView hvacComboView = this.mDriverComboView;
        if (hvacComboView != null) {
            hvacComboView.setSelected(focused);
        }
        HvacComboView hvacComboView2 = this.mPassengerComboView;
        if (hvacComboView2 != null) {
            hvacComboView2.setSelected(focused);
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setDriverTemperature(float temperature) {
        HvacComboView hvacComboView = this.mDriverComboView;
        if (hvacComboView != null) {
            hvacComboView.setText(temperature);
        }
        AnimatedProgressBar animatedProgressBar = this.mDriverProgressBar;
        if (animatedProgressBar != null) {
            animatedProgressBar.setProgress(NavigationHvacHandler.getProgressByTemperature(temperature));
        }
    }

    @Override // com.xiaopeng.systemui.navigationbar.INavigationBarView
    public void setPassengerTemperature(float temperature) {
        HvacComboView hvacComboView = this.mPassengerComboView;
        if (hvacComboView != null) {
            hvacComboView.setText(temperature);
        }
        AnimatedProgressBar animatedProgressBar = this.mPassengerProgressBar;
        if (animatedProgressBar != null) {
            animatedProgressBar.setProgress(NavigationHvacHandler.getProgressByTemperature(temperature));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initView() {
        View view = this.mBackDefrostView;
        if (view != null) {
            view.setOnClickListener(this);
        }
        View view2 = this.mFrontDefrostView;
        if (view2 != null) {
            view2.setOnClickListener(this);
        }
        AnimatedTextView animatedTextView = this.mDriverSyncView;
        if (animatedTextView != null) {
            animatedTextView.setOnClickListener(this);
        }
        AnimatedTextView animatedTextView2 = this.mPassengerSyncView;
        if (animatedTextView2 != null) {
            animatedTextView2.setOnClickListener(this);
        }
        HvacComboView hvacComboView = this.mDriverComboView;
        if (hvacComboView != null) {
            hvacComboView.setListener(this.mDriverHvacListener);
            this.mDriverComboView.setScrollYDistanceToShowHvacDashboard(this.mScrollYDistanceToShowHvacDashboard);
        }
        HvacComboView hvacComboView2 = this.mPassengerComboView;
        if (hvacComboView2 != null) {
            hvacComboView2.setListener(this.mPassengerHvacListener);
        }
        AnimatedProgressBar animatedProgressBar = this.mDriverProgressBar;
        if (animatedProgressBar != null) {
            animatedProgressBar.setProgressListener(this);
        }
        AnimatedProgressBar animatedProgressBar2 = this.mPassengerProgressBar;
        if (animatedProgressBar2 != null) {
            animatedProgressBar2.setProgressListener(this);
        }
    }
}
