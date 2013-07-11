package com.desipal.eventu;

import java.util.Locale;

import com.desipal.Librerias.TestFragmentAdapter;
import com.desipal.Servidor.categorias;
import com.desipal.eventu.R;
import com.google.android.gms.maps.model.LatLng;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	static Locale currentLocale = new Locale("es", "ES");

	boolean bloquearPeticion = false;// bandera que bloquea para no poder jhacer
										// la peticion
	boolean finPaginadoCerca = false;// bandera para saber que no tiene que
										// hacer mas peticiones en cerca

	Button btnCrearEvento;

	TestFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	public static String fCategorias = "categorias.json";

	// Localizacion
	public static LatLng PosicionActual = null;
	private LocationManager locManager;
	private Location LocAnterior;
	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (LocAnterior == null) {
				LocAnterior = location;
			}
			if (location.getAccuracy() < 300
					|| LocAnterior.getAccuracy() > location.getAccuracy()) {
				LocAnterior = location;
				PosicionActual = new LatLng(location.getLatitude(),
						location.getLongitude());
			}
		}

		public void onProviderDisabled(String provider) {
			// Eliminamos el proveedor
		}

		public void onProviderEnabled(String provider) {
			// Agregamos el proveedor
			if (provider.equals("gps")) {
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						0, 0, locationListener);
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// invesitgar uso
		}
	};
	public static boolean errorServicios = false;

	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// LOCALIZACION
			locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			// Obtenemos la última posición conocida
			Location locGps = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location locNet = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (locGps != null) {
				PosicionActual = new LatLng(locGps.getLatitude(),
						locGps.getLongitude());
				errorServicios = false;
			} else if (locNet != null) {
				PosicionActual = new LatLng(locNet.getLatitude(),
						locNet.getLongitude());
				errorServicios = false;
			} else {
				Toast.makeText(this,
						"Se necesita servicios de localizacion como Wifi,Gps",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 1);
				errorServicios = true;
			}
			if (!errorServicios) {
				// Se llama al listener con ambos proveedores
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						0, 0, locationListener);
				locManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListener);
			}
			if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(
						this,
						"Si habilita el gps tendra una mejor precision de su situacion",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception e) {
		}
		// //////

		// Actualizacion de categorias
		String URL = "http://desipal.hol.es/app/eventos/categorias.php";
		categorias peticion = new categorias(MainActivity.this);
		peticion.execute(new String[] { URL });

		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		mIndicator.setViewPager(mPager);

		btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);

		btnCrearEvento.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						crearEventoActivity.class);
				startActivity(i);
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locManager.removeUpdates(locationListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0 && requestCode == 1) {
			finish();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
}