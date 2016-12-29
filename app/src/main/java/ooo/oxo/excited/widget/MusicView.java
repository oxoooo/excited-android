package ooo.oxo.excited.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

public class MusicView extends XWalkView {

    private String musicUrl;

    public MusicView(Context context) {
        this(context, null);
    }

    public MusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadMusic(View progressView, String url) {

        if (url.equals(musicUrl)) return;

        final XWalkSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        setResourceClient(new WebChrome(this, progressView));
        load(url, null);
        musicUrl = url;
    }

    public void resetUrl() {
        musicUrl = null;
        clearCache(true);
        load("about:blank", null);
    }

    private static class WebChrome extends XWalkResourceClient {

        private final View progressView;

        private WebChrome(XWalkView view, View progressView) {
            super(view);
            this.progressView = progressView;
        }

        @Override
        public void onProgressChanged(XWalkView view, int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);
            if (progressInPercent != 100) {
                progressView.setVisibility(VISIBLE);
            } else {
                progressView.animate()
                        .alpha(0)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                progressView.setVisibility(GONE);
                            }
                        })
                        .start();
            }
        }

    }

}
