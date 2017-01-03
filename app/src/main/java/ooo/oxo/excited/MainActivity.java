package ooo.oxo.excited;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.roughike.bottombar.BottomBar;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.fragment.BaseFragment;
import ooo.oxo.excited.fragment.ChannelFragment;
import ooo.oxo.excited.fragment.ExcitedFragment;
import ooo.oxo.excited.fragment.FavoritesFragment;
import ooo.oxo.excited.fragment.ShareFragment;
import ooo.oxo.excited.fragment.callback.AppBarCallback;
import ooo.oxo.excited.fragment.callback.Callback;
import ooo.oxo.excited.fragment.callback.ContainerCallback;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.item.SendingCard;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.AnimUtils;
import ooo.oxo.excited.utils.PreferenceManager;
import ooo.oxo.excited.utils.StatusBarUtils;
import ooo.oxo.excited.view.MiuiStatusBarCompat;
import ooo.oxo.excited.view.OxoAppBarBehavior;
import ooo.oxo.excited.widget.InsetsFrameLayout;
import ooo.oxo.excited.widget.MusicView;

import static ooo.oxo.excited.LoginActivity.ID;
import static ooo.oxo.excited.LoginActivity.TOKEN;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, Callback, ContainerCallback, AppBarCallback {

    public static final String MUSIC = "music";
    public static final String WEB = "web";
    public static final String VIDEO = "video";
    public static final String PICTURE = "image";
    public static final String COVER_WEB = "cover_web";
    private static final int EXCITED_FRAGMENT_INDEX = 0;
    private static final int CHANNEL_FRAGMENT_INDEX = 1;
    private static final int FAVORITES_FRAGMENT_INDEX = 2;
    private static final int SHARE_FRAGMENT_INDEX = 3;

    private ViewGroup webViewContent;
    private MusicView webView;
    private ImageView progressImage;
    private FrameLayout playContent;
    private ImageView play;
    private BottomBar bottomBar;
    private InsetsFrameLayout container;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolbarLayout;

    private List<BaseFragment> fragments = new ArrayList<>();
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiuiStatusBarCompat.enableLightStatusBar(getWindow());
        setContentView(R.layout.main_activity);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        container = (InsetsFrameLayout) findViewById(R.id.container);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        setBarBackground();

        webViewContent = (ViewGroup) findViewById(R.id.webView_content);
        webView = (MusicView) findViewById(R.id.webView);
        progressImage = (ImageView) findViewById(R.id.progress_image);
        playContent = (FrameLayout) findViewById(R.id.play_content);
        play = (ImageView) findViewById(R.id.image_play);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        container.setPrevent(InsetsFrameLayout.PREVENT_TOP);

        ImageButton down = (ImageButton) findViewById(R.id.down);
        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        down.setOnClickListener(this);
        cancel.setOnClickListener(this);
        playContent.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        userId = PreferenceManager.getValue(this, LoginActivity.ID);

        setupFragments();
        addHomeFrame();

        setNavigationListener();

        String targetFragment = getIntent().getStringExtra("fragment");
        switchFragment(targetFragment);

        checkLoginStatus();
    }

    private void setBarBackground() {
        for (int i = 0; i < bottomBar.getTabCount(); i++) {
            bottomBar.getTabAtPosition(i).setBackgroundResource(R.drawable.channel_ripple);
        }
    }

    private void switchFragment(String targetFragment) {
        if (targetFragment != null) {
            switch (targetFragment) {
                case ExcitedFragment.TAG:
                    bottomBar.selectTabAtPosition(EXCITED_FRAGMENT_INDEX);
                    break;
                case ChannelFragment.TAG:
                    bottomBar.selectTabAtPosition(CHANNEL_FRAGMENT_INDEX);
                    break;
                case FavoritesFragment.TAG:
                    SendingCard sendingCard = getIntent().getParcelableExtra("sending_card");
                    if (sendingCard != null) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("sending_card", sendingCard);
                        fragments.get(FAVORITES_FRAGMENT_INDEX).setArguments(bundle);
                    } else {
                        fragments.get(FAVORITES_FRAGMENT_INDEX).setArguments(null);
                    }
                    bottomBar.selectTabAtPosition(FAVORITES_FRAGMENT_INDEX);
                    break;
                case ShareFragment.TAG:
                    bottomBar.selectTabAtPosition(SHARE_FRAGMENT_INDEX);
                    break;
            }
        }
    }

    private void checkLoginStatus() {
        QueryAPI queryAPI = ExcitedRetrofitFactory.getRetrofit(this).createApi(QueryAPI.class);
        queryAPI.getData(QueryField.checkLogin())
                .map(newData -> newData.user)
                .compose(RxMainThread.mainThread())
                .subscribe(user -> {
                    if (user != null) {
                        String token = user.token;
                        String id = user.id;
                        if (token != null && id != null) {
                            PreferenceManager.putString(this, TOKEN, token);
                            PreferenceManager.putString(this, ID, id);
                        } else {
                            clearLoginState();
                        }
                    } else {
                        clearLoginState();
                    }
                }, Throwable::printStackTrace);
    }

    private void clearLoginState() {
        PreferenceManager.putString(this, TOKEN, null);
    }

    private void setupFragments() {
        fragments.add(new ExcitedFragment());
        fragments.add(new ChannelFragment());
        FavoritesFragment favoritesFragment = new FavoritesFragment();
        favoritesFragment.registerCallback(this);
        fragments.add(favoritesFragment);
        fragments.add(new ShareFragment());

        for (BaseFragment fragment : fragments) {
            fragment.setAppBarCallback(this);
            fragment.setContainerCallback(this);
        }
    }

    private void addHomeFrame() {
        Bundle arguments = new Bundle();
        arguments.putString(LoginActivity.ID, userId);
        setTitle(R.string.tab_excited);
        ExcitedFragment fragment = (ExcitedFragment) fragments.get(EXCITED_FRAGMENT_INDEX);
        fragment.setArguments(arguments);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
        fragment.registerCallback(this);
    }

    private boolean isFirstIn = true;

    private void setNavigationListener() {
        bottomBar.setOnTabSelectListener(tabId -> {

            if (tabId != R.id.tab_excited && playContent.getAlpha() == 1) {
                playContent.animate().alpha(0).start();
                playContent.setOnClickListener(null);
            } else if (tabId == R.id.tab_excited && playContent.getAlpha() == 0) {
                playContent.animate().alpha(1).start();
                playContent.setOnClickListener(this);
            }

            if (isFirstIn) {
                isFirstIn = false;
                return;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (tabId) {
                case R.id.tab_excited:
                    setTitle(R.string.tab_excited);
                    showFragment(transaction,
                            fragments.get(EXCITED_FRAGMENT_INDEX), ExcitedFragment.TAG);
                    break;
                case R.id.tab_channels:
                    setTitle(R.string.tab_channels);
                    showFragment(transaction,
                            fragments.get(CHANNEL_FRAGMENT_INDEX), ChannelFragment.TAG);
                    break;
                case R.id.tab_favorites:
                    setTitle(R.string.tab_favorites);
                    showFragment(transaction,
                            fragments.get(FAVORITES_FRAGMENT_INDEX), FavoritesFragment.TAG);
                    break;
                case R.id.tab_share:
                    setTitle(R.string.share_label);
                    showFragment(transaction,
                            fragments.get(SHARE_FRAGMENT_INDEX), ShareFragment.TAG);
                    break;
            }
            checkNeedClearStack(20);
        });
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);

        toolbarLayout.setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        toolbarLayout.setTitle(title);
    }

    @Override
    public void setExpanded(boolean expanded, boolean canDrag) {
        appBarLayout.setExpanded(expanded);

        OxoAppBarBehavior behavior =
                (OxoAppBarBehavior) ((CoordinatorLayout.LayoutParams)
                        appBarLayout.getLayoutParams()).getBehavior();
        if (behavior != null) {
            behavior.setCanMove(expanded);
            behavior.setCanDrag(canDrag);
        }
    }

    @Override
    public AppBarLayout getAppBar() {
        return appBarLayout;
    }

    private void checkNeedClearStack(int maxCount) {
        if (getSupportFragmentManager().getBackStackEntryCount() > maxCount) {
            getSupportFragmentManager().popBackStack(
                    ShareFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().popBackStack(
                    FavoritesFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().popBackStack(
                    ChannelFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().popBackStack(
                    ExcitedFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void showFragment(FragmentTransaction transaction, Fragment fragment, String tag) {
        transaction.replace(R.id.container, fragment, tag).addToBackStack(tag).commit();
    }

    @Override
    public void callback(Card card, String tag) {
        switch (tag) {
            case MUSIC:
                loadMusic(card.source);
                break;
        }
    }

    private void loadMusic(String url) {
        setMusicViewShown(true, true);
        AnimUtils.play(play);
        webView.loadMusic(progressImage, url);
    }

    /**
     * 设置音乐界面可见与否
     *
     * @param enabled     设为可见并动画
     * @param iconVisible 控制Toolbar中唱片动画可见性，亦可表述音乐是否保持播放
     */
    private void setMusicViewShown(boolean enabled, boolean iconVisible) {
        if (Build.VERSION.SDK_INT >= 23)
            StatusBarUtils.setLightStatusBar(getWindow().getDecorView());
        playContent.setVisibility(iconVisible ? View.VISIBLE : View.INVISIBLE);

        if (enabled) {
            MiuiStatusBarCompat.setStatusBarDarkMode(false, this);
            AnimUtils.animIn(webViewContent);
        } else {
            MiuiStatusBarCompat.setStatusBarDarkMode(true, this);
            if (!iconVisible) webView.resetUrl();
            AnimUtils.animOut(webViewContent);
        }
    }

    @Override
    public void onBackPressed() {
        if (webViewContent.getVisibility() == View.VISIBLE) {
            setMusicViewShown(false, true);
        } else {
            if (bottomBar.getCurrentTabPosition() != 0) {
                try {
                    Method selectTabAtPosition = BottomBar.class
                            .getDeclaredMethod("selectTabAtPosition", int.class, boolean.class);
                    selectTabAtPosition.setAccessible(true);
                    selectTabAtPosition.invoke(bottomBar, 0, true);
                } catch (NoSuchMethodException |
                        InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else
                finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down:
                setMusicViewShown(false, true);
                break;
            case R.id.cancel:
                setMusicViewShown(false, false);
                AnimUtils.stopPlay(play);
                break;
            case R.id.play_content:
                setMusicViewShown(true, true);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (bottomBar != null) {
            if (Build.VERSION.SDK_INT >= 24) {
                if (isInMultiWindowMode()) {}
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webViewContent.removeAllViews();
        webView.resetUrl();
    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }

    @Override
    public Rect getWindowInsetsRect() {
        if (container.getInsets() != null)
            return container.getInsets();
        return new Rect(0, 0, 0, 0);
    }

}
