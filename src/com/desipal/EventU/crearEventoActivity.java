package com.desipal.EventU;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.desipal.Entidades.eventoEN;
import com.desipal.Entidades.fechaEN;
import com.desipal.Servidor.creacionEvento;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class crearEventoActivity extends Activity {

	private int ESCALAMAXIMA = 300;// Tamaño máximo de las imagenes subidas
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/mm/yyyy hh:mm", MainActivity.currentLocale);
	private static Activity actividad;
	private TextView txtFecha;
	private TextView txtHora;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	public static boolean Creacionsatisfactoria;
	private boolean error = false;

	public static List<Bitmap> arrayImagen;
	double Latitud;
	double Longitud;
	InputStream is;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crearevento);
		actividad = this;
		arrayImagen = new ArrayList<Bitmap>();
		Spinner spiCategoria = (Spinner) findViewById(R.id.spiCategorias);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.categorias, R.layout.spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);

		Button btnInicio = (Button) findViewById(R.id.btnInicio);
		btnInicio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_right);
			}
		});

		CheckBox chTodo = (CheckBox) findViewById(R.id.chTodoElDia);
		chTodo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				final TableRow filaFin = (TableRow) findViewById(R.id.table2Row4);
				final TableRow filaTexFin = (TableRow) findViewById(R.id.table2Row3);
				final RelativeLayout relImagenes = (RelativeLayout) findViewById(R.id.relImagenes);
				Handler handler = new Handler();
				if (isChecked) {
					AlphaAnimation anim_alpha = new AlphaAnimation(1, 0);
					anim_alpha.setDuration(200);
					filaFin.startAnimation(anim_alpha);
					filaTexFin.startAnimation(anim_alpha);
					handler.postDelayed(new Runnable() {
						public void run() {
							filaFin.setVisibility(View.GONE);
							filaTexFin.setVisibility(View.GONE);
							int altura = filaFin.getHeight()
									+ filaTexFin.getHeight();
							TranslateAnimation anim_trans = new TranslateAnimation(
									0, 0, altura, 0);
							anim_trans.setDuration(200);
							relImagenes.startAnimation(anim_trans);
						}
					}, 200);

				} else {
					int altura = filaFin.getHeight() + filaTexFin.getHeight();
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, 0, altura);
					anim_trans.setDuration(200);
					relImagenes.startAnimation(anim_trans);
					handler.postDelayed(new Runnable() {
						public void run() {
							filaFin.setVisibility(View.VISIBLE);
							filaTexFin.setVisibility(View.VISIBLE);
							AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
							anim_alpha.setDuration(200);
							filaFin.startAnimation(anim_alpha);
							filaTexFin.startAnimation(anim_alpha);
						}
					}, 220);
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
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				final TableLayout tableLocalizacion = (TableLayout) findViewById(R.id.tableLocalizacion);
				final RelativeLayout relFechas = (RelativeLayout) findViewById(R.id.relFechas);
				Handler handler = new Handler();
				if (isChecked) {
					AlphaAnimation anim_alpha = new AlphaAnimation(1, 0);
					anim_alpha.setDuration(200);
					tableLocalizacion.startAnimation(anim_alpha);
					handler.postDelayed(new Runnable() {
						public void run() {
							tableLocalizacion.setVisibility(View.GONE);
							TranslateAnimation anim_trans = new TranslateAnimation(
									0, 0, tableLocalizacion.getHeight(), 0);
							anim_trans.setDuration(200);
							relFechas.startAnimation(anim_trans);
						}
					}, 200);
					try {
						LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
						Location loc = locManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						Latitud = loc.getLatitude();
						Longitud = loc.getLongitude();
					} catch (Exception e) {
						Toast.makeText(actividad, "Error:" + e.getMessage(),
								Toast.LENGTH_LONG).show();						
					}
				} else {
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, 0, tableLocalizacion.getHeight());
					anim_trans.setDuration(200);
					relFechas.startAnimation(anim_trans);
					handler.postDelayed(new Runnable() {
						public void run() {
							tableLocalizacion.setVisibility(View.VISIBLE);
							AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
							anim_alpha.setDuration(200);
							tableLocalizacion.startAnimation(anim_alpha);
						}
					}, 220);
				}
			}
		});

		Button btnSubirImagen = (Button) findViewById(R.id.btnSubirImagen);
		btnSubirImagen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent pickPhoto = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(pickPhoto, 1);
			}
		});

		final Button btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);
		btnCrearEvento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCrearEvento.setEnabled(false);
				crearEvento();
			}
		});
	}

	// //////// VENTANA MODAL PARA INTRODUCIR FECHA y HORA
	@Override
	protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
		if (id == 0) {
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog datePicker = new DatePickerDialog(this,
					datePickerListener, year, month, day);
			datePicker.setTitle("Introduzca fecha");
			return datePicker;
		} else {
			hour = c.get(Calendar.HOUR);
			minute = c.get(Calendar.MINUTE);
			TimePickerDialog timPicker = new TimePickerDialog(this,
					timePickerListener, hour, minute, true);
			timPicker.setTitle("Introduzca hora");
			return timPicker;
		}

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

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker arg0, int horaSel, int minSel) {
			hour = horaSel;
			minute = minSel;
			String min = Integer.toString(minute);
			String hor = Integer.toString(hour);
			if (hour < 10)
				hor = "0" + hor;
			if (minute < 10)
				min = "0" + min;
			txtHora.setText(new StringBuilder().append(hor).append(":")
					.append(min));
		}
	};

	// //////// SELECCIONAR IMAGEN
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (resultCode == RESULT_OK) {
			try {
				Uri selectedImage = imageReturnedIntent.getData();
				Bitmap bmp = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(selectedImage));
				arrayImagen.add(bmp);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else
			Toast.makeText(this, "No ha seleccionado ninguna imágen",
					Toast.LENGTH_SHORT).show();
		refrescarLista();
	}

	protected boolean crearEvento() {
		try {
			eventoEN evento = new eventoEN();
			fechaEN fecha = new fechaEN();
			EditText edNombre = (EditText) findViewById(R.id.editNombre);
			EditText edDesc = (EditText) findViewById(R.id.editDescrip);

			ToggleButton tgUbicacion = (ToggleButton) findViewById(R.id.togUbicacion);
			EditText edPoblacion = (EditText) findViewById(R.id.editPoblacion);
			EditText edProvincia = (EditText) findViewById(R.id.editProvincia);

			CheckBox chTodoElDia = (CheckBox) findViewById(R.id.chTodoElDia);
			EditText edFechaIni = (EditText) findViewById(R.id.editFechaInicio);
			EditText edFechaFin = (EditText) findViewById(R.id.editFechaFin);
			EditText edHoraIni = (EditText) findViewById(R.id.editHoraInicio);
			EditText edHoraFin = (EditText) findViewById(R.id.editHoraFin);

			Spinner spCate = (Spinner) findViewById(R.id.spiCategorias);

			ToggleButton tgComentarios = (ToggleButton) findViewById(R.id.togComentarios);

			if (!edNombre.getText().equals(""))
				evento.setNombre(edNombre.getText().toString());
			else
				error = true;
			if (!edDesc.getText().equals(""))
				evento.setDescripcion(edDesc.getText().toString());
			else
				error = true;

			if (tgUbicacion.isChecked()) {
				evento.setLongitud(Longitud);
				evento.setLatitud(Latitud);
			} else {
				evento.setLongitud(0.0);
				evento.setLatitud(0.0);
				if (!edPoblacion.getText().equals(""))
					evento.setPoblacion(edPoblacion.getText().toString());
				else
					error = true;
				if (!edProvincia.getText().equals(""))
					evento.setProvincia(edProvincia.getText().toString());
				else
					error = true;
			}

			if (tgComentarios.isChecked())
				evento.setComentarios(true);
			else
				evento.setComentarios(false);

			if (chTodoElDia.isChecked()) {
				if (!edFechaIni.getText().equals(""))
					fecha.setFechaInicio(dateFormat.parse(edFechaIni.getText()
							.toString() + " " + edHoraIni.getText().toString()));
				else
					error = true;
				fecha.setTodoElDia(true);
			} else {
				fecha.setFechaFin(dateFormat.parse(edFechaFin.getText()
						.toString() + " " + edHoraFin.getText().toString()));
				fecha.setTodoElDia(false);
			}

			evento.setIdCategoria((int) spCate.getSelectedItemId());
			if (!error)
				try {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					int i = 1;
					for (Bitmap imagen : arrayImagen) {
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						imagen = Herramientas.escalarImagen(imagen,
								ESCALAMAXIMA);
						imagen.compress(Bitmap.CompressFormat.JPEG, 80, bao);
						byte[] ba = bao.toByteArray();
						String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
						nameValuePairs.add(new BasicNameValuePair("imagen" + i,
								ba1));
						i++;
					}

					String android_id = Secure.getString(
							actividad.getContentResolver(), Secure.ANDROID_ID);
					nameValuePairs.add(new BasicNameValuePair("idCreador",
							android_id));
					nameValuePairs.add(new BasicNameValuePair("nombre", evento
							.getNombre()));
					nameValuePairs.add(new BasicNameValuePair("descripcion",
							evento.getDescripcion()));
					nameValuePairs.add(new BasicNameValuePair("latitud",String.valueOf(
							evento.getLatitud())));
					nameValuePairs.add(new BasicNameValuePair("longitud",String.valueOf(
							evento.getLongitud())));
					nameValuePairs.add(new BasicNameValuePair("poblacion",
							evento.getPoblacion()));
					nameValuePairs.add(new BasicNameValuePair("provincia",
							evento.getProvincia()));
					if (evento.isComentarios())
						nameValuePairs.add(new BasicNameValuePair(
								"comentarios", "1"));
					else
						nameValuePairs.add(new BasicNameValuePair(
								"comentarios", "0"));
					nameValuePairs.add(new BasicNameValuePair("idCategoria",
							evento.getIdCategoria() + ""));

					// FECHAS
					String fechaInicio = edFechaIni.getText().toString() + " "
							+ edHoraIni.getText().toString();
					nameValuePairs.add(new BasicNameValuePair("fechaInicio",
							fechaInicio));
					if (fecha.isTodoElDia())
						nameValuePairs.add(new BasicNameValuePair("todoElDia",
								"1"));
					else {
						String fechaFin = edFechaFin.getText().toString() + " "
								+ edHoraFin.getText().toString();
						nameValuePairs.add(new BasicNameValuePair("fechaFin",
								fechaFin));
						nameValuePairs.add(new BasicNameValuePair("todoElDia",
								"0"));
					}
					crearEventoActivity.Creacionsatisfactoria = false;
					String URL = "http://desipal.hol.es/app/eventos/alta.php";
					final creacionEvento peticion = new creacionEvento(
							nameValuePairs);
					peticion.execute(new String[] { URL });
					final Handler handler = new Handler();
					final Context co = getApplicationContext();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Status s = peticion.getStatus();
							if (s.name().equals("FINISHED"))
								if (crearEventoActivity.Creacionsatisfactoria) {
									arrayImagen.clear();
									finish();
									Toast.makeText(co, "Evento creado",
											Toast.LENGTH_SHORT).show();
								} else {
									Button btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);
									btnCrearEvento.setEnabled(true);
									Toast.makeText(co, "Error al crear evento",
											Toast.LENGTH_SHORT).show();
								}
							else
								handler.postDelayed(this, 500);

						}
					}, 500);

				} catch (Exception e) {
					Button btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);
					btnCrearEvento.setEnabled(true);
					Toast.makeText(actividad, "Error:" + e.getMessage(),
							Toast.LENGTH_LONG).show();
					error = true;
				}
		} catch (Exception e) {
			Button btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);
			btnCrearEvento.setEnabled(true);
			Toast.makeText(actividad, "Error:" + e.getMessage(),
					Toast.LENGTH_LONG).show();
			error = true;
		}
		return error;
	}

	protected static void refrescarLista() {
		GridView gridview = (GridView) actividad
				.findViewById(R.id.listImagenes);
		gridview.setAdapter(new listaImagenesAdapter(actividad, arrayImagen));
		int altura = 50 * arrayImagen.size();// Tamaño de la imagen
		if (altura > 0)
			gridview.setVisibility(View.VISIBLE);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				altura);
		gridview.setLayoutParams(params);
	}

}
