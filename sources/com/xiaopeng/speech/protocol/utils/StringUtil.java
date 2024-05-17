package com.xiaopeng.speech.protocol.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import com.bumptech.glide.load.Key;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class StringUtil {
    private static final String TAG = "AIOS-Adapter-StringUtil";

    public static void highNightWords(Context context, TextView textView, int colorId, String complete, String... words) {
        if (complete == null) {
            complete = "";
        }
        Log.i(TAG, "完整字符串：" + complete);
        SpannableStringBuilder builder = new SpannableStringBuilder(complete);
        if (words != null) {
            for (String word : words) {
                Log.i(TAG, "需要高亮的字符串：" + word);
                if (!TextUtils.isEmpty(word) && complete.contains(word)) {
                    ForegroundColorSpan highNightSpan = new ForegroundColorSpan(context.getResources().getColor(colorId));
                    int index = complete.indexOf(word);
                    builder.setSpan(highNightSpan, index, word.length() + index, 34);
                }
            }
        }
        textView.setText(builder);
    }

    public static String highNightWords(Context context, int colorId, String complete, String... words) {
        if (complete == null) {
            complete = "";
        }
        Log.i(TAG, "完整字符串：" + complete);
        SpannableStringBuilder builder = new SpannableStringBuilder(complete);
        if (words != null) {
            for (String word : words) {
                Log.i(TAG, "需要高亮的字符串：" + word);
                if (!TextUtils.isEmpty(word) && complete.contains(word)) {
                    ForegroundColorSpan highNightSpan = new ForegroundColorSpan(context.getResources().getColor(colorId));
                    int index = complete.indexOf(word);
                    builder.setSpan(highNightSpan, index, word.length() + index, 34);
                }
            }
        }
        return builder.toString();
    }

    public static String getEncodedString(byte[] bytes) {
        String result = "";
        try {
            result = new String(bytes, Key.STRING_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        }
        return result.trim();
    }

    public static boolean isDecimalNumber(String match) {
        if (match == null || match.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile("([+|-])?(([0-9]+.[0-9]*)|([0-9]*.[0-9]+)|([0-9]+))((e|E)([+|-])?[0-9]+)?");
        return pattern.matcher(match).matches();
    }

    public static JSONObject joinJSONObject(@NonNull JSONObject source, @NonNull JSONObject append) {
        Iterator iterator = append.keys();
        Log.d(TAG, "Before join:" + source.toString());
        while (iterator.hasNext()) {
            try {
                String itemName = iterator.next();
                Object object = append.get(itemName);
                source.put(itemName, object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "After join:" + source.toString());
        return source;
    }
}
