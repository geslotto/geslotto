package com.desipal.EventU;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.eventoEN;
import com.desipal.Servidor.buscarEventosCerca;
import com.desipal.Servidor.detalleEvento;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.view.Window;
import android.widget.TextView;

public class detalleEventoActivity extends Activity {

	public static eventoEN evento;
	TextView edNombre;
	TextView edDesc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detalleevento);
		Bundle e = getIntent().getExtras();

		edNombre = (TextView) findViewById(R.id.txtDetalleNombre);
		edDesc = (TextView) findViewById(R.id.txtDetalleDesc);

		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

		long idEvento = e.getLong("idEvento");
		parametros.add(new BasicNameValuePair("idEvento", idEvento + ""));
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (loc != null) {
			double latitud = loc.getLatitude();
			double longitud = loc.getLongitude();
			parametros.add(new BasicNameValuePair("latitud", String.valueOf(latitud)));
			parametros.add(new BasicNameValuePair("longitud", String.valueOf(longitud)));
		}

		String URL = "http://desipal.hol.es/app/eventos/verEvento.php";
		final detalleEvento peticion = new detalleEvento(parametros);
		peticion.execute(new String[] { URL });
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Status s = peticion.getStatus();
				if (s.name().equals("FINISHED")) {
					if (evento != null) {
						verEvento();
					}
				} else
					handler.postDelayed(this, 500);
			}
		}, 500);

	}

	public void verEvento() {
		edNombre.setText(evento.getNombre());
		edDesc.setText(evento.getDescripcion());
	}

}
