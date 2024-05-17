package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: ConfigurationControllerImpl.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000fH\u0016J\b\u0010\u0016\u001a\u00020\u0014H\u0016J\u0010\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\fH\u0016J\u0010\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lcom/android/systemui/statusbar/phone/ConfigurationControllerImpl;", "Lcom/android/systemui/statusbar/policy/ConfigurationController;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "density", "", "fontScale", "", "inCarMode", "", "lastConfig", "Landroid/content/res/Configuration;", "listeners", "", "Lcom/android/systemui/statusbar/policy/ConfigurationController$ConfigurationListener;", "localeList", "Landroid/os/LocaleList;", "uiMode", "addCallback", "", "listener", "notifyThemeChanged", "onConfigurationChanged", "newConfig", "removeCallback", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class ConfigurationControllerImpl implements ConfigurationController {
    private final Context context;
    private int density;
    private float fontScale;
    private final boolean inCarMode;
    private final Configuration lastConfig;
    private final List<ConfigurationController.ConfigurationListener> listeners;
    private LocaleList localeList;
    private int uiMode;

    public ConfigurationControllerImpl(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.listeners = new ArrayList();
        this.lastConfig = new Configuration();
        Resources resources = context.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        Configuration currentConfig = resources.getConfiguration();
        this.context = context;
        this.fontScale = currentConfig.fontScale;
        this.density = currentConfig.densityDpi;
        this.inCarMode = (currentConfig.uiMode & 15) == 3;
        this.uiMode = currentConfig.uiMode & 48;
        Intrinsics.checkExpressionValueIsNotNull(currentConfig, "currentConfig");
        this.localeList = currentConfig.getLocales();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController
    public void notifyThemeChanged() {
        Iterable listeners = new ArrayList(this.listeners);
        Iterable $receiver$iv = (Collection) listeners;
        Iterable $receiver$iv$iv = $receiver$iv;
        for (Object element$iv$iv : $receiver$iv$iv) {
            ConfigurationController.ConfigurationListener it = (ConfigurationController.ConfigurationListener) element$iv$iv;
            if (this.listeners.contains(it)) {
                ConfigurationController.ConfigurationListener it2 = (ConfigurationController.ConfigurationListener) element$iv$iv;
                it2.onThemeChanged();
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        boolean z;
        Intrinsics.checkParameterIsNotNull(newConfig, "newConfig");
        Iterable listeners = new ArrayList(this.listeners);
        Iterable $receiver$iv = (Collection) listeners;
        Iterable $receiver$iv$iv = $receiver$iv;
        for (Object element$iv$iv : $receiver$iv$iv) {
            ConfigurationController.ConfigurationListener it = (ConfigurationController.ConfigurationListener) element$iv$iv;
            if (this.listeners.contains(it)) {
                ConfigurationController.ConfigurationListener it2 = (ConfigurationController.ConfigurationListener) element$iv$iv;
                it2.onConfigChanged(newConfig);
            }
        }
        float fontScale = newConfig.fontScale;
        int density = newConfig.densityDpi;
        int uiMode = newConfig.uiMode & 48;
        boolean uiModeChanged = uiMode != this.uiMode;
        if (density != this.density || fontScale != this.fontScale || (this.inCarMode && uiModeChanged)) {
            Iterable $receiver$iv2 = (Collection) listeners;
            Iterable $receiver$iv$iv2 = $receiver$iv2;
            for (Object element$iv$iv2 : $receiver$iv$iv2) {
                ConfigurationController.ConfigurationListener it3 = (ConfigurationController.ConfigurationListener) element$iv$iv2;
                if (this.listeners.contains(it3)) {
                    ConfigurationController.ConfigurationListener it4 = (ConfigurationController.ConfigurationListener) element$iv$iv2;
                    it4.onDensityOrFontScaleChanged();
                }
            }
            this.density = density;
            this.fontScale = fontScale;
        }
        LocaleList localeList = newConfig.getLocales();
        if (!Intrinsics.areEqual(localeList, this.localeList)) {
            this.localeList = localeList;
            Iterable $receiver$iv3 = (Collection) listeners;
            Iterable $receiver$iv$iv3 = $receiver$iv3;
            z = false;
            for (Object element$iv$iv3 : $receiver$iv$iv3) {
                float fontScale2 = fontScale;
                ConfigurationController.ConfigurationListener it5 = (ConfigurationController.ConfigurationListener) element$iv$iv3;
                int density2 = density;
                if (this.listeners.contains(it5)) {
                    ConfigurationController.ConfigurationListener it6 = (ConfigurationController.ConfigurationListener) element$iv$iv3;
                    it6.onLocaleListChanged();
                }
                fontScale = fontScale2;
                density = density2;
            }
        } else {
            z = false;
        }
        if (uiModeChanged) {
            this.context.getTheme().applyStyle(this.context.getThemeResId(), true);
            this.uiMode = uiMode;
            Collection $receiver$iv4 = (Collection) listeners;
            Collection $receiver$iv$iv4 = $receiver$iv4;
            for (Object element$iv$iv4 : $receiver$iv$iv4) {
                ConfigurationController.ConfigurationListener it7 = (ConfigurationController.ConfigurationListener) element$iv$iv4;
                Collection $receiver$iv5 = $receiver$iv4;
                if (this.listeners.contains(it7)) {
                    ConfigurationController.ConfigurationListener it8 = (ConfigurationController.ConfigurationListener) element$iv$iv4;
                    it8.onUiModeChanged();
                }
                $receiver$iv4 = $receiver$iv5;
            }
        }
        if ((this.lastConfig.updateFrom(newConfig) & Integer.MIN_VALUE) != 0) {
            Iterable $receiver$iv6 = (Collection) listeners;
            Iterable $receiver$iv$iv5 = $receiver$iv6;
            for (Object element$iv$iv5 : $receiver$iv$iv5) {
                ConfigurationController.ConfigurationListener it9 = (ConfigurationController.ConfigurationListener) element$iv$iv5;
                if (this.listeners.contains(it9)) {
                    ConfigurationController.ConfigurationListener it10 = (ConfigurationController.ConfigurationListener) element$iv$iv5;
                    it10.onOverlayChanged();
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(@NotNull ConfigurationController.ConfigurationListener listener) {
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        this.listeners.add(listener);
        listener.onDensityOrFontScaleChanged();
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(@NotNull ConfigurationController.ConfigurationListener listener) {
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        this.listeners.remove(listener);
    }
}
