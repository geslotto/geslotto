package com.desipal.eventu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.adaptadorEventoEN;
import com.desipal.Librerias.Herramientas;
import com.desipal.Librerias.LoadMoreListView;
import com.desipal.Librerias.LoadMoreListView.OnLoadMoreListener;
import com.desipal.Servidor.buscarEventos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Fragment2 extends Fragment {
	public static int ELEMENTOSLISTA = 8;

	public static List<adaptadorEventoEN> eventosCerca = new ArrayList<adaptadorEventoEN>();
	public static List<adaptadorEventoEN> eventos = new ArrayList<adaptadorEventoEN>();
	AtomicReference<List<adaptadorEventoEN>> refCerca = new AtomicReference<List<adaptadorEventoEN>>();

	boolean bloquearPeticion = false;// bandera que bloquea para no poder jhacer la peticion

	Double latitud;
	Double longitud;
	int ratio;

	LoadMoreListView gridCerca;

	Button btnBuscarCerca;
	TextView txtKm;
	TextView txtErrorCerca;
	SeekBar seekRadio;
	ProgressBar progressCerca;
	ToggleButton togOpcionMapa;
	GoogleMap map;
	RelativeLayout LayoutMapa;
	List<Marker> listapuntos = new ArrayList<Marker>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tabcerca, container, false);
		gridCerca = (LoadMoreListView) view.findViewById(R.id.gridResultadosCerca);
		txtKm = (TextView) view.findViewById(R.id.txtKm);
		seekRadio = (SeekBar) view.findViewById(R.id.seekRadio);
		progressCerca = (ProgressBar) view.findViewById(R.id.proResulCerca);
		txtErrorCerca = (TextView) view.findViewById(R.id.txtErrorCerca);
		togOpcionMapa = (ToggleButton) view.findViewById(R.id.togOpcionMapa);
		btnBuscarCerca = (Button) view.findViewById(R.id.btnBuscarCerca);
		LayoutMapa = (RelativeLayout) view.findViewById(R.id.relativeMapa);
		map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapaCercanos))
				.getMap();
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				for (Marker item : listapuntos) {
					if (item.equals(marker)) {
						int i = listapuntos.indexOf(item);
						adaptadorEventoEN sel = Fragment2.eventosCerca.get(i);
						Intent ac = new Intent(getActivity(), detalleEventoActivity.class);
						ac.putExtra("idEvento", (long) sel.getIdEvento());
						startActivity(ac);
						break;
					}
				}

			}
		});
		txtKm.setText("50 Km");
		seekRadio.setProgress(50);
		seekRadio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				txtKm.setText(progress + " Km");
			}
		});
		gridCerca.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				Intent i = new Intent(getActivity(), detalleEventoActivity.class);
				i.putExtra("idEvento", id);
				startActivity(i);
			}
		});

		btnBuscarCerca.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBuscarCerca.setEnabled(false);
				progressCerca.setVisibility(View.VISIBLE);
				LayoutMapa.setVisibility(View.GONE);
				gridCerca.setVisibility(View.GONE);
				txtErrorCerca.setVisibility(View.GONE);
				togOpcionMapa.setEnabled(false);
				eventos.clear();
				bloquearPeticion = false;
				hacerPeticion();
			}
		});
		btnBuscarCerca.performClick();
		togOpcionMapa.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// MAPA
					map.clear();
					gridCerca.setVisibility(View.GONE);
					progressCerca.setVisibility(View.GONE);
					LayoutMapa.setVisibility(View.VISIBLE); // Pointer de mi posicion
					LatLng MiPosicion = Herramientas.ObtenerLocalizacion(getActivity());
					map.addMarker(new MarkerOptions().position(MiPosicion).title("Estas aquí"));
					listapuntos.clear();
					for (adaptadorEventoEN item : eventos) {
						LatLng Posicion = new LatLng(item.getLatitud(), item.getLongitud());
						listapuntos.add(map.addMarker(new MarkerOptions().position(Posicion).title(item.getNombre())
								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
					}
					CircleOptions circleOptions = new CircleOptions().center(MiPosicion).radius(ratio * 1000)
					// set radius in meters
							.fillColor(Color.argb(20, 0, 0, 255)).strokeColor(Color.BLUE).strokeWidth((float) 1.5);
					map.addCircle(circleOptions);
					int zoom = Herramientas.calcularZoom(ratio);
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(MiPosicion, zoom));
					map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);

					togOpcionMapa.setEnabled(true);

				} else {
					LayoutMapa.setVisibility(View.GONE);
					gridCerca.setVisibility(View.VISIBLE);
					gridCerca.setAdapter(new GridViewAdapter(getActivity(), eventos));
					progressCerca.setVisibility(View.GONE);
					togOpcionMapa.setEnabled(true);
				}

			}
		});
		gridCerca.setOnLoadMoreListener(new OnLoadMoreListener() {
			public void onLoadMore() {
				if (eventos.size() % ELEMENTOSLISTA == 0 && !bloquearPeticion) {
					gridCerca.mFooterView.setVisibility(View.VISIBLE);
					hacerPeticion();
				} else
					gridCerca.mFooterView.setVisibility(View.GONE);
			}
		});
		return view;
	}

	protected void hacerPeticion() {

		LatLng loc = Herramientas.ObtenerLocalizacion(getActivity());
		latitud = loc.latitude;
		longitud = loc.longitude;

		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("latitud", latitud.toString()));
		parametros.add(new BasicNameValuePair("longitud", longitud.toString()));
		int pagina = 0;
		String URL = "";

		ratio = seekRadio.getProgress();
		parametros.add(new BasicNameValuePair("ratio", ratio + ""));
		if (!togOpcionMapa.isChecked()) {
			pagina = ((int) eventos.size() / ELEMENTOSLISTA) + 1;
			parametros.add(new BasicNameValuePair("page", pagina + ""));
		}

		URL = "http://desipal.hol.es/app/eventos/eventosCerca.php";

		final buscarEventos peticion = new buscarEventos(parametros, getActivity(), refCerca);
		peticion.execute(new String[] { URL });

		final GridViewAdapter adaptador = new GridViewAdapter(getActivity(), eventos);
		gridCerca.setAdapter(adaptador);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Status s = peticion.getStatus();
				if (s.name().equals("FINISHED")) {
					eventosCerca = refCerca.get();

					if (eventos.size() > 0 && eventosCerca.size() == 0)
						bloquearPeticion = true;
					else
						bloquearPeticion = false;
					btnBuscarCerca.setEnabled(true);
					if (eventosCerca.size() > 0) {
						eventos.addAll(eventosCerca);
						if (togOpcionMapa.isChecked()) { // MAPA
							map.clear();
							progressCerca.setVisibility(View.GONE);
							LayoutMapa.setVisibility(View.VISIBLE); // Pointer de mi posicion
							LatLng MiPosicion = new LatLng(latitud, longitud);
							map.addMarker(new MarkerOptions().position(MiPosicion).title("Estas aquí"));
							listapuntos.clear();
							for (adaptadorEventoEN item : eventosCerca) {
								LatLng Posicion = new LatLng(item.getLatitud(), item.getLongitud());
								listapuntos.add(map.addMarker(new MarkerOptions().position(Posicion)
										.title(item.getNombre())
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
							}
							CircleOptions circleOptions = new CircleOptions().center(MiPosicion).radius(ratio * 1000)
									// set radius in meters
									.fillColor(Color.argb(20, 0, 0, 255)).strokeColor(Color.BLUE)
									.strokeWidth((float) 1.5);
							map.addCircle(circleOptions);
							int zoom = Herramientas.calcularZoom(ratio);
							map.moveCamera(CameraUpdateFactory.newLatLngZoom(MiPosicion, zoom));
							map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);

							togOpcionMapa.setEnabled(true);
						} else { // Lista
							gridCerca.setVisibility(View.VISIBLE);

							adaptador.notifyDataSetChanged();
							gridCerca.onLoadMoreComplete();
							progressCerca.setVisibility(View.GONE);
							togOpcionMapa.setEnabled(true);
						}
					} else {
						txtErrorCerca.setVisibility(View.VISIBLE);
						progressCerca.setVisibility(View.GONE);
					}

				} else
					handler.postDelayed(this, 500);
			}
		}, 500);
	}
}
