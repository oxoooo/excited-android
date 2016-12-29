package ooo.oxo.excited.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by seasonyuu on 2016/12/23.
 */

public class BitmapUtils {
    private static final int MAX_SIZE = 1024;

    public static File compress(String filePath) throws IOException {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(filePath);
        String outputPath = Environment.getExternalStorageDirectory().getPath()
                + "/Excited/";
        File folder = new File(outputPath);
        if (!folder.exists())
            folder.mkdir();
        File f = new File(outputPath + System.currentTimeMillis() + ".jpg");
        if (f.createNewFile()) {
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fOut == null)
                return null;
            if (Math.max(bitmap.getWidth(), bitmap.getHeight()) > MAX_SIZE
                    && !filePath.endsWith(".gif")) {
                float zoom = 1f * MAX_SIZE / Math.max(bitmap.getWidth(), bitmap.getHeight());

                Matrix matrix = new Matrix();
                matrix.setScale(zoom, zoom);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            fOut.flush();
            fOut.close();
            return f;
        }
        return null;
    }
}
