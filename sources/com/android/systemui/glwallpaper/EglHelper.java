package com.android.systemui.glwallpaper;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class EglHelper {
    private static final int EGL_CONTEXT_PRIORITY_LEVEL_IMG = 12544;
    private static final int EGL_CONTEXT_PRIORITY_LOW_IMG = 12547;
    private static final String TAG = EglHelper.class.getSimpleName();
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private boolean mEglReady;
    private EGLSurface mEglSurface;
    private final int[] mEglVersion = new int[2];

    public boolean init(SurfaceHolder surfaceHolder) {
        this.mEglDisplay = EGL14.eglGetDisplay(0);
        if (this.mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            String str = TAG;
            Log.w(str, "eglGetDisplay failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            return false;
        }
        EGLDisplay eGLDisplay = this.mEglDisplay;
        int[] iArr = this.mEglVersion;
        if (!EGL14.eglInitialize(eGLDisplay, iArr, 0, iArr, 1)) {
            String str2 = TAG;
            Log.w(str2, "eglInitialize failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            return false;
        }
        this.mEglConfig = chooseEglConfig();
        if (this.mEglConfig == null) {
            Log.w(TAG, "eglConfig not initialized!");
            return false;
        } else if (!createEglContext()) {
            Log.w(TAG, "Can't create EGLContext!");
            return false;
        } else if (!createEglSurface(surfaceHolder)) {
            Log.w(TAG, "Can't create EGLSurface!");
            return false;
        } else {
            this.mEglReady = true;
            return true;
        }
    }

    private EGLConfig chooseEglConfig() {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        int[] configSpec = getConfig();
        if (!EGL14.eglChooseConfig(this.mEglDisplay, configSpec, 0, configs, 0, 1, configsCount, 0)) {
            String str = TAG;
            Log.w(str, "eglChooseConfig failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            return null;
        } else if (configsCount[0] <= 0) {
            String str2 = TAG;
            Log.w(str2, "eglChooseConfig failed, invalid configs count: " + configsCount[0]);
            return null;
        } else {
            return configs[0];
        }
    }

    private int[] getConfig() {
        return new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12352, 4, 12327, 12344, 12344};
    }

    public boolean createEglSurface(SurfaceHolder surfaceHolder) {
        if (hasEglDisplay()) {
            this.mEglSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceHolder, null, 0);
            EGLSurface eGLSurface = this.mEglSurface;
            if (eGLSurface == null || eGLSurface == EGL14.EGL_NO_SURFACE) {
                String str = TAG;
                Log.w(str, "createWindowSurface failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                return false;
            }
            EGLDisplay eGLDisplay = this.mEglDisplay;
            EGLSurface eGLSurface2 = this.mEglSurface;
            if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.mEglContext)) {
                String str2 = TAG;
                Log.w(str2, "eglMakeCurrent failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                return false;
            }
            return true;
        }
        Log.w(TAG, "mEglDisplay is null");
        return false;
    }

    public void destroyEglSurface() {
        if (hasEglSurface()) {
            EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
            this.mEglSurface = null;
        }
    }

    public boolean hasEglSurface() {
        EGLSurface eGLSurface = this.mEglSurface;
        return (eGLSurface == null || eGLSurface == EGL14.EGL_NO_SURFACE) ? false : true;
    }

    public boolean createEglContext() {
        int[] attrib_list = {12440, 2, EGL_CONTEXT_PRIORITY_LEVEL_IMG, EGL_CONTEXT_PRIORITY_LOW_IMG, 12344};
        if (hasEglDisplay()) {
            this.mEglContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL14.EGL_NO_CONTEXT, attrib_list, 0);
            if (this.mEglContext == EGL14.EGL_NO_CONTEXT) {
                String str = TAG;
                Log.w(str, "eglCreateContext failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                return false;
            }
            return true;
        }
        Log.w(TAG, "mEglDisplay is null");
        return false;
    }

    public void destroyEglContext() {
        if (hasEglContext()) {
            EGL14.eglDestroyContext(this.mEglDisplay, this.mEglContext);
            this.mEglContext = null;
        }
    }

    public boolean hasEglContext() {
        return this.mEglContext != null;
    }

    public boolean hasEglDisplay() {
        return this.mEglDisplay != null;
    }

    public boolean swapBuffer() {
        boolean status = EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
        int error = EGL14.eglGetError();
        if (error != 12288) {
            String str = TAG;
            Log.w(str, "eglSwapBuffers failed: " + GLUtils.getEGLErrorString(error));
        }
        return status;
    }

    public void finish() {
        if (hasEglSurface()) {
            destroyEglSurface();
        }
        if (hasEglContext()) {
            destroyEglContext();
        }
        if (hasEglDisplay()) {
            EGL14.eglTerminate(this.mEglDisplay);
        }
        this.mEglReady = false;
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter out, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mEglVersion[0]);
        sb.append(".");
        sb.append(this.mEglVersion[1]);
        String eglVersion = sb.toString();
        out.print(prefix);
        out.print("EGL version=");
        out.print(eglVersion);
        out.print(", ");
        out.print("EGL ready=");
        out.print(this.mEglReady);
        out.print(", ");
        out.print("has EglContext=");
        out.print(hasEglContext());
        out.print(", ");
        out.print("has EglSurface=");
        out.println(hasEglSurface());
        int[] configs = getConfig();
        StringBuilder sb2 = new StringBuilder();
        sb2.append('{');
        for (int egl : configs) {
            sb2.append("0x");
            sb2.append(Integer.toHexString(egl));
            sb2.append(",");
        }
        sb2.setCharAt(sb2.length() - 1, '}');
        out.print(prefix);
        out.print("EglConfig=");
        out.println(sb2.toString());
    }
}
