package com.newagesmb.version3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class Common extends Activity {

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public void logout(View view) {
		finish();
	}
	public boolean emailValidation(String email){
		Pattern pattern=Pattern.compile(EMAIL_PATTERN);
		Matcher matcher=pattern.matcher(email);
		return matcher.matches();
	}
	 @SuppressLint("NewApi")
		public int getScreenWidth() {
	        int columnWidth;
	        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
	 
	        final Point point = new Point();
	        try {
	            display.getSize(point);
	        } catch (java.lang.NoSuchMethodError ignore) { // Older device
	            point.x = display.getWidth();
	            point.y = display.getHeight();
	        }
	        columnWidth = point.x;
	        return columnWidth;
	    }
	 @SuppressLint("NewApi")
		public int getScreenHeight() {
	        int columnHeight;
	        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
	 
	        final Point point = new Point();
	        try {
	            display.getSize(point);
	        } catch (java.lang.NoSuchMethodError ignore) { // Older device
	            point.x = display.getWidth();
	            point.y = display.getHeight();
	        }
	        columnHeight = point.y;
	        return columnHeight;
	    }

}
