package com.acvarium.tasclock;

import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	final String LOG_TAG = "myLogs";
	private ImageButton addBtn, removeBtn, editBtn, playBtn, settingsBtn;
	private ListView list;
	private ArrayAdapter<tpTask> listAdapter;
	private Vector<tpTask> tpTasks = new Vector<tpTask>();
	final String NameTable = "tasknames";
	final String NameSTable = "tasks_timing";

	private int sElenetPosition;

	private NamesDB dbHelper;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		dbHelper = new NamesDB(this);
		db = dbHelper.getWritableDatabase();
		sElenetPosition = -1;

		addBtn = (ImageButton) findViewById(R.id.add_button);
		removeBtn = (ImageButton) findViewById(R.id.remove_button);
		editBtn = (ImageButton) findViewById(R.id.edit_button);
		playBtn = (ImageButton) findViewById(R.id.play_button);
		settingsBtn = (ImageButton) findViewById(R.id.settings_button);

		addBtn.setOnClickListener(this);
		removeBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		removeBtn.setOnLongClickListener(this);
		settingsBtn.setOnClickListener(this);
		
		list = (ListView) findViewById(R.id.lvMain);

		listAdapter = new CustomListAdapter(this, R.layout.list_item);
		list.setAdapter(listAdapter);
		readData();
		listAdapter.notifyDataSetChanged();

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				sElenetPosition = position;
				Log.d(LOG_TAG, "Selected element " + sElenetPosition);
			}
		});

		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				sElenetPosition = position;
				workTimeAct(tpTasks.elementAt(position).getLabel());
				return true;
			}
		});
	}

	private void workTimeAct(String name) {	
		Intent intent = new Intent(this, TimingActivity.class);
		intent.putExtra("name", name);
		startActivityForResult(intent,1);
	}

	class CustomListAdapter extends ArrayAdapter<tpTask> {

		public CustomListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_item,
						null);
			}
			TextView label, time;
			label = (TextView) convertView.findViewById(R.id.title);
			label.setText(getItem(position).getLabel());
			time = (TextView) convertView.findViewById(R.id.title2);
			time.setText(timeToString(getItem(position).getPeriod()));
			return convertView;
		}
	}

	private void editLabel(int position) {
		Intent intent = new Intent(this, AddTask.class);
		intent.putExtra("name", tpTasks.elementAt(position).getLabel());
		intent.putExtra("edit", true);
		startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onActivityResult. Selected = " + sElenetPosition);
		if (data == null) {
			return;
		}
		String name = data.getStringExtra("name");
		Boolean editstate = data.getBooleanExtra("edit", false);
		long time = data.getLongExtra("time", 0);
		ContentValues cv = new ContentValues();
		if (name.length() > 0) {
			if (editstate) {
				if (sElenetPosition >= 0) {
					Log.d(LOG_TAG, "--- Update mytabe: ---");
					// Підготовка значення для обновлення
					cv.put("name", name);
					cv.put("sumoftp", 0);
					cv.put("comments", "");
					String ss = listAdapter.getItem(sElenetPosition).getLabel();
					int updCount = db.update(NameTable, cv, "name = ?",
							new String[] { ss });
					ContentValues cvS = new ContentValues();
					cvS.put("name", name);
					int updCountS = db.update(NameSTable, cvS, "name = ?",
							new String[] { ss });
					Log.d(LOG_TAG, "updated rows count = " + updCount);
					tpTasks.elementAt(sElenetPosition).setLabel(name);
				}
			} else {
				cv.put("name", name);
				cv.put("sumoftp", 0);
				cv.put("comments", "");
				long rowID = db.insert(NameTable, null, cv);
				tpTasks.add(new tpTask(name, 0));
				listAdapter.add(tpTasks.lastElement());
				Log.d(LOG_TAG, "row inserted, ID = " + rowID);
			}
		}
		if(time > 0){
			Log.d(LOG_TAG, "Rewrote time for element " + sElenetPosition);
			tpTasks.elementAt(sElenetPosition).setLabel("333");
		}
		sElenetPosition = -1;
		listAdapter.notifyDataSetChanged();
		cv.clear();
		Log.d(LOG_TAG, "return from onActivityResult ");
	}
	
	
	private String timeToString(long time) {
		time = time / 1000;
		String ss = String.format("%02d:%02d:%02d", time / 3600,(time % 3600) / 60, (time % 60), Locale.US);
		return ss;
	}

	private void readData() {

		listAdapter.clear();
		tpTasks.clear();
		Log.d(LOG_TAG, "--- Read data: ---");
		// Робимо запрос всіх даинх з таблиці, получаємо Cursor
		Cursor c = db.query(NameTable, null, null, null, null, null, null);

		// ставимо позицію курсора на першу строку виборки
		// якщо в виборці немає строк, то false
		if (c.moveToFirst()) {
			// визначаємо номер стовбця по виборці
			int idColIndex = c.getColumnIndex("id");
			int nameColIndex = c.getColumnIndex("name");
			int stColIndex = c.getColumnIndex("sumoftp");
			int commentColIndex = c.getColumnIndex("comments");

			do {
				tpTasks.add(new tpTask(c.getString(nameColIndex), c
						.getLong(stColIndex)));
				listAdapter.add(tpTasks.lastElement());
			} while (c.moveToNext());
		} else
			Log.d(LOG_TAG, "0 rows");
		c.close();
		listAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_button:
			Intent intent = new Intent(this, AddTask.class);
			startActivityForResult(intent, 1);

			break;
		case R.id.settings_button:
			Intent intent2 = new Intent(this, TimeDataPicker.class);
			startActivityForResult(intent2, 1);

			break;
		case R.id.remove_button:
			if (sElenetPosition >= 0) {
				int clearCount = db.delete(NameTable, "name = ?",
						new String[] { tpTasks.elementAt(sElenetPosition)
								.getLabel() });
				int clearCountS = db.delete(NameSTable, "name = ?",
						new String[] { tpTasks.elementAt(sElenetPosition)
								.getLabel() });
				readData();
			}

			break;
		case R.id.edit_button:
			if (sElenetPosition >= 0) {
				editLabel(sElenetPosition);
			}
			break;
		case R.id.play_button:
			Log.d(LOG_TAG, "--- Rows in mytable: ---");
			// Робимо запрос всіх даинх з таблиці, получаємо Cursor
			Cursor c = db.query(NameTable, null, null, null, null, null, null);
			// ставимо позицію курсора на першу строку виборки
			// якщо в виборці немає строк, то false
			if (c.moveToFirst()) {

				// визначаємо номер стовбця по виборці
				int idColIndex = c.getColumnIndex("id");
				int nameColIndex = c.getColumnIndex("name");
				int stColIndex = c.getColumnIndex("sumoftp");
				int commentColIndex = c.getColumnIndex("comments");

				do {
					// отримуємо значення по номерам стовбців і пишемо все в лог
					Log.d(LOG_TAG,
							"ID = " + c.getInt(idColIndex) + ", name = "
									+ c.getString(nameColIndex) + ", time = "
									+ c.getLong(stColIndex) + ", comment = "
									+ c.getString(commentColIndex));
					// перехід на наступну строку
					// а якщо наступної нема (поточна остання), то false -
					// виходимо з циклу
				} while (c.moveToNext());
			} else
				Log.d(LOG_TAG, "0 rows");
			c.close();

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.remove_button:
			Log.d(LOG_TAG, "--- Clear mytable: ---");
			// Видаляємо всі записи
			int clearCount = db.delete(NameTable, null, null);
			int clearCountS = db.delete(NameSTable, null, null);
			
			Log.d(LOG_TAG, "deleted rows count = " + clearCount);
			tpTasks.clear();
			listAdapter.clear();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
		//dbHelper.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// Робота з базою данних
	class NamesDB extends SQLiteOpenHelper {

		public NamesDB(Context context) {
			// конструктор суперкласу
			super(context, "db", null, 1);
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
					+ "id integer primary key autoincrement," + "name text,"
					+ "sumoftp integer," + "comments text" + ");");
			db.execSQL("create table " + NameSTable + " ("
					+ "id integer primary key autoincrement," + "name text,"
					+ "start integer," + "end integer" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(LOG_TAG, "--- onUpdate database ---");
			db.execSQL("DROP TABLE IF EXISTS " + NameTable);
			db.execSQL("DROP TABLE IF EXISTS " + NameSTable);
			onCreate(db);
		}
	}

}