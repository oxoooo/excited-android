package ooo.oxo.excited.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author zsj
 */

public class EmptyDataLayout extends LinearLayout {

    public EmptyDataLayout(Context context) {
        this(context, null);
    }

    public EmptyDataLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyDataLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
