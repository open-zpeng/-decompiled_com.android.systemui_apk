package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;
/* loaded from: classes21.dex */
public class ScreenBlendingAttribute extends Attribute {
    public static final String Alias = "ScreenBlendingAttribute";
    public static final long Type = register(Alias);
    public static long Mask = Type;

    public ScreenBlendingAttribute(long type) {
        super(type);
    }

    @Override // com.badlogic.gdx.graphics.g3d.Attribute
    public Attribute copy() {
        return new ScreenBlendingAttribute(Type);
    }

    @Override // java.lang.Comparable
    public int compareTo(Attribute attribute) {
        return 0;
    }
}
