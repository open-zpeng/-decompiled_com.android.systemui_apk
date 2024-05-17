package com.xiaopeng.systemui.infoflow.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.xiaopeng.systemui.infoflow.widget.drawable.XLoadingDrawable;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class AlphaImageView extends ImageView {
    private static final String TAG = "AlphaImageView";
    private final long DURATION_LOADING;
    private final int MSG_STOP_LOADING;
    private boolean isLoading;
    private Bitmap mBitmap;
    private Handler mHandler;
    private XLoadingDrawable mXLoadingDrawable;

    public AlphaImageView(Context context) {
        this(context, null);
    }

    public AlphaImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AlphaImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.DURATION_LOADING = 1000L;
        this.MSG_STOP_LOADING = 113;
        this.isLoading = false;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.widget.AlphaImageView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 113) {
                    AlphaImageView.this.stopLoading();
                }
            }
        };
    }

    @Override // android.view.View
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        alphaView(pressed);
    }

    private void alphaView(boolean pressed) {
        setAlpha(pressed ? 0.5f : 1.0f);
    }

    private void showLoading() {
        this.isLoading = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopLoading() {
        this.isLoading = false;
    }
}
