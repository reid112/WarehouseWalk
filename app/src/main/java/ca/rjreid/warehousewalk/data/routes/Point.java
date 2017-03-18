package ca.rjreid.warehousewalk.data.routes;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point implements Parcelable {
    @Expose @SerializedName("id") private int id;
    @Expose @SerializedName("name") private String name;
    @Expose @SerializedName("number") private int number;
    @Expose @SerializedName("lat") private double lat;
    @Expose @SerializedName("lng") private double lng;
    @Expose @SerializedName("tour_id") private int tourId;
    @Expose @SerializedName("icon") private String icon;
    @Expose @SerializedName("image") private String image;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getTourId() {
        return tourId;
    }

    public String getIcon() {
        return icon;
    }

    public String getImage() {
        return image;
    }

    public Point() {
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.number);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.tourId);
        dest.writeString(this.icon);
        dest.writeString(this.image);
    }

    protected Point(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.number = in.readInt();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.tourId = in.readInt();
        this.icon = in.readString();
        this.image = in.readString();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override public Point createFromParcel(Parcel source) {
            return new Point(source);
        }

        @Override public Point[] newArray(int size) {
            return new Point[size];
        }
    };
}
