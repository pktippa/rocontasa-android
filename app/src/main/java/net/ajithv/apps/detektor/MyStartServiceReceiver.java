package net.ajithv.apps.detektor;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MyStartServiceReceiver extends WakefulBroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Intent service = new Intent(context, GyroService.class);
    service.putExtra("SOURCE", "TIMER");
    context.startService(service);
  }
} 