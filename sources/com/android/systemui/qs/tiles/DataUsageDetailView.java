package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.qs.DataUsageGraph;
import java.text.DecimalFormat;
/* loaded from: classes21.dex */
public class DataUsageDetailView extends LinearLayout {
    private static final double GB = 1.073741824E9d;
    private static final double KB = 1024.0d;
    private static final double MB = 1048576.0d;
    private final DecimalFormat FORMAT;

    public DataUsageDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.FORMAT = new DecimalFormat("#.##");
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FontSizeUtils.updateFontSize(this, 16908310, R.dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize(this, R.id.usage_text, R.dimen.qs_data_usage_usage_text_size);
        FontSizeUtils.updateFontSize(this, R.id.usage_carrier_text, R.dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize(this, R.id.usage_info_top_text, R.dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize(this, R.id.usage_period_text, R.dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize(this, R.id.usage_info_bottom_text, R.dimen.qs_data_usage_text_size);
    }

    public void bind(DataUsageController.DataUsageInfo info) {
        int titleId;
        long bytes;
        String top;
        Resources res = this.mContext.getResources();
        ColorStateList usageColorState = null;
        String bottom = null;
        if (info.usageLevel < info.warningLevel || info.limitLevel <= 0) {
            titleId = R.string.quick_settings_cellular_detail_data_usage;
            bytes = info.usageLevel;
            top = res.getString(R.string.quick_settings_cellular_detail_data_warning, formatBytes(info.warningLevel));
        } else if (info.usageLevel <= info.limitLevel) {
            titleId = R.string.quick_settings_cellular_detail_remaining_data;
            bytes = info.limitLevel - info.usageLevel;
            top = res.getString(R.string.quick_settings_cellular_detail_data_used, formatBytes(info.usageLevel));
            bottom = res.getString(R.string.quick_settings_cellular_detail_data_limit, formatBytes(info.limitLevel));
        } else {
            titleId = R.string.quick_settings_cellular_detail_over_limit;
            bytes = info.usageLevel - info.limitLevel;
            top = res.getString(R.string.quick_settings_cellular_detail_data_used, formatBytes(info.usageLevel));
            bottom = res.getString(R.string.quick_settings_cellular_detail_data_limit, formatBytes(info.limitLevel));
            usageColorState = Utils.getColorError(this.mContext);
        }
        if (usageColorState == null) {
            usageColorState = Utils.getColorAccent(this.mContext);
        }
        TextView title = (TextView) findViewById(16908310);
        title.setText(titleId);
        TextView usage = (TextView) findViewById(R.id.usage_text);
        usage.setText(formatBytes(bytes));
        usage.setTextColor(usageColorState);
        DataUsageGraph graph = (DataUsageGraph) findViewById(R.id.usage_graph);
        graph.setLevels(info.limitLevel, info.warningLevel, info.usageLevel);
        TextView carrier = (TextView) findViewById(R.id.usage_carrier_text);
        carrier.setText(info.carrier);
        TextView period = (TextView) findViewById(R.id.usage_period_text);
        period.setText(info.period);
        TextView infoTop = (TextView) findViewById(R.id.usage_info_top_text);
        infoTop.setVisibility(top != null ? 0 : 8);
        infoTop.setText(top);
        TextView infoBottom = (TextView) findViewById(R.id.usage_info_bottom_text);
        infoBottom.setVisibility(bottom != null ? 0 : 8);
        infoBottom.setText(bottom);
        boolean showLevel = info.warningLevel > 0 || info.limitLevel > 0;
        graph.setVisibility(showLevel ? 0 : 8);
        if (!showLevel) {
            infoTop.setVisibility(8);
        }
    }

    private String formatBytes(long bytes) {
        double val;
        String suffix;
        long b = Math.abs(bytes);
        if (b > 1.048576E8d) {
            val = b / GB;
            suffix = "GB";
        } else {
            double val2 = b;
            if (val2 > 102400.0d) {
                val = b / MB;
                suffix = "MB";
            } else {
                double val3 = b;
                val = val3 / KB;
                suffix = "KB";
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.FORMAT.format((bytes < 0 ? -1 : 1) * val));
        sb.append(" ");
        sb.append(suffix);
        return sb.toString();
    }
}
