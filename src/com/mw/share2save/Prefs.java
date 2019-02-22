package com.mw.share2save;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Prefs 
{
	private static Prefs inst;
	SharedPreferences mPref;
	/** куда добавлять запись, в начало файла, или конец */
	public static boolean where_rec = false;
/** ключ, имя файла для добавления текста */
	public static String FILENAME= "add_filename";
	public static String FILENAME_DEF= "_MWshare2save.txt";
	public static String FILENAME_EXT_DEF= ".txt";
	public static String METHOD = "save_method";
	/** ключ, куда добавлять запись - начало или конец файла */
	public static String WHERE_RECORD = "where_record";

	public static void init(Context c)
	{
		inst = new Prefs();
		SharedPreferences sp = c.getSharedPreferences("settings", Context.MODE_PRIVATE);
		//SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		inst.mPref = sp;
	}
// читаем настройки
	public static void readPreference()
	{
		removePreferences();
		where_rec = Prefs.getBoolean(Prefs.WHERE_RECORD, false);
	}
	// удаляем лишние (неиспользуемые) настройки
	public static void removePreferences()
	{
		//get().edit().remove("newrec_col_tb");
	}
	public static SharedPreferences get()
	{
		return inst.mPref;
	}
	public static final int getInt(String key,int fallback)
	{
		return get().getInt(key, fallback);
	}
	public static void setInt(String key,int value)
	{
		inst.mPref.edit().putInt(key, value).commit();
		readPreference();
	}
	public static final String getString(String key,String fallback)
	{
		return get().getString(key, fallback);
	}
	public static void setString(String key,String value)
	{
		inst.mPref.edit().putString(key, value).commit();
		readPreference();
	}
	public static final boolean getBoolean(String key, boolean def) {
		return get().getBoolean(key, def);
	}
	public static void setBoolean(String key,boolean value)
	{
		inst.mPref.edit().putBoolean(key, value).commit();
		readPreference();
	}
	public static final String getFilename()
	{
		return get().getString(FILENAME, FILENAME_DEF);
	}
	public static void setFilename(String value)
	{
		inst.mPref.edit().putString(FILENAME, value).commit();
		readPreference();
	}
	public static final int getMethod()
	{
		return get().getInt(METHOD, 1);
	}

}