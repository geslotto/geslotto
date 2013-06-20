package com.desipal.Servidor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.desipal.Entidades.eventoEN;
import com.desipal.eventu.detalleEventoActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class detalleEvento extends AsyncTask<String, Void, Void> {

	private ArrayList<NameValuePair> parametros;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("es", "ES"));
	private eventoEN evento;
	private Context mContext;
	private boolean asiste = false;

	public detalleEvento(ArrayList<NameValuePair> parametros, Context mContext) {
		this.parametros = parametros;
		this.mContext = mContext;
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
				BufferedReader r = new BufferedReader(new InputStreamReader(content));
				StringBuilder total = new StringBuilder();

				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}

				if (!total.toString().equals("null")) {
					JSONObject jobj = new JSONObject(total.toString());
					JSONObject jsEvento = new JSONObject(jobj.getString("evento"));
					//JSONObject jsAsistire = new JSONObject(jobj.getString("asiste"));
					eventoEN e = new eventoEN();
					
					e.setIdEvento(jsEvento.getInt("idEvento"));
					e.setIdCreador(jsEvento.getString("idCreador"));
					e.setNombre(jsEvento.getString("nombre"));
					e.setDescripcion(jsEvento.getString("descripcion"));
					e.setLatitud(jsEvento.getDouble("latitud"));
					e.setLongitud(jsEvento.getDouble("longitud"));
					e.setAsistencia(jsEvento.getInt("asistencia"));
					e.setValidado(Boolean.parseBoolean(jsEvento.getString("validado")));
					e.setComentarios(Boolean.parseBoolean(jsEvento.getString("comentarios")));
					e.setDireccion(jsEvento.getString("direccion"));
					e.setIdCategoria(jsEvento.getInt("idCategoria"));
					e.setDistancia(jsEvento.getDouble("distancia"));
					e.setFechaInicio(dateFormat.parse(jsEvento.getString("fechaInicio")));
					e.setFechaFin(dateFormat.parse(jsEvento.getString("fechaFin")));
					e.setTodoElDia(Boolean.parseBoolean(jsEvento.getString("todoElDia")));
					e.setUrl(jsEvento.getString("url"));
					asiste = jobj.getBoolean("asiste");
					JSONObject jurls = new JSONObject(jsEvento.getString("urlImagenes"));
					List<Drawable> imagenes = new ArrayList<Drawable>();
					for (int i = 0; i < jurls.length(); i++) {
						Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(jurls.getString((i + 1) + ""))
								.getContent());
						Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
						imagenes.add(d);
					}
					e.setImagenes(imagenes);
					this.evento = e;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void onPostExecute(Void result) {
		detalleEventoActivity.evento = this.evento;
		detalleEventoActivity.asiste = this.asiste;
		return;
	}

}
