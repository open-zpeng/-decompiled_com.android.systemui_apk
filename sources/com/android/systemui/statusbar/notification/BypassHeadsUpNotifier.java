package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.media.MediaMetadata;
import android.provider.Settings;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.notification.collection.NotificationData;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.tuner.TunerService;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import javax.inject.Inject;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: BypassHeadsUpNotifier.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B?\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0002\u0010\u0011J\u0010\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020\u0013H\u0002J\b\u0010 \u001a\u00020\u0015H\u0002J\u001a\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010%\u001a\u00020&H\u0016J\b\u0010'\u001a\u00020\"H\u0016J\u000e\u0010(\u001a\u00020\"2\u0006\u0010\u0016\u001a\u00020\u0017J\u0012\u0010)\u001a\u00020\"2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0013H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082.¢\u0006\u0002\n\u0000R$\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0015@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006*"}, d2 = {"Lcom/android/systemui/statusbar/notification/BypassHeadsUpNotifier;", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController$StateListener;", "Lcom/android/systemui/statusbar/NotificationMediaManager$MediaListener;", "context", "Landroid/content/Context;", "bypassController", "Lcom/android/systemui/statusbar/phone/KeyguardBypassController;", "statusBarStateController", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController;", "headsUpManager", "Lcom/android/systemui/statusbar/phone/HeadsUpManagerPhone;", "notificationLockscreenUserManager", "Lcom/android/systemui/statusbar/NotificationLockscreenUserManager;", "mediaManager", "Lcom/android/systemui/statusbar/NotificationMediaManager;", "tunerService", "Lcom/android/systemui/tuner/TunerService;", "(Landroid/content/Context;Lcom/android/systemui/statusbar/phone/KeyguardBypassController;Lcom/android/systemui/plugins/statusbar/StatusBarStateController;Lcom/android/systemui/statusbar/phone/HeadsUpManagerPhone;Lcom/android/systemui/statusbar/NotificationLockscreenUserManager;Lcom/android/systemui/statusbar/NotificationMediaManager;Lcom/android/systemui/tuner/TunerService;)V", "currentMediaEntry", "Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;", VuiConstants.ELEMENT_ENABLED, "", "entryManager", "Lcom/android/systemui/statusbar/notification/NotificationEntryManager;", VuiConstants.ELEMENT_VALUE, "fullyAwake", "getFullyAwake", "()Z", "setFullyAwake", "(Z)V", "canAutoHeadsUp", "entry", "isAutoHeadsUpAllowed", "onMetadataOrStateChanged", "", "metadata", "Landroid/media/MediaMetadata;", "state", "", "onStatePostChange", "setUp", "updateAutoHeadsUp", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class BypassHeadsUpNotifier implements StatusBarStateController.StateListener, NotificationMediaManager.MediaListener {
    private final KeyguardBypassController bypassController;
    private final Context context;
    private NotificationEntry currentMediaEntry;
    private boolean enabled;
    private NotificationEntryManager entryManager;
    private boolean fullyAwake;
    private final HeadsUpManagerPhone headsUpManager;
    private final NotificationMediaManager mediaManager;
    private final NotificationLockscreenUserManager notificationLockscreenUserManager;
    private final StatusBarStateController statusBarStateController;

    @Inject
    public BypassHeadsUpNotifier(@NotNull Context context, @NotNull KeyguardBypassController bypassController, @NotNull StatusBarStateController statusBarStateController, @NotNull HeadsUpManagerPhone headsUpManager, @NotNull NotificationLockscreenUserManager notificationLockscreenUserManager, @NotNull NotificationMediaManager mediaManager, @NotNull TunerService tunerService) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(bypassController, "bypassController");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(headsUpManager, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(notificationLockscreenUserManager, "notificationLockscreenUserManager");
        Intrinsics.checkParameterIsNotNull(mediaManager, "mediaManager");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        this.context = context;
        this.bypassController = bypassController;
        this.statusBarStateController = statusBarStateController;
        this.headsUpManager = headsUpManager;
        this.notificationLockscreenUserManager = notificationLockscreenUserManager;
        this.mediaManager = mediaManager;
        this.enabled = true;
        this.statusBarStateController.addCallback(this);
        tunerService.addTunable(new TunerService.Tunable() { // from class: com.android.systemui.statusbar.notification.BypassHeadsUpNotifier.1
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public final void onTuningChanged(String $noName_0, String $noName_1) {
                BypassHeadsUpNotifier bypassHeadsUpNotifier = BypassHeadsUpNotifier.this;
                bypassHeadsUpNotifier.enabled = Settings.Secure.getIntForUser(bypassHeadsUpNotifier.context.getContentResolver(), "show_media_when_bypassing", 0, KeyguardUpdateMonitor.getCurrentUser()) != 0;
            }
        }, "show_media_when_bypassing");
    }

    public final boolean getFullyAwake() {
        return this.fullyAwake;
    }

    public final void setFullyAwake(boolean value) {
        this.fullyAwake = value;
        if (value) {
            updateAutoHeadsUp(this.currentMediaEntry);
        }
    }

    public final void setUp(@NotNull NotificationEntryManager entryManager) {
        Intrinsics.checkParameterIsNotNull(entryManager, "entryManager");
        this.entryManager = entryManager;
        this.mediaManager.addCallback(this);
    }

    @Override // com.android.systemui.statusbar.NotificationMediaManager.MediaListener
    public void onMetadataOrStateChanged(@Nullable MediaMetadata metadata, int state) {
        NotificationEntry previous = this.currentMediaEntry;
        NotificationEntryManager notificationEntryManager = this.entryManager;
        if (notificationEntryManager == null) {
            Intrinsics.throwUninitializedPropertyAccessException("entryManager");
        }
        NotificationEntry newEntry = notificationEntryManager.getNotificationData().get(this.mediaManager.getMediaNotificationKey());
        if (!NotificationMediaManager.isPlayingState(state)) {
            newEntry = null;
        }
        this.currentMediaEntry = newEntry;
        updateAutoHeadsUp(previous);
        updateAutoHeadsUp(this.currentMediaEntry);
    }

    private final void updateAutoHeadsUp(NotificationEntry entry) {
        if (entry != null) {
            boolean autoHeadsUp = Intrinsics.areEqual(entry, this.currentMediaEntry) && canAutoHeadsUp(entry);
            entry.setAutoHeadsUp(autoHeadsUp);
            if (autoHeadsUp) {
                this.headsUpManager.showNotification(entry);
            }
        }
    }

    private final boolean canAutoHeadsUp(NotificationEntry entry) {
        if (isAutoHeadsUpAllowed() && !entry.isSensitive() && this.notificationLockscreenUserManager.shouldShowOnKeyguard(entry)) {
            NotificationEntryManager notificationEntryManager = this.entryManager;
            if (notificationEntryManager == null) {
                Intrinsics.throwUninitializedPropertyAccessException("entryManager");
            }
            NotificationData notificationData = notificationEntryManager.getNotificationData();
            Intrinsics.checkExpressionValueIsNotNull(notificationData, "entryManager.notificationData");
            return notificationData.getActiveNotifications().contains(entry);
        }
        return false;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStatePostChange() {
        updateAutoHeadsUp(this.currentMediaEntry);
    }

    private final boolean isAutoHeadsUpAllowed() {
        return this.enabled && this.bypassController.getBypassEnabled() && this.statusBarStateController.getState() == 1 && this.fullyAwake;
    }
}
