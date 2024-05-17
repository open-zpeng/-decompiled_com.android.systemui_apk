package com.xiaopeng.module.aiavatar.graphics.g3d;

import com.badlogic.gdx.graphics.Color;
import java.io.Serializable;
/* loaded from: classes23.dex */
public class SenceConfiguration implements Serializable {
    private boolean mShowFps = false;
    private boolean mShowAxe = false;
    private boolean mRotaCamera = true;
    private Color mBgColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    private boolean showLog = false;
    private boolean mShowOpengGL = false;
    private boolean mURevers = true;
    private boolean mDebug = false;

    public boolean ismShowOpengGL() {
        return this.mShowOpengGL;
    }

    public void setmShowOpengGL(boolean mShowOpengGL) {
        this.mShowOpengGL = mShowOpengGL;
    }

    public boolean ismDebug() {
        return this.mDebug;
    }

    public void setmDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    public boolean ismURevers() {
        return this.mURevers;
    }

    public void setmURevers(boolean mURevers) {
        this.mURevers = mURevers;
    }

    public boolean ismShowAxe() {
        return this.mShowAxe;
    }

    public boolean isShowLog() {
        return this.showLog;
    }

    public void setmBgColor(Color mBgColor) {
        this.mBgColor = mBgColor;
    }

    public void setmShowFps(boolean mShowFps) {
        this.mShowFps = mShowFps;
    }

    public void setmShowAxe(boolean mShowAxe) {
        this.mShowAxe = mShowAxe;
    }

    public void setmRotaCamera(boolean mRotaCamera) {
        this.mRotaCamera = mRotaCamera;
    }

    public void reset() {
        this.mShowFps = false;
        this.mShowAxe = false;
        this.mRotaCamera = false;
        this.showLog = false;
        this.mShowOpengGL = false;
        this.mURevers = true;
        this.mDebug = false;
    }

    public void debugModel() {
        this.mShowFps = true;
        this.mShowAxe = true;
        this.mRotaCamera = false;
        this.showLog = true;
        this.mShowOpengGL = true;
        this.mURevers = true;
        this.mDebug = true;
    }

    public String toString() {
        return "SenceConfiguration{mShowFps=" + this.mShowFps + ", mShowAxe=" + this.mShowAxe + ", mBgColor=" + this.mBgColor + ", showLog=" + this.showLog + ", mShowOpengGL=" + this.mShowOpengGL + ", mURevers=" + this.mURevers + ", mDebug=" + this.mDebug + '}';
    }

    public boolean ismShowFps() {
        return this.mShowFps;
    }
}
