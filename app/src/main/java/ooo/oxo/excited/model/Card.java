package ooo.oxo.excited.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by seasonyuu on 2016/12/8.
 */

public class Card implements Parcelable {

    public String id;
    public String title;
    public String cover;
    public String ratio;
    @SerializedName("head_icon")
    public String headIcon;
    @SerializedName("head_name")
    public String headName;
    public String source;
    public int remains;
    public int sum;
    public int timestamp;
    public boolean refined;
    public String type;
    public String description;
    public String distance;
    public String uuid;
    public String link;
    @SerializedName("author_name")
    public String authorName;

    public Card() {
    }

    protected Card(Parcel in) {
        id = in.readString();
        title = in.readString();
        cover = in.readString();
        ratio = in.readString();
        headIcon = in.readString();
        headName = in.readString();
        source = in.readString();
        remains = in.readInt();
        sum = in.readInt();
        timestamp = in.readInt();
        type = in.readString();
        description = in.readString();
        distance = in.readString();
        uuid = in.readString();
        link = in.readString();
        authorName = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(cover);
        parcel.writeString(ratio);
        parcel.writeString(headIcon);
        parcel.writeString(headName);
        parcel.writeString(source);
        parcel.writeInt(remains);
        parcel.writeInt(sum);
        parcel.writeInt(timestamp);
        parcel.writeString(type);
        parcel.writeString(description);
        parcel.writeString(distance);
        parcel.writeString(uuid);
        parcel.writeString(link);
        parcel.writeString(authorName);
    }
}
