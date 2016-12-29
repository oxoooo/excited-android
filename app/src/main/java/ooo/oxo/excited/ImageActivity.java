package ooo.oxo.excited;

import android.Manifest;
import android.app.WallpaperManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Locale;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import ooo.oxo.excited.rx.RxFiles;
import ooo.oxo.excited.rx.RxGlide;
import ooo.oxo.excited.utils.ImmersiveUtil;
import ooo.oxo.excited.utils.TextureSizeUtils;
import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.library.widget.TouchImageView;
import rx.Observable;

public class ImageActivity extends RxAppCompatActivity implements
        ImageViewTouch.OnImageViewTouchSingleTapListener, View.OnLongClickListener {

    private static final String AUTHORITY_IMAGES = BuildConfig.APPLICATION_ID + ".images";

    private ProgressBar progressBar;

    private String imageUrl;
    private String uuid;

    private AlertDialog longPressMenuDialog;

    private Observable<Void> menuItemClicks(Toolbar toolbar, @IdRes int id) {
        return RxMenuItem.clicks(toolbar.getMenu().findItem(id));
    }

    private Observable.Transformer<Object, Boolean> ensurePermissions(String... permissions) {
        return RxPermissions.getInstance(this).ensure(permissions);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersiveUtil.enter(this);

        setContentView(R.layout.image_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_image);

        final Observable<File> observableSave = Observable.just(null)
                .compose(ensurePermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .filter(granted -> {
                    if (granted) {
                        return true;
                    } else {
                        showMessage(getResources().getString(R.string.permission_required));
                        return false;
                    }
                })
                .flatMap(aBoolean -> download())
                .doOnNext(file -> showMessage(getString(R.string.save_success, file.getPath())));

        final Observable<Uri> observableSetWallpaper = Observable.just(null)
                .compose(ensurePermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .filter(granted -> {
                    if (granted) {
                        return true;
                    } else {
                        showMessage(getResources().getString(R.string.permission_required));
                        return false;
                    }
                })
                .flatMap(aBoolean -> download(imageUrl))
                .map(file -> FileProvider.getUriForFile(ImageActivity.this, AUTHORITY_IMAGES, file))
                .doOnNext(uri -> {
                    final WallpaperManager wm = WallpaperManager.getInstance(ImageActivity.this);
                    startActivity(wm.getCropAndSetWallpaperIntent(uri));
                });

        menuItemClicks(toolbar, R.id.save)
                .compose(bindToLifecycle())
                .flatMap(object -> observableSave)
                .subscribe();

        menuItemClicks(toolbar, R.id.set_wallpaper)
                .compose(bindToLifecycle())
                .flatMap(object -> observableSetWallpaper)
                .subscribe();

        TouchImageView image = (TouchImageView) findViewById(R.id.image);

        image.setSingleTapListener(this);
        image.setLongClickable(true);
        image.setOnLongClickListener(this);

        longPressMenuDialog = new AlertDialog.Builder(this, R.style.ImageTheme_Dialog)
                .setItems(R.array.images, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            observableSave
                                    .compose(bindToLifecycle())
                                    .subscribe();
                            break;
                        case 1:
                            observableSetWallpaper
                                    .compose(bindToLifecycle())
                                    .subscribe();
                    }
                })
                .create();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        imageUrl = getIntent().getDataString();
        uuid = getIntent().getStringExtra("uuid");

        progressBar.setVisibility(View.VISIBLE);
        int targetSize = TextureSizeUtils.getMaxTextureSize() / 4;

        Glide.with(this)
                .load(imageUrl)
                .override(targetSize, targetSize)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model,
                                               Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);
    }

    @Override
    public void onSingleTapConfirmed() {
        finish();
    }

    @Override
    public boolean onLongClick(View view) {
        imageDialog();
        return true;
    }

    private void imageDialog() {
        longPressMenuDialog.show();
    }

    private Observable<File> ensureExternalDirectory(String name) {
        return RxFiles.mkdirsIfNotExists(new File(Environment.getExternalStorageDirectory(), name));
    }

    private Observable<File> download(String url) {
        return RxGlide.download(Glide.with(this), url);
    }

    private Observable<File> save(String url, final File destination) {
        return download(url).flatMap(tmp -> RxFiles.copy(tmp, destination));
    }

    private String makeFileName() {
        return String.format(Locale.US, "%s.%s", uuid, "jpg");
    }

    private Observable<File> download() {
        return ensureExternalDirectory("Excited")
                .map(file -> new File(file, makeFileName()))
                .flatMap(file -> file.exists()
                        ? Observable.just(file)
                        : ImageActivity.this.save(imageUrl, file))
                .doOnNext(this::notifyMediaScanning);
    }

    private void notifyMediaScanning(File file) {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{file.getPath()}, null, null);
    }

    private void showMessage(String message) {
        ToastUtils.shorts(this, message);
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
    }

}
