package ooo.oxo.excited.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import ooo.oxo.excited.utils.ScreenUtils;

/**
 * Created by zsj on 2016/10/18.
 */

public class SpringView extends View {

    private Paint blackPaint;

    private Spring spring;

    private int originViewWidth;
    private float viewWidth;
    private int height;

    public SpringView(Context context) {
        this(context, null);
    }

    public SpringView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setColor(Color.BLACK);

        spring = SpringSystem.create().createSpring();
        SimpleSpringListener springListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);

                double value = spring.getCurrentValue();
                viewWidth = (int) value;
                invalidate();
            }
        };
        spring.addListener(springListener);
        originViewWidth = ScreenUtils.getWidth(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        height = h;
        originViewWidth = w;

    }
    
    public void startSpring(float radio) {
        viewWidth = originViewWidth * radio;
        postDelayed(() -> spring.setEndValue(viewWidth), 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, viewWidth, height, blackPaint);
    }

    public void setWidth(float radio) {
        viewWidth = originViewWidth * radio;
        invalidate();
    }

}
