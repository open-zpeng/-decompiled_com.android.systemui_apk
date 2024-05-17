package com.xiaopeng.systemui.infoflow.message.presenter;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.android.systemui.R;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.ICruiseSceneCardView;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.Action;
import com.xiaopeng.systemui.infoflow.message.data.bean.ActionList;
import com.xiaopeng.systemui.infoflow.message.data.bean.Request;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.viewmodel.ViewModelManager;
import com.xiaopeng.systemui.viewmodel.car.IVcuViewModel;
import com.xiaopeng.systemui.viewmodel.car.VcuViewModel;
import com.xiaopeng.xuimanager.contextinfo.HomeCompanyRouteInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class CruiseSceneCardPresenter extends BaseCardPresenter {
    private static final int ACTION_ACTIVITY = 0;
    private static final int ACTION_BROADCAST = 1;
    private static final int ACTION_SERVICE = 2;
    private static final String TAG = "CruiseSceneCardPresente";
    private List<Action> mActions;
    private ICruiseSceneCardView mCruiseSceneCardView;
    private final String PACKAGE_MONTECARLO = VuiConstants.MAP_APPNMAE;
    private ContextManager.OnCruiseDataChangedListener mOnCruiseDataChangeListener = new ContextManager.OnCruiseDataChangedListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CruiseSceneCardPresenter.1
        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnCruiseDataChangedListener
        public void onHomeCompanyRouteInfo(HomeCompanyRouteInfo remainInfo) {
            CruiseSceneCardPresenter.this.mInfoflowView.setCruiseSceneCardRouteInfo(remainInfo);
        }
    };
    private ContextManager mContextManager = ContextManager.getInstance();
    private VcuViewModel mVcuViewModel = (VcuViewModel) ViewModelManager.getInstance().getViewModel(IVcuViewModel.class, this.mContext);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final CruiseSceneCardPresenter sInstance = new CruiseSceneCardPresenter();

        private SingleHolder() {
        }
    }

    public static CruiseSceneCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    protected CruiseSceneCardPresenter() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 25;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        return PackageHelper.getInstance().getMapPkgName();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    public void bindDataImpl(CardEntry cardEntry) {
        List<String> actionList = getActionList();
        this.mContextManager.setOnCruiseDataChangedListener(this.mOnCruiseDataChangeListener);
        ICruiseSceneCardView iCruiseSceneCardView = this.mCruiseSceneCardView;
        if (iCruiseSceneCardView != null) {
            iCruiseSceneCardView.setCruiseSceneCardBtnImages(actionList);
            this.mCruiseSceneCardView.setCruiseSceneCardActionNum(this.mActions.size() - 1);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mCruiseSceneCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mCruiseSceneCardView = (ICruiseSceneCardView) this.mCardHolder;
    }

    @NonNull
    private List<String> getActionList() {
        parseActions();
        List<String> images = new ArrayList<>();
        List<Action> list = this.mActions;
        if (list != null && list.size() > 1) {
            for (int i = 1; i < this.mActions.size(); i++) {
                Action action = this.mActions.get(i);
                String imageString = action.actionImg;
                if (!TextUtils.isEmpty(imageString)) {
                    images.add(imageString);
                } else {
                    Logger.d(TAG, "imageString is null");
                }
            }
        }
        return images;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onActionClicked(int actionIndex) {
        if (actionIndex >= this.mActions.size()) {
            return;
        }
        Action action = this.mActions.get(actionIndex);
        if (action != null) {
            Log.i(TAG, "startWithAction name:" + action.actionName);
            Intent intent = new Intent(action.actionName);
            if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
                intent.setPackage("com.xiaopeng.napa");
            } else {
                intent.setPackage(this.mContext.getString(R.string.pkg_map));
            }
            intent.addFlags(268435456);
            Request request = action.datas;
            if (request != null) {
                Bundle bundle = new Bundle();
                bundle.putString("app_id", this.mContext.getPackageName());
                bundle.putInt("request_id", request.getRequest_id());
                bundle.putInt("what", request.getWhat());
                bundle.putString("content", request.getContent());
                intent.putExtra("param", bundle);
            }
            int actionType = action.actionType;
            try {
                if (actionType == 0) {
                    PackageHelper.startActivity(this.mContext, intent, (Bundle) null);
                } else if (actionType == 2) {
                    this.mContext.startForegroundService(intent);
                } else if (actionType == 1) {
                    this.mContext.sendBroadcast(intent);
                }
            } catch (ActivityNotFoundException | IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        int isMapShow = 0;
        ComponentName currentComponent = ActivityController.getCurrentComponent();
        if (currentComponent != null) {
            String currentPackageName = currentComponent.getPackageName();
            if (VuiConstants.MAP_APPNMAE.equals(currentPackageName)) {
                isMapShow = 1;
            }
        }
        int type = -1;
        if (actionIndex == 1 || actionIndex == 2) {
            type = actionIndex + 1;
        }
        if (type != -1) {
            DataLogUtils.sendDataLog(DataLogUtils.INFO_NAVI_PAGE_ID, "B001", type, isMapShow, this.mVcuViewModel.getDriveDistance());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        Log.i(TAG, "processCardClicked");
        try {
            Intent intent = new Intent(this.mContext.getString(R.string.action_map));
            intent.setPackage(this.mContext.getString(R.string.pkg_map));
            Bundle bundle = new Bundle();
            bundle.putString("app_id", this.mContext.getPackageName());
            bundle.putInt("request_id", 123456);
            bundle.putInt("what", 2);
            bundle.putString("content", "{\"dest\":{\"lat\":23.1066805,\"lon\":113.3245904}}");
            intent.putExtra("param", bundle);
            this.mContext.startService(intent);
        } catch (Exception e) {
            Logger.w(TAG, "navi to guangzhouta e=" + e);
        }
    }

    private void parseActions() {
        Logger.d(TAG, "parseActions : mCardData = " + this.mCardData);
        if (this.mCardData != null) {
            String extraDataString = this.mCardData.extraData;
            Logger.d(TAG, "parseActions : extraData = " + extraDataString);
            if (!TextUtils.isEmpty(extraDataString)) {
                ActionList actionList = (ActionList) GsonUtil.fromJson(extraDataString, (Class<Object>) ActionList.class);
                this.mActions = actionList.actions;
            }
        }
    }
}
