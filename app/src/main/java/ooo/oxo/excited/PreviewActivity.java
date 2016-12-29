package ooo.oxo.excited;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.fragment.FavoritesFragment;
import ooo.oxo.excited.model.AddCard;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.preview.PreviewFragment;
import ooo.oxo.excited.preview.PreviewMusicFragment;
import ooo.oxo.excited.preview.PreviewWebFragment;
import ooo.oxo.excited.preview.PreviewWrongFragment;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.SnackbarUtils;
import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.excited.view.MiuiStatusBarCompat;
import ooo.oxo.excited.widget.InsetsToolbar;

public class PreviewActivity extends RxAppCompatActivity {

    private QueryAPI queryAPI;

    private String shareUrl;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiuiStatusBarCompat.enableLightStatusBar(getWindow());
        setContentView(R.layout.preview_activity);
        InsetsToolbar toolbar = (InsetsToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        progressBar = (ProgressBar) findViewById(R.id.preview_progress);

        shareUrl = getIntent().getStringExtra(ShareLinkActivity.URL);

        queryAPI = ExcitedRetrofitFactory.getRetrofit(this).createApi(QueryAPI.class);

        previewCard(shareUrl);
    }

    private void previewCard(String url) {
        queryAPI.mutation(QueryField.previewCard(url))
                .compose(bindToLifecycle())
                .compose(RxMainThread.mainThread())
                .map(newData -> newData.previewCard.card)
                .subscribe(card -> {
                    progressBar.setVisibility(View.GONE);
                    if (card != null) attachFragment(card);
                    else attachFragment(null);
                }, throwable -> attachFragment(null));
    }

    private void attachFragment(Card card) {
        Fragment fragment = null;
        Bundle arguments = new Bundle();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (card != null) {
            switch (card.type) {
                case MainActivity.MUSIC:
                    fragment = new PreviewMusicFragment();
                    break;
                case MainActivity.COVER_WEB:
                case MainActivity.WEB:
                    fragment = new PreviewWebFragment();
                    break;
            }
            arguments.putParcelable("card", card);
            arguments.putString("url", shareUrl);
        } else {
            fragment = new PreviewWrongFragment();
        }
        assert fragment != null;
        fragment.setArguments(arguments);
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.preview_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_release, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close_action) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
