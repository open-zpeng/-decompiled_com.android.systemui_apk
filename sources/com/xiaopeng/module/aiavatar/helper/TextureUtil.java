package com.xiaopeng.module.aiavatar.helper;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
/* loaded from: classes23.dex */
public class TextureUtil {
    public static void bindTexture(Texture texture, Bitmap bitmap) {
        if (texture == null || bitmap == null || bitmap.isRecycled()) {
            return;
        }
        GLES20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTextureObjectHandle());
        GLUtils.texImage2D(GL20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
    }

    public static boolean isTexture(GLTexture texture) {
        if (texture == null) {
            return false;
        }
        return Gdx.gl20.glIsTexture(texture.getTextureObjectHandle());
    }
}
