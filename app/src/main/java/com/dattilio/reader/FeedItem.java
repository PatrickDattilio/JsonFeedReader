package com.dattilio.reader;


import android.os.Parcel;
import android.os.Parcelable;

public class FeedItem implements Parcelable {
    final String href;
    final String src;
    final String desc;
    private final String board;
    final String attrib;
    final User user;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.href);
        dest.writeString(this.src);
        dest.writeString(this.desc);
        dest.writeString(this.board);
        dest.writeString(this.attrib);
        dest.writeParcelable(this.user, flags);
    }

    private FeedItem(Parcel in) {
        this.href = in.readString();
        this.src = in.readString();
        this.desc = in.readString();
        this.board = in.readString();
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
