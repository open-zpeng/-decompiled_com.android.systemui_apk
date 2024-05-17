package com.android.systemui.statusbar.notification.collection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Person;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes21.dex */
public final class NotificationEntry {
    private static final int COLOR_INVALID = 1;
    private static final long INITIALIZATION_DELAY = 400;
    private static final long LAUNCH_COOLDOWN = 2000;
    private static final long NOT_LAUNCHED_YET = -2000;
    private static final long REMOTE_INPUT_COOLDOWN = 500;
    public boolean ambient;
    public StatusBarIconView aodIcon;
    public boolean autoRedacted;
    public boolean canBubble;
    public StatusBarIconView centeredIcon;
    public NotificationChannel channel;
    public EditedSuggestionInfo editedSuggestionInfo;
    public StatusBarIconView expandedIcon;
    private boolean hasSentReply;
    public CharSequence headsUpStatusBarText;
    public CharSequence headsUpStatusBarTextPublic;
    public StatusBarIconView icon;
    public int importance;
    private long initializationTime;
    private boolean interruption;
    public boolean isVisuallyInterruptive;
    public final String key;
    public long lastAudiblyAlertedMs;
    private long lastFullScreenIntentLaunchTime;
    public long lastRemoteInputSent;
    public ArraySet<Integer> mActiveAppOps;
    private boolean mAutoHeadsUp;
    private int mCachedContrastColor;
    private int mCachedContrastColorIsFor;
    private Throwable mDebugThrowable;
    private boolean mHighPriority;
    public Boolean mIsSystemNotification;
    private boolean mIsTopBucket;
    private Runnable mOnSensitiveChangedListener;
    private boolean mPulseSupressed;
    private InflationTask mRunningTask;
    private boolean mSensitive;
    public boolean noisy;
    public StatusBarNotification notification;
    private NotificationEntry parent;
    public CharSequence remoteInputText;
    public CharSequence remoteInputTextWhenReset;
    private ExpandableNotificationRow row;
    public List<SnoozeCriterion> snoozeCriteria;
    @VisibleForTesting
    public int suppressedVisualEffects;
    public boolean suspended;
    public List<Notification.Action> systemGeneratedSmartActions;
    public CharSequence[] systemGeneratedSmartReplies;
    public int targetSdk;
    public int userSentiment;

    public NotificationEntry(StatusBarNotification n) {
        this(n, null);
    }

    public NotificationEntry(StatusBarNotification n, @Nullable NotificationListenerService.Ranking ranking) {
        this.lastFullScreenIntentLaunchTime = NOT_LAUNCHED_YET;
        this.userSentiment = 0;
        this.systemGeneratedSmartActions = Collections.emptyList();
        this.systemGeneratedSmartReplies = new CharSequence[0];
        this.mCachedContrastColor = 1;
        this.mCachedContrastColorIsFor = 1;
        this.mRunningTask = null;
        this.lastRemoteInputSent = NOT_LAUNCHED_YET;
        this.mActiveAppOps = new ArraySet<>(3);
        this.initializationTime = -1L;
        this.mSensitive = true;
        this.key = n.getKey();
        this.notification = n;
        if (ranking != null) {
            populateFromRanking(ranking);
        }
    }

    public void populateFromRanking(NotificationListenerService.Ranking ranking) {
        CharSequence[] charSequenceArr;
        this.channel = ranking.getChannel();
        this.lastAudiblyAlertedMs = ranking.getLastAudiblyAlertedMillis();
        this.importance = ranking.getImportance();
        this.ambient = ranking.isAmbient();
        this.snoozeCriteria = ranking.getSnoozeCriteria();
        this.userSentiment = ranking.getUserSentiment();
        this.systemGeneratedSmartActions = ranking.getSmartActions() == null ? Collections.emptyList() : ranking.getSmartActions();
        if (ranking.getSmartReplies() == null) {
            charSequenceArr = new CharSequence[0];
        } else {
            charSequenceArr = (CharSequence[]) ranking.getSmartReplies().toArray(new CharSequence[0]);
        }
        this.systemGeneratedSmartReplies = charSequenceArr;
        this.suppressedVisualEffects = ranking.getSuppressedVisualEffects();
        this.suspended = ranking.isSuspended();
        this.canBubble = ranking.canBubble();
        this.isVisuallyInterruptive = ranking.visuallyInterruptive();
    }

