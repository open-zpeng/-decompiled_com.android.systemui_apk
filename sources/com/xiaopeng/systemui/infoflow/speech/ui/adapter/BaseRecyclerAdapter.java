package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.FeedUIEvent;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "BaseRecyclerAdapter";
    public Context mContext;
    private List<T> mData;
    protected OnItemClickListener mOnItemClickListener;

    /* loaded from: classes24.dex */
    public interface OnItemClickListener {
        void onItemClick(BaseRecyclerAdapter baseRecyclerAdapter, View view, int i);
    }

    public abstract void bindData(BaseRecyclerAdapter<T>.BaseViewHolder baseViewHolder, T t, int i);

    public abstract int getItemLayoutId();

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public void addData(T data) {
        this.mData.add(data);
        notifyItemInserted(this.mData.size());
    }

    public void addData(List<T> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setNewData(List<T> data) {
        List<T> list = this.mData;
        if (list == null) {
            this.mData = new ArrayList();
        } else {
            list.clear();
        }
        if (data != null) {
            this.mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        List<T> list = this.mData;
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    public List<T> getData() {
        return this.mData;
    }

    public T getItem(int position) {
        if (position >= 0 && position < this.mData.size()) {
            return this.mData.get(position);
        }
        return null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(getItemLayoutId(), parent, false);
        BaseRecyclerAdapter<T>.BaseViewHolder holder = new BaseViewHolder(view);
        bindViewClickListener(holder);
        return holder;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        T t = this.mData.get(position);
        bindData(holder, t, position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<T> list = this.mData;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public class BaseViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> views;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.views = new SparseArray<>();
        }

        public View getView(int id) {
            View view = this.views.get(id);
            if (view == null) {
                View view2 = this.itemView.findViewById(id);
                this.views.put(id, view2);
                return view2;
            }
            return view;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public final OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    private void bindViewClickListener(final BaseRecyclerAdapter<T>.BaseViewHolder baseViewHolder) {
        View view;
        if (baseViewHolder != null && (view = baseViewHolder.itemView) != null && getOnItemClickListener() != null) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    BaseRecyclerAdapter.this.setOnItemClick(v, baseViewHolder.getLayoutPosition());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnItemClick(View v, int position) {
        getOnItemClickListener().onItemClick(this, v, position);
    }

    public void sendSelectedEvent(int position) {
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = this.mContext.getPackageName();
        feedListUIValue.index = position + 1;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_SELECT, FeedListUIValue.toJson(feedListUIValue));
    }

    public void sendFocusedEvent(int position) {
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = this.mContext.getPackageName();
        feedListUIValue.index = position + 1;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_FOCUS, FeedListUIValue.toJson(feedListUIValue));
    }
}
