package ooo.oxo.excited.database;

import android.content.ContentValues;
import android.database.Cursor;

import ooo.oxo.excited.App;
import ooo.oxo.excited.model.Card;

public class CardConverter {

    public static Card createCard(Cursor cursor) {
        Card card = new Card();
        card.id = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.ID));
        card.link = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.LINK));
        card.authorName = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.AUTHOR_NAME));
        card.cover = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.COVER));
        card.description = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.DESCRIPTION));
        card.distance = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.DISTANCE));
        card.headIcon = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.HEAD_ICON));
        card.headName = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.HEAD_NAME));
        card.ratio = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.RATIO));
        card.refined = cursor.getInt(cursor.getColumnIndex(FavSQLiteHelper.Fav.REFINED)) == 1;
        card.remains = cursor.getInt(cursor.getColumnIndex(FavSQLiteHelper.Fav.REMAINS));
        card.source = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.SOURCE));
        card.timestamp = cursor.getInt(cursor.getColumnIndex(FavSQLiteHelper.Fav.TIMESTAMP));
        card.title = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.TITLE));
        card.type = cursor.getString(cursor.getColumnIndex(FavSQLiteHelper.Fav.TYPE));
        card.sum = cursor.getInt(cursor.getColumnIndex(FavSQLiteHelper.Fav.SUM));
        return card;
    }

    public static ContentValues createContentValues(Card card, String userId, String token) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavSQLiteHelper.Fav.ID, card.id);
        contentValues.put(FavSQLiteHelper.Fav.USER_ID, userId);
        contentValues.put(FavSQLiteHelper.Fav.TOKEN, token);
        contentValues.put(FavSQLiteHelper.Fav.LINK, card.link);
        contentValues.put(FavSQLiteHelper.Fav.AUTHOR_NAME, card.authorName);
        contentValues.put(FavSQLiteHelper.Fav.COVER, card.cover);
        contentValues.put(FavSQLiteHelper.Fav.DESCRIPTION, card.description);
        contentValues.put(FavSQLiteHelper.Fav.DISTANCE, card.distance);
        contentValues.put(FavSQLiteHelper.Fav.HEAD_ICON, card.headIcon);
        contentValues.put(FavSQLiteHelper.Fav.HEAD_NAME, card.headName);
        contentValues.put(FavSQLiteHelper.Fav.RATIO, card.ratio);
        contentValues.put(FavSQLiteHelper.Fav.REFINED, card.refined);
        contentValues.put(FavSQLiteHelper.Fav.REMAINS, card.remains);
        contentValues.put(FavSQLiteHelper.Fav.SOURCE, card.source);
        contentValues.put(FavSQLiteHelper.Fav.UUID, card.uuid);
        contentValues.put(FavSQLiteHelper.Fav.TIMESTAMP, card.timestamp);
        contentValues.put(FavSQLiteHelper.Fav.TYPE, card.type);
        contentValues.put(FavSQLiteHelper.Fav.TITLE, card.title);
        contentValues.put(FavSQLiteHelper.Fav.SUM, card.sum);
        return contentValues;
    }
}
