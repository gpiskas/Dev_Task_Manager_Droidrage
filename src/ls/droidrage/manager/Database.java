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

import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper; 

// The database that holds the log.
public class Database {
	
	// Primary key and timestamp identifier.
	private static final String KEY_ROWID = "_id";
	private static final String TIMESTAMP = "timestamp";

	// RAM table.
	private static final String R_MEMORYUSED = "R_memoryused";
	
	// SERVICES table.
	private static final String S_SERVICENAME = "s_servicename";
	private static final String S_CLASSNAME = "s_classname";
	private static final String S_MEMORYUSED = "s_memoryused";
	private static final String S_PID = "s_pid";
	private static final String S_UID = "s_uid";
	private static final String S_PROCESS = "s_process";
	private static final String S_STARTED = "s_started";
	private static final String S_FOREGROUND = "s_foreground";
	private static final String S_FLAGS = "s_flags";
	private static final String S_ACTIVESINCE = "s_activesince";
	private static final String S_LASTACTIVITYTIME = "s_lastactivitytime";
	private static final String S_CRASHCOUNT = "s_crashcount";
	private static final String S_RESTARTING = "s_restarting";
	private static final String S_CLIENTCOUNT = "s_clientcount";
	private static final String S_CLIENTLABEL = "s_clientlabel";
	private static final String S_CLIENTPACKAGE = "s_clientpackage";
	
	// PROCESSES table.
	private static final String P_PROCESSNAME = "p_processname";
	private static final String P_MEMORYUSED = "p_memoryused";

