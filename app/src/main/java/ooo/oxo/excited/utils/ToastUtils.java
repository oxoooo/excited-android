package ooo.oxo.excited.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils {

    public static void show(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void show(Context context, int resId, int duration) {
        Toast.makeText(context, context.getString(resId), duration).show();
    }

    public static void shorts(Context context, int resId) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static void shorts(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void longs(Context context, int resId) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_LONG).show();
    }

    public static void longs(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
