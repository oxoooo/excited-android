package ooo.oxo.excited.fragment.callback;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.WindowInsets;

/**
 * only use in MainActivity, and may be called by 4 fragments in MainActivity
 * Created by seasonyuu on 2016/11/17.
 */

public interface ContainerCallback {
    ViewGroup getContainer();

    Rect getWindowInsetsRect();
}
