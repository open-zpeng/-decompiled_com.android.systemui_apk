package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class UserTile extends QSTileImpl<QSTile.State> implements UserInfoController.OnUserInfoChangedListener {
    private Pair<String, Drawable> mLastUpdate;
    private final UserInfoController mUserInfoController;
    private final UserSwitcherController mUserSwitcherController;

    @Inject
    public UserTile(QSHost host, UserSwitcherController userSwitcherController, UserInfoController userInfoController) {
        super(host);
        this.mUserSwitcherController = userSwitcherController;
        this.mUserInfoController = userInfoController;
        this.mUserInfoController.observe(getLifecycle(), (Lifecycle) this);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.State newTileState() {
        return new QSTile.State();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.settings.USER_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        showDetail(true);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public DetailAdapter getDetailAdapter() {
        return this.mUserSwitcherController.userDetailAdapter;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 260;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return getState().label;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleUpdateState(QSTile.State state, Object arg) {
        final Pair<String, Drawable> p = arg != null ? (Pair) arg : this.mLastUpdate;
        if (p != null) {
            state.label = (CharSequence) p.first;
            state.contentDescription = (CharSequence) p.first;
            state.icon = new QSTile.Icon() { // from class: com.android.systemui.qs.tiles.UserTile.1
                @Override // com.android.systemui.plugins.qs.QSTile.Icon
                public Drawable getDrawable(Context context) {
                    return (Drawable) p.second;
                }
            };
        }
    }

    @Override // com.android.systemui.statusbar.policy.UserInfoController.OnUserInfoChangedListener
    public void onUserInfoChanged(String name, Drawable picture, String userAccount) {
        this.mLastUpdate = new Pair<>(name, picture);
        refreshState(this.mLastUpdate);
    }
}