	// DB name and above table names.
	private static final String DB_NAME = "DB";
	private static final String DB_TABLE_R = "TBL_R";
	private static final String DB_TABLE_S = "TBL_S";
	private static final String DB_TABLE_P = "TBL_P";
	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;
	private final Context context;
	private SQLiteDatabase db;

	
	// Helper class that initiates database.
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}

		// Creates all tables and their fields.
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DB_TABLE_R + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
					+ TIMESTAMP + " TEXT NOT NULL UNIQUE, " 
					+ R_MEMORYUSED + " TEXT NOT NULL);");
			
			db.execSQL("CREATE TABLE " + DB_TABLE_S + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
					+ TIMESTAMP + " TEXT NOT NULL, " 
				    + S_SERVICENAME + " TEXT, "
					+ S_CLASSNAME + " TEXT, "
					+ S_MEMORYUSED + " TEXT, "
					+ S_PID + " TEXT, "
					+ S_UID + " TEXT, "
					+ S_PROCESS + " TEXT, "
					+ S_STARTED + " TEXT, "
				    + S_FOREGROUND + " TEXT, "
					+ S_FLAGS + " TEXT, "
					+ S_ACTIVESINCE + " TEXT, "
					+ S_LASTACTIVITYTIME + " TEXT, "
					+ S_CRASHCOUNT + " TEXT, "
					+ S_RESTARTING + " TEXT, "
					+ S_CLIENTCOUNT + " TEXT, "		
					+ S_CLIENTLABEL + " TEXT, "
					+ S_CLIENTPACKAGE + " TEXT);");
			
			db.execSQL("CREATE TABLE " + DB_TABLE_P + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
					+ TIMESTAMP + " TEXT NOT NULL, " 
				    + P_PROCESSNAME + " TEXT, "
					+ P_MEMORYUSED + " TEXT); ");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_R);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_S);
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_P);
			onCreate(db);
		}
	}

	public Database(Context c) {
		context = c;
	}

	public Database open() throws SQLException {
		dbHelper = new DbHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	// Creates an entry in RAM table.
	public long createEntry_R(String timestamp, String memoryused) {
		ContentValues cv = new ContentValues();
		cv.put(TIMESTAMP, timestamp); 
		cv.put(R_MEMORYUSED, memoryused); 
		return db.insert(DB_TABLE_R, null, cv);
	}
	
	// Creates an entry in SERVICES table.
	public long createEntry_S(String timestamp, String servicename, String memoryused, RunningServiceInfo myService) {
		ContentValues cv = new ContentValues();
		cv.put(TIMESTAMP, timestamp); 
		cv.put(S_SERVICENAME, servicename); 
		cv.put(S_CLASSNAME, myService.service.getClassName()); 
		cv.put(S_MEMORYUSED, memoryused); 
		cv.put(S_PID, myService.pid); 
		cv.put(S_UID , myService.uid); 
		cv.put(S_PROCESS, myService.process); 
		cv.put(S_STARTED, myService.started); 
		cv.put(S_FOREGROUND, myService.foreground); 
		cv.put(S_FLAGS, myService.flags); 
		cv.put(S_ACTIVESINCE, myService.activeSince); 
		cv.put(S_LASTACTIVITYTIME, myService.lastActivityTime); 
		cv.put(S_CRASHCOUNT, myService.crashCount); 
		cv.put(S_RESTARTING, myService.restarting); 
		cv.put(S_CLIENTCOUNT, myService.clientCount); 
		cv.put(S_CLIENTLABEL, myService.clientLabel); 
		cv.put(S_CLIENTPACKAGE, myService.clientPackage); 
		return db.insert(DB_TABLE_S, null, cv);
	}	
	
	// Creates an entry in PROCESSES table.
	public long createEntry_P(String timestamp, String processname, String memoryused) {
		ContentValues cv = new ContentValues();
		cv.put(TIMESTAMP, timestamp); 
		cv.put(P_PROCESSNAME, processname); 
		cv.put(P_MEMORYUSED, memoryused); 
		return db.insert(DB_TABLE_P, null, cv);
	}	

	// Finds and returns all unique timestamps from RAM table. These exist on all tables.
	public ArrayList<String> getTimestamps() { 
		Cursor c = db.query(DB_TABLE_R, new String[] { TIMESTAMP }, null, null, null, null, null);
		
		int ts = c.getColumnIndex(TIMESTAMP);

		ArrayList<String> result = new ArrayList<String>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result.add(c.getString(ts));
		}
		c.close();
		return result;
	}	
	
	// Returns RAM entry from RAM table based on timestamp.
	public String getRam(String timestamp) { 
		Cursor c = db.query(DB_TABLE_R, new String[] { R_MEMORYUSED }, TIMESTAMP + " = '" + timestamp +"'", null, null, null, null);
			
		c.moveToFirst();
		String rtrn = c.getString(c.getColumnIndex(R_MEMORYUSED));
		c.close();
		return rtrn;
	}	

	// Returns Services from SERVICES table based on timestamp.
	public ArrayList<String> getServicesNames(String timestamp) { 
		Cursor c = db.query(DB_TABLE_S, new String[] { S_SERVICENAME, S_MEMORYUSED }, TIMESTAMP + " = '" + timestamp +"'", null, null, null, null);

		int sName = c.getColumnIndex(S_SERVICENAME);
		int sMem = c.getColumnIndex(S_MEMORYUSED); 
		
		c.moveToFirst();		
		ArrayList<String> result = new ArrayList<String>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result.add(c.getString(sName) + "\n RAM Used: " + c.getString(sMem)); 
		}
		c.close();
		return result;
	}		
	
	// Returns Service details from SERVICES table based on timestamp AND service name.
	public ArrayList<String> getServiceDetails(String timestamp, String serrvicename) { 
		Cursor c = db.query(DB_TABLE_S, new String[] {S_CLASSNAME, S_PID, S_UID , S_PROCESS, S_STARTED, S_FOREGROUND, S_FLAGS, S_ACTIVESINCE, S_LASTACTIVITYTIME, S_CRASHCOUNT, S_RESTARTING, S_CLIENTCOUNT, S_CLIENTLABEL, S_CLIENTPACKAGE }, TIMESTAMP + " = '" + timestamp +"' AND " + S_SERVICENAME + " = '" + serrvicename +"'" , null, null, null, null);

		int sClass = c.getColumnIndex(S_CLASSNAME); 
		int sPid = c.getColumnIndex(S_PID); 
		int sUid = c.getColumnIndex(S_UID); 
		int sProc = c.getColumnIndex(S_PROCESS); 
		int sStart = c.getColumnIndex(S_STARTED); 
		int sForeg = c.getColumnIndex(S_FOREGROUND); 
		int sFlags = c.getColumnIndex(S_FLAGS); 
		int sAct = c.getColumnIndex(S_ACTIVESINCE); 
		int sLast = c.getColumnIndex(S_LASTACTIVITYTIME); 
		int sCrash = c.getColumnIndex(S_CRASHCOUNT); 
		int sRest = c.getColumnIndex(S_RESTARTING); 
		int sCc = c.getColumnIndex(S_CLIENTCOUNT); 
		int sCl = c.getColumnIndex(S_CLIENTLABEL); 
		int sCp = c.getColumnIndex(S_CLIENTPACKAGE); 
		
		c.moveToFirst();
		ArrayList<String> result = new ArrayList<String>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result.add(c.getString(sClass));
			result.add(c.getString(sPid));
			result.add(c.getString(sUid));
			result.add(c.getString(sProc));
			result.add(c.getString(sStart));
			result.add(c.getString(sForeg));
			result.add(c.getString(sFlags));
			result.add(c.getString(sAct));
			result.add(c.getString(sLast));
			result.add(c.getString(sCrash));
			result.add(c.getString(sRest));
			result.add(c.getString(sCc));
			result.add(c.getString(sCl));
			result.add(c.getString(sCp)); 
		}
		c.close();
		return result;
	}		
	
	// Returns Processes from PROCESSES table based on timestamp.
	public ArrayList<String> getProcesses(String timestamp) { 
		Cursor c = db.query(DB_TABLE_P, new String[] { P_PROCESSNAME, P_MEMORYUSED }, TIMESTAMP + " = '" + timestamp +"'", null, null, null, null);

		int pName = c.getColumnIndex(P_PROCESSNAME);
		int pMem = c.getColumnIndex(P_MEMORYUSED); 
		
		c.moveToFirst();		
		ArrayList<String> result = new ArrayList<String>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result.add(c.getString(pName) + "\n RAM Used: " + c.getString(pMem)); 
		}
		c.close();
		return result;
	}	
	
	// Erases all data.
	public void wipeDatabase() throws SQLException {
		db.execSQL("DELETE FROM " + DB_TABLE_R);
		db.execSQL("DELETE FROM " + DB_TABLE_S);
		db.execSQL("DELETE FROM " + DB_TABLE_P); 
	}
	
}
