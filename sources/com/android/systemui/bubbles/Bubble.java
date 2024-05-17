package com.android.systemui.bubbles;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class Bubble {
    private static final String TAG = "Bubble";
    private String mAppName;
    private NotificationEntry mEntry;
    private BubbleExpandedView mExpandedView;
    private final String mGroupId;
    private BubbleView mIconView;
    private boolean mInflated;
    private boolean mIsRemoved;
    private final String mKey;
    private long mLastAccessed;
    private long mLastUpdated;
    private boolean mSuppressFlyout;
    private Drawable mUserBadgedAppIcon;
    private boolean mShowInShadeWhenBubble = true;
    private boolean mShowBubbleUpdateDot = true;

    public static String groupId(NotificationEntry entry) {
        UserHandle user = entry.notification.getUser();
        return user.getIdentifier() + "|" + entry.notification.getPackageName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Bubble(Context context, NotificationEntry e) {
        this.mEntry = e;
        this.mKey = e.key;
        this.mLastUpdated = e.notification.getPostTime();
        this.mGroupId = groupId(e);
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(this.mEntry.notification.getPackageName(), 795136);
            if (info != null) {
                this.mAppName = String.valueOf(pm.getApplicationLabel(info));
            }
            Drawable appIcon = pm.getApplicationIcon(this.mEntry.notification.getPackageName());
            this.mUserBadgedAppIcon = pm.getUserBadgedIcon(appIcon, this.mEntry.notification.getUser());
        } catch (PackageManager.NameNotFoundException e2) {
            this.mAppName = this.mEntry.notification.getPackageName();
        }
    }

    public String getKey() {
        return this.mKey;
    }

    public NotificationEntry getEntry() {
        return this.mEntry;
    }

    public String getGroupId() {
        return this.mGroupId;
    }

    public String getPackageName() {
        return this.mEntry.notification.getPackageName();
    }

    public String getAppName() {
        return this.mAppName;
    }

    boolean isInflated() {
        return this.mInflated;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDotVisibility() {
        BubbleView bubbleView = this.mIconView;
        if (bubbleView != null) {
            bubbleView.updateDotVisibility(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BubbleView getIconView() {
        return this.mIconView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BubbleExpandedView getExpandedView() {
        return this.mExpandedView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cleanupExpandedState() {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.cleanUpExpandedState();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void inflate(LayoutInflater inflater, BubbleStackView stackView) {
        if (this.mInflated) {
            return;
        }
        this.mIconView = (BubbleView) inflater.inflate(R.layout.bubble_view, (ViewGroup) stackView, false);
        this.mIconView.setBubble(this);
        this.mIconView.setAppIcon(this.mUserBadgedAppIcon);
        this.mExpandedView = (BubbleExpandedView) inflater.inflate(R.layout.bubble_expanded_view, (ViewGroup) stackView, false);
        this.mExpandedView.setBubble(this, stackView, this.mAppName);
        this.mInflated = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContentVisibility(boolean visibility) {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            bubbleExpandedView.setContentVisibility(visibility);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateEntry(NotificationEntry entry) {
        this.mEntry = entry;
        this.mLastUpdated = entry.notification.getPostTime();
        if (this.mInflated) {
            this.mIconView.update(this);
            this.mExpandedView.update(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastActivity() {
        return Math.max(this.mLastUpdated, this.mLastAccessed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastUpdateTime() {
        return this.mLastUpdated;
    }

    long getLastAccessTime() {
        return this.mLastAccessed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDisplayId() {
        BubbleExpandedView bubbleExpandedView = this.mExpandedView;
        if (bubbleExpandedView != null) {
            return bubbleExpandedView.getVirtualDisplayId();
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markAsAccessedAt(long lastAccessedMillis) {
        this.mLastAccessed = lastAccessedMillis;
        setShowInShadeWhenBubble(false);
        setShowBubbleDot(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean showInShadeWhenBubble() {
        return (this.mEntry.isRowDismissed() || shouldSuppressNotification() || (this.mEntry.isClearable() && !this.mShowInShadeWhenBubble)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowInShadeWhenBubble(boolean showInShade) {
        this.mShowInShadeWhenBubble = showInShade;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowBubbleDot(boolean showDot) {
        this.mShowBubbleUpdateDot = showDot;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean showBubbleDot() {
        return this.mShowBubbleUpdateDot && !this.mEntry.shouldSuppressNotificationDot();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean showFlyoutForBubble() {
        return (this.mSuppressFlyout || this.mEntry.shouldSuppressPeek() || this.mEntry.shouldSuppressNotificationList()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSuppressFlyout(boolean suppressFlyout) {
        this.mSuppressFlyout = suppressFlyout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOngoing() {
        int flags = this.mEntry.notification.getNotification().flags;
        return (flags & 64) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getDesiredHeight(Context context) {
        Notification.BubbleMetadata data = this.mEntry.getBubbleMetadata();
        boolean useRes = data.getDesiredHeightResId() != 0;
        if (useRes) {
            return getDimenForPackageUser(context, data.getDesiredHeightResId(), this.mEntry.notification.getPackageName(), this.mEntry.notification.getUser().getIdentifier());
        }
        return data.getDesiredHeight() * context.getResources().getDisplayMetrics().density;
    }

    String getDesiredHeightString() {
        Notification.BubbleMetadata data = this.mEntry.getBubbleMetadata();
        boolean useRes = data.getDesiredHeightResId() != 0;
        if (useRes) {
            return String.valueOf(data.getDesiredHeightResId());
        }
        return String.valueOf(data.getDesiredHeight());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PendingIntent getBubbleIntent(Context context) {
        Notification notif = this.mEntry.notification.getNotification();
        Notification.BubbleMetadata data = notif.getBubbleMetadata();
        if (BubbleController.canLaunchInActivityView(context, this.mEntry) && data != null) {
            return data.getIntent();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Intent getSettingsIntent() {
        Intent intent = new Intent("android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        intent.putExtra("app_uid", this.mEntry.notification.getUid());
        intent.addFlags(134217728);
        intent.addFlags(268435456);
        intent.addFlags(536870912);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getUpdateMessage(Context context) {
        CharSequence personName;
        Notification underlyingNotif = this.mEntry.notification.getNotification();
        Class<? extends Notification.Style> style = underlyingNotif.getNotificationStyle();
        try {
        } catch (ArrayIndexOutOfBoundsException | ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
        if (Notification.BigTextStyle.class.equals(style)) {
            CharSequence bigText = underlyingNotif.extras.getCharSequence("android.bigText");
            if (!TextUtils.isEmpty(bigText)) {
                return bigText;
            }
            return underlyingNotif.extras.getCharSequence("android.text");
        }
        if (Notification.MessagingStyle.class.equals(style)) {
            List<Notification.MessagingStyle.Message> messages = Notification.MessagingStyle.Message.getMessagesFromBundleArray((Parcelable[]) underlyingNotif.extras.get("android.messages"));
            Notification.MessagingStyle.Message latestMessage = Notification.MessagingStyle.findLatestIncomingMessage(messages);
            if (latestMessage != null) {
                if (latestMessage.getSenderPerson() != null) {
                    personName = latestMessage.getSenderPerson().getName();
                } else {
                    personName = null;
                }
                if (!TextUtils.isEmpty(personName)) {
                    return context.getResources().getString(R.string.notification_summary_message_format, personName, latestMessage.getText());
                }
                return latestMessage.getText();
            }
        } else if (Notification.InboxStyle.class.equals(style)) {
            CharSequence[] lines = underlyingNotif.extras.getCharSequenceArray("android.textLines");
            if (lines != null && lines.length > 0) {
                return lines[lines.length - 1];
            }
        } else if (Notification.MediaStyle.class.equals(style)) {
            return null;
        } else {
            return underlyingNotif.extras.getCharSequence("android.text");
        }
        return null;
    }

    private int getDimenForPackageUser(Context context, int resId, String pkg, int userId) {
        PackageManager pm = context.getPackageManager();
        if (pkg != null) {
            if (userId == -1) {
                userId = 0;
            }
            try {
                Resources r = pm.getResourcesForApplicationAsUser(pkg, userId);
                return r.getDimensionPixelSize(resId);
            } catch (PackageManager.NameNotFoundException e) {
                return 0;
            } catch (Resources.NotFoundException e2) {
                Log.e(TAG, "Couldn't find desired height res id", e2);
                return 0;
            }
        }
        return 0;
    }

    private boolean shouldSuppressNotification() {
        return this.mEntry.getBubbleMetadata() != null && this.mEntry.getBubbleMetadata().isNotificationSuppressed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldAutoExpand() {
        Notification.BubbleMetadata metadata = this.mEntry.getBubbleMetadata();
        return metadata != null && metadata.getAutoExpandBubble();
    }

    public String toString() {
        return "Bubble{" + this.mKey + '}';
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("key: ");
        pw.println(this.mKey);
        pw.print("  showInShade:   ");
        pw.println(showInShadeWhenBubble());
        pw.print("  showDot:       ");
        pw.println(showBubbleDot());
        pw.print("  showFlyout:    ");
        pw.println(showFlyoutForBubble());
        pw.print("  desiredHeight: ");
        pw.println(getDesiredHeightString());
        pw.print("  suppressNotif: ");
        pw.println(shouldSuppressNotification());
        pw.print("  autoExpand:    ");
        pw.println(shouldAutoExpand());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Bubble) {
            Bubble bubble = (Bubble) o;
            return Objects.equals(this.mKey, bubble.mKey);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mKey);
    }
}
