package lt.kikutis.libreexplorer;

import android.os.Parcel;
import android.os.Parcelable;

public class PathVisit implements Parcelable {

    public static final Parcelable.Creator<PathVisit> CREATOR = new Creator<PathVisit>() {
        @Override
        public PathVisit createFromParcel(Parcel source) {
            return new PathVisit(source);
        }

        @Override
        public PathVisit[] newArray(int size) {
            return new PathVisit[size];
        }
    };

    private String mPath;
    private int mPosition;

    public PathVisit(String path) {
        mPath = path;
    }

    private PathVisit(Parcel source) {
        mPath = source.readString();
        mPosition = source.readInt();
    }

    public String getPath() {
        return mPath;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeInt(mPosition);
    }
}
