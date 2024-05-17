package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import org.json.JSONObject;
@Deprecated
/* loaded from: classes25.dex */
public class XTitleBar extends XLinearLayout implements View.OnClickListener, IVuiElementListener {
    private static final int MODE_BUTTON = 2;
    private static final int MODE_CUSTOM = 4;
    private static final int MODE_ICON = 1;
    private static final int MODE_LOADING = 3;
    private static final int MODE_NONE = 0;
    private XImageButton mBtnBack;
    private XImageButton mBtnClose;
    private ViewGroup mBtnCloseLay;
    private OnTitleBarClickListener mOnTitleBarClickListener;
    private ViewGroup mRightContainer;
    private TextView mTitle;

    /* loaded from: classes25.dex */
    public interface OnTitleBarClickListener {
        void onTitleBarActionClick(View view, int i);

        void onTitleBarBackClick();

        void onTitleBarCloseClick();
    }

    public XTitleBar(Context context) {
        this(context, null);
    }

    public XTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XTitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        int actionCustom;
        LayoutInflater.from(context).inflate(R.layout.x_titlebar, this);
        initView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XTitleBar);
        int showBack = a.getInt(R.styleable.XTitleBar_title_back, 8);
        setBackVisibility(showBack);
        int showClose = a.getInt(R.styleable.XTitleBar_title_close, 0);
        setCloseVisibility(showClose);
        String title = a.getString(R.styleable.XTitleBar_title_text);
        setTitle(title);
        int actionMode = a.getInt(R.styleable.XTitleBar_title_action_mode, 0);
        if (actionMode == 0) {
            this.mRightContainer.setVisibility(8);
        } else if (actionMode == 1) {
            int actionIcon1 = a.getResourceId(R.styleable.XTitleBar_title_action_1, -1);
            int actionIcon2 = a.getResourceId(R.styleable.XTitleBar_title_action_2, -1);
            if (actionIcon1 != -1) {
                setActionWithIcon(actionIcon1);
            }
            if (actionIcon1 != -1 && actionIcon2 != -1) {
                setActionWithIcon(actionIcon1, actionIcon2);
            }
        } else if (actionMode == 2) {
            String actionText1 = a.getString(R.styleable.XTitleBar_title_action_1);
            if (actionText1 != null) {
                setActionWithButton(actionText1);
            }
        } else if (actionMode != 3 && actionMode == 4 && (actionCustom = a.getResourceId(R.styleable.XTitleBar_title_action_1, -1)) != -1) {
            setActionWithCustom(actionCustom);
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
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mBtnClose, this.mBtnCloseLay);
    }

    private void addActionWithIcon(@DrawableRes int... action) {
        if (action != null) {
            for (int i = 0; i < action.length; i++) {
                ImageButton icon = (ImageButton) LayoutInflater.from(getContext()).inflate(R.layout.x_titlebar_action_icon, this.mRightContainer, false);
                icon.setImageResource(action[i]);
                icon.setOnClickListener(this);
                icon.setId(i);
                this.mRightContainer.addView(icon);
            }
        }
    }

    private void addActionWithButton(@NonNull CharSequence... action) {
        for (int i = 0; i < action.length; i++) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.x_titlebar_action_ghostbtn, this.mRightContainer, false);
            textView.setOnClickListener(this);
            textView.setId(i);
            textView.setText(action[i]);
            this.mRightContainer.addView(textView);
        }
    }

    public void setTitle(@Nullable CharSequence title) {
        this.mTitle.setText(title);
    }

    public void setCloseVisibility(int visibility) {
        this.mBtnClose.setVisibility(visibility);
    }

    public void setBackVisibility(int visibility) {
        this.mBtnBack.setVisibility(visibility);
    }

    public void setActionWithIcon(@DrawableRes int action1) {
        this.mRightContainer.removeAllViews();
        addActionWithIcon(action1);
        this.mRightContainer.setVisibility(0);
    }

    public void setActionWithIcon(@DrawableRes int action1, @DrawableRes int action2) {
        this.mRightContainer.removeAllViews();
        addActionWithIcon(action1, action2);
        this.mRightContainer.setVisibility(0);
    }

    public void setActionWithButton(@NonNull CharSequence text) {
        this.mRightContainer.removeAllViews();
        addActionWithButton(text);
        this.mRightContainer.setVisibility(0);
    }

    public void setActionWithCustom(@LayoutRes int layout) {
        View view = LayoutInflater.from(getContext()).inflate(layout, this.mRightContainer, false);
        setActionWithCustom(view);
    }

    public void setActionWithCustom(View view) {
        this.mRightContainer.removeAllViews();
        this.mRightContainer.addView(view);
        this.mRightContainer.setVisibility(0);
    }

    public void clearAction() {
        this.mRightContainer.removeAllViews();
        this.mRightContainer.setVisibility(8);
        this.mTitle.requestLayout();
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener l) {
        this.mOnTitleBarClickListener = l;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.x_titlebar_btn_close) {
            OnTitleBarClickListener onTitleBarClickListener = this.mOnTitleBarClickListener;
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.onTitleBarCloseClick();
            }
        } else if (id == R.id.x_titlebar_btn_back) {
            OnTitleBarClickListener onTitleBarClickListener2 = this.mOnTitleBarClickListener;
            if (onTitleBarClickListener2 != null) {
                onTitleBarClickListener2.onTitleBarBackClick();
            }
        } else {
            OnTitleBarClickListener onTitleBarClickListener3 = this.mOnTitleBarClickListener;
            if (onTitleBarClickListener3 != null) {
                onTitleBarClickListener3.onTitleBarActionClick(v, id);
            }
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
            return null;
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(View view, VuiEvent vuiEvent) {
        return false;
    }
}
