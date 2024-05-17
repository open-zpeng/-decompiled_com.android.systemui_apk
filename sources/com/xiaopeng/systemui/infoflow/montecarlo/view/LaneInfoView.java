package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import java.lang.reflect.Field;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class LaneInfoView extends AnimatedImageView {
    private static final String TAG = LaneInfoView.class.getSimpleName();
    private static final int TYPE_AUTO_CARD_TOLL_GATE = 2;
    private static final int TYPE_ETC_TOLL_GATE = 1;
    private static final int TYPE_NORMAL_LANE = 0;
    private static final int TYPE_NORMAL_TOLL_GATE = 0;
    private static final int TYPE_TOLL_GATE = 1;
    private int ITEM_SIZE;
    private int ITEM_TOP;
    private int LINE_HEIGHT;
    private int LINE_TOP;
    private int LINE_WIDTH;
    private final int MAX_SIZE;
    private Lane mLaneData;
    private Paint mLaneLinePaint;
    private Paint mModuleLinePaint;

    public LaneInfoView(Context context) {
        super(context);
        this.ITEM_SIZE = 64;
        this.ITEM_TOP = 13;
        this.LINE_TOP = 23;
        this.LINE_HEIGHT = 45;
        this.LINE_WIDTH = 1;
        this.MAX_SIZE = 7;
        init();
    }

    public LaneInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.ITEM_SIZE = 64;
        this.ITEM_TOP = 13;
        this.LINE_TOP = 23;
        this.LINE_HEIGHT = 45;
        this.LINE_WIDTH = 1;
        this.MAX_SIZE = 7;
        init();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mLaneData.getLaneType() == 0) {
            drawLane(canvas);
            drawLine(canvas);
        } else {
            drawTollGate(canvas);
            drawTollGateLine(canvas);
        }
        drawSepLine(canvas);
    }

    private void init() {
        this.mLaneLinePaint = new Paint();
        this.mLaneLinePaint.setColor(getResources().getColor(R.color.colorLaneSeparateLine, getContext().getTheme()));
        this.mLaneLinePaint.setStrokeWidth(1.0f);
        this.mLaneLinePaint.setAntiAlias(true);
        this.mModuleLinePaint = new Paint();
        this.mModuleLinePaint.setColor(getResources().getColor(R.color.colorModuleSeparateLine, getContext().getTheme()));
        this.mModuleLinePaint.setStrokeWidth(1.0f);
        this.mModuleLinePaint.setAntiAlias(true);
    }

    public void setData(Lane lane) {
        this.mLaneData = lane;
        if (lane != null) {
            Logger.d(TAG, "set Data lane");
            invalidate();
        }
    }

    private void drawLane(Canvas canvas) {
        Lane lane = this.mLaneData;
        if (lane == null || lane.getFrontLane() == null || this.mLaneData.getBackLane() == null) {
            return;
        }
        int[] frontLanes = this.mLaneData.getFrontLane();
        int[] backLanes = this.mLaneData.getBackLane();
        int laneSize = frontLanes.length <= 7 ? frontLanes.length : 7;
        int start = (getMeasuredWidth() - (this.ITEM_SIZE * laneSize)) / 2;
        for (int i = 0; i < laneSize; i++) {
            int resId = getLaneResource(backLanes[i], frontLanes[i], true);
            if (resId != 0) {
                Drawable drawable = getResources().getDrawable(resId, getContext().getTheme());
                int i2 = this.ITEM_SIZE;
                int left = (i * i2) + start;
                int top = this.ITEM_TOP;
                int right = left + i2;
                int bottom = i2 + top;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
        }
    }

    private void drawTollGate(Canvas canvas) {
        int[] tollGateInfo;
        Lane lane = this.mLaneData;
        if (lane == null || (tollGateInfo = lane.getTollGateInfo()) == null) {
            return;
        }
        int tollGateSize = tollGateInfo.length <= 7 ? tollGateInfo.length : 7;
        int start = (getMeasuredWidth() - (this.ITEM_SIZE * tollGateSize)) / 2;
        for (int i = 0; i < tollGateSize; i++) {
            Drawable drawable = getTollGateDrawable(tollGateInfo[i]);
            if (drawable != null) {
                int i2 = this.ITEM_SIZE;
                int left = (i * i2) + start;
                int top = this.ITEM_TOP;
                int right = left + i2;
                int bottom = i2 + top;
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(canvas);
            }
        }
    }

    private Drawable getTollGateDrawable(int tollGateId) {
        if (tollGateId == 0) {
            return getResources().getDrawable(R.drawable.ic_mid_normal_ic_landtypenormal, getContext().getTheme());
        }
        if (tollGateId == 1) {
            return getResources().getDrawable(R.drawable.ic_mid_normal_ic_landtypeetc, getContext().getTheme());
        }
        if (tollGateId == 2) {
            return getResources().getDrawable(R.drawable.ic_mid_normal_ic_landtypeautomatric, getContext().getTheme());
        }
        return null;
    }

    private void drawTollGateLine(Canvas canvas) {
        int[] tollGateInfo;
        Lane lane = this.mLaneData;
        if (lane == null || (tollGateInfo = lane.getTollGateInfo()) == null) {
            return;
        }
        int tollGateSize = tollGateInfo.length <= 7 ? tollGateInfo.length : 7;
        if (tollGateSize < 2) {
            return;
        }
        int measuredWidth = getMeasuredWidth();
        int i = this.ITEM_SIZE;
        int start = ((measuredWidth - (i * tollGateSize)) / 2) + i;
        for (int i2 = 0; i2 < tollGateSize - 1; i2++) {
            int left = (this.ITEM_SIZE * i2) + start;
            int top = this.LINE_TOP;
            int right = this.LINE_WIDTH + left;
            int bottom = this.LINE_HEIGHT + top;
            canvas.drawLine(left, top, right, bottom, this.mLaneLinePaint);
        }
    }

    private void drawLine(Canvas canvas) {
        Lane lane = this.mLaneData;
        if (lane == null || lane.getFrontLane() == null || this.mLaneData.getBackLane() == null) {
            return;
        }
        int[] frontLanes = this.mLaneData.getFrontLane();
        int laneSize = frontLanes.length <= 7 ? frontLanes.length : 7;
        if (laneSize < 2) {
            return;
        }
        int measuredWidth = getMeasuredWidth();
        int i = this.ITEM_SIZE;
        int start = ((measuredWidth - (i * laneSize)) / 2) + i;
        for (int i2 = 0; i2 < laneSize - 1; i2++) {
            int left = (this.ITEM_SIZE * i2) + start;
            int top = this.LINE_TOP;
            int right = this.LINE_WIDTH + left;
            int bottom = this.LINE_HEIGHT + top;
            canvas.drawLine(left, top, right, bottom, this.mLaneLinePaint);
        }
    }

    private int getLaneResource(int backLaneType, int frontLaneType, boolean isNavi) {
        String res;
        String str = TAG;
        Logger.d(str, ">>> getLaneResource backLaneTypee=[ " + backLaneType + "] frontLaneType=[" + frontLaneType + "] isNavi=" + isNavi);
        if (isNavi) {
            if (255 == backLaneType) {
                if (255 == frontLaneType) {
                    return 0;
                }
                String res2 = String.format("ic_mid_normal_ic_landfront_%s", Integer.toHexString(frontLaneType));
                int resId = getResIdByResName(res2);
                return resId;
            } else if (255 == frontLaneType) {
                if (21 == backLaneType) {
                    String res3 = String.format("ic_mid_normal_ic_landfront_%s", Integer.toHexString(backLaneType));
                    int resId2 = getResIdByResName(res3);
                    return resId2;
                }
                String res4 = String.format("ic_mid_normal_ic_landback_%s", Integer.toHexString(backLaneType));
                int resId3 = getResIdByResName(res4);
                return resId3;
            } else {
                if (backLaneType == frontLaneType) {
                    res = String.format("ic_mid_normal_ic_landfront_%s", Integer.toHexString(frontLaneType));
                } else {
                    res = String.format("ic_mid_normal_ic_landfront_%s%s", Integer.toHexString(backLaneType), Integer.toHexString(frontLaneType));
                }
                int resId4 = getResIdByResName(res);
                return resId4;
            }
        } else if (255 == backLaneType) {
            return 0;
        } else {
            String res5 = String.format("ic_mid_normal_ic_landfront_%s", Integer.toHexString(backLaneType));
            int resId5 = getResIdByResName(res5);
            return resId5;
        }
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

    private void drawSepLine(Canvas canvas) {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        canvas.drawLine(0.0f, height - 1, width, height, this.mModuleLinePaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AnimatedImageView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            this.mLaneLinePaint.setColor(getResources().getColor(R.color.colorLaneSeparateLine, getContext().getTheme()));
            this.mModuleLinePaint.setColor(getResources().getColor(R.color.colorModuleSeparateLine, getContext().getTheme()));
            invalidate();
        }
    }
}
