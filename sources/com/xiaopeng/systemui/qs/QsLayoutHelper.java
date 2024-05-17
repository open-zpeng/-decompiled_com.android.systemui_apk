package com.xiaopeng.systemui.qs;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class QsLayoutHelper {
    private static final int MAX_LEN_IN_ONE_BLOCK = 4;
    private final Context mContext;
    private final QsDynamicLayout mQsLayout;
    private final VehicleDataLoader dataLoader = new VehicleDataLoader();
    private ArrayList<TileState> mTileStateList = new ArrayList<>();

    public QsLayoutHelper(Context context) {
        this.mContext = context;
        this.mQsLayout = new QsDynamicLayout(this.mContext);
    }

    public QsDynamicLayout getQsLayout(int screenId) {
        this.mTileStateList = this.dataLoader.getFilteredTileStates(screenId);
        ArrangeTileStates();
        this.mQsLayout.initQsLayout(this.mTileStateList);
        return this.mQsLayout;
    }

    private void ArrangeTileStates() {
        HashMap<Integer, ArrayList<TileState>> groupMap = new HashMap<>();
        HashMap<Integer, Integer> groupAreaMap = new HashMap<>();
        int groupId = 1;
        int groupIdMax = 1;
        int groupArea = 0;
        int sumArea = 0;
        ArrayList<TileState> tileStateArrayList = new ArrayList<>();
        tileStateListSort();
        Iterator<TileState> it = this.mTileStateList.iterator();
        while (it.hasNext()) {
            TileState tileState = it.next();
            if (tileState.groupId != groupId) {
                if (tileStateArrayList.size() != 0) {
                    groupMap.put(Integer.valueOf(groupId), tileStateArrayList);
                    groupAreaMap.put(Integer.valueOf(groupId), Integer.valueOf(groupArea));
                }
                groupId = tileState.groupId;
                if (groupId > groupIdMax) {
                    groupIdMax = groupId;
                }
                tileStateArrayList = new ArrayList<>();
                groupArea = 0;
            }
            tileStateArrayList.add(tileState);
            groupArea += tileState.width * tileState.height;
            sumArea += tileState.width * tileState.height;
        }
        groupMap.put(Integer.valueOf(groupId), tileStateArrayList);
        groupAreaMap.put(Integer.valueOf(groupId), Integer.valueOf(groupArea));
        int lenGroup = sumArea > 12 ? 4 : 3;
        if (groupAreaMap.get(1) != null && groupAreaMap.get(1).intValue() < 4 && groupAreaMap.containsKey(Integer.valueOf(groupIdMax)) && groupAreaMap.get(Integer.valueOf(groupIdMax)).intValue() > 4 - groupAreaMap.get(1).intValue()) {
            Iterator<TileState> it2 = groupMap.get(Integer.valueOf(groupIdMax)).iterator();
            while (it2.hasNext()) {
                TileState tileState2 = it2.next();
                groupMap.get(1).add(tileState2);
                groupMap.get(Integer.valueOf(groupIdMax)).remove(tileState2);
                groupAreaMap.put(1, Integer.valueOf(groupAreaMap.get(1).intValue() + tileState2.width));
                if (groupAreaMap.get(1).intValue() >= 4) {
                    break;
                }
            }
        }
        int i = 2;
        if (groupAreaMap.get(2) == null && groupAreaMap.get(5) != null) {
            groupAreaMap.put(2, 1);
            ArrayList<TileState> arrayList = new ArrayList<>();
            arrayList.add(groupMap.get(5).get(0));
            groupMap.put(2, arrayList);
            groupMap.get(5).remove(0);
        }
        if (groupAreaMap.get(2) != null && groupAreaMap.get(2).intValue() < 4 && groupIdMax > 2) {
            if (lenGroup == 3 && groupIdMax != 2) {
                groupMap.get(Integer.valueOf(groupIdMax)).addAll(groupMap.get(2));
                groupMap.remove(2);
            } else if (groupAreaMap.containsKey(Integer.valueOf(groupIdMax))) {
                if (groupAreaMap.get(Integer.valueOf(groupIdMax)).intValue() > 4 - groupAreaMap.get(2).intValue()) {
                    ArrayList<TileState> arrayList2 = new ArrayList<>();
                    arrayList2.addAll(groupMap.get(Integer.valueOf(groupIdMax)));
                    Iterator<TileState> it3 = arrayList2.iterator();
                    while (it3.hasNext()) {
                        TileState tileState3 = it3.next();
                        groupMap.get(Integer.valueOf(i)).add(tileState3);
                        groupMap.get(Integer.valueOf(groupIdMax)).remove(tileState3);
                        groupAreaMap.put(Integer.valueOf(i), Integer.valueOf(groupAreaMap.get(Integer.valueOf(i)).intValue() + tileState3.width));
                        if (groupAreaMap.get(2).intValue() >= 4) {
                            break;
                        }
                        i = 2;
                    }
                } else {
                    int intValue = groupAreaMap.get(2).intValue();
                    if (intValue != 1) {
                        if (intValue == 2) {
                            Iterator<TileState> it4 = groupMap.get(4).iterator();
                            while (it4.hasNext()) {
                                groupMap.get(2).add(it4.next());
                            }
                            groupMap.put(4, null);
                        } else if (intValue == 3) {
                            groupMap.get(Integer.valueOf(groupIdMax)).add(groupMap.get(2).get(2));
                            groupMap.get(2).remove(2);
                            Iterator<TileState> it5 = groupMap.get(4).iterator();
                            while (it5.hasNext()) {
                                groupMap.get(2).add(it5.next());
                            }
                            groupMap.put(4, null);
                        }
                    } else {
                        if (groupMap.get(Integer.valueOf(groupIdMax)).size() > 0) {
                            groupMap.get(2).add(groupMap.get(Integer.valueOf(groupIdMax)).get(0));
                            groupMap.get(Integer.valueOf(groupIdMax)).remove(0);
                        }
                        Iterator<TileState> it6 = groupMap.get(4).iterator();
                        while (it6.hasNext()) {
                            groupMap.get(2).add(it6.next());
                        }
                        groupMap.put(4, null);
                    }
                }
            }
        }
        this.mTileStateList.clear();
        int flag = 0;
        for (int i2 = 1; i2 < 4; i2++) {
            if (!groupMap.containsKey(Integer.valueOf(i2))) {
                flag = -1;
            } else {
                int idx = 0;
                int idy = 0;
                Iterator<TileState> it7 = groupMap.get(Integer.valueOf(i2)).iterator();
                while (it7.hasNext()) {
                    TileState tileState4 = it7.next();
                    tileState4.x = idx + (((i2 - 1) + flag) * 2);
                    tileState4.y = idy;
                    this.mTileStateList.add(tileState4);
                    idy += (tileState4.width + idx) / 2;
                    idx = (tileState4.width + idx) % 2;
                }
            }
        }
        int idx2 = 0;
        for (int i3 = 4; i3 < 6; i3++) {
            if (groupMap.get(Integer.valueOf(i3)) != null) {
                Iterator<TileState> it8 = groupMap.get(Integer.valueOf(i3)).iterator();
                while (it8.hasNext()) {
                    TileState tileState5 = it8.next();
                    tileState5.x = idx2;
                    tileState5.y = 2;
                    this.mTileStateList.add(tileState5);
                    idx2 += tileState5.width;
                }
            }
        }
    }

    private void tileStateListSort() {
        boolean swap = false;
        int length = this.mTileStateList.size();
        for (int i = length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (this.mTileStateList.get(j).groupId > this.mTileStateList.get(j + 1).groupId) {
                    TileState tmp = this.mTileStateList.get(j);
                    ArrayList<TileState> arrayList = this.mTileStateList;
                    arrayList.set(i, arrayList.get(j + 1));
                    this.mTileStateList.set(j + 1, tmp);
                    swap = true;
                }
            }
            if (!swap) {
                return;
            }
        }
    }
}
