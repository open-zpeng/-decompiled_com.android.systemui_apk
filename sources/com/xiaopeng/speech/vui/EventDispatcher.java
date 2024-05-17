package com.xiaopeng.speech.vui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.event.IVuiEvent;
import com.xiaopeng.speech.vui.event.ListItemClickEvent;
import com.xiaopeng.speech.vui.event.ScrollByXEvent;
import com.xiaopeng.speech.vui.event.ScrollByYEvent;
import com.xiaopeng.speech.vui.event.ScrollToEvent;
import com.xiaopeng.speech.vui.event.SetCheckEvent;
import com.xiaopeng.speech.vui.event.SetValueEvent;
import com.xiaopeng.speech.vui.listener.IUnityVuiSceneListener;
import com.xiaopeng.speech.vui.listener.IVuiEventListener;
import com.xiaopeng.speech.vui.model.VuiEventImpl;
import com.xiaopeng.speech.vui.model.VuiEventInfo;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.AnimationObj;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class EventDispatcher implements Runnable {
    private Context mContext;
    private VuiScene mEventData;
    private VuiElement vuiElement;
    private WeakReference<View> weakView;
    private Gson gson = new Gson();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Map<String, IVuiEvent> events = new HashMap();
    private String TAG = "EventDispatcher";
    private String mDispatchSceneId = null;

    public EventDispatcher(Context context, boolean initEvent) {
        this.mContext = context;
        if (initEvent) {
            initEvents();
        }
    }

    private void initEvents() {
        if (!VuiUtils.isThirdApp(VuiSceneManager.instance().getmPackageName())) {
            this.events.put("scrollTo", new ScrollToEvent());
            this.events.put("Click", new ListItemClickEvent());
            this.events.put("click", new ListItemClickEvent());
            this.events.put("SetCheck", new SetCheckEvent());
            this.events.put("listItemClick", new ListItemClickEvent());
            this.events.put(VuiAction.SCROLLBYY.getName(), new ScrollByYEvent());
            this.events.put(VuiAction.SCROLLBYX.getName(), new ScrollByXEvent());
            this.events.put(VuiAction.SETVALUE.getName(), new SetValueEvent());
        }
    }

    public void dispatch(String vuiEvent, String data) {
        boolean customDoAction;
        Object doAction;
        if (data == null) {
            return;
        }
        LogUtils.logInfo(this.TAG, Thread.currentThread().getName() + "-----result datasource =====" + data);
        final long startTime = System.currentTimeMillis();
        this.mEventData = (VuiScene) this.gson.fromJson(data, (Class<Object>) VuiScene.class);
        VuiScene vuiScene = this.mEventData;
        if (vuiScene == null) {
            LogUtils.e(this.TAG, "mEventData is Null");
        } else if (vuiScene.getSceneId() == null || !VuiUtils.getPackageNameFromSceneId(this.mEventData.getSceneId()).equals(VuiSceneManager.instance().getmPackageName())) {
        } else {
            if (this.mEventData.getSceneId().equals(VuiEngineImpl.mActiveSceneId)) {
                this.vuiElement = getHitVuiElements(this.mEventData.getElements());
                if (this.vuiElement == null) {
                    LogUtils.e(this.TAG, "事件派发Element 为空");
                    return;
                }
                LogUtils.logDebug(this.TAG, "VuiElement  ===== " + this.vuiElement.toString());
                if (!VuiUtils.isThirdApp(VuiSceneManager.instance().getmPackageName())) {
                    this.mDispatchSceneId = this.mEventData.getSceneId();
                    LogUtils.logInfo(this.TAG, "dispatch Scene:" + this.mDispatchSceneId);
                    if (!VuiElementType.VIRTUALLIST.getType().equals(this.vuiElement.getType()) && !VuiElementType.VIRTUALLISTITEM.getType().equals(this.vuiElement.getType())) {
                        final VuiEventInfo viewInfo = VuiSceneManager.instance().findView(this.mDispatchSceneId, this.vuiElement.getId());
                        if (viewInfo == null || viewInfo.hitView == null) {
                            if (this.mDispatchSceneId.endsWith("Dialog") || this.mDispatchSceneId.endsWith("dialog")) {
                                final IVuiSceneListener listener = VuiSceneManager.instance().getVuiSceneListener(this.mEventData.getSceneId());
                                if (listener != null) {
                                    this.handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.EventDispatcher.3
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            listener.onVuiEvent(null, new VuiEventImpl(EventDispatcher.this.vuiElement));
                                        }
                                    });
                                    return;
                                }
                                return;
                            }
                            LogUtils.e(this.TAG, "没找到正确的执行操作的view");
                            return;
                        }
                        if (!"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName()) && (doAction = viewInfo.hitView.getTag(R.id.customDoAction)) != null) {
                            boolean customDoAction2 = ((Boolean) doAction).booleanValue();
                            customDoAction = customDoAction2;
                        } else {
                            customDoAction = false;
                        }
                        if ((isCustomView(this.vuiElement) || customDoAction) && callOnVuiEvent(this.vuiElement, viewInfo)) {
                            LogUtils.logInfo(this.TAG, "custom view dispatch success");
                            return;
                        } else if (!TextUtils.isEmpty(this.vuiElement.getId())) {
                            if (viewInfo.hitView != null) {
                                this.weakView = new WeakReference<>(viewInfo.hitView);
                                if (!viewInfo.hitView.isEnabled() && !isCustomHandle(this.vuiElement, viewInfo.hitView)) {
                                    LogUtils.e(this.TAG, "view 不可操作");
                                    if (!isCustomFeedback(this.vuiElement, viewInfo.hitView)) {
                                        handleDisableFeedBack(this.vuiElement, viewInfo.hitView);
                                        return;
                                    }
                                    return;
                                }
                                final VuiEvent event = new VuiEventImpl(this.vuiElement);
                                getResultAction(this.vuiElement);
                                AnimationObj animationObj = this.vuiElement.getAnimation();
                                final boolean effectOnly = animationObj == null ? false : animationObj.isEffectOnly();
                                this.handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.EventDispatcher.4
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        if (effectOnly) {
                                            LogUtils.w(EventDispatcher.this.TAG, "不执行模拟点击");
                                            return;
                                        }
                                        IVuiEventListener eventListener = VuiSceneManager.instance().getVuiEventListener(viewInfo.sceneId);
                                        if (eventListener != null) {
                                            eventListener.onVuiEventExecutioned();
                                        }
                                        IVuiSceneListener listener2 = VuiSceneManager.instance().getVuiSceneListener(viewInfo.sceneId);
                                        if (listener2 != null) {
                                            listener2.onVuiEventExecutioned();
                                        }
                                        if (listener2 != null && listener2.onInterceptVuiEvent(viewInfo.hitView, event)) {
                                            LogUtils.i(EventDispatcher.this.TAG, "user intercept Events");
                                            return;
                                        }
                                        if (viewInfo.hitView instanceof IVuiElementListener) {
                                            boolean b = ((IVuiElementListener) viewInfo.hitView).onVuiElementEvent(viewInfo.hitView, event);
                                            if (b) {
                                                return;
                                            }
                                        }
                                        EventDispatcher.this.run();
                                        long endTime = System.currentTimeMillis();
                                        String str = EventDispatcher.this.TAG;
                                        LogUtils.logInfo(str, "程序运行时间： " + (endTime - startTime) + "ms");
                                    }
                                });
                                return;
                            }
                            LogUtils.e(this.TAG, "没找到正确的执行操作的view");
                            return;
                        } else {
                            return;
                        }
                    }
                    JsonObject props = this.vuiElement.getProps();
                    if (props.has("parentId")) {
                        String parentId = props.get("parentId").getAsString();
                        VuiEventInfo viewInfo2 = VuiSceneManager.instance().findView(this.mDispatchSceneId, parentId);
                        if (viewInfo2 != null && viewInfo2.hitView != null) {
                            this.weakView = new WeakReference<>(viewInfo2.hitView);
                            this.handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.EventDispatcher.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    EventDispatcher.this.run();
                                }
                            });
                            SetValueEvent ev = (SetValueEvent) this.events.get(VuiAction.SETVALUE.getName());
                            if (ev != null) {
                                ev.setSceneId(viewInfo2.sceneId);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
                LogUtils.logInfo(this.TAG, "Event will dispatch to third app");
                final IVuiSceneListener listener2 = VuiSceneManager.instance().getVuiSceneListener(this.mEventData.getSceneId());
                if (listener2 != null) {
                    this.handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.EventDispatcher.1
                        @Override // java.lang.Runnable
                        public void run() {
                            IVuiSceneListener iVuiSceneListener = listener2;
                            if (iVuiSceneListener instanceof IUnityVuiSceneListener) {
                                ((IUnityVuiSceneListener) iVuiSceneListener).onVuiEvent(EventDispatcher.this.gson.toJson(EventDispatcher.this.vuiElement));
                            } else {
                                iVuiSceneListener.onVuiEvent(new VuiEventImpl(EventDispatcher.this.vuiElement));
                            }
                        }
                    });
                    return;
                }
                return;
            }
            LogUtils.e(this.TAG, "场景不是当前活跃场景，返回");
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        IVuiEvent ev;
        WeakReference<View> weakReference;
        List<String> actions = this.vuiElement.getResultActions();
        if (actions != null && !actions.isEmpty() && (ev = this.events.get(actions.get(0))) != null && (weakReference = this.weakView) != null && weakReference.get() != null) {
            ev.run(this.weakView.get(), this.vuiElement);
        }
    }

    private VuiElement getHitVuiElements(List<VuiElement> vuiElements) {
        if (vuiElements == null || vuiElements.isEmpty()) {
            return null;
        }
        List<String> actions = vuiElements.get(0).getResultActions();
        if (actions != null && !actions.isEmpty()) {
            return vuiElements.get(0);
        }
        if (vuiElements.get(0).getElements() == null || vuiElements.get(0).getElements().isEmpty()) {
            return vuiElements.get(0);
        }
        VuiElement vui = vuiElements.get(0);
        if (vui == null) {
            return null;
        }
        if (isCustomView(vui)) {
            return vui;
        }
        return getHitVuiElements(vui.getElements());
    }

    private boolean callOnVuiEvent(VuiElement vui, final VuiEventInfo viewInfo) {
        if (vui == null || viewInfo == null || viewInfo.hitView == null) {
            return false;
        }
        if (VuiElementType.STATEFULBUTTON.getType().equals(vui.getType()) && !"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName())) {
            Object o = viewInfo.hitView.getTag(R.id.vuiStatefulButtonClick);
            if (o != null) {
                vui.setResultActions(Arrays.asList(VuiAction.CLICK.getName()));
                return false;
            }
            List<VuiElement> elements = vui.getElements();
            if (elements != null && elements.size() == 2) {
                vui.setResultActions(Arrays.asList(VuiAction.CLICK.getName()));
                return false;
            }
        }
        if (!viewInfo.hitView.isEnabled() && !isCustomHandle(vui, viewInfo.hitView)) {
            LogUtils.e(this.TAG, "view 不可操作");
            if (!isCustomFeedback(vui, viewInfo.hitView)) {
                handleDisableFeedBack(vui, viewInfo.hitView);
            }
            return true;
        }
        final VuiEvent event = new VuiEventImpl(vui);
        IVuiEventListener eventListener = VuiSceneManager.instance().getVuiEventListener(viewInfo.sceneId);
        final IVuiSceneListener listener = VuiSceneManager.instance().getVuiSceneListener(viewInfo.sceneId);
        if (viewInfo.hitView instanceof IVuiElementListener) {
            boolean b = ((IVuiElementListener) viewInfo.hitView).onVuiElementEvent(viewInfo.hitView, event);
            if (b && eventListener != null) {
                eventListener.onVuiEventExecutioned();
            }
            if (b && listener != null) {
                listener.onVuiEventExecutioned();
            }
            return b;
        }
        if (listener != null) {
            this.handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.EventDispatcher.5
                @Override // java.lang.Runnable
                public void run() {
                    listener.onVuiEvent(viewInfo.hitView, event);
                }
            });
        }
        if (eventListener != null) {
            eventListener.onVuiEventExecutioned();
        }
        if (listener != null) {
            listener.onVuiEventExecutioned();
        }
        return true;
    }

    private boolean isCustomView(VuiElement vui) {
        if (vui == null) {
            return false;
        }
        if (!VuiElementType.XTABLAYOUT.getType().equals(vui.getType()) && !VuiElementType.XSLIDER.getType().equals(vui.getType()) && !VuiElementType.STATEFULBUTTON.getType().equals(vui.getType()) && !VuiElementType.CUSTOM.getType().equals(vui.getType())) {
            return false;
        }
        return true;
    }

    private String getResultAction(VuiElement element) {
        List<String> actions = this.vuiElement.getResultActions();
        if (actions == null || actions.isEmpty()) {
            return null;
        }
        return actions.get(0);
    }

    private void handleDisableFeedBack(VuiElement vui, View view) {
        JsonObject props;
        if (vui.getProps() != null && (props = vui.getProps()) != null && props.has(VuiConstants.PROPS_FEEDBACK)) {
            boolean hasFeedback = props.get(VuiConstants.PROPS_FEEDBACK).getAsBoolean();
            if (hasFeedback) {
                String tts = "当前该功能不可用";
                if (view != null && view.getTag(R.id.customDisableFeedbackTTS) != null) {
                    tts = (String) view.getTag(R.id.customDisableFeedbackTTS);
                }
                VuiFeedback feedback = new VuiFeedback.Builder().state(-1).content(tts).build();
                VuiEngine.getInstance(this.mContext).vuiFeedback(view, feedback);
            }
        }
    }

    private boolean isCustomHandle(VuiElement vui, View view) {
        JsonObject props;
        if (vui.getProps() != null && (props = vui.getProps()) != null && props.has(VuiConstants.PROPS_DISABLECONTROL)) {
            boolean customControl = props.get(VuiConstants.PROPS_DISABLECONTROL).getAsBoolean();
            return customControl;
        } else if (view != null && !"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName())) {
            Object customControl2 = view.getTag(R.id.customDisableControl);
            if (customControl2 != null) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    private boolean isCustomFeedback(VuiElement vui, View view) {
        if (view != null && !"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName())) {
            Object customControl = view.getTag(R.id.customDisableFeedback);
            if (customControl != null) {
                return true;
            }
            return false;
        }
        return false;
    }
}
