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

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

// Home screen activity.
public class Home extends Activity implements OnClickListener {

	private Button bSystemInfo, bServices, bProcesses, bKillAll, bDb; 
	private Toast toast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		init(); 
	} 
	
	// Initialization of UI elements.
	private void init() {
		bSystemInfo = (Button) findViewById(R.id.bsysteminfo);
		bSystemInfo.setOnClickListener(this);
		bServices = (Button) findViewById(R.id.bservices);
		bServices.setOnClickListener(this);
		bProcesses = (Button) findViewById(R.id.bprocesses);
		bProcesses.setOnClickListener(this);
		bKillAll = (Button) findViewById(R.id.bkillall);
		bKillAll.setOnClickListener(this);
		bDb = (Button) findViewById(R.id.bdb);
		bDb.setOnClickListener(this);

	}

	
	// Click listener that acts according to the pressed button.
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bsysteminfo:
			startActivity(new Intent(this, SystemInfo.class));
			break;
		case R.id.bservices:
			startActivity(new Intent(this, Services.class));
			break;
		case R.id.bprocesses:
			startActivity(new Intent(this, Processes.class));
			break;
		case R.id.bkillall:
			// Gets all running processes and kills them!
			ActivityManager actMgr = (ActivityManager) getApplicationContext()
					.getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningPrc = actMgr
					.getRunningAppProcesses();
			for (RunningAppProcessInfo prc : runningPrc) {
				actMgr.killBackgroundProcesses(prc.processName);
			}
			if(toast!=null){
				toast.cancel();
				
			}
			toast = Toast.makeText(this, "Operation Completed Successfully",
					Toast.LENGTH_SHORT);
			toast.show();
			
			break;
		case R.id.bdb:
			startActivity(new Intent(this, ActivityLog.class));
			break;  

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equals("About")) {
			startActivity(new Intent(this, About.class));
		} else if (item.getTitle().toString().equals("Exit")) {
			System.exit(0);
		}
		return true;
	}

}
