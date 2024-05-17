package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import java.lang.reflect.Field;
/* loaded from: classes24.dex */
public class RouteInfoView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = RouteInfoView.class.getSimpleName();
    private final int ROUTER_INFO_MAX_LENGTH;
    private TextView mExitInfoTv;
    private ImageView mManeuverIcon;
    private TextView mNextRouteNameEnd;
    private TextView mNextRouteNameStart;
    private Paint mPaint;
    private TextView mRouteRemain;

    public RouteInfoView(Context context) {
        super(context);
        this.ROUTER_INFO_MAX_LENGTH = 9;
        init();
    }

    public RouteInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.ROUTER_INFO_MAX_LENGTH = 9;
        init();
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setColor(getContext().getColor(R.color.colorModuleSeparateLine));
        this.mPaint.setAntiAlias(true);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mManeuverIcon = (ImageView) findViewById(R.id.img_maneuver);
        this.mRouteRemain = (TextView) findViewById(R.id.tv_route_remain);
        this.mExitInfoTv = (TextView) findViewById(R.id.tv_exit_info);
        this.mNextRouteNameStart = (TextView) findViewById(R.id.tv_next_route_start);
        this.mNextRouteNameEnd = (TextView) findViewById(R.id.tv_next_route_end);
    }

    public void setNaviData(Navi navi) {
        this.mRouteRemain.setText(NaviUtil.getDistanceSpannableStringWithUnit(getContext(), navi.getSegmentRemainDistDisplay(), navi.getSegmentRemainDistUnitDisplay(), 72, 36));
        String exitInfo = navi.getExitInfo();
        String routerInfo = navi.getNextRouteName();
        if (navi.getIsShowExitInfo() && !TextUtils.isEmpty(exitInfo)) {
            if (navi.getExitInfoType() == 2) {
                this.mExitInfoTv.setText(getResources().getString(R.string.entrance, exitInfo));
            } else {
                this.mExitInfoTv.setText(getResources().getString(R.string.exit, exitInfo));
            }
            this.mExitInfoTv.setVisibility(0);
            this.mExitInfoTv.setPadding(6, 0, 6, 0);
            int nextRouteStartLength = (9 - exitInfo.length()) - 2;
            if (nextRouteStartLength <= routerInfo.length()) {
                String startString = routerInfo.substring(0, nextRouteStartLength);
                String endString = routerInfo.substring(nextRouteStartLength);
                this.mNextRouteNameStart.setText(startString);
                this.mNextRouteNameEnd.setText(endString);
                this.mNextRouteNameEnd.setVisibility(0);
                return;
            }
            this.mNextRouteNameStart.setText(routerInfo);
            this.mNextRouteNameEnd.setVisibility(8);
            return;
        }
        this.mExitInfoTv.setText("");
        this.mExitInfoTv.setPadding(0, 0, 0, 0);
        this.mExitInfoTv.setVisibility(4);
        this.mNextRouteNameStart.setText(routerInfo);
        this.mNextRouteNameEnd.setVisibility(8);
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
        Matrix matrix = new Matrix();
        matrix.postScale(0.88f, 0.88f);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBitmap;
    }

    private void setManeuverIcon(int id) {
        String res = String.format("ic_xlarge_normal_ic_sou%d", Integer.valueOf(id));
        int resId = getResIdByResName(res);
        this.mManeuverIcon.setImageResource(resId);
    }

    private static int getResIdByResName(String resName) {
        try {
            Field field = R.drawable.class.getField(resName);
            field.setAccessible(true);
            try {
                int resId = field.getInt(null);
                return resId;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                String str = TAG;
                Logger.d(str, ">>> The resource id can not be obtained by name[" + resName + "] !");
                return 0;
            }
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
            String str2 = TAG;
            Logger.d(str2, ">>> The resource id can not be obtained by name[" + resName + "] !");
            return 0;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawSepLine(canvas);
    }

    private void drawSepLine(Canvas canvas) {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        canvas.drawLine(0.0f, height - 1, width, height, this.mPaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            this.mPaint.setColor(getResources().getColor(R.color.colorModuleSeparateLine, getContext().getTheme()));
            invalidate();
        }
    }
}
