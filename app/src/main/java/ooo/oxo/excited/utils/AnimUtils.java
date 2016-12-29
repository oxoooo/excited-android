package ooo.oxo.excited.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import static android.animation.ObjectAnimator.ofFloat;

/**
 * Created by zsj on 2016/10/27.
 */

public class AnimUtils {

    private static Interpolator interpolator;

    public static void animIn(final View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, ScreenUtils.getHeight(view.getContext()), 0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(300);
        animation.setInterpolator(getInterpolator());
        view.startAnimation(animation);
    }

    public static void animOut(final View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, ScreenUtils.getHeight(view.getContext()));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(300);
        animation.setInterpolator(getInterpolator());
        view.startAnimation(animation);
    }

    public static void play(View view) {
        RotateAnimation animation = new RotateAnimation(0, 360f, view.getWidth() / 2, view.getHeight() / 2);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(7000);
        animation.setRepeatMode(ValueAnimator.RESTART);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        view.startAnimation(animation);
    }

    public static void stopPlay(View view) {
        view.clearAnimation();
    }

    public static void slideIn(View view) {
        view.setTranslationY(-ScreenUtils.getHeight(view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(getInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .setDuration(350)
                .start();
    }

    public static void slideOut(View view) {
        view.animate()
                .translationY(-ScreenUtils.getHeight(view.getContext()))
                .setInterpolator(getInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                })
                .setDuration(350)
                .start();
    }

    private static Interpolator getInterpolator() {
        if (interpolator == null) {
            interpolator = new LinearOutSlowInInterpolator();
        }
        return interpolator;
    }
}
