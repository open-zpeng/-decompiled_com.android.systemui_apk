package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
/* loaded from: classes21.dex */
public class SmartReplyView extends ViewGroup {
    private static final int SQUEEZE_FAILED = -1;
    private static final String TAG = "SmartReplyView";
    private ActivityStarter mActivityStarter;
    private final BreakIterator mBreakIterator;
    private PriorityQueue<Button> mCandidateButtonQueueForSqueezing;
    private final SmartReplyConstants mConstants;
    private int mCurrentBackgroundColor;
    private final int mDefaultBackgroundColor;
    private final int mDefaultStrokeColor;
    private final int mDefaultTextColor;
    private final int mDefaultTextColorDarkBg;
    private final int mDoubleLineButtonPaddingHorizontal;
    private final int mHeightUpperLimit;
    private final KeyguardDismissUtil mKeyguardDismissUtil;
    private final double mMinStrokeContrast;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final int mRippleColor;
    private final int mRippleColorDarkBg;
    private final int mSingleLineButtonPaddingHorizontal;
    private final int mSingleToDoubleLineButtonWidthIncrease;
    private boolean mSmartRepliesGeneratedByAssistant;
    private View mSmartReplyContainer;
    private final int mSpacing;
    private final int mStrokeWidth;
    private static final int MEASURE_SPEC_ANY_LENGTH = View.MeasureSpec.makeMeasureSpec(0, 0);
    private static final Comparator<View> DECREASING_MEASURED_WIDTH_WITHOUT_PADDING_COMPARATOR = new Comparator() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyView$UA3QkbRzztEFRlbb86djKcGIV5E
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return SmartReplyView.lambda$static$0((View) obj, (View) obj2);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public enum SmartButtonType {
        REPLY,
        ACTION
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$static$0(View v1, View v2) {
        return ((v2.getMeasuredWidth() - v2.getPaddingLeft()) - v2.getPaddingRight()) - ((v1.getMeasuredWidth() - v1.getPaddingLeft()) - v1.getPaddingRight());
    }

    public SmartReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSmartRepliesGeneratedByAssistant = false;
        this.mConstants = (SmartReplyConstants) Dependency.get(SmartReplyConstants.class);
        this.mKeyguardDismissUtil = (KeyguardDismissUtil) Dependency.get(KeyguardDismissUtil.class);
        this.mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        this.mHeightUpperLimit = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.smart_reply_button_max_height);
        this.mCurrentBackgroundColor = context.getColor(R.color.smart_reply_button_background);
        this.mDefaultBackgroundColor = this.mCurrentBackgroundColor;
        this.mDefaultTextColor = this.mContext.getColor(R.color.smart_reply_button_text);
        this.mDefaultTextColorDarkBg = this.mContext.getColor(R.color.smart_reply_button_text_dark_bg);
        this.mDefaultStrokeColor = this.mContext.getColor(R.color.smart_reply_button_stroke);
        this.mRippleColor = this.mContext.getColor(R.color.notification_ripple_untinted_color);
        this.mRippleColorDarkBg = Color.argb(Color.alpha(this.mRippleColor), 255, 255, 255);
        this.mMinStrokeContrast = ContrastColorUtil.calculateContrast(this.mDefaultStrokeColor, this.mDefaultBackgroundColor);
        int spacing = 0;
        int singleLineButtonPaddingHorizontal = 0;
        int doubleLineButtonPaddingHorizontal = 0;
        int strokeWidth = 0;
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SmartReplyView, 0, 0);
        int length = arr.getIndexCount();
        for (int i = 0; i < length; i++) {
            int attr = arr.getIndex(i);
            if (attr == R.styleable.SmartReplyView_spacing) {
                spacing = arr.getDimensionPixelSize(i, 0);
            } else if (attr == R.styleable.SmartReplyView_singleLineButtonPaddingHorizontal) {
                singleLineButtonPaddingHorizontal = arr.getDimensionPixelSize(i, 0);
            } else if (attr == R.styleable.SmartReplyView_doubleLineButtonPaddingHorizontal) {
                doubleLineButtonPaddingHorizontal = arr.getDimensionPixelSize(i, 0);
            } else if (attr == R.styleable.SmartReplyView_buttonStrokeWidth) {
                strokeWidth = arr.getDimensionPixelSize(i, 0);
            }
        }
        arr.recycle();
        this.mStrokeWidth = strokeWidth;
        this.mSpacing = spacing;
        this.mSingleLineButtonPaddingHorizontal = singleLineButtonPaddingHorizontal;
        this.mDoubleLineButtonPaddingHorizontal = doubleLineButtonPaddingHorizontal;
        this.mSingleToDoubleLineButtonWidthIncrease = (doubleLineButtonPaddingHorizontal - singleLineButtonPaddingHorizontal) * 2;
        this.mBreakIterator = BreakIterator.getLineInstance();
        reallocateCandidateButtonQueueForSqueezing();
    }

    public int getHeightUpperLimit() {
        return this.mHeightUpperLimit;
    }

    private void reallocateCandidateButtonQueueForSqueezing() {
        this.mCandidateButtonQueueForSqueezing = new PriorityQueue<>(Math.max(getChildCount(), 1), DECREASING_MEASURED_WIDTH_WITHOUT_PADDING_COMPARATOR);
    }

    public void resetSmartSuggestions(View newSmartReplyContainer) {
        this.mSmartReplyContainer = newSmartReplyContainer;
        removeAllViews();
        this.mCurrentBackgroundColor = this.mDefaultBackgroundColor;
    }

    public void addPreInflatedButtons(List<Button> smartSuggestionButtons) {
        for (Button button : smartSuggestionButtons) {
            addView(button);
        }
        reallocateCandidateButtonQueueForSqueezing();
    }

    public List<Button> inflateRepliesFromRemoteInput(SmartReplies smartReplies, SmartReplyController smartReplyController, NotificationEntry entry, boolean delayOnClickListener) {
        List<Button> buttons = new ArrayList<>();
        if (smartReplies.remoteInput != null && smartReplies.pendingIntent != null && smartReplies.choices != null) {
            for (int i = 0; i < smartReplies.choices.length; i++) {
                buttons.add(inflateReplyButton(this, getContext(), i, smartReplies, smartReplyController, entry, delayOnClickListener));
            }
            this.mSmartRepliesGeneratedByAssistant = smartReplies.fromAssistant;
        }
        return buttons;
    }

    public List<Button> inflateSmartActions(Context packageContext, SmartActions smartActions, SmartReplyController smartReplyController, NotificationEntry entry, HeadsUpManager headsUpManager, boolean delayOnClickListener) {
        Context themedPackageContext = new ContextThemeWrapper(packageContext, this.mContext.getTheme());
        List<Button> buttons = new ArrayList<>();
        int numSmartActions = smartActions.actions.size();
        for (int n = 0; n < numSmartActions; n++) {
            Notification.Action action = smartActions.actions.get(n);
            if (action.actionIntent != null) {
                buttons.add(inflateActionButton(this, getContext(), themedPackageContext, n, smartActions, smartReplyController, entry, headsUpManager, delayOnClickListener));
            }
        }
        return buttons;
    }

    public static SmartReplyView inflate(Context context) {
        return (SmartReplyView) LayoutInflater.from(context).inflate(R.layout.smart_reply_view, (ViewGroup) null);
    }

    @VisibleForTesting
    static Button inflateReplyButton(final SmartReplyView smartReplyView, final Context context, final int replyIndex, final SmartReplies smartReplies, final SmartReplyController smartReplyController, final NotificationEntry entry, boolean useDelayedOnClickListener) {
        final Button b = (Button) LayoutInflater.from(context).inflate(R.layout.smart_reply_button, (ViewGroup) smartReplyView, false);
        final CharSequence choice = smartReplies.choices[replyIndex];
        b.setText(choice);
        final ActivityStarter.OnDismissAction action = new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyView$rVuoX0krA-dMy7xAwdbzCHW8AzI
            @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
            public final boolean onDismiss() {
                return SmartReplyView.lambda$inflateReplyButton$1(SmartReplyView.this, smartReplies, choice, replyIndex, b, smartReplyController, entry, context);
            }
        };
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyView$zCSq2JAz-cY64WTEY4XQsF-yGXs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SmartReplyView smartReplyView2 = SmartReplyView.this;
                ActivityStarter.OnDismissAction onDismissAction = action;
                NotificationEntry notificationEntry = entry;
                smartReplyView2.mKeyguardDismissUtil.executeWhenUnlocked(onDismissAction, !notificationEntry.isRowPinned());
            }
        };
        if (useDelayedOnClickListener) {
            onClickListener = new DelayedOnClickListener(onClickListener, smartReplyView.mConstants.getOnClickInitDelay());
        }
        b.setOnClickListener(onClickListener);
        b.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.systemui.statusbar.policy.SmartReplyView.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                String label = SmartReplyView.this.getResources().getString(R.string.accessibility_send_smart_reply);
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, label));
            }
        });
        setButtonColors(b, smartReplyView.mCurrentBackgroundColor, smartReplyView.mDefaultStrokeColor, smartReplyView.mDefaultTextColor, smartReplyView.mRippleColor, smartReplyView.mStrokeWidth);
        return b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$inflateReplyButton$1(SmartReplyView smartReplyView, SmartReplies smartReplies, CharSequence choice, int replyIndex, Button b, SmartReplyController smartReplyController, NotificationEntry entry, Context context) {
        if (smartReplyView.mConstants.getEffectiveEditChoicesBeforeSending(smartReplies.remoteInput.getEditChoicesBeforeSending())) {
            NotificationEntry.EditedSuggestionInfo editedSuggestionInfo = new NotificationEntry.EditedSuggestionInfo(choice, replyIndex);
            smartReplyView.mRemoteInputManager.activateRemoteInput(b, new RemoteInput[]{smartReplies.remoteInput}, smartReplies.remoteInput, smartReplies.pendingIntent, editedSuggestionInfo);
            return false;
        }
        smartReplyController.smartReplySent(entry, replyIndex, b.getText(), NotificationLogger.getNotificationLocation(entry).toMetricsEventEnum(), false);
        Bundle results = new Bundle();
        results.putString(smartReplies.remoteInput.getResultKey(), choice.toString());
        Intent intent = new Intent().addFlags(268435456);
        RemoteInput.addResultsToIntent(new RemoteInput[]{smartReplies.remoteInput}, intent, results);
        RemoteInput.setResultsSource(intent, 1);
        entry.setHasSentReply();
        try {
            try {
                smartReplies.pendingIntent.send(context, 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e = e;
                Log.w(TAG, "Unable to send smart reply", e);
                smartReplyView.mSmartReplyContainer.setVisibility(8);
                return false;
            }
        } catch (PendingIntent.CanceledException e2) {
            e = e2;
        }
        smartReplyView.mSmartReplyContainer.setVisibility(8);
        return false;
    }

    @VisibleForTesting
    static Button inflateActionButton(final SmartReplyView smartReplyView, Context context, Context packageContext, final int actionIndex, final SmartActions smartActions, final SmartReplyController smartReplyController, final NotificationEntry entry, final HeadsUpManager headsUpManager, boolean useDelayedOnClickListener) {
        View.OnClickListener onClickListener;
        final Notification.Action action = smartActions.actions.get(actionIndex);
        Button button = (Button) LayoutInflater.from(context).inflate(R.layout.smart_action_button, (ViewGroup) smartReplyView, false);
        button.setText(action.title);
        Drawable iconDrawable = action.getIcon().loadDrawable(packageContext);
        int newIconSize = context.getResources().getDimensionPixelSize(R.dimen.smart_action_button_icon_size);
        iconDrawable.setBounds(0, 0, newIconSize, newIconSize);
        button.setCompoundDrawables(iconDrawable, null, null, null);
        View.OnClickListener onClickListener2 = new View.OnClickListener() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyView$tct0o0Zp_9czv90IHtUOrdcaxl0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SmartReplyView.this.getActivityStarter().startPendingIntentDismissingKeyguard(r1.actionIntent, new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SmartReplyView$TA933H11Yl_oDGgX0f0ntr5xGgI
                    @Override // java.lang.Runnable
                    public final void run() {
                        SmartReplyView.lambda$inflateActionButton$3(SmartReplyController.this, r2, r3, r4, r5, r6);
                    }
                }, entry.getRow());
            }
        };
        if (!useDelayedOnClickListener) {
            onClickListener = onClickListener2;
        } else {
            onClickListener = new DelayedOnClickListener(onClickListener2, smartReplyView.mConstants.getOnClickInitDelay());
        }
        button.setOnClickListener(onClickListener);
        LayoutParams lp = (LayoutParams) button.getLayoutParams();
        lp.buttonType = SmartButtonType.ACTION;
        return button;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$inflateActionButton$3(SmartReplyController smartReplyController, NotificationEntry entry, int actionIndex, Notification.Action action, SmartActions smartActions, HeadsUpManager headsUpManager) {
        smartReplyController.smartActionClicked(entry, actionIndex, action, smartActions.fromAssistant);
        headsUpManager.removeNotification(entry.key, true);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.mContext, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        return new LayoutParams(params.width, params.height);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        List<View> smartActions;
        int maxNumActions;
        Iterator<View> it;
        int spacing;
        int targetWidth = View.MeasureSpec.getMode(widthMeasureSpec) == 0 ? Integer.MAX_VALUE : View.MeasureSpec.getSize(widthMeasureSpec);
        resetButtonsLayoutParams();
        if (!this.mCandidateButtonQueueForSqueezing.isEmpty()) {
            Log.wtf(TAG, "Single line button queue leaked between onMeasure calls");
            this.mCandidateButtonQueueForSqueezing.clear();
        }
        SmartSuggestionMeasures accumulatedMeasures = new SmartSuggestionMeasures(this.mPaddingLeft + this.mPaddingRight, 0, this.mSingleLineButtonPaddingHorizontal);
        int displayedChildCount = 0;
        List<View> smartActions2 = filterActionsOrReplies(SmartButtonType.ACTION);
        List<View> smartReplies = filterActionsOrReplies(SmartButtonType.REPLY);
        List<View> smartSuggestions = new ArrayList<>(smartActions2);
        smartSuggestions.addAll(smartReplies);
        List<View> coveredSuggestions = new ArrayList<>();
        SmartSuggestionMeasures actionsMeasures = null;
        int maxNumActions2 = this.mConstants.getMaxNumActions();
        int numShownActions = 0;
        Iterator<View> it2 = smartSuggestions.iterator();
        while (it2.hasNext()) {
            View child = it2.next();
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (maxNumActions2 == -1) {
                smartActions = smartActions2;
            } else {
                smartActions = smartActions2;
                if (lp.buttonType == SmartButtonType.ACTION && numShownActions >= maxNumActions2) {
                    maxNumActions = maxNumActions2;
                    it = it2;
                    smartActions2 = smartActions;
                    maxNumActions2 = maxNumActions;
                    it2 = it;
                }
            }
            int i = accumulatedMeasures.mButtonPaddingHorizontal;
            int paddingTop = child.getPaddingTop();
            maxNumActions = maxNumActions2;
            int maxNumActions3 = accumulatedMeasures.mButtonPaddingHorizontal;
            it = it2;
            child.setPadding(i, paddingTop, maxNumActions3, child.getPaddingBottom());
            child.measure(MEASURE_SPEC_ANY_LENGTH, heightMeasureSpec);
            coveredSuggestions.add(child);
            int lineCount = ((Button) child).getLineCount();
            if (lineCount >= 1 && lineCount <= 2) {
                if (lineCount == 1) {
                    this.mCandidateButtonQueueForSqueezing.add((Button) child);
                }
                SmartSuggestionMeasures originalMeasures = accumulatedMeasures.m33clone();
                if (actionsMeasures == null && lp.buttonType == SmartButtonType.REPLY) {
                    actionsMeasures = accumulatedMeasures.m33clone();
                }
                int spacing2 = displayedChildCount == 0 ? 0 : this.mSpacing;
                int childWidth = child.getMeasuredWidth();
                SmartSuggestionMeasures actionsMeasures2 = actionsMeasures;
                int childHeight = child.getMeasuredHeight();
                accumulatedMeasures.mMeasuredWidth += spacing2 + childWidth;
                accumulatedMeasures.mMaxChildHeight = Math.max(accumulatedMeasures.mMaxChildHeight, childHeight);
                int i2 = accumulatedMeasures.mButtonPaddingHorizontal;
                int childHeight2 = this.mSingleLineButtonPaddingHorizontal;
                boolean increaseToTwoLines = i2 == childHeight2 && (lineCount == 2 || accumulatedMeasures.mMeasuredWidth > targetWidth);
                if (increaseToTwoLines) {
                    accumulatedMeasures.mMeasuredWidth += (displayedChildCount + 1) * this.mSingleToDoubleLineButtonWidthIncrease;
                    accumulatedMeasures.mButtonPaddingHorizontal = this.mDoubleLineButtonPaddingHorizontal;
                }
                if (accumulatedMeasures.mMeasuredWidth > targetWidth) {
                    while (accumulatedMeasures.mMeasuredWidth > targetWidth && !this.mCandidateButtonQueueForSqueezing.isEmpty()) {
                        Button candidate = this.mCandidateButtonQueueForSqueezing.poll();
                        int squeezeReduction = squeezeButton(candidate, heightMeasureSpec);
                        boolean increaseToTwoLines2 = increaseToTwoLines;
                        if (squeezeReduction == -1) {
                            spacing = spacing2;
                        } else {
                            spacing = spacing2;
                            accumulatedMeasures.mMaxChildHeight = Math.max(accumulatedMeasures.mMaxChildHeight, candidate.getMeasuredHeight());
                            accumulatedMeasures.mMeasuredWidth -= squeezeReduction;
                        }
                        increaseToTwoLines = increaseToTwoLines2;
                        spacing2 = spacing;
                    }
                    if (accumulatedMeasures.mMeasuredWidth <= targetWidth) {
                        markButtonsWithPendingSqueezeStatusAs(2, coveredSuggestions);
                    } else {
                        accumulatedMeasures = originalMeasures;
                        markButtonsWithPendingSqueezeStatusAs(3, coveredSuggestions);
                        smartActions2 = smartActions;
                        maxNumActions2 = maxNumActions;
                        it2 = it;
                        actionsMeasures = actionsMeasures2;
                    }
                }
                lp.show = true;
                displayedChildCount++;
                if (lp.buttonType == SmartButtonType.ACTION) {
                    numShownActions++;
                }
                smartActions2 = smartActions;
                maxNumActions2 = maxNumActions;
                it2 = it;
                actionsMeasures = actionsMeasures2;
            }
            smartActions2 = smartActions;
            maxNumActions2 = maxNumActions;
            it2 = it;
        }
        if (this.mSmartRepliesGeneratedByAssistant && !gotEnoughSmartReplies(smartReplies)) {
            for (View smartReplyButton : smartReplies) {
                ((LayoutParams) smartReplyButton.getLayoutParams()).show = false;
            }
            accumulatedMeasures = actionsMeasures;
        }
        this.mCandidateButtonQueueForSqueezing.clear();
        remeasureButtonsIfNecessary(accumulatedMeasures.mButtonPaddingHorizontal, accumulatedMeasures.mMaxChildHeight);
        int buttonHeight = Math.max(getSuggestedMinimumHeight(), this.mPaddingTop + accumulatedMeasures.mMaxChildHeight + this.mPaddingBottom);
        for (View smartSuggestionButton : smartSuggestions) {
            setCornerRadius((Button) smartSuggestionButton, buttonHeight / 2.0f);
        }
        setMeasuredDimension(resolveSize(Math.max(getSuggestedMinimumWidth(), accumulatedMeasures.mMeasuredWidth), widthMeasureSpec), resolveSize(buttonHeight, heightMeasureSpec));
    }

    /* loaded from: classes21.dex */
    private static class SmartSuggestionMeasures {
        int mButtonPaddingHorizontal;
        int mMaxChildHeight;
        int mMeasuredWidth;

        SmartSuggestionMeasures(int measuredWidth, int maxChildHeight, int buttonPaddingHorizontal) {
            this.mMeasuredWidth = -1;
            this.mMaxChildHeight = -1;
            this.mButtonPaddingHorizontal = -1;
            this.mMeasuredWidth = measuredWidth;
            this.mMaxChildHeight = maxChildHeight;
            this.mButtonPaddingHorizontal = buttonPaddingHorizontal;
        }

        /* renamed from: clone */
        public SmartSuggestionMeasures m33clone() {
            return new SmartSuggestionMeasures(this.mMeasuredWidth, this.mMaxChildHeight, this.mButtonPaddingHorizontal);
        }
    }

    private boolean gotEnoughSmartReplies(List<View> smartReplies) {
        int numShownReplies = 0;
        for (View smartReplyButton : smartReplies) {
            LayoutParams lp = (LayoutParams) smartReplyButton.getLayoutParams();
            if (lp.show) {
                numShownReplies++;
            }
        }
        if (numShownReplies == 0 || numShownReplies >= this.mConstants.getMinNumSystemGeneratedReplies()) {
            return true;
        }
        return false;
    }

    private List<View> filterActionsOrReplies(SmartButtonType buttonType) {
        List<View> actions = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (child.getVisibility() == 0 && (child instanceof Button) && lp.buttonType == buttonType) {
                actions.add(child);
            }
        }
        return actions;
    }

    private void resetButtonsLayoutParams() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.show = false;
            lp.squeezeStatus = 0;
        }
    }

    private int squeezeButton(Button button, int heightMeasureSpec) {
        int estimatedOptimalTextWidth = estimateOptimalSqueezedButtonTextWidth(button);
        if (estimatedOptimalTextWidth == -1) {
            return -1;
        }
        return squeezeButtonToTextWidth(button, heightMeasureSpec, estimatedOptimalTextWidth);
    }

    private int estimateOptimalSqueezedButtonTextWidth(Button button) {
        boolean tooFar;
        String rawText = button.getText().toString();
        TransformationMethod transformation = button.getTransformationMethod();
        String text = transformation == null ? rawText : transformation.getTransformation(rawText, button).toString();
        int length = text.length();
        this.mBreakIterator.setText(text);
        int i = -1;
        if (this.mBreakIterator.preceding(length / 2) == -1 && this.mBreakIterator.next() == -1) {
            return -1;
        }
        TextPaint paint = button.getPaint();
        int initialPosition = this.mBreakIterator.current();
        int i2 = 0;
        float initialLeftTextWidth = Layout.getDesiredWidth(text, 0, initialPosition, paint);
        float initialRightTextWidth = Layout.getDesiredWidth(text, initialPosition, length, paint);
        float optimalTextWidth = Math.max(initialLeftTextWidth, initialRightTextWidth);
        if (initialLeftTextWidth != initialRightTextWidth) {
            boolean moveLeft = initialLeftTextWidth > initialRightTextWidth;
            int maxSqueezeRemeasureAttempts = this.mConstants.getMaxSqueezeRemeasureAttempts();
            float optimalTextWidth2 = optimalTextWidth;
            int i3 = 0;
            while (i3 < maxSqueezeRemeasureAttempts) {
                BreakIterator breakIterator = this.mBreakIterator;
                int newPosition = moveLeft ? breakIterator.previous() : breakIterator.next();
                if (newPosition != i) {
                    float newLeftTextWidth = Layout.getDesiredWidth(text, i2, newPosition, paint);
                    float newRightTextWidth = Layout.getDesiredWidth(text, newPosition, length, paint);
                    float newOptimalTextWidth = Math.max(newLeftTextWidth, newRightTextWidth);
                    if (newOptimalTextWidth >= optimalTextWidth2) {
                        break;
                    }
                    optimalTextWidth2 = newOptimalTextWidth;
                    if (moveLeft) {
                        tooFar = newLeftTextWidth <= newRightTextWidth;
                    } else {
                        tooFar = newLeftTextWidth >= newRightTextWidth;
                    }
                    if (!tooFar) {
                        i3++;
                        i = -1;
                        i2 = 0;
                    } else {
                        optimalTextWidth = optimalTextWidth2;
                        break;
                    }
                } else {
                    break;
                }
            }
            optimalTextWidth = optimalTextWidth2;
        }
        return (int) Math.ceil(optimalTextWidth);
    }

    private int getLeftCompoundDrawableWidthWithPadding(Button button) {
        Drawable[] drawables = button.getCompoundDrawables();
        Drawable leftDrawable = drawables[0];
        if (leftDrawable == null) {
            return 0;
        }
        return leftDrawable.getBounds().width() + button.getCompoundDrawablePadding();
    }

    private int squeezeButtonToTextWidth(Button button, int heightMeasureSpec, int textWidth) {
        int oldWidth = button.getMeasuredWidth();
        if (button.getPaddingLeft() != this.mDoubleLineButtonPaddingHorizontal) {
            oldWidth += this.mSingleToDoubleLineButtonWidthIncrease;
        }
        button.setPadding(this.mDoubleLineButtonPaddingHorizontal, button.getPaddingTop(), this.mDoubleLineButtonPaddingHorizontal, button.getPaddingBottom());
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((this.mDoubleLineButtonPaddingHorizontal * 2) + textWidth + getLeftCompoundDrawableWidthWithPadding(button), Integer.MIN_VALUE);
        button.measure(widthMeasureSpec, heightMeasureSpec);
        int newWidth = button.getMeasuredWidth();
        LayoutParams lp = (LayoutParams) button.getLayoutParams();
        if (button.getLineCount() > 2 || newWidth >= oldWidth) {
            lp.squeezeStatus = 3;
            return -1;
        }
        lp.squeezeStatus = 1;
        return oldWidth - newWidth;
    }

    private void remeasureButtonsIfNecessary(int buttonPaddingHorizontal, int maxChildHeight) {
        int maxChildHeightMeasure = View.MeasureSpec.makeMeasureSpec(maxChildHeight, 1073741824);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.show) {
                boolean requiresNewMeasure = false;
                int newWidth = child.getMeasuredWidth();
                if (lp.squeezeStatus == 3) {
                    requiresNewMeasure = true;
                    newWidth = Integer.MAX_VALUE;
                }
                if (child.getPaddingLeft() != buttonPaddingHorizontal) {
                    requiresNewMeasure = true;
                    if (newWidth != Integer.MAX_VALUE) {
                        if (buttonPaddingHorizontal == this.mSingleLineButtonPaddingHorizontal) {
                            newWidth -= this.mSingleToDoubleLineButtonWidthIncrease;
                        } else {
                            newWidth += this.mSingleToDoubleLineButtonWidthIncrease;
                        }
                    }
                    child.setPadding(buttonPaddingHorizontal, child.getPaddingTop(), buttonPaddingHorizontal, child.getPaddingBottom());
                }
                if (child.getMeasuredHeight() != maxChildHeight) {
                    requiresNewMeasure = true;
                }
                if (requiresNewMeasure) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(newWidth, Integer.MIN_VALUE), maxChildHeightMeasure);
                }
            }
        }
    }

    private void markButtonsWithPendingSqueezeStatusAs(int squeezeStatus, List<View> coveredChildren) {
        for (View child : coveredChildren) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.squeezeStatus == 1) {
                lp.squeezeStatus = squeezeStatus;
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        boolean isRtl = getLayoutDirection() == 1;
        int width = right - left;
        int position = isRtl ? width - this.mPaddingRight : this.mPaddingLeft;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.show) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                int childLeft = isRtl ? position - childWidth : position;
                child.layout(childLeft, 0, childLeft + childWidth, childHeight);
                int childWidthWithSpacing = this.mSpacing + childWidth;
                if (isRtl) {
                    position -= childWidthWithSpacing;
                } else {
                    position += childWidthWithSpacing;
                }
            }
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return lp.show && super.drawChild(canvas, child, drawingTime);
    }

    public void setBackgroundTintColor(int backgroundColor) {
        if (backgroundColor == this.mCurrentBackgroundColor) {
            return;
        }
        this.mCurrentBackgroundColor = backgroundColor;
        boolean dark = !ContrastColorUtil.isColorLight(backgroundColor);
        int textColor = ContrastColorUtil.ensureTextContrast(dark ? this.mDefaultTextColorDarkBg : this.mDefaultTextColor, backgroundColor | (-16777216), dark);
        int strokeColor = ContrastColorUtil.ensureContrast(this.mDefaultStrokeColor, (-16777216) | backgroundColor, dark, this.mMinStrokeContrast);
        int rippleColor = dark ? this.mRippleColorDarkBg : this.mRippleColor;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            Button child = (Button) getChildAt(i);
            setButtonColors(child, backgroundColor, strokeColor, textColor, rippleColor, this.mStrokeWidth);
        }
    }

    private static void setButtonColors(Button button, int backgroundColor, int strokeColor, int textColor, int rippleColor, int strokeWidth) {
        Drawable drawable = button.getBackground();
        if (drawable instanceof RippleDrawable) {
            Drawable drawable2 = drawable.mutate();
            RippleDrawable ripple = (RippleDrawable) drawable2;
            ripple.setColor(ColorStateList.valueOf(rippleColor));
            Drawable inset = ripple.getDrawable(0);
            if (inset instanceof InsetDrawable) {
                Drawable background = ((InsetDrawable) inset).getDrawable();
                if (background instanceof GradientDrawable) {
                    GradientDrawable gradientDrawable = (GradientDrawable) background;
                    gradientDrawable.setColor(backgroundColor);
                    gradientDrawable.setStroke(strokeWidth, strokeColor);
                }
            }
            button.setBackground(drawable2);
        }
        button.setTextColor(textColor);
    }

    private void setCornerRadius(Button button, float radius) {
        Drawable drawable = button.getBackground();
        if (drawable instanceof RippleDrawable) {
            RippleDrawable ripple = (RippleDrawable) drawable.mutate();
            Drawable inset = ripple.getDrawable(0);
            if (inset instanceof InsetDrawable) {
                Drawable background = ((InsetDrawable) inset).getDrawable();
                if (background instanceof GradientDrawable) {
                    GradientDrawable gradientDrawable = (GradientDrawable) background;
                    gradientDrawable.setCornerRadius(radius);
                }
            }
        }
    }

    private ActivityStarter getActivityStarter() {
        if (this.mActivityStarter == null) {
            this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        }
        return this.mActivityStarter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        private static final int SQUEEZE_STATUS_FAILED = 3;
        private static final int SQUEEZE_STATUS_NONE = 0;
        private static final int SQUEEZE_STATUS_PENDING = 1;
        private static final int SQUEEZE_STATUS_SUCCESSFUL = 2;
        private SmartButtonType buttonType;
        private boolean show;
        private int squeezeStatus;

        private LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.show = false;
            this.squeezeStatus = 0;
            this.buttonType = SmartButtonType.REPLY;
        }

        private LayoutParams(int width, int height) {
            super(width, height);
            this.show = false;
            this.squeezeStatus = 0;
            this.buttonType = SmartButtonType.REPLY;
        }

        @VisibleForTesting
        boolean isShown() {
            return this.show;
        }
    }

    /* loaded from: classes21.dex */
    public static class SmartReplies {
        public final CharSequence[] choices;
        public final boolean fromAssistant;
        public final PendingIntent pendingIntent;
        public final RemoteInput remoteInput;

        public SmartReplies(CharSequence[] choices, RemoteInput remoteInput, PendingIntent pendingIntent, boolean fromAssistant) {
            this.choices = choices;
            this.remoteInput = remoteInput;
            this.pendingIntent = pendingIntent;
            this.fromAssistant = fromAssistant;
        }
    }

    /* loaded from: classes21.dex */
    public static class SmartActions {
        public final List<Notification.Action> actions;
        public final boolean fromAssistant;

        public SmartActions(List<Notification.Action> actions, boolean fromAssistant) {
            this.actions = actions;
            this.fromAssistant = fromAssistant;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class DelayedOnClickListener implements View.OnClickListener {
        private final View.OnClickListener mActualListener;
        private final long mInitDelayMs;
        private final long mInitTimeMs = SystemClock.elapsedRealtime();

        DelayedOnClickListener(View.OnClickListener actualOnClickListener, long initDelayMs) {
            this.mActualListener = actualOnClickListener;
            this.mInitDelayMs = initDelayMs;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (hasFinishedInitialization()) {
                this.mActualListener.onClick(v);
                return;
            }
            Log.i(SmartReplyView.TAG, "Accidental Smart Suggestion click registered, delay: " + this.mInitDelayMs);
        }

        private boolean hasFinishedInitialization() {
            return SystemClock.elapsedRealtime() >= this.mInitTimeMs + this.mInitDelayMs;
        }
    }
}
