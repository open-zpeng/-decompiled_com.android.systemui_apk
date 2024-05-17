package com.xiaopeng.systemui.controller;

import android.content.Context;
import android.util.SparseArray;
import com.android.systemui.R;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.viewmodel.car.CarViewModel;
/* loaded from: classes24.dex */
public class ItemController {
    private static CarViewModel sCarViewModel;

    public static void setCarViewModel(CarViewModel model) {
        sCarViewModel = model;
    }

    /* loaded from: classes24.dex */
    public static abstract class ItemInfo {
        public int resId;
        public int tagId;

        public ItemInfo(int tagId, int resId) {
            this.tagId = tagId;
            this.resId = resId;
        }

        public void onClicked(Context context) {
        }
    }

    /* loaded from: classes24.dex */
    public static class VerticalNavigationItem {
        public static final ItemInfo sBottom2;
        public static final ItemInfo sTop1;
        public static final ItemInfo sBottom1 = new ItemInfo(R.string.component_music, R.drawable.ic_navbar_item_music) { // from class: com.xiaopeng.systemui.controller.ItemController.VerticalNavigationItem.1
            @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
            public void onClicked(Context context) {
                String component = context.getString(R.string.component_music);
                if (!PackageHelper.checkAppOpened(context, component)) {
                    PackageHelper.startXpMusic(context);
                }
                BIHelper.sendBIData(BIHelper.ID.music, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
            }
        };
        public static final ItemInfo sTop2 = new ItemInfo(R.string.component_carcontrol, R.drawable.ic_navbar_item_carcontrol) { // from class: com.xiaopeng.systemui.controller.ItemController.VerticalNavigationItem.2
            @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
            public void onClicked(Context context) {
                String component = context.getString(R.string.component_carcontrol);
                if (!PackageHelper.checkAppOpened(context, component) && ItemController.sCarViewModel != null && ItemController.sCarViewModel.isCarControlLoadReady()) {
                    PackageHelper.startCarControl(context);
                }
                BIHelper.sendBIData(BIHelper.ID.carcontrol, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
            }
        };

        static {
            sTop1 = new ItemInfo(R.string.component_autopilot, CarModelsManager.getFeature().isAutopilotSupport() ? R.drawable.ic_navbar_item_autoparking : R.drawable.ic_navbar_item_carcamera) { // from class: com.xiaopeng.systemui.controller.ItemController.VerticalNavigationItem.3
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                    super.onClicked(context);
                    PackageHelper.startService(context, R.string.action_autopilot, "com.xiaopeng.autopilot", null);
                    BIHelper.sendBIData(BIHelper.ID.parking, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
                }
            };
            sBottom2 = new ItemInfo(R.string.component_phone, R.drawable.ic_navbar_item_phone) { // from class: com.xiaopeng.systemui.controller.ItemController.VerticalNavigationItem.4
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                    String component = context.getString(R.string.component_phone);
                    if (!PackageHelper.checkAppOpened(context, component)) {
                        PackageHelper.startBtPhone(context);
                    }
                    BIHelper.sendBIData(BIHelper.ID.phone, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
                }
            };
        }
    }

    /* loaded from: classes24.dex */
    public static class NavigationItemFactory {
        private static NavigationItemFactory sInstance;
        private SparseArray<ItemInfo> mLeftItemArray = new SparseArray<>();
        private SparseArray<ItemInfo> mRightItemArray = new SparseArray<>();

        public static NavigationItemFactory getInstance() {
            if (sInstance == null) {
                sInstance = new NavigationItemFactory();
            }
            return sInstance;
        }

        private NavigationItemFactory() {
            init();
        }

        public void addLeftItem(int index, ItemInfo itemInfo) {
            this.mLeftItemArray.put(index, itemInfo);
        }

        public void addRightItem(int index, ItemInfo itemInfo) {
            this.mRightItemArray.put(index, itemInfo);
        }

        public void init() {
            addBottomItem();
        }

        public ItemInfo getLeftItemInfo(int index) {
            return this.mLeftItemArray.get(index);
        }

        public ItemInfo getRightItemInfo(int index) {
            return this.mRightItemArray.get(index);
        }

        private void addBottomItem() {
            int i;
            addLeftItem(2, new ItemInfo(R.string.component_carcontrol, R.drawable.ic_navbar_item_carcontrol) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.1
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                    PackageHelper.startCarControl(context);
                    BIHelper.sendBIData(BIHelper.ID.carcontrol, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
                }
            });
            addLeftItem(1, new ItemInfo(R.string.component_autopilot, CarModelsManager.getFeature().isAutopilotSupport() ? R.drawable.ic_navbar_item_autoparking : R.drawable.ic_navbar_item_carcamera) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.2
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                    PackageHelper.startService(context, R.string.action_autopilot, "com.xiaopeng.autopilot", null);
                    BIHelper.sendBIData(BIHelper.ID.parking, BIHelper.Type.dock, BIHelper.Action.click, BIHelper.Screen.main);
                }
            });
            addRightItem(1, new ItemInfo(0, R.drawable.ic_navbar_item_defrost_front) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.3
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                }
            });
            if (CarModelsManager.getConfig().isRearMirrorFoldSupport()) {
                i = R.drawable.ic_navbar_item_defrost_mirror;
            } else {
                i = R.drawable.ic_navbar_item_defrost_back;
            }
            addRightItem(2, new ItemInfo(0, i) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.4
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                }
            });
        }

        private void initForForeignVersion() {
            addLeftItem(2, new ItemInfo(R.string.component_carcontrol, R.drawable.ic_navbar_item_carcontrol) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.5
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                    PackageHelper.startCarControl(context);
                    DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.CAR_CONTROL_ID);
                }
            });
            addLeftItem(1, new ItemInfo(R.string.component_autopilot, CarModelsManager.getFeature().isAutopilotSupport() ? R.drawable.ic_navbar_item_autoparking : R.drawable.ic_navbar_item_carcamera) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.6
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                    PackageHelper.startService(context, R.string.action_autopilot, "com.xiaopeng.autopilot", null);
                    DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.PARKING_ID);
                }
            });
            addRightItem(1, new ItemInfo(0, R.drawable.ic_navbar_item_defrost_front) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.7
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                }
            });
            addRightItem(2, new ItemInfo(0, R.drawable.ic_navbar_item_defrost_back) { // from class: com.xiaopeng.systemui.controller.ItemController.NavigationItemFactory.8
                @Override // com.xiaopeng.systemui.controller.ItemController.ItemInfo
                public void onClicked(Context context) {
                }
            });
        }
    }
}
