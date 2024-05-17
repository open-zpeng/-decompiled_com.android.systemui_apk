package com.android.systemui.recents;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.leak.RotationUtils;
import com.xiaopeng.systemui.helper.WindowHelper;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class ScreenPinningRequest implements View.OnClickListener, NavigationModeController.ModeChangedListener {
    private final AccessibilityManager mAccessibilityService;
    private final Context mContext;
    private RequestWindowView mRequestWindow;
    private final WindowManager mWindowManager;
    private int taskId;
    private final OverviewProxyService mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
    private int mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);

    public ScreenPinningRequest(Context context) {
        this.mContext = context;
        this.mAccessibilityService = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
    }

    public void clearPrompt() {
        RequestWindowView requestWindowView = this.mRequestWindow;
        if (requestWindowView != null) {
            this.mWindowManager.removeView(requestWindowView);
            this.mRequestWindow = null;
        }
    }

    public void showPrompt(int taskId, boolean allowCancel) {
        try {
            clearPrompt();
        } catch (IllegalArgumentException e) {
        }
        this.taskId = taskId;
        this.mRequestWindow = new RequestWindowView(this.mContext, allowCancel);
        this.mRequestWindow.setSystemUiVisibility(256);
        WindowManager.LayoutParams lp = getWindowLayoutParams();
        this.mWindowManager.addView(this.mRequestWindow, lp);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
    }

    public void onConfigurationChanged() {
        RequestWindowView requestWindowView = this.mRequestWindow;
        if (requestWindowView != null) {
            requestWindowView.onConfigurationChanged();
        }
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, WindowHelper.TYPE_NAVIGATION_BAR_PANEL, 264, -3);
        lp.token = new Binder();
        lp.privateFlags |= 16;
        lp.setTitle("ScreenPinningConfirmation");
        lp.gravity = 119;
        return lp;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getId() == R.id.screen_pinning_ok_button || this.mRequestWindow == v) {
            try {
                ActivityTaskManager.getService().startSystemLockTaskMode(this.taskId);
            } catch (RemoteException e) {
            }
        }
        clearPrompt();
    }

    public FrameLayout.LayoutParams getRequestLayoutParams(int rotation) {
        int i;
        if (rotation == 2) {
            i = 19;
        } else {
            i = rotation == 1 ? 21 : 81;
        }
        return new FrameLayout.LayoutParams(-2, -2, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class RequestWindowView extends FrameLayout {
        private static final int OFFSET_DP = 96;
        private final ColorDrawable mColor;
        private ValueAnimator mColorAnim;
        private ViewGroup mLayout;
        private final BroadcastReceiver mReceiver;
        private boolean mShowCancel;
        private final Runnable mUpdateLayoutRunnable;

        public RequestWindowView(Context context, boolean showCancel) {
            super(context);
            this.mColor = new ColorDrawable(0);
            this.mUpdateLayoutRunnable = new Runnable() { // from class: com.android.systemui.recents.ScreenPinningRequest.RequestWindowView.2
                @Override // java.lang.Runnable
                public void run() {
                    if (RequestWindowView.this.mLayout != null && RequestWindowView.this.mLayout.getParent() != null) {
                        RequestWindowView.this.mLayout.setLayoutParams(ScreenPinningRequest.this.getRequestLayoutParams(RotationUtils.getRotation(RequestWindowView.this.mContext)));
                    }
                }
            };
            this.mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.recents.ScreenPinningRequest.RequestWindowView.3
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    if (intent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
                        RequestWindowView requestWindowView = RequestWindowView.this;
                        requestWindowView.post(requestWindowView.mUpdateLayoutRunnable);
                    } else if (intent.getAction().equals("android.intent.action.USER_SWITCHED") || intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                        ScreenPinningRequest.this.clearPrompt();
                    }
                }
            };
            setClickable(true);
            setOnClickListener(ScreenPinningRequest.this);
            setBackground(this.mColor);
            this.mShowCancel = showCancel;
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            DisplayMetrics metrics = new DisplayMetrics();
            ScreenPinningRequest.this.mWindowManager.getDefaultDisplay().getMetrics(metrics);
            float density = metrics.density;
            int rotation = RotationUtils.getRotation(this.mContext);
            inflateView(rotation);
            int bgColor = this.mContext.getColor(R.color.screen_pinning_request_window_bg);
            if (ActivityManager.isHighEndGfx()) {
                this.mLayout.setAlpha(0.0f);
                if (rotation != 2) {
                    if (rotation == 1) {
                        this.mLayout.setTranslationX(96.0f * density);
                    } else {
                        this.mLayout.setTranslationY(96.0f * density);
                    }
                } else {
                    this.mLayout.setTranslationX((-96.0f) * density);
                }
                this.mLayout.animate().alpha(1.0f).translationX(0.0f).translationY(0.0f).setDuration(300L).setInterpolator(new DecelerateInterpolator()).start();
                this.mColorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), 0, Integer.valueOf(bgColor));
                this.mColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.recents.ScreenPinningRequest.RequestWindowView.1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int c = ((Integer) animation.getAnimatedValue()).intValue();
                        RequestWindowView.this.mColor.setColor(c);
                    }
                });
                this.mColorAnim.setDuration(1000L);
                this.mColorAnim.start();
            } else {
                this.mColor.setColor(bgColor);
            }
            IntentFilter filter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
            filter.addAction("android.intent.action.USER_SWITCHED");
            filter.addAction("android.intent.action.SCREEN_OFF");
            this.mContext.registerReceiver(this.mReceiver, filter);
        }

        private void inflateView(int rotation) {
            int i;
            int descriptionStringResId;
            Context context = getContext();
            boolean recentsVisible = true;
            if (rotation == 2) {
                i = R.layout.screen_pinning_request_sea_phone;
            } else {
                i = rotation == 1 ? R.layout.screen_pinning_request_land_phone : R.layout.screen_pinning_request;
            }
            this.mLayout = (ViewGroup) View.inflate(context, i, null);
            this.mLayout.setClickable(true);
            this.mLayout.setLayoutDirection(0);
            this.mLayout.findViewById(R.id.screen_pinning_text_area).setLayoutDirection(3);
            View buttons = this.mLayout.findViewById(R.id.screen_pinning_buttons);
            WindowManagerWrapper wm = WindowManagerWrapper.getInstance();
            if (!QuickStepContract.isGesturalMode(ScreenPinningRequest.this.mNavBarMode) && wm.hasSoftNavigationBar(this.mContext.getDisplayId())) {
                buttons.setLayoutDirection(3);
                swapChildrenIfRtlAndVertical(buttons);
            } else {
                buttons.setVisibility(8);
            }
            ((Button) this.mLayout.findViewById(R.id.screen_pinning_ok_button)).setOnClickListener(ScreenPinningRequest.this);
            if (this.mShowCancel) {
                ((Button) this.mLayout.findViewById(R.id.screen_pinning_cancel_button)).setOnClickListener(ScreenPinningRequest.this);
            } else {
                ((Button) this.mLayout.findViewById(R.id.screen_pinning_cancel_button)).setVisibility(4);
            }
            StatusBar statusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
            NavigationBarView navigationBarView = statusBar != null ? statusBar.getNavigationBarView() : null;
            if (navigationBarView == null || !navigationBarView.isRecentsButtonVisible()) {
                recentsVisible = false;
            }
            boolean touchExplorationEnabled = ScreenPinningRequest.this.mAccessibilityService.isTouchExplorationEnabled();
            if (QuickStepContract.isGesturalMode(ScreenPinningRequest.this.mNavBarMode)) {
                descriptionStringResId = R.string.screen_pinning_description_gestural;
            } else if (recentsVisible) {
                this.mLayout.findViewById(R.id.screen_pinning_recents_group).setVisibility(0);
                this.mLayout.findViewById(R.id.screen_pinning_home_bg_light).setVisibility(4);
                this.mLayout.findViewById(R.id.screen_pinning_home_bg).setVisibility(4);
                if (touchExplorationEnabled) {
                    descriptionStringResId = R.string.screen_pinning_description_accessible;
                } else {
                    descriptionStringResId = R.string.screen_pinning_description;
                }
            } else {
                this.mLayout.findViewById(R.id.screen_pinning_recents_group).setVisibility(4);
                this.mLayout.findViewById(R.id.screen_pinning_home_bg_light).setVisibility(0);
                this.mLayout.findViewById(R.id.screen_pinning_home_bg).setVisibility(0);
                if (touchExplorationEnabled) {
                    descriptionStringResId = R.string.screen_pinning_description_recents_invisible_accessible;
                } else {
                    descriptionStringResId = R.string.screen_pinning_description_recents_invisible;
                }
            }
            if (navigationBarView != null) {
                ((ImageView) this.mLayout.findViewById(R.id.screen_pinning_back_icon)).setImageDrawable(navigationBarView.getBackDrawable());
                ((ImageView) this.mLayout.findViewById(R.id.screen_pinning_home_icon)).setImageDrawable(navigationBarView.getHomeDrawable());
            }
            ((TextView) this.mLayout.findViewById(R.id.screen_pinning_description)).setText(descriptionStringResId);
            int backBgVisibility = touchExplorationEnabled ? 4 : 0;
            this.mLayout.findViewById(R.id.screen_pinning_back_bg).setVisibility(backBgVisibility);
            this.mLayout.findViewById(R.id.screen_pinning_back_bg_light).setVisibility(backBgVisibility);
            addView(this.mLayout, ScreenPinningRequest.this.getRequestLayoutParams(rotation));
        }

        private void swapChildrenIfRtlAndVertical(View group) {
            if (this.mContext.getResources().getConfiguration().getLayoutDirection() != 1) {
                return;
            }
            LinearLayout linearLayout = (LinearLayout) group;
            if (linearLayout.getOrientation() == 1) {
                int childCount = linearLayout.getChildCount();
                ArrayList<View> childList = new ArrayList<>(childCount);
                for (int i = 0; i < childCount; i++) {
                    childList.add(linearLayout.getChildAt(i));
                }
                linearLayout.removeAllViews();
                for (int i2 = childCount - 1; i2 >= 0; i2--) {
                    linearLayout.addView(childList.get(i2));
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            this.mContext.unregisterReceiver(this.mReceiver);
        }

        protected void onConfigurationChanged() {
            removeAllViews();
            inflateView(RotationUtils.getRotation(this.mContext));
        }
    }
}
