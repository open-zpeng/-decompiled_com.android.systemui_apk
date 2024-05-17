package com.xiaopeng.systemui.infoflow.checking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.ThemeController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.checking.bean.CheckInfo;
import com.xiaopeng.systemui.infoflow.checking.bean.CheckItemInfo;
import com.xiaopeng.systemui.infoflow.common.event.EventCenter;
import com.xiaopeng.systemui.infoflow.message.event.CarCheckEventPackage;
import com.xiaopeng.systemui.infoflow.message.event.EventType;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.util.List;
/* loaded from: classes24.dex */
public class CheckedView extends RelativeLayout implements View.OnClickListener, ThemeController.OnThemeListener {
    public static final String AI_CENTER_URI = "xp://notification/detail?category=ai";
    private static final String TAG = CheckedView.class.getSimpleName();
    private final String CUSTOM_SERVICE_NUM;
    private final int MAX_ITEM_COUNT;
    private final String TEXT_ERROR;
    private final String TEXT_EXCEPTION;
    private final String TEXT_WARNNING;
    private Button mCallBtn;
    private CheckInfo mCheckInfo;
    private TextView mCheckedContent;
    private RecyclerView mCheckedList;
    private TextView mCheckedTitle;
    private String mContent;
    private TextView mMoreIndicateTv;
    private String mNotificationId;
    private String mTitle;

    public CheckedView(Context context) {
        this(context, null);
    }

    public CheckedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CheckedView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.MAX_ITEM_COUNT = 3;
        this.TEXT_ERROR = "严重";
        this.TEXT_WARNNING = "普通";
        this.TEXT_EXCEPTION = "轻微";
        this.CUSTOM_SERVICE_NUM = "4008193388";
        ThemeController.getInstance(this.mContext).registerThemeListener(this);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCheckedTitle = (TextView) findViewById(R.id.tv_checked_title);
        this.mCheckedContent = (TextView) findViewById(R.id.tv_checked_content);
        this.mCheckedList = (RecyclerView) findViewById(R.id.checked_list);
        this.mCallBtn = (Button) findViewById(R.id.btn_checked_call);
        this.mMoreIndicateTv = (TextView) findViewById(R.id.tv_more_indication);
        this.mCheckedList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mCallBtn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.checking.-$$Lambda$fp6LQ5C6XGbtfNUjv-KOWVB5UdE
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CheckedView.this.onClick(view);
            }
        });
        setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.checking.-$$Lambda$fp6LQ5C6XGbtfNUjv-KOWVB5UdE
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CheckedView.this.onClick(view);
            }
        });
    }

    public void setData(String title, String content, CheckInfo checkInfo, String notificationId) {
        this.mTitle = title;
        this.mContent = content;
        this.mCheckInfo = checkInfo;
        this.mNotificationId = notificationId;
        this.mCheckedTitle.setText(title);
        this.mCheckedContent.setText(content);
        this.mCheckedList.setBackgroundResource(R.drawable.bg_car_checked_list);
        if (checkInfo.detailResult != null && checkInfo.detailResult.size() > 0) {
            CheckedListAdapter checkedListAdapter = new CheckedListAdapter(checkInfo.detailResult);
            this.mCheckedList.setAdapter(checkedListAdapter);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getId() == R.id.btn_checked_call) {
            callCustomService();
        } else {
            callCustomService();
        }
    }

    private void callCustomService() {
        EventCenter.instance().raiseEvent(new CarCheckEventPackage(EventType.EXIT_CAR_CHECK, this, true));
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setFlags(268435456);
        Uri data = Uri.parse("tel:4008193388");
        intent.setData(data);
        PackageHelper.startActivity(getContext(), intent, (Bundle) null);
    }

    private void openAiCenter() {
        EventCenter.instance().raiseEvent(new CarCheckEventPackage(EventType.EXIT_CAR_CHECK, this, true));
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(AI_CENTER_URI));
        PackageHelper.startActivity(this.mContext, intent, (Bundle) null);
    }

    @Override // com.xiaopeng.systemui.controller.ThemeController.OnThemeListener
    public void onThemeChanged(boolean selfChange, Uri uri) {
        if (uri.equals(ThemeController.URI_THEME_STATE) && this.mCheckedList != null) {
            Logger.d(TAG, "onThemeChanged");
            this.mCheckedList.setBackground(getContext().getDrawable(R.drawable.bg_car_checked_list));
            this.mCheckedList.setBackgroundResource(R.drawable.bg_car_checked_list);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public class CheckedListAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<CheckItemInfo> mData;

        public CheckedListAdapter(List<CheckItemInfo> data) {
            this.mData = data;
        }

        public void setData(List<CheckItemInfo> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_checked, viewGroup, false);
            return new ItemViewHolder(itemView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            itemViewHolder.bindData(this.mData.get(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mData.size();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes24.dex */
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView mCheckedStatus;
            private TextView mCheckedTitle;

            public ItemViewHolder(View itemView) {
                super(itemView);
                this.mCheckedTitle = (TextView) itemView.findViewById(R.id.tv_checked_item_title);
                this.mCheckedStatus = (TextView) itemView.findViewById(R.id.tv_checked_item_status);
            }

            public void bindData(CheckItemInfo checkItemInfo) {
                this.mCheckedTitle.setText(checkItemInfo.title);
                String statusText = checkItemInfo.result;
                this.mCheckedStatus.setText(statusText);
                if ("严重".equals(statusText)) {
                    this.mCheckedStatus.setTextColor(CheckedView.this.getContext().getColor(R.color.colorCheckError));
                } else if ("普通".equals(statusText)) {
                    this.mCheckedStatus.setTextColor(CheckedView.this.getContext().getColor(R.color.colorCheckWarn));
                } else {
                    this.mCheckedStatus.setTextColor(CheckedView.this.getContext().getColor(R.color.colorCheckException));
                }
            }
        }
    }
}
