package com.xiaopeng.speech.speechwidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ListWidget extends SpeechWidget {
    public static final String EXTRA_TYPE_AUDIO = "audio";
    public static final String EXTRA_TYPE_NAVI = "navi";
    public static final String EXTRA_TYPE_NAVI_ROUTE = "navi_route";
    public static final String EXTRA_TYPE_PHONE = "phone";
    public static final String KEY_STATUS = "status";
    public static final String STATUS_EXISTS = "1";
    public static final String STATUS_NOT_FOUND = "0";

    public ListWidget(String type) {
        super(type);
    }

    public ListWidget() {
        super(SpeechWidget.TYPE_LIST);
    }

    @Override // com.xiaopeng.speech.speechwidget.SpeechWidget
    public ListWidget fromJson(String data) {
        super.fromJson(data);
        JSONArray jsonArray = this.mWidget.optJSONArray("content");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                ContentWidget speechWidget = new ContentWidget();
                try {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    speechWidget.fromJson(jsonObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.mList.add(speechWidget);
            }
        }
        return this;
    }

    public ListWidget setTitle(String title) {
        addExtra(SpeechWidget.WIDGET_TITLE, title);
        return this;
    }

    public String getTitle() {
        return getExtra(SpeechWidget.WIDGET_TITLE);
    }

    public int getCurrentPage() {
        return getIntContent("currentPage") - 1;
    }

    public int getPageSize() {
        return getIntContent("itemsPerPage");
    }

    public ListWidget addContentWidget(ContentWidget var1) {
        return (ListWidget) super.addWidget(var1);
    }

    @Override // com.xiaopeng.speech.speechwidget.SpeechWidget
    public ListWidget addExtra(String var1, String var2) {
        return (ListWidget) super.addExtra(var1, var2);
    }

    public ListWidget setExist(boolean exist) {
        return (ListWidget) super.addContent("status", exist ? "1" : "0");
    }
}
