package com.acvarium.tasclock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TimePicker;

public class TimeDataPicker extends Activity implements OnClickListener{
	
	private TimePicker timePickerStart, timePickerEnd;
	private DatePicker datePickerStart, datePickerEnd;
	long startTime, endTime;
	private ImageButton okBtn;
	private Intent intent;
	
	private Calendar cal;
	private SimpleDateFormat timeFormat;
	private SimpleDateFormat dateFormat;

	final String LOG_TAG = "myLogs";
	private Button timeEditStartBtn, timeEditEndBtn, dateEditStartBtn, dateEditEndBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_picker);
		
		long t = (System.currentTimeMillis());

		intent = getIntent();
		startTime = intent.getLongExtra("startTime",t);
		endTime = intent.getLongExtra("endTime",t);

		timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
		dateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
		cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);

		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Start time");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("End time");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
		
		datePickerStart = (DatePicker) findViewById(R.id.datePicker);
		timePickerStart = (TimePicker) findViewById(R.id.timePicker);
		timePickerStart.setIs24HourView(true);
		
		datePickerEnd = (DatePicker) findViewById(R.id.datePicker2);
		timePickerEnd = (TimePicker) findViewById(R.id.timePicker2);
		timePickerEnd.setIs24HourView(true);

		
		timeEditStartBtn= (Button)findViewById(R.id.time_edit_start_button);
		timeEditEndBtn = (Button)findViewById(R.id.time_edit_end_button);
		dateEditStartBtn = (Button)findViewById(R.id.date_edit_start_button);
		dateEditEndBtn = (Button)findViewById(R.id.date_edit_end_button);
		
		timeEditEndBtn.setOnClickListener(this);
		timeEditStartBtn.setOnClickListener(this);
		dateEditStartBtn.setOnClickListener(this);
		dateEditEndBtn.setOnClickListener(this);
		timeEditEndBtn.setBackgroundResource(R.color.selected_task);
		timeEditStartBtn.setBackgroundResource(R.color.selected_task);
		
		okBtn = (ImageButton) findViewById(R.id.ok_button);
		okBtn.setOnClickListener(this);
		
		cal.setTimeInMillis(startTime);
		
		timePickerStart.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePickerStart.setCurrentMinute(cal.get(Calendar.MINUTE));
	
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH);
		int day=cal.get(Calendar.DAY_OF_MONTH);
		datePickerStart.updateDate(year, month, day);
		
		cal.setTimeInMillis(endTime);
		
		timePickerEnd.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePickerEnd.setCurrentMinute(cal.get(Calendar.MINUTE));
		year=cal.get(Calendar.YEAR);
		month=cal.get(Calendar.MONTH);
		day=cal.get(Calendar.DAY_OF_MONTH);
		datePickerEnd.updateDate(year, month, day);
	
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.time_edit_start_button:
			datePickerStart.setVisibility(View.GONE);
			timePickerStart.setVisibility(View.VISIBLE);
			timeEditStartBtn.setBackgroundResource(R.color.selected_task);
			dateEditStartBtn.setBackgroundResource(R.drawable.buttonshape);
			break;
		case R.id.date_edit_start_button:
			datePickerStart.setVisibility(View.VISIBLE);
			timePickerStart.setVisibility(View.GONE);
			timeEditStartBtn.setBackgroundResource(R.drawable.buttonshape);
			dateEditStartBtn.setBackgroundResource(R.color.selected_task);
			break;
			
		case R.id.time_edit_end_button:
			datePickerEnd.setVisibility(View.GONE);
			timePickerEnd.setVisibility(View.VISIBLE);
			timeEditEndBtn.setBackgroundResource(R.color.selected_task);
			dateEditEndBtn.setBackgroundResource(R.drawable.buttonshape);
			break;
		case R.id.date_edit_end_button:
			datePickerEnd.setVisibility(View.VISIBLE);
			timePickerEnd.setVisibility(View.GONE);
			timeEditEndBtn.setBackgroundResource(R.drawable.buttonshape);
			dateEditEndBtn.setBackgroundResource(R.color.selected_task);
			break;
			
		case R.id.ok_button:
			cal.setTimeInMillis(0);
			cal.set(Calendar.HOUR_OF_DAY,timePickerStart.getCurrentHour());
			cal.set(Calendar.MINUTE, timePickerStart.getCurrentMinute());
			cal.set(Calendar.YEAR, datePickerStart.getYear());
			cal.set(Calendar.MONTH, datePickerStart.getMonth());
			cal.set(Calendar.DAY_OF_MONTH, datePickerStart.getDayOfMonth());
			startTime = cal.getTimeInMillis();
			
			cal.set(Calendar.HOUR_OF_DAY,timePickerEnd.getCurrentHour());
			cal.set(Calendar.MINUTE, timePickerEnd.getCurrentMinute());
			cal.set(Calendar.YEAR, datePickerEnd.getYear());
			cal.set(Calendar.MONTH, datePickerEnd.getMonth());
			cal.set(Calendar.DAY_OF_MONTH, datePickerEnd.getDayOfMonth());
			endTime = cal.getTimeInMillis();
			
			intent = new Intent();
			intent.putExtra("edited", true);		
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			setResult(RESULT_OK, intent);
			Log.d(LOG_TAG, "Start Time = " + startTime + " End Time = " + endTime);
			finish();
			break;
		default:
			break;
		}
		
	}

}
