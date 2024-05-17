package com.android.systemui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraManager;
import android.util.PathParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: CameraAvailabilityListener.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\u0018\u0000 #2\u00020\u0001:\u0002\"#B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0014J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0007H\u0002J\b\u0010\u001b\u001a\u00020\u0016H\u0002J\b\u0010\u001c\u001a\u00020\u0016H\u0002J\b\u0010\u001d\u001a\u00020\u0016H\u0002J\u000e\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0014J\u0006\u0010\u001f\u001a\u00020\u0016J\u0006\u0010 \u001a\u00020\u0016J\b\u0010!\u001a\u00020\u0016H\u0002R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006$"}, d2 = {"Lcom/android/systemui/CameraAvailabilityListener;", "", "cameraManager", "Landroid/hardware/camera2/CameraManager;", "cutoutProtectionPath", "Landroid/graphics/Path;", "targetCameraId", "", "excludedPackages", "executor", "Ljava/util/concurrent/Executor;", "(Landroid/hardware/camera2/CameraManager;Landroid/graphics/Path;Ljava/lang/String;Ljava/lang/String;Ljava/util/concurrent/Executor;)V", "availabilityCallback", "Landroid/hardware/camera2/CameraManager$AvailabilityCallback;", "cutoutBounds", "Landroid/graphics/Rect;", "excludedPackageIds", "", "listeners", "", "Lcom/android/systemui/CameraAvailabilityListener$CameraTransitionCallback;", "addTransitionCallback", "", "callback", "isExcluded", "", "packageId", "notifyCameraActive", "notifyCameraInactive", "registerCameraListener", "removeTransitionCallback", "startListening", "stop", "unregisterCameraListener", "CameraTransitionCallback", "Factory", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class CameraAvailabilityListener {
    public static final Factory Factory = new Factory(null);
    private final CameraManager.AvailabilityCallback availabilityCallback;
    private final CameraManager cameraManager;
    private Rect cutoutBounds;
    private final Path cutoutProtectionPath;
    private final Set<String> excludedPackageIds;
    private final Executor executor;
    private final List<CameraTransitionCallback> listeners;
    private final String targetCameraId;

    /* compiled from: CameraAvailabilityListener.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\u0003H&¨\u0006\t"}, d2 = {"Lcom/android/systemui/CameraAvailabilityListener$CameraTransitionCallback;", "", "onApplyCameraProtection", "", "protectionPath", "Landroid/graphics/Path;", "bounds", "Landroid/graphics/Rect;", "onHideCameraProtection", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public interface CameraTransitionCallback {
        void onApplyCameraProtection(@NotNull Path path, @NotNull Rect rect);

        void onHideCameraProtection();
    }

    public CameraAvailabilityListener(@NotNull CameraManager cameraManager, @NotNull Path cutoutProtectionPath, @NotNull String targetCameraId, @NotNull String excludedPackages, @NotNull Executor executor) {
        Intrinsics.checkParameterIsNotNull(cameraManager, "cameraManager");
        Intrinsics.checkParameterIsNotNull(cutoutProtectionPath, "cutoutProtectionPath");
        Intrinsics.checkParameterIsNotNull(targetCameraId, "targetCameraId");
        Intrinsics.checkParameterIsNotNull(excludedPackages, "excludedPackages");
        Intrinsics.checkParameterIsNotNull(executor, "executor");
        this.cameraManager = cameraManager;
        this.cutoutProtectionPath = cutoutProtectionPath;
        this.targetCameraId = targetCameraId;
        this.executor = executor;
        this.cutoutBounds = new Rect();
        this.listeners = new ArrayList();
        this.availabilityCallback = new CameraManager.AvailabilityCallback() { // from class: com.android.systemui.CameraAvailabilityListener$availabilityCallback$1
            public void onCameraClosed(@NotNull String cameraId) {
                String str;
                Intrinsics.checkParameterIsNotNull(cameraId, "cameraId");
                str = CameraAvailabilityListener.this.targetCameraId;
                if (Intrinsics.areEqual(str, cameraId)) {
                    CameraAvailabilityListener.this.notifyCameraInactive();
                }
            }

            public void onCameraOpened(@NotNull String cameraId, @NotNull String packageId) {
                String str;
                boolean isExcluded;
                Intrinsics.checkParameterIsNotNull(cameraId, "cameraId");
                Intrinsics.checkParameterIsNotNull(packageId, "packageId");
                str = CameraAvailabilityListener.this.targetCameraId;
                if (Intrinsics.areEqual(str, cameraId)) {
                    isExcluded = CameraAvailabilityListener.this.isExcluded(packageId);
                    if (!isExcluded) {
                        CameraAvailabilityListener.this.notifyCameraActive();
                    }
                }
            }
        };
        RectF computed = new RectF();
        this.cutoutProtectionPath.computeBounds(computed, false);
        this.cutoutBounds.set(MathKt.roundToInt(computed.left), MathKt.roundToInt(computed.top), MathKt.roundToInt(computed.right), MathKt.roundToInt(computed.bottom));
        this.excludedPackageIds = CollectionsKt.toSet(StringsKt.split$default((CharSequence) excludedPackages, new String[]{","}, false, 0, 6, (Object) null));
    }

    public final void startListening() {
        registerCameraListener();
    }

    public final void stop() {
        unregisterCameraListener();
    }

    public final void addTransitionCallback(@NotNull CameraTransitionCallback callback) {
        Intrinsics.checkParameterIsNotNull(callback, "callback");
        this.listeners.add(callback);
    }

    public final void removeTransitionCallback(@NotNull CameraTransitionCallback callback) {
        Intrinsics.checkParameterIsNotNull(callback, "callback");
        this.listeners.remove(callback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isExcluded(String packageId) {
        return this.excludedPackageIds.contains(packageId);
    }

    private final void registerCameraListener() {
        this.cameraManager.registerAvailabilityCallback(this.executor, this.availabilityCallback);
    }

    private final void unregisterCameraListener() {
        this.cameraManager.unregisterAvailabilityCallback(this.availabilityCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void notifyCameraActive() {
        Iterable $receiver$iv = this.listeners;
        for (Object element$iv : $receiver$iv) {
            CameraTransitionCallback it = (CameraTransitionCallback) element$iv;
            it.onApplyCameraProtection(this.cutoutProtectionPath, this.cutoutBounds);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void notifyCameraInactive() {
        Iterable $receiver$iv = this.listeners;
        for (Object element$iv : $receiver$iv) {
            CameraTransitionCallback it = (CameraTransitionCallback) element$iv;
            it.onHideCameraProtection();
        }
    }

    /* compiled from: CameraAvailabilityListener.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002¨\u0006\r"}, d2 = {"Lcom/android/systemui/CameraAvailabilityListener$Factory;", "", "()V", "build", "Lcom/android/systemui/CameraAvailabilityListener;", "context", "Landroid/content/Context;", "executor", "Ljava/util/concurrent/Executor;", "pathFromString", "Landroid/graphics/Path;", "pathString", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Factory {
        private Factory() {
        }

        public /* synthetic */ Factory(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        @NotNull
        public final CameraAvailabilityListener build(@NotNull Context context, @NotNull Executor executor) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(executor, "executor");
            Object systemService = context.getSystemService("camera");
            if (systemService == null) {
                throw new TypeCastException("null cannot be cast to non-null type android.hardware.camera2.CameraManager");
            }
            CameraManager manager = (CameraManager) systemService;
            Resources res = context.getResources();
            String pathString = res.getString(R.string.config_frontBuiltInDisplayCutoutProtection);
            String cameraId = res.getString(R.string.config_protectedCameraId);
            String excluded = res.getString(R.string.config_cameraProtectionExcludedPackages);
            Intrinsics.checkExpressionValueIsNotNull(pathString, "pathString");
            Path pathFromString = pathFromString(pathString);
            Intrinsics.checkExpressionValueIsNotNull(cameraId, "cameraId");
            Intrinsics.checkExpressionValueIsNotNull(excluded, "excluded");
            return new CameraAvailabilityListener(manager, pathFromString, cameraId, excluded, executor);
        }

        private final Path pathFromString(String pathString) {
            if (pathString == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
            }
            String spec = StringsKt.trim((CharSequence) pathString).toString();
            try {
                Path p = PathParser.createPathFromPathData(spec);
                Intrinsics.checkExpressionValueIsNotNull(p, "PathParser.createPathFromPathData(spec)");
                return p;
            } catch (Throwable e) {
                throw new IllegalArgumentException("Invalid protection path", e);
            }
        }
    }
}
