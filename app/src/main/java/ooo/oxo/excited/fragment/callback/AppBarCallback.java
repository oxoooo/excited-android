package ooo.oxo.excited.fragment.callback;

import android.support.design.widget.AppBarLayout;

/**
 * Created by seasonyuu on 2016/11/30.
 */

public interface AppBarCallback {
    /**
     * to set appBar expanded state
     *
     * @param expanded expanded state
     * @param canDrag  can appBar be drag
     */
    void setExpanded(boolean expanded, boolean canDrag);

    /**
     * to get appBar
     *
     * @return appBarLayout
     */
    AppBarLayout getAppBar();
}
