package com.xiaopeng.systemui.viewmodel;

import android.content.Context;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.viewmodel.bluetooth.BluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel;
import com.xiaopeng.systemui.viewmodel.car.BcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.CarViewModel;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.IBcmViewModel;
import com.xiaopeng.systemui.viewmodel.car.ICarViewModel;
import com.xiaopeng.systemui.viewmodel.car.IHvacViewModel;
import com.xiaopeng.systemui.viewmodel.car.ITboxViewModel;
import com.xiaopeng.systemui.viewmodel.car.IVcuViewModel;
import com.xiaopeng.systemui.viewmodel.car.TboxViewModel;
import com.xiaopeng.systemui.viewmodel.car.VcuViewModel;
import com.xiaopeng.systemui.viewmodel.carmode.CarModeViewModel;
import com.xiaopeng.systemui.viewmodel.iot.IoTViewModel;
import com.xiaopeng.systemui.viewmodel.signal.ISignalViewModel;
import com.xiaopeng.systemui.viewmodel.signal.SignalViewModel;
import com.xiaopeng.systemui.viewmodel.storage.IStorageViewModel;
import com.xiaopeng.systemui.viewmodel.storage.StorageViewModel;
import com.xiaopeng.systemui.viewmodel.upgrade.IUpgradeViewModel;
import com.xiaopeng.systemui.viewmodel.upgrade.UpgradeViewModel;
import com.xiaopeng.systemui.viewmodel.usb.IUsbViewModel;
import com.xiaopeng.systemui.viewmodel.usb.UsbViewModel;
import com.xiaopeng.systemui.viewmodel.volume.AudioViewModel;
import java.util.Hashtable;
/* loaded from: classes24.dex */
public final class ViewModelManager {
    private static ViewModelManager sInstance = null;
    private final Hashtable<Class<?>, IViewModel> mViewModelCache = new Hashtable<>();

    public static ViewModelManager getInstance() {
        if (sInstance == null) {
            synchronized (ViewModelManager.class) {
                if (sInstance == null) {
                    sInstance = new ViewModelManager();
                }
            }
        }
        return sInstance;
    }

    private ViewModelManager() {
    }

    public static <T> void setValue(MutableLiveData<T> data, T value) {
        if (data != null) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                data.setValue(value);
            } else {
                data.postValue(value);
            }
        }
    }

    public static <T> MutableLiveData<T> create(T dv) {
        MutableLiveData<T> data = new MutableLiveData<>();
        data.setValue(dv);
        return data;
    }

    public <T extends IViewModel> T getViewModel(Class<?> clazz, Context context) throws IllegalArgumentException {
        synchronized (ViewModelManager.class) {
            T t = (T) this.mViewModelCache.get(clazz);
            if (clazz.isInstance(t)) {
                return t;
            }
            T t2 = (T) createViewModel(clazz, context);
            this.mViewModelCache.put(clazz, t2);
            return t2;
        }
    }

    private <T extends IViewModel> T createViewModel(Class<?> clazz, Context context) {
        if (clazz == ICarViewModel.class) {
            return new CarViewModel(context);
        }
        if (clazz == IHvacViewModel.class) {
            return new HvacViewModel(context);
        }
        if (clazz == ITboxViewModel.class) {
            return new TboxViewModel(context);
        }
        if (clazz == IBcmViewModel.class) {
            return new BcmViewModel(context);
        }
        if (clazz == IVcuViewModel.class) {
            return new VcuViewModel(context);
        }
        if (clazz == IBluetoothViewModel.class) {
            return new BluetoothViewModel(context);
        }
        if (clazz == AudioViewModel.class) {
            return new AudioViewModel(context);
        }
        if (clazz == ISignalViewModel.class) {
            return new SignalViewModel(context);
        }
        if (clazz == IUsbViewModel.class) {
            return new UsbViewModel(context);
        }
        if (clazz == IStorageViewModel.class) {
            return new StorageViewModel(context);
        }
        if (clazz == IUpgradeViewModel.class) {
            return new UpgradeViewModel(context);
        }
        if (clazz == CarModeViewModel.class) {
            return new CarModeViewModel(context);
        }
        if (clazz == IoTViewModel.class) {
            return new IoTViewModel(context);
        }
        throw new IllegalArgumentException("createViewModel fail class: " + clazz.getSimpleName());
    }
}
