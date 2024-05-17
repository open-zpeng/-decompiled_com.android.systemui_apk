package com.xiaopeng.xui.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XLogUtils;
/* loaded from: classes25.dex */
public class XLoadingDialog extends Dialog implements Runnable {
    public static final int BOTTOM = 80;
    public static final int CENTER_VERTICAL = 16;
    public static final int TOP = 48;
    private ImageButton mCloseView;
    private Handler mHandler;
    private TextView mMessageView;
    private OnTimeOutListener mOnTimeOutListener;
    private int mTimeOut;
    private boolean mTimeOutCheck;

    /* loaded from: classes25.dex */
    public interface OnTimeOutListener {
        void onTimeOut(XLoadingDialog xLoadingDialog);
    }

    public XLoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mHandler = new Handler();
    }

    public static XLoadingDialog show(Context context) {
        return show(context, false, null);
    }

    public static XLoadingDialog show(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        return show(context, context.getString(R.string.x_loading_dialog_message), cancelable, cancelListener);
    }

    public static XLoadingDialog show(Context context, CharSequence message) {
        return show(context, message, false, (DialogInterface.OnCancelListener) null);
    }

    public static XLoadingDialog show(Context context, CharSequence message, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        return show(context, message, cancelable, cancelListener, false, 0, null);
    }

    public static XLoadingDialog show(Context context, boolean timeoutCheck, int timeout, OnTimeOutListener timeOutListener) {
        return show(context, context.getString(R.string.x_loading_dialog_message), timeoutCheck, timeout, timeOutListener);
    }

    public static XLoadingDialog show(Context context, CharSequence message, boolean timeoutCheck, int timeout, OnTimeOutListener timeOutListener) {
        return show(context, message, false, null, timeoutCheck, timeout, timeOutListener);
    }

    public static XLoadingDialog show(Context context, CharSequence message, boolean cancelable, DialogInterface.OnCancelListener cancelListener, boolean timeoutCheck, int timeout, OnTimeOutListener timeOutListener) {
        XLoadingDialog dialog = new XLoadingDialog(context, R.style.XAppTheme_XDialog_Loading);
        dialog.create();
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.setOnTimeOutListener(timeOutListener);
        dialog.setTimeOutCheck(timeoutCheck);
        dialog.setTimeOut(timeout);
        dialog.show();
        return dialog;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.x_loading_dialog, (ViewGroup) null);
        this.mMessageView = (TextView) v.findViewById(R.id.x_loading_dialog_text);
        this.mCloseView = (ImageButton) v.findViewById(R.id.x_loading_dialog_close);
        this.mCloseView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.app.XLoadingDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v2) {
                XLoadingDialog.this.log("close is click ");
                XLoadingDialog.this.cancel();
            }
        });
        setContentView(v);
        super.onCreate(savedInstanceState);
    }

    public void setMessage(CharSequence message) {
        this.mMessageView.setText(message);
    }

    @Override // android.app.Dialog
    public void setCancelable(boolean cancelable) {
        super.setCancelable(false);
        this.mCloseView.setVisibility(cancelable ? 0 : 8);
    }

    public void setOnTimeOutListener(OnTimeOutListener timeOutListener) {
        this.mOnTimeOutListener = timeOutListener;
    }

    public void setTimeOut(int timeout) {
        this.mTimeOut = timeout;
    }

    public void setTimeOutCheck(boolean timeoutCheck) {
        this.mTimeOutCheck = timeoutCheck;
        if (!this.mTimeOutCheck) {
            this.mHandler.removeCallbacks(this);
        }
    }

    @Override // android.app.Dialog
    public void show() {
        super.show();
        if (this.mTimeOutCheck) {
            this.mHandler.removeCallbacks(this);
            this.mHandler.postDelayed(this, this.mTimeOut);
        }
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        log("dismiss");
        this.mHandler.removeCallbacks(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        OnTimeOutListener onTimeOutListener = this.mOnTimeOutListener;
        if (onTimeOutListener != null) {
            onTimeOutListener.onTimeOut(this);
        }
        log("time out");
        dismiss();
    }

    public void setVerticalTopDefault() {
        setVertical(48, (int) getContext().getResources().getDimension(R.dimen.x_loading_dialog_top));
    }

    public void setVertical(int yOffset) {
        setVertical(48, yOffset);
    }

    public void setVertical(int gravity, int yOffset) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = gravity;
            lp.y = yOffset;
            window.setAttributes(lp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String msg) {
        XLogUtils.i("xpui-XLoadingDialog", msg);
    }
}
