package com.android.systemui.statusbar.notification.row;

import android.app.Notification;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.ImageMessageConsumer;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.MediaNotificationProcessor;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.util.Assert;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
/* loaded from: classes21.dex */
public class NotificationContentInflater {
    public static final int FLAG_CONTENT_VIEW_ALL = -1;
    public static final int FLAG_CONTENT_VIEW_CONTRACTED = 1;
    public static final int FLAG_CONTENT_VIEW_EXPANDED = 2;
    public static final int FLAG_CONTENT_VIEW_HEADS_UP = 4;
    public static final int FLAG_CONTENT_VIEW_PUBLIC = 8;
    private static final int REQUIRED_INFLATION_FLAGS = 3;
    public static final String TAG = "NotifContentInflater";
    private InflationCallback mCallback;
    private boolean mIsChildInGroup;
    private boolean mIsLowPriority;
    private RemoteViews.OnClickHandler mRemoteViewClickHandler;
    private final ExpandableNotificationRow mRow;
    private boolean mUsesIncreasedHeadsUpHeight;
    private boolean mUsesIncreasedHeight;
    private int mInflationFlags = 3;
    private boolean mInflateSynchronously = false;
    private final ArrayMap<Integer, RemoteViews> mCachedContentViews = new ArrayMap<>();

    /* loaded from: classes21.dex */
    public interface InflationCallback {
        void handleInflationException(StatusBarNotification statusBarNotification, Exception exc);

