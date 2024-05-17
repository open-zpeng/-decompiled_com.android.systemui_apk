package com.android.systemui.qs.customize;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.customize.TileQueryHelper;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tileimpl.QSIconViewImpl;
import com.android.systemui.statusbar.notification.stack.StackStateAnimator;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class TileAdapter extends RecyclerView.Adapter<Holder> implements TileQueryHelper.TileStateListener {
    private static final int ACTION_ADD = 1;
    private static final int ACTION_MOVE = 2;
    private static final int ACTION_NONE = 0;
    private static final long DIVIDER_ID = 20000;
    private static final long DRAG_LENGTH = 100;
    private static final float DRAG_SCALE = 1.2f;
    private static final long EDIT_ID = 10000;
    public static final long MOVE_DURATION = 150;
    private static final int TYPE_ACCESSIBLE_DROP = 2;
    private static final int TYPE_DIVIDER = 4;
    private static final int TYPE_EDIT = 1;
    private static final int TYPE_HEADER = 3;
    private static final int TYPE_TILE = 0;
    private int mAccessibilityFromIndex;
    private CharSequence mAccessibilityFromLabel;
    private final AccessibilityManager mAccessibilityManager;
    private List<TileQueryHelper.TileInfo> mAllTiles;
    private final Context mContext;
    private Holder mCurrentDrag;
    private List<String> mCurrentSpecs;
    private final RecyclerView.ItemDecoration mDecoration;
    private int mEditIndex;
    private QSTileHost mHost;
    private final int mMinNumTiles;
    private boolean mNeedsFocus;
    private List<TileQueryHelper.TileInfo> mOtherTiles;
    private int mTileDividerIndex;
    private final Handler mHandler = new Handler();
    private final List<TileQueryHelper.TileInfo> mTiles = new ArrayList();
    private int mAccessibilityAction = 0;
    private final GridLayoutManager.SpanSizeLookup mSizeLookup = new GridLayoutManager.SpanSizeLookup() { // from class: com.android.systemui.qs.customize.TileAdapter.5
        @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
        public int getSpanSize(int position) {
            int type = TileAdapter.this.getItemViewType(position);
            return (type == 1 || type == 4 || type == 3) ? 3 : 1;
        }
    };
    private final ItemTouchHelper.Callback mCallbacks = new ItemTouchHelper.Callback() { // from class: com.android.systemui.qs.customize.TileAdapter.6
        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState != 2) {
                viewHolder = null;
            }
            if (viewHolder == TileAdapter.this.mCurrentDrag) {
                return;
            }
            if (TileAdapter.this.mCurrentDrag != null) {
                int position = TileAdapter.this.mCurrentDrag.getAdapterPosition();
                if (position == -1) {
                    return;
                }
                TileQueryHelper.TileInfo info = (TileQueryHelper.TileInfo) TileAdapter.this.mTiles.get(position);
                TileAdapter.this.mCurrentDrag.mTileView.setShowAppLabel(position > TileAdapter.this.mEditIndex && !info.isSystem);
                TileAdapter.this.mCurrentDrag.stopDrag();
                TileAdapter.this.mCurrentDrag = null;
            }
            if (viewHolder != null) {
                TileAdapter.this.mCurrentDrag = (Holder) viewHolder;
                TileAdapter.this.mCurrentDrag.startDrag();
            }
            TileAdapter.this.mHandler.post(new Runnable() { // from class: com.android.systemui.qs.customize.TileAdapter.6.1
                @Override // java.lang.Runnable
                public void run() {
                    TileAdapter.this.notifyItemChanged(TileAdapter.this.mEditIndex);
                }
            });
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            int position = target.getAdapterPosition();
            if (position == 0 || position == -1) {
                return false;
            }
            return (TileAdapter.this.canRemoveTiles() || current.getAdapterPosition() >= TileAdapter.this.mEditIndex) ? position <= TileAdapter.this.mEditIndex + 1 : position < TileAdapter.this.mEditIndex;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1 || itemViewType == 3 || itemViewType == 4) {
                int dragFlags = makeMovementFlags(0, 0);
                return dragFlags;
            }
            return makeMovementFlags(15, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            if (from != 0 && from != -1 && to != 0 && to != -1) {
                return TileAdapter.this.move(from, to, target.itemView);
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }
    };
    private final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(this.mCallbacks);

    public TileAdapter(Context context) {
        this.mContext = context;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mDecoration = new TileItemDecoration(context);
        this.mMinNumTiles = context.getResources().getInteger(R.integer.quick_settings_min_num_tiles);
    }

    public void setHost(QSTileHost host) {
        this.mHost = host;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return this.mItemTouchHelper;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return this.mDecoration;
    }

    public void saveSpecs(QSTileHost host) {
        List<String> newSpecs = new ArrayList<>();
        clearAccessibilityState();
        for (int i = 1; i < this.mTiles.size() && this.mTiles.get(i) != null; i++) {
            newSpecs.add(this.mTiles.get(i).spec);
        }
        host.changeTiles(this.mCurrentSpecs, newSpecs);
        this.mCurrentSpecs = newSpecs;
    }

    private void clearAccessibilityState() {
        if (this.mAccessibilityAction == 1) {
            List<TileQueryHelper.TileInfo> list = this.mTiles;
            int i = this.mEditIndex - 1;
            this.mEditIndex = i;
            list.remove(i);
            this.mTileDividerIndex--;
            notifyDataSetChanged();
        }
        this.mAccessibilityAction = 0;
    }

    public void resetTileSpecs(QSTileHost host, List<String> specs) {
        host.changeTiles(this.mCurrentSpecs, specs);
        setTileSpecs(specs);
    }

    public void setTileSpecs(List<String> currentSpecs) {
        if (currentSpecs.equals(this.mCurrentSpecs)) {
            return;
        }
        this.mCurrentSpecs = currentSpecs;
        recalcSpecs();
    }

    @Override // com.android.systemui.qs.customize.TileQueryHelper.TileStateListener
    public void onTilesChanged(List<TileQueryHelper.TileInfo> tiles) {
        this.mAllTiles = tiles;
        recalcSpecs();
    }

    private void recalcSpecs() {
        List<TileQueryHelper.TileInfo> list;
        if (this.mCurrentSpecs == null || (list = this.mAllTiles) == null) {
            return;
        }
        this.mOtherTiles = new ArrayList(list);
        this.mTiles.clear();
        this.mTiles.add(null);
        for (int i = 0; i < this.mCurrentSpecs.size(); i++) {
            TileQueryHelper.TileInfo tile = getAndRemoveOther(this.mCurrentSpecs.get(i));
            if (tile != null) {
                this.mTiles.add(tile);
            }
        }
        this.mTiles.add(null);
        int i2 = 0;
        while (i2 < this.mOtherTiles.size()) {
            TileQueryHelper.TileInfo tile2 = this.mOtherTiles.get(i2);
            if (tile2.isSystem) {
                this.mOtherTiles.remove(i2);
                this.mTiles.add(tile2);
                i2--;
            }
            i2++;
        }
        this.mTileDividerIndex = this.mTiles.size();
        this.mTiles.add(null);
        this.mTiles.addAll(this.mOtherTiles);
        updateDividerLocations();
        notifyDataSetChanged();
    }

    private TileQueryHelper.TileInfo getAndRemoveOther(String s) {
        for (int i = 0; i < this.mOtherTiles.size(); i++) {
            if (this.mOtherTiles.get(i).spec.equals(s)) {
                return this.mOtherTiles.remove(i);
            }
        }
        return null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        if (position == 0) {
            return 3;
        }
        if (this.mAccessibilityAction == 1 && position == this.mEditIndex - 1) {
            return 2;
        }
        if (position == this.mTileDividerIndex) {
            return 4;
        }
        return this.mTiles.get(position) == null ? 1 : 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 3) {
            return new Holder(inflater.inflate(R.layout.qs_customize_header, parent, false));
        }
        if (viewType == 4) {
            return new Holder(inflater.inflate(R.layout.qs_customize_tile_divider, parent, false));
        }
        if (viewType != 1) {
            FrameLayout frame = (FrameLayout) inflater.inflate(R.layout.qs_customize_tile_frame, parent, false);
            frame.addView(new CustomizeTileView(context, new QSIconViewImpl(context)));
            return new Holder(frame);
        }
        return new Holder(inflater.inflate(R.layout.qs_customize_divider, parent, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mTiles.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public boolean onFailedToRecycleView(Holder holder) {
        holder.clearDrag();
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final Holder holder, int position) {
        String titleText;
        if (holder.getItemViewType() == 3) {
            return;
        }
        boolean selectable = false;
        if (holder.getItemViewType() == 4) {
            holder.itemView.setVisibility(this.mTileDividerIndex < this.mTiles.size() - 1 ? 0 : 4);
        } else if (holder.getItemViewType() == 1) {
            Resources res = this.mContext.getResources();
            if (this.mCurrentDrag == null) {
                titleText = res.getString(R.string.drag_to_add_tiles);
            } else if (!canRemoveTiles() && this.mCurrentDrag.getAdapterPosition() < this.mEditIndex) {
                titleText = res.getString(R.string.drag_to_remove_disabled, Integer.valueOf(this.mMinNumTiles));
            } else {
                titleText = res.getString(R.string.drag_to_remove_tiles);
            }
            ((TextView) holder.itemView.findViewById(16908310)).setText(titleText);
        } else if (holder.getItemViewType() != 2) {
            TileQueryHelper.TileInfo info = this.mTiles.get(position);
            if (position > this.mEditIndex) {
                info.state.contentDescription = this.mContext.getString(R.string.accessibility_qs_edit_add_tile_label, info.state.label);
            } else {
                int i = this.mAccessibilityAction;
                if (i == 1) {
                    info.state.contentDescription = this.mContext.getString(R.string.accessibility_qs_edit_tile_add, this.mAccessibilityFromLabel, Integer.valueOf(position));
                } else if (i == 2) {
                    info.state.contentDescription = this.mContext.getString(R.string.accessibility_qs_edit_tile_move, this.mAccessibilityFromLabel, Integer.valueOf(position));
                } else {
                    info.state.contentDescription = this.mContext.getString(R.string.accessibility_qs_edit_tile_label, Integer.valueOf(position), info.state.label);
                }
            }
            holder.mTileView.handleStateChanged(info.state);
            holder.mTileView.setShowAppLabel(position > this.mEditIndex && !info.isSystem);
            if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
                if (this.mAccessibilityAction == 0 || position < this.mEditIndex) {
                    selectable = true;
                }
                holder.mTileView.setClickable(selectable);
                holder.mTileView.setFocusable(selectable);
                holder.mTileView.setImportantForAccessibility(selectable ? 1 : 4);
                if (!selectable) {
                    return;
                }
                holder.mTileView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.customize.TileAdapter.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        int position2 = holder.getAdapterPosition();
                        if (position2 == -1) {
                            return;
                        }
                        if (TileAdapter.this.mAccessibilityAction != 0) {
                            TileAdapter.this.selectPosition(position2, v);
                        } else if (position2 >= TileAdapter.this.mEditIndex || !TileAdapter.this.canRemoveTiles()) {
                            TileAdapter.this.startAccessibleAdd(position2);
                        } else {
                            TileAdapter.this.showAccessibilityDialog(position2, v);
                        }
                    }
                });
            }
        } else {
            holder.mTileView.setClickable(true);
            holder.mTileView.setFocusable(true);
            holder.mTileView.setFocusableInTouchMode(true);
            holder.mTileView.setVisibility(0);
            holder.mTileView.setImportantForAccessibility(1);
            holder.mTileView.setContentDescription(this.mContext.getString(R.string.accessibility_qs_edit_tile_add, this.mAccessibilityFromLabel, Integer.valueOf(position)));
            holder.mTileView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.customize.TileAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    TileAdapter.this.selectPosition(holder.getAdapterPosition(), v);
                }
            });
            if (!this.mNeedsFocus) {
                return;
            }
            holder.mTileView.requestLayout();
            holder.mTileView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.qs.customize.TileAdapter.2
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    holder.mTileView.removeOnLayoutChangeListener(this);
                    holder.mTileView.requestFocus();
                }
            });
            this.mNeedsFocus = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canRemoveTiles() {
        return this.mCurrentSpecs.size() > this.mMinNumTiles;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectPosition(int position, View v) {
        if (this.mAccessibilityAction == 1) {
            List<TileQueryHelper.TileInfo> list = this.mTiles;
            int i = this.mEditIndex;
            this.mEditIndex = i - 1;
            list.remove(i);
            notifyItemRemoved(this.mEditIndex);
        }
        this.mAccessibilityAction = 0;
        move(this.mAccessibilityFromIndex, position, v);
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAccessibilityDialog(final int position, final View v) {
        final TileQueryHelper.TileInfo info = this.mTiles.get(position);
        CharSequence[] options = {this.mContext.getString(R.string.accessibility_qs_edit_move_tile, info.state.label), this.mContext.getString(R.string.accessibility_qs_edit_remove_tile, info.state.label)};
        AlertDialog dialog = new AlertDialog.Builder(this.mContext).setItems(options, new DialogInterface.OnClickListener() { // from class: com.android.systemui.qs.customize.TileAdapter.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int which) {
                if (which == 0) {
                    TileAdapter.this.startAccessibleMove(position);
                    return;
                }
                TileAdapter.this.move(position, info.isSystem ? TileAdapter.this.mEditIndex : TileAdapter.this.mTileDividerIndex, v);
                TileAdapter tileAdapter = TileAdapter.this;
                tileAdapter.notifyItemChanged(tileAdapter.mTileDividerIndex);
                TileAdapter.this.notifyDataSetChanged();
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        SystemUIDialog.setShowForAllUsers(dialog, true);
        SystemUIDialog.applyFlags(dialog);
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAccessibleAdd(int position) {
        this.mAccessibilityFromIndex = position;
        this.mAccessibilityFromLabel = this.mTiles.get(position).state.label;
        this.mAccessibilityAction = 1;
        List<TileQueryHelper.TileInfo> list = this.mTiles;
        int i = this.mEditIndex;
        this.mEditIndex = i + 1;
        list.add(i, null);
        this.mTileDividerIndex++;
        this.mNeedsFocus = true;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAccessibleMove(int position) {
        this.mAccessibilityFromIndex = position;
        this.mAccessibilityFromLabel = this.mTiles.get(position).state.label;
        this.mAccessibilityAction = 2;
        notifyDataSetChanged();
    }

    public GridLayoutManager.SpanSizeLookup getSizeLookup() {
        return this.mSizeLookup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean move(int from, int to, View v) {
        if (to == from) {
            return true;
        }
        CharSequence charSequence = this.mTiles.get(from).state.label;
        move(from, to, this.mTiles);
        updateDividerLocations();
        int i = this.mEditIndex;
        if (to >= i) {
            MetricsLogger.action(this.mContext, (int) StackStateAnimator.ANIMATION_DURATION_STANDARD, strip(this.mTiles.get(to)));
            MetricsLogger.action(this.mContext, 361, from);
        } else if (from >= i) {
            MetricsLogger.action(this.mContext, 362, strip(this.mTiles.get(to)));
            MetricsLogger.action(this.mContext, 363, to);
        } else {
            MetricsLogger.action(this.mContext, 364, strip(this.mTiles.get(to)));
            MetricsLogger.action(this.mContext, 365, to);
        }
        saveSpecs(this.mHost);
        return true;
    }

    private void updateDividerLocations() {
        this.mEditIndex = -1;
        this.mTileDividerIndex = this.mTiles.size();
        for (int i = 1; i < this.mTiles.size(); i++) {
            if (this.mTiles.get(i) == null) {
                if (this.mEditIndex == -1) {
                    this.mEditIndex = i;
                } else {
                    this.mTileDividerIndex = i;
                }
            }
        }
        int size = this.mTiles.size() - 1;
        int i2 = this.mTileDividerIndex;
        if (size == i2) {
            notifyItemChanged(i2);
        }
    }

    private static String strip(TileQueryHelper.TileInfo tileInfo) {
        String spec = tileInfo.spec;
        if (spec.startsWith(CustomTile.PREFIX)) {
            ComponentName component = CustomTile.getComponentFromSpec(spec);
            return component.getPackageName();
        }
        return spec;
    }

    private <T> void move(int from, int to, List<T> list) {
        list.add(to, list.remove(from));
        notifyItemMoved(from, to);
    }

    /* loaded from: classes21.dex */
    public class Holder extends RecyclerView.ViewHolder {
        private CustomizeTileView mTileView;

        public Holder(View itemView) {
            super(itemView);
            if (itemView instanceof FrameLayout) {
                this.mTileView = (CustomizeTileView) ((FrameLayout) itemView).getChildAt(0);
                this.mTileView.setBackground(null);
                this.mTileView.getIcon().disableAnimation();
            }
        }

        public void clearDrag() {
            this.itemView.clearAnimation();
            this.mTileView.findViewById(R.id.tile_label).clearAnimation();
            this.mTileView.findViewById(R.id.tile_label).setAlpha(1.0f);
            this.mTileView.getAppLabel().clearAnimation();
            this.mTileView.getAppLabel().setAlpha(0.6f);
        }

        public void startDrag() {
            this.itemView.animate().setDuration(TileAdapter.DRAG_LENGTH).scaleX(TileAdapter.DRAG_SCALE).scaleY(TileAdapter.DRAG_SCALE);
            this.mTileView.findViewById(R.id.tile_label).animate().setDuration(TileAdapter.DRAG_LENGTH).alpha(0.0f);
            this.mTileView.getAppLabel().animate().setDuration(TileAdapter.DRAG_LENGTH).alpha(0.0f);
        }

        public void stopDrag() {
            this.itemView.animate().setDuration(TileAdapter.DRAG_LENGTH).scaleX(1.0f).scaleY(1.0f);
            this.mTileView.findViewById(R.id.tile_label).animate().setDuration(TileAdapter.DRAG_LENGTH).alpha(1.0f);
            this.mTileView.getAppLabel().animate().setDuration(TileAdapter.DRAG_LENGTH).alpha(0.6f);
        }
    }

    /* loaded from: classes21.dex */
    private class TileItemDecoration extends RecyclerView.ItemDecoration {
        private final Drawable mDrawable;

        private TileItemDecoration(Context context) {
            this.mDrawable = context.getDrawable(R.drawable.qs_customize_tile_decoration);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int childCount = parent.getChildCount();
            int width = parent.getWidth();
            int bottom = parent.getBottom();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.ViewHolder holder = parent.getChildViewHolder(child);
                if (holder.getAdapterPosition() != 0 && (holder.getAdapterPosition() >= TileAdapter.this.mEditIndex || (child instanceof TextView))) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int top = child.getTop() + params.topMargin + Math.round(ViewCompat.getTranslationY(child));
                    this.mDrawable.setBounds(0, top, width, bottom);
                    this.mDrawable.draw(c);
                    return;
                }
            }
        }
    }
}
