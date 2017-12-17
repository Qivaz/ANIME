package io.qivaz.anime.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

import io.qivaz.anime.db.AnimeSQLiteOpenHelper;

/**
 * @author Qinghua Zhang @create 2017/3/25.
 */
public class AnimeContentProvider extends ContentProvider {
    public static final String URI = "ANIME_DB_URI";
    private static final String DATABASE_NAME = "_anime0.db3";
    private SQLiteOpenHelper mSQLiteHelper;
    private Context mContext;

    AnimeContentProvider(Context context) {
        mContext = context;
    }

    @Override
    public boolean onCreate() {
        mSQLiteHelper = new AnimeSQLiteOpenHelper(mContext, DATABASE_NAME, null, 0);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
