package com.android.systemui.statusbar;

import android.app.Notification;
import android.app.RemoteInput;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import androidx.annotation.VisibleForTesting;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes21.dex */
public class NotificationUiAdjustment {
    public final String key;
    public final List<Notification.Action> smartActions;
    public final CharSequence[] smartReplies;

    @VisibleForTesting
    NotificationUiAdjustment(String key, List<Notification.Action> smartActions, CharSequence[] smartReplies) {
        List<Notification.Action> arrayList;
        this.key = key;
        if (smartActions == null) {
            arrayList = Collections.emptyList();
        } else {
            arrayList = new ArrayList<>(smartActions);
        }
        this.smartActions = arrayList;
        this.smartReplies = smartReplies == null ? new CharSequence[0] : (CharSequence[]) smartReplies.clone();
    }

    public static NotificationUiAdjustment extractFromNotificationEntry(NotificationEntry entry) {
        return new NotificationUiAdjustment(entry.key, entry.systemGeneratedSmartActions, entry.systemGeneratedSmartReplies);
    }

    public static boolean needReinflate(NotificationUiAdjustment oldAdjustment, NotificationUiAdjustment newAdjustment) {
        if (oldAdjustment == newAdjustment) {
            return false;
        }
        return areDifferent(oldAdjustment.smartActions, newAdjustment.smartActions) || !Arrays.equals(oldAdjustment.smartReplies, newAdjustment.smartReplies);
    }

    public static boolean areDifferent(List<Notification.Action> first, List<Notification.Action> second) {
        if (first == second) {
            return false;
        }
        if (first == null || second == null || first.size() != second.size()) {
            return true;
        }
        for (int i = 0; i < first.size(); i++) {
            Notification.Action firstAction = first.get(i);
            Notification.Action secondAction = second.get(i);
            if (!TextUtils.equals(firstAction.title, secondAction.title) || areDifferent(firstAction.getIcon(), secondAction.getIcon()) || !Objects.equals(firstAction.actionIntent, secondAction.actionIntent) || areDifferent(firstAction.getRemoteInputs(), secondAction.getRemoteInputs())) {
                return true;
            }
        }
        return false;
    }

    private static boolean areDifferent(Icon first, Icon second) {
        if (first == second) {
            return false;
        }
        if (first == null || second == null) {
            return true;
        }
        return true ^ first.sameAs(second);
    }

    private static boolean areDifferent(RemoteInput[] first, RemoteInput[] second) {
        if (first == second) {
            return false;
        }
        if (first == null || second == null || first.length != second.length) {
            return true;
        }
        for (int i = 0; i < first.length; i++) {
            RemoteInput firstRemoteInput = first[i];
            RemoteInput secondRemoteInput = second[i];
            if (!TextUtils.equals(firstRemoteInput.getLabel(), secondRemoteInput.getLabel()) || areDifferent(firstRemoteInput.getChoices(), secondRemoteInput.getChoices())) {
                return true;
            }
        }
        return false;
    }

    private static boolean areDifferent(CharSequence[] first, CharSequence[] second) {
        if (first == second) {
            return false;
        }
        if (first == null || second == null || first.length != second.length) {
            return true;
        }
        for (int i = 0; i < first.length; i++) {
            CharSequence firstCharSequence = first[i];
            CharSequence secondCharSequence = second[i];
            if (!TextUtils.equals(firstCharSequence, secondCharSequence)) {
                return true;
            }
        }
        return false;
    }
}
