package com.desipal.EventU;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.eventoEN;
import com.desipal.Servidor.detalleEvento;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class detalleEventoActivity extends Activity {
	public static eventoEN evento;
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
	Button btnSi;
	Button btnNo;
	ProgressBar progressBar;
	Gallery galeria;

	static List<Drawable> imagenes;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", MainActivity.currentLocale);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detalleevento);
		Bundle e = getIntent().getExtras();
		try {
			edNombre = (TextView) findViewById(R.id.txtDetalleNombre);
			edDesc = (TextView) findViewById(R.id.txtDetalleDesc);
			txtAsistentes = (TextView) findViewById(R.id.txtDetalleAsistentes);
			galeria = (Gallery) findViewById(R.id.imgDetalleImagen);
			txtDir = (TextView) findViewById(R.id.txtDetalleDir);
			txtDireccion = (TextView) findViewById(R.id.txtDetalleDireccion);
			txtDetalleDist = (TextView) findViewById(R.id.txtDetalleDist);
			txtDetalleDistancia = (TextView) findViewById(R.id.txtDetalleDistancia);
			txtDetalleFechaIni = (TextView) findViewById(R.id.txtDetalleFechaIni);
			txtDetalleFechaInicio = (TextView) findViewById(R.id.txtDetalleFechaInicio);
			txtDetalleFechaFin = (TextView) findViewById(R.id.txtDetalleFechaFin);
			txtDetalleFechaFinal = (TextView) findViewById(R.id.txtDetalleFechaFinal);
			btnSi = (Button) findViewById(R.id.btnDetalleSi);
			btnNo = (Button) findViewById(R.id.btnDetalleNo);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
			galeria.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent i = new Intent(detalleEventoActivity.this, galeriaActivity.class);
					imagenes = evento.getImagenes();
					startActivity(i);
				}
			});

			String URL = "http://desipal.hol.es/app/eventos/verEvento.php";
			final detalleEvento peticion = new detalleEvento(parametros, detalleEventoActivity.this);
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
			Toast.makeText(this, "Error: " + ex.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	public void verEvento() {
		edNombre.setText(evento.getNombre());
		edDesc.setText(evento.getDescripcion());
		txtAsistentes.setText(evento.getAsistencia() + " "
				+ detalleEventoActivity.this.getString(R.string.detalleEventoAsistencia));
		galeria.setAdapter(new galeriaAdapter(this, evento.getImagenes(),false));
		String direccion = "";
		if (!evento.getDireccion().split(",")[4].equals(""))
			direccion = direccion + evento.getDireccion().split(",")[4] + ",";
		if (!evento.getDireccion().split(",")[5].equals(""))
			direccion = direccion + evento.getDireccion().split(",")[5] + ",";
		direccion = direccion + evento.getDireccion().split(",")[3] + "(" + evento.getDireccion().split(",")[2] + ")";
		txtDireccion.setText(direccion);
		txtDir.setText("Dirección: ");
		txtDetalleDist.setText("Distancia: ");
		String distancia = new DecimalFormat("#.##").format(evento.getDistancia()) + " Km.";
		txtDetalleDistancia.setText(distancia);

		String fechaI = dateFormat.format(evento.getFechaInicio());
		String fechaF = dateFormat.format(evento.getFechaFin());

		txtDetalleFechaIni.setText(R.string.crearEventoFechaInicio);
		txtDetalleFechaInicio.setText(fechaI + " Durante todo el día");
		if (!evento.isTodoElDia()) {
			txtDetalleFechaFin.setText(R.string.crearEventoFechaFin);
			txtDetalleFechaFinal.setText(fechaF);
		}

		btnSi.setVisibility(View.VISIBLE);
		btnNo.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}
}
