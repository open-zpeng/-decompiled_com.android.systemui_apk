package com.xiaopeng.systemui.quickmenu.tiles;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.volume.AudioViewModel;
/* loaded from: classes24.dex */
public class SoundTile extends ViewModelTile {
    public static final String TAG = "SoundTile";
    protected MutableLiveData<Integer> mPropData;
    protected int mSound;
    protected AudioViewModel mViewModel;

    public SoundTile(String tileSpec) {
        super(tileSpec);
        this.mViewModel = (AudioViewModel) ViewModelManager.getInstance().getViewModel(AudioViewModel.class, this.mContext);
        this.mPropData = this.mViewModel.getMusicVolumeData();
        this.mSound = this.mViewModel.getMusicVolume();
        this.mPropData.observe(this, new Observer() { // from class: com.xiaopeng.systemui.quickmenu.tiles.-$$Lambda$SoundTile$6ve-1wfm2s9M9bLqbV848JyBzhA
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                SoundTile.this.onSoundVolumeChange(((Integer) obj).intValue());
            }
        });
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        Log.d(TAG, "OnClicked value: " + value);
        this.mViewModel.setMusicVolume(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSoundVolumeChange(int value) {
        if (this.mSound != value) {
            this.mSound = value;
            refreshState(value);
        }
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return this.mSound;
    }
}
