package com.xiaopeng.systemui;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.quickmenu.tiles.XpTile;
/* loaded from: classes24.dex */
public class TileViewModel implements XpTile.Callback {
    private static final String TAG = "TileViewModel";
    private final MutableLiveData<Pair<String, Integer>> mTileLiveData = new MutableLiveData<>();
    private final TileManager mTileManager;

    public TileViewModel(Context context) {
        this.mTileManager = new TileManager(context);
    }

    public int getCurrentState(String key) {
        return this.mTileManager.getCurrentState(key);
    }

    public MutableLiveData<Pair<String, Integer>> getTileLiveData() {
        return this.mTileLiveData;
    }

    public void onStartVm() {
        this.mTileManager.createAndRegisterAllTileCallback(this);
    }

    public void onStopVm() {
        this.mTileManager.destroyAndUnregisterAllTileCallback(this);
    }

    public void registerSoundCallback() {
        this.mTileManager.registerTileCallback("volume_adjustment", this);
    }

    public void registerBrightnessCallback() {
        this.mTileManager.registerTileCallback("brightness_adjustment", this);
    }

    public void registerWindCallback() {
        this.mTileManager.registerTileCallback("wind_adjustment", this);
    }

    public void unregisterSoundCallback() {
        this.mTileManager.unregisterTileCallback("volume_adjustment", this);
    }

    public void unregisterBrightnessCallback() {
        this.mTileManager.unregisterTileCallback("brightness_adjustment", this);
    }

    public void unregisterWindCallback() {
        this.mTileManager.unregisterTileCallback("wind_adjustment", this);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile.Callback
    public void onStateChanged(String key, int state) {
        Log.d(TAG, "xptile callback onStateChanged key:" + key + " state:" + state);
        this.mTileLiveData.setValue(new Pair<>(key, Integer.valueOf(state)));
    }

    public void onClickTileView(String key, int value) {
        this.mTileManager.onClickTile(key, value);
    }

    public int getWindMaxValue() {
        return this.mTileManager.getWindMax();
    }

    public int getTemperatureMinValue() {
        return this.mTileManager.getTemperatureMin();
    }

    public int getTemperatureMaxValue() {
        return this.mTileManager.getTemperatureMax();
    }

    public int getSoundMaxValue() {
        return this.mTileManager.getSoundMaxValue();
    }

    public void setValue(String key, int value) {
        this.mTileManager.onClickTile(key, value);
    }

    public void unregisterPsnSoundCallback() {
        this.mTileManager.unregisterTileCallback("passenger_volume_adjustment", this);
    }

    public void unregisterPsnBrightnessCallback() {
        this.mTileManager.unregisterTileCallback("screen_brightness_1", this);
    }

    public void registerPsnSoundCallback() {
        this.mTileManager.registerTileCallback("passenger_volume_adjustment", this);
    }

    public void registerPsnBrightnessCallback() {
        this.mTileManager.registerTileCallback("screen_brightness_1", this);
    }

    public void setScreenIdByKey(String key, int id) {
        this.mTileManager.setScreenIdByKey(key, id);
    }
}
