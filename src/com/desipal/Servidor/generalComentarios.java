package com.desipal.Servidor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.desipal.Entidades.comentarioEN;
import android.os.AsyncTask;

public class generalComentarios extends AsyncTask<String, Void, Void> {

	private ArrayList<NameValuePair> parametros;
	private List<comentarioEN> comentarios = new ArrayList<comentarioEN>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss", new Locale("es", "ES"));
	private AtomicReference<List<comentarioEN>> referencia;

	public generalComentarios(ArrayList<NameValuePair> parametros,
			AtomicReference<List<comentarioEN>> ref) {
		this.parametros = parametros;
		referencia = ref;
	}

	@Override
	protected Void doInBackground(String... urls) {
		for (String url : urls) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(parametros));

				HttpResponse execute = client.execute(httppost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader r = new BufferedReader(new InputStreamReader(
						content));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
				if (!total.toString().equals("null")) {
					JSONArray o = new JSONArray(total.toString());
					for (int i = 0; o.length() > i; i++) {
						comentarioEN e = new comentarioEN();
						JSONObject jobj = o.getJSONObject(i);
						e.setComentario(jobj.getString("comentario"));
						e.setFecha(dateFormat.parse(jobj.getString("fecha")));
						e.setValoracion(((float) jobj.getDouble("valoracion")));
						this.comentarios.add(e);
					}
					referencia.set(this.comentarios);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * protected void onPostExecute(Void result) { if(referencia.get()!=null)
	 * referencia.set(this.comentarios); }
	 */
}
