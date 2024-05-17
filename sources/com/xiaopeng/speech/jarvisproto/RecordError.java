package com.xiaopeng.speech.jarvisproto;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class RecordError extends JarvisProto {
    public static final String ERROR_CREATE_FILE = "error_create_file";
    public static final String ERROR_EMPTY_FILE_PATH = "error_empty_file_path";
    public static final String ERROR_MAX_LENGTH = "error_max_length";
    public static final String EVENT = "jarvis.record.error";
    public String msg;

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg", this.msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
