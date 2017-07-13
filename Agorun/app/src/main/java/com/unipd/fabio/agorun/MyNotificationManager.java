package com.unipd.fabio.agorun;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MyNotificationManager extends BroadcastReceiver {

    private static final int TIMER = 10;
    
    private String timeForNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            System.out.println("SONO ANCORA QUI");
            String myFullHour = MySharedPreferencesHandler.getMySharedPreferencesString(context, MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour, "");
            String myDate = MySharedPreferencesHandler.getMySharedPreferencesString(context, MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate, "");

            // Parso l'orario così da togliere i secondi e tenere soltando l'ora ed i minuti.
            String[] startParsed = myFullHour.split(":");
            String[] newHour = Arrays.copyOf(startParsed, startParsed.length - 1);

            setNotificationTime(newHour);



            Date d = new Date();
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
            if (myDate.equals(date.format(d)) && timeForNotification.equals(hour.format(d))){
                Intent it =  new Intent(context, Fragment1.class);
                createNotification(context, it, "Agorun - Activity Reminder", "Agorun - Time to get ready!", "It's almost time! Get ready for your activity!");
            }
        }catch (Exception e){
            Log.i("date","error == "+e.getMessage());
        }
    }

    public void createNotification(Context context, Intent intent, CharSequence ticker, CharSequence title, CharSequence descricao){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(p);
        builder.setPriority(Notification.PRIORITY_MAX);
        Notification n = builder.build();
        //create the notification
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.drawable.amu_bubble_mask, n);
        //create a vibration
        try{

            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }
        catch(Exception e){}
    }

    // Metodo che controlla e setta il tempo per la ricezione della notifica. Questa verrà mandata 10 minuti prima dell'inizio dell'attività, per cui si controllano i minuti per un eventuale cambio d'ora.
    private void setNotificationTime(String[] newHour) {
        int activityHour = Integer.parseInt(newHour[0]);
        int activityM = Integer.parseInt(newHour[1]);

        if (activityM > 9 && activityM <= 59) {
            activityM -= TIMER;
        } else if (activityM >= 0 && activityM <= 9) {
            int temp = 60 + activityM;
            temp -= TIMER;
            activityM = temp;
            activityHour--;
        }


        timeForNotification = activityHour + ":" + activityM;

        
    }

    public static MyNotificationManager getNewInstance() {
        return new MyNotificationManager();
    }
}
