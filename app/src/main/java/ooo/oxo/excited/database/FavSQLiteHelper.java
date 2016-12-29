package ooo.oxo.excited.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by seasonyuu on 2016/12/20.
 */

public class FavSQLiteHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "excited.db";

    public FavSQLiteHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QuerySQL.createFavorite());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static final class Fav {
        public static final String TABLE_NAME = "favorite";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String TOKEN = "token";

        public static final String TITLE = "title";
        public static final String COVER = "cover";
        public static final String RATIO = "ratio";
        public static final String HEAD_ICON = "head_icon";
        public static final String HEAD_NAME = "head_name";
        public static final String SOURCE = "source";
        public static final String REMAINS = "remains";
        public static final String SUM = "sum";
        public static final String TIMESTAMP = "timestamp";
        public static final String REFINED = "refined";
        public static final String TYPE = "type";
        public static final String DESCRIPTION = "description";
        public static final String DISTANCE = "distance";
        public static final String UUID = "uuid";
        public static final String LINK = "link";
        public static final String AUTHOR_NAME = "author_name";
    }
}
