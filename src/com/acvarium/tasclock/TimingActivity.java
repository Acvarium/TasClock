package com.acvarium.tasclock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TimingActivity extends Activity implements OnClickListener {
	private long c = System.currentTimeMillis();
	private boolean startStop = false;
	private Button startBtn;
	private Button editBtn;
	private Button resetBtn;
	private long startTime, endTime;
	private TextView mainTV, dataTV;
	private String tpID = "T1";
	private TimePeriods timePeriods;
	private SharedPreferences sPref;
	private Editor ed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timing);

		Intent intent = getIntent();

		tpID = intent.getStringExtra("name");
		
		sPref = getSharedPreferences(tpID, Activity.MODE_PRIVATE);

		timePeriods = new TimePeriods(tpID);

		startBtn = (Button) findViewById(R.id.start_button);
		editBtn = (Button) findViewById(R.id.edit_button);
		resetBtn = (Button) findViewById(R.id.reset_button);
		mainTV = (TextView) findViewById(R.id.mainTV);
		dataTV = (TextView) findViewById(R.id.dataTV);

		readData();
		rewriteScreen();

		startBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		resetBtn.setOnClickListener(this);
	}

	private void readData() {

		timePeriods.clear();
		long tpnum = sPref.getLong("tpnum", 0);
		for (int i = 0; i < tpnum; i++) {
			startTime = sPref.getLong(String.valueOf(tpID + "_s_" + i), 0);
			endTime = sPref.getLong(String.valueOf(tpID + "_e_" + i), 0);

			timePeriods.add(startTime, endTime);
		}
	}

	private void rewriteScreen() {
		c = timePeriods.getSumOfAllPeriods();
		String ss = String.format("%02d:%02d:%02d", c / 3600, (c % 3600) / 60,
				(c % 60));
		mainTV.setText(ss);
		ss = "";
		for (int i = 0; i < timePeriods.getQuantity(); i++) {
			c = timePeriods.getSumOfPeriod(i);
			ss = ss
					+ String.format("%02d:%02d:%02d", c / 3600,
							(c % 3600) / 60, (c % 60)) + "\n";
		}
		dataTV.setText(ss);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_button:

			if (startStop) { // --STOP---
				startStop = false;
				endTime = ((System.currentTimeMillis()) / 1000);
				timePeriods.add(startTime, endTime);
				rewriteScreen();
				startBtn.setText("START");
				startBtn.setBackgroundResource(R.drawable.startbuttonshape);

			} else { // --START---
				startStop = true;
				startTime = ((System.currentTimeMillis()) / 1000);
				startBtn.setText("STOP");
				startBtn.setBackgroundResource(R.drawable.stopbuttonshape);
			}

			break;
		case R.id.edit_button:

			break;
		case R.id.reset_button:
			ed = sPref.edit();
			ed.clear();
			ed.commit();
			timePeriods.clear();
			rewriteScreen();
			break;

		default:
			break;
		}

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
		//closeAndSave();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeAndSave();
	}

	private void closeAndSave() {
		if (startStop) { // --STOP---
			endTime = ((System.currentTimeMillis()) / 1000);
			timePeriods.add(startTime, endTime);
		}
		ed = sPref.edit();
		timePeriods.saveData(ed);
		ed.commit();
	}

}
