package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.Config;
/* loaded from: classes24.dex */
public class BrightnessTile extends XpTile {
    public static final String TAG = "BrightnessTile";
    private Context mContext;
    private int mPreValue;
    private ContentObserver mScreenBrightnessObserver;

    public BrightnessTile(String tileSpec) {
        super(tileSpec);
        this.mContext = ContextUtils.getContext();
        this.mScreenBrightnessObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.quickmenu.tiles.BrightnessTile.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (Settings.System.getUriFor("screen_brightness").equals(uri)) {
                    BrightnessTile brightnessTile = BrightnessTile.this;
                    brightnessTile.refreshState(brightnessTile.getCurrentState());
                }
            }
        };
        this.mPreValue = getCurrentState();
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        Log.d("BrightnessTile", "xptile brightness ui:" + value);
        setScreenBrightness(this.mContext, value, true);
    }

    public void setScreenBrightness(Context context, int newBrightness, boolean isFinal) {
        if (newBrightness < 1) {
            newBrightness = 1;
        }
        if (newBrightness > Config.BRIGHTNESS_TO_MAX_VALUE) {
            newBrightness = Config.BRIGHTNESS_TO_MAX_VALUE;
        }
        Log.d("BrightnessTile", "xpdisplay setScreenBrightness:" + newBrightness + " isFinal:" + isFinal);
        Settings.System.putInt(context.getContentResolver(), "screen_brightness", newBrightness);
        int autoBrightnessDevice = CarModelsManager.getConfig().autoBrightness();
        if (autoBrightnessDevice == CarModelsManager.getFeature().hasCIU() || autoBrightnessDevice == CarModelsManager.getFeature().hasXPU()) {
            if (isAdptiveBrightness(context)) {
                setAdaptiveBrightness(context, false);
                Settings.System.putLong(context.getContentResolver(), "screen_brightness_mode_sync", System.currentTimeMillis());
                return;
            }
            return;
        }
        Log.i("BrightnessTile", "setScreenBrightness no icu or no xpu ");
    }

    public boolean isAdptiveBrightness(Context context) {
        int brightnessMode = Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode_0", 1);
        Log.d("BrightnessTile", "isAdptiveBrightness brightnessMode:" + brightnessMode);
        return brightnessMode == 1;
    }

    public boolean setAdaptiveBrightness(Context context, boolean isOpen) {
        Log.d("BrightnessTile", "setAdaptiveBrightness isOpen:" + isOpen);
        return Settings.System.putInt(context.getContentResolver(), "screen_brightness_mode_0", isOpen ? 1 : 0);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
        unRegisterScreenBrightness();
    }

    private void unRegisterScreenBrightness() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mScreenBrightnessObserver);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
        registerScreenBrightness();
    }

    private void registerScreenBrightness() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness"), true, this.mScreenBrightnessObserver);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        int brightness = getScreenBrightness(this.mContext);
        return brightness;
    }

    public int getScreenBrightness(Context applicationContext) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = applicationContext.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver, "screen_brightness");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("BrightnessTile", "getScreenBrightness:" + nowBrightnessValue);
        return nowBrightnessValue;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void refreshState(int state) {
        if (this.mPreValue == state) {
            return;
        }
        this.mPreValue = state;
        super.refreshState(state);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return state;
    }
}
