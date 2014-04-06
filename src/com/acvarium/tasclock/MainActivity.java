package com.acvarium.tasclock;

import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
	private Handler myHandler = new Handler();
	private TimesDB timesDB;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		timesDB = new TimesDB(this);
		db = timesDB.getWritableDatabase();
		Log.d(LOG_TAG, "Database version " + db.getVersion());
		
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
		myHandler.postDelayed(updateTimerMethod, 0);
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
		startActivityForResult(intent, 1);
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
			LinearLayout bg_view;
			
			label = (TextView) convertView.findViewById(R.id.title);
			label.setText(getItem(position).getLabel());
			time = (TextView) convertView.findViewById(R.id.title2);
			
			bg_view = (LinearLayout)convertView.findViewById(R.id.bg_view);
			if(getItem(position).getStatus() > 1000){
				bg_view.setBackgroundResource(R.color.sbcolor);
				long t = getItem(position).getPeriod() + (System.currentTimeMillis() - getItem(position).getStatus());
				time.setText(timeToString(t));
			}else{
				bg_view.setBackgroundResource(R.color.unselected_task);
				time.setText(timeToString(getItem(position).getPeriod()));		
			}
			return convertView;
		}
	}
	
	
	private Runnable updateTimerMethod = new Runnable() {

		public void run() {
			listAdapter.notifyDataSetChanged();
			myHandler.postDelayed(this, 1000);
		}

	};

	private void editLabel(int position) {
		Intent intent = new Intent(this, AddTask.class);
		intent.putExtra("name", tpTasks.elementAt(position).getLabel());
		intent.putExtra("edit", true);
		startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onActivityResult. Selected = " + sElenetPosition);
		if (sElenetPosition >= 0) {
			String resetLabel = tpTasks.elementAt(sElenetPosition).getLabel();
			Cursor c = db.query(NameTable, null, "name = ?",
					new String[] { resetLabel }, null, null, null);
			if (c.moveToFirst()) {
				int stColIndex = c.getColumnIndex("sumoftp");
				int statusColIndex = c.getColumnIndex("status");
				tpTasks.elementAt(sElenetPosition).setPeriod(c.getLong(stColIndex));
				tpTasks.elementAt(sElenetPosition).setStatus(c.getLong(statusColIndex));
				c.close();
			}
			listAdapter.notifyDataSetChanged();
		}

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
		if (time > 0) {
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
		String ss = String.format("%02d:%02d:%02d", time / 3600,
				(time % 3600) / 60, (time % 60), Locale.US);
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
			int statusColIndex = c.getColumnIndex("status");
			int commentColIndex = c.getColumnIndex("comments");
			do {
				tpTasks.add(new tpTask(c.getString(nameColIndex), c
						.getLong(stColIndex)));
				tpTasks.lastElement().setPeriod(c.getLong(stColIndex));
				tpTasks.lastElement().setStatus(c.getLong(statusColIndex));
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
				int statusColIndex = c.getColumnIndex("status");
				int commentColIndex = c.getColumnIndex("comments");

				do {
					// отримуємо значення по номерам стовбців і пишемо все в лог
					Log.d(LOG_TAG,
							"ID = " + c.getInt(idColIndex) + ", name = "
									+ c.getString(nameColIndex) + ", status = "
									+ c.getString(statusColIndex) + ", time = "
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

}