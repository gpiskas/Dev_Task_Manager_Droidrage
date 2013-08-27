/*
*  Dev Task Manager (Droidrage)
*  Copyright (C) 2013  George Piskas
*
*  This program is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License along
*  with this program; if not, write to the Free Software Foundation, Inc.,
*  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
*  Contact: geopiskas@gmail.com
*/

package ls.droidrage.manager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

// Activity Log. List of Database entries and ability to create new.
public class ActivityLog extends Activity {

	
	private ArrayList<String> timestamps;
	private ListView lvLog;
	private Button bRec;
	private ProgressBar bProg;
	// Handler to repeat db entry every 20 sec and refresh list ever 4 sec.
	private Handler dbWriteH, dbRefreshH;
	private boolean isLogging;
	private boolean enableButton;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.enableActionBar(this);
		setContentView(R.layout.activitylog);
		
		timestamps = new ArrayList<String>();
		enableButton = false;
		
		init();
		populateList();
	}
	// Function that updates the list items based on entry timestamps.
	private void populateList() {
		try {
			Database db = new Database(this);
			db.open();
			timestamps = db.getTimestamps();
			db.close();
			lvLog.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, timestamps));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Something went wrong..", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}
	// Item click listener for the list. Shows the entry contents from the database.
	private void setLvListener() {
		lvLog.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {

				Intent i = new Intent(ActivityLog.this, DbViewer.class);
				i.putExtra("timestamp", timestamps.get(pos));
				startActivity(i);
			}
		});

	}

	private void init() {
		isLogging = false;
		dbWriteH = new Handler();
		dbRefreshH = new Handler();
		lvLog = (ListView) findViewById(R.id.lvlog);
		setLvListener();
		bProg = (ProgressBar) findViewById(R.id.pbrec);
		bRec = (Button) findViewById(R.id.brec);
		// Toggle button start/stop logging.
		bRec.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLogging == false) {
					isLogging = true;
					bRec.setText("Stop Logging Data");
					bRec.setEnabled(false);
					bProg.setVisibility(View.VISIBLE);
					dbWrite.run();
					dbRefresh.run();
				} else {
					Toast.makeText(ActivityLog.this, "Logging Stopped",
							Toast.LENGTH_SHORT).show();
					isLogging = false;
					enableButton = false;
					bRec.setText("Start Logging Data");
					bRec.setEnabled(true);
					bProg.setVisibility(View.GONE);
					dbWriteH.removeCallbacks(dbWrite);
					dbRefreshH.removeCallbacks(dbRefresh);
					populateList();
				}
			}
		});
	}

	// Overriden onPause to stop loggin on exit.
	@Override
	protected void onPause() {
		if (isLogging == true) {
			Toast.makeText(ActivityLog.this, "Logging Stopped",
					Toast.LENGTH_SHORT).show();
			isLogging = false;
			enableButton = false;
			bRec.setText("Start Logging Data");
			bRec.setEnabled(true);
			bProg.setVisibility(View.GONE);
			dbWriteH.removeCallbacks(dbWrite);
			dbRefreshH.removeCallbacks(dbRefresh);
		}
		super.onPause();
	}

	// Runnable that writes to database every 20 sec.
	Runnable dbWrite = new Runnable() {
		@Override
		public void run() {
			try {
				new addDBEntry().execute(ActivityLog.this, null, null);
				Toast.makeText(ActivityLog.this, "Entry Added!", Toast.LENGTH_SHORT).show(); 
			} catch (Exception e) {
				Toast.makeText(ActivityLog.this, "Something went wrong..", Toast.LENGTH_SHORT)
						.show(); 
			}
			dbWriteH.postDelayed(dbWrite, 20000);
		}
	};

	// Runnable that refreshes the view every 4 sec.
	Runnable dbRefresh = new Runnable() {
		@Override
		public void run() {
			populateList();
			if(enableButton){
				bRec.setEnabled(true);
			}
			enableButton = true;
			dbRefreshH.postDelayed(dbRefresh, 4000);
		}
	};

	// Asynchronous Task that adds at entry on a different thread to avoid UI lag.
	private static class addDBEntry extends AsyncTask<Context, String, String> {

		@Override
		protected String doInBackground(Context... c) {
			Utils.addEntry(c[0]);
			return null;
		}

	}

	// Menu methods
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Wipe DB option erases all entries in the database.
		if (item.getTitle().toString().equals("Wipe DB")) {
			if (isLogging == false) {
				Utils.wipeDatabase(this);
				populateList();
			} else {
				if(toast!=null){
					toast.cancel();
					
				}
				toast = Toast.makeText(this, "Can't wipe while logging",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			if (item.getItemId() == android.R.id.home) {
				finish();
			}
		}
		return true;
	}

}
