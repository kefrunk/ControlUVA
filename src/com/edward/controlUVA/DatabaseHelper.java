package com.edward.controlUVA;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME ="diasDB.db";
	public static final String ON_OFF ="on_off";
	public static final String HORA="hora";
	public static final String R1="r1";
	public static final String R2="r2";
	public static final String AB1="ab1";
	public static final String AB2="ab2";
	public static final String SYS_ON="sys_on";
	public static final String AUTO_ON="auto_on";


	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE dias(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			" on_off TEXT, hora TEXT, r1 TEXT, r2 TEXT, ab1 TEXT, ab2 TEXT, sys_on TEXT, auto_on TEXT);";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE); 
	}
	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS dias");
		onCreate(db);
	}
}
