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

	public static boolean cb_theme_app = true;
	public static int app_theme = R.style.AppTheme;  //пример - android.R.style.Theme_Holo
	public static boolean rec_cb_date1 = true;
	public static boolean rec_cb_separator = true;
	public static String rec_et_separator = Set.STR_RAZDELITEL;
	public static int max_edit_line= 4; 
// константы
/** ключ - задаёт количество строк для поля редактирования записи */	
	public static String MAX_EDIT_LINE = "max_edit_line";
	/** добавлять дату в формате dd.MM.yyyy HH:mm:ss */
	public static String RECORD_CB_DATE1= "record_check_date1";
	public static String RECORD_LINE_SEPARATOR = "record_line_separator";
	public static String RECORD_CB_SEPARATOR = "record_check_separator";
/** ключ, имя файла(ов) для добавления текста */
	public static String FILENAME= "add_filename";
	public static String FILENAME_DEF= "_MWshare2save.txt";
	public static String FILENAME_EXT_DEF= ".txt";
	/** ключ, куда добавлять запись - начало или конец файла */
	public static String WHERE_RECORD = "where_record";

	public static String THEME_APP = "theme_app";

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
		where_rec = Prefs.getBoolean(Prefs.THEME_APP, true); // по умолчание светлая тема
		app_theme = R.style.AppTheme;
		if (!where_rec)
			app_theme = android.R.style.Theme_Holo;
		where_rec = Prefs.getBoolean(Prefs.WHERE_RECORD, false);
		rec_cb_date1 = Prefs.getBoolean(Prefs.RECORD_CB_DATE1, true);
		rec_cb_separator = Prefs.getBoolean(Prefs.RECORD_CB_SEPARATOR, true);
		rec_et_separator = Prefs.getString(Prefs.RECORD_LINE_SEPARATOR, Set.STR_RAZDELITEL);
		if (rec_cb_separator&&rec_et_separator.isEmpty()) {
			Prefs.setString(Prefs.RECORD_LINE_SEPARATOR, Set.STR_RAZDELITEL);
		}
		max_edit_line = Prefs.getInt(MAX_EDIT_LINE, 5);
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

}