package com.acvarium.tasclock;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	final String LOG_TAG = "myLogs";
	private SharedPreferences sPref;
	private ImageButton addBtn, removeBtn;
	private ListView list;
	private ArrayAdapter<String> listAdapter;
	private boolean removeButtonState = false;
	private int tpnum;
	private int editPosition;
	private Editor ed;
	private String[] ids;
	private Vector<tpTask> tpTasks = new Vector<tpTask>();

	private boolean startStop = false;
	private int sElenetPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sPref = getSharedPreferences("main_pref", Activity.MODE_PRIVATE);
		ed = sPref.edit();

		addBtn = (ImageButton) findViewById(R.id.add_button);
		removeBtn = (ImageButton) findViewById(R.id.remove_button);
		addBtn.setOnClickListener(this);
		removeBtn.setOnClickListener(this);

		list = (ListView) findViewById(R.id.lvMain);

		// Creating the list adapter and populating the list
		listAdapter = new CustomListAdapter(this, R.layout.list_item);

		readData();
		list.setAdapter(listAdapter);

		// Creating an item click listener, to open/close our toolbar for each
		// item

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {

				if (removeButtonState) {
					listAdapter.remove(listAdapter.getItem(position));

					SharedPreferences TimeDataFile;
					TimeDataFile = getSharedPreferences(
							tpTasks.elementAt(position).getId(),
							Activity.MODE_PRIVATE);
					Editor clearFile = TimeDataFile.edit();
					clearFile.clear();
					clearFile.commit();
					Log.d(LOG_TAG,
							"Clear data of "
									+ tpTasks.elementAt(position).getId());
					tpTasks.remove(position);

					removeBtn.setBackgroundResource(R.drawable.buttonshape);
					removeButtonState = false;
					tpnum = tpTasks.size();
					saveData();
				}

			}
		});

		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Log.d(LOG_TAG, tpTasks.elementAt(position).getLabel()
						+ "  Hesh = " + tpTasks.elementAt(position).getId());
				workTimeAct(tpTasks.elementAt(position).getId());
				return true;
			}
		});
	}

	private void workTimeAct(String name) {
		Intent intent = new Intent(this, TimingActivity.class);
		intent.putExtra("name", name);

		startActivity(intent);
	}

	private void editLabel(int position) {
		editPosition = position;
		Intent intent = new Intent(this, AddTask.class);
		intent.putExtra("name", tpTasks.elementAt(position).getLabel());
		intent.putExtra("edit", true);
		startActivityForResult(intent, 1);
	}

	private void readData() {
		listAdapter.clear();
		tpnum = sPref.getInt("tpnum", 0);
		tpTasks.clear();
		ids = new String[tpnum];

		for (int i = 0; i < tpnum; i++) {
			ids[i] = sPref.getString("ids" + i, null);
			String la = sPref.getString(ids[i] + "_lab", null);
			tpTasks.add(new tpTask(la, ids[i]));
			listAdapter.add(la);
		}
	}

	private void saveData() {
		Log.d(LOG_TAG, "saving data");
		ed.putInt("tpnum", tpnum);
		for (int i = 0; i < tpnum; i++) {
			ed.putString("ids" + i, tpTasks.elementAt(i).getId());
			ed.putString(tpTasks.elementAt(i).getId() + "_lab", tpTasks
					.elementAt(i).getLabel());
			Log.d(LOG_TAG, "ids" + i + " " + tpTasks.elementAt(i).getId());
		}

		ed.commit();
	}

	/**
	 * A simple implementation of list adapter.
	 */
	class CustomListAdapter extends ArrayAdapter<String> {

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


			((TextView) convertView.findViewById(R.id.title)).setText(tpTasks
					.elementAt(position).getLabel());
			((TextView) convertView.findViewById(R.id.title2)).setText(tpTasks
					.elementAt(position).getId());

			return convertView;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String name = data.getStringExtra("name");
		Boolean editstate = data.getBooleanExtra("edit", false);

		if (name.length() != 0) {
			if (editstate) {
				tpTasks.elementAt(editPosition).setLabel(name);
				listAdapter.notifyDataSetChanged();
				// Save changes
				ed.putString("ids" + editPosition,
						tpTasks.elementAt(editPosition).getId());
				ed.putString(tpTasks.elementAt(editPosition).getId() + "_lab",
						tpTasks.elementAt(editPosition).getLabel());
				ed.commit();
			} else {
				tpTasks.add(new tpTask(name, Integer.toHexString(name
						.hashCode())));
				listAdapter.add(name);
				tpnum = tpTasks.size();

			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_button:
			removeButtonState = false;
			removeBtn.setBackgroundResource(R.drawable.buttonshape);
			Intent intent = new Intent(this, AddTask.class);
			startActivityForResult(intent, 1);
			// list.setAdapter(listAdapter);

			break;
		case R.id.remove_button:
			if (removeButtonState) {
				removeBtn.setBackgroundResource(R.drawable.buttonshape);
				removeButtonState = false;
			} else {
				removeBtn.setBackgroundResource(R.drawable.stopbuttonshape);
				removeButtonState = true;
			}

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
		saveData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveData();
	}

}