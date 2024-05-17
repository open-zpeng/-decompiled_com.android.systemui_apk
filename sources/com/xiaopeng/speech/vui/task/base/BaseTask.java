package com.xiaopeng.speech.vui.task.base;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.task.TaskWrapper;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.ResourceUtil;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.VuiMode;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
/* loaded from: classes.dex */
public abstract class BaseTask implements Task, IVuiElementBuilder {
    private String mPackageName;
    private String mPackageVersion;
    private String sceneId;
    public TaskWrapper wrapper;
    private String TAG = "BaseTask";
    private String mSceneIdPrefix = null;
    private String[] bizTypeEnum = {"Address", "Navi", "Route", "Waypoint", "Pic", "Charge", "Connect", "Disconnect", "Null"};
    protected Gson mGson = new Gson();

    public BaseTask(TaskWrapper viewWrapper) {
        this.wrapper = viewWrapper;
        TaskWrapper taskWrapper = this.wrapper;
        this.sceneId = taskWrapper != null ? taskWrapper.getSceneId() : "";
        init();
    }

    private void init() {
        this.mPackageName = Foo.getContext().getApplicationInfo().packageName;
        PackageManager packageManager = Foo.getContext().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(this.mPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.mPackageVersion = packageInfo.versionName;
        this.mSceneIdPrefix = this.mPackageName + "-" + this.mPackageVersion;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        execute();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(9:10|11|(3:312|313|(2:319|(2:321|322)(6:324|14|15|16|(7:136|137|(1:139)(1:304)|140|141|142|(4:(1:145)(2:236|237)|146|(4:148|149|150|(11:168|169|(2:225|226)|171|(5:173|(1:177)|185|179|(1:181))|186|187|188|189|190|(9:192|193|194|(6:200|201|202|203|204|205)|209|202|203|204|205)(4:214|215|216|217))(5:152|(1:154)|155|(3:164|165|(1:167))|157))(1:235)|158)(4:238|239|240|(5:242|243|244|(3:246|(1:256)|(1:253)(1:254))(4:257|258|259|260)|255)(3:266|267|(1:298)(5:269|(1:271)(1:297)|272|(6:274|(3:286|287|(5:289|290|279|280|281))|278|279|280|281)(1:296)|282))))(5:18|19|20|21|(4:(1:24)(2:97|98)|25|(4:27|28|29|(10:31|32|33|(1:35)|37|(3:50|51|(1:53))|39|40|41|42)(9:56|57|58|(1:60)|61|62|63|64|(2:73|(4:75|76|77|78)(1:83))(4:68|69|70|71)))(1:96)|43)(5:99|100|101|(6:103|104|105|106|(3:119|120|(2:122|123))|(4:109|110|111|112)(1:118))(1:129)|113))|44)))|13|14|15|16|(0)(0)|44) */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x05c1, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0120, code lost:
        if (r1.equals(com.xiaopeng.vui.commons.VuiMode.SILENT) != false) goto L185;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:173:0x03ea  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x0091 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r10v25 */
    /* JADX WARN: Type inference failed for: r10v35 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.xiaopeng.vui.commons.model.VuiElement buildView(java.lang.ref.SoftReference<android.view.View> r24, java.util.List<com.xiaopeng.vui.commons.model.VuiElement> r25, java.util.List<java.lang.Integer> r26, java.lang.ref.SoftReference<com.xiaopeng.vui.commons.IVuiElementListener> r27, java.util.List<java.lang.String> r28, long r29, java.util.List<java.lang.String> r31, java.util.List<java.lang.String> r32, java.lang.String r33, int r34, boolean r35, boolean r36, com.xiaopeng.vui.commons.IVuiElementChangedListener r37) {
        /*
            Method dump skipped, instructions count: 1493
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.task.base.BaseTask.buildView(java.lang.ref.SoftReference, java.util.List, java.util.List, java.lang.ref.SoftReference, java.util.List, long, java.util.List, java.util.List, java.lang.String, int, boolean, boolean, com.xiaopeng.vui.commons.IVuiElementChangedListener):com.xiaopeng.vui.commons.model.VuiElement");
    }

    private VuiElement buildVuiElementGroup(SoftReference<View> view, String fatherId, List<Integer> customizeIds, SoftReference<IVuiElementListener> customViewVuiHandler, List<String> idList, long timeStamp, List<String> allIdList, List<String> bizIds, int position, List<VuiElement> elements, boolean isDynamic, boolean isRecyclerView, IVuiElementChangedListener elementChangedListener) {
        List<VuiElement> childElements;
        VuiElement element = buildVuiElement(view, idList, allIdList, fatherId, position, timeStamp, isDynamic, isRecyclerView);
        if (element == null || element.getId() == null || element.getType() == null || element.getType().equals(VuiElementType.XSLIDER.getType())) {
            return element;
        }
        String fatherId2 = element.getId();
        if (element.getType().equals(VuiElementType.UNKNOWN.getType())) {
            String str = this.TAG;
            LogUtils.d(str, "element type is unknown" + element.getLabel());
            removeId(view, element.getId(), idList, allIdList);
            return null;
        } else if (element.getType().equals(VuiElementType.STATEFULBUTTON.getType())) {
            return element;
        } else {
            if (view != null && (view.get() instanceof IVuiElement)) {
                IVuiElement IVuiElement = (IVuiElement) view.get();
                if (IVuiElement.isVuiLayoutLoadable() && view.get().getVisibility() != 0) {
                    return element;
                }
                if (!"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName()) && view.get().getTag(R.id.disableChildVuiAttrsWhenInvisible) != null && view.get().getVisibility() != 0) {
                    return element;
                }
            }
            List<VuiElement> childElements2 = element.getElements();
            if (childElements2 != null) {
                childElements = childElements2;
            } else {
                childElements = new ArrayList<>();
            }
            List<VuiElement> childElements3 = childElements;
            getChildElements(view, childElements, customizeIds, customViewVuiHandler, idList, timeStamp, allIdList, bizIds, fatherId2, element.isLayoutLoadable(), isRecyclerView, elementChangedListener);
            if (childElements3.size() > 0) {
                element.setElements(childElements3);
                return element;
            }
            return element;
        }
    }

    private void setVuiElementChangedListener(SoftReference<View> view, IVuiElementChangedListener listener, String elementId) {
        if (listener != null && (view.get() instanceof IVuiElement)) {
            IVuiElement vuiElement = (IVuiElement) view.get();
            if (TextUtils.isEmpty(vuiElement.getVuiElementId())) {
                vuiElement.setVuiElementId(elementId);
            }
            if (vuiElement.getVuiElementChangedListener() == null) {
                ((IVuiElement) view.get()).setVuiElementChangedListener(listener);
            }
        }
    }

    private void removeId(SoftReference<View> view, String id, List<String> idList, List<String> allIdList) {
        if (view == null || view.get() == null) {
            return;
        }
        if (idList.contains(id)) {
            idList.remove(id);
        }
        if (allIdList != null && allIdList.contains(id)) {
            allIdList.remove(id);
        }
        if (!"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName())) {
            String tag = (String) view.get().getTag(R.id.vuiElementId);
            if (!TextUtils.isEmpty(tag)) {
                view.get().setTag(R.id.vuiElementId, "");
                view.get().setTag(null);
            }
        }
    }

    private String escapeQueryChars(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                sb.append(" ");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private VuiElement buildVuiElement(SoftReference<View> view, List<String> idList, List<String> allIdList, String fatherId, int position, long timeStamp, boolean isDynamic, boolean isRecyclerView) {
        boolean layoutloadable;
        if (view == null || !(view.get() instanceof IVuiElement)) {
            return null;
        }
        VuiElement element = buildVuiElementAttr(view);
        if (element != null) {
            IVuiElement vuiFriendlyView = (IVuiElement) view.get();
            String id = getElementId(view, VuiConstants.ATTRS_ELEMENT_ID);
            handleId(id, element, idList, allIdList, view, fatherId, position, isDynamic, isRecyclerView);
            if (vuiFriendlyView.isVuiLayoutLoadable()) {
                layoutloadable = true;
            } else if (isDynamic && !VuiElementType.XGROUPHEADER.getType().equals(element.getType())) {
                layoutloadable = true;
            } else {
                layoutloadable = false;
            }
            if (layoutloadable) {
                element.setLayoutLoadable(Boolean.valueOf(layoutloadable));
            }
            JSONObject vuiPropsJson = vuiFriendlyView.getVuiProps();
            if (vuiPropsJson != null) {
                if (VuiElementType.STATEFULBUTTON.getType().equals(element.getType()) || VuiElementType.RECYCLEVIEW.getType().equals(element.getType())) {
                    createElementByProps(view, element, vuiPropsJson, timeStamp, layoutloadable, false);
                } else if (vuiPropsJson.keys().hasNext()) {
                    element.setProps((JsonObject) this.mGson.fromJson(vuiPropsJson.toString(), (Class<Object>) JsonObject.class));
                }
            }
            if (view.get() != null && (view.get() instanceof ViewGroup) && view.get().getVisibility() == 0 && !vuiFriendlyView.isVuiModeEnabled() && view.get() != null && VuiElementType.GROUP.getType().equals(element.getType()) && TextUtils.isEmpty(element.getLabel()) && TextUtils.isEmpty(vuiFriendlyView.getVuiAction())) {
                if (view.get().getId() != -1) {
                    if (VuiUtils.getViewMode() != null && VuiUtils.getViewMode().equals(VuiMode.DISABLED)) {
                        LogUtils.d(this.TAG, "ViewGroup is ignored:" + view.get());
                        if (!isContainsInAllCache(element.getId())) {
                            removeId(view, element.getId(), idList, allIdList);
                            return null;
                        }
                    }
                } else {
                    LogUtils.d(this.TAG, "ViewGroup is ignored:" + view.get());
                    removeId(view, element.getId(), idList, allIdList);
                    return null;
                }
            }
        }
        return element;
    }

    private boolean isContainsInAllCache(String id) {
        VuiSceneCache vuiSceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        VuiElement targetElement = vuiSceneCache.getVuiElementById(this.sceneId, id);
        if (targetElement == null) {
            LogUtils.e(this.TAG, "缓存中没有此元素");
            return false;
        } else if (!VuiElementType.GROUP.getType().equals(targetElement.getType())) {
            LogUtils.e(this.TAG, "缓存中没有此元素");
            return false;
        } else {
            return true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x01ba  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x01c9  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x028f  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00fc  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0113  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x018e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.xiaopeng.vui.commons.model.VuiElement buildVuiElementAttr(java.lang.ref.SoftReference<android.view.View> r18) {
        /*
            Method dump skipped, instructions count: 682
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.task.base.BaseTask.buildVuiElementAttr(java.lang.ref.SoftReference):com.xiaopeng.vui.commons.model.VuiElement");
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x0103 A[Catch: Exception -> 0x010f, TryCatch #2 {Exception -> 0x010f, blocks: (B:40:0x00a5, B:55:0x00da, B:57:0x0103, B:58:0x010b, B:46:0x00b7, B:48:0x00c3, B:53:0x00d4, B:54:0x00d7, B:51:0x00c9), top: B:211:0x00a5 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void createElementByProps(java.lang.ref.SoftReference<android.view.View> r26, com.xiaopeng.vui.commons.model.VuiElement r27, org.json.JSONObject r28, long r29, boolean r31, boolean r32) {
        /*
            Method dump skipped, instructions count: 1138
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.task.base.BaseTask.createElementByProps(java.lang.ref.SoftReference, com.xiaopeng.vui.commons.model.VuiElement, org.json.JSONObject, long, boolean, boolean):void");
    }

    private VuiElement getStateFulBtnCurr(String curState, List<VuiElement> elements) {
        String[] indexs;
        if (TextUtils.isEmpty(curState) || elements == null || elements.isEmpty() || TextUtils.isEmpty(curState) || (indexs = curState.split("_")) == null || indexs.length <= 1 || !VuiUtils.isNumeric(indexs[1])) {
            return null;
        }
        return elements.get(Integer.parseInt(indexs[1]) - 1);
    }

    private void getChildElements(SoftReference<View> view, List<VuiElement> elements, List<Integer> customizeIds, SoftReference<IVuiElementListener> customsizeCallback, List<String> idList, long timeStamp, List<String> allIdList, List<String> bizIds, String fatherId, boolean isDynamic, boolean isRecyclerView, IVuiElementChangedListener elementChangedListener) {
        int i;
        int len;
        String str;
        String bizId;
        BaseTask baseTask = this;
        if (view == null || view.get() == null) {
            return;
        }
        if (view.get() != null && (view.get() instanceof IVuiElement) && (bizId = ((IVuiElement) view.get()).getVuiBizId()) != null) {
            if (bizIds.contains(bizId)) {
                return;
            }
            bizIds.add(bizId);
        }
        if (view.get() == null || !(view.get() instanceof RecyclerView)) {
            if (view.get() instanceof ListView) {
                SoftReference<ListView> listView = new SoftReference<>((ListView) view.get());
                int i2 = 0;
                while (listView.get() != null && i2 < listView.get().getCount()) {
                    if (listView.get().getAdapter() == null) {
                        i = i2;
                    } else {
                        SoftReference<View> child = new SoftReference<>(listView.get().getAdapter().getView(i2, null, listView.get()));
                        i = i2;
                        addChildElement(child, elements, bizIds, customizeIds, customsizeCallback, idList, timeStamp, allIdList, fatherId, i2, isDynamic, isRecyclerView, elementChangedListener);
                    }
                    i2 = i + 1;
                }
            } else if (view.get() instanceof ViewGroup) {
                SoftReference<ViewGroup> viewGroup = new SoftReference<>((ViewGroup) view.get());
                for (int i3 = 0; viewGroup.get() != null && i3 < viewGroup.get().getChildCount(); i3++) {
                    SoftReference<View> child2 = new SoftReference<>(viewGroup.get().getChildAt(i3));
                    addChildElement(child2, elements, bizIds, customizeIds, customsizeCallback, idList, timeStamp, allIdList, fatherId, i3, isDynamic, isRecyclerView, elementChangedListener);
                }
            }
        } else if (((RecyclerView) view.get()).getAdapter() != null) {
            SoftReference<RecyclerView> recyclerView = new SoftReference<>((RecyclerView) view.get());
            int len2 = recyclerView.get().getChildCount();
            if (recyclerView.get() != null && recyclerView.get().getChildCount() == 0) {
                LogUtils.e(baseTask.TAG, "RecyclerView 的child count 为0,view：" + view.get());
            }
            String str2 = baseTask.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("RecyclerView 的child count:");
            sb.append(recyclerView.get().getChildCount());
            String str3 = ",view：";
            sb.append(",view：");
            sb.append(recyclerView.get());
            LogUtils.d(str2, sb.toString());
            int i4 = 0;
            while (recyclerView.get() != null && i4 < recyclerView.get().getChildCount() && i4 < recyclerView.get().getAdapter().getItemCount()) {
                SoftReference<View> child3 = new SoftReference<>(recyclerView.get().getChildAt(i4));
                if (child3.get() == null) {
                    LogUtils.e(baseTask.TAG, "RecyclerView 的child 为null,postion:" + i4);
                }
                int i5 = i4;
                String str4 = str3;
                int len3 = len2;
                addChildElement(child3, elements, bizIds, customizeIds, customsizeCallback, idList, timeStamp, allIdList, fatherId, i4, isDynamic, true, elementChangedListener);
                if (recyclerView.get() == null || recyclerView.get().getChildCount() == len3) {
                    len = len3;
                    str = str4;
                    baseTask = this;
                } else {
                    len = len3;
                    baseTask = this;
                    String str5 = baseTask.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("RecyclerView is not same child count:");
                    sb2.append(recyclerView.get().getChildCount());
                    str = str4;
                    sb2.append(str);
                    sb2.append(recyclerView.get());
                    sb2.append(",len:");
                    sb2.append(len);
                    LogUtils.w(str5, sb2.toString());
                }
                i4 = i5 + 1;
                len2 = len;
                str3 = str;
            }
        }
    }

    private void addChildElement(SoftReference<View> child, List<VuiElement> elements, List<String> bizIds, List<Integer> customsizeIds, SoftReference<IVuiElementListener> customsizeCallback, List<String> idList, long timeStamp, List<String> allIdList, String fatherId, int postiton, boolean isDynamic, boolean isRecyclerView, IVuiElementChangedListener elementChangedListener) {
        VuiElement childElement = buildView(child, elements, customsizeIds, customsizeCallback, idList, timeStamp, allIdList, bizIds, fatherId, postiton, isDynamic, isRecyclerView, elementChangedListener);
        if (childElement == null || childElement.getId() == null) {
            return;
        }
        String fatherId2 = childElement.getId();
        elements.add(childElement);
        childElement.setTimestamp(timeStamp);
        if ((child.get() instanceof ViewGroup) && childElement.getType() != null && childElement.getType().equals(VuiElementType.STATEFULBUTTON.getType())) {
            getChildElements(child, elements, customsizeIds, customsizeCallback, idList, timeStamp, allIdList, bizIds, fatherId2, isDynamic, isRecyclerView, elementChangedListener);
        }
    }

    public String handleId(String id, VuiElement childElement, List<String> idList, List<String> allIdList, SoftReference<View> child, String fatherId, int position, boolean isDynamic, boolean isRecyclerView) {
        if ("com.android.systemui".equals(VuiSceneManager.instance().getmPackageName())) {
            if (idList != null && !idList.contains(id)) {
                idList.add(id);
                if (childElement != null) {
                    childElement.setId(id);
                }
            }
            if (allIdList != null && !allIdList.contains(id)) {
                allIdList.add(id);
            }
            return id;
        }
        if ((!isDynamic && !isRecyclerView && (id == null || id.equals("0") || !id.contains("_"))) || (child != null && (child.get() instanceof RecyclerView))) {
            String tag = child.get() != null ? (String) child.get().getTag(R.id.vuiElementId) : null;
            if (!TextUtils.isEmpty(tag)) {
                String id2 = tag;
                if (!idList.contains(id2)) {
                    idList.add(id2);
                } else {
                    LogUtils.w(this.TAG, "更新不合法");
                }
                if (allIdList != null && !allIdList.contains(id2)) {
                    allIdList.add(id2);
                }
                if (childElement != null) {
                    childElement.setId(id2);
                }
                return id2;
            }
        }
        if (id == null || id.equals("0")) {
            if (fatherId == null) {
                id = "4657_" + position;
            } else {
                id = fatherId + "_" + position;
            }
        } else if (idList != null && idList.contains(id)) {
            if (fatherId == null) {
                id = id + "_" + position;
            } else {
                id = fatherId + "_" + id;
                if (idList.contains(id)) {
                    id = id + "_" + position;
                }
            }
        }
        if (idList != null && !idList.contains(id)) {
            idList.add(id);
            if (childElement != null) {
                childElement.setId(id);
            }
            setVuiTag(child, id);
            if (allIdList != null && !allIdList.contains(id)) {
                allIdList.add(id);
            }
            return id;
        }
        LogUtils.w(this.TAG, "容错失败：" + id);
        return id + "_error";
    }

    public void setVuiTag(SoftReference<View> view, String id) {
        if ("com.android.systemui".equals(VuiSceneManager.instance().getmPackageName()) || view == null || view.get() == null) {
            return;
        }
        if (view.get().getTag() == null) {
            view.get().setTag(id);
            view.get().setTag(R.id.vuiElementId, id);
            return;
        }
        String tag = (String) view.get().getTag(R.id.vuiElementId);
        if (tag != null && !tag.equals(id)) {
            view.get().setTag(id);
            view.get().setTag(R.id.vuiElementId, id);
        }
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.xiaopeng.speech.vui.task.base.BaseTask.1
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(Foo.getContext(), message, 1).show();
            }
        });
    }

    private String getElementLabel(IVuiElement view) {
        String label = view.getVuiLabel();
        if (label != null) {
            if (label.contains("|")) {
                String[] labels = label.split("\\|");
                if (labels.length > 5) {
                    LogUtils.e(this.TAG, "label 不能超过5个");
                    return null;
                }
            }
            return label;
        }
        if (!"com.android.systemui".equals(VuiSceneManager.instance().getmPackageName())) {
            Object isSupport = ((View) view).getTag(R.id.vuiLabelUnSupportText);
            if ((isSupport instanceof Boolean) && ((Boolean) isSupport).booleanValue()) {
                return label == null ? "" : label;
            }
        }
        CharSequence text = null;
        if (view instanceof TextView) {
            if (!(view instanceof EditText)) {
                text = ((TextView) view).getText();
            } else {
                text = ((EditText) view).getHint();
                if (text == null) {
                    text = ((EditText) view).getText();
                }
            }
        }
        return !TextUtils.isEmpty(text) ? text.toString() : "";
    }

    private String getElementId(SoftReference<View> view, String name) {
        if (view == null || !(view.get() instanceof IVuiElement)) {
            return null;
        }
        IVuiElement viewVuiFriendly = (IVuiElement) view.get();
        if (name.equals(VuiConstants.ATTRS_ELEMENT_ID) && view.get() != null && view.get().getTag() != null && (view.get().getTag() instanceof String)) {
            String tag = (String) view.get().getTag();
            if (tag.startsWith("4657")) {
                String str = this.TAG;
                LogUtils.d(str, "client has set Tag" + tag);
                return tag;
            }
        }
        String elementId = null;
        if (name.equals(VuiConstants.ATTRS_ELEMENT_FATHER_ID)) {
            elementId = viewVuiFriendly.getVuiFatherElementId();
        } else if (name.equals(VuiConstants.ATTRS_ELEMENT_ID)) {
            elementId = viewVuiFriendly.getVuiElementId();
        }
        if (elementId != null && !TextUtils.isEmpty(elementId)) {
            if (isNumber(elementId)) {
                return elementId;
            }
            return "" + ResourceUtil.getId(Foo.getContext(), elementId);
        } else if (name.contains(VuiConstants.ATTRS_ELEMENT_FATHER_ID) || view.get() == null || view.get().getId() == -1) {
            return null;
        } else {
            return "" + view.get().getId();
        }
    }

    private boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]+(_[0-9]+)*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public List<VuiElement> buildVuiElement(View view) {
        return null;
    }

    public List<VuiElement> buildVuiElement(List<View> views) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VuiScene getNewVuiScene(String sceneId, long timeStemp) {
        return new VuiScene.Builder().sceneId(sceneId).appVersion(this.mPackageVersion).packageName(this.mPackageName).timestamp(timeStemp).build();
    }

    protected String getSceneUnqiueId(String sceneId) {
        return this.mSceneIdPrefix + "-" + sceneId;
    }

    public String getSceneId() {
        return this.sceneId;
    }
}
