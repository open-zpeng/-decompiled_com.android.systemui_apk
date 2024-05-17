package com.xiaopeng.speech.vui.model;

import android.view.View;
import com.xiaopeng.speech.vui.listener.IVuiEventListener;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class VuiSceneInfo {
    private IVuiElementChangedListener elementChangedListener;
    private int state = VuiSceneState.IDLE.getState();
    private View rootView = null;
    private IVuiSceneListener listener = null;
    private List<String> subSceneList = null;
    private List<String> idList = null;
    private List<String> wholeSceneIds = null;
    private boolean isWholeScene = true;
    private int addSubSceneNum = 0;
    private boolean isContainNotChildrenView = false;
    private List<SoftReference<View>> notChildrenViewList = null;
    private List<String> notChildrenViewIdList = null;
    private String lastUpdateStr = null;
    private String lastAddStr = null;
    private IVuiEventListener eventListener = null;
    private boolean isBuild = false;
    private boolean isBuildComplete = false;

    public void updateAddSubSceneNum() {
        this.addSubSceneNum++;
    }

    public boolean isFull() {
        List<String> list;
        return !this.isWholeScene || (list = this.subSceneList) == null || this.addSubSceneNum == list.size();
    }

    public boolean isWholeScene() {
        return this.isWholeScene;
    }

    public void setWholeScene(boolean wholeScene) {
        this.isWholeScene = wholeScene;
    }

    public void setWholeSceneId(String wholeSceneId) {
        if (this.wholeSceneIds == null) {
            this.wholeSceneIds = new ArrayList();
        }
        if (!this.wholeSceneIds.contains(wholeSceneId)) {
            this.wholeSceneIds.add(wholeSceneId);
        }
    }

    public List<String> getWholeSceneId() {
        return this.wholeSceneIds;
    }

    public List<String> getIdList() {
        return this.idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public int getState() {
        return this.state;
    }

    public View getRootView() {
        return this.rootView;
    }

    public IVuiSceneListener getListener() {
        return this.listener;
    }

    public List<String> getSubSceneList() {
        return this.subSceneList;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public void setListener(IVuiSceneListener listener) {
        this.listener = listener;
    }

    public void setSubSceneList(List<String> subSceneList) {
        this.subSceneList = subSceneList;
    }

    public void setContainNotChildrenView(boolean containNotChildrenView) {
        this.isContainNotChildrenView = containNotChildrenView;
    }

    public boolean isContainNotChildrenView() {
        return this.isContainNotChildrenView;
    }

    public void setNotChildrenViewList(List<SoftReference<View>> list) {
        this.notChildrenViewList = list;
    }

    public List<SoftReference<View>> getNotChildrenViewList() {
        return this.notChildrenViewList;
    }

    public List<String> getNotChildrenViewIdList() {
        return this.notChildrenViewIdList;
    }

    public void setNotChildrenViewIdList(List<String> notChildrenViewIdList) {
        this.notChildrenViewIdList = notChildrenViewIdList;
    }

    public void resetViewInfo() {
        this.rootView = null;
        this.listener = null;
        this.eventListener = null;
        this.elementChangedListener = null;
    }

    public void reset(boolean resetViewInfo) {
        if (resetViewInfo) {
            this.state = VuiSceneState.IDLE.getState();
        } else {
            this.state = VuiSceneState.INIT.getState();
        }
        this.isContainNotChildrenView = false;
        this.notChildrenViewList = null;
        this.notChildrenViewIdList = null;
        this.addSubSceneNum = 0;
        this.lastUpdateStr = null;
        this.lastAddStr = null;
        this.isBuild = false;
        this.isWholeScene = true;
        this.subSceneList = null;
        this.idList = null;
        this.wholeSceneIds = null;
        this.isBuildComplete = false;
        if (resetViewInfo) {
            resetViewInfo();
        }
    }

    public String getLastUpdateStr() {
        return this.lastUpdateStr;
    }

    public String getLastAddStr() {
        return this.lastAddStr;
    }

    public void setLastUpdateStr(String lastUpdateStr) {
        this.lastUpdateStr = lastUpdateStr;
    }

    public void setLastAddStr(String lastAddStr) {
        this.lastAddStr = lastAddStr;
    }

    public void setEventListener(IVuiEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public IVuiEventListener getEventListener() {
        return this.eventListener;
    }

    public boolean isBuild() {
        return this.isBuild;
    }

    public void setBuild(boolean build) {
        this.isBuild = build;
    }

    public boolean isBuildComplete() {
        return this.isBuildComplete;
    }

    public void setBuildComplete(boolean buildComplete) {
        this.isBuildComplete = buildComplete;
    }

    public IVuiElementChangedListener getElementChangedListener() {
        return this.elementChangedListener;
    }

    public void setElementChangedListener(IVuiElementChangedListener elementChangedListener) {
        this.elementChangedListener = elementChangedListener;
    }
}
