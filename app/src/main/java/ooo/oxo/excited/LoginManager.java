package ooo.oxo.excited;

import android.content.Context;

import ooo.oxo.excited.utils.PreferenceManager;

import static ooo.oxo.excited.LoginActivity.ID;
import static ooo.oxo.excited.LoginActivity.TOKEN;

/**
 * Created by zsj on 2016/10/21.
 */

public class LoginManager {

    public static boolean checkLogin(Context context) {
        String token = PreferenceManager.getValue(context, TOKEN);
        String id = PreferenceManager.getValue(context, ID);

        return !(token == null || id == null);
    }

}
