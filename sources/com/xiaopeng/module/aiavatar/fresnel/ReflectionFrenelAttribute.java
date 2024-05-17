package com.xiaopeng.module.aiavatar.fresnel;

import com.badlogic.gdx.graphics.g3d.Attribute;
/* loaded from: classes23.dex */
public class ReflectionFrenelAttribute extends Attribute {
    public int mBodystatus;
    public static final String Alias = "reflectionFresnel";
    public static final long Type = register(Alias);
    protected static long Mask = Type;

    public ReflectionFrenelAttribute(long type) {
        super(type);
        this.mBodystatus = -1;
    }

    public void setBodyStatus(int status) {
        this.mBodystatus = status;
    }

    public int getBodyStatus() {
        return this.mBodystatus;
    }

    @Override // com.badlogic.gdx.graphics.g3d.Attribute
    public Attribute copy() {
        return new ReflectionFrenelAttribute(Type);
    }

    @Override // java.lang.Comparable
    public int compareTo(Attribute attribute) {
        return 0;
    }
}
