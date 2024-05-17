package com.xiaopeng.systemui.infoflow.egg;

import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.SystemUIApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.lib.utils.SystemPropertyUtil;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayConfig;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayConfigItem;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayView;
import com.xiaopeng.systemui.infoflow.egg.bean.MatchTimeFile;
import com.xiaopeng.systemui.infoflow.egg.bean.UpdateResBean;
import com.xiaopeng.systemui.infoflow.egg.model.HolidayDebug;
import com.xiaopeng.systemui.infoflow.egg.model.HolidayModel;
import com.xiaopeng.systemui.infoflow.egg.utils.RandomUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes24.dex */
public class HolidayEventManager {
    private static final String DIR_NAME_FLOAT = "float";
    private static final String DIR_NAME_HOLIDAY = "holiday";
    private static final String DIR_NAME_TEMP = "temp";
    private static final String FILE_NAME_CONFIG = "config.json";
    public static final String FILE_NAME_FLOAT = "float.json";
    private static final String KEY_LAST_HOLIDAY_TIME = "KEY_LAST_HOLIDAY_TIME";
    private static final String PRE_FILE_NAME = "base_config";
    public static final String SUFFIX_BOTH = ".3";
    public static final String SUFFIX_OFF = ".2";
    public static final String SUFFIX_ON = ".1";
    private static final String TAG = "HolidayEventManager";
    private static int sIndex = 0;

    public static File getHolidayFile() {
        File holidayFile = new File(Environment.getExternalStorageDirectory(), DIR_NAME_HOLIDAY);
        if (!holidayFile.exists()) {
            holidayFile.mkdirs();
        }
        return holidayFile;
    }

    public static boolean hasHolidayEvent(boolean isGetOffScene) {
        return getMachTimeFile(getFactor(isGetOffScene)) != null;
    }

    private static String getFactor(boolean isGetOffScene) {
        return isGetOffScene ? "2" : "1";
    }

    private static MatchTimeFile getMachTimeFile(String factor) {
        MatchTimeFile matchTimeFile;
        long currentTime = System.currentTimeMillis();
        List<UpdateResBean> matchedList = HolidayModel.instance().getHolidayList();
        MatchTimeFile matchTimeFile2 = null;
        UpdateResBean holiday = null;
        if (matchedList == null || matchedList.isEmpty()) {
            matchTimeFile = null;
        } else {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(11);
            long uid = SystemPropertyUtil.getAccountUid();
            List<UpdateResBean> matchedList2 = new LinkedList<>();
            for (UpdateResBean updateResBean : matchedList) {
                List<UpdateResBean> holidayList = matchedList;
                MatchTimeFile matchTimeFile3 = matchTimeFile2;
                List<UpdateResBean> holidayList2 = matchedList2;
                if (updateResBean.matchTime(currentTime, hour, uid) && canShow(updateResBean.sign, factor)) {
                    holidayList2.add(updateResBean);
                }
                matchedList2 = holidayList2;
                matchedList = holidayList;
                matchTimeFile2 = matchTimeFile3;
            }
            matchTimeFile = matchTimeFile2;
            List<UpdateResBean> holidayList3 = matchedList2;
            for (UpdateResBean updateResBean2 : holidayList3) {
                if (holiday == null || updateResBean2.priority > holiday.priority || (updateResBean2.priority == holiday.priority && updateResBean2.getDuration() < holiday.getDuration())) {
                    holiday = updateResBean2;
                }
            }
        }
        if (holiday == null) {
            holiday = getHolidayByConfigFile(factor);
        }
        if (holiday != null) {
            matchTimeFile = new MatchTimeFile(holiday, currentTime);
        }
        LogUtils.e(TAG, "getMatchTimeFile cost time = " + (System.currentTimeMillis() - currentTime));
        return matchTimeFile;
    }

