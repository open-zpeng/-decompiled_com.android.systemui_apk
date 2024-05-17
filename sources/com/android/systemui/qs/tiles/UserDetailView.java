package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.R;
import com.android.systemui.qs.PseudoGridView;
import com.android.systemui.statusbar.policy.UserSwitcherController;
/* loaded from: classes21.dex */
public class UserDetailView extends PseudoGridView {
    protected Adapter mAdapter;

    public UserDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static UserDetailView inflate(Context context, ViewGroup parent, boolean attach) {
        return (UserDetailView) LayoutInflater.from(context).inflate(R.layout.qs_user_detail, parent, attach);
    }

    public void createAndSetAdapter(UserSwitcherController controller) {
        this.mAdapter = new Adapter(this.mContext, controller);
        PseudoGridView.ViewGroupAdapterBridge.link(this, this.mAdapter);
    }

    public void refreshAdapter() {
        this.mAdapter.refresh();
    }

    /* loaded from: classes21.dex */
    public static class Adapter extends UserSwitcherController.BaseUserAdapter implements View.OnClickListener {
        private final Context mContext;
        protected UserSwitcherController mController;

        public Adapter(Context context, UserSwitcherController controller) {
            super(controller);
            this.mContext = context;
            this.mController = controller;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            UserSwitcherController.UserRecord item = getItem(position);
            return createUserDetailItemView(convertView, parent, item);
        }

        public UserDetailItemView createUserDetailItemView(View convertView, ViewGroup parent, UserSwitcherController.UserRecord item) {
            UserDetailItemView v = UserDetailItemView.convertOrInflate(this.mContext, convertView, parent);
            if (!item.isCurrent || item.isGuest) {
                v.setOnClickListener(this);
            } else {
                v.setOnClickListener(null);
                v.setClickable(false);
            }
            String name = getName(this.mContext, item);
            if (item.picture == null) {
                v.bind(name, getDrawable(this.mContext, item), item.resolveId());
            } else {
                v.bind(name, item.picture, item.info.id);
            }
            v.setActivated(item.isCurrent);
            v.setDisabledByAdmin(item.isDisabledByAdmin);
            if (!item.isSwitchToEnabled) {
                v.setEnabled(false);
            }
            v.setTag(item);
            return v;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            UserSwitcherController.UserRecord tag = (UserSwitcherController.UserRecord) view.getTag();
            if (tag.isDisabledByAdmin) {
                Intent intent = RestrictedLockUtils.getShowAdminSupportDetailsIntent(this.mContext, tag.enforcedAdmin);
                this.mController.startActivity(intent);
            } else if (tag.isSwitchToEnabled) {
                MetricsLogger.action(this.mContext, (int) Cea708CCParser.Const.CODE_C1_DF4);
                switchTo(tag);
            }
        }
    }
}
