package app.com.lamdbui.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lamdbui on 1/10/17.
 */

public class MovieReview implements Parcelable {

//    {
//        "id": 12,
//            "page": 1,
//            "results": [
//        {
//            "id": "52d4a742760ee30e2d0dac9d",
//                "author": "Dave09",
//                "content": "One of the best animated films I have ever seen. Great characters, amusing animation, and laugh-out-loud humor. Also, watch for the little skit shown after the credits. It's all great stuff that simply must be seen.",
//                "url": "https://www.themoviedb.org/review/52d4a742760ee30e2d0dac9d"
//        }
//        ],
//        "total_pages": 1,
//            "total_results": 1
//    }

    private String mID;
    private String mReviewId;
    private String mReviewAuthor;
    private String mReviewContent;
    private String mReviewUrl;

    private MovieReview(Parcel in) {
        mID = in.readString();
        mReviewId = in.readString();
        mReviewAuthor = in.readString();
        mReviewContent = in.readString();
        mReviewUrl = in.readString();

    }

    public MovieReview() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mReviewId);
        dest.writeString(mReviewAuthor);
        dest.writeString(mReviewContent);
        dest.writeString(mReviewUrl);
    }

    public static final Parcelable.Creator<MovieReview> CREATOR
            = new Parcelable.Creator<MovieReview>() {

        @Override
        public MovieReview createFromParcel(Parcel source) {
            return new MovieReview(source);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public String getReviewId() {
        return mReviewId;
    }

    public void setReviewId(String reviewId) {
        mReviewId = reviewId;
    }

    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        mReviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

    public void setReviewContent(String reviewContent) {
        mReviewContent = reviewContent;
    }

    public String getReviewUrl() {
        return mReviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        mReviewUrl = reviewUrl;
    }
}
