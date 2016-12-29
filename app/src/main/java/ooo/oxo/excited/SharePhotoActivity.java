package ooo.oxo.excited;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;

import ooo.oxo.excited.channel.SelectChannelActivity;
import ooo.oxo.excited.fragment.FavoritesFragment;
import ooo.oxo.excited.provider.item.SendingCard;
import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.excited.utils.UriUtils;
import ooo.oxo.excited.widget.InsetsToolbar;
import ooo.oxo.excited.widget.RatioImageView;


public class SharePhotoActivity extends RxAppCompatActivity implements View.OnClickListener {
    private Uri data;
    static final int REQUEST_CODE = 10006;

    private String channelId;
    private TextView selectChannel;
    private TextView photoDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_photo_activity);

        selectChannel = (TextView) findViewById(R.id.tv_select_channel);
        photoDescription = (TextView) findViewById(R.id.photo_desc);

        InsetsToolbar toolbar = (InsetsToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        RatioImageView thumb = (RatioImageView) findViewById(R.id.thumb);
        data = getIntent().getData();

        selectChannel.setOnClickListener(this);

        try {
            Bitmap src = UriUtils.getBitmapFormUri(this, data);
            if (src != null) {
                thumb.setImageBitmap(src);
                if (src.getHeight() >= src.getWidth()) {
                    thumb.setOriginalSize(1, 1);
                } else thumb.setOriginalSize(16, 9);
            } else {
                Glide.with(this).load(data).into(thumb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (TextUtils.isEmpty(channelId)) {
                ToastUtils.shorts(this, R.string.empty_channel_text);
                return true;
            } else if (TextUtils.isEmpty(photoDescription.getText())) {
                ToastUtils.shorts(this, R.string.empty_photo_description);
                return true;
            }
            String filePath = UriUtils.getPath(this, data);
            if (filePath == null) {
                ToastUtils.shorts(this, R.string.file_not_exits);
                return true;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                ToastUtils.shorts(this, R.string.file_not_exits);
                return true;
            }
            SendingCard sendingCard = new SendingCard();
            sendingCard.url = filePath;
            sendingCard.description = photoDescription.getText().toString();
            sendingCard.channelId = channelId;
            sendingCard.type = SendingCard.IMAGE;

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("sending_card", sendingCard);
            intent.putExtra("fragment", FavoritesFragment.TAG);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        startActivityForResult(new Intent(this, SelectChannelActivity.class), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            channelId = data.getStringExtra("id");
            String channel = data.getStringExtra("name");
            selectChannel.setText(channel);
            selectChannel.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        }
    }

}
