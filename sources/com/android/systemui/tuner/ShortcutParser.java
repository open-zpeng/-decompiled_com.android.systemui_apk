package com.android.systemui.tuner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes21.dex */
public class ShortcutParser {
    private static final String INTENT = "intent";
    private static final String SHORTCUT = "shortcut";
    private static final String SHORTCUTS = "android.app.shortcuts";
    private AttributeSet mAttrs;
    private final Context mContext;
    private final String mName;
    private final String mPkg;
    private final int mResId;
    private Resources mResources;

    public ShortcutParser(Context context, ComponentName component) throws PackageManager.NameNotFoundException {
        this(context, component.getPackageName(), component.getClassName(), getResId(context, component));
    }

    private static int getResId(Context context, ComponentName component) throws PackageManager.NameNotFoundException {
        ActivityInfo i = context.getPackageManager().getActivityInfo(component, 128);
        if (i.metaData == null || !i.metaData.containsKey(SHORTCUTS)) {
            return 0;
        }
        int resId = i.metaData.getInt(SHORTCUTS);
        return resId;
    }

    public ShortcutParser(Context context, String pkg, String name, int resId) {
        this.mContext = context;
        this.mPkg = pkg;
        this.mResId = resId;
        this.mName = name;
    }

    public List<Shortcut> getShortcuts() {
        Shortcut c;
        List<Shortcut> list = new ArrayList<>();
        if (this.mResId != 0) {
            try {
                this.mResources = this.mContext.getPackageManager().getResourcesForApplication(this.mPkg);
                XmlResourceParser parser = this.mResources.getXml(this.mResId);
                this.mAttrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    if (type == 1) {
                        break;
                    } else if (type == 2 && parser.getName().equals(SHORTCUT) && (c = parseShortcut(parser)) != null) {
                        list.add(c);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private Shortcut parseShortcut(XmlResourceParser parser) throws IOException, XmlPullParserException {
        TypedArray sa = this.mResources.obtainAttributes(this.mAttrs, R.styleable.Shortcut);
        Shortcut c = new Shortcut();
        boolean enabled = sa.getBoolean(1, true);
        if (!enabled) {
            return null;
        }
        String id = sa.getString(2);
        int iconResId = sa.getResourceId(0, 0);
        int titleResId = sa.getResourceId(3, 0);
        String str = this.mPkg;
        c.pkg = str;
        c.icon = Icon.createWithResource(str, iconResId);
        c.id = id;
        c.label = this.mResources.getString(titleResId);
        c.name = this.mName;
        while (true) {
            int type = parser.next();
            if (type == 3) {
                break;
            } else if (type == 2 && parser.getName().equals(INTENT)) {
                c.intent = Intent.parseIntent(this.mResources, parser, this.mAttrs);
            }
        }
        if (c.intent != null) {
            return c;
        }
        return null;
    }

    /* loaded from: classes21.dex */
    public static class Shortcut {
        public Icon icon;
        public String id;
        public Intent intent;
        public String label;
        public String name;
        public String pkg;

        public static Shortcut create(Context context, String value) {
            String[] sp = value.split("::");
            try {
                for (Shortcut shortcut : new ShortcutParser(context, new ComponentName(sp[0], sp[1])).getShortcuts()) {
                    if (shortcut.id.equals(sp[2])) {
                        return shortcut;
                    }
                }
                return null;
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }

        public String toString() {
            return this.pkg + "::" + this.name + "::" + this.id;
        }
    }
}
