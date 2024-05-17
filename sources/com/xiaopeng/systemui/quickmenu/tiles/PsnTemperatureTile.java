package com.xiaopeng.systemui.quickmenu.tiles;

import androidx.lifecycle.Observer;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.IHvacViewModel;
/* loaded from: classes24.dex */
public class PsnTemperatureTile extends ViewModelTile {
    private HvacViewModel mHvacViewModel;

    public PsnTemperatureTile(String tileSpec) {
        super(tileSpec);
        this.mHvacViewModel = (HvacViewModel) ViewModelManager.getInstance().getViewModel(IHvacViewModel.class, this.mContext);
        this.mHvacViewModel.getHvacTempPassengerData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.quickmenu.tiles.-$$Lambda$PsnTemperatureTile$bAv307pUmYrwsDyqcD9LDt8fzUU
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                PsnTemperatureTile.this.lambda$new$0$PsnTemperatureTile((Float) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$PsnTemperatureTile(Float temperature) {
        updatePsnTemperature(temperature.floatValue());
    }

    private void updatePsnTemperature(float temperature) {
        Logger.d("aaa", "updatePsnTemperature : " + temperature);
        refreshState((int) (10.0f * temperature));
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return (int) (this.mHvacViewModel.getHvacPassengerTemperature() * 10.0f);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ViewModelTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        this.mHvacViewModel.setHvacPassengerTemperature(value / 10.0f);
    }
}
