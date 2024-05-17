package com.xiaopeng.appstore.storeprovider.store.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes22.dex */
public class ResourceContainerBean implements Parcelable {
    public static final Parcelable.Creator<ResourceContainerBean> CREATOR = new Parcelable.Creator<ResourceContainerBean>() { // from class: com.xiaopeng.appstore.storeprovider.store.bean.ResourceContainerBean.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceContainerBean createFromParcel(Parcel in) {
            return new ResourceContainerBean(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResourceContainerBean[] newArray(int size) {
            return new ResourceContainerBean[size];
        }
    };
    private int currentPage;
    private List<ResourceBean> mResourceBeanList;
    private int pageCount;
    private int totalCount;
    private int totalPage;

    public ResourceContainerBean() {
        this.mResourceBeanList = new ArrayList();
    }

    protected ResourceContainerBean(Parcel in) {
        this.mResourceBeanList = new ArrayList();
        this.totalCount = in.readInt();
        this.pageCount = in.readInt();
        this.totalPage = in.readInt();
        this.currentPage = in.readInt();
        this.mResourceBeanList = in.createTypedArrayList(ResourceBean.CREATOR);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalCount);
        dest.writeInt(this.pageCount);
        dest.writeInt(this.totalPage);
        dest.writeInt(this.currentPage);
        dest.writeTypedList(this.mResourceBeanList);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    private int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    private int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<ResourceBean> getResourceBeanList() {
        return this.mResourceBeanList;
    }

    public void setResourceBeanList(List<ResourceBean> resourceBeanList) {
        this.mResourceBeanList = resourceBeanList;
    }

    public String toString() {
        return "ResourceContainerBean{totalCount=" + this.totalCount + ", pageCount=" + this.pageCount + ", totalPage=" + this.totalPage + ", currentPage=" + this.currentPage + ", mResourceBeanList=" + this.mResourceBeanList + '}';
    }
}
