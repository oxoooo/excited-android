package ooo.oxo.excited.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class ScreenUtils {

    private ScreenUtils(){}

    public static int getWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        Display display = manager.getDefaultDisplay();
        display.getRealSize(size);

        return size.x;
    }

    public static int getHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        Display display = manager.getDefaultDisplay();
        display.getRealSize(size);
        return size.y;
    }

}
