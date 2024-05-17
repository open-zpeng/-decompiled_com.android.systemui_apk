package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.widget.XTabLayout;
import org.json.JSONObject;
/* loaded from: classes25.dex */
public class XTabsBar extends XRelativeLayout implements IVuiElementListener {
    private XImageButton mIconBtn;
    private ViewGroup mIconBtnLay;
    private OnTabsBarClickListener mOnTabChangeListener;
    private XTabLayout mTabLayout;

    /* loaded from: classes25.dex */
    public interface OnTabsBarClickListener extends XTabLayout.OnTabChangeListener {
        void onTabsBarCloseClick(View view);
    }

    public XTabsBar(Context context) {
        this(context, null);
    }

    public XTabsBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTabsBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XTabsBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int defStyleAttr2;
        Resources.Theme theme = getContext().getTheme();
        if (defStyleAttr != 0) {
            defStyleAttr2 = defStyleAttr;
        } else {
            defStyleAttr2 = R.style.XTabsBarAppearance_Left;
        }
        TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.XTabsBar, defStyleAttr2, defStyleAttr2);
        float tabMarginStart = typedArray.getDimension(R.styleable.XTabsBar_tabsBarMarginStart, dp(24));
        float tabGap = typedArray.getDimension(R.styleable.XTabsBar_tabsBarTabGap, 0.0f);
        float tabWidth = typedArray.getDimension(R.styleable.XTabsBar_tabsBarTabWidth, dp(192));
        float tabHeight = typedArray.getDimension(R.styleable.XTabsBar_tabsBarTabHeight, dp(130));
        int tabsBarBtnVisibility = typedArray.getInteger(R.styleable.XTabsBar_tabsBarBtnVisibility, 0);
        int tabsBarTitleVisibility = typedArray.getInteger(R.styleable.XTabsBar_tabsBarTitleVisibility, 8);
        typedArray.recycle();
        LayoutInflater.from(context).inflate(R.layout.x_tabsbar, this);
        this.mIconBtn = (XImageButton) findViewById(R.id.x_tabsbar_btn_close);
        this.mIconBtn.setVisibility(tabsBarBtnVisibility);
        this.mIconBtn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTabsBar$p4pps3QhNrK50K5MT1dALhVk4Qw
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                XTabsBar.this.lambda$initView$0$XTabsBar(view);
            }
        });
        this.mIconBtnLay = (ViewGroup) findViewById(R.id.x_x_tabsbar_btn_close_lay);
        XTextView titleView = (XTextView) findViewById(R.id.x_tabsbar_tv_title);
        titleView.setVisibility(tabsBarTitleVisibility);
        this.mTabLayout = new XTabLayout(context, attrs, defStyleAttr2, defStyleRes);
        int count = this.mTabLayout.getTabCount();
        int w = (int) ((count * tabWidth) + ((count - 1) * tabGap));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (count == 0) {
            int w2 = dm.widthPixels;
            w = (int) (w2 - tabMarginStart);
        }
        Configuration mConfiguration = getResources().getConfiguration();
        int defStyleAttr3 = mConfiguration.orientation;
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(w, (int) tabHeight);
        if (defStyleAttr3 == 1) {
            p.addRule(14);
        } else {
            p.addRule(9);
            p.leftMargin = (int) tabMarginStart;
        }
        addView(this.mTabLayout, p);
        this.mTabLayout.setOnTabChangeListener(new XTabLayout.OnTabChangeListener() { // from class: com.xiaopeng.xui.widget.XTabsBar.1
            @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
            public boolean onInterceptTabChange(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
                if (XTabsBar.this.mOnTabChangeListener != null) {
                    return XTabsBar.this.mOnTabChangeListener.onInterceptTabChange(tabLayout, index, tabChange, fromUser);
                }
                return false;
            }

            @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
            public void onTabChangeStart(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
                if (XTabsBar.this.mOnTabChangeListener != null) {
                    XTabsBar.this.mOnTabChangeListener.onTabChangeStart(tabLayout, index, tabChange, fromUser);
                }
            }

            @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
            public void onTabChangeEnd(XTabLayout tabLayout, int index, boolean tabChange, boolean fromUser) {
                if (XTabsBar.this.mOnTabChangeListener != null) {
                    XTabsBar.this.mOnTabChangeListener.onTabChangeEnd(tabLayout, index, tabChange, fromUser);
                }
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$XTabsBar(View v) {
        OnTabsBarClickListener onTabsBarClickListener = this.mOnTabChangeListener;
        if (onTabsBarClickListener != null) {
            onTabsBarClickListener.onTabsBarCloseClick(v);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mIconBtn, this.mIconBtnLay);
    }

    protected int dp(int dp) {
        return (int) TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics());
    }

    public void setOnTabsClickListener(OnTabsBarClickListener listener) {
        this.mOnTabChangeListener = listener;
    }

    public int getTabCount() {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            return xTabLayout.getTabCount();
        }
        return 0;
    }

    public void addTab(String title) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.addTab(title);
        }
    }

    public void addTab(String title, int index) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.addTab(title, index);
        }
    }

    public void updateTabTitle(int index, String title) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.updateTabTitle(index, title);
        }
    }

    public void removeTab(int index) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.removeTab(index);
        }
    }

    public void removeTab(int index, int selectIndex) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.removeTab(index, selectIndex);
        }
    }

    public CharSequence getTabTitle(int index) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            return xTabLayout.getTabTitle(index);
        }
        return "";
    }

    public void setTabLayoutVisible(boolean isVisible) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.setVisibility(isVisible ? 0 : 8);
        }
    }

    public void selectTab(int index, boolean animator) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.selectTab(index, animator);
        }
    }

    public void selectTab(int index) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.selectTab(index, true);
        }
    }

    public int getSelectedTabIndex() {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            return xTabLayout.getSelectedTabIndex();
        }
        return -1;
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.setEnabled(enabled);
        }
    }

    @Override // android.view.View
    public boolean isEnabled() {
        XTabLayout xTabLayout = this.mTabLayout;
        return xTabLayout != null && xTabLayout.isEnabled();
    }

    public void setEnabled(boolean enabled, int position) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.setEnabled(enabled, position);
        }
    }

    public boolean isEnabled(int position) {
        XTabLayout xTabLayout = this.mTabLayout;
        return xTabLayout != null && xTabLayout.isEnabled(position);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String s, IVuiElementBuilder iVuiElementBuilder) {
        try {
            this.mTabLayout.setVuiElementType(VuiElementType.XTABLAYOUT);
            this.mTabLayout.setVuiMode(VuiMode.NORMAL);
            if (this.mIconBtn.getVisibility() != 0) {
                this.mIconBtn.setVuiMode(VuiMode.DISABLED);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(VuiConstants.PROPS_GENERALACT, VuiConstants.GENERALACT_CLOSE);
                this.mIconBtn.setVuiProps(jsonObject);
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
