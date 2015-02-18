package org.pkhub.rocontasa.mobile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONArray;

public class MySensorListener implements SensorEventListener, LocationListener {

/*	private static final int SHAKE_THRESHOLD = 600;
	private long lastUpdate = 0; 
	private float last_x, last_y, last_z;
*/	private float x, y, z;                         // To hold acccelerometer data
	private double speed, latitude, longitude;     // To hold location data
	private long gpsDate, accelerometerDate;       // to hold timestamps for accelerometer and location data
	private StringBuffer data = new StringBuffer();// Buffer to hold data until it is written to CSV
	private String android_id;                     // Unique ID of device
	private String datafilename = null;            // Name of CSV file to write to.
	private String datafilenamebase;               // Base Name of CSV file to write to.
	private String upload_service_url = "http://ajithv.net/detektor/upload";
	private long csvfilesizelimit = 1 * 200 * 1024;  // in bytes  
	private long buffersizelimit = 100 * 1024;     // in bytes 
    TextToSpeech tts;
    float potholeWarningDistance = 20.0f;
    private static double lat= 0.0,lng=0.0;
    private static int counter;
	String TAG = "MySensorListener";
	public MySensorListener(String android_id) {
		super();
		this.android_id = android_id;
		File folder = new File(Environment.getExternalStorageDirectory() + "/GyroService");
		if (!folder.exists()) folder.mkdir();
		datafilenamebase = folder.toString() + "/data";
	}

