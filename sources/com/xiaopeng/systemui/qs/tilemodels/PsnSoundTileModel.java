package com.xiaopeng.systemui.qs.tilemodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.volume.AudioViewModel;
/* loaded from: classes24.dex */
public class PsnSoundTileModel extends XpTileModel implements LifecycleOwner {
    public static final String TAG = "PsnSoundTile";
    private final AudioViewModel mAudioViewModel;
    private final LifecycleRegistry mLifecycleRegistry;

    public PsnSoundTileModel(String tileSpec) {
        super(tileSpec);
        this.mLifecycleRegistry = new LifecycleRegistry(this);
        this.mAudioViewModel = (AudioViewModel) ViewModelManager.getInstance().getViewModel(AudioViewModel.class, this.mContext);
        this.mAudioViewModel.getPsnVolumeData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.tilemodels.-$$Lambda$PsnSoundTileModel$dw3YKH28_TbespLGxu5vj7dVRcM
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                PsnSoundTileModel.this.updatePsnVolume(((Integer) obj).intValue());
            }
        });
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePsnVolume(int volume) {
        this.mCurrentLivedata.setValue(Integer.valueOf((int) ((volume * 100.0f) / 30.0f)));
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        this.mAudioViewModel.setPsnVolume((int) ((value * 30.0f) / 100.0f));
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        return this.mAudioViewModel.getPsnVolume();
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
}
