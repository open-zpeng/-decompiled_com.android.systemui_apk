package com.com.badlogic.gdx.graphics.webp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
/* loaded from: classes21.dex */
public class WebpAnimationLruCache<K, V> extends LruCache<K, V> {
    private static final String TAG = "WebpAnimationLruCache";
    private OnEntryRemovedListener mListener;

    /* loaded from: classes21.dex */
    public interface OnEntryRemovedListener {
        void onEntryRemoved(Runnable runnable);
    }

    public WebpAnimationLruCache(int maxSize) {
        super(maxSize);
    }

    @Override // android.support.v4.util.LruCache
    protected void entryRemoved(boolean evicted, @NonNull K key, @NonNull final V oldValue, @Nullable V newValue) {
        if ((oldValue instanceof WebpAnimationTexture) && this.mListener != null) {
            Runnable entryRemovedTask = new Runnable() { // from class: com.com.badlogic.gdx.graphics.webp.WebpAnimationLruCache.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.d(WebpAnimationLruCache.TAG, "entryRemovedTask");
                    WebpAnimationTexture webpAnimationTexture = (WebpAnimationTexture) oldValue;
                    if (webpAnimationTexture.getSequence() != null) {
                        webpAnimationTexture.getSequence().destroy();
                    }
                }
            };
            this.mListener.onEntryRemoved(entryRemovedTask);
        }
    }

    public void setEntryRemovedListener(OnEntryRemovedListener listener) {
        this.mListener = listener;
    }
}
