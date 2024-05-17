package com.xiaopeng.systemui.viewmodel.storage;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.controller.StorageController;
/* loaded from: classes24.dex */
public class StorageViewModel implements IStorageViewModel, StorageController.OnStorageCallback {
    private Context mContext;
    private final MutableLiveData<Boolean> mShowStorage = new MutableLiveData<>();
    private StorageController mStorageController;

    public StorageViewModel(Context context) {
        this.mContext = context;
        this.mStorageController = new StorageController(this.mContext);
        this.mStorageController.setCallback(this);
        initLiveData();
    }

    private void initLiveData() {
        this.mShowStorage.setValue(false);
    }

    @Override // com.xiaopeng.systemui.controller.StorageController.OnStorageCallback
    public void onStorageChanged(boolean show) {
        if (show != this.mShowStorage.getValue().booleanValue()) {
            this.mShowStorage.postValue(Boolean.valueOf(show));
        }
    }

    @Override // com.xiaopeng.systemui.viewmodel.storage.IStorageViewModel
    public boolean showStorage() {
        return this.mShowStorage.getValue().booleanValue();
    }

    public MutableLiveData<Boolean> getShowStorageData() {
        return this.mShowStorage;
    }
}
