package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.systemui.R;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: ChannelEditorListView.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u0019\u001a\u00020\u001aH\u0014R\u001a\u0010\u0007\u001a\u00020\bX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\u00020\u000eX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\u00020\u0014X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018¨\u0006\u001b"}, d2 = {"Lcom/android/systemui/statusbar/notification/row/AppControlView;", "Landroid/widget/LinearLayout;", "c", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "channelName", "Landroid/widget/TextView;", "getChannelName", "()Landroid/widget/TextView;", "setChannelName", "(Landroid/widget/TextView;)V", "iconView", "Landroid/widget/ImageView;", "getIconView", "()Landroid/widget/ImageView;", "setIconView", "(Landroid/widget/ImageView;)V", "switch", "Landroid/widget/Switch;", "getSwitch", "()Landroid/widget/Switch;", "setSwitch", "(Landroid/widget/Switch;)V", "onFinishInflate", "", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class AppControlView extends LinearLayout {
    @NotNull
    public TextView channelName;
    @NotNull
    public ImageView iconView;
    @NotNull

    /* renamed from: switch  reason: not valid java name */
    public Switch f0switch;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AppControlView(@NotNull Context c, @NotNull AttributeSet attrs) {
        super(c, attrs);
        Intrinsics.checkParameterIsNotNull(c, "c");
        Intrinsics.checkParameterIsNotNull(attrs, "attrs");
    }

    @NotNull
    public final ImageView getIconView() {
        ImageView imageView = this.iconView;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("iconView");
        }
        return imageView;
    }

    public final void setIconView(@NotNull ImageView imageView) {
        Intrinsics.checkParameterIsNotNull(imageView, "<set-?>");
        this.iconView = imageView;
    }

    @NotNull
    public final TextView getChannelName() {
        TextView textView = this.channelName;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("channelName");
        }
        return textView;
    }

    public final void setChannelName(@NotNull TextView textView) {
        Intrinsics.checkParameterIsNotNull(textView, "<set-?>");
        this.channelName = textView;
    }

    @NotNull
    public final Switch getSwitch() {
        Switch r0 = this.f0switch;
        if (r0 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("switch");
        }
        return r0;
    }

    public final void setSwitch(@NotNull Switch r2) {
        Intrinsics.checkParameterIsNotNull(r2, "<set-?>");
        this.f0switch = r2;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        View findViewById = findViewById(R.id.icon);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById(R.id.icon)");
        this.iconView = (ImageView) findViewById;
        View findViewById2 = findViewById(R.id.app_name);
        Intrinsics.checkExpressionValueIsNotNull(findViewById2, "findViewById(R.id.app_name)");
        this.channelName = (TextView) findViewById2;
        View findViewById3 = findViewById(R.id.toggle);
        Intrinsics.checkExpressionValueIsNotNull(findViewById3, "findViewById(R.id.toggle)");
        this.f0switch = (Switch) findViewById3;
    }
}
