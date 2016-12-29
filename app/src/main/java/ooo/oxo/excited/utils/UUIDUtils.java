package ooo.oxo.excited.utils;

import android.content.Context;

import java.util.UUID;

import ooo.oxo.excited.LoginActivity;

/**
 * Created by zsj on 2016/10/28.
 */

public class UUIDUtils {

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String saveUUID(Context context) {
        String uuid = randomUUID();
        PreferenceManager.putString(context, LoginActivity.RANDOM_UUID, uuid);
        return uuid;
    }
}
