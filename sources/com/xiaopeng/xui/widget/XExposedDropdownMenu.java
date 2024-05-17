package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XuiUtils;
/* loaded from: classes25.dex */
public class XExposedDropdownMenu extends XTextView implements AdapterView.OnItemClickListener {
    private static final String TAG = XExposedDropdownMenu.class.getSimpleName();
    private ArrayAdapter<String> mAdapter;
    private int mCurrentSelection;
    private final int mHoriOffset;
    private ListView mListView;
    private OnItemSelectedListener mOnItemClickListener;
    private PopupWindow mPopupWindow;
    private final int mVerticalOffset;

    /* loaded from: classes25.dex */
    public interface OnItemSelectedListener {
        void onItemSelected(int i, String str);
    }

    public XExposedDropdownMenu(Context context) {
        this(context, null);
    }

    public XExposedDropdownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XExposedDropdownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.mVerticalOffset = XuiUtils.dip2px(16.0f) - getResources().getDimensionPixelOffset(R.dimen.x_exposed_dropdown_menu_inset_vertical);
        this.mHoriOffset = getResources().getDimensionPixelOffset(R.dimen.x_exposed_dropdown_menu_inset_horizontal);
        this.mCurrentSelection = 0;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getResources().obtainAttributes(attrs, R.styleable.XExposedDropdownMenu);
        CharSequence[] array = a.getTextArray(R.styleable.XExposedDropdownMenu_edmDropdownEntries);
        int selection = a.getInteger(R.styleable.XExposedDropdownMenu_edmDropdownSelection, 0);
        this.mAdapter = new ArrayAdapter<>(getContext(), R.layout.x_exposed_dropdown_menu_item);
        createPopupWindow();
        setEntries(array);
        setSelection(selection);
        setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.XExposedDropdownMenu.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                XExposedDropdownMenu.this.show();
            }
        });
        a.recycle();
    }

    private void createPopupWindow() {
        this.mPopupWindow = new PopupWindow(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.x_exposed_dropdown_menu_list, (ViewGroup) null);
        this.mPopupWindow.setContentView(view);
        this.mListView = (ListView) view.findViewById(R.id.list_view);
        this.mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        this.mPopupWindow.setOutsideTouchable(true);
        this.mPopupWindow.setClippingEnabled(false);
        this.mListView.setAdapter((ListAdapter) this.mAdapter);
        this.mListView.setOnItemClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show() {
        this.mPopupWindow.setWidth(getWidth() - (this.mHoriOffset * 2));
        int[] outLocation = new int[2];
        getLocationOnScreen(outLocation);
        int bottom = outLocation[1] + getHeight() + this.mPopupWindow.getHeight() + this.mVerticalOffset;
        int screenBottom = getContext().getResources().getDisplayMetrics().heightPixels;
        int yOffset = this.mVerticalOffset;
        if (bottom > screenBottom) {
            yOffset = ((-getHeight()) - this.mPopupWindow.getHeight()) - this.mVerticalOffset;
        }
        this.mPopupWindow.showAsDropDown(this, this.mHoriOffset, yOffset);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelection(position);
        String title = getTitleWithIndex(position);
        OnItemSelectedListener onItemSelectedListener = this.mOnItemClickListener;
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(position, title);
        }
        if (this.mPopupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
        }
    }

    public void setSelection(int position) {
        if (position < 0 || position >= this.mAdapter.getCount()) {
            return;
        }
        this.mCurrentSelection = position;
        setText(getTitleWithIndex(position));
    }

    public int getSelection() {
        return this.mCurrentSelection;
    }

    public String getTitleWithIndex(int index) {
        if (index >= 0 && index < this.mAdapter.getCount()) {
            return this.mAdapter.getItem(index);
        }
        return "";
    }

    public String getSelectionTitle() {
        return getTitleWithIndex(getSelection());
    }

    public void setEntries(String[] array) {
        this.mAdapter.clear();
        if (array != null && array.length > 0) {
            this.mAdapter.addAll(array);
            setSelection(0);
        }
        setDropdownHeight(this.mAdapter.getCount());
    }

    public void setEntries(CharSequence[] array) {
        this.mAdapter.clear();
        if (array != null && array.length > 0) {
            for (CharSequence c : array) {
                this.mAdapter.add(c.toString());
            }
            setSelection(0);
        }
        setDropdownHeight(this.mAdapter.getCount());
    }

    private void setDropdownHeight(int itemSize) {
        int windowHeight;
        if (itemSize > 5) {
            windowHeight = XuiUtils.dip2px(408.0f);
        } else if (itemSize > 0) {
            windowHeight = XuiUtils.dip2px((itemSize * 80) + ((itemSize - 1) * 2));
        } else {
            windowHeight = 0;
        }
        this.mPopupWindow.setHeight(windowHeight);
    }

    public void setOnItemClickListener(OnItemSelectedListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XTextView, android.widget.TextView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (XThemeManager.isThemeChanged(newConfig)) {
            setListViewTheme();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XTextView, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setListViewTheme();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XTextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setListViewTheme();
    }

    private void setListViewTheme() {
        ListView listView = this.mListView;
        if (listView != null) {
            listView.setBackground(getContext().getDrawable(R.drawable.x_exposed_dropdown_menu_list_bg));
            this.mListView.setDivider(getContext().getDrawable(R.drawable.x_exposed_dropdown_menu_divider));
        }
    }
}
