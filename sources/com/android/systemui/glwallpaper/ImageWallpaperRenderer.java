package com.android.systemui.glwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.util.Log;
import android.util.MathUtils;
import android.util.Size;
import android.view.DisplayInfo;
import com.android.systemui.R;
import com.android.systemui.glwallpaper.GLWallpaperRenderer;
import com.android.systemui.glwallpaper.ImageRevealHelper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class ImageWallpaperRenderer implements GLWallpaperRenderer, ImageRevealHelper.RevealStateListener {
    private static final boolean DEBUG = true;
    private static final float SCALE_VIEWPORT_MAX = 1.1f;
    private static final float SCALE_VIEWPORT_MIN = 1.0f;
    private static final String TAG = ImageWallpaperRenderer.class.getSimpleName();
    private Bitmap mBitmap;
    private final ImageProcessHelper mImageProcessHelper;
    private final ImageRevealHelper mImageRevealHelper;
    private final ImageGLProgram mProgram;
    private GLWallpaperRenderer.SurfaceProxy mProxy;
    private final Rect mScissor;
    private boolean mScissorMode;
    private final Rect mSurfaceSize = new Rect();
    private final Rect mViewport = new Rect();
    private final ImageGLWallpaper mWallpaper;
    private final WallpaperManager mWallpaperManager;
    private float mXOffset;
    private float mYOffset;

    public ImageWallpaperRenderer(Context context, GLWallpaperRenderer.SurfaceProxy proxy) {
        this.mWallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        if (this.mWallpaperManager == null) {
            Log.w(TAG, "WallpaperManager not available");
        }
        DisplayInfo displayInfo = new DisplayInfo();
        context.getDisplay().getDisplayInfo(displayInfo);
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == 1) {
            this.mScissor = new Rect(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        } else {
            this.mScissor = new Rect(0, 0, displayInfo.logicalHeight, displayInfo.logicalWidth);
        }
        this.mProxy = proxy;
        this.mProgram = new ImageGLProgram(context);
        this.mWallpaper = new ImageGLWallpaper(this.mProgram);
        this.mImageProcessHelper = new ImageProcessHelper();
        this.mImageRevealHelper = new ImageRevealHelper(this);
        if (loadBitmap()) {
            this.mImageProcessHelper.start(this.mBitmap);
        }
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void onSurfaceCreated() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.mProgram.useGLProgram(R.raw.image_wallpaper_vertex_shader, R.raw.image_wallpaper_fragment_shader);
        if (!loadBitmap()) {
            Log.w(TAG, "reload bitmap failed!");
        }
        this.mWallpaper.setup(this.mBitmap);
        this.mBitmap = null;
    }

    private boolean loadBitmap() {
        String str = TAG;
        Log.d(str, "loadBitmap: mBitmap=" + this.mBitmap);
        WallpaperManager wallpaperManager = this.mWallpaperManager;
        if (wallpaperManager != null && this.mBitmap == null) {
            this.mBitmap = wallpaperManager.getBitmap();
            this.mWallpaperManager.forgetLoadedWallpaper();
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null) {
                this.mSurfaceSize.set(0, 0, bitmap.getWidth(), this.mBitmap.getHeight());
            }
        }
        String str2 = TAG;
        Log.d(str2, "loadBitmap done, surface size=" + this.mSurfaceSize);
        return this.mBitmap != null;
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void onDrawFrame() {
        float threshold = this.mImageProcessHelper.getThreshold();
        float reveal = this.mImageRevealHelper.getReveal();
        GLES20.glUniform1f(this.mWallpaper.getHandle("uAod2Opacity"), 1.0f);
        GLES20.glUniform1f(this.mWallpaper.getHandle("uPer85"), threshold);
        GLES20.glUniform1f(this.mWallpaper.getHandle("uReveal"), reveal);
        GLES20.glClear(16384);
        if (this.mScissorMode) {
            scaleViewport(reveal);
        } else {
            GLES20.glViewport(0, 0, this.mSurfaceSize.width(), this.mSurfaceSize.height());
        }
        this.mWallpaper.useTexture();
        this.mWallpaper.draw();
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void updateAmbientMode(boolean inAmbientMode, long duration) {
        this.mImageRevealHelper.updateAwake(!inAmbientMode, duration);
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void updateOffsets(float xOffset, float yOffset) {
        this.mXOffset = xOffset;
        this.mYOffset = yOffset;
        int left = (int) ((this.mSurfaceSize.width() - this.mScissor.width()) * xOffset);
        int right = this.mScissor.width() + left;
        Rect rect = this.mScissor;
        rect.set(left, rect.top, right, this.mScissor.bottom);
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public Size reportSurfaceSize() {
        return new Size(this.mSurfaceSize.width(), this.mSurfaceSize.height());
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void finish() {
        this.mProxy = null;
    }

    private void scaleViewport(float reveal) {
        int left = this.mScissor.left;
        int top = this.mScissor.top;
        int width = this.mScissor.width();
        int height = this.mScissor.height();
        float vpScaled = MathUtils.lerp(1.0f, (float) SCALE_VIEWPORT_MAX, reveal);
        float offset = (1.0f - vpScaled) / 2.0f;
        this.mViewport.set((int) (left + (width * offset)), (int) (top + (height * offset)), (int) (width * vpScaled), (int) (height * vpScaled));
        GLES20.glViewport(this.mViewport.left, this.mViewport.top, this.mViewport.right, this.mViewport.bottom);
    }

    @Override // com.android.systemui.glwallpaper.ImageRevealHelper.RevealStateListener
    public void onRevealStateChanged() {
        this.mProxy.requestRender();
    }

    @Override // com.android.systemui.glwallpaper.ImageRevealHelper.RevealStateListener
    public void onRevealStart(boolean animate) {
        String str = TAG;
        Log.v(str, "onRevealStart: start, anim=" + animate);
        if (animate) {
            this.mScissorMode = true;
            this.mWallpaper.adjustTextureCoordinates(this.mSurfaceSize, this.mScissor, this.mXOffset, this.mYOffset);
        }
        this.mProxy.preRender();
        Log.v(TAG, "onRevealStart: done");
    }

    @Override // com.android.systemui.glwallpaper.ImageRevealHelper.RevealStateListener
    public void onRevealEnd() {
        String str = TAG;
        Log.v(str, "onRevealEnd: start, mScissorMode=" + this.mScissorMode);
        if (this.mScissorMode) {
            this.mScissorMode = false;
            this.mWallpaper.adjustTextureCoordinates(null, null, 0.0f, 0.0f);
            this.mProxy.requestRender();
        }
        this.mProxy.postRender();
        Log.v(TAG, "onRevealEnd: done");
    }

    @Override // com.android.systemui.glwallpaper.GLWallpaperRenderer
    public void dump(String prefix, FileDescriptor fd, PrintWriter out, String[] args) {
        out.print(prefix);
        out.print("mProxy=");
        out.print(this.mProxy);
        out.print(prefix);
        out.print("mSurfaceSize=");
        out.print(this.mSurfaceSize);
        out.print(prefix);
        out.print("mScissor=");
        out.print(this.mScissor);
        out.print(prefix);
        out.print("mViewport=");
        out.print(this.mViewport);
        out.print(prefix);
        out.print("mScissorMode=");
        out.print(this.mScissorMode);
        out.print(prefix);
        out.print("mXOffset=");
        out.print(this.mXOffset);
        out.print(prefix);
        out.print("mYOffset=");
        out.print(this.mYOffset);
        out.print(prefix);
        out.print("threshold=");
        out.print(this.mImageProcessHelper.getThreshold());
        out.print(prefix);
        out.print("mReveal=");
        out.print(this.mImageRevealHelper.getReveal());
        this.mWallpaper.dump(prefix, fd, out, args);
    }
}
