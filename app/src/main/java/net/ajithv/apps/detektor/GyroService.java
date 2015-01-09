package net.ajithv.apps.detektor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GyroService extends Service {
	private final IBinder mBinder = new MyBinder();
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private MySensorListener mysensorListener;
	private LocationManager locationManager;
	private PowerManager pm;
    WakeLock wl;
    boolean running = false;
    private static String TAG = "GyroService";
    
    @Override
    public void onCreate() 
    {
    	super.onCreate();
    	Log.v("Gyro Service", "GyroService Created");
    }
    
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(running) return Service.START_NOT_STICKY;
		String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID); 
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyWakeLock");
		wl.acquire();

		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setAction("net.ajithv.apps.detektor.main");
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		Notification notification = new NotificationCompat.Builder(this)
		.setContentTitle("Gyro Service")
		.setContentText("Gyro Service")
		.setSmallIcon(R.drawable.ic_launcher)
		.setLargeIcon(
		Bitmap.createScaledBitmap(icon, 128, 128, false))
		.setContentIntent(pendingIntent)
		.setOngoing(true)
		.build();
		startForeground(101, notification);
		running = true;
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mysensorListener = new MySensorListener(android_id);
		
		sensorManager.registerListener(mysensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mysensorListener);
		
		Log.w("Gyro Service", "Registering SensorListener and LocationListener");
		
		Log.d("Gyro Service", "onStartCommand(): SOURCE = " + intent.getStringExtra("SOURCE"));

		boolean gpsproviderenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		Log.d("Gyro Service", "gpsproviderenabled:" + gpsproviderenabled);
		Log.d(TAG,"Getting potholes data");
        PotHoleReadings potHoleReadings = new PotHoleReadings().getInstance(this.getApplication());
        potHoleReadings.getAllpotHoles();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		GyroService getService() {
			return GyroService.this;
		}
	}

	
	public boolean getStatus() {
		return running;
	}
	
	@Override
	public void onDestroy() 
	{
	    super.onDestroy();
	    running = false;
		if(sensorManager != null && mysensorListener != null) sensorManager.unregisterListener(mysensorListener);
		if(locationManager != null && mysensorListener != null) locationManager.removeUpdates(mysensorListener);
	}

	
	
}