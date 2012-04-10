package com.edward.controlUVA;


import java.net.Socket;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private Intent intent;
    /** Called when the activity is first created. */
	public String diasSel="";	
	private CheckBox lunes;
	private CheckBox martes;	
	private CheckBox miercoles;
	private CheckBox jueves;
	private CheckBox viernes;
	private CheckBox sabado;
	private CheckBox domingo;
	private Button DaysButton;
	private int mHour;
    private int mMinute;
    private TextView horaButton;
    private Resources res;
    private Intent ServerIntent;
	ServiceConnection _connection;
    static final int TIME_DIALOG_ID = 0;
    static final int DAYS_DIALOG_ID = 1;
    static final int AGUA_ID = 7;
    private TextView IndEstadoTxt;
	private TextView TimeEstadoTxt;
	//private TextView ParpadeanteTxt;
	private TextView AutoONTxt;
	private Button R1Button;
	private Button R2Button;
	private Button ABS1Button;
	private Button ABS2Button;
	private Button SysONButton;
	private Button AutoONButton;
	private Button ConfigButton;
	private Button OkConfigButton;
	//private Button ConfigTipoButton;
	//private CheckBox InformeShowCkBx;
	//private ScrollView InformeScrView;
	private RelativeLayout PantallaConfig;
	private RelativeLayout PantallaGeneral;	
	private Boolean R1_activo = false;
	private Boolean R2_activo = false;
	private Boolean ABS1_activo = false;
	private Boolean ABS2_activo = false;
	private Boolean SysON_activo = false;
	private Boolean AutoON_activo = false;
	private String TipoProg = "P1";
	private String orden ="";
	private ProgressDialog dialogwait;
	private Socket socket = null;
    private int count;
    private byte [] ipAddress = new byte[] {(byte)192,(byte)168,(byte)1,(byte)3};
    private int puerto = 5205;
    private String mensText = "Abrir Puerta";
    private boolean finalizada = false;
    private static final int CONFIG = Menu.FIRST;
    private static final int IP = Menu.FIRST + 1;
    private static final int MSG = Menu.FIRST + 2;
    private static final int AGUA = Menu.FIRST + 3;
    private static final int PROG = Menu.FIRST + 4;
    private static final int DIALOGO_IP = 2;
    private static final int DIALOGO_MSG = 3;
    private static final int ID_DIALOG_LOADING = 4;
    private Toast mToast;
	private SoundPool soundPool;
	private int ID_SOUND_INICIO;
	private int ID_SOUND_E1;
	private int ID_SOUND_E2;
	private int ID_SOUND_E3;
	private int ID_SOUND_E4;
    //private InetAddress addr;

	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
       initConfig(); //Inicializacion de las views y otras variables
       
       
       // Definicion de onClick de los botones //
       horaButton.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
	    		    	}
	    	});
              
       DaysButton.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				showDialog(DAYS_DIALOG_ID);
	    		    	}
	    	});
       
       R1Button.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
			R1_activo = !R1_activo;
			if(R1_activo) orden ="R\n";
			else orden ="r\n";
			SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
     		ContentValues cv = new ContentValues();
			cv.put(DatabaseHelper.R1,R1_activo.toString());
			db.update("dias",cv,"_id=1",null); 
	      	db.close();
	      	showDialog(ID_DIALOG_LOADING);			
			Thread background =new Thread (new Runnable(){
				public void run(){				
					ServerService.enviarOrden(orden);
			}
					});
			
			background.start();
			
			}
	    });
       
       R2Button.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {				
				R2_activo = !R2_activo;
				if(R2_activo) orden ="D\n";
				else orden ="d\n";
				SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
	     		ContentValues cv = new ContentValues();
				cv.put(DatabaseHelper.R2,R2_activo.toString());
				db.update("dias",cv,"_id=1",null); 
		      	db.close();
				showDialog(ID_DIALOG_LOADING);
    			Thread background =new Thread (new Runnable(){
    				public void run(){
    					ServerService.enviarOrden(orden);
    			}
    					});
    			
    			background.start();
			
			}
	    });
       ABS1Button.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				ABS1_activo = !ABS1_activo;
				if(ABS1_activo) orden ="B\n";
				else orden ="b\n";
				SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
	     		ContentValues cv = new ContentValues();
				cv.put(DatabaseHelper.AB1,ABS1_activo.toString());
				db.update("dias",cv,"_id=1",null); 
		      	db.close();
				showDialog(ID_DIALOG_LOADING);
    			Thread background =new Thread (new Runnable(){
    				public void run(){
    					ServerService.enviarOrden(orden);
    			}
    					});   			
    		background.start();

			}
	    });
       ABS2Button.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				ABS2_activo = !ABS2_activo;
				if(ABS2_activo) orden ="N\n";
				else orden ="n\n";
				SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
	    		ContentValues cv = new ContentValues();
				cv.put(DatabaseHelper.AB2,ABS2_activo.toString());
				db.update("dias",cv,"_id=1",null); 
		      	db.close();
				showDialog(ID_DIALOG_LOADING);
    			Thread background =new Thread (new Runnable(){
    				public void run(){
    					ServerService.enviarOrden(orden);
    			}
    					});
    			
    			background.start();
			
			}
	    });
       SysONButton.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				SysON_activo = !SysON_activo;
				if(SysON_activo) orden ="S\n";
				else orden ="s\n";
				SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
	   		ContentValues cv = new ContentValues();
				cv.put(DatabaseHelper.SYS_ON,SysON_activo.toString());
				db.update("dias",cv,"_id=1",null); 
		      	db.close();
				showDialog(ID_DIALOG_LOADING);
    			Thread background =new Thread (new Runnable(){
    				public void run(){
    					ServerService.enviarOrden(orden);
    			}
    					});
    			
    			background.start();

			}
	    });
       AutoONButton.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				AutoON_activo = !AutoON_activo;
				if(AutoON_activo) orden ="T\n";
				else orden ="t\n";
				SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
	   		ContentValues cv = new ContentValues();
				cv.put(DatabaseHelper.AUTO_ON,AutoON_activo.toString());
				db.update("dias",cv,"_id=1",null); 
		      	db.close();
				showDialog(ID_DIALOG_LOADING);
    			Thread background =new Thread (new Runnable(){
    				public void run(){
    					ServerService.enviarOrden(orden);
    			}
    					});
    			
    			background.start();

			}
	    });
     //definimos la acci�n del bot�n Go_PantallaConfig_btn
   	ConfigButton.setOnClickListener(new View.OnClickListener() {
			@Override 		    	    		    	    		    	    		    	 
	    	public void onClick(View v) {
				PantallaGeneral.setVisibility(View.INVISIBLE);	
				PantallaConfig.setVisibility(View.VISIBLE);
	    		    	}
	    	});
  //definimos la acci�n del bot�n Go_PantallaGeneral_btn
	OkConfigButton.setOnClickListener(new View.OnClickListener() {
		@Override 		    	    		    	    		    	    		    	 
    	public void onClick(View v) {
			PantallaConfig.setVisibility(View.INVISIBLE);	
			PantallaGeneral.setVisibility(View.VISIBLE);
    		    	}
    	});
   	
    }
    
	@Override
	public void onResume() {
		super.onResume();		
		startService(intent);
		startService(ServerIntent);
		registerReceiver(broadcastReceiver, new IntentFilter(UpdateService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent); 	
		stopService(ServerIntent); 
	}	
    private String checkEstado() {
    	String estado="";
    	if (SysON_activo)
    	{
    		if(R1_activo || R2_activo) 
    		{
    			estado="REGANDO";
    			if(R1_activo)
    			{
    				estado = estado +" SECTOR 1 ";
    				if(R2_activo) estado = estado +"Y 2 ";
    			}
    			else estado = estado + " SECTOR 2 ";
    			estado = estado +"\n ";
    		}
    		else estado=" ";
    		
    		if(ABS1_activo || ABS2_activo) 
    		{
    			estado=estado +"ABONANDO";
    			if(ABS1_activo)
    			{
    				estado = estado +" SECTOR 1 ";
    				if(ABS2_activo) estado = estado +"Y 2";
    			}
    			else estado = estado + " SECTOR 2";
    		}
    		if(!ABS1_activo && !ABS2_activo && !R1_activo && !R2_activo) estado="RIEGO APAGADO";
    		
    	}
    	else estado="RIEGO APAGADO";
    	return  estado;
    }

    
    @Override
    protected Dialog onCreateDialog(int id) {
    	 
        switch (id) {
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mTimeSetListener, mHour, mMinute, false);
        case DAYS_DIALOG_ID:
        	
        	// This example shows how to add a custom layout to an AlertDialog
            LayoutInflater factory1 = LayoutInflater.from(this);
            final View textEntryView1 = factory1.inflate(R.layout.ckbx_selectdays, null);
            return new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.waterDays)
                .setView(textEntryView1)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	
                        /*  */
                    	lunes = (CheckBox) textEntryView1.findViewById (R.id.checkBox1);
                    	martes = (CheckBox) textEntryView1.findViewById (R.id.checkBox2);
                    	miercoles = (CheckBox) textEntryView1.findViewById (R.id.checkBox3);
                    	jueves = (CheckBox) textEntryView1.findViewById (R.id.checkBox4);
                    	viernes = (CheckBox) textEntryView1.findViewById (R.id.checkBox5);
                    	sabado = (CheckBox) textEntryView1.findViewById (R.id.checkBox6);
                    	domingo = (CheckBox) textEntryView1.findViewById (R.id.checkBox7);
                    	
                    	if (lunes.isChecked()) diasSel="2"+diasSel;
                    	if (martes.isChecked()) diasSel="3"+diasSel;
                    	if (miercoles.isChecked()) diasSel="4"+diasSel;
                    	if (jueves.isChecked()) diasSel="5"+diasSel;
                    	if (viernes.isChecked()) diasSel="6"+diasSel;
                    	if (sabado.isChecked()) diasSel="7"+diasSel;
                    	if (domingo.isChecked()) diasSel="1"+diasSel;
                    	
                    	SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
                    	ContentValues cv = new ContentValues();
    	    			cv.put(DatabaseHelper.ON_OFF,diasSel);
    	    			db.update("dias",cv,"_id=1",null); 
    	    	      	db.close();
                    	diasSel = "";                  	                   	
                    }
                    
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	
                    }
                })
                .create();
        case DIALOGO_IP:
            // This example shows how to add a custom layout to an AlertDialog
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.dialog_text_entry, null);
            return new AlertDialog.Builder(MainActivity.this)
                .setTitle("Inserta nueva IP/Puerto")
                .setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* Insertamos la nueva IP y puerto */
                    	EditText eT1 = (EditText) textEntryView.findViewById (R.id.ip_edit1);
                    	EditText eT2 = (EditText) textEntryView.findViewById (R.id.ip_edit3);
                    	EditText eT3 = (EditText) textEntryView.findViewById (R.id.ip_edit2);
                    	EditText eT4 = (EditText) textEntryView.findViewById (R.id.ip_edit4);
                    	EditText eTpt = (EditText) textEntryView.findViewById (R.id.puerto_edit);
                    	                
                    	ServerService.setIP(eT1.getText().toString()+"."+eT2.getText().toString()+
                    			"."+eT3.getText().toString()+"."+eT4.getText().toString(),
                    			Integer.parseInt(eTpt.getText().toString()));                    	
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* No hacemos nada */
                    }
                })
                .create();
            
    	case DIALOGO_MSG:
            // This example shows how to add a custom layout to an AlertDialog
            LayoutInflater factory2 = LayoutInflater.from(this);
            final View textEntryView2 = factory2.inflate(R.layout.dialog_text_entry_msg, null);
            return new AlertDialog.Builder(MainActivity.this)
                .setTitle("Inserta el mensaje a enviar el servidor")
                .setView(textEntryView2)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* Insertamos el nuevo mensaje */
                    	EditText editTextmsg = (EditText) textEntryView2.findViewById (R.id.msgText);
       
                    	mensText =editTextmsg.getText().toString();
                    	//if (mensText == "Abrir Puerta") orden = 'b';
            	    	Toast.makeText(MainActivity.this, "Mensaje Cambiado\nNuevo mensaje" +
            	    			mensText, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* No hacemos nada */
                    }
                })
                .create();
            
    	case ID_DIALOG_LOADING:
            	dialogwait.setMessage("Enviando Peticion. Please wait...");
            	dialogwait.setIndeterminate(true);
            	dialogwait.setCancelable(true);
            	return dialogwait;
        }
        return null;
    }
    
    //Dialogo diAs if (checkBox.isChecked()) {
    //a�adir los seleccionados al array "Seleccionados";}
    
      public void setAlarmaInicioRiego() {
	
	
	Calendar RiegoTimeAlarm;
	RiegoTimeAlarm = Calendar.getInstance();        
    
	if(mHour< Calendar.HOUR_OF_DAY && mMinute < Calendar.MINUTE)
		Toast.makeText(MainActivity.this,
				   "Error:"+R.string.tiempoPasado,Toast.LENGTH_SHORT).show();
	
	else {RiegoTimeAlarm.set(Calendar.HOUR_OF_DAY,mHour);
	RiegoTimeAlarm.set(Calendar.MINUTE, mMinute);
	RiegoTimeAlarm.set(Calendar.SECOND, 0);
	long millis = RiegoTimeAlarm.getTimeInMillis();
	Intent intent = new Intent(UpdateService.BROADCAST_ACTION);
	intent.putExtra("estado", 0);
	intent.putExtra("alarm",1);
	PendingIntent p_i = PendingIntent.getBroadcast(
			this.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	
	// La alarma se activa todos los dias y el Broadcast receiver 
	//decide que hacer viendo los dias que hay en la BaseDatos
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
			millis,AlarmManager.INTERVAL_DAY,p_i);}
									
}
      

  //Rellenar la hora con ceros si el n� es menor que 10
      private static String pad(long c) {
          if (c >= 10)
              return String.valueOf(c);
          else
              return "0" + String.valueOf(c);
      }
      
      //Funcion llamada por el dialogo de set Hora
      private TimePickerDialog.OnTimeSetListener mTimeSetListener =
          new TimePickerDialog.OnTimeSetListener() {
              public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                  mHour = hourOfDay;
                  mMinute = minute;               
                  //Guardo la nueva hora en la base de datos
                  SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
              		ContentValues cv = new ContentValues();
	    			cv.put(DatabaseHelper.HORA,pad(mHour)+":"+pad(mMinute));
	    			db.update("dias",cv,"_id=1",null); 
	    	      	db.close();
                 //Acciono la alarma
                 setAlarmaInicioRiego(); 
              }
          };
          
       //Receptor de intents para actualizar la UI o alguna alarma   
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          		if (intent.getExtras().getInt("alarm")==1){
          			int estado=intent.getExtras().getInt("estado");
          			switch(estado){
          			case 0:
          				String diasSelec="";
          				try{     		 
          		      	   SQLiteDatabase db = (new DatabaseHelper(context)).getReadableDatabase();
          		      	    Cursor mCursor = db.query("dias", new String[] {"*"}, 
          		      	    	    		null, null, null, null, null);   	   
          		      	   mCursor.moveToFirst();
          		      	   diasSelec = mCursor.getString(1);
          		      	   mCursor.close();
          		      	   db.close();  	    
          		      	   }catch (java.lang.RuntimeException e){    		   
          		      		   Toast.makeText(context,
          		      				   "Error",Toast.LENGTH_SHORT).show();
          		      	   }
          		      	Calendar sDia=Calendar.getInstance();
          	          	int diaHoy=sDia.get(Calendar.DAY_OF_WEEK);
          	          	if(diasSelec.contains(diaHoy+"")){
          			Toast.makeText(context, R.string.ToastInicioRiego,
          	      			Toast.LENGTH_SHORT).show();
          			soundPool.play(ID_SOUND_INICIO, 0.5f, 0.5f, 1, 0, 1f);
          			updateAlarm(5,MainActivity.this,1);
          	          	}
          			break;
          			case 1:
          			Toast.makeText(context, R.string.ToastE1Riego,
                      		Toast.LENGTH_SHORT).show(); 
          			soundPool.play(ID_SOUND_E1, 0.5f, 0.5f, 1, 0, 1f);
          			updateAlarm(5,MainActivity.this,2);
              		break;	
          			case 2:
          				Toast.makeText(context, R.string.ToastE2Riego,
                      			Toast.LENGTH_SHORT).show(); 
          				soundPool.play(ID_SOUND_E2, 0.5f, 0.5f, 1, 0, 1f);
          				updateAlarm(5,MainActivity.this,3);
          				break; 
          			case 3:
          				Toast.makeText(context, R.string.ToastE3Riego,
                      			Toast.LENGTH_SHORT).show(); 
          				soundPool.play(ID_SOUND_E3, 0.5f, 0.5f, 1, 0, 1f);
          				updateAlarm(5,MainActivity.this,4);
          				break;
          			case 4:
          				Toast.makeText(context, R.string.ToastE4Riego,
                      			Toast.LENGTH_SHORT).show(); 
          				soundPool.play(ID_SOUND_E4, 0.5f, 0.5f, 1, 0, 1f);
              			break;
          			}	
          		}
          			
          		else if (intent.getExtras().getInt("alarm")==0){
          			//Log.i("hola","hemso");
          			updateUI(intent); 
          		}
          			      
        }
    };    
    


	
	////////////////////////////////////////////////////////////////
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	SubMenu sub = menu.addSubMenu (SubMenu.NONE, CONFIG, SubMenu.NONE, R.string.Configurar)
    		.setIcon(android.R.drawable.ic_menu_preferences);
    		
    	menu.addSubMenu (SubMenu.NONE, AGUA, SubMenu.NONE, "Sensores")
		.setIcon(android.R.drawable.ic_menu_view);
    	
    	menu.addSubMenu (SubMenu.NONE, PROG, SubMenu.NONE, "Programar Riego")
		.setIcon(android.R.drawable.ic_menu_recent_history);
    	
    	sub.add(SubMenu.NONE, IP, SubMenu.NONE, R.string.cambiarIP);
    	sub.add(SubMenu.NONE, MSG, SubMenu.NONE, R.string.cambiarMensaje);
    	sub.add(SubMenu.NONE, AGUA, SubMenu.NONE, R.string.Niveles);
    	
    	return true;
    }
	 
   
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	switch (item.getItemId()) {
    		case IP:
    			showDialog(DIALOGO_IP);
    			break;
    
    		case MSG:	    			
    			showDialog(DIALOGO_MSG);
    			break;
    			
    		case AGUA:	    	
    			Intent intentagua = new Intent(MainActivity.this,SensorShowActivity.class);
            	startActivity(intentagua);
    			break;
    			
    		case PROG:	    	
    			PantallaGeneral.setVisibility(View.INVISIBLE);	
				PantallaConfig.setVisibility(View.VISIBLE);
    			break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    
    public void actualizarIP(String b1,String b2,String b3, String b4) {
    	ipAddress = new byte[] {(byte)Integer.parseInt(b1),(byte)Integer.parseInt(b2)
    			,(byte)Integer.parseInt(b3),(byte)Integer.parseInt(b4)};
    	
        Toast.makeText(MainActivity.this, 
         		 "IP Cambiada\nNueva IP " +
         		byteArrayToString(ipAddress)+
         		 ":"+puerto
         		 ,Toast.LENGTH_LONG).show();

    }
    
    
    public static final String byteArrayToString(byte[] arr) {   
        int r0 = (arr[0] & 0xFF) ;
        int r1 = (arr[1] & 0xFF) ;
        int r2 = (arr[2] & 0xFF) ;
        int r3 = arr[3] & 0xFF;
        return r0 +"."+ r1 +"."+ r2 +"."+ r3;
    }
    
    
    // Define the Handler that receives messages from the thread 
    //and update the progress bar
    final Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
        	switch(msg.arg1){
        	case 0:
             	mToast.setText(R.string.enviandoOrden);
             	break;
        	case 1:
             	mToast.setText(R.string.ServContactado);
             	break;
        	case 2:
             	mToast.setText(R.string.OrdenRecibida);
             	break;
        	case 4:
             	mToast.setText(R.string.OrdenNoRec);
             	removeDialog(ID_DIALOG_LOADING);
             	break;
        	case 3:
        		removeDialog(ID_DIALOG_LOADING);
                String text = (String)msg.obj;
                mToast.setText(text);
                break;
        	}
            mToast.show();
        }
    };
