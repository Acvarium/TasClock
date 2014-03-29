package com.acvarium.tasclock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
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

public class TimingActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	private ImageButton startBtn, editBtn, resetBtn;
	private TextView mainTV;
	private Handler myHandler = new Handler();
	private ListView list;
	private ArrayAdapter<String> listAdapter;
	private String tpID = "T1";
	private TimePeriods timePeriods;
	private SharedPreferences sPref;
	private Calendar cal;
	private Editor ed;
	private int sElenetPosition = -1;

	private SimpleDateFormat timeFormat;
	private SimpleDateFormat dateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.timing);

		Intent intent = getIntent();

		tpID = intent.getStringExtra("name");

		sPref = getSharedPreferences(tpID, Activity.MODE_PRIVATE);

		timePeriods = new TimePeriods(tpID);

		timeFormat = new SimpleDateFormat("hh:mm:ss");
		dateFormat = new SimpleDateFormat("dd MM yyyy");
		cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		startBtn = (ImageButton) findViewById(R.id.start_button);
		editBtn = (ImageButton) findViewById(R.id.edit_button);
		resetBtn = (ImageButton) findViewById(R.id.reset_button);
		mainTV = (TextView) findViewById(R.id.mainTV);

		list = (ListView) findViewById(R.id.lvTimes);

		// Creating the list adapter and populating the list
		listAdapter = new CustomListAdapter(this, R.layout.list_time);

		readData();
		list.setAdapter(listAdapter);

		startBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		resetBtn.setOnClickListener(this);
		resetBtn.setOnLongClickListener(this);
		mainTV.setText(timeToString(timePeriods.getSumOfAllPeriods()));

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				sElenetPosition = position;

			}
		});

		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				return true;
			}
		});

	}

	private String timeToString(long time) {

		String ss = String.format("%02d:%02d:%02d", time / 3600,
				(time % 3600) / 60, (time % 60));
		return ss;
	}

	private String perionToString(int period) {
		long c = timePeriods.getSumOfPeriod(period);
		return timeToString(c);
	}

	private Runnable updateTimerMethod = new Runnable() {

		public void run() {
			mainTV.setText(timeToString(timePeriods.getSumOfAllPeriods()));
			myHandler.postDelayed(this, 1000);
		}

	};

	private void readData() {

		timePeriods.clear();
		long tpnum = sPref.getLong("tpnum", 0);
		for (int i = 0; i < tpnum; i++) {
			long startTime = sPref.getLong(String.valueOf(tpID + "_s_" + i), 0);
			long endTime = sPref.getLong(String.valueOf(tpID + "_e_" + i), 0);
			timePeriods.add(startTime, endTime);
			listAdapter.add("");
			listAdapter.notifyDataSetChanged();

		}
	}

	class CustomListAdapter extends ArrayAdapter<String> {

		public CustomListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			String ss = "";
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_time,
						null);
			}
			((TextView) convertView.findViewById(R.id.title))
					.setText(perionToString(position));

			cal.setTimeInMillis(timePeriods.getStartTime(position) * 1000);

			ss = timeFormat.format(cal.getTime());
			((TextView) convertView.findViewById(R.id.start_time_title))
					.setText(ss);
			ss = dateFormat.format(cal.getTime());
			((TextView) convertView.findViewById(R.id.start_date_title))
					.setText(ss);

			cal.setTimeInMillis(timePeriods.getEndTime(position) * 1000);
			ss = timeFormat.format(cal.getTime());
			((TextView) convertView.findViewById(R.id.end_time_title))
					.setText(ss);
			ss = dateFormat.format(cal.getTime());
			((TextView) convertView.findViewById(R.id.end_date_title))
					.setText(ss);

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_button:

			if (timePeriods.tpStarted) { // --STOP---

				timePeriods.stop();
				startBtn.setImageResource(R.drawable.play);
				startBtn.setBackgroundResource(R.drawable.buttonshape);
				myHandler.removeCallbacks(updateTimerMethod);
				listAdapter.add(String.valueOf(timePeriods.getSize()));
				listAdapter.notifyDataSetChanged();

			} else { // --START---

				timePeriods.start();
				startBtn.setImageResource(R.drawable.stop);
				startBtn.setBackgroundResource(R.drawable.stopbuttonshape);
				myHandler.postDelayed(updateTimerMethod, 0);
			}

			break;
		case R.id.edit_button:

			break;
		case R.id.reset_button:
			/*
			 * if (sElenetPosition >= 0) {
			 * listAdapter.remove(listAdapter.getItem(sElenetPosition));
			 * listAdapter.remove(listAdapter.getItem(sElenetPosition));
			 * listAdapter.notifyDataSetChanged();
			 * timePeriods.remove(sElenetPosition);
			 * 
			 * sElenetPosition = -1; }
			 */
			break;

		default:
			break;
		}

	}
	
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.reset_button:

			ed = sPref.edit();
			ed.clear();
			ed.commit();
			timePeriods.clear();
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
		// closeAndSave();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeAndSave();
	}

	private void closeAndSave() {
		if (timePeriods.tpStarted) { // --STOP---
			timePeriods.stop();
		}
		ed = sPref.edit();
		timePeriods.saveData(ed);
		ed.commit();
	}

}
