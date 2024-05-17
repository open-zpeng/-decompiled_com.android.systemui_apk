package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
/* loaded from: classes21.dex */
public interface ShaderProvider {
    void dispose();

    Shader getShader(Renderable renderable);
}
