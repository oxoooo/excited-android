package ooo.oxo.excited.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zsj on 2016/10/18.
 */

public class Result implements Parcelable{

    public String title;
    public String type;
    public String url;
    public Author author;
    public Header header;
    public String cover;
    public String description;
    public String ratio;
    public String uuid;

    public static class Header {
        public Icon icon;
        public String title;

        public static class Icon {
            public String x2;
            public String x3;
        }
    }

    protected Result(Parcel in) {
        title = in.readString();
        type = in.readString();
        url = in.readString();
        cover = in.readString();
        description = in.readString();
        ratio = in.readString();
        uuid = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(type);
        parcel.writeString(url);
        parcel.writeString(cover);
        parcel.writeString(description);
        parcel.writeString(ratio);
        parcel.writeString(uuid);
    }

    public static class Author {
        public String name;
        public Object icon;
    }
}
