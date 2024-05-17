package com.xiaopeng.systemui.infoflow.message.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.FixedScaleDrawable;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes24.dex */
public class MusicResourcesHelper {
    private static Bitmap mDefaultMusicLogo;
    private static HashMap<String, String> mAppNameMap = new HashMap<>();
    private static HashMap<String, Drawable> mAppIconMap = new HashMap<>();
    private static HashMap<String, Bitmap> mAppIconBitmapMap = new HashMap<>();
    private static final Map<Integer, Integer> mMusicTypeStringMap = new HashMap<Integer, Integer>() { // from class: com.xiaopeng.systemui.infoflow.message.helper.MusicResourcesHelper.1
        {
            put(0, Integer.valueOf((int) R.string.music_type_default));
            put(2, Integer.valueOf((int) R.string.music_type_reading));
            put(1, Integer.valueOf((int) R.string.music_type_fm));
            put(3, Integer.valueOf((int) R.string.music_type_bt));
        }
    };
    private static final Map<Integer, Integer> mMusicTypeDefaultAblumMap = new HashMap<Integer, Integer>() { // from class: com.xiaopeng.systemui.infoflow.message.helper.MusicResourcesHelper.2
        {
            put(0, Integer.valueOf((int) R.mipmap.ic_default_music_album));
            put(2, Integer.valueOf((int) R.mipmap.ic_default_reading_album));
            put(1, Integer.valueOf((int) R.mipmap.ic_default_fm_album));
        }
    };

    public static int getTypeStringByMusicSource(int sourceType) {
        if (mMusicTypeStringMap.get(Integer.valueOf(sourceType)) == null) {
            return R.string.music_type_default;
        }
        int id = mMusicTypeStringMap.get(Integer.valueOf(sourceType)).intValue();
        return id;
    }

    public static Bitmap getDefaultMusicLogo() {
        if (mDefaultMusicLogo == null) {
            mDefaultMusicLogo = BitmapFactory.decodeResource(SystemUIApplication.getApplication().getResources(), R.drawable.ic_default_music_logo);
        }
        return mDefaultMusicLogo;
    }

    public static int getTypeAlbumByMusicSource(int sourceType) {
        if (mMusicTypeDefaultAblumMap.get(Integer.valueOf(sourceType)) == null) {
            return R.mipmap.ic_default_music_album;
        }
        int id = mMusicTypeDefaultAblumMap.get(Integer.valueOf(sourceType)).intValue();
        return id;
    }

    public static String getAppNameByPackage(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return "";
        }
        if (mAppNameMap.containsKey(pkgName)) {
            return mAppNameMap.get(pkgName);
        }
        String appName = getAppNameByPackageManager(pkgName);
        mAppNameMap.put(pkgName, appName);
        return appName;
    }

    public static String getAppNameByPackageManager(String pkgName) {
        try {
            PackageManager packageManager = SystemUIApplication.getContext().getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(pkgName, 128);
            String appLabel = appInfo.loadLabel(packageManager).toString();
            return appLabel;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Drawable getAppIconByPackage(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        if (mAppIconMap.containsKey(pkgName)) {
            return mAppIconMap.get(pkgName);
        }
        Drawable drawable = getAppIconByPackageManager(pkgName);
        mAppIconMap.put(pkgName, drawable);
        return drawable;
    }

    public static Bitmap getAppIconBitmap(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        if (mAppIconBitmapMap.containsKey(pkgName)) {
            return mAppIconBitmapMap.get(pkgName);
        }
        Drawable drawable = getAppIconByPackage(pkgName);
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
        if (bitmap != null) {
            mAppIconBitmapMap.put(pkgName, bitmap);
            return bitmap;
        }
        return bitmap;
    }

    public static Drawable getAppIconByPackageManager(String pkgName) {
        try {
            Context context = SystemUIApplication.getContext();
            PackageManager packageManager = context.getPackageManager();
            Drawable drawable = makeIconRound(context, packageManager.getApplicationIcon(pkgName));
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Drawable makeIconRound(Context context, Drawable icon) {
        if (!(icon instanceof AdaptiveIconDrawable)) {
            AdaptiveIconDrawable dr = (AdaptiveIconDrawable) context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
            dr.setBounds(0, 0, 1, 1);
            Drawable foreground = dr.getForeground();
            if (foreground instanceof FixedScaleDrawable) {
                FixedScaleDrawable fsd = (FixedScaleDrawable) foreground;
                fsd.setDrawable(icon);
                return dr;
            }
        }
        return icon;
    }
}