    public void setInterruption() {
        this.interruption = true;
    }

    public boolean hasInterrupted() {
        return this.interruption;
    }

    public boolean isHighPriority() {
        return this.mHighPriority;
    }

    public void setIsHighPriority(boolean highPriority) {
        this.mHighPriority = highPriority;
    }

    public boolean isTopBucket() {
        return this.mIsTopBucket;
    }

    public void setIsTopBucket(boolean isTopBucket) {
        this.mIsTopBucket = isTopBucket;
    }

    public boolean isBubble() {
        return (this.notification.getNotification().flags & 4096) != 0;
    }

    public Notification.BubbleMetadata getBubbleMetadata() {
        return this.notification.getNotification().getBubbleMetadata();
    }

    public void reset() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.reset();
        }
    }

    public ExpandableNotificationRow getRow() {
        return this.row;
    }

    public void setRow(ExpandableNotificationRow row) {
        this.row = row;
    }

    @Nullable
    public List<NotificationEntry> getChildren() {
        List<ExpandableNotificationRow> rowChildren;
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow == null || (rowChildren = expandableNotificationRow.getNotificationChildren()) == null) {
            return null;
        }
        ArrayList<NotificationEntry> children = new ArrayList<>();
        for (ExpandableNotificationRow child : rowChildren) {
            children.add(child.getEntry());
        }
        return children;
    }

    public void notifyFullScreenIntentLaunched() {
        setInterruption();
        this.lastFullScreenIntentLaunchTime = SystemClock.elapsedRealtime();
    }

    public boolean hasJustLaunchedFullScreenIntent() {
        return SystemClock.elapsedRealtime() < this.lastFullScreenIntentLaunchTime + 2000;
    }

    public boolean hasJustSentRemoteInput() {
        return SystemClock.elapsedRealtime() < this.lastRemoteInputSent + 500;
    }

    public boolean hasFinishedInitialization() {
        return this.initializationTime == -1 || SystemClock.elapsedRealtime() > this.initializationTime + INITIALIZATION_DELAY;
    }

    public void createIcons(Context context, StatusBarNotification sbn) throws InflationException {
        Notification n = sbn.getNotification();
        Icon smallIcon = n.getSmallIcon();
        if (smallIcon == null) {
            throw new InflationException("No small icon in notification from " + sbn.getPackageName());
        }
        this.icon = new StatusBarIconView(context, sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId()), sbn);
        this.icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.expandedIcon = new StatusBarIconView(context, sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId()), sbn);
        this.expandedIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.aodIcon = new StatusBarIconView(context, sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId()), sbn);
        this.aodIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.aodIcon.setIncreasedSize(true);
        StatusBarIcon ic = new StatusBarIcon(sbn.getUser(), sbn.getPackageName(), smallIcon, n.iconLevel, n.number, StatusBarIconView.contentDescForNotification(context, n));
        if (!this.icon.set(ic) || !this.expandedIcon.set(ic) || !this.aodIcon.set(ic)) {
            this.icon = null;
            this.expandedIcon = null;
            this.centeredIcon = null;
            this.aodIcon = null;
            throw new InflationException("Couldn't create icon: " + ic);
        }
        this.expandedIcon.setVisibility(4);
        this.expandedIcon.setOnVisibilityChangedListener(new StatusBarIconView.OnVisibilityChangedListener() { // from class: com.android.systemui.statusbar.notification.collection.-$$Lambda$NotificationEntry$-qQWpuXv2gxu8--zPidD9i3gPVE
            @Override // com.android.systemui.statusbar.StatusBarIconView.OnVisibilityChangedListener
            public final void onVisibilityChanged(int i) {
                NotificationEntry.this.lambda$createIcons$0$NotificationEntry(i);
            }
        });
        if (this.notification.getNotification().isMediaNotification()) {
            this.centeredIcon = new StatusBarIconView(context, sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId()), sbn);
            this.centeredIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (!this.centeredIcon.set(ic)) {
                this.centeredIcon = null;
                throw new InflationException("Couldn't update centered icon: " + ic);
            }
        }
    }

    public /* synthetic */ void lambda$createIcons$0$NotificationEntry(int newVisibility) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setIconsVisible(newVisibility != 0);
        }
    }

    public void setIconTag(int key, Object tag) {
        StatusBarIconView statusBarIconView = this.icon;
        if (statusBarIconView != null) {
            statusBarIconView.setTag(key, tag);
            this.expandedIcon.setTag(key, tag);
        }
        StatusBarIconView statusBarIconView2 = this.centeredIcon;
        if (statusBarIconView2 != null) {
            statusBarIconView2.setTag(key, tag);
        }
        StatusBarIconView statusBarIconView3 = this.aodIcon;
        if (statusBarIconView3 != null) {
            statusBarIconView3.setTag(key, tag);
        }
    }

    public void updateIcons(Context context, StatusBarNotification sbn) throws InflationException {
        if (this.icon != null) {
            Notification n = sbn.getNotification();
            StatusBarIcon ic = new StatusBarIcon(this.notification.getUser(), this.notification.getPackageName(), n.getSmallIcon(), n.iconLevel, n.number, StatusBarIconView.contentDescForNotification(context, n));
            this.icon.setNotification(sbn);
            this.expandedIcon.setNotification(sbn);
            this.aodIcon.setNotification(sbn);
            if (!this.icon.set(ic) || !this.expandedIcon.set(ic) || !this.aodIcon.set(ic)) {
                throw new InflationException("Couldn't update icon: " + ic);
            }
            StatusBarIconView statusBarIconView = this.centeredIcon;
            if (statusBarIconView != null) {
                statusBarIconView.setNotification(sbn);
                if (!this.centeredIcon.set(ic)) {
                    throw new InflationException("Couldn't update centered icon: " + ic);
                }
            }
        }
    }

    public int getContrastedColor(Context context, boolean isLowPriority, int backgroundColor) {
        int i;
        int rawColor = isLowPriority ? 0 : this.notification.getNotification().color;
        if (this.mCachedContrastColorIsFor == rawColor && (i = this.mCachedContrastColor) != 1) {
            return i;
        }
        int contrasted = ContrastColorUtil.resolveContrastColor(context, rawColor, backgroundColor);
        this.mCachedContrastColorIsFor = rawColor;
        this.mCachedContrastColor = contrasted;
        return this.mCachedContrastColor;
    }

    public void abortTask() {
        InflationTask inflationTask = this.mRunningTask;
        if (inflationTask != null) {
            inflationTask.abort();
            this.mRunningTask = null;
        }
    }

    public void setInflationTask(InflationTask abortableTask) {
        InflationTask inflationTask;
        InflationTask existing = this.mRunningTask;
        abortTask();
        this.mRunningTask = abortableTask;
        if (existing != null && (inflationTask = this.mRunningTask) != null) {
            inflationTask.supersedeTask(existing);
        }
    }

    public void onInflationTaskFinished() {
        this.mRunningTask = null;
    }

    @VisibleForTesting
    public InflationTask getRunningTask() {
        return this.mRunningTask;
    }

    public void setDebugThrowable(Throwable debugThrowable) {
        this.mDebugThrowable = debugThrowable;
    }

    public Throwable getDebugThrowable() {
        return this.mDebugThrowable;
    }

    public void onRemoteInputInserted() {
        this.lastRemoteInputSent = NOT_LAUNCHED_YET;
        this.remoteInputTextWhenReset = null;
    }

    public void setHasSentReply() {
        this.hasSentReply = true;
    }

    public boolean isLastMessageFromReply() {
        Notification.MessagingStyle.Message lastMessage;
        if (this.hasSentReply) {
            Bundle extras = this.notification.getNotification().extras;
            CharSequence[] replyTexts = extras.getCharSequenceArray("android.remoteInputHistory");
            if (ArrayUtils.isEmpty(replyTexts)) {
                Parcelable[] messages = extras.getParcelableArray("android.messages");
                if (messages != null && messages.length > 0) {
                    Parcelable message = messages[messages.length - 1];
                    if ((message instanceof Bundle) && (lastMessage = Notification.MessagingStyle.Message.getMessageFromBundle((Bundle) message)) != null) {
                        Person senderPerson = lastMessage.getSenderPerson();
                        if (senderPerson == null) {
                            return true;
                        }
                        Person user = (Person) extras.getParcelable("android.messagingUser");
                        return Objects.equals(user, senderPerson);
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public void setInitializationTime(long time) {
        if (this.initializationTime == -1) {
            this.initializationTime = time;
        }
    }

    public void sendAccessibilityEvent(int eventType) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.sendAccessibilityEvent(eventType);
        }
    }

    public boolean isMediaNotification() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow == null) {
            return false;
        }
        return expandableNotificationRow.isMediaRow();
    }

    public boolean isTopLevelChild() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isTopLevelChild();
    }

    public void resetUserExpansion() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.resetUserExpansion();
        }
    }

    public void freeContentViewWhenSafe(int inflationFlag) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.freeContentViewWhenSafe(inflationFlag);
        }
    }

    public boolean rowExists() {
        return this.row != null;
    }

    public boolean isRowDismissed() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isDismissed();
    }

    public boolean isRowRemoved() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isRemoved();
    }

    public boolean isRemoved() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow == null || expandableNotificationRow.isRemoved();
    }

    public boolean isRowPinned() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isPinned();
    }

    public void setRowPinned(boolean pinned) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setPinned(pinned);
        }
    }

    public boolean isRowHeadsUp() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isHeadsUp();
    }

    public boolean showingPulsing() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.showingPulsing();
    }

    public void setHeadsUp(boolean shouldHeadsUp) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setHeadsUp(shouldHeadsUp);
        }
    }

    public void setHeadsUpAnimatingAway(boolean animatingAway) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setHeadsUpAnimatingAway(animatingAway);
        }
    }

    public void setAutoHeadsUp(boolean autoHeadsUp) {
        this.mAutoHeadsUp = autoHeadsUp;
    }

    public boolean isAutoHeadsUp() {
        return this.mAutoHeadsUp;
    }

    public boolean mustStayOnScreen() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.mustStayOnScreen();
    }

    public void setHeadsUpIsVisible() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setHeadsUpIsVisible();
        }
    }

    public ExpandableNotificationRow getHeadsUpAnimationView() {
        return this.row;
    }

    public void setUserLocked(boolean userLocked) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setUserLocked(userLocked);
        }
    }

    public void setUserExpanded(boolean userExpanded, boolean allowChildExpansion) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setUserExpanded(userExpanded, allowChildExpansion);
        }
    }

    public void setGroupExpansionChanging(boolean changing) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setGroupExpansionChanging(changing);
        }
    }

    public void notifyHeightChanged(boolean needsAnimation) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.notifyHeightChanged(needsAnimation);
        }
    }

    public void closeRemoteInput() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.closeRemoteInput();
        }
    }

    public boolean areChildrenExpanded() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.areChildrenExpanded();
    }

    public boolean keepInParent() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.keepInParent();
    }

    public boolean isGroupNotFullyVisible() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow == null || expandableNotificationRow.isGroupNotFullyVisible();
    }

    public NotificationGuts getGuts() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            return expandableNotificationRow.getGuts();
        }
        return null;
    }

    public void removeRow() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setRemoved();
        }
    }

    public boolean isSummaryWithChildren() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return expandableNotificationRow != null && expandableNotificationRow.isSummaryWithChildren();
    }

    public void setKeepInParent(boolean keep) {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.setKeepInParent(keep);
        }
    }

    public void onDensityOrFontScaleChanged() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.onDensityOrFontScaleChanged();
        }
    }

    public boolean areGutsExposed() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        return (expandableNotificationRow == null || expandableNotificationRow.getGuts() == null || !this.row.getGuts().isExposed()) ? false : true;
    }

    public boolean isChildInGroup() {
        return this.parent == null;
    }

    public boolean isClearable() {
        StatusBarNotification statusBarNotification = this.notification;
        if (statusBarNotification == null || !statusBarNotification.isClearable()) {
            return false;
        }
        List<NotificationEntry> children = getChildren();
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                NotificationEntry child = children.get(i);
                if (!child.isClearable()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public boolean canViewBeDismissed() {
        ExpandableNotificationRow expandableNotificationRow = this.row;
        if (expandableNotificationRow == null) {
            return true;
        }
        return expandableNotificationRow.canViewBeDismissed();
    }

    @VisibleForTesting
    boolean isExemptFromDndVisualSuppression() {
        if (isNotificationBlockedByPolicy(this.notification.getNotification())) {
            return false;
        }
        if ((this.notification.getNotification().flags & 64) == 0 && !this.notification.getNotification().isMediaNotification()) {
            Boolean bool = this.mIsSystemNotification;
            return bool != null && bool.booleanValue();
        }
        return true;
    }

    private boolean shouldSuppressVisualEffect(int effect) {
        return (isExemptFromDndVisualSuppression() || (this.suppressedVisualEffects & effect) == 0) ? false : true;
    }

    public boolean shouldSuppressFullScreenIntent() {
        return shouldSuppressVisualEffect(4);
    }

    public boolean shouldSuppressPeek() {
        return shouldSuppressVisualEffect(16);
    }

    public boolean shouldSuppressStatusBar() {
        return shouldSuppressVisualEffect(32);
    }

    public boolean shouldSuppressAmbient() {
        return shouldSuppressVisualEffect(128);
    }

    public boolean shouldSuppressNotificationList() {
        return shouldSuppressVisualEffect(256);
    }

    public boolean shouldSuppressNotificationDot() {
        return shouldSuppressVisualEffect(64);
    }

    private static boolean isNotificationBlockedByPolicy(Notification n) {
        return isCategory("call", n) || isCategory("msg", n) || isCategory("alarm", n) || isCategory("event", n) || isCategory("reminder", n);
    }

    private static boolean isCategory(String category, Notification n) {
        return Objects.equals(n.category, category);
    }

    public void setSensitive(boolean sensitive, boolean deviceSensitive) {
        getRow().setSensitive(sensitive, deviceSensitive);
        if (sensitive != this.mSensitive) {
            this.mSensitive = sensitive;
            Runnable runnable = this.mOnSensitiveChangedListener;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public boolean isSensitive() {
        return this.mSensitive;
    }

    public void setOnSensitiveChangedListener(Runnable listener) {
        this.mOnSensitiveChangedListener = listener;
    }

    public boolean isPulseSuppressed() {
        return this.mPulseSupressed;
    }

    public void setPulseSuppressed(boolean suppressed) {
        this.mPulseSupressed = suppressed;
    }

    /* loaded from: classes21.dex */
    public static class EditedSuggestionInfo {
        public final int index;
        public final CharSequence originalText;

        public EditedSuggestionInfo(CharSequence originalText, int index) {
            this.originalText = originalText;
            this.index = index;
        }
    }
}
