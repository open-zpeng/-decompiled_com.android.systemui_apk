package com.xiaopeng.systemui.carmanager;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import com.xiaopeng.systemui.carmanager.IBaseCallback;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes24.dex */
public abstract class BaseCarController<C, T extends IBaseCallback> implements IBaseCarController<T> {
    protected C mCarManager;
    protected final Object mCallbackLock = new Object();
    protected final CopyOnWriteArrayList<T> mCallbacks = new CopyOnWriteArrayList<>();
    protected final ConcurrentHashMap<Integer, CarPropertyValue<?>> mCarPropertyMap = new ConcurrentHashMap<>();
    protected final List<Integer> mPropertyIds = getRegisterPropertyIds();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void disconnect();

    protected abstract List<Integer> getRegisterPropertyIds();

    protected abstract void handleEventsUpdate(CarPropertyValue<?> carPropertyValue);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void initCarManager(Car car);

    @Override // com.xiaopeng.systemui.carmanager.IBaseCarController
    public final void registerCallback(T callback) {
        if (callback != null) {
            this.mCallbacks.add(callback);
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.IBaseCarController
    public final void unregisterCallback(T callback) {
        if (callback != null) {
            this.mCallbacks.remove(callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void handleCarEventsUpdate(final CarPropertyValue<?> value) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.-$$Lambda$BaseCarController$Ruk1ilP0W9HGlTAD3Yl3L2rjOV4
            @Override // java.lang.Runnable
            public final void run() {
                BaseCarController.this.lambda$handleCarEventsUpdate$0$BaseCarController(value);
            }
        });
    }

    public /* synthetic */ void lambda$handleCarEventsUpdate$0$BaseCarController(CarPropertyValue value) {
        this.mCarPropertyMap.put(Integer.valueOf(value.getPropertyId()), value);
        handleEventsUpdate(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getIntProperty(int propertyId) throws Exception {
        return ((Integer) getValue(getCarProperty(propertyId))).intValue();
    }

    protected final int[] getIntArrayProperty(int propertyId) throws Exception {
        return getIntArrayProperty(getCarProperty(propertyId));
    }

    protected final int[] getIntArrayProperty(CarPropertyValue<?> value) {
        Object[] values = (Object[]) getValue(value);
        int[] result = null;
        if (values != null) {
            result = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                Object objValue = values[i];
                if (objValue instanceof Integer) {
                    result[i] = ((Integer) objValue).intValue();
                }
            }
        }
        return result;
    }

    protected final float getFloatProperty(int propertyId) throws Exception {
        return ((Float) getValue(getCarProperty(propertyId))).floatValue();
    }

    protected final float[] getFloatArrayProperty(int propertyId) throws Exception {
        return getFloatArrayProperty(getCarProperty(propertyId));
    }

    protected final float[] getFloatArrayProperty(CarPropertyValue<?> value) {
        Object[] values = (Object[]) getValue(value);
        float[] result = null;
        if (values != null) {
            result = new float[values.length];
            for (int i = 0; i < values.length; i++) {
                Object objValue = values[i];
                if (objValue instanceof Float) {
                    result[i] = ((Float) objValue).floatValue();
                }
            }
        }
        return result;
    }

    private CarPropertyValue<?> getCarProperty(int propertyId) throws Exception {
        CarPropertyValue<?> property = this.mCarPropertyMap.get(Integer.valueOf(propertyId));
        if (property != null) {
            return property;
        }
        throw new Exception("Car property not found");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final <E> E getValue(CarPropertyValue<?> value) {
        return (E) value.getValue();
    }
}
