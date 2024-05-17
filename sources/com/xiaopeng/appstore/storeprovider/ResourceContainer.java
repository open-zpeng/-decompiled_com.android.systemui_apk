package com.xiaopeng.appstore.storeprovider;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes22.dex */
public class ResourceContainer implements Parcelable {
    public static final Parcelable.Creator<ResourceContainer> CREATOR = new Parcelable.Creator<ResourceContainer>() { // from class: com.xiaopeng.appstore.storeprovider.ResourceContainer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceContainer createFromParcel(Parcel in) {
            return new ResourceContainer(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceContainer[] newArray(int size) {
            return new ResourceContainer[size];
        }
    };
    private int currentPage;
    private List<Resource> mResourceList;
    private int pageCount;
    private int totalCount;
    private int totalPage;

    public ResourceContainer() {
        this.mResourceList = new ArrayList();
    }

    public String toString() {
        return "ResContainer{totalCount=" + this.totalCount + ", pageCount=" + this.pageCount + ", totalPage=" + this.totalPage + ", currentPage=" + this.currentPage + ", resList=" + this.mResourceList + '}';
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<Resource> getResourceList() {
        return this.mResourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.mResourceList = resourceList;
    }

    protected ResourceContainer(Parcel in) {
        this.mResourceList = new ArrayList();
        this.totalCount = in.readInt();
        this.pageCount = in.readInt();
        this.totalPage = in.readInt();
        this.currentPage = in.readInt();
        this.mResourceList = in.createTypedArrayList(Resource.CREATOR);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalCount);
        dest.writeInt(this.pageCount);
        dest.writeInt(this.totalPage);
        dest.writeInt(this.currentPage);
        dest.writeTypedList(this.mResourceList);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
