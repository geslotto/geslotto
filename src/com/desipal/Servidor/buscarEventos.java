package com.desipal.Servidor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

import com.desipal.Entidades.adaptadorEventoEN;
import com.desipal.eventu.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class buscarEventos extends AsyncTask<String, Void, Void> {

	private Context mContext;
	private ArrayList<NameValuePair> parametros;
	private List<adaptadorEventoEN> adaptadorEventos = new ArrayList<adaptadorEventoEN>();
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss", new Locale("es", "ES"));
	private AtomicReference<List<adaptadorEventoEN>> referencia;

	public buscarEventos(ArrayList<NameValuePair> parametros, Context context,
			AtomicReference<List<adaptadorEventoEN>> ref) {
		this.parametros = parametros;
		this.mContext = context;
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
						adaptadorEventoEN e = new adaptadorEventoEN();
						JSONObject jobj = o.getJSONObject(i);
						e.setIdEvento(jobj.getInt("idEvento"));
						e.setNombre(jobj.getString("nombre"));
						e.setDescripcion(jobj.getString("descripcion"));
						e.setDistancia(jobj.getDouble("distancia"));
						e.setUrl(jobj.getString("url"));
						e.setLatitud(jobj.getDouble("latitud"));
						e.setLongitud(jobj.getDouble("longitud"));

						if (!jobj.getString("imagen").equals("noimagen")) {
							String ere = jobj.getString("imagen");
							Bitmap bitmap = BitmapFactory
									.decodeStream((InputStream) new URL(ere)
											.getContent());
							Drawable d = new BitmapDrawable(
									mContext.getResources(), bitmap);
							e.setImagen(d);
						} else
							e.setImagen(mContext.getResources().getDrawable(
									drawable.grid_1));

						e.setFecha(dateFormat.parse(jobj.getString("fecha")));
						this.adaptadorEventos.add(e);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		referencia.set(this.adaptadorEventos);
		return;
	}
}
