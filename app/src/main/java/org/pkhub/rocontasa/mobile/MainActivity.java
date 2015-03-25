package org.pkhub.rocontasa.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;


public class MainActivity extends Activity implements OnClickListener {
    private GyroService s;
    Intent service;
    private LocationManager locationManager;
    private ToggleButton sftyAlrmngTglBtn;
    private static String TAG = "MainActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeServices();
        setListeners();
        GlobalContext.getInstance().setCtx(getApplicationContext());
    }
    // All views used in the class initializes in this method.
    private void initializeViews(){
	    sftyAlrmngTglBtn = (ToggleButton)findViewById(R.id.sftyAlrmngTglBtn);
    }

    private void initializeServices(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    // All UI element Event Listeners
    private void setListeners(){
	    sftyAlrmngTglBtn.setOnClickListener(this);
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
    	        if(s.getStatus()) sftyAlrmngTglBtn.setChecked(true); else  sftyAlrmngTglBtn.setChecked(false);
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
        Log.d(TAG,"onClick enter");
	    switch(view.getId()){
            case R.id.sftyAlrmngTglBtn:
                Log.d(TAG,"case sftyAlrmngTglBtn");
                if (sftyAlrmngTglBtn.isChecked()) {
                    // Check GPS is enabled or not. If not enabled? ask user to enable it first.
                    // Need to redirect to settings or not?
                    // Start GPS service to provide safety alarming
                    Toast.makeText(MainActivity.this, "111 onSbmtPthlsTglBtnClicked", Toast.LENGTH_SHORT).show();
                    checkGPSEnabledAndProceed();
                } else {
                    // Stop GPS service to provide safety alarming
                    stopPthlsDtctnsService();
                    Toast.makeText(MainActivity.this, "000 onSbmtPthlsTglBtnClicked", Toast.LENGTH_SHORT).show();
                }
                break;
	    }
        Log.d(TAG,"onClick exit");
    }
    private boolean isGPSEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private void checkGPSEnabledAndProceed(){
        if(isGPSEnabled()){
            service = new Intent(this, GyroService.class);
            service.putExtra("SOURCE", "MAIN");
            this.startService(service);
            //status.setText("Running");
            Toast.makeText(MainActivity.this, "GPS is enabled.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "GPS is disabled, show alert", Toast.LENGTH_SHORT).show();
            //buildAlertMessageNoGps();
        }
    }
    private void stopPthlsDtctnsService(){
        if (s != null) {
            if(s.wl != null) s.wl.release();
            s.stopForeground(true);
            s.stopSelf();
            s.onDestroy();
            //status.setText("Stopped");
        }
    }
}