        void onAsyncInflationFinished(NotificationEntry notificationEntry, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface InflationFlag {
    }

    public NotificationContentInflater(ExpandableNotificationRow row) {
        this.mRow = row;
    }

    public void setIsLowPriority(boolean isLowPriority) {
        this.mIsLowPriority = isLowPriority;
    }

    public void setIsChildInGroup(boolean childInGroup) {
        if (childInGroup != this.mIsChildInGroup) {
            this.mIsChildInGroup = childInGroup;
            if (this.mIsLowPriority) {
                inflateNotificationViews(3);
            }
        }
    }

    public void setUsesIncreasedHeight(boolean usesIncreasedHeight) {
        this.mUsesIncreasedHeight = usesIncreasedHeight;
    }

    public void setUsesIncreasedHeadsUpHeight(boolean usesIncreasedHeight) {
        this.mUsesIncreasedHeadsUpHeight = usesIncreasedHeight;
    }

    public void setRemoteViewClickHandler(RemoteViews.OnClickHandler remoteViewClickHandler) {
        this.mRemoteViewClickHandler = remoteViewClickHandler;
    }

    public void updateNeedsRedaction(boolean needsRedaction) {
        if (this.mRow.getEntry() != null && needsRedaction) {
            inflateNotificationViews(8);
        }
    }

    public void updateInflationFlag(int flag, boolean shouldInflate) {
        if (shouldInflate) {
            this.mInflationFlags |= flag;
        } else if ((flag & 3) == 0) {
            this.mInflationFlags &= ~flag;
        }
    }

    @VisibleForTesting
    public void addInflationFlags(int flags) {
        this.mInflationFlags |= flags;
    }

    public boolean isInflationFlagSet(int flag) {
        return (this.mInflationFlags & flag) != 0;
    }

    public void inflateNotificationViews() {
        inflateNotificationViews(this.mInflationFlags);
    }

    private void inflateNotificationViews(int reInflateFlags) {
        if (this.mRow.isRemoved()) {
            return;
        }
        StatusBarNotification sbn = this.mRow.getEntry().notification;
        this.mRow.getImageResolver().preloadImages(sbn.getNotification());
        AsyncInflationTask task = new AsyncInflationTask(sbn, this.mInflateSynchronously, reInflateFlags & this.mInflationFlags, this.mCachedContentViews, this.mRow, this.mIsLowPriority, this.mIsChildInGroup, this.mUsesIncreasedHeight, this.mUsesIncreasedHeadsUpHeight, this.mCallback, this.mRemoteViewClickHandler);
        if (this.mInflateSynchronously) {
            task.onPostExecute(task.doInBackground(new Void[0]));
        } else {
            task.execute(new Void[0]);
        }
    }

    @VisibleForTesting
    InflationProgress inflateNotificationViews(boolean inflateSynchronously, int reInflateFlags, Notification.Builder builder, Context packageContext) {
        InflationProgress result = inflateSmartReplyViews(createRemoteViews(reInflateFlags, builder, this.mIsLowPriority, this.mIsChildInGroup, this.mUsesIncreasedHeight, this.mUsesIncreasedHeadsUpHeight, packageContext), reInflateFlags, this.mRow.getEntry(), this.mRow.getContext(), packageContext, this.mRow.getHeadsUpManager(), this.mRow.getExistingSmartRepliesAndActions());
        apply(inflateSynchronously, result, reInflateFlags, this.mCachedContentViews, this.mRow, this.mRemoteViewClickHandler, null);
        return result;
    }

    public void freeNotificationView(int inflateFlag) {
        if ((this.mInflationFlags & inflateFlag) != 0) {
            return;
        }
        if (inflateFlag == 4) {
            if (this.mRow.getPrivateLayout().isContentViewInactive(2)) {
                this.mRow.getPrivateLayout().setHeadsUpChild(null);
                this.mCachedContentViews.remove(4);
                this.mRow.getPrivateLayout().setHeadsUpInflatedSmartReplies(null);
            }
        } else if (inflateFlag == 8 && this.mRow.getPublicLayout().isContentViewInactive(0)) {
            this.mRow.getPublicLayout().setContractedChild(null);
            this.mCachedContentViews.remove(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static InflationProgress inflateSmartReplyViews(InflationProgress result, int reInflateFlags, NotificationEntry entry, Context context, Context packageContext, HeadsUpManager headsUpManager, InflatedSmartReplies.SmartRepliesAndActions previousSmartRepliesAndActions) {
        SmartReplyConstants smartReplyConstants = (SmartReplyConstants) Dependency.get(SmartReplyConstants.class);
        SmartReplyController smartReplyController = (SmartReplyController) Dependency.get(SmartReplyController.class);
        if ((reInflateFlags & 2) != 0 && result.newExpandedView != null) {
            result.expandedInflatedSmartReplies = InflatedSmartReplies.inflate(context, packageContext, entry, smartReplyConstants, smartReplyController, headsUpManager, previousSmartRepliesAndActions);
        }
        if ((reInflateFlags & 4) != 0 && result.newHeadsUpView != null) {
            result.headsUpInflatedSmartReplies = InflatedSmartReplies.inflate(context, packageContext, entry, smartReplyConstants, smartReplyController, headsUpManager, previousSmartRepliesAndActions);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static InflationProgress createRemoteViews(int reInflateFlags, Notification.Builder builder, boolean isLowPriority, boolean isChildInGroup, boolean usesIncreasedHeight, boolean usesIncreasedHeadsUpHeight, Context packageContext) {
        InflationProgress result = new InflationProgress();
        boolean isLowPriority2 = isLowPriority && !isChildInGroup;
        if ((reInflateFlags & 1) != 0) {
            result.newContentView = createContentView(builder, isLowPriority2, usesIncreasedHeight);
        }
        if ((reInflateFlags & 2) != 0) {
            result.newExpandedView = createExpandedView(builder, isLowPriority2);
        }
        if ((reInflateFlags & 4) != 0) {
            result.newHeadsUpView = builder.createHeadsUpContentView(usesIncreasedHeadsUpHeight);
        }
        if ((reInflateFlags & 8) != 0) {
            result.newPublicView = builder.makePublicContentView(isLowPriority2);
        }
        result.packageContext = packageContext;
        result.headsUpStatusBarText = builder.getHeadsUpStatusBarText(false);
        result.headsUpStatusBarTextPublic = builder.getHeadsUpStatusBarText(true);
        return result;
    }

    public static CancellationSignal apply(boolean inflateSynchronously, final InflationProgress result, int reInflateFlags, ArrayMap<Integer, RemoteViews> cachedContentViews, ExpandableNotificationRow row, RemoteViews.OnClickHandler remoteViewClickHandler, InflationCallback callback) {
        int i;
        HashMap<Integer, CancellationSignal> runningInflations;
        NotificationContentView publicLayout;
        NotificationContentView privateLayout;
        NotificationContentView privateLayout2;
        final InflationProgress inflationProgress;
        NotificationContentView privateLayout3 = row.getPrivateLayout();
        NotificationContentView publicLayout2 = row.getPublicLayout();
        HashMap<Integer, CancellationSignal> runningInflations2 = new HashMap<>();
        if ((reInflateFlags & 1) == 0) {
            i = 1;
            runningInflations = runningInflations2;
            publicLayout = publicLayout2;
            privateLayout = privateLayout3;
        } else {
            boolean isNewView = !canReapplyRemoteView(result.newContentView, cachedContentViews.get(1));
            ApplyCallback applyCallback = new ApplyCallback() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentInflater.1
                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public void setResultView(View v) {
                    InflationProgress.this.inflatedContentView = v;
                }

                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newContentView;
                }
            };
            i = 1;
            runningInflations = runningInflations2;
            publicLayout = publicLayout2;
            privateLayout = privateLayout3;
            applyRemoteView(inflateSynchronously, result, reInflateFlags, 1, cachedContentViews, row, isNewView, remoteViewClickHandler, callback, privateLayout3, privateLayout3.getContractedChild(), privateLayout3.getVisibleWrapper(0), runningInflations, applyCallback);
        }
        if ((reInflateFlags & 2) == 0) {
            privateLayout2 = privateLayout;
            inflationProgress = result;
        } else if (result.newExpandedView != null) {
            boolean isNewView2 = !canReapplyRemoteView(result.newExpandedView, cachedContentViews.get(2));
            int i2 = i;
            inflationProgress = result;
            ApplyCallback applyCallback2 = new ApplyCallback() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentInflater.2
                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public void setResultView(View v) {
                    InflationProgress.this.inflatedExpandedView = v;
                }

                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newExpandedView;
                }
            };
            NotificationContentView privateLayout4 = privateLayout;
            privateLayout2 = privateLayout4;
            applyRemoteView(inflateSynchronously, result, reInflateFlags, 2, cachedContentViews, row, isNewView2, remoteViewClickHandler, callback, privateLayout4, privateLayout.getExpandedChild(), privateLayout4.getVisibleWrapper(i2), runningInflations, applyCallback2);
        } else {
            privateLayout2 = privateLayout;
            inflationProgress = result;
        }
        if ((reInflateFlags & 4) != 0 && result.newHeadsUpView != null) {
            boolean isNewView3 = !canReapplyRemoteView(result.newHeadsUpView, cachedContentViews.get(4));
            ApplyCallback applyCallback3 = new ApplyCallback() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentInflater.3
                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public void setResultView(View v) {
                    InflationProgress.this.inflatedHeadsUpView = v;
                }

                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newHeadsUpView;
                }
            };
            NotificationContentView privateLayout5 = privateLayout2;
            applyRemoteView(inflateSynchronously, result, reInflateFlags, 4, cachedContentViews, row, isNewView3, remoteViewClickHandler, callback, privateLayout5, privateLayout2.getHeadsUpChild(), privateLayout5.getVisibleWrapper(2), runningInflations, applyCallback3);
        }
        if ((reInflateFlags & 8) != 0) {
            boolean isNewView4 = !canReapplyRemoteView(result.newPublicView, cachedContentViews.get(8));
            ApplyCallback applyCallback4 = new ApplyCallback() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentInflater.4
                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public void setResultView(View v) {
                    InflationProgress.this.inflatedPublicView = v;
                }

                @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.ApplyCallback
                public RemoteViews getRemoteView() {
                    return InflationProgress.this.newPublicView;
                }
            };
            NotificationContentView publicLayout3 = publicLayout;
            applyRemoteView(inflateSynchronously, result, reInflateFlags, 8, cachedContentViews, row, isNewView4, remoteViewClickHandler, callback, publicLayout3, publicLayout.getContractedChild(), publicLayout3.getVisibleWrapper(0), runningInflations, applyCallback4);
        }
        finishIfDone(result, reInflateFlags, cachedContentViews, runningInflations, callback, row);
        CancellationSignal cancellationSignal = new CancellationSignal();
        final HashMap<Integer, CancellationSignal> runningInflations3 = runningInflations;
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$NotificationContentInflater$WjCddtvZmmNqAdGsBYXcbiOdWQY
            @Override // android.os.CancellationSignal.OnCancelListener
            public final void onCancel() {
                runningInflations3.values().forEach($$Lambda$POlPJz26zF5Nt5Z2kVGSqFxN8Co.INSTANCE);
            }
        });
        return cancellationSignal;
    }

    @VisibleForTesting
    static void applyRemoteView(boolean inflateSynchronously, final InflationProgress result, final int reInflateFlags, final int inflationId, final ArrayMap<Integer, RemoteViews> cachedContentViews, final ExpandableNotificationRow row, final boolean isNewView, final RemoteViews.OnClickHandler remoteViewClickHandler, final InflationCallback callback, final NotificationContentView parentLayout, final View existingView, final NotificationViewWrapper existingWrapper, final HashMap<Integer, CancellationSignal> runningInflations, final ApplyCallback applyCallback) {
        CancellationSignal cancellationSignal;
        final RemoteViews newContentView = applyCallback.getRemoteView();
        if (inflateSynchronously) {
            try {
                if (!isNewView) {
                    try {
                        newContentView.reapply(result.packageContext, existingView, remoteViewClickHandler);
                        existingWrapper.onReinflated();
                    } catch (Exception e) {
                        e = e;
                        handleInflationError(runningInflations, e, row.getStatusBarNotification(), callback);
                        runningInflations.put(Integer.valueOf(inflationId), new CancellationSignal());
                    }
                } else {
                    try {
                    } catch (Exception e2) {
                        e = e2;
                    }
                    try {
                        View v = newContentView.apply(result.packageContext, parentLayout, remoteViewClickHandler);
                        v.setIsRootNamespace(true);
                        applyCallback.setResultView(v);
                    } catch (Exception e3) {
                        e = e3;
                        handleInflationError(runningInflations, e, row.getStatusBarNotification(), callback);
                        runningInflations.put(Integer.valueOf(inflationId), new CancellationSignal());
                    }
                }
            } catch (Exception e4) {
                e = e4;
            }
        } else {
            RemoteViews.OnViewAppliedListener listener = new RemoteViews.OnViewAppliedListener() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentInflater.5
                public void onViewInflated(View v2) {
                    if (v2 instanceof ImageMessageConsumer) {
                        ((ImageMessageConsumer) v2).setImageResolver(ExpandableNotificationRow.this.getImageResolver());
                    }
                }

                public void onViewApplied(View v2) {
                    if (isNewView) {
                        v2.setIsRootNamespace(true);
                        applyCallback.setResultView(v2);
                    } else {
                        NotificationViewWrapper notificationViewWrapper = existingWrapper;
                        if (notificationViewWrapper != null) {
                            notificationViewWrapper.onReinflated();
                        }
                    }
                    runningInflations.remove(Integer.valueOf(inflationId));
                    NotificationContentInflater.finishIfDone(result, reInflateFlags, cachedContentViews, runningInflations, callback, ExpandableNotificationRow.this);
                }

                public void onError(Exception e5) {
                    try {
                        View newView = existingView;
                        if (isNewView) {
                            newView = newContentView.apply(result.packageContext, parentLayout, remoteViewClickHandler);
                        } else {
                            newContentView.reapply(result.packageContext, existingView, remoteViewClickHandler);
                        }
                        Log.wtf(NotificationContentInflater.TAG, "Async Inflation failed but normal inflation finished normally.", e5);
                        onViewApplied(newView);
                    } catch (Exception e6) {
                        runningInflations.remove(Integer.valueOf(inflationId));
                        NotificationContentInflater.handleInflationError(runningInflations, e5, ExpandableNotificationRow.this.getStatusBarNotification(), callback);
                    }
                }
            };
            if (isNewView) {
                cancellationSignal = newContentView.applyAsync(result.packageContext, parentLayout, null, listener, remoteViewClickHandler);
            } else {
                cancellationSignal = newContentView.reapplyAsync(result.packageContext, existingView, null, listener, remoteViewClickHandler);
            }
            runningInflations.put(Integer.valueOf(inflationId), cancellationSignal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void handleInflationError(HashMap<Integer, CancellationSignal> runningInflations, Exception e, StatusBarNotification notification, InflationCallback callback) {
        Assert.isMainThread();
        runningInflations.values().forEach($$Lambda$POlPJz26zF5Nt5Z2kVGSqFxN8Co.INSTANCE);
        if (callback != null) {
            callback.handleInflationException(notification, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean finishIfDone(InflationProgress result, int reInflateFlags, ArrayMap<Integer, RemoteViews> cachedContentViews, HashMap<Integer, CancellationSignal> runningInflations, InflationCallback endListener, ExpandableNotificationRow row) {
        Assert.isMainThread();
        NotificationEntry entry = row.getEntry();
        NotificationContentView privateLayout = row.getPrivateLayout();
        NotificationContentView publicLayout = row.getPublicLayout();
        if (runningInflations.isEmpty()) {
            if ((reInflateFlags & 1) != 0) {
                if (result.inflatedContentView == null) {
                    if (cachedContentViews.get(1) != null) {
                        cachedContentViews.put(1, result.newContentView);
                    }
                } else {
                    privateLayout.setContractedChild(result.inflatedContentView);
                    cachedContentViews.put(1, result.newContentView);
                }
            }
            if ((reInflateFlags & 2) != 0) {
                if (result.inflatedExpandedView != null) {
                    privateLayout.setExpandedChild(result.inflatedExpandedView);
                    cachedContentViews.put(2, result.newExpandedView);
                } else if (result.newExpandedView == null) {
                    privateLayout.setExpandedChild(null);
                    cachedContentViews.put(2, null);
                } else if (cachedContentViews.get(2) != null) {
                    cachedContentViews.put(2, result.newExpandedView);
                }
                if (result.newExpandedView != null) {
                    privateLayout.setExpandedInflatedSmartReplies(result.expandedInflatedSmartReplies);
                } else {
                    privateLayout.setExpandedInflatedSmartReplies(null);
                }
                row.setExpandable(result.newExpandedView != null);
            }
            if ((reInflateFlags & 4) != 0) {
                if (result.inflatedHeadsUpView != null) {
                    privateLayout.setHeadsUpChild(result.inflatedHeadsUpView);
                    cachedContentViews.put(4, result.newHeadsUpView);
                } else if (result.newHeadsUpView == null) {
                    privateLayout.setHeadsUpChild(null);
                    cachedContentViews.put(4, null);
                } else if (cachedContentViews.get(4) != null) {
                    cachedContentViews.put(4, result.newHeadsUpView);
                }
                if (result.newHeadsUpView != null) {
                    privateLayout.setHeadsUpInflatedSmartReplies(result.headsUpInflatedSmartReplies);
                } else {
                    privateLayout.setHeadsUpInflatedSmartReplies(null);
                }
            }
            if ((reInflateFlags & 8) != 0) {
                if (result.inflatedPublicView == null) {
                    if (cachedContentViews.get(8) != null) {
                        cachedContentViews.put(8, result.newPublicView);
                    }
                } else {
                    publicLayout.setContractedChild(result.inflatedPublicView);
                    cachedContentViews.put(8, result.newPublicView);
                }
            }
            entry.headsUpStatusBarText = result.headsUpStatusBarText;
            entry.headsUpStatusBarTextPublic = result.headsUpStatusBarTextPublic;
            if (endListener != null) {
                endListener.onAsyncInflationFinished(row.getEntry(), reInflateFlags);
            }
            return true;
        }
        return false;
    }

    private static RemoteViews createExpandedView(Notification.Builder builder, boolean isLowPriority) {
        RemoteViews bigContentView = builder.createBigContentView();
        if (bigContentView != null) {
            return bigContentView;
        }
        if (isLowPriority) {
            RemoteViews contentView = builder.createContentView();
            Notification.Builder.makeHeaderExpanded(contentView);
            return contentView;
        }
        return null;
    }

    private static RemoteViews createContentView(Notification.Builder builder, boolean isLowPriority, boolean useLarge) {
        if (isLowPriority) {
            return builder.makeLowPriorityContentView(false);
        }
        return builder.createContentView(useLarge);
    }

    @VisibleForTesting
    static boolean canReapplyRemoteView(RemoteViews newView, RemoteViews oldView) {
        if (newView == null && oldView == null) {
            return true;
        }
        return (newView == null || oldView == null || oldView.getPackage() == null || newView.getPackage() == null || !newView.getPackage().equals(oldView.getPackage()) || newView.getLayoutId() != oldView.getLayoutId() || oldView.hasFlags(1)) ? false : true;
    }

    public void setInflationCallback(InflationCallback callback) {
        this.mCallback = callback;
    }

    public void clearCachesAndReInflate() {
        this.mCachedContentViews.clear();
        inflateNotificationViews();
    }

    @VisibleForTesting
    void setInflateSynchronously(boolean inflateSynchronously) {
        this.mInflateSynchronously = inflateSynchronously;
    }

    /* loaded from: classes21.dex */
    public static class AsyncInflationTask extends AsyncTask<Void, Void, InflationProgress> implements InflationCallback, InflationTask {
        private final ArrayMap<Integer, RemoteViews> mCachedContentViews;
        private final InflationCallback mCallback;
        private CancellationSignal mCancellationSignal;
        private final Context mContext;
        private Exception mError;
        private final boolean mInflateSynchronously;
        private final boolean mIsChildInGroup;
        private final boolean mIsLowPriority;
        private int mReInflateFlags;
        private RemoteViews.OnClickHandler mRemoteViewClickHandler;
        private ExpandableNotificationRow mRow;
        private final StatusBarNotification mSbn;
        private final boolean mUsesIncreasedHeadsUpHeight;
        private final boolean mUsesIncreasedHeight;

        private AsyncInflationTask(StatusBarNotification notification, boolean inflateSynchronously, int reInflateFlags, ArrayMap<Integer, RemoteViews> cachedContentViews, ExpandableNotificationRow row, boolean isLowPriority, boolean isChildInGroup, boolean usesIncreasedHeight, boolean usesIncreasedHeadsUpHeight, InflationCallback callback, RemoteViews.OnClickHandler remoteViewClickHandler) {
            this.mRow = row;
            this.mSbn = notification;
            this.mInflateSynchronously = inflateSynchronously;
            this.mReInflateFlags = reInflateFlags;
            this.mCachedContentViews = cachedContentViews;
            this.mContext = this.mRow.getContext();
            this.mIsLowPriority = isLowPriority;
            this.mIsChildInGroup = isChildInGroup;
            this.mUsesIncreasedHeight = usesIncreasedHeight;
            this.mUsesIncreasedHeadsUpHeight = usesIncreasedHeadsUpHeight;
            this.mRemoteViewClickHandler = remoteViewClickHandler;
            this.mCallback = callback;
            NotificationEntry entry = row.getEntry();
            entry.setInflationTask(this);
        }

        @VisibleForTesting
        public int getReInflateFlags() {
            return this.mReInflateFlags;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public InflationProgress doInBackground(Void... params) {
            try {
                Notification.Builder recoveredBuilder = Notification.Builder.recoverBuilder(this.mContext, this.mSbn.getNotification());
                Context packageContext = this.mSbn.getPackageContext(this.mContext);
                Notification notification = this.mSbn.getNotification();
                if (notification.isMediaNotification()) {
                    MediaNotificationProcessor processor = new MediaNotificationProcessor(this.mContext, packageContext);
                    processor.processNotification(notification, recoveredBuilder);
                }
                InflationProgress inflationProgress = NotificationContentInflater.createRemoteViews(this.mReInflateFlags, recoveredBuilder, this.mIsLowPriority, this.mIsChildInGroup, this.mUsesIncreasedHeight, this.mUsesIncreasedHeadsUpHeight, packageContext);
                return NotificationContentInflater.inflateSmartReplyViews(inflationProgress, this.mReInflateFlags, this.mRow.getEntry(), this.mRow.getContext(), packageContext, this.mRow.getHeadsUpManager(), this.mRow.getExistingSmartRepliesAndActions());
            } catch (Exception e) {
                this.mError = e;
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(InflationProgress result) {
            Exception exc = this.mError;
            if (exc == null) {
                this.mCancellationSignal = NotificationContentInflater.apply(this.mInflateSynchronously, result, this.mReInflateFlags, this.mCachedContentViews, this.mRow, this.mRemoteViewClickHandler, this);
            } else {
                handleError(exc);
            }
        }

        private void handleError(Exception e) {
            this.mRow.getEntry().onInflationTaskFinished();
            StatusBarNotification sbn = this.mRow.getStatusBarNotification();
            String ident = sbn.getPackageName() + "/0x" + Integer.toHexString(sbn.getId());
            Log.e(StatusBar.TAG, "couldn't inflate view for notification " + ident, e);
            this.mCallback.handleInflationException(sbn, new InflationException("Couldn't inflate contentViews" + e));
        }

        @Override // com.android.systemui.statusbar.InflationTask
        public void abort() {
            cancel(true);
            CancellationSignal cancellationSignal = this.mCancellationSignal;
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
        }

        @Override // com.android.systemui.statusbar.InflationTask
        public void supersedeTask(InflationTask task) {
            if (task instanceof AsyncInflationTask) {
                this.mReInflateFlags |= ((AsyncInflationTask) task).mReInflateFlags;
            }
        }

        @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.InflationCallback
        public void handleInflationException(StatusBarNotification notification, Exception e) {
            handleError(e);
        }

        @Override // com.android.systemui.statusbar.notification.row.NotificationContentInflater.InflationCallback
        public void onAsyncInflationFinished(NotificationEntry entry, int inflatedFlags) {
            this.mRow.getEntry().onInflationTaskFinished();
            this.mRow.onNotificationUpdated();
            this.mCallback.onAsyncInflationFinished(this.mRow.getEntry(), inflatedFlags);
            this.mRow.getImageResolver().purgeCache();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public static class InflationProgress {
        private InflatedSmartReplies expandedInflatedSmartReplies;
        private InflatedSmartReplies headsUpInflatedSmartReplies;
        private CharSequence headsUpStatusBarText;
        private CharSequence headsUpStatusBarTextPublic;
        private View inflatedContentView;
        private View inflatedExpandedView;
        private View inflatedHeadsUpView;
        private View inflatedPublicView;
        private RemoteViews newContentView;
        private RemoteViews newExpandedView;
        private RemoteViews newHeadsUpView;
        private RemoteViews newPublicView;
        @VisibleForTesting
        Context packageContext;

        InflationProgress() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public static abstract class ApplyCallback {
        public abstract RemoteViews getRemoteView();

        public abstract void setResultView(View view);

        ApplyCallback() {
        }
    }
}
