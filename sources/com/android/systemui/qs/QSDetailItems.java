package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
/* loaded from: classes21.dex */
public class QSDetailItems extends FrameLayout {
    private final Adapter mAdapter;
    private Callback mCallback;
    private final Context mContext;
    private View mEmpty;
    private ImageView mEmptyIcon;
    private TextView mEmptyText;
    private final H mHandler;
    private AutoSizingList mItemList;
    private Item[] mItems;
    private boolean mItemsVisible;
    private final int mQsDetailIconOverlaySize;
    private String mTag;
    private static final String TAG = "QSDetailItems";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    /* loaded from: classes21.dex */
    public interface Callback {
        void onDetailItemClick(Item item);

        void onDetailItemDisconnect(Item item);
    }

    /* loaded from: classes21.dex */
    public static class Item {
        public boolean canDisconnect;
        public QSTile.Icon icon;
        public int icon2 = -1;
        public int iconResId;
        public CharSequence line1;
        public CharSequence line2;
        public Drawable overlay;
        public Object tag;
    }

    public QSDetailItems(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandler = new H();
        this.mAdapter = new Adapter();
        this.mItemsVisible = true;
        this.mContext = context;
        this.mTag = TAG;
        this.mQsDetailIconOverlaySize = (int) getResources().getDimension(R.dimen.qs_detail_icon_overlay_size);
    }

    public static QSDetailItems convertOrInflate(Context context, View convert, ViewGroup parent) {
        if (convert instanceof QSDetailItems) {
            return (QSDetailItems) convert;
        }
        return (QSDetailItems) LayoutInflater.from(context).inflate(R.layout.qs_detail_items, parent, false);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mItemList = (AutoSizingList) findViewById(16908298);
        this.mItemList.setVisibility(8);
        this.mItemList.setAdapter(this.mAdapter);
        this.mEmpty = findViewById(16908292);
        this.mEmpty.setVisibility(8);
        this.mEmptyText = (TextView) this.mEmpty.findViewById(16908310);
        this.mEmptyIcon = (ImageView) this.mEmpty.findViewById(16908294);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FontSizeUtils.updateFontSize(this.mEmptyText, R.dimen.qs_detail_empty_text_size);
        int count = this.mItemList.getChildCount();
        for (int i = 0; i < count; i++) {
            View item = this.mItemList.getChildAt(i);
            FontSizeUtils.updateFontSize(item, 16908310, R.dimen.qs_detail_item_primary_text_size);
            FontSizeUtils.updateFontSize(item, 16908304, R.dimen.qs_detail_item_secondary_text_size);
        }
    }

    public void setTagSuffix(String suffix) {
        this.mTag = "QSDetailItems." + suffix;
    }

    public void setEmptyState(final int icon, final int text) {
        this.mEmptyIcon.post(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSDetailItems$8UkcDK0xyJROkQ0Pv0OF8HNZO94
            @Override // java.lang.Runnable
            public final void run() {
                QSDetailItems.this.lambda$setEmptyState$0$QSDetailItems(icon, text);
            }
        });
    }

    public /* synthetic */ void lambda$setEmptyState$0$QSDetailItems(int icon, int text) {
        this.mEmptyIcon.setImageResource(icon);
        this.mEmptyText.setText(text);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DEBUG) {
            Log.d(this.mTag, "onAttachedToWindow");
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (DEBUG) {
            Log.d(this.mTag, "onDetachedFromWindow");
        }
        this.mCallback = null;
    }

    public void setCallback(Callback callback) {
        this.mHandler.removeMessages(2);
        this.mHandler.obtainMessage(2, callback).sendToTarget();
    }

    public void setItems(Item[] items) {
        this.mHandler.removeMessages(1);
        this.mHandler.obtainMessage(1, items).sendToTarget();
    }

    public void setItemsVisible(boolean visible) {
        this.mHandler.removeMessages(3);
        this.mHandler.obtainMessage(3, visible ? 1 : 0, 0).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetCallback(Callback callback) {
        this.mCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetItems(Item[] items) {
        int itemCount = items != null ? items.length : 0;
        this.mEmpty.setVisibility(itemCount == 0 ? 0 : 8);
        this.mItemList.setVisibility(itemCount == 0 ? 8 : 0);
        this.mItems = items;
        this.mAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetItemsVisible(boolean visible) {
        if (this.mItemsVisible == visible) {
            return;
        }
        this.mItemsVisible = visible;
        for (int i = 0; i < this.mItemList.getChildCount(); i++) {
            this.mItemList.getChildAt(i).setVisibility(this.mItemsVisible ? 0 : 4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class Adapter extends BaseAdapter {
        private Adapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (QSDetailItems.this.mItems != null) {
                return QSDetailItems.this.mItems.length;
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return QSDetailItems.this.mItems[position];
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return 0L;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View view, ViewGroup parent) {
            int i;
            final Item item = QSDetailItems.this.mItems[position];
            if (view == null) {
                view = LayoutInflater.from(QSDetailItems.this.mContext).inflate(R.layout.qs_detail_item, parent, false);
            }
            view.setVisibility(QSDetailItems.this.mItemsVisible ? 0 : 4);
            ImageView iv = (ImageView) view.findViewById(16908294);
            if (item.icon != null) {
                iv.setImageDrawable(item.icon.getDrawable(iv.getContext()));
            } else {
                iv.setImageResource(item.iconResId);
            }
            iv.getOverlay().clear();
            if (item.overlay != null) {
                item.overlay.setBounds(0, 0, QSDetailItems.this.mQsDetailIconOverlaySize, QSDetailItems.this.mQsDetailIconOverlaySize);
                iv.getOverlay().add(item.overlay);
            }
            TextView title = (TextView) view.findViewById(16908310);
            title.setText(item.line1);
            TextView summary = (TextView) view.findViewById(16908304);
            boolean twoLines = !TextUtils.isEmpty(item.line2);
            title.setMaxLines(twoLines ? 1 : 2);
            if (twoLines) {
                i = 0;
            } else {
                i = 8;
            }
            summary.setVisibility(i);
            summary.setText(twoLines ? item.line2 : null);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.QSDetailItems.Adapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (QSDetailItems.this.mCallback != null) {
                        QSDetailItems.this.mCallback.onDetailItemClick(item);
                    }
                }
            });
            ImageView icon2 = (ImageView) view.findViewById(16908296);
            if (item.canDisconnect) {
                icon2.setImageResource(R.drawable.ic_qs_cancel);
                icon2.setVisibility(0);
                icon2.setClickable(true);
                icon2.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.QSDetailItems.Adapter.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (QSDetailItems.this.mCallback != null) {
                            QSDetailItems.this.mCallback.onDetailItemDisconnect(item);
                        }
                    }
                });
            } else if (item.icon2 != -1) {
                icon2.setVisibility(0);
                icon2.setImageResource(item.icon2);
                icon2.setClickable(false);
            } else {
                icon2.setVisibility(8);
            }
            return view;
        }
    }

    /* loaded from: classes21.dex */
    private class H extends Handler {
        private static final int SET_CALLBACK = 2;
        private static final int SET_ITEMS = 1;
        private static final int SET_ITEMS_VISIBLE = 3;

        public H() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                QSDetailItems.this.handleSetItems((Item[]) msg.obj);
            } else if (msg.what == 2) {
                QSDetailItems.this.handleSetCallback((Callback) msg.obj);
            } else if (msg.what == 3) {
                QSDetailItems.this.handleSetItemsVisible(msg.arg1 != 0);
            }
        }
    }
}
