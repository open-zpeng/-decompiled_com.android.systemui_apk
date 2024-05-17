package com.badlogic.gdx.assets;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonRegionLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.util.Iterator;
import java.util.Stack;
/* loaded from: classes21.dex */
public class AssetManager implements Disposable {
    final ObjectMap<String, Array<String>> assetDependencies;
    final ObjectMap<String, Class> assetTypes;
    final ObjectMap<Class, ObjectMap<String, RefCountedContainer>> assets;
    final AsyncExecutor executor;
    final ObjectSet<String> injected;
    AssetErrorListener listener;
    final Array<AssetDescriptor> loadQueue;
    int loaded;
    final ObjectMap<Class, ObjectMap<String, AssetLoader>> loaders;
    Logger log;
    int peakTasks;
    final FileHandleResolver resolver;
    final Stack<AssetLoadingTask> tasks;
    int toLoad;

    public AssetManager() {
        this(new InternalFileHandleResolver());
    }

    public AssetManager(FileHandleResolver resolver) {
        this(resolver, true);
    }

    public AssetManager(FileHandleResolver resolver, boolean defaultLoaders) {
        this.assets = new ObjectMap<>();
        this.assetTypes = new ObjectMap<>();
        this.assetDependencies = new ObjectMap<>();
        this.injected = new ObjectSet<>();
        this.loaders = new ObjectMap<>();
        this.loadQueue = new Array<>();
        this.tasks = new Stack<>();
        this.listener = null;
        this.loaded = 0;
        this.toLoad = 0;
        this.peakTasks = 0;
        this.log = new Logger("AssetManager", 0);
        this.resolver = resolver;
        if (defaultLoaders) {
            setLoader(BitmapFont.class, new BitmapFontLoader(resolver));
            setLoader(Music.class, new MusicLoader(resolver));
            setLoader(Pixmap.class, new PixmapLoader(resolver));
            setLoader(Sound.class, new SoundLoader(resolver));
            setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
            setLoader(Texture.class, new TextureLoader(resolver));
            setLoader(Skin.class, new SkinLoader(resolver));
            setLoader(ParticleEffect.class, new ParticleEffectLoader(resolver));
            setLoader(com.badlogic.gdx.graphics.g3d.particles.ParticleEffect.class, new com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader(resolver));
            setLoader(PolygonRegion.class, new PolygonRegionLoader(resolver));
            setLoader(I18NBundle.class, new I18NBundleLoader(resolver));
            setLoader(Model.class, ".g3dj", new G3dModelLoader(new JsonReader(), resolver));
            setLoader(Model.class, ".g3db", new G3dModelLoader(new UBJsonReader(), resolver));
            setLoader(Model.class, ".obj", new ObjLoader(resolver));
            setLoader(ShaderProgram.class, new ShaderProgramLoader(resolver));
            setLoader(Cubemap.class, new CubemapLoader(resolver));
        }
        this.executor = new AsyncExecutor(1, "AssetManager");
    }

    public FileHandleResolver getFileHandleResolver() {
        return this.resolver;
    }

