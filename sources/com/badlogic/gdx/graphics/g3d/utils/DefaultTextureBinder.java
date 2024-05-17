package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.IntBuffer;
/* loaded from: classes21.dex */
public final class DefaultTextureBinder implements TextureBinder {
    public static final int MAX_GLES_UNITS = 32;
    public static final int ROUNDROBIN = 0;
    public static final int WEIGHTED = 1;
    private int bindCount;
    private final int count;
    private int currentTexture;
    private final int method;
    private final int offset;
    private int reuseCount;
    private final int reuseWeight;
    private boolean reused;
    private final TextureDescriptor tempDesc;
    private final GLTexture[] textures;
    private final int[] weights;

    public DefaultTextureBinder(int method) {
        this(method, 0);
    }

    public DefaultTextureBinder(int method, int offset) {
        this(method, offset, -1);
    }

    public DefaultTextureBinder(int method, int offset, int count) {
        this(method, offset, count, 10);
    }

    public DefaultTextureBinder(int method, int offset, int count, int reuseWeight) {
        this.reuseCount = 0;
        this.bindCount = 0;
        this.tempDesc = new TextureDescriptor();
        this.currentTexture = 0;
        int max = Math.min(getMaxTextureUnits(), 32);
        count = count < 0 ? max - offset : count;
        if (offset < 0 || count < 0 || offset + count > max || reuseWeight < 1) {
            throw new GdxRuntimeException("Illegal arguments");
        }
        this.method = method;
        this.offset = offset;
        this.count = count;
        this.textures = new GLTexture[count];
        this.reuseWeight = reuseWeight;
        this.weights = method == 1 ? new int[count] : null;
    }

    private static int getMaxTextureUnits() {
        IntBuffer buffer = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
        return buffer.get(0);
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public void begin() {
        for (int i = 0; i < this.count; i++) {
            this.textures[i] = null;
            int[] iArr = this.weights;
            if (iArr != null) {
                iArr[i] = 0;
            }
        }
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public void end() {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public final int bind(TextureDescriptor textureDesc) {
        return bindTexture(textureDesc, false);
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public final int bind(GLTexture texture) {
        this.tempDesc.set(texture, null, null, null, null);
        return bindTexture(this.tempDesc, false);
    }

    private final int bindTexture(TextureDescriptor textureDesc, boolean rebind) {
        int result;
        GLTexture texture = textureDesc.texture;
        this.reused = false;
        int i = this.method;
        if (i == 0) {
            int result2 = this.offset;
            result = result2 + bindTextureRoundRobin(texture);
        } else if (i == 1) {
            result = this.offset + bindTextureWeighted(texture);
        } else {
            return -1;
        }
        if (this.reused) {
            this.reuseCount++;
            if (rebind) {
                texture.bind(result);
            } else {
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + result);
            }
        } else {
            this.bindCount++;
        }
        texture.unsafeSetWrap(textureDesc.uWrap, textureDesc.vWrap);
        texture.unsafeSetFilter(textureDesc.minFilter, textureDesc.magFilter);
        return result;
    }

    private final int bindTextureRoundRobin(GLTexture texture) {
        int i = 0;
        while (true) {
            int i2 = this.count;
            if (i < i2) {
                int idx = (this.currentTexture + i) % i2;
                if (this.textures[idx] != texture) {
                    i++;
                } else {
                    this.reused = true;
                    return idx;
                }
            } else {
                int i3 = this.currentTexture;
                this.currentTexture = (i3 + 1) % i2;
                GLTexture[] gLTextureArr = this.textures;
                int i4 = this.currentTexture;
                gLTextureArr[i4] = texture;
                texture.bind(this.offset + i4);
                return this.currentTexture;
            }
        }
    }

    private final int bindTextureWeighted(GLTexture texture) {
        int result = -1;
        int weight = this.weights[0];
        int windex = 0;
        for (int i = 0; i < this.count; i++) {
            if (this.textures[i] == texture) {
                result = i;
                int[] iArr = this.weights;
                iArr[i] = iArr[i] + this.reuseWeight;
            } else {
                int[] iArr2 = this.weights;
                if (iArr2[i] >= 0) {
                    int i2 = iArr2[i] - 1;
                    iArr2[i] = i2;
                    if (i2 >= weight) {
                    }
                }
                weight = this.weights[i];
                windex = i;
            }
        }
        if (result < 0) {
            this.textures[windex] = texture;
            this.weights[windex] = 100;
            int result2 = windex;
            texture.bind(this.offset + windex);
            return result2;
        }
        this.reused = true;
        return result;
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public final int getBindCount() {
        return this.bindCount;
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public final int getReuseCount() {
        return this.reuseCount;
    }

    @Override // com.badlogic.gdx.graphics.g3d.utils.TextureBinder
    public final void resetCounts() {
        this.reuseCount = 0;
        this.bindCount = 0;
    }
}
