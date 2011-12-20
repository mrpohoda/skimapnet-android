package net.skimap.utililty;

import net.skimap.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings
{
	private SharedPreferences mSharedPrefs;
	private Context mContext;
	
	//public static final int NULL_INT = -1;
	public static final long NULL_LONG = -1;
	public static final String NULL_STRING = "";
	//public static final double NULL_DOUBLE = -1;
	//public static final boolean NULL_BOOLEAN = false;
	
	
	public Settings(Context context)
	{
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mContext = context;
	}
	

	public void resetSettings()
	{
		Editor editor = mSharedPrefs.edit();
		editor.clear();
		editor.commit();
	}
	
	
	// GETTERS ////////////////////////////////////////////////////////////////////////////////////
	
	
	public long getLastSynchro()
	{
		String key = mContext.getString(R.string.settings_key_last_synchro);
		long defaultValue = NULL_LONG;
		long value = mSharedPrefs.getLong(key, defaultValue);
		return value;
	}
	
	
	public String getCurrentVersion()
	{
		String key = mContext.getString(R.string.settings_key_current_version);
		String defaultValue = NULL_STRING;
		String value = mSharedPrefs.getString(key, defaultValue);
		return value;
	}
	
	
	// SETTERS ////////////////////////////////////////////////////////////////////////////////////
	
	
	public void setLastSynchro(long timestamp)
	{
		String key = mContext.getString(R.string.settings_key_last_synchro);
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putLong(key, timestamp);
	 	editor.commit();
	}
	
	
	public void setCurrentVersion(String version)
	{
		String key = mContext.getString(R.string.settings_key_current_version);
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putString(key, version);
	 	editor.commit();
	}
}
