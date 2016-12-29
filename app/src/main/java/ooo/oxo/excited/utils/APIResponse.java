package ooo.oxo.excited.utils;


import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Notices;
import retrofit2.Response;

public class APIResponse {

    public static <T>T response(Context context, Response<T> response) {
        if (response.code() >= 200 && response.code() < 300) {
            return response.body();
        } else if (response.code() >= 400 && response.code() < 500) {
            networkError(context, response.errorBody());
        } else if (response.code() >= 500) {
            serverError(context);
        }
        return null;
    }

    private static void networkError(Context context, ResponseBody errorBody) {
        try {
            Notices notices = new Gson().fromJson(errorBody.string(), Notices.class);
            ToastUtils.shorts(context, notices.notice.text);
        } catch (IOException e) {
            ToastUtils.shorts(context, e.getMessage());
        }
    }

    private static void serverError(Context context) {
        ToastUtils.shorts(context, R.string.server_error);
    }
}
