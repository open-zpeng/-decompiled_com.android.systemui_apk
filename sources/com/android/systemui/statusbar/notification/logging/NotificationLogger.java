package com.android.systemui.statusbar.notification.logging;

import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationLogger implements StatusBarStateController.StateListener {
    private static final String TAG = "NotificationLogger";
    private static final int VISIBILITY_REPORT_MIN_DELAY_MS = 500;
    private boolean mDozing;
    private final NotificationEntryManager mEntryManager;
    private final ExpansionStateLogger mExpansionStateLogger;
    private HeadsUpManager mHeadsUpManager;
    private long mLastVisibilityReportUptimeMs;
    private NotificationListContainer mListContainer;
    private final NotificationListenerService mNotificationListener;
    private final UiOffloadThread mUiOffloadThread;
    private final ArraySet<NotificationVisibility> mCurrentlyVisibleNotifications = new ArraySet<>();
    protected Handler mHandler = new Handler();
    private final Object mDozingLock = new Object();
    protected final OnChildLocationsChangedListener mNotificationLocationsChangedListener = new OnChildLocationsChangedListener() { // from class: com.android.systemui.statusbar.notification.logging.NotificationLogger.1
        @Override // com.android.systemui.statusbar.notification.logging.NotificationLogger.OnChildLocationsChangedListener
        public void onChildLocationsChanged() {
            if (!NotificationLogger.this.mHandler.hasCallbacks(NotificationLogger.this.mVisibilityReporter)) {
                long nextReportUptimeMs = NotificationLogger.this.mLastVisibilityReportUptimeMs + 500;
                NotificationLogger.this.mHandler.postAtTime(NotificationLogger.this.mVisibilityReporter, nextReportUptimeMs);
            }
        }
    };
    protected Runnable mVisibilityReporter = new Runnable() { // from class: com.android.systemui.statusbar.notification.logging.NotificationLogger.2
        private final ArraySet<NotificationVisibility> mTmpNewlyVisibleNotifications = new ArraySet<>();
        private final ArraySet<NotificationVisibility> mTmpCurrentlyVisibleNotifications = new ArraySet<>();
        private final ArraySet<NotificationVisibility> mTmpNoLongerVisibleNotifications = new ArraySet<>();

        @Override // java.lang.Runnable
        public void run() {
            NotificationLogger.this.mLastVisibilityReportUptimeMs = SystemClock.uptimeMillis();
            ArrayList<NotificationEntry> activeNotifications = NotificationLogger.this.mEntryManager.getNotificationData().getActiveNotifications();
            int N = activeNotifications.size();
            for (int i = 0; i < N; i++) {
                NotificationEntry entry = activeNotifications.get(i);
                String key = entry.notification.getKey();
                boolean isVisible = NotificationLogger.this.mListContainer.isInVisibleLocation(entry);
                NotificationVisibility visObj = NotificationVisibility.obtain(key, i, N, isVisible, NotificationLogger.getNotificationLocation(entry));
                boolean previouslyVisible = NotificationLogger.this.mCurrentlyVisibleNotifications.contains(visObj);
                if (isVisible) {
                    this.mTmpCurrentlyVisibleNotifications.add(visObj);
                    if (!previouslyVisible) {
                        this.mTmpNewlyVisibleNotifications.add(visObj);
                    }
                } else {
                    visObj.recycle();
                }
            }
            this.mTmpNoLongerVisibleNotifications.addAll(NotificationLogger.this.mCurrentlyVisibleNotifications);
            this.mTmpNoLongerVisibleNotifications.removeAll((ArraySet<? extends NotificationVisibility>) this.mTmpCurrentlyVisibleNotifications);
            NotificationLogger.this.logNotificationVisibilityChanges(this.mTmpNewlyVisibleNotifications, this.mTmpNoLongerVisibleNotifications);
            NotificationLogger notificationLogger = NotificationLogger.this;
            notificationLogger.recycleAllVisibilityObjects(notificationLogger.mCurrentlyVisibleNotifications);
            NotificationLogger.this.mCurrentlyVisibleNotifications.addAll((ArraySet) this.mTmpCurrentlyVisibleNotifications);
            ExpansionStateLogger expansionStateLogger = NotificationLogger.this.mExpansionStateLogger;
            ArraySet<NotificationVisibility> arraySet = this.mTmpCurrentlyVisibleNotifications;
            expansionStateLogger.onVisibilityChanged(arraySet, arraySet);
            NotificationLogger.this.recycleAllVisibilityObjects(this.mTmpNoLongerVisibleNotifications);
            this.mTmpCurrentlyVisibleNotifications.clear();
            this.mTmpNewlyVisibleNotifications.clear();
            this.mTmpNoLongerVisibleNotifications.clear();
        }
    };
    protected IStatusBarService mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));

    /* loaded from: classes21.dex */
    public interface OnChildLocationsChangedListener {
        void onChildLocationsChanged();
    }

    public static NotificationVisibility.NotificationLocation getNotificationLocation(NotificationEntry entry) {
        if (entry == null || entry.getRow() == null || entry.getRow().getViewState() == null) {
            return NotificationVisibility.NotificationLocation.LOCATION_UNKNOWN;
        }
        return convertNotificationLocation(entry.getRow().getViewState().location);
    }

    private static NotificationVisibility.NotificationLocation convertNotificationLocation(int location) {
        if (location != 1) {
            if (location != 2) {
                if (location != 4) {
                    if (location != 8) {
                        if (location != 16) {
                            if (location == 64) {
                                return NotificationVisibility.NotificationLocation.LOCATION_GONE;
                            }
                            return NotificationVisibility.NotificationLocation.LOCATION_UNKNOWN;
                        }
                        return NotificationVisibility.NotificationLocation.LOCATION_BOTTOM_STACK_HIDDEN;
                    }
                    return NotificationVisibility.NotificationLocation.LOCATION_BOTTOM_STACK_PEEKING;
                }
                return NotificationVisibility.NotificationLocation.LOCATION_MAIN_AREA;
            }
            return NotificationVisibility.NotificationLocation.LOCATION_HIDDEN_TOP;
        }
        return NotificationVisibility.NotificationLocation.LOCATION_FIRST_HEADS_UP;
    }

    @Inject
    public NotificationLogger(NotificationListener notificationListener, UiOffloadThread uiOffloadThread, NotificationEntryManager entryManager, StatusBarStateController statusBarStateController, ExpansionStateLogger expansionStateLogger) {
        this.mNotificationListener = notificationListener;
        this.mUiOffloadThread = uiOffloadThread;
        this.mEntryManager = entryManager;
        this.mExpansionStateLogger = expansionStateLogger;
        statusBarStateController.addCallback(this);
        entryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.notification.logging.NotificationLogger.3
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryRemoved(NotificationEntry entry, NotificationVisibility visibility, boolean removedByUser) {
                if (removedByUser && visibility != null) {
                    NotificationLogger.this.logNotificationClear(entry.key, entry.notification, visibility);
                }
                NotificationLogger.this.mExpansionStateLogger.onEntryRemoved(entry.key);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onEntryReinflated(NotificationEntry entry) {
                NotificationLogger.this.mExpansionStateLogger.onEntryReinflated(entry.key);
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onInflationError(StatusBarNotification notification, Exception exception) {
                NotificationLogger.this.logNotificationError(notification, exception);
            }
        });
    }

    public void setUpWithContainer(NotificationListContainer listContainer) {
        this.mListContainer = listContainer;
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void stopNotificationLogging() {
        if (!this.mCurrentlyVisibleNotifications.isEmpty()) {
            logNotificationVisibilityChanges(Collections.emptyList(), this.mCurrentlyVisibleNotifications);
            recycleAllVisibilityObjects(this.mCurrentlyVisibleNotifications);
        }
        this.mHandler.removeCallbacks(this.mVisibilityReporter);
        this.mListContainer.setChildLocationsChangedListener(null);
    }

    public void startNotificationLogging() {
        this.mListContainer.setChildLocationsChangedListener(this.mNotificationLocationsChangedListener);
        this.mNotificationLocationsChangedListener.onChildLocationsChanged();
    }

    private void setDozing(boolean dozing) {
        synchronized (this.mDozingLock) {
            this.mDozing = dozing;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logNotificationClear(String key, StatusBarNotification notification, NotificationVisibility nv) {
        String pkg = notification.getPackageName();
        String tag = notification.getTag();
        int id = notification.getId();
        int userId = notification.getUserId();
        int dismissalSurface = 3;
        try {
            try {
                if (this.mHeadsUpManager.isAlerting(key)) {
                    dismissalSurface = 1;
                } else if (this.mListContainer.hasPulsingNotifications()) {
                    dismissalSurface = 2;
                }
                this.mBarService.onNotificationClear(pkg, tag, id, userId, notification.getKey(), dismissalSurface, 1, nv);
            } catch (RemoteException e) {
            }
        } catch (RemoteException e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logNotificationError(StatusBarNotification notification, Exception exception) {
        try {
            this.mBarService.onNotificationError(notification.getPackageName(), notification.getTag(), notification.getId(), notification.getUid(), notification.getInitialPid(), exception.getMessage(), notification.getUserId());
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logNotificationVisibilityChanges(Collection<NotificationVisibility> newlyVisible, Collection<NotificationVisibility> noLongerVisible) {
        if (newlyVisible.isEmpty() && noLongerVisible.isEmpty()) {
            return;
        }
        final NotificationVisibility[] newlyVisibleAr = cloneVisibilitiesAsArr(newlyVisible);
        final NotificationVisibility[] noLongerVisibleAr = cloneVisibilitiesAsArr(noLongerVisible);
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.notification.logging.-$$Lambda$NotificationLogger$e3uK-rBablkegG4HWqs1WzubMAs
            @Override // java.lang.Runnable
            public final void run() {
                NotificationLogger.this.lambda$logNotificationVisibilityChanges$0$NotificationLogger(newlyVisibleAr, noLongerVisibleAr);
            }
        });
    }

    public /* synthetic */ void lambda$logNotificationVisibilityChanges$0$NotificationLogger(NotificationVisibility[] newlyVisibleAr, NotificationVisibility[] noLongerVisibleAr) {
        try {
            this.mBarService.onNotificationVisibilityChanged(newlyVisibleAr, noLongerVisibleAr);
        } catch (RemoteException e) {
        }
        int N = newlyVisibleAr.length;
        if (N > 0) {
            String[] newlyVisibleKeyAr = new String[N];
            for (int i = 0; i < N; i++) {
                newlyVisibleKeyAr[i] = newlyVisibleAr[i].key;
            }
            synchronized (this.mDozingLock) {
                if (!this.mDozing) {
                    try {
                        this.mNotificationListener.setNotificationsShown(newlyVisibleKeyAr);
                    } catch (RuntimeException e2) {
                        Log.d(TAG, "failed setNotificationsShown: ", e2);
                    }
                }
            }
        }
        recycleAllVisibilityObjects(newlyVisibleAr);
        recycleAllVisibilityObjects(noLongerVisibleAr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recycleAllVisibilityObjects(ArraySet<NotificationVisibility> array) {
        int N = array.size();
        for (int i = 0; i < N; i++) {
            array.valueAt(i).recycle();
        }
        array.clear();
    }

    private void recycleAllVisibilityObjects(NotificationVisibility[] array) {
        int N = array.length;
        for (int i = 0; i < N; i++) {
            if (array[i] != null) {
                array[i].recycle();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NotificationVisibility[] cloneVisibilitiesAsArr(Collection<NotificationVisibility> c) {
        NotificationVisibility[] array = new NotificationVisibility[c.size()];
        int i = 0;
        for (NotificationVisibility nv : c) {
            if (nv != null) {
                array[i] = nv.clone();
            }
            i++;
        }
        return array;
    }

    @VisibleForTesting
    public Runnable getVisibilityReporter() {
        return this.mVisibilityReporter;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        setDozing(isDozing);
    }

    public void onExpansionChanged(String key, boolean isUserAction, boolean isExpanded) {
        NotificationVisibility.NotificationLocation location = getNotificationLocation(this.mEntryManager.getNotificationData().get(key));
        this.mExpansionStateLogger.onExpansionChanged(key, isUserAction, isExpanded, location);
    }

    @VisibleForTesting
    public void setVisibilityReporter(Runnable visibilityReporter) {
        this.mVisibilityReporter = visibilityReporter;
    }

    /* loaded from: classes21.dex */
    public static class ExpansionStateLogger {
        private final UiOffloadThread mUiOffloadThread;
        private final Map<String, State> mExpansionStates = new ArrayMap();
        private final Map<String, Boolean> mLoggedExpansionState = new ArrayMap();
        @VisibleForTesting
        IStatusBarService mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));

        @Inject
        public ExpansionStateLogger(UiOffloadThread uiOffloadThread) {
            this.mUiOffloadThread = uiOffloadThread;
        }

        @VisibleForTesting
        void onExpansionChanged(String key, boolean isUserAction, boolean isExpanded, NotificationVisibility.NotificationLocation location) {
            State state = getState(key);
            state.mIsUserAction = Boolean.valueOf(isUserAction);
            state.mIsExpanded = Boolean.valueOf(isExpanded);
            state.mLocation = location;
            maybeNotifyOnNotificationExpansionChanged(key, state);
        }

        @VisibleForTesting
        void onVisibilityChanged(Collection<NotificationVisibility> newlyVisible, Collection<NotificationVisibility> noLongerVisible) {
            NotificationVisibility[] newlyVisibleAr = NotificationLogger.cloneVisibilitiesAsArr(newlyVisible);
            NotificationVisibility[] noLongerVisibleAr = NotificationLogger.cloneVisibilitiesAsArr(noLongerVisible);
            for (NotificationVisibility nv : newlyVisibleAr) {
                State state = getState(nv.key);
                state.mIsVisible = true;
                state.mLocation = nv.location;
                maybeNotifyOnNotificationExpansionChanged(nv.key, state);
            }
            for (NotificationVisibility nv2 : noLongerVisibleAr) {
                getState(nv2.key).mIsVisible = false;
            }
        }

        @VisibleForTesting
        void onEntryRemoved(String key) {
            this.mExpansionStates.remove(key);
            this.mLoggedExpansionState.remove(key);
        }

        @VisibleForTesting
        void onEntryReinflated(String key) {
            this.mLoggedExpansionState.remove(key);
        }

        private State getState(String key) {
            State state = this.mExpansionStates.get(key);
            if (state == null) {
                State state2 = new State();
                this.mExpansionStates.put(key, state2);
                return state2;
            }
            return state;
        }

        private void maybeNotifyOnNotificationExpansionChanged(final String key, State state) {
            if (!state.isFullySet() || !state.mIsVisible.booleanValue()) {
                return;
            }
            Boolean loggedExpansionState = this.mLoggedExpansionState.get(key);
            if (loggedExpansionState == null && !state.mIsExpanded.booleanValue()) {
                return;
            }
            if (loggedExpansionState != null && state.mIsExpanded == loggedExpansionState) {
                return;
            }
            this.mLoggedExpansionState.put(key, state.mIsExpanded);
            final State stateToBeLogged = new State(state);
            this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.notification.logging.-$$Lambda$NotificationLogger$ExpansionStateLogger$2Eiyi73G6QB8CNmBwaixENnG5Co
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationLogger.ExpansionStateLogger.this.lambda$maybeNotifyOnNotificationExpansionChanged$0$NotificationLogger$ExpansionStateLogger(key, stateToBeLogged);
                }
            });
        }

        public /* synthetic */ void lambda$maybeNotifyOnNotificationExpansionChanged$0$NotificationLogger$ExpansionStateLogger(String key, State stateToBeLogged) {
            try {
                this.mBarService.onNotificationExpansionChanged(key, stateToBeLogged.mIsUserAction.booleanValue(), stateToBeLogged.mIsExpanded.booleanValue(), stateToBeLogged.mLocation.ordinal());
            } catch (RemoteException e) {
                Log.e(NotificationLogger.TAG, "Failed to call onNotificationExpansionChanged: ", e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public static class State {
            @Nullable
            Boolean mIsExpanded;
            @Nullable
            Boolean mIsUserAction;
            @Nullable
            Boolean mIsVisible;
            @Nullable
            NotificationVisibility.NotificationLocation mLocation;

            private State() {
            }

            private State(State state) {
                this.mIsUserAction = state.mIsUserAction;
                this.mIsExpanded = state.mIsExpanded;
                this.mIsVisible = state.mIsVisible;
                this.mLocation = state.mLocation;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public boolean isFullySet() {
                return (this.mIsUserAction == null || this.mIsExpanded == null || this.mIsVisible == null || this.mLocation == null) ? false : true;
            }
        }
    }
}
