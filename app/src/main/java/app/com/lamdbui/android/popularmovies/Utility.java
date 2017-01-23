package app.com.lamdbui.android.popularmovies;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by lamdbui on 1/22/17.
 */

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    // NOTE: This function assumes 'textSize' is given in actual pixels not "sp"
    public static float getTextWidth(String text, float textSize, Typeface typeface) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        // calculate size of the text
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        //textWidth = paint.measureText(text);

        return textBounds.width();
    }

    // NOTE: This function assumes 'textSize' is given in actual pixels not "sp"
    public static float getTextHeight(String text, float textSize, Typeface typeface) {
        float textHeight;

        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        // calculate size of the text
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        //textWidth = paint.measureText(text);
        //textHeight = textBounds.height();

        // this seems more accurate than textBounds.height()
        textHeight = paint.descent() - paint.ascent();

        return textHeight;
    }
}
