package com.xiaopeng.systemui.infoflow.message.adapter;

import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.message.helper.AiPushCardHelper;
import com.xiaopeng.systemui.infoflow.message.manager.ModeManager;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
/* loaded from: classes24.dex */
class CardListUpdateCallback implements ListUpdateCallback {
    private static final String TAG = CardListUpdateCallback.class.getSimpleName();
    private boolean insertCardScroll;
    private final RecyclerView.Adapter mAdapter;
    private ModeManager mModeManager = ModeManager.getInstance();
    private final RecyclerView mRecyclerView;
    private boolean updateCardScroll;

    public CardListUpdateCallback(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
        this.mRecyclerView = recyclerView;
        initConfig();
    }

    private void initConfig() {
        InfoFlowConfigDao.Config config = InfoFlowConfigDao.getInstance().getConfig();
        if (config != null) {
            this.updateCardScroll = config.cardUpdateScroll;
            this.insertCardScroll = config.cardInsertScroll;
        }
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onInserted(int position, int count) {
        String str = TAG;
        Logger.d(str, "onInserted --" + position);
        if (this.mModeManager.isNormalMode()) {
            this.mAdapter.notifyItemRangeInserted(position, count);
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView instanceof CardStack) {
                AiPushCardHelper.scrollAndFocusNewItem(position, (CardStack) recyclerView, this.insertCardScroll);
            }
        }
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onRemoved(int position, int count) {
        String str = TAG;
        Logger.d(str, "onRemoved ----" + position);
        if (this.mModeManager.isNormalMode()) {
            this.mAdapter.notifyItemRangeRemoved(position, count);
        }
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onMoved(int fromPosition, int toPosition) {
        String str = TAG;
        Logger.d(str, "onMoved -" + fromPosition + "-to-" + toPosition);
        if (this.mModeManager.isNormalMode()) {
            this.mAdapter.notifyItemMoved(fromPosition, toPosition);
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView instanceof CardStack) {
                AiPushCardHelper.scrollAndFocusMovedItem(fromPosition, toPosition, (CardStack) recyclerView, this.insertCardScroll);
            }
        }
    }

    @Override // androidx.recyclerview.widget.ListUpdateCallback
    public void onChanged(int position, int count, Object payload) {
        String str = TAG;
        Logger.d(str, "onChanged --" + position);
        if (this.mModeManager.isNormalMode()) {
            this.mAdapter.notifyItemRangeChanged(position, count, payload);
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView instanceof CardStack) {
                AiPushCardHelper.scrollAndFocusChangedItem(position, (CardStack) recyclerView, this.updateCardScroll);
            }
        }
    }
}
