package com.mw.share2save;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
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
		String str = clip.getItemAt(0).getText().toString();
        Intent in = new Intent(Intent.ACTION_VIEW)
        .setComponent(new ComponentName(mact, MainActivity.class))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Intent.EXTRA_TEXT, str)
        .setType("text/plain");
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mact);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.ic_launcher)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Напоминание")
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Пора покормить кота") // Текст уведомления
                // необязательные настройки
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher)) // большая
                // картинка
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
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
