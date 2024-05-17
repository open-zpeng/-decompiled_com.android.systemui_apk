package com.android.systemui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.ViewProvider;
import com.android.systemui.shared.plugins.PluginManager;
/* loaded from: classes21.dex */
public class PluginInflateContainer extends AutoReinflateContainer implements PluginListener<ViewProvider> {
    private static final String TAG = "PluginInflateContainer";
    private Class<?> mClass;
    private View mPluginView;

    public PluginInflateContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PluginInflateContainer);
        String viewType = a.getString(R.styleable.PluginInflateContainer_viewType);
        try {
            this.mClass = Class.forName(viewType);
        } catch (Exception e) {
            Log.d(TAG, "Problem getting class info " + viewType, e);
            this.mClass = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.AutoReinflateContainer, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mClass != null) {
            ((PluginManager) Dependency.get(PluginManager.class)).addPluginListener(this, this.mClass);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.AutoReinflateContainer, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mClass != null) {
            ((PluginManager) Dependency.get(PluginManager.class)).removePluginListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.AutoReinflateContainer
    public void inflateLayoutImpl() {
        View view = this.mPluginView;
        if (view != null) {
            addView(view);
        } else {
            super.inflateLayoutImpl();
        }
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(ViewProvider plugin, Context context) {
        this.mPluginView = plugin.getView();
        inflateLayout();
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(ViewProvider plugin) {
        this.mPluginView = null;
        inflateLayout();
    }
}
