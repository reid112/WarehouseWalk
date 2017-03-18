package ca.rjreid.warehousewalk.data.routes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Route implements Parcelable {
    @Expose @SerializedName("id") private int id;
    @Expose @SerializedName("name") private String name;
    @Expose @SerializedName("icon") private String icon;
    @Expose @SerializedName("up_votes") private String upVotes;
    @Expose @SerializedName("down_votes") private String downVotes;
    @Expose @SerializedName("start_point") private Point point;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getUpVotes() {
        return upVotes;
    }

    public String getDownVotes() {
        return downVotes;
    }

    public Point getPoint() {
        return point;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.upVotes);
        dest.writeString(this.downVotes);
        dest.writeParcelable(this.point, flags);
    }

    protected Route(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.icon = in.readString();
        this.upVotes = in.readString();
        this.downVotes = in.readString();
        this.point = in.readParcelable(Point.class.getClassLoader());
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        @Override public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
