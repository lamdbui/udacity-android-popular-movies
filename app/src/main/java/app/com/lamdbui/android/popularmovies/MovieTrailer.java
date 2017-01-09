package app.com.lamdbui.android.popularmovies;

/**
 * Created by lamdbui on 1/8/17.
 */

public class MovieTrailer {
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
