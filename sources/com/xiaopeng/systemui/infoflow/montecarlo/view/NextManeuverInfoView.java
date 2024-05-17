package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import java.lang.reflect.Field;
/* loaded from: classes24.dex */
public class NextManeuverInfoView extends AlphaOptimizedLinearLayout {
    private static final String TAG = NextManeuverInfoView.class.getSimpleName();
    private ImageView mIcon;
    private TextView mInfo;
    private Paint mPaint;

    public NextManeuverInfoView(Context context) {
        super(context);
        init();
    }

    public NextManeuverInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setColor(getContext().getColor(R.color.colorModuleSeparateLine));
        this.mPaint.setAntiAlias(true);
    }

    public void setData(Navi navi) {
        this.mInfo.setText(NaviUtil.getNextManeuverSpannableString(NaviUtil.getDistanceString(getContext(), navi.getNextManeuverDist())));
        setManeuverIcon((int) navi.getNextManeuverID());
    }

    private void setManeuverIcon(int id) {
        String res = String.format("ic_mid_normal_ic_sou%d", Integer.valueOf(id));
        int resId = getResIdByResName(res);
        this.mIcon.setImageResource(resId);
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

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIcon = (ImageView) findViewById(R.id.img_next_maneuver);
        this.mInfo = (TextView) findViewById(R.id.tv_next_maneuver);
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
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            this.mPaint.setColor(getResources().getColor(R.color.colorModuleSeparateLine, getContext().getTheme()));
            invalidate();
        }
    }
}
