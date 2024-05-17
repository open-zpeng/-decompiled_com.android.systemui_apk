package com.xiaopeng.systemui.viewmodel.carmode;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.systemui.R;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.controller.RepairModeController;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.viewmodel.IViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class CarModeViewModel extends BroadcastReceiver implements RepairModeController.OnRepairModeChangeChangeListener, IViewModel, RepairModeController.OnAuthModeChangedListener {
    private static final String BROADCAST_DIAGNOSTIC_MODE = "com.xiaopeng.broadcast.ACTION_DIAGNOSTIC_MODE";
    public static final int DISABLE_MODE_EXHIBITION = 2;
    public static final int DISABLE_MODE_EXIT = 0;
    public static final int DISABLE_MODE_TEST = 1;
    private static final String EXTRA_DIAGNOSTIC_MODE = "mode";
    private static final String KEY_CHILD_MODE = "child_mode_sw";
    private static final String TAG = "CarModeViewModel";
    private ContentObserver mContentObserver;
    private ContentResolver mContentResolver;
    private Context mContext;
    private RepairModeController mRepairModeController;
    private static final String KEY_DISABLE_MODE = "model_setting_disable";
    private static final Uri URI_DISABLE_MODE = Settings.System.getUriFor(KEY_DISABLE_MODE);
    private static final Uri URI_CHILD_MODE = Settings.System.getUriFor("child_mode_sw");
    private final MutableLiveData<Boolean> mRepairModeState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mAuthModeState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDisableModeData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mGearLevel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mEvSysReadyState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mExhibitionMode = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mChildMode = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDiagnosticMode = new MutableLiveData<>();
    private CarController.CarCallback mCarCallback = new CarController.CarCallback() { // from class: com.xiaopeng.systemui.viewmodel.carmode.CarModeViewModel.1
        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, Object newValue) {
            boolean needUpdateDisableMode = false;
            switch (type) {
                case CarController.TYPE_VCU_EVSYS_READYST /* 3202 */:
                    CarModeViewModel.this.mEvSysReadyState.setValue((Integer) newValue);
                    needUpdateDisableMode = true;
                    break;
                case CarController.TYPE_VCU_EXHIBITION_MODE /* 3203 */:
                    CarModeViewModel.this.mExhibitionMode.setValue((Integer) newValue);
                    needUpdateDisableMode = true;
                    break;
                case CarController.TYPE_VCU_DISPLAY_GEAR_LEVEL /* 3204 */:
                    CarModeViewModel.this.mGearLevel.setValue((Integer) newValue);
                    needUpdateDisableMode = true;
                    break;
            }
            if (needUpdateDisableMode) {
                CarModeViewModel.this.updateDisableMode();
            }
        }
    };

    public CarModeViewModel(Context context) {
        this.mContext = context;
        this.mRepairModeController = new RepairModeController(context);
        this.mRepairModeController.setOnRepairModeChangeChangeListener(this);
        this.mRepairModeController.setOnAuthModeChangedListener(this);
        this.mContentResolver = this.mContext.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.viewmodel.carmode.CarModeViewModel.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                if (!selfChange) {
                    if (CarModeViewModel.URI_DISABLE_MODE.equals(uri)) {
                        CarModeViewModel.this.onDisableModeChanged();
                    } else if (CarModeViewModel.URI_CHILD_MODE.equals(uri)) {
                        CarModeViewModel.this.updateChildMode();
                    }
                }
            }
        };
        this.mContentResolver.registerContentObserver(URI_DISABLE_MODE, false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(URI_CHILD_MODE, false, this.mContentObserver);
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_DIAGNOSTIC_MODE);
        this.mContext.registerReceiver(this, intentFilter);
        initLiveData();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateChildMode() {
        this.mChildMode.postValue(Boolean.valueOf(Settings.System.getIntForUser(this.mContentResolver, "child_mode_sw", 0, -2) == 1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisableModeChanged() {
        updateDisableMode();
    }

    private void initLiveData() {
        this.mRepairModeState.setValue(Boolean.valueOf(this.mRepairModeController.isInRepairMode()));
        this.mAuthModeState.setValue(Boolean.valueOf(this.mRepairModeController.isInAuthMode()));
        this.mEvSysReadyState.setValue(Integer.valueOf(CarController.getInstance(this.mContext).getCarServiceAdapter().getEvSysReadyState()));
        this.mGearLevel.setValue(Integer.valueOf(CarController.getInstance(this.mContext).getCarServiceAdapter().getGearLevel()));
        this.mExhibitionMode.setValue(Integer.valueOf(CarController.getInstance(this.mContext).getCarServiceAdapter().getExhibitionMode()));
        this.mDiagnosticMode.setValue(false);
        updateDisableMode();
        updateChildMode();
        updateDiagnosticMode();
    }

    private void updateDiagnosticMode() {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.viewmodel.carmode.CarModeViewModel.3
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                try {
                    String result = (String) ApiRouter.route(builder.authority("com.xiaopeng.diagnostic.DiagnoseService").path("getFactoryMode").build());
                    Logger.d(CarModeViewModel.TAG, "updateDiagnosticMode :" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    CarModeViewModel.this.mDiagnosticMode.postValue(Boolean.valueOf(jsonObject.optInt(CarModeViewModel.EXTRA_DIAGNOSTIC_MODE, 0) == 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisableMode() {
        int disableMode = Settings.System.getIntForUser(this.mContentResolver, KEY_DISABLE_MODE, 0, -2);
        if (isInExhibitionMode()) {
            disableMode = 2;
        }
        this.mDisableModeData.postValue(Integer.valueOf(disableMode));
    }

    public boolean isInExhibitionMode() {
        return isEvSysUnReady() && isCarGearP() && isExhibitionMode();
    }

    private boolean isCarGearP() {
        return this.mGearLevel.getValue().intValue() == 4;
    }

    private boolean isEvSysUnReady() {
        return this.mEvSysReadyState.getValue().intValue() != 2;
    }

    private boolean isExhibitionMode() {
        return this.mExhibitionMode.getValue().intValue() == 1;
    }

    public boolean isDiagnosticModeOn() {
        return this.mDiagnosticMode.getValue().booleanValue();
    }

    public LiveData<Boolean> getRepairModeStateData() {
        return this.mRepairModeState;
    }

    public LiveData<Boolean> getAuthModeStateData() {
        return this.mAuthModeState;
    }

    public LiveData<Integer> getDisableModeData() {
        return this.mDisableModeData;
    }

    public LiveData<Boolean> getChildModeData() {
        return this.mChildMode;
    }

    public LiveData<Boolean> getDiagnosticModeData() {
        return this.mDiagnosticMode;
    }

    public void destroy() {
        this.mRepairModeController.destroy();
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
    }

    @Override // com.xiaopeng.systemui.controller.RepairModeController.OnRepairModeChangeChangeListener
    public void onRepairModeChanged(boolean status, int switchResult) {
        this.mRepairModeState.postValue(Boolean.valueOf(status));
    }

    @Override // com.xiaopeng.systemui.controller.RepairModeController.OnAuthModeChangedListener
    public void onAuthModeChanged(boolean status, int switchResult) {
        this.mAuthModeState.postValue(Boolean.valueOf(status));
    }

    public String getAuthEndTime() {
        long endTime = this.mRepairModeController.getAuthEndTime();
        Logger.d(TAG, "getAuthEndTime : " + endTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.mContext.getString(R.string.auth_close_time_format), Locale.getDefault());
        return simpleDateFormat.format(new Date(endTime));
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (((action.hashCode() == -710292781 && action.equals(BROADCAST_DIAGNOSTIC_MODE)) ? (char) 0 : (char) 65535) == 0) {
            Logger.d(TAG, "diagnostic mode = " + intent.getIntExtra(EXTRA_DIAGNOSTIC_MODE, 0));
            this.mDiagnosticMode.postValue(Boolean.valueOf(intent.getIntExtra(EXTRA_DIAGNOSTIC_MODE, 0) == 1));
        }
    }
}
