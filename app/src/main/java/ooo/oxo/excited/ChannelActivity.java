package ooo.oxo.excited;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.data.DataObserver;
import ooo.oxo.excited.data.IData;
import ooo.oxo.excited.fragment.ChannelFragment;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.model.Channel;
import ooo.oxo.excited.provider.MusicViewProvider;
import ooo.oxo.excited.provider.PictureViewProvider;
import ooo.oxo.excited.provider.VideoViewProvider;
import ooo.oxo.excited.provider.WebViewProvider;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Header;
import ooo.oxo.excited.provider.item.Music;
import ooo.oxo.excited.provider.item.Picture;
import ooo.oxo.excited.provider.item.Video;
import ooo.oxo.excited.provider.item.Web;
import ooo.oxo.excited.provider.listener.OnItemClickListener;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.AnimUtils;
import ooo.oxo.excited.view.MiuiStatusBarCompat;
import ooo.oxo.excited.widget.InsetsToolbar;
import ooo.oxo.excited.widget.MusicView;

import static ooo.oxo.excited.MainActivity.MUSIC;


public class ChannelActivity extends RxAppCompatActivity implements
        OnItemClickListener, View.OnClickListener,
        AppBarLayout.OnOffsetChangedListener, IData.IItems {

    private SwipeRefreshLayout refreshLayout;
    private ViewGroup webViewContent;
    private MusicView webView;
    private ImageView progressImage;
    private AppBarLayout appBarLayout;

    private MultiTypeAdapter adapter;

    private IData.IItems iItems;
    private Items items = new Items();
    private QueryAPI queryAPI;

    private Channel channel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiuiStatusBarCompat.enableLightStatusBar(getWindow());
        setContentView(R.layout.channel_activity);
        InsetsToolbar toolbar = (InsetsToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        webViewContent = (ViewGroup) findViewById(R.id.webView_content);
        webView = (MusicView) findViewById(R.id.webView);
        progressImage = (ImageView) findViewById(R.id.progress_image);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        ImageButton down = (ImageButton) findViewById(R.id.down);
        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        down.setOnClickListener(this);
        cancel.setOnClickListener(this);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        channel = getIntent().getParcelableExtra(ChannelFragment.CHANNEL);

        adapter = new MultiTypeAdapter(items);

        Register.registerChannelItem(adapter, new DataService(this));
        list.setAdapter(adapter);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(channel.name);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        coordinatorLayout.setOnApplyWindowInsetsListener(((view, insets) -> {
            boolean consumed = false;
            for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
                View child = coordinatorLayout.getChildAt(i);

                Rect childInsets = new Rect(
                        insets.getSystemWindowInsetLeft(),
                        (child instanceof RecyclerView) ? 0 : insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());

                child.dispatchApplyWindowInsets(insets.replaceSystemWindowInsets(childInsets));

                consumed = true;
            }

            return consumed ? insets.consumeSystemWindowInsets() : insets;
        }));

        list.setOnApplyWindowInsetsListener(((view, windowInsets) -> {
            list.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
            return windowInsets;
        }));

        queryAPI = ExcitedRetrofitFactory.getRetrofit(this).createApi(QueryAPI.class);
        iItems = new DataObserver(this);

        refreshLayout.setOnRefreshListener(() -> loadData(true));
        int offset = getResources().getDimensionPixelOffset(R.dimen.default_toolbar_height);
        refreshLayout.setProgressViewOffset(true, offset, 2 * offset);

        setListener();

        loadData();
    }

    private void loadData() {
        loadData(false);
    }

    private void loadData(final boolean clean) {
        queryAPI.getData(QueryField.cards(null, true, 10, channel.id))
                .compose(bindToLifecycle())
                .compose(RxMainThread.mainThread())
                .doOnNext(newChannels -> {
                    if (clean) items.clear();
                })
                .map(newData -> newData.cards)
                .doAfterTerminate(() -> refreshLayout.setRefreshing(false))
                .subscribe(cards -> {
                    if (cards.size() > 0) {
                        refreshLayout.setBackgroundColor(
                                getResources().getColor(R.color.list_background));
                    } else {
                        refreshLayout.setBackgroundColor(Color.WHITE);
                    }
                    addData(adapter, cards);
                }, throwable -> {
                    Snackbar.make(refreshLayout,
                            throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void addData(MultiTypeAdapter adapter, List<Card> cards) {
        items.add(new Header(channel, adapter));
        iItems.addData(adapter, cards);
    }

    @Override
    public Items items() {
        return items;
    }

    @Override
    public boolean showPlus() {
        return false;
    }

    private void setListener() {
        MusicViewProvider musicProvider = adapter.getProviderByClass(Music.class);
        WebViewProvider webProvider = adapter.getProviderByClass(Web.class);
        VideoViewProvider videoProvider = adapter.getProviderByClass(Video.class);
        PictureViewProvider pictureProvider = adapter.getProviderByClass(Picture.class);
        musicProvider.setOnItemClickListener(this);
        webProvider.setOnItemClickListener(this);
        videoProvider.setOnItemClickListener(this);
        pictureProvider.setOnItemClickListener(this);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onItemClick(Card data, String tag) {
        switch (tag) {
            case MUSIC:
                loadMusic(data.source);
                break;
        }
    }

    private void loadMusic(String url) {
        webViewContent.setVisibility(View.VISIBLE);
        AnimUtils.animIn(webViewContent);
        webView.loadMusic(progressImage, url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.down:
                AnimUtils.animOut(webViewContent);
                break;
            case R.id.cancel:
                webView.resetUrl();
                AnimUtils.animOut(webViewContent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("channel", channel);
        setResult(0, result);
        super.onBackPressed();
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset >= 0) {
            refreshLayout.setEnabled(true);
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.setEnabled(false);
        }
    }

}
