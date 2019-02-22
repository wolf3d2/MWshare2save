package com.mw.share2save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.TextView;

public class MainActivity extends Activity 
{
	String[] arFname = null;
	public static MainActivity inst = null;
//	TextView vers =null;
	TextView desc =null;
	TextView more =null;
	EditText et =null;
	Button save =null;
	CheckBox cb_where = null;
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inst = this;
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
        try{
            Prefs.init(this);
            Prefs.readPreference();
// пример записи нового параметра в настройки
//    		Pref.get().edit().putString("parameter", "value").commit();
        } catch (Throwable e) 
        {}
        arFname = getFilenameArray();
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
		
		et = (EditText) inst.findViewById(R.id.main_fname);
		et.setText(getFilenameString());
		et.addTextChangedListener(tw);
    	changed = false;

		//setFilenameToEditText(getFilenameString());
		
		save = (Button) inst.findViewById(R.id.main_save);
		save.setOnClickListener(m_ClickListener);

		cb_where = (CheckBox) inst.findViewById(R.id.cb_where_record);
		cb_where.setChecked(Prefs.where_rec);
		cb_where.setOnClickListener(m_ClickListener);
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
			String text = inst.getString(R.string.app_name)+st.STR_CR;
	        try{
	            String ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
	            String app = getString(R.string.version)+" "+ver;
	            text+=app+st.STR_CR+st.STR_CR;
	        }
	        catch (Throwable e) {}
	        text+=inst.getString(R.string.about_author)+inst.getString(R.string.about_author_desc);
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
		    	in.setData(Uri.parse(Set.ALL_APP_INMARKET));
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
    		txt=inst.getCurrentDate()+Set.STR_CR+Set.STR_RAZDELITEL
    				+Set.STR_CR+txt+Set.STR_CR+Set.STR_RAZDELITEL
    				+Set.STR_CR+Set.STR_CR;
			 	wr.append(txt);
			 	wr.flush();
			 	wr.close();
        	st.toast(R.string.add);
        }  catch (IOException e) 
    	{
        	e.printStackTrace();
        	st.toast(R.string.add_error);
    	};
    	
    	finish();

    }
    /** добавляет текст в начало файла */
    public void addSaveStartText(String txt, String fn)
    {
        if (!Perm.checkPermission(inst)) {
        	st.toast(R.string.perm_not_all_perm);
        	return;
        }
		txt=inst.getCurrentDate()+Set.STR_CR+Set.STR_RAZDELITEL
		+Set.STR_CR+txt+Set.STR_CR+Set.STR_RAZDELITEL
		+Set.STR_CR+Set.STR_CR;
    	
    	//fn = Prefs.getFilename();
    	try {
			MyRandomAccessFile eraf = new MyRandomAccessFile(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+fn,
					"rw");
			eraf.insert(txt.getBytes(), 0);
			eraf.close();
        	st.toast(R.string.add);
			
		} catch (Throwable e) {
		}
    	finish();
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
			fn += ar[i]+st.STR_CR;
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
		return fn.split(st.STR_CR);
    }
    /** сохраняем массив имён файлов в настройки если EditText менялся */
	public void saveFilename() {
		if (changed) {
        	String nm = et.getText().toString().trim();
        	if (nm.isEmpty())
        		Prefs.setFilename(Prefs.FILENAME_DEF);
        	else{
        		int ind = nm.indexOf(st.STR_CR);
        		while (ind>-1) {
        			nm=nm.substring(0,ind)+st.STR_COMMA+nm.substring(ind+1);
            		ind = nm.indexOf(st.STR_CR);
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
		if (cb_where!=null) {
			Prefs.setBoolean(Prefs.WHERE_RECORD, cb_where.isChecked());
		}
	}
    
}