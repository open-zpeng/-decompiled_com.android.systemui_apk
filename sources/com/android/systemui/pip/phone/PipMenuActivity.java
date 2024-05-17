package com.android.systemui.pip.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes21.dex */
public class PipMenuActivity extends Activity {
    private static final float DISABLED_ACTION_ALPHA = 0.54f;
    private static final float DISMISS_BACKGROUND_ALPHA = 0.6f;
    private static final int INITIAL_DISMISS_DELAY = 3500;
    private static final float MENU_BACKGROUND_ALPHA = 0.3f;
    private static final long MENU_FADE_DURATION = 125;
    public static final int MESSAGE_ANIMATION_ENDED = 6;
    public static final int MESSAGE_HIDE_MENU = 3;
    public static final int MESSAGE_POKE_MENU = 2;
    public static final int MESSAGE_SHOW_MENU = 1;
    public static final int MESSAGE_TOUCH_EVENT = 7;
    public static final int MESSAGE_UPDATE_ACTIONS = 4;
    public static final int MESSAGE_UPDATE_DISMISS_FRACTION = 5;
    private static final int POST_INTERACTION_DISMISS_DELAY = 2000;
    private static final String TAG = "PipMenuActivity";
    private AccessibilityManager mAccessibilityManager;
    private LinearLayout mActionsGroup;
    private Drawable mBackgroundDrawable;
    private int mBetweenActionPaddingLand;
    private View mDismissButton;
    private ImageView mExpandButton;
    private View mMenuContainer;
    private AnimatorSet mMenuContainerAnimator;
    private int mMenuState;
    private View mSettingsButton;
    private Messenger mToControllerMessenger;
    private View mViewRoot;
    private boolean mAllowMenuTimeout = true;
    private boolean mAllowTouches = true;
    private final List<RemoteAction> mActions = new ArrayList();
    private ValueAnimator.AnimatorUpdateListener mMenuBgUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.pip.phone.PipMenuActivity.1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            float alpha = ((Float) animation.getAnimatedValue()).floatValue();
            PipMenuActivity.this.mBackgroundDrawable.setAlpha((int) (PipMenuActivity.MENU_BACKGROUND_ALPHA * alpha * 255.0f));
        }
    };
    private Handler mHandler = new Handler();
    private Messenger mMessenger = new Messenger(new Handler() { // from class: com.android.systemui.pip.phone.PipMenuActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle data = (Bundle) msg.obj;
                    PipMenuActivity.this.showMenu(data.getInt(PipMenuActivityController.EXTRA_MENU_STATE), (Rect) data.getParcelable(PipMenuActivityController.EXTRA_STACK_BOUNDS), (Rect) data.getParcelable(PipMenuActivityController.EXTRA_MOVEMENT_BOUNDS), data.getBoolean(PipMenuActivityController.EXTRA_ALLOW_TIMEOUT), data.getBoolean(PipMenuActivityController.EXTRA_WILL_RESIZE_MENU));
                    return;
                case 2:
                    PipMenuActivity.this.cancelDelayedFinish();
                    return;
                case 3:
                    PipMenuActivity.this.hideMenu((Runnable) msg.obj);
                    return;
                case 4:
                    Bundle data2 = (Bundle) msg.obj;
                    ParceledListSlice actions = data2.getParcelable("actions");
                    PipMenuActivity.this.setActions((Rect) data2.getParcelable(PipMenuActivityController.EXTRA_STACK_BOUNDS), actions != null ? actions.getList() : Collections.EMPTY_LIST);
                    return;
                case 5:
                    PipMenuActivity.this.updateDismissFraction(((Bundle) msg.obj).getFloat(PipMenuActivityController.EXTRA_DISMISS_FRACTION));
                    return;
                case 6:
                    PipMenuActivity.this.mAllowTouches = true;
                    return;
                case 7:
                    MotionEvent ev = (MotionEvent) msg.obj;
                    PipMenuActivity.this.dispatchTouchEvent(ev);
                    return;
                default:
                    return;
            }
        }
    });
    private final Runnable mFinishRunnable = new Runnable() { // from class: com.android.systemui.pip.phone.PipMenuActivity.3
        @Override // java.lang.Runnable
        public void run() {
            PipMenuActivity.this.hideMenu();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(262144);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pip_menu_activity);
        this.mAccessibilityManager = (AccessibilityManager) getSystemService(AccessibilityManager.class);
        this.mBackgroundDrawable = new ColorDrawable(-16777216);
        this.mBackgroundDrawable.setAlpha(0);
        this.mViewRoot = findViewById(R.id.background);
        this.mViewRoot.setBackground(this.mBackgroundDrawable);
        this.mMenuContainer = findViewById(R.id.menu_container);
        this.mMenuContainer.setAlpha(0.0f);
        this.mSettingsButton = findViewById(R.id.settings);
        this.mSettingsButton.setAlpha(0.0f);
        this.mSettingsButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$4MVIZwVdJN3lkWpqrFrI53Q9bPQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PipMenuActivity.this.lambda$onCreate$0$PipMenuActivity(view);
            }
        });
        this.mDismissButton = findViewById(R.id.dismiss);
        this.mDismissButton.setAlpha(0.0f);
        this.mDismissButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$lkNLpysIkUfrlXCWX9bvozrYe1U
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PipMenuActivity.this.lambda$onCreate$1$PipMenuActivity(view);
            }
        });
        findViewById(R.id.expand_button).setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$70yHDyzrwE1GNEVEQrmSEL7H6fY
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PipMenuActivity.this.lambda$onCreate$2$PipMenuActivity(view);
            }
        });
        this.mActionsGroup = (LinearLayout) findViewById(R.id.actions_group);
        this.mBetweenActionPaddingLand = getResources().getDimensionPixelSize(R.dimen.pip_between_action_padding_land);
        this.mExpandButton = (ImageView) findViewById(R.id.expand_button);
        updateFromIntent(getIntent());
        setTitle(R.string.pip_menu_title);
        setDisablePreviewScreenshots(true);
    }

    public /* synthetic */ void lambda$onCreate$0$PipMenuActivity(View v) {
        if (v.getAlpha() != 0.0f) {
            showSettings();
        }
    }

    public /* synthetic */ void lambda$onCreate$1$PipMenuActivity(View v) {
        dismissPip();
    }

    public /* synthetic */ void lambda$onCreate$2$PipMenuActivity(View v) {
        if (this.mMenuContainer.getAlpha() != 0.0f) {
            expandPip();
        }
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateFromIntent(intent);
    }

    @Override // android.app.Activity
    public void onUserInteraction() {
        if (this.mAllowMenuTimeout) {
            repostDelayedFinish(2000);
        }
    }

    @Override // android.app.Activity
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        hideMenu();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        cancelDelayedFinish();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        notifyActivityCallback(null);
    }

    @Override // android.app.Activity
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (!isInPictureInPictureMode) {
            finish();
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!this.mAllowTouches) {
            return false;
        }
        if (ev.getAction() == 4) {
            hideMenu();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.app.Activity
    public void finish() {
        notifyActivityCallback(null);
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // android.app.Activity
    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMenu(int menuState, Rect stackBounds, Rect movementBounds, boolean allowMenuTimeout, boolean resizeMenuOnShow) {
        this.mAllowMenuTimeout = allowMenuTimeout;
        int i = this.mMenuState;
        if (i == menuState) {
            if (allowMenuTimeout) {
                repostDelayedFinish(2000);
                return;
            }
            return;
        }
        boolean disallowTouchesUntilAnimationEnd = resizeMenuOnShow && (i == 2 || menuState == 2);
        this.mAllowTouches = !disallowTouchesUntilAnimationEnd;
        cancelDelayedFinish();
        updateActionViews(stackBounds);
        AnimatorSet animatorSet = this.mMenuContainerAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        notifyMenuStateChange(menuState);
        this.mMenuContainerAnimator = new AnimatorSet();
        ObjectAnimator menuAnim = ObjectAnimator.ofFloat(this.mMenuContainer, View.ALPHA, this.mMenuContainer.getAlpha(), 1.0f);
        menuAnim.addUpdateListener(this.mMenuBgUpdateListener);
        ObjectAnimator settingsAnim = ObjectAnimator.ofFloat(this.mSettingsButton, View.ALPHA, this.mSettingsButton.getAlpha(), 1.0f);
        ObjectAnimator dismissAnim = ObjectAnimator.ofFloat(this.mDismissButton, View.ALPHA, this.mDismissButton.getAlpha(), 1.0f);
        if (menuState == 2) {
            this.mMenuContainerAnimator.playTogether(menuAnim, settingsAnim, dismissAnim);
        } else {
            this.mMenuContainerAnimator.playTogether(dismissAnim);
        }
        this.mMenuContainerAnimator.setInterpolator(Interpolators.ALPHA_IN);
        this.mMenuContainerAnimator.setDuration(MENU_FADE_DURATION);
        if (allowMenuTimeout) {
            this.mMenuContainerAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.pip.phone.PipMenuActivity.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PipMenuActivity.this.repostDelayedFinish(PipMenuActivity.INITIAL_DISMISS_DELAY);
                }
            });
        }
        this.mMenuContainerAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideMenu() {
        hideMenu(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideMenu(Runnable animationEndCallback) {
        hideMenu(animationEndCallback, true, false);
    }

    private void hideMenu(final Runnable animationFinishedRunnable, boolean notifyMenuVisibility, final boolean isDismissing) {
        if (this.mMenuState != 0) {
            cancelDelayedFinish();
            if (notifyMenuVisibility) {
                notifyMenuStateChange(0);
            }
            this.mMenuContainerAnimator = new AnimatorSet();
            ObjectAnimator menuAnim = ObjectAnimator.ofFloat(this.mMenuContainer, View.ALPHA, this.mMenuContainer.getAlpha(), 0.0f);
            menuAnim.addUpdateListener(this.mMenuBgUpdateListener);
            ObjectAnimator settingsAnim = ObjectAnimator.ofFloat(this.mSettingsButton, View.ALPHA, this.mSettingsButton.getAlpha(), 0.0f);
            ObjectAnimator dismissAnim = ObjectAnimator.ofFloat(this.mDismissButton, View.ALPHA, this.mDismissButton.getAlpha(), 0.0f);
            this.mMenuContainerAnimator.playTogether(menuAnim, settingsAnim, dismissAnim);
            this.mMenuContainerAnimator.setInterpolator(Interpolators.ALPHA_OUT);
            this.mMenuContainerAnimator.setDuration(MENU_FADE_DURATION);
            this.mMenuContainerAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.pip.phone.PipMenuActivity.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    Runnable runnable = animationFinishedRunnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                    if (!isDismissing) {
                        PipMenuActivity.this.finish();
                    }
                }
            });
            this.mMenuContainerAnimator.start();
            return;
        }
        finish();
    }

    private void updateFromIntent(Intent intent) {
        this.mToControllerMessenger = (Messenger) intent.getParcelableExtra(PipMenuActivityController.EXTRA_CONTROLLER_MESSENGER);
        if (this.mToControllerMessenger == null) {
            Log.w(TAG, "Controller messenger is null. Stopping.");
            finish();
            return;
        }
        notifyActivityCallback(this.mMessenger);
        ParceledListSlice actions = intent.getParcelableExtra("actions");
        if (actions != null) {
            this.mActions.clear();
            this.mActions.addAll(actions.getList());
        }
        int menuState = intent.getIntExtra(PipMenuActivityController.EXTRA_MENU_STATE, 0);
        if (menuState != 0) {
            Rect stackBounds = (Rect) intent.getParcelableExtra(PipMenuActivityController.EXTRA_STACK_BOUNDS);
            Rect movementBounds = (Rect) intent.getParcelableExtra(PipMenuActivityController.EXTRA_MOVEMENT_BOUNDS);
            boolean allowMenuTimeout = intent.getBooleanExtra(PipMenuActivityController.EXTRA_ALLOW_TIMEOUT, true);
            boolean willResizeMenu = intent.getBooleanExtra(PipMenuActivityController.EXTRA_WILL_RESIZE_MENU, false);
            showMenu(menuState, stackBounds, movementBounds, allowMenuTimeout, willResizeMenu);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setActions(Rect stackBounds, List<RemoteAction> actions) {
        this.mActions.clear();
        this.mActions.addAll(actions);
        updateActionViews(stackBounds);
    }

    private void updateActionViews(Rect stackBounds) {
        int i;
        ViewGroup expandContainer = (ViewGroup) findViewById(R.id.expand_container);
        ViewGroup actionsContainer = (ViewGroup) findViewById(R.id.actions_container);
        actionsContainer.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$BXxmOnLU-s8BTsc_oWau4TVb1pE
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return PipMenuActivity.lambda$updateActionViews$3(view, motionEvent);
            }
        });
        if (!this.mActions.isEmpty()) {
            boolean isLandscapePip = true;
            if (this.mMenuState != 1) {
                actionsContainer.setVisibility(0);
                if (this.mActionsGroup != null) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    while (this.mActionsGroup.getChildCount() < this.mActions.size()) {
                        this.mActionsGroup.addView((ImageView) inflater.inflate(R.layout.pip_menu_action, (ViewGroup) this.mActionsGroup, false));
                    }
                    for (int i2 = 0; i2 < this.mActionsGroup.getChildCount(); i2++) {
                        View childAt = this.mActionsGroup.getChildAt(i2);
                        if (i2 < this.mActions.size()) {
                            i = 0;
                        } else {
                            i = 8;
                        }
                        childAt.setVisibility(i);
                    }
                    isLandscapePip = (stackBounds == null || stackBounds.width() <= stackBounds.height()) ? false : false;
                    int i3 = 0;
                    while (i3 < this.mActions.size()) {
                        final RemoteAction action = this.mActions.get(i3);
                        final ImageView actionView = (ImageView) this.mActionsGroup.getChildAt(i3);
                        action.getIcon().loadDrawableAsync(this, new Icon.OnDrawableLoadedListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$ElzPpK0Puw-PlOEH67SuhTD5JYQ
                            @Override // android.graphics.drawable.Icon.OnDrawableLoadedListener
                            public final void onDrawableLoaded(Drawable drawable) {
                                PipMenuActivity.lambda$updateActionViews$4(actionView, drawable);
                            }
                        }, this.mHandler);
                        actionView.setContentDescription(action.getContentDescription());
                        if (action.isEnabled()) {
                            actionView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$Ts5um0YR6IQ0YRdLS2dyHj4GSpg
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view) {
                                    PipMenuActivity.this.lambda$updateActionViews$6$PipMenuActivity(action, view);
                                }
                            });
                        }
                        actionView.setEnabled(action.isEnabled());
                        actionView.setAlpha(action.isEnabled() ? 1.0f : DISABLED_ACTION_ALPHA);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) actionView.getLayoutParams();
                        lp.leftMargin = (!isLandscapePip || i3 <= 0) ? 0 : this.mBetweenActionPaddingLand;
                        i3++;
                    }
                }
                FrameLayout.LayoutParams expandedLp = (FrameLayout.LayoutParams) expandContainer.getLayoutParams();
                expandedLp.topMargin = getResources().getDimensionPixelSize(R.dimen.pip_action_padding);
                expandedLp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.pip_expand_container_edge_margin);
                expandContainer.requestLayout();
                return;
            }
        }
        actionsContainer.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$updateActionViews$3(View v, MotionEvent ev) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateActionViews$4(ImageView actionView, Drawable d) {
        d.setTint(-1);
        actionView.setImageDrawable(d);
    }

    public /* synthetic */ void lambda$updateActionViews$6$PipMenuActivity(final RemoteAction action, View v) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$KxDwr2Rt3pvR-EKt-FVSgFixejo
            @Override // java.lang.Runnable
            public final void run() {
                PipMenuActivity.lambda$updateActionViews$5(action);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateActionViews$5(RemoteAction action) {
        try {
            action.getActionIntent().send();
        } catch (PendingIntent.CanceledException e) {
            Log.w(TAG, "Failed to send action", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDismissFraction(float fraction) {
        int alpha;
        float menuAlpha = 1.0f - fraction;
        int i = this.mMenuState;
        if (i == 2) {
            this.mMenuContainer.setAlpha(menuAlpha);
            this.mSettingsButton.setAlpha(menuAlpha);
            this.mDismissButton.setAlpha(menuAlpha);
            float interpolatedAlpha = (MENU_BACKGROUND_ALPHA * menuAlpha) + (0.6f * fraction);
            alpha = (int) (255.0f * interpolatedAlpha);
        } else {
            if (i == 1) {
                this.mDismissButton.setAlpha(menuAlpha);
            }
            alpha = (int) (0.6f * fraction * 255.0f);
        }
        this.mBackgroundDrawable.setAlpha(alpha);
    }

    private void notifyMenuStateChange(int menuState) {
        this.mMenuState = menuState;
        Message m = Message.obtain();
        m.what = 100;
        m.arg1 = menuState;
        sendMessage(m, "Could not notify controller of PIP menu visibility");
    }

    private void expandPip() {
        hideMenu(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$gxeJOYpgn30UbyKen9nD4GpRdFQ
            @Override // java.lang.Runnable
            public final void run() {
                PipMenuActivity.this.lambda$expandPip$7$PipMenuActivity();
            }
        }, false, false);
    }

    public /* synthetic */ void lambda$expandPip$7$PipMenuActivity() {
        sendEmptyMessage(101, "Could not notify controller to expand PIP");
    }

    private void dismissPip() {
        hideMenu(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivity$guHLrBiStjvmB9r01MbFqRGaK3c
            @Override // java.lang.Runnable
            public final void run() {
                PipMenuActivity.this.lambda$dismissPip$8$PipMenuActivity();
            }
        }, false, true);
    }

    public /* synthetic */ void lambda$dismissPip$8$PipMenuActivity() {
        sendEmptyMessage(103, "Could not notify controller to dismiss PIP");
    }

    private void showSettings() {
        Pair<ComponentName, Integer> topPipActivityInfo = PipUtils.getTopPinnedActivity(this, ActivityManager.getService());
        if (topPipActivityInfo.first != null) {
            UserHandle user = UserHandle.of(((Integer) topPipActivityInfo.second).intValue());
            Intent settingsIntent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS", Uri.fromParts("package", ((ComponentName) topPipActivityInfo.first).getPackageName(), null));
            settingsIntent.putExtra("android.intent.extra.user_handle", user);
            settingsIntent.setFlags(268468224);
            startActivity(settingsIntent);
        }
    }

    private void notifyActivityCallback(Messenger callback) {
        Message m = Message.obtain();
        m.what = 104;
        m.replyTo = callback;
        sendMessage(m, "Could not notify controller of activity finished");
    }

    private void sendEmptyMessage(int what, String errorMsg) {
        Message m = Message.obtain();
        m.what = what;
        sendMessage(m, errorMsg);
    }

    private void sendMessage(Message m, String errorMsg) {
        Messenger messenger = this.mToControllerMessenger;
        if (messenger == null) {
            return;
        }
        try {
            messenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, errorMsg, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelDelayedFinish() {
        this.mHandler.removeCallbacks(this.mFinishRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void repostDelayedFinish(int delay) {
        int recommendedTimeout = this.mAccessibilityManager.getRecommendedTimeoutMillis(delay, 5);
        this.mHandler.removeCallbacks(this.mFinishRunnable);
        this.mHandler.postDelayed(this.mFinishRunnable, recommendedTimeout);
    }
}
