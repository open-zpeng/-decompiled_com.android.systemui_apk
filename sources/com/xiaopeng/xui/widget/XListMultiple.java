package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
public class XListMultiple extends XRelativeLayout {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_TW0 = 1;
    private static final int TYPE_WRAP = 2;
    private ViewStub mBottomViewStub;
    private ViewStub mRightViewStub;
    private TextView mText;
    private TextView mTextSub;

    public XListMultiple(Context context) {
        this(context, null);
    }

    public XListMultiple(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XListMultiple(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XListMultiple(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        int layout;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XListMultiple);
        int type = a.getInt(R.styleable.XListMultiple_list_multiple_type, 0);
        if (type == 1) {
            layout = R.layout.x_list_multiple_two;
        } else if (type != 2) {
            layout = R.layout.x_list_multiple;
        } else {
            layout = R.layout.x_list_multiple_wrap;
        }
        LayoutInflater.from(context).inflate(layout, this);
        initView();
        int lines = a.getInt(R.styleable.XListMultiple_list_text_sub_lines, 1);
        this.mTextSub.setMaxLines(lines);
        String text = a.getString(R.styleable.XListMultiple_list_multiple_text);
        setText(text);
        String textSub = a.getString(R.styleable.XListMultiple_list_multiple_text_sub);
        setTextSub(textSub);
        int bottom = a.getResourceId(R.styleable.XListMultiple_list_multiple_bottom, -1);
        if (bottom != -1) {
            this.mBottomViewStub.setLayoutResource(bottom);
            this.mBottomViewStub.inflate();
        }
        int right = a.getResourceId(R.styleable.XListMultiple_list_multiple_right, -1);
        if (right != -1) {
            this.mRightViewStub.setLayoutResource(right);
            this.mRightViewStub.inflate();
        }
        a.recycle();
    }

    private /* synthetic */ void lambda$new$0(View v) {
        logD("list-mul mBottomViewStub w " + v.getWidth() + " h " + v.getHeight());
    }

    private /* synthetic */ void lambda$new$1(int lines, int type) {
        logD("list-mul this w " + getWidth() + " h " + getHeight() + ", lines : " + lines + ", type :" + type);
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mTextSub = (TextView) findViewById(R.id.x_list_tv_sub);
        this.mBottomViewStub = (ViewStub) findViewById(R.id.x_list_bottom);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
    }

    public void setText(@Nullable CharSequence text) {
        this.mText.setText(text);
    }

    public void setTextSub(@Nullable CharSequence text) {
        this.mTextSub.setText(text);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(boolean enabled, boolean containChild) {
        super.setEnabled(enabled);
        if (containChild) {
            setChildEnabled(this, enabled);
        }
    }

    private void setChildEnabled(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildEnabled((ViewGroup) view, enabled);
            }
            view.setEnabled(enabled);
        }
    }
}
