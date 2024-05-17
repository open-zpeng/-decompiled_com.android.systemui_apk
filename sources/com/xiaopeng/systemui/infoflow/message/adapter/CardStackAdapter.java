package com.xiaopeng.systemui.infoflow.message.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.AppCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.AutoParkingCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.CallCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.CarCheckHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.CarControlCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.CruiseSceneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.DefaultCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.ExploreSceneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.HomeCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.MusicCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.NaviSceneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.NotificationCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.PhoneCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolderFactory;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.RecommendCardHolder;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.SmartCardHolder;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class CardStackAdapter extends RecyclerView.Adapter<BaseCardHolder> implements BaseCardHolder.OnCardClickedListener {
    private static final String TAG = CardStackAdapter.class.getSimpleName();
    private CardEntry mAIPushCardEntry;
    private AsyncListDiffer<CardEntry> mDiffer;
    private OnItemClickListener mOnItemClickListener;
    private DiffUtil.ItemCallback<CardEntry> diffCallback = new DiffUtil.ItemCallback<CardEntry>() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.CardStackAdapter.1
        @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
        public boolean areItemsTheSame(CardEntry oldItem, CardEntry newItem) {
            return oldItem.type == newItem.type && oldItem.key.equals(newItem.key);
        }

        @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
        public boolean areContentsTheSame(CardEntry oldItem, CardEntry newItem) {
            return !(newItem.type == 5 && oldItem.when != newItem.when && CardStackAdapter.this.isMissedCall(newItem.content)) && oldItem.title.equals(newItem.title) && oldItem.content.equals(newItem.content) && oldItem.status == newItem.status && oldItem.extraData.equals(newItem.extraData) && oldItem.priority == newItem.priority && oldItem.importance == newItem.importance;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.ItemCallback
        @Nullable
        public Object getChangePayload(@NonNull CardEntry oldItem, @NonNull CardEntry newItem) {
            return newItem;
        }
    };
    private PushCardHolderFactory mPushCardHolderFactory = new PushCardHolderFactory();
    private List<CardEntry> mData = new ArrayList();

    /* loaded from: classes24.dex */
    public interface OnItemClickListener {
        void onItemClick(CardEntry cardEntry);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mDiffer = new AsyncListDiffer<>(new CardListUpdateCallback(recyclerView, this), new AsyncDifferConfig.Builder(this.diffCallback).build());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public BaseCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        String str = TAG;
        Logger.d(str, "onCreateViewHolder : " + viewType);
        if (viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
            return new NotificationCardHolder(itemView);
        } else if (viewType != 17) {
            if (viewType == 19) {
                View itemView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_checked, parent, false);
                return new CarCheckHolder(itemView2);
            } else if (viewType == 22) {
                View itemView3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_launcher, parent, false);
                return new HomeCardHolder(itemView3);
            } else if (viewType == 4) {
                View itemView4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_call, parent, false);
                return new CallCardHolder(itemView4);
            } else if (viewType == 5) {
                View itemView5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_phone, parent, false);
                return new PhoneCardHolder(itemView5);
            } else if (viewType == 7) {
                View itemView6 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_control, parent, false);
                return new CarControlCardHolder(itemView6);
            } else if (viewType == 8) {
                View itemView7 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_default, parent, false);
                return new AppCardHolder(itemView7);
            } else if (viewType == 9) {
                View itemView8 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_music, parent, false);
                return new MusicCardHolder(itemView8);
            } else {
                switch (viewType) {
                    case 24:
                        View itemView9 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_navi, parent, false);
                        return new NaviSceneCardHolder(itemView9);
                    case 25:
                        View itemView10 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cruise, parent, false);
                        return new CruiseSceneCardHolder(itemView10);
                    case 26:
                        View itemView11 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recommend, parent, false);
                        return new RecommendCardHolder(itemView11);
                    case 27:
                        View itemView12 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_explore, parent, false);
                        return new ExploreSceneCardHolder(itemView12);
                    case 28:
                        View itemView13 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_smart, parent, false);
                        return new SmartCardHolder(itemView13);
                    case 29:
                        View itemView14 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_auto_parking, parent, false);
                        return new AutoParkingCardHolder(itemView14);
                    default:
                        View itemView15 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_default, parent, false);
                        return new DefaultCardHolder(itemView15);
                }
            }
        } else {
            return this.mPushCardHolderFactory.createCardHolder(parent, this.mAIPushCardEntry);
        }
    }

    public void addItem(CardEntry cardEntry) {
        String str = TAG;
        Logger.d(str, "addItem entry=" + cardEntry.toString());
        this.mData.add(0, cardEntry);
        notifyItemInserted(0);
    }

    public void removeItem(CardEntry cardEntry) {
        String str = TAG;
        Logger.d(str, "removeItem entry=" + cardEntry.toString());
        int index = getIndex(cardEntry);
        if (index > 0 && index < this.mData.size()) {
            this.mData.remove(index);
            notifyItemRemoved(index);
        }
    }

    private int getIndex(CardEntry cardEntry) {
        for (int i = 0; i < this.mData.size(); i++) {
            CardEntry entry = this.mData.get(i);
            if (entry.key == cardEntry.key) {
                return i;
            }
        }
        return -1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(BaseCardHolder holder, int position) {
        holder.bindData(getItem(position));
        holder.setCardClickedListener(new BaseCardHolder.OnCardClickedListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.-$$Lambda$R41wYXTmm6Hu5-JW7Z5vwx1hDE4
            @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder.OnCardClickedListener
            public final void onCardClicked(CardEntry cardEntry) {
                CardStackAdapter.this.onCardClicked(cardEntry);
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        int viewType = getItem(position).type;
        if (viewType == 17) {
            this.mAIPushCardEntry = getItem(position);
        }
        return viewType;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mDiffer.getCurrentList().size();
    }

    private CardEntry getItem(int position) {
        return this.mDiffer.getCurrentList().get(position);
    }

    public void submitList(List<CardEntry> list) {
        String str = TAG;
        Logger.d(str, "submit new list size = " + list.size());
        List<CardEntry> newList = new ArrayList<>();
        newList.addAll(list);
        AsyncListDiffer<CardEntry> asyncListDiffer = this.mDiffer;
        if (asyncListDiffer != null) {
            asyncListDiffer.submitList(newList);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder.OnCardClickedListener
    public void onCardClicked(CardEntry cardData) {
        OnItemClickListener onItemClickListener = this.mOnItemClickListener;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(cardData);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMissedCall(String title) {
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        return title.contains(SystemUIApplication.getContext().getString(R.string.missed_call));
    }
}
