package com.xiaopeng.systemui.quickmenu.tiles;

import androidx.lifecycle.Observer;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.volume.AudioViewModel;
/* loaded from: classes24.dex */
public class PsnSoundTile extends ViewModelTile {
    public static final String TAG = "PsnSoundTile";
    private AudioViewModel mAudioViewModel;

    public PsnSoundTile(String tileSpec) {
        super(tileSpec);
        this.mAudioViewModel = (AudioViewModel) ViewModelManager.getInstance().getViewModel(AudioViewModel.class, this.mContext);
        this.mAudioViewModel.getPsnVolumeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.quickmenu.tiles.-$$Lambda$PsnSoundTile$HXfk0wXZiZnjvYZj4oBk_eTNeRo
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                PsnSoundTile.this.lambda$new$0$PsnSoundTile((Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$PsnSoundTile(Integer volume) {
        updatePsnVolume(volume.intValue());
    }

    private void updatePsnVolume(int volume) {
        refreshState(volume);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        this.mAudioViewModel.setPsnVolume(value);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return this.mAudioViewModel.getPsnVolume();
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return state;
    }
}
