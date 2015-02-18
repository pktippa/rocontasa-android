package org.pkhub.rocontasa.mobile;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MyScheduleReceiver extends WakefulBroadcastReceiver {

  // restart service every 30 seconds
  private static final long REPEAT_TIME = 1000 * 30;

  @Override
  public void onReceive(Context context, Intent intent) {
    AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent i = new Intent(context, MyStartServiceReceiver.class);
    PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    Calendar cal = Calendar.getInstance();
    // start 30 seconds after boot completed
    cal.add(Calendar.SECOND, 30);
    // fetch every 30 seconds
    service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);
  }
} 