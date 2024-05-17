package com.xiaopeng.systemui.infoflow.egg.bean;

import android.net.Uri;
import android.os.RemoteException;
import com.google.gson.Gson;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.systemui.infoflow.egg.HolidayEventManager;
import java.io.File;
/* loaded from: classes24.dex */
public class MatchTimeFile {
    private static final String TAG = "MatchTimeFile";
    public long currentTime;
    public String dialogData;
    private UpdateResBean mHoliday;

    public MatchTimeFile(UpdateResBean holiday, long currentTime) {
        this.mHoliday = holiday;
        this.currentTime = currentTime;
    }

    public UpdateResBean getHoliday() {
        return this.mHoliday;
    }

    public void rename() {
        File holidayFile = HolidayEventManager.getHolidayFile();
        String cacheKey = getCacheKey();
        File configDirFile = new File(holidayFile, cacheKey);
        File file = getRenameFile(configDirFile, cacheKey);
        if (file == null) {
            LogUtils.i(TAG, "rename:file is null");
            return;
        }
        File targetFile = new File(configDirFile, cacheKey);
        boolean renameTo = file.renameTo(targetFile);
        LogUtils.i(TAG, "rename:renameTo=" + renameTo + ",file=" + file.toString() + ",targetFile=" + targetFile);
    }

    public File getRenameFile(File configDirFile, String cacheKey) {
        File file = new File(configDirFile, cacheKey + HolidayEventManager.SUFFIX_BOTH);
        if (file.exists()) {
            return file;
        }
        File file2 = new File(configDirFile, cacheKey + HolidayEventManager.SUFFIX_ON);
        if (file2.exists()) {
            return file2;
        }
        File file3 = new File(configDirFile, cacheKey + HolidayEventManager.SUFFIX_OFF);
        if (file3.exists()) {
            return file3;
        }
        return null;
    }

    public boolean exists() {
        File holidayFile = HolidayEventManager.getHolidayFile();
        File file = new File(holidayFile, this.mHoliday.sign);
        return file.exists();
    }

    public void createMessageRecord(HolidayView holidayView) {
        LogUtils.e(TAG, "--message = " + holidayView.message);
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("createHolidayMessage").appendQueryParameter("holidayMessageJson", new Gson().toJson(holidayView.message)).appendQueryParameter("startTime", String.valueOf(this.mHoliday.getStartTime())).appendQueryParameter("lastTime", String.valueOf(this.mHoliday.getEndTime()));
            ApiRouter.route(builder.build());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "{" + this.currentTime + "," + this.dialogData + "," + getCacheKey() + "}";
    }

    public String getCacheKey() {
        return this.mHoliday.sign;
    }
}
