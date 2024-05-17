package com.android.systemui.statusbar.phone;

import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class StatusBarIconList {
    private ArrayList<Slot> mSlots = new ArrayList<>();

    public StatusBarIconList(String[] slots) {
        for (String str : slots) {
            this.mSlots.add(new Slot(str, null));
        }
    }

    public int getSlotIndex(String slot) {
        int N = this.mSlots.size();
        for (int i = 0; i < N; i++) {
            Slot item = this.mSlots.get(i);
            if (item.getName().equals(slot)) {
                return i;
            }
        }
        this.mSlots.add(0, new Slot(slot, null));
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArrayList<Slot> getSlots() {
        return new ArrayList<>(this.mSlots);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Slot getSlot(String name) {
        return this.mSlots.get(getSlotIndex(name));
    }

    public int size() {
        return this.mSlots.size();
    }

    public void setIcon(int index, StatusBarIconHolder holder) {
        this.mSlots.get(index).addHolder(holder);
    }

    public void removeIcon(int index, int tag) {
        this.mSlots.get(index).removeForTag(tag);
    }

    public String getSlotName(int index) {
        return this.mSlots.get(index).getName();
    }

    public StatusBarIconHolder getIcon(int index, int tag) {
        return this.mSlots.get(index).getHolderForTag(tag);
    }

    public int getViewIndex(int slotIndex, int tag) {
        int count = 0;
        for (int i = 0; i < slotIndex; i++) {
            Slot item = this.mSlots.get(i);
            if (item.hasIconsInSlot()) {
                count += item.numberOfIcons();
            }
        }
        Slot viewItem = this.mSlots.get(slotIndex);
        return viewItem.viewIndexOffsetForTag(tag) + count;
    }

    public void dump(PrintWriter pw) {
        pw.println("StatusBarIconList state:");
        int N = this.mSlots.size();
        pw.println("  icon slots: " + N);
        for (int i = 0; i < N; i++) {
            pw.printf("    %2d:%s\n", Integer.valueOf(i), this.mSlots.get(i).toString());
        }
    }

    /* loaded from: classes21.dex */
    public static class Slot {
        private StatusBarIconHolder mHolder;
        private final String mName;
        private ArrayList<StatusBarIconHolder> mSubSlots;

        public Slot(String name, StatusBarIconHolder iconHolder) {
            this.mName = name;
            this.mHolder = iconHolder;
        }

        public String getName() {
            return this.mName;
        }

        public StatusBarIconHolder getHolderForTag(int tag) {
            if (tag == 0) {
                return this.mHolder;
            }
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList != null) {
                Iterator<StatusBarIconHolder> it = arrayList.iterator();
                while (it.hasNext()) {
                    StatusBarIconHolder holder = it.next();
                    if (holder.getTag() == tag) {
                        return holder;
                    }
                }
                return null;
            }
            return null;
        }

        public void addHolder(StatusBarIconHolder holder) {
            int tag = holder.getTag();
            if (tag == 0) {
                this.mHolder = holder;
            } else {
                setSubSlot(holder, tag);
            }
        }

        public void removeForTag(int tag) {
            if (tag == 0) {
                this.mHolder = null;
                return;
            }
            int index = getIndexForTag(tag);
            if (index != -1) {
                this.mSubSlots.remove(index);
            }
        }

        @VisibleForTesting
        public void clear() {
            this.mHolder = null;
            if (this.mSubSlots != null) {
                this.mSubSlots = null;
            }
        }

        private void setSubSlot(StatusBarIconHolder holder, int tag) {
            if (this.mSubSlots == null) {
                this.mSubSlots = new ArrayList<>();
                this.mSubSlots.add(holder);
            } else if (getIndexForTag(tag) != -1) {
            } else {
                this.mSubSlots.add(holder);
            }
        }

        private int getIndexForTag(int tag) {
            for (int i = 0; i < this.mSubSlots.size(); i++) {
                StatusBarIconHolder h = this.mSubSlots.get(i);
                if (h.getTag() == tag) {
                    return i;
                }
            }
            return -1;
        }

        public boolean hasIconsInSlot() {
            if (this.mHolder != null) {
                return true;
            }
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            return arrayList != null && arrayList.size() > 0;
        }

        public int numberOfIcons() {
            int num = this.mHolder == null ? 0 : 1;
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            return arrayList == null ? num : arrayList.size() + num;
        }

        public int viewIndexOffsetForTag(int tag) {
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList == null) {
                return 0;
            }
            int subSlots = arrayList.size();
            if (tag == 0) {
                return subSlots;
            }
            return (subSlots - getIndexForTag(tag)) - 1;
        }

        public List<StatusBarIconHolder> getHolderListInViewOrder() {
            ArrayList<StatusBarIconHolder> holders = new ArrayList<>();
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList != null) {
                for (int i = arrayList.size() - 1; i >= 0; i--) {
                    holders.add(this.mSubSlots.get(i));
                }
            }
            StatusBarIconHolder statusBarIconHolder = this.mHolder;
            if (statusBarIconHolder != null) {
                holders.add(statusBarIconHolder);
            }
            return holders;
        }

        public List<StatusBarIconHolder> getHolderList() {
            ArrayList<StatusBarIconHolder> holders = new ArrayList<>();
            StatusBarIconHolder statusBarIconHolder = this.mHolder;
            if (statusBarIconHolder != null) {
                holders.add(statusBarIconHolder);
            }
            Collection<? extends StatusBarIconHolder> collection = this.mSubSlots;
            if (collection != null) {
                holders.addAll(collection);
            }
            return holders;
        }

        public String toString() {
            return String.format("(%s) %s", this.mName, subSlotsString());
        }

        private String subSlotsString() {
            if (this.mSubSlots == null) {
                return "";
            }
            return "" + this.mSubSlots.size() + " subSlots";
        }
    }
}
