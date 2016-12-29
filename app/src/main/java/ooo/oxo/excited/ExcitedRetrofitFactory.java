package ooo.oxo.excited;

import android.content.Context;

/**
 * Created by zsj on 2016/10/18.
 */

public class ExcitedRetrofitFactory {

    private static final Object object = new Object();
    private static volatile ExcitedRetrofit retrofit;

    public static ExcitedRetrofit getRetrofit(Context context) {
        synchronized (object) {
            if (retrofit == null) {
                retrofit = new ExcitedRetrofit(context);
            }
            return retrofit;
        }
    }
}
