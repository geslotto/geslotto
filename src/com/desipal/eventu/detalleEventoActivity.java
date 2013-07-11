package com.desipal.eventu;

import java.text.DecimalFormat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.eventoEN;
import com.desipal.eventu.R;
import com.desipal.Servidor.asistenciaEvento;
import com.desipal.Servidor.detalleEvento;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class detalleEventoActivity extends FragmentActivity {
	public static eventoEN evento;
	private GoogleMap map;
	static LatLng Posicion = null;

	TextView edNombre;
	TextView edDesc;
	TextView txtAsistentes;
	TextView txtDir;
	TextView txtDireccion;
	TextView txtDetalleDist;
	TextView txtDetalleDistancia;
	TextView txtDetalleFechaIni;
	TextView txtDetalleFechaInicio;
	TextView txtDetalleFechaFin;
	TextView txtDetalleFechaFinal;
	ToggleButton togAsistencia;
	ProgressBar progressBar;
	ImageView galeria;
	RelativeLayout relInformacion;
	RelativeLayout relsituacion;

	public static boolean asiste;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm", MainActivity.currentLocale);
///GALERIA
	public static List<Drawable> fotosGaleria = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.detalleevento);
			Bundle e = getIntent().getExtras();
			edNombre = (TextView) findViewById(R.id.txtDetalleNombre);
			edDesc = (TextView) findViewById(R.id.txtDetalleDesc);
			txtAsistentes = (TextView) findViewById(R.id.txtDetalleAsistentes);
			galeria = (ImageView) findViewById(R.id.imgDetalleImagen);
			txtDir = (TextView) findViewById(R.id.txtDetalleDir);
			txtDireccion = (TextView) findViewById(R.id.txtDetalleDireccion);
			txtDetalleDist = (TextView) findViewById(R.id.txtDetalleDist);
			txtDetalleDistancia = (TextView) findViewById(R.id.txtDetalleDistancia);
			txtDetalleFechaIni = (TextView) findViewById(R.id.txtDetalleFechaIni);
			txtDetalleFechaInicio = (TextView) findViewById(R.id.txtDetalleFechaInicio);
			txtDetalleFechaFin = (TextView) findViewById(R.id.txtDetalleFechaFin);
			txtDetalleFechaFinal = (TextView) findViewById(R.id.txtDetalleFechaFinal);
			togAsistencia = (ToggleButton) findViewById(R.id.togAsistencia);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			relInformacion = (RelativeLayout) findViewById(R.id.relInformacion);
			relsituacion = (RelativeLayout) findViewById(R.id.relsituacion);
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

			long idEvento = e.getLong("idEvento");
			parametros.add(new BasicNameValuePair("idEvento", idEvento + ""));
			LatLng loc = MainActivity.PosicionActual;
			if (loc != null) {
				double latitud = loc.latitude;
				double longitud = loc.longitude;
				parametros.add(new BasicNameValuePair("latitud", String
						.valueOf(latitud)));
				parametros.add(new BasicNameValuePair("longitud", String
						.valueOf(longitud)));
			}
			String android_id = Secure.getString(this.getContentResolver(),
					Secure.ANDROID_ID);
			parametros.add(new BasicNameValuePair("idDispositivo", android_id));
			galeria.setOnClickListener(new OnClickListener() {				
				public void onClick(View v) {
					Intent i = new Intent(detalleEventoActivity.this,
							galeriaActivity.class);
					startActivity(i);
				}
			});

			togAsistencia
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							try {
								if (asiste != isChecked) {
									ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

									long idEvento = evento.getIdEvento();
									parametros.add(new BasicNameValuePair(
											"idEvento", idEvento + ""));
									String android_id = Secure.getString(
											detalleEventoActivity.this
													.getContentResolver(),
											Secure.ANDROID_ID);
									parametros.add(new BasicNameValuePair(
											"idDispositivo", android_id));
									String asistire;
									if (isChecked)
										asistire = "true";
									else
										asistire = "false";
									parametros.add(new BasicNameValuePair(
											"asiste", asistire));
									String URL = "http://desipal.hol.es/app/eventos/asistenciaEvento.php";
									final asistenciaEvento peticion = new asistenciaEvento(
											parametros);
									peticion.execute(new String[] { URL });
									final Handler handler = new Handler();
									handler.postDelayed(new Runnable() {
										@Override
										public void run() {
											Status s = peticion.getStatus();
											if (!s.name().equals("FINISHED"))
												handler.postDelayed(this, 500);
										}
									}, 500);
								}
							} catch (Exception ex) {
								Toast.makeText(detalleEventoActivity.this,
										"Error: " + ex.toString(),
										Toast.LENGTH_SHORT).show();
							}

						}
					});

			String URL = "http://desipal.hol.es/app/eventos/verEvento.php";
			final detalleEvento peticion = new detalleEvento(parametros,
					detalleEventoActivity.this);
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
		} catch (Exception ex) {
			Toast.makeText(this, "Error: " + ex.toString(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void verEvento() {
		edNombre.setText(evento.getNombre());
		edDesc.setText(evento.getDescripcion());
		txtAsistentes.setText(evento.getAsistencia()
				+ " "
				+ detalleEventoActivity.this
						.getString(R.string.detalleEventoAsistencia));
		fotosGaleria = evento.getImagenes();
		galeria.setImageDrawable(evento.getImagenes().get(0));
		// ///////////////////////////
		String direccion = "";
		String[] spidesc = evento.getDireccion().split(",");
		for (int i = 2; i < spidesc.length; i++) {
			if (!spidesc[i].equals(""))
				if (i == spidesc.length - 1)
					direccion += spidesc[i];
				else
					direccion += spidesc[i] + ",";
		}
		txtDireccion.setText(direccion);
		//
		txtDir.setText("Direcci�n: ");
		txtDetalleDist.setText("Distancia: ");
		String distancia = new DecimalFormat("#.##").format(evento
				.getDistancia()) + " Km.";
		txtDetalleDistancia.setText(distancia);

		String fechaI = dateFormat.format(evento.getFechaInicio());
		String fechaF = dateFormat.format(evento.getFechaFin());

		// txtDetalleFechaInicio.setText(fechaI + " Durante todo el d�a");
		if (evento.isTodoElDia()) {
			txtDetalleFechaInicio.setText(fechaI + " Durante todo el d�a");
			txtDetalleFechaFin.setVisibility(View.GONE);
			txtDetalleFechaFinal.setVisibility(View.GONE);
		} else {
			txtDetalleFechaInicio.setText(fechaI);
			txtDetalleFechaFinal.setText(fechaF);
		}

		togAsistencia.setVisibility(View.VISIBLE);

		if (asiste) {
			togAsistencia.setChecked(true);
		} else {
			togAsistencia.setChecked(false);
		}
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapa)).getMap();
		map.getUiSettings().setScrollGesturesEnabled(false);
		Posicion = new LatLng(evento.getLatitud(), evento.getLongitud());
		map.addMarker(new MarkerOptions().position(Posicion).title(
				evento.getNombre()));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Posicion, 15));
		relInformacion.setVisibility(View.VISIBLE);
		relsituacion.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}
}
