package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
/* loaded from: classes21.dex */
public class NotificationSnooze extends LinearLayout implements NotificationGuts.GutsContent, View.OnClickListener {
    private static final String KEY_DEFAULT_SNOOZE = "default";
    private static final String KEY_OPTIONS = "options_array";
    private static final int MAX_ASSISTANT_SUGGESTIONS = 1;
    private static final String TAG = "NotificationSnooze";
    private int mCollapsedHeight;
    private NotificationSwipeActionHelper.SnoozeOption mDefaultOption;
    private View mDivider;
    private AnimatorSet mExpandAnimation;
    private ImageView mExpandButton;
    private boolean mExpanded;
    private NotificationGuts mGutsContainer;
    private MetricsLogger mMetricsLogger;
    private KeyValueListParser mParser;
    private StatusBarNotification mSbn;
    private NotificationSwipeActionHelper.SnoozeOption mSelectedOption;
    private TextView mSelectedOptionText;
    private NotificationSwipeActionHelper mSnoozeListener;
    private ViewGroup mSnoozeOptionContainer;
    private List<NotificationSwipeActionHelper.SnoozeOption> mSnoozeOptions;
    private boolean mSnoozing;
    private TextView mUndoButton;
    private static final LogMaker OPTIONS_OPEN_LOG = new LogMaker(1142).setType(1);
    private static final LogMaker OPTIONS_CLOSE_LOG = new LogMaker(1142).setType(2);
    private static final LogMaker UNDO_LOG = new LogMaker(1141).setType(4);
    private static final int[] sAccessibilityActions = {R.id.action_snooze_shorter, R.id.action_snooze_short, R.id.action_snooze_long, R.id.action_snooze_longer};

    public NotificationSnooze(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMetricsLogger = new MetricsLogger();
        this.mParser = new KeyValueListParser(',');
    }

    @VisibleForTesting
    NotificationSwipeActionHelper.SnoozeOption getDefaultOption() {
        return this.mDefaultOption;
    }