////////////////////////////////////////////////////7
	

    private void updateUI(Intent intent) {
    	if (intent.getExtras().getInt("aceptada")!=0){
    		Message msg = new Message(); 
        	msg.obj = "Peticion aceptada";
        	msg.arg1= intent.getExtras().getInt("aceptada");
		progressHandler.sendMessage(msg);
		//desbloquear UI
		return;
    	}
		IndEstadoTxt.setText(checkEstado());
       	String R1 = intent.getStringExtra("R1");
    	String AUTO_ON = intent.getStringExtra("AUTO_ON"); 
    	String SYS_ON = intent.getStringExtra("SYS_ON"); 
       	String R2 = intent.getStringExtra("R2"); 
        String AB1 = intent.getStringExtra("AB1"); 
       	String AB2 = intent.getStringExtra("AB2"); 
       	String hora = intent.getStringExtra("hora");
       	String dias = intent.getStringExtra("dias");
       	String diasS="";
       	if (dias.contains("1")){
       		diasS = diasS.concat("DOM|");
       	}
       	if (dias.contains("2")){
       		diasS = diasS.concat("LUN|");
       	}
       	if (dias.contains("3")){
       		diasS = diasS.concat("MAR|");
       	}
       	if (dias.contains("4")){
       		diasS = diasS.concat("MIE|");
       	}
       	if (dias.contains("5")){
       		diasS = diasS.concat("JUE|");
       	}
       	if (dias.contains("6")){
       		diasS = diasS.concat("VIE|");
       	}
       	if (dias.contains("7")){
       		diasS = diasS.concat("SAB|");
       	}
        horaButton.setText(hora);  	
        DaysButton.setText(diasS); 
        	if ( R1.equals("true")) 
			R1Button.setBackgroundResource(R.drawable.swonmin);
		else R1Button.setBackgroundResource(R.drawable.swoffmin);
       	
       	
       	if (R2.equals("true")) 
			R2Button.setBackgroundResource(R.drawable.swonmin);
		else R2Button.setBackgroundResource(R.drawable.swoffmin);

       	if ( AB1.equals("true"))  
			ABS1Button.setBackgroundResource(R.drawable.swonmin);
		else ABS1Button.setBackgroundResource(R.drawable.swoffmin);

       	if ( AB2.equals("true")) 
       		ABS2Button.setBackgroundResource(R.drawable.swonmin);
		else ABS2Button.setBackgroundResource(R.drawable.swoffmin);
       	if ( SYS_ON.equals("true")) 
       		SysONButton.setBackgroundResource(R.drawable.swonmin);
		else SysONButton.setBackgroundResource(R.drawable.swoffmin);
       	if ( AUTO_ON.equals("true")){ 
       		AutoONButton.setBackgroundResource(R.drawable.swchauto);		
       		AutoONTxt.setText("Auto ON");}
       		else{ 
       		AutoONButton.setBackgroundResource(R.drawable.swchmanual);
       		AutoONTxt.setText("Auto OFF");}
    }
    
    private void updateAlarm(long tiempo, Context context, int estado) {
    	long timerol =Calendar.getInstance().getTimeInMillis();
      	long timerolo = timerol +tiempo*1000;
      	Intent alarmintent = new Intent(UpdateService.BROADCAST_ACTION);
      	alarmintent.putExtra("estado", estado);
      	alarmintent.putExtra("alarm", 1);
      	alarmintent.putExtra("aceptada",0);
      	PendingIntent p_i = PendingIntent.getBroadcast(context,
      	 234324243, alarmintent, PendingIntent.FLAG_UPDATE_CURRENT);
      	AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      	alarmManager2.set(AlarmManager.RTC_WAKEUP,timerolo,p_i);
      				
    	sendBroadcast(intent);
    }
    
    
    
    private void initConfig() 
    {
    	
        setContentView(R.layout.main);
      //Para mostrar la hora actual en la pantalla
        Thread myThread = null;
    	Runnable runnable = new CountDownRunner();
        myThread= new Thread(runnable);   
        myThread.start();
        //Inicializacion de la base de datos
        SQLiteDatabase db = (new DatabaseHelper(MainActivity.this)).getWritableDatabase();
        ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.HORA,"0");
		cv.put(DatabaseHelper.ON_OFF,"0");
		cv.put(DatabaseHelper.R1,"0");
		cv.put(DatabaseHelper.R2,"0");
		cv.put(DatabaseHelper.AB1,"0");
		cv.put(DatabaseHelper.AB2,"0");
		cv.put(DatabaseHelper.SYS_ON,"0");
		cv.put(DatabaseHelper.AUTO_ON,"0");
        Cursor mCursor = db.query("dias", new String[] {"*"}, 
          		null, null, null, null, null);      	   	
      	if(mCursor.getCount()==0) db.insert("dias",null ,cv);
      	mCursor.close();
      	db.close();
        
      	
     // Para reproducir sonidos
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		ID_SOUND_INICIO = soundPool.load(MainActivity.this, R.raw.inicio, 1);
		ID_SOUND_E1 = soundPool.load(MainActivity.this, R.raw.e1riego, 1);
		ID_SOUND_E2 = soundPool.load(MainActivity.this, R.raw.e2riego, 1);
		ID_SOUND_E3 = soundPool.load(MainActivity.this, R.raw.e3riego, 1);
		ID_SOUND_E4 = soundPool.load(MainActivity.this, R.raw.e4riego, 1);
       intent = new Intent(this, UpdateService.class);
       ServerIntent = new Intent(this, ServerService.class);
       dialogwait = new ProgressDialog(MainActivity.this);
       res = getResources(); 
        horaButton = (Button) findViewById(R.id.BtHoraIni);
       	DaysButton = (Button) findViewById(R.id.Days);
    	IndEstadoTxt = (TextView) findViewById(R.id.Ind_estado_txt);
    	TimeEstadoTxt= (TextView) findViewById(R.id.label_time_estado);
    	//ParpadeanteTxt= (TextView) findViewById(R.id.Parpadeante_ON);
    	AutoONTxt= (TextView) findViewById(R.id.tvAutoON);
    	R1Button = (Button) findViewById(R.id.R1_btn);
    	R2Button = (Button) findViewById(R.id.R2_btn);
    	ABS1Button = (Button) findViewById(R.id.ABS1_btn);
    	ABS2Button = (Button) findViewById(R.id.ABS2_btn);
    	SysONButton =(Button) findViewById(R.id.SysON_btn);
    	AutoONButton =(Button) findViewById(R.id.AutoON_btn);
    	ConfigButton = (Button) findViewById(R.id.GoConfigPantalla_btn);
    	OkConfigButton = (Button) findViewById(R.id.GoGralPantalla_btn);
    	PantallaGeneral = (RelativeLayout) findViewById(R.id.Pantalla_general);
    	PantallaConfig = (RelativeLayout) findViewById(R.id.Pantalla_Config);
    	//ConfigTipoButton = (Button) findViewById(R.id.GoConfigTipo_btn);
    	//	    	Log.i(TAG, "Fin del initConfig().");
    	mToast = Toast.makeText(MainActivity.this  , "" , 
        		Toast.LENGTH_SHORT );
    }
    private String CalcHora()
    {
    	  Calendar cal;
          cal = Calendar.getInstance();        
          int hora;
          int minuto;
          String strminuto;
          String strhora;
          hora = cal.get(Calendar.HOUR_OF_DAY);
          minuto = cal.get(Calendar.MINUTE);
          strhora = ""+hora;
          strminuto = ""+minuto;
          if (minuto<=9) strminuto= "0"+minuto;
          if (hora<=9) strhora= "0"+hora;
          String HoraActual = strhora + ":"+ strminuto; //+ ":"+ segundos;
          return HoraActual;
    }
 
    
    // Se muestra la hora actual arriba
      
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                	TimeEstadoTxt.setText("Son las " + CalcHora());
                }catch (Exception e) {}
            }
        });
    }
    
    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try {
                    doWork();
                        Thread.sleep(59000);
                    } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                    }catch(Exception e){
                    }
                }
        }
    }
}    

    
    
    
