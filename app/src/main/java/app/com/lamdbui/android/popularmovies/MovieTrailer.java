package app.com.lamdbui.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lamdbui on 1/8/17.
 */

public class MovieTrailer implements Parcelable {
//    {
//        "id": 12,
//            "results": [
//        {
//            "id": "533ec651c3a3685448000010",
//                "iso_639_1": "en",
//                "iso_3166_1": "US",
//                "key": "SPHfeNgogVs",
//                "name": "Trailer 1",
//                "site": "YouTube",
//                "size": 720,
//                "type": "Trailer"
//        }
//        ]
//    }

    private String mId;
    private String mIso_639_1;
    private String mIso_3166_1;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;
    private String mType;

    private MovieTrailer(Parcel in) {
        mId = in.readString();
        mIso_639_1 = in.readString();
        mIso_3166_1 = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
        mType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mIso_639_1);
        dest.writeString(mIso_3166_1);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeInt(mSize);
        dest.writeString(mType);
    }

    public MovieTrailer() {
    }

    public static final Parcelable.Creator<MovieTrailer> CREATOR
            = new Parcelable.Creator<MovieTrailer>() {

        @Override
        public MovieTrailer createFromParcel(Parcel source) {
            return new MovieTrailer(source);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIso_639_1() {
        return mIso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        mIso_639_1 = iso_639_1;
    }

    public String getIso_3166_1() {
        return mIso_3166_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        mIso_3166_1 = iso_3166_1;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        mSite = site;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}