    public synchronized <T> T get(String fileName) {
        T asset;
        Class<T> type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        asset = (T) assetContainer.getObject(type);
        if (asset == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        T asset;
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        asset = (T) assetContainer.getObject(type);
        if (asset == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public synchronized <T> Array<T> getAll(Class<T> type, Array<T> out) {
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType != null) {
            ObjectMap.Entries<String, RefCountedContainer> it = assetsByType.entries().iterator();
            while (it.hasNext()) {
                ObjectMap.Entry asset = it.next();
                out.add(((RefCountedContainer) asset.value).getObject(type));
            }
        }
        return out;
    }

    public synchronized <T> T get(AssetDescriptor<T> assetDescriptor) {
        return (T) get(assetDescriptor.fileName, assetDescriptor.type);
    }

    public synchronized boolean contains(String fileName) {
        if (this.tasks.size() <= 0 || !this.tasks.firstElement().assetDesc.fileName.equals(fileName)) {
            for (int i = 0; i < this.loadQueue.size; i++) {
                if (this.loadQueue.get(i).fileName.equals(fileName)) {
                    return true;
                }
            }
            return isLoaded(fileName);
        }
        return true;
    }

    public synchronized boolean contains(String fileName, Class type) {
        if (this.tasks.size() > 0) {
            AssetDescriptor assetDesc = this.tasks.firstElement().assetDesc;
            if (assetDesc.type == type && assetDesc.fileName.equals(fileName)) {
                return true;
            }
        }
        for (int i = 0; i < this.loadQueue.size; i++) {
            AssetDescriptor assetDesc2 = this.loadQueue.get(i);
            if (assetDesc2.type == type && assetDesc2.fileName.equals(fileName)) {
                return true;
            }
        }
        return isLoaded(fileName, type);
    }

    public synchronized void unload(String fileName) {
        if (this.tasks.size() > 0) {
            AssetLoadingTask currAsset = this.tasks.firstElement();
            if (currAsset.assetDesc.fileName.equals(fileName)) {
                currAsset.cancel = true;
                this.log.info("Unload (from tasks): " + fileName);
                return;
            }
        }
        int foundIndex = -1;
        int i = 0;
        while (true) {
            if (i >= this.loadQueue.size) {
                break;
            } else if (!this.loadQueue.get(i).fileName.equals(fileName)) {
                i++;
            } else {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != -1) {
            this.toLoad--;
            this.loadQueue.removeIndex(foundIndex);
            this.log.info("Unload (from queue): " + fileName);
            return;
        }
        Class type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        RefCountedContainer assetRef = this.assets.get(type).get(fileName);
        assetRef.decRefCount();
        if (assetRef.getRefCount() <= 0) {
            this.log.info("Unload (dispose): " + fileName);
            if (assetRef.getObject(Object.class) instanceof Disposable) {
                ((Disposable) assetRef.getObject(Object.class)).dispose();
            }
            this.assetTypes.remove(fileName);
            this.assets.get(type).remove(fileName);
        } else {
            this.log.info("Unload (decrement): " + fileName);
        }
        Array<String> dependencies = this.assetDependencies.get(fileName);
        if (dependencies != null) {
            Iterator<String> it = dependencies.iterator();
            while (it.hasNext()) {
                String dependency = it.next();
                if (isLoaded(dependency)) {
                    unload(dependency);
                }
            }
        }
        if (assetRef.getRefCount() <= 0) {
            this.assetDependencies.remove(fileName);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0020 A[Catch: all -> 0x0041, TryCatch #0 {, blocks: (B:3:0x0001, B:8:0x0012, B:9:0x001a, B:11:0x0020, B:13:0x0034), top: B:25:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized <T> boolean containsAsset(T r7) {
        /*
            r6 = this;
            monitor-enter(r6)
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r0 = r6.assets     // Catch: java.lang.Throwable -> L41
            java.lang.Class r1 = r7.getClass()     // Catch: java.lang.Throwable -> L41
            java.lang.Object r0 = r0.get(r1)     // Catch: java.lang.Throwable -> L41
            com.badlogic.gdx.utils.ObjectMap r0 = (com.badlogic.gdx.utils.ObjectMap) r0     // Catch: java.lang.Throwable -> L41
            r1 = 0
            if (r0 != 0) goto L12
            monitor-exit(r6)
            return r1
        L12:
            com.badlogic.gdx.utils.ObjectMap$Keys r2 = r0.keys()     // Catch: java.lang.Throwable -> L41
            com.badlogic.gdx.utils.ObjectMap$Keys r2 = r2.iterator()     // Catch: java.lang.Throwable -> L41
        L1a:
            boolean r3 = r2.hasNext()     // Catch: java.lang.Throwable -> L41
            if (r3 == 0) goto L3f
            java.lang.Object r3 = r2.next()     // Catch: java.lang.Throwable -> L41
            java.lang.String r3 = (java.lang.String) r3     // Catch: java.lang.Throwable -> L41
            java.lang.Object r4 = r0.get(r3)     // Catch: java.lang.Throwable -> L41
            com.badlogic.gdx.assets.RefCountedContainer r4 = (com.badlogic.gdx.assets.RefCountedContainer) r4     // Catch: java.lang.Throwable -> L41
            java.lang.Class<java.lang.Object> r5 = java.lang.Object.class
            java.lang.Object r4 = r4.getObject(r5)     // Catch: java.lang.Throwable -> L41
            if (r4 == r7) goto L3c
            boolean r5 = r7.equals(r4)     // Catch: java.lang.Throwable -> L41
            if (r5 == 0) goto L3b
            goto L3c
        L3b:
            goto L1a
        L3c:
            r1 = 1
            monitor-exit(r6)
            return r1
        L3f:
            monitor-exit(r6)
            return r1
        L41:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.containsAsset(java.lang.Object):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x002d A[Catch: all -> 0x004f, TryCatch #0 {, blocks: (B:3:0x0001, B:4:0x000b, B:6:0x0011, B:7:0x0027, B:9:0x002d, B:11:0x0041), top: B:24:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized <T> java.lang.String getAssetFileName(T r8) {
        /*
            r7 = this;
            monitor-enter(r7)
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r0 = r7.assets     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.utils.ObjectMap$Keys r0 = r0.keys()     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.utils.ObjectMap$Keys r0 = r0.iterator()     // Catch: java.lang.Throwable -> L4f
        Lb:
            boolean r1 = r0.hasNext()     // Catch: java.lang.Throwable -> L4f
            if (r1 == 0) goto L4c
            java.lang.Object r1 = r0.next()     // Catch: java.lang.Throwable -> L4f
            java.lang.Class r1 = (java.lang.Class) r1     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r2 = r7.assets     // Catch: java.lang.Throwable -> L4f
            java.lang.Object r2 = r2.get(r1)     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.utils.ObjectMap r2 = (com.badlogic.gdx.utils.ObjectMap) r2     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.utils.ObjectMap$Keys r3 = r2.keys()     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.utils.ObjectMap$Keys r3 = r3.iterator()     // Catch: java.lang.Throwable -> L4f
        L27:
            boolean r4 = r3.hasNext()     // Catch: java.lang.Throwable -> L4f
            if (r4 == 0) goto L4b
            java.lang.Object r4 = r3.next()     // Catch: java.lang.Throwable -> L4f
            java.lang.String r4 = (java.lang.String) r4     // Catch: java.lang.Throwable -> L4f
            java.lang.Object r5 = r2.get(r4)     // Catch: java.lang.Throwable -> L4f
            com.badlogic.gdx.assets.RefCountedContainer r5 = (com.badlogic.gdx.assets.RefCountedContainer) r5     // Catch: java.lang.Throwable -> L4f
            java.lang.Class<java.lang.Object> r6 = java.lang.Object.class
            java.lang.Object r5 = r5.getObject(r6)     // Catch: java.lang.Throwable -> L4f
            if (r5 == r8) goto L49
            boolean r6 = r8.equals(r5)     // Catch: java.lang.Throwable -> L4f
            if (r6 == 0) goto L48
            goto L49
        L48:
            goto L27
        L49:
            monitor-exit(r7)
            return r4
        L4b:
            goto Lb
        L4c:
            r0 = 0
            monitor-exit(r7)
            return r0
        L4f:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.getAssetFileName(java.lang.Object):java.lang.String");
    }

    public synchronized boolean isLoaded(AssetDescriptor assetDesc) {
        return isLoaded(assetDesc.fileName);
    }

    public synchronized boolean isLoaded(String fileName) {
        if (fileName == null) {
            return false;
        }
        return this.assetTypes.containsKey(fileName);
    }

    public synchronized boolean isLoaded(String fileName, Class type) {
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            return false;
        }
        RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            return false;
        }
        return assetContainer.getObject(type) != null;
    }

    public <T> AssetLoader getLoader(Class<T> type) {
        return getLoader(type, null);
    }

    public <T> AssetLoader getLoader(Class<T> type, String fileName) {
        ObjectMap<String, AssetLoader> loaders = this.loaders.get(type);
        if (loaders == null || loaders.size < 1) {
            return null;
        }
        if (fileName == null) {
            return loaders.get("");
        }
        AssetLoader result = null;
        int l = -1;
        ObjectMap.Entries<String, AssetLoader> it = loaders.entries().iterator();
        while (it.hasNext()) {
            ObjectMap.Entry entry = it.next();
            if (((String) entry.key).length() > l && fileName.endsWith((String) entry.key)) {
                AssetLoader result2 = entry.value;
                result = result2;
                l = ((String) entry.key).length();
            }
        }
        return result;
    }

    public synchronized <T> void load(String fileName, Class<T> type) {
        load(fileName, type, null);
    }

    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        AssetLoader loader = getLoader(type, fileName);
        if (loader == null) {
            throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(type));
        }
        if (this.loadQueue.size == 0) {
            this.loaded = 0;
            this.toLoad = 0;
            this.peakTasks = 0;
        }
        for (int i = 0; i < this.loadQueue.size; i++) {
            AssetDescriptor desc = this.loadQueue.get(i);
            if (desc.fileName.equals(fileName) && !desc.type.equals(type)) {
                throw new GdxRuntimeException("Asset with name '" + fileName + "' already in preload queue, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(desc.type) + NavigationBarInflaterView.KEY_CODE_END);
            }
        }
        for (int i2 = 0; i2 < this.tasks.size(); i2++) {
            AssetDescriptor desc2 = this.tasks.get(i2).assetDesc;
            if (desc2.fileName.equals(fileName) && !desc2.type.equals(type)) {
                throw new GdxRuntimeException("Asset with name '" + fileName + "' already in task list, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(desc2.type) + NavigationBarInflaterView.KEY_CODE_END);
            }
        }
        Class otherType = this.assetTypes.get(fileName);
        if (otherType != null && !otherType.equals(type)) {
            throw new GdxRuntimeException("Asset with name '" + fileName + "' already loaded, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(otherType) + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.toLoad++;
        AssetDescriptor assetDesc = new AssetDescriptor(fileName, type, parameter);
        this.loadQueue.add(assetDesc);
        this.log.debug("Queued: " + assetDesc);
    }

    public synchronized void load(AssetDescriptor desc) {
        load(desc.fileName, desc.type, desc.params);
    }

    public synchronized boolean update() {
        boolean z = false;
        try {
            if (this.tasks.size() == 0) {
                while (this.loadQueue.size != 0 && this.tasks.size() == 0) {
                    nextTask();
                }
                if (this.tasks.size() == 0) {
                    return true;
                }
            }
            if (updateTask() && this.loadQueue.size == 0) {
                if (this.tasks.size() == 0) {
                    z = true;
                }
            }
            return z;
        } catch (Throwable t) {
            handleTaskError(t);
            return this.loadQueue.size == 0;
        }
    }

    public boolean update(int millis) {
        boolean done;
        long endTime = TimeUtils.millis() + millis;
        while (true) {
            done = update();
            if (done || TimeUtils.millis() > endTime) {
                break;
            }
            ThreadUtils.yield();
        }
        return done;
    }

    public synchronized boolean isFinished() {
        boolean z;
        if (this.loadQueue.size == 0) {
            z = this.tasks.size() == 0;
        }
        return z;
    }

    public void finishLoading() {
        this.log.debug("Waiting for loading to complete...");
        while (!update()) {
            ThreadUtils.yield();
        }
        this.log.debug("Loading complete.");
    }

    public <T> T finishLoadingAsset(AssetDescriptor assetDesc) {
        return (T) finishLoadingAsset(assetDesc.fileName);
    }

    public <T> T finishLoadingAsset(String fileName) {
        ObjectMap<String, RefCountedContainer> assetsByType;
        RefCountedContainer assetContainer;
        T asset;
        Logger logger = this.log;
        logger.debug("Waiting for asset to be loaded: " + fileName);
        while (true) {
            synchronized (this) {
                Class<T> type = this.assetTypes.get(fileName);
                if (type != null && (assetsByType = this.assets.get(type)) != null && (assetContainer = assetsByType.get(fileName)) != null && (asset = (T) assetContainer.getObject(type)) != null) {
                    Logger logger2 = this.log;
                    logger2.debug("Asset loaded: " + fileName);
                    return asset;
                }
                update();
            }
            ThreadUtils.yield();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void injectDependencies(String parentAssetFilename, Array<AssetDescriptor> dependendAssetDescs) {
        ObjectSet<String> injected = this.injected;
        Iterator<AssetDescriptor> it = dependendAssetDescs.iterator();
        while (it.hasNext()) {
            AssetDescriptor desc = it.next();
            if (!injected.contains(desc.fileName)) {
                injected.add(desc.fileName);
                injectDependency(parentAssetFilename, desc);
            }
        }
        injected.clear(32);
    }

    private synchronized void injectDependency(String parentAssetFilename, AssetDescriptor dependendAssetDesc) {
        Array<String> dependencies = this.assetDependencies.get(parentAssetFilename);
        if (dependencies == null) {
            dependencies = new Array<>();
            this.assetDependencies.put(parentAssetFilename, dependencies);
        }
        dependencies.add(dependendAssetDesc.fileName);
        if (isLoaded(dependendAssetDesc.fileName)) {
            Logger logger = this.log;
            logger.debug("Dependency already loaded: " + dependendAssetDesc);
            Class type = this.assetTypes.get(dependendAssetDesc.fileName);
            RefCountedContainer assetRef = this.assets.get(type).get(dependendAssetDesc.fileName);
            assetRef.incRefCount();
            incrementRefCountedDependencies(dependendAssetDesc.fileName);
        } else {
            Logger logger2 = this.log;
            logger2.info("Loading dependency: " + dependendAssetDesc);
            addTask(dependendAssetDesc);
        }
    }

    private void nextTask() {
        AssetDescriptor assetDesc = this.loadQueue.removeIndex(0);
        if (isLoaded(assetDesc.fileName)) {
            this.log.debug("Already loaded: " + assetDesc);
            Class type = this.assetTypes.get(assetDesc.fileName);
            RefCountedContainer assetRef = this.assets.get(type).get(assetDesc.fileName);
            assetRef.incRefCount();
            incrementRefCountedDependencies(assetDesc.fileName);
            if (assetDesc.params != null && assetDesc.params.loadedCallback != null) {
                assetDesc.params.loadedCallback.finishedLoading(this, assetDesc.fileName, assetDesc.type);
            }
            this.loaded++;
            return;
        }
        this.log.info("Loading: " + assetDesc);
        addTask(assetDesc);
    }

    private void addTask(AssetDescriptor assetDesc) {
        AssetLoader loader = getLoader(assetDesc.type, assetDesc.fileName);
        if (loader == null) {
            throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(assetDesc.type));
        }
        this.tasks.push(new AssetLoadingTask(this, assetDesc, loader, this.executor));
        this.peakTasks++;
    }

    protected <T> void addAsset(String fileName, Class<T> type, T asset) {
        this.assetTypes.put(fileName, type);
        ObjectMap<String, RefCountedContainer> typeToAssets = this.assets.get(type);
        if (typeToAssets == null) {
            typeToAssets = new ObjectMap<>();
            this.assets.put(type, typeToAssets);
        }
        typeToAssets.put(fileName, new RefCountedContainer(asset));
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x009a A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean updateTask() {
        /*
            r9 = this;
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r0 = r9.tasks
            java.lang.Object r0 = r0.peek()
            com.badlogic.gdx.assets.AssetLoadingTask r0 = (com.badlogic.gdx.assets.AssetLoadingTask) r0
            r1 = 1
            r2 = 0
            r3 = 1
            boolean r4 = r0.cancel     // Catch: java.lang.RuntimeException -> L1b
            if (r4 != 0) goto L18
            boolean r4 = r0.update()     // Catch: java.lang.RuntimeException -> L1b
            if (r4 == 0) goto L16
            goto L18
        L16:
            r4 = r2
            goto L19
        L18:
            r4 = r3
        L19:
            r1 = r4
            goto L23
        L1b:
            r4 = move-exception
            r0.cancel = r3
            com.badlogic.gdx.assets.AssetDescriptor r5 = r0.assetDesc
            r9.taskFailed(r5, r4)
        L23:
            if (r1 == 0) goto L9a
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r4 = r9.tasks
            int r4 = r4.size()
            if (r4 != r3) goto L34
            int r4 = r9.loaded
            int r4 = r4 + r3
            r9.loaded = r4
            r9.peakTasks = r2
        L34:
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r2 = r9.tasks
            r2.pop()
            boolean r2 = r0.cancel
            if (r2 == 0) goto L3e
            return r3
        L3e:
            com.badlogic.gdx.assets.AssetDescriptor r2 = r0.assetDesc
            java.lang.String r2 = r2.fileName
            com.badlogic.gdx.assets.AssetDescriptor r4 = r0.assetDesc
            java.lang.Class<T> r4 = r4.type
            java.lang.Object r5 = r0.getAsset()
            r9.addAsset(r2, r4, r5)
            com.badlogic.gdx.assets.AssetDescriptor r2 = r0.assetDesc
            com.badlogic.gdx.assets.AssetLoaderParameters r2 = r2.params
            if (r2 == 0) goto L6c
            com.badlogic.gdx.assets.AssetDescriptor r2 = r0.assetDesc
            com.badlogic.gdx.assets.AssetLoaderParameters r2 = r2.params
            com.badlogic.gdx.assets.AssetLoaderParameters$LoadedCallback r2 = r2.loadedCallback
            if (r2 == 0) goto L6c
            com.badlogic.gdx.assets.AssetDescriptor r2 = r0.assetDesc
            com.badlogic.gdx.assets.AssetLoaderParameters r2 = r2.params
            com.badlogic.gdx.assets.AssetLoaderParameters$LoadedCallback r2 = r2.loadedCallback
            com.badlogic.gdx.assets.AssetDescriptor r4 = r0.assetDesc
            java.lang.String r4 = r4.fileName
            com.badlogic.gdx.assets.AssetDescriptor r5 = r0.assetDesc
            java.lang.Class<T> r5 = r5.type
            r2.finishedLoading(r9, r4, r5)
        L6c:
            long r4 = com.badlogic.gdx.utils.TimeUtils.nanoTime()
            com.badlogic.gdx.utils.Logger r2 = r9.log
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Loaded: "
            r6.append(r7)
            long r7 = r0.startTime
            long r7 = r4 - r7
            float r7 = (float) r7
            r8 = 1232348160(0x49742400, float:1000000.0)
            float r7 = r7 / r8
            r6.append(r7)
            java.lang.String r7 = "ms "
            r6.append(r7)
            com.badlogic.gdx.assets.AssetDescriptor r7 = r0.assetDesc
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r2.debug(r6)
            return r3
        L9a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.updateTask():boolean");
    }

    protected void taskFailed(AssetDescriptor assetDesc, RuntimeException ex) {
        throw ex;
    }

    private void incrementRefCountedDependencies(String parent) {
        Array<String> dependencies = this.assetDependencies.get(parent);
        if (dependencies == null) {
            return;
        }
        Iterator<String> it = dependencies.iterator();
        while (it.hasNext()) {
            String dependency = it.next();
            Class type = this.assetTypes.get(dependency);
            RefCountedContainer assetRef = this.assets.get(type).get(dependency);
            assetRef.incRefCount();
            incrementRefCountedDependencies(dependency);
        }
    }

    private void handleTaskError(Throwable t) {
        this.log.error("Error loading asset.", t);
        if (this.tasks.isEmpty()) {
            throw new GdxRuntimeException(t);
        }
        AssetLoadingTask task = this.tasks.pop();
        AssetDescriptor assetDesc = task.assetDesc;
        if (task.dependenciesLoaded && task.dependencies != null) {
            Iterator<AssetDescriptor> it = task.dependencies.iterator();
            while (it.hasNext()) {
                AssetDescriptor desc = it.next();
                unload(desc.fileName);
            }
        }
        this.tasks.clear();
        AssetErrorListener assetErrorListener = this.listener;
        if (assetErrorListener != null) {
            assetErrorListener.error(assetDesc, t);
            return;
        }
        throw new GdxRuntimeException(t);
    }

    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
        setLoader(type, null, loader);
    }

    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, String suffix, AssetLoader<T, P> loader) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        if (loader == null) {
            throw new IllegalArgumentException("loader cannot be null.");
        }
        Logger logger = this.log;
        logger.debug("Loader set: " + ClassReflection.getSimpleName(type) + " -> " + ClassReflection.getSimpleName(loader.getClass()));
        ObjectMap<String, AssetLoader> loaders = this.loaders.get(type);
        if (loaders == null) {
            ObjectMap<Class, ObjectMap<String, AssetLoader>> objectMap = this.loaders;
            ObjectMap<String, AssetLoader> objectMap2 = new ObjectMap<>();
            loaders = objectMap2;
            objectMap.put(type, objectMap2);
        }
        loaders.put(suffix == null ? "" : suffix, loader);
    }

    public synchronized int getLoadedAssets() {
        return this.assetTypes.size;
    }

    public synchronized int getQueuedAssets() {
        return this.loadQueue.size + this.tasks.size();
    }

    public synchronized float getProgress() {
        if (this.toLoad == 0) {
            return 1.0f;
        }
        float fractionalLoaded = this.loaded;
        if (this.peakTasks > 0) {
            fractionalLoaded += (this.peakTasks - this.tasks.size()) / this.peakTasks;
        }
        return Math.min(1.0f, fractionalLoaded / this.toLoad);
    }

    public synchronized void setErrorListener(AssetErrorListener listener) {
        this.listener = listener;
    }

    @Override // com.badlogic.gdx.utils.Disposable
    public synchronized void dispose() {
        this.log.debug("Disposing.");
        clear();
        this.executor.dispose();
    }

    public synchronized void clear() {
        this.loadQueue.clear();
        while (!update()) {
        }
        ObjectIntMap<String> dependencyCount = new ObjectIntMap<>();
        while (this.assetTypes.size > 0) {
            dependencyCount.clear();
            Array<String> assets = this.assetTypes.keys().toArray();
            Iterator<String> it = assets.iterator();
            while (it.hasNext()) {
                dependencyCount.put(it.next(), 0);
            }
            Iterator<String> it2 = assets.iterator();
            while (it2.hasNext()) {
                Array<String> dependencies = this.assetDependencies.get(it2.next());
                if (dependencies != null) {
                    Iterator<String> it3 = dependencies.iterator();
                    while (it3.hasNext()) {
                        String dependency = it3.next();
                        int count = dependencyCount.get(dependency, 0);
                        dependencyCount.put(dependency, count + 1);
                    }
                }
            }
            Iterator<String> it4 = assets.iterator();
            while (it4.hasNext()) {
                String asset = it4.next();
                if (dependencyCount.get(asset, 0) == 0) {
                    unload(asset);
                }
            }
        }
        this.assets.clear();
        this.assetTypes.clear();
        this.assetDependencies.clear();
        this.loaded = 0;
        this.toLoad = 0;
        this.peakTasks = 0;
        this.loadQueue.clear();
        this.tasks.clear();
    }

    public Logger getLogger() {
        return this.log;
    }

    public void setLogger(Logger logger) {
        this.log = logger;
    }

    public synchronized int getReferenceCount(String fileName) {
        Class type;
        type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return this.assets.get(type).get(fileName).getRefCount();
    }

    public synchronized void setReferenceCount(String fileName, int refCount) {
        Class type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        this.assets.get(type).get(fileName).setRefCount(refCount);
    }

    public synchronized String getDiagnostics() {
        StringBuilder sb;
        sb = new StringBuilder(256);
        ObjectMap.Keys<String> it = this.assetTypes.keys().iterator();
        while (it.hasNext()) {
            String fileName = it.next();
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(fileName);
            sb.append(", ");
            Class type = this.assetTypes.get(fileName);
            RefCountedContainer assetRef = this.assets.get(type).get(fileName);
            Array<String> dependencies = this.assetDependencies.get(fileName);
            sb.append(ClassReflection.getSimpleName(type));
            sb.append(", refs: ");
            sb.append(assetRef.getRefCount());
            if (dependencies != null) {
                sb.append(", deps: [");
                Iterator<String> it2 = dependencies.iterator();
                while (it2.hasNext()) {
                    String dep = it2.next();
                    sb.append(dep);
                    sb.append(",");
                }
                sb.append(NavigationBarInflaterView.SIZE_MOD_END);
            }
        }
        return sb.toString();
    }

    public synchronized Array<String> getAssetNames() {
        return this.assetTypes.keys().toArray();
    }

    public synchronized Array<String> getDependencies(String fileName) {
        return this.assetDependencies.get(fileName);
    }

    public synchronized Class getAssetType(String fileName) {
        return this.assetTypes.get(fileName);
    }
}
