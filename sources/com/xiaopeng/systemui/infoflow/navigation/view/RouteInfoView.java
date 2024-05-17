package com.xiaopeng.systemui.infoflow.navigation.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.navigation.RoundBackgroundColorSpan;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import java.lang.reflect.Field;
/* loaded from: classes24.dex */
public class RouteInfoView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = "Card-RouteInfoView";
    private final int WORD_SPLIT_MINIMUM;
    private boolean isExploreMode;
    private ImageView mManeuverIcon;
    private NaviTextView mNextRouter;
    private NaviTextView mRouteRemain;
    private TextView mRouteRemainUnit;

    public RouteInfoView(Context context) {
        super(context);
        this.isExploreMode = false;
        this.WORD_SPLIT_MINIMUM = 3;
        init();
    }

    public RouteInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isExploreMode = false;
        this.WORD_SPLIT_MINIMUM = 3;
        init();
    }

    private void init() {
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mManeuverIcon = (ImageView) findViewById(R.id.img_navi_router_maneuver);
        this.mRouteRemain = (NaviTextView) findViewById(R.id.tv_navi_route_remain);
        this.mRouteRemain.setTag("RouteRemain");
        this.mRouteRemain.setRequestLog(true);
        this.mRouteRemainUnit = (TextView) findViewById(R.id.tv_navi_route_remain_unit);
        this.mNextRouter = (NaviTextView) findViewById(R.id.tv_router_next);
        this.mNextRouter.setTag("NextRouter");
        if (this.isExploreMode) {
            this.mNextRouter.setSingleLine(true);
            this.mNextRouter.setHorizontalFadingEdgeEnabled(true);
            this.mNextRouter.setFadingEdgeLength(this.mContext.getResources().getDimensionPixelSize(R.dimen.navi_text_horizontal_fading_length));
            return;
        }
        this.mNextRouter.setMaxLines(2);
    }

    public void setNaviData(Navi navi) {
        String unitString;
        this.mRouteRemain.setContent(navi.getSegmentRemainDistDisplay());
        if (navi.getSegmentRemainDistUnitDisplay() == 0) {
            unitString = getContext().getString(R.string.meter_unit);
        } else {
            unitString = getContext().getString(R.string.kilometer_unit);
        }
        this.mRouteRemainUnit.setText(unitString);
        refreshNextRouterInfo(navi);
    }

    private String processRawText(TextView nextRouteInfoTV, float startX, String rawText) {
        float startX2;
        int insertPos;
        Paint nextRouteInfoPaint = nextRouteInfoTV.getPaint();
        float textViewWidth = (nextRouteInfoTV.getWidth() - nextRouteInfoTV.getPaddingLeft()) - nextRouteInfoTV.getPaddingRight();
        String rawText2 = rawText.trim().replace(' ', (char) 8239);
        StringBuilder resBuilder = new StringBuilder();
        if (nextRouteInfoPaint.measureText(rawText2) + startX <= textViewWidth) {
            resBuilder.append(rawText2);
        } else {
            float preCharWidth = 0.0f;
            float dashWidth = nextRouteInfoPaint.measureText(String.valueOf('-'));
            int charIdxOfWord = -1;
            int curIdx = 0;
            float startX3 = startX;
            while (true) {
                if (curIdx == rawText2.length()) {
                    break;
                }
                char ch = rawText2.charAt(curIdx);
                float curCharWidth = nextRouteInfoPaint.measureText(String.valueOf(ch));
                charIdxOfWord = ch == 8239 ? -1 : charIdxOfWord + 1;
                float widthRemain = textViewWidth - startX3;
                startX3 += curCharWidth;
                if (startX3 <= textViewWidth) {
                    resBuilder.append(ch);
                    preCharWidth = curCharWidth;
                    curIdx++;
                } else {
                    if (ch == 8239) {
                        resBuilder.append('\n');
                        startX2 = 0.0f;
                        curIdx++;
                    } else if (charIdxOfWord <= 3 || ((charIdxOfWord == 4 && widthRemain < dashWidth) || (charIdxOfWord == 5 && widthRemain + preCharWidth < dashWidth))) {
                        int spacePos = (curIdx - charIdxOfWord) - 1;
                        if (spacePos >= 0) {
                            resBuilder.setCharAt(spacePos, ' ');
                        }
                        startX2 = nextRouteInfoPaint.measureText(rawText2.substring(spacePos + 1, curIdx));
                    } else {
                        if (widthRemain >= dashWidth) {
                            insertPos = curIdx;
                        } else if (widthRemain + preCharWidth >= dashWidth) {
                            insertPos = curIdx - 1;
                        } else {
                            insertPos = curIdx - 2;
                        }
                        resBuilder.insert(insertPos, "-\n");
                        startX2 = nextRouteInfoPaint.measureText(rawText2.substring(insertPos + 1, curIdx + 1));
                    }
                    resBuilder.append((CharSequence) procStrContainSlash(rawText2.substring(curIdx), startX2, nextRouteInfoPaint, textViewWidth));
                }
            }
        }
        return resBuilder.toString();
    }

    private StringBuilder procStrContainSlash(String str, float startX, Paint nextRouteInfoPaint, float textViewWidth) {
        char[] charArray;
        StringBuilder resBuilder = new StringBuilder();
        if (!str.contains("/")) {
            resBuilder.append(str);
        } else {
            for (char ch : str.toCharArray()) {
                float chWidth = nextRouteInfoPaint.measureText(String.valueOf(ch));
                startX += chWidth;
                resBuilder.append(ch);
            }
        }
        return resBuilder;
    }

    private void refreshNextRouterInfo(Navi navi) {
        String showInfo;
        String realExitInfo = "";
        String routerInfo = navi.getNextRouteName();
        if (navi.getIsShowExitInfo()) {
            String exitInfo = navi.getExitInfo();
            if (TextUtils.isEmpty(exitInfo)) {
                exitInfo = "";
            }
            if (navi.getExitInfoType() == 2) {
                realExitInfo = getResources().getString(R.string.entrance, exitInfo);
            } else {
                realExitInfo = getResources().getString(R.string.exit, exitInfo);
            }
        }
        if (CarModelsManager.getFeature().Navigation_isProcessRawTextSupport()) {
            float routeInfoStartX = 0.0f;
            if (!TextUtils.isEmpty(realExitInfo)) {
                TextPaint textPaint = new TextPaint();
                textPaint.setTextSize(28.0f);
                routeInfoStartX = textPaint.measureText(realExitInfo) + 28.0f;
            }
            showInfo = realExitInfo + processRawText(this.mNextRouter, routeInfoStartX, routerInfo);
        } else {
            showInfo = realExitInfo + routerInfo;
        }
        SpannableString showInfoSpannable = new SpannableString(showInfo);
        if (!TextUtils.isEmpty(realExitInfo)) {
            int realExitInfoLength = realExitInfo.length();
            showInfoSpannable.setSpan(new AbsoluteSizeSpan(28), 0, realExitInfoLength, 17);
            showInfoSpannable.setSpan(new RoundBackgroundColorSpan(Color.parseColor("#FF00700A"), Color.parseColor("#FFFFFFFF")), 0, realExitInfoLength, 17);
        }
        this.mNextRouter.setContent(showInfoSpannable);
    }

    public void setManeuverData(Maneuver maneuver) {
        refreshManeuverIcon(maneuver);
    }

    private void refreshManeuverIcon(Maneuver maneuver) {
        if (TextUtils.isEmpty(maneuver.getManeuverData())) {
            int maneuverId = (int) maneuver.getManeuverID();
            setManeuverIcon(maneuverId);
            return;
        }
        byte[] data = Base64.decode(maneuver.getManeuverData(), 0);
        this.mManeuverIcon.setImageResource(0);
        this.mManeuverIcon.setImageBitmap(getBitmap(data));
    }

    private Bitmap getTransparentBgBitmap(byte[] data) {
        int targetColor = getContext().getColor(R.color.colorManeuverIconTargetBg);
        int replaceColor = getContext().getColor(R.color.colorManeuverIconReplaceBg);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = bitmap.getPixel(i, j);
                if (color == targetColor) {
                    bitmap.setPixel(i, j, replaceColor);
                }
            }
        }
        return bitmap;
    }

    private Bitmap getBitmap(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bitmap;
    }

    private void setManeuverIcon(int id) {
        String res = String.format("ic_xlarge_normal_ic_sou%d", Integer.valueOf(id));
        int resId = getResIdByResName(res);
        this.mManeuverIcon.setImageResource(resId);
    }

    private static int getResIdByResName(String resName) {
        String str = "] !";
        int resId = 0;
        try {
            Field field = R.drawable.class.getField(resName);
            field.setAccessible(true);
            try {
                int resId2 = field.getInt(null);
                resId = resId2;
                str = resId2;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Logger.d(TAG, ">>> The resource id can not be obtained by name[" + resName + "] !");
                str = str;
            }
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
            Logger.d(TAG, ">>> The resource id can not be obtained by name[" + resName + str);
        }
        return resId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.d(TAG, "mManeuverIcon setImageBitmap null");
        this.mManeuverIcon.setImageBitmap(null);
    }

    public void setExploreMode(boolean exploreMode) {
        this.isExploreMode = exploreMode;
    }
}
