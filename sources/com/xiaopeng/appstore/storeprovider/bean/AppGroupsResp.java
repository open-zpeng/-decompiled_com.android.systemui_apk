package com.xiaopeng.appstore.storeprovider.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;
/* loaded from: classes22.dex */
public class AppGroupsResp implements Parcelable {
    public static final Parcelable.Creator<AppGroupsResp> CREATOR = new Parcelable.Creator<AppGroupsResp>() { // from class: com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppGroupsResp createFromParcel(Parcel in) {
            return new AppGroupsResp(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppGroupsResp[] newArray(int size) {
            return new AppGroupsResp[size];
        }
    };
    public long code;
    public List<AppGroup> data;
    public String msg;

    protected AppGroupsResp(Parcel in) {
        this.code = in.readLong();
        this.msg = in.readString();
        this.data = in.createTypedArrayList(AppGroup.CREATOR);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.code);
        dest.writeString(this.msg);
        dest.writeTypedList(this.data);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "AppGroupsResp{code=" + this.code + ", msg='" + this.msg + "', data=" + this.data + '}';
    }

    /* loaded from: classes22.dex */
    public static class AppGroup implements Parcelable {
        public static final Parcelable.Creator<AppGroup> CREATOR = new Parcelable.Creator<AppGroup>() { // from class: com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp.AppGroup.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AppGroup createFromParcel(Parcel in) {
                return new AppGroup(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AppGroup[] newArray(int size) {
                return new AppGroup[size];
            }
        };
        public List<App> apps;

        protected AppGroup(Parcel in) {
            this.apps = in.createTypedArrayList(App.CREATOR);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.apps);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "AppGroup{apps=" + this.apps + '}';
        }
    }

    /* loaded from: classes22.dex */
    public static class App implements Parcelable {
        public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() { // from class: com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp.App.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public App createFromParcel(Parcel in) {
                return new App(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public App[] newArray(int size) {
                return new App[size];
            }
        };
        public AppIcons app_icons;
        public String app_id;
        public String app_name;
        public String brief_desc;
        public String config_md5;
        public String config_url;
        public String download_url;
        public String md5;
        public String package_name;
        public String photo_url;
        public String size;
        public long update_time;
        public long version_code;
        public String version_name;

        protected App(Parcel in) {
            this.size = in.readString();
            this.md5 = in.readString();
            this.app_id = in.readString();
            this.app_name = in.readString();
            this.package_name = in.readString();
            this.brief_desc = in.readString();
            this.app_icons = (AppIcons) in.readParcelable(AppIcons.class.getClassLoader());
            this.version_name = in.readString();
            this.version_code = in.readLong();
            this.download_url = in.readString();
            this.update_time = in.readLong();
            this.config_url = in.readString();
            this.config_md5 = in.readString();
            this.photo_url = in.readString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.size);
            dest.writeString(this.md5);
            dest.writeString(this.app_id);
            dest.writeString(this.app_name);
            dest.writeString(this.package_name);
            dest.writeString(this.brief_desc);
            dest.writeParcelable(this.app_icons, flags);
            dest.writeString(this.version_name);
            dest.writeLong(this.version_code);
            dest.writeString(this.download_url);
            dest.writeLong(this.update_time);
            dest.writeString(this.config_url);
            dest.writeString(this.config_md5);
            dest.writeString(this.photo_url);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "App{app_name='" + this.app_name + "', package_name='" + this.package_name + "', version_code=" + this.version_code + '}';
        }
    }

    /* loaded from: classes22.dex */
    public static class AppIcons implements Parcelable {
        public static final Parcelable.Creator<AppIcons> CREATOR = new Parcelable.Creator<AppIcons>() { // from class: com.xiaopeng.appstore.storeprovider.bean.AppGroupsResp.AppIcons.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AppIcons createFromParcel(Parcel in) {
                return new AppIcons(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AppIcons[] newArray(int size) {
                return new AppIcons[size];
            }
        };
        public String large_icon;
        public String small_icon;

        protected AppIcons(Parcel in) {
            this.small_icon = in.readString();
            this.large_icon = in.readString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.small_icon);
            dest.writeString(this.large_icon);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }
    }
}
