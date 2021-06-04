package netw4ppl.ines.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Class extending the AutoCompleteTextView class to modify its behaviour.
 */
public class InstantAutoComplete extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    /**
     * Class constructor taking just the Context as an argument
     *
     * @param context the application Context
     */
    public InstantAutoComplete(Context context) {
        super(context);
    }

    /**
     * Class constructor taking two parameters.
     *
     * @param arg0 the application context
     * @param arg1 an AttributeSet object
     */
    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    /**
     * Class constructor taking three parameters.
     *
     * @param arg0 the application context
     * @param arg1 an AttributeSet object
     * @param arg2 an int
     */
    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    /**
     * Always return true. That way, even when no letter is put in the AutoCompleteTextView, the dropdown will
     * be shown.
     *
     * @return a boolean, always true.
     */
    @Override
    public boolean enoughToFilter() {
        return true;
    }

    /**
     * Override of the function onFocusChanged.
     *
     * @param focused a boolean
     * @param direction an int
     * @param previouslyFocusedRect a Rect object
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            performFiltering(getText(), 0);
        }
    }
}