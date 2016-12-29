package ooo.oxo.excited;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.io.IOUtils;
import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.IOException;

import ooo.oxo.excited.utils.CustomTabActivityHelper;
import ooo.oxo.excited.view.MiuiStatusBarCompat;
import ooo.oxo.excited.widget.AjaXWalkView;

public class BrowserActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener, AjaXWalkView.OnScrollChangeListener,
        Toolbar.OnMenuItemClickListener {

    private static final String TAG = "BrowserActivity";
    public static final String REFINED_URL = "refined_url";
    public static final String TITLE = "tvTitle";

    private Toolbar toolbar;
    private AppBarLayout appbar;
    private SwipeRefreshLayout refresher;
    private NumberProgressBar progressBar;
    private TextView tvTitle;

    private Switch toolbarSwitch;

    private AjaXWalkView web;
    private AjaXWalkView reader;

    private String url;
    private String title;
    private String refinedUrl;
    private boolean readerModeInjected = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MiuiStatusBarCompat.enableLightStatusBar(getWindow());

        setContentView(R.layout.browser_activity);

        toolbarSwitch = (Switch) findViewById(R.id.reading_mode);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        refresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
        reader = (AjaXWalkView) findViewById(R.id.reading);
        progressBar = (NumberProgressBar) findViewById(R.id.progressbar);
        tvTitle = (TextView) findViewById(R.id.title);
        web = (AjaXWalkView) findViewById(R.id.web);


        final Intent intent = getIntent();

        url = intent.getDataString();
        refinedUrl = intent.getStringExtra(REFINED_URL);
        title = intent.getStringExtra(TITLE);

        toolbar.setTitle(intent.getDataString());
        if (refinedUrl == null)
            toolbar.inflateMenu(R.menu.menu_browser);
        else
            toolbar.inflateMenu(R.menu.menu_browser_with_share);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setOnTouchListener(((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (reader.isShown()) {
                    reader.scrollTo(reader.getCurrentScrollX(), reader.getCurrentScrollY());
                    reader.postDelayed(() -> {
                        try {
                            reader.evaluateJavascript(IOUtils.toString(
                                    getResources().openRawResource(R.raw.goto_top), "UTF-8"), null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, 150);
                } else {
                    web.scrollTo(web.getCurrentScrollX(), web.getCurrentScrollY());
                    web.postDelayed(() -> {
                        try {
                            web.evaluateJavascript(IOUtils.toString(
                                    getResources().openRawResource(R.raw.goto_top), "UTF-8"), null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, 150);
                }
            }
            return true;
        }));

        refresher.setOnRefreshListener(this);

        int offset = getResources().getDimensionPixelSize(R.dimen.default_toolbar_height);
        refresher.setProgressViewOffset(true, -offset / 4, offset / 2);

        tvTitle.setText(title);

        final XWalkSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);

        web.setUIClient(new WebUIClient());
        web.setResourceClient(new WebResourceClient());

        web.load(refinedUrl != null ? refinedUrl : intent.getDataString(), null);

        if (refinedUrl == null) {
            web.addJavascriptInterface(new ReaderMode(), "ReaderMode");

            reader.setResourceClient(new ReaderResourceClient());
            reader.addJavascriptInterface(new ReaderMode(), "ReaderMode");

            final XWalkSettings readerSettings = web.getSettings();
            readerSettings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.SINGLE_COLUMN);
            readerSettings.setSupportZoom(true);

            toolbarSwitch.setOnCheckedChangeListener((button, checked) ->
                    reader.setVisibility(checked ? View.VISIBLE : View.GONE));
        } else {
            toolbarSwitch.setVisibility(View.VISIBLE);
            toolbarSwitch.setChecked(false);
            toolbarSwitch.setText("显示原网站");
            toolbarSwitch.setOnCheckedChangeListener((button, checked) -> {
                if (checked) {
                    web.load(url, null);
                } else {
                    web.load(refinedUrl, null);
                }
                web.postDelayed(() -> web.getNavigationHistory().clear(), 250);
            });
        }

        web.setOnScrollChangeListener(this);
        reader.setOnScrollChangeListener(this);
    }

    @Override
    public void onRefresh() {
        web.reload(XWalkView.RELOAD_NORMAL);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_in_browser:
                openInBrowser();
                return true;
            case R.id.share_action:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_content_format), title, refinedUrl));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                return true;
            default:
                return false;
        }
    }

    private void openInBrowser() {
        final CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .addDefaultShareMenuItem()
                .build();

        CustomTabActivityHelper.openCustomTab(this, intent, Uri.parse(url));
        finish();
    }

    private void injectReaderMode() throws IOException {
        if (refinedUrl == null) {
            Log.d(TAG, "Enter reader mode");
            final String bootstrap = IOUtils.toString(
                    getResources().openRawResource(R.raw.reader_mode), "UTF-8");

            web.evaluateJavascript(bootstrap, null);
        }
    }

    private void enterReaderMode(String title, String article) throws IOException {
        String page = IOUtils.toString(
                getResources().openRawResource(R.raw.reader_view), "UTF-8");

        page = page.replace("{{title}}", title);
        page = page.replace("{{article}}", article);

        toolbar.setTitle(null);

        reader.load(getIntent().getDataString(), page);
        reader.setVisibility(View.VISIBLE);

        toolbarSwitch.setVisibility(View.VISIBLE);
        toolbarSwitch.setChecked(true);
    }

    private void openWebImage(String url) {
        Log.d(TAG, "open image: " + url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        int maxElevation = getResources().getDimensionPixelOffset(R.dimen.max_elevation);
        appbar.setElevation(1f * (t > 56 ? 56 : t) / 56 * maxElevation);
        tvTitle.setAlpha(1f * (t > 128 ? 128 : t) / 128);
    }

    @SuppressWarnings("unused")
    private final class ReaderMode {

        @JavascriptInterface
        public void enter(final String title, final String article) {
            runOnUiThread(() -> {
                readerModeInjected = true;
                try {
                    enterReaderMode(title, article);
                } catch (IOException e) {
                    Log.e(TAG, "console: failed entering reader mode", e);
                }
            });
        }

        @JavascriptInterface
        public void unavailable() {
            runOnUiThread(() -> {
                readerModeInjected = true;
                toolbarSwitch.setVisibility(View.GONE);
            });
        }

        @JavascriptInterface
        public void openImage(final String url) {
            runOnUiThread(() -> openWebImage(url));
        }

    }

    private final class WebUIClient extends XWalkUIClient {

        private WebUIClient() {
            super(web);
        }

        @Override
        public void onPageLoadStarted(XWalkView view, String url) {
            super.onPageLoadStarted(view, url);
            readerModeInjected = false;
        }

        @Override
        public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
            super.onPageLoadStopped(view, url, status);

            if (!readerModeInjected) {
                try {
                    injectReaderMode();
                } catch (IOException e) {
                    Log.e(TAG, "console: failed injecting reader mode script", e);
                }
            }
        }

        @Override
        public void onReceivedTitle(XWalkView view, String title) {
            super.onReceivedTitle(view, title);
            toolbar.setTitle(null);
        }

    }

    private final class WebResourceClient extends XWalkResourceClient {

        private WebResourceClient() {
            super(web);
        }

        @Override
        public void onProgressChanged(XWalkView view, int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);

            progressBar.setProgress(progressInPercent);
            if (progressInPercent != 100) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                refresher.setRefreshing(false);
            }

            if (!readerModeInjected && progressInPercent >= 50) {
                try {
                    injectReaderMode();
                } catch (IOException e) {
                    Log.e(TAG, "console: failed injecting reader mode script", e);
                }
            }
        }

    }

    private final class ReaderResourceClient extends XWalkResourceClient {

        ReaderResourceClient() {
            super(reader);
        }

        @Override
        public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
            reader.setVisibility(View.GONE);
            toolbarSwitch.setChecked(false);
            web.load(url, null);
            return true;
        }

    }

}
