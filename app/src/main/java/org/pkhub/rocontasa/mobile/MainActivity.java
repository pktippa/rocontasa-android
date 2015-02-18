package org.pkhub.rocontasa.mobile;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements OnClickListener {
  private GyroService s;
  Intent service;
  TextView status;
  private Button start,stop;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeViews();
    setListeners();
      GlobalContext.getInstance().setCtx(getApplicationContext());
  }
  // All views used in the class initializes in this method.
  private void initializeViews(){
	  start=(Button)findViewById(R.id.startService);
	  stop=(Button)findViewById(R.id.stopService);
	  status = (TextView)findViewById(R.id.textView2);
  }
  
  // All UI element Event Listeners 
  private void setListeners(){
	  start.setOnClickListener(this);
	  stop.setOnClickListener(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Bind the service to control the service and get service statuses
    Intent intent= new Intent(this, GyroService.class);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onPause() {
    super.onPause();
    // Unbind the service
    unbindService(mConnection);
  }

  // The service connection object to bind the Gyro Service
  private ServiceConnection mConnection = new ServiceConnection() {

	// callback method if the service is connected.
    public void onServiceConnected(ComponentName className, IBinder binder) {
    	GyroService.MyBinder b = (GyroService.MyBinder) binder;
      s = b.getService();
      if(s != null) {
    	  if(s.getStatus()) status.setText("Running"); else  status.setText("Stopped"); 
      }
      Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
    }
    // callback method if the service is disconnected.
    public void onServiceDisconnected(ComponentName className) {
      s = null;
      Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
    }
  };

  public void onClick(View view) {
	  switch(view.getId()){
	  case R.id.startService:
		  // Start Gyro Service
		  service = new Intent(this, GyroService.class);
	    	service.putExtra("SOURCE", "MAIN");
		    this.startService(service);
		    status.setText("Running");
		  break;
	  case R.id.stopService:
		  // Stop Gyro Service - release wake lock
		  if (s != null) {
			  if(s.wl != null) s.wl.release();
		      s.stopForeground(true);
		      s.stopSelf();
		      s.onDestroy();
		      status.setText("Stopped");
		  }
		  break;
	  }
  }
}