package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ChannelEditorListView.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0018\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u00172\u0006\u0010&\u001a\u00020'H\u0002J\b\u0010(\u001a\u00020$H\u0014J\u0010\u0010)\u001a\u00020$2\u0006\u0010*\u001a\u00020+H\u0002J\b\u0010,\u001a\u00020$H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082.¢\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R0\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00170\u00162\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001d\u001a\u00020\u001eX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"¨\u0006-"}, d2 = {"Lcom/android/systemui/statusbar/notification/row/ChannelEditorListView;", "Landroid/widget/LinearLayout;", "c", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "appControlRow", "Lcom/android/systemui/statusbar/notification/row/AppControlView;", "appIcon", "Landroid/graphics/drawable/Drawable;", "getAppIcon", "()Landroid/graphics/drawable/Drawable;", "setAppIcon", "(Landroid/graphics/drawable/Drawable;)V", "appName", "", "getAppName", "()Ljava/lang/String;", "setAppName", "(Ljava/lang/String;)V", "newValue", "", "Landroid/app/NotificationChannel;", "channels", "getChannels", "()Ljava/util/List;", "setChannels", "(Ljava/util/List;)V", "controller", "Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;", "getController", "()Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;", "setController", "(Lcom/android/systemui/statusbar/notification/row/ChannelEditorDialogController;)V", "addChannelRow", "", "channel", "inflater", "Landroid/view/LayoutInflater;", "onFinishInflate", "updateAppControlRow", VuiConstants.ELEMENT_ENABLED, "", "updateRows", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class ChannelEditorListView extends LinearLayout {
    private AppControlView appControlRow;
    @Nullable
    private Drawable appIcon;
    @Nullable
    private String appName;
    @NotNull
    private List<NotificationChannel> channels;
    @NotNull
    public ChannelEditorDialogController controller;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ChannelEditorListView(@NotNull Context c, @NotNull AttributeSet attrs) {
        super(c, attrs);
        Intrinsics.checkParameterIsNotNull(c, "c");
        Intrinsics.checkParameterIsNotNull(attrs, "attrs");
        this.channels = new ArrayList();
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

    @Nullable
    public final Drawable getAppIcon() {
        return this.appIcon;
    }

    public final void setAppIcon(@Nullable Drawable drawable) {
        this.appIcon = drawable;
    }

    @Nullable
    public final String getAppName() {
        return this.appName;
    }

    public final void setAppName(@Nullable String str) {
        this.appName = str;
    }

    @NotNull
    public final List<NotificationChannel> getChannels() {
        return this.channels;
    }

    public final void setChannels(@NotNull List<NotificationChannel> newValue) {
        Intrinsics.checkParameterIsNotNull(newValue, "newValue");
        this.channels = newValue;
        updateRows();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R.id.app_control);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById(R.id.app_control)");
        this.appControlRow = (AppControlView) findViewById;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateRows() {
        ChannelEditorDialogController channelEditorDialogController = this.controller;
        if (channelEditorDialogController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("controller");
        }
        boolean enabled = channelEditorDialogController.getAppNotificationsEnabled();
        AutoTransition transition = new AutoTransition();
        transition.setDuration(200L);
        transition.addListener(new Transition.TransitionListener() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorListView$updateRows$1
            @Override // android.transition.Transition.TransitionListener
            public void onTransitionEnd(@Nullable Transition p0) {
                ChannelEditorListView.this.notifySubtreeAccessibilityStateChangedIfNeeded();
            }

            @Override // android.transition.Transition.TransitionListener
            public void onTransitionResume(@Nullable Transition p0) {
            }

            @Override // android.transition.Transition.TransitionListener
            public void onTransitionPause(@Nullable Transition p0) {
            }

            @Override // android.transition.Transition.TransitionListener
            public void onTransitionCancel(@Nullable Transition p0) {
            }

            @Override // android.transition.Transition.TransitionListener
            public void onTransitionStart(@Nullable Transition p0) {
            }
        });
        TransitionManager.beginDelayedTransition(this, transition);
        int n = getChildCount();
        for (int i = n; i >= 0; i--) {
            View child = getChildAt(i);
            if (child instanceof ChannelRow) {
                removeView(child);
            }
        }
        updateAppControlRow(enabled);
        if (enabled) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (NotificationChannel channel : this.channels) {
                Intrinsics.checkExpressionValueIsNotNull(inflater, "inflater");
                addChannelRow(channel, inflater);
            }
        }
    }

    private final void addChannelRow(NotificationChannel channel, LayoutInflater inflater) {
        View inflate = inflater.inflate(R.layout.notif_half_shelf_row, (ViewGroup) null);
        if (inflate == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.statusbar.notification.row.ChannelRow");
        }
        ChannelRow row = (ChannelRow) inflate;
        ChannelEditorDialogController channelEditorDialogController = this.controller;
        if (channelEditorDialogController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("controller");
        }
        row.setController(channelEditorDialogController);
        row.setChannel(channel);
        addView(row);
    }

    private final void updateAppControlRow(boolean enabled) {
        AppControlView appControlView = this.appControlRow;
        if (appControlView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
        }
        appControlView.getIconView().setImageDrawable(this.appIcon);
        AppControlView appControlView2 = this.appControlRow;
        if (appControlView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
        }
        TextView channelName = appControlView2.getChannelName();
        Context context = getContext();
        Intrinsics.checkExpressionValueIsNotNull(context, "context");
        channelName.setText(context.getResources().getString(R.string.notification_channel_dialog_title, this.appName));
        AppControlView appControlView3 = this.appControlRow;
        if (appControlView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
        }
        appControlView3.getSwitch().setChecked(enabled);
        AppControlView appControlView4 = this.appControlRow;
        if (appControlView4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("appControlRow");
        }
        appControlView4.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.systemui.statusbar.notification.row.ChannelEditorListView$updateAppControlRow$1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton $noName_0, boolean b) {
                ChannelEditorListView.this.getController().setAppNotificationsEnabled(b);
                ChannelEditorListView.this.updateRows();
            }
        });
    }
}
