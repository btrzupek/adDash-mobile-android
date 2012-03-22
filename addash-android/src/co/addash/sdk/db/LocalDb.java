package co.addash.sdk.db;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class LocalDb {
	
	private final String PREFS_NAME;
	
	protected Context mContext;
	
	protected LocalDb(Context context, String prefsName) {
		super();
		mContext = context;
		PREFS_NAME = prefsName;
	}
	
	public void read() {
		SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);
		doRead(prefs);
	}
	
	protected abstract void doRead(SharedPreferences prefs);
	
	public void write() {
		SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		
		doWrite(editor);
		
		editor.commit();
	}
	
	protected abstract void doWrite(SharedPreferences.Editor editor);

}
