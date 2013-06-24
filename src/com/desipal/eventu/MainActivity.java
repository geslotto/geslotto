package com.desipal.eventu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.adaptadorEventoEN;
import com.desipal.Librerias.Herramientas;
import com.desipal.eventu.R;
import com.desipal.Servidor.buscarEventos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends FragmentActivity {

	Double latitud;
	Double longitud;
	int ratio;
	static Locale currentLocale = new Locale("es", "ES");

	public static List<adaptadorEventoEN> eventosCerca = new ArrayList<adaptadorEventoEN>();
	public static List<adaptadorEventoEN> eventosFiltro = new ArrayList<adaptadorEventoEN>();

	AtomicReference<List<adaptadorEventoEN>> ref = new AtomicReference<List<adaptadorEventoEN>>();
	public static Display display;

	private boolean recogido = true;
	private int year;
	private int month;
	private int day;

	ListView gridResultados;
	ListView gridCerca;

	Button btnBuscarCerca;
	Button btnRecoger;
	Button btnFecha;
	Button btnFiltrar;
	Button btnCrearEvento;

	LinearLayout filtro;

	EditText campoFiltro;
	TextView txtFecha;
	TextView txtKm;
	TextView txtErrorCerca;
	TextView txtErrorResultados;
	ImageButton btnLimpiar;
	Spinner spiCategoria;
	SeekBar seekRadio;

	ProgressBar progressResult;
	ProgressBar progressCerca;
	ToggleButton togOpcionMapa;
	GoogleMap map;
	RelativeLayout LayoutMapa;
	List<Marker> listapuntos = new ArrayList<Marker>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		display = getWindowManager().getDefaultDisplay();
		Resources res = getResources();

		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("Inicio");
		spec.setContent(R.id.tab1Layout);
		spec.setIndicator("Inicio", res.getDrawable(R.drawable.ic_menu_home));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("Cerca de mí");
		spec.setContent(R.id.tab2Layout);
		spec.setIndicator("Cerca de mí",
				res.getDrawable(android.R.drawable.ic_menu_myplaces));
		tabs.addTab(spec);
		tabs.setCurrentTab(0);

		gridCerca = (ListView) findViewById(R.id.gridResultadosCerca);
		gridResultados = (ListView) findViewById(R.id.gridResultados);
		filtro = (LinearLayout) findViewById(R.id.linear_filtro);

		btnBuscarCerca = (Button) findViewById(R.id.btnBuscarCerca);
		btnRecoger = (Button) findViewById(R.id.btn_recoger);
		btnFecha = (Button) findViewById(R.id.btnFecha);
		btnFiltrar = (Button) findViewById(R.id.btnBuscar);
		btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);

		btnLimpiar = (ImageButton) findViewById(R.id.btnLimpiar);

		campoFiltro = (EditText) findViewById(R.id.campoFiltro);
		txtFecha = (TextView) findViewById(R.id.campoFecha);
		txtKm = (TextView) findViewById(R.id.txtKm);

		spiCategoria = (Spinner) findViewById(R.id.spiCategorias);
		seekRadio = (SeekBar) findViewById(R.id.seekRadio);
		progressResult = (ProgressBar) findViewById(R.id.proResultados);
		progressCerca = (ProgressBar) findViewById(R.id.proResulCerca);
		txtErrorResultados = (TextView) findViewById(R.id.txtErrorResultados);
		txtErrorCerca = (TextView) findViewById(R.id.txtErrorCerca);
		togOpcionMapa = (ToggleButton) findViewById(R.id.togOpcionMapa);

		LayoutMapa = (RelativeLayout) findViewById(R.id.relativeMapa);
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapaCercanos)).getMap();
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				for (Marker item : listapuntos) {
					if (item.equals(marker)) {
						int i = listapuntos.indexOf(item);
						adaptadorEventoEN sel = eventosCerca.get(i);
						Intent ac = new Intent(MainActivity.this,
								detalleEventoActivity.class);
						ac.putExtra("idEvento", (long) sel.getIdEvento());
						startActivity(ac);
						break;
					}
				}

			}
		});
		btnFecha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(999);
			}
		});

		btnRecoger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				recoger();
			}
		});

		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.categorias, R.layout.spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);

		btnFiltrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// OCULTAR TECLADO
				InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(campoFiltro.getWindowToken(), 0);
				btnFiltrar.setEnabled(false);
				txtErrorResultados.setVisibility(View.GONE);
				progressResult.setVisibility(View.VISIBLE);
				gridResultados.setVisibility(View.GONE);
				MainActivity.eventosFiltro.clear();
				LatLng loc= Herramientas.ObtenerLocalizacion(MainActivity.this);
				Double latitud = loc.latitude;
				Double longitud = loc.longitude;
				ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
				parametros.add(new BasicNameValuePair("filtro", campoFiltro
						.getText().toString()));
				parametros.add(new BasicNameValuePair("fecha", txtFecha
						.getText().toString()));
				parametros.add(new BasicNameValuePair("latitud", latitud
						.toString()));
				parametros.add(new BasicNameValuePair("longitud", longitud
						.toString()));

				String URL = "http://desipal.hol.es/app/eventos/filtro.php";
				final buscarEventos peticion = new buscarEventos(parametros,
						MainActivity.this, ref);
				peticion.execute(new String[] { URL });
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Status s = peticion.getStatus();
						if (s.name().equals("FINISHED")) {
							eventosFiltro = ref.get();
							btnFiltrar.setEnabled(true);
							if (eventosFiltro.size() > 0) {
								recoger();
								gridResultados.setVisibility(View.VISIBLE);
								gridResultados.setAdapter(new GridViewAdapter(
										MainActivity.this, eventosFiltro));
								progressResult.setVisibility(View.GONE);
							} else {
								txtErrorResultados.setVisibility(View.VISIBLE);
								progressResult.setVisibility(View.GONE);
							}
						} else
							handler.postDelayed(this, 500);
					}
				}, 500);
			}
		});
		btnFiltrar.performClick();
		btnLimpiar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				gridResultados.setAdapter(null);
				EditText txtFiltro = (EditText) findViewById(R.id.campoFiltro);
				txtFiltro.setText("");
				EditText txtFecha = (EditText) findViewById(R.id.campoFecha);
				txtFecha.setText("");
			}
		});

		btnCrearEvento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						crearEventoActivity.class);
				startActivity(i);
			}
		});

		txtKm.setText("50 Km");

		seekRadio.setProgress(50);
		seekRadio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				txtKm.setText(progress + " Km");
			}
		});

		// @SuppressWarnings("deprecation")
		// int anchura = display.getWidth();

		// int ancho[] = new int[2];
		// ancho = redimensionarColumnas(anchura);

		// gridCerca.setNumColumns(ancho[0]);
		// gridCerca.setColumnWidth(ancho[1]);
		// gridResultados.setNumColumns(ancho[0]);
		// gridResultados.setColumnWidth(ancho[1]);
		// gridCerca.setNumColumns(1);
		// gridCerca.setColumnWidth(320);

		gridCerca.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Intent i = new Intent(MainActivity.this,
						detalleEventoActivity.class);
				i.putExtra("idEvento", id);
				startActivity(i);
			}
		});

		gridResultados.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Intent i = new Intent(MainActivity.this,
						detalleEventoActivity.class);
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
				MainActivity.eventosCerca.clear();
				LatLng localizacion = Herramientas
						.ObtenerLocalizacion(MainActivity.this);
				latitud = localizacion.latitude;
				longitud = localizacion.longitude;
				ratio = seekRadio.getProgress();
				ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
				parametros.add(new BasicNameValuePair("latitud", latitud
						.toString()));
				parametros.add(new BasicNameValuePair("longitud", longitud
						.toString()));
				parametros.add(new BasicNameValuePair("ratio", ratio + ""));

				String URL = "http://desipal.hol.es/app/eventos/eventosCerca.php";
				final buscarEventos peticion = new buscarEventos(parametros,
						MainActivity.this, ref);
				peticion.execute(new String[] { URL });
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Status s = peticion.getStatus();
						if (s.name().equals("FINISHED")) {
							eventosCerca = ref.get();
							btnBuscarCerca.setEnabled(true);
							if (eventosCerca.size() > 0) {
								if (togOpcionMapa.isChecked()) {
									// MAPA
									map.clear();
									progressCerca.setVisibility(View.GONE);
									LayoutMapa.setVisibility(View.VISIBLE);
									// Pointer de mi posicion
									LatLng MiPosicion = new LatLng(latitud,
											longitud);
									map.addMarker(new MarkerOptions().position(
											MiPosicion).title("Estas aquí"));
									listapuntos.clear();
									for (adaptadorEventoEN item : eventosCerca) {
										LatLng Posicion = new LatLng(item
												.getLatitud(), item
												.getLongitud());
										listapuntos
												.add(map.addMarker(new MarkerOptions()
														.position(Posicion)
														.title(item.getNombre())
														.icon(BitmapDescriptorFactory
																.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
									}
									CircleOptions circleOptions = new CircleOptions()
											.center(MiPosicion)
											// set center
											.radius(ratio * 1000)
											// set radius in meters
											.fillColor(
													Color.argb(20, 0, 0, 255))
											// default
											.strokeColor(Color.BLUE)
											.strokeWidth((float) 1.5);
									map.addCircle(circleOptions);
									int zoom = Herramientas.calcularZoom(ratio);
									map.moveCamera(CameraUpdateFactory
											.newLatLngZoom(MiPosicion, zoom));
									map.animateCamera(
											CameraUpdateFactory.zoomTo(zoom),
											2000, null);
								} else {
									// Lista
									gridCerca.setVisibility(View.VISIBLE);
									gridCerca.setAdapter(new GridViewAdapter(
											MainActivity.this, eventosCerca));
									progressCerca.setVisibility(View.GONE);
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
		});
		btnBuscarCerca.performClick();
	}

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig); display =
	 * getWindowManager().getDefaultDisplay(); //int ancho[] = new int[2];
	 * 
	 * @SuppressWarnings("deprecation") //int anchura = display.getWidth();
	 * //ancho = redimensionarColumnas(anchura);
	 * 
	 * //gridCerca.setNumColumns(ancho[0]);
	 * //gridCerca.setColumnWidth(ancho[1]);
	 * 
	 * }
	 */

	// //////// RECOGER FILTRO
	protected void recoger() {
		Handler handler = new Handler();
		if (recogido) {
			btnRecoger.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					android.R.drawable.arrow_up_float, 0);
			TranslateAnimation anim_trans = new TranslateAnimation(0, 0, 0,
					filtro.getHeight());
			anim_trans.setDuration(200);
			gridResultados.startAnimation(anim_trans);
			handler.postDelayed(new Runnable() {
				public void run() {
					filtro.setVisibility(View.VISIBLE);
					AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
					anim_alpha.setDuration(200);
					filtro.startAnimation(anim_alpha);
				}
			}, 220);
			recogido = false;
		} else {
			btnRecoger.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					android.R.drawable.arrow_down_float, 0);
			AlphaAnimation anim_trans = new AlphaAnimation(1, 0);
			anim_trans.setDuration(200);
			filtro.startAnimation(anim_trans);
			handler.postDelayed(new Runnable() {
				public void run() {
					filtro.setVisibility(View.GONE);
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, filtro.getHeight(), 0);
					anim_trans.setDuration(200);
					gridResultados.startAnimation(anim_trans);
				}
			}, 200);
			recogido = true;
		}
	}

	// //////// TAMAÑO DE LAS COLUMNAS GRIDVIEW
	protected int[] redimensionarColumnas(int anchura) {
		int retorno[] = new int[2];
		if (anchura < 400) {
			retorno[0] = 1;
			retorno[1] = anchura - 10;
		} else if (anchura < 600) {
			retorno[0] = 2;
			retorno[1] = (anchura / 2) - 10;
		} else if (anchura < 800) {
			retorno[0] = 3;
			retorno[1] = (anchura / 3) - 10;
		} else if (anchura < 1200) {
			retorno[0] = 3;
			retorno[1] = (anchura / 3) - 10;
		} else {
			retorno[0] = 4;
			retorno[1] = (anchura / 4) - 10;
		}
		return retorno;
	}

	// //////// VENTANA MODAL PARA INTRODUCIR FECHA
	@Override
	protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(this, datePickerListener, year, month, day);
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			String dia = Integer.toString(day);
			String mes = Integer.toString(month + 1);
			if (day < 10)
				dia = "0" + dia;
			if (month + 1 < 10)
				mes = "0" + mes;
			// set selected date into textview
			txtFecha.setText(new StringBuilder().append(dia).append("/")
					.append(mes).append("/").append(year));
		}
	};
}