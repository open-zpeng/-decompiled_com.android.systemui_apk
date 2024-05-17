package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import com.android.systemui.R;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
/* loaded from: classes21.dex */
public class RowInflaterTask implements InflationTask, AsyncLayoutInflater.OnInflateFinishedListener {
    private static final String TAG = "RowInflaterTask";
    private static final boolean TRACE_ORIGIN = true;
    private boolean mCancelled;
    private NotificationEntry mEntry;
    private Throwable mInflateOrigin;
    private RowInflationFinishedListener mListener;

    /* loaded from: classes21.dex */
    public interface RowInflationFinishedListener {
        void onInflationFinished(ExpandableNotificationRow expandableNotificationRow);
    }

    public void inflate(Context context, ViewGroup parent, NotificationEntry entry, RowInflationFinishedListener listener) {
        this.mInflateOrigin = new Throwable("inflate requested here");
        this.mListener = listener;
        AsyncLayoutInflater inflater = new AsyncLayoutInflater(context);
        this.mEntry = entry;
        entry.setInflationTask(this);
        inflater.inflate(R.layout.status_bar_notification_row, parent, this);
    }

    @Override // com.android.systemui.statusbar.InflationTask
    public void abort() {
        this.mCancelled = true;
    }

    @Override // androidx.asynclayoutinflater.view.AsyncLayoutInflater.OnInflateFinishedListener
    public void onInflateFinished(View view, int resid, ViewGroup parent) {
        if (!this.mCancelled) {
            try {
                this.mEntry.onInflationTaskFinished();
                this.mListener.onInflationFinished((ExpandableNotificationRow) view);
            } catch (Throwable t) {
                if (this.mInflateOrigin != null) {
                    Log.e(TAG, "Error in inflation finished listener: " + t, this.mInflateOrigin);
                    t.addSuppressed(this.mInflateOrigin);
                }
                throw t;
            }
        }
    }
}
