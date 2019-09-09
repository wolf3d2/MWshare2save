package com.mw.share2save;


import android.app.Application;
import android.content.res.Configuration;

public class App extends Application {

	@Override
	public void onCreate() {
        try{
            Prefs.init(this);
            Prefs.readPreference();
        } catch (Throwable e) {}
		
		Configuration config = new Configuration();
		getBaseContext().getResources().updateConfiguration(config, null);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
		Configuration config = new Configuration();
		getBaseContext().getResources().updateConfiguration(config, null);     
    }	
}