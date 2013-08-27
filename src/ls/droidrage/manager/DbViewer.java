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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

// Helper class that allows viewing entry content in a handy way.
public class DbViewer extends Activity{

	private ListView lvDbViewer;
	private String timestamp;
	private TextView tvDbTimestamp, tvDbRam;
	private ArrayList<String> list;
	private int firstSvc;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbviewer);
		init();

		// Get data from database and populate the list.
		list = new ArrayList<String>(); 
		list.addAll(Utils.getDbProcesses(this, timestamp));
		firstSvc = list.size();
		list.addAll(Utils.getDbServices(this, timestamp));
		lvDbViewer.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list));

		setLvListener();
		
	}
	
	// Item click listener.
	private void setLvListener() {
		lvDbViewer.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v,
					int pos, long id) { 
				// If in services area, open ServiceDetails class.
				if(pos>=firstSvc){
					Intent i = new Intent(DbViewer.this, ServiceDetails.class);
					i.putExtra("timestamp", timestamp);
					String name = list.get(pos);
					i.putExtra("servicename", name.substring(0,name.indexOf("\n")));
					startActivity(i);		 
				}
				

			}
		});

	}
	
	private void init() {
		timestamp = getIntent().getStringExtra("timestamp");
		
		tvDbTimestamp = (TextView) findViewById(R.id.tvdbtimestamp);
		tvDbTimestamp.setText("Log: " + timestamp);

		tvDbRam = (TextView) findViewById(R.id.tvdbram);
		tvDbRam.append(Utils.getDbRam(this, timestamp));
		
		lvDbViewer = (ListView) findViewById(R.id.lvdbviewer);
	}

}
