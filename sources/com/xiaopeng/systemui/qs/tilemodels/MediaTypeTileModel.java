package com.xiaopeng.systemui.qs.tilemodels;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.bluetooth.BluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel;
/* loaded from: classes24.dex */
public class MediaTypeTileModel extends XpTileModel implements LifecycleOwner {
    public static final String AVAS_LOUD_SPEAKER_SW = "avas_speaker";
    public static final int MEDIA_TYPE_BT = 2;
    public static final int MEDIA_TYPE_INSIDE = 0;
    public static final int MEDIA_TYPE_OUTSIDE = 1;
    private final BluetoothViewModel mBluetoothViewModel;
    ContentObserver mCallbackObserver;
    protected final LifecycleRegistry mLifecycleRegistry;

    public MediaTypeTileModel(String tileSpec) {
        super(tileSpec);
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.qs.tilemodels.MediaTypeTileModel.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.d("MediaTypeTile", "uri : " + uri);
                if (uri.equals(Settings.System.getUriFor("avas_speaker"))) {
                    MediaTypeTileModel.this.updateVolumeType(-1);
                }
            }
        };
        this.mBluetoothViewModel = (BluetoothViewModel) ViewModelManager.getInstance().getViewModel(IBluetoothViewModel.class, this.mContext);
        this.mBluetoothViewModel.getPsnBluetoothStateData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.tilemodels.-$$Lambda$MediaTypeTileModel$plQNy1hcZ5Px3XUVyh9JUeEU2ME
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                MediaTypeTileModel.this.updateVolumeType(((Integer) obj).intValue());
            }
        });
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("avas_speaker"), true, this.mCallbackObserver);
        updateVolumeType(-1);
        this.mCurrentLivedata.setValue(Integer.valueOf(this.mCurrentState));
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    protected int getCurrentState() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVolumeType(int value) {
        int btType = this.mBluetoothViewModel.getPsnBluetoothState();
        int outsideMediaType = Settings.System.getInt(this.mContext.getContentResolver(), "avas_speaker", 0);
        if (btType == 2) {
            this.mCurrentState = 2;
        } else if (outsideMediaType == 1) {
            this.mCurrentState = 1;
        } else {
            this.mCurrentState = 0;
        }
        this.mCurrentLivedata.setValue(Integer.valueOf(this.mCurrentState));
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
}
