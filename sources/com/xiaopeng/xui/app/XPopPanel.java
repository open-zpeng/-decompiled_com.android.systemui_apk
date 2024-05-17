package com.xiaopeng.xui.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.vui.IVuiViewScene;
import com.xiaopeng.xui.widget.XRelativeLayout;
import com.xiaopeng.xui.widget.popPanelView.XPopPanelView;
import com.xiaopeng.xuimanager.systemui.osd.IOsdListener;
import com.xiaopeng.xuimanager.systemui.osd.OsdManager;
import com.xiaopeng.xuimanager.systemui.osd.OsdRegionRecord;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
/* loaded from: classes25.dex */
public class XPopPanel implements IVuiViewScene {
    private static final String TAG = "XPopPanel";
    private static final String VUI_SCENE_TAG = "PopPanel";
    private Context mContext;
    private XPopPanelView mPopPanelView;
    private TN mTN;

    /* loaded from: classes25.dex */
    public interface PopPanelInterface {
        void onHide();

        void onShow();
    }

    private XPopPanel(Context context, final TN tn, View view) {
        this.mContext = context;
        this.mTN = tn;
        if (view != null) {
            this.mPopPanelView = new XPopPanelView(view);
            this.mPopPanelView.setPopPanelViewInterface(new XPopPanelView.IPopPanelViewInterface() { // from class: com.xiaopeng.xui.app.XPopPanel.1
                @Override // com.xiaopeng.xui.widget.popPanelView.XPopPanelView.IPopPanelViewInterface
                public int getDisplayScreenId() {
                    return XPopPanel.this.getScreenId();
                }

                @Override // com.xiaopeng.xui.widget.popPanelView.XPopPanelView.IPopPanelViewInterface
                public void onVuiEventExecuted() {
                    Log.d(XPopPanel.TAG, "onVuiEventExecuted");
                    tn.holdPanel();
                    tn.reHidePanel();
                }
            });
        }
    }

    public static void init(@NonNull Context context) {
    }

    public void show() {
        OsdManager osdManager = OsdManager.get();
        TN tn = this.mTN;
        osdManager.requestShow(tn, tn.mType, this.mTN.mRegionId);
    }

    public void cancel() {
        if (this.mTN.getWindowState()) {
            this.mTN.hideOsd();
            OsdManager osdManager = OsdManager.get();
            TN tn = this.mTN;
            osdManager.notifyHide(tn, tn.mRegionId);
        }
    }

    public boolean isShowing() {
        return this.mTN.getWindowState();
    }

    public int getScreenId() {
        TN tn = this.mTN;
        if (tn != null) {
            return tn.getScreenId();
        }
        return 1;
    }

    public void setOnStateListener(PopPanelInterface listener) {
        this.mTN.setListener(listener);
    }

    /* loaded from: classes25.dex */
    public static class XPopPanelBuilder {
        public static final int ID_SHARED_PRIMARY = 1;
        public static final int ID_SHARED_SECONDARY = 2;
        public static final int LENGTH_LONG = 5000;
        public static final int LENGTH_LONGER = 10000;
        public static final int LENGTH_SHORT = 2000;
        public static final int TYPE_NORMAL_WINDOW = 1;
        public static final int TYPE_STACK_WINDOW = 2;
        private boolean mClickable;
        private Context mContext;
        private View mItemView;
        private String mRegionId;
        private int mDuration = 2000;
        private int mWidth = 400;
        private int mHeight = 400;
        private int mType = 1;
        private int mBg = R.drawable.x_osd_background;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes25.dex */
        public @interface Duration {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes25.dex */
        public @interface Type {
        }

        public XPopPanelBuilder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public XPopPanelBuilder setDurationLength(int duration) {
            this.mDuration = duration;
            return this;
        }

        public XPopPanelBuilder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        public XPopPanelBuilder setHeight(int height) {
            this.mHeight = height;
            return this;
        }

