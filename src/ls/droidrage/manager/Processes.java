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
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

// Process activity containing a list of all running processes and their ram usage percentage.
public class Processes extends ListActivity {

	private ListView lv;
	private List<RunningAppProcessInfo> runningPrc;
	private ArrayList<String> processes;
	private ArrayList<String> proPackage;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.enableActionBar(this);

		populateList();
		lv = getListView();
		// Kill dialogue on long click.
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				handleLongClick(pos);
				return false;
			}
		});
	} 

	// Populates the list
	private void populateList() {
		// Get processes, populate "processes" with actual names and "proPackage" with package names useful for kills.
		runningPrc = Utils.getRunningProcesses(this);
		processes = new ArrayList<String>();
		proPackage = new ArrayList<String>();
		 
		for (RunningAppProcessInfo prc : runningPrc) {
			processes.add(Utils.getProcesseName(this, prc) + "\nRAM Used: " + Utils.getProcessMemoryUsed(this, prc) + "MB");
			proPackage.add(prc.processName);
		}
		// Then populate the list.
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, processes));
	}
	
	
	// Kill dialogue on long click.
	private void handleLongClick( final int pos) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Kill Process");
			alertDialog.setMessage("Are you sure you want to kill "
					+ processes.get(pos).substring(0,
							processes.get(pos).indexOf("\n")) + "?");
			alertDialog.setCanceledOnTouchOutside(false);

			alertDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							// Do nothing!
						}
					});

			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing!
						}
					});

			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Kill vian activity manager using package name.
							((ActivityManager) getApplicationContext().getSystemService(
									Activity.ACTIVITY_SERVICE)).killBackgroundProcesses(proPackage.get(pos));
							processes.remove(pos);
							// Update UI without killed process.
							setListAdapter(new ArrayAdapter<String>(Processes.this,
									android.R.layout.simple_list_item_1, processes));
						}
					});
			alertDialog.show(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.kill, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			if (item.getItemId() == android.R.id.home) {
				finish();
			}
		}
		// Kill all alert dialogue.
		if (item.getTitle().toString().equals("Kill All")) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Kill All Processes");
			alertDialog.setMessage("Are you sure you want to proceed?");
			alertDialog.setCanceledOnTouchOutside(false);

			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// Do nothing!
				}
			});

			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing!
						}
					});

			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
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
							toast = Toast.makeText(Processes.this, "Operation Completed Successfully",
									Toast.LENGTH_SHORT);
							toast.show();
							populateList();
							
						}
					});
			alertDialog.show();
		} 
		return true;
	}

}
