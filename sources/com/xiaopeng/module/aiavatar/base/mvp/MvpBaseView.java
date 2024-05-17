package com.xiaopeng.module.aiavatar.base.mvp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.xiaopeng.module.aiavatar.base.mvp.BaseModel;
import com.xiaopeng.module.aiavatar.base.mvp.BasePresenter;
import com.xiaopeng.module.aiavatar.base.mvp.BaseView;
import com.xiaopeng.module.aiavatar.system.EventDispatcherManager;
/* loaded from: classes23.dex */
public abstract class MvpBaseView<M extends BaseModel, V extends BaseView, P extends BasePresenter> extends FrameLayout {
    protected M model;
    protected P presenter;
    protected V view;

    protected abstract M createModel();

    protected abstract P createPresenter();

    protected abstract V createView();

    protected abstract void initView();

    public abstract void postGLTask(Runnable runnable);

    public MvpBaseView(@NonNull Context context) {
        super(context);
        init();
    }

    public MvpBaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MvpBaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        M m;
        V v;
        this.model = createModel();
        this.presenter = createPresenter();
        this.view = createView();
        P p = this.presenter;
        if (p != null && (m = this.model) != null && (v = this.view) != null) {
            p.setVM(v, m);
            this.model.setPresenter(this.presenter);
            EventDispatcherManager.getInstance().register(this.model);
        }
        initView();
    }
}
