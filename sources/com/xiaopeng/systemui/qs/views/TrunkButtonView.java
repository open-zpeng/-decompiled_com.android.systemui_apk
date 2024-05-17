package com.xiaopeng.systemui.qs.views;

import android.car.Car;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import com.android.systemui.R;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.systemui.controller.ThemeController;
import com.xiaopeng.systemui.qs.QsPanelSetting;
import com.xiaopeng.systemui.qs.TileState;
import com.xiaopeng.systemui.qs.XpTileFactory;
import com.xiaopeng.systemui.qs.tilemodels.XpTileModel;
import com.xiaopeng.systemui.qs.widgets.XTileButton;
import com.xiaopeng.systemui.utils.IntervalControl;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XRelativeLayout;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class TrunkButtonView extends TileView implements View.OnClickListener, LifecycleOwner, ThemeController.OnThemeListener {
    private XImageView mCarModel;
    private XpTileModel mCloseTruckTile;
    private Context mContext;
    private IntervalControl mIntervalControl;
    protected final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private XpTileModel mOpenTruckTile;
    private ViewGroup mParentView;
    private TileState mTileState;
    private XTileButton mTruckBtn;
    private XRelativeLayout mTruckChoseBtn;
    private View mTruckChoseCloseBtn;
    private View mTruckChoseOpenBtn;
    private View mView;

    public TrunkButtonView(Context context, TileState tileState, ViewGroup viewGroup) {
        this.mContext = context;
        this.mTileState = tileState;
        this.mParentView = viewGroup;
        this.mIntervalControl = new IntervalControl(this.mTileState.key);
        ThemeController.getInstance(this.mContext).registerThemeListener(this);
        initView();
        initTile();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mIntervalControl.isFrequently(this.mTileState.quickClickSafeTime)) {
            return;
        }
        int id = view.getId();
        if (id == R.id.quickmenu_truck) {
            getCurrentTile().click(-1);
        } else if (id == R.id.quickmenu_truck_close) {
            this.mCloseTruckTile.click(-1);
        } else if (id == R.id.quickmenu_truck_open) {
            this.mOpenTruckTile.click(-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateViewState(int state) {
        this.mView.findViewById(R.id.quickmenu_truck_chose).setVisibility(8);
        this.mTruckBtn.setVisibility(0);
        switch (state) {
            case 1:
                this.mTruckBtn.setTextRes(R.string.qs_panel_trunk_0);
                this.mTruckBtn.setTextColor(R.color.quick_menu_text_icon_normal_color);
                this.mTruckBtn.setImageLevel(0);
                this.mCarModel.setImageLevel(0);
                this.mTruckBtn.setSelected(false);
                return;
            case 2:
                this.mTruckBtn.setTextRes(R.string.qs_panel_trunk_1);
                this.mTruckBtn.setTextColor(R.color.quick_menu_ui_icon_open_state);
                this.mTruckBtn.setImageLevel(2);
                this.mCarModel.setImageLevel(2);
                this.mTruckBtn.setSelected(true);
                return;
            case 3:
                this.mTruckBtn.setTextRes(R.string.qs_panel_trunk_2);
                this.mTruckBtn.setTextColor(R.color.quick_menu_text_icon_normal_color);
                this.mTruckBtn.setImageLevel(1);
                this.mCarModel.setImageLevel(1);
                this.mTruckBtn.setSelected(true);
                return;
            case 4:
                this.mTruckBtn.setTextRes(R.string.qs_panel_trunk_3);
                this.mTruckBtn.setTextColor(R.color.quick_menu_text_icon_normal_color);
                this.mTruckBtn.setImageLevel(1);
                this.mCarModel.setImageLevel(1);
                this.mTruckBtn.setSelected(true);
                return;
            case 5:
            case 6:
                this.mTruckBtn.setVisibility(8);
                this.mCarModel.setImageLevel(1);
                this.mTruckChoseBtn.setVisibility(0);
                this.mTruckChoseBtn.setSelected(true);
                ((XImageView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_close_img)).setImageResource(R.drawable.ic_qs_trunk_down);
                ((XTextView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_close_txt)).setTextColor(this.mContext.getColor(R.color.quick_menu_text_icon_normal_color));
                return;
            case 7:
            default:
                return;
            case 8:
                this.mTruckBtn.setVisibility(8);
                this.mCarModel.setImageLevel(1);
                this.mTruckChoseBtn.setVisibility(0);
                this.mTruckChoseBtn.setSelected(true);
                ((XImageView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_close_img)).setImageResource(R.drawable.ic_qs_trunk_down_disable);
                ((XTextView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_close_txt)).setTextColor(this.mContext.getColor(R.color.quick_menu_text_disable_color));
                return;
        }
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public ViewGroup getView() {
        return (ViewGroup) this.mView;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    public XpTileModel getTile() {
        return this.mTile;
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    protected void initView() {
        int width = (this.mTileState.width * (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.H_GAP)) - QsPanelSetting.H_GAP;
        int height = (this.mTileState.height * (QsPanelSetting.ATOM_WIDTH + QsPanelSetting.V_GAP)) - QsPanelSetting.V_GAP;
        this.mView = LayoutInflater.from(this.mContext).inflate(R.layout.layout_toggle_tile_truck, this.mParentView, false);
        this.mTruckBtn = (XTileButton) this.mView.findViewById(R.id.quickmenu_truck);
        this.mTruckBtn.setWidthHeight(width, height);
        this.mTruckChoseOpenBtn = this.mView.findViewById(R.id.quickmenu_truck_open);
        this.mTruckChoseCloseBtn = this.mView.findViewById(R.id.quickmenu_truck_close);
        this.mTruckBtn.setOnClickListener(this);
        this.mTruckChoseOpenBtn.setOnClickListener(this);
        this.mTruckChoseCloseBtn.setOnClickListener(this);
        this.mTruckBtn.setTitleRes(R.id.quickmenu_truck);
        this.mTruckBtn.setTitleRes(R.string.qs_panel_trunk);
        this.mCarModel = new XImageView(this.mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -1);
        this.mCarModel.setLayoutParams(lp);
        this.mTruckBtn.addView(this.mCarModel);
        this.mTruckChoseBtn = (XRelativeLayout) this.mView.findViewById(R.id.quickmenu_truck_chose);
        this.mTruckChoseBtn.setOnClickListener(this);
        setCarModelRes();
    }

    @Override // com.xiaopeng.systemui.qs.views.TileView
    protected void initTile() {
        this.mOpenTruckTile = XpTileFactory.createTile("open_back_box");
        this.mCloseTruckTile = XpTileFactory.createTile("close_back_box");
        this.mOpenTruckTile.getCurrentData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.views.-$$Lambda$TrunkButtonView$UTULcuYelh6WnuCfEtsmDGxvpw0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                TrunkButtonView.this.updateViewState(((Integer) obj).intValue());
            }
        });
        this.mCloseTruckTile.getCurrentData().observe(this, new Observer() { // from class: com.xiaopeng.systemui.qs.views.-$$Lambda$TrunkButtonView$UTULcuYelh6WnuCfEtsmDGxvpw0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                TrunkButtonView.this.updateViewState(((Integer) obj).intValue());
            }
        });
        this.mTile = getCurrentTile();
        this.mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0022, code lost:
        if (r0 != 4) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.xiaopeng.systemui.qs.tilemodels.XpTileModel getCurrentTile() {
        /*
            r2 = this;
            com.xiaopeng.systemui.qs.tilemodels.XpTileModel r0 = r2.mTile
            if (r0 != 0) goto L8
            com.xiaopeng.systemui.qs.tilemodels.XpTileModel r0 = r2.mOpenTruckTile
            r2.mTile = r0
        L8:
            com.xiaopeng.systemui.qs.tilemodels.XpTileModel r0 = r2.mTile
            androidx.lifecycle.MutableLiveData r0 = r0.getCurrentData()
            java.lang.Object r0 = r0.getValue()
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r1 = 1
            if (r0 == r1) goto L2a
            r1 = 2
            if (r0 == r1) goto L25
            r1 = 3
            if (r0 == r1) goto L2a
            r1 = 4
            if (r0 == r1) goto L25
            goto L2f
        L25:
            com.xiaopeng.systemui.qs.tilemodels.XpTileModel r1 = r2.mCloseTruckTile
            r2.mTile = r1
            goto L2f
        L2a:
            com.xiaopeng.systemui.qs.tilemodels.XpTileModel r1 = r2.mOpenTruckTile
            r2.mTile = r1
        L2f:
            com.xiaopeng.systemui.qs.tilemodels.XpTileModel r1 = r2.mTile
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.qs.views.TrunkButtonView.getCurrentTile():com.xiaopeng.systemui.qs.tilemodels.XpTileModel");
    }

    @Override // com.xiaopeng.systemui.controller.ThemeController.OnThemeListener
    public void onThemeChanged(boolean selfChange, Uri uri) {
        this.mTruckBtn.refreshTheme();
        this.mTruckChoseCloseBtn.refreshDrawableState();
        this.mTruckChoseOpenBtn.refreshDrawableState();
        setCarModelRes();
    }

    private void setCarModelRes() {
        if (this.mCarModel != null) {
            String xpCduType = Car.getXpCduType();
            char c = 65535;
            switch (xpCduType.hashCode()) {
                case 2566:
                    if (xpCduType.equals(VuiUtils.CAR_PLATFORM_Q7)) {
                        c = 2;
                        break;
                    }
                    break;
                case 2567:
                    if (xpCduType.equals("Q8")) {
                        c = 0;
                        break;
                    }
                    break;
                case 2568:
                    if (xpCduType.equals("Q9")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                this.mCarModel.setImageResource(R.drawable.icbg_qs_trunk_e28a);
                ((XImageView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_pause_carmodel)).setBackground(this.mContext.getDrawable(R.drawable.icbg_qs_trunk_closing_e28a));
            } else if (c == 1) {
                this.mCarModel.setImageResource(R.drawable.icbg_qs_trunk_f30);
                ((XImageView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_pause_carmodel)).setBackground(this.mContext.getDrawable(R.drawable.icbg_qs_trunk_closing_f30));
            } else {
                this.mCarModel.setImageResource(R.drawable.icbg_qs_trunk_e38);
                ((XImageView) this.mTruckChoseBtn.findViewById(R.id.quickmenu_truck_pause_carmodel)).setBackground(this.mContext.getDrawable(R.drawable.icbg_qs_trunk_closing_e38));
            }
        }
    }
}
