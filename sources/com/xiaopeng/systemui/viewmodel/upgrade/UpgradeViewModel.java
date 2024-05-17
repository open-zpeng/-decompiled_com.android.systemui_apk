package com.xiaopeng.systemui.viewmodel.upgrade;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.controller.UpgradeController;
/* loaded from: classes24.dex */
public class UpgradeViewModel implements IUpgradeViewModel, UpgradeController.OnUpgradeStatusChangeListener {
    private Context mContext;
    private UpgradeController mUpgradeController;
    private final MutableLiveData<Boolean> mUpgradeState = new MutableLiveData<>();

    public UpgradeViewModel(Context context) {
        this.mContext = context;
        this.mUpgradeController = new UpgradeController(context);
        this.mUpgradeController.setOnUpgradeStatusChangeListener(this);
        initLiveData();
    }

    private void initLiveData() {
        this.mUpgradeState.setValue(false);
    }

    public LiveData<Boolean> getUpgradeStateData() {
        return this.mUpgradeState;
    }

    public boolean getUpgradeState() {
        return this.mUpgradeState.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.controller.UpgradeController.OnUpgradeStatusChangeListener
    public void OnUpgradeStatusChange(boolean ready) {
        this.mUpgradeState.postValue(Boolean.valueOf(ready));
    }

    public void destroy() {
        this.mUpgradeController.destroy();
    }
}
