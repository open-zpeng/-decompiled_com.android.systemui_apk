package com.android.systemui.bubbles;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.ActivityView;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StatsLog;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.bubbles.BubbleExpandedView;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.statusbar.AlphaOptimizedButton;
/* loaded from: classes21.dex */
public class BubbleExpandedView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "Bubbles";
    private ActivityView mActivityView;
    private ActivityViewStatus mActivityViewStatus;
    private Drawable mAppIcon;
    private String mAppName;
    private Bubble mBubble;
    private BubbleController mBubbleController;
    private PendingIntent mBubbleIntent;
    private Point mDisplaySize;
    private int mExpandedViewTouchSlop;
    private boolean mKeyboardVisible;
    private int mMinHeight;
    private boolean mNeedsNewHeight;
    private PackageManager mPm;
    private ShapeDrawable mPointerDrawable;
    private int mPointerHeight;
    private int mPointerMargin;
    private View mPointerView;
    private int mPointerWidth;
    private AlphaOptimizedButton mSettingsIcon;
    private int mSettingsIconHeight;
    private BubbleStackView mStackView;
    private ActivityView.StateCallback mStateCallback;
    private int mTaskId;
    private int[] mTempLoc;
    private Rect mTempRect;
    private WindowManager mWindowManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public enum ActivityViewStatus {
        INITIALIZING,
        INITIALIZED,
        ACTIVITY_STARTED,
        RELEASED
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.bubbles.BubbleExpandedView$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends ActivityView.StateCallback {
        AnonymousClass1() {
        }

        public void onActivityViewReady(ActivityView view) {
            int i = AnonymousClass2.$SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[BubbleExpandedView.this.mActivityViewStatus.ordinal()];
            if (i == 1 || i == 2) {
                final ActivityOptions options = ActivityOptions.makeCustomAnimation(BubbleExpandedView.this.getContext(), 0, 0);
                BubbleExpandedView.this.post(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleExpandedView$1$g0YjNvBWtSGWit8uywvLlkarcag
                    @Override // java.lang.Runnable
                    public final void run() {
                        BubbleExpandedView.AnonymousClass1.this.lambda$onActivityViewReady$0$BubbleExpandedView$1(options);
                    }
                });
                BubbleExpandedView.this.mActivityViewStatus = ActivityViewStatus.ACTIVITY_STARTED;
            }
        }

        public /* synthetic */ void lambda$onActivityViewReady$0$BubbleExpandedView$1(ActivityOptions options) {
            try {
                BubbleExpandedView.this.mActivityView.startActivity(BubbleExpandedView.this.mBubbleIntent, options);
            } catch (RuntimeException e) {
                Log.w(BubbleExpandedView.TAG, "Exception while displaying bubble: " + BubbleExpandedView.this.getBubbleKey() + ", " + e.getMessage() + "; removing bubble");
                BubbleExpandedView.this.mBubbleController.removeBubble(BubbleExpandedView.this.mBubble.getKey(), 10);
            }
        }

        public void onActivityViewDestroyed(ActivityView view) {
            BubbleExpandedView.this.mActivityViewStatus = ActivityViewStatus.RELEASED;
        }

        public void onTaskCreated(int taskId, ComponentName componentName) {
            BubbleExpandedView.this.mTaskId = taskId;
        }

        public void onTaskRemovalStarted(int taskId) {
            if (BubbleExpandedView.this.mBubble != null) {
                BubbleExpandedView.this.post(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleExpandedView$1$wFmGYWDvx1tFURTJCp8j5qJlvAk
                    @Override // java.lang.Runnable
                    public final void run() {
                        BubbleExpandedView.AnonymousClass1.this.lambda$onTaskRemovalStarted$1$BubbleExpandedView$1();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onTaskRemovalStarted$1$BubbleExpandedView$1() {
            BubbleExpandedView.this.mBubbleController.removeBubble(BubbleExpandedView.this.mBubble.getKey(), 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.bubbles.BubbleExpandedView$2  reason: invalid class name */
    /* loaded from: classes21.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus = new int[ActivityViewStatus.values().length];

        static {
            try {
                $SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[ActivityViewStatus.INITIALIZING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[ActivityViewStatus.INITIALIZED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[ActivityViewStatus.ACTIVITY_STARTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public BubbleExpandedView(Context context) {
        this(context, null);
    }

    public BubbleExpandedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleExpandedView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BubbleExpandedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mActivityViewStatus = ActivityViewStatus.INITIALIZING;
        this.mTaskId = -1;
        this.mTempRect = new Rect();
        this.mTempLoc = new int[2];
        this.mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
        this.mStateCallback = new AnonymousClass1();
        this.mPm = context.getPackageManager();
        this.mDisplaySize = new Point();
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mWindowManager.getDefaultDisplay().getRealSize(this.mDisplaySize);
        Resources res = getResources();
        this.mMinHeight = res.getDimensionPixelSize(R.dimen.bubble_expanded_default_height);
        this.mPointerMargin = res.getDimensionPixelSize(R.dimen.bubble_pointer_margin);
        this.mExpandedViewTouchSlop = res.getDimensionPixelSize(R.dimen.bubble_expanded_view_slop);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        Resources res = getResources();
        this.mPointerView = findViewById(R.id.pointer_view);
        this.mPointerWidth = res.getDimensionPixelSize(R.dimen.bubble_pointer_width);
        this.mPointerHeight = res.getDimensionPixelSize(R.dimen.bubble_pointer_height);
        this.mPointerDrawable = new ShapeDrawable(TriangleShape.create(this.mPointerWidth, this.mPointerHeight, true));
        this.mPointerView.setBackground(this.mPointerDrawable);
        this.mPointerView.setVisibility(4);
        this.mSettingsIconHeight = getContext().getResources().getDimensionPixelSize(R.dimen.bubble_settings_size);
        this.mSettingsIcon = (AlphaOptimizedButton) findViewById(R.id.settings_button);
        this.mSettingsIcon.setOnClickListener(this);
        this.mActivityView = new ActivityView(this.mContext, (AttributeSet) null, 0, true);
        setContentVisibility(false);
        addView(this.mActivityView);
        bringChildToFront(this.mActivityView);
        bringChildToFront(this.mSettingsIcon);
        applyThemeAttrs();
        setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleExpandedView$BUIzmdcN6x4TJwxemNSjSITgNeY
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return BubbleExpandedView.this.lambda$onFinishInflate$0$BubbleExpandedView(view, windowInsets);
            }
        });
    }

    public /* synthetic */ WindowInsets lambda$onFinishInflate$0$BubbleExpandedView(View view, WindowInsets insets) {
        int keyboardHeight = insets.getSystemWindowInsetBottom() - insets.getStableInsetBottom();
        this.mKeyboardVisible = keyboardHeight != 0;
        if (!this.mKeyboardVisible && this.mNeedsNewHeight) {
            updateHeight();
        }
        return view.onApplyWindowInsets(insets);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getBubbleKey() {
        Bubble bubble = this.mBubble;
        return bubble != null ? bubble.getKey() : "null";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyThemeAttrs() {
        TypedArray ta = getContext().obtainStyledAttributes(R.styleable.BubbleExpandedView);
        int bgColor = ta.getColor(R.styleable.BubbleExpandedView_android_colorBackgroundFloating, -1);
        float cornerRadius = ta.getDimension(R.styleable.BubbleExpandedView_android_dialogCornerRadius, 0.0f);
        ta.recycle();
        this.mPointerDrawable.setTint(bgColor);
        if (ScreenDecorationsUtils.supportsRoundedCornersOnWindows(this.mContext.getResources())) {
            this.mActivityView.setCornerRadius(cornerRadius);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyboardVisible = false;
        this.mNeedsNewHeight = false;
        ActivityView activityView = this.mActivityView;
        if (activityView != null) {
            activityView.setForwardedInsets(Insets.of(0, 0, 0, 0));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContentVisibility(boolean visibility) {
        float alpha = visibility ? 1.0f : 0.0f;
        this.mPointerView.setAlpha(alpha);
        ActivityView activityView = this.mActivityView;
        if (activityView != null) {
            activityView.setAlpha(alpha);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateInsets(WindowInsets insets) {
        int i;
        if (usingActivityView()) {
            int[] screenLoc = this.mActivityView.getLocationOnScreen();
            int activityViewBottom = screenLoc[1] + this.mActivityView.getHeight();
            int i2 = this.mDisplaySize.y;
            int systemWindowInsetBottom = insets.getSystemWindowInsetBottom();
            if (insets.getDisplayCutout() != null) {
                i = insets.getDisplayCutout().getSafeInsetBottom();
            } else {
                i = 0;
            }
            int keyboardTop = i2 - Math.max(systemWindowInsetBottom, i);
            int insetsBottom = Math.max(activityViewBottom - keyboardTop, 0);
            this.mActivityView.setForwardedInsets(Insets.of(0, 0, 0, insetsBottom));
        }
    }

    public void setBubble(Bubble bubble, BubbleStackView stackView, String appName) {
        this.mStackView = stackView;
        this.mBubble = bubble;
        this.mAppName = appName;
        try {
            ApplicationInfo info = this.mPm.getApplicationInfo(bubble.getPackageName(), 795136);
            this.mAppIcon = this.mPm.getApplicationIcon(info);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (this.mAppIcon == null) {
            this.mAppIcon = this.mPm.getDefaultActivityIcon();
        }
        applyThemeAttrs();
        showSettingsIcon();
        updateExpandedView();
    }

    public void populateExpandedView() {
        if (usingActivityView()) {
            this.mActivityView.setCallback(this.mStateCallback);
        } else {
            Log.e(TAG, "Cannot populate expanded view.");
        }
    }

    public void update(Bubble bubble) {
        if (bubble.getKey().equals(this.mBubble.getKey())) {
            this.mBubble = bubble;
            updateSettingsContentDescription();
            updateHeight();
            return;
        }
        Log.w(TAG, "Trying to update entry with different key, new bubble: " + bubble.getKey() + " old bubble: " + bubble.getKey());
    }

    private void updateExpandedView() {
        this.mBubbleIntent = this.mBubble.getBubbleIntent(this.mContext);
        if (this.mBubbleIntent != null) {
            setContentVisibility(false);
            this.mActivityView.setVisibility(0);
        }
        updateView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean performBackPressIfNeeded() {
        if (!usingActivityView()) {
            return false;
        }
        this.mActivityView.performBackPress();
        return true;
    }

    void updateHeight() {
        if (usingActivityView()) {
            float desiredHeight = Math.max(this.mBubble.getDesiredHeight(this.mContext), this.mMinHeight);
            float height = Math.max(Math.min(desiredHeight, getMaxExpandedHeight()), this.mMinHeight);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mActivityView.getLayoutParams();
            this.mNeedsNewHeight = ((float) lp.height) != height;
            if (!this.mKeyboardVisible) {
                lp.height = (int) height;
                this.mActivityView.setLayoutParams(lp);
                this.mNeedsNewHeight = false;
            }
        }
    }

    private int getMaxExpandedHeight() {
        int bottomInset;
        this.mWindowManager.getDefaultDisplay().getRealSize(this.mDisplaySize);
        int[] windowLocation = this.mActivityView.getLocationOnScreen();
        if (getRootWindowInsets() != null) {
            bottomInset = getRootWindowInsets().getStableInsetBottom();
        } else {
            bottomInset = 0;
        }
        return ((((this.mDisplaySize.y - windowLocation[1]) - this.mSettingsIconHeight) - this.mPointerHeight) - this.mPointerMargin) - bottomInset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean intersectingTouchableContent(int rawX, int rawY) {
        this.mTempRect.setEmpty();
        ActivityView activityView = this.mActivityView;
        if (activityView != null) {
            this.mTempLoc = activityView.getLocationOnScreen();
            Rect rect = this.mTempRect;
            int[] iArr = this.mTempLoc;
            int i = iArr[0];
            int i2 = this.mExpandedViewTouchSlop;
            rect.set(i - i2, iArr[1] - i2, iArr[0] + this.mActivityView.getWidth() + this.mExpandedViewTouchSlop, this.mTempLoc[1] + this.mActivityView.getHeight() + this.mExpandedViewTouchSlop);
        }
        if (this.mTempRect.contains(rawX, rawY)) {
            return true;
        }
        this.mTempLoc = this.mSettingsIcon.getLocationOnScreen();
        Rect rect2 = this.mTempRect;
        int[] iArr2 = this.mTempLoc;
        rect2.set(iArr2[0], iArr2[1], iArr2[0] + this.mSettingsIcon.getWidth(), this.mTempLoc[1] + this.mSettingsIcon.getHeight());
        return this.mTempRect.contains(rawX, rawY);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mBubble == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.settings_button) {
            final Intent intent = this.mBubble.getSettingsIntent();
            this.mStackView.collapseStack(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleExpandedView$iBWZJs6SpKXryYoaz8vCiAaSUqI
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleExpandedView.this.lambda$onClick$1$BubbleExpandedView(intent);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onClick$1$BubbleExpandedView(Intent intent) {
        this.mContext.startActivityAsUser(intent, this.mBubble.getEntry().notification.getUser());
        logBubbleClickEvent(this.mBubble, 9);
    }

    private void updateSettingsContentDescription() {
        this.mSettingsIcon.setContentDescription(getResources().getString(R.string.bubbles_settings_button_description, this.mAppName));
    }

    void showSettingsIcon() {
        updateSettingsContentDescription();
        this.mSettingsIcon.setVisibility(0);
    }

    public void updateView() {
        if (usingActivityView() && this.mActivityView.getVisibility() == 0 && this.mActivityView.isAttachedToWindow()) {
            this.mActivityView.onLocationChanged();
        }
        updateHeight();
    }

    public void setPointerPosition(float x) {
        float halfPointerWidth = this.mPointerWidth / 2.0f;
        float pointerLeft = x - halfPointerWidth;
        this.mPointerView.setTranslationX(pointerLeft);
        this.mPointerView.setVisibility(0);
    }

    public void cleanUpExpandedState() {
        if (this.mActivityView == null) {
            return;
        }
        int i = AnonymousClass2.$SwitchMap$com$android$systemui$bubbles$BubbleExpandedView$ActivityViewStatus[this.mActivityViewStatus.ordinal()];
        if (i == 2 || i == 3) {
            this.mActivityView.release();
        }
        if (this.mTaskId != -1) {
            try {
                ActivityTaskManager.getService().removeTask(this.mTaskId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to remove taskId " + this.mTaskId);
            }
            this.mTaskId = -1;
        }
        removeView(this.mActivityView);
        this.mActivityView = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyDisplayEmpty() {
        if (this.mActivityViewStatus == ActivityViewStatus.ACTIVITY_STARTED) {
            this.mActivityViewStatus = ActivityViewStatus.INITIALIZED;
        }
    }

    private boolean usingActivityView() {
        return (this.mBubbleIntent == null || this.mActivityView == null) ? false : true;
    }

    public int getVirtualDisplayId() {
        if (usingActivityView()) {
            return this.mActivityView.getVirtualDisplayId();
        }
        return -1;
    }

    private void logBubbleClickEvent(Bubble bubble, int action) {
        StatusBarNotification notification = bubble.getEntry().notification;
        String packageName = notification.getPackageName();
        String channelId = notification.getNotification().getChannelId();
        int id = notification.getId();
        BubbleStackView bubbleStackView = this.mStackView;
        StatsLog.write(149, packageName, channelId, id, bubbleStackView.getBubbleIndex(bubbleStackView.getExpandedBubble()), this.mStackView.getBubbleCount(), action, this.mStackView.getNormalizedXPosition(), this.mStackView.getNormalizedYPosition(), bubble.showInShadeWhenBubble(), bubble.isOngoing(), false);
    }
}
