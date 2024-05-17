package com.android.systemui;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.HandlerThread;
import android.os.Trace;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.util.Size;
import android.view.DisplayInfo;
import android.view.SurfaceHolder;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.ImageWallpaper;
import com.android.systemui.glwallpaper.EglHelper;
import com.android.systemui.glwallpaper.GLWallpaperRenderer;
import com.android.systemui.glwallpaper.ImageWallpaperRenderer;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.DozeParameters;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class ImageWallpaper extends WallpaperService {
    private static final boolean DEBUG = true;
    private static final int DELAY_FINISH_RENDERING = 1000;
    private static final int INTERVAL_WAIT_FOR_RENDERING = 100;
    private static final int PATIENCE_WAIT_FOR_RENDERING = 10;
    private static final String TAG = ImageWallpaper.class.getSimpleName();
    private HandlerThread mWorker;

    @Override // android.service.wallpaper.WallpaperService, android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mWorker = new HandlerThread(TAG);
        this.mWorker.start();
    }

    @Override // android.service.wallpaper.WallpaperService
    public WallpaperService.Engine onCreateEngine() {
        return new GLEngine(this);
    }

    @Override // android.service.wallpaper.WallpaperService, android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.mWorker.quitSafely();
        this.mWorker = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class GLEngine extends WallpaperService.Engine implements GLWallpaperRenderer.SurfaceProxy, StatusBarStateController.StateListener {
        @VisibleForTesting
        static final int MIN_SURFACE_HEIGHT = 64;
        @VisibleForTesting
        static final int MIN_SURFACE_WIDTH = 64;
        private StatusBarStateController mController;
        private final DisplayInfo mDisplayInfo;
        private final boolean mDisplayNeedsBlanking;
        private EglHelper mEglHelper;
        private final Runnable mFinishRenderingTask;
        private final boolean mIsHighEndGfx;
        private final Object mMonitor;
        private boolean mNeedRedraw;
        private final boolean mNeedTransition;
        private GLWallpaperRenderer mRenderer;
        private boolean mShouldStopTransition;
        private boolean mWaitingForRendering;

        GLEngine(Context context) {
            super(ImageWallpaper.this);
            this.mFinishRenderingTask = new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$4IwqG_0jMNtMT6yCqqj-KAFKSvE
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.finishRendering();
                }
            };
            this.mDisplayInfo = new DisplayInfo();
            this.mMonitor = new Object();
            this.mIsHighEndGfx = ActivityManager.isHighEndGfx();
            this.mDisplayNeedsBlanking = DozeParameters.getInstance(context).getDisplayNeedsBlanking();
            this.mNeedTransition = this.mIsHighEndGfx && !this.mDisplayNeedsBlanking;
            this.mController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);
            StatusBarStateController statusBarStateController = this.mController;
            if (statusBarStateController != null) {
                statusBarStateController.addCallback(this);
            }
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onCreate(SurfaceHolder surfaceHolder) {
            this.mEglHelper = new EglHelper();
            this.mRenderer = new ImageWallpaperRenderer(getDisplayContext(), this);
            getDisplayContext().getDisplay().getDisplayInfo(this.mDisplayInfo);
            setFixedSizeAllowed(true);
            setOffsetNotificationsEnabled(true);
            updateSurfaceSize();
        }

        private void updateSurfaceSize() {
            SurfaceHolder holder = getSurfaceHolder();
            Size frameSize = this.mRenderer.reportSurfaceSize();
            int width = Math.max(64, frameSize.getWidth());
            int height = Math.max(64, frameSize.getHeight());
            holder.setFixedSize(width, height);
        }

        @VisibleForTesting
        boolean checkIfShouldStopTransition() {
            int orientation = getDisplayContext().getResources().getConfiguration().orientation;
            boolean portrait = orientation == 1;
            Rect frame = getSurfaceHolder().getSurfaceFrame();
            int frameWidth = frame.width();
            int frameHeight = frame.height();
            DisplayInfo displayInfo = this.mDisplayInfo;
            int displayWidth = portrait ? displayInfo.logicalWidth : displayInfo.logicalHeight;
            DisplayInfo displayInfo2 = this.mDisplayInfo;
            int displayHeight = portrait ? displayInfo2.logicalHeight : displayInfo2.logicalWidth;
            if (this.mNeedTransition) {
                return frameWidth < displayWidth || frameHeight < displayHeight;
            }
            return false;
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onOffsetsChanged(final float xOffset, final float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$g3IyjqoMJVi1L9x8yfO51WpEVxQ
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.lambda$onOffsetsChanged$0$ImageWallpaper$GLEngine(xOffset, yOffset);
                }
            });
        }

        public /* synthetic */ void lambda$onOffsetsChanged$0$ImageWallpaper$GLEngine(float xOffset, float yOffset) {
            this.mRenderer.updateOffsets(xOffset, yOffset);
        }

        public void onAmbientModeChanged(final boolean inAmbientMode, long animationDuration) {
            if (ImageWallpaper.this.mWorker == null || !this.mNeedTransition) {
                return;
            }
            final long duration = this.mShouldStopTransition ? 0L : animationDuration;
            String str = ImageWallpaper.TAG;
            Log.d(str, "onAmbientModeChanged: inAmbient=" + inAmbientMode + ", duration=" + duration + ", mShouldStopTransition=" + this.mShouldStopTransition);
            ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$w2dgQ1kcC5UhS4OuTNdpiCJsVqQ
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.lambda$onAmbientModeChanged$1$ImageWallpaper$GLEngine(inAmbientMode, duration);
                }
            });
            if (inAmbientMode && animationDuration == 0) {
                waitForBackgroundRendering();
            }
        }

        public /* synthetic */ void lambda$onAmbientModeChanged$1$ImageWallpaper$GLEngine(boolean inAmbientMode, long duration) {
            this.mRenderer.updateAmbientMode(inAmbientMode, duration);
        }

        private void waitForBackgroundRendering() {
            synchronized (this.mMonitor) {
                try {
                    this.mWaitingForRendering = true;
                    int patience = 1;
                    while (this.mWaitingForRendering) {
                        this.mMonitor.wait(100L);
                        this.mWaitingForRendering &= patience < 10;
                        patience++;
                    }
                    this.mWaitingForRendering = false;
                } catch (InterruptedException e) {
                    this.mWaitingForRendering = false;
                } catch (Throwable th) {
                    this.mWaitingForRendering = false;
                    throw th;
                }
            }
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onDestroy() {
            StatusBarStateController statusBarStateController = this.mController;
            if (statusBarStateController != null) {
                statusBarStateController.removeCallback(this);
            }
            this.mController = null;
            ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$Rhxb7oaAcAGNLCxy2rNqC6pp_0w
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.lambda$onDestroy$2$ImageWallpaper$GLEngine();
                }
            });
        }

        public /* synthetic */ void lambda$onDestroy$2$ImageWallpaper$GLEngine() {
            this.mRenderer.finish();
            this.mRenderer = null;
            this.mEglHelper.finish();
            this.mEglHelper = null;
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onSurfaceCreated(final SurfaceHolder holder) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            this.mShouldStopTransition = checkIfShouldStopTransition();
            ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$WwPnKXUZbkazdjOcqYKAzWQFvTQ
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.lambda$onSurfaceCreated$3$ImageWallpaper$GLEngine(holder);
                }
            });
        }

        public /* synthetic */ void lambda$onSurfaceCreated$3$ImageWallpaper$GLEngine(SurfaceHolder holder) {
            this.mEglHelper.init(holder);
            this.mRenderer.onSurfaceCreated();
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onSurfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$syj9B-tRzmYbOUFqEOGp6WsQqI0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.lambda$onSurfaceChanged$4$ImageWallpaper$GLEngine(width, height);
                }
            });
        }

        public /* synthetic */ void lambda$onSurfaceChanged$4$ImageWallpaper$GLEngine(int width, int height) {
            String str = ImageWallpaper.TAG;
            Log.d(str, "onSurfaceChanged: w=" + width + ", h=" + height);
            this.mRenderer.onSurfaceChanged(width, height);
            this.mNeedRedraw = true;
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$nUXqEeCVFkWFioUicXPSoLlcN1s
                @Override // java.lang.Runnable
                public final void run() {
                    ImageWallpaper.GLEngine.this.lambda$onSurfaceRedrawNeeded$5$ImageWallpaper$GLEngine();
                }
            });
        }

        public /* synthetic */ void lambda$onSurfaceRedrawNeeded$5$ImageWallpaper$GLEngine() {
            String str = ImageWallpaper.TAG;
            Log.d(str, "onSurfaceRedrawNeeded: mNeedRedraw=" + this.mNeedRedraw);
            if (this.mNeedRedraw) {
                drawFrame();
                this.mNeedRedraw = false;
            }
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        public void onVisibilityChanged(boolean visible) {
            String str = ImageWallpaper.TAG;
            Log.d(str, "wallpaper visibility changes to: " + visible);
        }

        private void drawFrame() {
            preRender();
            requestRender();
            postRender();
        }

        @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
        public void onStatePostChange() {
            if (ImageWallpaper.this.mWorker != null && this.mController.getState() == 0) {
                ImageWallpaper.this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ImageWallpaper$GLEngine$8Tw1AsmyFt-Lr4VSDxpiW6fEz7g
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImageWallpaper.GLEngine.this.scheduleFinishRendering();
                    }
                });
            }
        }

        @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer.SurfaceProxy
        public void preRender() {
            Log.d(ImageWallpaper.TAG, "preRender start");
            Trace.beginSection("ImageWallpaper#preRender");
            preRenderInternal();
            Trace.endSection();
            Log.d(ImageWallpaper.TAG, "preRender end");
        }

        private void preRenderInternal() {
            boolean contextRecreated = false;
            Rect frame = getSurfaceHolder().getSurfaceFrame();
            cancelFinishRenderingTask();
            if (!this.mEglHelper.hasEglContext()) {
                this.mEglHelper.destroyEglSurface();
                if (!this.mEglHelper.createEglContext()) {
                    Log.w(ImageWallpaper.TAG, "recreate egl context failed!");
                } else {
                    contextRecreated = true;
                }
            }
            if (this.mEglHelper.hasEglContext() && !this.mEglHelper.hasEglSurface() && !this.mEglHelper.createEglSurface(getSurfaceHolder())) {
                Log.w(ImageWallpaper.TAG, "recreate egl surface failed!");
            }
            if (this.mEglHelper.hasEglContext() && this.mEglHelper.hasEglSurface() && contextRecreated) {
                this.mRenderer.onSurfaceCreated();
                this.mRenderer.onSurfaceChanged(frame.width(), frame.height());
            }
        }

        @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer.SurfaceProxy
        public void requestRender() {
            Trace.beginSection("ImageWallpaper#requestRender");
            requestRenderInternal();
            Trace.endSection();
        }

        private void requestRenderInternal() {
            Rect frame = getSurfaceHolder().getSurfaceFrame();
            boolean readyToRender = this.mEglHelper.hasEglContext() && this.mEglHelper.hasEglSurface() && frame.width() > 0 && frame.height() > 0;
            if (!readyToRender) {
                String str = ImageWallpaper.TAG;
                Log.e(str, "requestRender: not ready, has context=" + this.mEglHelper.hasEglContext() + ", has surface=" + this.mEglHelper.hasEglSurface() + ", frame=" + frame);
                return;
            }
            this.mRenderer.onDrawFrame();
            if (!this.mEglHelper.swapBuffer()) {
                Log.e(ImageWallpaper.TAG, "drawFrame failed!");
            }
        }

        @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer.SurfaceProxy
        public void postRender() {
            Log.d(ImageWallpaper.TAG, "postRender start");
            Trace.beginSection("ImageWallpaper#postRender");
            notifyWaitingThread();
            scheduleFinishRendering();
            Trace.endSection();
            Log.d(ImageWallpaper.TAG, "postRender end");
        }

        private void notifyWaitingThread() {
            synchronized (this.mMonitor) {
                if (this.mWaitingForRendering) {
                    try {
                        this.mWaitingForRendering = false;
                        this.mMonitor.notify();
                    } catch (IllegalMonitorStateException e) {
                    }
                }
            }
        }

        private void cancelFinishRenderingTask() {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            ImageWallpaper.this.mWorker.getThreadHandler().removeCallbacks(this.mFinishRenderingTask);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void scheduleFinishRendering() {
            if (ImageWallpaper.this.mWorker == null) {
                return;
            }
            cancelFinishRenderingTask();
            ImageWallpaper.this.mWorker.getThreadHandler().postDelayed(this.mFinishRenderingTask, 1000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finishRendering() {
            String str = ImageWallpaper.TAG;
            Log.d(str, "finishRendering, preserve=" + needPreserveEglContext());
            Trace.beginSection("ImageWallpaper#finishRendering");
            EglHelper eglHelper = this.mEglHelper;
            if (eglHelper != null) {
                eglHelper.destroyEglSurface();
                if (!needPreserveEglContext()) {
                    this.mEglHelper.destroyEglContext();
                }
            }
            Trace.endSection();
        }

        private boolean needPreserveEglContext() {
            StatusBarStateController statusBarStateController;
            return this.mNeedTransition && (statusBarStateController = this.mController) != null && statusBarStateController.getState() == 1;
        }

        @Override // android.service.wallpaper.WallpaperService.Engine
        protected void dump(String prefix, FileDescriptor fd, PrintWriter out, String[] args) {
            Boolean bool;
            super.dump(prefix, fd, out, args);
            out.print(prefix);
            out.print("Engine=");
            out.println(this);
            out.print(prefix);
            out.print("isHighEndGfx=");
            out.println(this.mIsHighEndGfx);
            out.print(prefix);
            out.print("displayNeedsBlanking=");
            out.println(this.mDisplayNeedsBlanking);
            out.print(prefix);
            out.print("displayInfo=");
            out.print(this.mDisplayInfo);
            out.print(prefix);
            out.print("mNeedTransition=");
            out.println(this.mNeedTransition);
            out.print(prefix);
            out.print("mShouldStopTransition=");
            out.println(this.mShouldStopTransition);
            out.print(prefix);
            out.print("StatusBarState=");
            StatusBarStateController statusBarStateController = this.mController;
            out.println(statusBarStateController != null ? Integer.valueOf(statusBarStateController.getState()) : "null");
            out.print(prefix);
            out.print("valid surface=");
            if (getSurfaceHolder() != null && getSurfaceHolder().getSurface() != null) {
                bool = Boolean.valueOf(getSurfaceHolder().getSurface().isValid());
            } else {
                bool = "null";
            }
            out.println(bool);
            out.print(prefix);
            out.print("surface frame=");
            out.println(getSurfaceHolder() != null ? getSurfaceHolder().getSurfaceFrame() : "null");
            this.mEglHelper.dump(prefix, fd, out, args);
            this.mRenderer.dump(prefix, fd, out, args);
        }
    }
}
