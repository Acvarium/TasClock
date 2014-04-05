package com.acvarium.tasclock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TimesDB extends SQLiteOpenHelper {
	final String NameTable = "tasknames";
	final String NameSTable = "tasks_timing";
	final String LOG_TAG = "myLogs";
	final static int DB_VERSION = 2;
	
	public TimesDB(Context context) {
		// конструктор суперкласу
		super(context, "db", null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "--- onCreate database ---");
		// Створюємо таблицю з полями
		// id - порядковий номер елемента
		// name - назва завдання
		// sumoftp - сумарний підрахований час
		// comments - коментар до завдання
		db.execSQL("create table " + NameTable + " ("
				+ "id integer primary key autoincrement," + " name text,"
				+ " sumoftp integer," + " comments text," + " status integer " +  ");");
		db.execSQL("create table " + NameSTable + " ("
				+ "id integer primary key autoincrement," + "name text,"
				+ "start integer," + "end integer" + ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "--- onUpdate database ---");
		if (oldVersion == 1 && newVersion == 2) {
			db.execSQL("ALTER TABLE " + NameTable + " ADD COLUMN " + "status integer");				
		}
		db.execSQL("DROP TABLE IF EXISTS " + NameTable);
		db.execSQL("DROP TABLE IF EXISTS " + NameSTable);
		onCreate(db);

	}

}
