package com.xiaopeng.libtheme;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.xiaopeng.libtheme.ThemeManager;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
/* loaded from: classes23.dex */
public class ThemeParser {
    private static final boolean DEBUG = false;
    private static final String NODE = "view";
    private static final String TAG = "ThemeXmlParser";
    private static final String _ATTR = "attr";
    private static final String _ID = "id";
    private static final String _ROOT = "root";
    private static final String _VALUE = "value";

    public static synchronized List<ThemeView> parseXml(Context context, String xml) {
        synchronized (ThemeParser.class) {
            if (context != null) {
                try {
                    if (!TextUtils.isEmpty(xml)) {
                        return parseXml(context, context.getAssets().open(xml));
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }
    }

    public static synchronized List<ThemeView> parseXml(Context context, InputStream is) {
        List<ThemeView> list;
        synchronized (ThemeParser.class) {
            list = new ArrayList<>();
            if (is != null) {
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(is, "utf-8");
                    ThemeView themeView = null;
                    for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                        String nodeName = parser.getName();
                        if (!TextUtils.isEmpty(nodeName)) {
                            if (eventType != 2) {
                                if (eventType == 3) {
                                    if (themeView != null) {
                                        list.add(themeView);
                                    }
                                    themeView = null;
                                }
                            } else if (NODE.equals(nodeName.toLowerCase())) {
                                String xmlId = parser.getAttributeValue(null, "id");
                                String xmlRoot = parser.getAttributeValue(null, _ROOT);
                                String xmlAttr = parser.getAttributeValue(null, _ATTR);
                                String xmlValue = parser.getAttributeValue(null, "value");
                                if (!TextUtils.isEmpty(xmlId) && !TextUtils.isEmpty(xmlAttr) && !TextUtils.isEmpty(xmlValue)) {
                                    ThemeView themeView2 = new ThemeView();
                                    themeView2.xmlId = xmlId;
                                    themeView2.xmlRoot = xmlRoot;
                                    themeView2.xmlAttr = xmlAttr;
                                    themeView2.xmlValue = xmlValue;
                                    themeView = resolveThemeView(context, themeView2);
                                } else {
                                    themeView = null;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return list;
    }

    private static ThemeView resolveThemeView(Context context, ThemeView themeView) {
        if (context != null && themeView != null) {
            try {
                Resources res = context.getResources();
                String pkg = context.getPackageName();
                if (!TextUtils.isEmpty(themeView.xmlId)) {
                    themeView.resId = res.getIdentifier(themeView.xmlId, "id", pkg);
                }
                if (!TextUtils.isEmpty(themeView.xmlAttr)) {
                    themeView.resAttr = themeView.xmlAttr;
                }
                if (!TextUtils.isEmpty(themeView.xmlRoot)) {
                    themeView.resRoot = res.getIdentifier(themeView.xmlRoot, "id", pkg);
                }
                if (!TextUtils.isEmpty(themeView.xmlValue) && themeView.xmlValue.startsWith("@") && themeView.xmlValue.contains("/")) {
                    String type = themeView.xmlValue.substring(1, themeView.xmlValue.indexOf("/"));
                    String name = themeView.xmlValue.substring(themeView.xmlValue.indexOf("/") + 1);
                    int identifier = res.getIdentifier(name, type, pkg);
                    themeView.resType = ThemeManager.ResourceType.getType(type);
                    themeView.resValue = Integer.valueOf(identifier);
                }
                return themeView;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
