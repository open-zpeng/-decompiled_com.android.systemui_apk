package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.utils.XuiUtils;
import java.util.List;
/* loaded from: classes25.dex */
public class XListSingle extends XRelativeLayout {
    private int mLeftRightTouchFull;
    private ViewGroup mLeftView;
    private ViewStub mLeftViewStub;
    private TextView mNum;
    private ViewStub mRightViewStub;
    private ImageView mTag;
    private TextView mText;
    private ViewGroup mViewGroupLeft;
    private ViewGroup mViewGroupRight;

    public XListSingle(Context context) {
        this(context, null);
    }

    public XListSingle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XListSingle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XListSingle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.x_list_single, this);
        initView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XListSingle);
        String text = a.getString(R.styleable.XListSingle_list_single_text);
        setText(text);
        int left = a.getResourceId(R.styleable.XListSingle_list_single_left, -1);
        if (left != -1) {
            this.mLeftViewStub.setLayoutResource(left);
            View view = this.mLeftViewStub.inflate();
            if (view instanceof ViewGroup) {
                this.mViewGroupLeft = (ViewGroup) view;
            }
        }
        int right = a.getResourceId(R.styleable.XListSingle_list_single_right, -1);
        if (right != -1) {
            this.mRightViewStub.setLayoutResource(right);
            View view2 = this.mRightViewStub.inflate();
            if (view2 instanceof ViewGroup) {
                this.mViewGroupRight = (ViewGroup) view2;
            }
        }
        int tagIcon = a.getResourceId(R.styleable.XListSingle_list_single_tag_icon, -1);
        if (tagIcon != -1) {
            setTagIcon(tagIcon);
        }
        boolean tag = a.getBoolean(R.styleable.XListSingle_list_single_tag, false);
        showIconTag(tag);
        this.mLeftRightTouchFull = a.getInt(R.styleable.XListSingle_list_single_left_right_touch_full, 0);
        a.recycle();
        if (right != -1) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.mText.getLayoutParams();
            int marginEnd = (int) getResources().getDimension(tag ? R.dimen.x_list_tv_has_tag_margin_end : R.dimen.x_list_tv_margin_end);
            lp.setMarginEnd(marginEnd);
            this.mText.setLayoutParams(lp);
        } else if (tag) {
            ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) this.mText.getLayoutParams();
            int marginEnd2 = (int) getResources().getDimension(R.dimen.x_list_tv_has_tag_margin_end);
            lp2.setMarginEnd(marginEnd2);
            this.mText.setLayoutParams(lp2);
        }
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mNum = (TextView) findViewById(R.id.x_list_num);
        this.mLeftView = (ViewGroup) findViewById(R.id.x_list_left_lay);
        this.mLeftViewStub = (ViewStub) findViewById(R.id.x_list_left);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
        this.mTag = (ImageView) findViewById(R.id.x_list_tag);
    }

    public void setNum(int num) {
        this.mNum.setText(String.valueOf(num));
    }

    public void showNum(boolean show) {
        this.mNum.setVisibility(show ? 0 : 8);
        int leftWidthForNum = getResources().getDimensionPixelSize(R.dimen.x_list_left_width_for_num);
        int leftWidth = getResources().getDimensionPixelSize(R.dimen.x_list_left_width);
        ViewGroup viewGroup = this.mLeftView;
        if (viewGroup != null) {
            ViewGroup.MarginLayoutParams lay = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            if (show) {
                lay.width = leftWidthForNum;
            } else {
                lay.width = leftWidth;
            }
            this.mLeftView.setLayoutParams(lay);
        }
        ViewGroup viewGroup2 = this.mViewGroupLeft;
        if (viewGroup2 != null) {
            ViewGroup.MarginLayoutParams lay2 = (ViewGroup.MarginLayoutParams) viewGroup2.getLayoutParams();
            if (show) {
                lay2.leftMargin = leftWidthForNum - leftWidth;
            } else {
                lay2.leftMargin = 0;
            }
            this.mViewGroupLeft.setLayoutParams(lay2);
        }
        TextView textView = this.mText;
        if (textView != null) {
            ViewGroup.MarginLayoutParams lay3 = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            if (show) {
                lay3.leftMargin = leftWidthForNum;
            } else {
                lay3.leftMargin = leftWidth;
            }
            this.mText.setLayoutParams(lay3);
        }
        extendTouchLeft();
    }

    public void setText(@Nullable CharSequence text) {
        this.mText.setText(text);
    }

    public void setTagIcon(@DrawableRes int res) {
        this.mTag.setImageResource(res);
    }

    public void showIconTag(boolean show) {
        this.mTag.setVisibility(show ? 0 : 4);
    }

    public void setTagOnClickListener(View.OnClickListener listener) {
        this.mTag.setOnClickListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        extendTouchLeft();
        extendTouchRight();
    }

    private void extendTouchLeft() {
        Class<?>[] clsArr;
        Class<?>[] clsArr2;
        if (this.mViewGroupLeft != null) {
            if (this.mLeftRightTouchFull == 1) {
                for (Class<?> cls : XTouchAreaUtils.CLASSES) {
                    List<View> views = XuiUtils.findViewsByType(this.mViewGroupLeft, cls);
                    if (views.size() > 0) {
                        View[] views1 = new View[views.size()];
                        views.toArray(views1);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(views1, this);
                    }
                }
            } else {
                XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mLeftView);
            }
        }
        ViewGroup viewGroup = this.mViewGroupRight;
        if (viewGroup != null) {
            if (this.mLeftRightTouchFull == 2) {
                for (Class<?> cls2 : XTouchAreaUtils.CLASSES) {
                    List<View> views2 = XuiUtils.findViewsByType(this.mViewGroupRight, cls2);
                    if (views2.size() > 0) {
                        View[] views12 = new View[views2.size()];
                        views2.toArray(views12);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(views12, this);
                    }
                }
                return;
            }
            XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewGroup);
        }
    }

    private void extendTouchRight() {
        Class<?>[] clsArr;
        ViewGroup viewGroup = this.mViewGroupRight;
        if (viewGroup != null) {
            if (this.mLeftRightTouchFull == 2) {
                for (Class<?> cls : XTouchAreaUtils.CLASSES) {
                    List<View> views = XuiUtils.findViewsByType(this.mViewGroupRight, cls);
                    if (views.size() > 0) {
                        View[] views1 = new View[views.size()];
                        views.toArray(views1);
                        XTouchAreaUtils.extendTouchAreaAsParentSameSize(views1, this);
                    }
                }
                return;
            }
            XTouchAreaUtils.extendTouchAreaAsParentSameSize(viewGroup);
        }
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
