package com.badlogic.gdx.assets;
/* loaded from: classes21.dex */
public class AssetLoaderParameters<T> {
    public LoadedCallback loadedCallback;

    /* loaded from: classes21.dex */
    public interface LoadedCallback {
        void finishedLoading(AssetManager assetManager, String str, Class cls);
    }
}
