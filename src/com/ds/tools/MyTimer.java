package com.ds.tools;

import java.util.Timer;

public class MyTimer {
	private static class Holder {
		public static final Timer timer = new Timer();
	}
	
	public static Timer getTimer() {
		return Holder.timer;
	} 
}
