package ooo.oxo.excited.utils;

import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by seasonyuu on 2016/11/17.
 */

public class SnackbarUtils {
    public static void shorts(View container, int resId) {
        shorts(container, resId, null);
    }

    public static void indefinite(View container, int resId) {
        shorts(container, resId, null);
    }

    public static void longs(View container, int resId) {
        shorts(container, resId, null);
    }

    /**
     * To show a SnackBar for a short period of time with a particular padding.
     *
     * @param container  The view to find a parent from.
     * @param resId  The resource id of the string resource to use. Can be formatted text.
     * @param rect The padding(without top) that view need to add, if null then won't add padding.
     */
    public static void shorts(View container, int resId, Rect rect) {
        Snackbar snackbar = Snackbar.make(container, resId, Snackbar.LENGTH_SHORT);
        if (rect != null) {
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.show();
    }

    public static void indefinite(View container, int resId, Rect rect) {
        Snackbar snackbar = Snackbar.make(container, resId, Snackbar.LENGTH_INDEFINITE);
        if (rect != null) {
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.show();
    }

    public static void longs(View container, int resId, Rect rect) {
        Snackbar snackbar = Snackbar.make(container, resId, Snackbar.LENGTH_SHORT);
        if (rect != null) {
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.show();
    }

    public static void shorts(View container, String message, Rect rect) {
        Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_SHORT);
        if (rect != null) {
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.show();
    }

    public static void indefinite(View container, String message, Rect rect) {
        Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_INDEFINITE);
        if (rect != null) {
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.show();
    }

    public static void longs(View container, String message, Rect rect) {
        Snackbar snackbar = Snackbar.make(container, message, Snackbar.LENGTH_SHORT);
        if (rect != null) {
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.show();
    }
}
