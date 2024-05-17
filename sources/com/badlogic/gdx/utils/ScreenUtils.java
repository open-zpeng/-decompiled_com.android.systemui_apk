package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import java.nio.ByteBuffer;
/* loaded from: classes21.dex */
public final class ScreenUtils {
    public static TextureRegion getFrameBufferTexture() {
        int w = Gdx.graphics.getBackBufferWidth();
        int h = Gdx.graphics.getBackBufferHeight();
        return getFrameBufferTexture(0, 0, w, h);
    }

    public static TextureRegion getFrameBufferTexture(int x, int y, int w, int h) {
        int potW = MathUtils.nextPowerOfTwo(w);
        int potH = MathUtils.nextPowerOfTwo(h);
        Pixmap pixmap = getFrameBufferPixmap(x, y, w, h);
        Pixmap potPixmap = new Pixmap(potW, potH, Pixmap.Format.RGBA8888);
        potPixmap.setBlending(Pixmap.Blending.None);
        potPixmap.drawPixmap(pixmap, 0, 0);
        Texture texture = new Texture(potPixmap);
        TextureRegion textureRegion = new TextureRegion(texture, 0, h, w, -h);
        potPixmap.dispose();
        pixmap.dispose();
        return textureRegion;
    }

    public static Pixmap getFrameBufferPixmap(int x, int y, int w, int h) {
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
        return pixmap;
    }

    public static byte[] getFrameBufferPixels(boolean flipY) {
        int w = Gdx.graphics.getBackBufferWidth();
        int h = Gdx.graphics.getBackBufferHeight();
        return getFrameBufferPixels(0, 0, w, h, flipY);
    }

    public static byte[] getFrameBufferPixels(int x, int y, int w, int h, boolean flipY) {
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
        ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 4);
        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
        int numBytes = w * h * 4;
        byte[] lines = new byte[numBytes];
        if (flipY) {
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position(((h - i) - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
        } else {
            pixels.clear();
            pixels.get(lines);
        }
        return lines;
    }
}
