package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.Dependency;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.SmartReplyView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes21.dex */
public class InflatedSmartReplies {
    private final SmartRepliesAndActions mSmartRepliesAndActions;
    private final SmartReplyView mSmartReplyView;
    private final List<Button> mSmartSuggestionButtons;
    private static final String TAG = "InflatedSmartReplies";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    private InflatedSmartReplies(SmartReplyView smartReplyView, List<Button> smartSuggestionButtons, SmartRepliesAndActions smartRepliesAndActions) {
        this.mSmartReplyView = smartReplyView;
        this.mSmartSuggestionButtons = smartSuggestionButtons;
        this.mSmartRepliesAndActions = smartRepliesAndActions;
    }

    public SmartReplyView getSmartReplyView() {
        return this.mSmartReplyView;
    }

    public List<Button> getSmartSuggestionButtons() {
        return this.mSmartSuggestionButtons;
    }

    public SmartRepliesAndActions getSmartRepliesAndActions() {
        return this.mSmartRepliesAndActions;
    }

    public static InflatedSmartReplies inflate(Context context, Context packageContext, NotificationEntry entry, SmartReplyConstants smartReplyConstants, SmartReplyController smartReplyController, HeadsUpManager headsUpManager, SmartRepliesAndActions existingSmartRepliesAndActions) {
        SmartRepliesAndActions newSmartRepliesAndActions = chooseSmartRepliesAndActions(smartReplyConstants, entry);
        if (!shouldShowSmartReplyView(entry, newSmartRepliesAndActions)) {
            return new InflatedSmartReplies(null, null, newSmartRepliesAndActions);
        }
        boolean delayOnClickListener = !areSuggestionsSimilar(existingSmartRepliesAndActions, newSmartRepliesAndActions);
        SmartReplyView smartReplyView = SmartReplyView.inflate(context);
        List<Button> suggestionButtons = new ArrayList<>();
        if (newSmartRepliesAndActions.smartReplies != null) {
            suggestionButtons.addAll(smartReplyView.inflateRepliesFromRemoteInput(newSmartRepliesAndActions.smartReplies, smartReplyController, entry, delayOnClickListener));
        }
        if (newSmartRepliesAndActions.smartActions != null) {
            suggestionButtons.addAll(smartReplyView.inflateSmartActions(packageContext, newSmartRepliesAndActions.smartActions, smartReplyController, entry, headsUpManager, delayOnClickListener));
        }
        return new InflatedSmartReplies(smartReplyView, suggestionButtons, newSmartRepliesAndActions);
    }

    @VisibleForTesting
    static boolean areSuggestionsSimilar(SmartRepliesAndActions left, SmartRepliesAndActions right) {
        if (left == right) {
            return true;
        }
        if (left != null && right != null && Arrays.equals(left.getSmartReplies(), right.getSmartReplies())) {
            return true ^ NotificationUiAdjustment.areDifferent(left.getSmartActions(), right.getSmartActions());
        }
        return false;
    }

    public static boolean shouldShowSmartReplyView(NotificationEntry entry, SmartRepliesAndActions smartRepliesAndActions) {
        if (smartRepliesAndActions.smartReplies == null && smartRepliesAndActions.smartActions == null) {
            return false;
        }
        boolean showingSpinner = entry.notification.getNotification().extras.getBoolean("android.remoteInputSpinner", false);
        if (showingSpinner) {
            return false;
        }
        boolean hideSmartReplies = entry.notification.getNotification().extras.getBoolean("android.hideSmartReplies", false);
        return !hideSmartReplies;
    }

