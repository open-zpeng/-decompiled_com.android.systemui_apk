package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.policy.RemoteInputView;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationRemoteInputManager implements Dumpable {
    private static final boolean DEBUG = false;
    public static final boolean ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
    public static boolean FORCE_REMOTE_INPUT_HISTORY = SystemProperties.getBoolean("debug.force_remoteinput_history", true);
    private static final int REMOTE_INPUT_KEPT_ENTRY_AUTO_CANCEL_DELAY = 200;
    private static final String TAG = "NotifRemoteInputManager";
    protected Callback mCallback;
    protected final Context mContext;
    private final NotificationEntryManager mEntryManager;
    private final KeyguardManager mKeyguardManager;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainHandler;
    protected NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    protected RemoteInputController mRemoteInputController;
    private final Lazy<ShadeController> mShadeController;
    private final SmartReplyController mSmartReplyController;
    private final StatusBarStateController mStatusBarStateController;
    private final UserManager mUserManager;
    protected final ArraySet<String> mKeysKeptForRemoteInputHistory = new ArraySet<>();
    protected final ArraySet<NotificationEntry> mEntriesKeptForRemoteInputActive = new ArraySet<>();
    protected final ArrayList<NotificationLifetimeExtender> mLifetimeExtenders = new ArrayList<>();
    private final RemoteViews.OnClickHandler mOnClickHandler = new AnonymousClass1();
    protected IStatusBarService mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));

    /* loaded from: classes21.dex */
    public interface Callback {
        boolean handleRemoteViewClick(View view, PendingIntent pendingIntent, ClickHandler clickHandler);

        void onLockedRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view);

        void onLockedWorkRemoteInput(int i, ExpandableNotificationRow expandableNotificationRow, View view);

        void onMakeExpandedVisibleForRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view);

        boolean shouldHandleRemoteInput(View view, PendingIntent pendingIntent);
    }

    /* loaded from: classes21.dex */
    public interface ClickHandler {
        boolean handleClick();
    }

    /* renamed from: com.android.systemui.statusbar.NotificationRemoteInputManager$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass1 implements RemoteViews.OnClickHandler {
        AnonymousClass1() {
        }

        public boolean onClickHandler(final View view, final PendingIntent pendingIntent, final RemoteViews.RemoteResponse response) {
            ((ShadeController) NotificationRemoteInputManager.this.mShadeController.get()).wakeUpIfDozing(SystemClock.uptimeMillis(), view, "NOTIFICATION_CLICK");
            if (handleRemoteInput(view, pendingIntent)) {
                return true;
            }
            logActionClick(view, pendingIntent);
            try {
                ActivityManager.getService().resumeAppSwitches();
            } catch (RemoteException e) {
            }
            return NotificationRemoteInputManager.this.mCallback.handleRemoteViewClick(view, pendingIntent, new ClickHandler() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationRemoteInputManager$1$9gPb9F64OW5Dxh7FkFJc-IgAVZQ
                @Override // com.android.systemui.statusbar.NotificationRemoteInputManager.ClickHandler
                public final boolean handleClick() {
                    return NotificationRemoteInputManager.AnonymousClass1.lambda$onClickHandler$0(response, view, pendingIntent);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$onClickHandler$0(RemoteViews.RemoteResponse response, View view, PendingIntent pendingIntent) {
            Pair<Intent, ActivityOptions> options = response.getLaunchOptions(view);
            ((ActivityOptions) options.second).setLaunchWindowingMode(4);
            return RemoteViews.startPendingIntent(view, pendingIntent, options);
        }

        private void logActionClick(View view, PendingIntent actionIntent) {
            int buttonIndex;
            Integer actionIndex = (Integer) view.getTag(16909261);
            if (actionIndex == null) {
                return;
            }
            ViewParent parent = view.getParent();
            StatusBarNotification statusBarNotification = getNotificationForParent(parent);
            if (statusBarNotification == null) {
                Log.w(NotificationRemoteInputManager.TAG, "Couldn't determine notification for click.");
                return;
            }
            String key = statusBarNotification.getKey();
            if (view.getId() == 16908768 && parent != null && (parent instanceof ViewGroup)) {
                ViewGroup actionGroup = (ViewGroup) parent;
                int buttonIndex2 = actionGroup.indexOfChild(view);
                buttonIndex = buttonIndex2;
            } else {
                buttonIndex = -1;
            }
            int count = NotificationRemoteInputManager.this.mEntryManager.getNotificationData().getActiveNotifications().size();
            int rank = NotificationRemoteInputManager.this.mEntryManager.getNotificationData().getRank(key);
            Notification.Action[] actions = statusBarNotification.getNotification().actions;
            if (actions != null && actionIndex.intValue() < actions.length) {
                Notification.Action action = statusBarNotification.getNotification().actions[actionIndex.intValue()];
                if (!Objects.equals(action.actionIntent, actionIntent)) {
                    Log.w(NotificationRemoteInputManager.TAG, "actionIntent does not match");
                    return;
                }
                NotificationVisibility.NotificationLocation location = NotificationLogger.getNotificationLocation(NotificationRemoteInputManager.this.mEntryManager.getNotificationData().get(key));
                NotificationVisibility nv = NotificationVisibility.obtain(key, rank, count, true, location);
                try {
                    try {
                        NotificationRemoteInputManager.this.mBarService.onNotificationActionClick(key, buttonIndex, action, nv, false);
                        return;
                    } catch (RemoteException e) {
                        return;
                    }
                } catch (RemoteException e2) {
                    return;
                }
            }
            Log.w(NotificationRemoteInputManager.TAG, "statusBarNotification.getNotification().actions is null or invalid");
        }

        private StatusBarNotification getNotificationForParent(ViewParent parent) {
            while (parent != null) {
                if (parent instanceof ExpandableNotificationRow) {
                    return ((ExpandableNotificationRow) parent).getStatusBarNotification();
                }
                parent = parent.getParent();
            }
            return null;
        }

        private boolean handleRemoteInput(View view, PendingIntent pendingIntent) {
            if (NotificationRemoteInputManager.this.mCallback.shouldHandleRemoteInput(view, pendingIntent)) {
                return true;
            }
            Object tag = view.getTag(16909379);
            RemoteInput[] inputs = null;
            if (tag instanceof RemoteInput[]) {
                inputs = (RemoteInput[]) tag;
            }
            if (inputs == null) {
                return false;
            }
            RemoteInput input = null;
            for (RemoteInput i : inputs) {
                if (i.getAllowFreeFormInput()) {
                    input = i;
                }
            }
            if (input == null) {
                return false;
            }
            return NotificationRemoteInputManager.this.activateRemoteInput(view, inputs, input, pendingIntent, null);
        }
    }

    @Inject
    public NotificationRemoteInputManager(Context context, NotificationLockscreenUserManager lockscreenUserManager, SmartReplyController smartReplyController, NotificationEntryManager notificationEntryManager, Lazy<ShadeController> shadeController, StatusBarStateController statusBarStateController, @Named("main_handler") Handler mainHandler) {
        this.mContext = context;
        this.mLockscreenUserManager = lockscreenUserManager;
        this.mSmartReplyController = smartReplyController;
        this.mEntryManager = notificationEntryManager;
        this.mShadeController = shadeController;
        this.mMainHandler = mainHandler;
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        addLifetimeExtenders();
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mStatusBarStateController = statusBarStateController;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.NotificationRemoteInputManager.2
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPreEntryUpdated(NotificationEntry entry) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(entry);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(entry);
                if (removedByUser && entry != null) {
                    NotificationRemoteInputManager.this.onPerformRemoveNotification(entry, entry.key);
                }
            }
        });
    }

    public void setUpWithCallback(Callback callback, RemoteInputController.Delegate delegate) {
        this.mCallback = callback;
        this.mRemoteInputController = new RemoteInputController(delegate);
        this.mRemoteInputController.addCallback(new AnonymousClass3());
        this.mSmartReplyController.setCallback(new SmartReplyController.Callback() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationRemoteInputManager$Nf_J1NPWba8TQAi27Yt-XiB5drE
            @Override // com.android.systemui.statusbar.SmartReplyController.Callback
            public final void onSmartReplySent(NotificationEntry notificationEntry, CharSequence charSequence) {
                NotificationRemoteInputManager.this.lambda$setUpWithCallback$0$NotificationRemoteInputManager(notificationEntry, charSequence);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.NotificationRemoteInputManager$3  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass3 implements RemoteInputController.Callback {
        AnonymousClass3() {
        }

        @Override // com.android.systemui.statusbar.RemoteInputController.Callback
        public void onRemoteInputSent(final NotificationEntry entry) {
            if (NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY && NotificationRemoteInputManager.this.isNotificationKeptForRemoteInputHistory(entry.key)) {
                NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(entry.key);
            } else if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.contains(entry)) {
                NotificationRemoteInputManager.this.mMainHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationRemoteInputManager$3$4_sgjm8NgJs8c5OYAKLP29ZAlfg
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationRemoteInputManager.AnonymousClass3.this.lambda$onRemoteInputSent$0$NotificationRemoteInputManager$3(entry);
                    }
                }, 200L);
            }
            try {
                NotificationRemoteInputManager.this.mBarService.onNotificationDirectReplied(entry.notification.getKey());
                if (entry.editedSuggestionInfo != null) {
                    boolean modifiedBeforeSending = !TextUtils.equals(entry.remoteInputText, entry.editedSuggestionInfo.originalText);
                    NotificationRemoteInputManager.this.mBarService.onNotificationSmartReplySent(entry.notification.getKey(), entry.editedSuggestionInfo.index, entry.editedSuggestionInfo.originalText, NotificationLogger.getNotificationLocation(entry).toMetricsEventEnum(), modifiedBeforeSending);
                }
            } catch (RemoteException e) {
            }
        }

        public /* synthetic */ void lambda$onRemoteInputSent$0$NotificationRemoteInputManager$3(NotificationEntry entry) {
            if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove(entry)) {
                NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(entry.key);
            }
        }
    }

    public /* synthetic */ void lambda$setUpWithCallback$0$NotificationRemoteInputManager(NotificationEntry entry, CharSequence reply) {
        StatusBarNotification newSbn = rebuildNotificationWithRemoteInput(entry, reply, true);
        this.mEntryManager.updateNotification(newSbn, null);
    }

    public boolean activateRemoteInput(View view, RemoteInput[] inputs, RemoteInput input, PendingIntent pendingIntent, NotificationEntry.EditedSuggestionInfo editedSuggestionInfo) {
        ViewParent p = view.getParent();
        RemoteInputView riv = null;
        ExpandableNotificationRow row = null;
        while (true) {
            if (p == null) {
                break;
            }
            if (p instanceof View) {
                View pv = (View) p;
                if (pv.isRootNamespace()) {
                    riv = findRemoteInputView(pv);
                    row = (ExpandableNotificationRow) pv.getTag(R.id.row_tag_for_content_view);
                    break;
                }
            }
            p = p.getParent();
        }
        if (row == null) {
            return false;
        }
        row.setUserExpanded(true);
        if (!this.mLockscreenUserManager.shouldAllowLockscreenRemoteInput()) {
            int userId = pendingIntent.getCreatorUserHandle().getIdentifier();
            if (this.mLockscreenUserManager.isLockscreenPublicMode(userId) || this.mStatusBarStateController.getState() == 1) {
                this.mCallback.onLockedRemoteInput(row, view);
                return true;
            } else if (this.mUserManager.getUserInfo(userId).isManagedProfile() && this.mKeyguardManager.isDeviceLocked(userId)) {
                this.mCallback.onLockedWorkRemoteInput(userId, row, view);
                return true;
            }
        }
        if (riv != null && !riv.isAttachedToWindow()) {
            riv = null;
        }
        if (riv == null && (riv = findRemoteInputView(row.getPrivateLayout().getExpandedChild())) == null) {
            return false;
        }
        if (riv == row.getPrivateLayout().getExpandedRemoteInput() && !row.getPrivateLayout().getExpandedChild().isShown()) {
            this.mCallback.onMakeExpandedVisibleForRemoteInput(row, view);
            return true;
        } else if (!riv.isAttachedToWindow()) {
            return false;
        } else {
            int width = view.getWidth();
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getLayout() != null) {
                    int innerWidth = (int) tv.getLayout().getLineWidth(0);
                    width = Math.min(width, innerWidth + tv.getCompoundPaddingLeft() + tv.getCompoundPaddingRight());
                }
            }
            int innerWidth2 = view.getLeft();
            int cx = innerWidth2 + (width / 2);
            int cy = view.getTop() + (view.getHeight() / 2);
            int w = riv.getWidth();
            int h = riv.getHeight();
            int r = Math.max(Math.max(cx + cy, (h - cy) + cx), Math.max((w - cx) + cy, (w - cx) + (h - cy)));
            riv.setRevealParameters(cx, cy, r);
            riv.setPendingIntent(pendingIntent);
            riv.setRemoteInput(inputs, input, editedSuggestionInfo);
            riv.focusAnimated();
            return true;
        }
    }

    private RemoteInputView findRemoteInputView(View v) {
        if (v == null) {
            return null;
        }
        return (RemoteInputView) v.findViewWithTag(RemoteInputView.VIEW_TAG);
    }

    protected void addLifetimeExtenders() {
        this.mLifetimeExtenders.add(new RemoteInputHistoryExtender());
        this.mLifetimeExtenders.add(new SmartReplyHistoryExtender());
        this.mLifetimeExtenders.add(new RemoteInputActiveExtender());
    }

    public ArrayList<NotificationLifetimeExtender> getLifetimeExtenders() {
        return this.mLifetimeExtenders;
    }

    public RemoteInputController getController() {
        return this.mRemoteInputController;
    }

    @VisibleForTesting
    void onPerformRemoveNotification(NotificationEntry entry, String key) {
        if (this.mKeysKeptForRemoteInputHistory.contains(key)) {
            this.mKeysKeptForRemoteInputHistory.remove(key);
        }
        if (this.mRemoteInputController.isRemoteInputActive(entry)) {
            this.mRemoteInputController.removeRemoteInput(entry, null);
        }
    }

    public void onPanelCollapsed() {
        for (int i = 0; i < this.mEntriesKeptForRemoteInputActive.size(); i++) {
            NotificationEntry entry = this.mEntriesKeptForRemoteInputActive.valueAt(i);
            this.mRemoteInputController.removeRemoteInput(entry, null);
            NotificationLifetimeExtender.NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationLifetimeFinishedCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(entry.key);
            }
        }
        this.mEntriesKeptForRemoteInputActive.clear();
    }

    public boolean isNotificationKeptForRemoteInputHistory(String key) {
        return this.mKeysKeptForRemoteInputHistory.contains(key);
    }

    public boolean shouldKeepForRemoteInputHistory(NotificationEntry entry) {
        if (FORCE_REMOTE_INPUT_HISTORY) {
            return this.mRemoteInputController.isSpinning(entry.key) || entry.hasJustSentRemoteInput();
        }
        return false;
    }

    public boolean shouldKeepForSmartReplyHistory(NotificationEntry entry) {
        if (!FORCE_REMOTE_INPUT_HISTORY) {
            return false;
        }
        return this.mSmartReplyController.isSendingSmartReply(entry.key);
    }

    public void checkRemoteInputOutside(MotionEvent event) {
        if (event.getAction() == 4 && event.getX() == 0.0f && event.getY() == 0.0f && this.mRemoteInputController.isRemoteInputActive()) {
            this.mRemoteInputController.closeRemoteInputs();
        }
    }

    @VisibleForTesting
    StatusBarNotification rebuildNotificationForCanceledSmartReplies(NotificationEntry entry) {
        return rebuildNotificationWithRemoteInput(entry, null, false);
    }

    @VisibleForTesting
    StatusBarNotification rebuildNotificationWithRemoteInput(NotificationEntry entry, CharSequence remoteInputText, boolean showSpinner) {
        CharSequence[] newHistory;
        StatusBarNotification sbn = entry.notification;
        Notification.Builder b = Notification.Builder.recoverBuilder(this.mContext, sbn.getNotification().clone());
        if (remoteInputText != null) {
            CharSequence[] oldHistory = sbn.getNotification().extras.getCharSequenceArray("android.remoteInputHistory");
            if (oldHistory != null) {
                newHistory = new CharSequence[oldHistory.length + 1];
                System.arraycopy(oldHistory, 0, newHistory, 1, oldHistory.length);
            } else {
                newHistory = new CharSequence[1];
            }
            newHistory[0] = String.valueOf(remoteInputText);
            b.setRemoteInputHistory(newHistory);
        }
        b.setShowRemoteInputSpinner(showSpinner);
        b.setHideSmartReplies(true);
        Notification newNotification = b.build();
        newNotification.contentView = sbn.getNotification().contentView;
        newNotification.bigContentView = sbn.getNotification().bigContentView;
        newNotification.headsUpContentView = sbn.getNotification().headsUpContentView;
        return new StatusBarNotification(sbn.getPackageName(), sbn.getOpPkg(), sbn.getId(), sbn.getTag(), sbn.getUid(), sbn.getInitialPid(), newNotification, sbn.getUser(), sbn.getOverrideGroupKey(), sbn.getPostTime());
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NotificationRemoteInputManager state:");
        pw.print("  mKeysKeptForRemoteInputHistory: ");
        pw.println(this.mKeysKeptForRemoteInputHistory);
        pw.print("  mEntriesKeptForRemoteInputActive: ");
        pw.println(this.mEntriesKeptForRemoteInputActive);
    }

    public void bindRow(ExpandableNotificationRow row) {
        row.setRemoteInputController(this.mRemoteInputController);
        row.setRemoteViewClickHandler(this.mOnClickHandler);
    }

    @VisibleForTesting
    public Set<NotificationEntry> getEntriesKeptForRemoteInputActive() {
        return this.mEntriesKeptForRemoteInputActive;
    }

    /* loaded from: classes21.dex */
    protected abstract class RemoteInputExtender implements NotificationLifetimeExtender {
        protected RemoteInputExtender() {
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public void setCallback(NotificationLifetimeExtender.NotificationSafeToRemoveCallback callback) {
            if (NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback == null) {
                NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback = callback;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class RemoteInputHistoryExtender extends RemoteInputExtender {
        protected RemoteInputHistoryExtender() {
            super();
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public boolean shouldExtendLifetime(NotificationEntry entry) {
            return NotificationRemoteInputManager.this.shouldKeepForRemoteInputHistory(entry);
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public void setShouldManageLifetime(NotificationEntry entry, boolean shouldExtend) {
            if (shouldExtend) {
                CharSequence remoteInputText = entry.remoteInputText;
                if (TextUtils.isEmpty(remoteInputText)) {
                    remoteInputText = entry.remoteInputTextWhenReset;
                }
                StatusBarNotification newSbn = NotificationRemoteInputManager.this.rebuildNotificationWithRemoteInput(entry, remoteInputText, false);
                entry.onRemoteInputInserted();
                if (newSbn != null) {
                    NotificationRemoteInputManager.this.mEntryManager.updateNotification(newSbn, null);
                    if (entry.isRemoved()) {
                        return;
                    }
                    if (Log.isLoggable(NotificationRemoteInputManager.TAG, 3)) {
                        Log.d(NotificationRemoteInputManager.TAG, "Keeping notification around after sending remote input " + entry.key);
                    }
                    NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add(entry.key);
                    return;
                }
                return;
            }
            NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove(entry.key);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class SmartReplyHistoryExtender extends RemoteInputExtender {
        protected SmartReplyHistoryExtender() {
            super();
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public boolean shouldExtendLifetime(NotificationEntry entry) {
            return NotificationRemoteInputManager.this.shouldKeepForSmartReplyHistory(entry);
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public void setShouldManageLifetime(NotificationEntry entry, boolean shouldExtend) {
            if (shouldExtend) {
                StatusBarNotification newSbn = NotificationRemoteInputManager.this.rebuildNotificationForCanceledSmartReplies(entry);
                if (newSbn != null) {
                    NotificationRemoteInputManager.this.mEntryManager.updateNotification(newSbn, null);
                    if (entry.isRemoved()) {
                        return;
                    }
                    if (Log.isLoggable(NotificationRemoteInputManager.TAG, 3)) {
                        Log.d(NotificationRemoteInputManager.TAG, "Keeping notification around after sending smart reply " + entry.key);
                    }
                    NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add(entry.key);
                    return;
                }
                return;
            }
            NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove(entry.key);
            NotificationRemoteInputManager.this.mSmartReplyController.stopSending(entry);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class RemoteInputActiveExtender extends RemoteInputExtender {
        protected RemoteInputActiveExtender() {
            super();
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public boolean shouldExtendLifetime(NotificationEntry entry) {
            return NotificationRemoteInputManager.this.mRemoteInputController.isRemoteInputActive(entry);
        }

        @Override // com.android.systemui.statusbar.NotificationLifetimeExtender
        public void setShouldManageLifetime(NotificationEntry entry, boolean shouldExtend) {
            if (shouldExtend) {
                if (Log.isLoggable(NotificationRemoteInputManager.TAG, 3)) {
                    Log.d(NotificationRemoteInputManager.TAG, "Keeping notification around while remote input active " + entry.key);
                }
                NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.add(entry);
                return;
            }
            NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove(entry);
        }
    }
}
