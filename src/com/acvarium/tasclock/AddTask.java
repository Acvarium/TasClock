package com.acvarium.tasclock;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddTask extends Activity implements OnClickListener {

	private EditText etName;
	private ImageButton btnOK;
	private Intent intent;
	private boolean editState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtask_layout);

		
		intent = getIntent();

		editState = intent.getBooleanExtra("edit",false);
		String eName = intent.getStringExtra("name");
		etName = (EditText) findViewById(R.id.etName);
		if(editState){
			etName.setText(eName);
		}
		btnOK = (ImageButton) findViewById(R.id.add_ok_button);
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

		default:
			break;
		}

	}
}
