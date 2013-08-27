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

import android.app.ActivityManager.RunningServiceInfo;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Services activity containing a list of all running services. Opens ServiceDetails on item click.
public class Services extends ListActivity {

	private List<RunningServiceInfo> runningSvs;
	private ArrayList<String> services;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.enableActionBar(this);

		// Get Services, populate list.
		runningSvs = Utils.getRunningServices(this);

		services = new ArrayList<String>();
		for (RunningServiceInfo s: runningSvs) {
			String service = s.service.getClassName(); 
			services.add(service.substring(service.lastIndexOf(".") + 1) + "\nRAM Used: " + Utils.getServiceMemoryUsed(this, s) + "MB");
		}

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, services));
	}
	
	// Open ServiceDetails on click.
	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {
		super.onListItemClick(l, v, pos, id);
		Intent i = new Intent(this, ServiceDetails.class);
		i.putExtra("service", runningSvs.get(pos));
		i.putExtra("title", services.get(pos));
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			if (item.getItemId() == android.R.id.home) {
				finish();
			}
		}
		return true;
	}
}
