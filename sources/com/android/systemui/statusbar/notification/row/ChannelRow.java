package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.systemui.R;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ChannelEditorListView.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u001f\u001a\u00020 H\u0014J\b\u0010!\u001a\u00020 H\u0002J\b\u0010\"\u001a\u00020 H\u0002R(\u0010\t\u001a\u0004\u0018\u00010\b2\b\u0010\u0007\u001a\u0004\u0018\u00010\b@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082.¢\u0006\u0002\n\u0000R\u001a\u0010\u0011\u001a\u00020\u0012X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u001eX\u0082.¢\u0006\u0002\n\u0000¨\u0006#"}, d2 = {"Lcom/android/systemui/statusbar/notification/row/ChannelRow;", "Landroid/widget/LinearLayout;", "c", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "newValue", "Landroid/app/NotificationChannel;", "channel", "getChannel", "()Landroid/app/NotificationChannel;", "setChannel", "(Landroid/app/NotificationChannel;)V", "channelDescription", "Landroid/widget/TextView;", "channelName", "controller", "Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;", "getController", "()Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;", "setController", "(Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;)V", "gentle", "", "getGentle", "()Z", "setGentle", "(Z)V", "switch", "Landroid/widget/Switch;", "onFinishInflate", "", "updateImportance", "updateViews", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class ChannelRow extends LinearLayout {
    @Nullable
    private NotificationChannel channel;
    private TextView channelDescription;
    private TextView channelName;
    @NotNull
    public ChannelEditorDialogController controller;
    private boolean gentle;

    /* renamed from: switch  reason: not valid java name */
    private Switch f1switch;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ChannelRow(@NotNull Context c, @NotNull AttributeSet attrs) {
        super(c, attrs);
        Intrinsics.checkParameterIsNotNull(c, "c");
        Intrinsics.checkParameterIsNotNull(attrs, "attrs");
    }

    @NotNull
    public final ChannelEditorDialogController getController() {
        ChannelEditorDialogController channelEditorDialogController = this.controller;
        if (channelEditorDialogController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("controller");
        }
        return channelEditorDialogController;
    }

    public final void setController(@NotNull ChannelEditorDialogController channelEditorDialogController) {
        Intrinsics.checkParameterIsNotNull(channelEditorDialogController, "<set-?>");
        this.controller = channelEditorDialogController;
    }

    public final boolean getGentle() {
        return this.gentle;
    }

    public final void setGentle(boolean z) {
        this.gentle = z;
    }

    @Nullable
    public final NotificationChannel getChannel() {
        return this.channel;
    }

    public final void setChannel(@Nullable NotificationChannel newValue) {
        this.channel = newValue;
        updateImportance();
        updateViews();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        View findViewById = findViewById(R.id.channel_name);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById(R.id.channel_name)");
        this.channelName = (TextView) findViewById;
        View findViewById2 = findViewById(R.id.channel_description);
        Intrinsics.checkExpressionValueIsNotNull(findViewById2, "findViewById(R.id.channel_description)");
        this.channelDescription = (TextView) findViewById2;
        View findViewById3 = findViewById(R.id.toggle);
        Intrinsics.checkExpressionValueIsNotNull(findViewById3, "findViewById(R.id.toggle)");
        this.f1switch = (Switch) findViewById3;
        Switch r0 = this.f1switch;
        if (r0 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("switch");
        }
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.systemui.statusbar.notification.row.ChannelRow$onFinishInflate$1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton $noName_0, boolean b) {
                NotificationChannel it = ChannelRow.this.getChannel();
                if (it != null) {
                    ChannelRow.this.getController().proposeEditForChannel(it, b ? it.getImportance() : 0);
                }
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x007d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final void updateViews() {
        /*
            r7 = this;
            android.app.NotificationChannel r0 = r7.channel
            if (r0 == 0) goto L82
            android.widget.TextView r1 = r7.channelName
            if (r1 != 0) goto Ld
            java.lang.String r2 = "channelName"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
        Ld:
            java.lang.CharSequence r2 = r0.getName()
            if (r2 == 0) goto L14
            goto L18
        L14:
            java.lang.String r2 = ""
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
        L18:
            r1.setText(r2)
            java.lang.String r1 = r0.getGroup()
            java.lang.String r2 = "channelDescription"
            if (r1 == 0) goto L3d
            r3 = 0
            android.widget.TextView r4 = r7.channelDescription
            if (r4 != 0) goto L2b
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
        L2b:
            com.android.systemui.statusbar.notification.row.ChannelEditorDialogController r5 = r7.controller
            if (r5 != 0) goto L34
            java.lang.String r6 = "controller"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
        L34:
            java.lang.CharSequence r5 = r5.groupNameForId(r1)
            r4.setText(r5)
        L3d:
            java.lang.String r1 = r0.getGroup()
            r3 = 0
            if (r1 == 0) goto L61
            android.widget.TextView r1 = r7.channelDescription
            if (r1 != 0) goto L4b
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
        L4b:
            java.lang.CharSequence r1 = r1.getText()
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L56
            goto L61
        L56:
            android.widget.TextView r1 = r7.channelDescription
            if (r1 != 0) goto L5d
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
        L5d:
            r1.setVisibility(r3)
            goto L6d
        L61:
            android.widget.TextView r1 = r7.channelDescription
            if (r1 != 0) goto L68
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
        L68:
            r2 = 8
            r1.setVisibility(r2)
        L6d:
            android.widget.Switch r1 = r7.f1switch
            if (r1 != 0) goto L77
            java.lang.String r2 = "switch"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r2)
        L77:
            int r2 = r0.getImportance()
            if (r2 == 0) goto L7e
            r3 = 1
        L7e:
            r1.setChecked(r3)
            return
        L82:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.ChannelRow.updateViews():void");
    }

    private final void updateImportance() {
        NotificationChannel notificationChannel = this.channel;
        boolean z = false;
        int importance = notificationChannel != null ? notificationChannel.getImportance() : 0;
        if (importance != -1000 && importance < 3) {
            z = true;
        }
        this.gentle = z;
    }
}
