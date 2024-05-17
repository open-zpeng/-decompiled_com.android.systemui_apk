package com.badlogic.gdx.graphics.g3d.model.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
/* loaded from: classes21.dex */
public class ModelMaterial {
    public Color ambient;
    public Color diffuse;
    public Color emissive;
    public String id;
    public float opacity = 1.0f;
    public Color reflection;
    public float shininess;
    public Color specular;
    public Array<ModelTexture> textures;
    public MaterialType type;

    /* loaded from: classes21.dex */
    public enum MaterialType {
        Lambert,
        Phong
    }
}
