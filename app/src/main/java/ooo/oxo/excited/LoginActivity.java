package ooo.oxo.excited;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ooo.oxo.excited.api.UserApi;
import ooo.oxo.excited.model.Data;
import ooo.oxo.excited.model.Notices;
import ooo.oxo.excited.model.User;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.APIResponse;
import ooo.oxo.excited.utils.CustomTabActivityHelper;
import ooo.oxo.excited.utils.PreferenceManager;
import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.excited.view.MiuiStatusBarCompat;

/**
 * Created by zsj on 2016/10/17.
 */

public class LoginActivity extends RxAppCompatActivity implements View.OnClickListener {

    private static final String PHONE = "phone";
    private static final String VERIFY_CODE = "code";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    public static final String RANDOM_UUID = "uuid";

    private EditText phone;
    private EditText verifyCode;
    private Button verifyCodeBtn;
    private TextView tvCountryName;
    private TextView tvCountryNumbers;

    private UserApi userApi;

    private String phoneNumber;
    private String codeNumber;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiuiStatusBarCompat.enableLightStatusBar(getWindow());
        setContentView(R.layout.login_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        LinearLayout userProtocol = (LinearLayout) findViewById(R.id.user_protocol);
        phone = (EditText) findViewById(R.id.phone);
        verifyCode = (EditText) findViewById(R.id.code);
        verifyCodeBtn = (Button) findViewById(R.id.get_verify_code);
        Button submit = (Button) findViewById(R.id.submit);
        tvCountryName = (TextView) findViewById(R.id.country_name);
        tvCountryNumbers = (TextView) findViewById(R.id.country_numbers);

        userApi = ExcitedRetrofitFactory.getRetrofit(this).createApi(UserApi.class);

        verifyCodeBtn.setOnClickListener(this);
        findViewById(R.id.countries).setOnClickListener(this);
        submit.setOnClickListener(submitListener);
        userProtocol.setOnClickListener(v ->
                CustomTabActivityHelper.openCustomTab(LoginActivity.this, new CustomTabsIntent.Builder()
                                .setToolbarColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary))
                                .addDefaultShareMenuItem()
                                .build(),
                        Uri.parse("https://excited.aja.im/eula")));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_verify_code:
                phoneNumber = phone.getText().toString();
                if (!tvCountryNumbers.getText().toString().equals("+86"))
                    phoneNumber = tvCountryNumbers.getText().toString().substring(1) + phoneNumber;
                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.shorts(this, getString(R.string.phone_empty));
                } else {
                    HashMap<String, String> body = new HashMap<>();
                    body.put(PHONE, phoneNumber);
                    userApi.getCode(body)
                            .compose(bindToLifecycle())
                            .compose(RxMainThread.mainThread())
                            .subscribe(noticesResponse -> {
                                Notices notices = APIResponse.response(this, noticesResponse);
                                if (notices != null) {
                                    verifyCodeBtn.setEnabled(false);
                                    countDownTime();
                                    verifyCode.requestFocus();
                                } else {
                                    verifyCodeBtn.setEnabled(true);
                                }
                            }, throwable -> ToastUtils.shorts(this, throwable.getMessage()));
                }
                break;
            case R.id.countries:
                startActivityForResult(new Intent(this, SelectCountryActivity.class), 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (data == null) return;
            String number = data.getStringExtra("number");
            String country = data.getStringExtra("country");

            tvCountryName.setText(country);
            tvCountryNumbers.setText(number);
        }
    }

    private void countDownTime() {
        final CountDownTimer timer = new CountDownTimer(TimeUnit.SECONDS.toMillis(30),
                TimeUnit.SECONDS.toMillis(1)) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                verifyCodeBtn.setText(String.format("%d秒", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                verifyCodeBtn.setEnabled(true);
                verifyCodeBtn.setText(getResources().getString(R.string.verify_code));
            }
        };
        timer.start();
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            phoneNumber = phone.getText().toString();
            if (!tvCountryNumbers.getText().toString().equals("+86"))
                phoneNumber = tvCountryNumbers.getText().toString().substring(1) + phoneNumber;
            codeNumber = verifyCode.getText().toString();
            if (TextUtils.isEmpty(codeNumber) && TextUtils.isEmpty(phoneNumber)) {
                ToastUtils.shorts(LoginActivity.this, getString(R.string.code_phone_empty));
            } else {
                HashMap<String, String> body = new HashMap<>();
                body.put(PHONE, phoneNumber);
                body.put(VERIFY_CODE, codeNumber);
                userApi.verifyUser(body)
                        .compose(bindToLifecycle())
                        .compose(RxMainThread.mainThread())
                        .subscribe(userResponse -> {
                            User user = APIResponse.response(LoginActivity.this, userResponse);
                            if (user != null) {
                                saveData(user.data);
                            }
                        }, throwable -> ToastUtils.shorts(LoginActivity.this, throwable.getMessage()));
            }
        }
    };

    private void saveData(Data data) {
        PreferenceManager.putString(this, TOKEN, data.attributes.token);
        PreferenceManager.putString(this, ID, data.id);
        toShareInteresting();
    }

    private void toShareInteresting() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
}
