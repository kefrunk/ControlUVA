package com.edward.controlUVA;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.edward.controlUVA.component.NivelAgua1;
import com.edward.controlUVA.component.NivelAgua2;
import com.edward.controlUVA.component.NivelHumedad1;
import com.edward.controlUVA.component.NivelHumedad2;
import com.edward.controlUVA.component.NivelPresion1;
import com.edward.controlUVA.component.NivelPresion2;


public class SensorShowActivity extends Activity{
	
	private final Handler handler = new Handler();
	private Intent ServerIntent;
	private static final int SENSOR_AGUA1 = 0;
	private static final int SENSOR_PRESION1 = 2;
	private static final int SENSOR_PRESION2 = 3;
	private static final int SENSOR_AGUA2 = 1;
	private static final int SENSOR_LUZ = 1;
	private static final int SENSOR_HUMEDAD2 = 5;
	private boolean id_sensor = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.niveles);	
		ServerIntent = new Intent(this, ServerService.class);
		}
	
	
	
	
	private BroadcastReceiver broadcastReceivero = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {       	   		
          		int sensor = intent.getExtras().getInt("sensor");
          		switch(sensor){
          		case SENSOR_AGUA1:
          			int valor = intent.getExtras().getInt("valor");
          			double percent = (double) (1024-valor)/ 1024;
            		int level = (int)Math.floor(percent*100);
              		NivelAgua1.setProgress(level);
              		Log.d("sensorAgua",level+"");
              		break;
          		case SENSOR_LUZ:
          			int valor2 = intent.getExtras().getInt("valor")-400;
          			double percent2 = (double) valor2/ 266;
            		int level2 = (int)Math.floor(percent2*100);
              		NivelPresion1.setProgress(level2);
              		Log.d("sensorLuz",level2+"");
              		break;
          		}
        }
    };
	
        
    private Runnable UpdateNiveles = new Runnable() {
    	public void run() {
    		if(id_sensor) ServerService.enviarOrden("L\n");
    		else ServerService.enviarOrden("l\n");
    		id_sensor = !id_sensor;
    	    handler.postDelayed(this, 500); // 4 seconds
    	}
    }; 
	
	@Override
	public void onResume() {
		super.onResume();
		startService(ServerIntent);
		handler.removeCallbacks(UpdateNiveles);
	    handler.postDelayed(UpdateNiveles, 4000); // 1 second
		registerReceiver(broadcastReceivero, new IntentFilter("agua"));
	}

	@Override
	public void onPause() {
		super.onPause();
		stopService(ServerIntent);
		handler.removeCallbacks(UpdateNiveles);
		unregisterReceiver(broadcastReceivero);
		}	

	


    
}
