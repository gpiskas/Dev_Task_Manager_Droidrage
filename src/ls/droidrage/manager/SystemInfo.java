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

import java.io.RandomAccessFile; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.os.BatteryManager;

// System info activity containing various information about the system.
public class SystemInfo extends Activity {

	private TextView tvBattery, tvBatteryType, tvBatteryHealth, tvKernel, tvMod, tvExternal,
			tvInternal, tvAndroid, tvRam, tvRamLeft, tvRamCached;
	private ProgressBar pbBattery, pbRam;
	private Handler ramHandler;
	private BroadcastReceiver ramReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.enableActionBar(this);
		setContentView(R.layout.systeminfo);
		init();

		// Receiver that updates the battery info field.
	    if (ramReceiver == null)
	    {
	    	ramReceiver = this.batteryInfo;
	        registerReceiver(ramReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    }

		getSystemInfo();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stops updating ram and battery status.
		
		ramHandler.removeCallbacks(ramCheck);
	    if (ramReceiver != null)
	    {
	        unregisterReceiver(ramReceiver);
	        ramReceiver = null;
	    }
	}

	private void init() {
		tvAndroid = (TextView) findViewById(R.id.tvandroid);
		tvKernel = (TextView) findViewById(R.id.tvkernel);
		tvMod = (TextView) findViewById(R.id.tvmod);

		tvExternal = (TextView) findViewById(R.id.tvexternal);
		tvInternal = (TextView) findViewById(R.id.tvinternal);

		tvBattery = (TextView) findViewById(R.id.tvbattery);
		tvBatteryType = (TextView) findViewById(R.id.tvbatterytype);
		tvBatteryHealth = (TextView) findViewById(R.id.tvbatteryhealth);
		pbBattery = (ProgressBar) findViewById(R.id.pbbattery);

		tvRam = (TextView) findViewById(R.id.tvtotalram);
		tvRamLeft = (TextView) findViewById(R.id.tvramleft);
		tvRamCached = (TextView) findViewById(R.id.tvramcached);
		pbRam = (ProgressBar) findViewById(R.id.pbram);

	}

	private void getSystemInfo() {
		tvAndroid.setText(android.os.Build.VERSION.RELEASE);
		tvKernel.setText(System.getProperty("os.version"));
		tvMod.setText(android.os.Build.HOST);
		tvExternal.setText(getAvailableExternalMemorySize());
		tvInternal.setText(getAvailableInternalMemorySize());
		ramHandler = new Handler();
		ramCheck.run();
	}

	// Updates ram status every 2 sec.
	Runnable ramCheck = new Runnable() {
		@Override
		public void run() {
			try {
				RandomAccessFile reader;
				int total = 0;
				int avail = 0;
				int buff = 0;
				int cached = 0;
				reader = new RandomAccessFile("/proc/meminfo", "r");
				Pattern p = Pattern.compile("[0-9]+");

				Matcher m = p.matcher(reader.readLine());
				m.find();
				total = Integer.parseInt(m.group());

				m = p.matcher(reader.readLine());
				m.find();
				avail = Integer.parseInt(m.group());

				m = p.matcher(reader.readLine());
				m.find();
				buff = Integer.parseInt(m.group());

				m = p.matcher(reader.readLine());
				m.find();
				cached = Integer.parseInt(m.group());
				tvRam.setText(String.valueOf(total / 1024) + "MB");
				tvRamLeft.setText(String.valueOf((avail + buff + cached) / 1024) + "MB");
				tvRamCached.setText(String.valueOf(cached / 1024) + "MB");

				pbRam.setMax(total);
				pbRam.setProgress(avail + buff + cached);
				ramHandler.postDelayed(ramCheck, 2000);

				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// Returns formatted internal memory size.
	private String getAvailableInternalMemorySize() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		return formatSize((long) stat.getAvailableBlocks()
				* stat.getBlockSize());
	}

	// Returns formatted external memory size.
	private String getAvailableExternalMemorySize() {
		// If present...
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			return formatSize((long) stat.getAvailableBlocks()
					* stat.getBlockSize());
		} else {
			return "Not Present";
		}
	}

	// Formats storage size and appends KB or MB suffix.
	private String formatSize(long size) {
		String suffix = null;

		if (size >= 1024) {
			suffix = "KB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		
		// Comma
		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}

	private BroadcastReceiver batteryInfo = new BroadcastReceiver() {
		
		// On Receive battery update.
		@Override
		public void onReceive(Context context, Intent intent) {
			int btr = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			tvBattery.setText(btr + "%");
			tvBatteryType.setText(intent.getStringExtra("technology"));
			tvBatteryHealth.setText(getHealthString(intent.getIntExtra(
							BatteryManager.EXTRA_HEALTH, 0)));
			pbBattery.setProgress(btr);

		}
		
		// Battery health status.
		private String getHealthString(int health) {
			String healthString = "Unknown";

			switch (health) {
			case BatteryManager.BATTERY_HEALTH_DEAD:
				healthString = "Dead!";
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				healthString = "Healthy";
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				healthString = "Over Voltage!";
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				healthString = "Overheat!";
				break;
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				healthString = "Failure!";
				break;
			}

			return healthString;
		}
	};

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
