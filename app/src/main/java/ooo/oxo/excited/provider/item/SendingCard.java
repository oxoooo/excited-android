package ooo.oxo.excited.provider.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by seasonyuu on 2016/12/20.
 */

public class SendingCard implements Parcelable {
    public static final int MUSIC = 0x2;
    public static final int IMAGE = 0x1;
    public static final int WEB = 0x0;
    /**
     * 0表示分享链接，1表示分享图片
     */
    public int type;
    public String title; // null if type = 1
    public String url;
    public String description;
    public String channelId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.description);
        dest.writeString(this.channelId);
    }

    public SendingCard() {
    }

    protected SendingCard(Parcel in) {
        this.type = in.readInt();
        this.title = in.readString();
        this.url = in.readString();
        this.description = in.readString();
        this.channelId = in.readString();
    }

    public static final Parcelable.Creator<SendingCard> CREATOR = new Parcelable.Creator<SendingCard>() {
        @Override
        public SendingCard createFromParcel(Parcel source) {
            return new SendingCard(source);
        }

        @Override
        public SendingCard[] newArray(int size) {
            return new SendingCard[size];
        }
    };
}
