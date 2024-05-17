package com.xiaopeng.xui.vui.utils;

import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XCheckBox;
import com.xiaopeng.xui.widget.XEditText;
import com.xiaopeng.xui.widget.XGroupHeader;
import com.xiaopeng.xui.widget.XImageButton;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XProgressBar;
import com.xiaopeng.xui.widget.XRadioButton;
import com.xiaopeng.xui.widget.XRadioGroup;
import com.xiaopeng.xui.widget.XRecyclerView;
import com.xiaopeng.xui.widget.XScrollView;
import com.xiaopeng.xui.widget.XSeekBar;
import com.xiaopeng.xui.widget.XSwitch;
import com.xiaopeng.xui.widget.XTabLayout;
import com.xiaopeng.xui.widget.XTextView;
import com.xiaopeng.xui.widget.XViewPager;
import com.xiaopeng.xui.widget.slider.XSlider;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@Deprecated
/* loaded from: classes25.dex */
public class VuiUtils {
    public static VuiElementType getElementType(Object view) {
        if (view instanceof XImageView) {
            return VuiElementType.IMAGEVIEW;
        }
        if (view instanceof XImageButton) {
            return VuiElementType.IMAGEBUTTON;
        }
        if (view instanceof XButton) {
            return VuiElementType.BUTTON;
        }
        if (view instanceof XTextView) {
            return VuiElementType.TEXTVIEW;
        }
        if (view instanceof XRadioButton) {
            return VuiElementType.RADIOBUTTON;
        }
        if (view instanceof XCheckBox) {
            return VuiElementType.CHECKBOX;
        }
        if (view instanceof XSwitch) {
            return VuiElementType.SWITCH;
        }
        if (view instanceof XRecyclerView) {
            return VuiElementType.RECYCLEVIEW;
        }
        if (view instanceof XProgressBar) {
            return VuiElementType.PROGRESSBAR;
        }
        if (view instanceof XScrollView) {
            return VuiElementType.SCROLLVIEW;
        }
        if (view instanceof XSlider) {
            return VuiElementType.XSLIDER;
        }
        if (view instanceof XTabLayout) {
            return VuiElementType.XTABLAYOUT;
        }
        if (view instanceof XRadioGroup) {
            return VuiElementType.RADIOGROUP;
        }
        if (view instanceof XEditText) {
            return VuiElementType.EDITTEXT;
        }
        if (view instanceof XGroupHeader) {
            return VuiElementType.XGROUPHEADER;
        }
        if (view instanceof XSeekBar) {
            return VuiElementType.SEEKBAR;
        }
        if (view instanceof XTimePicker) {
            return VuiElementType.TIMEPICKER;
        }
        if (view instanceof XViewPager) {
            return VuiElementType.VIEWPAGER;
        }
        if (view instanceof ViewGroup) {
            return VuiElementType.GROUP;
        }
        return VuiElementType.UNKNOWN;
    }

    @Deprecated
    public static boolean isPerformVuiAction(View view) {
        return isPerformVuiActionAndReset(view);
    }

    public static boolean isPerformVuiActionNonReset(View view) {
        if (view instanceof VuiView) {
            VuiView xViewVui = (VuiView) view;
            return xViewVui.isPerformVuiAction();
        }
        return false;
    }

    public static boolean isPerformVuiActionAndReset(View view) {
        if (view instanceof VuiView) {
            VuiView xViewVui = (VuiView) view;
            boolean isPerformVuiAction = xViewVui.isPerformVuiAction();
            xViewVui.setPerformVuiAction(false);
            return isPerformVuiAction;
        }
        return false;
    }

    public static void setStatefulButtonAttr(VuiView view, int currIndex, String[] vuilabels) {
        JSONObject props;
        if (view != null && (props = createStatefulButtonData(currIndex, vuilabels)) != null) {
            view.setVuiElementType(VuiElementType.STATEFULBUTTON);
            view.setVuiAction("SetValue");
            view.setVuiProps(props);
        }
    }

    public static JSONObject createStatefulButtonData(int currIndex, String[] vuilabels) {
        if (vuilabels != null && vuilabels.length != 0 && currIndex >= 0 && currIndex <= vuilabels.length - 1) {
            JSONObject props = new JSONObject();
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
            } catch (JSONException var9) {
                var9.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
