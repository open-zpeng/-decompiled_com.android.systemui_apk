package com.xiaopeng.xui.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import com.android.settingslib.wifi.AccessPoint;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xuimanager.systemui.masklayer.IMaskLayerListener;
import com.xiaopeng.xuimanager.systemui.masklayer.MaskLayerManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
/* loaded from: classes25.dex */
public class XMaskLayer {
    private static final String TAG = "XMaskLayer";
    private Context mContext;
    private TN mTN;

    /* loaded from: classes25.dex */
    public interface MaskLayerInterface {
        void onHide();

        void onShow();
    }

    private XMaskLayer(Context context, TN tn) {
        this.mContext = context;
        this.mTN = tn;
    }

    public static void init(@NonNull Context context) {
    }

    public void show() {
        Log.i(TAG, "requestShow: mTN = " + this.mTN + "mScreenId" + this.mTN.mScreenId);
        MaskLayerManager maskLayerManager = MaskLayerManager.get();
        TN tn = this.mTN;
        maskLayerManager.requestShow(tn, tn.mIsStackWindow, this.mTN.mScreenId);
    }

    public void cancel() throws RemoteException {
        if (this.mTN.getWindowState()) {
            MaskLayerManager maskLayerManager = MaskLayerManager.get();
            TN tn = this.mTN;
            maskLayerManager.notifyDismiss(tn, tn.mScreenId);
            this.mTN.dismissMaskLayer();
        }
    }

    /* loaded from: classes25.dex */
    public static class XMaskLayerBuilder {
        public static final int ID_SHARED_PRIMARY = 0;
        public static final int ID_SHARED_SECONDARY = 1;
        private int mBg;
        private Context mContext;
        private int mHeight;
        private View mItemView;
        private int mWidth;
        private boolean mClickable = false;
        private int mScreenId = 0;
        private boolean mIsStackWindow = true;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes25.dex */
        public @interface ScreenId {
        }

        public XMaskLayerBuilder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public XMaskLayerBuilder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        public XMaskLayerBuilder setHeight(int height) {
            this.mHeight = height;
            return this;
        }

        public XMaskLayerBuilder setClickable(boolean clickable) {
            this.mClickable = clickable;
            return this;
        }

        public XMaskLayerBuilder setStackWindow(boolean state) {
            this.mIsStackWindow = state;
            return this;
        }

        public XMaskLayerBuilder setScreenId(int screenId) {
            this.mScreenId = screenId;
            return this;
        }

        public XMaskLayerBuilder setItemView(View itemView) {
            this.mItemView = itemView;
            return this;
        }

        public XMaskLayerBuilder setBackground(int Resource) {
            this.mBg = Resource;
            return this;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public XMaskLayer create() {
            TN tn = new TN(this.mContext, this.mWidth, this.mHeight, this.mScreenId, this.mIsStackWindow, this.mBg, this.mClickable, this.mItemView);
            Log.i(XMaskLayer.TAG, "create: TN : 构造TN");
            return new XMaskLayer(this.mContext, tn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public static class TN extends IMaskLayerListener.Stub {
        private static final int HIDE = 1;
        private MaskLayerInterface listener;
        private BackgroundLayout mBackgroundLayout;
        private final int mBg;
        private boolean mClickable;
        private final Context mContext;
        private final Handler mHandler;
        private boolean mIsStackWindow;
        private final int mScreenId;
        private final WindowManager mWM;
        private boolean mWindowState = false;
        private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

        public TN(Context context, int width, int height, int screenId, boolean isStackWindow, int bg, boolean clickable, View itemView) {
            this.mContext = context;
            this.mClickable = clickable;
            this.mIsStackWindow = isStackWindow;
            this.mScreenId = screenId;
            this.mBg = bg;
            this.mWM = (WindowManager) context.getSystemService("window");
            WindowManager.LayoutParams layoutParams = this.mParams;
            layoutParams.format = -2;
            layoutParams.width = AccessPoint.LOWER_FREQ_24GHZ;
            layoutParams.height = 1200;
            layoutParams.x = 0;
            layoutParams.y = 0;
            if (this.mClickable) {
                layoutParams.flags = 16777256;
            } else {
                layoutParams.flags = 25165880;
            }
            setXpFlags(this.mParams, this.mScreenId);
            setBackgroundLayout(itemView);
            this.mHandler = new Handler(Looper.myLooper(), null) { // from class: com.xiaopeng.xui.app.XMaskLayer.TN.1
                @Override // android.os.Handler
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        TN.this.handleHide();
                    }
                }
            };
        }

        private void setBackgroundLayout(View itemView) {
            this.mBackgroundLayout = new BackgroundLayout(this.mContext, this.mBg);
            this.mBackgroundLayout.setTN(this);
            if (itemView != null) {
                this.mBackgroundLayout.addView(itemView);
            }
        }

        private static void setXpFlags(WindowManager.LayoutParams layoutParams, int screenId) {
            try {
                Field fieldXpFlags = WindowManager.LayoutParams.class.getField("xpFlags");
                int currentXpFlags = fieldXpFlags.getInt(layoutParams);
                Field field = WindowManager.LayoutParams.class.getField("intentFlags");
                int currentIntentFlags = field.getInt(layoutParams);
                field.setInt(layoutParams, currentIntentFlags | 32);
                if (screenId == 0) {
                    currentXpFlags |= 16;
                    layoutParams.gravity = 51;
                } else if (screenId == 1) {
                    currentXpFlags |= 32;
                    layoutParams.gravity = 53;
                }
                fieldXpFlags.setInt(layoutParams, currentXpFlags);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        private void handleShow() {
            if (this.mWM != null) {
                Log.i(XMaskLayer.TAG, "showMaskLayer: 弹出水印");
                this.mWM.addView(this.mBackgroundLayout, this.mParams);
            }
            this.mWindowState = true;
            MaskLayerInterface maskLayerInterface = this.listener;
            if (maskLayerInterface != null) {
                maskLayerInterface.onShow();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleHide() {
            this.mHandler.removeMessages(1);
            if (this.mWM != null) {
                Log.i(XMaskLayer.TAG, "dismissMaskLayer: 隐藏水印");
                this.mWM.removeView(this.mBackgroundLayout);
            }
            this.mWindowState = false;
            MaskLayerInterface maskLayerInterface = this.listener;
            if (maskLayerInterface != null) {
                maskLayerInterface.onHide();
            }
            MaskLayerManager.get().notifyDismiss(this, this.mScreenId);
        }

        public boolean getWindowState() {
            return this.mWindowState;
        }

        public void showMaskLayer(int windowParamsType) throws RemoteException {
            this.mParams.type = windowParamsType;
            handleShow();
        }

        public void dismissMaskLayer() {
            Log.i(XMaskLayer.TAG, "dismissMaskLayer: 收到dismiss回调");
            handleHide();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public static class BackgroundLayout extends RelativeLayout {
        private final int mBg;
        private final Context mContext;
        private TN mTN;

        public BackgroundLayout(Context context, int bg) {
            super(context);
            this.mContext = context;
            this.mBg = bg;
        }

        public void setTN(TN mTN) {
            this.mTN = mTN;
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            if (XThemeManager.isThemeChanged(configuration)) {
                int i = this.mBg;
                if (i != 0) {
                    setBackground(this.mContext.getDrawable(i));
                }
                invalidate();
            }
        }
    }
}
