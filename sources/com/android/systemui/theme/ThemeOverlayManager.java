package com.android.systemui.theme;

import android.content.om.OverlayInfo;
import android.content.om.OverlayManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class ThemeOverlayManager {
    @VisibleForTesting
    static final String ANDROID_PACKAGE = "android";
    private static final boolean DEBUG = false;
    @VisibleForTesting
    static final String SETTINGS_PACKAGE = "com.android.settings";
    @VisibleForTesting
    static final String SYSUI_PACKAGE = "com.android.systemui";
    private static final String TAG = "ThemeOverlayManager";
    private final Executor mExecutor;
    private final String mLauncherPackage;
    private final OverlayManager mOverlayManager;
    private final String mThemePickerPackage;
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_ICON_LAUNCHER = "android.theme.customization.icon_pack.launcher";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_SHAPE = "android.theme.customization.adaptive_icon_shape";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_FONT = "android.theme.customization.font";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_COLOR = "android.theme.customization.accent_color";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_ICON_ANDROID = "android.theme.customization.icon_pack.android";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_ICON_SYSUI = "android.theme.customization.icon_pack.systemui";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_ICON_SETTINGS = "android.theme.customization.icon_pack.settings";
    @VisibleForTesting
    static final String OVERLAY_CATEGORY_ICON_THEME_PICKER = "android.theme.customization.icon_pack.themepicker";
    static final List<String> THEME_CATEGORIES = Lists.newArrayList(new String[]{OVERLAY_CATEGORY_ICON_LAUNCHER, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_COLOR, OVERLAY_CATEGORY_ICON_ANDROID, OVERLAY_CATEGORY_ICON_SYSUI, OVERLAY_CATEGORY_ICON_SETTINGS, OVERLAY_CATEGORY_ICON_THEME_PICKER});
    @VisibleForTesting
    static final Set<String> SYSTEM_USER_CATEGORIES = Sets.newHashSet(new String[]{OVERLAY_CATEGORY_COLOR, OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_ICON_ANDROID, OVERLAY_CATEGORY_ICON_SYSUI});
    private final Map<String, Set<String>> mTargetPackageToCategories = new ArrayMap();
    private final Map<String, String> mCategoryToTargetPackage = new ArrayMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThemeOverlayManager(OverlayManager overlayManager, Executor executor, String launcherPackage, String themePickerPackage) {
        this.mOverlayManager = overlayManager;
        this.mExecutor = executor;
        this.mLauncherPackage = launcherPackage;
        this.mThemePickerPackage = themePickerPackage;
        this.mTargetPackageToCategories.put("android", Sets.newHashSet(new String[]{OVERLAY_CATEGORY_COLOR, OVERLAY_CATEGORY_FONT, OVERLAY_CATEGORY_SHAPE, OVERLAY_CATEGORY_ICON_ANDROID}));
        this.mTargetPackageToCategories.put("com.android.systemui", Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_SYSUI}));
        this.mTargetPackageToCategories.put(SETTINGS_PACKAGE, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_SETTINGS}));
        this.mTargetPackageToCategories.put(this.mLauncherPackage, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_LAUNCHER}));
        this.mTargetPackageToCategories.put(this.mThemePickerPackage, Sets.newHashSet(new String[]{OVERLAY_CATEGORY_ICON_THEME_PICKER}));
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_COLOR, "android");
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_FONT, "android");
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_SHAPE, "android");
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_ICON_ANDROID, "android");
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_ICON_SYSUI, "com.android.systemui");
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_ICON_SETTINGS, SETTINGS_PACKAGE);
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_ICON_LAUNCHER, this.mLauncherPackage);
        this.mCategoryToTargetPackage.put(OVERLAY_CATEGORY_ICON_THEME_PICKER, this.mThemePickerPackage);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyCurrentUserOverlays(Map<String, String> categoryToPackage, Set<UserHandle> userHandles) {
        final Set<String> overlayCategoriesToDisable = new HashSet<>(THEME_CATEGORIES);
        overlayCategoriesToDisable.removeAll(categoryToPackage.keySet());
        Set<String> targetPackagesToQuery = (Set) overlayCategoriesToDisable.stream().map(new Function() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$XHd3K8Vp7fhFb4ucZudIi42URZk
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ThemeOverlayManager.this.lambda$applyCurrentUserOverlays$0$ThemeOverlayManager((String) obj);
            }
        }).collect(Collectors.toSet());
        final List<OverlayInfo> overlays = new ArrayList<>();
        targetPackagesToQuery.forEach(new Consumer() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$Ce247HGCsGLtUA2wdEQCGlPUIx4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ThemeOverlayManager.this.lambda$applyCurrentUserOverlays$1$ThemeOverlayManager(overlays, (String) obj);
            }
        });
        Map<String, String> overlaysToDisable = (Map) overlays.stream().filter(new Predicate() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$FzQkanwY8TEeM97QNlP4yjS7F4s
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ThemeOverlayManager.this.lambda$applyCurrentUserOverlays$2$ThemeOverlayManager((OverlayInfo) obj);
            }
        }).filter(new Predicate() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$rD72NeWKvvYjih6pAWlvN555mFM
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean contains;
                contains = overlayCategoriesToDisable.contains(((OverlayInfo) obj).category);
                return contains;
            }
        }).filter(new Predicate() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$vK2aROqMaNCgMb7ixs5bp0NF79c
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isEnabled;
                isEnabled = ((OverlayInfo) obj).isEnabled();
                return isEnabled;
            }
        }).collect(Collectors.toMap(new Function() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$tpreaivLMVK4R3Uf26BCg27-Af8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String str;
                str = ((OverlayInfo) obj).category;
                return str;
            }
        }, new Function() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$GlioDk646gj_04NkaTcsRN_awI4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String str;
                str = ((OverlayInfo) obj).packageName;
                return str;
            }
        }));
        for (String category : THEME_CATEGORIES) {
            if (categoryToPackage.containsKey(category)) {
                setEnabled(categoryToPackage.get(category), category, userHandles, true);
            } else if (overlaysToDisable.containsKey(category)) {
                setEnabled(overlaysToDisable.get(category), category, userHandles, false);
            }
        }
    }

    public /* synthetic */ String lambda$applyCurrentUserOverlays$0$ThemeOverlayManager(String category) {
        return this.mCategoryToTargetPackage.get(category);
    }

    public /* synthetic */ void lambda$applyCurrentUserOverlays$1$ThemeOverlayManager(List overlays, String targetPackage) {
        overlays.addAll(this.mOverlayManager.getOverlayInfosForTarget(targetPackage, UserHandle.SYSTEM));
    }

    public /* synthetic */ boolean lambda$applyCurrentUserOverlays$2$ThemeOverlayManager(OverlayInfo o) {
        return this.mTargetPackageToCategories.get(o.targetPackageName).contains(o.category);
    }

    private void setEnabled(String packageName, String category, Set<UserHandle> handles, boolean enabled) {
        for (UserHandle userHandle : handles) {
            setEnabledAsync(packageName, userHandle, enabled);
        }
        if (!handles.contains(UserHandle.SYSTEM) && SYSTEM_USER_CATEGORIES.contains(category)) {
            setEnabledAsync(packageName, UserHandle.SYSTEM, enabled);
        }
    }

    private void setEnabledAsync(final String pkg, final UserHandle userHandle, final boolean enabled) {
        this.mExecutor.execute(new Runnable() { // from class: com.android.systemui.theme.-$$Lambda$ThemeOverlayManager$Za-49vHyQK-Yiveq0XqQrGRFGHg
            @Override // java.lang.Runnable
            public final void run() {
                ThemeOverlayManager.this.lambda$setEnabledAsync$7$ThemeOverlayManager(pkg, userHandle, enabled);
            }
        });
    }

    public /* synthetic */ void lambda$setEnabledAsync$7$ThemeOverlayManager(String pkg, UserHandle userHandle, boolean enabled) {
        try {
            if (!enabled) {
                this.mOverlayManager.setEnabled(pkg, false, userHandle);
            } else {
                this.mOverlayManager.setEnabledExclusiveInCategory(pkg, userHandle);
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, String.format("setEnabled failed: %s %s %b", pkg, userHandle, Boolean.valueOf(enabled)), e);
        }
    }
}
