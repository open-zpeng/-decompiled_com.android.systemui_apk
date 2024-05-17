package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XFrameLayout;
@Deprecated
/* loaded from: classes25.dex */
class XDialogList extends XFrameLayout {
    private ListView mListView;

    public XDialogList(Context context) {
        this(context, null);
    }

    public XDialogList(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XDialogList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XDialogList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.x_dialog_list, this);
        this.mListView = (ListView) findViewById(R.id.x_dialog_listview);
    }

    public void setSingleChoiceItems(CharSequence[] items, int checkedItem, AdapterView.OnItemClickListener listener) {
        this.mListView.setChoiceMode(1);
        this.mListView.setOnItemClickListener(listener);
        CheckedItemAdapter adapter = new CheckedItemAdapter(getContext(), R.layout.x_dialog_item_singlechoice, R.id.x_dialog_item_text1, items);
        this.mListView.setAdapter((ListAdapter) adapter);
        this.mListView.setItemChecked(checkedItem, true);
    }

    /* loaded from: classes25.dex */
    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        CheckedItemAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        @NonNull
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ColorStateList textColor = view.getContext().getResources().getColorStateList(R.color.x_dialog_choice_text_color, view.getContext().getTheme());
            ((TextView) view).setTextColor(textColor);
            return view;
        }
    }
}
