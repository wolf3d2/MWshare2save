package com.mw.share2save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	String record = null;
	String[] arFname = null;
	public static MainActivity inst = null;
//	TextView vers =null;
	Button btn_rec = null;
	TextView desc = null;
	TextView more = null;
	EditText et = null;
	Button save = null;
	CheckBox cb_where = null;
	CheckBox cb_rec_date1 = null;
	CheckBox cb_rec_separ = null;
	EditText et_rec_separ = null;
	boolean bchange = false;
	// развернуть/свернуть описание
	boolean bdescmore = false;
	boolean changed = false;
	TextWatcher tw = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			changed = true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// настройки читаются в App.java
		inst = this;
		setTheme(Prefs.app_theme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		inst = this;
        if (!Perm.checkPermission(inst)) {
        	Dlg.helpDialog(inst, inst.getString(R.string.perm_ann), new st.UniObserver() {
				
				@Override
				public int OnObserver(Object param1, Object param2) {
		            String[] perms = Perm.getPermissionStartArray();
		            Perm.requestPermission(inst, perms, Perm.RPC);
					return 0;
				}
			});
        }
//        try{
//            Prefs.init(this);
//            Prefs.readPreference();
//// пример записи нового параметра в настройки
////    		Pref.get().edit().putString("parameter", "value").commit();
//        } catch (Throwable e) 
//        {}
        arFname = getFilenameArray();
        btn_rec = (Button) inst.findViewById(R.id.main_save_query);
        btn_rec.setOnClickListener(m_ClickListener);
        btn_rec.setVisibility(View.GONE);

		// проверяем был ли послан текст для записи	
        checkStartIntent();
//		vers = (TextView) inst.findViewById(R.id.main_vers);
//        try{
//            String ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//            String app = getString(R.string.version)+" "+ver;
//            vers.setText(app);
//        }
//        catch (Throwable e) {}
		desc = (TextView) inst.findViewById(R.id.main_desc);
		desc.setOnClickListener(m_ClickListener);
		more = (TextView) inst.findViewById(R.id.main_read_more);
		more.setOnClickListener(m_ClickListener);
		
		et_rec_separ = (EditText) inst.findViewById(R.id.main__record_separator);
		et_rec_separ.setText(Prefs.rec_et_separator);

		et = (EditText) inst.findViewById(R.id.main_fname);
		if (Prefs.app_theme != R.style.AppTheme) {
			et.setBackgroundColor(Color.GRAY); 
			et_rec_separ.setBackgroundColor(Color.GRAY); 
		}
		et.setText(getFilenameString());
		et.addTextChangedListener(tw);
//		et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean bfocus) {
//				if (!bfocus) {
//	            	savePrefs();
//	            	saveFilename();
//				}
//			}
//		});
    	changed = false;

		//setFilenameToEditText(getFilenameString());
		
		save = (Button) inst.findViewById(R.id.main_save);
		save.setOnClickListener(m_ClickListener);

		cb_where = (CheckBox) inst.findViewById(R.id.cb_where_record);
		cb_where.setChecked(Prefs.where_rec);
		cb_where.setOnClickListener(m_ClickListener);
		
		cb_rec_date1 = (CheckBox) inst.findViewById(R.id.cb_record_date1);
		cb_rec_date1.setChecked(Prefs.rec_cb_date1);
		cb_rec_date1.setOnClickListener(m_ClickListener);

		cb_rec_separ = (CheckBox) inst.findViewById(R.id.cb_record_separator);
		cb_rec_separ.setChecked(Prefs.rec_cb_separator);
		cb_rec_separ.setOnClickListener(m_ClickListener);
		
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.theme_radioGroup);
        int th = R.id.radioButtonLight;
        // если тема тёмная
        if (Prefs.app_theme != R.style.AppTheme) {
        	th = R.id.radioButtonDark;
        	//et.setTextColor(Color.BLACK);
        }
        radioGroup.check(th);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonLight:
                    	Prefs.setBoolean(Prefs.THEME_APP, true);
                    	Prefs.readPreference();
                        break;
                    case R.id.radioButtonDark:
                    	Prefs.setBoolean(Prefs.THEME_APP, false);
                        break;
                }
                inst.recreate();
            }
        });
        
	}
	
    View.OnClickListener m_ClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
        	if (v.getId()!=R.id.main_fname)
        		st.hideKbd(inst);
            switch (v.getId())
            {
            case R.id.main_save_query:
            	savePrefs();
            	saveFilename();
            	addSaveText(record);
            	return;
            case R.id.cb_record_date1:
            case R.id.cb_record_separator:
            case R.id.cb_where_record:
            	savePrefs();
            	return;
            case R.id.main_save:
            	savePrefs();
            	saveFilename();
            	return;
            case R.id.main_desc:
            case R.id.main_read_more:
            	if (bdescmore){
            		bdescmore = false;
            		desc.setMaxLines(2);
            		more.setText(R.string.read_more1);
            	} else {
            		bdescmore = true;
            		desc.setMaxLines(35);
            		more.setText(R.string.read_more2);
            		
            	}
                return;
            }
        }
    };

	@Override
	public void onBackPressed() {
		savePrefs();
		if (changed) {
			Dlg.yesNoDialog(inst, inst.getString(R.string.data_changed), new st.UniObserver() {
				
				@Override
				public int OnObserver(Object param1, Object param2) {
                    if(((Integer)param1).intValue()==AlertDialog.BUTTON_POSITIVE)
                    {
    					saveFilename();
                    }
					finish();
					return 0;
				}
			});
		} else
			super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent in;
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId())
		{
		case R.id.action_about:
			String text = inst.getString(R.string.app_name)+st.STR_LF+st.STR_LF;
	        try{
	        	PackageManager pm = getPackageManager();
	            String vname = pm.getPackageInfo(getPackageName(), 0).versionName;
	            String vnum = " ("+pm.getPackageInfo(getPackageName(), 0).versionCode+")";
	            String app = getString(R.string.version)+" "+vname+vnum;
	            text+=app+st.STR_LF;
	        }
	        catch (Throwable e) {}
	        text+=inst.getString(R.string.about_author)+st.STR_SPACE
	        		+inst.getString(R.string.about_author_desc);
			Dlg.helpDialog(inst, text);
			return true;
		case R.id.action_rate:
			String link =  "https://play.google.com/store/apps/details?id=";
			try {
				link += inst.getPackageName();
			} catch (Throwable e) {
				return true;
			}
			try {
		    	in = new Intent(Intent.ACTION_VIEW);
		    	in.setData(Uri.parse(link));
		    	inst.startActivity(in);
			} catch (Throwable e) {
			}
			return true;
		case R.id.action_other_app:
			try {
		    	in= new Intent(Intent.ACTION_VIEW);
		    	in.setData(Uri.parse(st.ALL_APP_INMARKET));
		    	inst.startActivity(in);
				
			} catch (Throwable e) {
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    public String getCurrentDate() 
    {
    	String datetime = ""; 
    	datetime = "dd.MM.yyyy HH:mm:ss";
	    SimpleDateFormat sdf;
	    Date dt;
    	dt = new Date();
    	//dt.setTime(fi.dateedit);
		sdf = new SimpleDateFormat(datetime);
		datetime= sdf.format(dt);
    	
    	return datetime;
    }
	/** проверяем был ли послан текст для записи в буфер */	
    public boolean checkStartIntent()
    {
    	//Intent intent = new Intent();
    	Intent intent = getIntent();
    	if (intent == null)
    		return false;
    	String type = intent.getType ();
        String action = intent.getAction();
    	String txt = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (txt ==null){
        	record = null;
        	return false;
        }
    	boolean ret = false;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
            	addSaveText(txt);
            	//addSaveAppendText(txt);
            	ret = true;
            	//            	st.toast(getString(R.string.add));
            }
        }
        return ret;
    }
    public void addSaveText(String txt) {
    	record = txt;
        arFname = getFilenameArray();
    	if (Prefs.where_rec) {
        	if (arFname.length < 2)
        		addSaveStartText(txt, arFname[0]);
        	else
        		addSaveTextDialog(txt);
    	} else {
        	if (arFname.length < 2)
        		addSaveAppendText(txt, arFname[0]);
        	else
        		addSaveTextDialog(txt);
    	}
    	
    }
    public void addSaveTextDialog(final String addtext)
    {
    	if (record!=null)
    		btn_rec.setVisibility(View.VISIBLE);
        //arFname = getFilenameArray();
    	final String[] ars = new String[arFname.length+2];
    	ars[0] = inst.getString(R.string.cancel);
    	ars[1] = inst.getString(R.string.in_app);
    	for (int i=0;i<arFname.length;i++) {
    		ars[i+2]= arFname[i];
    	}
       	ArrayAdapter<String> ar = new ArrayAdapter<String>(this, 
       			R.layout.item_list,
                ars
                );
        
        Dlg.customMenu(this, ar, 
        		inst.getString(R.string.actions), 
        		new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                int pos = (((Integer)param1).intValue());
                if (pos == 0)
                	finish();
                else if (pos == 1)
                	return 0;
                else {
                	if (Prefs.where_rec)
                    	addSaveStartText(addtext,ars[pos]);
                	else
                		addSaveAppendText(addtext,ars[pos]);
                }
                	
			return 0;
            }
        });
    }
    /** добавляет текст в конец файла */
    public void addSaveAppendText(String txt, String fn)
    {
        if (!Perm.checkPermission(inst)) {
        	st.toast(R.string.perm_not_all_perm);
        	return;
        }
    	
    	//String fn = Prefs.getFilename();
    	File ff = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fn);
    	FileWriter wr;
    	try{
    		wr= new FileWriter(ff, true);
    		txt = getHeadRecordProcessing(txt);
		 	wr.append(txt);
		 	wr.flush();
		 	wr.close();
        	record = null;
        	st.toast(R.string.add);
        }  catch (IOException e) 
    	{
        	e.printStackTrace();
        	st.toast(R.string.add_error);
    	};
    	record = null;
    	
    	finish();

    }
    /** добавляет текст в начало файла */
    public void addSaveStartText(String txt, String fn)
    {
        if (!Perm.checkPermission(inst)) {
        	st.toast(R.string.perm_not_all_perm);
        	return;
        }
		txt = getHeadRecordProcessing(txt);
    	try {
			MyRandomAccessFile eraf = new MyRandomAccessFile(
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DOWNLOADS)+"/"+fn, "rw");
			eraf.insert(txt.getBytes(), 0);
			eraf.close();
        	record = null;
        	st.toast(R.string.add);
			
		} catch (Throwable e) {
		}
    	finish();
    }
    /** подготавливаем запись к записи */
    public String getHeadRecordProcessing(String rec)
    {
		String start = st.STR_NULL;
		String end= st.STR_LF;
		// добавляем дату1
		if (Prefs.rec_cb_date1)
			start=inst.getCurrentDate()+st.STR_LF;
		// добавляем разделитель
		if (Prefs.rec_cb_separator) {
			start+= Prefs.rec_et_separator+st.STR_LF;
			end = st.STR_LF+Prefs.rec_et_separator+st.STR_LF+st.STR_LF;
		}
		rec=start+rec+end; 

		String empty_str = st.STR_NULL;
		
		return rec;
	}
		
	    public String getFilenameString()
	    {
			String fn = Prefs.getFilename();
			if (fn==null|fn.startsWith(Prefs.FILENAME_DEF))
				return st.STR_NULL;
			String ar[] = fn.split(st.STR_COMMA);
			fn = st.STR_NULL;
			for (int i=0;i<ar.length;i++) {
				if (ar[i].isEmpty())
					continue;
				if (!ar[i].startsWith(st.STR_UNDERSCORING))
					ar[i]=st.STR_UNDERSCORING+ar[i];
				if (!ar[i].endsWith(Prefs.FILENAME_EXT_DEF))
					ar[i]+=Prefs.FILENAME_EXT_DEF;
				fn += ar[i]+st.STR_LF;
			}
			
		return fn;
    }
    public String[] getFilenameArray()
    {
		String fn = getFilenameString();
		if (fn==null)
			return new String[] {Prefs.FILENAME_DEF};
		if (fn.length()==0)
			return new String[] {Prefs.FILENAME_DEF};
		return fn.split(st.STR_LF);
    }
    /** сохраняем массив имён файлов в настройки если EditText менялся */
	public void saveFilename() {
		if (changed) {
        	String nm = et.getText().toString().trim();
        	if (nm.isEmpty())
        		Prefs.setFilename(Prefs.FILENAME_DEF);
        	else{
        		int ind = nm.indexOf(st.STR_LF);
        		// меняем \n на запятые
        		while (ind>-1) {
        			nm=nm.substring(0,ind)+st.STR_COMMA+nm.substring(ind+1);
            		ind = nm.indexOf(st.STR_LF);
        		}
        		Prefs.setFilename(nm);
        		et.setText(getFilenameString());
        		//setFilenameToEditText(nm);
        	}
        	changed = false;
        	st.toast(R.string.saved);
		}
	}
	public void savePrefs() {
		if (cb_where!=null)
			Prefs.setBoolean(Prefs.WHERE_RECORD, cb_where.isChecked());
		if (cb_rec_date1!=null) 
			Prefs.setBoolean(Prefs.RECORD_CB_DATE1, cb_rec_date1.isChecked());
		if (cb_rec_separ!=null) 
			Prefs.setBoolean(Prefs.RECORD_CB_SEPARATOR, cb_rec_separ.isChecked());
		if (et_rec_separ!=null) {
			Prefs.setString(Prefs.RECORD_LINE_SEPARATOR, et_rec_separ.getText().toString());
			et_rec_separ.setText(Prefs.rec_et_separator);
		}
		
		
	}
    
}