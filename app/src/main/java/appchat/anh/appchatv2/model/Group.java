package appchat.anh.appchatv2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {
    private long createAt;
    private String groupIcon;
    private String id;
    private String name;

    public Group() {
    }

    public Group(long createAt, String groupIcon, String id, String name) {
        this.createAt = createAt;
        this.groupIcon = groupIcon;
        this.id = id;
        this.name = name;
    }

    protected Group(Parcel in) {
        createAt = in.readLong();
        groupIcon = in.readString();
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createAt);
        dest.writeString(groupIcon);
        dest.writeString(id);
        dest.writeString(name);
    }
}
