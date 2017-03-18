package ca.rjreid.warehousewalk.data.routes;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteDetails implements Parcelable {
    @Expose @SerializedName("id") private int id;
    @Expose @SerializedName("name") private String name;
    @Expose @SerializedName("icon") private String icon;
    @Expose @SerializedName("up_votes") private int upVotes;
    @Expose @SerializedName("down_votes") private int downVotes;
    @Expose @SerializedName("points") private List<Point> points;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public List<Point> getPoints() {
        return points;
    }


    public RouteDetails() {
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeInt(this.upVotes);
        dest.writeInt(this.downVotes);
        dest.writeTypedList(this.points);
    }

    protected RouteDetails(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.icon = in.readString();
        this.upVotes = in.readInt();
        this.downVotes = in.readInt();
        this.points = in.createTypedArrayList(Point.CREATOR);
    }

    public static final Creator<RouteDetails> CREATOR = new Creator<RouteDetails>() {
        @Override public RouteDetails createFromParcel(Parcel source) {
            return new RouteDetails(source);
        }

        @Override public RouteDetails[] newArray(int size) {
            return new RouteDetails[size];
        }
    };
}