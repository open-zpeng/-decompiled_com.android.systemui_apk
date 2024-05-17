package com.xiaopeng.systemui.server;

import android.app.INotificationManager;
import android.app.NotificationManager;
import android.os.Environment;
import android.os.RemoteException;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.xuimanager.systemui.osd.IOsdListener;
import com.xiaopeng.xuimanager.systemui.osd.OsdRegionRecord;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
/* loaded from: classes24.dex */
class OsdServer extends BaseServer {
    private final HashMap<String, IOsdListener> mOsdQueue;
    private final HashMap<String, OsdRegionRecord> sRecordMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    public int showOsd(IOsdListener callback, int osdType, String regionId) {
        logI("requestShow: callback = " + callback + "   osdType = " + osdType + "  regionId = " + regionId);
        INotificationManager service = NotificationManager.getService();
        try {
            if (!service.isOsdEnable()) {
                logI("poppanel is blocked");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getRegionRecord(regionId) == null) {
            return 0;
        }
        OsdRegionRecord record = getRegionRecord(regionId);
        synchronized (this.mOsdQueue) {
            try {
                if (osdType == 1) {
                    for (String regionName : this.sRecordMap.keySet()) {
                        if (this.sRecordMap.get(regionName).getScreenId() == record.getScreenId() && this.mOsdQueue.containsKey(regionName)) {
                            try {
                                this.mOsdQueue.get(regionName).hideOsd();
                            } catch (RemoteException e2) {
                                e2.printStackTrace();
                                this.mOsdQueue.remove(regionName);
                            }
                        }
                    }
                    try {
                        callback.showOsd(record);
                    } catch (RemoteException e3) {
                        e3.printStackTrace();
                    }
                    this.mOsdQueue.put(regionId, callback);
                } else if (osdType == 2) {
                    if (this.mOsdQueue.containsKey(regionId)) {
                        try {
                            this.mOsdQueue.get(regionId).hideOsd();
                        } catch (RemoteException e4) {
                            e4.printStackTrace();
                            this.mOsdQueue.remove(regionId);
                        }
                    }
                    try {
                        callback.showOsd(record);
                    } catch (RemoteException e5) {
                        e5.printStackTrace();
                    }
                    this.mOsdQueue.put(regionId, callback);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int hideOsd(IOsdListener callback, String regionId) {
        logI(String.format("hideOsd callback:%s, regionId: %s", callback, regionId));
        this.mOsdQueue.remove(regionId);
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getJSON() {
        try {
            String filePath = Environment.getRootDirectory() + "/etc/xuiservice/systemui/pop_panel_config.json";
            File file = new File(filePath);
            if (!file.exists()) {
                logI("getJSON: url = null");
                return;
            }
            InputStreamReader inputSR = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            logI("getJSON: inputSR = " + inputSR);
            BufferedReader bufferedR = new BufferedReader(inputSR);
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = bufferedR.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
            bufferedR.close();
            inputSR.close();
            JSONObject object = new JSONObject(String.valueOf(builder));
            JSONArray array = object.getJSONArray("region_config_sheet");
            logI("array = " + array);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                OsdRegionRecord record = new OsdRegionRecord();
                record.setRegionId(item.getString("RegionID"));
                record.setX(item.getInt("X"));
                record.setY(item.getInt("Y"));
                record.setMinWidth(item.getInt("MinWidth"));
                record.setMinHeight(item.getInt("MinHeight"));
                record.setMaxWidth(item.getInt("MaxWidth"));
                record.setMaxHeight(item.getInt("MaxHeight"));
                record.setScreenId(item.getString("ScreenID"));
                record.setWindowType(item.optInt("WindowType", WindowHelper.TYPE_VUI));
                this.sRecordMap.put(record.getRegionId(), record);
                logI("object = " + item + "  id = " + item.getString("RegionID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OsdRegionRecord getRegionRecord(String regionId) {
        return this.sRecordMap.getOrDefault(regionId, null);
    }

    @Override // com.xiaopeng.systemui.server.BaseServer
    protected String logTag() {
        return "Osd";
    }

    /* loaded from: classes24.dex */
    private static class Holder {
        private static final OsdServer sInstance = new OsdServer();

        private Holder() {
        }
    }

    private OsdServer() {
        this.mOsdQueue = new HashMap<>();
        this.sRecordMap = new HashMap<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static OsdServer get() {
        return Holder.sInstance;
    }
}
