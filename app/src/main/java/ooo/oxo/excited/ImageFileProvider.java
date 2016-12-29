package ooo.oxo.excited;

import android.net.Uri;
import android.support.v4.content.FileProvider;

/**
 * Created by zsj on 2016/11/2.
 */

public class ImageFileProvider extends FileProvider {

    @Override
    public String getType(Uri uri) {
        return "image/jpg";
    }
}
