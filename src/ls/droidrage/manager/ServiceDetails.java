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
import android.app.ActivityManager.RunningServiceInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

// Service Details dialogue, displaying info.
public class ServiceDetails extends Activity{

	private Button bBack;
	private RunningServiceInfo myService;
	private TextView tvService, tvStarted, tvProcess, tvUid, tvPid, tvActiveSince, tvForeground, tvFlags, tvLastActivityTime, tvCrashCount, tvRestarting, tvClientCount, tvClientLabel, tvClientPackage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicedetails);
		getWindow().setLayout(LayoutParams.MATCH_PARENT /* width */,
				LayoutParams.WRAP_CONTENT /* height */);

		init();
		
		// If timestamp exists, it should load data from database.
		if(getIntent().hasExtra("timestamp")){
			loadFromDb();
		} else {
			myService = (RunningServiceInfo) getIntent().getParcelableExtra("service");
			setTitle(getIntent().getStringExtra("title"));
			loadData();
		}
		
		bBack = (Button) findViewById(R.id.bback);
		bBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}
	
	// Load data on runtime.
	private void loadData() {
		if( myService.service.getClassName()!=null ){
			tvService.append( Html.fromHtml("<b>Class Name: </b> <i>" + myService.service.getClassName() +"</i>") );
		}
		tvPid.append( Html.fromHtml("<b>Pid: </b> <i>" + myService.pid +"</i>") );
		tvUid.append( Html.fromHtml("<b>Uid: </b> <i>" + myService.uid +"</i>") );
		tvProcess.append( Html.fromHtml("<b>Process: </b> <i>" + myService.process +"</i>") );
		tvStarted.append( Html.fromHtml("<b>Started: </b> <i>" + myService.started +"</i>") );
		tvActiveSince.append( Html.fromHtml("<b>Active Since: </b> <i>" + myService.activeSince +"ms</i>") );
		tvForeground.append( Html.fromHtml("<b>Foreground: </b> <i>" + myService.foreground +"</i>") );
		tvFlags.append( Html.fromHtml("<b>Flags: </b> <i>" + myService.flags +"</i>") );
		tvLastActivityTime.append( Html.fromHtml("<b>Last Activity Time: </b> <i>" + myService.lastActivityTime +"ms</i>") );
		tvCrashCount.append( Html.fromHtml("<b>Crash Count: </b> <i>" + myService.crashCount +"</i>") );
		tvRestarting.append( Html.fromHtml("<b>Restarting: </b> <i>" + myService.restarting +"</i>") );
		tvClientCount.append( Html.fromHtml("<b>Client Count: </b> <i>" + myService.clientCount +"</i>") );
		tvClientLabel.append( Html.fromHtml("<b>Client Label: </b> <i>" + myService.clientLabel +"</i>") );
		tvClientPackage.append( Html.fromHtml("<b>Client Package: </b> <i>" + myService.clientPackage +"</i>") );	
	}

	// Load log data.
	private void loadFromDb() {
		ArrayList<String> data = new ArrayList<String>();
		data = Utils.getDbServiceDetails(this, getIntent().getStringExtra("timestamp"), getIntent().getStringExtra("servicename"));
		
		tvService.append( Html.fromHtml("<b>Class Name: </b> <i>" + data.get(0) +"</i>") );
		tvPid.append( Html.fromHtml("<b>Pid: </b> <i>" + data.get(1) +"</i>") );
		tvUid.append( Html.fromHtml("<b>Uid: </b> <i>" + data.get(2) +"</i>") );
		tvProcess.append( Html.fromHtml("<b>Process: </b> <i>" + data.get(3) +"</i>") );
		tvStarted.append( Html.fromHtml("<b>Started: </b> <i>" + data.get(4) +"</i>") );
		tvActiveSince.append( Html.fromHtml("<b>Active Since: </b> <i>" + data.get(5) +"ms</i>") );
		tvForeground.append( Html.fromHtml("<b>Foreground: </b> <i>" + data.get(6) +"</i>") );
		tvFlags.append( Html.fromHtml("<b>Flags: </b> <i>" + data.get(7) +"</i>") );
		tvLastActivityTime.append( Html.fromHtml("<b>Last Activity Time: </b> <i>" + data.get(8) +"ms</i>") );
		tvCrashCount.append( Html.fromHtml("<b>Crash Count: </b> <i>" + data.get(9) +"</i>") );
		tvRestarting.append( Html.fromHtml("<b>Restarting: </b> <i>" + data.get(10) +"</i>") );
		tvClientCount.append( Html.fromHtml("<b>Client Count: </b> <i>" +data.get(11) +"</i>") );
		tvClientLabel.append( Html.fromHtml("<b>Client Label: </b> <i>" + data.get(12) +"</i>") );
		tvClientPackage.append( Html.fromHtml("<b>Client Package: </b> <i>" + data.get(13) +"</i>") );			
	}

	private void init() {
		tvService = (TextView) findViewById(R.id.tvservice);
		tvPid = (TextView) findViewById(R.id.tvpid);
		tvUid = (TextView) findViewById(R.id.tvuid);
		tvProcess = (TextView) findViewById(R.id.tvprocess);		
		tvStarted = (TextView) findViewById(R.id.tvstarted);		
		tvActiveSince = (TextView) findViewById(R.id.tvactivesince);
		tvForeground = (TextView) findViewById(R.id.tvforeground);		
		tvFlags = (TextView) findViewById(R.id.tvflags);		
		tvLastActivityTime = (TextView) findViewById(R.id.tvlastactivitytime);		
		tvCrashCount = (TextView) findViewById(R.id.tvcrashcount);		
		tvRestarting = (TextView) findViewById(R.id.tvrestarting);		
		tvClientCount = (TextView) findViewById(R.id.tvclientcount);
		tvClientLabel = (TextView) findViewById(R.id.tvclientlabel);
		tvClientPackage = (TextView) findViewById(R.id.tvclientpackage);	
		}

	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	public void onTouch(android.view.View v) {
		finish();
	} 
}
