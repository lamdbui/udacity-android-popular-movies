package app.com.lamdbui.android.popularmovies;

/**
 * Created by lamdbui on 1/10/17.
 */

public class MovieReview {

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
