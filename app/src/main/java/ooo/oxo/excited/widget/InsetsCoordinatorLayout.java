package ooo.oxo.excited.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

/**
 * Created by seasonyuu on 2016/11/30.
 */

public class InsetsCoordinatorLayout extends CoordinatorLayout {
    private Rect mInsets;

    public InsetsCoordinatorLayout(Context context) {
        this(context, null);
    }

    public InsetsCoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetsCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnApplyWindowInsetsListener((v, insets) ->
                applySystemWindowInsets(insets) ? insets.consumeSystemWindowInsets() : insets);
    }

    private boolean applySystemWindowInsets(WindowInsets insets) {
        boolean consumed = false;

        mInsets = new Rect(
                insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom());

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            Rect childInsets = new Rect(
                    insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());

            child.dispatchApplyWindowInsets(insets.replaceSystemWindowInsets(childInsets));

            consumed = true;
        }

        return consumed;
    }

    public Rect getInsets() {
        return mInsets;
    }
}
