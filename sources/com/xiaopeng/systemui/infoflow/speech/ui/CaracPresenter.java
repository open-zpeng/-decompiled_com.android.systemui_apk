package com.xiaopeng.systemui.infoflow.speech.ui;

import android.content.Context;
import com.xiaopeng.libcarcontrol.CarControlCallback;
import com.xiaopeng.libcarcontrol.CarControlManager;
/* loaded from: classes24.dex */
public class CaracPresenter implements ICaracPresenter {
    private CarControlManager mCarControlManager;
    public ICaracView mCaracView;
    private Context mContext;

    public CaracPresenter(ICaracView caracView) {
        this.mCaracView = caracView;
        this.mContext = caracView.getContext();
        this.mCarControlManager = CarControlManager.getInstance(this.mContext);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.ICaracPresenter
    public void registerCaracListener() {
        this.mCarControlManager.registerCallback(new CarControlCallback() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.CaracPresenter.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacDriverTempChanged(float temp) {
                int currentTemp = (int) temp;
                CaracPresenter.this.mCaracView.onTempSet(currentTemp);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.ICaracPresenter
    public void getCurrentValue() {
        int currentTemp = (int) this.mCarControlManager.getHvacDriverTemp();
        this.mCaracView.onTempSet(currentTemp);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.ICaracPresenter
    public void setCaracTemp(float value) {
        this.mCarControlManager.setHvacDriverTemp(value);
    }
}
