package com.xiaopeng.xui.sound;
/* loaded from: classes25.dex */
class SoundEffectResource {
    private int location;
    private String path;
    private int resId;
    private int streamType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SoundEffectResource(String path, int location, int streamType) {
        this.path = path;
        this.location = location;
        this.streamType = streamType;
    }

    SoundEffectResource(int resId, int streamType) {
        this.resId = resId;
        this.location = 2;
        this.streamType = streamType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getPath() {
        return this.path;
    }

    int getResId() {
        return this.resId;
    }

    void setResId(int resId) {
        this.resId = resId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLocation() {
        return this.location;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getStreamType() {
        return this.streamType;
    }
}