    private static UpdateResBean getHolidayByConfigFile(String factor) {
        File[] files;
        Calendar calendar;
        HashMap<String, Object> hashMap;
        StringBuilder sb;
        File[] files2 = getHolidayFile().listFiles();
        if (files2 == null || files2.length <= 0) {
            return null;
        }
        LogUtils.i(TAG, "file size = " + files2.length);
        Calendar calendar2 = Calendar.getInstance();
        int hour = calendar2.get(11);
        long currentTime = System.currentTimeMillis();
        int length = files2.length;
        int i = 0;
        UpdateResBean holiday = null;
        while (i < length) {
            File file = files2[i];
            if (file.isFile()) {
                files = files2;
                calendar = calendar2;
            } else if (DIR_NAME_TEMP.equals(file.getName())) {
                files = files2;
                calendar = calendar2;
            } else {
                String sign = file.getName();
                File configFile = new File(file, FILE_NAME_CONFIG);
                try {
                    HolidayConfig config = (HolidayConfig) new Gson().fromJson((Reader) new FileReader(configFile), (Class<Object>) HolidayConfig.class);
                    if (config == null) {
                        try {
                            LogUtils.e(TAG, "无法解析config文件");
                            files = files2;
                            calendar = calendar2;
                        } catch (FileNotFoundException e) {
                            e = e;
                            files = files2;
                            calendar = calendar2;
                            e.printStackTrace();
                            i++;
                            files2 = files;
                            calendar2 = calendar;
                        }
                    } else if (!config.matchTime(currentTime, hour)) {
                        LogUtils.e(TAG, "日期时间不匹配");
                        files = files2;
                        calendar = calendar2;
                    } else {
                        try {
                            if (!canShow(file.getName(), factor)) {
                                try {
                                    LogUtils.e(TAG, "彩蛋匹配场景不匹配");
                                    files = files2;
                                    calendar = calendar2;
                                } catch (FileNotFoundException e2) {
                                    e = e2;
                                    files = files2;
                                    calendar = calendar2;
                                    e.printStackTrace();
                                    i++;
                                    files2 = files;
                                    calendar2 = calendar;
                                }
                            } else {
                                holiday = new UpdateResBean();
                                holiday.sign = sign;
                                holiday.priority = 1;
                                holiday.extra = new HashMap<>();
                                files = files2;
                                try {
                                    holiday.extra.put("startTime", String.valueOf(config.getHolidayConfigItem().startTime));
                                    holiday.extra.put("endTime", String.valueOf(config.getHolidayConfigItem().endTime));
                                    hashMap = holiday.extra;
                                    sb = new StringBuilder();
                                    calendar = calendar2;
                                } catch (FileNotFoundException e3) {
                                    e = e3;
                                    calendar = calendar2;
                                }
                                try {
                                    sb.append(config.getHolidayConfigItem().showHourStart);
                                    sb.append("-");
                                    sb.append(config.getHolidayConfigItem().showHourEnd);
                                    hashMap.put("showHour", String.valueOf(sb.toString()));
                                    LogUtils.i(TAG, "match file :" + holiday.toString());
                                    return holiday;
                                } catch (FileNotFoundException e4) {
                                    e = e4;
                                    e.printStackTrace();
                                    i++;
                                    files2 = files;
                                    calendar2 = calendar;
                                }
                            }
                        } catch (FileNotFoundException e5) {
                            e = e5;
                            files = files2;
                            calendar = calendar2;
                            e.printStackTrace();
                            i++;
                            files2 = files;
                            calendar2 = calendar;
                        }
                    }
                } catch (FileNotFoundException e6) {
                    e = e6;
                }
            }
            i++;
            files2 = files;
            calendar2 = calendar;
        }
        return holiday;
    }

    private static boolean canShow(String sign, String factor) {
        if (TextUtils.isEmpty(sign)) {
            return false;
        }
        File configDirFile = new File(getHolidayFile(), sign);
        if (HolidayDebug.sDebugNoRename) {
            return configDirFile.exists();
        }
        File file = new File(configDirFile, sign + "." + factor);
        if (file.exists()) {
            return true;
        }
        File file2 = new File(configDirFile, sign + SUFFIX_BOTH);
        return file2.exists();
    }

    public static HolidayView getHolidayView(boolean isGetOffScene) {
        long currentTime = System.currentTimeMillis();
        MatchTimeFile matchTimeFile = getMachTimeFile(getFactor(isGetOffScene));
        if (matchTimeFile == null) {
            return null;
        }
        HolidayConfigItem holidayConfigItem = getHolidayConfigItem(matchTimeFile);
        HolidayView holidayView = createHolidayView(holidayConfigItem, matchTimeFile, DIR_NAME_FLOAT, FILE_NAME_FLOAT, (String) null);
        LogUtils.i(TAG, "create HolidayView cost time : " + (System.currentTimeMillis() - currentTime) + "ms");
        if (holidayView != null) {
            matchTimeFile.rename();
            matchTimeFile.createMessageRecord(holidayView);
        }
        return holidayView;
    }

    private static HolidayView createHolidayView(HolidayConfigItem holidayConfigItem, MatchTimeFile matchTimeFile, String dataDir, String fileName, String dataFileName) {
        if (holidayConfigItem == null) {
            holidayConfigItem = getHolidayConfigItem(matchTimeFile);
        }
        return createHolidayView(holidayConfigItem, matchTimeFile.currentTime, dataDir, fileName, dataFileName);
    }

