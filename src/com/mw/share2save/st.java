package com.mw.share2save;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class st extends Set
{
	//--------------------------------------------------------------------------
    /** Универсальный обсервер. Содержит 2 параметра m_param1 и m_param2, которые вызываются и меняются в зависимости от контекста*/
    public static abstract class UniObserver
    {
    /** Конструктор с двумя параметрами */
        public UniObserver(Object param1,Object param2)
        {
            m_param1 = param1;
            m_param2 = param2;
        }
    /** Пустой конструктор. Оба параметра - null*/
        public UniObserver()
        {
        }
    /** Вызов функции {@link #OnObserver(Object, Object)} с текущими параметрами*/
        public int Observ()
        {
        	return OnObserver(m_param1, m_param2);
        }
    /** Основная функция обработчика */ 
        public abstract int OnObserver(Object param1,Object param2);
    /** Пользовательский параметр 1 */  
        Object m_param1;
    /** Пользовательский параметр 2 */  
        Object m_param2;
    }
    public static Context getMainContext(){
    	if (MainActivity.inst!=null)
    		return MainActivity.inst;
    	return null;
    }
    // вывод сообщения длительностью 700мс
    public static void toast(String txt)
    {
        Context c = getMainContext();
        if (c!=null)
        	Toast.makeText(c, txt, 700).show();
   	}
    // вывод сообщения по id длительностью 700мс
    public static void toast(int id)
    {
        Context c = getMainContext();
        if (c!=null)
        	Toast.makeText(c, c.getString(id), 700).show();
   	}
    public static void toast(boolean cr,Integer ...id)
    {
    	String out = "";
        Context c = getMainContext();
        if (c==null)
        	return;
        for (int i=0; i<id.length;i++) {
        	if (cr)
        		out+=c.getString(id[i])+"\n";
        	else
        		out+=c.getString(id[i]);
        }
    	Toast.makeText(c, out, 700).show();
   	}
    public static void sleep(int ms)
    {
      	try {
      		Thread.sleep(ms); // спать ms милисекунд.
        } catch(Exception e){}    	        
   	}
    public static void showKbd(final EditText et)
    {
    	if (et==null)
    		return;
       (new Handler()).postDelayed(new Runnable() {

			public void run() {

                et.requestFocus();
                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));                       
               	et.setSelection(et.getText().toString().length());
            }
        }, 200);
    }
    public static void hideKbd(Activity con)
    {
    	try {
			InputMethodManager imm = (InputMethodManager) con.getSystemService(con.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(con.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
       	}catch(Throwable e) {}
    }

}