        public XPopPanelBuilder setItemView(View itemView) {
            this.mItemView = itemView;
            return this;
        }

        public XPopPanelBuilder setRegionId(String regionId) {
            this.mRegionId = regionId;
            return this;
        }

        public XPopPanelBuilder setType(int type) {
            this.mType = type;
            return this;
        }

        public XPopPanelBuilder setBackground(int resource) {
            this.mBg = resource;
            return this;
        }

        public XPopPanelBuilder setClickable(boolean clickable) {
            this.mClickable = clickable;
            return this;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public XPopPanel create() {
            Log.i(XPopPanel.TAG, "create: mContext = " + this.mContext + "  mWidth = " + this.mWidth + "  mHeight = " + this.mHeight + "  mDuration = " + this.mDuration + "  mRegionId = " + this.mRegionId + "  mType = " + this.mType + "  mBg = " + this.mBg + "  mClickable = " + this.mClickable + "  mItemView = " + this.mItemView);
            TN tn = new TN(this.mContext, this.mWidth, this.mHeight, this.mDuration, this.mRegionId, this.mType, this.mBg, this.mClickable, this.mItemView);
            return new XPopPanel(this.mContext, tn, this.mItemView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public static class TN extends IOsdListener.Stub {
        private static final int HIDE = 1;
        private PopPanelInterface listener;
        private BackgroundLayout mBackgroundLayout;
        private final int mBg;
        private final boolean mClickable;
        private final Context mContext;
        private final int mDuration;
        private final Handler mHandler;
        private final int mHeight;
        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        private final String mRegionId;
        private int mScreenId;
        private final int mType;
        private final WindowManager mWM;
        private final int mWidth;

        public TN(Context context, int width, int height, int duration, String regionId, int type, int bg, boolean clickable, View itemView) {
            this.mType = type;
            this.mDuration = duration;
            this.mContext = context;
            this.mWidth = width;
            this.mHeight = height;
            this.mBg = bg;
            this.mRegionId = regionId;
            this.mClickable = clickable;
            this.mWM = (WindowManager) context.getSystemService("window");
            WindowManager.LayoutParams layoutParams = this.mParams;
            layoutParams.gravity = 51;
            layoutParams.type = WindowHelper.TYPE_OSD;
            layoutParams.format = -3;
            layoutParams.flags = 168;
            layoutParams.packageName = this.mContext.getPackageName();
            this.mParams.windowAnimations = R.style.XAnimation_XOsd;
            setBackgroundLayout(itemView);
            this.mHandler = new Handler(Looper.myLooper(), null) { // from class: com.xiaopeng.xui.app.XPopPanel.TN.1
                @Override // android.os.Handler
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        TN.this.handleHide();
                    }
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setListener(PopPanelInterface listener) {
            this.listener = listener;
        }

        private void setBackgroundLayout(View itemView) {
            this.mBackgroundLayout = new BackgroundLayout(this.mContext, this.mBg, this.mClickable);
            this.mBackgroundLayout.setTN(this);
            this.mBackgroundLayout.setBackground(ContextCompat.getDrawable(this.mContext, this.mBg));
            this.mBackgroundLayout.setElevation(5.0f);
            if (itemView != null) {
                this.mBackgroundLayout.addView(itemView);
            }
        }

        private static void setXpFlags(WindowManager.LayoutParams layoutParams, int screenId) {
            try {
                Field field = WindowManager.LayoutParams.class.getField("xpFlags");
                int currentXpFlags = field.getInt(layoutParams);
                if (screenId == 1) {
                    currentXpFlags |= 16;
                } else if (screenId == 2) {
                    currentXpFlags |= 32;
                }
                field.setInt(layoutParams, currentXpFlags);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        private void handleShow() {
            if (this.mBackgroundLayout.getParent() != null) {
                this.mWM.removeView(this.mBackgroundLayout);
            }
            WindowManager windowManager = this.mWM;
            if (windowManager != null) {
                windowManager.addView(this.mBackgroundLayout, this.mParams);
            }
            PopPanelInterface popPanelInterface = this.listener;
            if (popPanelInterface != null) {
                popPanelInterface.onShow();
            }
            this.mHandler.sendEmptyMessageDelayed(1, this.mDuration);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleHide() {
            this.mHandler.removeMessages(1);
            if (this.mBackgroundLayout.getParent() != null) {
                this.mWM.removeView(this.mBackgroundLayout);
            }
            PopPanelInterface popPanelInterface = this.listener;
            if (popPanelInterface != null) {
                popPanelInterface.onHide();
            }
            OsdManager.get().notifyHide(this, this.mRegionId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void holdPanel() {
            this.mHandler.removeMessages(1);
        }

        public void reHidePanel() {
            this.mHandler.sendEmptyMessageDelayed(1, this.mDuration);
        }

        public boolean getWindowState() {
            return this.mBackgroundLayout.getParent() != null;
        }

        public void hideOsd() {
            handleHide();
        }

        public int getScreenId() {
            return this.mScreenId;
        }

        public void setScreenId(int mScreenId) {
            this.mScreenId = mScreenId;
        }

        public void showOsd(OsdRegionRecord record) {
            Log.i(XPopPanel.TAG, "showPanel: recordID = " + record.getScreenId() + "recordMinWidth = " + record.getMinWidth() + "MaxHeight = " + record.getMaxHeight());
            this.mParams.x = record.getX();
            this.mParams.y = record.getY();
            if (this.mWidth >= record.getMinWidth() && this.mWidth <= record.getMaxWidth()) {
                this.mParams.width = this.mWidth;
            } else if (this.mWidth < record.getMinWidth()) {
                this.mParams.width = record.getMinWidth();
            } else if (this.mWidth > record.getMaxWidth()) {
                this.mParams.width = record.getMaxWidth();
            }
            if (this.mHeight >= record.getMinHeight() && this.mHeight <= record.getMaxHeight()) {
                this.mParams.height = this.mHeight;
            } else if (this.mWidth < record.getMinHeight()) {
                this.mParams.height = record.getMinHeight();
            } else if (this.mWidth > record.getMaxHeight()) {
                this.mParams.height = record.getMaxHeight();
            }
            setScreenId(record.getScreenId());
            setXpFlags(this.mParams, record.getScreenId());
            handleShow();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public static class BackgroundLayout extends XRelativeLayout {
        private final int mBg;
        private final boolean mClickable;
        private final Context mContext;
        private TN mTN;

        public BackgroundLayout(Context context, int bg, boolean clickable) {
            super(context);
            this.mContext = context;
            this.mBg = bg;
            this.mClickable = clickable;
            this.mXViewDelegate.getThemeViewModel().setBackgroundResource(this.mBg);
        }

        public void setTN(TN mTN) {
            this.mTN = mTN;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.View
        public void onConfigurationChanged(Configuration newConfig) {
            Log.i(XPopPanel.TAG, "onConfigurationChanged: X = " + this.mTN.mParams.x + "  Y = " + this.mTN.mParams.y);
            super.onConfigurationChanged(newConfig);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked == 1) {
                    this.mTN.reHidePanel();
                } else if (actionMasked == 3) {
                    this.mTN.handleHide();
                }
            } else if (this.mClickable) {
                this.mTN.holdPanel();
            } else {
                return false;
            }
            return super.dispatchTouchEvent(motionEvent);
        }
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String sceneId, IVuiEngine vuiEngine) {
        initVuiScene(sceneId, vuiEngine, null);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String sceneId, IVuiEngine vuiEngine, IVuiSceneListener listener) {
        if (this.mPopPanelView != null) {
            if (!sceneId.endsWith(VUI_SCENE_TAG)) {
                sceneId = sceneId + VUI_SCENE_TAG;
            }
            this.mPopPanelView.initVuiScene(sceneId, vuiEngine, listener);
        }
    }
}
