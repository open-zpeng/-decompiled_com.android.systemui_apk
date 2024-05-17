package com.badlogic.gdx.graphics.g3d.model;
/* loaded from: classes21.dex */
public class NodeKeyframe<T> {
    public float keytime;
    public final T value;

    public NodeKeyframe(float t, T v) {
        this.keytime = t;
        this.value = v;
    }
}
