package com.xiaopeng.speech.actor;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaopeng.speech.actorapi.AvatarActor;
import com.xiaopeng.speech.actorapi.DataActor;
import com.xiaopeng.speech.actorapi.DialogActor;
import com.xiaopeng.speech.actorapi.ResultActor;
import com.xiaopeng.speech.actorapi.ShowActor;
import com.xiaopeng.speech.actorapi.SupportActor;
import com.xiaopeng.speech.actorapi.TextActor;
import com.xiaopeng.speech.actorapi.ValueActor;
import com.xiaopeng.speech.common.util.LogUtils;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes23.dex */
public class Actor implements Parcelable {
    protected String mName;
    public static Map<String, Class<? extends Actor>> sActorMap = new HashMap<String, Class<? extends Actor>>() { // from class: com.xiaopeng.speech.actor.Actor.1
        {
            put(AvatarActor.NAME, AvatarActor.class);
            put(DialogActor.NAME, DialogActor.class);
            put(ResultActor.NAME, ResultActor.class);
            put(TextActor.NAME, TextActor.class);
            put(ShowActor.NAME, ShowActor.class);
            put(SupportActor.NAME, SupportActor.class);
            put(DataActor.NAME, DataActor.class);
            put(ValueActor.NAME, ValueActor.class);
        }
    };
    public static final Parcelable.Creator<Actor> CREATOR = new Parcelable.Creator<Actor>() { // from class: com.xiaopeng.speech.actor.Actor.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Actor createFromParcel(Parcel source) {
            String actorName = source.readString();
            Class<? extends Actor> actorClass = Actor.sActorMap.get(actorName);
            try {
                Constructor<? extends Actor> ctor = actorClass.getConstructor(String.class, Parcel.class);
                return ctor.newInstance(actorName, source);
            } catch (Exception e) {
                LogUtils.e(this, "createFromParcel error:", e);
                return new Actor(actorName, source);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Actor[] newArray(int size) {
            return new Actor[size];
        }
    };

    public Actor() {
    }

    public Actor(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Actor(String name, Parcel in) {
        this.mName = name;
    }
}
