package com.dattilio.reader.types;

import android.os.Parcel;
import android.os.Parcelable;

public class Avatar implements Parcelable {
    public String src;
    public int width;
    public int height;

    public Avatar(String src, int width, int height) {
        this.src = src;
        this.width = width;
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.src);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    private Avatar(Parcel in) {
        this.src = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static Parcelable.Creator<Avatar> CREATOR = new Parcelable.Creator<Avatar>() {
        public Avatar createFromParcel(Parcel source) {
            return new Avatar(source);
        }

        public Avatar[] newArray(int size) {
            return new Avatar[size];
        }
    };
}
