package com.android.systemui.screenshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.screenshot.GlobalScreenshot;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.NotificationChannels;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class GlobalScreenshot {
    static final String ACTION_TYPE_DELETE = "Delete";
    static final String ACTION_TYPE_EDIT = "Edit";
    static final String ACTION_TYPE_SHARE = "Share";
    private static final float BACKGROUND_ALPHA = 0.5f;
    static final String EXTRA_ACTION_INTENT = "android:screenshot_action_intent";
    static final String EXTRA_ACTION_TYPE = "android:screenshot_action_type";
    static final String EXTRA_CANCEL_NOTIFICATION = "android:screenshot_cancel_notification";
    static final String EXTRA_DISALLOW_ENTER_PIP = "android:screenshot_disallow_enter_pip";
    static final String EXTRA_ID = "android:screenshot_id";
    static final String EXTRA_SMART_ACTIONS_ENABLED = "android:smart_actions_enabled";
    private static final int SCREENSHOT_DROP_IN_DURATION = 430;
    private static final float SCREENSHOT_DROP_IN_MIN_SCALE = 0.725f;
    private static final int SCREENSHOT_DROP_OUT_DELAY = 500;
    private static final int SCREENSHOT_DROP_OUT_DURATION = 430;
    private static final float SCREENSHOT_DROP_OUT_MIN_SCALE = 0.45f;
    private static final float SCREENSHOT_DROP_OUT_MIN_SCALE_OFFSET = 0.0f;
    private static final int SCREENSHOT_DROP_OUT_SCALE_DURATION = 370;
    private static final int SCREENSHOT_FAST_DROP_OUT_DURATION = 320;
    private static final float SCREENSHOT_FAST_DROP_OUT_MIN_SCALE = 0.6f;
    private static final int SCREENSHOT_FLASH_TO_PEAK_DURATION = 130;
    private static final float SCREENSHOT_SCALE = 1.0f;
    static final String SCREENSHOT_URI_ID = "android:screenshot_uri_id";
    private static final String TAG = "GlobalScreenshot";
    private ImageView mBackgroundView;
    private float mBgPadding;
    private float mBgPaddingScale;
    private MediaActionSound mCameraSound;
    private Context mContext;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;
    private int mNotificationIconSize;
    private NotificationManager mNotificationManager;
    private final int mPreviewHeight;
    private final int mPreviewWidth;
    private AsyncTask<Void, Void, Void> mSaveInBgTask;
    private Bitmap mScreenBitmap;
    private AnimatorSet mScreenshotAnimation;
    private ImageView mScreenshotFlash;
    private View mScreenshotLayout;
    private ScreenshotSelectorView mScreenshotSelectorView;
    private ImageView mScreenshotView;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private WindowManager mWindowManager;

    public GlobalScreenshot(Context context) {
        Resources r = context.getResources();
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mScreenshotLayout = layoutInflater.inflate(R.layout.global_screenshot, (ViewGroup) null);
        this.mBackgroundView = (ImageView) this.mScreenshotLayout.findViewById(R.id.global_screenshot_background);
        this.mScreenshotView = (ImageView) this.mScreenshotLayout.findViewById(R.id.global_screenshot);
        this.mScreenshotFlash = (ImageView) this.mScreenshotLayout.findViewById(R.id.global_screenshot_flash);
        this.mScreenshotSelectorView = (ScreenshotSelectorView) this.mScreenshotLayout.findViewById(R.id.global_screenshot_selector);
        this.mScreenshotLayout.setFocusable(true);
        this.mScreenshotSelectorView.setFocusable(true);
        this.mScreenshotSelectorView.setFocusableInTouchMode(true);
        this.mScreenshotLayout.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.screenshot.GlobalScreenshot.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.mWindowLayoutParams = new WindowManager.LayoutParams(-1, -1, 0, 0, 2036, 17302784, -3);
        this.mWindowLayoutParams.setTitle("ScreenshotAnimation");
        this.mWindowLayoutParams.layoutInDisplayCutoutMode = 1;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        this.mDisplay = this.mWindowManager.getDefaultDisplay();
        this.mDisplayMetrics = new DisplayMetrics();
        this.mDisplay.getRealMetrics(this.mDisplayMetrics);
        this.mNotificationIconSize = r.getDimensionPixelSize(17104902);
        this.mBgPadding = r.getDimensionPixelSize(R.dimen.global_screenshot_bg_padding);
        this.mBgPaddingScale = this.mBgPadding / this.mDisplayMetrics.widthPixels;
        int panelWidth = 0;
        try {
            panelWidth = r.getDimensionPixelSize(R.dimen.notification_panel_width);
        } catch (Resources.NotFoundException e) {
        }
        this.mPreviewWidth = panelWidth <= 0 ? this.mDisplayMetrics.widthPixels : panelWidth;
        this.mPreviewHeight = r.getDimensionPixelSize(R.dimen.notification_max_height);
        this.mCameraSound = new MediaActionSound();
        this.mCameraSound.load(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveScreenshotInWorkerThread(Consumer<Uri> finisher) {
        SaveImageInBackgroundData data = new SaveImageInBackgroundData();
        data.context = this.mContext;
        data.image = this.mScreenBitmap;
        data.iconSize = this.mNotificationIconSize;
        data.finisher = finisher;
        data.previewWidth = this.mPreviewWidth;
        data.previewheight = this.mPreviewHeight;
        AsyncTask<Void, Void, Void> asyncTask = this.mSaveInBgTask;
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
        this.mSaveInBgTask = new SaveImageInBackgroundTask(this.mContext, data, this.mNotificationManager).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void takeScreenshot(Consumer<Uri> finisher, boolean statusBarVisible, boolean navBarVisible, Rect crop) {
        int rot = this.mDisplay.getRotation();
        int width = crop.width();
        int height = crop.height();
        this.mScreenBitmap = SurfaceControl.screenshot(crop, width, height, rot);
        Bitmap bitmap = this.mScreenBitmap;
        if (bitmap == null) {
            notifyScreenshotError(this.mContext, this.mNotificationManager, R.string.screenshot_failed_to_capture_text);
            finisher.accept(null);
            return;
        }
        bitmap.setHasAlpha(false);
        this.mScreenBitmap.prepareToDraw();
        startAnimation(finisher, this.mDisplayMetrics.widthPixels, this.mDisplayMetrics.heightPixels, statusBarVisible, navBarVisible);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void takeScreenshot(Consumer<Uri> finisher, boolean statusBarVisible, boolean navBarVisible) {
        this.mDisplay.getRealMetrics(this.mDisplayMetrics);
        takeScreenshot(finisher, statusBarVisible, navBarVisible, new Rect(0, 0, this.mDisplayMetrics.widthPixels, this.mDisplayMetrics.heightPixels));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void takeScreenshotPartial(final Consumer<Uri> finisher, final boolean statusBarVisible, final boolean navBarVisible) {
        this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
        this.mScreenshotSelectorView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.screenshot.GlobalScreenshot.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                ScreenshotSelectorView view = (ScreenshotSelectorView) v;
                int action = event.getAction();
                if (action == 0) {
                    view.startSelection((int) event.getX(), (int) event.getY());
                    return true;
                } else if (action != 1) {
                    if (action == 2) {
                        view.updateSelection((int) event.getX(), (int) event.getY());
                        return true;
                    }
                    return false;
                } else {
                    view.setVisibility(8);
                    GlobalScreenshot.this.mWindowManager.removeView(GlobalScreenshot.this.mScreenshotLayout);
                    final Rect rect = view.getSelectionRect();
                    if (rect != null && rect.width() != 0 && rect.height() != 0) {
                        GlobalScreenshot.this.mScreenshotLayout.post(new Runnable() { // from class: com.android.systemui.screenshot.GlobalScreenshot.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                GlobalScreenshot.this.takeScreenshot(finisher, statusBarVisible, navBarVisible, rect);
                            }
                        });
                    }
                    view.stopSelection();
                    return true;
                }
            }
        });
        this.mScreenshotLayout.post(new Runnable() { // from class: com.android.systemui.screenshot.GlobalScreenshot.3
            @Override // java.lang.Runnable
            public void run() {
                GlobalScreenshot.this.mScreenshotSelectorView.setVisibility(0);
                GlobalScreenshot.this.mScreenshotSelectorView.requestFocus();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopScreenshot() {
        if (this.mScreenshotSelectorView.getSelectionRect() != null) {
            this.mWindowManager.removeView(this.mScreenshotLayout);
            this.mScreenshotSelectorView.stopSelection();
        }
    }

    private void startAnimation(final Consumer<Uri> finisher, int w, int h, boolean statusBarVisible, boolean navBarVisible) {
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
        if (powerManager.isPowerSaveMode()) {
            Toast.makeText(this.mContext, R.string.screenshot_saved_title, 0).show();
        }
        this.mScreenshotView.setImageBitmap(this.mScreenBitmap);
        this.mScreenshotLayout.requestFocus();
        AnimatorSet animatorSet = this.mScreenshotAnimation;
        if (animatorSet != null) {
            if (animatorSet.isStarted()) {
                this.mScreenshotAnimation.end();
            }
            this.mScreenshotAnimation.removeAllListeners();
        }
        this.mWindowManager.addView(this.mScreenshotLayout, this.mWindowLayoutParams);
        ValueAnimator screenshotDropInAnim = createScreenshotDropInAnimation();
        ValueAnimator screenshotFadeOutAnim = createScreenshotDropOutAnimation(w, h, statusBarVisible, navBarVisible);
        this.mScreenshotAnimation = new AnimatorSet();
        this.mScreenshotAnimation.playSequentially(screenshotDropInAnim, screenshotFadeOutAnim);
        this.mScreenshotAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.screenshot.GlobalScreenshot.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                GlobalScreenshot.this.saveScreenshotInWorkerThread(finisher);
                GlobalScreenshot.this.mWindowManager.removeView(GlobalScreenshot.this.mScreenshotLayout);
                GlobalScreenshot.this.mScreenBitmap = null;
                GlobalScreenshot.this.mScreenshotView.setImageBitmap(null);
            }
        });
        this.mScreenshotLayout.post(new Runnable() { // from class: com.android.systemui.screenshot.GlobalScreenshot.5
            @Override // java.lang.Runnable
            public void run() {
                GlobalScreenshot.this.mCameraSound.play(0);
                GlobalScreenshot.this.mScreenshotView.setLayerType(2, null);
                GlobalScreenshot.this.mScreenshotView.buildLayer();
                GlobalScreenshot.this.mScreenshotAnimation.start();
            }
        });
    }

    private ValueAnimator createScreenshotDropInAnimation() {
        final Interpolator flashAlphaInterpolator = new Interpolator() { // from class: com.android.systemui.screenshot.GlobalScreenshot.6
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float x) {
                if (x <= 0.60465115f) {
                    return (float) Math.sin((x / 0.60465115f) * 3.141592653589793d);
                }
                return 0.0f;
            }
        };
        final Interpolator scaleInterpolator = new Interpolator() { // from class: com.android.systemui.screenshot.GlobalScreenshot.7
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float x) {
                if (x < 0.30232558f) {
                    return 0.0f;
                }
                return (x - 0.60465115f) / 0.39534885f;
            }
        };
        Resources r = this.mContext.getResources();
        if ((r.getConfiguration().uiMode & 48) == 32) {
            this.mScreenshotView.getBackground().setTint(-16777216);
        } else {
            this.mScreenshotView.getBackground().setTintList(null);
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.setDuration(430L);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.screenshot.GlobalScreenshot.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                GlobalScreenshot.this.mBackgroundView.setAlpha(0.0f);
                GlobalScreenshot.this.mBackgroundView.setVisibility(0);
                GlobalScreenshot.this.mScreenshotView.setAlpha(0.0f);
                GlobalScreenshot.this.mScreenshotView.setTranslationX(0.0f);
                GlobalScreenshot.this.mScreenshotView.setTranslationY(0.0f);
                GlobalScreenshot.this.mScreenshotView.setScaleX(GlobalScreenshot.this.mBgPaddingScale + 1.0f);
                GlobalScreenshot.this.mScreenshotView.setScaleY(GlobalScreenshot.this.mBgPaddingScale + 1.0f);
                GlobalScreenshot.this.mScreenshotView.setVisibility(0);
                GlobalScreenshot.this.mScreenshotFlash.setAlpha(0.0f);
                GlobalScreenshot.this.mScreenshotFlash.setVisibility(0);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                GlobalScreenshot.this.mScreenshotFlash.setVisibility(8);
            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.screenshot.GlobalScreenshot.9
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = ((Float) animation.getAnimatedValue()).floatValue();
                float scaleT = (GlobalScreenshot.this.mBgPaddingScale + 1.0f) - (scaleInterpolator.getInterpolation(t) * 0.27499998f);
                GlobalScreenshot.this.mBackgroundView.setAlpha(scaleInterpolator.getInterpolation(t) * 0.5f);
                GlobalScreenshot.this.mScreenshotView.setAlpha(t);
                GlobalScreenshot.this.mScreenshotView.setScaleX(scaleT);
                GlobalScreenshot.this.mScreenshotView.setScaleY(scaleT);
                GlobalScreenshot.this.mScreenshotFlash.setAlpha(flashAlphaInterpolator.getInterpolation(t));
            }
        });
        return anim;
    }

    private ValueAnimator createScreenshotDropOutAnimation(int w, int h, boolean statusBarVisible, boolean navBarVisible) {
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.setStartDelay(500L);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.screenshot.GlobalScreenshot.10
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                GlobalScreenshot.this.mBackgroundView.setVisibility(8);
                GlobalScreenshot.this.mScreenshotView.setVisibility(8);
                GlobalScreenshot.this.mScreenshotView.setLayerType(0, null);
            }
        });
        if (!statusBarVisible || !navBarVisible) {
            anim.setDuration(320L);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.screenshot.GlobalScreenshot.11
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = ((Float) animation.getAnimatedValue()).floatValue();
                    float scaleT = (GlobalScreenshot.this.mBgPaddingScale + GlobalScreenshot.SCREENSHOT_DROP_IN_MIN_SCALE) - (0.125f * t);
                    GlobalScreenshot.this.mBackgroundView.setAlpha((1.0f - t) * 0.5f);
                    GlobalScreenshot.this.mScreenshotView.setAlpha(1.0f - t);
                    GlobalScreenshot.this.mScreenshotView.setScaleX(scaleT);
                    GlobalScreenshot.this.mScreenshotView.setScaleY(scaleT);
                }
            });
        } else {
            final Interpolator scaleInterpolator = new Interpolator() { // from class: com.android.systemui.screenshot.GlobalScreenshot.12
                @Override // android.animation.TimeInterpolator
                public float getInterpolation(float x) {
                    if (x < 0.8604651f) {
                        return (float) (1.0d - Math.pow(1.0f - (x / 0.8604651f), 2.0d));
                    }
                    return 1.0f;
                }
            };
            float f = this.mBgPadding;
            float halfScreenWidth = (w - (f * 2.0f)) / 2.0f;
            float halfScreenHeight = (h - (f * 2.0f)) / 2.0f;
            final PointF finalPos = new PointF((-halfScreenWidth) + (halfScreenWidth * SCREENSHOT_DROP_OUT_MIN_SCALE), (-halfScreenHeight) + (SCREENSHOT_DROP_OUT_MIN_SCALE * halfScreenHeight));
            anim.setDuration(430L);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.screenshot.GlobalScreenshot.13
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = ((Float) animation.getAnimatedValue()).floatValue();
                    float scaleT = (GlobalScreenshot.this.mBgPaddingScale + GlobalScreenshot.SCREENSHOT_DROP_IN_MIN_SCALE) - (scaleInterpolator.getInterpolation(t) * 0.27500004f);
                    GlobalScreenshot.this.mBackgroundView.setAlpha((1.0f - t) * 0.5f);
                    GlobalScreenshot.this.mScreenshotView.setAlpha(1.0f - scaleInterpolator.getInterpolation(t));
                    GlobalScreenshot.this.mScreenshotView.setScaleX(scaleT);
                    GlobalScreenshot.this.mScreenshotView.setScaleY(scaleT);
                    GlobalScreenshot.this.mScreenshotView.setTranslationX(finalPos.x * t);
                    GlobalScreenshot.this.mScreenshotView.setTranslationY(finalPos.y * t);
                }
            });
        }
        return anim;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void notifyScreenshotError(Context context, NotificationManager nManager, int msgResId) {
        Resources r = context.getResources();
        String errorMsg = r.getString(msgResId);
        Notification.Builder b = new Notification.Builder(context, NotificationChannels.ALERTS).setTicker(r.getString(R.string.screenshot_failed_title)).setContentTitle(r.getString(R.string.screenshot_failed_title)).setContentText(errorMsg).setSmallIcon(R.drawable.stat_notify_image_error).setWhen(System.currentTimeMillis()).setVisibility(1).setCategory("err").setAutoCancel(true).setColor(context.getColor(17170460));
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService("device_policy");
        Intent intent = dpm.createAdminSupportIntent("policy_disable_screen_capture");
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivityAsUser(context, 0, intent, 67108864, null, UserHandle.CURRENT);
            b.setContentIntent(pendingIntent);
        }
        SystemUI.overrideNotificationAppName(context, b, true);
        Notification n = new Notification.BigTextStyle(b).bigText(errorMsg).build();
        nManager.notify(1, n);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public static CompletableFuture<List<Notification.Action>> getSmartActionsFuture(String screenshotId, Bitmap image, ScreenshotNotificationSmartActionsProvider smartActionsProvider, boolean smartActionsEnabled, boolean isManagedProfile) {
        ComponentName componentName;
        if (!smartActionsEnabled) {
            Slog.i(TAG, "Screenshot Intelligence not enabled, returning empty list.");
            return CompletableFuture.completedFuture(Collections.emptyList());
        } else if (image.getConfig() != Bitmap.Config.HARDWARE) {
            Slog.w(TAG, String.format("Bitmap expected: Hardware, Bitmap found: %s. Returning empty list.", image.getConfig()));
            return CompletableFuture.completedFuture(Collections.emptyList());
        } else {
            Slog.d(TAG, "Screenshot from a managed profile: " + isManagedProfile);
            long startTimeMs = SystemClock.uptimeMillis();
            try {
                ActivityManager.RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
                if (runningTask != null && runningTask.topActivity != null) {
                    componentName = runningTask.topActivity;
                } else {
                    componentName = new ComponentName("", "");
                }
                try {
                    CompletableFuture<List<Notification.Action>> smartActionsFuture = smartActionsProvider.getActions(screenshotId, image, componentName, isManagedProfile);
                    return smartActionsFuture;
                } catch (Throwable th) {
                    e = th;
                    long waitTimeMs = SystemClock.uptimeMillis() - startTimeMs;
                    CompletableFuture<List<Notification.Action>> smartActionsFuture2 = CompletableFuture.completedFuture(Collections.emptyList());
                    Slog.e(TAG, "Failed to get future for screenshot notification smart actions.", e);
                    notifyScreenshotOp(screenshotId, smartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp.REQUEST_SMART_ACTIONS, ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.ERROR, waitTimeMs);
                    return smartActionsFuture2;
                }
            } catch (Throwable th2) {
                e = th2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public static List<Notification.Action> getSmartActions(String screenshotId, CompletableFuture<List<Notification.Action>> smartActionsFuture, int timeoutMs, ScreenshotNotificationSmartActionsProvider smartActionsProvider) {
        ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus status;
        long startTimeMs = SystemClock.uptimeMillis();
        try {
            try {
                List<Notification.Action> actions = smartActionsFuture.get(timeoutMs, TimeUnit.MILLISECONDS);
                long waitTimeMs = SystemClock.uptimeMillis() - startTimeMs;
                Slog.d(TAG, String.format("Got %d smart actions. Wait time: %d ms", Integer.valueOf(actions.size()), Long.valueOf(waitTimeMs)));
                notifyScreenshotOp(screenshotId, smartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp.WAIT_FOR_SMART_ACTIONS, ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.SUCCESS, waitTimeMs);
                return actions;
            } catch (Throwable th) {
                e = th;
                long waitTimeMs2 = SystemClock.uptimeMillis() - startTimeMs;
                Slog.e(TAG, String.format("Error getting smart actions. Wait time: %d ms", Long.valueOf(waitTimeMs2)), e);
                if (e instanceof TimeoutException) {
                    status = ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.TIMEOUT;
                } else {
                    status = ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus.ERROR;
                }
                notifyScreenshotOp(screenshotId, smartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp.WAIT_FOR_SMART_ACTIONS, status, waitTimeMs2);
                return Collections.emptyList();
            }
        } catch (Throwable th2) {
            e = th2;
        }
    }

    static void notifyScreenshotOp(String screenshotId, ScreenshotNotificationSmartActionsProvider smartActionsProvider, ScreenshotNotificationSmartActionsProvider.ScreenshotOp op, ScreenshotNotificationSmartActionsProvider.ScreenshotOpStatus status, long durationMs) {
        try {
            smartActionsProvider.notifyOp(screenshotId, op, status, durationMs);
        } catch (Throwable e) {
            Slog.e(TAG, "Error in notifyScreenshotOp: ", e);
        }
    }

    static void notifyScreenshotAction(Context context, String screenshotId, String action, boolean isSmartAction) {
        try {
            ScreenshotNotificationSmartActionsProvider provider = SystemUIFactory.getInstance().createScreenshotNotificationSmartActionsProvider(context, AsyncTask.THREAD_POOL_EXECUTOR, new Handler());
            provider.notifyAction(screenshotId, action, isSmartAction);
        } catch (Throwable e) {
            Slog.e(TAG, "Error in notifyScreenshotAction: ", e);
        }
    }

    /* loaded from: classes21.dex */
    public static class ActionProxyReceiver extends BroadcastReceiver {
        static final int CLOSE_WINDOWS_TIMEOUT_MILLIS = 3000;

        @Override // android.content.BroadcastReceiver
        public void onReceive(final Context context, final Intent intent) {
            String actionType;
            final Intent actionIntent = (Intent) intent.getParcelableExtra(GlobalScreenshot.EXTRA_ACTION_INTENT);
            Runnable startActivityRunnable = new Runnable() { // from class: com.android.systemui.screenshot.-$$Lambda$GlobalScreenshot$ActionProxyReceiver$h8Os9j18oS09gh31eR_OoVgcfiw
                @Override // java.lang.Runnable
                public final void run() {
                    GlobalScreenshot.ActionProxyReceiver.lambda$onReceive$0(intent, context, actionIntent);
                }
            };
            StatusBar statusBar = (StatusBar) SysUiServiceProvider.getComponent(context, StatusBar.class);
            statusBar.executeRunnableDismissingKeyguard(startActivityRunnable, null, true, true, true);
            if (intent.getBooleanExtra(GlobalScreenshot.EXTRA_SMART_ACTIONS_ENABLED, false)) {
                if ("android.intent.action.EDIT".equals(actionIntent.getAction())) {
                    actionType = GlobalScreenshot.ACTION_TYPE_EDIT;
                } else {
                    actionType = GlobalScreenshot.ACTION_TYPE_SHARE;
                }
                GlobalScreenshot.notifyScreenshotAction(context, intent.getStringExtra(GlobalScreenshot.EXTRA_ID), actionType, false);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onReceive$0(Intent intent, Context context, Intent actionIntent) {
            try {
                ActivityManagerWrapper.getInstance().closeSystemWindows(StatusBar.SYSTEM_DIALOG_REASON_SCREENSHOT).get(3000L, TimeUnit.MILLISECONDS);
                if (intent.getBooleanExtra(GlobalScreenshot.EXTRA_CANCEL_NOTIFICATION, false)) {
                    GlobalScreenshot.cancelScreenshotNotification(context);
                }
                ActivityOptions opts = ActivityOptions.makeBasic();
                opts.setDisallowEnterPictureInPictureWhileLaunching(intent.getBooleanExtra(GlobalScreenshot.EXTRA_DISALLOW_ENTER_PIP, false));
                context.startActivityAsUser(actionIntent, opts.toBundle(), UserHandle.CURRENT);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Slog.e(GlobalScreenshot.TAG, "Unable to share screenshot", e);
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class TargetChosenReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            GlobalScreenshot.cancelScreenshotNotification(context);
        }
    }

    /* loaded from: classes21.dex */
    public static class DeleteScreenshotReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(GlobalScreenshot.SCREENSHOT_URI_ID)) {
                GlobalScreenshot.cancelScreenshotNotification(context);
                Uri uri = Uri.parse(intent.getStringExtra(GlobalScreenshot.SCREENSHOT_URI_ID));
                new DeleteImageInBackgroundTask(context).execute(uri);
                if (intent.getBooleanExtra(GlobalScreenshot.EXTRA_SMART_ACTIONS_ENABLED, false)) {
                    GlobalScreenshot.notifyScreenshotAction(context, intent.getStringExtra(GlobalScreenshot.EXTRA_ID), GlobalScreenshot.ACTION_TYPE_DELETE, false);
                }
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class SmartActionsReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra(GlobalScreenshot.EXTRA_ACTION_INTENT);
            Intent actionIntent = pendingIntent.getIntent();
            String actionType = intent.getStringExtra(GlobalScreenshot.EXTRA_ACTION_TYPE);
            Slog.d(GlobalScreenshot.TAG, "Executing smart action [" + actionType + "]:" + actionIntent);
            ActivityOptions opts = ActivityOptions.makeBasic();
            context.startActivityAsUser(actionIntent, opts.toBundle(), UserHandle.CURRENT);
            GlobalScreenshot.notifyScreenshotAction(context, intent.getStringExtra(GlobalScreenshot.EXTRA_ID), actionType, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void cancelScreenshotNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService("notification");
        nm.cancel(1);
    }
}
