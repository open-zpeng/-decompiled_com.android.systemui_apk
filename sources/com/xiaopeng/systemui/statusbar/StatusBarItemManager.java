package com.xiaopeng.systemui.statusbar;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.rastermill.FrameSequenceDrawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes24.dex */
public class StatusBarItemManager implements View.OnClickListener, FrameSequenceDrawable.OnFinishedListener {
    private static final int AUTO_HIDE_OVERFLOW_ITEM_CONTAINER_TIME = 10000;
    private static final int MSG_AUTO_HIDE_OVERFLOW_ITEM_CONTAINER = 1;
    private static final String TAG = "StatusBarItemManager";
    public static final int TYPE_AUTH_MODE = 2;
    public static final int TYPE_DOWNLOAD = 5;
    public static final int TYPE_MICROPHONE_MUTE = 1;
    public static final int TYPE_USB = 4;
    public static final int TYPE_WIRELESS_CHARGE = 3;
    private Context mContext;
    private View mDownloadButton;
    private ViewGroup mLeftContainer;
    private View.OnClickListener mOnClickListener;
    private View mOverflowButton;
    private int mOverflowSize;
    private int mStatusBarMaxItemNum;
    private ViewGroup mStatusBarOverflowItemContainer;
    private WindowManager mWindowManager;
    private SparseArray<View> mStatusBarItemArray = new SparseArray<>();
    private SparseArray<View> mOverflowItemArray = new SparseArray<>();
    private List<Integer> mVisibleItemTypes = new ArrayList();
    private List<Integer> mStatusBarVisibleItemTypes = new ArrayList();
    private List<Integer> mOverflowVisibleItemTypes = new ArrayList();
    private boolean mStatusBarOverflowItemContainerAdded = false;
    private boolean mOverflowItemContainerHideByTouchOutside = false;
    private boolean mOverflowButtonAnimFinished = true;
    private int[][] mStatusBarItemTable = {new int[]{1, R.id.btn_microphone_mute}, new int[]{2, R.id.btn_auth_mode}, new int[]{3, R.id.wireless_charge}, new int[]{4, R.id.usb}, new int[]{5, R.id.download_container}};
    private List<Integer> mNotClickableItems = new ArrayList();
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.statusbar.StatusBarItemManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                StatusBarItemManager.this.hideOverflowItemContainer();
            }
        }
    };

    public StatusBarItemManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mStatusBarMaxItemNum = context.getResources().getInteger(R.integer.statusbar_max_item_num);
        initNotClickableItems();
        this.mStatusBarOverflowItemContainer = (ViewGroup) View.inflate(this.mContext, R.layout.status_bar_overflow_item_container, null);
        this.mStatusBarOverflowItemContainer.setOnTouchListener(new View.OnTouchListener() { // from class: com.xiaopeng.systemui.statusbar.StatusBarItemManager.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 4) {
                    StatusBarItemManager.this.hideOverflowItemContainer();
                    StatusBarItemManager statusBarItemManager = StatusBarItemManager.this;
                    if (statusBarItemManager.isInTouch(statusBarItemManager.mOverflowButton, (int) event.getRawX(), (int) event.getRawY())) {
                        StatusBarItemManager.this.mOverflowItemContainerHideByTouchOutside = true;
                    }
                    return true;
                }
                return false;
            }
        });
        this.mStatusBarOverflowItemContainer.setOnClickListener(this);
        setOverflowContainer(this.mStatusBarOverflowItemContainer, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInTouch(View v, int x, int y) {
        Rect viewRect = new Rect();
        v.getGlobalVisibleRect(viewRect);
        if (viewRect.contains(x, y)) {
            return true;
        }
        return false;
    }

    private void initNotClickableItems() {
        this.mNotClickableItems.add(3);
    }

    public void setOverflowButton(View view) {
        this.mOverflowButton = view;
    }

    public void setDownloadButton(View view) {
        this.mDownloadButton = view;
    }

    public void setStatusBarContainer(ViewGroup viewGroup) {
        fillStatusBarItemMap(this.mStatusBarItemArray, viewGroup, this);
        this.mLeftContainer = (ViewGroup) viewGroup.findViewById(R.id.left_container);
        this.mOverflowSize = (this.mStatusBarMaxItemNum - getFixedItemCount()) + 1;
    }

    private void setOverflowContainer(ViewGroup viewGroup, final View.OnClickListener onClickListener) {
        fillStatusBarItemMap(this.mOverflowItemArray, viewGroup, new View.OnClickListener() { // from class: com.xiaopeng.systemui.statusbar.StatusBarItemManager.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                StatusBarItemManager.this.hideOverflowItemContainer();
                onClickListener.onClick(v);
            }
        });
    }

    private void fillStatusBarItemMap(SparseArray<View> map, ViewGroup itemContainer, View.OnClickListener onClickListener) {
        int i = 0;
        while (true) {
            int[][] iArr = this.mStatusBarItemTable;
            if (i < iArr.length) {
                View view = itemContainer.findViewById(iArr[i][1]);
                if (view != null) {
                    int type = this.mStatusBarItemTable[i][0];
                    map.put(type, view);
                    if (!this.mNotClickableItems.contains(Integer.valueOf(type))) {
                        view.setOnClickListener(onClickListener);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void showAndCheckOverflow(int type, boolean show) {
        boolean overflowContainerItemAdded = false;
        int lastItemNum = this.mVisibleItemTypes.size();
        if (show) {
            if (!this.mVisibleItemTypes.contains(Integer.valueOf(type))) {
                this.mVisibleItemTypes.add(Integer.valueOf(type));
            }
        } else {
            this.mVisibleItemTypes.remove(Integer.valueOf(type));
        }
        int curItemNum = this.mVisibleItemTypes.size();
        if (curItemNum > lastItemNum) {
            overflowContainerItemAdded = true;
        }
        Collections.sort(this.mVisibleItemTypes);
        updateStatusBarItems();
        updateOverflowItems();
        if (this.mOverflowButton != null) {
            if (isOverflow()) {
                Logger.i(TAG, "showAndCheckOverflow : overflow");
                showOverflowButton(overflowContainerItemAdded);
            } else {
                hideOverflowButton();
            }
        }
        if (!isOverflow()) {
            hideOverflowItemContainer();
        }
    }

    private int getFixedItemCount() {
        int itemCount = 0;
        for (int i = 0; i < this.mLeftContainer.getChildCount(); i++) {
            View v = this.mLeftContainer.getChildAt(i);
            if (v.getVisibility() == 0 && isFixedItem(v.getId())) {
                itemCount++;
            }
        }
        return itemCount;
    }

    private boolean isFixedItem(int viewId) {
        int i = 0;
        while (true) {
            int[][] iArr = this.mStatusBarItemTable;
            if (i >= iArr.length) {
                return true;
            }
            if (viewId != iArr[i][1]) {
                i++;
            } else {
                return false;
            }
        }
    }

    private void hideOverflowButton() {
        if (this.mOverflowButton.getVisibility() != 8) {
            this.mOverflowButton.setVisibility(8);
            AnimationHelper.destroyAnim((ImageView) this.mOverflowButton);
        }
    }

    private void showOverflowButton(boolean overflowContainerItemAdded) {
        if (this.mOverflowButton.getVisibility() != 0) {
            this.mOverflowButton.setVisibility(0);
        }
        if (overflowContainerItemAdded) {
            showOverflowAnim();
        }
    }

    private void showOverflowAnim() {
        this.mOverflowButtonAnimFinished = false;
        AnimationHelper.startAnimOnce((ImageView) this.mOverflowButton, R.drawable.anim_sysbar_overflow, R.drawable.ic_sysbar_overflow, this);
    }

    public boolean isOverflowButtonAnimFinished() {
        return this.mOverflowButtonAnimFinished;
    }

    public void showOverflowItemContainer() {
        this.mOverflowButtonAnimFinished = true;
        if (!this.mStatusBarOverflowItemContainerAdded && !this.mOverflowItemContainerHideByTouchOutside) {
            this.mStatusBarOverflowItemContainerAdded = true;
            WindowHelper.addStatusBarOverflowWindow(this.mContext, this.mWindowManager, this.mStatusBarOverflowItemContainer);
            this.mHandler.sendEmptyMessageDelayed(1, 10000L);
        }
        this.mOverflowItemContainerHideByTouchOutside = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideOverflowItemContainer() {
        ViewGroup viewGroup = this.mStatusBarOverflowItemContainer;
        if (viewGroup != null && viewGroup.isAttachedToWindow()) {
            this.mStatusBarOverflowItemContainerAdded = false;
            this.mWindowManager.removeViewImmediate(this.mStatusBarOverflowItemContainer);
            this.mHandler.removeMessages(1);
        }
    }

    private void updateOverflowItems() {
        this.mOverflowVisibleItemTypes.clear();
        if (isOverflow()) {
            for (int i = this.mOverflowSize - 2; i < this.mVisibleItemTypes.size(); i++) {
                this.mOverflowVisibleItemTypes.add(this.mVisibleItemTypes.get(i));
            }
        }
        updateItemContainer(this.mOverflowVisibleItemTypes, this.mOverflowItemArray);
    }

    private void updateStatusBarItems() {
        int statusBarItemCount = this.mVisibleItemTypes.size();
        if (isOverflow()) {
            statusBarItemCount = this.mOverflowSize - 2;
        }
        this.mStatusBarVisibleItemTypes.clear();
        for (int i = 0; i < statusBarItemCount; i++) {
            this.mStatusBarVisibleItemTypes.add(this.mVisibleItemTypes.get(i));
        }
        updateItemContainer(this.mStatusBarVisibleItemTypes, this.mStatusBarItemArray);
        if (this.mStatusBarVisibleItemTypes.contains(5)) {
            ViewGroup viewGroup = (ViewGroup) this.mStatusBarItemArray.get(5);
            if (viewGroup.getChildCount() == 0) {
                ((ViewGroup) this.mOverflowItemArray.get(5)).removeView(this.mDownloadButton);
                viewGroup.addView(this.mDownloadButton);
                return;
            }
            return;
        }
        ViewGroup viewGroupOverflow = (ViewGroup) this.mOverflowItemArray.get(5);
        if (viewGroupOverflow.getChildCount() == 0) {
            ((ViewGroup) this.mStatusBarItemArray.get(5)).removeView(this.mDownloadButton);
            viewGroupOverflow.addView(this.mDownloadButton);
        }
    }

    private void updateItemContainer(List<Integer> visibleItemTypes, SparseArray<View> itemArray) {
        for (int i = 0; i < itemArray.size(); i++) {
            int itemType = itemArray.keyAt(i);
            View v = itemArray.get(itemType);
            if (visibleItemTypes.contains(Integer.valueOf(itemType))) {
                if (v.getVisibility() != 0) {
                    v.setVisibility(0);
                }
            } else if (v.getVisibility() == 0) {
                v.setVisibility(8);
            }
        }
    }

    private boolean isOverflow() {
        return this.mVisibleItemTypes.size() >= this.mOverflowSize;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mStatusBarOverflowItemContainer) {
            hideOverflowItemContainer();
            return;
        }
        View.OnClickListener onClickListener = this.mOnClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    @Override // android.support.rastermill.FrameSequenceDrawable.OnFinishedListener
    public void onFinished(FrameSequenceDrawable frameSequenceDrawable) {
        this.mOverflowButtonAnimFinished = true;
    }
}
