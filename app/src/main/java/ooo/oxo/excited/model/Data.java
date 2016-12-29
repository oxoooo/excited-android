package ooo.oxo.excited.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zsj on 2016/10/18.
 */

public class Data implements Parcelable {

    public String type;
    public String id;
    public Attributes attributes;

    protected Data(Parcel in) {
        type = in.readString();
        id = in.readString();
        attributes = in.readParcelable(Attributes.class.getClassLoader());
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(id);
        parcel.writeParcelable(attributes, i);
    }

}
