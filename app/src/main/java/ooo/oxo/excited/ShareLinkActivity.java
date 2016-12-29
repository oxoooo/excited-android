package ooo.oxo.excited;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.excited.view.MiuiStatusBarCompat;
import ooo.oxo.excited.widget.InsetsToolbar;


public class ShareLinkActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String URL = "url";

    private EditText input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiuiStatusBarCompat.enableLightStatusBar(getWindow());
        setContentView(R.layout.share_link_activity);

        InsetsToolbar toolbar = (InsetsToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        toolbar.setNavigationOnClickListener(view -> finish());

        input = (EditText) findViewById(R.id.input);
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_SEND)) {
                handleActionSend(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
    }

    private void handleActionSend(String shareText) {
        if (shareText == null) return;

        Pattern match = Pattern.compile("((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");
        Matcher matcher = match.matcher(shareText);
        if (matcher.find()) {
            input.setText(matcher.group());
        }
    }

    private String getUrl() {
        String url = input.getText().toString();
        if (TextUtils.isEmpty(url)) {
            ToastUtils.shorts(this, R.string.link_empty_text);
            return null;
        } else {
            return url;
        }
    }

    private static final int REQUEST_CODE = 10007;

    @Override
    public void onClick(View view) {
        String url = getUrl();
        if (url != null) {
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(URL, url);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && data != null) {
            finish();
        }
    }
}
