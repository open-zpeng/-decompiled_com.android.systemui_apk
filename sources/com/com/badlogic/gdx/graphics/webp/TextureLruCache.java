package com.com.badlogic.gdx.graphics.webp;

import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import com.com.badlogic.gdx.graphics.webp.WebpAnimationTexture;
/* loaded from: classes21.dex */
public class TextureLruCache<K, V> extends LruCache<K, V> {
    private static final String TAG = "TextureLruCache";
    private GLSurfaceView glSurfaceView;

    public TextureLruCache(int maxSize) {
        super(maxSize);
    }

    @Override // android.support.v4.util.LruCache
    protected void entryRemoved(boolean evicted, @NonNull K key, @NonNull final V oldValue, @Nullable V newValue) {
        GLSurfaceView gLSurfaceView;
        if ((oldValue instanceof WebpAnimationTexture.WebpFrame) && (gLSurfaceView = this.glSurfaceView) != null) {
            gLSurfaceView.queueEvent(new Runnable() { // from class: com.com.badlogic.gdx.graphics.webp.TextureLruCache.1
                @Override // java.lang.Runnable
                public void run() {
                    WebpAnimationTexture.WebpFrame webpFrame = (WebpAnimationTexture.WebpFrame) oldValue;
                    if (webpFrame.mTexture != null) {
                        webpFrame.mTexture.dispose();
                        webpFrame.mTextureAttribute = null;
                        webpFrame.mTexture = null;
                    }
                }
            });
        }
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
    }
}
