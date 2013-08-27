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

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;

// About Dialogue Activity
public class About extends Activity {
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		getWindow().setLayout(LayoutParams.MATCH_PARENT /* width */,
				LayoutParams.WRAP_CONTENT /* height */);
	}

	
	// Close on touch
	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}

	public void onTouch(android.view.View v) {
		finish();
	}
}
