package com.xiaopeng.speech.vui.utils;

import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.xiaopeng.speech.apirouter.Utils;
import com.xiaopeng.speech.common.SpeechConstant;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class VuiUtils {
    public static final String CAR_PLATFORM_A1 = "A1";
    public static final String CAR_PLATFORM_A2 = "A2";
    public static final String CAR_PLATFORM_A3 = "A3";
    public static final String CAR_PLATFORM_Q1 = "Q1";
    public static final String CAR_PLATFORM_Q2 = "Q2";
    public static final String CAR_PLATFORM_Q3 = "Q3";
    public static final String CAR_PLATFORM_Q5 = "Q5";
    public static final String CAR_PLATFORM_Q6 = "Q6";
    public static final String CAR_PLATFORM_Q7 = "Q7";
    private static List<String> support3DPlatform;
    public static int LIST_VEDIO_TYPE = 1;
    private static ExclusionStrategy mExclusionStrategy = new ExclusionStrategy() { // from class: com.xiaopeng.speech.vui.utils.VuiUtils.1
        @Override // com.google.gson.ExclusionStrategy
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals(VuiConstants.TIMESTAMP);
        }

        @Override // com.google.gson.ExclusionStrategy
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };
    public static final List<String> VUI_ENABLE_APP = Arrays.asList(VuiConstants.MAP_APPNMAE, VuiConstants.MUSIC, VuiConstants.CARCONTROL, VuiConstants.SETTINS, VuiConstants.CHARGE, "com.xiaopeng.xmart.camera", "com.xiaopeng.btphone", "com.xiaopeng.aiassistant", SpeechConstant.SPEECH_SERVICE_PACKAGE_NAME, VuiConstants.SCENEDEMO, "com.xiaopeng.vui.demo");
    private static ExclusionStrategy mAttrExclusionStrategy = new ExclusionStrategy() { // from class: com.xiaopeng.speech.vui.utils.VuiUtils.2
        @Override // com.google.gson.ExclusionStrategy
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals(VuiConstants.SCENE_ELEMENTS);
        }

        @Override // com.google.gson.ExclusionStrategy
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };
    private static boolean isDisableVuiFeature = false;
    private static boolean userDisabledFeature = false;
    private static String sRegion = null;
    private static VuiMode viewMode = VuiMode.NORMAL;
    private static String sType = null;
    private static List<String> supportType = new ArrayList();

    static {
        supportType.add(CAR_PLATFORM_Q2);
        support3DPlatform = new ArrayList();
        support3DPlatform.add(CAR_PLATFORM_Q7);
    }

    public static void test() {
        VuiScene vuiScene = stringConvertToVuiScene("{\n    \"id\": \"navigation_search\",\n    \"elements\": [\n        {\n            \"type\": \"Button\",\n            \"label\": \"返回\",\n            \"id\": 10020,\n            \"position\": 2\n        },\n        {\n            \"type\": \"EditText\",\n            \"label\": \"请输入目的地\",\n            \"id\": 10030\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"重新设置家的位置\",\n            \"id\": 10050\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"添加公司位置\",\n            \"id\": 10056\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"打开收藏夹\",\n            \"id\": 10062\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"充电站\",\n            \"id\": 10098\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"美食\",\n            \"id\": 10034\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"洗手间\",\n            \"id\": 10089\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"停车场\",\n            \"id\": 10056\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"维修\",\n            \"id\": 10065\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"小鹏4S\",\n            \"id\": 10035\n        },\n        {\n            \"type\": \"Button\",\n            \"label\": \"智能洗车\",\n            \"id\": 10076\n        },\n        {\n            \"type\": \"RecycleView\",\n            \"dynamic\": true,\n            \"id\": 10056,\n            \"elements\": [\n                {\n                    \"type\": \"Button\",\n                    \"label\": \"维修\",\n                    \"id\": 10023\n                }\n            ]\n        }\n    ]\n}");
        String str = vuiSceneConvertToString(vuiScene);
        System.out.println(str);
    }

    public static String vuiFeedBackConvertToString(VuiFeedback feedBack) {
        if (feedBack == null) {
            return null;
        }
        Gson gson = new GsonBuilder().create();
        return gson.toJson(feedBack);
    }

    public static synchronized String vuiSceneConvertToString(VuiScene vuiScene) {
        synchronized (VuiUtils.class) {
            if (vuiScene == null) {
                return null;
            }
            Gson mGsonBuild = new GsonBuilder().registerTypeAdapter(Integer.class, new SceneIntegerTypeAdapter()).registerTypeAdapter(Boolean.class, new SceneBooleanTypeAdapter()).create();
            try {
                return mGsonBuild.toJson(vuiScene);
            } catch (Exception e) {
                LogUtils.e("VuiUtils", "vuiSceneConvertToString e:" + e.toString());
                return null;
            }
        }
    }

    public static synchronized String vuiUpdateSceneConvertToString(VuiScene vuiScene) {
        synchronized (VuiUtils.class) {
            if (vuiScene == null) {
                return null;
            }
            Gson mGsonExclsionBuild = new GsonBuilder().registerTypeAdapter(Integer.class, new SceneIntegerTypeAdapter()).registerTypeAdapter(Boolean.class, new SceneBooleanTypeAdapter()).setExclusionStrategies(mExclusionStrategy).create();
            try {
                return mGsonExclsionBuild.toJson(vuiScene);
            } catch (Exception e) {
                LogUtils.e("VuiUtils", "vuiSceneConvertToString e:" + e.toString());
                return null;
            }
        }
    }

    public static void generateElementValueJSON(JSONObject actionObjs, String action, Object value) {
        try {
            JSONObject valueObj = new JSONObject();
            valueObj.put(VuiConstants.ELEMENT_VALUE, value);
            actionObjs.put(action, valueObj);
        } catch (Exception e) {
            LogUtils.e("VuiUtils", "generateElementValueJSON e:" + e.getMessage());
        }
    }

    public static String vuiElementGroupConvertToString(List<VuiElement> elements) {
        if (elements == null) {
            return null;
        }
        Gson mGsonExclsionBuild = new GsonBuilder().registerTypeAdapter(Integer.class, new SceneIntegerTypeAdapter()).registerTypeAdapter(Boolean.class, new SceneBooleanTypeAdapter()).setExclusionStrategies(mExclusionStrategy).create();
        return mGsonExclsionBuild.toJson(elements);
    }

    public static VuiScene stringConvertToVuiScene(String jsonStr) {
        Gson mGson = new Gson();
        if (jsonStr == null) {
            return null;
        }
        return (VuiScene) mGson.fromJson(jsonStr, (Class<Object>) VuiScene.class);
    }

    public static VuiElement stringConvertToVuiElement(String jsonStr) {
        Gson mGson = new Gson();
        if (jsonStr == null) {
            return null;
        }
        return (VuiElement) mGson.fromJson(jsonStr, (Class<Object>) VuiElement.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SceneIntegerTypeAdapter extends TypeAdapter<Integer> {
        SceneIntegerTypeAdapter() {
        }

        @Override // com.google.gson.TypeAdapter
        public void write(JsonWriter jsonWriter, Integer integer) throws IOException {
            if (integer != null && integer.intValue() != -1) {
                jsonWriter.value(integer);
            } else {
                jsonWriter.value((Number) null);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.TypeAdapter
        public Integer read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SceneBooleanTypeAdapter extends TypeAdapter<Boolean> {
        SceneBooleanTypeAdapter() {
        }

        @Override // com.google.gson.TypeAdapter
        public void write(JsonWriter jsonWriter, Boolean b) throws IOException {
            if (b != null) {
                jsonWriter.value(b);
            } else {
                jsonWriter.value((Number) null);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.gson.TypeAdapter
        public Boolean read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }

    public static boolean isNumer(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        try {
            String bigStr = new BigDecimal(str).toString();
            Matcher isNum = pattern.matcher(bigStr);
            if (!isNum.matches()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static <T> T getValueByName(VuiElement vuiElement, String name) {
        Map<String, Object> value;
        Gson mGson = new Gson();
        LogUtils.logDebug("getEventValueByName", "name:" + name + "," + mGson.toJson(vuiElement));
        if (vuiElement == null || vuiElement.getResultActions() == null || vuiElement.getResultActions().isEmpty()) {
            return null;
        }
        String key = vuiElement.getResultActions().get(0);
        if ((vuiElement.getValues() instanceof LinkedTreeMap) && (value = (Map) vuiElement.getValues()) != null) {
            if (value.get(key) instanceof LinkedTreeMap) {
                Map<String, Object> insideValue = (Map) value.get(key);
                if (insideValue != null && insideValue.containsKey(name) && insideValue.get(name) != null) {
                    return (T) insideValue.get(name);
                }
            } else if (value.get(name) != null) {
                return (T) value.get(name);
            }
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:60:0x012c  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0138  */
    /* JADX WARN: Removed duplicated region for block: B:77:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void setStatefulButtonAttr(android.view.View r16, int r17, java.lang.String[] r18, java.lang.String r19, boolean r20) {
        /*
            Method dump skipped, instructions count: 335
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.utils.VuiUtils.setStatefulButtonAttr(android.view.View, int, java.lang.String[], java.lang.String, boolean):void");
    }

    public static void setStatefulButtonAttr(View view, int currIndex, String[] vuilabels, String action) {
        setStatefulButtonAttr(view, currIndex, vuilabels, action, false);
    }

    public static void setStatefulButtonAttr(View view, int currIndex, String[] vuilabels) {
        setStatefulButtonAttr(view, currIndex, vuilabels, null, false);
    }

    public static JSONObject createStatefulButtonData(int currIndex, String[] vuilabels, JSONObject props) {
        if (vuilabels != null && vuilabels.length != 0 && currIndex >= 0 && currIndex <= vuilabels.length - 1) {
            JSONArray states = new JSONArray();
            try {
                String[] stateNames = new String[vuilabels.length];
                for (int i = 0; i < vuilabels.length; i++) {
                    JSONObject state = new JSONObject();
                    String stateName = "state_" + (i + 1);
                    stateNames[i] = stateName;
                    state.put(stateName, vuilabels[i]);
                    states.put(state);
                }
                props.put("states", states);
                props.put("curState", stateNames[currIndex]);
                return props;
            } catch (JSONException var8) {
                var8.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static JSONObject createStatefulButtonData(int currIndex, String[] vuilabels, JSONObject props, View view) {
        if (vuilabels != null && vuilabels.length != 0 && (view instanceof IVuiElement) && currIndex >= 0 && currIndex <= vuilabels.length - 1) {
            JSONArray states = new JSONArray();
            try {
                String[] stateNames = new String[vuilabels.length];
                for (int i = 0; i < vuilabels.length; i++) {
                    JSONObject state = new JSONObject();
                    String stateName = "state_" + (i + 1);
                    stateNames[i] = stateName;
                    state.put(stateName, vuilabels[i]);
                    states.put(state);
                }
                props.put("states", states);
                ((IVuiElement) view).setVuiValue(stateNames[currIndex], view);
                return props;
            } catch (JSONException var8) {
                var8.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void setStatefulButtonValue(View view, int currIndex) {
        if (view instanceof IVuiElement) {
            ((IVuiElement) view).setVuiValue("state_" + (currIndex + 1), view);
        }
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label, String action) {
        return generateCommonVuiElement(id, type, label, action, false, VuiPriority.LEVEL3);
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label, String action, boolean isLayoutLoadable, VuiPriority priority) {
        VuiElement vuiElement = new VuiElement.Builder().id(id).type(type.getType()).label(label).action(action).timestamp(System.currentTimeMillis()).build();
        if (isLayoutLoadable) {
            vuiElement.setLayoutLoadable(Boolean.valueOf(isLayoutLoadable));
        }
        if (VuiElementType.RECYCLEVIEW.getType().equals(type.getType())) {
            vuiElement.setLayoutLoadable(true);
        }
        if (VuiPriority.LEVEL3.getLevel() != priority.getLevel()) {
            vuiElement.setPriority(priority.getLevel());
        }
        return vuiElement;
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label, String action, boolean isLayoutLoadable, VuiPriority priority, int listType) {
        VuiElement vuiElement = new VuiElement.Builder().id(id).type(type.getType()).label(label).action(action).timestamp(System.currentTimeMillis()).build();
        if (isLayoutLoadable) {
            vuiElement.setLayoutLoadable(Boolean.valueOf(isLayoutLoadable));
        }
        if (VuiElementType.RECYCLEVIEW.getType().equals(type.getType())) {
            vuiElement.setLayoutLoadable(true);
            if (LIST_VEDIO_TYPE == listType) {
                JsonObject obj = new JsonObject();
                new HashMap();
                obj.addProperty("matchedFirstPriority", (Boolean) true);
                obj.addProperty("firstPriority", (Boolean) true);
                obj.addProperty("listType", "videoList");
                vuiElement.setProps(obj);
            }
        }
        if (VuiPriority.LEVEL3.getLevel() != priority.getLevel()) {
            vuiElement.setPriority(priority.getLevel());
        }
        return vuiElement;
    }

    public static VuiElement generateCommonVuiElement(int id, VuiElementType type, String label, String action) {
        return generateCommonVuiElement("" + id, type, label, action);
    }

    public static VuiElement generateCommonVuiElement(int id, VuiElementType type, String label) {
        return generateCommonVuiElement("" + id, type, label, (String) null);
    }

    public static VuiElement generateCommonVuiElement(String id, VuiElementType type, String label) {
        return generateCommonVuiElement(id, type, label, (String) null);
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, int id) {
        String name = VuiAction.SETVALUE.getName();
        return generateStatefulButtonElement(currIndex, vuilabels, name, "" + id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String id) {
        return generateStatefulButtonElement(currIndex, vuilabels, VuiAction.SETVALUE.getName(), id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String action, String id) {
        return generateStatefulButtonElement(currIndex, vuilabels, action, id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String action, int id) {
        return generateStatefulButtonElement(currIndex, vuilabels, action, "" + id, "");
    }

    public static VuiElement generateStatefulButtonElement(int currIndex, String[] vuilabels, String action, String id, String label) {
        if (action == null) {
            action = VuiAction.SETVALUE.getName();
        }
        long time = System.currentTimeMillis();
        VuiElement element = new VuiElement.Builder().id(id).type(VuiElementType.STATEFULBUTTON.getType()).label(label).timestamp(time).action(action).build();
        List<VuiElement> elements = new ArrayList<>();
        for (int i = 0; i < vuilabels.length; i++) {
            String[] value_lbs = TextUtils.isEmpty(vuilabels[i]) ? null : vuilabels[i].split("-");
            if (value_lbs != null) {
                VuiElement.Builder builder = new VuiElement.Builder();
                VuiElement childEle = builder.id(id + "_state_" + i).type(VuiElementType.STATE.getType()).label(value_lbs[0]).timestamp(time).build();
                if (value_lbs.length > 1) {
                    childEle.setValues(value_lbs[1]);
                } else {
                    childEle.setValues(value_lbs[0]);
                }
                elements.add(childEle);
            }
        }
        setStatefulButtonValues(currIndex, vuilabels, element);
        if (!elements.isEmpty()) {
            element.setElements(elements);
        }
        return element;
    }

    public static void setStatefulButtonValues(int currIndex, String[] vuilabels, VuiElement element) {
        Gson mGson = new Gson();
        if (element.getActions() != null && currIndex >= 0 && currIndex < vuilabels.length) {
            List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(element.actions.entrySet());
            JSONObject actionObj = new JSONObject();
            for (int i = 0; i < entries.size(); i++) {
                String actionKey = entries.get(i).getKey();
                if (VuiAction.SETVALUE.getName().equalsIgnoreCase(actionKey)) {
                    String[] value_lbs = TextUtils.isEmpty(vuilabels[currIndex]) ? null : vuilabels[currIndex].split("-");
                    if (value_lbs != null && value_lbs.length >= 1) {
                        generateElementValueJSON(actionObj, "SetValue", value_lbs[0]);
                    }
                }
            }
            element.setValues(mGson.fromJson(actionObj.toString(), (Class<Object>) JsonObject.class));
        }
    }

    public static boolean canUseVuiFeature() {
        return platformIsSupport() && !userDisabledFeature;
    }

    public static boolean platformIsSupport() {
        if (isOverseasVersion()) {
            return false;
        }
        String type = getCarPlatForm();
        if (supportType.contains(type)) {
            return TextUtils.isEmpty(type) && Utils.checkApkExist("com.xiaopeng.speechtesttools");
        }
        return true;
    }

    public static boolean is3DUIPlatForm() {
        String type = getCarPlatForm();
        return support3DPlatform.contains(type);
    }

    public static boolean isOverseasVersion() {
        if (sRegion == null) {
            sRegion = getVersionInCountryCode();
            LogUtils.i("VuiUtils", "CountryCode:" + sRegion);
        }
        if (TextUtils.isEmpty(sRegion)) {
            return false;
        }
        return "EU".equals(sRegion.toUpperCase());
    }

    public static String getVersionInCountryCode() {
        String versionFinger = SystemProperties.get("ro.xiaopeng.software", "");
        if (!"".equals(versionFinger) && versionFinger != null) {
            String countryCode = versionFinger.substring(7, 9);
            return countryCode;
        }
        return versionFinger;
    }

    public static boolean cannotUpload() {
        if (isFeatureDisabled()) {
            LogUtils.d("VuiUtils", "canUseVuiFeature():" + canUseVuiFeature() + ",isFeatureDisabled:" + isFeatureDisabled());
        }
        return !canUseVuiFeature() || isFeatureDisabled();
    }

    public static boolean isFeatureDisabled() {
        return isDisableVuiFeature;
    }

    public static void disableVuiFeature() {
        isDisableVuiFeature = true;
    }

    public static void enableVuiFeature() {
        isDisableVuiFeature = false;
    }

    public static void userSetFeatureState(boolean isDisable) {
        userDisabledFeature = isDisable;
    }

    public static String getResourceName(int id) {
        int index;
        String resourceName = null;
        try {
            resourceName = Foo.getContext().getResources().getResourceName(id);
            if (!TextUtils.isEmpty(resourceName) && (index = resourceName.indexOf(NavigationBarInflaterView.KEY_IMAGE_DELIM)) != -1) {
                return resourceName.substring(index + 1);
            }
            return resourceName;
        } catch (Exception e) {
            return resourceName;
        }
    }

    public static void userDisableViewMode() {
        viewMode = VuiMode.DISABLED;
    }

    public static VuiMode getViewMode() {
        return viewMode;
    }

    public static SoftReference<View> findRecyclerView(SoftReference<View> rootView) {
        Queue<SoftReference<View>> queue = new LinkedList<>();
        queue.offer(rootView);
        while (!queue.isEmpty()) {
            SoftReference<View> view = queue.poll();
            if (view != null && (view.get() instanceof RecyclerView)) {
                return view;
            }
            if (view != null && (view.get() instanceof ViewGroup)) {
                SoftReference<ViewGroup> group = new SoftReference<>((ViewGroup) view.get());
                for (int i = 0; group.get() != null && i < group.get().getChildCount(); i++) {
                    SoftReference<View> child = new SoftReference<>(group.get().getChildAt(i));
                    if (child.get() instanceof ViewGroup) {
                        queue.offer(child);
                    }
                }
            }
        }
        return null;
    }

    public static View findChildRecyclerView(RecyclerView recyclerView) {
        if (recyclerView != null) {
            Queue<View> queue = new LinkedList<>();
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                queue.offer(recyclerView.getChildAt(i));
            }
            while (!queue.isEmpty()) {
                View view = queue.poll();
                if (view instanceof RecyclerView) {
                    return view;
                }
                if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int i2 = 0; i2 < group.getChildCount(); i2++) {
                        View child = group.getChildAt(i2);
                        if (child instanceof ViewGroup) {
                            queue.offer(child);
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }

    public static int getExtraPage(VuiElement vui) {
        JsonObject props;
        if (vui.getProps() != null && (props = vui.getProps()) != null && props.has("extraPage")) {
            return props.get("extraPage").getAsInt();
        }
        return -1;
    }

    public static ViewPager findViewPager(View rootView) {
        Queue<View> queue = new LinkedList<>();
        queue.offer(rootView);
        while (!queue.isEmpty()) {
            View view = queue.poll();
            if (view instanceof ViewPager) {
                return (ViewPager) view;
            }
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof ViewGroup) {
                        queue.offer(child);
                    }
                }
            }
        }
        return null;
    }

    public static String getPackageNameFromSceneId(String sceneId) {
        if (TextUtils.isEmpty(sceneId)) {
            return null;
        }
        if (sceneId.contains("-")) {
            String packageName = sceneId.split("-")[0];
            return packageName;
        } else if (!sceneId.contains("_")) {
            return null;
        } else {
            String packageName2 = sceneId.split("_")[0];
            return packageName2;
        }
    }

    public static boolean isThirdApp(String packageName) {
        if ("com.android.systemui".equals(packageName)) {
            return false;
        }
        return TextUtils.isEmpty(packageName) || !packageName.startsWith("com.xiaopeng") || "com.xiaopeng.xpdemo".equals(packageName) || "com.xiaopeng.VuiDemo".equals(packageName) || "com.xiaopeng.napa".equals(packageName);
    }

    public static void setValueAttribute(View view, VuiElement element) {
        Gson mGson = new Gson();
        if (view == null || element == null || !(view instanceof IVuiElement)) {
            return;
        }
        String type = element.getType();
        JSONObject actionObj = new JSONObject();
        if (type != null && type.equals(VuiElementType.RADIOBUTTON.getType())) {
            setPropsEvent(view, element, actionObj);
        } else if (type != null && type.equals(VuiElementType.CHECKBOX.getType())) {
            setPropsEvent(view, element, actionObj);
        } else if (type != null && type.equals(VuiElementType.SWITCH.getType())) {
            setPropsEvent(view, element, actionObj);
        } else if (type != null && type.equals(VuiElementType.SEEKBAR.getType())) {
            if (view instanceof ProgressBar) {
                generateElementValueJSON(actionObj, VuiAction.SETVALUE.getName(), Integer.valueOf(((ProgressBar) view).getProgress()));
            }
            element.setValues(mGson.fromJson(actionObj.toString(), (Class<Object>) JsonObject.class));
        } else if (type != null && type.equals(VuiElementType.XSLIDER.getType())) {
            Object obj = null;
            try {
                if (view instanceof IVuiElement) {
                    obj = ((IVuiElement) view).getVuiValue();
                }
            } catch (Throwable th) {
                try {
                    LogUtils.e("xpui version is not correct");
                    if (0 == 0) {
                        obj = getReflexMethod(view, "getIndicatorValue");
                    }
                    if (obj == null || !(obj instanceof Float)) {
                        return;
                    }
                } finally {
                    if (0 == 0) {
                        obj = getReflexMethod(view, "getIndicatorValue");
                    }
                    if (obj != null && (obj instanceof Float)) {
                        generateElementValueJSON(actionObj, VuiAction.SETVALUE.getName(), Integer.valueOf(((Float) obj).intValue()));
                        element.setValues(mGson.fromJson(actionObj.toString(), (Class<Object>) JsonObject.class));
                    }
                }
            }
        } else if (type != null && type.equals(VuiElementType.XTABLAYOUT.getType())) {
            Object obj2 = null;
            try {
                if (view instanceof IVuiElement) {
                    obj2 = ((IVuiElement) view).getVuiValue();
                }
            } catch (Throwable th2) {
                try {
                    LogUtils.w("xpui version is not correct");
                    if (0 == 0) {
                        obj2 = getReflexMethod(view, "getSelectedTabIndex");
                    }
                    if (obj2 == null || !(obj2 instanceof Integer)) {
                        return;
                    }
                } finally {
                    if (0 == 0) {
                        obj2 = getReflexMethod(view, "getSelectedTabIndex");
                    }
                    if (obj2 != null && (obj2 instanceof Integer)) {
                        generateElementValueJSON(actionObj, VuiAction.SELECTTAB.getName(), Integer.valueOf(((Integer) obj2).intValue()));
                        element.setValues(mGson.fromJson(actionObj.toString(), (Class<Object>) JsonObject.class));
                    }
                }
            }
        }
    }

    private static void setPropsEvent(View view, VuiElement element, JSONObject actionObj) {
        boolean isChecked = false;
        Gson mGson = new Gson();
        if (view != null && (view instanceof CompoundButton)) {
            isChecked = ((CompoundButton) view).isChecked();
        } else if (view != null && (view instanceof Checkable)) {
            isChecked = ((Checkable) view).isChecked();
        } else if (view != null) {
            Object value = ((IVuiElement) view).getVuiValue();
            if (value != null && (value instanceof Boolean)) {
                isChecked = ((Boolean) value).booleanValue();
            } else {
                isChecked = view.isSelected();
            }
        }
        generateElementValueJSON(actionObj, VuiAction.SETCHECK.getName(), Boolean.valueOf(isChecked));
        setVuiPriority((IVuiElement) view, element, isChecked);
        element.setValues(mGson.fromJson(actionObj.toString(), (Class<Object>) JsonObject.class));
    }

    private static void setVuiPriority(IVuiElement vuiElement, VuiElement element, boolean isChecked) {
        if (vuiElement != null && element != null) {
            try {
                JSONObject props = vuiElement.getVuiProps();
                if (props != null && props.has(VuiConstants.PROPS_DEFAULTPRIORITY)) {
                    if (isChecked) {
                        element.setPriority(VuiPriority.LEVEL1.getLevel());
                    } else {
                        element.setPriority(props.getInt(VuiConstants.PROPS_DEFAULTPRIORITY));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static Object getReflexMethod(View view, String methodName) {
        Method method;
        if (view == null || (method = getDeclaredMethod(view, methodName, null)) == null) {
            return null;
        }
        try {
            method.setAccessible(true);
            return method.invoke(view, null);
        } catch (Exception e) {
            LogUtils.e("VuiUtils", "getReflexMethod:" + e.getMessage());
            return null;
        }
    }

    private static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (Exception e) {
            }
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public static void addScrollProps(VuiElement element, View view) {
        if (element != null) {
            try {
                JsonObject props = element.getProps();
                if (props == null) {
                    props = new JsonObject();
                }
                if (view instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (element.getActions() != null) {
                        List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(element.actions.entrySet());
                        if (!entries.isEmpty()) {
                            if (VuiAction.SCROLLBYY.getName().equals(entries.get(0).getKey())) {
                                props.addProperty(VuiConstants.PROPS_SCROLLUP, Boolean.valueOf(recyclerView.canScrollVertically(-1)));
                                props.addProperty(VuiConstants.PROPS_SCROLLDOWN, Boolean.valueOf(recyclerView.canScrollVertically(1)));
                            } else if (VuiAction.SCROLLBYX.getName().equals(entries.get(0).getKey())) {
                                props.addProperty(VuiConstants.PROPS_SCROLLRIGHT, Boolean.valueOf(recyclerView.canScrollVertically(1)));
                                props.addProperty(VuiConstants.PROPS_SCROLLLEFT, Boolean.valueOf(recyclerView.canScrollVertically(-1)));
                            }
                        }
                    }
                }
                element.setProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addProps(VuiElement element, Map<String, Boolean> propsMap) {
        if (element == null || propsMap == null) {
            return;
        }
        try {
            if (propsMap.size() > 0) {
                JsonObject props = element.getProps();
                if (props == null) {
                    props = new JsonObject();
                }
                List<Map.Entry<String, Boolean>> entries = new ArrayList<>(propsMap.entrySet());
                for (int i = 0; i < entries.size(); i++) {
                    Map.Entry<String, Boolean> entry = entries.get(i);
                    props.addProperty(entry.getKey(), entry.getValue());
                }
                element.setProps(props);
            }
        } catch (Exception e) {
        }
    }

    public static void addHasFeedbackProp(IVuiElement vuiView) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_FEEDBACK, true);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addCanVoiceControlProp(IVuiElement vuiView) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_VOICECONTROL, true);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addGeneralActProp(IVuiElement vuiView, String generalAct) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_GENERALACT, generalAct);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addUnitProp(IVuiElement vuiView, String unit) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_UNIT, unit);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addUnsupportProp(IVuiElement vuiView) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_UNSUPPORTED, true);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addDisableTplProp(IVuiElement vuiView) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_DISABLETPL, true);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addMatchFirstProp(IVuiElement vuiView) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_MATCHFIRSTPRIORITY, true);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addSKipAlreadyProp(IVuiElement vuiView) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_SKIPALREADY, true);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addDefaultPriorityValue(IVuiElement vuiView, int value) {
        if (vuiView != null) {
            try {
                JSONObject props = vuiView.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                props.put(VuiConstants.PROPS_DEFAULTPRIORITY, value);
                vuiView.setVuiProps(props);
            } catch (Exception e) {
            }
        }
    }

    public static void addProps(IVuiElement element, Map<String, Object> propsMap) {
        if (element == null || propsMap == null) {
            return;
        }
        try {
            if (propsMap.size() > 0) {
                JSONObject props = element.getVuiProps();
                if (props == null) {
                    props = new JSONObject();
                }
                List<Map.Entry<String, Object>> entries = new ArrayList<>(propsMap.entrySet());
                for (int i = 0; i < entries.size(); i++) {
                    Map.Entry<String, Object> entry = entries.get(i);
                    props.put(entry.getKey(), entry.getValue());
                }
                element.setVuiProps(props);
            }
        } catch (Exception e) {
        }
    }

    public static void setRecyclerViewItemVuiAttr(IVuiElement vuiElement, int position, String label) {
        setRecyclerViewItemVuiAttr(vuiElement, position, label, null, null);
    }

    public static void setRecyclerViewItemVuiAttr(IVuiElement vuiElement, int position, String label, VuiElementType type) {
        setRecyclerViewItemVuiAttr(vuiElement, position, label, type, null);
    }

    public static void setRecyclerViewItemVuiAttr(IVuiElement vuiElement, int position, String label, VuiElementType type, String tag) {
        if (!TextUtils.isEmpty(tag)) {
            VuiEngine.getInstance(Foo.getContext()).setVuiElementTag((View) vuiElement, tag + "_" + position);
        } else {
            vuiElement.setVuiElementId(((View) vuiElement).getId() + "_" + position);
        }
        vuiElement.setVuiPosition(position);
        if (TextUtils.isEmpty(label)) {
            vuiElement.setVuiLabel(label);
        }
        vuiElement.setVuiPosition(position);
        if (type != null) {
            vuiElement.setVuiElementType(type);
        }
    }

    public static String getCarPlatForm() {
        String str = sType;
        if (str != null) {
            return str;
        }
        sType = getXpCduType();
        return sType;
    }

    public static String getXpCduType() {
        String versionFinger = SystemProperties.get("ro.xiaopeng.software", "");
        if ("".equals(versionFinger)) {
            return versionFinger;
        }
        String xpCduType = versionFinger.substring(5, 7);
        return xpCduType;
    }

    public static boolean isPerformVuiAction(View view) {
        if (view instanceof IVuiElement) {
            IVuiElement vuiElement = (IVuiElement) view;
            return vuiElement.isPerformVuiAction();
        }
        return false;
    }
}
