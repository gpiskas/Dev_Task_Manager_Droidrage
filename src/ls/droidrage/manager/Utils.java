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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

// Utilities class that hosts various static methods.
public class Utils {

	private static Toast toast;

	// Enables top left back button on action bar if API > HONEYCOMB.
	@TargetApi(14)
	public static void enableActionBar(Activity act) {
		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
			act.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// Adds an entry to the database. In fact, adds three on three different
	// tables.
	public static void addEntry(Context c) {
		String timestamp = getCurrentTimestamp();
		Database entry = new Database(c);
		entry.open();

		// Ram entry
		entry.createEntry_R(timestamp, getCurrentRam(c) + "MB");

		// Services entries
		List<RunningServiceInfo> rs = getRunningServices(c);
		for (RunningServiceInfo s : rs) {
			String service = s.service.getClassName();
			entry.createEntry_S(timestamp,
					service.substring(service.lastIndexOf(".") + 1),
					getServiceMemoryUsed(c, s) + "MB", s);
		}

		// Processes entries
		List<RunningAppProcessInfo> rp = getRunningProcesses(c);
		for (RunningAppProcessInfo p : rp) {
			entry.createEntry_P(timestamp, getProcesseName(c, p),
					getProcessMemoryUsed(c, p) + "MB");
		}

		entry.close();

	}

	// Returns the current time in a formatted timestamp.
	private static String getCurrentTimestamp() {
		Date date = new Date();
		return date.toString().substring(0, 19);
	}

	// Returns the current ram used percentage.
	private static String getCurrentRam(Context c) {
		MemoryInfo mi = new MemoryInfo();
		((ActivityManager) c.getSystemService(Activity.ACTIVITY_SERVICE))
				.getMemoryInfo(mi); 
		return String.valueOf(mi.availMem / 1048576L);
	}

	// Returns RAM entry from database based on timestamp.
	public static String getDbRam(Context c, String timestamp) {
		try {
			Database entry = new Database(c);
			String ram = new String();
			entry.open();
			ram = entry.getRam(timestamp);
			entry.close();
			return ram;
		} catch (Exception e) {
			if (toast != null) {
				toast.cancel();

			}
			toast = Toast.makeText(c, "Something went wrong..",
					Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			return null;
		}
	}

	// Returns Processes entries from database based on timestamp.
	public static ArrayList<String> getDbProcesses(Context c, String timestamp) {
		try {
			Database entry = new Database(c); 
			entry.open();
			ArrayList<String> prc = entry.getProcesses(timestamp);
			entry.close();
			return prc;
		} catch (Exception e) {
			if (toast != null) {
				toast.cancel();

			}
			toast = Toast.makeText(c, "Something went wrong..",
					Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			return null;
		}
	}

	// Returns Services entries from database based on timestamp.
	public static ArrayList<String> getDbServices(Context c, String timestamp) {
		try {
			Database entry = new Database(c); 
			entry.open();
			ArrayList<String> svs = entry.getServicesNames(timestamp);
			entry.close();
			return svs;
		} catch (Exception e) {
			if (toast != null) {
				toast.cancel();

			}
			toast = Toast.makeText(c, "Something went wrong..",
					Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			return null;
		}
	}

	// Returns Service details from database based on timestamp and service
	// name.
	public static ArrayList<String> getDbServiceDetails(Context c,
			String timestamp, String servicename) {
		try {
			Database entry = new Database(c); 
			entry.open();
			ArrayList<String> svs = entry.getServiceDetails(timestamp, servicename);
			entry.close();
			return svs;
		} catch (Exception e) {
			if (toast != null) {
				toast.cancel();

			}
			toast = Toast.makeText(c, "Something went wrong..",
					Toast.LENGTH_SHORT);
			toast.show();
			e.printStackTrace();
			return null;
		}
	}

	// Returns a single process ram usage percentage.
	public static String getProcessMemoryUsed(Context c, RunningAppProcessInfo p) {
		android.os.Debug.MemoryInfo memInfo = ((ActivityManager) c
				.getApplicationContext().getSystemService(
						Activity.ACTIVITY_SERVICE))
				.getProcessMemoryInfo(new int[] { p.pid })[0];
		int ram = memInfo.dalvikPss + memInfo.otherPss + memInfo.nativePss;c.getApplicationContext().getApplicationInfo();
		return String.format(Locale.getDefault(), "%.1f", ram / 1024.0);
	}

	// Returns a single service ram usage percentage.
	public static String getServiceMemoryUsed(Context c, RunningServiceInfo s) {
		android.os.Debug.MemoryInfo memInfo = ((ActivityManager) c
				.getApplicationContext().getSystemService(
						Activity.ACTIVITY_SERVICE))
				.getProcessMemoryInfo(new int[] { s.pid })[0];
		int ram = memInfo.dalvikPss + memInfo.otherPss + memInfo.nativePss;
		return String.format(Locale.getDefault(), "%.1f", ram / 1024.0);
	}

	// Returns a list of running services.
	public static List<RunningServiceInfo> getRunningServices(Context c) {
		return ((ActivityManager) c.getApplicationContext().getSystemService(
				Activity.ACTIVITY_SERVICE))
				.getRunningServices(Integer.MAX_VALUE);
	}

	// Returns a list of running processes.
	public static List<RunningAppProcessInfo> getRunningProcesses(Context c) {
		return ((ActivityManager) c.getApplicationContext().getSystemService(
				Activity.ACTIVITY_SERVICE)).getRunningAppProcesses();
	}

	// Returns the name of a process.
	public static String getProcesseName(Context c, RunningAppProcessInfo prc) {
		final PackageManager pm = c.getPackageManager();
		try {
			return (String) pm.getApplicationLabel(pm.getApplicationInfo(
					prc.processName, 0));
		} catch (Exception e) {
			return prc.processName;
		}

	}

	// Erases database content.
	public static boolean wipeDatabase(Context c) {
		try {
			Database entry = new Database(c);
			entry.open();
			entry.wipeDatabase();
			entry.close();
			if (toast != null) {
				toast.cancel();

			}
			toast = Toast.makeText(c, "Database Wiped!", Toast.LENGTH_SHORT);
			toast.show();
			return true;
		} catch (Exception e) {
			if (toast != null) {
				toast.cancel();

			}
			toast = Toast.makeText(c, "Something went wrong..", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
	}
}
