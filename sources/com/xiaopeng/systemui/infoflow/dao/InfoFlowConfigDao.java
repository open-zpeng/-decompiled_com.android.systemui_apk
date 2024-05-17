package com.xiaopeng.systemui.infoflow.dao;

import android.text.TextUtils;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class InfoFlowConfigDao {
    private static final String TAG = "InfoFlowConfigDao";
    private static volatile InfoFlowConfigDao mInstance;
    private final int visualizerViewDefaultType = 0;
    private final long cardAnimationDefaultDuration = 300;
    private final long cardFocusDefaultDuration = 14000;
    private final int carCheckIntervalDefault = 15;
    private Config mConfig = new Config();

    /* loaded from: classes24.dex */
    public static class Config {
        public boolean cardUpdateScroll = false;
        public boolean cardInsertScroll = true;
        public int visualizerViewType = 0;
        public long cardAnimationDuration = 300;
        public long cardFocusedTime = 14000;
        public boolean avatarEnable = true;
        public boolean infoflowEnable = true;
        public boolean visualizerEnable = false;
        public boolean angleCardEnable = false;
        public boolean infoCarCheck = true;
        public boolean blurAlbum = true;
        public int carCheckInterval = 15;
    }

    private InfoFlowConfigDao() {
    }

    public static InfoFlowConfigDao getInstance() {
        if (mInstance == null) {
            synchronized (InfoFlowConfigDao.class) {
                if (mInstance == null) {
                    mInstance = new InfoFlowConfigDao();
                }
            }
        }
        return mInstance;
    }

    public void parseConfigFile() {
        String infoflowConfigString = getInfoFlowConfig();
        Logger.d(TAG, "infoflowConfigString--" + infoflowConfigString);
        if (!TextUtils.isEmpty(infoflowConfigString)) {
            try {
                JSONObject jsonObject = new JSONObject(infoflowConfigString);
                this.mConfig.cardUpdateScroll = jsonObject.optBoolean("cardUpdateScroll", false);
                this.mConfig.cardInsertScroll = jsonObject.optBoolean("cardInsertScroll", true);
                this.mConfig.visualizerViewType = jsonObject.optInt("visualizerViewType", 0);
                this.mConfig.cardAnimationDuration = jsonObject.optLong("cardAnimationDuration", 300L);
                this.mConfig.cardFocusedTime = jsonObject.optLong("cardFocusedTime", 14000L);
                this.mConfig.avatarEnable = jsonObject.optBoolean("avatarEnable", true);
                this.mConfig.infoflowEnable = jsonObject.optBoolean("infoflowEnable", true);
                this.mConfig.visualizerEnable = jsonObject.optBoolean("visualizerEnable", false);
                this.mConfig.angleCardEnable = jsonObject.optBoolean("angleCardEnable", false);
                this.mConfig.infoCarCheck = jsonObject.optBoolean("infoCarCheck", true);
                this.mConfig.blurAlbum = jsonObject.optBoolean("blurAlbum", true);
                this.mConfig.carCheckInterval = jsonObject.optInt("carCheckInterval", 15);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Logger.d(TAG, "parseConfigFile success");
        }
    }

    public Config getConfig() {
        return this.mConfig;
    }

    private String getInfoFlowConfig() {
        try {
            File readname = new File("/data/xuiservice/infoflowconfig.json");
            if (!readname.exists()) {
                readname = new File("/system/etc/infoflowconfig.json");
                if (!readname.exists()) {
                    Logger.w(TAG, "getInfoFlowConfig infoflowconfig.json not exist!!");
                    return "";
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(new FileInputStream(readname));
            BufferedReader br = new BufferedReader(reader);
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    stringBuilder.append(line);
                } else {
                    br.close();
                    reader.close();
                    return stringBuilder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
