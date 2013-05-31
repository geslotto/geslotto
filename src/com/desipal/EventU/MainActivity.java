package com.desipal.EventU;

import java.util.Calendar;

import com.desipal.EventU.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	private TextView txtFecha;

	private boolean recogido = true;
	private int year;
	private int month;
	private int day;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

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

					btnRecoger.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							android.R.drawable.arrow_up_float, 0);
					GridView gridview = (GridView) findViewById(R.id.gridResultados);
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, 0, filtro.getHeight());
					anim_trans.setDuration(200);
					gridview.startAnimation(anim_trans);
					handler.postDelayed(new Runnable() {
						public void run() {
							filtro.setVisibility(View.VISIBLE);
							AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
							anim_alpha.setDuration(200);
							filtro.startAnimation(anim_alpha);
						}
					}, 200);
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
							GridView gridview = (GridView) findViewById(R.id.gridResultados);
							TranslateAnimation anim_trans = new TranslateAnimation(
									0, 0, filtro.getHeight(), 0);
							anim_trans.setDuration(200);
							gridview.startAnimation(anim_trans);
						}
					}, 200);
					recogido = true;
				}
			}
		});
		Spinner spiCategoria = (Spinner) findViewById(R.id.spiCategorias);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.categorias, R.layout.spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);
		Spinner spiEdad = (Spinner) findViewById(R.id.spiEdad);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
				this, R.array.edad, R.layout.spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiEdad.setAdapter(adapter2);

		Button btnFiltrar = (Button) findViewById(R.id.btnBuscar);
		btnFiltrar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GridView gridview = (GridView) findViewById(R.id.gridResultados);
				gridview.setAdapter(new GridViewAdapter(MainActivity.this));
			}
		});

		Button btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
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
				Intent i = new Intent(MainActivity.this,
						crearEventoActivity.class);
				startActivity(i);
			}
		});

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
			String dia = Integer.toString(day + 1);
			String mes = Integer.toString(month + 1);
			if (day + 1 < 10)
				dia = "0" + dia;
			if (month + 1 < 10)
				mes = "0" + mes;
			// set selected date into textview
			txtFecha.setText(new StringBuilder().append(dia).append("/")
					.append(mes).append("/").append(year));

		}
	};

}