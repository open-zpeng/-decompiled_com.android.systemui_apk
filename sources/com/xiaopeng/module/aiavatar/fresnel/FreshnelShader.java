package com.xiaopeng.module.aiavatar.fresnel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapLightAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ScreenBlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
/* loaded from: classes23.dex */
public class FreshnelShader extends DefaultShader {
    private static final Attributes tmpAttributes = new Attributes();
    public int u_bodyRenderY;
    public int u_defaultEndColor;
    public int u_defaultStartColor;
    public int u_environmentCubemap2;
    public int u_fresneWarningLevel;
    public int u_fresne_window_status;
    public int u_fresnebodystatus;
    public int u_fresnelCenterColor;
    public int u_fresnelEndColor;
    public int u_fresnelStartColor;
    public int u_fresnelfactor;
    public int u_fresnelow;
    public int u_fresnelpow;
    public int u_interpolatedTime;

    /* loaded from: classes23.dex */
    public static class Setters {
        public static final BaseShader.Setter defaultStartColor = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.1
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    Color color = attribute.getDefaultStartColor();
                    shader.set(inputID, color.r, color.g, color.b);
                }
            }
        };
        public static final BaseShader.Setter defaultEndColor = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.2
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    Color color = attribute.getDefaultEndColor();
                    shader.set(inputID, color.r, color.g, color.b);
                }
            }
        };
        public static final BaseShader.Setter fresnelStartColor = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.3
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    Color color = attribute.getStartColor();
                    shader.set(inputID, color.r, color.g, color.b);
                }
            }
        };
        public static final BaseShader.Setter fresnelCenterColor = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.4
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    Color color = attribute.getCenterColor();
                    shader.set(inputID, color.r, color.g, color.b);
                }
            }
        };
        public static final BaseShader.Setter fresnelEndColor = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.5
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    Color color = attribute.getEndColor();
                    shader.set(inputID, color.r, color.g, color.b);
                }
            }
        };
        public static final BaseShader.Setter fresnelLow = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.6
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    float low = attribute.getLow();
                    shader.set(inputID, low);
                }
            }
        };
        public static final BaseShader.Setter fresnelFactor = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.7
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    float factor = attribute.getFactor();
                    shader.set(inputID, factor);
                }
            }
        };
        public static final BaseShader.Setter fresnelPow = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.8
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    int pow = attribute.getPow();
                    shader.set(inputID, pow);
                }
            }
        };
        public static final BaseShader.Setter warningLevel = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.9
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    int warningLevel2 = attribute.getWarningLevel();
                    shader.set(inputID, warningLevel2);
                }
            }
        };
        public static final BaseShader.Setter interpolatedTime = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.10
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    float warningLevel2 = attribute.getInterpolatedTime();
                    shader.set(inputID, warningLevel2);
                }
            }
        };
        public static final BaseShader.Setter fresnelBodyStatus = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.11
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    int bodyStatus = attribute.getBodyStatus();
                    shader.set(inputID, bodyStatus);
                    return;
                }
                ReflectionFrenelAttribute frenelattribute = (ReflectionFrenelAttribute) combinedAttributes.get(ReflectionFrenelAttribute.Type);
                if (frenelattribute != null) {
                    int bodyStatus2 = frenelattribute.getBodyStatus();
                    shader.set(inputID, bodyStatus2);
                }
            }
        };
        public static final BaseShader.Setter fresnelWindowStatus = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.12
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    int status = attribute.getWindowStatus();
                    shader.set(inputID, status);
                }
            }
        };
        public static final BaseShader.Setter environmentCubemap2 = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.13
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                if (combinedAttributes.has(CubemapLightAttribute.EnvironmentMap)) {
                    shader.set(inputID, shader.context.textureBinder.bind(((CubemapLightAttribute) combinedAttributes.get(CubemapLightAttribute.EnvironmentMap)).textureDescription));
                }
            }
        };
        public static final BaseShader.Setter bodyRenderY = new BaseShader.LocalSetter() { // from class: com.xiaopeng.module.aiavatar.fresnel.FreshnelShader.Setters.14
            @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Setter
            public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                FresnelAttribute attribute = (FresnelAttribute) combinedAttributes.get(FresnelAttribute.Type);
                if (attribute != null) {
                    float renderY = attribute.getBodyRenderY();
                    shader.set(inputID, renderY);
                }
            }
        };
    }

    public FreshnelShader(Renderable renderable) {
        super(renderable);
    }

    public FreshnelShader(Renderable renderable, DefaultShader.Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }

    public FreshnelShader(Renderable renderable, DefaultShader.Config config, String prefix) {
        this(renderable, config, prefix, config.vertexShader != null ? config.vertexShader : getDefaultVertexShader(), config.fragmentShader != null ? config.fragmentShader : getDefaultFragmentShader());
    }

    public FreshnelShader(Renderable renderable, DefaultShader.Config config, String prefix, String vertexShader, String fragmentShader) {
        this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public FreshnelShader(Renderable renderable, DefaultShader.Config config, ShaderProgram shaderProgram) {
        super(renderable, config, shaderProgram);
        this.u_defaultStartColor = register(new BaseShader.Uniform("u_defaultstartcolor"), Setters.defaultStartColor);
        this.u_defaultEndColor = register(new BaseShader.Uniform("u_defaultendcolor"), Setters.defaultEndColor);
        this.u_fresnelStartColor = register(new BaseShader.Uniform("u_fresnelstartcolor"), Setters.fresnelStartColor);
        this.u_fresnelCenterColor = register(new BaseShader.Uniform("u_fresnelcentercolor"), Setters.fresnelCenterColor);
        this.u_fresnelEndColor = register(new BaseShader.Uniform("u_fresnelendcolor"), Setters.fresnelEndColor);
        this.u_fresnelow = register(new BaseShader.Uniform("u_fresnellow"), Setters.fresnelLow);
        this.u_fresnebodystatus = register(new BaseShader.Uniform("u_fresnelbodystatus"), Setters.fresnelBodyStatus);
        this.u_fresnelfactor = register(new BaseShader.Uniform("u_fresnelfactor"), Setters.fresnelFactor);
        this.u_fresnelpow = register(new BaseShader.Uniform("u_fresnelpow"), Setters.fresnelPow);
        this.u_fresneWarningLevel = register(new BaseShader.Uniform("u_warninglevel"), Setters.warningLevel);
        this.u_interpolatedTime = register(new BaseShader.Uniform("u_interpolatedtime"), Setters.interpolatedTime);
        this.u_fresne_window_status = register(new BaseShader.Uniform("u_fresne_window_status"), Setters.fresnelWindowStatus);
        this.u_environmentCubemap2 = register(new BaseShader.Uniform("u_environmentCubemap2"), Setters.environmentCubemap2);
        this.u_bodyRenderY = register(new BaseShader.Uniform("u_bodyRenderY"), Setters.bodyRenderY);
    }

    public static String createPrefix(Renderable renderable, DefaultShader.Config config) {
        String prefix = DefaultShader.createPrefix(renderable, config);
        Attributes attributes = combineAttributes(renderable);
        if (attributes.has(FresnelAttribute.Type)) {
            prefix = prefix + "#define fresnelFlag\n";
        }
        if (attributes.has(ReflectionFrenelAttribute.Type)) {
            prefix = prefix + "#define reflectionFresnelFlag\n";
        }
        if (attributes.has(ScreenBlendingAttribute.Type)) {
            return prefix + "#define screenBlendingFlag\n";
        }
        return prefix;
    }

    private static final Attributes combineAttributes(Renderable renderable) {
        tmpAttributes.clear();
        if (renderable.environment != null) {
            tmpAttributes.set(renderable.environment);
        }
        if (renderable.material != null) {
            tmpAttributes.set(renderable.material);
        }
        return tmpAttributes;
    }

    @Override // com.badlogic.gdx.graphics.g3d.shaders.BaseShader, com.badlogic.gdx.graphics.g3d.Shader
    public void render(Renderable renderable) {
        super.render(renderable);
    }
}
