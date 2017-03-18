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
    @Expose @SerializedName("up_votes") private String upVotes;
    @Expose @SerializedName("down_votes") private String downVotes;
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

    public String getUpVotes() {
        return upVotes;
    }

    public String getDownVotes() {
        return downVotes;
    }

    public List<Point> getPoints() {
        return points;
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
        dest.writeTypedList(this.points);
    }

    public RouteDetails() {
    }

    protected RouteDetails(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.icon = in.readString();
        this.upVotes = in.readString();
        this.downVotes = in.readString();
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