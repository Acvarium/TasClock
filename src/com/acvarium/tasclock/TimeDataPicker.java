package com.acvarium.tasclock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;

public class TimeDataPicker extends Activity implements OnClickListener{
	private TimePicker timePickerStart, timePickerEnd;
	private DatePicker datePickerStart, datePickerEnd;
	private Button timeEditStartBtn, timeEditEndBtn, dateEditStartBtn, dateEditEndBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_picker);
		
		
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
		timePickerStart.setCurrentHour(15);
		timePickerStart.setCurrentMinute(15);
		
		datePickerEnd = (DatePicker) findViewById(R.id.datePicker2);
		timePickerEnd = (TimePicker) findViewById(R.id.timePicker2);
		timePickerEnd.setIs24HourView(true);
		timePickerEnd.setCurrentHour(15);
		timePickerEnd.setCurrentMinute(15);
		
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
		default:
			break;
		}
		
	}

}
