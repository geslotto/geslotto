package com.desipal.eventu;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.desipal.Entidades.categoriaEN;
import com.desipal.Entidades.eventoEN;
import com.desipal.Entidades.seleccionUbicacionEN;
import com.desipal.Librerias.Herramientas;
import com.desipal.Librerias.datepicker;
import com.desipal.Librerias.timepicker;
import com.desipal.Servidor.creacionEvento;
import com.desipal.eventu.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class crearEventoActivity extends FragmentActivity {

	private int ESCALAMAXIMA = 400;// Tamaño máximo de las imagenes subidas
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm", MainActivity.currentLocale);
	private static Activity actividad;
	public static int Creacionsatisfactoria = 0;
	private boolean error = false;
	private boolean[] errores = new boolean[6];
	private boolean direccionvalida = false;
	public static seleccionUbicacionEN[] opcionesDireccion;

	public static List<Bitmap> arrayImagen;
	double Latitud;
	double Longitud;

	// CONTROLES
	Button btnInicio;
	Button btnSubirImagen;
	Button btnCrearEvento;

	TextView txtUbicacion;
	EditText edNombre;
	EditText edDesc;
	EditText edDireccion;
	EditText edCiudad;
	EditText edNumero;
	EditText edFechaIni;
	EditText edFechaFin;
	EditText edHoraIni;
	EditText edHoraFin;

	CheckBox chTodoElDia;

	Spinner spiCategoria;

	ToggleButton tgUbicacion;
	ToggleButton tgComentarios;

	TableLayout tableLocalizacion;
	RelativeLayout relFechas;
	TableRow filaFin;
	TableRow filaTexFin;
	RelativeLayout relImagenes;

	private GoogleMap mapaLocalizacion;
	RelativeLayout relativeMapa;
	private EditText[] editError = new EditText[] { edNombre, edDesc,
			edDireccion, edCiudad, edFechaIni, edFechaFin };

	// IdCategoria
	int IdCategoriaSel = 0;
	boolean errorspiner = true;
	List<categoriaEN> listaCategorias = null;
	private int NUMTOTALFOTOS = 5;
	public static int contaFotos = 0;

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

		btnInicio = (Button) findViewById(R.id.btnInicio);
		btnSubirImagen = (Button) findViewById(R.id.btnSubirImagen);
		btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);

		txtUbicacion = (TextView) findViewById(R.id.txtUbicacion);
		edNombre = (EditText) findViewById(R.id.editNombre);
		edDesc = (EditText) findViewById(R.id.editDescrip);
		edDireccion = (EditText) findViewById(R.id.editDireccion);
		edCiudad = (EditText) findViewById(R.id.editCiudad);
		edNumero = (EditText) findViewById(R.id.editNumero);
		edFechaIni = (EditText) findViewById(R.id.editFechaInicio);
		edFechaFin = (EditText) findViewById(R.id.editFechaFin);
		edHoraIni = (EditText) findViewById(R.id.editHoraInicio);
		edHoraFin = (EditText) findViewById(R.id.editHoraFin);

		chTodoElDia = (CheckBox) findViewById(R.id.chTodoElDia);

		spiCategoria = (Spinner) findViewById(R.id.spiCategorias);

		tgUbicacion = (ToggleButton) findViewById(R.id.togUbicacion);
		tgComentarios = (ToggleButton) findViewById(R.id.togComentarios);

		tableLocalizacion = (TableLayout) findViewById(R.id.tableLocalizacion);
		relFechas = (RelativeLayout) findViewById(R.id.relFechas);
		filaFin = (TableRow) findViewById(R.id.table2Row4);
		filaTexFin = (TableRow) findViewById(R.id.table2Row3);
		relImagenes = (RelativeLayout) findViewById(R.id.relImagenes);
		relativeMapa = (RelativeLayout) findViewById(R.id.relativeMapa);
		mapaLocalizacion = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapaLocalizacion)).getMap();

		// Spinner catgorias

		listaCategorias = Herramientas
				.Obtenercategorias(crearEventoActivity.this);
		String a[] = new String[listaCategorias.size()];
		for (int i = 0; i < listaCategorias.size(); i++) {
			a[i] = listaCategorias.get(i).getTexto();
		}
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
				crearEventoActivity.this, R.layout.spinner_item, a);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter2);
		spiCategoria.setEnabled(true);
		btnInicio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_right);
			}
		});

		chTodoElDia.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

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

		edFechaIni.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogFragment newFragment = new datepicker();
				((datepicker) newFragment).establecerCampo(edFechaIni);
				newFragment.show(getSupportFragmentManager(), "DatePicker");
				// showDialog(0);
			}
		});

		edFechaFin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new datepicker();
				((datepicker) newFragment).establecerCampo(edFechaFin);
				newFragment.show(getSupportFragmentManager(), "DatePicker");
				// showDialog(0);
			}
		});

		edHoraIni.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new timepicker();
				((timepicker) newFragment).establecerCampo(edHoraIni);
				newFragment.show(getSupportFragmentManager(), "TimePicker");
				// txtHora = (EditText) v.findViewById(R.id.);
				// showDialog(1);
			}
		});

		edHoraFin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new timepicker();
				((timepicker) newFragment).establecerCampo(edHoraFin);
				newFragment.show(getSupportFragmentManager(), "TimePicker");
				// showDialog(1);
			}
		});

		tgUbicacion.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Handler handler = new Handler();
				Boolean recoger = true;
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
						LatLng loc = MainActivity.PosicionActual;
						if (loc == null) {
							Toast.makeText(actividad,
									"La ubicación no se encuentra disponible",
									Toast.LENGTH_LONG).show();
							buttonView.setChecked(false);
							recoger = true;
						} else {
							relativeMapa.setVisibility(View.VISIBLE);
							Latitud = loc.latitude;
							Longitud = loc.longitude;
							mapaLocalizacion.clear();
							LatLng Posicion = new LatLng(Latitud, Longitud);
							mapaLocalizacion.addMarker(new MarkerOptions()
									.position(Posicion).title("estas aquí"));
							mapaLocalizacion.getUiSettings()
									.setScrollGesturesEnabled(false);
							mapaLocalizacion.moveCamera(CameraUpdateFactory
									.newLatLngZoom(Posicion, 15));
							recoger = false;
						}
					} catch (Exception e) {
						Toast.makeText(actividad, "Error:" + e.getMessage(),
								Toast.LENGTH_LONG).show();
					}
				}
				if (recoger) {
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, 0, tableLocalizacion.getHeight());
					anim_trans.setDuration(200);
					relFechas.startAnimation(anim_trans);
					handler.postDelayed(new Runnable() {
						public void run() {
							relativeMapa.setVisibility(View.GONE);
							tableLocalizacion.setVisibility(View.VISIBLE);
							AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
							anim_alpha.setDuration(200);
							tableLocalizacion.startAnimation(anim_alpha);
						}
					}, 220);
				}
			}
		});

		btnSubirImagen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contaFotos != NUMTOTALFOTOS) {
					Intent pickPhoto = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(pickPhoto, 1);
					if (arrayImagen.size() == 0)
						Toast.makeText(
								actividad,
								"La primera imagen seleccionada se utilizara como imagen principal",
								Toast.LENGTH_LONG).show();
				}
				else
					Toast.makeText(
							actividad,
							"Se ha cumplido el maximo de fotos",
							Toast.LENGTH_LONG).show();
			}
		});

		btnCrearEvento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCrearEvento.setEnabled(false);
				crearEvento();
			}
		});
	}

	// //////// SELECCIONAR IMAGEN
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (resultCode == RESULT_OK) {
			try {
				Uri selectedImage = imageReturnedIntent.getData();
				Bitmap bmp = Herramientas.reescalarBitmapPorUri(selectedImage,
						crearEventoActivity.this, ESCALAMAXIMA);
				contaFotos++;
				arrayImagen.add(bmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (arrayImagen.size() == 0)
			Toast.makeText(this, "No ha elegido ninguna imágen",
					Toast.LENGTH_SHORT).show();
		refrescarLista();
	}

	@SuppressWarnings("deprecation")
	protected boolean crearEvento() {
		try {
			eventoEN evento = new eventoEN();

			editError[0] = edNombre;
			editError[1] = edDesc;
			editError[2] = edDireccion;
			editError[3] = edCiudad;
			editError[4] = edFechaIni;
			editError[5] = edFechaFin;

			if (edNombre.getText().length() > 0) {
				errores[0] = false;
				evento.setNombre(edNombre.getText().toString());
			} else
				errores[0] = true;
			if (edDesc.getText().length() > 0) {
				errores[1] = false;
				evento.setDescripcion(edDesc.getText().toString());
			} else
				errores[1] = true;

			// id Categoria
			String Text = spiCategoria.getSelectedItem().toString();
			boolean encontrado = false;
			if (listaCategorias != null)
				for (int i = 0; i < listaCategorias.size() && !encontrado; i++) {
					if (listaCategorias.get(i).getTexto().equals(Text)) {
						IdCategoriaSel = listaCategorias.get(i)
								.getIdCategoria();
						encontrado = true;
						error = false;
					}
				}
			if (IdCategoriaSel == 1) {
				errorspiner = true;
				error = true;
			} else
				errorspiner = false;
			// /

			if (tgUbicacion.isChecked()) {
				evento.setLongitud(Longitud);
				evento.setLatitud(Latitud);
				evento.setDireccion("");
				errores[2] = false;// Quitamos el error de direccion
				errores[3] = false;// Quitamos el error de ciudad
			} else {
				if (direccionvalida) {
					evento.setLongitud(Longitud);
					evento.setLatitud(Latitud);
					evento.setDireccion("");
				} else {
					String direccion = "";
					if (edDireccion.getText().length() > 0) {
						direccion = edDireccion.getText().toString();
						errores[2] = false;// Quitamos el error de direccion
						if (edNumero.getText().length() > 0)
							direccion = direccion + ","
									+ edNumero.getText().toString();
						if (edCiudad.getText().length() > 0) {
							direccion = direccion + ","
									+ edCiudad.getText().toString();
							evento.setDireccion(direccion);
							errores[3] = false;// Quitamos el error de ciudad
						} else
							errores[3] = true;
					} else {
						if (edCiudad.getText().length() > 0) {
							evento.setDireccion(edCiudad.getText().toString());
							errores[2] = false;// Quitamos el error de direccion
							errores[3] = false;// Quitamos el error de ciudad
						} else {
							errores[2] = true;
							errores[3] = true;
						}
					}
				}
			}

			if (tgComentarios.isChecked())
				evento.setComentarios(true);
			else
				evento.setComentarios(false);

			if (chTodoElDia.isChecked()) {
				if (edFechaIni.getText().length() > 0) {
					errores[4] = false;// Quitamos el error de FechaInicio
					if (edHoraIni.getText().length() > 0)
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString()
								+ " "
								+ edHoraIni.getText().toString()));
					else
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString() + " 00:00"));
				} else
					errores[4] = true;
				evento.setTodoElDia(true);
				errores[5] = false;// Quitamos el error de FechaFin
			} else {
				if (edFechaIni.getText().length() > 0) {
					if (edHoraIni.getText().length() > 0)
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString()
								+ " "
								+ edHoraIni.getText().toString()));
					else
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString() + " 00:00"));
					errores[4] = false;// Quitamos el error de FechaInicio
				} else
					errores[4] = true;
				if (edFechaFin.getText().length() > 0) {
					if (edHoraFin.getText().length() > 0)
						evento.setFechaFin(dateFormat.parse(edFechaFin
								.getText().toString()
								+ " "
								+ edHoraFin.getText().toString()));
					else
						evento.setFechaFin(dateFormat.parse(edFechaFin
								.getText().toString() + " 00:00"));
					errores[5] = false;
				} else
					errores[5] = true;// Quitamos el error de FechaFin

				evento.setTodoElDia(false);
			}
			for (int i = 0; i < errores.length; i++) {
				if (errores[i]) {
					error = true;
					editError[i].setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.camporojo));
				} else
					editError[i].setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.campo));
			}

			if (errorspiner) {
				spiCategoria.setBackgroundDrawable(getResources().getDrawable(
						R.anim.anim_boton_rojo));
				error = true;
			} else {
				spiCategoria.setBackgroundDrawable(getResources().getDrawable(
						R.anim.anim_boton));
				evento.setIdCategoria(IdCategoriaSel);
			}
			if (!error)
				try {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					int i = 1;
					for (Bitmap imagen : arrayImagen) {
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						/*
						 * imagen = Herramientas.disminuirImagen(imagen,
						 * ESCALAMAXIMA);
						 */
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
					nameValuePairs.add(new BasicNameValuePair("latitud", String
							.valueOf(evento.getLatitud())));
					nameValuePairs.add(new BasicNameValuePair("longitud",
							String.valueOf(evento.getLongitud())));
					nameValuePairs.add(new BasicNameValuePair("direccion",
							evento.getDireccion()));
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
					if (evento.isTodoElDia())
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
					crearEventoActivity.Creacionsatisfactoria = 1;
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
								if (crearEventoActivity.Creacionsatisfactoria == 0) {
									arrayImagen.clear();
									finish();
									Toast.makeText(co, "Evento creado",
											Toast.LENGTH_SHORT).show();
								} else {
									if (crearEventoActivity.Creacionsatisfactoria == 1)
										Toast.makeText(co,
												"Error al crear evento",
												Toast.LENGTH_SHORT).show();
									else if (crearEventoActivity.Creacionsatisfactoria == 2)
										Toast.makeText(
												co,
												"No se ha encontrado la dirección especificada.",
												Toast.LENGTH_SHORT).show();
									else if (crearEventoActivity.Creacionsatisfactoria == 3) {
										Toast.makeText(
												co,
												"La dirección devuelve varios resultados.",
												Toast.LENGTH_SHORT).show();
										ventanaModal();
									}
									btnCrearEvento.setEnabled(true);
								}
							else
								handler.postDelayed(this, 500);
						}
					}, 500);

				} catch (Exception e) {
					btnCrearEvento.setEnabled(true);
					Toast.makeText(actividad, "Error:" + e.getMessage(),
							Toast.LENGTH_LONG).show();
					error = true;
				}
			else {
				Toast.makeText(actividad, "Rellene todos los campos",
						Toast.LENGTH_SHORT).show();
				btnCrearEvento.setEnabled(true);
			}
		} catch (Exception e) {
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
		float altura = Herramientas.convertDpToPixel(55 * arrayImagen.size(),
				actividad);// Tamaño de la imagen
		if (altura > 0)
			gridview.setVisibility(View.VISIBLE);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) altura);
		gridview.setLayoutParams(params);
	}

	protected void ventanaModal() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				actividad);

		String[] resultados = new String[opcionesDireccion.length];
		for (int i = 0; i < opcionesDireccion.length; i++) {
			resultados[i] = opcionesDireccion[i].getDireccion();
		}
		alertDialogBuilder.setTitle("Existen varias opciones");
		alertDialogBuilder.setItems(resultados,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						Latitud = opcionesDireccion[position].getLatitud();
						Longitud = opcionesDireccion[position].getLongitud();
						tgUbicacion.setEnabled(false);
						txtUbicacion.setText("Ubicación seleccionada");
						direccionvalida = true;
						crearEvento();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
