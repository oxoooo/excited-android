package ooo.oxo.excited.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MusicPlayerBottomBar extends LinearLayout implements View.OnClickListener {

    public MusicPlayerBottomBar(Context context) {
        this(context, null);
    }

    public MusicPlayerBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicPlayerBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setOnApplyWindowInsetsListener((v, insets) -> {
            final int l = insets.getSystemWindowInsetLeft();
            final int t = insets.getSystemWindowInsetTop();
            final int r = insets.getSystemWindowInsetRight();
            final int b = insets.getSystemWindowInsetBottom();
            setPadding(l, 0, r, b);
            return insets.replaceSystemWindowInsets(0, t, 0, 0);
        });

        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {}

}
