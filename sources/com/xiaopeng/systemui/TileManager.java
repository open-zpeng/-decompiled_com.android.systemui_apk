package com.xiaopeng.systemui;

import android.content.Context;
import android.media.AudioManager;
import android.util.ArrayMap;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.systemui.quickmenu.tiles.TemperatureTile;
import com.xiaopeng.systemui.quickmenu.tiles.WindTile;
import com.xiaopeng.systemui.quickmenu.tiles.XpTile;
/* loaded from: classes24.dex */
public class TileManager {
    protected AudioManager mAudioManager;
    public String TAG = "TileManager";
    protected ArrayMap<String, XpTile> mTileMap = new ArrayMap<>();
    protected TileFactory mTileFactory = new TileFactory();

    public TileManager(Context context) {
        this.mAudioManager = (AudioManager) context.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
    }

    public void onClickTile(String key, int value) {
        getTile(key).click(value);
    }

    private void addMapTile(String key) {
        if (!this.mTileMap.containsKey(key)) {
            this.mTileMap.put(key, this.mTileFactory.createTile(key));
        }
    }

    public void registerTileCallback(String key, XpTile.Callback callback) {
        getTile(key).addCallback(callback);
    }

    public void createAndRegisterAllTileCallback(XpTile.Callback callback) {
        for (XpTile xpTile : this.mTileMap.values()) {
            if (!isFilterTile(xpTile.getTileKey())) {
                xpTile.addCallback(callback);
                xpTile.create();
            }
        }
    }

    public void destroyAndUnregisterAllTileCallback(XpTile.Callback callback) {
        for (XpTile xpTile : this.mTileMap.values()) {
            if (!isFilterTile(xpTile.getTileKey())) {
                xpTile.removeCallback(callback);
                xpTile.destroy();
            }
        }
    }

    public void unregisterTileCallback(String key, XpTile.Callback callback) {
        getTile(key).removeCallback(callback);
    }

    private boolean isFilterTile(String key) {
        return "clean_mode".equals(key) || "meditation_mode_switch".equals(key) || "rear_mirror_angle_switch".equals(key);
    }

    public int getCurrentState(String key) {
        addMapTile(key);
        return getTile(key).getCurrentState();
    }

    public int getSoundMaxValue() {
        return this.mAudioManager.getStreamMaxVolume(3);
    }

    public int getTemperatureMin() {
        return TemperatureTile.getMinTemperature();
    }

    public int getTemperatureMax() {
        return TemperatureTile.getMaxTemperature();
    }

    public int getWindMin() {
        return WindTile.getMinWindSpeed();
    }

    public int getWindMax() {
        return WindTile.getMaxWindSpeed();
    }

    private XpTile getTile(String key) {
        addMapTile(key);
        return this.mTileMap.get(key);
    }

    public void setScreenIdByKey(String key, int id) {
        getTile(key).setScreenId(id);
    }
}
