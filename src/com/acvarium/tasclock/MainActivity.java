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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	final String LOG_TAG = "myLogs";
	private ImageButton addBtn, removeBtn, editBtn, arrowUbBtn, arrowDownBtn;
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
		arrowUbBtn = (ImageButton) findViewById(R.id.arrow_up);
		arrowDownBtn = (ImageButton) findViewById(R.id.arrow_down);

		addBtn.setOnClickListener(this);
		removeBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		arrowUbBtn.setOnClickListener(this);
		arrowDownBtn.setOnClickListener(this);

		removeBtn.setOnLongClickListener(this);
		addBtn.setOnLongClickListener(this);

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
				listAdapter.notifyDataSetChanged();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_item,
						null);
			}
			TextView label, time;
			LinearLayout selector1, l1;
			RelativeLayout bg_view;
			ImageButton play_ib;
			int playColor;
			play_ib = (ImageButton) convertView
					.findViewById(R.id.play_imageButton);

			play_ib.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startStopTiming(position);
				}
			});

			selector1 = (LinearLayout) convertView
					.findViewById(R.id.llselector1);

			label = (TextView) convertView.findViewById(R.id.title);
			label.setText(tpTasks.elementAt(position).getLabel());
			time = (TextView) convertView.findViewById(R.id.title2);
			bg_view = (RelativeLayout) convertView.findViewById(R.id.bg_view);
			l1 = (LinearLayout) convertView.findViewById(R.id.l1);
			playColor = (R.color.sgray);

			if (sElenetPosition == position) {
				playColor = (R.color.selected_task);
				play_ib.setVisibility(View.VISIBLE);
				l1.setBackgroundResource(R.color.selected_task);

			} else {
				play_ib.setVisibility(View.GONE);
				l1.setBackgroundResource(R.color.sgray);
			}
			if (tpTasks.elementAt(position).getStatus() > 1000) {
				play_ib.setVisibility(View.VISIBLE);
				play_ib.setImageResource(R.drawable.stop);
				playColor = (R.color.sbcolor);
				long t = tpTasks.elementAt(position).getPeriod()
						+ (System.currentTimeMillis() - tpTasks.elementAt(
								position).getStatus());
				time.setText(timeToString(t));
				time.setTextAppearance(getApplicationContext(),
						R.style.boldText);
				label.setTextAppearance(getApplicationContext(),
						R.style.boldText);
			} else {
				play_ib.setImageResource(R.drawable.play);
				time.setText(timeToString(tpTasks.elementAt(position)
						.getPeriod()));
				time.setTextAppearance(getApplicationContext(),
						R.style.normalText);
				label.setTextAppearance(getApplicationContext(),
						R.style.normalText);
			}

			selector1.setBackgroundResource(playColor);
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
				tpTasks.elementAt(sElenetPosition).setPeriod(
						c.getLong(stColIndex));
				tpTasks.elementAt(sElenetPosition).setStatus(
						c.getLong(statusColIndex));
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
					String ss = tpTasks.elementAt(sElenetPosition).getLabel();
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
				cv.clear();
				cv.put("name", name);
				cv.put("sumoftp", 0);
				cv.put("ordernum", tpTasks.size());
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
		Cursor c = db
				.query(NameTable, null, null, null, null, null, "ordernum");
		// ставимо позицію курсора на першу строку виборки
		// якщо в виборці немає строк, то false
		if (c.moveToFirst()) {
			// визначаємо номер стовбця по виборці
			int idCI = c.getColumnIndex("id");
			int nameCI = c.getColumnIndex("name");
			int sumCI = c.getColumnIndex("sumoftp");
			int statusCI = c.getColumnIndex("status");
			int commentCI = c.getColumnIndex("comments");
			int orderNumCI = c.getColumnIndex("ordernum");
			do {
				tpTasks.add(new tpTask(c.getString(nameCI), c
						.getLong(sumCI)));
				tpTasks.lastElement().setStatus(c.getLong(statusCI));
				tpTasks.lastElement().setOrderNum(c.getInt(orderNumCI));
				tpTasks.lastElement().setComment(c.getString(commentCI));
				listAdapter.add(tpTasks.lastElement());
			} while (c.moveToNext());
		} else
			Log.d(LOG_TAG, "0 rows");
		c.close();
		listAdapter.notifyDataSetChanged();
	}
	
	private void swichTwoTasks(int a, int b){
		tpTask t = tpTasks.elementAt(a);
		ContentValues cv = new ContentValues();
		cv.put("ordernum", b);
		db.update(NameTable, cv, "name = ?",new String[] { tpTasks.elementAt(a).getLabel() });
		cv.clear();
		cv.put("ordernum", a);
		db.update(NameTable, cv, "name = ?",new String[] { tpTasks.elementAt(b).getLabel() });
		tpTasks.set(a, tpTasks.elementAt(b));
		tpTasks.set(b, t);

	}

	private void startStopTiming(int position) {
		long t = (System.currentTimeMillis());
		ContentValues cv = new ContentValues();
		if (tpTasks.elementAt(position).getStatus() > 1000) {
			cv.put("end", t);
			db.update(NameSTable, cv, "start = ?", new String[] { String
					.valueOf(tpTasks.elementAt(position).getStatus()) });
			cv.clear();
			long p = tpTasks.elementAt(position).getPeriod();
			long s = tpTasks.elementAt(position).getStatus();
			tpTasks.elementAt(position).setPeriod(p + (t - s));
			tpTasks.elementAt(position).setStatus(0);

			cv.put("sumoftp", tpTasks.elementAt(position).getPeriod());
			cv.put("status", 0);
			db.update(NameTable, cv, "name = ?", new String[] { tpTasks
					.elementAt(position).getLabel() });
			cv.clear();
		} else {
			tpTasks.elementAt(position).setStatus(t);
			cv.put("start", t);
			cv.put("end", 0);
			cv.put("name", tpTasks.elementAt(position).getLabel());
			db.insert(NameSTable, null, cv);
			cv.clear();

			cv.put("status", t);
			db.update(NameTable, cv, "name = ?", new String[] { tpTasks
					.elementAt(position).getLabel() });
			cv.clear();
		}
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_button:
			Intent intent = new Intent(this, AddTask.class);
			startActivityForResult(intent, 1);

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
			sElenetPosition = -1;
			break;
		case R.id.edit_button:
			if (sElenetPosition >= 0) {
				editLabel(sElenetPosition);
			}
			break;
		case R.id.arrow_up:
			if (sElenetPosition > 0) {
				swichTwoTasks(sElenetPosition, sElenetPosition - 1);
				sElenetPosition -= 1;
			}

			break;
		case R.id.arrow_down:
			if ((sElenetPosition >= 0)
					&& (sElenetPosition != (tpTasks.size() - 1))) {
				swichTwoTasks(sElenetPosition, sElenetPosition + 1);
				sElenetPosition += 1;
			}
			break;

		default:
			break;
		}
		listAdapter.notifyDataSetChanged();
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
			sElenetPosition = -1;
			tpTasks.clear();
			listAdapter.clear();
			break;
		case R.id.add_button:
			Log.d(LOG_TAG, "--- Rows in mytable: ---");
			// Робимо запрос всіх даинх з таблиці, получаємо Cursor
			Cursor c = db.query(NameTable, null, null, null, null, null, "ordernum");
			// ставимо позицію курсора на першу строку виборки
			// якщо в виборці немає строк, то false
			if (c.moveToFirst()) {

				// визначаємо номер стовбця по виборці
				int idCI = c.getColumnIndex("id");
				int nameCI = c.getColumnIndex("name");
				int sumCI = c.getColumnIndex("sumoftp");
				int statusCI = c.getColumnIndex("status");
				int commentCI = c.getColumnIndex("comments");
				int orderNumCI = c.getColumnIndex("ordernum");
				do {
					// отримуємо значення по номерам стовбців і пишемо все в лог
					Log.d(LOG_TAG,
							"ID = " + c.getInt(idCI) + ", name = "
									+ c.getString(nameCI) + ", status = "
									+ c.getString(sumCI) + ", time = "
									+ c.getLong(statusCI) + ", comment = "
									+ c.getString(commentCI) + ", orderNum = "
									+ c.getInt(orderNumCI));
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
		return false;
	}

}