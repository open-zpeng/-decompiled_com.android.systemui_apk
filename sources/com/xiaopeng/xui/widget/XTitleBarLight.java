package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import org.json.JSONObject;
/* loaded from: classes25.dex */
public class XTitleBarLight extends XRelativeLayout implements View.OnClickListener, IVuiElementListener {
    public static final int MAIN_ACTION_BACK = 1;
    public static final int MAIN_ACTION_CLOSE = 0;
    public static final int MAIN_ACTION_NONE = -1;
    public static final int TYPE_COMPACT = 0;
    public static final int TYPE_SPARSE = 1;
    private XImageButton mBtnBack;
    private XImageButton mBtnClose;
    private ViewGroup mBtnCloseLay;
    private OnTitleBarClickListener mOnTitleBarClickListener;
    private ViewGroup mRightContainer;
    private int mSparsePadding;
    private TextView mTitle;

    /* loaded from: classes25.dex */
    public interface OnTitleBarClickListener {
        void onTitleBarBackClick();

        void onTitleBarCloseClick();
    }

    public XTitleBarLight(Context context) {
        this(context, null);
    }

    public XTitleBarLight(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTitleBarLight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XTitleBarLight(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.x_titlebarlight, this);
        initView();
        this.mSparsePadding = (int) getResources().getDimension(R.dimen.x_title_bar_light_sparse_padding);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XTitleBarLight);
        String title = a.getString(R.styleable.XTitleBarLight_title_text);
        setTitle(title);
        int mainAction = a.getInt(R.styleable.XTitleBarLight_title_main_action, 0);
        setMainAction(mainAction);
        int type = a.getInt(R.styleable.XTitleBarLight_title_text_type, 0);
        setTitleType(type);
        int rightAction = a.getResourceId(R.styleable.XTitleBarLight_title_right_action_layout, 0);
        if (rightAction > 0) {
            setRightAction(rightAction);
        }
        a.recycle();
    }

    private void initView() {
        this.mBtnClose = (XImageButton) findViewById(R.id.x_titlebar_btn_close);
        this.mBtnBack = (XImageButton) findViewById(R.id.x_titlebar_btn_back);
        this.mTitle = (TextView) findViewById(R.id.x_titlebar_tv_title);
        this.mRightContainer = (ViewGroup) findViewById(R.id.x_titlebar_right_container);
        this.mBtnCloseLay = (ViewGroup) findViewById(R.id.x_titlebar_btn_close_lay);
        this.mBtnClose.setOnClickListener(this);
        this.mBtnBack.setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mBtnClose, this.mBtnCloseLay);
        XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mBtnBack, this.mBtnCloseLay);
    }

    public void setTitle(@StringRes int res) {
        this.mTitle.setText(res);
    }

    public void setTitle(@Nullable CharSequence title) {
        this.mTitle.setText(title);
    }

    public void setTitleType(int type) {
        if (type != 1) {
            TextView textView = this.mTitle;
            textView.setPadding(0, textView.getPaddingTop(), this.mTitle.getPaddingRight(), this.mTitle.getPaddingBottom());
            return;
        }
        TextView textView2 = this.mTitle;
        textView2.setPadding(this.mSparsePadding, textView2.getPaddingTop(), this.mTitle.getPaddingRight(), this.mTitle.getPaddingBottom());
    }

    public void setMainAction(int action) {
        if (action == -1) {
            this.mBtnBack.setVisibility(4);
            this.mBtnClose.setVisibility(4);
        } else if (action != 1) {
            this.mBtnClose.setVisibility(0);
            this.mBtnBack.setVisibility(4);
        } else {
            this.mBtnBack.setVisibility(0);
            this.mBtnClose.setVisibility(4);
        }
    }

    public void setRightAction(@LayoutRes int layout) {
        View view = LayoutInflater.from(getContext()).inflate(layout, this.mRightContainer, false);
        setRightAction(view);
    }

    public void setRightAction(View view) {
        this.mRightContainer.removeAllViews();
        this.mRightContainer.addView(view);
        this.mRightContainer.setVisibility(0);
    }

    public void clearRightAction() {
        this.mRightContainer.removeAllViews();
        this.mRightContainer.setVisibility(8);
        this.mTitle.requestLayout();
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener l) {
        this.mOnTitleBarClickListener = l;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        OnTitleBarClickListener onTitleBarClickListener;
        int id = v.getId();
        if (id == R.id.x_titlebar_btn_close) {
            OnTitleBarClickListener onTitleBarClickListener2 = this.mOnTitleBarClickListener;
            if (onTitleBarClickListener2 != null) {
                onTitleBarClickListener2.onTitleBarCloseClick();
            }
        } else if (id == R.id.x_titlebar_btn_back && (onTitleBarClickListener = this.mOnTitleBarClickListener) != null) {
            onTitleBarClickListener.onTitleBarBackClick();
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String s, IVuiElementBuilder iVuiElementBuilder) {
        try {
            if (this.mBtnBack != null) {
                if (this.mBtnBack.getVisibility() != 0) {
                    this.mBtnBack.setVuiMode(VuiMode.DISABLED);
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(VuiConstants.PROPS_GENERALACT, VuiConstants.GENERALACT_RETURN);
                    this.mBtnBack.setVuiProps(jsonObject);
                }
            }
            if (this.mBtnClose != null) {
                if (this.mBtnClose.getVisibility() != 0) {
                    this.mBtnClose.setVuiMode(VuiMode.DISABLED);
                    return null;
                }
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put(VuiConstants.PROPS_GENERALACT, VuiConstants.GENERALACT_CLOSE);
                this.mBtnClose.setVuiProps(jsonObject2);
                return null;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(View view, VuiEvent vuiEvent) {
        return false;
    }
}
