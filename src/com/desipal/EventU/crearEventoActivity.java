package com.desipal.EventU;

import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class crearEventoActivity extends Activity {
	private TextView txtFecha;
	private TextView txtHora;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crearevento);

		Spinner spiCategoria = (Spinner) findViewById(R.id.spiCategorias);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.categorias,
				R.layout.spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);
		Spinner spiEdad = (Spinner) findViewById(R.id.spiEdad);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
				.createFromResource(this, R.array.edad, R.layout.spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiEdad.setAdapter(adapter2);

		Button btnInicio = (Button) findViewById(R.id.btnInicio);
		btnInicio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			}
		});

		CheckBox chTodo = (CheckBox) findViewById(R.id.chTodoElDia);
		chTodo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TableRow filaFin = (TableRow) findViewById(R.id.table2Row4);
				TableRow filaTexFin = (TableRow) findViewById(R.id.table2Row3);
				if (isChecked) {
					filaFin.setVisibility(View.GONE);
					filaTexFin.setVisibility(View.GONE);
				} else {
					filaFin.setVisibility(View.VISIBLE);
					filaTexFin.setVisibility(View.VISIBLE);
				}
			}
		});

		EditText edFechaIni = (EditText) findViewById(R.id.editFechaInicio);
		edFechaIni.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtFecha = (EditText) v.findViewById(R.id.editFechaInicio);
				showDialog(0);
			}
		});

		EditText edFechaFin = (EditText) findViewById(R.id.editFechaFin);
		edFechaFin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtFecha = (EditText) v.findViewById(R.id.editFechaFin);
				showDialog(0);
			}
		});

		EditText edHoraInicio = (EditText) findViewById(R.id.editHoraInicio);
		edHoraInicio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtHora = (EditText) v.findViewById(R.id.editHoraInicio);
				showDialog(1);
			}
		});

		EditText edHoraFin = (EditText) findViewById(R.id.editHoraFin);
		edHoraFin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txtHora = (EditText) v.findViewById(R.id.editHoraFin);
				showDialog(1);
			}
		});
		ToggleButton tgUbicacion = (ToggleButton) findViewById(R.id.togUbicacion);
		tgUbicacion.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EditText edProvincia = (EditText) findViewById(R.id.editProvincia);
				EditText edPoblacion = (EditText) findViewById(R.id.editPoblacion);
				if (isChecked) {
					edProvincia.setText("Canillejas");
					edPoblacion.setText("Zaragoza");
					edProvincia.setEnabled(false);
					edPoblacion.setEnabled(false);
				} else {
					edProvincia.setText("");
					edPoblacion.setText("");
					edProvincia.setEnabled(true);
					edPoblacion.setEnabled(true);
				}
			}
		});

		Button btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);
		btnCrearEvento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(crearEventoActivity.this, "CREAR EVENTO", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	// //////// VENTANA MODAL PARA INTRODUCIR FECHA
	@Override
	protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
		if (id == 0) {
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog datePicker = new DatePickerDialog(this, datePickerListener, year, month, day);
			datePicker.setTitle("Introduzca fecha");
			return datePicker;
		} else {
			hour = c.get(Calendar.HOUR);
			minute = c.get(Calendar.MINUTE);
			TimePickerDialog timPicker = new TimePickerDialog(this, timePickerListener, hour, minute, true);
			timPicker.setTitle("Introduzca hora");
			return timPicker;
		}

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

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0, int horaSel, int minSel) {
			// TODO Apéndice de método generado automáticamente
			hour = horaSel;
			minute = minSel;
			String min = Integer.toString(minute);
			String hor = Integer.toString(hour);
			if (hour < 10)
				hor = "0" + hor;
			if (minute < 10)
				min = "0" + min;
			txtHora.setText(new StringBuilder().append(hor).append(":").append(min));
		}
	};

}