	// This method is called whenever new sensor (accelerometer) data is available
	@Override
	public void onSensorChanged(SensorEvent event) {

		Sensor mySensor = event.sensor;

		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			
			accelerometerDate = System.currentTimeMillis();
			// Get accelerometer values
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			
			// Write accelerometer data to CSV and send to server
			writeToCSV(android_id + ", A," + accelerometerDate + "," + x + "," + y + "," + z);
			
			Log.w("MySensorListener", "onSensorChanged: [" + accelerometerDate 	+ ": " + x + ", " + y + ", " + z + "]");

			/*
			 * long curTime = System.currentTimeMillis();
			 * 
			 * if ((curTime - lastUpdate) > 100) { long diffTime = (curTime -
			 * lastUpdate); lastUpdate = curTime;
			 * 
			 * float speed = Math.abs(x + y + z - last_x - last_y - last_z)/
			 * diffTime * 10000;
			 * 
			 * if (speed > SHAKE_THRESHOLD) { Log.w("MySensorListener",
			 * "Device Shook"); }
			 * 
			 * last_x = x; last_y = y; last_z = z; }
			 */}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d("MySensorListener", "onAccuracyChanged:" + accuracy);
	}

	
	
	// This method is called whenever new location data is available
	@Override
	public void onLocationChanged(Location location) {

		gpsDate = System.currentTimeMillis();
		
		// get location data
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		speed = location.getSpeed();
		
		// Write location data to CSV and send to server
		writeToCSV(android_id + ",G," + gpsDate + "," + speed + "," + latitude + "," + longitude);

        new Thread() {
            public void run() {
                JSONArray jsonArray = new PotHoleReadings().getInstance(GlobalContext.getInstance().getCtx()).getAllpotHoles();
                for ( int i = 0; i < jsonArray.length(); i++)
                {
                    try {
                        checkIfNearPothole(jsonArray.getJSONObject(i).getDouble("x"), jsonArray.getJSONObject(i).getDouble("y"), latitude, longitude);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();



        Log.d("MySensorListener", "onLocationChanged: [" + gpsDate + ":" + speed + ", " + latitude + ", " + longitude + "]");

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	
	
	// Collects data into a buffer till it reaches 100kB. 
	// Once 100kB is reached, data is appended to the CSV file and buffer is cleared.
	// The above repeats ill CSV file size reaches 1MB.
	// Once file size reaches 1MB, it is compressed and sent to server.
	
	public void writeToCSV(final String line) 
	{
		{
			data.append(line + "\n");   // Add incoming line data to buffer
			
			if(data.length() < buffersizelimit)  // Do not write file if buffer is less than 100kB 
			{
				Log.d("MySensorListener", "writeToCSV: Buffer not full. Not writing to csv file.");
				return;
			}
			
			Log.d("MySensorListener", "writeToCSV: Buffer is full. Writing to csv file.");
			final String dataToWrite = new String(data);
			data = new StringBuffer();  // Resetting buffer	
			if(datafilename == null) datafilename = datafilenamebase + System.currentTimeMillis() + ".csv";
			
			// Write CSV file asynchrounously
			new Thread() {
				public void run() {
					try 
					{
						FileWriter fw = new FileWriter(datafilename, true);
						fw.append(dataToWrite);
						fw.flush();
						fw.close();
						final File csvFile = new File(datafilename);
				
						new Thread() {
							public void run() {
								if(csvFile.length() > csvfilesizelimit)   // Check if CSV file size > 2MB
								{
									datafilename = null;
									Log.d("MySensorListener", "writeToCSV: CSV size above 1MB. Sending to server and deleting");
									try {
										String csvString = new Scanner( csvFile ).useDelimiter("\\A").next();  // Read CSV file contents into String
										csvFile.delete();
										byte[] bytes = compress(csvString);
										upload(bytes);
									} catch (Exception e) {
										e.printStackTrace();
									}    
								}
							}
						}.start();
						
					} catch (Exception e) {
						Log.e("MySensorListener", "writeToCSV: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	
	// Method to compress String using GZip algorithm
	public static byte[] compress(String string) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
	    GZIPOutputStream gos = new GZIPOutputStream(os);
	    gos.write(string.getBytes());
	    gos.close();
	    byte[] compressed = os.toByteArray();
	    os.close();
	    return compressed;
	}
	
	
	// Not used here, but given for getting logic to be written in NodeJS.
	public static String decompress(byte[] compressed) throws IOException {
	    final int BUFFER_SIZE = 32;
	    ByteArrayInputStream is = new ByteArrayInputStream(compressed);
	    GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
	    StringBuilder string = new StringBuilder();
	    byte[] data = new byte[BUFFER_SIZE];
	    int bytesRead;
	    while ((bytesRead = gis.read(data)) != -1) {
	        string.append(new String(data, 0, bytesRead));
	    }
	    gis.close();
	    is.close();
	    return string.toString();
	}
	
	
	// POST file via multipart form-data
	public void upload(byte[] bytes)
	{
		try {
	        URL url = new URL(upload_service_url);

	        String attachmentName = "data";
	        String attachmentFileName = "data.csv.gz";
	        String crlf = "\r\n";
	        String twoHyphens = "--";
	        String boundary =  "*****";
	        HttpURLConnection httpUrlConnection = null;
	        httpUrlConnection = (HttpURLConnection) url.openConnection();
	        httpUrlConnection.setUseCaches(false);
	        httpUrlConnection.setDoOutput(true);

	        httpUrlConnection.setRequestMethod("POST");
	        httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
	        httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
	        httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	        DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

	        request.writeBytes(twoHyphens + boundary + crlf);
	        request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
	        request.writeBytes(crlf);
	        request.write(bytes);
	        request.writeBytes(crlf);
	        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
	        request.flush();
	        request.close();
	        InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());

	        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
	        String line = "";
	        StringBuilder stringBuilder = new StringBuilder();
	        while ((line = responseStreamReader.readLine()) != null)
	        {
	            stringBuilder.append(line).append("\n");
	        }
	        responseStreamReader.close();

	        String response = stringBuilder.toString();
	        Log.d("MySensorListener", response);
	        responseStream.close();
	        httpUrlConnection.disconnect();

	    } catch (Exception e) {
            Log.d("Detektor", "Exception while uploading data " + e.getMessage());
	        e.printStackTrace();
	    }
	}

    private void checkIfNearPothole(double startLatitude, double startLongitude,
                                    double endLatitude, double endLongitude){
        float res[] = new float[] {1f,1f,1f};
        if(lat == 0.0 && lng == 0.0){
            lat =startLatitude;lng=startLongitude;
        }
        Location.distanceBetween(startLatitude,startLongitude,endLatitude,endLongitude,res);
        float dis = res[0];
        Log.d(TAG, "distance : " + dis);
        if(dis <= potholeWarningDistance){
            Log.d(TAG,"Context is : " + GlobalContext.getInstance().getCtx());
            if(lat == startLatitude && lng == startLongitude){
                counter++;
            }else{
                lat=lng=0;
                counter=0;
            }
            if(counter<3){
                tts= new TextToSpeech(GlobalContext.getInstance().getCtx(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            tts.setLanguage(Locale.UK);
                        }
                    }
                }
                );
                tts.speak("Pot Hole in less than " + (int)potholeWarningDistance + " metres. Please slow down!", TextToSpeech.QUEUE_FLUSH, null);
            }

        }
    }



}
