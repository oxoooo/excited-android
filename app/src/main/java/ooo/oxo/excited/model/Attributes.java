package ooo.oxo.excited.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zsj on 2016/10/18.
 */

public class Attributes implements Parcelable{

    public Result result;
    public int remains;
    public int sum;
    public Created created;
    public String token;
    public String name;
    public String description;
    public String icon;
    public boolean followed;

    @SerializedName("refined_url")
    public String refinedUrl;

    public static class Created {
        public long timestamp;
        public String distance;
    }

    protected Attributes(Parcel in) {
        result = in.readParcelable(Result.class.getClassLoader());
        remains = in.readInt();
        sum = in.readInt();
        token = in.readString();
        name = in.readString();
        description = in.readString();
        icon = in.readString();
        followed = in.readByte() != 0;
        refinedUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);
        dest.writeInt(remains);
        dest.writeInt(sum);
        dest.writeString(token);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(icon);
        dest.writeByte((byte) (followed ? 1 : 0));
        dest.writeString(refinedUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Attributes> CREATOR = new Creator<Attributes>() {
        @Override
        public Attributes createFromParcel(Parcel in) {
            return new Attributes(in);
        }

        @Override
        public Attributes[] newArray(int size) {
            return new Attributes[size];
        }
    };
}
