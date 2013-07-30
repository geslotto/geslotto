package com.desipal.eventu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.desipal.Entidades.comentarioEN;
import com.desipal.Librerias.LoadMoreListView;
import com.desipal.Librerias.LoadMoreListView.OnLoadMoreListener;
import com.desipal.eventu.R;
import com.desipal.Servidor.generalComentarios;
import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

public class VerTodosComentariosActivity extends FragmentActivity {

	public long idEvento;
	boolean bloquearPeticion = false;
	public static int ELEMENTOSLISTA = 8;
	AtomicReference<List<comentarioEN>> refListaComentarios = new AtomicReference<List<comentarioEN>>();
	public static List<comentarioEN> comentarios = new ArrayList<comentarioEN>();
	public static List<comentarioEN> comentariosañadir = new ArrayList<comentarioEN>();
	private int page = 0;
	LoadMoreListView listComentarios;
	ProgressBar progressResult;
	verTodosComentariosAdapter adaptador = new verTodosComentariosAdapter(this,
			comentarios);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			comentarios.clear();
			comentariosañadir.clear();
			setContentView(R.layout.ver_todos_comentarios);
			listComentarios = (LoadMoreListView) findViewById(R.id.listComentarios);
			progressResult = (ProgressBar) findViewById(R.id.proResultados);
			Bundle e = getIntent().getExtras();
			idEvento = e.getLong("idEvento");
			listComentarios.setOnLoadMoreListener(new OnLoadMoreListener() {
				public void onLoadMore() {
					if (comentarios.size() % ELEMENTOSLISTA == 0
							&& !bloquearPeticion) {
						listComentarios.mFooterView.setVisibility(View.VISIBLE);
						hacerPeticion();
					} else
						listComentarios.mFooterView.setVisibility(View.GONE);
				}
			});
			listComentarios.setAdapter(adaptador);
			hacerPeticion();
		} catch (Exception ex) {
			Toast.makeText(this, "Error: " + ex.toString(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void hacerPeticion() {

		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("idEvento", idEvento + ""));
		parametros.add(new BasicNameValuePair("elementsPerPage", ELEMENTOSLISTA
				+ ""));
		page++;
		parametros.add(new BasicNameValuePair("page", page + ""));
		String URL = "http://desipal.hol.es/app/eventos/listaComentarios.php";
		final generalComentarios peticion = new generalComentarios(parametros,
				refListaComentarios);
		peticion.execute(new String[] { URL });
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Status s = peticion.getStatus();
				if (s.name().equals("FINISHED")) {
					comentariosañadir = refListaComentarios.get();
					if (comentariosañadir.size() > 0) {
						comentarios.addAll(comentariosañadir);
						listComentarios.setVisibility(View.VISIBLE);
						adaptador.notifyDataSetChanged();

						progressResult.setVisibility(View.GONE);
						bloquearPeticion = false;
					} else {
						progressResult.setVisibility(View.GONE);
					}
					listComentarios.onLoadMoreComplete();
				} else
					handler.postDelayed(this, 500);
			}
		}, 500);

	}

}
