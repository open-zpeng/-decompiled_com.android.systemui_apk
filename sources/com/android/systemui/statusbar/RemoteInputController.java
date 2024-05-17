package com.android.systemui.statusbar;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.Context;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.Pair;
import com.android.internal.util.Preconditions;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class RemoteInputController {
    private static final boolean ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
    private final Delegate mDelegate;
    private final ArrayList<Pair<WeakReference<NotificationEntry>, Object>> mOpen = new ArrayList<>();
    private final ArrayMap<String, Object> mSpinning = new ArrayMap<>();
    private final ArrayList<Callback> mCallbacks = new ArrayList<>(3);

    /* loaded from: classes21.dex */
    public interface Delegate {
        void lockScrollTo(NotificationEntry notificationEntry);

        void requestDisallowLongPressAndDismiss();

        void setRemoteInputActive(NotificationEntry notificationEntry, boolean z);
    }

    public RemoteInputController(Delegate delegate) {
        this.mDelegate = delegate;
    }

    public static void processForRemoteInput(Notification n, Context context) {
        RemoteInput[] remoteInputs;
        if (ENABLE_REMOTE_INPUT && n.extras != null && n.extras.containsKey("android.wearable.EXTENSIONS")) {
            if (n.actions == null || n.actions.length == 0) {
                Notification.Action viableAction = null;
                Notification.WearableExtender we = new Notification.WearableExtender(n);
                List<Notification.Action> actions = we.getActions();
                int numActions = actions.size();
                for (int i = 0; i < numActions; i++) {
                    Notification.Action action = actions.get(i);
                    if (action != null && (remoteInputs = action.getRemoteInputs()) != null) {
                        int length = remoteInputs.length;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= length) {
                                break;
                            }
                            RemoteInput ri = remoteInputs[i2];
                            if (!ri.getAllowFreeFormInput()) {
                                i2++;
                            } else {
                                viableAction = action;
                                break;
                            }
                        }
                        if (viableAction != null) {
                            break;
                        }
                    }
                }
                if (viableAction != null) {
                    Notification.Builder rebuilder = Notification.Builder.recoverBuilder(context, n);
                    rebuilder.setActions(viableAction);
                    rebuilder.build();
                }
            }
        }
    }

    public void addRemoteInput(NotificationEntry entry, Object token) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(token);
        boolean found = pruneWeakThenRemoveAndContains(entry, null, token);
        if (!found) {
            this.mOpen.add(new Pair<>(new WeakReference(entry), token));
        }
        apply(entry);
    }

    public void removeRemoteInput(NotificationEntry entry, Object token) {
        Preconditions.checkNotNull(entry);
        pruneWeakThenRemoveAndContains(null, entry, token);
        apply(entry);
    }

    public void addSpinning(String key, Object token) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(token);
        this.mSpinning.put(key, token);
    }

    public void removeSpinning(String key, Object token) {
        Preconditions.checkNotNull(key);
        if (token == null || this.mSpinning.get(key) == token) {
            this.mSpinning.remove(key);
        }
    }

    public boolean isSpinning(String key) {
        return this.mSpinning.containsKey(key);
    }

    public boolean isSpinning(String key, Object token) {
        return this.mSpinning.get(key) == token;
    }

    private void apply(NotificationEntry entry) {
        this.mDelegate.setRemoteInputActive(entry, isRemoteInputActive(entry));
        boolean remoteInputActive = isRemoteInputActive();
        int N = this.mCallbacks.size();
        for (int i = 0; i < N; i++) {
            this.mCallbacks.get(i).onRemoteInputActive(remoteInputActive);
        }
    }

    public boolean isRemoteInputActive(NotificationEntry entry) {
        return pruneWeakThenRemoveAndContains(entry, null, null);
    }

    public boolean isRemoteInputActive() {
        pruneWeakThenRemoveAndContains(null, null, null);
        return !this.mOpen.isEmpty();
    }

    private boolean pruneWeakThenRemoveAndContains(NotificationEntry contains, NotificationEntry remove, Object removeToken) {
        boolean found = false;
        for (int i = this.mOpen.size() - 1; i >= 0; i--) {
            NotificationEntry item = (NotificationEntry) ((WeakReference) this.mOpen.get(i).first).get();
            Object itemToken = this.mOpen.get(i).second;
            boolean removeTokenMatches = removeToken == null || itemToken == removeToken;
            if (item == null || (item == remove && removeTokenMatches)) {
                this.mOpen.remove(i);
            } else if (item == contains) {
                if (removeToken != null && removeToken != itemToken) {
                    this.mOpen.remove(i);
                } else {
                    found = true;
                }
            }
        }
        return found;
    }

    public void addCallback(Callback callback) {
        Preconditions.checkNotNull(callback);
        this.mCallbacks.add(callback);
    }

    public void remoteInputSent(NotificationEntry entry) {
        int N = this.mCallbacks.size();
        for (int i = 0; i < N; i++) {
            this.mCallbacks.get(i).onRemoteInputSent(entry);
        }
    }

    public void closeRemoteInputs() {
        if (this.mOpen.size() == 0) {
            return;
        }
        ArrayList<NotificationEntry> list = new ArrayList<>(this.mOpen.size());
        for (int i = this.mOpen.size() - 1; i >= 0; i--) {
            NotificationEntry entry = (NotificationEntry) ((WeakReference) this.mOpen.get(i).first).get();
            if (entry != null && entry.rowExists()) {
                list.add(entry);
            }
        }
        int i2 = list.size();
        for (int i3 = i2 - 1; i3 >= 0; i3--) {
            NotificationEntry entry2 = list.get(i3);
            if (entry2.rowExists()) {
                entry2.closeRemoteInput();
            }
        }
    }

    public void requestDisallowLongPressAndDismiss() {
        this.mDelegate.requestDisallowLongPressAndDismiss();
    }

    public void lockScrollTo(NotificationEntry entry) {
        this.mDelegate.lockScrollTo(entry);
    }

    /* loaded from: classes21.dex */
    public interface Callback {
        default void onRemoteInputActive(boolean active) {
        }

        default void onRemoteInputSent(NotificationEntry entry) {
        }
    }
}
