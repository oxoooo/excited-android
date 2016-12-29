package ooo.oxo.excited;

import android.app.Application;

import org.xwalk.core.XWalkPreferences;

/**
 * Created by zsj on 2016/10/18.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);
        XWalkPreferences.setValue(XWalkPreferences.ENABLE_THEME_COLOR, false);
    }
}
