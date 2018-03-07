package com.mw.share2save;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

// уведомление в шторке
public class Notif 
{
	static MainActivity mact;
	final public static int NOTIFY_ALL = 0;
	final public static int NOTIFY_ID = 1;
	NotificationManager notificationManager = null;
	
	public Notif(MainActivity act) {
		mact = act;
	}
	@SuppressLint("NewApi")
	public void createNotif()
	{
		ClipboardManager cl= (ClipboardManager) mact.getSystemService(mact.CLIPBOARD_SERVICE);
		ClipData clip = cl.getPrimaryClip();
		String str = st.STR_NULL;
		if (clip != null)
			str = clip.getItemAt(0).getText().toString();
//        Intent in = new Intent(Intent.ACTION_SEND)
//        .setAction(Intent.ACTION_SEND)
//        .setComponent(new ComponentName(mact, MainActivity.class))
//        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        .putExtra(Intent.EXTRA_TEXT, str)
//        .setType("text/plain");
		
        Intent in = new Intent();
        in.setAction(Intent.ACTION_SEND);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra(Intent.EXTRA_TEXT, str);
        in.setType("text/plain");
        in.setComponent(new ComponentName(mact, MainActivity.class));
        in.setClipData(clip);

        //mact.startActivity(in);
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "bbb");
//        sendIntent.setType("text/plain");
        //mact.startActivity(sendIntent);
        PendingIntent sendPendingIntent = PendingIntent.getActivity(mact,
                0, in,
                PendingIntent.FLAG_CANCEL_CURRENT);

		Intent notificationIntent = new Intent(mact, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mact,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = mact.getResources();
		
        // до версии Android 8.0 API 26
        Notification.Builder builder = new Notification.Builder(mact);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.ic_launcher)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(res.getString(R.string.app_name))
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(res.getString(R.string.add_sel)) // Текст уведомления
                // необязательные настройки
             // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
             // текст в строке состояния
                //.setTicker(res.getString(R.string.warning)) 
                .setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .addAction(R.drawable.ic_launcher, "Сохранить",
                		sendPendingIntent)
                //.setAutoCancel(true);

                .setAutoCancel(false); // автоматически закрыть уведомление после нажатия

        notificationManager =
                (NotificationManager) mact.getSystemService(Context.NOTIFICATION_SERVICE);
		// Альтернативный вариант
		// NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, builder.build());		
	}
	
	public void dismiss(int id)
	{
		if (notificationManager==null)
			return;
		// если id = 0, то удаляем все уведомления программы
		switch (id)
		{
		case NOTIFY_ALL:
			notificationManager.cancelAll();
			break;
		case NOTIFY_ID:
			notificationManager.cancel(NOTIFY_ID);
			break;
		
		}
	}
}
