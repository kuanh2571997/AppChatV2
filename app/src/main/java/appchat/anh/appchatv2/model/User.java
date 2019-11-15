package appchat.anh.appchatv2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String fullName;
    private String id;
    private String profilePic;
    private String status;
  
    public User() {
    }

    public User(String fullName, String id, String profilePic, String status) {
        this.fullName = fullName;
        this.id = id;
        this.profilePic = profilePic;
        this.status = status;
    }

    protected User(Parcel in) {
        fullName = in.readString();
        id = in.readString();
        profilePic = in.readString();
        status = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(id);
        dest.writeString(profilePic);
        dest.writeString(status);
    }
}