    @VisibleForTesting
    void setKeyValueListParser(KeyValueListParser parser) {
        this.mParser = parser;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCollapsedHeight = getResources().getDimensionPixelSize(R.dimen.snooze_snackbar_min_height);
        findViewById(R.id.notification_snooze).setOnClickListener(this);
        this.mSelectedOptionText = (TextView) findViewById(R.id.snooze_option_default);
        this.mUndoButton = (TextView) findViewById(R.id.undo);
        this.mUndoButton.setOnClickListener(this);
        this.mExpandButton = (ImageView) findViewById(R.id.expand_button);
        this.mDivider = findViewById(R.id.divider);
        this.mDivider.setAlpha(0.0f);
        this.mSnoozeOptionContainer = (ViewGroup) findViewById(R.id.snooze_options);
        this.mSnoozeOptionContainer.setVisibility(4);
        this.mSnoozeOptionContainer.setAlpha(0.0f);
        this.mSnoozeOptions = getDefaultSnoozeOptions();
        createOptionViews();
        setSelected(this.mDefaultOption, false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        logOptionSelection(1137, this.mDefaultOption);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        NotificationGuts notificationGuts = this.mGutsContainer;
        if (notificationGuts != null && notificationGuts.isExposed() && event.getEventType() == 32) {
            event.getText().add(this.mSelectedOptionText.getText());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.id.action_snooze_undo, getResources().getString(R.string.snooze_undo)));
        int count = this.mSnoozeOptions.size();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo.AccessibilityAction action = this.mSnoozeOptions.get(i).getAccessibilityAction();
            if (action != null) {
                info.addAction(action);
            }
        }
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (super.performAccessibilityActionInternal(action, arguments)) {
            return true;
        }
        if (action == R.id.action_snooze_undo) {
            undoSnooze(this.mUndoButton);
            return true;
        }
        for (int i = 0; i < this.mSnoozeOptions.size(); i++) {
            NotificationSwipeActionHelper.SnoozeOption so = this.mSnoozeOptions.get(i);
            if (so.getAccessibilityAction() != null && so.getAccessibilityAction().getId() == action) {
                setSelected(so, true);
                return true;
            }
        }
        return false;
    }

    public void setSnoozeOptions(List<SnoozeCriterion> snoozeList) {
        if (snoozeList == null) {
            return;
        }
        this.mSnoozeOptions.clear();
        this.mSnoozeOptions = getDefaultSnoozeOptions();
        int count = Math.min(1, snoozeList.size());
        for (int i = 0; i < count; i++) {
            SnoozeCriterion sc = snoozeList.get(i);
            AccessibilityNodeInfo.AccessibilityAction action = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_snooze_assistant_suggestion_1, sc.getExplanation());
            this.mSnoozeOptions.add(new NotificationSnoozeOption(sc, 0, sc.getExplanation(), sc.getConfirmation(), action));
        }
        createOptionViews();
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setSnoozeListener(NotificationSwipeActionHelper listener) {
        this.mSnoozeListener = listener;
    }

    public void setStatusBarNotification(StatusBarNotification sbn) {
        this.mSbn = sbn;
    }

    @VisibleForTesting
    ArrayList<NotificationSwipeActionHelper.SnoozeOption> getDefaultSnoozeOptions() {
        Resources resources = getContext().getResources();
        ArrayList<NotificationSwipeActionHelper.SnoozeOption> options = new ArrayList<>();
        try {
            String config = Settings.Global.getString(getContext().getContentResolver(), "notification_snooze_options");
            this.mParser.setString(config);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Bad snooze constants");
        }
        int defaultSnooze = this.mParser.getInt(KEY_DEFAULT_SNOOZE, resources.getInteger(R.integer.config_notification_snooze_time_default));
        int[] snoozeTimes = this.mParser.getIntArray(KEY_OPTIONS, resources.getIntArray(R.array.config_notification_snooze_times));
        for (int i = 0; i < snoozeTimes.length; i++) {
            int[] iArr = sAccessibilityActions;
            if (i >= iArr.length) {
                break;
            }
            int snoozeTime = snoozeTimes[i];
            NotificationSwipeActionHelper.SnoozeOption option = createOption(snoozeTime, iArr[i]);
            if (i == 0 || snoozeTime == defaultSnooze) {
                this.mDefaultOption = option;
            }
            options.add(option);
        }
        return options;
    }

    private NotificationSwipeActionHelper.SnoozeOption createOption(int minutes, int accessibilityActionId) {
        int i;
        Resources res = getResources();
        boolean showInHours = minutes >= 60;
        if (showInHours) {
            i = R.plurals.snoozeHourOptions;
        } else {
            i = R.plurals.snoozeMinuteOptions;
        }
        int pluralResId = i;
        int count = showInHours ? minutes / 60 : minutes;
        String description = res.getQuantityString(pluralResId, count, Integer.valueOf(count));
        String resultText = String.format(res.getString(R.string.snoozed_for_time), description);
        AccessibilityNodeInfo.AccessibilityAction action = new AccessibilityNodeInfo.AccessibilityAction(accessibilityActionId, description);
        int index = resultText.indexOf(description);
        if (index == -1) {
            return new NotificationSnoozeOption(null, minutes, description, resultText, action);
        }
        SpannableString string = new SpannableString(resultText);
        string.setSpan(new StyleSpan(1), index, description.length() + index, 0);
        return new NotificationSnoozeOption(null, minutes, description, string, action);
    }

    private void createOptionViews() {
        this.mSnoozeOptionContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        for (int i = 0; i < this.mSnoozeOptions.size(); i++) {
            NotificationSwipeActionHelper.SnoozeOption option = this.mSnoozeOptions.get(i);
            TextView tv = (TextView) inflater.inflate(R.layout.notification_snooze_option, this.mSnoozeOptionContainer, false);
            this.mSnoozeOptionContainer.addView(tv);
            tv.setText(option.getDescription());
            tv.setTag(option);
            tv.setOnClickListener(this);
        }
    }

    private void hideSelectedOption() {
        int childCount = this.mSnoozeOptionContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mSnoozeOptionContainer.getChildAt(i);
            child.setVisibility(child.getTag() == this.mSelectedOption ? 8 : 0);
        }
    }

    private void showSnoozeOptions(boolean show) {
        int drawableId = show ? 17302354 : 17302413;
        this.mExpandButton.setImageResource(drawableId);
        if (this.mExpanded != show) {
            this.mExpanded = show;
            animateSnoozeOptions(show);
            NotificationGuts notificationGuts = this.mGutsContainer;
            if (notificationGuts != null) {
                notificationGuts.onHeightChanged();
            }
        }
    }

    private void animateSnoozeOptions(final boolean show) {
        AnimatorSet animatorSet = this.mExpandAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        View view = this.mDivider;
        Property property = View.ALPHA;
        float[] fArr = new float[2];
        fArr[0] = this.mDivider.getAlpha();
        fArr[1] = show ? 1.0f : 0.0f;
        ObjectAnimator dividerAnim = ObjectAnimator.ofFloat(view, property, fArr);
        ViewGroup viewGroup = this.mSnoozeOptionContainer;
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[2];
        fArr2[0] = this.mSnoozeOptionContainer.getAlpha();
        fArr2[1] = show ? 1.0f : 0.0f;
        ObjectAnimator optionAnim = ObjectAnimator.ofFloat(viewGroup, property2, fArr2);
        this.mSnoozeOptionContainer.setVisibility(0);
        this.mExpandAnimation = new AnimatorSet();
        this.mExpandAnimation.playTogether(dividerAnim, optionAnim);
        this.mExpandAnimation.setDuration(150L);
        this.mExpandAnimation.setInterpolator(show ? Interpolators.ALPHA_IN : Interpolators.ALPHA_OUT);
        this.mExpandAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.NotificationSnooze.1
            boolean cancelled = false;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.cancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!show && !this.cancelled) {
                    NotificationSnooze.this.mSnoozeOptionContainer.setVisibility(4);
                    NotificationSnooze.this.mSnoozeOptionContainer.setAlpha(0.0f);
                }
            }
        });
        this.mExpandAnimation.start();
    }

    private void setSelected(NotificationSwipeActionHelper.SnoozeOption option, boolean userAction) {
        this.mSelectedOption = option;
        this.mSelectedOptionText.setText(option.getConfirmation());
        showSnoozeOptions(false);
        hideSelectedOption();
        sendAccessibilityEvent(32);
        if (userAction) {
            logOptionSelection(1138, option);
        }
    }

    private void logOptionSelection(int category, NotificationSwipeActionHelper.SnoozeOption option) {
        int index = this.mSnoozeOptions.indexOf(option);
        long duration = TimeUnit.MINUTES.toMillis(option.getMinutesToSnoozeFor());
        this.mMetricsLogger.write(new LogMaker(category).setType(4).addTaggedData(1140, Integer.valueOf(index)).addTaggedData(1139, Long.valueOf(duration)));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        NotificationGuts notificationGuts = this.mGutsContainer;
        if (notificationGuts != null) {
            notificationGuts.resetFalsingCheck();
        }
        int id = v.getId();
        NotificationSwipeActionHelper.SnoozeOption tag = (NotificationSwipeActionHelper.SnoozeOption) v.getTag();
        if (tag != null) {
            setSelected(tag, true);
        } else if (id == R.id.notification_snooze) {
            showSnoozeOptions(true ^ this.mExpanded);
            this.mMetricsLogger.write(!this.mExpanded ? OPTIONS_OPEN_LOG : OPTIONS_CLOSE_LOG);
        } else {
            undoSnooze(v);
            this.mMetricsLogger.write(UNDO_LOG);
        }
    }

    private void undoSnooze(View v) {
        this.mSelectedOption = null;
        int[] parentLoc = new int[2];
        int[] targetLoc = new int[2];
        this.mGutsContainer.getLocationOnScreen(parentLoc);
        v.getLocationOnScreen(targetLoc);
        int centerX = v.getWidth() / 2;
        int centerY = v.getHeight() / 2;
        int x = (targetLoc[0] - parentLoc[0]) + centerX;
        int y = (targetLoc[1] - parentLoc[1]) + centerY;
        showSnoozeOptions(false);
        this.mGutsContainer.closeControls(x, y, false, false);
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public int getActualHeight() {
        return this.mExpanded ? getHeight() : this.mCollapsedHeight;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean willBeRemoved() {
        return this.mSnoozing;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public View getContentView() {
        setSelected(this.mDefaultOption, false);
        return this;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public void setGutsParent(NotificationGuts guts) {
        this.mGutsContainer = guts;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean handleCloseControls(boolean save, boolean force) {
        NotificationSwipeActionHelper.SnoozeOption snoozeOption;
        if (this.mExpanded && !force) {
            showSnoozeOptions(false);
            return true;
        }
        NotificationSwipeActionHelper notificationSwipeActionHelper = this.mSnoozeListener;
        if (notificationSwipeActionHelper != null && (snoozeOption = this.mSelectedOption) != null) {
            this.mSnoozing = true;
            notificationSwipeActionHelper.snooze(this.mSbn, snoozeOption);
            return true;
        }
        setSelected(this.mSnoozeOptions.get(0), false);
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean isLeavebehind() {
        return true;
    }

    @Override // com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent
    public boolean shouldBeSaved() {
        return true;
    }

    /* loaded from: classes21.dex */
    public class NotificationSnoozeOption implements NotificationSwipeActionHelper.SnoozeOption {
        private AccessibilityNodeInfo.AccessibilityAction mAction;
        private CharSequence mConfirmation;
        private SnoozeCriterion mCriterion;
        private CharSequence mDescription;
        private int mMinutesToSnoozeFor;

        public NotificationSnoozeOption(SnoozeCriterion sc, int minToSnoozeFor, CharSequence description, CharSequence confirmation, AccessibilityNodeInfo.AccessibilityAction action) {
            this.mCriterion = sc;
            this.mMinutesToSnoozeFor = minToSnoozeFor;
            this.mDescription = description;
            this.mConfirmation = confirmation;
            this.mAction = action;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption
        public SnoozeCriterion getSnoozeCriterion() {
            return this.mCriterion;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption
        public CharSequence getDescription() {
            return this.mDescription;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption
        public CharSequence getConfirmation() {
            return this.mConfirmation;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption
        public int getMinutesToSnoozeFor() {
            return this.mMinutesToSnoozeFor;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption
        public AccessibilityNodeInfo.AccessibilityAction getAccessibilityAction() {
            return this.mAction;
        }
    }
}
