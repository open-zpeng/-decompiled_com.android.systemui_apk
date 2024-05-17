package com.xiaopeng.systemui.speech.model;
/* loaded from: classes24.dex */
public class SpeechWindowInfo {
    private int gravity;
    private int h;
    private String name;
    private int w;
    private int windowType;
    private int x;
    private int y;

    public SpeechWindowInfo(String name, int x, int y, int w, int h, int gravity, int windowType) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.gravity = gravity;
        this.windowType = windowType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getGravity() {
        return this.gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public int getW() {
        return this.w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return this.h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getWindowType() {
        return this.windowType;
    }

    public void setWindowType(int windowType) {
        this.windowType = windowType;
    }

    public String toString() {
        return "SpeechWindowInfo{name='" + this.name + "', x=" + this.x + ", y=" + this.y + ", w=" + this.w + ", h=" + this.h + ", gravity=" + this.gravity + ", windowType=" + this.windowType + '}';
    }
}
