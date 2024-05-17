package com.xiaopeng.speech.vui.utils;

import android.text.TextUtils;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SceneMergeUtils {
    public static List<VuiElement> updateMerge(List<VuiElement> elements, List<VuiElement> updateElements, boolean isAttribute) {
        List<VuiElement> vuiElements = merge(elements, updateElements, isAttribute);
        if (vuiElements == null || vuiElements.isEmpty()) {
            return vuiElements;
        }
        return duplicateRemoval(vuiElements);
    }

    private static List<VuiElement> duplicateRemoval(List<VuiElement> vuiElements) {
        Map<String, VuiElement> dupMap = new HashMap<>();
        Map<String, VuiElement> dupParentMap = new HashMap<>();
        VuiElement emptyElement = new VuiElement();
        emptyElement.setElements(vuiElements);
        emptyElement.setId("-1");
        findNode(emptyElement, null, dupMap, dupParentMap);
        return emptyElement.getElements();
    }

    private static void findNode(VuiElement node, VuiElement parentElement, Map<String, VuiElement> dupMap, Map<String, VuiElement> parentElements) {
        if (node == null) {
            return;
        }
        if (!TextUtils.isEmpty(node.getId())) {
            if (dupMap.containsKey(node.getId())) {
                VuiElement dupElement = dupMap.get(node.getId());
                if (dupElement == null) {
                    return;
                }
                if (node.getTimestamp() >= dupElement.getTimestamp()) {
                    VuiElement parentEle = parentElements.get(node.getId());
                    if (parentEle == null) {
                        return;
                    }
                    if (parentEle.getElements() != null && !parentEle.getElements().isEmpty()) {
                        parentEle.getElements().remove(dupMap.get(node.getId()));
                    }
                    dupMap.put(node.getId(), node);
                    parentElements.put(node.getId(), parentElement);
                }
            } else {
                dupMap.put(node.getId(), node);
                parentElements.put(node.getId(), parentElement);
            }
        }
        List<VuiElement> children = node.getElements();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                findNode(children.get(i), node, dupMap, parentElements);
            }
        }
    }

    public static List<VuiElement> merge(List<VuiElement> elements, List<VuiElement> updateElements, boolean isAttribute) {
        if (updateElements == null || updateElements.isEmpty()) {
            return elements;
        }
        if (elements == null || elements.isEmpty()) {
            return updateElements;
        }
        List<String> indexs = new ArrayList<>();
        for (VuiElement vuiElement : updateElements) {
            mergeElement(elements, vuiElement, indexs, isAttribute);
        }
        if (updateElements.size() != indexs.size() && updateElements.size() > indexs.size()) {
            for (VuiElement vuiElement2 : updateElements) {
                if (vuiElement2.getId() != null && !indexs.contains(vuiElement2.getId())) {
                    elements.add(vuiElement2);
                }
            }
        }
        return elements;
    }

    public static List<VuiElement> removeElementById(List<VuiElement> elements, List<String> ids) {
        if (elements == null || elements.isEmpty()) {
            return elements;
        }
        if (ids == null || ids.isEmpty()) {
            return elements;
        }
        for (String id : ids) {
            removeElement(elements, id);
        }
        return elements;
    }

    public static String removeElementById(String sceneStr, List<String> ids) {
        if (TextUtils.isEmpty(sceneStr)) {
            return sceneStr;
        }
        if (ids == null || ids.isEmpty()) {
            return sceneStr;
        }
        try {
            JSONObject oldScene = new JSONObject(sceneStr);
            if (!oldScene.has(VuiConstants.SCENE_ELEMENTS)) {
                return sceneStr;
            }
            JSONArray els = oldScene.getJSONArray(VuiConstants.SCENE_ELEMENTS);
            for (String id : ids) {
                removeElement(els, id);
            }
            return String.valueOf(els);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void removeElement(List<VuiElement> elements, String id) {
        if (TextUtils.isEmpty(id) || elements == null) {
            return;
        }
        for (int i = 0; i < elements.size(); i++) {
            VuiElement elt = elements.get(i);
            if (elt != null) {
                if (id.equals(elt.getId())) {
                    elements.remove(i);
                    int i2 = i - 1;
                    return;
                } else if (elt.getElements() != null && !elt.getElements().isEmpty()) {
                    removeElement(elt.getElements(), id);
                }
            }
        }
    }

    private static void mergeElement(List<VuiElement> elements, VuiElement element, List<String> indexs, boolean isAttribute) {
        if (element == null || elements == null || element.getId() == null) {
            return;
        }
        for (VuiElement elt : elements) {
            if (element.getId().equals(elt.getId())) {
                int index = elements.indexOf(elt);
                if (isAttribute && element.getElements() == null && elt.getElements() != null) {
                    element.setElements(elt.getElements());
                }
                elements.set(index, element);
                indexs.add(element.getId());
                return;
            } else if (elt.getElements() != null && !elt.getElements().isEmpty()) {
                mergeElement(elt.getElements(), element, indexs, isAttribute);
            }
        }
    }

    private static void removeElement(JSONArray elements, String id) throws Exception {
        if (id == null || elements == null) {
            return;
        }
        for (int i = 0; i < elements.length(); i++) {
            JSONObject elt = elements.getJSONObject(i);
            if (elt != null && elt.has("id")) {
                if (!TextUtils.isEmpty(elt.optString("id")) && elt.optString("id").equals(id)) {
                    elements.remove(i);
                    return;
                } else if (elt.has(VuiConstants.SCENE_ELEMENTS) && elt.optJSONArray(VuiConstants.SCENE_ELEMENTS).length() > 0) {
                    removeElement(elt.optJSONArray(VuiConstants.SCENE_ELEMENTS), id);
                }
            }
        }
    }
}
