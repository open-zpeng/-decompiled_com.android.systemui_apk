package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class VehicleSoundWaveTile extends ContentProviderTile {
    private static final String KEY_SUPPORT_SOUND_WAVE = "key_napa_soundwave";
    public String TAG;
    public Context mContext;
    private final ContentObserver mIfShowSoundWaveObserver;
    private boolean mIsSupport;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface BaseState {
        public static final int INIT = -1;
        public static final int OFF = 0;
        public static final int ON = 1;
    }

    public VehicleSoundWaveTile(String tileSpec) {
        super(tileSpec);
        this.TAG = VehicleSoundWaveTile.class.getSimpleName();
        this.mIsSupport = false;
        this.mContext = ContextUtils.getContext();
        this.mIfShowSoundWaveObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.quickmenu.tiles.VehicleSoundWaveTile.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri.equals(Settings.Global.getUriFor(VehicleSoundWaveTile.KEY_SUPPORT_SOUND_WAVE))) {
                    try {
                        VehicleSoundWaveTile vehicleSoundWaveTile = VehicleSoundWaveTile.this;
                        boolean z = true;
                        if (Settings.Global.getInt(VehicleSoundWaveTile.this.mContext.getContentResolver(), VehicleSoundWaveTile.KEY_SUPPORT_SOUND_WAVE) != 1) {
                            z = false;
                        }
                        vehicleSoundWaveTile.mIsSupport = z;
                    } catch (Exception e) {
                        Log.d(VehicleSoundWaveTile.this.TAG, e.toString());
                    }
                    String str = VehicleSoundWaveTile.this.TAG;
                    Log.d(str, "isSupport: " + VehicleSoundWaveTile.this.mIsSupport);
                    if (!VehicleSoundWaveTile.this.mIsSupport) {
                        VehicleSoundWaveTile.this.refreshState(-1);
                        return;
                    }
                    VehicleSoundWaveTile vehicleSoundWaveTile2 = VehicleSoundWaveTile.this;
                    vehicleSoundWaveTile2.refreshState(vehicleSoundWaveTile2.getCurrentState());
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(KEY_SUPPORT_SOUND_WAVE), true, this.mIfShowSoundWaveObserver);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int state = getCurrentState();
        int nextState = -1;
        if (state == -1 || state == 0) {
            nextState = 1;
        } else if (state == 1) {
            nextState = 0;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState == 1 ? 2 : 1, this.mScreenId);
        saveContentProvider(state, nextState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile
    public void saveContentProvider(int state, int nextState) {
        String value = Integer.toString(nextState);
        String str = this.TAG;
        Log.d(str, "xptile saveContentProvider  key:" + this.mTileKey + " value:" + value + " mCurrentStateValue:" + this.mCurrentStateValue);
        Settings.System.putString(this.mContext.getContentResolver(), this.mTileKey, value);
        this.mCurrentStateValue = value;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        this.mCurrentStateValue = Settings.System.getString(this.mContext.getContentResolver(), this.mTileKey);
        Log.d(this.TAG, "xptile contentprovider getvalue  key:" + this.mTileKey + " value:" + this.mCurrentStateValue);
        try {
            boolean z = true;
            if (Settings.Global.getInt(this.mContext.getContentResolver(), KEY_SUPPORT_SOUND_WAVE) != 1) {
                z = false;
            }
            this.mIsSupport = z;
        } catch (Exception e) {
            Log.d(this.TAG, e.toString());
        }
        if (TextUtils.isEmpty(this.mCurrentStateValue) || !this.mIsSupport) {
            return this.mIsSupport ? 0 : -1;
        }
        String[] value = this.mCurrentStateValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
        return Integer.parseInt(value[0]);
    }
}
