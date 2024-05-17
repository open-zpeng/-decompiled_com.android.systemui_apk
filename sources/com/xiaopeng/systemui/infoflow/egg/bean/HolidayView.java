package com.xiaopeng.systemui.infoflow.egg.bean;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import com.android.systemui.SystemUIApplication;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.systemui.infoflow.egg.HolidayEventManager;
import com.xiaopeng.systemui.infoflow.egg.utils.HolidayViewDataUtils;
import com.xiaopeng.systemui.infoflow.egg.utils.NumberUtils;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class HolidayView implements Serializable {
    @SerializedName("data")
    public HashMap<String, Object> data;
    @SerializedName("layout")
    public HolidayLayout layout;
    @Expose(deserialize = false, serialize = false)
    private transient HolidayConfigItem mHolidayConfigItem;
    @SerializedName("message")
    public HolidayMessage message;

    public String getTts() {
        String tts = getString("tts");
        return tts;
    }

    public int getTtsDelay() {
        int ttsDelay = NumberUtils.pause(getString("ttsDelay"), 0);
        return ttsDelay;
    }

    public boolean ttsContainsNickName() {
        String tts = getString("tts");
        return tts != null && tts.contains("${nickname}");
    }

    public String getIntroTts() {
        String tts = getString("introTts");
        return tts;
    }

    public boolean introTtsContainsNickName() {
        return getString("introTts") != null && getString("introTts").contains("${nickname}");
    }

    public String getTtsMusic() {
        String ttsMusic = getString("ttsMusic");
        return ttsMusic;
    }

    public String getString(String key) {
        return getField(HolidayViewDataUtils.getData(this.data, key));
    }

    public String getField(String field) {
        return HolidayViewDataUtils.getField(this.data, field);
    }

    public File getFile(String field) {
        String field2 = getField(field);
        if (TextUtils.isEmpty(field2)) {
            return null;
        }
        String fieldPath = this.mHolidayConfigItem.wrapPath(field2);
        File fieldFile = new File(HolidayEventManager.getHolidayFile(), fieldPath);
        return fieldFile;
    }

    public int getColor(String field, int defaultColor) {
        String field2 = getField(field);
        if (TextUtils.isEmpty(field2)) {
            return defaultColor;
        }
        try {
            int color = Color.parseColor(field2);
            return color;
        } catch (Exception e) {
            LogUtils.e("HolidayView", "getColor", e);
            return defaultColor;
        }
    }

    public int getInteger(String field, int defaultValue) {
        String field2 = getField(field);
        if (TextUtils.isEmpty(field2)) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(field2);
            return value;
        } catch (Exception e) {
            LogUtils.e("HolidayView", "getInteger", e);
            return defaultValue;
        }
    }

    public Drawable getDrawable(String field) {
        String field2 = getField(field);
        if (TextUtils.isEmpty(field2)) {
            return null;
        }
        Context context = SystemUIApplication.getContext();
        File fieldFile = getFile(field2);
        try {
            Drawable drawable = new BitmapDrawable(context.getResources(), fieldFile.getAbsolutePath());
            return drawable;
        } catch (Exception e) {
            LogUtils.e("HolidayView", "getDrawable", e);
            return null;
        }
    }

    public void setHolidayConfigItem(HolidayConfigItem holidayConfigItem) {
        this.mHolidayConfigItem = holidayConfigItem;
        HolidayLayout holidayLayout = this.layout;
        if (holidayLayout != null) {
            holidayLayout.setHolidayView(this);
        }
        HolidayMessage holidayMessage = this.message;
        if (holidayMessage != null) {
            holidayMessage.setHolidayView(this);
        }
    }

    public HolidayConfigItem getHolidayConfigItem() {
        return this.mHolidayConfigItem;
    }

    public View createView() {
        HolidayLayout holidayLayout = this.layout;
        if (holidayLayout == null) {
            return null;
        }
        return holidayLayout.createView();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        HolidayLayout holidayLayout = this.layout;
        if (holidayLayout != null) {
            holidayLayout.setOnClickListener(onClickListener);
        }
    }

    public void initExtraData(MatchTimeFile matchTimeFile) {
        HolidayViewDataUtils.initUserInfo(this);
        UpdateResBean holiday = matchTimeFile.getHoliday();
        if (holiday == null) {
            return;
        }
        HashMap<String, Object> data = holiday.getExtra();
        putData(data);
    }

    public void putData(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        HashMap<String, Object> hashMap = this.data;
        if (hashMap != null) {
            hashMap.put(key, value);
            return;
        }
        this.data = new HashMap<>();
        this.data.put(key, value);
    }

    public void putData(HashMap<String, Object> data) {
        if (data == null) {
            return;
        }
        HashMap<String, Object> hashMap = this.data;
        if (hashMap != null) {
            hashMap.putAll(data);
        } else {
            this.data = data;
        }
    }
}
