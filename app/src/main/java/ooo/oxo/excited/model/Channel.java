package ooo.oxo.excited.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seasonyuu on 2016/12/8.
 */

public class Channel implements Parcelable {
    public String id;
    public  String name;
    public String icon;
    public boolean followed;
    public String description;
    public String createdAt;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeByte(this.followed ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeString(this.createdAt);
    }

    public Channel() {
    }

    protected Channel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
        this.followed = in.readByte() != 0;
        this.description = in.readString();
        this.createdAt = in.readString();
    }

    public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
