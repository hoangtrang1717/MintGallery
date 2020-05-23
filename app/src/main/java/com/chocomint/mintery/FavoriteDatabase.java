package com.chocomint.mintery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class FavoriteDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static class FavoriteTable implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_NAME_FILE_ID = "file_id";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FavoriteTable.TABLE_NAME + " (" +
                    FavoriteTable._ID + " INTEGER PRIMARY KEY," +
                    FavoriteTable.COLUMN_NAME_FILE_ID + " INTEGER," +
                    FavoriteTable.COLUMN_NAME_TYPE + " INTEGER)";

    public FavoriteDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.setVersion(i1);
    }

    public boolean insertFavorite(int id, int type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteTable.COLUMN_NAME_FILE_ID, id);
        contentValues.put(FavoriteTable.COLUMN_NAME_TYPE, type);

        long newRowId = db.insert(FavoriteTable.TABLE_NAME, null, contentValues);
        return newRowId != 0;
    }

    public boolean deleteFavorite(int id, int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FavoriteTable.COLUMN_NAME_FILE_ID + " = " + id + " AND " + FavoriteTable.COLUMN_NAME_TYPE + " = " + type;
        int deleteRows = db.delete(FavoriteTable.TABLE_NAME, selection, null);
        return deleteRows > 0;
    }

    public boolean readFavorite(int id, int type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FavoriteTable.TABLE_NAME, null, FavoriteTable.COLUMN_NAME_FILE_ID + " = " + id + " AND " + FavoriteTable.COLUMN_NAME_TYPE + " = " + type, null, null, null, null);
        return cursor.getCount() > 0;
    }
}
