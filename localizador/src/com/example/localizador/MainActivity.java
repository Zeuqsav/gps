package com.example.localizador;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {


	private Context context;
	private Button benviar;
	private TextView messageTextView;
	private TextView messageTextView2;
	private String latit ="";
	private String longi ="";
	private String totalEmail ="nada";
	private String possibleEmail= "";
	private Location loc;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		benviar  = (Button) findViewById(R.id.button1);
		context = getApplicationContext();
		messageTextView = (TextView) findViewById(R.id.message_id);
		messageTextView2 = (TextView) findViewById(R.id.message_id2);
		Criteria req = new Criteria();
		req.setAccuracy(Criteria.ACCURACY_FINE);
		req.setAltitudeRequired(true);
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
			
		Account[] accounts = AccountManager.get(context).getAccounts();
		
		
				for (Account account : accounts) {
				    if (emailPattern.matcher(account.name).matches()) {
				        possibleEmail = account.name;
				    }
				    totalEmail= totalEmail + emailPattern.toString() + "//" + account.name.toString();
				}
		
			    	
		
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Location loc = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		MyLocationListener mlocListener = new MyLocationListener();
		mlocListener.setMainActivity(this);
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				(LocationListener) mlocListener);

		messageTextView.setText("LocationListener agregado" + totalEmail);
		messageTextView2.setText("");
		
		benviar.setOnClickListener(new View.OnClickListener() 
			{
			public void onClick(View arg0) 
				{
				final HttpClient httpClient = new DefaultHttpClient();
				// para local final HttpPost post =  new HttpPost("http://192.168.0.100:8080/ubicacion");
				final HttpPost post =  new HttpPost("https://lit-refuge-8196.herokuapp.com/ubicacion");
				post.setHeader("content-type", "application/json");

				JSONObject dato = new JSONObject();	
				try 
				{
					dato.put("id", "1");
					dato.put("coorx", "coorExitoooo");
					dato.put("coory", "cooyesss");
					dato.put("otro", "otracosaaaa");
					
					if (longi==""){
						dato.put("coorx", "cooy");
					}else{
						dato.put("coorx", longi);	
					}
					if (latit==""){
						dato.put("coory", "cooy");
					}else{
						dato.put("coory", latit);
					}
					if (possibleEmail==""){
						dato.put("otro", "emai");
					}else{
						dato.put("otro", possibleEmail);
					}
					
					
				
				StringEntity entity = new StringEntity(dato.toString());
				post.setEntity(entity);
				Thread thread = new Thread(new Runnable()
					{
					@Override
					public void run() 
						{	
							
							try {
							HttpResponse resp = httpClient.execute(post);
				
							String respStr = EntityUtils.toString(resp.getEntity());
							} catch (ParseException | IOException e) {
								// TODO Auto-generated catch block
								Toast toast = Toast.makeText(context, e.toString()  , 500);
								toast.show();
							}
						}
				
					});
					thread.start();
				}
				catch (Exception e)
				{
					Toast toast = Toast.makeText(context, e.toString()  , 500);
					toast.show();					
				}			}
			});
	}
	
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public class MyLocationListener implements LocationListener {
		MainActivity mainActivity;

		public MainActivity getMainActivity() {
			return mainActivity;
		}

		public void setMainActivity(MainActivity mainActivity) {
			this.mainActivity = mainActivity;
		}

		@Override
		public void onLocationChanged(Location loc) {
			// Este mŽtodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
			// debido a la detecci—n de un cambio de ubicacion
			loc.getLatitude();
			loc.getLongitude();
			String Text = totalEmail + "Mi ubicaci—n actual es: " + "\n Lat = "
					+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
			messageTextView.setText(Text);
			
			latit=loc.getLatitude() + ""   ;
			longi=loc.getLongitude() + "";
			
			this.mainActivity.setLocation(loc);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// Este mŽtodo se ejecuta cuando el GPS es desactivado
			messageTextView.setText("GPS Desactivado");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// Este mŽtodo se ejecuta cuando el GPS es activado
			messageTextView.setText("GPS Activado");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Este mŽtodo se ejecuta cada vez que se detecta un cambio en el
			// status del proveedor de localizaci—n (GPS)
			// Los diferentes Status son:
			// OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
			// TEMPORARILY_UNAVAILABLE -> Temp˜ralmente no disponible pero se
			// espera que este disponible en breve
			// AVAILABLE -> Disponible
		}

	}/* End of Class MyLocationListener */
	
	public void setLocation(Location loc) {
		//Obtener la direcci—n de la calle a partir de la latitud y la longitud 
		if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
			try {
				Geocoder geocoder = new Geocoder(this, Locale.getDefault());
				List<Address> list = geocoder.getFromLocation(
						loc.getLatitude(), loc.getLongitude(), 1);
				if (!list.isEmpty()) {
					Address address = list.get(0);
					messageTextView2.setText("Mi direcci—n es: \n"
							+ address.getAddressLine(0));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