    private static HolidayView createHolidayView(HolidayConfigItem holidayConfigItem, long currentTime, String dataDir, String fileName, String dataFileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("createHolidayView:holidayConfigItem=");
        sb.append(holidayConfigItem != null);
        Log.i(TAG, sb.toString());
        if (holidayConfigItem == null) {
            return null;
        }
        HashMap<String, Object> data = getData(holidayConfigItem.getFile(dataDir), dataFileName);
        File floatFile = holidayConfigItem.getFile(fileName);
        Gson gson = new Gson();
        HolidayView holidayView = null;
        try {
            holidayView = (HolidayView) gson.fromJson((Reader) new FileReader(floatFile), (Class<Object>) HolidayView.class);
            if (holidayView != null) {
                saveLastHolidayTime(currentTime);
                holidayView.putData(data);
                holidayView.initExtraData(holidayConfigItem.getMatchTimeFile());
                holidayView.setHolidayConfigItem(holidayConfigItem);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
        return holidayView;
    }

    public static HolidayConfigItem getHolidayConfigItem(MatchTimeFile matchTimeFile) {
        HolidayConfigItem holidayConfigItem = null;
        HolidayConfig holidayConfig = getHolidayConfig(matchTimeFile.getCacheKey());
        if (holidayConfig != null) {
            holidayConfig.setConfigKey(matchTimeFile.getCacheKey());
            holidayConfigItem = holidayConfig.getHolidayConfigItem();
            if (holidayConfigItem != null) {
                holidayConfigItem.setMatchTimeFile(matchTimeFile);
            }
        }
        return holidayConfigItem;
    }

    public static HolidayConfig getHolidayConfig(String configDir) {
        Log.i(TAG, "getHolidayConfig:configDir=" + configDir);
        Gson gson = new Gson();
        File holidayDir = getHolidayFile();
        File configFile = new File(holidayDir, configDir + "/" + FILE_NAME_CONFIG);
        try {
            FileReader fileReader = new FileReader(configFile);
            HolidayConfig holidayConfig = (HolidayConfig) gson.fromJson((Reader) fileReader, (Class<Object>) HolidayConfig.class);
            return holidayConfig;
        } catch (FileNotFoundException e) {
            LogUtils.e(TAG, e);
            return null;
        } catch (Exception e2) {
            LogUtils.e(TAG, e2);
            return null;
        }
    }

    public static String getDataName(File dataDir) {
        String[] jsonFiles;
        int index;
        if (!dataDir.exists() || (jsonFiles = dataDir.list(new JsonFilter())) == null || jsonFiles.length <= 0) {
            return null;
        }
        if (HolidayDebug.sDebugSequenceData) {
            index = sIndex;
            sIndex = (index + 1) % jsonFiles.length;
        } else {
            int index2 = jsonFiles.length;
            index = RandomUtil.nextInt(index2);
        }
        String dataFileName = jsonFiles[index];
        return dataFileName;
    }

    public static HashMap<String, Object> getData(File dataDir, String dataFileName) {
        String[] jsonFiles;
        int index;
        File jsonFile = null;
        if (!TextUtils.isEmpty(dataFileName)) {
            File dataFile = new File(dataDir, dataFileName);
            if (dataFile.exists()) {
                jsonFile = dataFile;
            }
        }
        boolean jsonFileExit = jsonFile != null && jsonFile.exists();
        if (!jsonFileExit && dataDir.exists() && (jsonFiles = dataDir.list(new JsonFilter())) != null && jsonFiles.length > 0) {
            if (HolidayDebug.sDebugSequenceData) {
                index = sIndex;
                sIndex = (index + 1) % jsonFiles.length;
            } else {
                int index2 = jsonFiles.length;
                index = RandomUtil.nextInt(index2);
            }
            jsonFile = new File(dataDir, jsonFiles[index]);
        }
        HashMap<String, Object> data = null;
        if (jsonFile == null || !jsonFile.exists()) {
            return null;
        }
        try {
            data = (HashMap) new Gson().fromJson(new FileReader(jsonFile), new TypeToken<HashMap<String, Object>>() { // from class: com.xiaopeng.systemui.infoflow.egg.HolidayEventManager.1
            }.getType());
            LogUtils.i(TAG, "getData:jsonFile=" + jsonFile);
            return data;
        } catch (Exception e) {
            LogUtils.e(TAG, "getData", e.getMessage());
            return data;
        }
    }

    private static void saveLastHolidayTime(long currenTime) {
        SharedPreferences sharedPreferences = SystemUIApplication.getContext().getSharedPreferences(PRE_FILE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_HOLIDAY_TIME, currenTime);
        editor.apply();
    }

    /* loaded from: classes24.dex */
    public static class JsonFilter implements FilenameFilter {
        @Override // java.io.FilenameFilter
        public boolean accept(File dir, String name) {
            return name != null && name.endsWith(".json");
        }
    }
}