    public static SmartRepliesAndActions chooseSmartRepliesAndActions(SmartReplyConstants smartReplyConstants, NotificationEntry entry) {
        boolean enableAppGeneratedSmartReplies;
        boolean appGeneratedSmartRepliesExist;
        boolean useGeneratedReplies;
        Notification notification = entry.notification.getNotification();
        Pair<RemoteInput, Notification.Action> remoteInputActionPair = notification.findRemoteInputActionPair(false);
        Pair<RemoteInput, Notification.Action> freeformRemoteInputActionPair = notification.findRemoteInputActionPair(true);
        if (!smartReplyConstants.isEnabled()) {
            if (DEBUG) {
                Log.d(TAG, "Smart suggestions not enabled, not adding suggestions for " + entry.notification.getKey());
            }
            return new SmartRepliesAndActions(null, null);
        }
        if (smartReplyConstants.requiresTargetingP() && entry.targetSdk < 28) {
            enableAppGeneratedSmartReplies = false;
        } else {
            enableAppGeneratedSmartReplies = true;
        }
        if (!enableAppGeneratedSmartReplies || remoteInputActionPair == null || ArrayUtils.isEmpty(((RemoteInput) remoteInputActionPair.first).getChoices()) || ((Notification.Action) remoteInputActionPair.second).actionIntent == null) {
            appGeneratedSmartRepliesExist = false;
        } else {
            appGeneratedSmartRepliesExist = true;
        }
        List<Notification.Action> appGeneratedSmartActions = notification.getContextualActions();
        boolean appGeneratedSmartActionsExist = !appGeneratedSmartActions.isEmpty();
        SmartReplyView.SmartReplies smartReplies = null;
        SmartReplyView.SmartActions smartActions = null;
        if (appGeneratedSmartRepliesExist) {
            smartReplies = new SmartReplyView.SmartReplies(((RemoteInput) remoteInputActionPair.first).getChoices(), (RemoteInput) remoteInputActionPair.first, ((Notification.Action) remoteInputActionPair.second).actionIntent, false);
        }
        if (appGeneratedSmartActionsExist) {
            smartActions = new SmartReplyView.SmartActions(appGeneratedSmartActions, false);
        }
        if (!appGeneratedSmartRepliesExist && !appGeneratedSmartActionsExist) {
            if (ArrayUtils.isEmpty(entry.systemGeneratedSmartReplies) || freeformRemoteInputActionPair == null || !((Notification.Action) freeformRemoteInputActionPair.second).getAllowGeneratedReplies() || ((Notification.Action) freeformRemoteInputActionPair.second).actionIntent == null) {
                useGeneratedReplies = false;
            } else {
                useGeneratedReplies = true;
            }
            if (useGeneratedReplies) {
                smartReplies = new SmartReplyView.SmartReplies(entry.systemGeneratedSmartReplies, (RemoteInput) freeformRemoteInputActionPair.first, ((Notification.Action) freeformRemoteInputActionPair.second).actionIntent, true);
            }
            boolean useSmartActions = !ArrayUtils.isEmpty(entry.systemGeneratedSmartActions) && notification.getAllowSystemGeneratedContextualActions();
            if (useSmartActions) {
                List<Notification.Action> systemGeneratedActions = entry.systemGeneratedSmartActions;
                ActivityManagerWrapper activityManagerWrapper = (ActivityManagerWrapper) Dependency.get(ActivityManagerWrapper.class);
                if (activityManagerWrapper.isLockTaskKioskModeActive()) {
                    systemGeneratedActions = filterWhiteListedLockTaskApps(systemGeneratedActions);
                }
                smartActions = new SmartReplyView.SmartActions(systemGeneratedActions, true);
            }
        }
        return new SmartRepliesAndActions(smartReplies, smartActions);
    }

    private static List<Notification.Action> filterWhiteListedLockTaskApps(List<Notification.Action> actions) {
        PackageManagerWrapper packageManagerWrapper = (PackageManagerWrapper) Dependency.get(PackageManagerWrapper.class);
        DevicePolicyManagerWrapper devicePolicyManagerWrapper = (DevicePolicyManagerWrapper) Dependency.get(DevicePolicyManagerWrapper.class);
        List<Notification.Action> filteredActions = new ArrayList<>();
        for (Notification.Action action : actions) {
            if (action.actionIntent != null) {
                Intent intent = action.actionIntent.getIntent();
                ResolveInfo resolveInfo = packageManagerWrapper.resolveActivity(intent, 0);
                if (resolveInfo != null && devicePolicyManagerWrapper.isLockTaskPermitted(resolveInfo.activityInfo.packageName)) {
                    filteredActions.add(action);
                }
            }
        }
        return filteredActions;
    }

    public static boolean hasFreeformRemoteInput(NotificationEntry entry) {
        Notification notification = entry.notification.getNotification();
        return notification.findRemoteInputActionPair(true) != null;
    }

    /* loaded from: classes21.dex */
    public static class SmartRepliesAndActions {
        public final SmartReplyView.SmartActions smartActions;
        public final SmartReplyView.SmartReplies smartReplies;

        SmartRepliesAndActions(SmartReplyView.SmartReplies smartReplies, SmartReplyView.SmartActions smartActions) {
            this.smartReplies = smartReplies;
            this.smartActions = smartActions;
        }

        public CharSequence[] getSmartReplies() {
            SmartReplyView.SmartReplies smartReplies = this.smartReplies;
            return smartReplies == null ? new CharSequence[0] : smartReplies.choices;
        }

        public List<Notification.Action> getSmartActions() {
            SmartReplyView.SmartActions smartActions = this.smartActions;
            return smartActions == null ? Collections.emptyList() : smartActions.actions;
        }
    }
}
