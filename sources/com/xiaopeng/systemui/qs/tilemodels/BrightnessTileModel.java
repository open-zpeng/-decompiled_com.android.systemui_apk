package com.xiaopeng.systemui.qs.tilemodels;

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
public class BrightnessTileModel extends XpTileModel {
    public static final String TAG = "BrightnessTile";
    private Context mContext;
    private ContentObserver mScreenBrightnessObserver;

    public BrightnessTileModel(String tileSpec) {
        super(tileSpec);
        this.mContext = ContextUtils.getContext();
        this.mScreenBrightnessObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.qs.tilemodels.BrightnessTileModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (Settings.System.getUriFor("screen_brightness").equals(uri)) {
                    BrightnessTileModel brightnessTileModel = BrightnessTileModel.this;
                    brightnessTileModel.mCurrentState = brightnessTileModel.getCurrentState();
                    BrightnessTileModel.this.mCurrentLivedata.setValue(Integer.valueOf(BrightnessTileModel.this.mCurrentState));
                }
            }
        };
        this.mCurrentState = getCurrentState();
        this.mCurrentLivedata.setValue(Integer.valueOf(this.mCurrentState));
        registerScreenBrightness();
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        Log.d("BrightnessTile", "xptile brightness ui:" + value);
        if (value == 0) {
            value = 1;
        }
        setScreenBrightness(this.mContext, (int) ((value * 255.0f) / 100.0f), true);
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

    private void registerScreenBrightness() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness"), true, this.mScreenBrightnessObserver);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        int brightness = getScreenBrightness(this.mContext);
        return (int) ((brightness * 100.0f) / 255.0f);
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
}
