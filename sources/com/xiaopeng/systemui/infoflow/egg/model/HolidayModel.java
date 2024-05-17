package com.xiaopeng.systemui.infoflow.egg.model;

import com.xiaopeng.systemui.infoflow.egg.HolidayEventManager;
import com.xiaopeng.systemui.infoflow.egg.bean.UpdateHolBean;
import com.xiaopeng.systemui.infoflow.egg.bean.UpdateResBean;
import com.xiaopeng.systemui.infoflow.egg.utils.FileUtils;
import java.io.File;
import java.util.List;
/* loaded from: classes24.dex */
public class HolidayModel {
    public static final String FILE_NAME_HOLIDAY = "hol.json";
    public static final String FILE_NAME_HOLIDAY_TEMP = "hol.json.t";
    private static final String TAG = "HolidayModel";
    private Object mLock;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class Holder {
        private static final HolidayModel Instance = new HolidayModel();

        private Holder() {
        }
    }

    public static final HolidayModel instance() {
        return Holder.Instance;
    }

    private HolidayModel() {
        this.mLock = new Object();
    }

    public List<UpdateResBean> getHolidayList() {
        UpdateHolBean updateResBean = read();
        return updateResBean.holidayList;
    }

    private UpdateHolBean read() {
        UpdateHolBean updateHolBean;
        File file = new File(HolidayEventManager.getHolidayFile(), FILE_NAME_HOLIDAY);
        synchronized (this.mLock) {
            updateHolBean = (UpdateHolBean) FileUtils.readFileObject(file, (Class<Object>) UpdateHolBean.class);
        }
        return updateHolBean == null ? UpdateHolBean.empty() : updateHolBean;
    }
}
