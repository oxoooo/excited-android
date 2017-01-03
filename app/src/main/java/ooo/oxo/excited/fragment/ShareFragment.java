package ooo.oxo.excited.fragment;


import android.Manifest;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import ooo.oxo.excited.LoginActivity;
import ooo.oxo.excited.LoginManager;
import ooo.oxo.excited.R;
import ooo.oxo.excited.ShareLinkActivity;
import ooo.oxo.excited.SharePhotoActivity;
import ooo.oxo.excited.utils.ToastUtils;
import rx.Observable;

public class ShareFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "Share";

    private static final int CAMERA_REQUEST_CODE = 10006;
    private static final int ALBUM_REQUEST_CODE = 10007;
    private static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath()
                    + "/Excited/";

    private File photoFile;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (appBarCallback != null) {
            appBarCallback.setExpanded(true, false);
        }
        if (!LoginManager.checkLogin(context)) {
            View view = inflater.inflate(R.layout.layout_share_unlogin, container, false);
            Button login = (Button) view.findViewById(R.id.login);
            login.setOnClickListener(this);
            return view;
        } else {
            View view = inflater.inflate(R.layout.share_fragment, container, false);
            view.findViewById(R.id.link).setOnClickListener(onClickListener);
            view.findViewById(R.id.picture).setOnClickListener(onClickListener);
            return view;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener onClickListener = view -> {
        if (view.getId() == R.id.link) {
            startActivity(new Intent(context, ShareLinkActivity.class));
        } else if (view.getId() == R.id.picture) {
            Observable.just(null)
                    .compose(ensurePermissions(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    .subscribe(granted -> {
                        if (granted) {
                            photoSelector();
                        } else {
                            ToastUtils.shorts(context, R.string.required_camera_store_permission);
                        }
                    });
        }
    };

    private void photoSelector() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setItems(R.array.photo_select_options, (dialogInterface, index) -> {
                    if (index == 0) {
                        openCamera();
                    } else if (index == 1) {
                        openGallery();
                    }
                });
        builder.show();
    }

    public void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            String cameraPath = SAVED_IMAGE_DIR_PATH +
                    System.currentTimeMillis() + ".png";
            Intent intent = new Intent();
            // 指定开启系统相机的Action
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String outFilePath = SAVED_IMAGE_DIR_PATH;
            File dir = new File(outFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            photoFile = new File(cameraPath);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    private Observable.Transformer<Object, Boolean> ensurePermissions(String... permissions) {
        return RxPermissions.getInstance(context).ensure(permissions);
    }

    private void notifyMediaScanning(File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.getPath()}, null, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(context, SharePhotoActivity.class);
        if (requestCode == CAMERA_REQUEST_CODE && data != null) {
            notifyMediaScanning(photoFile);
            intent.setData(data.getData());
            startActivity(intent);
        } else if (requestCode == ALBUM_REQUEST_CODE && data != null) {
            intent.setData(data.getData());
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

}
