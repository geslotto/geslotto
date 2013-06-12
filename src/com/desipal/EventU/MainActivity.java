package com.desipal.EventU;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.adaptadorEventoEN;
import com.desipal.EventU.R;
import com.desipal.Librerias.ExpandableHeightGridView;
import com.desipal.Servidor.buscarEventosCerca;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

	static Locale currentLocale = new Locale("es", "ES");

	public static List<adaptadorEventoEN> adaptadorEventosURL = new ArrayList<adaptadorEventoEN>();
	public static Display display;
	private TextView txtFecha;

	private boolean recogido = true;
	private int year;
	private int month;
	private int day;

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
		spec.setIndicator("Cerca de mí", res.getDrawable(android.R.drawable.ic_menu_myplaces));
		tabs.addTab(spec);
		tabs.setCurrentTab(0);

		txtFecha = (TextView) findViewById(R.id.campoFecha);
		Button btnFecha = (Button) findViewById(R.id.btnFecha);
		btnFecha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(999);
			}
		});

		final Button btnRecoger = (Button) findViewById(R.id.btn_recoger);
		btnRecoger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final LinearLayout filtro = (LinearLayout) findViewById(R.id.linear_filtro);
				Handler handler = new Handler();
				if (recogido) {
					btnRecoger.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
					GridView gridview = (GridView) findViewById(R.id.gridResultados);
					TranslateAnimation anim_trans = new TranslateAnimation(0, 0, 0, filtro.getHeight());
					anim_trans.setDuration(200);
					gridview.startAnimation(anim_trans);
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
					btnRecoger.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
					AlphaAnimation anim_trans = new AlphaAnimation(1, 0);
					anim_trans.setDuration(200);
					filtro.startAnimation(anim_trans);
					handler.postDelayed(new Runnable() {
						public void run() {
							filtro.setVisibility(View.GONE);
							GridView gridview = (GridView) findViewById(R.id.gridResultados);
							TranslateAnimation anim_trans = new TranslateAnimation(0, 0, filtro.getHeight(), 0);
							anim_trans.setDuration(200);
							gridview.startAnimation(anim_trans);
						}
					}, 200);
					recogido = true;
				}
			}
		});
		Spinner spiCategoria = (Spinner) findViewById(R.id.spiCategorias);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.categorias,
				R.layout.spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);

		Button btnFiltrar = (Button) findViewById(R.id.btnBuscar);
		btnFiltrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GridView gridview = (GridView) findViewById(R.id.gridResultados);
				gridview.setAdapter(new GridViewAdapter(MainActivity.this, adaptadorEventosURL));
			}
		});

		ImageButton btnLimpiar = (ImageButton) findViewById(R.id.btnLimpiar);
		btnLimpiar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GridView gridview = (GridView) findViewById(R.id.gridResultados);
				gridview.setAdapter(null);
				EditText txtFiltro = (EditText) findViewById(R.id.campoFiltro);
				txtFiltro.setText("");
				EditText txtFecha = (EditText) findViewById(R.id.campoFecha);
				txtFecha.setText("");
			}
		});

		Button btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);
		btnCrearEvento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, crearEventoActivity.class);
				startActivity(i);
			}
		});

		final TextView txtKm = (TextView) findViewById(R.id.txtKm);
		txtKm.setText("50 Km");
		final SeekBar seekRadio = (SeekBar) findViewById(R.id.seekRadio);
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

		ExpandableHeightGridView grid = (ExpandableHeightGridView) findViewById(R.id.gridResultadosCerca);
		int anchura = display.getWidth();

		int ancho[] = new int[2];
		ancho = redimensionarColumnas(anchura);

		grid.setNumColumns(ancho[0]);
		grid.setColumnWidth(ancho[1]);

		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				Intent i = new Intent(MainActivity.this, detalleEventoActivity.class);
				i.putExtra("idEvento", id);
				startActivity(i);
			}
		});
		Button btnBuscarCerca = (Button) findViewById(R.id.btnBuscarCerca);
		btnBuscarCerca.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.adaptadorEventosURL.clear();
				LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				Location loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				Double latitud = loc.getLatitude();
				Double longitud = loc.getLongitude();
				int ratio = seekRadio.getProgress();
				ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
				parametros.add(new BasicNameValuePair("latitud", latitud.toString()));
				parametros.add(new BasicNameValuePair("longitud", longitud.toString()));
				parametros.add(new BasicNameValuePair("ratio", ratio + ""));

				String URL = "http://desipal.hol.es/app/eventos/eventosCerca.php";
				final buscarEventosCerca peticion = new buscarEventosCerca(parametros, MainActivity.this);
				peticion.execute(new String[] { URL });
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Status s = peticion.getStatus();
						if (s.name().equals("FINISHED")) {
							if (adaptadorEventosURL.size() > 0) {
								GridView gridview = (GridView) findViewById(R.id.gridResultadosCerca);
								gridview.setAdapter(new GridViewAdapter(MainActivity.this, adaptadorEventosURL));

							}
						} else
							handler.postDelayed(this, 500);
					}
				}, 500);

			}
		});

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		display = getWindowManager().getDefaultDisplay();
		int rotation = display.getOrientation();
		int ancho[] = new int[2];
		ExpandableHeightGridView grid = (ExpandableHeightGridView) findViewById(R.id.gridResultadosCerca);

		int anchura = display.getWidth();
		ancho = redimensionarColumnas(anchura);

		grid.setNumColumns(ancho[0]);
		grid.setColumnWidth(ancho[1]);

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
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			String dia = Integer.toString(day + 1);
			String mes = Integer.toString(month + 1);
			if (day + 1 < 10)
				dia = "0" + dia;
			if (month + 1 < 10)
				mes = "0" + mes;
			// set selected date into textview
			txtFecha.setText(new StringBuilder().append(dia).append("/").append(mes).append("/").append(year));
		}
	};
}