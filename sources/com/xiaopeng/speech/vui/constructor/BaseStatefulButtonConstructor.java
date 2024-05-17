package com.xiaopeng.speech.vui.constructor;

import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public abstract class BaseStatefulButtonConstructor implements IStatefulButtonConstructor {
    private String TAG = getClass().getSimpleName();
    private String[] stateNames;

    public void setStatefulButtonData(IVuiElement vuiFriendly, int currIndex, String... vuilabels) {
        if (vuiFriendly == null) {
            LogUtils.e(this.TAG, "vuiFriendly is null");
        } else if (vuilabels == null || vuilabels.length == 0) {
            LogUtils.e(this.TAG, "vuilabels  is empty");
        } else if (currIndex < 0 || currIndex > vuilabels.length - 1) {
            LogUtils.e(this.TAG, "currIndex 超过vuilabels数组边界");
        } else {
            JSONObject props = new JSONObject();
            JSONArray states = new JSONArray();
            try {
                this.stateNames = new String[vuilabels.length];
                for (int i = 0; i < vuilabels.length; i++) {
                    JSONObject state = new JSONObject();
                    String stateName = "state_" + (i + 1);
                    this.stateNames[i] = stateName;
                    state.put(stateName, vuilabels[i]);
                    states.put(state);
                }
                props.put("states", states);
                props.put("curState", this.stateNames[currIndex]);
                vuiFriendly.setVuiProps(props);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtils.e(this.TAG, "e:" + e.getMessage());
            }
        }
    }

    public void setStatefulButtonData(IVuiElement vuiFriendly, int currIndex, int minValue, int maxValue, float interval, String... vuilabels) {
        if (vuiFriendly == null) {
            LogUtils.e(this.TAG, "vuiFriendly is null");
        } else if (vuilabels == null || vuilabels.length == 0) {
            LogUtils.e(this.TAG, "vuilabels  is empty");
        } else if (currIndex < 0 || currIndex > vuilabels.length - 1) {
            LogUtils.e(this.TAG, "currIndex 超过vuilabels数组边界");
        } else {
            JSONObject props = new JSONObject();
            JSONArray states = new JSONArray();
            try {
                this.stateNames = new String[vuilabels.length];
                for (int i = 0; i < vuilabels.length; i++) {
                    JSONObject state = new JSONObject();
                    String stateName = "state_" + (i + 1);
                    this.stateNames[i] = stateName;
                    state.put(stateName, vuilabels[i]);
                    states.put(state);
                }
                props.put(VuiConstants.PROPS_MINVALUE, minValue);
                props.put(VuiConstants.PROPS_MAXVALUE, maxValue);
                props.put(VuiConstants.PROPS_INTERVAL, interval);
                props.put("states", states);
                props.put("curState", this.stateNames[currIndex]);
                vuiFriendly.setVuiProps(props);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtils.e(this.TAG, "e:" + e.getMessage());
            }
        }
    }
}
