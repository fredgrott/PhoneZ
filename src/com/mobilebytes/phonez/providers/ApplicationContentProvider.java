package com.mobilebytes.phonez.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationContentProvider.
 */
public class ApplicationContentProvider extends ContentProvider {

	/** The Constant CONTENT_URI. */
	public static final Uri CONTENT_URI = Uri.parse("content://com.mobilebytes.phonez.RecentApplications");

	/** The PACKAGE. */
	public static  String PACKAGE = "package";

	/** The CLASS. */
	public static  String CLASS = "class";

	/** The DATE. */
	public static  String DATE = "date";

	/** The FLAGS. */
	public static  String FLAGS = "flags";

	/**
	 * The Class DatabaseHelper.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Instantiates a new database helper.
         *
         * @param context the context
         */
        public DatabaseHelper(Context context) {
            super(context, "applications.db", null, 1);
        }

        /* (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
         */
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

        /* (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

	/** The m open helper. */
	private com.mobilebytes.phonez.providers.ApplicationContentProvider.DatabaseHelper mOpenHelper;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.dir/recentApplicatons";
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
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

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query("applications", null, null, null, null, null, "date desc");
		return c;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}