package com.xiaopeng.module.aiavatar.fresnel;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
/* loaded from: classes23.dex */
public class FreshnelShaderProvider extends BaseShaderProvider {
    public final DefaultShader.Config config;

    public FreshnelShaderProvider(DefaultShader.Config config) {
        this.config = config == null ? new DefaultShader.Config() : config;
    }

    public FreshnelShaderProvider(String vertexShader, String fragmentShader) {
        this(new DefaultShader.Config(vertexShader, fragmentShader));
    }

    public FreshnelShaderProvider(FileHandle vertexShader, FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }

    public FreshnelShaderProvider() {
        this(null);
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
    protected Shader createShader(Renderable renderable) {
        return new FreshnelShader(renderable, this.config);
    }
}
