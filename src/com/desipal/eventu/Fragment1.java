package com.desipal.eventu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.Entidades.adaptadorEventoEN;
import com.desipal.Entidades.categoriaEN;
import com.desipal.Librerias.Herramientas;
import com.desipal.Librerias.LoadMoreListView;
import com.desipal.Librerias.LoadMoreListView.OnLoadMoreListener;
import com.desipal.Librerias.datepicker;
import com.desipal.Servidor.buscarEventos;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment1 extends Fragment {

	public static int ELEMENTOSLISTA = 8;

	public static List<adaptadorEventoEN> eventosFiltro = new ArrayList<adaptadorEventoEN>();
	public static List<adaptadorEventoEN> eventos = new ArrayList<adaptadorEventoEN>();
	AtomicReference<List<adaptadorEventoEN>> refFiltro = new AtomicReference<List<adaptadorEventoEN>>();

	boolean bloquearPeticion = false;// bandera que bloquea para no poder jhacer
										// la peticion
	boolean categoriasActu = false;
	Button btnRecoger;

	LoadMoreListView gridResultados;
	TextView txtErrorResultados;
	TextView txtFecha;

	ProgressBar progressResult;
	ImageButton btnLimpiar;
	Spinner spiCategoria;
	EditText campoFiltro;
	EditText campoFecha;
	Button btnFiltrar;
	LinearLayout filtro;

	RelativeLayout relFecha;
	RelativeLayout relCampos;

	private boolean recogido = true;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tabinicio, container, false);

		btnRecoger = (Button) view.findViewById(R.id.btn_recoger);

		gridResultados = (LoadMoreListView) view
				.findViewById(R.id.gridResultados);

		btnFiltrar = (Button) view.findViewById(R.id.btnBuscar);
		filtro = (LinearLayout) view.findViewById(R.id.linear_filtro);
		btnLimpiar = (ImageButton) view.findViewById(R.id.btnLimpiar);
		campoFiltro = (EditText) view.findViewById(R.id.campoFiltro);
		campoFecha = (EditText) view.findViewById(R.id.campoFecha);
		spiCategoria = (Spinner) view.findViewById(R.id.spiCategorias);
		progressResult = (ProgressBar) view.findViewById(R.id.proResultados);
		txtErrorResultados = (TextView) view
				.findViewById(R.id.txtErrorResultados);
		txtFecha = (TextView) view.findViewById(R.id.txtfecha);

		relFecha = (RelativeLayout) view.findViewById(R.id.rel_fecha);
		relCampos = (RelativeLayout) view.findViewById(R.id.rel_campos);

		spiCategoria.setEnabled(false);// se bloque las cat mientras asta que se
										// actulicen
		String a[] = new String[] { "Todos" };
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item, a);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter1);

		campoFecha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// getActivity().showDialog(999);
				DialogFragment newFragment = new datepicker();
				((datepicker) newFragment).establecerCampo(campoFecha);
				newFragment.show(getFragmentManager(), "DatePicker");
			}
		});

		btnRecoger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				recoger();
			}
		});

		btnFiltrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// OCULTAR TECLADO
				// InputMethodManager imm = (InputMethodManager)
				// getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
				// imm.hideSoftInputFromWindow(campoFiltro.getWindowToken(), 0);
				btnFiltrar.setEnabled(false);
				txtErrorResultados.setVisibility(View.GONE);
				progressResult.setVisibility(View.VISIBLE);
				gridResultados.setVisibility(View.GONE);
				eventos.clear();
				bloquearPeticion = false;
				hacerPeticion();
				recoger();
			}
		});

		btnFiltrar.performClick();
		btnLimpiar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gridResultados.setAdapter(null);
				campoFiltro.setText("");
				campoFecha.setText("");
			}
		});

		gridResultados.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				Intent i = new Intent(getActivity(),
						detalleEventoActivity.class);
				i.putExtra("idEvento", id);
				getActivity().startActivity(i);
			}
		});

		gridResultados.setOnLoadMoreListener(new OnLoadMoreListener() {
			public void onLoadMore() {
				if (eventos.size() % ELEMENTOSLISTA == 0 && !bloquearPeticion) {
					gridResultados.mFooterView.setVisibility(View.VISIBLE);
					hacerPeticion();
				} else
					gridResultados.mFooterView.setVisibility(View.GONE);
			}
		});
		return view;
	}

	protected void hacerPeticion() {

		LatLng loc = Herramientas.ObtenerLocalizacion(getActivity());
		final Double latitud = loc.latitude;
		final Double longitud = loc.longitude;

		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("latitud", latitud.toString()));
		parametros.add(new BasicNameValuePair("longitud", longitud.toString()));
		int pagina = 0;
		String URL = "http://desipal.hol.es/app/eventos/filtro.php";

		parametros.add(new BasicNameValuePair("filtro", campoFiltro.getText()
				.toString()));
		parametros.add(new BasicNameValuePair("fecha", campoFecha.getText()
				.toString()));
		pagina = ((int) eventos.size() / ELEMENTOSLISTA) + 1;

		parametros.add(new BasicNameValuePair("page", pagina + ""));
		final buscarEventos peticion = new buscarEventos(parametros,
				getActivity(), refFiltro);
		peticion.execute(new String[] { URL });

		final GridViewAdapter adaptador = new GridViewAdapter(getActivity(),
				eventos);
		gridResultados.setAdapter(adaptador);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Status s = peticion.getStatus();
				if (s.name().equals("FINISHED")) {
					eventosFiltro = refFiltro.get();
					if (!categoriasActu) {// Se actualiza las categorias solo
											// una vez
						List<categoriaEN> lista = Herramientas
								.Obtenercategorias(getActivity());
						String a[] = new String[lista.size()];
						for (int i = 0; i < lista.size(); i++) {
							a[i] = lista.get(i).getTexto();
						}
						ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
								getActivity(), R.layout.spinner_item, a);
						adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spiCategoria.setAdapter(adapter1);
						spiCategoria.setEnabled(true);
						categoriasActu = true;
					}
					if (eventos.size() > 0 && eventosFiltro.size() == 0)
						bloquearPeticion = true;
					else
						bloquearPeticion = false;
					btnFiltrar.setEnabled(true);
					if (eventosFiltro.size() > 0) {
						eventos.addAll(eventosFiltro);
						gridResultados.setVisibility(View.VISIBLE);
						adaptador.notifyDataSetChanged();
						gridResultados.onLoadMoreComplete();
						progressResult.setVisibility(View.GONE);
						bloquearPeticion = false;
					} else {
						if (eventos.size() == 0)
							txtErrorResultados.setVisibility(View.VISIBLE);
						progressResult.setVisibility(View.GONE);
					}

				} else
					handler.postDelayed(this, 500);
			}
		}, 500);
	}

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

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.txtfiltro);
			params.setMargins(0, 0, 10, 0);
			campoFiltro.setWidth(190);
			campoFiltro.setLayoutParams(params);

			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.campoFiltro);
			relFecha.setLayoutParams(params);

			LinearLayout.LayoutParams linear = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			linear.setMargins(0, 10, 0, 0);
			relCampos.setLayoutParams(linear);

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

			RelativeLayout.LayoutParams linear = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			linear.setMargins(0, 10, 0, 0);
			linear.addRule(RelativeLayout.BELOW, R.id.campoFiltro);
			relFecha.setLayoutParams(linear);

			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params2.addRule(RelativeLayout.RIGHT_OF, R.id.txtfiltro);
			campoFiltro.setLayoutParams(params2);
		}
	}

}
