package com.acvarium.tasclock;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddTask extends Activity implements OnClickListener {

	private EditText etName;
	private Button btnOK,cancelBtn;
	private Intent intent;
	private boolean editState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtask_layout);

		
		Intent intent = getIntent();

		editState = intent.getBooleanExtra("edit",false);
		String eName = intent.getStringExtra("name");
		etName = (EditText) findViewById(R.id.etName);
		if(editState){
			etName.setText(eName);
		}
		btnOK = (Button) findViewById(R.id.add_ok_button);
		cancelBtn = (Button)findViewById(R.id.cansel_button);
		cancelBtn.setOnClickListener(this);
		btnOK.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_ok_button:
			intent = new Intent();
			intent.putExtra("name", etName.getText().toString());
			if(editState)
				intent.putExtra("edit",true);
			setResult(RESULT_OK, intent);
			finish();			
			break;
		case R.id.cansel_button:	

			finish();
			break;
		default:
			break;
		}

	}
}
