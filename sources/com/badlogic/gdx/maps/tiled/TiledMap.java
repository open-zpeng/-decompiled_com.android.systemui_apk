package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class TiledMap extends Map {
    private Array<? extends Disposable> ownedResources;
    private TiledMapTileSets tilesets = new TiledMapTileSets();

    public TiledMapTileSets getTileSets() {
        return this.tilesets;
    }

    public void setOwnedResources(Array<? extends Disposable> resources) {
        this.ownedResources = resources;
    }

    @Override // com.badlogic.gdx.maps.Map, com.badlogic.gdx.utils.Disposable
    public void dispose() {
        Array<? extends Disposable> array = this.ownedResources;
        if (array != null) {
            Iterator<? extends Disposable> it = array.iterator();
            while (it.hasNext()) {
                Disposable resource = it.next();
                resource.dispose();
            }
        }
    }
}
