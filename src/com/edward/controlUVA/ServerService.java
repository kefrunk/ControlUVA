package com.edward.controlUVA;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

//Este servicio solo se ejecuta en el programa central (no en el cliente) y 
//tiene 2 funciones:
//
// 	1. Escuchar peticiones de los programas clientes.
//	   Si se recibe cambiar el valor de una variable, actualiza la base de datos
//	
//	2. Enviar la orden a Arduino, comprobar que se ha ejecutado y enviar al 
//	   cliente ACK de confirmacion.


public class ServerService extends Service{
	static NetworkTask1 networktaskArduino;  //Socket para comunicacion Arduino
	static NetworkTask2 networktaskClient; 	//Socket para comunicacion Cliente
	Intent ackintent;
	Intent aguaintent;
	public static String ipString ="77.211.98.109";
	public static int puerto= 5207;


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ackintent = new Intent(UpdateService.BROADCAST_ACTION);
		aguaintent = new Intent("agua");
	}
	
	 @Override
	 public void onStart(Intent intent, int startId) {
		networktaskArduino = new NetworkTask1(); //New instance of NetworkTask
	    networktaskArduino.execute();
	    networktaskClient = new NetworkTask2(); //New instance of NetworkTask
	    networktaskClient.execute();
	}
	 
	 @Override
	public void onDestroy() {			
			super.onDestroy();
		}	
	
	public class NetworkTask1 extends AsyncTask<Void, byte[], Boolean> {
        Socket nsocket; //Network Socket
        InputStream nis; //Network Input Stream
        OutputStream nos; //Network Output Stream

        @Override
        protected void onPreExecute() {
            Log.i("AsyncTask", "onPreExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) { //This runs on a different thread
            boolean result = false;
            try {
                Log.i("AsyncTask", "doInBackground: Creating socket");
                SocketAddress sockaddr = new InetSocketAddress(ipString, puerto);
                nsocket = new Socket();
                nsocket.connect(sockaddr, 5000); //10 second connection timeout
                if (nsocket.isConnected()) { 
                    nis = nsocket.getInputStream();
                    nos = nsocket.getOutputStream();
                    Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
                    Log.i("AsyncTask", "doInBackground: Waiting for inital data...");
                    byte[] buffer = new byte[32];
                    int read = nis.read(buffer, 0, 32); //This is blocking
                    while(read != -1){
                        byte[] tempdata = new byte[read];
                        System.arraycopy(buffer, 0, tempdata, 0, read);
                        publishProgress(tempdata);
                        Log.i("AsyncTask", "doInBackground: Got some data");
                        read = nis.read(buffer, 0, 32); //This is blocking
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("AsyncTask", "doInBackground: IOException");
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AsyncTask", "doInBackground: Exception");
                result = true;
            } finally {
                try {
                    nis.close();
                    nos.close();
                    nsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("AsyncTask", "doInBackground: Finished");
            }
            return result;
        }
        public void SendDataToNetwork(String cmd) { //You run this from the main thread.
            try {
                if (nsocket.isConnected()) {
                    Log.i("AsyncTask", "SendDataToNetwork: Writing received message to socket");
                    nos.write(cmd.getBytes());
                    //bloquear la UI
                } else {
                    Log.i("AsyncTask", "SendDataToNetwork: Cannot send message. Socket is closed");
                  //desbloquear la UI con mensage de error
                }
            } catch (Exception e) {
                Log.i("AsyncTask", "SendDataToNetwork: Message send failed. Caught an exception");
              //desbloquear la UI con mensage de error
            }
        }
        @Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.i("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
                String recibido = new String(values[0]);
                Log.i("AsyncTask", "bytesrecividos:" +recibido);
                if (recibido.contains("q")){
                	Log.i("AsyncTask", "Servidor Activo"); 
                	ackintent.putExtra("alarm",0);
                	ackintent.putExtra("aceptada",1);
                	sendBroadcast(ackintent);
                	}
                if (recibido.contains("x")){
                	Log.i("AsyncTask", "Orden no recibida"); 
               // ackintent.putExtra("alarm",0);
               // ackintent.putExtra("aceptada",4);
            	//sendBroadcast(ackintent);
                	return;
                	}
            	if (recibido.contains("p")){
                	Log.i("AsyncTask", "Orden Recibida"); 
                //ackintent.putExtra("alarm",0);
               // ackintent.putExtra("aceptada",2);
            	//sendBroadcast(ackintent);
            	}
            	if (recibido.contains("y")){
            	  try{ 
            		  SQLiteDatabase db = (new DatabaseHelper(ServerService.this)).getWritableDatabase();
            		  ContentValues cv = new ContentValues();
            		  ackintent.putExtra("alarm",0);
            		  ackintent.putExtra("aceptada",3);
            		  sendBroadcast(ackintent);
            		  if (recibido.contains("Activar R1")) cv.put(DatabaseHelper.R1,"true");
            		  else if (recibido.contains("Desactivar R1")) cv.put(DatabaseHelper.R1,"false");
            		  else if (recibido.contains("Activar R2")) cv.put(DatabaseHelper.R2,"true");
            		  else if (recibido.contains("Desactivar R2")) cv.put(DatabaseHelper.R2,"false");
            		  else if (recibido.contains("Activar AB1")) cv.put(DatabaseHelper.AB1,"true");
            		  else if (recibido.contains("Desactivar AB1")) cv.put(DatabaseHelper.AB1,"false");
            		  else if (recibido.contains("Activar AB2")) cv.put(DatabaseHelper.AB2,"true");
            		  else if (recibido.contains("Desactivar AB2")) cv.put(DatabaseHelper.AB2,"false");
            		  else if (recibido.contains("Activar Sistema")) cv.put(DatabaseHelper.SYS_ON,"true");
            		  else if (recibido.contains("Desactivar Sistema")) cv.put(DatabaseHelper.SYS_ON,"false");
            		  else if (recibido.contains("Encender motor")) cv.put(DatabaseHelper.AUTO_ON,"true");
            		  else if (recibido.contains("Apagar motor")) cv.put(DatabaseHelper.AUTO_ON,"false");
            		  if (cv!=null) db.update("dias",cv,"_id=1",null); 
        			  db.close();
                        }catch(Exception e) {
                        Log.e("AsyncTask", "no se ha actualizado el valor");
                        }
                    }
            		else if (recibido.contains("g")|| recibido.contains("l")){
            			for (int i = 0; i < values[0].length; i++) {
            				if (values[0][i]==103 || values[0][i]==108 ){
            					byte[] num = new byte[4];
            					num[0]=values[0][i-4];
                				num[1]=values[0][i-3];
                				num[2]=values[0][i-2];
                				num[3]=values[0][i-1];                				
                				String numero =  new String(num);
                    			Log.i("numeroStr",numero+"\n");
                    			int sensorvalue = 0;
                    			try {
                    				sensorvalue = Integer.parseInt(numero);
                    				Log.i("numero",sensorvalue+"");
                    				aguaintent.putExtra("valor",sensorvalue);
                    				if (recibido.contains("g"))
                    					aguaintent.putExtra("sensor",0);
                    				else aguaintent.putExtra("sensor",1);
                         			sendBroadcast(aguaintent);
                         			Log.i("AsyncTask", "Broacast enviado"); 
                    			} catch(NumberFormatException nfe) {
                    			   Log.i("AsyncTASk","Could not parse " + nfe);
                    			}
                    			return;
            				}                  			
            			}            			
                	}               		                  	
            	}
        }  
        
        
        
        @Override
        protected void onCancelled() {
            Log.i("AsyncTask", "Cancelled.");
           // btnStart.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
             //   textStatus.setText("There was a connection error.");
            } else {
                Log.i("AsyncTask", "onPostExecute: Completed.");
            }
           // btnStart.setVisibility(View.VISIBLE);
        }
    }

	

	 
	 
	 
	 
	
	public class NetworkTask2 extends AsyncTask<Void, byte[], Boolean> {
        Socket nsocket; //Network Socket
        InputStream nis; //Network Input Stream
        OutputStream nos; //Network Output Stream

        @Override
        protected void onPreExecute() {
            Log.i("AsyncTask", "onPreExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) { //This runs on a different thread
            boolean result = false;
            try {
                Log.i("AsyncTask", "doInBackground: Creating socket");
                SocketAddress sockaddr = new InetSocketAddress("192.168.0.159", 5207);
                nsocket = new Socket();
                nsocket.connect(sockaddr, 60000); //1 minute connection timeout
                if (nsocket.isConnected()) { 
                    nis = nsocket.getInputStream();
                    nos = nsocket.getOutputStream();
                    Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
                    Log.i("AsyncTask", "doInBackground: Waiting for inital data...");
                    byte[] buffer = new byte[4096];
                    int read = nis.read(buffer, 0, 4096); //This is blocking
                    while(read != -1){
                        byte[] tempdata = new byte[read];
                        System.arraycopy(buffer, 0, tempdata, 0, read);
                        publishProgress(tempdata);
                        Log.i("AsyncTask", "doInBackground: Got some data");
                        read = nis.read(buffer, 0, 4096); //This is blocking
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("AsyncTask", "doInBackground: IOException");
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("AsyncTask", "doInBackground: Exception");
                result = true;
            } finally {
                try {
                    nis.close();
                    nos.close();
                    nsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("AsyncTask", "doInBackground: Finished");
            }
            return result;
        }
        public void SendDataToNetwork(String cmd) { //You run this from the main thread.
            try {
                if (nsocket.isConnected()) {
                    Log.i("AsyncTask", "SendDataToNetwork: Writing received message to socket");
                    nos.write(cmd.getBytes());
                    //bloquear la UI
                } else {
                    Log.i("AsyncTask", "SendDataToNetwork: Cannot send message. Socket is closed");
                  //desbloquear la UI con mensage de error
                }
            } catch (Exception e) {
                Log.i("AsyncTask", "SendDataToNetwork: Message send failed. Caught an exception");
              //desbloquear la UI con mensage de error
            }
        }
        @Override
        protected void onProgressUpdate(byte[]... values) {
            if (values.length > 0) {
                Log.i("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
                String recibido = new String(values[0]);
                Log.i("AsyncTask", "bytesrecividos:" +recibido);
                
            	if (recibido.contains("y")){
            		if (recibido.contains("orden")){
                    	Log.i("AsyncTask", "R1 a ON"); 
                   try{ 
                	   SQLiteDatabase db = (new DatabaseHelper(ServerService.this)).getWritableDatabase();
                	   ContentValues cv = new ContentValues();
                	   if (recibido.contains("0")){
                		   if (recibido.contains("r")) {
                			   cv.put(DatabaseHelper.R1,"true");
                			   enviarOrden("r\n");
                		   }
                		   if (recibido.contains("R")) cv.put(DatabaseHelper.R2,"true");
                		   if (recibido.contains("b")) cv.put(DatabaseHelper.AB1,"true");
                		   if (recibido.contains("B")) cv.put(DatabaseHelper.AB2,"true");
                		   if (recibido.contains("S")) cv.put(DatabaseHelper.SYS_ON,"true");
                		   if (recibido.contains("A")) cv.put(DatabaseHelper.AUTO_ON,"true");       		   
                	   }
                	   else if (recibido.contains("1")){
                		   if (recibido.contains("r")) cv.put(DatabaseHelper.R1,"false");
                		   if (recibido.contains("R")) cv.put(DatabaseHelper.R2,"false");
                		   if (recibido.contains("b")) cv.put(DatabaseHelper.AB1,"false");
                		   if (recibido.contains("B")) cv.put(DatabaseHelper.AB2,"false");
                		   if (recibido.contains("S")) cv.put(DatabaseHelper.SYS_ON,"false");
                		   if (recibido.contains("A")) cv.put(DatabaseHelper.AUTO_ON,"false"); 
                	   }
                	   if(cv!=null) {
                		   db.update("dias",cv,"_id=1",null); 
                		   Log.i("AsyncTask", "orden remota recibida"); 
                           ackintent.putExtra("alarm",0);
                           ackintent.putExtra("aceptada",5);
                           ackintent.putExtra("cadena",cv.toString());
                       	sendBroadcast(ackintent);
                       	SendDataToNetwork("y\n");
                	   }
                		   db.close();
                   }catch(Exception e) {
                       Log.e("AsyncTask", "no se ha actualizado el valor");
                       }
                	}
            		
                	}            	
            }
        }
        @Override
        protected void onCancelled() {
            Log.i("AsyncTask", "Cancelled.");
           // btnStart.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
             //   textStatus.setText("There was a connection error.");
            } else {
                Log.i("AsyncTask", "onPostExecute: Completed.");
            }
           // btnStart.setVisibility(View.VISIBLE);
        }
    }
	
	
	public static void setIP(String ip, int pto) {
		networktaskArduino.cancel(true);
		ipString = ip;
		puerto = pto;		
		SocketAddress sockaddr = new InetSocketAddress(ip, pto);
		try {
			networktaskArduino.nsocket.connect(sockaddr, 5000);
		} catch (Exception e) {
			Log.e("kk","mal");
			e.printStackTrace();
		}
	}
	

	
	public static void enviarOrden(String cmd) {
		 
		networktaskArduino.SendDataToNetwork(cmd);		
	}

}
