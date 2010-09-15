package com.mobilebytes.phonez.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class ApplicationContentProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://com.mobilebytes.phonez.RecentApplications");

	public static  String PACKAGE = "package";
	public static  String CLASS = "class";
	public static  String DATE = "date";
	public static  String FLAGS = "flags";

	private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, "applications.db", null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE applications (" +
                    "_id INTEGER PRIMARY KEY" +
                    ",package TEXT" +
                    ",class TEXT" +
                    ",flags INTEGER" +
                    ",date LONG" +
                    ");";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

	private com.mobilebytes.phonez.providers.ApplicationContentProvider.DatabaseHelper mOpenHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.dir/recentApplicatons";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String[] whereArgs = {
				values.getAsString("package"),
				values.getAsString("class"),
		};
		int rows = db.update("applications", values, "package=? and class=?", whereArgs);
		if (rows == 0) {
			db.insert("applications", null, values);
		}
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);
		return null;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query("applications", null, null, null, null, null, "date desc");
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}