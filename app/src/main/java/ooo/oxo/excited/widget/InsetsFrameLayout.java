/*
 * Mantou Earth - Live your wallpaper with live earth
 * Copyright (C) 2015  XiNGRZ <xxx@oxo.ooo>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ooo.oxo.excited.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

public class InsetsFrameLayout extends FrameLayout {

    private static final String TAG = "WindowInsetsFrameLayout";

    private Rect mInsets;

    public static final int PREVENT_TOP = 0x1;
    public static final int PREVENT_LEFT = 0x2;
    public static final int PREVENT_RIGHT = 0x4;
    public static final int PREVENT_BOTTOM = 0x8;

    private int prevent;


    public InsetsFrameLayout(Context context) {
        this(context, null);
    }

    public InsetsFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnApplyWindowInsetsListener((v, insets) ->
                applySystemWindowInsets(insets) ? insets.consumeSystemWindowInsets() : insets);
    }

    public int getPrevent() {
        return prevent;
    }

    public void setPrevent(int prevent) {
        this.prevent = prevent;
    }

    private boolean applySystemWindowInsets(WindowInsets insets) {
        boolean consumed = false;

        mInsets = new Rect(
                (prevent ^ PREVENT_LEFT) > 0 ? insets.getSystemWindowInsetLeft() : 0,
                (prevent ^ PREVENT_TOP) > 0 ? insets.getSystemWindowInsetTop() : 0,
                (prevent ^ PREVENT_RIGHT) > 0 ? insets.getSystemWindowInsetRight() : 0,
                (prevent ^ PREVENT_BOTTOM) > 0 ? insets.getSystemWindowInsetBottom() : 0);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            Rect childInsets = new Rect(
                    (prevent ^ PREVENT_LEFT) > 0 ? insets.getSystemWindowInsetLeft() : 0,
                    (prevent ^ PREVENT_TOP) > 0 ? insets.getSystemWindowInsetTop() : 0,
                    (prevent ^ PREVENT_RIGHT) > 0 ? insets.getSystemWindowInsetRight() : 0,
                    (prevent ^ PREVENT_BOTTOM) > 0 ? insets.getSystemWindowInsetBottom() : 0);

            child.dispatchApplyWindowInsets(insets.replaceSystemWindowInsets(childInsets));

            consumed = true;
        }

        return consumed;
    }

    public Rect getInsets() {
        return mInsets;
    }
}
