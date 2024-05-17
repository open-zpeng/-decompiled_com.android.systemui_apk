package com.android.systemui.statusbar.notification.row;

import android.app.Dialog;
import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ParceledListSlice;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.row.NotificationInfo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.carmanager.controller.IIcmController;
import com.xiaopeng.systemui.helper.WindowHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ChannelEditorDialogController.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\r\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u00109\u001a\u00020:H\u0007J\u0010\u0010;\u001a\u00020:2\u0006\u0010<\u001a\u00020\fH\u0002J\b\u0010=\u001a\u00020:H\u0002J\b\u0010>\u001a\u00020\fH\u0002J\u0006\u0010?\u001a\u00020:J\b\u0010@\u001a\u00020:H\u0002J\u000e\u0010A\u001a\b\u0012\u0004\u0012\u00020\u00160BH\u0002J\u001c\u0010C\u001a\b\u0012\u0004\u0012\u00020\"0D2\f\u0010E\u001a\b\u0012\u0004\u0012\u00020\u00160DH\u0002J\u0010\u0010F\u001a\u00020%2\b\u0010G\u001a\u0004\u0018\u00010\nJ\b\u0010H\u001a\u00020:H\u0002J\u0010\u0010I\u001a\u00020:2\u0006\u0010J\u001a\u00020KH\u0007J\u0016\u0010L\u001a\u00020:2\f\u0010M\u001a\b\u0012\u0004\u0012\u00020\"0NH\u0002J>\u0010O\u001a\u00020:2\u0006\u0010\t\u001a\u00020\n2\u0006\u00103\u001a\u00020\n2\u0006\u0010P\u001a\u00020\u00122\f\u0010M\u001a\b\u0012\u0004\u0012\u00020\"0N2\u0006\u0010\u0007\u001a\u00020\b2\b\u00101\u001a\u0004\u0018\u000102J\u0016\u0010Q\u001a\u00020:2\u0006\u0010R\u001a\u00020\"2\u0006\u0010S\u001a\u00020\u0012J\b\u0010T\u001a\u00020:H\u0002J\u0018\u0010U\u001a\u00020:2\u0006\u0010R\u001a\u00020\"2\u0006\u0010V\u001a\u00020\u0012H\u0002J\u0006\u0010W\u001a\u00020:R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0013R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0017\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u001bX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00120!X\u0082\u0004¢\u0006\u0002\n\u0000R8\u0010#\u001a\u001e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020%0$j\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020%`&8\u0000X\u0081\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b'\u0010(\u001a\u0004\b)\u0010*R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010+\u001a\u0004\u0018\u00010,X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u0010\u00101\u001a\u0004\u0018\u000102X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00103\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\"\u00104\u001a\b\u0012\u0004\u0012\u00020\"0\u00158\u0000X\u0081\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b5\u0010(\u001a\u0004\b6\u00107R\u000e\u00108\u001a\u00020\u0012X\u0082D¢\u0006\u0002\n\u0000¨\u0006X"}, d2 = {"Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;", "", "c", "Landroid/content/Context;", "noMan", "Landroid/app/INotificationManager;", "(Landroid/content/Context;Landroid/app/INotificationManager;)V", "appIcon", "Landroid/graphics/drawable/Drawable;", "appName", "", "appNotificationsEnabled", "", "getAppNotificationsEnabled", "()Z", "setAppNotificationsEnabled", "(Z)V", "appUid", "", "Ljava/lang/Integer;", "channelGroupList", "", "Landroid/app/NotificationChannelGroup;", "context", "getContext", "()Landroid/content/Context;", "dialog", "Landroid/app/Dialog;", "getDialog", "()Landroid/app/Dialog;", "setDialog", "(Landroid/app/Dialog;)V", "edits", "", "Landroid/app/NotificationChannel;", "groupNameLookup", "Ljava/util/HashMap;", "", "Lkotlin/collections/HashMap;", "groupNameLookup$annotations", "()V", "getGroupNameLookup$name", "()Ljava/util/HashMap;", "onFinishListener", "Lcom/android/systemui/statusbar/notification/row/OnChannelEditorDialogFinishedListener;", "getOnFinishListener", "()Lcom/android/systemui/statusbar/notification/row/OnChannelEditorDialogFinishedListener;", "setOnFinishListener", "(Lcom/android/systemui/statusbar/notification/row/OnChannelEditorDialogFinishedListener;)V", "onSettingsClickListener", "Lcom/android/systemui/statusbar/notification/row/NotificationInfo$OnSettingsClickListener;", VuiConstants.SCENE_PACKAGE_NAME, "providedChannels", "providedChannels$annotations", "getProvidedChannels$name", "()Ljava/util/List;", "wmFlags", "apply", "", "applyAppNotificationsOn", "b", "buildGroupNameLookup", "checkAreAppNotificationsOn", IIcmController.CMD_CLOSE, "done", "fetchNotificationChannelGroups", "", "getDisplayableChannels", "Lkotlin/sequences/Sequence;", "groupList", "groupNameForId", "groupId", "initDialog", "launchSettings", "sender", "Landroid/view/View;", "padToFourChannels", "channels", "", "prepareDialogForApp", "uid", "proposeEditForChannel", "channel", "edit", "resetState", "setChannelImportance", "importance", "show", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class ChannelEditorDialogController {
    private Drawable appIcon;
    private String appName;
    private boolean appNotificationsEnabled;
    private Integer appUid;
    private final List<NotificationChannelGroup> channelGroupList;
    @NotNull
    private final Context context;
    @NotNull
    public Dialog dialog;
    private final Map<NotificationChannel, Integer> edits;
    @NotNull
    private final HashMap<String, CharSequence> groupNameLookup;
    private final INotificationManager noMan;
    @Nullable
    private OnChannelEditorDialogFinishedListener onFinishListener;
    private NotificationInfo.OnSettingsClickListener onSettingsClickListener;
    private String packageName;
    @NotNull
    private final List<NotificationChannel> providedChannels;
    private final int wmFlags;

    @VisibleForTesting
    public static /* synthetic */ void groupNameLookup$annotations() {
    }

    @VisibleForTesting
    public static /* synthetic */ void providedChannels$annotations() {
    }

    @Inject
    public ChannelEditorDialogController(@NotNull Context c, @NotNull INotificationManager noMan) {
        Intrinsics.checkParameterIsNotNull(c, "c");
        Intrinsics.checkParameterIsNotNull(noMan, "noMan");
        this.noMan = noMan;
        Context applicationContext = c.getApplicationContext();
        Intrinsics.checkExpressionValueIsNotNull(applicationContext, "c.applicationContext");
        this.context = applicationContext;
        this.providedChannels = new ArrayList();
        this.edits = new LinkedHashMap();
        this.appNotificationsEnabled = true;
        this.groupNameLookup = new HashMap<>();
        this.channelGroupList = new ArrayList();
        this.wmFlags = -2130444032;
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }

    @NotNull
    public final Dialog getDialog() {
        Dialog dialog = this.dialog;
        if (dialog == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
        }
        return dialog;
    }

    public final void setDialog(@NotNull Dialog dialog) {
        Intrinsics.checkParameterIsNotNull(dialog, "<set-?>");
        this.dialog = dialog;
    }

    @Nullable
    public final OnChannelEditorDialogFinishedListener getOnFinishListener() {
        return this.onFinishListener;
    }

    public final void setOnFinishListener(@Nullable OnChannelEditorDialogFinishedListener onChannelEditorDialogFinishedListener) {
        this.onFinishListener = onChannelEditorDialogFinishedListener;
    }

    @NotNull
    public final List<NotificationChannel> getProvidedChannels$name() {
        return this.providedChannels;
    }

    public final boolean getAppNotificationsEnabled() {
        return this.appNotificationsEnabled;
    }

    public final void setAppNotificationsEnabled(boolean z) {
        this.appNotificationsEnabled = z;
    }

    @NotNull
    public final HashMap<String, CharSequence> getGroupNameLookup$name() {
        return this.groupNameLookup;
    }

    public final void prepareDialogForApp(@NotNull String appName, @NotNull String packageName, int uid, @NotNull Set<NotificationChannel> channels, @NotNull Drawable appIcon, @Nullable NotificationInfo.OnSettingsClickListener onSettingsClickListener) {
        Intrinsics.checkParameterIsNotNull(appName, "appName");
        Intrinsics.checkParameterIsNotNull(packageName, "packageName");
        Intrinsics.checkParameterIsNotNull(channels, "channels");
        Intrinsics.checkParameterIsNotNull(appIcon, "appIcon");
        this.appName = appName;
        this.packageName = packageName;
        this.appUid = Integer.valueOf(uid);
        this.appIcon = appIcon;
        this.appNotificationsEnabled = checkAreAppNotificationsOn();
        this.onSettingsClickListener = onSettingsClickListener;
        this.channelGroupList.clear();
        this.channelGroupList.addAll(fetchNotificationChannelGroups());
        buildGroupNameLookup();
        padToFourChannels(channels);
    }

    private final void buildGroupNameLookup() {
        Iterable $receiver$iv = this.channelGroupList;
        for (Object element$iv : $receiver$iv) {
            NotificationChannelGroup group = (NotificationChannelGroup) element$iv;
            if (group.getId() != null) {
                String id = group.getId();
                Intrinsics.checkExpressionValueIsNotNull(id, "group.id");
                CharSequence name = group.getName();
                Intrinsics.checkExpressionValueIsNotNull(name, "group.name");
                this.groupNameLookup.put(id, name);
            }
        }
    }

    private final void padToFourChannels(Set<NotificationChannel> set) {
        this.providedChannels.clear();
        CollectionsKt.addAll(this.providedChannels, SequencesKt.take(CollectionsKt.asSequence(set), 4));
        CollectionsKt.addAll(this.providedChannels, SequencesKt.take(SequencesKt.distinct(SequencesKt.filterNot(getDisplayableChannels(CollectionsKt.asSequence(this.channelGroupList)), new Function1<NotificationChannel, Boolean>() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$padToFourChannels$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Boolean invoke(NotificationChannel notificationChannel) {
                return Boolean.valueOf(invoke2(notificationChannel));
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final boolean invoke2(@NotNull NotificationChannel it) {
                Intrinsics.checkParameterIsNotNull(it, "it");
                return ChannelEditorDialogController.this.getProvidedChannels$name().contains(it);
            }
        })), 4 - this.providedChannels.size()));
        if (this.providedChannels.size() == 1 && Intrinsics.areEqual("miscellaneous", this.providedChannels.get(0).getId())) {
            this.providedChannels.clear();
        }
    }

    private final Sequence<NotificationChannel> getDisplayableChannels(Sequence<NotificationChannelGroup> sequence) {
        Sequence channels = SequencesKt.flatMap(sequence, new Function1<NotificationChannelGroup, Sequence<? extends NotificationChannel>>() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$getDisplayableChannels$channels$1
            @Override // kotlin.jvm.functions.Function1
            @NotNull
            public final Sequence<NotificationChannel> invoke(@NotNull NotificationChannelGroup group) {
                Intrinsics.checkParameterIsNotNull(group, "group");
                List<NotificationChannel> channels2 = group.getChannels();
                Intrinsics.checkExpressionValueIsNotNull(channels2, "group.channels");
                return SequencesKt.filterNot(CollectionsKt.asSequence(channels2), new Function1<NotificationChannel, Boolean>() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$getDisplayableChannels$channels$1.1
                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Boolean invoke(NotificationChannel notificationChannel) {
                        return Boolean.valueOf(invoke2(notificationChannel));
                    }

                    /* renamed from: invoke  reason: avoid collision after fix types in other method */
                    public final boolean invoke2(NotificationChannel channel) {
                        Intrinsics.checkExpressionValueIsNotNull(channel, "channel");
                        return channel.isImportanceLockedByOEM() || channel.getImportance() == 0 || channel.isImportanceLockedByCriticalDeviceFunction();
                    }
                });
            }
        });
        return SequencesKt.sortedWith(channels, new Comparator<T>() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$getDisplayableChannels$$inlined$compareBy$1
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                String id;
                String id2;
                NotificationChannel it = (NotificationChannel) t;
                Intrinsics.checkExpressionValueIsNotNull(it, "it");
                CharSequence name = it.getName();
                if (name == null || (id = name.toString()) == null) {
                    id = it.getId();
                }
                String str = id;
                NotificationChannel it2 = (NotificationChannel) t2;
                Intrinsics.checkExpressionValueIsNotNull(it2, "it");
                CharSequence name2 = it2.getName();
                if (name2 == null || (id2 = name2.toString()) == null) {
                    id2 = it2.getId();
                }
                return ComparisonsKt.compareValues(str, id2);
            }
        });
    }

    public final void show() {
        initDialog();
        Dialog dialog = this.dialog;
        if (dialog == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
        }
        dialog.show();
    }

    public final void close() {
        done();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void done() {
        resetState();
        Dialog dialog = this.dialog;
        if (dialog == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
        }
        dialog.dismiss();
    }

    private final void resetState() {
        this.appIcon = null;
        this.appUid = null;
        String str = null;
        this.packageName = str;
        this.appName = str;
        this.edits.clear();
        this.providedChannels.clear();
        this.groupNameLookup.clear();
    }

    @NotNull
    public final CharSequence groupNameForId(@Nullable String groupId) {
        CharSequence charSequence = this.groupNameLookup.get(groupId);
        return charSequence != null ? charSequence : "";
    }

    public final void proposeEditForChannel(@NotNull NotificationChannel channel, int edit) {
        Intrinsics.checkParameterIsNotNull(channel, "channel");
        if (channel.getImportance() == edit) {
            this.edits.remove(channel);
        } else {
            this.edits.put(channel, Integer.valueOf(edit));
        }
    }

    private final List<NotificationChannelGroup> fetchNotificationChannelGroups() {
        try {
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            if (str == null) {
                Intrinsics.throwNpe();
            }
            Integer num = this.appUid;
            if (num == null) {
                Intrinsics.throwNpe();
            }
            ParceledListSlice notificationChannelGroupsForPackage = iNotificationManager.getNotificationChannelGroupsForPackage(str, num.intValue(), false);
            Intrinsics.checkExpressionValueIsNotNull(notificationChannelGroupsForPackage, "noMan.getNotificationCha…eName!!, appUid!!, false)");
            List<NotificationChannelGroup> list = notificationChannelGroupsForPackage.getList();
            if (!(list instanceof List)) {
                list = null;
            }
            if (list != null) {
                return list;
            }
            return CollectionsKt.emptyList();
        } catch (Exception e) {
            Log.e(ChannelEditorDialogControllerKt.TAG, "Error fetching channel groups", e);
            return CollectionsKt.emptyList();
        }
    }

    private final boolean checkAreAppNotificationsOn() {
        try {
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            if (str == null) {
                Intrinsics.throwNpe();
            }
            Integer num = this.appUid;
            if (num == null) {
                Intrinsics.throwNpe();
            }
            return iNotificationManager.areNotificationsEnabledForPackage(str, num.intValue());
        } catch (Exception e) {
            Log.e(ChannelEditorDialogControllerKt.TAG, "Error calling NoMan", e);
            return false;
        }
    }

    private final void applyAppNotificationsOn(boolean b) {
        try {
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            if (str == null) {
                Intrinsics.throwNpe();
            }
            Integer num = this.appUid;
            if (num == null) {
                Intrinsics.throwNpe();
            }
            iNotificationManager.setNotificationsEnabledForPackage(str, num.intValue(), b);
        } catch (Exception e) {
            Log.e(ChannelEditorDialogControllerKt.TAG, "Error calling NoMan", e);
        }
    }

    private final void setChannelImportance(NotificationChannel channel, int importance) {
        try {
            channel.setImportance(importance);
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            if (str == null) {
                Intrinsics.throwNpe();
            }
            Integer num = this.appUid;
            if (num == null) {
                Intrinsics.throwNpe();
            }
            iNotificationManager.updateNotificationChannelForPackage(str, num.intValue(), channel);
        } catch (Exception e) {
            Log.e(ChannelEditorDialogControllerKt.TAG, "Unable to update notification importance", e);
        }
    }

    @VisibleForTesting
    public final void apply() {
        for (Map.Entry<NotificationChannel, Integer> entry : this.edits.entrySet()) {
            NotificationChannel channel = entry.getKey();
            int importance = entry.getValue().intValue();
            if (channel.getImportance() != importance) {
                setChannelImportance(channel, importance);
            }
        }
        if (this.appNotificationsEnabled != checkAreAppNotificationsOn()) {
            applyAppNotificationsOn(this.appNotificationsEnabled);
        }
    }

    @VisibleForTesting
    public final void launchSettings(@NotNull View sender) {
        Intrinsics.checkParameterIsNotNull(sender, "sender");
        NotificationInfo.OnSettingsClickListener onSettingsClickListener = this.onSettingsClickListener;
        if (onSettingsClickListener != null) {
            Integer num = this.appUid;
            if (num == null) {
                Intrinsics.throwNpe();
            }
            onSettingsClickListener.onClick(sender, null, num.intValue());
        }
    }

    private final void initDialog() {
        this.dialog = new Dialog(this.context);
        Dialog dialog = this.dialog;
        if (dialog == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
        }
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(1);
        }
        Dialog dialog2 = this.dialog;
        if (dialog2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
        }
        dialog2.setTitle(" ");
        Dialog $receiver = this.dialog;
        if ($receiver == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
        }
        $receiver.setContentView(R.layout.notif_half_shelf);
        $receiver.setCanceledOnTouchOutside(true);
        $receiver.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$initDialog$$inlined$apply$lambda$1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(@Nullable DialogInterface dialog3) {
                OnChannelEditorDialogFinishedListener onFinishListener = ChannelEditorDialogController.this.getOnFinishListener();
                if (onFinishListener != null) {
                    onFinishListener.onChannelEditorDialogFinished();
                }
            }
        });
        ChannelEditorListView $receiver2 = (ChannelEditorListView) $receiver.findViewById(R.id.half_shelf_container);
        $receiver2.setController(this);
        $receiver2.setAppIcon(this.appIcon);
        $receiver2.setAppName(this.appName);
        $receiver2.setChannels(this.providedChannels);
        TextView textView = (TextView) $receiver.findViewById(R.id.done_button);
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$initDialog$$inlined$apply$lambda$2
                @Override // android.view.View.OnClickListener
                public final void onClick(View it) {
                    ChannelEditorDialogController.this.apply();
                    ChannelEditorDialogController.this.done();
                }
            });
        }
        TextView textView2 = (TextView) $receiver.findViewById(R.id.see_more_button);
        if (textView2 != null) {
            textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$initDialog$$inlined$apply$lambda$3
                @Override // android.view.View.OnClickListener
                public final void onClick(View it) {
                    ChannelEditorDialogController channelEditorDialogController = ChannelEditorDialogController.this;
                    Intrinsics.checkExpressionValueIsNotNull(it, "it");
                    channelEditorDialogController.launchSettings(it);
                    ChannelEditorDialogController.this.done();
                }
            });
        }
        Window $receiver3 = $receiver.getWindow();
        if ($receiver3 != null) {
            $receiver3.setBackgroundDrawable(new ColorDrawable(0));
            $receiver3.addFlags(this.wmFlags);
            $receiver3.setType(WindowHelper.TYPE_STATUS_BAR_PANEL);
            $receiver3.setWindowAnimations(16973910);
            WindowManager.LayoutParams $receiver4 = $receiver3.getAttributes();
            $receiver4.format = -3;
            $receiver4.setTitle(ChannelEditorDialogController.class.getSimpleName());
            $receiver4.gravity = 81;
            $receiver4.width = -1;
            $receiver4.height = -2;
            $receiver3.setAttributes($receiver4);
        }
    }
}
