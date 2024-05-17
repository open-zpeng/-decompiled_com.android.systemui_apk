package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XFrameLayout;
/* loaded from: classes25.dex */
class XDialogMessage extends XFrameLayout {
    private ImageView mImageView;
    private TextView mTextMessage;

    public XDialogMessage(Context context) {
        this(context, null);
    }

    public XDialogMessage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XDialogMessage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XDialogMessage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.x_dialog_message, this);
        initView();
    }

    private void initView() {
        this.mTextMessage = (TextView) findViewById(R.id.x_dialog_message);
        this.mImageView = (ImageView) findViewById(R.id.x_dialog_icon);
    }

    public void setMessage(@StringRes int resId) {
        this.mTextMessage.setText(resId);
        checkLines();
    }

    public void setMessage(@Nullable CharSequence text) {
        this.mTextMessage.setText(text);
        checkLines();
    }

    private void checkLines() {
        if (this.mTextMessage.getWidth() > 0) {
            _checkLines();
        } else {
            this.mTextMessage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.xiaopeng.xui.widget.dialogview.XDialogMessage.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    XDialogMessage.this.mTextMessage.getViewTreeObserver().removeOnPreDrawListener(this);
                    XDialogMessage.this._checkLines();
                    return true;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _checkLines() {
        int lineCount = this.mTextMessage.getLineCount();
        if (lineCount < 2) {
            this.mTextMessage.setGravity(1);
        } else {
            this.mTextMessage.setGravity(8388611);
        }
        this.mTextMessage.setVisibility(0);
    }

    public void setIcon(@DrawableRes int iconId) {
        this.mImageView.setImageResource(iconId);
        this.mImageView.setVisibility(iconId != 0 ? 0 : 8);
    }

    public void setIcon(Drawable icon) {
        this.mImageView.setImageResource(0);
        this.mImageView.setImageDrawable(icon);
        this.mImageView.setVisibility(icon == null ? 8 : 0);
    }
}
