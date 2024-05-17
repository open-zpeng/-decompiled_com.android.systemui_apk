package com.xiaopeng.systemui.infoflow.egg.utils;

import android.text.TextUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes24.dex */
public class HolidayViewDataUtils {
    public static final Pattern DATA_PATTERN_2 = Pattern.compile("\\$\\{(.*)\\}\\[(.*)\\]");

    public static String getData(HashMap<String, Object> data, String key) {
        if (!isDataAvailable(data)) {
            return null;
        }
        Object object = data.get(key);
        if (object instanceof String) {
            String value = (String) object;
            return value;
        } else if (object instanceof List) {
            List<String> list = (List) object;
            String value2 = list.get(RandomUtil.nextInt(list.size()));
            return value2;
        } else if (object instanceof Number) {
            String value3 = String.valueOf(object);
            return value3;
        } else if (object == null) {
            return null;
        } else {
            Gson gson = new Gson();
            String value4 = gson.toJson(object);
            return value4;
        }
    }

    public static String getData(HashMap<String, Object> data, String key, int index) {
        if (!isDataAvailable(data)) {
            return null;
        }
        Object object = data.get(key);
        if (object instanceof List) {
            List<String> list = (List) object;
            if (index >= list.size()) {
                return null;
            }
            String value = list.get(index);
            return value;
        } else if (object instanceof Map) {
            Object valueObj = ((Map) object).get(String.valueOf(index));
            String value2 = GsonUtils.obj2String(valueObj);
            return value2;
        } else if (!(object instanceof String)) {
            return null;
        } else {
            String string = ((String) object).trim();
            if (string.startsWith(NavigationBarInflaterView.SIZE_MOD_START) && string.endsWith(NavigationBarInflaterView.SIZE_MOD_END)) {
                Gson gson = new Gson();
                try {
                    ArrayList<String> list2 = (ArrayList) gson.fromJson(string, new TypeToken<ArrayList<String>>() { // from class: com.xiaopeng.systemui.infoflow.egg.utils.HolidayViewDataUtils.1
                    }.getType());
                    data.put(key, list2);
                    if (index >= list2.size()) {
                        return null;
                    }
                    String value3 = list2.get(index);
                    return value3;
                } catch (JsonSyntaxException e) {
                    return null;
                }
            } else if (!string.startsWith("{") || !string.endsWith("}")) {
                return null;
            } else {
                Gson gson2 = new Gson();
                try {
                    HashMap<String, Object> map = (HashMap) gson2.fromJson(string, new TypeToken<HashMap<String, Object>>() { // from class: com.xiaopeng.systemui.infoflow.egg.utils.HolidayViewDataUtils.2
                    }.getType());
                    data.put(key, map);
                    Object valueObj2 = map.get(String.valueOf(index));
                    String value4 = GsonUtils.obj2String(valueObj2);
                    return value4;
                } catch (JsonSyntaxException e2) {
                    return null;
                }
            }
        }
    }

    public static String getData(HashMap<String, Object> data, String key, String mapKey) {
        if (!isDataAvailable(data)) {
            return null;
        }
        Object object = data.get(key);
        if (object instanceof Map) {
            Object valueObj = ((Map) object).get(mapKey);
            String value = GsonUtils.obj2String(valueObj);
            return value;
        } else if (!(object instanceof String)) {
            return null;
        } else {
            String string = ((String) object).trim();
            if (!string.startsWith("{") || !string.endsWith("}")) {
                return null;
            }
            Gson gson = new Gson();
            try {
                HashMap<String, Object> map = (HashMap) gson.fromJson(string, new TypeToken<HashMap<String, Object>>() { // from class: com.xiaopeng.systemui.infoflow.egg.utils.HolidayViewDataUtils.3
                }.getType());
                data.put(key, map);
                Object valueObj2 = map.get(mapKey);
                String value2 = GsonUtils.obj2String(valueObj2);
                return value2;
            } catch (JsonSyntaxException e) {
                return null;
            }
        }
    }

