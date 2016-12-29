package ooo.oxo.excited.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import org.xwalk.core.XWalkView;

/**
 * Created by seasonyuu on 2016/11/18.
 */

public class AjaXWalkView extends XWalkView {
    private OnScrollChangeListener onScrollChangeListener;

    private int mScrollX;
    private int mScrollY;

    public int getCurrentScrollX() {
        return mScrollX;
    }

    public int getCurrentScrollY() {
        return mScrollY;
    }

    public AjaXWalkView(Context context) {
        super(context);
    }

    public AjaXWalkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AjaXWalkView(Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        onScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        mScrollX = l;
        mScrollY = t;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
