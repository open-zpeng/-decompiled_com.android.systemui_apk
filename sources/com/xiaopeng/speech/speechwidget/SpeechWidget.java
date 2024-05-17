package com.xiaopeng.speech.speechwidget;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SpeechWidget {
    public static final String DATA_SOURCE_API = "api";
    public static final String DATA_SOURCE_MATCH = "partialMatch";
    public static final String DATA_SOURCE_SELECT = "selected";
    public static final String TYPE_CARD = "card";
    public static final String TYPE_CONTENT = "content";
    public static final String TYPE_CUSTOM = "custom";
    public static final String TYPE_LIST = "list";
    public static final String TYPE_MEDIA = "media";
    public static final String TYPE_SEARCH = "search";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_WEB = "web";
    protected static final String WIDGET_CONTENT = "content";
    protected static final String WIDGET_COUNT = "count";
    public static final String WIDGET_DATA_SOURCE = "dataSource";
    public static final String WIDGET_EXTRA = "extra";
    protected static final String WIDGET_EXTRA_TYPE = "extraType";
    public static final String WIDGET_IMAGEURL = "imageUrl";
    public static final String WIDGET_LABEL = "label";
    public static final String WIDGET_LINKURL = "linkUrl";
    public static final String WIDGET_SEARCH_CONTENT = "searchContent";
    public static final String WIDGET_SUBTITLE = "subTitle";
    public static final String WIDGET_TEXT = "text";
    public static final String WIDGET_TITLE = "title";
    protected static final String WIDGET_TYPE = "type";
    protected static final String WIDGET_TYPE_EX = "duiWidget";
    protected static final String WIDGET_UNIQUE_ID = "widgetId";
    public static final String WIDGET_URL = "url";
    protected JSONObject mWidget = new JSONObject();
    protected JSONObject mExtra = new JSONObject();
    protected List<SpeechWidget> mList = new ArrayList();

    public static String getType(JSONObject jsonObject) {
        return jsonObject.optString("type");
    }

    public SpeechWidget() {
    }

    public SpeechWidget(String type) {
        try {
            this.mWidget.put("type", type);
            this.mWidget.put(WIDGET_TYPE_EX, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SpeechWidget fromJson(String data) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fromJson(jsonObject);
    }

    public SpeechWidget fromJson(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.mWidget = jsonObject;
            JSONObject extraObj = jsonObject.optJSONObject(WIDGET_EXTRA);
            if (extraObj != null) {
                this.mExtra = extraObj;
            }
        }
        return this;
    }

    public SpeechWidget setType(String type) {
        try {
            this.mWidget.put("type", type);
            this.mWidget.put(WIDGET_TYPE_EX, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getType() {
        return this.mWidget.optString("type", "");
    }

    public SpeechWidget setExtraType(String type) {
        return addExtra(WIDGET_EXTRA_TYPE, type);
    }

    public String getExtraType() {
        return getExtra(WIDGET_EXTRA_TYPE);
    }

    public SpeechWidget addContent(String key, String value) {
        try {
            this.mWidget.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getContent(String key) {
        return this.mWidget.optString(key, "");
    }

    public int getIntContent(String key) {
        try {
            int val = Integer.parseInt(this.mWidget.optString(key, ""));
            return val;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public SpeechWidget addWidget(SpeechWidget widget) {
        this.mList.add(widget);
        return this;
    }

    public SpeechWidget addExtra(String key, String value) {
        try {
            this.mExtra.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SpeechWidget setExtra(JSONObject jsonObject) {
        this.mExtra = jsonObject;
        return this;
    }

    public JSONObject getExtra() {
        return this.mExtra;
    }

    public String getExtra(String key) {
        return this.mExtra.optString(key, "");
    }

    public String getDataSource() {
        return this.mWidget.optString(WIDGET_DATA_SOURCE);
    }

    public List<SpeechWidget> getList() {
        return this.mList;
    }

    public String toString() {
        if (this.mExtra.length() > 0) {
            try {
                this.mWidget.put(WIDGET_EXTRA, this.mExtra);
            } catch (JSONException var6) {
                var6.printStackTrace();
            }
        }
        String type = this.mWidget.optString("type", "");
        if (TYPE_LIST.equals(type) || "media".equals(type)) {
            try {
                JSONArray content = new JSONArray();
                for (SpeechWidget widget : this.mList) {
                    content.put(new JSONObject(widget.toString()));
                }
                this.mWidget.put(WIDGET_COUNT, content.length());
                this.mWidget.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this.mWidget.toString();
    }

    public SpeechWidget setWidgetId(String id) {
        try {
            this.mWidget.put(WIDGET_UNIQUE_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getWidgetId() {
        return this.mWidget.optString(WIDGET_UNIQUE_ID, "");
    }
}
