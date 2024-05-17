package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes24.dex */
public class QuickMenu3DView implements IQuickMenuViewHolder {
    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateViewState(String key, int value) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put(VuiConstants.ELEMENT_VALUE, Integer.valueOf(value));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("updateViewState", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public View initView(Context context, ViewGroup view) {
        return null;
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateControlBtn(int state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Integer.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("updateControlBtn", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateMusicProgress(long position, long duration) {
        Map<String, Object> map = new HashMap<>();
        map.put(VuiConstants.ELEMENT_POSITION, Long.valueOf(position));
        map.put("duration", Long.valueOf(duration));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("updateMusicProgress", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateMusicInfo(String songTitle, String artist, String album, int stateMusic) {
        Map<String, Object> map = new HashMap<>();
        map.put("songTitle", songTitle);
        map.put("artist", artist);
        map.put("album", album);
        map.put("stateMusic", Integer.valueOf(stateMusic));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("updateMusicInfo", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void enableMediaBtn(boolean state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Boolean.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("enableMediaBtn", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void initSlider(int maxWind, int maxVolume, int minTemperature, int maxTemperature) {
        Map<String, Object> map = new HashMap<>();
        map.put("maxWind", Integer.valueOf(maxWind));
        map.put("maxVolume", Integer.valueOf(maxVolume));
        map.put("minTemperature", Integer.valueOf(minTemperature));
        map.put("maxTemperature", Integer.valueOf(maxTemperature));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("initSlider", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void themeChanged(boolean state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Boolean.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("themeChanged", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void updateSoundType(int state) {
        Map<String, Object> map = new HashMap<>();
        map.put("state", Integer.valueOf(state));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("updateSoundType", map);
    }

    @Override // com.xiaopeng.systemui.quickmenu.IQuickMenuViewHolder
    public void openNapaAppWindow(String tileKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("tileKey", tileKey);
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("openNapaAppWindow", map);
    }
}
