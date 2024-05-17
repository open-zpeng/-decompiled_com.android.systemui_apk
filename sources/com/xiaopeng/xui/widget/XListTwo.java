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
public class XListTwo extends XRelativeLayout {
    private int mLeftRightTouchFull;
    private ViewGroup mLeftView;
    private ViewStub mLeftViewStub;
    private TextView mNum;
    private ViewStub mRightViewStub;
    private ImageView mTag;
    private ViewStub mTagCustomViewStub;
    private TextView mText;
    private TextView mTextSub;
    private ViewGroup mTvLay;
    private ViewGroup mViewGroupLeft;
    private ViewGroup mViewGroupRight;

    public XListTwo(Context context) {
        this(context, null);
    }

    public XListTwo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XListTwo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XListTwo(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.x_list_two, this);
        initView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XListTwo);
        String text = a.getString(R.styleable.XListTwo_list_two_text);
        setText(text);
        String textSub = a.getString(R.styleable.XListTwo_list_two_text_sub);
        setTextSub(textSub);
        int lines = a.getInt(R.styleable.XListTwo_list_text_sub_lines, 1);
        this.mTextSub.setMaxLines(lines);
        int left = a.getResourceId(R.styleable.XListTwo_list_two_left, -1);
        if (left != -1) {
            this.mLeftViewStub.setLayoutResource(left);
            View view = this.mLeftViewStub.inflate();
            if (view instanceof ViewGroup) {
                this.mViewGroupLeft = (ViewGroup) view;
            }
        }
        int right = a.getResourceId(R.styleable.XListTwo_list_two_right, -1);
        if (right != -1) {
            this.mRightViewStub.setLayoutResource(right);
            View view2 = this.mRightViewStub.inflate();
            if (view2 instanceof ViewGroup) {
                this.mViewGroupRight = (ViewGroup) view2;
            }
        }
        int tagIcon = a.getResourceId(R.styleable.XListTwo_list_two_tag_icon, -1);
        if (tagIcon != -1) {
            setTagIcon(tagIcon);
        }
        boolean tag = a.getBoolean(R.styleable.XListTwo_list_two_tag, false);
        showIconTag(tag);
        int tag_custom = a.getResourceId(R.styleable.XListTwo_list_two_tag_custom, -1);
        if (tag_custom != -1) {
            this.mTagCustomViewStub.setLayoutResource(tag_custom);
            this.mTagCustomViewStub.inflate();
        }
        this.mLeftRightTouchFull = a.getInt(R.styleable.XListTwo_list_two_left_right_touch_full, 0);
        a.recycle();
        if (right != -1) {
            View x_list_tv_lay = findViewById(R.id.x_list_tv_lay);
            int marginEnd = (int) getResources().getDimension(R.dimen.x_list_tv_margin_end);
            x_list_tv_lay.setPadding(x_list_tv_lay.getPaddingLeft(), x_list_tv_lay.getPaddingTop(), marginEnd, x_list_tv_lay.getPaddingBottom());
            TextView textView = this.mTextSub;
            textView.setPadding(textView.getPaddingLeft(), this.mTextSub.getPaddingTop(), marginEnd, this.mTextSub.getPaddingBottom());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        extendTouchLeft();
        extendTouchRight();
    }

    private /* synthetic */ void lambda$onAttachedToWindow$0() {
        logD("list-two this w " + getWidth() + " h " + getHeight());
    }

    private void extendTouchLeft() {
        Class<?>[] clsArr;
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
                return;
            }
            XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mLeftView);
        }
    }

    private /* synthetic */ void lambda$extendTouchLeft$1() {
        logD("list-two mLeftView w " + this.mLeftView.getWidth() + " h " + this.mLeftView.getHeight());
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

    private /* synthetic */ void lambda$extendTouchRight$2() {
        logD("list-two mViewGroupRight w " + this.mViewGroupRight.getWidth() + " h " + this.mViewGroupRight.getHeight());
    }

    private void initView() {
        this.mText = (TextView) findViewById(R.id.x_list_tv);
        this.mNum = (TextView) findViewById(R.id.x_list_num);
        this.mLeftView = (ViewGroup) findViewById(R.id.x_list_left_lay);
        this.mTvLay = (ViewGroup) findViewById(R.id.x_list_tv_lay);
        this.mTextSub = (TextView) findViewById(R.id.x_list_tv_sub);
        this.mLeftViewStub = (ViewStub) findViewById(R.id.x_list_left);
        this.mRightViewStub = (ViewStub) findViewById(R.id.x_list_right);
        this.mTag = (ImageView) findViewById(R.id.x_list_tag);
        this.mTagCustomViewStub = (ViewStub) findViewById(R.id.x_list_tag_custom_lay);
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
        ViewGroup viewGroup3 = this.mTvLay;
        if (viewGroup3 != null) {
            ViewGroup.MarginLayoutParams lay3 = (ViewGroup.MarginLayoutParams) viewGroup3.getLayoutParams();
            if (show) {
                lay3.leftMargin = leftWidthForNum;
            } else {
                lay3.leftMargin = leftWidth;
            }
            this.mTvLay.setLayoutParams(lay3);
        }
        extendTouchLeft();
    }

    public void setText(@Nullable CharSequence text) {
        this.mText.setText(text);
        this.mText.requestLayout();
    }

    public void setTextSub(@Nullable CharSequence text) {
        this.mTextSub.setText(text);
    }

    public void setTagIcon(@DrawableRes int res) {
        this.mTag.setImageResource(res);
    }

    public void showIconTag(boolean show) {
        this.mTag.setVisibility(show ? 0 : 8);
    }

    public boolean isShowIconTag() {
        return this.mTag.getVisibility() == 0;
    }

    public void setTagOnClickListener(View.OnClickListener listener) {
        this.mTag.setOnClickListener(listener);
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