    private static boolean isDataAvailable(HashMap<String, Object> data) {
        return (data == null || data.isEmpty()) ? false : true;
    }

    public static String getField(HashMap<String, Object> data, String field) {
        if (TextUtils.isEmpty(field)) {
            return field;
        }
        if (!checkDollar(field)) {
            return field;
        }
        boolean dataPattern = false;
        if (field.startsWith("${") && field.endsWith(NavigationBarInflaterView.SIZE_MOD_END)) {
            Matcher matcher = DATA_PATTERN_2.matcher(field);
            if (matcher.matches()) {
                try {
                    String key = matcher.group(1);
                    String indexStr = matcher.group(2);
                    if (indexStr.contains(",")) {
                        String[] indexArray = indexStr.split(",");
                        field = getField(data, getData(data, key)).substring(Integer.parseInt(indexArray[0]), Integer.parseInt(indexArray[1]));
                    } else {
                        try {
                            int index = Integer.parseInt(indexStr);
                            field = getData(data, key, index);
                            field = getField(data, field);
                        } catch (NumberFormatException e) {
                            field = getField(data, getData(data, key, indexStr));
                        }
                    }
                    dataPattern = true;
                } catch (Exception e2) {
                    LogUtils.e("HolidayView", "getField", e2);
                }
            }
        }
        if (!dataPattern) {
            LinkedList<String> arrayField = toArrayField(data, field);
            if (arrayField.size() > 1) {
                StringBuilder valueBuilder = new StringBuilder();
                Iterator<String> it = arrayField.iterator();
                while (it.hasNext()) {
                    String str = it.next();
                    valueBuilder.append(str);
                }
                field = valueBuilder.toString();
            }
            if (arrayField.size() == 1) {
                return arrayField.get(0);
            }
            return field;
        }
        return field;
    }

    public static LinkedList<String> toArrayField(HashMap<String, Object> data, String field) {
        if (field == null || field.length() == 0) {
            return null;
        }
        StringBuilder keyBuilder = new StringBuilder();
        int charCount = field.length();
        boolean dataMatched = false;
        LinkedList<String> strArray = new LinkedList<>();
        char lastCh = 0;
        for (int i = 0; i < charCount; i++) {
            char ch = field.charAt(i);
            if (ch != '{') {
                if (ch == '}') {
                    if (dataMatched) {
                        String added = getField(data, getData(data, keyBuilder.toString()));
                        if (TextUtils.isEmpty(added)) {
                            added = "";
                        }
                        strArray.add(added);
                        keyBuilder.delete(0, keyBuilder.length());
                        dataMatched = false;
                    } else {
                        keyBuilder.append(ch);
                    }
                } else {
                    keyBuilder.append(ch);
                }
            } else if (lastCh == '$') {
                keyBuilder.deleteCharAt(keyBuilder.length() - 1);
                if (keyBuilder.length() > 0) {
                    String str = keyBuilder.toString();
                    strArray.add(str);
                    keyBuilder.delete(0, keyBuilder.length());
                }
                dataMatched = true;
            } else {
                keyBuilder.append(ch);
            }
            if (i == charCount - 1 && keyBuilder.length() > 0) {
                String str2 = keyBuilder.toString();
                strArray.add(str2);
                keyBuilder.delete(0, keyBuilder.length());
            }
            lastCh = ch;
        }
        return strArray;
    }

    public static void initUserInfo(HolidayView holidayView) {
        String nickName = NicknameUtils.getNickname();
        holidayView.putData("nickname", nickName);
    }

    private static boolean checkDollar(String field) {
        if (field == null || field.length() == 0) {
            return false;
        }
        int charCount = field.length();
        char lastCh = 0;
        for (int i = 0; i < charCount; i++) {
            char ch = field.charAt(i);
            if (lastCh == '$' && ch == '{') {
                return true;
            }
            lastCh = ch;
        }
        return false;
    }
}
