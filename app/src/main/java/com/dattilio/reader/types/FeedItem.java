package com.dattilio.reader.types;


import android.os.Parcel;
import android.os.Parcelable;

public class FeedItem implements Parcelable {
    public final String href;
    public final String src;
    public final String desc;
    public final String attrib;
    public final User user;

    public FeedItem(String href, String src, String desc, String attrib, User user) {
        this.href = href;
        this.src = src;
        this.desc = desc;
        this.attrib = attrib;
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.href);
        dest.writeString(this.src);
        dest.writeString(this.desc);
        dest.writeString(this.attrib);
        dest.writeParcelable(this.user, flags);
    }

    private FeedItem(Parcel in) {
        this.href = in.readString();
        this.src = in.readString();
        this.desc = in.readString();
        this.attrib = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}
