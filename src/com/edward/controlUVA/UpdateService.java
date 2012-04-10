package com.edward.controlUVA;

import java.util.Calendar;
import java.util.Date;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class UpdateService  extends Service {
	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.edward.controlUVA.updateevent";
	private final Handler handler = new Handler();
	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() {
		super.onCreate();

    	intent = new Intent(BROADCAST_ACTION);	
	}

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
   
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		DisplayLoggingInfo();    		
    	    handler.postDelayed(this, 500); // 10 seconds
    	}
    };    
    
    private void DisplayLoggingInfo() { 
    	SQLiteDatabase db = (new DatabaseHelper(UpdateService.this)).getReadableDatabase();
        Cursor mCursor = db.query("dias", new String[] {"*"}, 
          		null, null, null, null, null);  
        mCursor.moveToFirst();     	
    	intent.putExtra("alarm",0);
    	intent.putExtra("aceptada",0);
    	intent.putExtra("hora",mCursor.getString(2));
    	intent.putExtra("R1", mCursor.getString(3));
    	intent.putExtra("R2", mCursor.getString(4));
    	intent.putExtra("AB1", mCursor.getString(5));
    	intent.putExtra("AB2", mCursor.getString(6));
    	intent.putExtra("SYS_ON", mCursor.getString(7));
    	intent.putExtra("AUTO_ON", mCursor.getString(8));
    	intent.putExtra("dias", mCursor.getString(1));
    	mCursor.close();
    	db.close();
    	sendBroadcast(intent);
    }
    
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}		
}