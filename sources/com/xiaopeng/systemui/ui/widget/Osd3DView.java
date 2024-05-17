package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import com.android.launcher3.icons.cache.BaseIconCache;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import com.xiaopeng.systemui.infoflow.widget.RoundedDrawable;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class Osd3DView implements IOsdView {
    private Context mContext = ContextUtils.getContext();

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void dispatchConfigurationChanged(Configuration newConfig) {
    }

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void showOsd(OsdController.OsdParams params) {
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(VuiConstants.ELEMENT_TYPE, params.mType);
            jsonParams.put("streamType", params.mStreamType);
            Drawable icon = params.mIcon != null ? params.mIcon.loadDrawable(this.mContext) : null;
            if (icon != null) {
                jsonParams.put(BaseIconCache.IconDB.COLUMN_ICON, ImageUtil.getBase64String(RoundedDrawable.drawableToBitmap(icon)));
            }
            jsonParams.put(SpeechWidget.WIDGET_TITLE, params.mTitle);
            jsonParams.put("content", params.mContent);
            jsonParams.put("progress", params.mProgress);
            jsonParams.put("progressMin", params.mProgressMin);
            jsonParams.put("progressMax", params.mProgressMax);
            map.put("params", jsonParams.toString());
            SystemUIMediatorUtil.systemUIMediatorApiRouterCall("showOsd", map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.ui.widget.IOsdView
    public void showOsd(boolean show) {
        Map<String, Object> map = new HashMap<>();
        map.put("show", Boolean.valueOf(show));
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("setOsdVisibility", map);
    }
}
