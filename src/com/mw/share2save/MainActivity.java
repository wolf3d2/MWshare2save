package com.mw.share2save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	public static final String CR = "\n";
	public static final String RAZDELITEL = "==========";
	public static MainActivity inst = null;
	RadioButton rb1=null;
	RadioButton rb2=null;
	TextView vers =null;
	TextView desc =null;
	TextView more =null;
	TextView method_desc =null;
	TextView method_more =null;
	EditText et =null;
	Button save =null;
	boolean bdescmore = false;
	boolean bmetmore = false;
	Notif notif = null;
	
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
//    		Pref.get().edit().putString("sss", "ssss").commit();
        } catch (Throwable e) 
        {}
		// проверяем был ли послан текст для записи	
        checkStartIntent();
        notif = new Notif(inst);
		vers = (TextView) inst.findViewById(R.id.main_vers);
        try{
            String ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String app = getString(R.string.version)+" "+ver;
            vers.setText(app);
        }
        catch (Throwable e) {}
		desc = (TextView) inst.findViewById(R.id.main_desc);
		desc.setOnClickListener(m_ClickListener);
		more = (TextView) inst.findViewById(R.id.main_read_more);
		more.setOnClickListener(m_ClickListener);
//		method_desc = (TextView) inst.findViewById(R.id.main_method_desc);
//		method_desc.setOnClickListener(m_ClickListener);
//		method_more = (TextView) inst.findViewById(R.id.main_method_more);
//		method_more.setOnClickListener(m_ClickListener);
		
		et = (EditText) inst.findViewById(R.id.main_fname);
		setFilename(Prefs.getFilename());
		
		save = (Button) inst.findViewById(R.id.main_save);
		save.setOnClickListener(m_ClickListener);
//		rb1 = (RadioButton) inst.findViewById(R.id.rbtn1);
//		rb1.setOnClickListener(m_ClickListener);
//		rb2= (RadioButton) inst.findViewById(R.id.rbtn2);
//		rb2.setOnClickListener(m_ClickListener);
		setMethodChecked(-1);

	}
	private void setMethodChecked(int mode)
	{
		// вызов при старте программы
		if (mode == -1)
			mode = Prefs.getMethod();
		Prefs.setInt(Prefs.METHOD, mode);
//		switch (mode)
//		{
//		case 1:
//			rb1.setChecked(true);
//			rb2.setChecked(false);
//			notif.dismiss(Notif.NOTIFY_ID);;
//			break;
//		case 2:
//			rb1.setChecked(false);
//			rb2.setChecked(true);
//			notif.createNotif();
//			break;
//		}
		
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
            case R.id.main_save:
            	String nm = et.getText().toString().trim();
            	if (nm.isEmpty())
            		Prefs.setFilename(Prefs.FILENAME_DEF);
            	else{
            		setFilename(nm);
            		Prefs.setFilename(et.getText().toString().trim());
            	}
            	st.toast(R.string.saved);
            	return;
            case R.id.main_desc:
            case R.id.main_read_more:
            	if (bdescmore){
            		bdescmore = false;
            		desc.setMaxLines(2);
            		more.setText(R.string.read_more1);
            	} else {
            		bdescmore = true;
            		desc.setMaxLines(25);
            		more.setText(R.string.read_more2);
            		
            	}
                return;
//            case R.id.rbtn1:
//            	setMethodChecked(1);
//            	break;
//            case R.id.rbtn2:
//            	setMethodChecked(2);
//            	break;
//            case R.id.main_method_desc:
//            case R.id.main_method_more:
//            	if (bmetmore){
//            		bmetmore = false;
//            		method_desc.setMaxLines(2);
//            		method_more.setText(R.string.read_more1);
//            	} else {
//            		bmetmore = true;
//            		method_desc.setMaxLines(25);
//            		method_more.setText(R.string.read_more2);
//            		
//            	}
//                return;
            }
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId())
		{
		case R.id.action_other_app:
	    	Intent intent = new Intent(Intent.ACTION_VIEW);
	    	intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Михаил+Вязенкин"));
	    	inst.startActivity(intent);
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
	// проверяем был ли послан текст для записи в буфер	
    public void checkStartIntent()
    {
    	//Intent intent = new Intent();
    	Intent intent = getIntent();
    	if (intent == null)
    		return;
    	String type = intent.getType ();
        String action = intent.getAction();
    	String txt = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (txt ==null){
        	return;
        }
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
            	save(txt);
            	//            	st.toast(getString(R.string.add));
            }
        }
    }
    public void save (String txt)
    {
        if (!Perm.checkPermission(inst)) {
        	st.toast(R.string.perm_not_all_perm);
        	return;
        }
    	
    	String fn = Prefs.getFilename();
    	File ff = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fn);
    	FileWriter wr;
    	try{
    		wr= new FileWriter(ff, true);
    		txt=inst.getCurrentDate()+CR+RAZDELITEL+CR+txt+CR+RAZDELITEL+CR+CR;
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
    public void setFilename(String txt)
    {
    	if (txt== null)
    		return;
		if (txt.compareTo(Prefs.FILENAME_DEF)!=0){
			if (!txt.startsWith("_"))
				txt="_"+txt;
			if (!txt.endsWith(Prefs.FILENAME_EXT_DEF)){
				txt += Prefs.FILENAME_EXT_DEF;
			}
		} else
			txt = "";
		if (et!=null)
			et.setText(txt);
   	
    }
}